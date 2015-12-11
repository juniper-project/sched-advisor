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

import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsAbstract;
import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsForH2;
import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsInterface;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import org.h2.tools.Server;

/**
 * The class implementing a local database server for monitoring monitoring
 * agents to receive their reported monitoring data.
 *
 * @author rychly
 */
public class MonitoringDbServer {

    final static String KEEP_RUNNING_SYSTEM_PROPERTY_NAME = "KeepRunning";

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        if (args.length < 1) {
            final String className = MonitoringDbServer.class.getCanonicalName();
            System.err.println(""
                    + "Usage: " + className + " [h2-server-args ...] <h2-db-file-or-jdbc-uri>\n"
                    + "Run a H2 database TCP server with given options (optional) and open or create a database in a given file.\n"
                    + "The database is ready to be used by monitoring agents.\n"
                    + "Supported options for the server are: -tcpPort, -tcpSSL, -tcpPassword, -tcpAllowOthers, -tcpDaemon, -trace, -ifExists, -baseDir, -key.\n"
                    + "Use system property " + KEEP_RUNNING_SYSTEM_PROPERTY_NAME + ", i.e. -D" + KEEP_RUNNING_SYSTEM_PROPERTY_NAME + ", to keep the server running forever (until killed).\n"
                    + "JDBC username and password can be set by system properties as"
                    + " -D" + MonitoringDbActionsAbstract.SYSTEM_PROPERTY_NAME_FOR_JDBC_USER + "=username and"
                    + " -D" + MonitoringDbActionsAbstract.SYSTEM_PROPERTY_NAME_FOR_JDBC_PASSWORD + "=password."
            );
            System.exit(-1);
        }
        // run the server
        System.out.println("*** Running a H2 database TCP server...");
        Server server = Server.createTcpServer(Arrays.copyOf(args, args.length - 1)).start();
        System.out.println("*** " + server.getStatus());
        // create the database
        final String argsLast = args[args.length - 1];
        final String jdbcUrl = argsLast.startsWith("jdbc:")
                ? argsLast.replace(":h2:", ":h2:" + server.getURL() + "/")
                : "jdbc:h2:" + server.getURL() + "/" + (new File(argsLast)).getAbsolutePath() + ";COMPRESS=TRUE";
        System.out.println("*** Openning/creating JDBC database for monitoring results cache:\n"
                + "*** " + jdbcUrl);
        MonitoringDbActionsInterface monitoringDbActions = new MonitoringDbActionsForH2(jdbcUrl);
        System.out.println("*** Creating database tables (if not exist)...");
        monitoringDbActions.createDatabaseTables();
        // wait for clients
        System.out.println("*** Waiting for clients connecting to the JDBC URL above...");
        // waiting for Enter or (in)finite waiting in a loop
        if (System.getProperty(KEEP_RUNNING_SYSTEM_PROPERTY_NAME) != null) {
            System.out.println("*** (kill the application to close the database, shutdown the server, and quit)");
            final Long sleepTimeMilis = Long.getLong(KEEP_RUNNING_SYSTEM_PROPERTY_NAME, Long.MAX_VALUE);
            while (true) {
                try {
                    Thread.sleep(sleepTimeMilis);
                    System.out.println("*** still waiting...");
                }
                catch (InterruptedException ex) {
                    // NOP
                }
            }
        } else {
            System.out.println("*** (press Enter to close the database, shutdown the server, and quit)");
            System.in.read();
        }
        // shut down
        System.out.println("*** Stopping the H2 database TCP server...");
        server.stop();
        // done
        System.out.println("*** Done.");
    }
}
