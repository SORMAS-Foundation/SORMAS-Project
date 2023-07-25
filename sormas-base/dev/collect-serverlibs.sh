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

### Collect serverlibs to be later copied into the Payara domain ###


# 0. Initialize settings
source apply-settings.sh
# Decoupled from COLLECT_ROOT_PATH because Maven runs in another directory
COLLECT_SERVERLIBS_PATH=../deploy/serverlibs

# 1. Command chain
rm ../$COLLECT_SERVERLIBS_PATH/*.jar
"$MVN_BIN" -f ../../sormas-serverlibs/pom.xml -DincludeScope=compile -DoutputDirectory=$COLLECT_SERVERLIBS_PATH $MVN_OPTIONS dependency:copy-dependencies
