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
package eu.juniper.sa.monitoring.resources;

/**
 * The interface of strategies to monitor JVM process resource utilization.
 *
 * @author rychly
 */
public interface MonitoredResourcesStrategyInterface {

    /**
     * Returns the total CPU time for the current thread in seconds. The
     * returned value is of nanoseconds precision but not necessarily
     * nanoseconds accuracy. If the implementation distinguishes between user
     * mode time and system mode time, the returned CPU time is the amount of
     * time that the current thread has executed in user mode or system mode.
     *
     * @return the total CPU time for the current thread in seconds if CPU time
     * measurement is enabled; a negative number otherwise
     */
    double getCurrentThreadCpuTime();

    /**
     * Returns the CPU time that the current thread has executed in user mode in
     * seconds. The returned value is of nanoseconds precision but not
     * necessarily nanoseconds accuracy.
     *
     * @return the user-level CPU time for the current thread in seconds if CPU
     * time measurement is enabled; a negative number otherwise
     */
    double getCurrentThreadUserTime();

    /**
     * Returns time in seconds since a given initial timestamp.
     *
     * @param initialLocalTimestamp the initial timestamp in seconds
     * @return time elapsed since the initial timestamp in seconds
     */
    double getElaspedTime(double initialLocalTimestamp);

    /**
     * Returns a sum of the total number of collections that have occurred by
     * all garbage collectors.
     *
     * @return sum of the total number of collections that have occurred
     */
    long getGarbageCollectionCount();

    /**
     * Returns a sum of the approximate accumulated collection elapsed time in
     * seconds by all garbage collectors. This method returns a negative number
     * if the collection elapsed time is undefined for all collectors.
     *
     * @return sum of the approximate accumulated collection elapsed time in
     * seconds
     */
    double getGarbageCollectionTime();

    /**
     * Get a hostname of the running Java Virtual Machine.
     *
     * @return the hostname of the running Java Virtual Machine
     * @throws UnsupportedOperationException if the hostname cannnot be obtained
     */
    String getHostname() throws UnsupportedOperationException;

    /**
     * Returns the "recent cpu usage" for the Java Virtual Machine process. This
     * value is a double in the [0.0,1.0] interval. A value of 0.0 means that
     * none of the CPUs were running threads from the JVM process during the
     * recent period of time observed, while a value of 1.0 means that all CPUs
     * were actively running threads from the JVM 100% of the time during the
     * recent period being observed. Threads from the JVM include the
     * application threads as well as the JVM internal threads. All values
     * betweens 0.0 and 1.0 are possible depending of the activities going on in
     * the JVM process and the whole system. If the Java Virtual Machine recent
     * CPU usage is not available, the method returns a negative value.
     *
     * @return the "recent cpu usage" for the Java Virtual Machine process; a
     * negative number if not available
     */
    double getProcessCpuLoad();

    /**
     * Returns the CPU time used by the process (tms_utime) on which the Java
     * virtual machine is running in seconds. The returned value is of
     * nanoseconds precision but not necessarily nanoseconds accuracy. This
     * method returns a negative number if the the platform does not support
     * this operation.
     *
     * @return the CPU time used by the process in nanoseconds, or a negative
     * number if this operation is not supported
     */
    double getProcessCpuTime();

    /**
     * Get a process ID (PID) of the running Java Virtual Machine (available on
     * Windows and Linux operating systems).
     *
     * @return the process ID (PID) of the running Java Virtual Machine
     * @throws UnsupportedOperationException if the PID cannnot be obtained
     */
    int getProcessID() throws UnsupportedOperationException;

    /**
     * Returns the start time of the Java virtual machine in seconds. This
     * method returns the approximate time when the Java virtual machine
     * started.
     *
     * @return start time of the Java virtual machine in seconds
     */
    double getStartTime();

    /**
     * Returns time in seconds since an arbitrary time in the past.
     *
     * @return time in seconds since an arbitrary time in the past
     */
    double getTimestamp();

    /**
     * Returns the uptime of the Java virtual machine in seconds.
     *
     * @return uptime of the Java virtual machine in seconds
     */
    double getUptime();

    /**
     * Returns the current memory usage of the heap that is used for object
     * allocation. The heap consists of one or more memory pools. The used size
     * is the sum of those values of all heap memory pools. The amount of used
     * memory is the amount of memory occupied by both live objects and garbage
     * objects that have not been collected, if any.
     *
     * @return the amount of used heap memory in bytes
     */
    long getUsedHeapMemory();

    /**
     * Returns the amount of heap memory in bytes that is committed for the Java
     * virtual machine to use. This amount of memory is guaranteed for the Java
     * virtual machine to use.
     *
     * @return the amount of committed memory in bytes
     */
    long getCommittedHeapMemory();

    /**
     * Returns the maximum amount of heap memory in bytes that can be used for
     * memory management. This method returns -1 if the maximum memory size is
     * undefined. This amount of memory is not guaranteed to be available for
     * memory management if it is greater than the amount of committed memory.
     * The Java virtual machine may fail to allocate memory even if the amount
     * of used memory does not exceed this maximum size.
     *
     * @return the maximum amount of memory in bytes; -1 if undefined
     */
    long getMaxHeapMemory();

    /**
     * Returns the current memory usage of non-heap memory that is used by the
     * Java virtual machine. The non-heap memory consists of one or more memory
     * pools. The used size is the sum of those values of all non-heap memory
     * pools.
     *
     * @return the amount of used non-heap memory in bytes
     */
    long getUsedNonHeapMemory();

    /**
     * Returns the amount of non-heap memory in bytes that is committed for the
     * Java virtual machine to use. This amount of memory is guaranteed for the
     * Java virtual machine to use.
     *
     * @return the amount of committed memory in bytes
     */
    long getCommittedNonHeapMemory();

    /**
     * Returns the maximum amount of non-heap memory in bytes that can be used
     * for memory management. This method returns -1 if the maximum memory size
     * is undefined. This amount of memory is not guaranteed to be available for
     * memory management if it is greater than the amount of committed memory.
     * The Java virtual machine may fail to allocate memory even if the amount
     * of used memory does not exceed this maximum size.
     *
     * @return the maximum amount of memory in bytes; -1 if undefined
     */
    long getMaxNonHeapMemory();

    /**
     * Returns the used amount of swap space in bytes by all processes (computed
     * as the total amount minus the amount of free swap space in bytes).
     *
     * @return the used amount of swap space in bytes by all processes
     */
    long getUsedSwapSpaceSize();

    /**
     * Returns the total amount of swap space in bytes.
     *
     * @return the total amount of swap space in bytes
     */
    long getMaxSwapSpaceSize();

    /**
     * Returns free swap space in bytes.
     *
     * @return free swap space in bytes; -1 if undefined
     */
    long getFreeSwapSpaceSize();

    /**
     * Enables or disables thread CPU time measurement which has to be enabled
     * for <code>getCurrentThreadCpuTime()</code> and
     * <code>getCurrentThreadUserTime()</code> methods of this class. The
     * default is platform dependent.
     *
     * @param enable <code>true</code> to enable; <code>false</code> to disable
     * @return <code>true</code> if thread CPU time measurement is enabled after
     * the method call; <code>false</code> otherwise
     */
    boolean setThreadCpuTimeEnabled(boolean enable);

    /**
     * Returns the number of processors available to the Java virtual machine.
     * This value may change during a particular invocation of the virtual
     * machine.
     *
     * @return the number of processors available to the virtual machine; never
     * smaller than one.
     */
    int getAvailableProcessors();

}
