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

### Apply settings for script, enable configuration via dev.env by developer ###

# Don't read settings multiple times when scripts are combined
if [[ "$DEV_SETTINGS_APPLIED" != "true" ]]; then

	# 0. Default settings
	MVN_BIN=mvn
	BUILD_SKIP_TESTS=true
	COLLECT_ROOT_PATH=../../deploy
	COLLECT_APPS_PATH=$COLLECT_ROOT_PATH/apps

	# 1. Read user settings
	source ../dev.env

	# 2. Apply other derived settings
	DEPLOY_SERVERLIBS_PATH=$GLASSFISH_DOMAIN_ROOT/lib

	# 3. Print used toolchain
	echo "Java: $JAVA_HOME" 
	echo "Maven: $MVN_BIN"

	# 4. Avoid repeated read of settings
	DEV_SETTINGS_APPLIED=true
fi
