#!/usr/bin/env bash

red=$(tput setaf 1)
green=$(tput setaf 2)
reset=$(tput sgr0)

function print_line() {
  echo "${green}"
  for a in {1..40}; do echo -n "*"; done
  echo
  echo "$1"
  for a in {1..40}; do echo -n "*"; done
  echo
  echo "${reset}"
}
print_line "Creating Job"
./create-job.sh
source ./create-job.vars
print_line "Describing Job"
./describe-job.sh ${JOB_NAME}
print_line "Describing Pod running this Job"
./describe-pod.sh ${JOB_NAME}
sleep 5
print_line "Waiting for Job to finish"
./wait-job.sh ${JOB_NAME}
print_line "Deleting Job"
./delete-job.sh ${JOB_NAME}
