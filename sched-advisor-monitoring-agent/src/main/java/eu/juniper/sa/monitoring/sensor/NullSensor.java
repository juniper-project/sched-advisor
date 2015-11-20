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

import eu.juniper.sa.monitoring.agent.MonitoringAgentInterface;

/**
 * The class of a null sensor for a data connection between Juniper programs and
 * for a Juniper program instance. The sensor does nothing, it is just to make
 * its methods callable in utilizing objects.
 *
 * @author rychly
 */
public class NullSensor extends AbstractSensor implements DataConnectionSensorInterface, ProgramInstanceSensorInterface {

    /**
     * Create a null sensor that will utilize a given monitoring agent.
     *
     * @param monitoringAgent a monitoring agent to be utilized by the sensor
     * @throws IllegalArgumentException if <code>monitoringAgent</code>
     * parameter is null
     */
    public NullSensor(MonitoringAgentInterface monitoringAgent) throws IllegalArgumentException {
        super(monitoringAgent, null);
    }

    /**
     * Should be invoked just before
     * <code>JuniperProgram.transferData(...)</code> method.
     */
    @Override
    public void receiveStarts() {
        // NOP
    }

    /**
     * Should be invoked just after
     * <code>JuniperProgram.transferData(...)</code> method with a size of
     * transferred data in bytes.
     *
     * @param sizeOfReceivedData a size of transferred data in bytes
     * @return 0 as a duration of this method in seconds (an overhead of the
     * method to be subtracted from surrounding measurements)
     */
    @Override
    public double receiveEnds(Double sizeOfReceivedData) {
        // NOP
        return 0D;
    }

    /**
     * Should be invoked just after
     * <code>JuniperProgram.transferData(...)</code> method.
     *
     * @return 0 as a duration of this method in seconds (an overhead of the
     * method to be subtracted from surrounding measurements)
     */
    @Override
    public double receiveEnds() {
        // NOP
        return 0D;
    }

    /**
     * Should be invoked just at the beginning of
     * <code>JuniperProgram.run(...)</code> method implementation.
     */
    @Override
    public void programStarts() {
        // NOP
    }

    /**
     * May be invoked during <code>JuniperProgram.run(...)</code> method
     * implementation if the sensors data need to be reported before a monitored
     * program ends (before a final call of <code>programEnds()</code> method).
     *
     * @return 0 as a duration of this method in seconds (an overhead of the
     * method to be subtracted from surrounding measurements)
     */
    @Override
    public double programDoesNotEnd() {
        // NOP
        return 0D;
    }

    /**
     * Should be invoked just at the end of <code>JuniperProgram.run(...)</code>
     * method implementation.
     *
     * @return 0 as a duration of this method in seconds (an overhead of the
     * method to be subtracted from surrounding measurements)
     */
    @Override
    public double programEnds() {
        // NOP
        return 0D;
    }

    /**
     * Get the current duration from the last <code>*Starts()</code> invocation
     * in seconds.
     *
     * @return 0 as a duration in seconds
     */
    @Override
    public double getCurrentDuration() {
        return 0D;
    }
}
