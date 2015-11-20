#!/bin/sh

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

NAME=$(basename "${0}" .sh)
DIR=$(dirname "${0}")
DEPLOYMENTPLAN="${DIR}/${NAME/run/deployment_plan}.xml"

[[ -f "${DIR}/hosts" ]] && USEHOSTS="-machinefile hosts -npernode 1"

exec mvn --file $(dirname "${0}")/pom.xml \
	exec:exec \
	-Dexec.executable="mpirun" \
	-Dexec.args="${USEHOSTS} -np $(grep --count '<cloudnode ' ${DEPLOYMENTPLAN}) java -classpath %classpath -DMonitoringAgentEnabled=${1:-/tmp/monitor.sql} eu.juniper.platform.Rte ${DEPLOYMENTPLAN}"
