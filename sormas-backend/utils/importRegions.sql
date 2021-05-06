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
CREATE TEMP TABLE tmp_region
(
    name        varchar(255),
    epidCode    varchar(255),
    growthRate  real,
    archived    boolean,
    externalID  varchar(255),
    area    varchar(255),
    country varchar(255)
);

/** TODO
	Adjust the path below to the one that provides the CSV file.
	Note that the user running the postgres services needs to have access rights.
**/
COPY tmp_region
    FROM '/Users/barnabartha/Sormas/sprints/sprint 102/regions.csv'
    DELIMITER ';'
    CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces - especially important for region, district and enum values */
UPDATE tmp_region
SET (name, epidcode, externalid, area, country) = (trim(name), trim(epidcode), trim(externalid), trim(area), trim(country));

/* fill area_id and country_id */
ALTER TABLE tmp_region ADD COLUMN area_id integer;
ALTER TABLE tmp_region ADD COLUMN country_id integer;
UPDATE tmp_region SET area_id = areas.id FROM areas WHERE areas.name = area;
UPDATE tmp_region SET country_id = country.id FROM country WHERE country.defaultName = country;

UPDATE tmp_region SET archived = 'FALSE' WHERE archived IS NULL;

/* 3. Validation */
DO
$$
DECLARE
	errordetails text;
BEGIN

    /* make sure all areas and countries were found (considering that not all systems will import both) */
    IF (SELECT count(*) FROM tmp_region WHERE tmp_region.area_id IS NULL AND tmp_region.country_id IS NULL) > 0 THEN
        errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_region WHERE area_id IS NULL AND country_id IS NULL);
        RAISE WARNING 'Ignoring regions without area and country: %', errordetails;
    END IF;

    DELETE FROM tmp_region WHERE tmp_region.area_id IS NULL AND tmp_region.country_id IS NULL;

/* make sure all entries have an external id */
    IF (SELECT count(*) FROM tmp_region WHERE externalid IS NULL) > 0 THEN
        errordetails = (SELECT string_agg(name, ', ') FROM tmp_region WHERE externalid IS NULL);
        RAISE WARNING 'Ignoring regions without externalid: %', errordetails;
    END IF;

    DELETE FROM tmp_region WHERE externalid IS NULL;

/* make sure externalids are only used once in the imported data */
    ALTER TABLE tmp_region ADD COLUMN externalidcount integer;
    UPDATE tmp_region
    SET externalidcount = cnt
    FROM (SELECT externalid, COUNT(externalid) as cnt FROM tmp_region GROUP BY externalid) calc
    WHERE calc.externalid = tmp_region.externalid;

    IF (SELECT MAX(externalidcount) FROM tmp_region) > 1 THEN
        errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_region WHERE externalidcount > 1);
        RAISE WARNING 'Ignoring regions that are using the same externalid: %', errordetails;
    END IF;

    DELETE FROM tmp_region WHERE externalidcount > 1;

END;
$$ LANGUAGE plpgsql;

/* 4. Update existing regions */
UPDATE region
SET (name, epidcode, growthrate, area_id, country_id, archived)
        = (r.name, r.epidcode, r.growthrate, r.area_id, r.country_id, r.archived)
FROM tmp_region AS r
WHERE region.externalid IS NOT NULL AND region.externalid = r.externalid;

/* 5. Insert new regions */
INSERT INTO region
(id, changedate, creationdate, uuid, name, epidcode, growthrate, externalid, area_id, country_id, archived)
    (SELECT nextval('entity_seq'),
            now(),
            now(),
            generate_base32_uuid(),
            name,
            epidcode,
            growthrate,
            externalid,
            tmp_region.area_id,
            tmp_region.country_id,
            archived
     FROM tmp_region
     WHERE (SELECT COUNT(*) FROM region WHERE region.externalid = tmp_region.externalid) = 0);

DROP TABLE tmp_region;
