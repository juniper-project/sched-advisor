#!/bin/sh

PACKAGE=eu.juniper.sa.deployment.monitor

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

exec mvn -q --file $(dirname "${0}")/pom.xml \
	exec:java -e -Dexec.mainClass="${PACKAGE}.$(basename ${0} .sh)" -Dexec.args="$*"

# Example:
# $ ssh 172.18.2.117 -L 3000:0.0.0.0:3000 -N
# $ ./MonitoringService.sh "http://localhost:3000/executions/" aggr 0 1524785785
# $ ./MonitoringService.sh "http://localhost:3000/executions/" aggr 0 1524785785 zELdPPf4TNKQa9BEQfk41g
# $ ./MonitoringService.sh "http://localhost:3000/executions/" aggr 0 1524785785 zELdPPf4TNKQa9BEQfk41g mem_used
# $ ./MonitoringService.sh "http://localhost:3000/executions/" vals TestIDIO iostat:xvda:tps
# $ ./MonitoringService.sh "http://localhost:3000/executions/" vals TestIDIO iostat:xvda:tps Timestamp 1427124450
