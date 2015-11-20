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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Locale;

/**
 * The class to send metric values to an SQL file (to append new lines to the
 * SQL file with SQL insert statements). The metric values sent may be both the
 * resource utilization and custom values and they can be send by dynamic
 * methods of the class instances, with a predefined SQL file name and a
 * predefined application ID.
 *
 * @author rychly
 */
public class MonitoringAgentForSqlFile extends MonitoringAgentAbstract implements MonitoringAgentInterface, AutoCloseable {

    private final PrintWriter monitoringSqlPrintWriter;

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring SQL file with a given default monitored resource
     * strategy.
     *
     * @param monitoringSqlFileName a monitoring SQL file name
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     */
    public MonitoringAgentForSqlFile(String monitoringSqlFileName, String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) throws IOException {
        super(applicationId, monitoredResourcesDefaultStrategy);
        this.monitoringSqlPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(monitoringSqlFileName, true)));
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring SQL file with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param monitoringSqlFileName a monitoring SQL file name
     * @param applicationId an application ID
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     */
    public MonitoringAgentForSqlFile(String monitoringSqlFileName, String applicationId) throws IOException {
        super(applicationId);
        this.monitoringSqlPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(monitoringSqlFileName, true)));
    }

    /**
     * Close resources alocated by the class instance.
     */
    @Override
    public void close() {
        this.monitoringSqlPrintWriter.close();
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the monitoring SQL file.
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
        final String formatRecord = "INSERT INTO records(time, metrictype, hostname) VALUES ('%s', '%s', '%s');\n";
        final String formatNumericValue = "INSERT INTO metrics(recordid, name, numericvalue) VALUES (IDENTITY(), '%s', %f);\n";
        final String formatTextValue = "INSERT INTO metrics(recordid, name, textvalue) VALUES (IDENTITY(), '%s', '%s');\n";
        this.monitoringSqlPrintWriter.println("-- ApplicationID: " + this.getApplicationId());
        // generate the metrics data header
        this.monitoringSqlPrintWriter.printf(Locale.ROOT, formatRecord,
                new Timestamp((long) (timestampSec * 1000)), metricType, hostname);
        // generate the metrics data key-value pairs
        for (int i = 0; i < metricNames.length && i < metricValues.length; i++) {
            if ((metricNames[i] != null) && (metricValues[i] != null)) {
                try {
                    // print the value as a double number value if possible
                    double doubleValue = Double.parseDouble(metricValues[i]);
                    this.monitoringSqlPrintWriter.printf(Locale.ROOT, formatNumericValue,
                            metricNames[i], doubleValue);
                }
                catch (NumberFormatException e) {
                    // print the value as a string value otherwise
                    this.monitoringSqlPrintWriter.printf(Locale.ROOT, formatTextValue,
                            metricNames[i], metricValues[i]);
                }
            }
        }
        this.monitoringSqlPrintWriter.flush();
        return null;
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the monitoring SQL file. The method utilizes <code>getHostname()</code>
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
     * application to the monitoring SQL file. The method utilizes methods of
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
