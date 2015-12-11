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
package eu.juniper.sa.deployment.monitor.db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Methods of actions that are specific for H2 database utilized for a local
 * database cache or a storage of monitoring data.
 *
 * @author rychly
 */
public class MonitoringDbActionsForH2 extends MonitoringDbActionsAbstract implements MonitoringDbActionsInterface {

    /**
     * Class of a JDBC driver for the database for the monitoring data.
     */
    public static final String JDBC_DRIVER_CLASS_NAME = "org.h2.Driver";

    /**
     * Default JDB URL for the database for the monitoring data.
     */
    public static final String JDBC_DEFAULT_URL = "jdbc:h2:mem:";

    // the size of VARCHARs is not limited, only the actual data is persisted
    private static final String SQL_CREATE_TABLE_RECORDS = "CREATE TABLE IF NOT EXISTS records ("
            // "SERIAL" is equivalent to "int IDENTITY" in H2 (undocumented) and compatible with PostgreSQL (documented)
            + "id SERIAL, "
            + "time timestamp NOT NULL, "
            + "metrictype varchar, "
            + "hostname varchar NOT NULL, "
            // disabled due to fast generating agents that may break this constraint (timestamp has insufficient precision)
            //+ "CONSTRAINT unique_records UNIQUE (time, metrictype, hostname), "
            + "PRIMARY KEY (id)"
            + ");";
    private static final String SQL_CREATE_TABLE_METRICS = "CREATE TABLE IF NOT EXISTS metrics ("
            + "recordid int NOT NULL, "
            + "name varchar NOT NULL, "
            + "numericvalue double precision, "
            + "textvalue varchar, "
            + "CONSTRAINT not_null_metric_value CHECK (numericvalue IS NOT NULL OR textvalue IS NOT NULL), "
            + "CONSTRAINT record_has_recorded_metrics FOREIGN KEY (recordid) REFERENCES records (id) ON UPDATE Cascade ON DELETE Cascade, "
            + "PRIMARY KEY (recordid, name)"
            + ");";
    //private static final String SQL_CREATE_ALIAS_SECONDS = "CREATE ALIAS IF NOT EXISTS seconds "
    //        + "DETERMINISTIC AS $$ long seconds(Timestamp timestamp) { return timestamp.getTime() / 1000; } $$;";
    //private static final String SQL_DROP_ALIAS_SECONDS = "DROP ALIAS IF EXISTS seconds;";
    private static final String SQL_DROP_TABLE_METRICS = "DROP TABLE IF EXISTS metrics;";
    private static final String SQL_DROP_TABLE_RECORDS = "DROP TABLE IF EXISTS records;";
    private static final String SQL_DROP_ALL_AND_DELETE = "DROP ALL OBJECTS DELETE FILES;";
    private static final String SQL_DELETE_FROM_RECORDS = "DELETE FROM records;";

    /**
     * Create an actions provider for a particular JDBC connection.
     *
     * @param databaseConnection the particular JDBC connection to use in the
     * actions provider
     */
    public MonitoringDbActionsForH2(Connection databaseConnection) {
        super(databaseConnection);
    }

    /**
     * Create an actions provider for a particular JDBC URL.
     *
     * @param dbURL the particular JDBC URL to use in the actions provider
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     */
    public MonitoringDbActionsForH2(String dbURL) throws ClassNotFoundException, SQLException {
        super(dbURL);
    }

    /**
     * Create an actions provider for the default JDBC URL.
     *
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     */
    public MonitoringDbActionsForH2() throws ClassNotFoundException, SQLException {
        super();
    }

    /**
     * Get a class name of a JDBC driver for the database for the monitoring
     * data.
     *
     * @return the class name of a JDBC driver for database for the monitoring
     * data.
     */
    @Override
    public String getJdbcDriverClassName() {
        return JDBC_DRIVER_CLASS_NAME;
    }

    /**
     * Get a default JDB URL for the database for the monitoring data.
     *
     * @return the default JDB URL for database for the monitoring data.
     */
    @Override
    public String getJdbcDefaultUrl() {
        return JDBC_DEFAULT_URL;
    }

    /**
     * Create tables of the database for the monitoring data.
     *
     * @param databaseConnection a JDBC connection to the database for the
     * monitoring data
     * @throws SQLException if a database access error occurs
     */
    public static void createDatabaseTables(Connection databaseConnection) throws SQLException {
        try (final Statement statement = databaseConnection.createStatement()) {
            statement.addBatch(SQL_CREATE_TABLE_RECORDS);
            statement.addBatch(SQL_CREATE_TABLE_METRICS);
            // disabled as it is not utilized and due to non-standardized SQL
            //statement.addBatch(SQL_CREATE_ALIAS_SECONDS);
            statement.executeBatch();
        }
    }

    /**
     * Create tables of the database for the monitoring data.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void createDatabaseTables() throws SQLException {
        createDatabaseTables(this.databaseConnection);
    }

    /**
     * Drop tables of the database for the monitoring data.
     *
     * @param databaseConnection a JDBC connection to the database for the
     * monitoring data
     * @throws SQLException if a database access error occurs
     */
    public static void dropDatabaseTables(Connection databaseConnection) throws SQLException {
        try (final Statement statement = databaseConnection.createStatement()) {
            //statement.addBatch(SQL_DROP_ALIAS_SECONDS);
            statement.addBatch(SQL_DROP_TABLE_METRICS);
            statement.addBatch(SQL_DROP_TABLE_RECORDS);
            statement.executeBatch();
        }
    }

    /**
     * Drop tables of the database for the monitoring data.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void dropDatabaseTables() throws SQLException {
        dropDatabaseTables(this.databaseConnection);
    }

    /**
     * Export data from the database for the monitoring data to a local file as
     * an SQL dump.
     *
     * @param databaseConnection a JDBC connection to the database for the
     * monitoring data
     * @param exportSqlScriptFilename a local file to export the monitorign data
     * as an SQL dump
     * @throws SQLException if a database access error occurs
     */
    public static void exportDatabase(Connection databaseConnection, String exportSqlScriptFilename) throws SQLException {
        try (final Statement statement = databaseConnection.createStatement()) {
            statement.execute("SCRIPT NOPASSWORDS NOSETTINGS DROP TO '" + exportSqlScriptFilename + "';");
        }
    }

    /**
     * Export data from the database for the monitoring data to a local file as
     * an SQL dump.
     *
     * @param exportSqlScriptFilename a local file to export the monitorign data
     * as an SQL dump
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void exportDatabase(String exportSqlScriptFilename) throws SQLException {
        exportDatabase(this.databaseConnection, exportSqlScriptFilename);
    }

    /**
     * Import data from a local file as an SQL dump into the database for the
     * monitoring data. Method <code>createDatabaseTables()</code> should be
     * invoked before to create tables for the imported data.
     *
     * @param databaseConnection a JDBC connection to the database for the
     * monitoring data
     * @param importSqlScriptFilename a local file as an SQL dump to import data
     * from
     * @throws SQLException if a database access error occurs
     */
    public static void importDatabase(Connection databaseConnection, String importSqlScriptFilename) throws SQLException {
        final int lastSeparatorIndex = importSqlScriptFilename.lastIndexOf(File.separatorChar);
        final int lastDotIndex = importSqlScriptFilename.lastIndexOf('.');
        final String suffix = (lastDotIndex > lastSeparatorIndex) ? importSqlScriptFilename.substring(lastDotIndex + 1) : "";
        String compression;
        switch (suffix) {
            case "deflate":
                compression = " COMPRESSION DEFLATE";
                break;
            case "lzf":
                compression = " COMPRESSION LZF";
                break;
            case "zip":
                compression = " COMPRESSION ZIP";
                break;
            case "gz":
            case "gzip":
                compression = " COMPRESSION GZIP";
                break;
            default:
                compression = "";
        }
        try (final Statement statement = databaseConnection.createStatement()) {
            statement.execute("RUNSCRIPT FROM '" + importSqlScriptFilename + "'" + compression + ";");
        }
    }

    /**
     * Import data from a local file as an SQL dump into the database for the
     * monitoring data. Method <code>createDatabaseTables()</code> should be
     * invoked before to create tables for the imported data.
     *
     * @param importSqlScriptFilename a local file as an SQL dump to import data
     * from
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void importDatabase(String importSqlScriptFilename) throws SQLException {
        importDatabase(this.databaseConnection, importSqlScriptFilename);
    }

    /**
     * Delete database from a database server.
     *
     * @param databaseConnection a JDBC connection to the database for the
     * monitoring data
     * @throws SQLException if a database access error occurs
     */
    public void deleteDatabase(Connection databaseConnection) throws SQLException {
        try (final Statement statement = databaseConnection.createStatement()) {
            statement.execute(SQL_DROP_ALL_AND_DELETE);
        }
    }

    /**
     * Delete the database from a database server.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void deleteDatabase() throws SQLException {
        deleteDatabase(this.databaseConnection);
    }

    /**
     * Delete all data from the database tables at a database server.
     *
     * @param databaseConnection a JDBC connection to the database for the
     * monitoring data
     * @throws SQLException if a database access error occurs
     */
    public static void cleanDatabaseTables(Connection databaseConnection) throws SQLException {
        try (final Statement statement = databaseConnection.createStatement()) {
            statement.execute(SQL_DELETE_FROM_RECORDS);
        }
    }

    /**
     * Delete all data from the database tables at a database server.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void cleanDatabaseTables() throws SQLException {
        cleanDatabaseTables(this.databaseConnection);
    }

}
