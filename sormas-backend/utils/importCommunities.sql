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
CREATE TEMP TABLE tmp_community
(
    name       varchar(255),
    growthrate real,
    regionname varchar(255),
    districtname varchar(255),
    archived   boolean,
    externalid varchar(255)
);

/** TODO
	Adjust the path below to the one that provides the CSV file.
	Note that the user running the postgres services needs to have access rights.
**/
COPY tmp_community
    FROM '/home/communities.csv'
    DELIMITER ';'
    CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces - especially important for district, district and enum values */
UPDATE tmp_community
SET (name, externalid, regionname, districtname) = (trim(name), trim(externalid), trim(regionname), trim(districtname));

/* fill district_id */
ALTER TABLE tmp_community ADD COLUMN district_id integer;
UPDATE tmp_community SET district_id = d.id FROM district d left join region r on d.region_id = r.id WHERE d.name = districtname AND r.name = regionname;

UPDATE tmp_community SET archived = 'FALSE' WHERE archived IS NULL;

/* 3. Validation */
DO
$$
    DECLARE
        errordetails text;
    BEGIN

        /* make sure all districts were found */
        IF (SELECT count(*) FROM tmp_community WHERE district_id IS NULL) > 0 THEN
            errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_community WHERE district_id IS NULL);
            RAISE WARNING 'Ignoring communities without district: %', errordetails;
        END IF;

        DELETE FROM tmp_community WHERE district_id IS NULL;

/* make sure all entries have an external id */
        IF (SELECT count(*) FROM tmp_community WHERE externalid IS NULL) > 0 THEN
            errordetails = (SELECT string_agg(name, ', ') FROM tmp_community WHERE externalid IS NULL);
            RAISE WARNING 'Ignoring communities without externalid: %', errordetails;
        END IF;

        DELETE FROM tmp_community WHERE externalid IS NULL;

/* make sure externalids are only used once in the imported data */
        ALTER TABLE tmp_community ADD COLUMN externalidcount integer;
        UPDATE tmp_community
        SET externalidcount = cnt
        FROM (SELECT externalid, COUNT(externalid) as cnt FROM tmp_community GROUP BY externalid) calc
        WHERE calc.externalid = tmp_community.externalid;

        IF (SELECT MAX(externalidcount) FROM tmp_community) > 1 THEN
            errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_community WHERE externalidcount > 1);
            RAISE WARNING 'Ignoring communities that are using the same externalid: %', errordetails;
        END IF;

        DELETE FROM tmp_community WHERE externalidcount > 1;

    END;
$$ LANGUAGE plpgsql;


/* 4. Update existing communities */
UPDATE community
SET (name, growthrate, district_id, archived)
        = (c.name, c.growthrate, c.district_id, c.archived)
FROM tmp_community AS c
WHERE community.externalid IS NOT NULL AND community.externalid = c.externalid;

/* 5. Insert new communities */
INSERT INTO community
(id, changedate, creationdate, uuid, name, growthrate, district_id, externalid, archived)
    (SELECT nextval('entity_seq'), now(), now(), generate_base32_uuid(), name, growthrate, district_id, externalid, archived
     FROM tmp_community WHERE (SELECT COUNT(*) FROM community WHERE community.externalid = tmp_community.externalid) = 0);

DROP TABLE tmp_community;