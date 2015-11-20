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

import eu.juniper.sa.deployment.model.JuniperApplication;
import eu.juniper.sa.tool.utils.ClassFinder;
import java.beans.IntrospectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * An abstract class for advisors utilizing database connections to get
 * monitoring data.
 *
 * @author rychly
 */
public abstract class AdvisorUsingDatabaseAbstract implements AdvisorInterface {

    private final JuniperApplication juniperApplication;
    private final Connection monitoringDatabaseConnection;
    private boolean enabled = true;
    private final static String QUERY_FIRST_TIMESTAMP
            = "SELECT MIN(time) FROM records;";
    private final static String QUERY_LAST_TIMESTAMP
            = "SELECT MAX(time) FROM records;";

    /**
     * Create the advisor that will utilize a database connection to get
     * monitoring data.
     *
     * @param juniperApplication a Juniper application model related to
     * monitoring data
     * @param monitoringDatabaseConnection a database connection to get
     * monitoring data
     */
    public AdvisorUsingDatabaseAbstract(JuniperApplication juniperApplication, Connection monitoringDatabaseConnection) {
        this.juniperApplication = juniperApplication;
        this.monitoringDatabaseConnection = monitoringDatabaseConnection;
    }

    /**
     * Check if the advisor is enabled.
     *
     * @return <code>true</code> if the advisor is enabled, <code>false</code>
     * otherwise
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Enable the advisor.
     *
     * @param enabled <code>true</code> to enable the advisor,
     * <code>false</code> to disable the advisor
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Check if the advisor is disabled.
     *
     * @return <code>true</code> if the advisor is disabled, <code>false</code>
     * otherwise
     */
    @Override
    public boolean isDisabled() {
        return !this.enabled;
    }

    /**
     * Disable the advisor.
     *
     * @param disabled <code>true</code> to disable the advisor,
     * <code>false</code> to enable the advisor
     */
    @Override
    public void setDisabled(boolean disabled) {
        this.enabled = !disabled;
    }

    /**
     * Create an instance of a given class implementing AdvisorInterface with
     * given arguments.
     *
     * @param advisorClass a given class implementing AdvisorInterface
     * @param juniperApplication a Juniper application model related to
     * monitoring data
     * @param monitoringDatabaseConnection a database connection to get
     * monitoring data
     * @throws AdvisorException if the advisor class cannot be instantiated
     * @return an instance of a given class
     */
    public static AdvisorInterface newInstance(Class<?> advisorClass, JuniperApplication juniperApplication, Connection monitoringDatabaseConnection) throws AdvisorException {
        if (!AdvisorUsingDatabaseAbstract.class.isAssignableFrom(advisorClass)) {
            throw new AdvisorException("Class in the first argument does not extend AdvisorUsingDatabaseAbstract class.", new IllegalArgumentException());
        }
        try {
            final Constructor<?> advisorConstructor = advisorClass.getDeclaredConstructor(JuniperApplication.class, Connection.class);
            return (AdvisorInterface) advisorConstructor.newInstance(juniperApplication, monitoringDatabaseConnection);
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new AdvisorException("Class in the first argument cannot be instantiated by the second and third arguments.", ex);
        }
    }

    public void setObjectProperties(Properties properties) throws AdvisorException {
        final String thisSimpleName = this.getClass().getSimpleName();
        for (String propertyName : properties.stringPropertyNames()) {
            if (propertyName.startsWith(thisSimpleName + ".")) {
                try {
                    if (!ClassFinder.setProperty(this, propertyName.substring(thisSimpleName.length() + 1), properties.getProperty(propertyName))) {
                        throw new AdvisorException("Property " + propertyName + " cannot be set due to incorrect name or value.");
                    }
                }
                catch (IntrospectionException | InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
                    throw new AdvisorException("Property " + propertyName + " cannot be set due to a Java exception.", ex);
                }
            }
        }
    }

    /**
     * Get a Juniper application model related to monitoring data.
     *
     * @return a Juniper application model related to monitoring data
     */
    public JuniperApplication getJuniperApplication() {
        return juniperApplication;
    }

    /**
     * Get a database connection to get monitoring data.
     *
     * @return a database connection to get monitoring data
     */
    public Connection getMonitoringDatabaseConnection() {
        return monitoringDatabaseConnection;
    }

    private Timestamp getTimestampOfFirstRecord() throws SQLException {
        Timestamp result = null;
        try (Statement statement = this.monitoringDatabaseConnection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(QUERY_FIRST_TIMESTAMP)) {
                if (resultSet.next()) {
                    result = resultSet.getTimestamp(1);
                }
            }
        }
        return result;
    }

    private Timestamp getTimestampOfLastRecord() throws SQLException {
        Timestamp result = null;
        try (Statement statement = this.monitoringDatabaseConnection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(QUERY_LAST_TIMESTAMP)) {
                if (resultSet.next()) {
                    result = resultSet.getTimestamp(1);
                }
            }
        }
        return result;
    }

    /**
     * Execute advisor on all monitoring results and produce a list of advice.
     *
     * @return a list of advice
     * @throws AdvisorException if there is error while reading the monitoring
     * results
     */
    @Override
    public Advice[] execute() throws AdvisorException {
        try {
            return this.execute(this.getTimestampOfFirstRecord());
        }
        catch (SQLException ex) {
            throw new AdvisorException(ex);
        }
    }

    /**
     * Execute advisor on selected monitoring results and produce a list of
     * advice.
     *
     * @param monitoringStartTime a start time of the monitoring results
     * @return a list of advice
     * @throws AdvisorException if there is error while reading the monitoring
     * results
     */
    @Override
    public Advice[] execute(Timestamp monitoringStartTime) throws AdvisorException {
        try {
            return this.execute(monitoringStartTime, this.getTimestampOfLastRecord());
        }
        catch (SQLException ex) {
            throw new AdvisorException(ex);
        }
    }

    /**
     * Generate FROM and WHERE parts of an SQL SELECT statement to get values of
     * given monitoring metrics of a given type. Please note that the generated
     * from clause uses an inner join, so all joined tables must contain
     * definned metrics (if some of the metrics are missing in the databases the
     * whole result is empty).
     *
     * @param metricType a type of monitoring metrics
     * @param metricNames a names of values of monitoring metrics to get
     * @return a fragment of an SQL SELECT statement
     */
    protected static String generateFromWhereFragment(String metricType, String[] metricNames) {
        return generateFromWhereFragment(metricType, metricNames, false);
    }

    /**
     * Generate FROM and WHERE parts of an SQL SELECT statement to get values of
     * given monitoring metrics of a given type and using inner or left outer
     * join in the generated from clause. Please note that in the case of (left)
     * outer join, performance may significantly drop.
     *
     * @param metricType a type of monitoring metrics
     * @param metricNames a names of values of monitoring metrics to get
     * @param useLeftOuterJoins <code>true</code> to use (left) outer joins,
     * <code>false</code> to use inner joins
     * @return a fragment of an SQL SELECT statement
     */
    protected static String generateFromWhereFragment(String metricType, String[] metricNames, boolean useLeftOuterJoins) {
        String resultFrom = " FROM records ";
        String resultWhere = "WHERE (metrictype = '" + metricType + "') ";
        for (int i = 0; i < metricNames.length; i++) {
            if (useLeftOuterJoins) {
                resultFrom += "LEFT OUTER ";
            }
            resultFrom += "JOIN metrics m" + i + " ON (records.id = m" + i + ".recordid) ";
            resultWhere += "AND (m" + i + ".name = '" + metricNames[i] + "') ";
        }
        return resultFrom + resultWhere;
    }

}
