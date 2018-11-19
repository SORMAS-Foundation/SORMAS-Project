#*******************************************************************************
# SORMAS® - Surveillance Outbreak Response Management & Analysis System
# Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
# ===========================================================
# ===== extracts database update script and executes it =====
# ===========================================================

DB_NAME='sormas_db'

DB_VERSION=$(sudo -i -u postgres psql -d $DB_NAME -c 'SELECT MAX (version_number) FROM schema_version;' | sed -n 's/[^0-9]//g; 3,3p')
#echo "Client: DB: $DB_NAME   Schema Version: $CM_DB_VERSION"

DB_SCRIPT_NAME=sormas_schema_update_from_${DB_VERSION}.sql
echo ${DB_SCRIPT_NAME}
sed -n "/^INSERT INTO schema_version.*($DB_VERSION,/,\$p" sormas_schema.sql | sed "1,1d ; 0,/^/s//BEGIN\;/ ; \$a COMMIT;" > $DB_SCRIPT_NAME

read -p "Update database $DB_NAME with $DB_SCRIPT_NAME? " -t 30 -ei 'J' DO_UPDATE

if [ ${DO_UPDATE} = 'J' ]; then
		sudo -i -u postgres psql -d $DB_NAME < ${DB_SCRIPT_NAME}
		echo done: "sudo -i -u postgres psql -d $DB_NAME < ${DB_SCRIPT_NAME}"
else
		echo bypassed: "sudo -i -u postgres psql -d $DB_NAME < ${DB_SCRIPT_NAME}"
fi
