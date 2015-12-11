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
package eu.juniper.sa.tool;

import eu.juniper.sa.deployment.model.JuniperApplication;
import eu.juniper.sa.deployment.plan.XMLDeploymentPlan;
import eu.juniper.sa.deployment.plan.XMLDeploymentPlanException;
import eu.juniper.sa.deployment.monitor.MonitoringDbService;
import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsAbstract;
import eu.juniper.sa.deployment.monitor.db.MonitoringDbActionsFactory;
import eu.juniper.sa.tool.utils.ClassFinder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.xml.stream.XMLStreamException;

/**
 * The main class of the scheduling advisor that is able to provide advice for a
 * given deployment plan and monitoring data source.
 *
 * @author rychly
 */
public class Advisor {

    private enum SecondArgType {

        MONITORING_SERVICE_URL,
        SQL_DUMP_FILEPATH,
        JDBC_TO_MONITORING_DB
    }

    private final static String PLUGINS_PACKAGE = "eu.juniper.sa.tool.plugins";
    private final static String PROPERTY_NAME_KEEP_DB_TEMP_FILE = "KeepDbTempFile";

    public static void main(String[] args) throws FileNotFoundException, XMLStreamException, XMLDeploymentPlanException, ClassNotFoundException, SQLException, IOException, AdvisorException, MonitoringDbActionsFactory.UnkownJdbcDatabase {
        if ((args.length < 3) || (args.length > 5)) {
            final String className = Advisor.class.getCanonicalName();
            System.err.println(""
                    + "Usage: " + className + " <deployment-plan-xml> <monitoring-service-URL> <output-advice-xml> [start-time] [end-time]\n"
                    + "Produce a list of advices and exports it into an output XML file for the given deployment plan of a Juniper application and its monitoring data provided by the given monitoring service.\n"
                    + "\n"
                    + "Usage: " + className + " <deployment-plan-xml> <monitoring-data-sql-dump> <output-advice-xml> [start-time] [end-time]\n"
                    + "Produce a list of advices exports it into an output XML file for the given deployment plan of a Juniper application and its monitoring data provided in an SQL dump file.\n"
                    + "\n"
                    + "Usage: " + className + " <deployment-plan-xml> <monitoring-JDBC-connection-string> <output-advice-xml> [start-time] [end-time]\n"
                    + "Produce a list of advices exports it into an output XML file for the given deployment plan of a Juniper application and its monitoring data provided by a database accessed via the given JDBC connection string.\n"
                    + "\n"
                    + "Properties (-D<property>=<value> Java parameters):\n"
                    + "* properties of loaded advisor plugins can be set by Java system properties, e.g., -DAdvisorOutOfMemoryPrediction.disabled, -DAdvisorDataTransferOverhead.receivingToExecutionDurationRatio=0.25, etc.; run 'eu.juniper.sa.tool.utils.ClassFinder eu.juniper.sa.tool.plugins' for a list of available properties\n"
                    + "* use " + PROPERTY_NAME_KEEP_DB_TEMP_FILE + " (i.e., -D" + PROPERTY_NAME_KEEP_DB_TEMP_FILE + ") to keep a temporary database file for the database cache\n"
                    + "* JDBC username and password can be set as"
                    + " -D" + MonitoringDbActionsAbstract.SYSTEM_PROPERTY_NAME_FOR_JDBC_USER + "=username and"
                    + " -D" + MonitoringDbActionsAbstract.SYSTEM_PROPERTY_NAME_FOR_JDBC_PASSWORD + "=password\n"
                    + "\n"
            );
            System.exit(-1);
        }

        final String deploymentPlan = args[0];
        final String secondArg = args[1];
        final String outputFile = args[2];

        SecondArgType secondArgType;
        if (secondArg.startsWith("http://") || secondArg.startsWith("https://")) {
            secondArgType = SecondArgType.MONITORING_SERVICE_URL;
        } else if (secondArg.startsWith("jdbc:")) {
            secondArgType = SecondArgType.JDBC_TO_MONITORING_DB;
        } else {
            secondArgType = SecondArgType.SQL_DUMP_FILEPATH;
        }

        final boolean jdbcUriTemp = (secondArgType != SecondArgType.JDBC_TO_MONITORING_DB);
        final String jdbcUri = !jdbcUriTemp
                ? secondArg
                : "jdbc:h2:" + System.getProperty("java.io.tmpdir")
                + File.separator + Advisor.class.getCanonicalName() + "." + UUID.randomUUID().toString()
                + ";COMPRESS=TRUE";

        System.out.println("*** processing deployment plan " + deploymentPlan);
        final JuniperApplication juniperApplication = XMLDeploymentPlan.readJuniperApplication(deploymentPlan);

        System.out.println("*** openning/creating JDBC database for monitoring results cache " + jdbcUri);
        try (MonitoringDbService monitoringDbService = new MonitoringDbService(
                (secondArgType == SecondArgType.MONITORING_SERVICE_URL) ? secondArg : null,
                juniperApplication.getApplicationName(), jdbcUri)) {

            switch (secondArgType) {
                case JDBC_TO_MONITORING_DB: {
                    System.out.println("*** utilizing metrics already stored in the database");
                }
                break;
                case MONITORING_SERVICE_URL: {
                    monitoringDbService.getMonitoringDbActions().createDatabaseTables();
                    System.out.println("*** importing metrics from " + secondArg);
                    System.out.println("*** number of imported metrics = "
                            + monitoringDbService.importMetrics());
                }
                break;
                case SQL_DUMP_FILEPATH: {
                    monitoringDbService.getMonitoringDbActions().createDatabaseTables();
                    System.out.println("*** importing metrics from " + secondArg);
                    monitoringDbService.getMonitoringDbActions().importDatabase(secondArg);
                }
                break;
            }

            System.out.println("*** loading and executing plugins from package " + PLUGINS_PACKAGE);
            List<Advice> allAdvice = new ArrayList<>();
            for (Class<?> advisorClass : ClassFinder.getClassesForPackage(PLUGINS_PACKAGE)) {
                if (AdvisorUsingDatabaseAbstract.class.isAssignableFrom(advisorClass)) {
                    System.out.println("*** loading and setting advisor plugin " + advisorClass.getCanonicalName());
                    // create new instance of advisorClass
                    final AdvisorInterface advisorInstance = AdvisorUsingDatabaseAbstract.newInstance(advisorClass, juniperApplication, monitoringDbService.getMonitoringDbActions().getDatabaseConnection());
                    ((AdvisorUsingDatabaseAbstract) advisorInstance).setObjectProperties(System.getProperties());
                    if (advisorInstance.isEnabled()) {
                        // print information on the advisor
                        System.out.println("\n*** executing advisor plugin " + advisorInstance.getName() + " with the following description:\n" + advisorInstance.getDescription() + "\n");
                        // execute the advisor
                        final Advice[] adviceArray = advisorInstance.execute();
                        for (Advice advice : adviceArray) {
                            System.out.println(advice.toString());
                        }
                        allAdvice.addAll(Arrays.asList(adviceArray));
                    }
                }
            }
            System.out.println("\n*** writing the list of advice into XML file " + outputFile);
            Advice.writeAdviceArray(allAdvice.toArray(new Advice[0]), outputFile, juniperApplication);

            if (jdbcUriTemp && (System.getProperty(Advisor.PROPERTY_NAME_KEEP_DB_TEMP_FILE) == null)) {
                System.out.println("*** removing database files with monitoring results cache");
                monitoringDbService.getMonitoringDbActions().deleteDatabase();
            }

        }
    }
}
