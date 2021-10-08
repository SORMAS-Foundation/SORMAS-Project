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
CREATE TEMP TABLE tmp_country
(
    defaultName     varchar(255),
    externalId      varchar(255),
    isoCode         varchar(3),
    unoCode         varchar(3),
    archived        boolean,
    subcontinent    varchar(255)
);

/** TODO
	Adjust the path below to the one that provides the CSV file.
	Note that the user running the postgres services needs to have access rights.
**/
COPY tmp_country
    FROM '/home/countries.csv'
DELIMITER ';'
    CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces */
UPDATE tmp_country
SET (defaultName, externalId, isoCode, unoCode, subcontinent) = (trim(defaultName), trim(externalId), trim(isoCode), trim(unoCode), trim(subcontinent));

/* fill subcontinent */
ALTER TABLE tmp_country ADD COLUMN subcontinent_id integer;
UPDATE tmp_country SET subcontinent_id = subcontinent.id FROM subcontinent WHERE subcontinent.defaultname = subcontinent;

UPDATE tmp_country SET archived = 'FALSE' WHERE archived IS NULL;

/* 3. Validation */
DO
    $$
    DECLARE
        errordetails text;
BEGIN

    IF ((SELECT COUNT(*) FROM continent) > 0) THEN
            /* make sure all subcontinents were found */
            IF (SELECT count(*) FROM tmp_country WHERE tmp_country.subcontinent_id IS NULL) > 0 THEN
                errordetails =
                        (SELECT string_agg(externalId, ', ') FROM tmp_country WHERE subcontinent_id IS NULL);
RAISE WARNING 'Ignoring countries without a subcontinent: %', errordetails;
END IF;

DELETE FROM tmp_country WHERE tmp_country.subcontinent_id IS NULL;
END IF;

/* make sure all entries have an external id */
IF (SELECT count(*) FROM tmp_country WHERE externalId IS NULL) > 0 THEN
            errordetails = (SELECT string_agg(defaultName, ', ') FROM tmp_country WHERE externalId IS NULL);
RAISE WARNING 'Ignoring countries without externalId: %', errordetails;
END IF;

DELETE FROM tmp_country WHERE externalId IS NULL;

/* make sure externalIds are only used once in the imported data */
ALTER TABLE tmp_country ADD COLUMN externalidcount integer;
UPDATE tmp_country
SET externalidcount = cnt
    FROM (SELECT externalId, COUNT(externalId) as cnt FROM tmp_country GROUP BY externalId) calc
        WHERE calc.externalID = tmp_country.externalID;

IF (SELECT MAX(externalidcount) FROM tmp_country) > 1 THEN
            errordetails = (SELECT string_agg(externalId, ', ') FROM tmp_country WHERE externalidcount > 1);
RAISE WARNING 'Ignoring countries that are using the same externalId: %', errordetails;
END IF;

DELETE FROM tmp_country WHERE externalidcount > 1;

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

/* 4. Update existing countries */
UPDATE country
SET (defaultName, externalid, isocode, unocode, subcontinent_id, archived)
    = (c.defaultName, c.externalId, c.isoCode, c.unoCode, c.subcontinent_id, c.archived)
FROM tmp_country AS c
WHERE country.isocode IS NOT NULL AND country.isocode = c.isoCode;

/* 5. Insert new countries */
INSERT INTO country
(id, changedate, creationdate, uuid, defaultname, externalid, isocode, unocode, subcontinent_id, archived)
    (SELECT nextval('entity_seq'),
            now(),
            now(),
            generate_base32_uuid(),
            defaultname,
            externalid,
            isoCode,
            unoCode,
            tmp_country.subcontinent_id,
            archived
     FROM tmp_country
     WHERE (SELECT COUNT(*) FROM country WHERE country.isocode = tmp_country.isoCode) = 0);

DROP TABLE tmp_country;
