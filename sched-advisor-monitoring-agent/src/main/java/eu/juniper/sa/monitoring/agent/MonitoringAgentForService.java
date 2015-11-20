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
import eu.juniper.sa.monitoring.resources.MonitoredResourcesDefaultStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Scanner;

/**
 * The class to send metric values to a monitoring service. The metric values
 * sent may be both the resource utilization and custom values and they can be
 * send in two ways by static methods of the class, with a given metric service
 * URL and a given application ID, and by dynamic methods of the class
 * instances, with a predefined metric service URL and a predefined application
 * ID.
 *
 * @author rychly
 */
public class MonitoringAgentForService extends MonitoringAgentAbstract implements MonitoringAgentInterface {
    
    private final String monitoringServiceURL;

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring service with a given default monitored resource
     * strategy.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     */
    public MonitoringAgentForService(String monitoringServiceURL, String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) {
        super(applicationId, monitoredResourcesDefaultStrategy);
        this.monitoringServiceURL = monitoringServiceURL;
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring service with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     */
    public MonitoringAgentForService(String monitoringServiceURL, String applicationId) {
        super(applicationId);
        this.monitoringServiceURL = monitoringServiceURL;
    }

    /**
     * Get the monitoring service URL.
     *
     * @return the monitoring service URL
     */
    public String getMonitoringServiceURL() {
        return this.monitoringServiceURL;
    }

    /**
     * Send a set of given metrics of a particular type from an application to a
     * monitoring service.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @param timestampSec a timestamp in seconds of the metric set origin
     * @param hostname a hostname of a client sending the metric
     * @return the monitoring service response
     * @throws MalformedURLException if <code>monitoringServiceURL</code> is
     * malformed
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    public static String sendMetric(String monitoringServiceURL, String applicationId, String metricType, String[] metricNames, String[] metricValues, double timestampSec, String hostname) throws MalformedURLException, IOException {
        // access a monitoring service via HTTP POST
        URLConnection connection = new URL(monitoringServiceURL + applicationId).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        ((HttpURLConnection) connection).setRequestMethod("POST");
        try (OutputStream outputStream = connection.getOutputStream()) {
            // prepare the metrics data header
            String postString = String.format(Locale.ROOT, "{ "
                    + "\"Timestamp\": %f, "
                    + "\"hostname\": \"%s\", "
                    + "\"type\": \"%s\"",
                    timestampSec,
                    hostname,
                    metricType);
            // prepare the metrics data key-value pairs
            for (int i = 0; i < metricNames.length && i < metricValues.length; i++) {
                if ((metricNames[i] != null) && (metricValues[i] != null)) {
                    try {
                        // send the value as a double number value if possible
                        postString += String.format(Locale.ROOT, ", \"%s\": %f",
                                metricNames[i],
                                Double.parseDouble(metricValues[i]));
                    }
                    catch (NumberFormatException e) {
                        // send the value as a string value otherwise
                        postString += String.format(Locale.ROOT, ", \"%s\": \"%s\"",
                                metricNames[i],
                                metricValues[i]);
                    }
                }
            }
            postString += " }";
            // send the prepared
            outputStream.write(postString.getBytes("UTF-8"));
        }
        try (InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);) {
            // transform the input stream into string
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }

    /**
     * Send a set of given metrics of a particular type from an application to a
     * monitoring service. The method utilizes
     * <code>MonitoredResourcesDefaultStrategy.getHostname()</code> method to
     * get a hostname of a client sending the metric.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @param timestampSec a timestamp in seconds of the metric set origin
     * @return the monitoring service response
     * @throws MalformedURLException if <code>monitoringServiceURL</code> is
     * malformed
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    public static String sendMetric(String monitoringServiceURL, String applicationId, String metricType, String[] metricNames, String[] metricValues, double timestampSec) throws MalformedURLException, IOException {
        return MonitoringAgentForService.sendMetric(monitoringServiceURL, applicationId, metricType, metricNames, metricValues, timestampSec, new MonitoredResourcesDefaultStrategy().getHostname());
    }

    /**
     * Send the latest set of given metrics of a particular type from an
     * application to a monitoring service. The method utilizes
     * <code>MonitoredResourcesDefaultStrategy.getTimestamp()</code> method to
     * get a timestamp of metric to send and
     * <code>MonitoredResourcesDefaultStrategy.getHostname()</code> method to
     * get a hostname of a client sending the metric.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @return the monitoring service response
     * @throws MalformedURLException if <code>monitoringServiceURL</code> is
     * malformed
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    public static String sendMetric(String monitoringServiceURL, String applicationId, String metricType, String[] metricNames, String[] metricValues) throws MalformedURLException, IOException {
        return MonitoringAgentForService.sendMetric(monitoringServiceURL, applicationId, metricType, metricNames, metricValues, new MonitoredResourcesDefaultStrategy().getTimestamp(), new MonitoredResourcesDefaultStrategy().getHostname());
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the monitoring service.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @param timestampSec a timestamp in seconds of the metric set origin
     * @param hostname a hostname of a client sending the metric
     * @return the monitoring service response
     * @throws MalformedURLException if <code>monitoringServiceURL</code> is
     * malformed
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues, double timestampSec, String hostname) throws MalformedURLException, IOException {
        return MonitoringAgentForService.sendMetric(this.monitoringServiceURL, this.getApplicationId(), metricType, metricNames, metricValues, timestampSec, hostname);
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the monitoring service. The method utilizes <code>getHostname()</code>
     * method of the default monitored resource strategy to get a hostname of a
     * client sending the metric.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @param timestampSec a timestamp in seconds of the metric set origin
     * @return the monitoring service response
     * @throws MalformedURLException if <code>monitoringServiceURL</code> is
     * malformed
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues, double timestampSec) throws MalformedURLException, IOException {
        return MonitoringAgentForService.sendMetric(this.monitoringServiceURL, this.getApplicationId(), metricType, metricNames, metricValues, timestampSec, this.getMonitoredResourcesDefaultStrategy().getHostname());
    }

    /**
     * Send the latest set of given metrics of a particular type from the
     * application to the monitoring service. The method utilizes methods of the
     * default monitored resource strategy, namely <code>getTimestamp()</code>
     * method to get a timestamp of metric to send and
     * <code>getHostname()</code> method to get a hostname of a client sending
     * the metric.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @return the monitoring service response
     * @throws MalformedURLException if <code>monitoringServiceURL</code> is
     * malformed
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues) throws MalformedURLException, IOException {
        return MonitoringAgentForService.sendMetric(this.monitoringServiceURL, this.getApplicationId(), metricType, metricNames, metricValues, this.getMonitoredResourcesDefaultStrategy().getTimestamp(), this.getMonitoredResourcesDefaultStrategy().getHostname());
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length < 5) {
            final String className = MonitoringAgentForService.class.getCanonicalName();
            System.err.println(""
                    + "Usage: " + className + "\n"
                    + "List detected resource utilization of the current JVM process.\n"
                    + "\n"
                    + "Usage: " + className + " <monitoring-service-URL> <application-id> <metric-type> <1st-metric-name> <1st-metric-value> [2nd-metric-name] [2nd-metric-value] [...]\n"
                    + "Send/report the current given metric set of an application to a monitoring service.\n"
            );
            MonitoredResourcesDefaultStrategy.main(args);
        } else {
            final MonitoringAgentForService monitoringAgent = new MonitoringAgentForService(args[0], args[1]);
            final String metricType = args[2];
            String[] metricNames = new String[(args.length - 3) / 2];
            String[] metricValues = new String[(args.length - 3) / 2];
            for (int i = 3; i < args.length; i += 2) {
                metricNames[(i - 3) / 2] = args[i];
                metricValues[(i - 3) / 2 + 1] = args[i + 1];
            }
            System.out.println("Sending the metric set...");
            final String result = monitoringAgent.sendMetric(metricType, metricNames, metricValues);
            System.out.println("Monitoring service result:\n" + result);
        }
    }
}
