/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

/* 1. Load CSV data into temp table */
CREATE TEMP TABLE tmp_subcontinent
(
    defaultName varchar(255),
    externalId  varchar(255),
    archived    boolean,
    continent   varchar(255)
);

/** TODO
	Adjust the path below to the one that provides the CSV file.
	Note that the user running the postgres services needs to have access rights.
**/
COPY tmp_subcontinent
    FROM '/home/subcontinents.csv'
DELIMITER ';'
    CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces */
UPDATE tmp_subcontinent
SET (defaultName, externalId, continent) = (trim(defaultName), trim(externalId), trim(continent));

/* fill continent */
ALTER TABLE tmp_subcontinent ADD COLUMN continent_id integer;
UPDATE tmp_subcontinent SET continent_id = continent.id FROM continent WHERE continent.defaultname = continent;

UPDATE tmp_subcontinent SET archived = 'FALSE' WHERE archived IS NULL;

/* 3. Validation */
DO
    $$
    DECLARE
        errordetails text;
BEGIN

    IF ((SELECT COUNT(*) FROM continent) > 0) THEN
            /* make sure all continents were found */
            IF (SELECT count(*) FROM tmp_subcontinent WHERE tmp_subcontinent.continent_id IS NULL) > 0 THEN
                errordetails =
                        (SELECT string_agg(externalId, ', ') FROM tmp_subcontinent WHERE continent_id IS NULL);
RAISE WARNING 'Ignoring subcontinents without a continent: %', errordetails;
END IF;

DELETE FROM tmp_subcontinent WHERE tmp_subcontinent.continent_id IS NULL;
END IF;

/* make sure all entries have an external id */
IF (SELECT count(*) FROM tmp_subcontinent WHERE externalId IS NULL) > 0 THEN
            errordetails = (SELECT string_agg(defaultName, ', ') FROM tmp_subcontinent WHERE externalId IS NULL);
RAISE WARNING 'Ignoring subcontinents without externalId: %', errordetails;
END IF;

DELETE FROM tmp_subcontinent WHERE externalId IS NULL;

/* make sure externalIds are only used once in the imported data */
ALTER TABLE tmp_subcontinent ADD COLUMN externalidcount integer;
UPDATE tmp_subcontinent
SET externalidcount = cnt
    FROM (SELECT externalId, COUNT(externalId) as cnt FROM tmp_subcontinent GROUP BY externalId) calc
        WHERE calc.externalID = tmp_subcontinent.externalID;

IF (SELECT MAX(externalidcount) FROM tmp_subcontinent) > 1 THEN
            errordetails = (SELECT string_agg(externalId, ', ') FROM tmp_subcontinent WHERE externalidcount > 1);
RAISE WARNING 'Ignoring subcontinents that are using the same externalId: %', errordetails;
END IF;

DELETE FROM tmp_subcontinent WHERE externalidcount > 1;

END;
$$ LANGUAGE plpgsql;

/* 4. Update existing subcontinents */
UPDATE subcontinent
SET (defaultName, externalid, continent_id, archived)
    = (s.defaultName, s.externalId, s.continent_id, s.archived)
FROM tmp_subcontinent AS s
WHERE subcontinent.externalId IS NOT NULL AND subcontinent.externalid = s.externalId;

/* 5. Insert new subcontinents */
INSERT INTO subcontinent
(id, changedate, creationdate, uuid, defaultname, externalid, continent_id, archived)
    (SELECT nextval('entity_seq'),
            now(),
            now(),
            generate_base32_uuid(),
            defaultname,
            externalid,
            tmp_subcontinent.continent_id,
            archived
     FROM tmp_subcontinent
     WHERE (SELECT COUNT(*) FROM subcontinent WHERE subcontinent.externalid = tmp_subcontinent.externalId) = 0);

DROP TABLE tmp_subcontinent;
