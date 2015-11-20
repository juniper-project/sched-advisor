#!/bin/sh

DIR=`realpath $(dirname "${0}")`
ADVISOR=${DIR}/../../../sched-advisor-tool/Advisor.sh

for DATAFILE in ${DIR}/data.lo/*.merged.gz; do
	DEPLFILE=${DIR}/deployment_plan_localhost.xml
	cd $(dirname "${ADVISOR}")
	echo "## $(basename ${DEPLFILE}) and $(basename ${DATAFILE})"
	${ADVISOR} ${DEPLFILE} ${DATAFILE} \
		"advice_$(basename ${DEPLFILE} .xml)_$(basename ${DATAFILE} .merged.gz).xml" | sed 's/^/\t/'
	echo
done

for DATAFILE in ${DIR}/data.net/*.merged.gz; do
	DEPLFILE=${DIR}/deployment_plan_network.xml
	cd $(dirname "${ADVISOR}")
	echo "## $(basename ${DEPLFILE}) and $(basename ${DATAFILE})"
	${ADVISOR} ${DEPLFILE} ${DATAFILE} \
		"advice_$(basename ${DEPLFILE} .xml)_$(basename ${DATAFILE} .merged.gz).xml" | sed 's/^/\t/'
	echo
done
