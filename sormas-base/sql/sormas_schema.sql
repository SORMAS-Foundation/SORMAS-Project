-- If a DB update was performed, insert a new line with a comment to the table SCHEMA_VERSION.
-- Example: INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');

-- #1
CREATE TABLE schema_version
(
  version_number integer NOT NULL,
  changedate timestamp without time zone NOT NULL DEFAULT now(),
  comment character varying(255),
  CONSTRAINT schema_version_pkey PRIMARY KEY (version_number )
)
WITH (
  OIDS=FALSE
);


SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 483 (class 2612 OID 11574)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 142 (class 1259 OID 341910)
-- Name: cases; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE cases (
    id bigint NOT NULL,
    casestatus character varying(255),
    changedate timestamp without time zone NOT NULL,
    confirmeddate timestamp without time zone,
    creationdate timestamp without time zone NOT NULL,
    description character varying(512),
    disease character varying(255),
    investigateddate timestamp without time zone,
    negativedate timestamp without time zone,
    nocasedate timestamp without time zone,
    postivedate timestamp without time zone,
    recovereddate timestamp without time zone,
    reportdate timestamp without time zone,
    suspectdate timestamp without time zone,
    uuid character varying(36) NOT NULL,
    caseofficer_id bigint,
    casesupervisor_id bigint,
    contactofficer_id bigint,
    contactsupervisor_id bigint,
    healthfacility_id bigint,
    illlocation_id bigint,
    reportinguser_id bigint,
    surveillanceofficer_id bigint,
    surveillancesupervisor_id bigint,
    person_id bigint NOT NULL
);


ALTER TABLE cases OWNER TO sormas_user;

--
-- TOC entry 148 (class 1259 OID 341964)
-- Name: community; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE community (
    id bigint NOT NULL,
    changedate timestamp without time zone NOT NULL,
    creationdate timestamp without time zone NOT NULL,
    name character varying(255),
    uuid character varying(36) NOT NULL,
    district_id bigint NOT NULL
);


ALTER TABLE community OWNER TO sormas_user;

--
-- TOC entry 147 (class 1259 OID 341957)
-- Name: district; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE district (
    id bigint NOT NULL,
    changedate timestamp without time zone NOT NULL,
    creationdate timestamp without time zone NOT NULL,
    name character varying(255),
    uuid character varying(36) NOT NULL,
    region_id bigint NOT NULL
);


ALTER TABLE district OWNER TO sormas_user;

--
-- TOC entry 151 (class 1259 OID 342106)
-- Name: entity_seq; Type: SEQUENCE; Schema: public; Owner: sormas_user
--

CREATE SEQUENCE entity_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE entity_seq OWNER TO sormas_user;

--
-- TOC entry 149 (class 1259 OID 341971)
-- Name: facility; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE facility (
    id bigint NOT NULL,
    changedate timestamp without time zone NOT NULL,
    creationdate timestamp without time zone NOT NULL,
    name character varying(255),
    publicownership boolean,
    type character varying(255),
    uuid character varying(36) NOT NULL,
    location_id bigint
);


ALTER TABLE facility OWNER TO sormas_user;

--
-- TOC entry 144 (class 1259 OID 341930)
-- Name: location; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE location (
    id bigint NOT NULL,
    address character varying(255),
    changedate timestamp without time zone NOT NULL,
    city character varying(255),
    creationdate timestamp without time zone NOT NULL,
    details character varying(255),
    latitude double precision,
    longitude double precision,
    uuid character varying(36) NOT NULL,
    community_id bigint,
    district_id bigint,
    region_id bigint
);


ALTER TABLE location OWNER TO sormas_user;

--
-- TOC entry 143 (class 1259 OID 341920)
-- Name: person; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE person (
    id bigint NOT NULL,
    approximateage integer,
    approximateagetype integer,
    birthdate date,
    burialconductor character varying(255),
    burialdate date,
    changedate timestamp without time zone NOT NULL,
    creationdate timestamp without time zone NOT NULL,
    dead boolean,
    deathdate date,
    firstname character varying(255) NOT NULL,
    lastname character varying(255) NOT NULL,
    occupationdetails character varying(255),
    occupationtype character varying(255),
    phone character varying(255),
    presentcondition integer,
    sex character varying(255),
    uuid character varying(36) NOT NULL,
    address_id bigint,
    buriallocation_id bigint,
    deathlocation_id bigint,
    occupationfacility_id bigint
);


ALTER TABLE person OWNER TO sormas_user;

--
-- TOC entry 146 (class 1259 OID 341950)
-- Name: region; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE region (
    id bigint NOT NULL,
    changedate timestamp without time zone NOT NULL,
    creationdate timestamp without time zone NOT NULL,
    name character varying(255),
    uuid character varying(36) NOT NULL
);


ALTER TABLE region OWNER TO sormas_user;

--
-- TOC entry 150 (class 1259 OID 341981)
-- Name: userroles; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE userroles (
    user_id bigint NOT NULL,
    userrole character varying(255) NOT NULL
);


ALTER TABLE userroles OWNER TO sormas_user;

--
-- TOC entry 145 (class 1259 OID 341940)
-- Name: users; Type: TABLE; Schema: public; Owner: sormas_user; Tablespace: 
--

CREATE TABLE users (
    id bigint NOT NULL,
    aktiv boolean NOT NULL,
    changedate timestamp without time zone NOT NULL,
    creationdate timestamp without time zone NOT NULL,
    firstname character varying(255) NOT NULL,
    lastname character varying(255) NOT NULL,
    password character varying(64) NOT NULL,
    phone character varying(255),
    seed character varying(16) NOT NULL,
    useremail character varying(255),
    username character varying(255) NOT NULL,
    uuid character varying(36) NOT NULL,
    address_id bigint,
    associatedofficer_id bigint,
    region_id bigint
);


ALTER TABLE users OWNER TO sormas_user;

--
-- TOC entry 1732 (class 2606 OID 341917)
-- Name: cases_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT cases_pkey PRIMARY KEY (id);


--
-- TOC entry 1734 (class 2606 OID 341919)
-- Name: cases_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT cases_uuid_key UNIQUE (uuid);


--
-- TOC entry 1756 (class 2606 OID 341968)
-- Name: community_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY community
    ADD CONSTRAINT community_pkey PRIMARY KEY (id);


--
-- TOC entry 1758 (class 2606 OID 341970)
-- Name: community_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY community
    ADD CONSTRAINT community_uuid_key UNIQUE (uuid);


--
-- TOC entry 1752 (class 2606 OID 341961)
-- Name: district_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY district
    ADD CONSTRAINT district_pkey PRIMARY KEY (id);


--
-- TOC entry 1754 (class 2606 OID 341963)
-- Name: district_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY district
    ADD CONSTRAINT district_uuid_key UNIQUE (uuid);


--
-- TOC entry 1760 (class 2606 OID 341978)
-- Name: facility_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY facility
    ADD CONSTRAINT facility_pkey PRIMARY KEY (id);


--
-- TOC entry 1762 (class 2606 OID 341980)
-- Name: facility_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY facility
    ADD CONSTRAINT facility_uuid_key UNIQUE (uuid);


--
-- TOC entry 1740 (class 2606 OID 341937)
-- Name: location_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY location
    ADD CONSTRAINT location_pkey PRIMARY KEY (id);


--
-- TOC entry 1742 (class 2606 OID 341939)
-- Name: location_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY location
    ADD CONSTRAINT location_uuid_key UNIQUE (uuid);


--
-- TOC entry 1736 (class 2606 OID 341927)
-- Name: person_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- TOC entry 1738 (class 2606 OID 341929)
-- Name: person_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_uuid_key UNIQUE (uuid);


--
-- TOC entry 1748 (class 2606 OID 341954)
-- Name: region_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY region
    ADD CONSTRAINT region_pkey PRIMARY KEY (id);


--
-- TOC entry 1750 (class 2606 OID 341956)
-- Name: region_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY region
    ADD CONSTRAINT region_uuid_key UNIQUE (uuid);


--
-- TOC entry 1764 (class 2606 OID 341985)
-- Name: unq_userroles_0; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY userroles
    ADD CONSTRAINT unq_userroles_0 UNIQUE (user_id, userrole);


--
-- TOC entry 1744 (class 2606 OID 341947)
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 1746 (class 2606 OID 341949)
-- Name: users_uuid_key; Type: CONSTRAINT; Schema: public; Owner: sormas_user; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_uuid_key UNIQUE (uuid);


--
-- TOC entry 1765 (class 2606 OID 341986)
-- Name: fk_cases_caseofficer_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_caseofficer_id FOREIGN KEY (caseofficer_id) REFERENCES users(id);


--
-- TOC entry 1769 (class 2606 OID 342006)
-- Name: fk_cases_casesupervisor_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_casesupervisor_id FOREIGN KEY (casesupervisor_id) REFERENCES users(id);


--
-- TOC entry 1770 (class 2606 OID 342011)
-- Name: fk_cases_contactofficer_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_contactofficer_id FOREIGN KEY (contactofficer_id) REFERENCES users(id);


--
-- TOC entry 1768 (class 2606 OID 342001)
-- Name: fk_cases_contactsupervisor_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_contactsupervisor_id FOREIGN KEY (contactsupervisor_id) REFERENCES users(id);


--
-- TOC entry 1772 (class 2606 OID 342021)
-- Name: fk_cases_healthfacility_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_healthfacility_id FOREIGN KEY (healthfacility_id) REFERENCES facility(id);


--
-- TOC entry 1773 (class 2606 OID 342026)
-- Name: fk_cases_illlocation_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_illlocation_id FOREIGN KEY (illlocation_id) REFERENCES location(id);


--
-- TOC entry 1767 (class 2606 OID 341996)
-- Name: fk_cases_person_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_person_id FOREIGN KEY (person_id) REFERENCES person(id);


--
-- TOC entry 1766 (class 2606 OID 341991)
-- Name: fk_cases_reportinguser_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_reportinguser_id FOREIGN KEY (reportinguser_id) REFERENCES users(id);


--
-- TOC entry 1774 (class 2606 OID 342031)
-- Name: fk_cases_surveillanceofficer_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_surveillanceofficer_id FOREIGN KEY (surveillanceofficer_id) REFERENCES users(id);


--
-- TOC entry 1771 (class 2606 OID 342016)
-- Name: fk_cases_surveillancesupervisor_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY cases
    ADD CONSTRAINT fk_cases_surveillancesupervisor_id FOREIGN KEY (surveillancesupervisor_id) REFERENCES users(id);


--
-- TOC entry 1786 (class 2606 OID 342091)
-- Name: fk_community_district_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY community
    ADD CONSTRAINT fk_community_district_id FOREIGN KEY (district_id) REFERENCES district(id);


--
-- TOC entry 1785 (class 2606 OID 342086)
-- Name: fk_district_region_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY district
    ADD CONSTRAINT fk_district_region_id FOREIGN KEY (region_id) REFERENCES region(id);


--
-- TOC entry 1787 (class 2606 OID 342096)
-- Name: fk_facility_location_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY facility
    ADD CONSTRAINT fk_facility_location_id FOREIGN KEY (location_id) REFERENCES location(id);


--
-- TOC entry 1779 (class 2606 OID 342056)
-- Name: fk_location_community_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY location
    ADD CONSTRAINT fk_location_community_id FOREIGN KEY (community_id) REFERENCES community(id);


--
-- TOC entry 1781 (class 2606 OID 342066)
-- Name: fk_location_district_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY location
    ADD CONSTRAINT fk_location_district_id FOREIGN KEY (district_id) REFERENCES district(id);


--
-- TOC entry 1780 (class 2606 OID 342061)
-- Name: fk_location_region_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY location
    ADD CONSTRAINT fk_location_region_id FOREIGN KEY (region_id) REFERENCES region(id);


--
-- TOC entry 1778 (class 2606 OID 342051)
-- Name: fk_person_address_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk_person_address_id FOREIGN KEY (address_id) REFERENCES location(id);


--
-- TOC entry 1776 (class 2606 OID 342041)
-- Name: fk_person_buriallocation_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk_person_buriallocation_id FOREIGN KEY (buriallocation_id) REFERENCES location(id);


--
-- TOC entry 1775 (class 2606 OID 342036)
-- Name: fk_person_deathlocation_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk_person_deathlocation_id FOREIGN KEY (deathlocation_id) REFERENCES location(id);


--
-- TOC entry 1777 (class 2606 OID 342046)
-- Name: fk_person_occupationfacility_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk_person_occupationfacility_id FOREIGN KEY (occupationfacility_id) REFERENCES facility(id);


--
-- TOC entry 1788 (class 2606 OID 342101)
-- Name: fk_userroles_user_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY userroles
    ADD CONSTRAINT fk_userroles_user_id FOREIGN KEY (user_id) REFERENCES users(id);


--
-- TOC entry 1783 (class 2606 OID 342076)
-- Name: fk_users_address_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk_users_address_id FOREIGN KEY (address_id) REFERENCES location(id);


--
-- TOC entry 1784 (class 2606 OID 342081)
-- Name: fk_users_associatedofficer_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk_users_associatedofficer_id FOREIGN KEY (associatedofficer_id) REFERENCES users(id);


--
-- TOC entry 1782 (class 2606 OID 342071)
-- Name: fk_users_region_id; Type: FK CONSTRAINT; Schema: public; Owner: sormas_user
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk_users_region_id FOREIGN KEY (region_id) REFERENCES region(id);


--
-- TOC entry 1881 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;

ALTER TABLE person ADD COLUMN phoneowner character varying(255);
  
INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');


-- 2016-09-27; #55; sszczesny
ALTER TABLE person DROP COLUMN birthdate;
ALTER TABLE person ADD COLUMN birthdate_dd integer;
ALTER TABLE person ADD COLUMN birthdate_mm integer;
ALTER TABLE person ADD COLUMN birthdate_yyyy integer;

INSERT INTO schema_version (version_number, comment) VALUES (2, 'Split person birthdate year, month, day');


-- 2016-10-04; #58
CREATE TABLE symptoms (
id bigint not null, 
abdominalpain varchar(255),
anorexiaappetiteloss varchar(255),
bleedingvagina varchar(255),
changedate timestamp not null,
chestpain varchar(255),
comaunconscious varchar(255),
confuseddisoriented varchar(255),
conjunctivitis varchar(255),
cough varchar(255),
creationdate timestamp not null,
diarrhea varchar(255),
difficultybreathing varchar(255),
difficultyswallowing varchar(255),
digestedbloodvomit varchar(255),
epistaxis varchar(255),
eyepainlightsensitive varchar(255),
fever varchar(255),
gumsbleeding varchar(255),
headache varchar(255),
hematemesis varchar(255),
hematuria varchar(255),
hemoptysis varchar(255),
hiccups varchar(255),
injectionsitebleeding varchar(255),
intensefatigueweakness varchar(255),
jaundice varchar(255),
jointpain varchar(255),
melena varchar(255),
musclepain varchar(255),
otherhemorrhagic varchar(255),
otherhemorrhagictext varchar(255),
othernonhemorrhagic varchar(255),
othernonhemorrhagicsymptoms varchar(255),
petechiae varchar(255),
skinrash varchar(255),
sorethroat varchar(255), 
onsetdate timestamp without time zone,
temperature real,
temperaturesource varchar(255),
unexplainedbleeding varchar(255),
uuid varchar(36) not null unique,
vomitingnausea varchar(255),
PRIMARY KEY (id));

ALTER TABLE symptoms OWNER TO sormas_user;

ALTER TABLE cases ADD COLUMN symptoms_id bigint;
ALTER TABLE cases ADD CONSTRAINT fk_cases_symptoms_id FOREIGN KEY (symptoms_id) REFERENCES symptoms (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO schema_version (version_number, comment) VALUES (3, 'Symptoms');



-- 2016-10-14; #63

CREATE TABLE task (
id bigint not null,
assigneereply varchar(512), 
changedate timestamp not null, 
creationdate timestamp not null, 
creatorcomment varchar(512), 
duedate timestamp, 
perceivedstart timestamp, 
statuschangedate timestamp, 
taskcontext varchar(255), 
taskstatus varchar(255), 
tasktype varchar(255), 
uuid varchar(36) not null unique, 
assigneeuser_id bigint, 
caze_id bigint, 
creatoruser_id bigint, 
PRIMARY KEY (id));

ALTER TABLE task OWNER TO sormas_user;

ALTER TABLE task ADD CONSTRAINT fk_task_caze_id FOREIGN KEY (caze_id) REFERENCES cases (id);
ALTER TABLE task ADD CONSTRAINT fk_task_creatoruser_id FOREIGN KEY (creatoruser_id) REFERENCES users (id);
ALTER TABLE task ADD CONSTRAINT fk_task_assigneeuser_id FOREIGN KEY (assigneeuser_id) REFERENCES users (id);

INSERT INTO schema_version (version_number, comment) VALUES (4, 'Task');

-- 2016-10-18; #63 additions

ALTER TABLE task ADD COLUMN priority varchar(255);
ALTER TABLE task ADD COLUMN suggestedstart timestamp;
DELETE FROM task; 

INSERT INTO schema_version (version_number, comment) VALUES (5, 'Task priority & suggested start');

-- 2016-10-25; #78 disease config

UPDATE cases SET disease='EVD' WHERE disease='EBOLA';

ALTER TABLE symptoms DROP COLUMN difficultyswallowing;
ALTER TABLE symptoms DROP COLUMN intensefatigueweakness;
ALTER TABLE symptoms RENAME othernonhemorrhagicsymptoms TO othernonhemorrhagicsymptomstext;
ALTER TABLE symptoms RENAME othernonhemorrhagic TO othernonhemorrhagicsymptoms;
ALTER TABLE symptoms RENAME otherhemorrhagictext TO otherhemorrhagicsymptomstext;
ALTER TABLE symptoms RENAME otherhemorrhagic TO otherhemorrhagicsymptoms;
ALTER TABLE symptoms RENAME vomitingnausea  TO vomiting;
ALTER TABLE symptoms ADD COLUMN chills character varying(255);
ALTER TABLE symptoms ADD COLUMN dehydration character varying(255);
ALTER TABLE symptoms ADD COLUMN fatigueweakness character varying(255);
ALTER TABLE symptoms ADD COLUMN highbloodpressure character varying(255);
ALTER TABLE symptoms ADD COLUMN kopliksspots character varying(255);
ALTER TABLE symptoms ADD COLUMN lethargy character varying(255);
ALTER TABLE symptoms ADD COLUMN lowbloodpressure character varying(255);
ALTER TABLE symptoms ADD COLUMN nausea character varying(255);
ALTER TABLE symptoms ADD COLUMN neckstiffness character varying(255);
ALTER TABLE symptoms ADD COLUMN oedema character varying(255);
ALTER TABLE symptoms ADD COLUMN onsetsymptom character varying(255);
ALTER TABLE symptoms ADD COLUMN otitismedia character varying(255);
ALTER TABLE symptoms ADD COLUMN refusalfeedordrink character varying(255);
ALTER TABLE symptoms ADD COLUMN runnynose character varying(255);
ALTER TABLE symptoms ADD COLUMN seizures character varying(255);
ALTER TABLE symptoms ADD COLUMN sepsis character varying(255);
ALTER TABLE symptoms ADD COLUMN swollenlymphnodes character varying(255);
ALTER TABLE symptoms ADD COLUMN symptomatic boolean;

INSERT INTO schema_version (version_number, comment) VALUES (6, 'EBOLA -> EVD; Symptoms');

-- 2016-11-08; case + user: replaced supervisor references with regional references #90

ALTER TABLE public.cases DROP COLUMN casesupervisor_id;
ALTER TABLE public.cases DROP COLUMN contactsupervisor_id;
ALTER TABLE public.cases DROP COLUMN surveillancesupervisor_id;
ALTER TABLE public.cases ADD COLUMN region_id bigint;
ALTER TABLE public.cases ADD COLUMN district_id bigint;
ALTER TABLE public.cases ADD COLUMN community_id bigint;
ALTER TABLE public.cases ADD CONSTRAINT fk_cases_region_id FOREIGN KEY (region_id) REFERENCES public.region (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE public.cases ADD CONSTRAINT fk_cases_district_id FOREIGN KEY (district_id) REFERENCES public.district (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE public.cases ADD CONSTRAINT fk_cases_community_id FOREIGN KEY (community_id) REFERENCES public.community (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
UPDATE public.cases SET region_id=(SELECT id FROM public.region LIMIT 1);

ALTER TABLE public.users ADD COLUMN district_id bigint;
ALTER TABLE public.users ADD CONSTRAINT fk_users_district_id FOREIGN KEY (district_id) REFERENCES public.district (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
UPDATE public.users SET region_id=(SELECT id FROM public.region LIMIT 1);

INSERT INTO schema_version (version_number, comment) VALUES (7, 'Case + User: replaced supervisor references with regional references');

-- 2016-11-10; Contact #85

CREATE TABLE contact (
id bigint not null, 
changedate timestamp not null, 
contactproximity varchar(255), 
contactstatus varchar(255), 
creationdate timestamp not null, 
lastcontactdate timestamp, 
reportdatetime timestamp not null, 
uuid varchar(36) not null unique, 
caze_id bigint not null,
person_id bigint not null, 
reportinguser_id bigint not null, 
primary key (id));

ALTER TABLE contact ADD CONSTRAINT fk_contact_person_id FOREIGN KEY (person_id) REFERENCES person (id);
ALTER TABLE contact ADD CONSTRAINT fk_contact_caze_id FOREIGN KEY (caze_id) REFERENCES cases (id);
ALTER TABLE contact ADD CONSTRAINT fk_contact_reportinguser_id FOREIGN KEY (reportinguser_id) REFERENCES users (id);

ALTER TABLE public.cases ALTER COLUMN reportdate SET NOT NULL;
ALTER TABLE public.cases ALTER COLUMN reportinguser_id SET NOT NULL;

INSERT INTO schema_version (version_number, comment) VALUES (8, 'Contact; Cases report not null');

-- 2016-11-15; Contact #85

ALTER TABLE contact ADD COLUMN description varchar(512);
 
INSERT INTO schema_version (version_number, comment) VALUES (9, 'Contact.description');

-- 2016-11-16; Contact #85

ALTER TABLE contact ADD COLUMN contactofficer_id bigint;
ALTER TABLE contact ADD CONSTRAINT fk_contact_contactofficer_id FOREIGN KEY (contactofficer_id) REFERENCES users (id);
ALTER TABLE contact OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (10, 'Contact.contactOfficer + OWNER');

-- 2016-11-29; Contact Visits backend #10

ALTER TABLE contact DROP COLUMN contactstatus;
ALTER TABLE contact ADD COLUMN contactclassification varchar(255);
ALTER TABLE contact ADD COLUMN followupstatus varchar(255);
ALTER TABLE contact ADD COLUMN followupuntil timestamp;

CREATE TABLE visit (
	id bigint not null, 
	uuid varchar(36) not null unique, 
	changedate timestamp not null, 
	creationdate timestamp not null,
	person_id bigint not null, 
	visituser_id bigint not null, 
    visitremarks character varying(512),
    disease character varying(255),
	visitdatetime timestamp not null,
    visitstatus character varying(255),
	symptoms_id bigint,
	primary key (id));
ALTER TABLE visit OWNER TO sormas_user;

ALTER TABLE visit ADD CONSTRAINT fk_visit_symptoms_id FOREIGN KEY (symptoms_id) REFERENCES symptoms (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE visit ADD CONSTRAINT fk_visit_person_id FOREIGN KEY (person_id) REFERENCES person (id);
ALTER TABLE visit ADD CONSTRAINT fk_visit_visituser_id FOREIGN KEY (visituser_id) REFERENCES users (id);

INSERT INTO schema_version (version_number, comment) VALUES (11, 'visit, contact classification, follow-up status, follow-up until');

-- 2016-12-15; Assign tasks to a contact #53

ALTER TABLE task ADD COLUMN contact_id bigint;
ALTER TABLE task ADD CONSTRAINT fk_task_contact_id FOREIGN KEY (contact_id) REFERENCES contact (id);
INSERT INTO schema_version (version_number, comment) VALUES (12, 'Contact added to task');

-- 2016-12-14 Split CaseStatus to CaseClassification and InvestigationStatus #40

ALTER TABLE cases ADD COLUMN caseclassification character varying(255) DEFAULT 'POSSIBLE' NOT NULL;
ALTER TABLE cases ADD COLUMN investigationstatus character varying(255) DEFAULT 'PENDING' NOT NULL;
ALTER TABLE cases DROP COLUMN casestatus;

INSERT INTO schema_version (version_number, comment) VALUES (13, 'Split CaseStatus to CaseClassification and InvestigationStatus');

-- 2016-12-16 Update symptoms #41

ALTER TABLE symptoms DROP COLUMN comaunconscious;
ALTER TABLE symptoms DROP COLUMN epistaxis;
ALTER TABLE symptoms DROP COLUMN hematemesis;
ALTER TABLE symptoms DROP COLUMN hematuria;
ALTER TABLE symptoms DROP COLUMN jaundice;
ALTER TABLE symptoms DROP COLUMN melena;
ALTER TABLE symptoms DROP COLUMN petechiae;
ALTER TABLE symptoms DROP COLUMN chills;
ALTER TABLE symptoms DROP COLUMN highbloodpressure;
ALTER TABLE symptoms DROP COLUMN lethargy;
ALTER TABLE symptoms DROP COLUMN lowbloodpressure;
ALTER TABLE symptoms DROP COLUMN oedema;
ALTER TABLE symptoms DROP COLUMN sepsis;
ALTER TABLE symptoms DROP COLUMN swollenlymphnodes;
ALTER TABLE symptoms ADD COLUMN bloodinstool character varying(255);
ALTER TABLE symptoms ADD COLUMN nosebleeding character varying(255);
ALTER TABLE symptoms ADD COLUMN bloodyblackstool character varying(255);
ALTER TABLE symptoms ADD COLUMN redbloodvomit character varying(255);
ALTER TABLE symptoms ADD COLUMN coughingblood character varying(255);
ALTER TABLE symptoms ADD COLUMN skinbruising character varying(255);
ALTER TABLE symptoms ADD COLUMN bloodurine character varying(255);
ALTER TABLE symptoms ADD COLUMN alteredconsciousness character varying(255);
ALTER TABLE symptoms ADD COLUMN throbocytopenia character varying(255);
ALTER TABLE symptoms ADD COLUMN hearingloss character varying(255);
ALTER TABLE symptoms ADD COLUMN shock character varying(255);
ALTER TABLE symptoms ADD COLUMN symptomscomments character varying(255);

INSERT INTO schema_version (version_number, comment) VALUES (14, 'Update symptoms');

-- 2017-01-05 Event backend #63

CREATE TABLE events (
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	eventtype varchar(255) not null,
	eventstatus varchar(255) not null,
	eventdesc varchar(512) not null,
	eventdate timestamp,
	reportdatetime timestamp not null,
	reportinguser_id bigint not null,
	location_id bigint,
	typeofplace varchar(255) not null,
	srcfirstname varchar(512) not null,
	srclastname varchar(512) not null,
	srctelno varchar(512) not null,
	srcemail varchar(512),
	primary key(id));
ALTER TABLE events OWNER TO sormas_user;

ALTER TABLE events ADD CONSTRAINT fk_events_reportinguser_id FOREIGN KEY (reportinguser_id) REFERENCES users (id);
ALTER TABLE events ADD CONSTRAINT fk_events_location_id FOREIGN KEY (location_id) REFERENCES location (id);

CREATE TABLE eventparticipant (
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	event_id bigint,
	person_id bigint,
	kindofinvolvement varchar(255),
	primary key(id));
ALTER TABLE eventparticipant OWNER TO sormas_user;

ALTER TABLE eventparticipant ADD CONSTRAINT fk_eventparticipant_event_id FOREIGN KEY (event_id) REFERENCES events (id);
ALTER TABLE eventparticipant ADD CONSTRAINT fk_eventparticipant_person_id FOREIGN KEY (person_id) REFERENCES person (id);

INSERT INTO schema_version (version_number, comment) VALUES (15, 'events, eventparticipant');

-- 2017-01-06 Renaming #63

ALTER TABLE events RENAME location_id TO eventlocation_id;

INSERT INTO schema_version (version_number, comment) VALUES (16, 'renamed eventlocation');

-- 2017-01-10 Update events #63

ALTER TABLE events ADD COLUMN disease character varying(255);
ALTER TABLE events ADD COLUMN surveillanceofficer_id bigint;

INSERT INTO schema_version (version_number, comment) VALUES (17, 'update events');

-- 2017-01-11 Update events with type of place text #63

ALTER TABLE events ADD COLUMN typeofplacetext character varying(255);

INSERT INTO schema_version (version_number, comment) VALUES (18, 'update events with type of place text');

-- 2017-01-12 Assign tasks to an event #65

ALTER TABLE task ADD COLUMN event_id bigint;
ALTER TABLE task ADD CONSTRAINT fk_task_event_id FOREIGN KEY (event_id) REFERENCES events (id);

INSERT INTO schema_version (version_number, comment) VALUES (19, 'Event added to task');

-- 2017-01-17 Change kind of involvement to involvement description #66

ALTER TABLE eventparticipant DROP COLUMN kindofinvolvement;
ALTER TABLE eventparticipant ADD COLUMN involvementdescription varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (20, 'Involvement description instead of Kind of involvement');

-- 2017-01-20 Add relation to case to contact #75

ALTER TABLE contact ADD COLUMN relationtocase varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (21, 'Add relation to case to contact');

-- 2017-01-23 Add nickname and mother's maiden name to person #19

ALTER TABLE person ADD COLUMN nickname varchar(255);
ALTER TABLE person ADD COLUMN mothersmaidenname varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (22, 'Add nickname and mothers maiden name to person');

-- 2017-01-26 add health facility to user (informant) #49

ALTER TABLE users ADD COLUMN healthfacility_id bigint;
ALTER TABLE users ADD CONSTRAINT fk_users_healthfacility_id FOREIGN KEY (healthfacility_id) REFERENCES facility(id);

INSERT INTO schema_version (version_number, comment) VALUES (23, 'Add health facility to users (informant)');

-- 2017-01-30 Sample and SampleTest backend #106

CREATE TABLE samples(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	associatedcase_id bigint not null,
	samplecode varchar(512),
	sampledatetime timestamp not null,
	reportdatetime timestamp not null,
	reportinguser_id bigint not null,
	samplematerial varchar(255) not null,
	samplematerialtext varchar(512),
	lab_id bigint not null,
	otherlab_id bigint,
	shipmentstatus varchar(255) not null,
	shipmentdate timestamp not null,
	shipmentdetails varchar(512),
	receiveddate timestamp,
	notestpossible boolean,
	notestpossiblereason varchar(512),
	primary key(id));

CREATE TABLE sampletest(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	sample_id bigint not null,
	testtype varchar(255) not null,
	testtypetext varchar(512),
	testdatetime timestamp not null,
	lab_id bigint not null,
	labuser_id bigint not null,
	testresult varchar(255) not null,
	testresulttext varchar(512) not null,
	testresultverified boolean not null,
	primary key(id));
	
ALTER TABLE samples OWNER TO sormas_user;
ALTER TABLE sampletest OWNER TO sormas_user;
	
INSERT INTO schema_version (version_number, comment) VALUES (24, 'Sample and SampleTest backend');

-- 2017-02-03 Foreign keys for samples and sampletests #106

ALTER TABLE samples ADD CONSTRAINT fk_samples_associatedcase_id FOREIGN KEY (associatedcase_id) REFERENCES cases (id);
ALTER TABLE samples ADD CONSTRAINT fk_samples_reportinguser_id FOREIGN KEY (reportinguser_id) REFERENCES users (id);
ALTER TABLE samples ADD CONSTRAINT fk_samples_lab_id FOREIGN KEY (lab_id) REFERENCES facility (id);
ALTER TABLE samples ADD CONSTRAINT fk_samples_otherlab_id FOREIGN KEY (otherlab_id) REFERENCES facility (id);
ALTER TABLE sampletest ADD CONSTRAINT fk_sampletest_sample_id FOREIGN KEY (sample_id) REFERENCES samples (id);
ALTER TABLE sampletest ADD CONSTRAINT fk_sampletest_lab_id FOREIGN KEY (lab_id) REFERENCES facility (id);
ALTER TABLE sampletest ADD CONSTRAINT fk_sampletest_labuser_id FOREIGN KEY (labuser_id) REFERENCES users (id);

INSERT INTO schema_version (version_number, comment) VALUES (25, 'Foreign keys for samples and sampletest');

-- 2017-02-03 Laboratory user role #111

ALTER TABLE users ADD COLUMN laboratory_id bigint;
ALTER TABLE users ADD CONSTRAINT fk_users_laboratory_id FOREIGN KEY (laboratory_id) REFERENCES facility (id);

INSERT INTO schema_version (version_number, comment) VALUES (26, 'Laboratory user role');

-- 2017-02-03 Add admin role to user 'admin'

INSERT INTO userroles (user_id, userrole) SELECT id, 'ADMIN' FROM users WHERE username = 'admin';

INSERT INTO schema_version (version_number, comment) VALUES (27, 'Add admin role to user admin');

-- 2017-02-08 Removed null constraint from shipment date

ALTER TABLE samples ALTER COLUMN shipmentdate DROP NOT NULL;

INSERT INTO schema_version (version_number, comment) VALUES (28, 'Removed null constraint from shipment date');

-- 2017-02-17 Update samples entity #117

ALTER TABLE samples ADD COLUMN comment varchar(512);
ALTER TABLE samples DROP COLUMN notestpossible;
ALTER TABLE samples ADD COLUMN specimencondition varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (29, 'Update samples entity');

-- 2017-02-21 Hospitalization and PreviousHospitalization entities #89

CREATE TABLE hospitalization(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	hospitalized varchar(255),
	admissiondate timestamp,
	healthfacility_id bigint,
	isolated varchar(255),
	isolationdate timestamp,
	hospitalizedpreviously varchar(255),
	primary key(id));
ALTER TABLE hospitalization OWNER TO sormas_user;
ALTER TABLE hospitalization ADD CONSTRAINT fk_hospitalization_healthfacility_id FOREIGN KEY (healthfacility_id) REFERENCES facility (id);

CREATE TABLE previoushospitalization(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	admissiondate timestamp,
	dischargedate timestamp,
	healthfacility_id bigint,
	isolated varchar(255),
	hospitalization_id bigint not null,
	primary key(id));
ALTER TABLE previoushospitalization OWNER TO sormas_user;
ALTER TABLE previoushospitalization ADD CONSTRAINT fk_previoushospitalization_healthfacility_id FOREIGN KEY (healthfacility_id) REFERENCES facility (id);
ALTER TABLE previoushospitalization ADD CONSTRAINT fk_previoushospitalization_hospitalization_id FOREIGN KEY (hospitalization_id) REFERENCES hospitalization (id);

ALTER TABLE cases ADD COLUMN hospitalization_id bigint;
ALTER TABLE cases ADD CONSTRAINT fk_cases_hospitalization_id FOREIGN KEY (hospitalization_id) REFERENCES hospitalization (id);

INSERT INTO schema_version (version_number, comment) VALUES (30, 'Hospitalization and PreviousHospitalization entities');

-- 2017-02-21 Drop Abia state #28 (Kano)

DELETE FROM task;
DELETE FROM sampletest;
DELETE FROM samples;
DELETE FROM visit;
DELETE FROM contact;
DELETE FROM eventparticipant;
DELETE FROM events;
DELETE FROM cases;
DELETE FROM symptoms;
DELETE FROM person;
DELETE FROM userroles;
DELETE FROM users;
DELETE FROM facility;
DELETE FROM location;
DELETE FROM community;
DELETE FROM district;
DELETE FROM region;

INSERT INTO schema_version (version_number, comment) VALUES (31, 'Removed Abia state demo data');

-- 2017-02-24 Update hospitalization and previoushospitalization entities

ALTER TABLE hospitalization DROP COLUMN hospitalized;
ALTER TABLE hospitalization ADD COLUMN dischargedate timestamp;
ALTER TABLE previoushospitalization ADD COLUMN description varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (32, 'Update hospitalization and previoushospitalization entities');

-- 2017-02-24 Removed health facility from hospitalization

ALTER TABLE public.hospitalization DROP COLUMN healthfacility_id;
INSERT INTO schema_version (version_number, comment) VALUES (33, 'Removed health facility from hospitalization');

-- 2017-02-28 Laboratories update

DELETE FROM facility WHERE type = 'Laboratory';
INSERT INTO schema_version (version_number, comment) VALUES (34, 'Laboratories update');

-- 2017-03-01 Case measles additions

ALTER TABLE cases ADD COLUMN pregnant varchar(255);
ALTER TABLE cases ADD COLUMN measlesVaccination varchar(255);
ALTER TABLE cases ADD COLUMN measlesDoses varchar(512);
ALTER TABLE cases ADD COLUMN measlesVaccinationInfoSource varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (35, 'Case measles addition');

-- 2017-03-02 Epidemiological data

CREATE TABLE epidata(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	rodents varchar(255),
	bats varchar(255),
	primates varchar(255),
	swine varchar(255),
	birds varchar(255),
	poultryeat varchar(255),
	poultry varchar(255),
	poultrydetails varchar(512),
	poultrysick varchar(255),
	poultrysickdetails varchar(512),
	poultrydate timestamp,
	poultrylocation varchar(512),
	wildbirds varchar(255),
	wildbirdsdetails varchar(512),
	wildbirdsdate timestamp,
	wildbirdslocation varchar(512),
	cattle varchar(255),
	otheranimals varchar(255),
	otheranimalsdetails varchar(512),
	watersource varchar(255),
	watersourceother varchar(512),
	waterbody varchar(255),
	waterbodydetails varchar(512),
	tickbite varchar(255),
	burialattended varchar(255),
	gatheringattended varchar(255),
	traveled varchar(255),
	primary key(id)
);
ALTER TABLE epidata OWNER TO sormas_user;

CREATE TABLE epidataburial(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	epidata_id bigint not null,
	burialpersonname varchar(512),
	burialrelation varchar(512),
	burialdatefrom timestamp,
	burialdateto timestamp,
	burialaddress_id bigint,
	burialill varchar(255),
	burialtouching varchar(255),
	primary key(id)
);
ALTER TABLE epidataburial OWNER TO sormas_user;
ALTER TABLE epidataburial ADD CONSTRAINT fk_epidataburial_epidata_id FOREIGN KEY (epidata_id) REFERENCES epidata(id);
ALTER TABLE epidataburial ADD CONSTRAINT fk_epidataburial_burialaddress_id FOREIGN KEY (burialaddress_id) REFERENCES location(id);

CREATE TABLE epidatagathering(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	epidata_id bigint not null,
	description varchar(512),
	gatheringdate timestamp,
	gatheringaddress_id bigint,
	primary key(id)
);
ALTER TABLE epidatagathering OWNER TO sormas_user;
ALTER TABLE epidatagathering ADD CONSTRAINT fk_epidatagathering_epidata_id FOREIGN KEY (epidata_id) REFERENCES epidata(id);
ALTER TABLE epidatagathering ADD CONSTRAINT fk_epidatagathering_gatheringaddress_id FOREIGN KEY (gatheringaddress_id) REFERENCES location(id);

CREATE TABLE epidatatravel(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	epidata_id bigint not null,
	traveltype varchar(255),
	traveldestination varchar(512),
	traveldatefrom timestamp,
	traveldateto timestamp,
	primary key(id)
);
ALTER TABLE epidatatravel OWNER TO sormas_user;
ALTER TABLE epidatatravel ADD CONSTRAINT fk_epidatatravel_epidata_id FOREIGN KEY (epidata_id) REFERENCES epidata(id);

ALTER TABLE cases ADD COLUMN epidata_id bigint;
ALTER TABLE cases ADD CONSTRAINT fk_cases_epidata_id FOREIGN KEY (epidata_id) REFERENCES epidata(id);

INSERT INTO schema_version (version_number, comment) VALUES (36, 'Epidemiological data');


CREATE TEMP TABLE tmp_caseids AS SELECT cases.id AS caseid, nextval('entity_seq') AS epiid FROM cases WHERE cases.epidata_id IS NULL;
INSERT INTO epidata (id, changedate, creationdate, uuid) 
	SELECT epiid, now(), now(), uuid_in(md5(random()::text || clock_timestamp()::text)::cstring) FROM tmp_caseids;
UPDATE cases SET epidata_id = epiid FROM tmp_caseids t where cases.id = t.caseid;
DROP TABLE tmp_caseids;

INSERT INTO schema_version (version_number, comment) VALUES (37, 'Epidemiological data: added empty entites');

-- 2017-03-02 Don't use location entity for facilities #138

ALTER TABLE facility DROP COLUMN location_id;
ALTER TABLE facility ADD COLUMN region_id bigint;
ALTER TABLE facility ADD COLUMN district_id bigint;
ALTER TABLE facility ADD COLUMN community_id bigint;
ALTER TABLE facility ADD COLUMN city varchar(512);
ALTER TABLE facility ADD COLUMN latitude double precision;
ALTER TABLE facility ADD COLUMN longitude double precision;

ALTER TABLE facility ADD CONSTRAINT fk_facility_region_id FOREIGN KEY (region_id) REFERENCES region(id);
ALTER TABLE facility ADD CONSTRAINT fk_facility_district_id FOREIGN KEY (district_id) REFERENCES district(id);
ALTER TABLE facility ADD CONSTRAINT fk_facility_community_id FOREIGN KEY (community_id) REFERENCES community(id);

DELETE FROM task;
DELETE FROM sampletest;
DELETE FROM samples;
DELETE FROM visit;
DELETE FROM contact;
DELETE FROM eventparticipant;
DELETE FROM events;
DELETE FROM cases;
DELETE FROM symptoms;
DELETE FROM person;
DELETE FROM userroles;
DELETE FROM users;
DELETE FROM previoushospitalization;
DELETE FROM hospitalization;
DELETE FROM facility;
DELETE FROM epidataburial;
DELETE FROM epidatagathering;
DELETE FROM epidatatravel;
DELETE FROM epidata;
DELETE FROM location;
DELETE FROM community;
DELETE FROM district;
DELETE FROM region;

INSERT INTO schema_version (version_number, comment) VALUES (38, 'Dont use location entity for facilities');

-- 2017-03-22 Split sample code and lab sample ID #155

ALTER TABLE samples ADD COLUMN labsampleid varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (39, 'Split sample code and lab sample ID');

-- 2017-03-22 Drop not null constraint from sample test result text

ALTER TABLE sampletest ALTER COLUMN testresulttext DROP NOT NULL;

INSERT INTO schema_version (version_number, comment) VALUES (40, 'Drop not null constraint from sample test result text');

-- 2017-03-28 Added EPID number to case #167

ALTER TABLE cases ADD COLUMN epidnumber varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (41, 'Added EPID number to case');

-- 2017-03-29 Task types update, drop all tasks #97

DELETE FROM task;

INSERT INTO schema_version (version_number, comment) VALUES (42, 'Task types update, drop all tasks');

-- 2017-04-05 Rename POSSIBLE case classification to NOT_CLASSIFIED #175

UPDATE cases SET caseclassification = 'NOT_CLASSIFIED' WHERE caseclassification = 'POSSIBLE';

INSERT INTO schema_version (version_number, comment) VALUES (43, 'Rename POSSBILE case classification to NOT_CLASSIFIED');

-- 2017-04-05 #176

UPDATE task SET taskstatus = 'REMOVED' WHERE taskstatus = 'DISCARDED';

INSERT INTO schema_version (version_number, comment) VALUES (44, 'Rename DISCARDED task status to REMOVED');

-- 2017-04-07 Sample soruce and suggested type of test #178

ALTER TABLE samples ADD COLUMN samplesource varchar(255);
ALTER TABLE samples ADD COLUMN suggestedtypeoftest varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (45, 'Sample source and suggested type of test');

-- 2017-05-17 Missing person fields #196

ALTER TABLE person DROP COLUMN dead;
ALTER TABLE person DROP COLUMN buriallocation_id;
ALTER TABLE person DROP COLUMN deathlocation_id;
ALTER TABLE person ADD COLUMN deathplacetype varchar(255);
ALTER TABLE person ADD COLUMN deathplacedescription varchar(512);
ALTER TABLE person ADD COLUMN burialplacedescription varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (46, 'Missing person fields #196');

-- 2017-05-18 IllLocation for symptoms #196

ALTER TABLE cases DROP COLUMN illlocation_id;
ALTER TABLE symptoms ADD COLUMN illlocation_id bigint;
ALTER TABLE symptoms ADD COLUMN illlocationfrom timestamp;
ALTER TABLE symptoms ADD COLUMN illlocationto timestamp;

ALTER TABLE symptoms ADD CONSTRAINT fk_symptoms_illlocation_id FOREIGN KEY (illlocation_id) REFERENCES location(id);

INSERT INTO schema_version (version_number, comment) VALUES (47, 'IllLocation for symptoms');

-- 2017-06-07 change date for embedded lists #221

ALTER TABLE epidata ADD COLUMN changedateofembeddedlists timestamp without time zone;
ALTER TABLE hospitalization ADD COLUMN changedateofembeddedlists timestamp without time zone;

INSERT INTO schema_version (version_number, comment) VALUES (48, 'Change data for embedded lists (epidata and hospitalization');

-- 2017-06-20 data history for future reporting (postgres temporal tables) #170

CREATE EXTENSION temporal_tables;

ALTER TABLE cases ADD COLUMN sys_period tstzrange;
UPDATE cases SET sys_period=tstzrange(creationdate, null);
ALTER TABLE cases ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE cases_history (LIKE cases);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON cases
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'cases_history', true);
ALTER TABLE cases_history OWNER TO sormas_user;

ALTER TABLE contact ADD COLUMN sys_period tstzrange;
UPDATE contact SET sys_period=tstzrange(creationdate, null);
ALTER TABLE contact ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE contact_history (LIKE contact);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON contact
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'contact_history', true);
ALTER TABLE contact_history OWNER TO sormas_user;

ALTER TABLE epidata ADD COLUMN sys_period tstzrange;
UPDATE epidata SET sys_period=tstzrange(creationdate, null);
ALTER TABLE epidata ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE epidata_history (LIKE epidata);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON epidata
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'epidata_history', true);
ALTER TABLE epidata_history OWNER TO sormas_user;

ALTER TABLE epidataburial ADD COLUMN sys_period tstzrange;
UPDATE epidataburial SET sys_period=tstzrange(creationdate, null);
ALTER TABLE epidataburial ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE epidataburial_history (LIKE epidataburial);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON epidataburial
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'epidataburial_history', true);
ALTER TABLE epidataburial_history OWNER TO sormas_user;

ALTER TABLE epidatagathering ADD COLUMN sys_period tstzrange;
UPDATE epidatagathering SET sys_period=tstzrange(creationdate, null);
ALTER TABLE epidatagathering ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE epidatagathering_history (LIKE epidatagathering);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON epidatagathering
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'epidatagathering_history', true);
ALTER TABLE epidatagathering_history OWNER TO sormas_user;

ALTER TABLE epidatatravel ADD COLUMN sys_period tstzrange;
UPDATE epidatatravel SET sys_period=tstzrange(creationdate, null);
ALTER TABLE epidatatravel ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE epidatatravel_history (LIKE epidatatravel);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON epidatatravel
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'epidatatravel_history', true);
ALTER TABLE epidatatravel_history OWNER TO sormas_user;

ALTER TABLE events ADD COLUMN sys_period tstzrange;
UPDATE events SET sys_period=tstzrange(creationdate, null);
ALTER TABLE events ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE events_history (LIKE events);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON events
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'events_history', true);
ALTER TABLE events_history OWNER TO sormas_user;

ALTER TABLE location ADD COLUMN sys_period tstzrange;
UPDATE location SET sys_period=tstzrange(creationdate, null);
ALTER TABLE location ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE location_history (LIKE location);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON location
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'location_history', true);
ALTER TABLE location_history OWNER TO sormas_user;

ALTER TABLE person ADD COLUMN sys_period tstzrange;
UPDATE person SET sys_period=tstzrange(creationdate, null);
ALTER TABLE person ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE person_history (LIKE person);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON person
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'person_history', true);
ALTER TABLE person_history OWNER TO sormas_user;

ALTER TABLE previoushospitalization ADD COLUMN sys_period tstzrange;
UPDATE previoushospitalization SET sys_period=tstzrange(creationdate, null);
ALTER TABLE previoushospitalization ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE previoushospitalization_history (LIKE previoushospitalization);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON previoushospitalization
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'previoushospitalization_history', true);
ALTER TABLE previoushospitalization_history OWNER TO sormas_user;

ALTER TABLE samples ADD COLUMN sys_period tstzrange;
UPDATE samples SET sys_period=tstzrange(creationdate, null);
ALTER TABLE samples ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE samples_history (LIKE samples);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON samples
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'samples_history', true);
ALTER TABLE samples_history OWNER TO sormas_user;

ALTER TABLE sampletest ADD COLUMN sys_period tstzrange;
UPDATE sampletest SET sys_period=tstzrange(creationdate, null);
ALTER TABLE sampletest ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE sampletest_history (LIKE sampletest);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON sampletest
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'sampletest_history', true);
ALTER TABLE sampletest_history OWNER TO sormas_user;

ALTER TABLE symptoms ADD COLUMN sys_period tstzrange;
UPDATE symptoms SET sys_period=tstzrange(creationdate, null);
ALTER TABLE symptoms ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE symptoms_history (LIKE symptoms);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON symptoms
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'symptoms_history', true);
ALTER TABLE symptoms_history OWNER TO sormas_user;

ALTER TABLE task ADD COLUMN sys_period tstzrange;
UPDATE task SET sys_period=tstzrange(creationdate, null);
ALTER TABLE task ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE task_history (LIKE task);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON task
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'task_history', true);
ALTER TABLE task_history OWNER TO sormas_user;

ALTER TABLE users ADD COLUMN sys_period tstzrange;
UPDATE users SET sys_period=tstzrange(creationdate, null);
ALTER TABLE users ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE users_history (LIKE users);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'users_history', true);
ALTER TABLE users_history OWNER TO sormas_user;

ALTER TABLE visit ADD COLUMN sys_period tstzrange;
UPDATE visit SET sys_period=tstzrange(creationdate, null);
ALTER TABLE visit ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE visit_history (LIKE visit);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON visit
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'visit_history', true);
ALTER TABLE visit_history OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (49, 'data history for future reporting (postgres temporal tables) #170');

-- 2017-07-19 other health facility description for cases #238
ALTER TABLE cases ADD COLUMN healthfacilitydetails varchar(512);

-- 2017-07-20 Database wipe for new infrastructure data #237
BEGIN;
DELETE FROM task;
DELETE FROM sampletest;
DELETE FROM samples;
DELETE FROM visit;
DELETE FROM contact;
DELETE FROM eventparticipant;
DELETE FROM events;
DELETE FROM cases;
DELETE FROM symptoms;
DELETE FROM person;
DELETE FROM previoushospitalization;
DELETE FROM hospitalization;
DELETE FROM epidataburial;
DELETE FROM epidatagathering;
DELETE FROM epidatatravel;
DELETE FROM epidata;
DELETE FROM userroles;
DELETE FROM users;
DELETE FROM location;
DELETE FROM facility;
DELETE FROM community;
DELETE FROM district;
DELETE FROM region;
COMMIT;

INSERT INTO schema_version (version_number, comment) VALUES (50, 'other health facility description for cases #238');

-- 2015-07-25 fix wrong ending in LGA names of state Oyo. Will not be sent to mobile devices unless reinstalled
UPDATE public.district SET name=replace(name,' LGA', '') WHERE name ~ ' LGA$';
INSERT INTO schema_version (version_number, comment) VALUES (51, 'Fix Oyo LGA names #230');

-- 2017-07-25 Country, state and LGA codes #230
ALTER TABLE district ADD COLUMN epidcode varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (52, 'Country, state and LGA codes');

-- 2017-07-26 Update EPID codes #230

UPDATE district SET epidcode = null;

INSERT INTO schema_version (version_number, comment) VALUES (53, 'Update EPID codes');

-- 2017-07-31 Split EPID code #230
ALTER TABLE region ADD COLUMN epidcode varchar(255);
UPDATE public.district SET epidcode=substr(epidcode, 9);
UPDATE public.district SET changedate=now();
UPDATE public.region SET changedate=now();

INSERT INTO schema_version (version_number, comment) VALUES (54, 'Split EPID code');

-- 2017-08-07 Remove contact officer from case #267
ALTER TABLE cases DROP COLUMN contactofficer_id;

INSERT INTO schema_version (version_number, comment) VALUES (55, 'Remove contact officer from case');

-- 2017-08-07 Add referredTo field to sample #255
ALTER TABLE samples ADD COLUMN referredto_id bigint;
ALTER TABLE samples ADD CONSTRAINT fk_samples_referredto_id FOREIGN KEY (referredto_id) REFERENCES samples (id);

INSERT INTO schema_version (version_number, comment) VALUES (56, 'Add referredTo field to sample');

-- 2017-08-10 Replace ShipmentStatus with shipped and received booleans #229
ALTER TABLE samples ADD COLUMN shipped boolean;
ALTER TABLE samples ADD COLUMN received boolean;
UPDATE samples SET shipped=true WHERE shipmentstatus = 'SHIPPED' OR shipmentstatus = 'RECEIVED' OR shipmentstatus = 'REFERRED_OTHER_LAB';
UPDATE samples SET received=true WHERE shipmentstatus = 'RECEIVED' OR shipmentstatus = 'REFERRED_OTHER_LAB';
UPDATE samples SET shipped=false WHERE shipmentstatus = 'NOT_SHIPPED';
UPDATE samples SET received=false WHERE shipmentstatus = 'NOT_SHIPPED' OR shipmentstatus = 'SHIPPED';
ALTER TABLE samples DROP COLUMN shipmentstatus;
ALTER TABLE samples_history DROP COLUMN shipmentstatus;
ALTER TABLE cases_history DROP COLUMN contactofficer_id;

INSERT INTO schema_version (version_number, comment) VALUES (57, 'Replace ShipmentStatus with shipped and received booleans');

-- 2017-08-16 Add GEO tags to cases, contacts, events, visits and tasks #86
ALTER TABLE cases ADD COLUMN reportlat double precision;
ALTER TABLE cases ADD COLUMN reportlon double precision;
ALTER TABLE contact ADD COLUMN reportlat double precision;
ALTER TABLE contact ADD COLUMN reportlon double precision;
ALTER TABLE events ADD COLUMN reportlat double precision;
ALTER TABLE events ADD COLUMN reportlon double precision;
ALTER TABLE visit ADD COLUMN reportlat double precision;
ALTER TABLE visit ADD COLUMN reportlon double precision;
ALTER TABLE task ADD COLUMN closedlat double precision;
ALTER TABLE task ADD COLUMN closedlon double precision;

INSERT INTO schema_version (version_number, comment) VALUES (58, 'Add GEO tags to cases, contacts, events, visits and tasks');

-- 2017-08-16 Add GEO tags to cases, contacts, events, visits and tasks #86
ALTER TABLE cases_history ADD COLUMN reportlat double precision;
ALTER TABLE cases_history ADD COLUMN reportlon double precision;
ALTER TABLE contact_history ADD COLUMN reportlat double precision;
ALTER TABLE contact_history ADD COLUMN reportlon double precision;
ALTER TABLE events_history ADD COLUMN reportlat double precision;
ALTER TABLE events_history ADD COLUMN reportlon double precision;
ALTER TABLE visit_history ADD COLUMN reportlat double precision;
ALTER TABLE visit_history ADD COLUMN reportlon double precision;
ALTER TABLE task_history ADD COLUMN closedlat double precision;
ALTER TABLE task_history ADD COLUMN closedlon double precision;

INSERT INTO schema_version (version_number, comment) VALUES (59, 'Add GEO tags to history tables of cases, contacts, events, visits and tasks');

-- 2017-08-28 Follow-up comment #70
ALTER TABLE contact ADD COLUMN followUpComment varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (60, 'Follow-up comment #70');

-- 2017-08-31 Rename 'Other' and 'None' health facilities #261
UPDATE facility SET name = 'Other health facility' WHERE uuid = 'SORMAS-CONSTID-OTHERS-FACILITY';
UPDATE facility SET name = 'Not a health facility' WHERE uuid = 'SORMAS-CONSTID-ISNONE-FACILITY';

INSERT INTO schema_version (version_number, comment) VALUES (61, 'Rename "Other" and "None" health facilities');

-- 2017-09-04 RDC for Previous Hospitalizations #104
ALTER TABLE previoushospitalization ADD COLUMN region_id bigint;
ALTER TABLE previoushospitalization ADD COLUMN district_id bigint;
ALTER TABLE previoushospitalization ADD COLUMN community_id bigint;

ALTER TABLE previoushospitalization ADD CONSTRAINT fk_previoushospitalization_region_id FOREIGN KEY (region_id) REFERENCES region (id);
ALTER TABLE previoushospitalization ADD CONSTRAINT fk_previoushospitalization_district_id FOREIGN KEY (district_id) REFERENCES district (id);
ALTER TABLE previoushospitalization ADD CONSTRAINT fk_previoushospitalization_community_id FOREIGN KEY (community_id) REFERENCES community (id);

ALTER TABLE previoushospitalization_history ADD COLUMN region_id bigint;
ALTER TABLE previoushospitalization_history ADD COLUMN district_id bigint;
ALTER TABLE previoushospitalization_history ADD COLUMN community_id bigint;

UPDATE previoushospitalization SET region_id = (SELECT region_id FROM facility WHERE facility.id = previoushospitalization.healthfacility_id);
UPDATE previoushospitalization SET district_id = (SELECT district_id FROM facility WHERE facility.id = previoushospitalization.healthfacility_id);
UPDATE previoushospitalization SET community_id = (SELECT community_id FROM facility WHERE facility.id = previoushospitalization.healthfacility_id);
-- Set region, district and community to the values of the case for 'Other' and 'None' health facilities
UPDATE previoushospitalization SET region_id = (SELECT region_id FROM cases WHERE cases.hospitalization_id = previoushospitalization.hospitalization_id) WHERE region_id IS NULL;
UPDATE previoushospitalization SET district_id = (SELECT district_id FROM cases WHERE cases.hospitalization_id = previoushospitalization.hospitalization_id) WHERE district_id IS NULL;
UPDATE previoushospitalization SET community_id = (SELECT community_id FROM cases WHERE cases.hospitalization_id = previoushospitalization.hospitalization_id) WHERE community_id IS NULL;

INSERT INTO schema_version (version_number, comment) VALUES (62, 'RDC for Previous Hospitalizations');

-- 2017-09-05 Generic names for 'Other' and 'None' facilities #261
UPDATE facility SET name = 'OTHER_FACILITY' WHERE uuid = 'SORMAS-CONSTID-OTHERS-FACILITY';
UPDATE facility SET name = 'NO_FACILITY' WHERE uuid = 'SORMAS-CONSTID-ISNONE-FACILITY';

INSERT INTO schema_version (version_number, comment) VALUES (63, 'Generic names for Other and None facilities');

-- 2017-09-06 Weekly Reports and Weekly Report Entries #171
CREATE TABLE weeklyreport(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	healthfacility_id bigint not null,
	informant_id bigint not null,
	reportdatetime timestamp not null,
	totalnumberofcases integer not null,
	primary key(id)
);

ALTER TABLE weeklyreport OWNER TO sormas_user;
ALTER TABLE weeklyreport ADD CONSTRAINT fk_weeklyreport_healthfacility_id FOREIGN KEY (healthfacility_id) REFERENCES facility(id);
ALTER TABLE weeklyreport ADD CONSTRAINT fk_weeklyreport_informant_id FOREIGN KEY (informant_id) REFERENCES users(id);

CREATE TABLE weeklyreportentry(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	weeklyreport_id bigint not null,
    disease character varying(255) not null,
    numberofcases integer not null,
    primary key(id)
);

ALTER TABLE weeklyreportentry OWNER TO sormas_user;
ALTER TABLE weeklyreportentry ADD CONSTRAINT fk_weeklyreportentry_weeklyreport_id FOREIGN KEY (weeklyreport_id) REFERENCES weeklyreport(id);

INSERT INTO schema_version (version_number, comment) VALUES (64, 'Weekly Reports and Weekly Report Entries');

-- 2017-09-12 Year and epi week for Weekly Reports #171
ALTER TABLE weeklyreport ADD COLUMN year integer not null;
ALTER TABLE weeklyreport ADD COLUMN epiweek integer not null;

INSERT INTO schema_version (version_number, comment) VALUES (65, 'Year and epi week for Weekly Reports');

-- 2017-09-12 Population for regions #82
ALTER TABLE region ADD COLUMN population integer;
ALTER TABLE region ADD COLUMN growthRate real;

INSERT INTO schema_version (version_number, comment) VALUES (66, 'Population for regions #82');

-- 2017-09-14 Create history table for UserRoles #328
ALTER TABLE userroles ADD COLUMN sys_period tstzrange;
UPDATE userroles SET sys_period=tstzrange((SELECT users.creationdate FROM users WHERE users.id = userroles.user_id), null);
ALTER TABLE userroles ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE userroles_history (LIKE userroles);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON userroles
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'userroles_history', true);
ALTER TABLE userroles_history OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (67, 'Create history table for UserRoles #328');

-- 2017-09-18 Symptoms for Yellow fever and Dengue #262
ALTER TABLE symptoms ADD COLUMN backache varchar(255);
ALTER TABLE symptoms ADD COLUMN eyesbleeding varchar(255);
ALTER TABLE symptoms ADD COLUMN jaundice varchar(255);
ALTER TABLE symptoms ADD COLUMN darkurine varchar(255);
ALTER TABLE symptoms ADD COLUMN stomachbleeding varchar(255);
ALTER TABLE symptoms ADD COLUMN rapidbreathing varchar(255);
ALTER TABLE symptoms ADD COLUMN swollenglands varchar(255);

ALTER TABLE cases ADD COLUMN yellowfevervaccination varchar(255);
ALTER TABLE cases ADD COLUMN yellowfevervaccinationinfosource varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (68, 'Additional data for Yellow fever and Dengue #262');

-- 2017-09-21 Disease details field #322
ALTER TABLE cases ADD COLUMN diseasedetails varchar(512);
ALTER TABLE events ADD COLUMN diseasedetails varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (69, 'Disease details field #322');

-- 2017-09-25 IllLoction removed from symptoms #347
ALTER TABLE symptoms DROP COLUMN illlocation_id;
ALTER TABLE symptoms DROP COLUMN illlocationfrom;
ALTER TABLE symptoms DROP COLUMN illlocationto;
ALTER TABLE symptoms_history DROP COLUMN illlocation_id;
ALTER TABLE symptoms_history DROP COLUMN illlocationfrom;
ALTER TABLE symptoms_history DROP COLUMN illlocationto;

-- 2017-10-26 Add accuracy to lat lon data #371
ALTER TABLE location ADD COLUMN latLonAccuracy real;
ALTER TABLE cases ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE contact ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE events ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE visit ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE task ADD COLUMN closedLatLonAccuracy real;
ALTER TABLE samples ADD COLUMN reportLat double precision;
ALTER TABLE samples ADD COLUMN reportLon double precision;
ALTER TABLE samples ADD COLUMN reportLatLonAccuracy real;

ALTER TABLE location_history ADD COLUMN latLonAccuracy real;
ALTER TABLE cases_history ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE contact_history ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE events_history ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE visit_history ADD COLUMN reportLatLonAccuracy real;
ALTER TABLE task_history ADD COLUMN closedLatLonAccuracy real;
ALTER TABLE samples_history ADD COLUMN reportLat double precision;
ALTER TABLE samples_history ADD COLUMN reportLon double precision;
ALTER TABLE samples_history ADD COLUMN reportLatLonAccuracy real;

INSERT INTO schema_version (version_number, comment) VALUES (71, 'Add accuracy to lat lon data #371');

-- 2017-10-24 Monkeypox disease fields #366
ALTER TABLE symptoms ADD COLUMN cutaneouseruption varchar(255);
ALTER TABLE symptoms ADD COLUMN lesions varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionssamestate varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionssamesize varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionsdeepprofound varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionsface boolean;
ALTER TABLE symptoms ADD COLUMN lesionslegs boolean;
ALTER TABLE symptoms ADD COLUMN lesionssolesfeet boolean;
ALTER TABLE symptoms ADD COLUMN lesionspalmshands boolean;
ALTER TABLE symptoms ADD COLUMN lesionsthorax boolean;
ALTER TABLE symptoms ADD COLUMN lesionsarms boolean;
ALTER TABLE symptoms ADD COLUMN lesionsgenitals boolean;
ALTER TABLE symptoms ADD COLUMN lesionsalloverbody boolean;
ALTER TABLE symptoms ADD COLUMN lesionsresembleimg1 varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionsresembleimg2 varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionsresembleimg3 varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionsresembleimg4 varchar(255);
ALTER TABLE symptoms ADD COLUMN lymphadenopathyinguinal varchar(255);
ALTER TABLE symptoms ADD COLUMN lymphadenopathyaxillary varchar(255);
ALTER TABLE symptoms ADD COLUMN lymphadenopathycervical varchar(255);
ALTER TABLE symptoms ADD COLUMN chillssweats varchar(255);
ALTER TABLE symptoms ADD COLUMN lesionsthatitch varchar(255);
ALTER TABLE symptoms ADD COLUMN bedridden varchar(255);
ALTER TABLE symptoms ADD COLUMN oralulcers varchar(255);
ALTER TABLE symptoms ADD COLUMN patientilllocation varchar(512);
ALTER TABLE cases ADD COLUMN smallpoxVaccinationScar varchar(255);
ALTER TABLE epidata ADD COLUMN dateoflastexposure timestamp;
ALTER TABLE epidata ADD COLUMN placeoflastexposure varchar(512);
ALTER TABLE epidata ADD COLUMN animalcondition varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (72, 'Monkeypox disease fields #366');

-- 2017-10-27 Monkeypox disease fields for history table #366
ALTER TABLE symptoms_history ADD COLUMN cutaneouseruption varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesions varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionssamestate varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionssamesize varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionsdeepprofound varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionsface boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionslegs boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionssolesfeet boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionspalmshands boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionsthorax boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionsarms boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionsgenitals boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionsalloverbody boolean;
ALTER TABLE symptoms_history ADD COLUMN lesionsresembleimg1 varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionsresembleimg2 varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionsresembleimg3 varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionsresembleimg4 varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lymphadenopathyinguinal varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lymphadenopathyaxillary varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lymphadenopathycervical varchar(255);
ALTER TABLE symptoms_history ADD COLUMN chillssweats varchar(255);
ALTER TABLE symptoms_history ADD COLUMN lesionsthatitch varchar(255);
ALTER TABLE symptoms_history ADD COLUMN bedridden varchar(255);
ALTER TABLE symptoms_history ADD COLUMN oralulcers varchar(255);
ALTER TABLE symptoms_history ADD COLUMN patientilllocation varchar(512);
ALTER TABLE cases_history ADD COLUMN smallpoxvaccinationscar varchar(255);
ALTER TABLE epidata_history ADD COLUMN dateoflastexposure timestamp;
ALTER TABLE epidata_history ADD COLUMN placeoflastexposure varchar(512);
ALTER TABLE epidata_history ADD COLUMN animalcondition varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (73, 'Monkeypox disease fields for history table #366');

-- 2017-11-01 Plague disease fields #373
ALTER TABLE symptoms ADD COLUMN painfullymphadenitis varchar(255);
ALTER TABLE symptoms ADD COLUMN buboesgroinarmpitneck varchar(255);
ALTER TABLE symptoms ADD COLUMN blackeningdeathoftissue varchar(255);
ALTER TABLE cases ADD COLUMN plaguetype varchar(255);
ALTER TABLE epidata ADD COLUMN fleabite varchar(255);
ALTER TABLE sampletest ADD COLUMN fourfoldincreaseantibodytiter boolean;

ALTER TABLE symptoms_history ADD COLUMN painfullymphadenitis varchar(255);
ALTER TABLE symptoms_history ADD COLUMN buboesgroinarmpitneck varchar(255);
ALTER TABLE symptoms_history ADD COLUMN blackeningdeathoftissue varchar(255);
ALTER TABLE cases_history ADD COLUMN plaguetype varchar(255);
ALTER TABLE epidata_history ADD COLUMN fleabite varchar(255);
ALTER TABLE sampletest_history ADD COLUMN fourfoldincreaseantibodytiter boolean;

INSERT INTO schema_version (version_number, comment) VALUES (74, 'Plague disease fields #373');

-- 2017-11-02 Additional Monkeypox fields #375
ALTER TABLE cases ADD COLUMN smallpoxvaccinationreceived varchar(255);
ALTER TABLE cases ADD COLUMN smallpoxvaccinationdate timestamp;

ALTER TABLE cases_history ADD COLUMN smallpoxvaccinationreceived varchar(255);
ALTER TABLE cases_history ADD COLUMN smallpoxvaccinationdate timestamp;

INSERT INTO schema_version (version_number, comment) VALUES (75, 'Additional Monkeypox fields #375');

-- 2017-11-22 Population for districts #342
ALTER TABLE district ADD COLUMN population integer;
ALTER TABLE district ADD COLUMN growthRate real;

INSERT INTO schema_version (version_number, comment) VALUES (76, 'Population for districts #342');

-- 2017-11-30 Soft validation for event fields #405
ALTER TABLE events ALTER COLUMN typeofplace DROP NOT NULL;
ALTER TABLE events ALTER COLUMN srcfirstname DROP NOT NULL;
ALTER TABLE events ALTER COLUMN srclastname DROP NOT NULL;
ALTER TABLE events ALTER COLUMN srctelno DROP NOT NULL;

INSERT INTO schema_version (version_number, comment) VALUES (77, 'Soft validation for event fields #405');

-- 2017-12-20 Drop not null constraints from events_history table #405
ALTER TABLE events_history ALTER COLUMN typeofplace DROP NOT NULL;
ALTER TABLE events_history ALTER COLUMN srcfirstname DROP NOT NULL;
ALTER TABLE events_history ALTER COLUMN srclastname DROP NOT NULL;
ALTER TABLE events_history ALTER COLUMN srctelno DROP NOT NULL;

INSERT INTO schema_version (version_number, comment) VALUES (78, 'Drop not null constraints from events_history table #405');

-- 2018-01-03 allow sormas user to dump the complete db and execute update script
ALTER TABLE schema_version OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (79, 'Give sormas_user access to schema_version table');

-- 2018-01-09 Cause of death fields #439
ALTER TABLE person ADD COLUMN causeofdeath varchar(255);
ALTER TABLE person ADD COLUMN causeofdeathdetails varchar(512);
ALTER TABLE person ADD COLUMN causeofdeathdisease varchar(255);
ALTER TABLE person ADD COLUMN causeofdeathdiseasedetails varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (80, 'Cause of death fields #439');

-- 2018-01-10 Case outcome #185
ALTER TABLE cases ADD COLUMN outcome varchar(255);
ALTER TABLE cases ADD COLUMN outcomedate timestamp without time zone;

UPDATE cases SET outcome = 'NO_OUTCOME';
UPDATE cases SET changedate = now();

INSERT INTO schema_version (version_number, comment) VALUES (81, 'Case outcome #185');

-- 2018-01-11 Remove unused case date fields #185
ALTER TABLE cases DROP COLUMN suspectdate;
ALTER TABLE cases DROP COLUMN confirmeddate;
ALTER TABLE cases DROP COLUMN negativedate;
ALTER TABLE cases DROP COLUMN nocasedate;
ALTER TABLE cases DROP COLUMN postivedate;
ALTER TABLE cases DROP COLUMN recovereddate;

INSERT INTO schema_version (version_number, comment) VALUES (82, 'Remove unused case date fields #185');

-- 2018-01-11 Cause of death fields #439
ALTER TABLE person DROP COLUMN causeofdeathdiseasedetails;

INSERT INTO schema_version (version_number, comment) VALUES (83, 'Cause of death fields #439');

-- 2018-01-12 New fields for CSM #474
ALTER TABLE hospitalization ADD COLUMN admittedtohealthfacility varchar(255);
ALTER TABLE symptoms ADD COLUMN bulgingfontanelle varchar(255);
ALTER TABLE cases ADD COLUMN vaccination varchar(255);
ALTER TABLE cases ADD COLUMN vaccinationdoses varchar(512);
ALTER TABLE cases ADD COLUMN vaccinationinfosource varchar(255);

UPDATE cases SET vaccination = measlesvaccination where measlesvaccination IS NOT NULL;
UPDATE cases SET vaccination = yellowfevervaccination where yellowfevervaccination IS NOT NULL;
UPDATE cases SET vaccinationdoses = measlesdoses where measlesdoses IS NOT NULL;
UPDATE cases SET vaccinationinfosource = measlesvaccinationinfosource where measlesvaccinationinfosource IS NOT NULL;
UPDATE cases SET vaccinationinfosource = yellowfevervaccinationinfosource where yellowfevervaccinationinfosource IS NOT NULL;

ALTER TABLE cases DROP COLUMN measlesvaccination;
ALTER TABLE cases DROP COLUMN measlesdoses;
ALTER TABLE cases DROP COLUMN measlesvaccinationinfosource;
ALTER TABLE cases DROP COLUMN yellowfevervaccination;
ALTER TABLE cases DROP COLUMN yellowfevervaccinationinfosource;

INSERT INTO schema_version (version_number, comment) VALUES (84, 'New fields for CSM #474');

-- 2018-01-16 Outbreak mode per disease and district #473
CREATE TABLE outbreak(
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	district_id bigint not null,
	disease varchar(255) not null,
	reportdate timestamp not null,
	reportinguser_id bigint not null,
	primary key(id)
);

ALTER TABLE outbreak OWNER TO sormas_user;
ALTER TABLE outbreak ADD CONSTRAINT fk_outbreak_district_id FOREIGN KEY (district_id) REFERENCES district(id);
ALTER TABLE outbreak ADD CONSTRAINT fk_outbreak_reportinguser_id FOREIGN KEY (reportinguser_id) REFERENCES users(id);

INSERT INTO schema_version (version_number, comment) VALUES (85, 'Outbreak mode per disease and district #473');

-- 2018-01-29 Outbreak history table #473

ALTER TABLE outbreak ADD COLUMN sys_period tstzrange;
UPDATE outbreak SET sys_period=tstzrange(creationdate, null);
ALTER TABLE outbreak ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE outbreak_history (LIKE outbreak);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON outbreak
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'outbreak_history', true);
ALTER TABLE outbreak_history OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (86, 'Outbreak history table #473');

-- 2018-02-08 Split contact classification into classification and status #454

ALTER TABLE contact ADD COLUMN contactstatus varchar(255);

UPDATE contact SET contactclassification = 'UNCONFIRMED' where contactclassification = 'POSSIBLE';
UPDATE contact SET contactstatus = 'DROPPED' where contactclassification = 'DROPPED';
UPDATE contact SET contactstatus = 'DROPPED' where contactclassification = 'NO_CONTACT';
UPDATE contact SET contactstatus = 'CONVERTED' where contactclassification = 'CONVERTED';
UPDATE contact SET contactstatus = 'ACTIVE' where contactclassification = 'UNCONFIRMED' or contactclassification = 'CONFIRMED';
UPDATE contact SET contactclassification = 'CONFIRMED' where contactclassification = 'CONVERTED' or contactclassification = 'DROPPED';

INSERT INTO schema_version (version_number, comment) VALUES (87, 'Split contact classification into classification and status #454');

-- 2018-02-08 Date of reception #438

ALTER TABLE cases ADD COLUMN receptiondate timestamp without time zone;

INSERT INTO schema_version (version_number, comment) VALUES (88, 'Date of reception #438');

-- 2018-02-08 Date of vaccination for all diseases #486

ALTER TABLE cases RENAME COLUMN smallpoxvaccinationdate TO vaccinationdate;

INSERT INTO schema_version (version_number, comment) VALUES (89, 'Date of vaccination for all diseases #486');

-- 2018-02-09 Monkeypox field changes #401

ALTER TABLE symptoms DROP COLUMN cutaneouseruption;
ALTER TABLE symptoms ADD COLUMN lesionsonsetdate timestamp;
ALTER TABLE symptoms_history DROP COLUMN cutaneouseruption;
ALTER TABLE symptoms_history ADD COLUMN lesionsonsetdate timestamp;

INSERT INTO schema_version (version_number, comment) VALUES (90, 'Monkeypox field changes #401');

-- 2018-02-09 History table updates

ALTER TABLE cases_history DROP COLUMN suspectdate;
ALTER TABLE cases_history DROP COLUMN confirmeddate;
ALTER TABLE cases_history DROP COLUMN negativedate;
ALTER TABLE cases_history DROP COLUMN nocasedate;
ALTER TABLE cases_history DROP COLUMN postivedate;
ALTER TABLE cases_history DROP COLUMN recovereddate;
ALTER TABLE symptoms_history ADD COLUMN bulgingfontanelle varchar(255);
ALTER TABLE cases_history ADD COLUMN vaccination varchar(255);
ALTER TABLE cases_history ADD COLUMN vaccinationdoses varchar(512);
ALTER TABLE cases_history ADD COLUMN vaccinationinfosource varchar(255);
ALTER TABLE cases_history DROP COLUMN measlesvaccination;
ALTER TABLE cases_history DROP COLUMN measlesdoses;
ALTER TABLE cases_history DROP COLUMN measlesvaccinationinfosource;
ALTER TABLE contact_history ADD COLUMN contactstatus varchar(255);
ALTER TABLE cases_history ADD COLUMN receptiondate timestamp without time zone;
ALTER TABLE cases_history RENAME COLUMN smallpoxvaccinationdate TO vaccinationdate;
ALTER TABLE cases_history ADD COLUMN outcome varchar(255);
ALTER TABLE cases_history ADD COLUMN outcomedate timestamp without time zone;
ALTER TABLE person_history ADD COLUMN causeofdeath varchar(255);
ALTER TABLE person_history ADD COLUMN causeofdeathdetails varchar(512);
ALTER TABLE person_history ADD COLUMN causeofdeathdisease varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (91, 'History table updates');

-- 2018-02-13 Test result filter under Samples Directory #482

ALTER TABLE samples ADD COLUMN mainsampletest_id bigint;
ALTER TABLE samples ADD CONSTRAINT fk_samples_mainsampletest_id FOREIGN KEY (mainsampletest_id) REFERENCES sampletest (id);
-- set to latest test, see https://www.periscopedata.com/blog/4-ways-to-join-only-the-first-row-in-sql
UPDATE samples SET mainsampletest_id=(SELECT DISTINCT ON (sample_id) id FROM sampletest WHERE sampletest.sample_id = samples.id ORDER BY sample_id, testdatetime DESC);
ALTER TABLE samples_history ADD COLUMN mainsampletest_id bigint;

INSERT INTO schema_version (version_number, comment) VALUES (92, 'Test result filter under Samples Directory #482');

-- 2018-02-23 Resulting case for contacts #402

ALTER TABLE contact ADD COLUMN resultingcase_id bigint;
ALTER TABLE contact ADD CONSTRAINT fk_contact_resultingcase_id FOREIGN KEY (resultingcase_id) REFERENCES cases (id);
ALTER TABLE contact_history ADD COLUMN resultingcase_id bigint;

INSERT INTO schema_version (version_number, comment) VALUES (93, 'Resulting case for contacts #402');

-- 2018-02-26 Export function for database export #507

CREATE FUNCTION export_database(table_name text, path text, file_name text)
	RETURNS VOID
	LANGUAGE plpgsql
	SECURITY DEFINER
	AS $BODY$
		BEGIN
			EXECUTE '
				COPY (SELECT * FROM 
					' || quote_ident(table_name) || '
				) TO 
					' || quote_literal(path || file_name) || '
				WITH (
					FORMAT CSV, DELIMITER '';'', HEADER
				);
			';
		END;
	$BODY$
;

CREATE FUNCTION export_database_join(table_name text, join_table_name text, column_name text, join_column_name text, path text, file_name text)
	RETURNS VOID
	LANGUAGE plpgsql
	SECURITY DEFINER
	AS $BODY$
		BEGIN
			EXECUTE '
				COPY (SELECT * FROM 
					' || quote_ident(table_name) || ' 
				INNER JOIN 
					' || quote_ident(join_table_name) || ' 
				ON 
					' || column_name || ' 
				= 
					' || join_column_name || ' 
				) TO 
					' || quote_literal(path || file_name) || ' 
				WITH (
					FORMAT CSV, DELIMITER '';'', HEADER
				);
			';
		END;
	$BODY$
;

INSERT INTO schema_version (version_number, comment) VALUES (94, 'Export function for database export #507');

-- 2018-03-01 Resulting case for event participant #402

ALTER TABLE eventparticipant ADD COLUMN resultingcase_id bigint;
ALTER TABLE eventparticipant ADD CONSTRAINT fk_eventparticipant_resultingcase_id FOREIGN KEY (resultingcase_id) REFERENCES cases (id);

INSERT INTO schema_version (version_number, comment) VALUES (95, 'Resulting case for eventparticipant #402');

-- 2018-03-05 SQL function to easily add new health facilities to the database #519

CREATE OR REPLACE FUNCTION add_health_facility(_name character varying(512), _islaboratory boolean, _regionname character varying(512), _districtname character varying(512), _communityname character varying(512), _city character varying(512), _latitude double precision, _longitude double precision)
RETURNS bigint AS $resultid$
DECLARE 
	_type character varying(512);
	_id bigint;
	_uuid character varying(36);
	_region_id bigint;
	_district_id bigint;
	_community_id bigint;
BEGIN
	IF (_regionname IS NULL) THEN
		RAISE EXCEPTION 'you need to pass a region name';
	END IF;
	IF (NOT(_islaboratory) AND _districtname IS NULL) THEN
		RAISE EXCEPTION 'you need to pass a district name';
	END IF;

	IF (_islaboratory) THEN
		_type = 'LABORATORY';
	END IF;

	SELECT region.id FROM region WHERE region.name = _regionname INTO _region_id;
	SELECT district.id FROM district WHERE district.name = _districtname AND district.region_id = _region_id INTO _district_id;
	SELECT community.id FROM community WHERE community.name = _communityname AND community.district_id = _district_id INTO _community_id;

	IF (_regionname IS NOT NULL AND _region_id IS NULL) THEN
		RAISE EXCEPTION 'region not found %', _regionname;
	END IF;
	IF (_districtname IS NOT NULL AND _district_id IS NULL) THEN
		RAISE EXCEPTION 'district not found %', _districtname;
	END IF;
	IF (_communityname IS NOT NULL AND _community_id IS NULL) THEN
		RAISE EXCEPTION 'community not found %', _communityname;
	END IF;

	_id = nextval('entity_seq');
	-- this is not the same format as we are using in code (which is base32 with 4 seperators)
	_uuid = upper(substring(md5(random()::text || clock_timestamp()::text)::uuid::text, 3, 29));

	IF ((SELECT facility.id FROM facility WHERE facility.name = _name AND facility.region_id = _region_id AND facility.district_id = _district_id) IS NOT NULL) THEN
		RAISE EXCEPTION 'facility % allready exists in district', _name;
	END IF;
	
	INSERT INTO facility(
            id, changedate, creationdate, name, publicownership, type, uuid, 
            region_id, district_id, community_id, city, latitude, longitude)
	VALUES (_id, now(), now(), _name, FALSE, _type, _uuid, 
            _region_id, _district_id, _community_id, _city, _latitude, _longitude);

        RETURN _id;
END;
$resultid$  LANGUAGE plpgsql;

INSERT INTO schema_version (version_number, comment) VALUES (96, 'SQL function to easily add new health facilities to the database #519');

-- 2018-03-05 Add upgrade column to schema_version for backend upgrade logic #402
ALTER TABLE schema_version ADD COLUMN upgradeNeeded boolean NOT NULL DEFAULT false;
UPDATE schema_version SET upgradeNeeded=true WHERE version_number=95;
GRANT SELECT, UPDATE ON TABLE schema_version TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (97, 'Add upgrade column to schema_version for backend upgrade logic #402');

-- 2018-03-06 Restrict outbreak mode to CSM for now
DELETE FROM outbreak WHERE NOT (disease = 'CSM');

INSERT INTO schema_version (version_number, comment) VALUES (98, 'Restrict outbreak mode to CSM for now #489');

-- 2018-03-06 Change export functions #507
DROP FUNCTION export_database(text, text, text);
DROP FUNCTION export_database_join(text, text, text, text, text, text);

CREATE FUNCTION export_database(table_name text, file_path text)
	RETURNS VOID
	LANGUAGE plpgsql
	SECURITY DEFINER
	AS $BODY$
		BEGIN
			EXECUTE '
				COPY (SELECT * FROM 
					' || quote_ident(table_name) || '
				) TO 
					' || quote_literal(file_path) || '
				WITH (
					FORMAT CSV, DELIMITER '';'', HEADER
				);
			';
		END;
	$BODY$
;

CREATE FUNCTION export_database_join(table_name text, join_table_name text, column_name text, join_column_name text, file_path text)
	RETURNS VOID
	LANGUAGE plpgsql
	SECURITY DEFINER
	AS $BODY$
		BEGIN
			EXECUTE '
				COPY (SELECT * FROM 
					' || quote_ident(table_name) || ' 
				INNER JOIN 
					' || quote_ident(join_table_name) || ' 
				ON 
					' || column_name || ' 
				= 
					' || join_column_name || ' 
				) TO 
					' || quote_literal(file_path) || ' 
				WITH (
					FORMAT CSV, DELIMITER '';'', HEADER
				);
			';
		END;
	$BODY$
;

INSERT INTO schema_version (version_number, comment) VALUES (99, 'Change export functions #507');

-- 2018-03-09 Create history table for event participant #509
ALTER TABLE eventparticipant ADD COLUMN sys_period tstzrange;
UPDATE eventparticipant SET sys_period=tstzrange(creationdate, null);
ALTER TABLE eventparticipant ALTER COLUMN sys_period SET NOT NULL;
CREATE TABLE eventparticipant_history (LIKE eventparticipant);
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON eventparticipant
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'eventparticipant_history', true);
ALTER TABLE eventparticipant_history OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (100, 'Create history table for event participant #509');

-- 2018-04-12 Remove cause of death disease from persons that died due to another cause
UPDATE person SET causeofdeathdisease = null WHERE causeofdeath != 'EPIDEMIC_DISEASE';

INSERT INTO schema_version (version_number, comment) VALUES (101, 'Remove cause of death disease from persons that died due to another cause');

-- 2018-05-07 Case age field #585
ALTER TABLE cases ADD COLUMN caseage integer;
ALTER TABLE cases_history ADD COLUMN caseage integer;

UPDATE cases SET caseage = p.approximateage FROM person p WHERE cases.person_id = p.id AND p.approximateage IS NOT NULL AND p.approximateagetype = 0;
UPDATE cases SET caseage = 0 FROM person p WHERE cases.person_id = p.id AND p.approximateage IS NOT NULL AND p.approximateagetype = 1;

INSERT INTO schema_version (version_number, comment) VALUES (102, 'Case age field #585');

-- 2018-05-12 epi week functions #541

-- week starts monday
-- 1st of january is always in week 1
CREATE OR REPLACE FUNCTION epi_week (indate timestamp)
RETURNS integer AS 
$$
DECLARE year integer;
	doy integer;
	doy_end integer;
	isodow_start_next integer;
	isodow_start integer;
	epi_week integer;
BEGIN
   year := date_part('year', indate);
   -- days until end of year
   -- DOY: The day of the year (1 - 365/366)
   doy = date_part('DOY', indate);
   doy_end := date_part('DOY', date (year || '-12-31'));
   -- week day of first day in next year
   -- isodow: The day of the week as Monday (1) to Sunday (7)
   isodow_start_next := date_part('isodow', date ((year+1) || '-01-01'));
   -- end of date year?
   -- DOY 31.12 - DOY < DOY(01.01.Y+1)
   if (doy_end - doy < isodow_start_next-1) THEN
	-- falls into next epi year
	epi_week := 1;
   ELSE
	-- 2018: 01.01 is monday -> ceil(doy/7) = epi-week
	-- 2017: 01.01 is sunday -> ceil((doy+6)/7) = epi-week
	-- 2016: 01.01 is friday -> ceil((doy+4)/7) = epi-week
	isodow_start := date_part('isodow', date (year || '-01-01'));
	epi_week := ceil((doy + isodow_start - 1) / 7.0);
   END if;
   
   RETURN epi_week;
END;
$$ 
LANGUAGE plpgsql;

-- see epi_week
CREATE OR REPLACE FUNCTION epi_year (indate timestamp)
RETURNS integer AS 
$$
DECLARE year integer;
	doy integer;
	doy_end integer;
	isodow_start_next integer;
	isodow_start integer;
	epi_year integer;
BEGIN
   year := date_part('year', indate);
   doy = date_part('DOY', indate);
   doy_end := date_part('DOY', date (year || '-12-31'));
   isodow_start_next := date_part('isodow', date ((year+1) || '-01-01'));
   if (doy_end - doy < isodow_start_next-1) THEN
	-- next year
	epi_year := year+1;
   ELSE
	epi_year := year;
   END if;
   
   RETURN epi_year;
END;
$$ 
LANGUAGE plpgsql;

-- e.g. SELECT epi_week('2015-12-27'), epi_year('2015-12-27'); -- 52-2015
-- e.g. SELECT epi_week('2015-12-28'), epi_year('2015-12-28'); -- 01-2016

INSERT INTO schema_version (version_number, comment) VALUES (103, 'Epi week functions #541');

-- 2018-05-28 Creating new event in app not working #614

ALTER TABLE events ALTER COLUMN typeofplace DROP NOT NULL;
ALTER TABLE events ALTER COLUMN srcfirstname DROP NOT NULL;
ALTER TABLE events ALTER COLUMN srclastname DROP NOT NULL;
ALTER TABLE events ALTER COLUMN srctelno DROP NOT NULL;
ALTER TABLE events_history ALTER COLUMN typeofplace DROP NOT NULL;
ALTER TABLE events_history ALTER COLUMN srcfirstname DROP NOT NULL;
ALTER TABLE events_history ALTER COLUMN srclastname DROP NOT NULL;
ALTER TABLE events_history ALTER COLUMN srctelno DROP NOT NULL;

INSERT INTO schema_version (version_number, comment) VALUES (104, 'Creating new event in app not working #614');

-- 2018-05-31 Case data changes for automatic case classification #628

ALTER TABLE cases ADD COLUMN denguefevertype character varying(255);
ALTER TABLE cases ADD COLUMN classificationcomment character varying(512);
ALTER TABLE cases ADD COLUMN classificationdate timestamp without time zone;
ALTER TABLE cases ADD COLUMN classificationuser_id bigint;
ALTER TABLE cases ADD CONSTRAINT fk_cases_classificationuser_id FOREIGN KEY (classificationuser_id) REFERENCES public.users (id);

UPDATE cases SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE cases_history SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE events SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE events_history SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE outbreak SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE outbreak_history SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE person SET causeofdeathdisease = 'NEW_INFLUENCA' where causeofdeathdisease = 'AVIAN_INFLUENCA';
UPDATE person_history SET causeofdeathdisease = 'NEW_INFLUENCA' where causeofdeathdisease = 'AVIAN_INFLUENCA';
UPDATE visit SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE visit_history SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';
UPDATE weeklyreportentry SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';

INSERT INTO schema_version (version_number, comment) VALUES (105, 'Case data changes for automatic case classification #628');-- 2018-05-31 Add description field for other health facility to previous hospitalizations and person occupations #549

-- 2018-06-01 Add description field for other health facility to previous hospitalizations and person occupations #549

ALTER TABLE previoushospitalization ADD COLUMN healthfacilitydetails varchar(512);
ALTER TABLE previoushospitalization_history ADD COLUMN healthfacilitydetails varchar(512);
ALTER TABLE person ADD COLUMN occupationfacilitydetails varchar(512);
ALTER TABLE person_history ADD COLUMN occupationfacilitydetails varchar(512);
ALTER TABLE person ADD COLUMN occupationregion_id bigint;
ALTER TABLE person ADD COLUMN occupationdistrict_id bigint;
ALTER TABLE person ADD COLUMN occupationcommunity_id bigint;
ALTER TABLE person_history ADD COLUMN occupationregion_id bigint;
ALTER TABLE person_history ADD COLUMN occupationdistrict_id bigint;
ALTER TABLE person_history ADD COLUMN occupationcommunity_id bigint;

ALTER TABLE person ADD CONSTRAINT fk_person_occupationregion_id FOREIGN KEY (occupationregion_id) REFERENCES region (id);
ALTER TABLE person ADD CONSTRAINT fk_person_occupationdistrict_id FOREIGN KEY (occupationdistrict_id) REFERENCES district (id);
ALTER TABLE person ADD CONSTRAINT fk_person_occupationcommunity_id FOREIGN KEY (occupationcommunity_id) REFERENCES community (id);

UPDATE person SET occupationregion_id = (SELECT region_id FROM facility WHERE facility.id = person.occupationfacility_id) WHERE occupationfacility_id IS NOT NULL;
UPDATE person SET occupationdistrict_id = (SELECT district_id FROM facility WHERE facility.id = person.occupationfacility_id) WHERE occupationfacility_id IS NOT NULL;
UPDATE person SET occupationcommunity_id = (SELECT community_id FROM facility WHERE facility.id = person.occupationfacility_id) WHERE occupationfacility_id IS NOT NULL;

-- 2018-06-01 Case symptoms changes for automatic case classification #631

ALTER TABLE symptoms ADD COLUMN meningealsigns varchar(255);
ALTER TABLE symptoms_history ADD COLUMN meningealsigns varchar(255);

INSERT INTO schema_version (version_number, comment) VALUES (107, 'Case symptoms changes for automatic case classification #631');

-- 2018-06-04 Add "Other laboratory" option for sample and sample test #440

ALTER TABLE samples ADD COLUMN labdetails varchar(512);
ALTER TABLE samples_history ADD COLUMN labdetails varchar(512);
ALTER TABLE sampletest ADD COLUMN labdetails varchar(512);
ALTER TABLE sampletest_history ADD COLUMN labdetails varchar(512);

INSERT INTO schema_version (version_number, comment) VALUES (108, 'Add "Other laboratory" option for sample and sample test #440');

-- 2018-06-05 Sample and sample test data model changes #627

UPDATE sampletest SET testtype = 'IGM_SERUM_ANTIBODY' WHERE testtype = 'SERUM_ANTIBODY_TITER';
UPDATE sampletest SET testtype = 'IGM_SERUM_ANTIBODY' WHERE testtype = 'ELISA';
UPDATE sampletest SET testtype = 'PCR_RT_PCR' WHERE testtype = 'PCR' OR testtype = 'RT_PCR';
UPDATE sampletest_history SET testtype = 'IGM_SERUM_ANTIBODY' WHERE testtype = 'SERUM_ANTIBODY_TITER';
UPDATE sampletest_history SET testtype = 'IGM_SERUM_ANTIBODY' WHERE testtype = 'ELISA';
UPDATE sampletest_history SET testtype = 'PCR_RT_PCR' WHERE testtype = 'PCR' OR testtype = 'RT_PCR';
UPDATE samples SET suggestedtypeoftest = 'IGM_SERUM_ANTIBODY' WHERE suggestedtypeoftest = 'SERUM_ANTIBODY_TITER';
UPDATE samples SET suggestedtypeoftest = 'IGM_SERUM_ANTIBODY' WHERE suggestedtypeoftest = 'ELISA';
UPDATE samples SET suggestedtypeoftest = 'PCR_RT_PCR' WHERE suggestedtypeoftest = 'PCR' OR suggestedtypeoftest = 'RT_PCR';
UPDATE samples_history SET suggestedtypeoftest = 'IGM_SERUM_ANTIBODY' WHERE suggestedtypeoftest = 'SERUM_ANTIBODY_TITER';
UPDATE samples_history SET suggestedtypeoftest = 'IGM_SERUM_ANTIBODY' WHERE suggestedtypeoftest = 'ELISA';
UPDATE samples_history SET suggestedtypeoftest = 'PCR_RT_PCR' WHERE suggestedtypeoftest = 'PCR' OR suggestedtypeoftest = 'RT_PCR';

INSERT INTO schema_version (version_number, comment) VALUES (109, 'Sample and sample test data model changes #627');

-- 2018-06-04 Case epi data changes for automatic case classification #632

ALTER TABLE epidata ADD COLUMN directcontactconfirmedcase varchar(255);
ALTER TABLE epidata ADD COLUMN directcontactprobablecase varchar(255);
ALTER TABLE epidata ADD COLUMN closecontactprobablecase varchar(255);
ALTER TABLE epidata ADD COLUMN areaconfirmedcases varchar(255);
ALTER TABLE epidata ADD COLUMN processingconfirmedcasefluidunsafe varchar(255);
ALTER TABLE epidata ADD COLUMN percutaneouscaseblood varchar(255);
ALTER TABLE epidata ADD COLUMN directcontactdeadunsafe varchar(255);
ALTER TABLE epidata ADD COLUMN processingsuspectedcasesampleunsafe varchar(255);
ALTER TABLE epidata ADD COLUMN areainfectedanimals varchar(255);
ALTER TABLE epidata RENAME COLUMN poultrysick TO sickdeadanimals;
ALTER TABLE epidata RENAME COLUMN poultrysickdetails TO sickdeadanimalsdetails;
ALTER TABLE epidata RENAME COLUMN poultrydate TO sickdeadanimalsdate;
ALTER TABLE epidata RENAME COLUMN poultrylocation TO sickdeadanimalslocation;
ALTER TABLE epidata ADD COLUMN eatingrawanimalsininfectedarea varchar(255);
ALTER TABLE epidata RENAME COLUMN poultryeat TO eatingrawanimals;
ALTER TABLE epidata ADD COLUMN eatingrawanimalsdetails varchar(512);
ALTER TABLE epidata DROP COLUMN poultry;
ALTER TABLE epidata DROP COLUMN poultrydetails;
ALTER TABLE epidata DROP COLUMN wildbirds;
ALTER TABLE epidata DROP COLUMN wildbirdsdetails;
ALTER TABLE epidata DROP COLUMN wildbirdsdate;
ALTER TABLE epidata DROP COLUMN wildbirdslocation;

ALTER TABLE epidata_history ADD COLUMN directcontactconfirmedcase varchar(255);
ALTER TABLE epidata_history ADD COLUMN directcontactprobablecase varchar(255);
ALTER TABLE epidata_history ADD COLUMN closecontactprobablecase varchar(255);
ALTER TABLE epidata_history ADD COLUMN areaconfirmedcases varchar(255);
ALTER TABLE epidata_history ADD COLUMN processingconfirmedcasefluidunsafe varchar(255);
ALTER TABLE epidata_history ADD COLUMN percutaneouscaseblood varchar(255);
ALTER TABLE epidata_history ADD COLUMN directcontactdeadunsafe varchar(255);
ALTER TABLE epidata_history ADD COLUMN processingsuspectedcasesampleunsafe varchar(255);
ALTER TABLE epidata_history ADD COLUMN areainfectedanimals varchar(255);
ALTER TABLE epidata_history RENAME COLUMN poultrysick TO sickdeadanimals;
ALTER TABLE epidata_history RENAME COLUMN poultrysickdetails TO sickdeadanimalsdetails;
ALTER TABLE epidata_history RENAME COLUMN poultrydate TO sickdeadanimalsdate;
ALTER TABLE epidata_history RENAME COLUMN poultrylocation TO sickdeadanimalslocation;
ALTER TABLE epidata_history ADD COLUMN eatingrawanimalsininfectedarea varchar(255);
ALTER TABLE epidata_history RENAME COLUMN poultryeat TO eatingrawanimals;
ALTER TABLE epidata_history ADD COLUMN eatingrawanimalsdetails varchar(512);
ALTER TABLE epidata_history DROP COLUMN poultry;
ALTER TABLE epidata_history DROP COLUMN poultrydetails;
ALTER TABLE epidata_history DROP COLUMN wildbirds;
ALTER TABLE epidata_history DROP COLUMN wildbirdsdetails;
ALTER TABLE epidata_history DROP COLUMN wildbirdsdate;
ALTER TABLE epidata_history DROP COLUMN wildbirdslocation;

INSERT INTO schema_version (version_number, comment) VALUES (110, 'Case epi data changes for automatic case classification #632');

-- 2018-06-04 Add "contact with source case" to epi data #629

ALTER TABLE contact ADD COLUMN resultingcaseuser_id bigint;
ALTER TABLE contact_history ADD COLUMN resultingcaseuser_id bigint;
ALTER TABLE contact ADD CONSTRAINT fk_contact_resultingcaseuser_id FOREIGN KEY (resultingcaseuser_id) REFERENCES public.users (id);

INSERT INTO schema_version (version_number, comment) VALUES (111, 'Add "contact with source case" to epi data #629');

-- 2018-06-05 Rename Rumor Manager user role to "Event Officer" #633

UPDATE userroles SET userrole = 'EVENT_OFFICER' WHERE userrole = 'RUMOR_MANAGER';
UPDATE userroles_history SET userrole = 'EVENT_OFFICER' WHERE userrole = 'RUMOR_MANAGER';

INSERT INTO schema_version (version_number, comment) VALUES (112, 'Rename Rumor Manager user role to "Event Officer" #633');

-- 2018-08-17 Automatic case classification for existing SORMAS diseases #61

ALTER TABLE cases ADD COLUMN systemcaseclassification character varying(255) DEFAULT 'NOT_CLASSIFIED' NOT NULL;
ALTER TABLE cases_history ADD COLUMN systemcaseclassification character varying(255);

INSERT INTO schema_version (version_number, comment) VALUES (113, 'System case classification column #61');

-- 2018-10-23 Archiving for cases and events #843

ALTER TABLE cases ADD COLUMN archived boolean NOT NULL DEFAULT false;
ALTER TABLE events ADD COLUMN archived boolean NOT NULL DEFAULT false;
ALTER TABLE cases_history ADD COLUMN archived boolean;
ALTER TABLE events_history ADD COLUMN archived boolean;

INSERT INTO schema_version (version_number, comment) VALUES (114, 'Archiving for cases and events #843');

-- 2018-11-16 Change dates with year 18 to 2018 #792

UPDATE cases SET receptiondate = receptiondate + interval '2000 years' WHERE EXTRACT(year FROM receptiondate) = 18;
UPDATE cases SET investigateddate = investigateddate + interval '2000 years' WHERE EXTRACT(year FROM investigateddate) = 18;
UPDATE symptoms SET onsetdate = onsetdate + interval '2000 years' WHERE EXTRACT(year FROM onsetdate) = 18;

INSERT INTO schema_version (version_number, comment) VALUES (115, 'Change dates with year 18 to 2018 #792');

-- 2018-12-03 Community Informant user role #872

UPDATE userroles SET userrole = REPLACE(userrole, 'INFORMANT', 'HOSPITAL_INFORMANT');
UPDATE userroles_history SET userrole = REPLACE(userrole, 'INFORMANT', 'HOSPITAL_INFORMANT');
ALTER TABLE users ADD COLUMN community_id bigint;
ALTER TABLE users ADD CONSTRAINT fk_users_community_id FOREIGN KEY (community_id) REFERENCES community (id);

INSERT INTO schema_version (version_number, comment) VALUES (116, 'Community informant user role #872');

-- 2018-12-03 Rename useroles to user_userroles #830

DROP TRIGGER versioning_trigger ON userroles;
ALTER TABLE userroles RENAME TO users_userroles;
ALTER TABLE userroles_history RENAME TO users_userroles_history;
CREATE TRIGGER versioning_trigger
BEFORE INSERT OR UPDATE OR DELETE ON users_userroles
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'users_userroles_history', true);

INSERT INTO schema_version (version_number, comment) VALUES (117, 'Rename useroles to users_userroles #830');

-- 2018-12-03 User role configuration #830

CREATE TABLE userrolesconfig (
	id bigint not null,
	uuid varchar(36) not null unique,
	changedate timestamp not null,
	creationdate timestamp not null,
	userrole varchar(255) not null unique,
	sys_period tstzrange NOT NULL,
	primary key(id)
);
ALTER TABLE userrolesconfig OWNER TO sormas_user;

CREATE TABLE userrolesconfig_history (LIKE userrolesconfig);
CREATE TRIGGER versioning_trigger BEFORE INSERT OR UPDATE OR DELETE ON userrolesconfig
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'userrolesconfig_history', true);
ALTER TABLE userrolesconfig_history OWNER TO sormas_user;

CREATE TABLE userroles_userrights (
    userrole character varying(255) NOT NULL,
    userright character varying(255) NOT NULL,
	sys_period tstzrange NOT NULL
);
ALTER TABLE userroles_userrights OWNER TO sormas_user;

ALTER TABLE ONLY userroles_userrights
    ADD CONSTRAINT unq_userroles_userrights_0 UNIQUE (userrole, userright);
ALTER TABLE ONLY userroles_userrights
    ADD CONSTRAINT fk_userroles_userrights_user_id FOREIGN KEY (userrole) REFERENCES userrolesconfig(userrole);

CREATE TABLE userroles_userrights_history (LIKE userroles_userrights);
CREATE TRIGGER versioning_trigger BEFORE INSERT OR UPDATE OR DELETE ON userroles_userrights
FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'userroles_userrights_history', true);
ALTER TABLE userroles_userrights_history OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (118, 'User role configuration #830');

ALTER TABLE userroles_userrights ADD COLUMN userrole_id bigint NOT NULL;
ALTER TABLE userroles_userrights DROP CONSTRAINT fk_userroles_userrights_user_id;
ALTER TABLE userroles_userrights DROP CONSTRAINT unq_userroles_userrights_0;
ALTER TABLE userroles_userrights DROP COLUMN userrole;
ALTER TABLE ONLY userroles_userrights
    ADD CONSTRAINT unq_userroles_userrights_0 UNIQUE (userrole_id, userright);
ALTER TABLE ONLY userroles_userrights
    ADD CONSTRAINT fk_userroles_userrights_userrole_id FOREIGN KEY (userrole_id) REFERENCES userrolesconfig(id);

DROP TABLE userroles_userrights_history;
CREATE TABLE userroles_userrights_history (LIKE userroles_userrights);
ALTER TABLE userroles_userrights_history OWNER TO sormas_user;

INSERT INTO schema_version (version_number, comment) VALUES (119, 'Fix for user role configuration #830');

-- 2018-12-04 Restructuring weekly reports #610

ALTER TABLE weeklyreport ALTER COLUMN healthfacility_id DROP NOT NULL;
ALTER TABLE weeklyreport RENAME informant_id TO reportinguser_id;
ALTER TABLE weeklyreport ADD COLUMN district_id bigint;
ALTER TABLE weeklyreport ADD CONSTRAINT fk_weeklyreport_district_id FOREIGN KEY (district_id) REFERENCES district (id);
ALTER TABLE weeklyreport ADD COLUMN community_id bigint;
ALTER TABLE weeklyreport ADD CONSTRAINT fk_weeklyreport_commuinty_id FOREIGN KEY (community_id) REFERENCES community (id);
ALTER TABLE weeklyreport ADD COLUMN assignedofficer_id bigint;
ALTER TABLE weeklyreport ADD CONSTRAINT fk_weeklyreport_assignedofficer_id FOREIGN KEY (assignedofficer_id) REFERENCES users (id);

ALTER TABLE users_history ADD COLUMN community_id bigint;
ALTER TABLE users_history ADD CONSTRAINT fk_users_community_id FOREIGN KEY (community_id) REFERENCES community (id);

INSERT INTO schema_version (version_number, comment) VALUES (120, 'Restructuring weekly reports #610');

-- 2018-12-10 Outbreak start & end #889

ALTER TABLE outbreak ADD COLUMN startdate timestamp;
UPDATE outbreak SET startdate=creationdate;
ALTER TABLE outbreak ALTER COLUMN startdate SET NOT NULL;
ALTER TABLE outbreak ADD COLUMN enddate timestamp;
ALTER TABLE outbreak_history ADD COLUMN startdate timestamp;
ALTER TABLE outbreak_history ADD COLUMN enddate timestamp;

INSERT INTO schema_version (version_number, comment) VALUES (121, 'Outbreak start & end #889');
