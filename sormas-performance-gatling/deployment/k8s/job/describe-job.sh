#!/usr/bin/env bash

if [ -z "$1" ]; then
  echo "Usage : $0 <job-name>"
  echo "Available jobs:"
  for a in $(kubectl get jobs | grep -v NAME | awk '{ print $1 }'); do
    echo "$0 $a"
  done
  exit 1
fi
JOB_NAME=$1
kubectl describe job ${JOB_NAME}
