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
 * The class of an advisor that detects Juniper programs with long data transfer
 * times (i.e., a time spent on waiting for receiving data) and short
 * computation.
 *
 * @author rychly
 */
public class AdvisorDataTransferOverhead extends AdvisorUsingDatabaseAbstract implements AdvisorInterface {

    // Advisor's metadata
    private final static String ADVISOR_NAME = AdvisorDataTransferOverhead.class.getSimpleName();
    private final static String ADVISOR_DESCRIPTION
            = "This advisor detects Juniper programs with long data"
            + " communication times (i.e., a time spent on waiting for receiving data)"
            + " and short computation. This indicate a very simple program with a high"
            + " data transfer overhead (it should be merged with other programs) or a program"
            + " waiting for data most the time (data flows/stream should be optimized"
            + " in the Juniper application of the Juniper program).";
    private final static Locale ADVISOR_LOCALE = Locale.ENGLISH;
    // Advice's metadata
    private final static String ADVICE_NAME = "DataTransferOverhead";
    private final static String ADVICE_TEXT
            = "The $ running at $"
            + " was receiving data in %f seconds of %f seconds of its total execution time"
            + " (averages are %f seconds for %d receives of data and %f seconds for %d executions)."
            + " That makes %f percentage of execution time spent by receiving data"
            + " (the cases with %f percentage and above are reported).";
    // Monitoring information processing SQL query
    private final static String[] QUERY_metricsInProgramRuntime = {"ProgramGlobalRank", "ProgramDuration"};
    private final static String[] QUERY_metricsInSendReceive = {"ReceiverGlobalRank", "SendReceiveDuration"};
    private final static String QUERY
            = "SELECT ProgramGlobalRank,\n"
            + "  ProgramDurationSum,\n"
            + "  ProgramDurationAvg,\n"
            + "  ProgramDurationCount,\n"
            + "  SendReceiveDurationSum,\n"
            + "  SendReceiveDurationAvg,\n"
            + "  SendReceiveDurationCount,\n"
            + "  SendReceiveDurationSum/ProgramDurationSum AS TransferToExecutionDurationRatio\n"
            + "FROM\n"
            + "  (SELECT m0.numericvalue AS ProgramGlobalRank,\n"
            + "    SUM(m1.numericvalue) AS ProgramDurationSum,\n"
            + "    AVG(m1.numericvalue) AS ProgramDurationAvg,\n"
            + "    COUNT(m1.numericvalue) AS ProgramDurationCount\n"
            + AdvisorUsingDatabaseAbstract.generateFromWhereFragment("ProgramRuntime", QUERY_metricsInProgramRuntime)
            + "  AND (records.time BETWEEN ? AND ?)\n"
            + "  GROUP BY ProgramGlobalRank\n"
            + "  ) ProgramRuntime\n"
            + "JOIN\n"
            + "  (SELECT m0.numericvalue AS ReceiverGlobalRank,\n"
            + "    SUM(m1.numericvalue) AS SendReceiveDurationSum,\n"
            + "    AVG(m1.numericvalue) AS SendReceiveDurationAvg,\n"
            + "    COUNT(m1.numericvalue) AS SendReceiveDurationCount\n"
            + AdvisorUsingDatabaseAbstract.generateFromWhereFragment("SendReceive", QUERY_metricsInSendReceive)
            + "  AND (records.time BETWEEN ? AND ?)\n"
            + "  GROUP BY ReceiverGlobalRank\n"
            + "  ) SendReceive ON (ProgramRuntime.ProgramGlobalRank = SendReceive.ReceiverGlobalRank)\n"
            + "WHERE SendReceiveDurationSum/ProgramDurationSum     >= ?\n"
            + "ORDER BY TransferToExecutionDurationRatio DESC;";

    /**
     * Maximal ratio of total data communication duration to total execution time of
     * a Juniper program (reaching of this ration causes the advice generation).
     */
    protected double receivingToExecutionDurationRatio = 0; // 0.50

    /**
     * Get a name of the advisor.
     *
     * @return a name of the advisor
     */
    @Override
    public String getName() {
        return AdvisorDataTransferOverhead.ADVISOR_NAME;
    }

    /**
     * Get a description of the advisor.
     *
     * @return a description of the advisor
     */
    @Override
    public String getDescription() {
        return AdvisorDataTransferOverhead.ADVISOR_DESCRIPTION;
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
            preparedStatement.setTimestamp(3, monitoringStartTime);
            preparedStatement.setTimestamp(4, monitoringEndTime);
            preparedStatement.setDouble(5, this.receivingToExecutionDurationRatio);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final int programInstanceId = resultSet.getInt("ProgramGlobalRank");
                    final ProgramInstance programInstance
                            = this.getJuniperApplication().getProgramModel().getProgramInstanceById(programInstanceId);
                    if (programInstance == null) {
                        throw new AdvisorException("Cannot find Juniper program instance with ID " + programInstanceId);
                    }
                    result.add(new Advice(AdvisorDataTransferOverhead.ADVICE_NAME, String.format(
                            AdvisorDataTransferOverhead.ADVISOR_LOCALE, AdvisorDataTransferOverhead.ADVICE_TEXT,
                            resultSet.getDouble("SendReceiveDurationSum"),
                            resultSet.getDouble("ProgramDurationSum"),
                            resultSet.getDouble("SendReceiveDurationAvg"),
                            resultSet.getInt("SendReceiveDurationCount"),
                            resultSet.getDouble("ProgramDurationAvg"),
                            resultSet.getInt("ProgramDurationCount"),
                            resultSet.getDouble("TransferToExecutionDurationRatio") * 100,
                            this.receivingToExecutionDurationRatio * 100
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
     * monitoring data and to detect high overhead of data transfers in programs
     * of a given Juniper application.
     *
     * @param juniperApplication a Juniper application model related to
     * monitoring data
     * @param monitoringDatabaseConnection a database connection to get
     * monitoring data
     */
    public AdvisorDataTransferOverhead(JuniperApplication juniperApplication, Connection monitoringDatabaseConnection) {
        super(juniperApplication, monitoringDatabaseConnection);
    }

    /**
     * Get a maximal ratio of total data communication duration to total execution
     * time of a Juniper program (reaching of this ration causes the advice
     * generation).
     *
     * @return a maximal ratio of total data communication duration to total
     * execution time of a Juniper program
     */
    public double getReceivingToExecutionDurationRatio() {
        return this.receivingToExecutionDurationRatio;
    }

    /**
     * Set a maximal ratio of total data communication duration to total execution
     * time of a Juniper program (reaching of this ration causes the advice
     * generation).
     *
     * @param receivingToExecutionDurationRatio a maximal ratio of total data
     * communication duration to total execution time of a Juniper program
     */
    public void setReceivingToExecutionDurationRatio(double receivingToExecutionDurationRatio) {
        this.receivingToExecutionDurationRatio = receivingToExecutionDurationRatio;
    }

    public static void main(String[] args) {
        System.err.println(
                "\nAdvisor Name: " + AdvisorDataTransferOverhead.ADVISOR_NAME
                + "\nAdvisor Description: " + AdvisorDataTransferOverhead.ADVISOR_DESCRIPTION
                + "\nAdvice Name: " + AdvisorDataTransferOverhead.ADVICE_NAME
                + "\nAdvice Text: " + AdvisorDataTransferOverhead.ADVICE_TEXT
                + "\nSQL:\n" + AdvisorDataTransferOverhead.QUERY
        );
    }

}
