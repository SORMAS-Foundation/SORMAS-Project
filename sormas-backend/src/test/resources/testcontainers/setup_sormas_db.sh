#!/bin/bash
set -e

# Set up the database
echo "Starting database setup..."

AUDIT_DB=sormas_audit

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -d ${POSTGRES_DB} <<EOSQL
    CREATE DATABASE ${AUDIT_DB} WITH OWNER = '${POSTGRES_USER}' ENCODING = 'UTF8';
    \c ${POSTGRES_DB}
    CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;
    ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO ${POSTGRES_USER};
    CREATE EXTENSION temporal_tables;
    CREATE EXTENSION pg_trgm;
    CREATE EXTENSION pgcrypto;
    CREATE EXTENSION pg_stat_statements;
    CREATE EXTENSION IF NOT EXISTS unaccent;
    GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO ${POSTGRES_USER};
    \c ${AUDIT_DB}
    CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
    COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';
    GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO ${POSTGRES_USER};
    ALTER TABLE IF EXISTS schema_version OWNER TO ${POSTGRES_USER};
EOSQL

echo "Initializing the database..."

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -d ${POSTGRES_DB} -f /tmp/sormas_schema.sql
