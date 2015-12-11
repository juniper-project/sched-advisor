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
 * Methods of actions that are specific for PostgreSQL database utilized for a
 * local database cache or a storage of monitoring data. Basic DDL statements
 * (without CREATE/DROP ALIAS) are compatible with the H2 database. DML
 * statements for IMPORT/EXPORT needs to be reimplemented.
 *
 * @author rychly
 */
public class MonitoringDbActionsForPgSQL extends MonitoringDbActionsForH2 implements MonitoringDbActionsInterface {

    /**
     * Create an actions provider for a particular JDBC connection.
     *
     * @param databaseConnection the particular JDBC connection to use in the
     * actions provider
     */
    public MonitoringDbActionsForPgSQL(Connection databaseConnection) {
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
    public MonitoringDbActionsForPgSQL(String dbURL) throws ClassNotFoundException, SQLException {
        super(dbURL);
    }

    /**
     * Create an actions provider for the default JDBC URL.
     *
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     */
    public MonitoringDbActionsForPgSQL() throws ClassNotFoundException, SQLException {
        super();
    }

    /**
     * Delete the database from a database server.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void deleteDatabase(Connection databaseConnection) throws SQLException {
        throw new UnsupportedOperationException("Method not implemented.");
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
        throw new UnsupportedOperationException("Method not implemented.");
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
        throw new UnsupportedOperationException("Method not implemented.");
    }

}
