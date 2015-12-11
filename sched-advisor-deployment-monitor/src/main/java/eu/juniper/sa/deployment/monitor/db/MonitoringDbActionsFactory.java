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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.OperationNotSupportedException;

/**
 * Factory for <code>MonitoringDbActionsFor*</code> objects creation base on
 * various conditions.
 *
 * @author rychly
 */
public class MonitoringDbActionsFactory {

    static public class UnkownJdbcDatabase extends Exception {

        public UnkownJdbcDatabase(String message) {
            super(message);
        }
    }

    /**
     * Create an object implementing <code>MonitoringDbActionsInterface</code>
     * based on a provided JDBC URL and connection.
     *
     * @param jdbcURL a provided JDBC URL to analyze for the creation of the
     * object
     * @param jdbcConnection a provided JDBC connection to use for the creation
     * of the object
     * @return the resulting object
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     * @throws UnkownJdbcDatabase if the provided JDBC URL cannot be analyzed
     */
    public static MonitoringDbActionsInterface createMonitoringDbActionsFor(String jdbcURL, Connection jdbcConnection) throws ClassNotFoundException, SQLException, UnkownJdbcDatabase {
        final String elements[] = jdbcURL.split(":");
        if (elements.length >= 2) {
            switch (elements[1]) {
                //case "firebirdsql": // org.firebirdsql.jdbc.FBDriver
                //    return jdbcConnection == null ? new MonitoringDbActionsForFirebird(jdbcURL) : new MonitoringDbActionsForFirebird(jdbcConnection);
                //case "derby": // org.apache.derby.jdbc.ClientDriver
                //    return jdbcConnection == null ? new MonitoringDbActionsForDerby(jdbcURL) : new MonitoringDbActionsForDery(jdbcConnection);
                case "h2": // org.h2.Driver
                    return jdbcConnection == null ? new MonitoringDbActionsForH2(jdbcURL) : new MonitoringDbActionsForH2(jdbcConnection);
                //case "sqlite": // org.sqlite.JDBC
                //    return jdbcConnection == null ? new MonitoringDbActionsForSQLite(jdbcURL) : new MonitoringDbActionsForSQLite(jdbcConnection);
                //case "db2": // COM.ibm.db2.jdbc.net.DB2Driver
                //    return jdbcConnection == null ? new MonitoringDbActionsForDB2(jdbcURL) : new MonitoringDbActionsForDB2(jdbcConnection);
                //case "oracle": // oracle.jdbc.driver.OracleDriver
                //    return jdbcConnection == null ? new MonitoringDbActionsForOracle(jdbcURL) : new MonitoringDbActionsForOracle(jdbcConnection);
                //case "microsoft": // com.microsoft.jdbc.sqlserver.SQLServerDriver (v2000)
                //    return jdbcConnection == null ? new MonitoringDbActionsForSQLServer2000(jdbcURL) : new MonitoringDbActionsForSQLServer2000(jdbcConnection);
                //case "sqlserver": // com.microsoft.sqlserver.jdbc.SQLServerDriver (v2005)
                //    return jdbcConnection == null ? new MonitoringDbActionsForSQLServer2005(jdbcURL) : new MonitoringDbActionsForSQLServer2005(jdbcConnection);
                case "postgresql": // org.postgresql.Driver
                    return jdbcConnection == null ? new MonitoringDbActionsForPgSQL(jdbcURL) : new MonitoringDbActionsForPgSQL(jdbcConnection);
                //case "mysql": // com.mysql.jdbc.Driver
                //    return jdbcConnection == null ? new MonitoringDbActionsForMySQL(jdbcURL) : new MonitoringDbActionsForMySQL(jdbcConnection);
                //case "hsqldb": // org.hsqldb.jdbcDriver
                //    return jdbcConnection == null ? new MonitoringDbActionsForHSQLDB(jdbcURL) : new MonitoringDbActionsForHSQLDB(jdbcConnection);
            }
        }
        throw new UnkownJdbcDatabase("Unkown JDBC database in " + jdbcURL);
    }

    /**
     * Create an object implementing <code>MonitoringDbActionsInterface</code>
     * based on a provided JDBC URL (create a new JDBC connection).
     *
     * @param jdbcURL a provided JDBC URL to analyze for the creation of the
     * object
     * @return the resulting object
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     * @throws UnkownJdbcDatabase if the provided JDBC URL cannot be analyzed
     */
    public static MonitoringDbActionsInterface createMonitoringDbActionsFor(String jdbcURL) throws ClassNotFoundException, SQLException, UnkownJdbcDatabase {
        return createMonitoringDbActionsFor(jdbcURL, null);
    }

    /**
     * Create an object implementing <code>MonitoringDbActionsInterface</code>
     * based on a provided JDBC connection.
     *
     * @param jdbcConnection a provided JDBC connection to analyze and use for
     * the creation of the object
     * @return the resulting object
     * @throws ClassNotFoundException if class name of a JDBC driver for the
     * database cannot be found
     * @throws SQLException if a database access error occurs
     * @throws UnkownJdbcDatabase if the provided JDBC URL cannot be analyzed
     */
    public static MonitoringDbActionsInterface createMonitoringDbActionsFor(Connection jdbcConnection) throws ClassNotFoundException, SQLException, UnkownJdbcDatabase {
        return createMonitoringDbActionsFor(jdbcConnection.getMetaData().getURL(), jdbcConnection);
    }

    public static void main(String[] args) throws OperationNotSupportedException, UnkownJdbcDatabase {
        if (args.length < 2) {
            final String className = MonitoringDbActionsFactory.class.getCanonicalName();
            System.err.println(""
                    + "Usage: " + className + " <jdbc-url> action1 [param1] [action2 [param2] ...]\n"
                    + "Connect to a target database via a given JDBC URL and perform a given sequence of actions that can be the following:\n"
                    + "* (create|drop|clean): to create, drop, or clean tables for monitoring data (cleaning the tables means to delete all their data)\n"
                    + "* (import|export) <file.sql>: to import data from or export data to a given SQL script file\n"
                    + "* query <sql-query>: to execute a given SQL query\n"
                    + "\n"
                    + "JDBC username and password can be set by system properties as"
                    + " -D" + MonitoringDbActionsAbstract.SYSTEM_PROPERTY_NAME_FOR_JDBC_USER + "=username and"
                    + " -D" + MonitoringDbActionsAbstract.SYSTEM_PROPERTY_NAME_FOR_JDBC_PASSWORD + "=password."
            );
            System.exit(-1);
        }
        final String jdbcURL = args[0];
        System.out.println("*** Connecting the database via " + jdbcURL);
        try {
            final MonitoringDbActionsInterface monitoringDbActions
                    = createMonitoringDbActionsFor(jdbcURL);
            int pos = 1;
            while (pos < args.length) {
                final String action = args[pos++];
                switch (action) {
                    case "create": {
                        System.out.println("*** Creating tables...");
                        monitoringDbActions.createDatabaseTables();
                    }
                    break;
                    case "drop": {
                        System.out.println("*** Droping tables...");
                        monitoringDbActions.dropDatabaseTables();
                    }
                    break;
                    case "clean": {
                        System.out.println("*** Cleaning tables (deleting all data)...");
                        monitoringDbActions.cleanDatabaseTables();
                    }
                    break;
                    case "import": {
                        final String file = args[pos++];
                        System.out.println("*** Importing from file " + file);
                        monitoringDbActions.importDatabase(file);
                    }
                    break;
                    case "export": {
                        final String file = args[pos++];
                        System.out.println("*** Exporting into file " + file);
                        monitoringDbActions.exportDatabase(file);
                    }
                    break;
                    case "query": {
                        final String query = args[pos++];
                        System.out.println("*** Executing query: " + query);
                        try (Statement statement = monitoringDbActions.getDatabaseConnection().createStatement();
                                ResultSet resultSet = statement.executeQuery(query);) {
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
                    default: {
                        System.err.println("Unknown action: " + action);
                        System.exit(-2);
                    }
                }
            }
            System.err.println("*** Done (" + (pos - 1) + " actions or parameters processed)");
        }
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            if (ex instanceof SQLException) {
                for (SQLException nextEx = ((SQLException) ex).getNextException();
                        nextEx != null; nextEx = nextEx.getNextException()) {
                    nextEx.printStackTrace();
                }
            }
        }
    }

}
