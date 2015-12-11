#!/bin/sh

MYDBFILE="$(pwd)/monitoring_data_db"
#TRACE="-trace"

PACKAGE=eu.juniper.sa.deployment.monitor

exec java -classpath external_libs/sched-advisor-deployment-monitor-1.0-jar-with-dependencies.jar \
	-DKeepRunning "${PACKAGE}.MonitoringDbServer" \
	-tcpAllowOthers ${TRACE} "$@" "${MYDBFILE}"
