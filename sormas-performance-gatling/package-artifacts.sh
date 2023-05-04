#!/usr/bin/env bash

## This utility packages simulations and resources into a tar.gz that can be fed into the gatling-server.
## See https://github.com/jecklgamis/gatling-server

CURRENT_DIR=$(pwd)

NAME=gatling-java-example
USER_FILES_DIR=${NAME}-user-files
TAR_GZ_FILE=${USER_FILES_DIR}.tar.gz

rm -rf ${USER_FILES_DIR}
mkdir -p ${USER_FILES_DIR}/simulations
mkdir -p ${USER_FILES_DIR}/bodies
mkdir -p ${USER_FILES_DIR}/resources
mkdir -p ${USER_FILES_DIR}/binaries
mkdir -p ${USER_FILES_DIR}/lib

cp -rf src/main/java/* ${USER_FILES_DIR}/simulations/
[[ $(ls -A "${USER_FILES_DIR}/bodies/") ]] && cp -rf src/main/resources/bodies/* ${USER_FILES_DIR}/bodies/
[[ $(ls -A "${USER_FILES_DIR}/resources/") ]] && cp -rf src/main/resources/data/* ${USER_FILES_DIR}/resources/
[[ $(ls -A "target/dependency") ]] && cp -rf target/dependency/* ${USER_FILES_DIR}/lib/

rm -f ${TAR_GZ_FILE}
tar cvzf ${TAR_GZ_FILE} ${USER_FILES_DIR}

echo "Created ${CURRENT_DIR}/${TAR_GZ_FILE}"
