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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Methods of actions for JDBC relational databases utilized for a local
 * database cache or a storage of monitoring data.
 *
 * @author rychly
 */
public abstract class MonitoringDbActionsAbstract implements MonitoringDbActionsInterface {

    /**
     * System property name for a username for a JDBC connection to the database
     * for the monitoring data.
     */
    public static final String SYSTEM_PROPERTY_NAME_FOR_JDBC_USER = "MonitoringJdbcUser";
    /**
     * System property name for a password for a JDBC connection to the database
     * for the monitoring data.
     */
    public static final String SYSTEM_PROPERTY_NAME_FOR_JDBC_PASSWORD = "MonitoringJdbcPassword";

    protected final Connection databaseConnection;

    /**
     * Create an actions provider for a particular JDBC connection.
     *
     * @param databaseConnection the particular JDBC connection to use in the
     * actions provider
     */
    public MonitoringDbActionsAbstract(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Create an actions provider for a particular JDBC URL.
     *
     * @param dbURL the particular JDBC URL to use in the actions provider
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     */
    public MonitoringDbActionsAbstract(String dbURL) throws ClassNotFoundException, SQLException {
        this.databaseConnection = openDatabaseConnection(dbURL);
    }

    /**
     * Create an actions provider for the default JDBC URL.
     *
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     */
    public MonitoringDbActionsAbstract() throws ClassNotFoundException, SQLException {
        this.databaseConnection = openDatabaseConnection();
    }

    /**
     * Get a JDBC connection for the database of the monitoring data.
     *
     * @return the JDBC connection to the database
     */
    @Override
    public final Connection getDatabaseConnection() {
        return this.databaseConnection;
    }

    /**
     * Open a new JDBC connection to a given URL.
     *
     * @param dbURL the URL of a new JDBC connection to open
     * @return the new JDBC connection
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public final Connection openDatabaseConnection(String dbURL) throws ClassNotFoundException, SQLException {
        // load driver classs
        Class.forName(this.getJdbcDriverClassName());
        // load JDBC user and password from system properties
        final String jdbcUser = System.getProperty(SYSTEM_PROPERTY_NAME_FOR_JDBC_USER);
        final String jdbcPassword = System.getProperty(SYSTEM_PROPERTY_NAME_FOR_JDBC_PASSWORD);
        final Properties properties = new Properties();
        if (jdbcUser != null) {
            properties.setProperty("user", jdbcUser);
        }
        if (jdbcPassword != null) {
            properties.setProperty("password", jdbcPassword);
        }
        // connect
        return DriverManager.getConnection(dbURL == null ? this.getJdbcDefaultUrl() : dbURL, properties);
    }

    /**
     * Open a new JDBC connection to a default URL.
     *
     * @return the new JDBC connection
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public final Connection openDatabaseConnection() throws ClassNotFoundException, SQLException {
        return openDatabaseConnection(null);
    }

    /**
     * Close the database for the monitoring result cache.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public final void closeDatabaseConenction() throws SQLException {
        if (!this.databaseConnection.isClosed()) {
            this.databaseConnection.close();
        }
    }

}
