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
package eu.juniper.sa.tool.plugins;

import eu.juniper.sa.deployment.model.JuniperApplication;
import eu.juniper.sa.deployment.model.ProgramInstance;
import eu.juniper.sa.tool.Advice;
import eu.juniper.sa.tool.AdvisorException;
import eu.juniper.sa.tool.AdvisorInterface;
import eu.juniper.sa.tool.AdvisorUsingDatabaseAbstract;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The class of an advisor that detects Juniper programs where the memory usage
 * is growing over time by detecting a linear trend in memory usage (the linear
 * regression analysis). For the algorithm see
 * https://en.wikipedia.org/wiki/Regression_analysis#Linear_regression
 *
 * @author rychly
 */
public class AdvisorOutOfMemoryPrediction extends AdvisorUsingDatabaseAbstract implements AdvisorInterface {

    // Advisor's metadata
    private final static String ADVISOR_NAME = AdvisorOutOfMemoryPrediction.class.getSimpleName();
    private final static String ADVISOR_DESCRIPTION
            = "This advisor detect Juniper programs"
            + " where the memory usage is growing over the time by detecting"
            + " a linear trend in memory usage (the linear regression analysis)."
            + " This may indicate potential memory leaks and eventually result"
            + " into performance related issues and OutOfMemoryError errors in a Juniper programs.";
    private final static Locale ADVISOR_LOCALE = Locale.ENGLISH;
    // Advice's metadata
    private final static String ADVICE_NAME_HEAPMEM = "OutOfMemoryPrediction_HeapMemory";
    private final static String ADVICE_NAME_NONHEAPMEM = "OutOfMemoryPrediction_NonHeapMemory";
    private final static String ADVICE_NAME_SWAPFILE = "OutOfMemoryPrediction_SwapFile";
    private final static String ADVICE_TEXT_MODEL
            = ". The sample linear regression model is Y_{size_in_bytes} = %f + %f * X_{time_in_sec}.";
    private final static String ADVICE_TEXT_HEAPMEM
            = "The $ running at $"
            + " has the memory usage growing over the time by the approximate rate"
            + " of change %f Bytes per second for the heap memory size"
            + " (the cases with %f Bytes per second and above are reported)."
            + " This may result into OutOfMemoryError errors in the program";
    private final static String ADVICE_TEXT_HEAPMEM_OMM
            = " on %s (that is %s since the beginning of analyzed data on %s; the heap memory is limited to %d Bytes)";
    private final static String ADVICE_TEXT_NONHEAPMEM
            = "The $ running at $"
            + " has the memory usage growing over the time by the approximate rate"
            + " of change %f Bytes per second for the non-heap memory size"
            + " (the cases with %f Bytes per second and above are reported)."
            + " This may result into OutOfMemoryError errors in the program";
    private final static String ADVICE_TEXT_NONHEAPMEM_OMM
            = " on %s (that is %s since the beginning of analyzed data on %s; the non-heap memory is limited to %d Bytes)";
    private final static String ADVICE_TEXT_SWAPSPACE
            = "The $ running at $"
            + " has the memory usage growing over the time by the approximate rate"
            + " of change %f Bytes per second for the swap space size"
            + " (the cases with %f Bytes per second and above are reported)."
            + " This can result in performance related issues for the program";
    private final static String ADVICE_TEXT_SWAPSPACE_OMM
            = " on %s (that is %s since the beginning of analyzed data on %s; the swap space size is limited to %d Bytes)";
    // Monitoring information processing SQL query
    private final static String[] QUERY_metricsInProgramRuntime = {"ProgramGlobalRank", "UsedHeapMemory", "UsedNonHeapMemory", "UsedSwapSpaceSize"};
    private final static String QUERY
            = "SELECT ProgramRuntime.ProgramGlobalRank AS ProgramGlobalRank, ProgramRuntimeAvg.AvgTime AS AvgTime,\n"
            + "  CASE WHEN SUM(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)=0 THEN 0 ELSE SUM((ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)*(ProgramRuntime.UsedHeapMemory-ProgramRuntimeAvg.AvgUsedHeapMemory))/SUM(POWER(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime, 2)) END AS Beta1UsedHeapMemory,\n"
            + "  ProgramRuntimeAvg.AvgUsedHeapMemory AS AvgUsedHeapMemory,\n"
            + "  CASE WHEN SUM(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)=0 THEN 0 ELSE SUM((ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)*(ProgramRuntime.UsedNonHeapMemory-ProgramRuntimeAvg.AvgUsedNonHeapMemory))/SUM(POWER(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime, 2)) END AS Beta1UsedNonHeapMemory,\n"
            + "  ProgramRuntimeAvg.AvgUsedNonHeapMemory AS AvgUsedNonHeapMemory,\n"
            + "  CASE WHEN SUM(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)=0 THEN 0 ELSE SUM((ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)*(ProgramRuntime.UsedSwapSpaceSize-ProgramRuntimeAvg.AvgUsedSwapSpaceSize))/SUM(POWER(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime, 2)) END AS Beta1UsedSwapSpaceSize,\n"
            + "  ProgramRuntimeAvg.AvgUsedSwapSpaceSize AS AvgUsedSwapSpaceSize\n"
            + "FROM\n"
            + "  (SELECT m0.numericvalue AS ProgramGlobalRank,\n"
            + "    m1.numericvalue AS UsedHeapMemory,\n"
            + "    m2.numericvalue AS UsedNonHeapMemory,\n"
            + "    m3.numericvalue AS UsedSwapSpaceSize,\n"
            + "    EXTRACT(SECOND FROM CAST(? AS timestamp)) - EXTRACT(SECOND FROM records.time) AS Time\n"
            + AdvisorUsingDatabaseAbstract.generateFromWhereFragment("ProgramRuntime", QUERY_metricsInProgramRuntime)
            + "  AND (records.time BETWEEN ? AND ?)\n"
            + "  ) ProgramRuntime\n"
            + "JOIN\n"
            + "  (SELECT m0.numericvalue AS ProgramGlobalRank,\n"
            + "    AVG(m1.numericvalue) AS AvgUsedHeapMemory,\n"
            + "    AVG(m2.numericvalue) AS AvgUsedNonHeapMemory,\n"
            + "    AVG(m3.numericvalue) AS AvgUsedSwapSpaceSize,\n"
            + "    AVG(EXTRACT(SECOND FROM CAST(? AS timestamp)) - EXTRACT(SECOND FROM records.time)) AS AvgTime\n"
            + AdvisorUsingDatabaseAbstract.generateFromWhereFragment("ProgramRuntime", QUERY_metricsInProgramRuntime)
            + "  AND (records.time BETWEEN ? AND ?)\n"
            + "  GROUP BY m0.numericvalue\n"
            + "  ) ProgramRuntimeAvg ON (ProgramRuntime.ProgramGlobalRank = ProgramRuntimeAvg.ProgramGlobalRank)\n"
            + "GROUP BY ProgramRuntime.ProgramGlobalRank, ProgramRuntimeAvg.AvgTime, ProgramRuntimeAvg.AvgUsedHeapMemory, ProgramRuntimeAvg.AvgUsedNonHeapMemory, ProgramRuntimeAvg.AvgUsedSwapSpaceSize\n"
            + "HAVING CASE WHEN SUM(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)=0 THEN 0 ELSE SUM((ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)*(ProgramRuntime.UsedHeapMemory-ProgramRuntimeAvg.AvgUsedHeapMemory))/SUM(POWER(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime, 2)) END >= ?\n"
            + "OR CASE WHEN SUM(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)=0 THEN 0 ELSE SUM((ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)*(ProgramRuntime.UsedNonHeapMemory-ProgramRuntimeAvg.AvgUsedNonHeapMemory))/SUM(POWER(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime, 2)) END >= ?\n"
            + "OR CASE WHEN SUM(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)=0 THEN 0 ELSE SUM((ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime)*(ProgramRuntime.UsedSwapSpaceSize-ProgramRuntimeAvg.AvgUsedSwapSpaceSize))/SUM(POWER(ProgramRuntime.Time-ProgramRuntimeAvg.AvgTime, 2)) END >= ?\n"
            + "ORDER BY ProgramGlobalRank ASC;";
    //      + "ORDER BY Beta1UsedHeapMemory+Beta1UsedNonHeapMemory+Beta1UsedSwapSpaceSize;"; // cannot by ordered by calculated columns
    private final static String[] QUERY_metricsInProgramRuntime_max = {"ProgramGlobalRank"};
    private final static String QUERY_MAX
            = "SELECT"
            + "  (SELECT numericvalue FROM metrics WHERE name = 'MaxSwapSpaceSize' AND recordid = records.id) AS MaxSwapSpaceSize,\n"
            + "  (SELECT numericvalue FROM metrics WHERE name = 'MaxHeapMemory' AND recordid = records.id) AS MaxHeapMemory,\n"
            + "  (SELECT numericvalue FROM metrics WHERE name = 'MaxNonHeapMemory' AND recordid = records.id) AS MaxNonHeapMemory\n"
            + AdvisorUsingDatabaseAbstract.generateFromWhereFragment("ProgramRuntime", QUERY_metricsInProgramRuntime_max)
            + "  AND (m0.numericvalue = ?) AND (records.time BETWEEN ? AND ?)\n"
            + "LIMIT 1;";

    /**
     * Maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the heap memory growth of a Juniper program (Bytes per
     * seconds; reaching of this value causes the advice generation).
     */
    protected double linearRegressionBeta1ForHeapMemory = 0.1;
    /**
     * Maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the non-heap memory growth of a Juniper program (Bytes per
     * seconds; reaching of this value causes the advice generation).
     */
    protected double linearRegressionBeta1ForNonHeapMemory = 0.1;
    /**
     * Maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the swap space growth of a Juniper program (Bytes per
     * seconds; reaching of this value causes the advice generation).
     */
    protected double linearRegressionBeta1ForSwapSpace = 0.1;

    /**
     * Get a name of the advisor.
     *
     * @return a name of the advisor
     */
    @Override
    public String getName() {
        return AdvisorOutOfMemoryPrediction.ADVISOR_NAME;
    }

    /**
     * Get a description of the advisor.
     *
     * @return a description of the advisor
     */
    @Override
    public String getDescription() {
        return AdvisorOutOfMemoryPrediction.ADVISOR_DESCRIPTION;
    }

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
    @Override
    public Advice[] execute(Timestamp monitoringStartTime, Timestamp monitoringEndTime) throws AdvisorException {
        ArrayList<Advice> result = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.getMonitoringDatabaseConnection().prepareStatement(QUERY);
                PreparedStatement preparedStatementMax = this.getMonitoringDatabaseConnection().prepareStatement(QUERY_MAX)) {
            preparedStatement.setTimestamp(1, monitoringStartTime);
            preparedStatement.setTimestamp(2, monitoringStartTime);
            preparedStatement.setTimestamp(3, monitoringEndTime);
            preparedStatement.setTimestamp(4, monitoringStartTime);
            preparedStatement.setTimestamp(5, monitoringStartTime);
            preparedStatement.setTimestamp(6, monitoringEndTime);
            preparedStatement.setDouble(7, this.linearRegressionBeta1ForHeapMemory);
            preparedStatement.setDouble(8, this.linearRegressionBeta1ForNonHeapMemory);
            preparedStatement.setDouble(9, this.linearRegressionBeta1ForSwapSpace);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final int programInstanceId = resultSet.getInt("ProgramGlobalRank");
                    final ProgramInstance programInstance
                            = this.getJuniperApplication().getProgramModel().getProgramInstanceById(programInstanceId);
                    if (programInstance == null) {
                        throw new AdvisorException("Cannot find Juniper program instance with ID " + programInstanceId);
                    }
                    // get limits for heap/non-heap memory and swap space of the program instance
                    preparedStatementMax.setInt(1, programInstanceId);
                    preparedStatementMax.setTimestamp(2, monitoringStartTime);
                    preparedStatementMax.setTimestamp(3, monitoringEndTime);
                    long maxHeapMemory = 0;
                    long maxNonHeapMemory = 0;
                    long maxSwapSpaceSize = 0;
                    try (ResultSet resultSetMax = preparedStatementMax.executeQuery()) {
                        if (resultSetMax.next()) {
                            maxHeapMemory = resultSetMax.getLong("MaxHeapMemory");
                            maxNonHeapMemory = resultSetMax.getLong("MaxNonHeapMemory");
                            maxSwapSpaceSize = resultSetMax.getLong("MaxSwapSpaceSize");
                        }
                    }
                    // check Beta_1 results of the simple regression and compute Beta_0 coefficients
                    final double beta1UsedHeapMemory = resultSet.getDouble("Beta1UsedHeapMemory");
                    final double beta1UsedNonHeapMemory = resultSet.getDouble("Beta1UsedNonHeapMemory");
                    final double beta1UsedSwapSpaceSize = resultSet.getDouble("Beta1UsedSwapSpaceSize");
                    if (beta1UsedHeapMemory >= this.linearRegressionBeta1ForHeapMemory) {
                        result.add(new Advice(AdvisorOutOfMemoryPrediction.ADVICE_NAME_HEAPMEM,
                                this.getAdviceString(
                                        beta1UsedHeapMemory, this.linearRegressionBeta1ForHeapMemory,
                                        resultSet.getDouble("AvgTime"), resultSet.getDouble("AvgUsedHeapMemory"),
                                        maxHeapMemory, monitoringStartTime,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_HEAPMEM,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_HEAPMEM_OMM,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_MODEL
                                ), programInstance, programInstance.getCloudNode())
                        );
                    }
                    if (beta1UsedNonHeapMemory >= this.linearRegressionBeta1ForNonHeapMemory) {
                        result.add(new Advice(AdvisorOutOfMemoryPrediction.ADVICE_NAME_NONHEAPMEM,
                                this.getAdviceString(
                                        beta1UsedNonHeapMemory, this.linearRegressionBeta1ForNonHeapMemory,
                                        resultSet.getDouble("AvgTime"), resultSet.getDouble("AvgUsedNonHeapMemory"),
                                        maxNonHeapMemory, monitoringStartTime,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_NONHEAPMEM,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_NONHEAPMEM_OMM,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_MODEL
                                ), programInstance, programInstance.getCloudNode())
                        );
                    }
                    if (beta1UsedSwapSpaceSize >= this.linearRegressionBeta1ForSwapSpace) {
                        result.add(new Advice(AdvisorOutOfMemoryPrediction.ADVICE_NAME_SWAPFILE,
                                this.getAdviceString(
                                        beta1UsedSwapSpaceSize, this.linearRegressionBeta1ForSwapSpace,
                                        resultSet.getDouble("AvgTime"), resultSet.getDouble("AvgUsedSwapSpaceSize"),
                                        maxSwapSpaceSize, monitoringStartTime,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_SWAPSPACE,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_SWAPSPACE_OMM,
                                        AdvisorOutOfMemoryPrediction.ADVICE_TEXT_MODEL
                                ), programInstance, programInstance.getCloudNode())
                        );
                    }
                }
            }
        }
        catch (SQLException ex) {
            throw new AdvisorException("SQL exception when retrieving monitoring data", ex);
        }
        return result.toArray(new Advice[0]);
    }

    private String getAdviceString(double beta1UsedMemory, double beta1Recommended, double avgTime, double avgUsedMemory, long maxMemory, Timestamp monitoringStartTime, String textFirst, String textOMM, String textModel) {
        final double beta0UsedMemory = avgUsedMemory - beta1UsedMemory * avgTime;
        String advice = String.format(AdvisorOutOfMemoryPrediction.ADVISOR_LOCALE, textFirst, beta1UsedMemory, beta1Recommended);
        if ((maxMemory > 0) && (beta1UsedMemory != 0)) {
            final long outOfMemoryMiliseconds = (long) (1000D * (maxMemory - beta0UsedMemory) / beta1UsedMemory);
            advice += String.format(AdvisorOutOfMemoryPrediction.ADVISOR_LOCALE, textOMM,
                    (new Timestamp(monitoringStartTime.getTime() + outOfMemoryMiliseconds)).toString(),
                    this.getMilisecondsIntervalBreakdown(outOfMemoryMiliseconds), monitoringStartTime.toString(), maxMemory);
        }
        advice += String.format(AdvisorOutOfMemoryPrediction.ADVISOR_LOCALE, textModel, beta0UsedMemory, beta1UsedMemory);
        return advice;
    }

    private String getMilisecondsIntervalBreakdown(long miliseconds) {
        final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return dateFormat.format(new Date(miliseconds));
    }

    /**
     * Create the advisor that will utilize a database connection to get
     * monitoring data and to detect Juniper programs of a given Juniper
     * application where the memory usage is growing over time.
     *
     * @param juniperApplication a Juniper application model related to
     * monitoring data
     * @param monitoringDatabaseConnection a database connection to get
     * monitoring data
     */
    public AdvisorOutOfMemoryPrediction(JuniperApplication juniperApplication, Connection monitoringDatabaseConnection) {
        super(juniperApplication, monitoringDatabaseConnection);
    }

    /**
     * Get a maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the heap memory growth of a Juniper program (Bytes per
     * seconds; reaching of this value causes the advice generation).
     *
     * @return a maximal value of a Beta_1 coefficient in a simple linear
     * regression formula for the heap memory growth
     */
    public double getLinearRegressionBeta1ForHeapMemory() {
        return this.linearRegressionBeta1ForHeapMemory;
    }

    /**
     * Set a maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the heap memory growth of a Juniper program (Bytes per
     * seconds; reaching of this value causes the advice generation).
     *
     * @param linearRegressionBeta1ForHeapMemory a maximal value of a Beta_1
     * coefficient in a simple linear regression formula for the heap memory
     * growth
     */
    public void setLinearRegressionBeta1ForHeapMemory(double linearRegressionBeta1ForHeapMemory) {
        this.linearRegressionBeta1ForHeapMemory = linearRegressionBeta1ForHeapMemory;
    }

    /**
     * Get a maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the non-heap memory growth of a Juniper program (Bytes per
     * seconds; reaching of this value causes the advice generation).
     *
     * @return a maximal value of a Beta_1 coefficient in a simple linear
     * regression formula for the non-heap memory growth
     */
    public double getLinearRegressionBeta1ForNonHeapMemory() {
        return this.linearRegressionBeta1ForNonHeapMemory;
    }

    /**
     * Set a maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the non-heap memory growth of a Juniper program (reaching of
     * this value causes the advice generation).
     *
     * @param linearRegressionBeta1ForNonHeapMemory a maximal value of a Beta_1
     * coefficient in a simple linear regression formula for the non-heap memory
     * growth
     */
    public void setLinearRegressionBeta1ForNonHeapMemory(double linearRegressionBeta1ForNonHeapMemory) {
        this.linearRegressionBeta1ForNonHeapMemory = linearRegressionBeta1ForNonHeapMemory;
    }

    /**
     * Get a maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the swap space growth of a Juniper program (reaching of this
     * value causes the advice generation).
     *
     * @return a maximal value of a Beta_1 coefficient in a simple linear
     * regression formula for the swap space growth
     */
    public double getLinearRegressionBeta1ForSwapSpace() {
        return this.linearRegressionBeta1ForSwapSpace;
    }

    /**
     * Set a maximal value of a Beta_1 coefficient in a simple linear regression
     * formula for the swap space growth of a Juniper program (reaching of this
     * value causes the advice generation).
     *
     * @param linearRegressionBeta1ForSwapSpace a maximal value of a Beta_1
     * coefficient in a simple linear regression formula for the swap space
     * growth
     */
    public void setLinearRegressionBeta1ForSwapSpace(double linearRegressionBeta1ForSwapSpace) {
        this.linearRegressionBeta1ForSwapSpace = linearRegressionBeta1ForSwapSpace;
    }

    public static void main(String[] args) {
        System.err.println("\nAdvisor Name: " + AdvisorOutOfMemoryPrediction.ADVISOR_NAME
                + "\nAdvisor Description: " + AdvisorOutOfMemoryPrediction.ADVISOR_DESCRIPTION
                + "\nAdvice Name 1: " + AdvisorOutOfMemoryPrediction.ADVICE_NAME_HEAPMEM
                + "\nAdvice Text 1: " + AdvisorOutOfMemoryPrediction.ADVICE_TEXT_HEAPMEM
                + "\nAdvice Name 2: " + AdvisorOutOfMemoryPrediction.ADVICE_NAME_NONHEAPMEM
                + "\nAdvice Text 2: " + AdvisorOutOfMemoryPrediction.ADVICE_TEXT_NONHEAPMEM
                + "\nAdvice Name 3: " + AdvisorOutOfMemoryPrediction.ADVICE_NAME_SWAPFILE
                + "\nAdvice Text 3: " + AdvisorOutOfMemoryPrediction.ADVICE_TEXT_SWAPSPACE
                + "\nSQL:\n" + AdvisorOutOfMemoryPrediction.QUERY
                + "\nSQL max:\n" + AdvisorOutOfMemoryPrediction.QUERY_MAX
        );
    }

}
