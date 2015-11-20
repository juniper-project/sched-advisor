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

/* Imported by the reflection in getCurrentThreadCpuTime() and getGarbageCollectionTime():
 * com.aicas.jamaica.lang.Debug
 * com.aicas.jamaica.lang.CpuTime
 * javax.realtime.RelativeTime
 */
import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * The class to implement a default strategy to monitor JVM process resource
 * utilization. Methods <code>getCurrentThreadCpuTime()</code> and
 * <code>getGarbageCollectionTime()</code> implement also resource checks for
 * JamaicaVM.
 *
 * @author rychly
 */
public class MonitoredResourcesDefaultStrategy implements MonitoredResourcesStrategyInterface {

    private static final boolean GET_PROCESS_CPU_LOAD_DECLARED_BY_OPERATINGSYSTEMMXBEAN;
    private static final int PID_MAX_LIMIT;

    private Double initialUptime = null;
    private Double initialProcessCpuTime = null;

    static {
        // GET_PROCESS_CPU_LOAD_DECLARED_BY_OPERATINGSYSTEMMXBEAN
        boolean isDeclaredGetProcessCpuLoad = false;
        for (Method methodInInterface : OperatingSystemMXBean.class.getMethods()) {
            if ("getProcessCpuLoad".equals(methodInInterface.getName())) {
                isDeclaredGetProcessCpuLoad = true;
                break;
            }
        }
        GET_PROCESS_CPU_LOAD_DECLARED_BY_OPERATINGSYSTEMMXBEAN = isDeclaredGetProcessCpuLoad;
        // PID_MAX_LIMIT
        int pidMaxLimit = Integer.MAX_VALUE;
        try {
            pidMaxLimit = (int) readLongValueFromFile("/proc/sys/kernel/pid_max");
        }
        catch (IOException ex) {
            // NOP
        }
        PID_MAX_LIMIT = pidMaxLimit;
    }

    /**
     * Get a hostname of the running Java Virtual Machine (available on Windows
     * and Linux operating systems).
     *
     * @return the hostname of the running Java Virtual Machine
     * @throws UnsupportedOperationException if the hostname cannnot be obtained
     */
    @Override
    public String getHostname() throws UnsupportedOperationException {
        // execute hostname utility (it is available at both linux and windows)
        try {
            final Process process = Runtime.getRuntime().exec("hostname");
            try (final InputStream inputStream = process.getInputStream(); final Scanner scanner = new Scanner(inputStream)) {
                // transform the input stream into string
                return scanner.useDelimiter("\\A").hasNext() ? scanner.next().trim() : "";
            }
        }
        catch (IOException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * Get a process ID (PID) of the running Java Virtual Machine (available on
     * Windows and Linux operating systems).
     *
     * @return the process ID (PID) of the running Java Virtual Machine
     * @throws UnsupportedOperationException if the PID cannnot be obtained
     */
    @Override
    public int getProcessID() throws UnsupportedOperationException {
        // java.lang.management way (jvmName format is not defined, but usually it is pid@hostname)
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int pos;
        if ((jvmName != null) && ((pos = jvmName.indexOf('@')) >= 0)) {
            try {
                final int result = Integer.parseInt(jvmName.substring(0, pos));
                if (result <= PID_MAX_LIMIT) {
                    return result;
                }
            }
            catch (NumberFormatException ex) {
                // do nothing. it will be fixed later
            }
        }
        // linux way via /proc filesystem mountpoint
        try {
            return Integer.parseInt(new File("/proc/self").getCanonicalFile().getName());
        }
        catch (IOException | NumberFormatException ex) {
            // do nothing. it will be fixed later
        }
        // no way
        throw new UnsupportedOperationException("There is no way how to get PID.");
    }

    /**
     * Returns the current memory usage of the heap that is used for object
     * allocation. The heap consists of one or more memory pools. The used size
     * is the sum of those values of all heap memory pools. The amount of used
     * memory is the amount of memory occupied by both live objects and garbage
     * objects that have not been collected, if any.
     *
     * @return the amount of used heap memory in bytes
     */
    @Override
    public long getUsedHeapMemory() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        // the implementation above returns the same results as the following
        //final Runtime runtime = Runtime.getRuntime();
        //return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Returns the amount of heap memory in bytes that is committed for the Java
     * virtual machine to use. This amount of memory is guaranteed for the Java
     * virtual machine to use.
     *
     * @return the amount of committed memory in bytes
     */
    @Override
    public long getCommittedHeapMemory() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted();
        // the implementation above returns the same results as the following
        //final Runtime runtime = Runtime.getRuntime();
        //return runtime.totalMemory();
    }

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
    @Override
    public long getMaxHeapMemory() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
        // the implementation above returns the same results as the following
        //final Runtime runtime = Runtime.getRuntime();
        //return runtime.maxMemory();
    }

    /**
     * Returns the current memory usage of non-heap memory that is used by the
     * Java virtual machine. The non-heap memory consists of one or more memory
     * pools. The used size is the sum of those values of all non-heap memory
     * pools.
     *
     * @return the amount of used non-heap memory in bytes
     */
    @Override
    public long getUsedNonHeapMemory() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
    }

    /**
     * Returns the amount of non-heap memory in bytes that is committed for the
     * Java virtual machine to use. This amount of memory is guaranteed for the
     * Java virtual machine to use.
     *
     * @return the amount of committed memory in bytes
     */
    @Override
    public long getCommittedNonHeapMemory() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted();
    }

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
    @Override
    public long getMaxNonHeapMemory() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax();
    }

    /**
     * Returns the used amount of swap space in bytes by all processes (computed
     * as the total amount minus the amount of free swap space in bytes).
     *
     * @return the used amount of swap space in bytes by all processes; -1 if
     * undefined
     */
    @Override
    public long getUsedSwapSpaceSize() {
        final long totalSwapSpaceSize = this.getMaxSwapSpaceSize();
        final long freeSwapSpaceSize = this.getFreeSwapSpaceSize();
        return ((totalSwapSpaceSize < 0) || (freeSwapSpaceSize < 0))
                ? -1
                : (totalSwapSpaceSize - freeSwapSpaceSize);
    }

    /**
     * Returns the total amount of swap space in bytes.
     *
     * @return the total amount of swap space in bytes; -1 if undefined
     */
    @Override
    public long getMaxSwapSpaceSize() {
        long result = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalSwapSpaceSize();
        if (result < 0) {
            try {
                // linux way via /proc filesystem mountpoint
                result = readFromProcMeminfo("SwapTotal");
            }
            catch (IOException ex) {
                // NOP, no way
            }
        }
        return result;
    }

    /**
     * Returns free swap space in bytes.
     *
     * @return free swap space in bytes; -1 if undefined
     */
    @Override
    public long getFreeSwapSpaceSize() {
        long result = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreeSwapSpaceSize();
        if (result < 0) {
            try {
                // linux way via /proc filesystem mountpoint
                result = readFromProcMeminfo("SwapFree");
            }
            catch (IOException ex) {
                // NOP, no way
            }
        }
        return result;
    }

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
    @Override
    public double getProcessCpuTime() {
        long result = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
        if (result < 0) {
            try {
                // linux way via /proc filesystem mountpoint
                // we expect _SC_CLK_TCK to be 100 on linux systems
                final long nsPerClockTick = 1000 * 1000 * 1000 / 100;
                // we read sum of tms_utime and tms_stime
                final int[] indices = {13, 14};
                result = readFromProcSelfStat(indices) * nsPerClockTick;
            }
            catch (IOException ex) {
                // NOP, no way
            }
        }
        return result / 1000000000D;
    }

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
     * negative value if not available
     */
    @Override
    public double getProcessCpuLoad() {
        if (!this.isProcessCpuLoadInitialised()) {
            // if invocated without previous getProcessCpuLoadInit()
            if (GET_PROCESS_CPU_LOAD_DECLARED_BY_OPERATINGSYSTEMMXBEAN) {
                // use OperatingSystemMXBean.getProcessCpuLoad() if possible (introduced in Java 7)
                return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad();
            } else {
                // perform a 500 ms measurement otherwise
                this.getProcessCpuLoadInit();
                try {
                    Thread.sleep(500);
                }
                catch (Exception e) {
                    // NOP
                }
            }
        }
        // compute the average CPU usage
        final double upTime = this.getUptime();
        final double processCpuTime = this.getProcessCpuTime();
        double result = 0.001D;
        if (upTime > this.initialUptime) {
            final double elapsedCpu = processCpuTime - this.initialProcessCpuTime;
            final double elapsedTime = upTime - this.initialUptime;
            final int nCPUs = this.getAvailableProcessors();
            result = Math.min(1, elapsedCpu / (elapsedTime * nCPUs));
        }
        this.initialUptime = null;
        this.initialProcessCpuTime = null;
        return result;
    }

    /**
     * Initiates the object's internal state for the future measurement of CPU
     * usage by <code>getProcessCpuLoad()</code>. The CPU usage returned by the
     * future invocation of <code>getProcessCpuLoad()</code> will be measured as
     * the average CPU usage between the invocation of this method
     * <code>getProcessCpuLoadInit()</code> and the future invocation of method
     * <code>getProcessCpuLoad()</code>.
     */
    public void getProcessCpuLoadInit() {
        this.initialUptime = this.getUptime();
        this.initialProcessCpuTime = this.getProcessCpuTime();
    }

    /**
     * Check if the object's internal state was initialized by
     * <code>getProcessCpuLoadInit()</code> for the future measurement of CPU
     * usage by <code>getProcessCpuLoad()</code>.
     *
     * @return <code>true</code> if the object's internal state was initialized,
     * <code>false</code> otherwise
     */
    public boolean isProcessCpuLoadInitialised() {
        return (this.initialUptime != null) && (this.initialProcessCpuTime != null);
    }

    /**
     * Returns time in seconds since an arbitrary time in the past. The value is
     * local, not synchronized across all processes in a cluster (to get a
     * synchronized timestamp on an MPI cluster use MPI_Wtime with
     * MPI_WTIME_IS_GLOBAL check).
     *
     * @return time in seconds since an arbitrary time in the past
     */
    public double getLocalTimestamp() {
        return System.currentTimeMillis() / 1000D;
    }

    /**
     * Returns time in seconds since a given initial timestamp. The value is
     * local, not synchronized across all processes in a cluster.
     *
     * @param initialLocalTimestamp the initial timestamp in seconds
     * @return time elapsed since the initial timestamp in seconds
     */
    public double getLocalElaspedTime(double initialLocalTimestamp) {
        return (System.currentTimeMillis() / 1000D) - initialLocalTimestamp;
    }

    /**
     * Returns time in seconds since an arbitrary time in the past. This
     * implementation is just an alias for <code>getLocalTimestamp()</code> to
     * provide a defined way how to get a timestamp. To get a synchronized
     * timestamp on an MPI cluster override this method by a method utilizing
     * MPI_Wtime with MPI_WTIME_IS_GLOBAL check.
     *
     * @return time in seconds since an arbitrary time in the past
     */
    @Override
    public double getTimestamp() {
        return this.getLocalTimestamp();
    }

    /**
     * Returns time in seconds since a given initial timestamp. This
     * implementation is just an alias for
     * <code>getLocalElaspedTime(double initialLocalTimestamp)</code> to provide
     * a defined way how to get an elapsed time. To get a synchronized elapsed
     * time on an MPI cluster override this method by a method utilizing
     * MPI_Wtime with MPI_WTIME_IS_GLOBAL check.
     *
     * @param initialLocalTimestamp the initial timestamp in seconds
     * @return time elapsed since the initial timestamp in seconds
     */
    @Override
    public double getElaspedTime(double initialLocalTimestamp) {
        return this.getLocalElaspedTime(initialLocalTimestamp);
    }

    /**
     * Returns the start time of the Java virtual machine in seconds. This
     * method returns the approximate time when the Java virtual machine
     * started.
     *
     * @return start time of the Java virtual machine in seconds
     */
    @Override
    public double getStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime() / 1000D;
    }

    /**
     * Returns the uptime of the Java virtual machine in seconds.
     *
     * @return uptime of the Java virtual machine in seconds
     */
    @Override
    public double getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime() / 1000D;
    }

    /**
     * Returns a sum of the approximate accumulated collection elapsed time in
     * seconds by all garbage collectors. This method returns a negative number
     * if the collection elapsed time is undefined for all collectors.
     *
     * @return sum of the approximate accumulated collection elapsed time in
     * seconds; a negative number otherwise
     */
    @Override
    public double getGarbageCollectionTime() {
        double totalGCTime = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            totalGCTime += garbageCollectorMXBean.getCollectionTime();
        }
        if (totalGCTime < 0) {
            try {
                final Class<?> debugClass = Class.forName("com.aicas.jamaica.lang.Debug");
                // Debug.getGarbageCollectionTime(Thread thread)
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/com/aicas/jamaica/lang/Debug.html#getGarbageCollectionTime(java.lang.Thread)
                final Class<?>[] debugParamTypes = new Class<?>[]{Thread.class};
                final Method getGarbageCollectionTimeMethod = debugClass.getMethod("getGarbageCollectionTime", debugParamTypes);
                final long gcTime = (Long) getGarbageCollectionTimeMethod.invoke(null, Thread.currentThread());
                // CpuTime.cycles2RelativeTime(long, RelativeTime)
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/com/aicas/jamaica/lang/CpuTime.html#cycles2RelativeTime(long, javax.realtime.RelativeTime)
                final Class<?> cpuTimeClass = Class.forName("com.aicas.jamaica.lang.CpuTime");
                final Class<?> relativeTimeClass = Class.forName("javax.realtime.RelativeTime");
                final Class<?>[] cycles2RelativeTimeParamTypes = new Class<?>[]{long.class, relativeTimeClass};
                final Method cycles2RelativeTimeMethod = cpuTimeClass.getMethod("cycles2RelativeTime", cycles2RelativeTimeParamTypes);
                Object relativeTime = relativeTimeClass.newInstance();
                cycles2RelativeTimeMethod.invoke(null, gcTime, relativeTime);
                // RelativeTime.getMilliseconds()
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/javax/realtime/HighResolutionTime.html#getMilliseconds()
                // RelativeTime.getNanoseconds()
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/javax/realtime/HighResolutionTime.html#getNanoseconds()
                final Method getMillisecondsMethod = relativeTimeClass.getMethod("getMilliseconds");
                final Method getNanosecondsMethod = relativeTimeClass.getMethod("getNanoseconds");
                totalGCTime = (Long) getMillisecondsMethod.invoke(relativeTime)
                        + ((Integer) getNanosecondsMethod.invoke(relativeTime)) / 1000000D;
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
                // the current JVM is not JamaicaVM so we do not know how to check the resource
                return -1;
            }
        }
        return totalGCTime / 1000D;
    }

    /**
     * Returns a sum of the total number of collections that have occurred by
     * all garbage collectors. This resource check is not implemented by
     * JamaicaVM (it returns a negative number).
     *
     * @return sum of the total number of collections that have occurred; a
     * negative number otherwise
     */
    @Override
    public long getGarbageCollectionCount() {
        long totalGCCount = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            totalGCCount += garbageCollectorMXBean.getCollectionCount();
        }
        return totalGCCount;
    }

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
    @Override
    public double getCurrentThreadCpuTime() {
        long resultInNanoseconds = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
        if (resultInNanoseconds < 0) {
            try {
                final Class<?> cpuTimeClass = Class.forName("com.aicas.jamaica.lang.CpuTime");
                // CpuTime.getCpuTime(Thread thread)
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/com/aicas/jamaica/lang/CpuTime.html#getCpuTime(java.lang.Thread)
                final Class<?>[] getCpuTimeParamTypes = new Class<?>[]{Thread.class};
                final Method getCpuTimeMethod = cpuTimeClass.getMethod("getCpuTime", getCpuTimeParamTypes);
                final long cpuTime = (Long) getCpuTimeMethod.invoke(null, Thread.currentThread());
                // CpuTime.cycles2RelativeTime(long, RelativeTime)
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/com/aicas/jamaica/lang/CpuTime.html#cycles2RelativeTime(long, javax.realtime.RelativeTime)
                final Class<?> relativeTimeClass = Class.forName("javax.realtime.RelativeTime");
                final Class<?>[] cycles2RelativeTimeParamTypes = new Class<?>[]{long.class, relativeTimeClass};
                final Method cycles2RelativeTimeMethod = cpuTimeClass.getMethod("cycles2RelativeTime", cycles2RelativeTimeParamTypes);
                Object relativeTime = relativeTimeClass.newInstance();
                cycles2RelativeTimeMethod.invoke(null, cpuTime, relativeTime);
                // RelativeTime.getMilliseconds()
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/javax/realtime/HighResolutionTime.html#getMilliseconds()
                // RelativeTime.getNanoseconds()
                // @see http://www.aicas.com/jamaica/6.3/doc/jamaica_api/javax/realtime/HighResolutionTime.html#getNanoseconds()
                final Method getMillisecondsMethod = relativeTimeClass.getMethod("getMilliseconds");
                final Method getNanosecondsMethod = relativeTimeClass.getMethod("getNanoseconds");
                resultInNanoseconds = ((Long) getMillisecondsMethod.invoke(relativeTime)) * 1000000
                        + (Integer) getNanosecondsMethod.invoke(relativeTime);
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
                // the current JVM is not JamaicaVM so we do not know how to check the resource
                return -1;
            }
        }
        return resultInNanoseconds / 1000000000D;
    }

    /**
     * Returns the CPU time that the current thread has executed in user mode in
     * seconds. The returned value is of nanoseconds precision but not
     * necessarily nanoseconds accuracy. This resource check is not implemented
     * by JamaicaVM (it returns a negative number).
     *
     * @return the user-level CPU time for the current thread in seconds if CPU
     * time measurement is enabled; a negative number otherwise
     */
    @Override
    public double getCurrentThreadUserTime() {
        final double resultInNanoseconds = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
        return (resultInNanoseconds >= 0)
                ? (resultInNanoseconds / 1000000000D)
                : -1;
    }

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
    @Override
    public boolean setThreadCpuTimeEnabled(boolean enable) {
        try {
            ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(enable);
            return ManagementFactory.getThreadMXBean().isThreadCpuTimeEnabled();
        }
        catch (UnsupportedOperationException | SecurityException ex) {
            return false;
        }
    }

    /**
     * Returns the number of processors available to the Java virtual machine.
     * This value may change during a particular invocation of the virtual
     * machine.
     *
     * @return the number of processors available to the virtual machine; never
     * smaller than one.
     */
    @Override
    public int getAvailableProcessors() {
        int result = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        // in the case of 1 reported CPU (as for JamaicaVM), do also the following check as
        // linux way via /proc/cpuinfo
        if (result == 1) {
            try (final FileReader fileReader = new FileReader("/proc/cpuinfo");
                    final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                result = 0;
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("processor")) {
                        result++;
                    }
                }
            }
            catch (IOException ex) {
                // NOP
            }
        }
        return result;
    }

    /**
     * Read a value (in Bytes) of a given key from /proc/meminfo file.
     *
     * @param key the key of the value to be read
     * @return the value (in Bytes) of a given key
     * @throws FileNotFoundException if /proc/meminfo file does not exists
     * @throws IOException if /proc/meminfo file cannot be read
     */
    protected static long readFromProcMeminfo(String key) throws FileNotFoundException, IOException {
        try (final FileReader fileReader = new FileReader("/proc/meminfo");
                final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String[] splittedLine = line.split("[:\\s]+");
                if (key.equals(splittedLine[0])) {
                    long value = Long.valueOf(splittedLine[1]);
                    switch (splittedLine[2]) {
                        case "kB":
                            value = value << 10;
                            break;
                        case "MB":
                            value = value << 20;
                            break;
                        case "GB":
                            value = value << 30;
                            break;
                    }
                    return value;
                }
            }
            return -1;
        }
    }

    /**
     * Read a sum of numerical values at a given positions from /proc/self/stat
     * file.
     *
     * @param indices the positions of the values to be read
     * @return the sum of value at the given positions
     * @throws FileNotFoundException if /proc/self/stat file does not exists
     * @throws IOException if /proc/self/stat file cannot be read
     */
    protected static long readFromProcSelfStat(int[] indices) throws FileNotFoundException, IOException {
        try (final FileReader fileReader = new FileReader("/proc/self/stat");
                final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final String line = bufferedReader.readLine();
            final String[] splittedLine = line.split("\\s+");
            try {
                long sum = 0;
                for (int index : indices) {
                    sum += Long.valueOf(splittedLine[index]);
                }
                return sum;
            }
            catch (IndexOutOfBoundsException ex) {
                // NOP
            }
            return -1;
        }
    }

    /**
     * Read a numerical value from a given file.
     *
     * @param pathTofile a path to the given file to read from
     * @return the numerical value read from a given file
     * @throws FileNotFoundException if the given file does not exists
     * @throws IOException if the given file cannot be read
     */
    protected static long readLongValueFromFile(String pathTofile) throws FileNotFoundException, IOException {
        try (final FileReader fileReader = new FileReader(pathTofile);
                final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return Long.valueOf(bufferedReader.readLine());
        }
    }

    public static void main(String[] args) throws IOException {
        final MonitoredResourcesStrategyInterface monitoredResourcesStrategy = new MonitoredResourcesDefaultStrategy();
        final double initialTimestamp = monitoredResourcesStrategy.getTimestamp();
        ((MonitoredResourcesDefaultStrategy) monitoredResourcesStrategy).getProcessCpuLoadInit();
        monitoredResourcesStrategy.setThreadCpuTimeEnabled(true);
        System.out.printf("===== Monitoring and Resource Utilization Information (JVM ID: %s) =====\n"
                + "Local timestamp: %f sec\n"
                + "JVM process PID and hostname: %d @ %s\n"
                + "JVM process used memory (heap/non-heap): %d / %d B\n"
                + "JVM process commited memory (heap/non-heap): %d / %d B\n"
                + "JVM process maximum available memory (heap/non-heap): %d / %d B\n"
                + "Swap-space size (total/used/free; by all processes): %d / %d / %d B\n"
                + "Number of CPUs available: %d\n"
                + "JVM process CPU time and the recent CPU utilization: %f sec / %f percent\n"
                + "Current thread CPU time and the time executed in user mode: %f / %f sec\n"
                + "JVM start timestamp and current uptime: %f / %f sec\n"
                + "Number of grabage collections and their accumulated elapsed time: %d collections / %f sec\n"
                + "Elapsed time by the measuring above: %f sec\n",
                ManagementFactory.getRuntimeMXBean().getName(),
                initialTimestamp,
                monitoredResourcesStrategy.getProcessID(), monitoredResourcesStrategy.getHostname(),
                monitoredResourcesStrategy.getUsedHeapMemory(), monitoredResourcesStrategy.getUsedNonHeapMemory(),
                monitoredResourcesStrategy.getCommittedHeapMemory(), monitoredResourcesStrategy.getCommittedNonHeapMemory(),
                monitoredResourcesStrategy.getMaxHeapMemory(), monitoredResourcesStrategy.getMaxNonHeapMemory(),
                monitoredResourcesStrategy.getMaxSwapSpaceSize(), monitoredResourcesStrategy.getUsedSwapSpaceSize(), monitoredResourcesStrategy.getFreeSwapSpaceSize(),
                monitoredResourcesStrategy.getAvailableProcessors(),
                monitoredResourcesStrategy.getProcessCpuTime(),
                monitoredResourcesStrategy.getProcessCpuLoad() * 100,
                monitoredResourcesStrategy.getCurrentThreadCpuTime(), monitoredResourcesStrategy.getCurrentThreadUserTime(),
                monitoredResourcesStrategy.getStartTime(), monitoredResourcesStrategy.getUptime(),
                monitoredResourcesStrategy.getGarbageCollectionCount(), monitoredResourcesStrategy.getGarbageCollectionTime(),
                monitoredResourcesStrategy.getElaspedTime(initialTimestamp));
    }

}
