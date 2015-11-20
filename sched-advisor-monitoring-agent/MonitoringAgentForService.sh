#!/bin/sh

PACKAGE=eu.juniper.sa.monitoring.agent

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

exec mvn -q --file $(dirname "${0}")/pom.xml \
	exec:java -e -Dexec.mainClass="${PACKAGE}.$(basename ${0} .sh)" -Dexec.args="$*"

# Example:
# $ ./MonitoringAgentForService.sh
# $ ssh 172.18.2.117 -L 3000:0.0.0.0:3000 -N
# $ ./MonitoringAgentForService.sh "http://localhost:3000/executions/" TestCustom custom:test key1 123 key2 val2
