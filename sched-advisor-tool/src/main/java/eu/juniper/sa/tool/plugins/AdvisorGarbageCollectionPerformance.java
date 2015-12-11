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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;

/**
 * The class of an advisor that detects Juniper programs that spent much time on
 * garbage collecting.
 *
 * @author rychly
 */
public class AdvisorGarbageCollectionPerformance extends AdvisorUsingDatabaseAbstract implements AdvisorInterface {

    // Advisor's metadata
    private final static String ADVISOR_NAME = AdvisorGarbageCollectionPerformance.class.getSimpleName();
    private final static String ADVISOR_DESCRIPTION
            = "This advisor detects Juniper programs that spent much time on garbage collecting."
            + " Long garbage collections may affect negatively responsiveness of"
            + " a Juniper application which is critical in real-time stream"
            + " processing of Big Data (any delay in processing of such data"
            + " may result into data-loss issues).";
    // Advice's metadata
    private final static String ADVICE_NAME = "GarbageCollectionDelays";
    private final static String ADVICE_TEXT
            = "The Java Hotspot JVM of the $ running at $"
            + " was performed %d garbage collections that took %f seconds"
            + " in %f seconds of total execution time of the program"
            + " (averages are %f seconds per garbage collection and %f seconds for the execution time)."
            + " That makes %f percentage of execution time spent by garbage collections"
            + " (the cases with %f percentage and above are reported).";
    private final static Locale ADVISOR_LOCALE = Locale.ENGLISH;
    // Monitoring information processing SQL query
    private final static String[] QUERY_metricsInProgramRuntime = {
        "ProgramGlobalRank",
        "ProgramDuration",
        "GarbageCollectionCount",
        "GarbageCollectionTime"
    };
    private final static String QUERY
            = "SELECT m0.numericvalue AS ProgramGlobalRank,\n"
            + "  SUM(m1.numericvalue) AS ProgramDurationSum,\n"
            + "  AVG(m1.numericvalue) AS ProgramDurationAvg,\n"
            + "  SUM(m2.numericvalue) AS GarbageCollectionCount,\n"
            + "  SUM(m3.numericvalue) AS GarbageCollectionTimeSum,\n"
            + "  CASE WHEN SUM(m2.numericvalue)=0 THEN 0 ELSE SUM(m3.numericvalue)/SUM(m2.numericvalue) END AS GarbageCollectionTimeAvg,\n"
            + "  CASE WHEN SUM(m1.numericvalue)=0 THEN 0 ELSE SUM(m3.numericvalue)/SUM(m1.numericvalue) END AS GarbageCollectionToExecutionDurationRatio\n"
            + AdvisorUsingDatabaseAbstract.generateFromWhereFragment("ProgramRuntime", QUERY_metricsInProgramRuntime)
            + "AND (records.time BETWEEN ? AND ?)\n"
            + "GROUP BY ProgramGlobalRank\n"
            + "HAVING CASE WHEN SUM(m1.numericvalue)=0 THEN 0 ELSE SUM(m3.numericvalue)/SUM(m1.numericvalue) END >= ?\n"
            + "ORDER BY GarbageCollectionToExecutionDurationRatio DESC;";

    /**
     * Maximal ratio of total garbage collection duration to total execution
     * time of a Juniper program (reaching of this ration causes the advice
     * generation).
     */
    protected double garbageCollectionToExecutionDurationRatio = 0; // 0.02

    /**
     * Get a name of the advisor.
     *
     * @return a name of the advisor
     */
    @Override
    public String getName() {
        return AdvisorGarbageCollectionPerformance.ADVISOR_NAME;
    }

    /**
     * Get a description of the advisor.
     *
     * @return a description of the advisor
     */
    @Override
    public String getDescription() {
        return AdvisorGarbageCollectionPerformance.ADVISOR_DESCRIPTION;
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
        try (PreparedStatement preparedStatement = this.getMonitoringDatabaseConnection().prepareStatement(QUERY)) {
            preparedStatement.setTimestamp(1, monitoringStartTime);
            preparedStatement.setTimestamp(2, monitoringEndTime);
            preparedStatement.setDouble(3, this.garbageCollectionToExecutionDurationRatio);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final int programInstanceId = resultSet.getInt("ProgramGlobalRank");
                    final ProgramInstance programInstance
                            = this.getJuniperApplication().getProgramModel().getProgramInstanceById(programInstanceId);
                    if (programInstance == null) {
                        throw new AdvisorException("Cannot find Juniper program instance with ID " + programInstanceId);
                    }
                    result.add(new Advice(AdvisorGarbageCollectionPerformance.ADVICE_NAME, String.format(
                            AdvisorGarbageCollectionPerformance.ADVISOR_LOCALE, AdvisorGarbageCollectionPerformance.ADVICE_TEXT,
                            resultSet.getInt("GarbageCollectionCount"),
                            resultSet.getDouble("GarbageCollectionTimeSum"),
                            resultSet.getDouble("ProgramDurationSum"),
                            resultSet.getDouble("GarbageCollectionTimeAvg"),
                            resultSet.getDouble("ProgramDurationAvg"),
                            resultSet.getDouble("GarbageCollectionToExecutionDurationRatio") * 100,
                            this.garbageCollectionToExecutionDurationRatio * 100
                    ), programInstance, programInstance.getCloudNode()));
                }
            }
        }
        catch (SQLException ex) {
            throw new AdvisorException("SQL exception when retrieving monitoring data", ex);
        }
        return result.toArray(new Advice[0]);
    }

    /**
     * Create the advisor that will utilize a database connection to get
     * monitoring data and to detect Juniper programs of a given Juniper
     * application that spent much time on garbage collecting.
     *
     * @param juniperApplication a Juniper application model related to
     * monitoring data
     * @param monitoringDatabaseConnection a database connection to get
     * monitoring data
     */
    public AdvisorGarbageCollectionPerformance(JuniperApplication juniperApplication, Connection monitoringDatabaseConnection) {
        super(juniperApplication, monitoringDatabaseConnection);
    }

    /**
     * Get a maximal ratio of total garbage collection duration to total
     * execution time of a Juniper program (reaching of this ration causes the
     * advice generation).
     *
     * @return a maximal ratio of total garbage collection duration to total
     * execution time of a Juniper program
     */
    public double getGarbageCollectionToExecutionDurationRatio() {
        return garbageCollectionToExecutionDurationRatio;
    }

    /**
     * Set a maximal ratio of total garbage collection duration to total
     * execution time of a Juniper program (reaching of this ration causes the
     * advice generation).
     *
     * @param garbageCollectionToExecutionDurationRatio a maximal ratio of total
     * garbage collection duration to total execution time of a Juniper program
     */
    public void setGarbageCollectionToExecutionDurationRatio(double garbageCollectionToExecutionDurationRatio) {
        this.garbageCollectionToExecutionDurationRatio = garbageCollectionToExecutionDurationRatio;
    }

    public static void main(String[] args) {
        System.err.println(
                "\nAdvisor Name: " + AdvisorGarbageCollectionPerformance.ADVISOR_NAME
                + "\nAdvisor Description: " + AdvisorGarbageCollectionPerformance.ADVISOR_DESCRIPTION
                + "\nAdvice Name: " + AdvisorGarbageCollectionPerformance.ADVICE_NAME
                + "\nAdvice Text: " + AdvisorGarbageCollectionPerformance.ADVICE_TEXT
                + "\nSQL:\n" + AdvisorGarbageCollectionPerformance.QUERY
        );
    }

}
