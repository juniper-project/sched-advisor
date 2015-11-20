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

import java.io.IOException;

/**
 * The interface of sensors for data connections between Juniper programs.
 *
 * @author rychly
 */
public interface DataConnectionSensorInterface {

    /**
     * Should be invoked just after
     * <code>JuniperProgram.transferData(...)</code> method with a size of
     * transferred data in bytes.
     *
     * @param sizeOfReceivedData a size of transferred data in bytes
     * @return a duration of the transfer in seconds
     * @throws IllegalStateException if the method is invoked before
     * <code>*Starts()</code> method
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    double receiveEnds(Double sizeOfReceivedData) throws IllegalStateException, IOException;

    /**
     * Should be invoked just after
     * <code>JuniperProgram.transferData(...)</code> method.
     *
     * @return a duration of this method in seconds (an overhead of the method
     * to be subtracted from surrounding measurements)
     * @throws IllegalStateException if the method is invoked before
     * <code>*Starts()</code> method
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    double receiveEnds() throws IllegalStateException, IOException;

    /**
     * Should be invoked just before
     * <code>JuniperProgram.transferData(...)</code> method.
     */
    void receiveStarts();

    /**
     * Subtracts a given duration from the overall duration between
     * <code>*Starts()</code> and <code>*Ends(...)</code>. Can be invoked
     * repeatedly to perform more substractions.
     *
     * @param duration a given duration in seconds to be subtracted from the overall
     * duration
     */
    void subtract(double duration);
}
