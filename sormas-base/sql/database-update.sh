# ===========================================================
# ===== extracts database update script and executes it =====
# ===========================================================

DB_NAME='sormas_db'
DB_USER='sormas_user'

DB_VERSION=$(psql -U $DB_USER -h localhost -W -d $DB_NAME -c 'SELECT MAX (version_number) FROM schema_version;' | sed -n 's/[^0-9]//g; 3,3p')
#echo "Client: DB: $DB_NAME   Schema Version: $CM_DB_VERSION"

DB_SCRIPT_NAME=sormas_schema_update_from_${DB_VERSION}.sql
echo ${DB_SCRIPT_NAME}
sed -n "/^INSERT INTO schema_version.*($DB_VERSION,/,\$p" sormas_schema.sql | sed "1,1d ; 0,/^/s//BEGIN\;/ ; \$a COMMIT;" > $DB_SCRIPT_NAME

read -p "Update database $DB_NAME with $DB_SCRIPT_NAME? " -t 30 -ei 'J' DO_UPDATE

if [ ${DO_UPDATE} = 'J' ]; then
		psql -U $DB_USER -h localhost -W -d $DB_NAME < ${DB_SCRIPT_NAME}
		echo done: "psql -U $DB_USER -h localhost -W -d $DB_NAME < ${DB_SCRIPT_NAME}"
else
		echo bypassed: "psql -U $DB_USER -h localhost -W -d $DB_NAME < ${DB_SCRIPT_NAME}"
fi
