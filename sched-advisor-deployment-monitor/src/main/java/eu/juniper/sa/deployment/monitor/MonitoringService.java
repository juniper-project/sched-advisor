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

import eu.juniper.MonitoringLib;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import org.json.simple.parser.ParseException;

/**
 * The class implementing a monitoring service client by utilization of
 * eu.juniper.MonitoringLib library.
 *
 * @author rychly
 */
public class MonitoringService implements MonitoringServiceInterface {

    private final MonitoringLib monitoringLib;
    private final String monitoringServiceURL;
    private final String applicationId;

    /**
     * Create an object of a monitoring service client accessing a remote
     * monitoring service via eu.juniper.MonitoringLib library.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     */
    public MonitoringService(String monitoringServiceURL, String applicationId) {
        this.applicationId = applicationId;
        this.monitoringServiceURL = monitoringServiceURL;
        // initiate monitoring library
        // (<code>applicationId</code> in <code>MonitoringLib</code> is needed just for reporting to, not for querying of, the monitoring service)
        this.monitoringLib = new MonitoringLib(this.monitoringServiceURL, this.applicationId);
        // fix temp directory
        Field field;
        try {
            field = MonitoringLib.class.getDeclaredField("filepath");
            field.setAccessible(true);
            field.set(this.monitoringLib, System.getProperty("java.io.tmpdir") + File.separator);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            // ignore
        }
    }

    /**
     * Get an eu.juniper.MonitoringLib instance to access a remote monitoring
     * service.
     *
     * @return an eu.juniper.MonitoringLib instance to access a remote
     * monitoring service
     */
    protected MonitoringLib getMonitoringLib() {
        return this.monitoringLib;
    }

    /**
     * Get an URL of the monitoring service if defined.
     *
     * @return an URL of the monitoring service if defined, null otherwise
     */
    @Override
    public String getMonitoringServiceURL() {
        return this.monitoringServiceURL;
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
        MonitoringService monitoringService = new MonitoringService(monitoringServiceURL, null);
        return monitoringService.monitoringLib.getApplications().toArray(new String[0]);
    }

    /**
     * Get a JSON representation of detailed information on a Juniper
     * application to be monitored.
     *
     * @return a JSON representation of detailed information on a Juniper
     * application to be monitored
     * @throws ParseException if the JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    @Override
    public String getApplicationDetails() throws ParseException, IOException {
        return this.monitoringLib.getApplicationDetails(this.applicationId);
    }

    /**
     * Get all possible names of metrics stored by the monitoring service.
     *
     * @return all possible names of metrics stored by the monitoring service
     * @throws ParseException if a JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    @Override
    public String[] getMetricsNames() throws ParseException, IOException {
        return this.monitoringLib.getApplicationMetrics(this.applicationId).toArray(new String[0]);
    }

    /**
     * Get aggregated values of a given metric stored by the monitoring service
     * in a given time period.
     *
     * @param metricName a metric to get the values of
     * @param timeInterval a string formated as a start and an end of a given
     * time period separated by a slash character
     * @return aggregated values of a given metric stored by the monitoring
     * service in a given time period
     * @throws ParseException if a JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    protected AggregatedMetric getMetricAggregated(String metricName, String timeInterval) throws ParseException, IOException {
        return new AggregatedMetric(this.monitoringLib.getMetricAggregated(this.applicationId, metricName, timeInterval));
    }

    /**
     * Get aggregated values of a given metric stored by the monitoring service
     * in a given time period.
     *
     * @param metricName a metric to get the values of
     * @param fromTimestamp a start of a given time period
     * @param toTimestamp an end of a given time period
     * @return aggregated values of a given metric stored by the monitoring
     * service in a given time period
     * @throws ParseException if a JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    @Override
    public AggregatedMetric getMetricAggregated(String metricName, long fromTimestamp, long toTimestamp) throws ParseException, IOException {
        return this.getMetricAggregated(metricName, fromTimestamp + "/" + toTimestamp);
    }

    /**
     * Get all values of a given metric stored by the monitoring service.
     *
     * @param metricName a metric to get the values of
     * @return all values of a given metric stored by the monitoring service
     * @throws ParseException if a JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    @Override
    public String[] getMetricValues(String metricName) throws ParseException, IOException {
        return this.monitoringLib.getAppMetricsValues(this.applicationId, metricName).toArray(new String[0]);
    }

    /**
     * Get all values of a given metric stored by the monitoring service having
     * a given metric in <code>conditionName</code> set to value in
     * <code>conditionValue</code>.
     *
     * @param metricName a metric to get the values of
     * @param conditionName a metric to be meet the <code>conditionValue</code>
     * @param conditionValue a value of <code>conditionName</code> metric to
     * restric obtined values of the <code>metricName</code> metric
     * @return all values of a given metric stored by the monitoring service
     * having a given condition met
     * @throws ParseException if a JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     */
    @Override
    public String[] getMetricValues(String metricName, String conditionName, String conditionValue) throws ParseException, IOException {
        return this.monitoringLib.getAppMetricsValuesByCondition(this.applicationId, metricName, conditionName, conditionValue).toArray(new String[0]);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if ((args.length < 4) || (args.length > 6)) {
            final String className = MonitoringService.class.getCanonicalName();
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
                            : MonitoringService.getApplications(monitoringServiceURL);
                    for (String applicationId : applications) {
                        System.out.println("********** " + applicationId + " **********");
                        final MonitoringServiceInterface monitoringService = new MonitoringService(monitoringServiceURL, applicationId);
                        System.out.println(monitoringService.getApplicationDetails() + "\n");
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
                    final MonitoringServiceInterface monitoringService = new MonitoringService(monitoringServiceURL, applicationId);
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
