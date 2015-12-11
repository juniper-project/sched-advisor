#!/bin/sh

MYDBFILE=$(pwd)/monitoring_data_db

PACKAGE=eu.juniper.sa.deployment.monitor.db

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

exec mvn -q --file $(dirname "${0}")/../sched-advisor-deployment-monitor/pom.xml \
	exec:java -e -Dexec.mainClass="${PACKAGE}.MonitoringDbActionsFactory" -Dexec.args="jdbc:h2:/${MYDBFILE};COMPRESS=TRUE $*"
