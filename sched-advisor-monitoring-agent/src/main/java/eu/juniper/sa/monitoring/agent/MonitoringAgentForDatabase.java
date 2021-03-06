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
package eu.juniper.sa.monitoring.agent;

import eu.juniper.sa.monitoring.resources.MonitoredResourcesStrategyInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * The class to send metric values to an SQL database over a JDBC connection.
 * The metric values sent may be both the resource utilization and custom values
 * and they can be send by dynamic methods of the class instances, with a
 * predefined JDBC connection and a predefined application ID.
 *
 * @author rychly
 */
public class MonitoringAgentForDatabase extends MonitoringAgentAbstract implements MonitoringAgentInterface, AutoCloseable {

    private PreparedStatement preparedStatementRecord;
    private PreparedStatement preparedStatementNumericValue;
    private PreparedStatement preparedStatementTextValue;
    private final static String SQL_INSERT_RECORD = "INSERT INTO records(time, metrictype, hostname) VALUES (?, ?, ?);";
    private final static String SQL_INSERT_NUMERIC_VALUE = "INSERT INTO metrics(recordid, name, numericvalue) VALUES (?, ?, ?);";
    private final static String SQL_INSERT_TEXT_VALUE = "INSERT INTO metrics(recordid, name, textvalue) VALUES (?, ?, ?);";
    private final Connection monitoringDatabaseConnection;
    private final boolean previousAutoCommit;

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring database connection with a given default monitored
     * resource strategy.
     *
     * @param monitoringDatabaseConnection a monitoring database connection (the
     * auto-commit on this connection will be temporarily turned off during the
     * monitoring)
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     * @throws java.sql.SQLException if the monitoring database connection
     * cannot be used
     */
    public MonitoringAgentForDatabase(Connection monitoringDatabaseConnection, String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) throws SQLException {
        super(applicationId, monitoredResourcesDefaultStrategy);
        this.monitoringDatabaseConnection = monitoringDatabaseConnection;
        this.previousAutoCommit = monitoringDatabaseConnection.getAutoCommit();
        setConnectionAndCreatePreparedStatements(monitoringDatabaseConnection);
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring database connection with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param monitoringDatabaseConnection a monitoring database connection (the
     * auto-commit on this connection will be temporarily turned off during the
     * monitoring)
     * @param applicationId an application ID
     * @throws java.sql.SQLException if the monitoring database connection
     * cannot be used
     */
    public MonitoringAgentForDatabase(Connection monitoringDatabaseConnection, String applicationId) throws SQLException {
        super(applicationId);
        this.monitoringDatabaseConnection = monitoringDatabaseConnection;
        this.previousAutoCommit = monitoringDatabaseConnection.getAutoCommit();
        setConnectionAndCreatePreparedStatements(monitoringDatabaseConnection);
    }

    private void setConnectionAndCreatePreparedStatements(Connection monitoringDatabaseConnection) throws SQLException {
        // trun off the auto-commit
        this.monitoringDatabaseConnection.setAutoCommit(false);
        // prepare statements for later
        this.preparedStatementRecord = monitoringDatabaseConnection.prepareStatement(SQL_INSERT_RECORD, Statement.RETURN_GENERATED_KEYS);
        this.preparedStatementNumericValue = monitoringDatabaseConnection.prepareStatement(SQL_INSERT_NUMERIC_VALUE);
        this.preparedStatementTextValue = monitoringDatabaseConnection.prepareStatement(SQL_INSERT_TEXT_VALUE);
    }

    /**
     * Close resources alocated by the class instance.
     *
     * @throws java.sql.SQLException if the resources of a database cannot be
     * closed
     */
    @Override
    public void close() throws SQLException {
        // set the auto-commit to the previous value
        this.monitoringDatabaseConnection.setAutoCommit(this.previousAutoCommit);
        // free prepared statement resources
        this.preparedStatementRecord.close();
        this.preparedStatementNumericValue.close();
        this.preparedStatementTextValue.close();
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the monitoring database.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @param timestampSec a timestamp in seconds of the metric set origin
     * @param hostname a hostname of a client sending the metric
     * @return null
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues, double timestampSec, String hostname) {
        try {
            Integer recId = null;
            // generate the metrics data header and send them into the database
            preparedStatementRecord.setTimestamp(1, new Timestamp((long) (timestampSec * 1000)));
            preparedStatementRecord.setString(2, metricType);
            preparedStatementRecord.setString(3, hostname);
            preparedStatementRecord.executeUpdate();
            try (ResultSet generatedKeys = preparedStatementRecord.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recId = generatedKeys.getInt(1);
                }
            }
            // generate the metrics data key-value pairs and send them into the database
            for (int i = 0; i < metricNames.length && i < metricValues.length; i++) {
                if ((metricNames[i] != null) && (metricValues[i] != null)) {
                    try {
                        // use the value as a double number value if possible
                        double doubleValue = Double.parseDouble(metricValues[i]);
                        preparedStatementNumericValue.setInt(1, recId);
                        preparedStatementNumericValue.setString(2, metricNames[i]);
                        preparedStatementNumericValue.setDouble(3, doubleValue);
                        preparedStatementNumericValue.executeUpdate();
                    } catch (NumberFormatException e) {
                        // use the value as a string value otherwise
                        preparedStatementTextValue.setInt(1, recId);
                        preparedStatementTextValue.setString(2, metricNames[i]);
                        preparedStatementTextValue.setString(3, metricValues[i]);
                        preparedStatementTextValue.executeUpdate();
                    }
                }
            }
            // commit the sent data in the database
            this.monitoringDatabaseConnection.commit();
        } catch (SQLException ex) {
            return ex.getMessage();
        }
        return null;
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the monitoring database. The method utilizes <code>getHostname()</code>
     * method of the default monitored resource strategy to get a hostname of a
     * client sending the metric.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @param timestampSec a timestamp in seconds of the metric set origin
     * @return null
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues, double timestampSec) {
        return this.sendMetric(metricType, metricNames, metricValues, timestampSec, this.getMonitoredResourcesDefaultStrategy().getHostname());
    }

    /**
     * Send the latest set of given metrics of a particular type from the
     * application to the monitoring database. The method utilizes methods of
     * the default monitored resource strategy, namely
     * <code>getTimestamp()</code> method to get a timestamp of metric to send
     * and <code>getHostname()</code> method to get a hostname of a client
     * sending the metric.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @return null
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues) {
        return this.sendMetric(metricType, metricNames, metricValues, this.getMonitoredResourcesDefaultStrategy().getTimestamp(), this.getMonitoredResourcesDefaultStrategy().getHostname());
    }
}
