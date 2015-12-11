#!/bin/sh

MYDBFILE=$(pwd)/monitoring_data_db
#TRACE="-trace"

PACKAGE=eu.juniper.sa.deployment.monitor

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

exec mvn -q --file $(dirname "${0}")/../sched-advisor-deployment-monitor/pom.xml \
	exec:java -e -Dexec.mainClass="${PACKAGE}.MonitoringDbServer" -Dexec.args="-tcpAllowOthers ${TRACE} $* ${MYDBFILE}"
