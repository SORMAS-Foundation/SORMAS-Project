#!/bin/bash
set -e

# Set up the database
echo "Starting database setup..."

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -d ${POSTGRES_DB} <<EOSQL
    \c ${POSTGRES_DB}
    CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;
    ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO ${POSTGRES_USER};
    CREATE EXTENSION pg_trgm;
    CREATE EXTENSION pgcrypto;
    CREATE EXTENSION pg_stat_statements;
    CREATE EXTENSION IF NOT EXISTS unaccent;
    GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO ${POSTGRES_USER};
EOSQL

echo "Creating versioning function..."

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -d ${POSTGRES_DB} -f /tmp/versioning_function.sql

echo "Initializing the database..."

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -d ${POSTGRES_DB} -f /tmp/sormas_schema.sql
