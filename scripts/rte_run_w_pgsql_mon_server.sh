#!/bin/sh

DIR="$(pwd)"
CLASSPATH="$(find -L external_libs -name '*.jar' -printf ':%p')"
DEPLOYMENTPLAN="${DIR}/rte_deployment_plan.xml"
PROCESSES="$(grep --count '<cloudnode ' ${DEPLOYMENTPLAN})"

JDBCURL="$(grep '^url=' db-connection.properties | cut -d '=' -f 2- | tr -d '\r\n')"
JDBCUSER="$(grep '^user=' db-connection.properties | cut -d '=' -f 2- | tr -d '\r\n')"
JDBCPASSWORD="$(grep '^pwd=' db-connection.properties | cut -d '=' -f 2- | tr -d '\r\n')"

echo "Monitoring data will be stored into ${JDBCURL}"
echo "Database tables need to be created before this execution by: ./access_pgsql_mon_server.sh create"
echo "If the database already contains monitoring data, it is recommended to clean it by: ./access_pgsql_mon_server.sh clean"

exec mpirun -machinefile hosts -np "${PROCESSES:-0}" --map-by node \
	java -classpath "bin${CLASSPATH}" \
	"-DMonitoringAgentEnabled=${JDBCURL}" "-DMonitoringAgentJdbcUser=${JDBCUSER}" "-DMonitoringAgentJdbcPassword=${JDBCPASSWORD}" \
	eu.juniper.platform.Rte "${DEPLOYMENTPLAN}"
