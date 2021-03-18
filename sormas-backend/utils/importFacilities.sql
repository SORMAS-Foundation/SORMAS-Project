
/* 1. Load CSV data into temp table */
CREATE TABLE tmp_facility (name text, regionname text, districtname text, communityname text, city text, postalcode text, street text, housenumber text, phonenumber text, emailaddress text, 
						   	contactFirstName text, contactLastName text, contactPhoneNumber text, contactEmailAddress text, additionalinformation text, 
							areatype varchar(255), latitude double precision, longitude double precision, type varchar(255), publicownership boolean, archived boolean, externalid text);
COPY tmp_facility
FROM 'C:\Users\Public\Documents\facilities.csv'
DELIMITER ';'
CSV HEADER;

/* 2. Clean up and map data */
/* trim whitespaces - especially important for region, district and enum values */
UPDATE tmp_facility 
	SET (name, regionname, districtname, communityname, city, postalcode, street, housenumber, phonenumber, emailaddress, 
		 contactFirstName, contactLastName, contactPhoneNumber, contactEmailAddress,  additionalinformation, areatype, type, externalid)
		 = (trim(name), trim(regionname), trim(districtname), trim(communityname), trim(city), trim(postalcode), trim(street), trim(housenumber), trim(phonenumber), trim(emailaddress), 
			trim(contactFirstName), trim(contactLastName), trim(contactPhoneNumber), trim(contactEmailAddress), trim(additionalinformation), trim(areatype), trim(type), trim(externalid));

/* fill region_id and district_id */
ALTER TABLE tmp_facility ADD COLUMN region_id integer;
ALTER TABLE tmp_facility ADD COLUMN district_id integer;
UPDATE tmp_facility SET region_id = region.id FROM region WHERE region.name = regionname;
UPDATE tmp_facility SET district_id = district.id FROM district WHERE district.name = districtname AND district.region_id = tmp_facility.region_id;

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

/* make sure all entries have an external id */
IF (SELECT count(*) FROM tmp_facility WHERE externalid IS NULL) > 0 THEN
	errordetails = (SELECT string_agg(name, ', ') FROM tmp_facility WHERE externalid IS NULL);
   RAISE WARNING 'Ignoring facilities without externalid: %', errordetails;
END IF;

END;
$$ LANGUAGE plpgsql;

/* 4. Update existing facilities */
UPDATE facility 
	SET (name, publicownership, type, city, latitude, longitude, archived,
		 street, housenumber, additionalinformation, postalcode, areatype)
	= (f.name, f.publicownership, f.type, f.city, f.latitude, f.longitude, f.archived, 
   		f.street, f.housenumber, f.additionalinformation, f.postalcode, f.areatype)
   FROM tmp_facility AS f 
   WHERE facility.externalid IS NOT NULL AND facility.externalid = f.externalid;

/* 5. Insert new facilities */
INSERT INTO facility
	(id, changedate, creationdate, uuid, region_id, district_id, externalid,
		name, publicownership, type, city, latitude, longitude, archived,
		street, housenumber, additionalinformation, postalcode, areatype)
	(SELECT nextval('entity_seq'), now(), now(), generate_base32_uuid(), region_id, district_id, externalid,
		name, publicownership, type, city, latitude, longitude, archived,
		street, housenumber, additionalinformation, postalcode, areatype
		FROM tmp_facility WHERE (SELECT COUNT(*) FROM facility WHERE facility.externalid = tmp_facility.externalid) = 0);
	
DROP TABLE tmp_facility;