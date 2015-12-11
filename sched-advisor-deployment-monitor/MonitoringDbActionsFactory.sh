#!/bin/bash

PACKAGE=eu.juniper.sa.deployment.monitor.db

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

for I in $*; do
	if [[ "${I:0:2}" == "-D" ]]; then
		JAVA_ARGS+="${I} "
	else
		CLASS_ARGS+="${I} "
	fi
done

exec mvn -q --file $(dirname "${0}")/pom.xml \
	exec:exec -e \
	-Dexec.executable="java" \
	-Dexec.args="-classpath %classpath ${JAVA_ARGS} ${PACKAGE}.$(basename ${0} .sh) ${CLASS_ARGS}"

# Example:
# $ ./MonitoringDbActionsFactory.sh -Djdbc.user=sa -Djdbc.password=secret jdbc:h2:mem: create export /tmp/my-db.sql
# $ ./MonitoringDbActionsFactory.sh -Djdbc.user=myuser -Djdbc.password=mypassword "jdbc:h2:./my-db;COMPRESS=TRUE" create export /tmp/my-db.sql
# $ ./MonitoringDbActionsFactory.sh -Djdbc.user=myuser -Djdbc.password=mypassword jdbc:postgresql://myhost:1234/mydb create import /tmp/my-monitoring-data.sql
