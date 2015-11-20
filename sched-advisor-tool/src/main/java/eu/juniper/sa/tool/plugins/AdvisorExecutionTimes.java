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
 * The class of an advisor that just computes execution time statistics for
 * Juniper programs.
 *
 * @author rychly
 */
public class AdvisorExecutionTimes extends AdvisorUsingDatabaseAbstract implements AdvisorInterface {

    // Advisor's metadata
    private final static String ADVISOR_NAME = AdvisorExecutionTimes.class.getSimpleName();
    private final static String ADVISOR_DESCRIPTION
            = "This advisor just computes execution time statistics for Juniper programs.";
    private final static Locale ADVISOR_LOCALE = Locale.ENGLISH;
    // Advice's metadata
    private final static String ADVICE_NAME = "ExecutionTimes";
    private final static String ADVICE_TEXT
            = "The $ running at $"
            + " has the following execution time statistics for its %d executions:"
            + " total %f seconds, average %f seconds, minimal %f seconds, maximal %f seconds.";
    // Monitoring information processing SQL query
    private final static String[] QUERY_metricsInProgramRuntime = {"ProgramGlobalRank", "ProgramDuration"};
    private final static String QUERY
            = "SELECT m0.numericvalue AS ProgramGlobalRank,\n"
            + "  SUM(m1.numericvalue) AS ProgramDurationSum,\n"
            + "  AVG(m1.numericvalue) AS ProgramDurationAvg,\n"
            + "  MIN(m1.numericvalue) AS ProgramDurationMin,\n"
            + "  MAX(m1.numericvalue) AS ProgramDurationMax,\n"
            + "  COUNT(m1.numericvalue) AS ProgramDurationCount\n"
            + AdvisorUsingDatabaseAbstract.generateFromWhereFragment("ProgramRuntime", QUERY_metricsInProgramRuntime)
            + "  AND (records.time BETWEEN ? AND ?)\n"
            + "GROUP BY ProgramGlobalRank;";

    /**
     * Get a name of the advisor.
     *
     * @return a name of the advisor
     */
    @Override
    public String getName() {
        return AdvisorExecutionTimes.ADVISOR_NAME;
    }

    /**
     * Get a description of the advisor.
     *
     * @return a description of the advisor
     */
    @Override
    public String getDescription() {
        return AdvisorExecutionTimes.ADVISOR_DESCRIPTION;
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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final int programInstanceId = resultSet.getInt("ProgramGlobalRank");
                    final ProgramInstance programInstance
                            = this.getJuniperApplication().getProgramModel().getProgramInstanceById(programInstanceId);
                    if (programInstance == null) {
                        throw new AdvisorException("Cannot find Juniper program instance with ID " + programInstanceId);
                    }
                    result.add(new Advice(AdvisorExecutionTimes.ADVICE_NAME, String.format(AdvisorExecutionTimes.ADVISOR_LOCALE, AdvisorExecutionTimes.ADVICE_TEXT,
                            resultSet.getInt("ProgramDurationCount"),
                            resultSet.getDouble("ProgramDurationSum"),
                            resultSet.getDouble("ProgramDurationAvg"),
                            resultSet.getDouble("ProgramDurationMin"),
                            resultSet.getDouble("ProgramDurationMax")
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
     * Create the advisor that just computes execution time statistics for
     * Juniper programs of a given Juniper application.
     *
     * @param juniperApplication a Juniper application model related to
     * monitoring data
     * @param monitoringDatabaseConnection a database connection to get
     * monitoring data
     */
    public AdvisorExecutionTimes(JuniperApplication juniperApplication, Connection monitoringDatabaseConnection) {
        super(juniperApplication, monitoringDatabaseConnection);
    }

    public static void main(String[] args) {
        System.err.println("\nAdvisor Name: " + AdvisorExecutionTimes.ADVISOR_NAME
                + "\nAdvisor Description: " + AdvisorExecutionTimes.ADVISOR_DESCRIPTION
                + "\nAdvice Name: " + AdvisorExecutionTimes.ADVICE_NAME
                + "\nAdvice Text: " + AdvisorExecutionTimes.ADVICE_TEXT
                + "\nSQL:\n" + AdvisorExecutionTimes.QUERY
        );
    }

}
