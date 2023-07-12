#!/bin/bash

#*******************************************************************************
# SORMAS® - Surveillance Outbreak Response Management & Analysis System
# Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#*******************************************************************************

### Collects artifacts to be deployed as apps in the Payara domain ###


# 0. Initialize settings
source apply-settings.sh

# 1. Command chain
mkdir -p $COLLECT_ROOT_PATH

mkdir -p $COLLECT_APPS_PATH
cp ../../sormas-ear/target/sormas-ear.ear $COLLECT_APPS_PATH/
cp ../../sormas-ui/target/sormas-ui.war $COLLECT_APPS_PATH/
cp ../../sormas-rest/target/sormas-rest.war $COLLECT_APPS_PATH/

mkdir -p $COLLECT_ROOT_PATH/openapi
cp ../../sormas-rest/target/swagger.json $COLLECT_ROOT_PATH/openapi/sormas-rest.json
cp ../../sormas-rest/target/swagger.yaml $COLLECT_ROOT_PATH/openapi/sormas-rest.yaml

mkdir -p $COLLECT_ROOT_PATH/keycloak
cp ../../sormas-keycloak-service-provider/target/*.jar $COLLECT_ROOT_PATH/keycloak/
cp -R ../../sormas-keycloak-service-provider/target/dependency $COLLECT_ROOT_PATH/keycloak/

mkdir -p $COLLECT_ROOT_PATH/android
cp -R ../../sormas-app/app/build/outputs/apk/* $COLLECT_ROOT_PATH/android
