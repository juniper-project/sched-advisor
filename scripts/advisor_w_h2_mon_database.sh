#!/bin/sh

DEPLOYMENTPLAN="$(pwd)/rte_deployment_plan.xml"

MYDBFILE="$(pwd)/monitoring_data_db"

PACKAGE=eu.juniper.sa.tool

exec java -classpath external_libs/sched-advisor-tool-1.0-jar-with-dependencies.jar:external_libs/postgresql-9.4-1201.jdbc4.jar \
	"-DMonitoringJdbcUser=${JDBCUSER}" "-DMonitoringJdbcPassword=${JDBCPASSWORD}" \
	"${PACKAGE}.Advisor" "${DEPLOYMENTPLAN}" "jdbc:h2:/${MYDBFILE};COMPRESS=TRUE" advice.xml "$@"
