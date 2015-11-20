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

/**
 * The abstract super-class of individual sensor classes.
 *
 * @author rychly
 */
public abstract class AbstractSensor {

    private final MonitoringAgentInterface monitoringAgent;
    private final MonitoredResourcesStrategyInterface monitoredResourcesStrategy;

    /**
     * A duration in seconds to be subtracted from the overall duration between
     * <code>*Starts()</code> and <code>*Ends(...)</code>.
     */
    protected double durationToSubtract = 0;

    /**
     * Create a sensor that will utilize a given monitoring agent.
     *
     * @param monitoringAgent a monitoring agent to be utilized by the sensor
     * @param monitoredResourcesStrategy a monitored resources strategy to be
     * utilized by the sensor
     * @throws IllegalArgumentException if <code>monitoringAgent</code>
     * parameter is null
     */
    public AbstractSensor(MonitoringAgentInterface monitoringAgent, MonitoredResourcesStrategyInterface monitoredResourcesStrategy) throws IllegalArgumentException {
        if (monitoringAgent == null) {
            throw new IllegalArgumentException("The monitoringAgent parameter cannot be null.");
        }
        this.monitoringAgent = monitoringAgent;
        this.monitoredResourcesStrategy = monitoredResourcesStrategy;
    }

    /**
     * Get a monitoring agent to be utilized by the sensor.
     *
     * @return a monitoring agent to be utilized by the sensor
     */
    public MonitoringAgentInterface getMonitoringAgent() {
        return monitoringAgent;
    }

    /**
     * Get a monitored resources strategy to be utilized by the sensor.
     *
     * @return a monitored resources strategy to be utilized by the sensor
     */
    public MonitoredResourcesStrategyInterface getMonitoredResourcesStrategy() {
        return monitoredResourcesStrategy;
    }

    /**
     * Subtracts a given duration from the overall duration between
     * <code>*Starts()</code> and <code>*Ends(...)</code>. Can be invoked
     * repeatedly to perform more substractions.
     *
     * @param duration a given duration in seconds to be subtracted from the
     * overall duration
     */
    public void subtract(double duration) {
        this.durationToSubtract += duration;
    }

}
