#!/bin/bash

SCRIPT="${0/.nohup/}"
STDOUT="${0/.sh/.log-stdout}"
STDERR="${0/.sh/.log-stderr}"

echo "Running ${SCRIPT} with nohup (you can exit the session and it will be still running; see ${STDOUT} and ${STDERR} log file)..."

nohup "${SCRIPT}" $* </dev/null 1>${STDOUT} 2>${STDERR} &
PID=$!

echo "... done; running in the background, can be killed by: kill ${PID}"
