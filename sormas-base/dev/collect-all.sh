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

### Collects all serverlibs, bundles and so on for the setup-script ###


# 0. Initialize settings
source apply-settings.sh

# 1. Command chain

echo "Collecting config und scripts"
source collect-setup.sh

echo "Collecting serverlibs"
source collect-serverlibs.sh

echo "Collecting SORMAS artifacts"
source collect-artifacts.sh
