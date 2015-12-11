#!/bin/sh

DIR="$(pwd)"
MYIP="$(ip -f inet addr show dev eth0 | grep -s inet | cut -d ' ' -f 6 | cut -d / -f 1)"
MYPORT=9092
MYDBFILE="${DIR}/monitoring_data_db"
CLASSPATH="$(find -L external_libs -name '*.jar' -printf ':%p')"
DEPLOYMENTPLAN="${DIR}/rte_deployment_plan.xml"
PROCESSES="$(grep --count '<cloudnode ' ${DEPLOYMENTPLAN})"

JDBCURL="jdbc:h2:tcp://${MYIP}:${MYPORT}/${MYDBFILE};COMPRESS=TRUE"

echo "IP address of this machine is '${MYIP}'."
echo "There should be a H2 monitoring server running at port '${MYPORT}'."
echo "The H2 monitoring server should be running with parameters:"
echo " -tcpAllowOthers ${MYDBFILE}"

exec mpirun -machinefile hosts -np "${PROCESSES:-0}" --map-by node \
	java -classpath "bin${CLASSPATH}" \
	"-DMonitoringAgentEnabled=${JDBCURL}" \
	eu.juniper.platform.Rte "${DEPLOYMENTPLAN}"
