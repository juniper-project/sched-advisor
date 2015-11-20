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

import eu.juniper.sa.monitoring.sensor.DataConnectionSensorInterface;
import eu.juniper.sa.monitoring.sensor.NullSensor;
import eu.juniper.sa.monitoring.sensor.ProgramInstanceSensorInterface;

/**
 * The class to send metric values to the null device.
 *
 * @author rychly
 */
public class MonitoringAgentForNullDevice extends MonitoringAgentAbstract implements MonitoringAgentInterface {

    /**
     * Implicit constructor as the null device target does not require any
     * setting.
     */
    public MonitoringAgentForNullDevice() {
        super(null, null);
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the null device.
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
        return null;
    }

    /**
     * Send a set of given metrics of a particular type from the application to
     * the null device.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @param timestampSec a timestamp in seconds of the metric set origin
     * @return null
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues, double timestampSec) {
        return null;
    }

    /**
     * Send the latest set of given metrics of a particular type from the
     * application to the null device.
     *
     * @param metricType a type of metrics in the set to send
     * @param metricNames names of metrics in the set to send
     * @param metricValues values of metrics in the set to send
     * @return null
     */
    @Override
    public String sendMetric(String metricType, String[] metricNames, String[] metricValues) {
        return null;
    }

    /**
     * Create a sensor for a data connection between Juniper programs that will
     * utilize this monitoring agent.
     *
     * @param receiverGlobalRank an MPI global rank of a receiving Juniper
     * program of a monitored connection (value of
     * <code>JuniperProgram.myGlobalRank</code>)
     * @param connectionName a name of an incomming monitored connection of a
     * Juniper program (value used in
     * <code>JuniperProgram.transferData(...)</code>)
     * @return a sensor for a data connection between Juniper programs that will
     * utilize this monitoring agent
     */
    @Override
    public DataConnectionSensorInterface createDataConnectionSensor(int receiverGlobalRank, String connectionName) {
        return new NullSensor(this);
    }

    /**
     * Create a sensor for a Juniper program instance that will utilize this
     * monitoring agent.
     *
     * @param programGlobalRank an MPI global rank of a monitored Juniper
     * program (value of <code>JuniperProgram.myGlobalRank</code>)
     * @return a sensor that will utilize this monitoring agent
     */
    @Override
    public ProgramInstanceSensorInterface createProgramInstanceSensor(int programGlobalRank) {
        return new NullSensor(this);
    }
}
