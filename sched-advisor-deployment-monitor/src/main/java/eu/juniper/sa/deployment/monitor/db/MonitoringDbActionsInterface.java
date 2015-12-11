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
import java.sql.SQLException;

/**
 * Interface with methods of actions that are specific for particular types of
 * JDBC databases utilized for a local database cache or a storage of monitoring
 * data.
 *
 * @author rychly
 */
public interface MonitoringDbActionsInterface {

    /**
     * Create tables of the database for the monitoring data.
     *
     * @throws SQLException if a database access error occurs
     */
    void createDatabaseTables() throws SQLException;

    /**
     * Delete the database from a database server.
     *
     * @throws SQLException if a database access error occurs
     */
    void deleteDatabase() throws SQLException;

    /**
     * Delete all data from the database tables at a database server.
     *
     * @throws SQLException if a database access error occurs
     */
    void cleanDatabaseTables() throws SQLException;

    /**
     * Drop tables of the database for the monitoring data.
     *
     * @throws SQLException if a database access error occurs
     */
    void dropDatabaseTables() throws SQLException;

    /**
     * Export data from the database for the monitoring data to a local file as
     * an SQL dump.
     *
     * @param exportSqlScriptFilename a local file to export the monitorign data
     * as an SQL dump
     * @throws SQLException if a database access error occurs
     */
    void exportDatabase(String exportSqlScriptFilename) throws SQLException;

    /**
     * Import data from a local file as an SQL dump into the database for the
     * monitoring data. Method <code>createDatabaseTables()</code> should be
     * invoked before to create tables for the imported data.
     *
     * @param importSqlScriptFilename a local file as an SQL dump to import data
     * from
     * @throws SQLException if a database access error occurs
     */
    void importDatabase(String importSqlScriptFilename) throws SQLException;

    /**
     * Get a JDBC connection for the database of the monitoring data.
     *
     * @return the JDBC connection to the database
     */
    Connection getDatabaseConnection();

    /**
     * Get a class name of a JDBC driver for the database for the monitoring
     * data.
     *
     * @return the class name of a JDBC driver for database for the monitoring
     * data.
     */
    String getJdbcDriverClassName();

    /**
     * Get a default JDB URL for the database for the monitoring data.
     *
     * @return the default JDB URL for database for the monitoring data.
     */
    String getJdbcDefaultUrl();

    /**
     * Open a new JDBC connection to a given URL.
     *
     * @param dbURL the URL of a new JDBC connection to open
     * @return the new JDBC connection
     * @throws java.lang.ClassNotFoundException if class name of a JDBC driver
     * for the database cannot be found
     * @throws java.sql.SQLException if a database access error occurs
     */
    Connection openDatabaseConnection(String dbURL) throws ClassNotFoundException, SQLException;

    /**
     * Open a new JDBC connection to a default URL.
     *
     * @return the new JDBC connection
     * @throws java.lang.ClassNotFoundException if class name of a JDBC driver
     * for the database cannot be found
     * @throws java.sql.SQLException if a database access error occurs
     */
    Connection openDatabaseConnection() throws ClassNotFoundException, SQLException;

    /**
     * Close the database for the monitoring result cache.
     *
     * @throws SQLException if a database access error occurs
     */
    void closeDatabaseConenction() throws SQLException;

}
