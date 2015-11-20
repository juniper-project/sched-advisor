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
package eu.juniper.sa.tool;

import java.sql.Timestamp;

/**
 * The interface for individual advisor implementations executed accroding to
 * Command design pattern.
 *
 * @author rychly
 */
public interface AdvisorInterface {

    /**
     * Get a name of the advisor.
     *
     * @return a name of the advisor
     */
    String getName();

    /**
     * Get a description of the advisor.
     *
     * @return a description of the advisor
     */
    String getDescription();

    /**
     * Check if the advisor is enabled.
     *
     * @return <code>true</code> if the advisor is enabled, <code>false</code>
     * otherwise
     */
    boolean isEnabled();

    /**
     * Enable the advisor.
     *
     * @param enabled <code>true</code> to enable the advisor,
     * <code>false</code> to disable the advisor
     */
    void setEnabled(boolean enabled);

    /**
     * Check if the advisor is disabled.
     *
     * @return <code>true</code> if the advisor is disabled, <code>false</code>
     * otherwise
     */
    boolean isDisabled();

    /**
     * Disable the advisor.
     *
     * @param disabled <code>true</code> to disable the advisor,
     * <code>false</code> to enable the advisor
     */
    void setDisabled(boolean disabled);

    /**
     * Execute advisor on all monitoring results and produce a list of advice.
     *
     * @return a list of advice
     * @throws AdvisorException if there is error while reading the monitoring
     * results
     */
    Advice[] execute() throws AdvisorException;

    /**
     * Execute advisor on selected monitoring results and produce a list of
     * advice.
     *
     * @param monitoringStartTime a start time of the monitoring results
     * @return a list of advice
     * @throws AdvisorException if there is error while reading the monitoring
     * results
     */
    Advice[] execute(Timestamp monitoringStartTime) throws AdvisorException;

    /**
     * Execute advisor on selected monitoring results and produce a list of
     * advice.
     *
     * @param monitoringStartTime a start time of the monitoring results
     * @param monitoringEndTime an end time of the monitoring results
     * @return a list of advice
     * @throws AdvisorException if there is error while reading the monitoring
     * results
     */
    Advice[] execute(Timestamp monitoringStartTime, Timestamp monitoringEndTime) throws AdvisorException;
}
