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
package eu.juniper.sa.monitoring.sensor;

import eu.juniper.sa.monitoring.resources.MonitoredResourcesStrategyInterface;
import eu.juniper.sa.monitoring.agent.MonitoringAgentInterface;
import java.io.IOException;
import java.util.Locale;

/**
 * The class of a sensor for a data connection between Juniper programs. Methods
 * of the class produce metric records in the following format (all timestamps,
 * times, and durations are in seconds)  <code>{ "Timestamp": timestamp_of_transfer_end,
 * "hostname": hostname_of_juniper_program_node, "type": "SendReceive",
 * "ReceiverGlobalRank": global_rank_of_receiving_program,
 * "ConnectionName": name_of_connection,
 * "ReceiveStartTimestamp": timestamp_of_the_receive_start,
 * "SendReceiveDuration": duration_of_the_transfer,
 * "ReceivedData": size_of_transferred_data_in_bytes,
 * "AverageSpeedBytesPerSecond": average_speed_of_the_transfer_in_bytes_per_second }</code>.
 *
 * @author rychly
 */
public class DataConnectionSensor extends AbstractSensor implements DataConnectionSensorInterface {

    private final int receiverGlobalRank;
    private final String connectionName;
    private Double receiveStartsTimestamp;

    /**
     * Create a sensor that will utilize a given monitoring agent.
     *
     * @param monitoringAgent a monitoring agent to be utilized by the sensor
     * @param monitoredResourcesStrategy a monitored resources strategy to be
     * utilized by the sensor
     * @param receiverGlobalRank an MPI global rank of a receiving Juniper
     * program of a monitored connection (value of
     * <code>JuniperProgram.myGlobalRank</code>)
     * @param connectionName a name of an incomming monitored connection of a
     * Juniper program (value used in
     * <code>JuniperProgram.transferData(...)</code>)
     */
    public DataConnectionSensor(MonitoringAgentInterface monitoringAgent, MonitoredResourcesStrategyInterface monitoredResourcesStrategy, int receiverGlobalRank, String connectionName) {
        super(monitoringAgent, monitoredResourcesStrategy);
        this.receiverGlobalRank = receiverGlobalRank;
        this.connectionName = connectionName;
    }

    /**
     * Should be invoked just before
     * <code>JuniperProgram.transferData(...)</code> method.
     */
    @Override
    public void receiveStarts() {
        this.durationToSubtract = 0;
        this.receiveStartsTimestamp = this.getMonitoredResourcesStrategy().getTimestamp();
    }

    /**
     * Should be invoked just after
     * <code>JuniperProgram.transferData(...)</code> method with a size of
     * transferred data in bytes.
     *
     * @param sizeOfReceivedData a size of transferred data in bytes
     * @return a duration of this method in seconds (an overhead of the method
     * to be subtracted from surrounding measurements)
     * @throws IllegalStateException if the method is invoked before
     * <code>*Starts()</code> method
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    @Override
    public double receiveEnds(Double sizeOfReceivedData) throws IllegalStateException, IOException {
        // generate and send monitoring data
        final double methodDuration = this.reportMonitoringData(sizeOfReceivedData);
        // null the sensor
        this.receiveStartsTimestamp = null;
        return methodDuration;
    }

    /**
     * Should be invoked just after
     * <code>JuniperProgram.transferData(...)</code> method.
     *
     * @return a duration of the transfer in seconds
     * @throws IllegalStateException if the method is invoked before
     * <code>*Starts()</code> method
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    @Override
    public double receiveEnds() throws IllegalStateException, IOException {
        return this.receiveEnds(null);
    }

    private double reportMonitoringData(Double sizeOfReceivedData) throws IllegalStateException, IOException {
        // the end timestamp should be taken just after a program so it cannnot be affected by the measuring and reporting below
        final double receiveEndsTimestamp = this.getMonitoredResourcesStrategy().getTimestamp();
        //
        if (this.receiveStartsTimestamp == null) {
            throw new IllegalStateException("The method must follow a previous invocation of receiveStarts() method.");
        }
        final double receiveDuration = receiveEndsTimestamp - this.receiveStartsTimestamp - this.durationToSubtract;
        final String[] metricNames = {
            "ReceiverGlobalRank",
            "ConnectionName",
            "ReceiveStartTimestamp",
            "SendReceiveDuration",
            "ReceivedData",
            "AverageSpeedBytesPerSecond"
        };
        final String[] metricValues = {
            String.format(Locale.ROOT, "%d", this.receiverGlobalRank),
            this.connectionName,
            String.format(Locale.ROOT, "%f", this.receiveStartsTimestamp),
            String.format(Locale.ROOT, "%f", receiveDuration),
            (sizeOfReceivedData == null) ? null : String.format(Locale.ROOT, "%f", sizeOfReceivedData),
            (sizeOfReceivedData == null) ? null : String.format(Locale.ROOT, "%f", sizeOfReceivedData / receiveDuration)
        };
        this.getMonitoringAgent().sendMetric("SendReceive", metricNames, metricValues, receiveEndsTimestamp, this.getMonitoredResourcesStrategy().getHostname());
        return this.getMonitoredResourcesStrategy().getTimestamp() - receiveEndsTimestamp;
    }
}
