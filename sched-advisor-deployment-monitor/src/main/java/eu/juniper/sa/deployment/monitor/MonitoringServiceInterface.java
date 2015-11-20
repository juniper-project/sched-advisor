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

import java.io.IOException;
import java.sql.SQLException;
import org.json.simple.parser.ParseException;

/**
 * The interface of monitoring service client implementations.
 *
 * @author rychly
 */
public interface MonitoringServiceInterface {

    /**
     * Get an URL of the monitoring service if defined.
     *
     * @return an URL of the monitoring service if defined, null otherwise
     */
    String getMonitoringServiceURL();

    /**
     * Get a name (or an ID) of a Juniper application to be monitored.
     *
     * @return a name (or an ID) of a Juniper application to be monitored
     */
    String getApplicationId();

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
    String getApplicationDetails() throws ParseException, IOException;

    /**
     * Get all possible names of metrics stored by the monitoring service.
     *
     * @return all possible names of metrics stored by the monitoring service
     * @throws ParseException if a JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     * @throws SQLException if a data provided by local database cache cannot be
     * obtained
     */
    String[] getMetricsNames() throws ParseException, IOException, SQLException;

    /**
     * Get all values of a given metric stored by the monitoring service.
     *
     * @param metricName a metric to get the values of
     * @return all values of a given metric stored by the monitoring service
     * @throws ParseException if a JSON information provided by the monitoring
     * service cannot be parsed
     * @throws IOException if the monitoring service cannot be accessed to
     * retrieve the data
     * @throws SQLException if a data provided by local database cache cannot be
     * obtained
     */
    String[] getMetricValues(String metricName) throws ParseException, IOException, SQLException;

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
     * @throws SQLException if a data provided by local database cache cannot be
     * obtained
     */
    String[] getMetricValues(String metricName, String conditionName, String conditionValue) throws ParseException, IOException, SQLException;

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
     * @throws SQLException if a data provided by local database cache cannot be
     * obtained
     */
    AggregatedMetric getMetricAggregated(String metricName, long fromTimestamp, long toTimestamp) throws ParseException, IOException, SQLException;

}
