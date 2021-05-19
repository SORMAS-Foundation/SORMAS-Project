/* 1. Load CSV data into temp table */
CREATE TEMP TABLE tmp_facility (name text, regionname text, districtname text, communityname text, city text, postalcode text, street text, housenumber text, 
						   	additionalinformation text, areatype varchar(255), contactpersonfirstname text, contactpersonlastname text, contactpersonphone text, contactpersonemail text, 
							latitude double precision, longitude double precision, type varchar(255), publicownership boolean, archived boolean, externalid text);

/** TODO
	Adjust the path below to the one that provides the CSV file.
	Note that the user running the postgres services needs to have access rights.
**/
COPY tmp_facility
FROM 'C:\Users\Public\Documents\facilities.csv'
DELIMITER ';'
CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces - especially important for region, district and enum values */
UPDATE tmp_facility 
	SET (name, regionname, districtname, communityname, city, postalcode, street, housenumber, 
		 additionalinformation, areatype, contactpersonfirstname, contactpersonlastname, contactpersonphone, contactpersonemail, type, externalid)
		 = (trim(name), trim(regionname), trim(districtname), trim(communityname), trim(city), trim(postalcode), trim(street), trim(housenumber), 
			trim(additionalinformation), trim(areatype), trim(contactpersonfirstname), trim(contactpersonlastname), trim(contactpersonphone), trim(contactpersonemail), trim(type), trim(externalid));

/* fill region_id and district_id and community_id */
ALTER TABLE tmp_facility ADD COLUMN region_id integer;
ALTER TABLE tmp_facility ADD COLUMN district_id integer;
ALTER TABLE tmp_facility ADD COLUMN community_id integer;
UPDATE tmp_facility SET region_id = region.id FROM region WHERE region.name = regionname;
UPDATE tmp_facility SET district_id = district.id FROM district WHERE district.name = districtname AND district.region_id = tmp_facility.region_id;
UPDATE tmp_facility SET community_id = community.id FROM community WHERE community.name = communityname AND community.district_id = tmp_facility.district_id;

/* fill publicownship default values */
UPDATE tmp_facility SET publicownership = 'FALSE' WHERE publicownership IS NULL;
UPDATE tmp_facility SET archived = 'FALSE' WHERE archived IS NULL;

/* 3. Validation */
DO
$$
DECLARE
	errordetails text;
BEGIN

/* make sure all regions and districts were found */
IF (SELECT count(*) FROM tmp_facility WHERE region_id IS NULL OR district_id IS NULL) > 0 THEN
	errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_facility WHERE region_id IS NULL OR district_id IS NULL);
   RAISE WARNING 'Ignoring facilities without region or district: %', errordetails;
END IF;

DELETE FROM tmp_facility WHERE region_id IS NULL OR district_id IS NULL;

/* make sure all entries have an external id */
IF (SELECT count(*) FROM tmp_facility WHERE externalid IS NULL) > 0 THEN
	errordetails = (SELECT string_agg(name, ', ') FROM tmp_facility WHERE externalid IS NULL);
   RAISE WARNING 'Ignoring facilities without externalid: %', errordetails;
END IF;

DELETE FROM tmp_facility WHERE externalid IS NULL;

/* make sure externalids are only used once in the imported data */
ALTER TABLE tmp_facility ADD COLUMN externalidcount integer;
UPDATE tmp_facility 
	SET externalidcount = cnt
	FROM (SELECT externalid, COUNT(externalid) as cnt FROM tmp_facility GROUP BY externalid) calc 
	WHERE calc.externalid = tmp_facility.externalid;

IF (SELECT MAX(externalidcount) FROM tmp_facility) > 1 THEN
	errordetails = (SELECT string_agg(externalid, ', ') FROM tmp_facility WHERE externalidcount > 1);
   RAISE WARNING 'Ignoring facilities that are using the same externalid: %', errordetails;
END IF;

DELETE FROM tmp_facility WHERE externalidcount > 1;

END;
$$ LANGUAGE plpgsql;


/* 4. Update existing facilities */
UPDATE facility 
	SET (name, city, postalcode, street, housenumber, additionalinformation, areatype,
		 contactpersonfirstname, contactpersonlastname, contactpersonphone, contactpersonemail, 
		 latitude, longitude, type, publicownership, archived
		 )
	= (f.name, f.city, f.postalcode, f.street, f.housenumber, f.additionalinformation, f.areatype, 
		f.contactpersonfirstname, f.contactpersonlastname, f.contactpersonphone, f.contactpersonemail, 
		f.latitude, f.longitude, f.type, f.publicownership, f.archived)
   FROM tmp_facility AS f 
   WHERE facility.externalid IS NOT NULL AND facility.externalid = f.externalid;

/* 5. Insert new facilities */
INSERT INTO facility
	(id, changedate, creationdate, uuid, name, region_id, district_id, community_id, 
		city, postalcode, street, housenumber, additionalinformation, areatype,
		contactpersonfirstname, contactpersonlastname, contactpersonphone, contactpersonemail, 
		latitude, longitude, type, publicownership, archived, externalid)
	(SELECT nextval('entity_seq'), now(), now(), generate_base32_uuid(), f.name, f.region_id, f.district_id, f.community_id,		
		f.city, f.postalcode, f.street, f.housenumber, f.additionalinformation, f.areatype,
		f.contactpersonfirstname, f.contactpersonlastname, f.contactpersonphone, f.contactpersonemail, 
		f.latitude, f.longitude, f.type, f.publicownership, f.archived, f.externalid
		FROM tmp_facility AS f
		WHERE (SELECT COUNT(*) FROM facility WHERE facility.externalid = f.externalid) = 0);
	
DROP TABLE tmp_facility;
