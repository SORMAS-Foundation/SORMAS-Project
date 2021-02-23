/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
  The following script provides an example of how to import a list of users into the database directly with a valid password, role and region.

  In this script, users are imported with the following information:
  - username
  - password
  - firstname
  - lastname
  - region
  - roles: CASE_SUPERVISOR, CONTACT_SUPERVISOR, SURVEILLANCE_SUPERVISOR

  Certain roles require multiple other fields in order to work properly (e.g. district or facility).


  In order to run the script the following extensions have to be enabled by the root user:
  CREATE EXTENSION pgcrypto;
  CREATE EXTENSION "uuid-ossp";

  Also make sure to replace the ROLLBACK statement at the end after testing that your script is correct and you are ready to run it.
*/

BEGIN;

CREATE OR REPLACE FUNCTION pg_temp.insert_user(username varchar, pass varchar, firstname varchar, lastname varchar,
                                               region_epid_code varchar)
    RETURNS integer
    LANGUAGE plpgsql AS
$$
DECLARE
    inserted_id integer;
    salt        varchar;
    region_id   bigint;
BEGIN

    inserted_id := (SELECT max(id) FROM users) + 1;
    salt := gen_salt('md5');
    region_id := (SELECT id FROM region WHERE epidcode = $5);
-- add here any other infrastructure relation query

    INSERT INTO users (id, uuid, active, changedate, creationdate, firstname, lastname, username, password, seed, region_id)
    VALUES (inserted_id, uuid_generate_v4(), true, now(), now(), $3, $4, $1, encode(digest(concat($2, salt), 'sha256'), 'hex'), salt, region_id);

    INSERT INTO users_userroles(user_id, userrole)
    VALUES (inserted_id, 'CASE_SUPERVISOR'),
           (inserted_id, 'CONTACT_SUPERVISOR'),
           (inserted_id, 'SURVEILLANCE_SUPERVISOR');

    RAISE NOTICE 'User % inserted successfully with id %', $1, inserted_id;
    RETURN inserted_id;
END
$$;

WITH src AS (SELECT *
             FROM (VALUES ('username1', 'password1', 'Firstname1', 'Lastname1', 'DEF-REG'),
                          ('username2', 'password2', 'Firstname2', 'Lastname2', 'DEF-REG')
                  ) AS v (username, password, firstname, lastname, region_epid_code))
SELECT pg_temp.insert_user(src.username, src.password, src.firstname, src.lastname, src.region_epid_code)
FROM src;

DROP FUNCTION pg_temp.insert_user(varchar, varchar, varchar, varchar, varchar);

ROLLBACK;
-- COMMIT;