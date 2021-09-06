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
CREATE TEMP TABLE tmp_continent
(
    defaultName varchar(255),
    externalId  varchar(255),
    archived    boolean
);

/** TODO
	Adjust the path below to the one that provides the CSV file.
	Note that the user running the postgres services needs to have access rights.
**/
COPY tmp_continent
    FROM '/home/continents.csv'
DELIMITER ';'
    CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces */
UPDATE tmp_continent
SET (defaultName, externalId) = (trim(defaultName), trim(externalId));

UPDATE tmp_continent SET archived = 'FALSE' WHERE archived IS NULL;

/* 3. Validation */
DO
    $$
    DECLARE
	errordetails text;
BEGIN

/* make sure all entries have an external id */
IF (SELECT count(*) FROM tmp_continent WHERE externalId IS NULL) > 0 THEN
        errordetails = (SELECT string_agg(defaultName, ', ') FROM tmp_continent WHERE externalId IS NULL);
RAISE WARNING 'Ignoring continents without externalId: %', errordetails;
END IF;

DELETE FROM tmp_continent WHERE externalId IS NULL;

/* make sure externalIds are only used once in the imported data */
ALTER TABLE tmp_continent ADD COLUMN externalidcount integer;
UPDATE tmp_continent
SET externalidcount = cnt
    FROM (SELECT externalId, COUNT(externalId) as cnt FROM tmp_continent GROUP BY externalId) calc
    WHERE calc.externalId = tmp_continent.externalId;

IF (SELECT MAX(externalidcount) FROM tmp_continent) > 1 THEN
        errordetails = (SELECT string_agg(externalId, ', ') FROM tmp_continent WHERE externalidcount > 1);
RAISE WARNING 'Ignoring continents that are using the same externalId: %', errordetails;
END IF;

DELETE FROM tmp_continent WHERE externalidcount > 1;

END;
$$ LANGUAGE plpgsql;

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

/* 4. Update existing continents */
UPDATE continent
SET (defaultname, externalid, archived)
    = (c.defaultName, c.externalId, c.archived)
FROM tmp_continent AS c
WHERE continent.externalId IS NOT NULL AND continent.externalid = c.externalId;

/* 5. Insert new continents */
INSERT INTO continent
(id, changedate, creationdate, uuid, defaultname, externalid, archived)
    (SELECT nextval('entity_seq'),
            now(),
            now(),
            generate_base32_uuid(),
            defaultname,
            externalid,
            archived
     FROM tmp_continent
     WHERE (SELECT COUNT(*) FROM continent WHERE continent.externalid = tmp_continent.externalId) = 0);

DROP TABLE tmp_continent;
