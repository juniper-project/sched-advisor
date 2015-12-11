/*
 * Copyright (c) 2015, Brno University of Technology, Faculty of Information Technology
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of sched-advisor nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.juniper.sa.deployment.monitor;

import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsFactory;
import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsFactory.UnkownJdbcDatabase;
import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsInterface;
import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsForH2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import org.json.simple.parser.ParseException;

/**
 * The class implementing a monitoring service client by a direct access to a
 * remote monitoring service and caching data in a local database.
 *
 * @author rychly
 */
public class MonitoringDbService implements MonitoringServiceInterface, AutoCloseable {

    private static final String SQL_SELECT_METRICS_NAME = "SELECT DISTINCT name FROM metrics;";
    private static final String SQL_SELECT_NUMERIC_VALUE = "SELECT CASE WHEN numericvalue IS NULL THEN textvalue ELSE numericvalue END"
            + " FROM metrics WHERE name=?;";
    private static final String SQL_SELECT_METRIC_RECORDS = "SELECT DISTINCT time, metrictype, hostname FROM records JOIN metrics ON (records.id = metrics.recordid) "
            + "WHERE (name=? AND (numericvalue=? OR textvalue=?) "
            + "OR (?='timestamp' AND time=?) "
            + "OR (?='type' AND metrictype=?) "
            + "OR (?='hostname' AND hostname=?));";
    private static final String SQL_SELECT_METRIC_VALUES = "SELECT CASE WHEN v1.numericvalue IS NULL THEN v1.textvalue ELSE v1.numericvalue END "
            + "FROM records JOIN metrics v1 ON (records.id = v1.recordid) WHERE v1.name=? AND ("
            + "EXISTS(SELECT 1 FROM metrics v2 WHERE v2.recordid=v1.recordid AND v2.name=? AND (v2.numericvalue=? OR v2.textvalue=?)) "
            + "OR (?='timestamp' AND time=?) "
            + "OR (?='type' AND metrictype=?) "
            + "OR (?='hostname' AND hostname=?));";
    private static final String SQL_SELECT_RECORD_STATS = "SELECT COUNT(numericvalue), MIN(numericvalue), MAX(numericvalue), AVG(numericvalue), SUM(numericvalue), SUM(numericvalue*numericvalue), VAR_POP(numericvalue), STDDEV_POP(numericvalue) "
            + "FROM records JOIN metrics ON (records.id = metrics.recordid) WHERE name=? AND time BETWEEN ? AND ?;";
    private static final String SQL_INSERT_RECORDS = "INSERT INTO records(time, metrictype, hostname) VALUES (?, ?, ?);";
    private static final String SQL_INSERT_METRICS_NUMERIC = "INSERT INTO metrics(recordid, name, numericvalue) VALUES (?, ?, ?);";
    private static final String SQL_INSERT_METRICS_TEXT = "INSERT INTO metrics(recordid, name, textvalue) VALUES (?, ?, ?);";
    private static final String SQL_SELECT_LATEST_PROGRAM_RUNTIMES = "SELECT r.id AS id"
            + " FROM records AS r JOIN metrics AS m1 ON (r.id = m1.recordid) JOIN metrics AS m2 ON (r.id = m2.recordid)"
            + " WHERE r.metrictype = 'ProgramRuntime' AND m1.name = 'ProgramGlobalRank' AND m2.name = 'ProgramStartTimestamp'"
            + " AND NOT EXISTS (SELECT 1"
            + "  FROM records AS er JOIN metrics AS em1 ON (er.id = em1.recordid) JOIN metrics AS em2 ON (er.id = em2.recordid)"
            + "  WHERE er.metrictype = 'ProgramRuntime' AND em1.name = 'ProgramGlobalRank' AND em2.name = 'ProgramStartTimestamp'"
            + "   AND em1.numericvalue = m1.numericvalue AND em2.numericvalue = m2.numericvalue AND er.time > r.time"
            + "  LIMIT 1"
            + " )"; // the SQL query is without ending semicolon, it will be included in other SQL queries
    private static final String SQL_DELETE_OLD_PROGRAM_RUNTIMES = "DELETE FROM records"
            + " WHERE metrictype = 'ProgramRuntime' AND id NOT IN ("
            + SQL_SELECT_LATEST_PROGRAM_RUNTIMES + ");";

    private final MonitoringDbActionsInterface monitoringDbActions;
    private final String monitoringServiceURL;
    private final String applicationId;

    /**
     * Open an H2 database for the monitoring result cache with a given URL.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @param dbURL an H2 database URL of the form jdbc:subprotocol:subname
     * @throws ClassNotFoundException if an H2 database driver class cannot be
     * located
     * @throws SQLException if a database access error occurs
     * @throws UnkownJdbcDatabase if the provided JDBC URL cannot be analyzed
     */
    public MonitoringDbService(String monitoringServiceURL, String applicationId, String dbURL) throws ClassNotFoundException, SQLException, MonitoringDbActionsFactory.UnkownJdbcDatabase {
        this.applicationId = applicationId;
        this.monitoringServiceURL = monitoringServiceURL;
        this.monitoringDbActions = MonitoringDbActionsFactory.createMonitoringDbActionsFor(dbURL);
    }

    /**
     * Open an H2 In-Memory unnamed private one-connection database
     * (jdbc:h2:mem:) for the monitoring result cache.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @throws ClassNotFoundException if an H2 database driver class cannot be
     * located
     * @throws SQLException if a database access error occurs
     */
    public MonitoringDbService(String monitoringServiceURL, String applicationId) throws ClassNotFoundException, SQLException {
        this.applicationId = applicationId;
        this.monitoringServiceURL = monitoringServiceURL;
        this.monitoringDbActions = new MonitoringDbActionsForH2();
    }

    /**
     * Get an URL of the monitoring service if defined.
     *
     * @return the URL of the monitoring service if defined, null otherwise
     */
    @Override
    public String getMonitoringServiceURL() {
        return this.monitoringServiceURL;
    }

    /**
     * Get an object with methods for monitoring database.
     *
     * @return the object with methods for monitoring database
     */
    public MonitoringDbActionsInterface getMonitoringDbActions() {
        return monitoringDbActions;
    }

    /**
     * Get a name (or an ID) of a Juniper application to be monitored.
     *
     * @return a name (or an ID) of a Juniper application to be monitored
     */
    @Override
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Get a list of names/IDs of all applications that have stored data at the
     * remote monitoring service.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @return a list of names/IDs of all applications that have stored data at
     * the remote monitoring service
     * @throws ParseException if the JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    public static String[] getApplications(String monitoringServiceURL) throws ParseException, IOException {
        ArrayList<String> resultStrings = new ArrayList<>();
        // query a monitoring service via HTTP GET
        URLConnection connection = new URL(monitoringServiceURL).openConnection();
        connection.setRequestProperty("Accept", "application/json");
        try (InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);) {
            // JSON parser
            String line;
            while ((line = reader.readLine()) != null) {
                int mark1, mark2;
                // key in quotation marks
                if (((mark1 = line.indexOf('"')) < 0) || ((mark2 = line.indexOf('"', mark1 + 1)) < 0)) {
                    // not JSON key-val -> it is a border between JSON records
                    continue;
                }
                String key = line.substring(mark1 + 1, mark2);
                if (!"id".equals(key)) {
                    // key is not "id" so val will be not the application ID
                    continue;
                }
                // value without quotation marks
                if ((mark1 = line.indexOf(':', mark2)) < 0) {
                    // parsing error
                    continue;
                }
                if ((mark2 = line.indexOf(',', mark1 + 1)) < 0) {
                    mark2 = line.length();
                }
                String val = line.substring(mark1 + 1, mark2).trim();
                // null value -> skip
                if ("null".equals(val)) {
                    continue;
                }
                // string value -> trim quotation marks
                if (val.charAt(0) == '"') {
                    val = val.substring(1, val.length() - 1);
                }
                // process the key-vale pair
                resultStrings.add(val);
            }
        }
        return resultStrings.toArray(new String[0]);
    }

    /**
     * Close the database for the monitoring result cache if needed.
     */
    @Override
    public void close() {
        try {
            this.monitoringDbActions.closeDatabaseConenction();
        }
        catch (SQLException ex) {
            // ignore
        }
    }

    /**
     * @deprecated used query <code>SQL_DELETE_OLD_PROGRAM_RUNTIMES</code> to
     * delete all records and their metrics but those with maximal record time
     * (the latest) is very slow for large data
     */
    private int removeRedundancyInDatabaseTables(Statement statement) throws SQLException {
        return statement.executeUpdate(MonitoringDbService.SQL_DELETE_OLD_PROGRAM_RUNTIMES);
    }

    /**
     * Remove redundant data from the local database cache. This method is
     * automatically invoked after <code>importDatabase(...)</code> and
     * <code>importMetrics()</code>.
     *
     * @return number of removed database records
     * @throws SQLException if a database access error occurs
     * @deprecated the query used to delete all records and their metrics but
     * those with maximal record time (the latest) is very slow for large data
     */
    public int removeRedundancyInDatabaseTables() throws SQLException {
        try (Statement statement = this.monitoringDbActions.getDatabaseConnection().createStatement()) {
            return this.removeRedundancyInDatabaseTables(statement);
        }
    }

    /**
     * Import the application metrics from the monitoring service to an internal
     * database. TODO: restrict import by start and end timestamps
     *
     * @return number of imported individual metrics (key-value pairs)
     * @throws MalformedURLException if <code>this.monitoringServiceURL</code>
     * is malformed
     * @throws IOException if there is an HTTP error when connecting to or
     * reading from the monitoring service
     * @throws SQLException if there is a database error when storing metrics
     * into the internal database
     */
    public int importMetrics() throws MalformedURLException, IOException, SQLException {
        int counter = 0;
        // query a monitoring service via HTTP GET
        URLConnection connection = new URL(this.getMonitoringServiceURL() + this.getApplicationId()).openConnection();
        connection.setRequestProperty("Accept", "application/json");
        try (InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                // prepared statements for insertion of metric records and their values in different formats
                PreparedStatement preparedStatementRecord
                = this.monitoringDbActions.getDatabaseConnection().prepareStatement(MonitoringDbService.SQL_INSERT_RECORDS, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement preparedStatementNumericValue
                = this.monitoringDbActions.getDatabaseConnection().prepareStatement(MonitoringDbService.SQL_INSERT_METRICS_NUMERIC);
                PreparedStatement preparedStatementTextValue
                = this.monitoringDbActions.getDatabaseConnection().prepareStatement(MonitoringDbService.SQL_INSERT_METRICS_TEXT);) {
            // state variables
            Integer recId = null;
            Timestamp recTimestamp = null;
            String recType = null;
            String recHostname = null;
            // JSON parser
            String line;
            while ((line = reader.readLine()) != null) {
                int mark1, mark2;
                // key in quotation marks
                if (((mark1 = line.indexOf('"')) < 0) || ((mark2 = line.indexOf('"', mark1 + 1)) < 0)) {
                    // not JSON key-val -> it is a border between JSON records
                    recId = null;
                    continue;
                }
                String key = line.substring(mark1 + 1, mark2);
                // value without quotation marks
                if ((mark1 = line.indexOf(':', mark2)) < 0) {
                    // parsing error
                    continue;
                }
                if ((mark2 = line.indexOf(',', mark1 + 1)) < 0) {
                    mark2 = line.length();
                }
                String val = line.substring(mark1 + 1, mark2).trim();
                // null value -> skip
                if ("null".equals(val)) {
                    continue;
                }
                // string value -> trim quotation marks
                if (val.charAt(0) == '"') {
                    val = val.substring(1, val.length() - 1);
                }
                // process the key-vale pair
                switch (key.toLowerCase()) {
                    case "timestamp":
                        recTimestamp = new Timestamp((long) (Double.parseDouble(val) * 1000D));
                        break;
                    case "type":
                        recType = val;
                        break;
                    case "hostname":
                        recHostname = val;
                        break;
                    default:
                        // create records row if ready (metrictype can be null) and not already created for this JSON record
                        if ((recId == null) && (recTimestamp != null) && (recHostname != null)) {
                            try {
                                //System.out.println("# INSERT INTO records: " + recTimestamp + ", " + recType + ", " + recHostname);
                                preparedStatementRecord.setTimestamp(1, recTimestamp);
                                recTimestamp = null;
                                preparedStatementRecord.setString(2, recType);
                                recType = null;
                                preparedStatementRecord.setString(3, recHostname);
                                recHostname = null;
                                preparedStatementRecord.executeUpdate();
                                try (ResultSet generatedKeys = preparedStatementRecord.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        recId = generatedKeys.getInt(1);
                                    }
                                }
                            }
                            catch (SQLException ex) {
                                // nothing, the record cannot be inserted -> will skip the rest of this JSON record as rec* are null
                                // this can be the case of duplicate timestamp-type-hostname triplets
                            }
                        }
                        // create metrics row if exist record row for this JSON record
                        if (recId != null) {
                            try {
                                try {
                                    final double valDouble = Double.parseDouble(val);
                                    //System.out.println("# INSERT INTO metrics: " + recId + ", " + key + ", " + valDouble);
                                    preparedStatementNumericValue.setInt(1, recId);
                                    preparedStatementNumericValue.setString(2, key);
                                    preparedStatementNumericValue.setDouble(3, valDouble);
                                    preparedStatementNumericValue.executeUpdate();
                                    counter++;
                                }
                                catch (NumberFormatException ex) {
                                    //System.out.println("# INSERT INTO metrics: " + recId + ", " + key + ", " + val);
                                    preparedStatementTextValue.setInt(1, recId);
                                    preparedStatementTextValue.setString(2, key);
                                    preparedStatementTextValue.setString(3, val);
                                    preparedStatementTextValue.executeUpdate();
                                    counter++;
                                }
                            }
                            catch (SQLException ex) {
                                // nothing, the mertic cannot be inserted -> will be skipped
                            }
                        }
                        break;
                }
            }
        }
        this.removeRedundancyInDatabaseTables();
        return counter;
    }

    /**
     * Get a JSON representation of detailed information on a Juniper
     * application to be monitored.
     *
     * @return a JSON representation of detailed information on a Juniper
     * application to be monitored
     * @throws MalformedURLException if the remote monitoring service URL is
     * invalid
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    @Override
    public String getApplicationDetails() throws MalformedURLException, IOException {
        // query a monitoring service via HTTP GET
        URLConnection connection = new URL(this.getMonitoringServiceURL() + "details/" + this.getApplicationId()).openConnection();
        connection.setRequestProperty("Accept", "application/json");
        try (InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);) {
            // transform the input stream into string
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }

    /**
     * Get all possible names of metrics stored in the local database cache.
     *
     * @return all possible names of metrics stored in the local database cache
     * @throws SQLException if a database access error occurs
     */
    @Override
    public String[] getMetricsNames() throws SQLException {
        try (Statement statement = this.monitoringDbActions.getDatabaseConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_SELECT_METRICS_NAME);) {
            ArrayList<String> resultStrings = new ArrayList<>();
            while (resultSet.next()) {
                resultStrings.add(resultSet.getString(1));
            }
            return resultStrings.toArray(new String[0]);
        }
    }

    /**
     * Get all values of a given metric stored in the local database cache.
     *
     * @param metricName a metric to get the values of
     * @return all values of a given metric stored in the local database cache
     * @throws SQLException if a database access error occurs
     */
    @Override
    public String[] getMetricValues(String metricName) throws SQLException {
        try (PreparedStatement preparedStatement
                = this.monitoringDbActions.getDatabaseConnection().prepareStatement(SQL_SELECT_NUMERIC_VALUE)) {
            preparedStatement.setString(1, metricName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<String> resultStrings = new ArrayList<>();
                while (resultSet.next()) {
                    resultStrings.add(resultSet.getString(1));
                }
                return resultStrings.toArray(new String[0]);
            }
        }
    }

    /**
     * Get all values of a given metric stored in the local database cache
     * having a given metric in <code>conditionName</code> set to value in
     * <code>conditionValue</code>.
     *
     * @param metricName a metric to get the values of
     * @param conditionName a metric to be meet the <code>conditionValue</code>
     * @param conditionValue a value of <code>conditionName</code> metric to
     * restric obtined values of the <code>metricName</code> metric
     * @return all values of a given metric stored by the monitoring service
     * having a given condition met
     * @throws SQLException if a database access error occurs
     */
    @Override
    public String[] getMetricValues(String metricName, String conditionName, String conditionValue) throws SQLException {
        final String metricNameLower = metricName.toLowerCase();
        final String conditionNameLower = conditionName.toLowerCase();
        final boolean metricNameIsTimestamp = "timestamp".equals(metricNameLower);
        final boolean metricNameIsType = "type".equals(metricNameLower);
        final boolean metricNameIsHostname = "hostname".equals(metricNameLower);
        // type-casting for prepared statement parameters substitution
        Double conditionValueDouble;
        try {
            conditionValueDouble = new Double(conditionValue);
        }
        catch (NumberFormatException ex) {
            conditionValueDouble = null;
        }
        Timestamp conditionValueTimestamp;
        try {
            conditionValueTimestamp = new Timestamp(Long.parseLong(conditionValue) * 1000);
        }
        catch (NumberFormatException ex) {
            conditionValueTimestamp = null;
        }
        // query
        if (metricNameIsTimestamp || metricNameIsType || metricNameIsHostname) {
            // metric is in records table
            try (PreparedStatement preparedStatement
                    = this.monitoringDbActions.getDatabaseConnection().prepareStatement(SQL_SELECT_METRIC_RECORDS)) {
                preparedStatement.setString(1, conditionName);
                preparedStatement.setDouble(2, conditionValueDouble);
                preparedStatement.setString(3, conditionValue);
                preparedStatement.setString(4, conditionNameLower);
                preparedStatement.setTimestamp(5, conditionValueTimestamp);
                preparedStatement.setString(6, conditionNameLower);
                preparedStatement.setString(7, conditionValue);
                preparedStatement.setString(8, conditionNameLower);
                preparedStatement.setString(9, conditionValue);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ArrayList<String> resultStrings = new ArrayList<>();
                    while (resultSet.next()) {
                        if (metricNameIsTimestamp) {
                            resultStrings.add(resultSet.getString(1));
                        } else if (metricNameIsType) {
                            resultStrings.add(resultSet.getString(2));
                        } else {
                            resultStrings.add(resultSet.getString(3));
                        }
                    }
                    return resultStrings.toArray(new String[0]);
                }
            }
        } else {
            // metric is in values table
            try (PreparedStatement preparedStatement
                    = this.monitoringDbActions.getDatabaseConnection().prepareStatement(SQL_SELECT_METRIC_VALUES)) {
                preparedStatement.setString(1, metricName);
                preparedStatement.setString(2, conditionName);
                preparedStatement.setDouble(3, conditionValueDouble);
                preparedStatement.setString(4, conditionValue);
                preparedStatement.setString(5, conditionNameLower);
                preparedStatement.setTimestamp(6, conditionValueTimestamp);
                preparedStatement.setString(7, conditionNameLower);
                preparedStatement.setString(8, conditionValue);
                preparedStatement.setString(9, conditionNameLower);
                preparedStatement.setString(10, conditionValue);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ArrayList<String> resultStrings = new ArrayList<>();
                    while (resultSet.next()) {
                        resultStrings.add(resultSet.getString(1));
                    }
                    return resultStrings.toArray(new String[0]);
                }
            }
        }
    }

    /**
     * Get aggregated values of a given metric stored in the local database
     * cache in a given time period.
     *
     * @param metricName a metric to get the values of
     * @param fromTimestamp a start of a given time period
     * @param toTimestamp an end of a given time period
     * @return aggregated values of a given metric stored by the monitoring
     * service in a given time period
     * @throws SQLException if a database access error occurs
     */
    @Override
    public AggregatedMetric getMetricAggregated(String metricName, long fromTimestamp, long toTimestamp) throws SQLException {
        try (PreparedStatement preparedStatement
                = this.monitoringDbActions.getDatabaseConnection().prepareStatement(SQL_SELECT_RECORD_STATS)) {
            preparedStatement.setString(1, metricName);
            preparedStatement.setTimestamp(2, new Timestamp(fromTimestamp * 1000));
            preparedStatement.setTimestamp(3, new Timestamp(toTimestamp * 1000));
            //System.out.println("# " + preparedStatement.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next()
                        ? new AggregatedMetric(resultSet.getInt(1), resultSet.getDouble(2), resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getDouble(5), resultSet.getDouble(6), resultSet.getDouble(7), resultSet.getDouble(8))
                        : null;
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if ((args.length < 3) || (args.length > 6)) {
            final String className = MonitoringDbService.class.getCanonicalName();
            System.err.println(""
                    + "Usage: " + className + " <monitoring-service-URL> aggr <start-time> <end-time>\n"
                    + "List all applications in the monitoring service, and their all aggregated metrics and values in the given time interval.\n"
                    + "\n"
                    + "Usage: " + className + " <monitoring-service-URL> aggr <start-time> <end-time> <application-id>\n"
                    + "List all aggregated metrics and values in the given time interval of the given application in the monitoring service.\n"
                    + "\n"
                    + "Usage: " + className + " <monitoring-service-URL> aggr <start-time> <end-time> <application-id> <metric-id>\n"
                    + "Get a value of the given aggregated metric in the given time interval of the given application in the monitoring service.\n"
                    + "\n"
                    + "Usage: " + className + " <monitoring-service-URL> vals <application-id> <metric-id>\n"
                    + "Get all values of the given metric of the given application in the monitoring service.\n"
                    + "\n"
                    + "Usage: " + className + " <monitoring-service-URL> vals <application-id> <metric-id> <cond-id> <cond-value>\n"
                    + "Get all values of the given metric of the given application in the monitoring service where the given condition has the given value.\n"
                    + "\n"
                    + "Usage: " + className + " <monitoring-service-URL> slct <application-id> <sql-select>\n"
                    + "Perform a given SQL select query on metrics of the given application.\n"
                    + "\n"
                    + "Usage: " + className + " <monitoring-service-URL> expt <application-id> <sql-filename>\n"
                    + "Export metrics of the given application into a given SQL script.\n"
            );
            System.exit(-1);
        }
        try {
            final String monitoringServiceURL = args[0];
            final String mode = args[1];
            switch (mode) {
                case "aggr": {
                    final long fromTimestamp = Long.parseLong(args[2]);
                    final long toTimestamp = Long.parseLong(args[3]);
                    final String[] applications = (args.length > 4)
                            ? new String[]{args[4]}
                            : MonitoringDbService.getApplications(monitoringServiceURL);
                    for (String applicationId : applications) {
                        System.out.println("********** " + applicationId + " **********");
                        final MonitoringServiceInterface monitoringService = new MonitoringDbService(monitoringServiceURL, applicationId);
                        System.out.println(monitoringService.getApplicationDetails() + "\n");
                        ((MonitoringDbService) monitoringService).getMonitoringDbActions().createDatabaseTables();
                        System.out.println("*** number of imported metrics = "
                                + ((MonitoringDbService) monitoringService).importMetrics());
                        final String[] metrics = (args.length > 5)
                                ? new String[]{args[5]}
                                : monitoringService.getMetricsNames();
                        for (String metricId : metrics) {
                            System.out.println("*** " + metricId + " = "
                                    + monitoringService.getMetricAggregated(metricId, fromTimestamp, toTimestamp));
                        }
                        System.out.println();
                    }
                }
                break;
                case "vals": {
                    final String applicationId = args[2];
                    final String metricId = args[3];
                    final MonitoringServiceInterface monitoringService = new MonitoringDbService(monitoringServiceURL, applicationId);
                    ((MonitoringDbService) monitoringService).getMonitoringDbActions().createDatabaseTables();
                    System.out.println("*** number of imported metrics = "
                            + ((MonitoringDbService) monitoringService).importMetrics());
                    if (args.length > 5) {
                        String conditionName = args[4];
                        String conditionValue = args[5];
                        System.out.println("*** " + metricId + " (where " + conditionName + " is " + conditionValue + ") =\n"
                                + Arrays.toString(monitoringService.getMetricValues(metricId, conditionName, conditionValue)));
                    } else {
                        System.out.println("*** " + metricId + " =\n"
                                + Arrays.toString(monitoringService.getMetricValues(metricId)));
                    }
                }
                break;
                case "slct": {
                    final String applicationId = args[2];
                    final String query = args[3];
                    final MonitoringDbService monitoringDbService = new MonitoringDbService(monitoringServiceURL, applicationId);
                    monitoringDbService.getMonitoringDbActions().createDatabaseTables();
                    System.out.println("*** number of imported metrics = "
                            + monitoringDbService.importMetrics());
                    try (Statement statement = monitoringDbService.getMonitoringDbActions().getDatabaseConnection().createStatement();
                            ResultSet resultSet = statement.executeQuery(query);) {
                        System.out.println("*** " + query);
                        while (resultSet.next()) {
                            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                                if (i > 1) {
                                    System.out.print(",\t");
                                }
                                System.out.print(resultSet.getString(i));
                            }
                            System.out.println();
                        }
                    }
                }
                break;
                case "expt": {
                    final String applicationId = args[2];
                    final String exportSqlScriptFilename = args[3];
                    final MonitoringDbService monitoringDbService = new MonitoringDbService(monitoringServiceURL, applicationId);
                    monitoringDbService.getMonitoringDbActions().createDatabaseTables();
                    System.out.println("*** number of imported metrics = "
                            + monitoringDbService.importMetrics());
                    System.out.println("*** exporting into " + exportSqlScriptFilename);
                    monitoringDbService.getMonitoringDbActions().exportDatabase(exportSqlScriptFilename);
                }
                break;
                default: {
                    System.err.println("Unknown parameter '" + mode + "'!");
                    System.exit(-2);
                }
            }
        }
        catch (ParseException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
