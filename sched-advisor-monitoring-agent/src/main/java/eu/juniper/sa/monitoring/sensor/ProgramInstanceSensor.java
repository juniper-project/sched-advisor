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
 * The class of a sensor for a Juniper program instance. Methods of the class
 * produce metric records in the following format (all timestamps, times, and
 * durations are in seconds)  <code>{ "Timestamp": timestamp_of_program_end,
 * "hostname": hostname_of_juniper_program_node, "type": "ProgramRuntime",
 * "ProgramGlobalRank": global_rank_of_the_program,
 * "ProgramStartTimestamp": timestamp_of_the_program_start,
 * "ProgramDuration": duration_of_the_program,
 * "GarbageCollectionCount": number_of_garbage_collections,
 * "GarbageCollectionTime": total_time_of_the_garbage_collections,
 * "ProgramCpuTime": cpu_time_used_by_the_program,
 * "StartHeapMemory": used_heap_memory_when_the_program_started,
 * "UsedHeapMemory": used_heap_memory_when_the_program_finished,
 * "MaxHeapMemory": limit_for_heap_memory_in_the_program,
 * "StartNonHeapMemory": used_non_heap_memory_when_the_program_started,
 * "UsedNonHeapMemory": used_non_heap_memory_when_the_program_finished,
 * "MaxNonHeapMemory": limit_for_non_heap_memory_in_the_program,
 * "StartSwapSpaceSize": used_size_of_used_swap_when_the_program_started,
 * "UsedSwapSpaceSize": used_size_of_used_swap_when_the_program_finished,
 * "MaxSwapSpaceSize": limit_for_size_of_swap_space }</code>.
 *
 * @author rychly
 */
public class ProgramInstanceSensor extends AbstractSensor implements ProgramInstanceSensorInterface {

    private final int programGlobalRank;
    private Double programStartsTimestamp;
    private long programStartsGCCount;
    private double programStartsGCTime;
    private double programStartsCPUTime;
    private long programStartsHeapMemory;
    private long programStartsNonHeapMemory;
    private long programStartsSwapSpaceSize;

    /**
     * Create a sensor that will utilize a given monitoring agent.
     *
     * @param monitoringAgent a monitoring agent to be utilized by the sensor
     * @param monitoredResourcesStrategy a monitored resources strategy to be
     * utilized by the sensor
     * @param programGlobalRank an MPI global rank of a monitored Juniper
     * program (value of <code>JuniperProgram.myGlobalRank</code>)
     */
    public ProgramInstanceSensor(MonitoringAgentInterface monitoringAgent, MonitoredResourcesStrategyInterface monitoredResourcesStrategy, int programGlobalRank) {
        super(monitoringAgent, monitoredResourcesStrategy);
        this.programGlobalRank = programGlobalRank;
    }

    /**
     * Should be invoked just at the beginning of
     * <code>JuniperProgram.run(...)</code> method implementation.
     */
    @Override
    public void programStarts() {
        this.durationToSubtract = 0;
        this.programStartsGCCount = this.getMonitoredResourcesStrategy().getGarbageCollectionCount();
        this.programStartsGCTime = this.getMonitoredResourcesStrategy().getGarbageCollectionTime();
        this.programStartsCPUTime = this.getMonitoredResourcesStrategy().getProcessCpuTime();
        this.programStartsHeapMemory = this.getMonitoredResourcesStrategy().getUsedHeapMemory();
        this.programStartsNonHeapMemory = this.getMonitoredResourcesStrategy().getUsedNonHeapMemory();
        this.programStartsSwapSpaceSize = this.getMonitoredResourcesStrategy().getUsedSwapSpaceSize();
        // the start timestamp should be taken just before a program so it cannnot be affected by the measuring above
        this.programStartsTimestamp = this.getMonitoredResourcesStrategy().getTimestamp();
    }

    /**
     * May be invoked during <code>JuniperProgram.run(...)</code> method
     * implementation if the sensors data need to be reported before a monitored
     * program ends (before a final call of <code>programEnds()</code> method).
     *
     * @return a duration of this method in seconds (an overhead of the method
     * to be subtracted from surrounding measurements)
     * @throws IllegalStateException if the method is invoked before
     * <code>*Starts()</code> method
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    @Override
    public double programDoesNotEnd() throws IllegalStateException, IOException {
        // just generate and send monitoring data
        return this.reportMonitoringData();
    }

    /**
     * Should be invoked just at the end of <code>JuniperProgram.run(...)</code>
     * method implementation.
     *
     * @return a duration of this method in seconds (an overhead of the method
     * to be subtracted from surrounding measurements)
     * @throws IllegalStateException if the method is invoked before
     * <code>*Starts()</code> method
     * @throws IOException if there is an HTTP error when connecting or sending
     * to the monitoring service
     */
    @Override
    public double programEnds() throws IllegalStateException, IOException {
        // generate and send monitoring data
        final double methodDuration = this.reportMonitoringData();
        // null the sensor
        this.programStartsTimestamp = null;
        return methodDuration;
    }

    private double reportMonitoringData() throws IllegalStateException, IOException {
        // the end timestamp should be taken just after a program so it cannnot be affected by the measuring and reporting below
        final double programEndsTimestamp = this.getMonitoredResourcesStrategy().getTimestamp();
        // and also we should take all memory measurements before creating additional objects
        final long usedHeapMemory = this.getMonitoredResourcesStrategy().getUsedHeapMemory();
        final long usedNonHeapMemory = this.getMonitoredResourcesStrategy().getUsedNonHeapMemory();
        final long usedSwapSpaceSize = this.getMonitoredResourcesStrategy().getUsedSwapSpaceSize();
        //
        if (this.programStartsTimestamp == null) {
            throw new IllegalStateException("The method must follow a previous invocation of programStarts() method.");
        }
        final String[] metricNames = {
            "ProgramGlobalRank",
            "ProgramStartTimestamp",
            "ProgramDuration",
            "GarbageCollectionCount",
            "GarbageCollectionTime",
            "ProgramCpuTime",
            "StartHeapMemory",
            "UsedHeapMemory",
            "MaxHeapMemory",
            "StartNonHeapMemory",
            "UsedNonHeapMemory",
            "MaxNonHeapMemory",
            "StartSwapSpaceSize",
            "UsedSwapSpaceSize",
            "MaxSwapSpaceSize"
        };
        final long maxHeapMemory = this.getMonitoredResourcesStrategy().getMaxHeapMemory();
        final long maxNonHeapMemory = this.getMonitoredResourcesStrategy().getMaxNonHeapMemory();
        final String[] metricValues = {
            String.format(Locale.ROOT, "%d", this.programGlobalRank),
            String.format(Locale.ROOT, "%f", this.programStartsTimestamp),
            String.format(Locale.ROOT, "%f", programEndsTimestamp - this.programStartsTimestamp - this.durationToSubtract),
            String.format(Locale.ROOT, "%d", this.getMonitoredResourcesStrategy().getGarbageCollectionCount() - this.programStartsGCCount),
            String.format(Locale.ROOT, "%f", this.getMonitoredResourcesStrategy().getGarbageCollectionTime() - this.programStartsGCTime),
            this.programStartsCPUTime >= 0 ? String.format(Locale.ROOT, "%f", this.getMonitoredResourcesStrategy().getProcessCpuTime() - this.programStartsCPUTime) : null,
            String.format(Locale.ROOT, "%d", this.programStartsHeapMemory),
            String.format(Locale.ROOT, "%d", usedHeapMemory),
            maxHeapMemory >= 0 ? String.format(Locale.ROOT, "%d", maxHeapMemory) : null,
            String.format(Locale.ROOT, "%d", this.programStartsNonHeapMemory),
            String.format(Locale.ROOT, "%d", usedNonHeapMemory),
            maxNonHeapMemory >= 0 ? String.format(Locale.ROOT, "%d", maxNonHeapMemory) : null,
            String.format(Locale.ROOT, "%d", this.programStartsSwapSpaceSize),
            String.format(Locale.ROOT, "%d", usedSwapSpaceSize),
            String.format(Locale.ROOT, "%d", this.getMonitoredResourcesStrategy().getMaxSwapSpaceSize())
        };
        this.getMonitoringAgent().sendMetric("ProgramRuntime", metricNames, metricValues, programEndsTimestamp, this.getMonitoredResourcesStrategy().getHostname());
        return this.getMonitoredResourcesStrategy().getTimestamp() - programEndsTimestamp;
    }

    /**
     * Get the current duration from the last <code>*Starts()</code> invocation
     * in seconds.
     *
     * @return a duration in seconds
     */
    @Override
    public double getCurrentDuration() {
        return this.getMonitoredResourcesStrategy().getTimestamp() - this.programStartsTimestamp - this.durationToSubtract;
    }
}
