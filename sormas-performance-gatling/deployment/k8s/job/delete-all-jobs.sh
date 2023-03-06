#!/usr/bin/env bash
if [ "$1" = "confirm" ]; then
  echo "Deleting jobs for real"
  for a in $(kubectl get jobs | grep -v NAME | awk '{ print $1 }'); do
    kubectl delete job $a
  done
else
  echo "These jobs will be deleted if you execute \"${0} confirm\""
  for a in $(kubectl get jobs | grep -v NAME | awk '{ print $1 }'); do
    echo "$a"
  done
fi
