#!/bin/sh

MYDBFILE="$(pwd)/monitoring_data_db"

PACKAGE=eu.juniper.sa.deployment.monitor.db

exec java -classpath external_libs/sched-advisor-deployment-monitor-1.0-jar-with-dependencies.jar \
	"${PACKAGE}.MonitoringDbActionsFactory" "jdbc:h2:/${MYDBFILE};COMPRESS=TRUE" "$@"
