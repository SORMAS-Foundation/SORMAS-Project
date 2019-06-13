-- If a DB update was performed, insert a new line with a comment to the table SCHEMA_VERSION.
-- Example: INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';

SET search_path = public, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE schema_version (
  version_number integer NOT NULL,
  changedate timestamp without time zone NOT NULL DEFAULT now(),
  comment character varying(255),
  CONSTRAINT schema_version_pkey PRIMARY KEY (version_number)
);
ALTER TABLE schema_version OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (1, 'Basic database configuration');

CREATE SEQUENCE auditlog_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE auditlog_seq OWNER TO sormas_user;

CREATE TABLE auditlogentry (
  id bigint NOT NULL,
  detection_ts timestamp without time zone NOT NULL,
  changetype character varying(255) NOT NULL,
  editinguser character varying(255),
  clazz character varying(255),
  uuid character varying(255),
  transaction_id character varying(255),
  transaction_ts timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT auditlogentry_pkey PRIMARY KEY (id)
);
ALTER TABLE auditlogentry OWNER TO sormas_user;

CREATE TABLE auditlogentry_attributes (
  auditlogentry_id bigint NOT NULL,
  attribute_key character varying(255) NOT NULL,
  attribute_value text,
  CONSTRAINT fk_attribute_changes_auditlogentry_id FOREIGN KEY (auditlogentry_id) REFERENCES auditlogentry (id)
);
ALTER TABLE auditlogentry_attributes OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (2, 'Initial entity model');