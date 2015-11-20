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

import eu.juniper.sa.monitoring.resources.MonitoredResourcesDefaultStrategy;
import eu.juniper.sa.monitoring.resources.MonitoredResourcesStrategyInterface;
import eu.juniper.sa.monitoring.sensor.DataConnectionSensor;
import eu.juniper.sa.monitoring.sensor.DataConnectionSensorInterface;
import eu.juniper.sa.monitoring.sensor.ProgramInstanceSensor;
import eu.juniper.sa.monitoring.sensor.ProgramInstanceSensorInterface;

/**
 * The abstract class for monitoring agent implementations.
 *
 * @author rychly
 */
public abstract class MonitoringAgentAbstract implements MonitoringAgentInterface {

    protected final String applicationId;
    protected final MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy;

    /**
     * Create a monitorign agent for monitoring of a given application with a
     * given default monitored resource strategy.
     *
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     */
    public MonitoringAgentAbstract(String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) {
        this.applicationId = applicationId;
        this.monitoredResourcesDefaultStrategy = monitoredResourcesDefaultStrategy;
    }

    /**
     * Create a monitorign agent for monitoring of a given application with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param applicationId an application ID
     */
    public MonitoringAgentAbstract(String applicationId) {
        this(applicationId, new MonitoredResourcesDefaultStrategy());
    }

    /**
     * Get the application ID.
     *
     * @return the application ID
     */
    @Override
    public String getApplicationId() {
        return this.applicationId;
    }

    /**
     * Get the default monitored resource strategy used by the agent.
     *
     * @return the default monitored resource strategy used by the agent
     */
    @Override
    public MonitoredResourcesStrategyInterface getMonitoredResourcesDefaultStrategy() {
        return this.monitoredResourcesDefaultStrategy;
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
        return new DataConnectionSensor(this, this.monitoredResourcesDefaultStrategy, receiverGlobalRank, connectionName);
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
        return new ProgramInstanceSensor(this, this.monitoredResourcesDefaultStrategy, programGlobalRank);
    }

}
