#!/usr/bin/env bash

if [ -z "$1" ]; then
  echo "Usage: $0 <job-name>"
  echo "Available jobs:"
  for a in $(kubectl get jobs | grep -v NAME | awk '{ print $1 }'); do
    echo "$0 $a"
  done
  exit 1
fi
JOB_NAME=$1

TIMEOUT_SEC=2
VERBOSE=true
function wait_until_completion() {
  while (true); do
    num_succeeded=$(kubectl get job -o jsonpath="{.status.succeeded}" ${JOB_NAME})
    num_failed=$(kubectl get job -o jsonpath="{.status.failed}" ${JOB_NAME})
    pod=$(kubectl get pods -l job-name=${JOB_NAME} -o jsonpath="{.items[0].metadata.name}")
    pod_logs=$(kubectl logs ${pod} | tail -40)
    if [ ${VERBOSE} = true ]; then echo "${pod_logs}"; fi
    if [[ ${num_succeeded} -eq 1 || ${num_failed} -eq 1 ]]; then
      if [[ ${num_succeeded} -eq 1 ]]; then
        echo "Test successful"
        exit 0
      else
        echo "Test failed"
        exit 1
      fi
    fi
    sleep ${TIMEOUT_SEC}
  done
}

wait_until_completion
