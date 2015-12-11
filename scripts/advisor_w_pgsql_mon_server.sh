#!/bin/sh

DEPLOYMENTPLAN="$(pwd)/rte_deployment_plan.xml"

JDBCURL="$(grep '^url=' db-connection.properties | cut -d '=' -f 2- | tr -d '\r\n')"
JDBCUSER="$(grep '^user=' db-connection.properties | cut -d '=' -f 2- | tr -d '\r\n')"
JDBCPASSWORD="$(grep '^pwd=' db-connection.properties | cut -d '=' -f 2- | tr -d '\r\n')"

PACKAGE=eu.juniper.sa.tool

exec java -classpath external_libs/sched-advisor-tool-1.0-jar-with-dependencies.jar:external_libs/postgresql-9.4-1201.jdbc4.jar \
	"-DMonitoringJdbcUser=${JDBCUSER}" "-DMonitoringJdbcPassword=${JDBCPASSWORD}" \
	"${PACKAGE}.Advisor" "${DEPLOYMENTPLAN}" "${JDBCURL}" advice.xml "$@"
