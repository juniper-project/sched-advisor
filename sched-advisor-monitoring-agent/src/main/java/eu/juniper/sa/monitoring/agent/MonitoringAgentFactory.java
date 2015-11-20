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
package eu.juniper.sa.monitoring.agent;

import eu.juniper.sa.monitoring.resources.MonitoredResourcesStrategyInterface;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The factory class to create various types of monitoring agent objects. If
 * disabled, the factory will create the null device monitoring agents only.
 * Initial enabled of the factory class (enable/disabled) is set to enabled if
 * the <code>MonitoringAgentEnabled</code> system property is set and to
 * disabled otherwise.
 *
 * @author rychly
 */
public final class MonitoringAgentFactory {

    private static final String STATUS_PROPERTY = "MonitoringAgentEnabled";
    private static final String STATUS_VALUE = System.getProperty(MonitoringAgentFactory.STATUS_PROPERTY);
    private static final String STATUS_PROPERTY_EXCEPTION = "System property " + MonitoringAgentFactory.STATUS_PROPERTY + " has to be set to a local file path of a monitoring SQL file, a JDBC connection to a monitoring database, or an URL of a monitoring service.";
    private static final String STATUS_VALUE_PREF_JDBC = "jdbc:";
    private static final String STATUS_VALUE_PREF_HTTP = "http://";
    private static final String STATUS_VALUE_PREF_HTTPS = "https://";
    private static boolean enabled = (STATUS_VALUE != null);
    private static MonitoringAgentInterface monitoringAgentSingleton = null;

    /**
     * Check if the factory is enabled. If disabled, the factory will create the
     * null device monitoring agents only.
     *
     * @return true if the factory is enabled, false otherwise
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the enabled/disabled status of the factory. If disabled, the factory
     * will create the null device monitoring agents only.
     *
     * @param enabled true if the factory is enabled, false otherwise
     */
    public static void setEnabled(boolean enabled) {
        if (enabled != MonitoringAgentFactory.enabled) {
            // different agent singletons for enabled and disabled states
            MonitoringAgentFactory.monitoringAgentSingleton = null;
        }
        MonitoringAgentFactory.enabled = enabled;
    }

    /**
     * Enable the factory so it will create working monitoring agents as
     * requested.
     */
    public static void enable() {
        MonitoringAgentFactory.setEnabled(true);
    }

    /**
     * Disable the factory so it will create the null device monitoring agents
     * only.
     */
    public static void disable() {
        MonitoringAgentFactory.setEnabled(false);
    }

    /**
     * Create the null device target monitoring agent.
     *
     * @return a created monitoring agent
     */
    public static MonitoringAgentInterface createMonitoringAgentForNullDevice() {
        return new MonitoringAgentForNullDevice();
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring service with a given default monitored resource
     * strategy.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     * @return a created monitoring agent
     */
    public static MonitoringAgentInterface createMonitoringAgentForService(String monitoringServiceURL, String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) {
        return MonitoringAgentFactory.enabled
                ? new MonitoringAgentForService(monitoringServiceURL, applicationId, monitoredResourcesDefaultStrategy)
                : new MonitoringAgentForNullDevice();
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring service with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param monitoringServiceURL a monitoring service URL (it should end with
     * '/' character)
     * @param applicationId an application ID
     * @return a created monitoring agent
     */
    public static MonitoringAgentInterface createMonitoringAgentForService(String monitoringServiceURL, String applicationId) {
        return MonitoringAgentFactory.enabled
                ? new MonitoringAgentForService(monitoringServiceURL, applicationId)
                : new MonitoringAgentForNullDevice();
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring SQL file with a given default monitored resource
     * strategy.
     *
     * @param monitoringSqlFileName a monitoring SQL file name
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     * @return a created monitoring agent
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     */
    public static MonitoringAgentInterface createMonitoringAgentForSqlFile(String monitoringSqlFileName, String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) throws IOException {
        return MonitoringAgentFactory.enabled
                ? new MonitoringAgentForSqlFile(monitoringSqlFileName, applicationId, monitoredResourcesDefaultStrategy)
                : new MonitoringAgentForNullDevice();
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring SQL file with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param monitoringSqlFileName a monitoring SQL file name
     * @param applicationId an application ID
     * @return a created monitoring agent
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     */
    public static MonitoringAgentInterface createMonitoringAgentForSqlFile(String monitoringSqlFileName, String applicationId) throws IOException {
        return MonitoringAgentFactory.enabled
                ? new MonitoringAgentForSqlFile(monitoringSqlFileName, applicationId)
                : new MonitoringAgentForNullDevice();
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring database connection with a given default monitored
     * resource strategy.
     *
     * @param monitoringDatabaseConnection a monitoring database connection
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     * @return a created monitoring agent
     * @throws java.sql.SQLException if the monitoring database connection
     * cannot be used
     */
    public static MonitoringAgentInterface createMonitoringAgentForDatabase(Connection monitoringDatabaseConnection, String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) throws SQLException {
        return MonitoringAgentFactory.enabled
                ? new MonitoringAgentForDatabase(monitoringDatabaseConnection, applicationId, monitoredResourcesDefaultStrategy)
                : new MonitoringAgentForNullDevice();
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring database connection with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param monitoringDatabaseConnection a monitoring database connection
     * @param applicationId an application ID
     * @return a created monitoring agent
     * @throws java.sql.SQLException if the monitoring database connection
     * cannot be used
     */
    public static MonitoringAgentInterface createMonitoringAgentForDatabase(Connection monitoringDatabaseConnection, String applicationId) throws SQLException {
        return MonitoringAgentFactory.enabled
                ? new MonitoringAgentForDatabase(monitoringDatabaseConnection, applicationId)
                : new MonitoringAgentForNullDevice();
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring SQL file, JDBC database, or a given monitoring service
     * (if <code>monitoringUrl</code> is a local file path, JDBC connectino
     * string, or an URL, respectively) with a given default monitored resource
     * strategy.
     *
     * @param monitoringUrl a local file path to the monitoring SQL file, a JDBC
     * connection string, or an URL to the monitoring service
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     * @return a created monitoring agent
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     * @throws java.sql.SQLException if the JDBC connection string cannot be
     * used
     */
    public static MonitoringAgentInterface createMonitoringAgent(String monitoringUrl, String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) throws IOException, SQLException {
        MonitoringAgentInterface monitoringAgent;
        if (monitoringUrl.startsWith(MonitoringAgentFactory.STATUS_VALUE_PREF_JDBC)) {
            monitoringAgent = MonitoringAgentFactory.createMonitoringAgentForDatabase(DriverManager.getConnection(monitoringUrl), applicationId, monitoredResourcesDefaultStrategy);
        } else if (monitoringUrl.startsWith(MonitoringAgentFactory.STATUS_VALUE_PREF_HTTP) || monitoringUrl.startsWith(MonitoringAgentFactory.STATUS_VALUE_PREF_HTTPS)) {
            monitoringAgent = MonitoringAgentFactory.createMonitoringAgentForService(monitoringUrl, applicationId, monitoredResourcesDefaultStrategy);
        } else {
            monitoringAgent = MonitoringAgentFactory.createMonitoringAgentForSqlFile(monitoringUrl, applicationId, monitoredResourcesDefaultStrategy);
        }
        return monitoringAgent;
    }

    /**
     * Create a monitoring agent for monitoring of a given application to a
     * given monitoring SQL file, JDBC database, or a given monitoring service
     * (if <code>monitoringUrl</code> is a local file path, JDBC connectino
     * string, or an URL, respectively) with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param monitoringUrl a local file path to the monitoring SQL file, a JDBC
     * connection string, or an URL to the monitoring service
     * @param applicationId an application ID
     * @return a created monitoring agent
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     * @throws java.sql.SQLException if the JDBC connection string cannot be
     * used
     */
    public static MonitoringAgentInterface createMonitoringAgent(String monitoringUrl, String applicationId) throws IOException, SQLException {
        MonitoringAgentInterface monitoringAgent;
        if (monitoringUrl.startsWith(MonitoringAgentFactory.STATUS_VALUE_PREF_JDBC)) {
            monitoringAgent = MonitoringAgentFactory.createMonitoringAgentForDatabase(DriverManager.getConnection(monitoringUrl), applicationId);
        } else if (monitoringUrl.startsWith(MonitoringAgentFactory.STATUS_VALUE_PREF_HTTP) || monitoringUrl.startsWith(MonitoringAgentFactory.STATUS_VALUE_PREF_HTTPS)) {
            monitoringAgent = MonitoringAgentFactory.createMonitoringAgentForService(monitoringUrl, applicationId);
        } else {
            monitoringAgent = MonitoringAgentFactory.createMonitoringAgentForSqlFile(monitoringUrl, applicationId);
        }
        return monitoringAgent;
    }

    /**
     * Create a monitoring agent singleton (or utilize the previously created)
     * for monitoring of a given application to a predefined monitoring SQL
     * file, JDBC database, or monitoring service (if system property named
     * according to <code>STATUS_PROPERTY</code> value is a local file path, a
     * JDBC connection string, or an URL, respectively) with a given default
     * monitored resource strategy.
     *
     * @param applicationId an application ID
     * @param monitoredResourcesDefaultStrategy a default monitored resource
     * strategy used by the agent
     * @return a created monitoring agent
     * @throws IllegalStateException if the system property value is not set
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     * @throws java.sql.SQLException if the JDBC connection string cannot be
     * used
     */
    public static MonitoringAgentInterface createMonitoringAgentSingletonBySystemProperty(String applicationId, MonitoredResourcesStrategyInterface monitoredResourcesDefaultStrategy) throws IllegalStateException, IOException, SQLException {
        if (MonitoringAgentFactory.enabled && STATUS_VALUE.isEmpty()) {
            throw new IllegalStateException(MonitoringAgentFactory.STATUS_PROPERTY_EXCEPTION);
        }
        if (MonitoringAgentFactory.monitoringAgentSingleton == null) {
            MonitoringAgentFactory.monitoringAgentSingleton = MonitoringAgentFactory.enabled
                    ? MonitoringAgentFactory.createMonitoringAgent(STATUS_VALUE, applicationId, monitoredResourcesDefaultStrategy)
                    : MonitoringAgentFactory.createMonitoringAgentForNullDevice();
        }
        return MonitoringAgentFactory.monitoringAgentSingleton;
    }

    /**
     * Create a monitoring agent singleton (or utilize the previously created)
     * for monitoring of a given application to a predefined monitoring SQL
     * file, JDBC database, or monitoring service (if system property named
     * according to <code>STATUS_PROPERTY</code> value is a local file path, a
     * JDBC connection string, or an URL, respectively) with
     * <code>MonitoredResourcesDefaultStrategy</code> default monitored resource
     * strategy.
     *
     * @param applicationId an application ID
     * @return a created monitoring agent
     * @throws IllegalStateException if the system property value is not set
     * @throws java.io.IOException if the monitoring SQL file cannot be opened
     * or created
     * @throws java.sql.SQLException if the JDBC connection string cannot be
     * used
     */
    public static MonitoringAgentInterface createMonitoringAgentSingletonBySystemProperty(String applicationId) throws IllegalStateException, IOException, SQLException {
        if (MonitoringAgentFactory.enabled && STATUS_VALUE.isEmpty()) {
            throw new IllegalStateException(MonitoringAgentFactory.STATUS_PROPERTY_EXCEPTION);
        }
        if (MonitoringAgentFactory.monitoringAgentSingleton == null) {
            MonitoringAgentFactory.monitoringAgentSingleton = MonitoringAgentFactory.enabled
                    ? MonitoringAgentFactory.createMonitoringAgent(STATUS_VALUE, applicationId)
                    : MonitoringAgentFactory.createMonitoringAgentForNullDevice();
        }
        return MonitoringAgentFactory.monitoringAgentSingleton;
    }

    /**
     * Get a monitoring agent singleton created by a previous call of
     * <code>createMonitoringAgentBySystemProperty</code> method.
     *
     * @return a monitoring agent singleton
     */
    public static MonitoringAgentInterface getMonitoringAgentSingleton() {
        return monitoringAgentSingleton;
    }

    public static void main(String[] args) {
        System.err.println(MonitoringAgentFactory.class.getCanonicalName() + ".enabled = " + MonitoringAgentFactory.enabled);
        System.err.println("Set " + MonitoringAgentFactory.STATUS_PROPERTY + " system property to enable the class property above."
                + " If createMonitoringAgentBySystemProperty is used, the system property value should be a local file path of a monitoring SQL file, a JDBC connection to a monitoring database, or an URL of a monitoring service."
                + " Current value of " + MonitoringAgentFactory.STATUS_PROPERTY + " system property is '" + MonitoringAgentFactory.STATUS_VALUE + "'");
    }
}
