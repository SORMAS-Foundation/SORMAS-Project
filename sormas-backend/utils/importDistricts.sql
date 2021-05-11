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
CREATE TEMP TABLE tmp_district
(
    name       varchar(255),
    epidcode   varchar(255),
    growthrate real,
    regionname varchar(255),
    archived   boolean,
    externalid varchar(255)
);

/** TODO
	Adjust the path below to the one that provides the CSV file.
	Note that the user running the postgres services needs to have access rights.
**/
COPY tmp_district
    FROM '/home/districts.csv'
    DELIMITER ';'
    CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces - especially important for district, district and enum values */
UPDATE tmp_district
SET (name, epidcode, externalid, regionname) = (trim(name), trim(epidcode), trim(externalid), trim(regionname));

/* fill region_id */
ALTER TABLE tmp_district ADD COLUMN region_id integer;
UPDATE tmp_district SET region_id = region.id FROM region WHERE region.name = regionname;

UPDATE tmp_district SET archived = 'FALSE' WHERE archived IS NULL;

/* 3. Validation */
DO
$$
DECLARE
	errordetails text;
BEGIN

    /* make sure all regions were found */
    IF (SELECT count(*) FROM tmp_district WHERE tmp_district.region_id IS NULL) > 0 THEN
        errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_district WHERE region_id IS NULL);
        RAISE WARNING 'Ignoring districts without region: %', errordetails;
    END IF;

    DELETE FROM tmp_district WHERE tmp_district.region_id IS NULL;

/* make sure all entries have an external id */
    IF (SELECT count(*) FROM tmp_district WHERE externalid IS NULL) > 0 THEN
        errordetails = (SELECT string_agg(name, ', ') FROM tmp_district WHERE externalid IS NULL);
        RAISE WARNING 'Ignoring districts without externalid: %', errordetails;
    END IF;

    DELETE FROM tmp_district WHERE externalid IS NULL;

/* make sure all entries have an epidCode */
    IF (SELECT count(*) FROM tmp_district WHERE epidCode IS NULL) > 0 THEN
        errordetails = (SELECT string_agg(name, ', ') FROM tmp_district WHERE epidCode IS NULL);
        RAISE WARNING 'Ignoring districts without epidCode: %', errordetails;
    END IF;

    DELETE FROM tmp_district WHERE epidCode IS NULL;

/* make sure externalids are only used once in the imported data */
    ALTER TABLE tmp_district ADD COLUMN externalidcount integer;
    UPDATE tmp_district
    SET externalidcount = cnt
    FROM (SELECT externalid, COUNT(externalid) as cnt FROM tmp_district GROUP BY externalid) calc
    WHERE calc.externalid = tmp_district.externalid;

    IF (SELECT MAX(externalidcount) FROM tmp_district) > 1 THEN
        errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_district WHERE externalidcount > 1);
        RAISE WARNING 'Ignoring districts that are using the same externalid: %', errordetails;
    END IF;

    DELETE FROM tmp_district WHERE externalidcount > 1;

END;
$$ LANGUAGE plpgsql;

/* 4. Update existing districts */
UPDATE district
SET (name, epidcode, growthrate, region_id, archived)
        = (d.name, d.epidcode, d.growthrate, d.region_id, d.archived)
FROM tmp_district AS d
WHERE district.externalid IS NOT NULL AND district.externalid = d.externalid;

/* 5. Insert new districts */
INSERT INTO district
(id, changedate, creationdate, uuid, name, epidcode, growthrate, externalid, region_id, archived)
    (SELECT nextval('entity_seq'),
            now(),
            now(),
            generate_base32_uuid(),
            name,
            epidcode,
            growthrate,
            externalid,
            tmp_district.region_id,
            archived
     FROM tmp_district
     WHERE (SELECT COUNT(*) FROM district WHERE district.externalid = tmp_district.externalid) = 0);

DROP TABLE tmp_district;
