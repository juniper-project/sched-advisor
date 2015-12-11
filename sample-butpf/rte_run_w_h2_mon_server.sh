#!/bin/sh

DIR=$(pwd)
MYIP=$(ip -f inet addr show dev enp3s0 | grep -s inet | cut -d ' ' -f 6 | cut -d / -f 1)
MYPORT=9092
MYDBFILE=${DIR}/monitoring_data_db
DEPLOYMENTPLAN=${DIR}/rte_deployment_plan.xml
PROCESSES=$(grep --count '<cloudnode ' ${DEPLOYMENTPLAN})

echo "IP address of this machine is '${MYIP}'."
echo "There should be a H2 monitoring server running at port '${MYPORT}'."
echo "The H2 monitoring server should be running with parameters:"
echo " -tcpAllowOthers ${MYDBFILE}"

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

exec mvn --file $(dirname "${0}")/pom.xml \
	exec:exec \
	-Dexec.executable="mpirun" \
	-Dexec.args="-machinefile hosts -np ${PROCESSES:-0} --map-by node \
java -cp %classpath \
-DMonitoringAgentEnabled=jdbc:h2:tcp://${MYIP}:${MYPORT}/${MYDBFILE};COMPRESS=TRUE \
eu.juniper.platform.Rte ${DEPLOYMENTPLAN}"
