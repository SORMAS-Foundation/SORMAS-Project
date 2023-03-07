FROM postgres:10.18-alpine

COPY setup_sormas_db.sh /docker-entrypoint-initdb.d/
COPY versioning_function.sql /tmp/versioning_function.sql
COPY sormas_schema.sql /tmp/sormas_schema.sql
