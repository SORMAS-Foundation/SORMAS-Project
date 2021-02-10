-- Deletes all cases that are marked as deleted = TRUE.
-- Deletes all contacts that are marked as deleted = TRUE.
-- Deletes all eventParticipants that are marked as deleted = TRUE.
-- All linked data will also be deleted, including contacts and event participants linked to the respective case.
-- All linked data the respective contacts will also be deleted.
-- A person entity will be deleted if no other entity references it.

BEGIN TRANSACTION;

CREATE TEMPORARY TABLE delete_cleanup
(
    table_name VARCHAR(255),
    id         BIGINT,
    CONSTRAINT delete_cleanup_pkey PRIMARY KEY (table_name, id)
);

CREATE FUNCTION pg_temp.trunc_register(table_name varchar, fk_id varchar, fk_table varchar) RETURNS void AS
$func$
BEGIN
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''' || table_name || ''' , id FROM ' || quote_ident(table_name) ||
            ' WHERE ' || quote_ident(fk_id) || ' IN (SELECT id FROM delete_cleanup WHERE table_name = ''' || fk_table ||
            '''))';
END
$func$ LANGUAGE plpgsql;

CREATE FUNCTION pg_temp.trunc_register(table_name varchar, id_column varchar, fk_id varchar,
                                       fk_table varchar) RETURNS void AS
$func$
BEGIN
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''' || table_name || ''' , ' || quote_ident(id_column) || ' FROM ' ||
            quote_ident(fk_table) || ' WHERE ' || quote_ident(id_column) || ' IS NOT NULL AND ' || quote_ident(fk_id) ||
            ' IN (SELECT id FROM delete_cleanup WHERE table_name = ''' || fk_table || '''))';
END
$func$ LANGUAGE plpgsql;

CREATE FUNCTION pg_temp.trunc_delete(table_name varchar, id_column varchar, id_table varchar) RETURNS void AS
$func$
BEGIN
    EXECUTE 'DELETE FROM ' || quote_ident(table_name) || ' WHERE ' || quote_ident(id_column) ||
            ' IN (SELECT id FROM delete_cleanup WHERE table_name = ''' || id_table || ''')';
END
$func$ LANGUAGE plpgsql;

CREATE FUNCTION pg_temp.trunc_delete(table_name varchar, id_table varchar) RETURNS void AS
$func$
DECLARE
    id_column varchar := id_table || '_id';
BEGIN
    PERFORM pg_temp.trunc_delete(table_name, id_column, id_table);
END
$func$ LANGUAGE plpgsql;

CREATE FUNCTION pg_temp.trunc_delete(table_name varchar) RETURNS void AS
$func$
BEGIN
    PERFORM pg_temp.trunc_delete(table_name, 'id', table_name);
END
$func$ LANGUAGE plpgsql;

CREATE FUNCTION pg_temp.truncate_rows() RETURNS void AS
$func$
BEGIN

    --## GATHER ENTRIES TO BE DELETED
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''cases'',id FROM cases WHERE deleted = true)';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''contact'',id FROM contact WHERE deleted = true)';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''eventparticipant'',id FROM eventparticipant WHERE deleted = true)';

    --### cases child entities
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''samples'', id FROM samples WHERE associatedcase_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''cases'') AND associatedcontact_id is NULL AND associatedeventparticipant_id is NULL)';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''person'', mC.person_id FROM cases mC WHERE mC.id IN (SELECT id FROM delete_cleanup WHERE table_name = ''cases'')
      and not exists (select 1 from cases c where c.person_id = mC.person_id and c.id <> mC.id)
      and not exists (select 1 from contact ct where ct.person_id = mC.person_id)
      and not exists (select 1 from eventparticipant ep LEFT JOIN events e ON ep.event_id = e.id where ep.person_id = mC.person_id))';

    PERFORM pg_temp.trunc_register('eventparticipant', 'resultingcase_id', 'cases');

    PERFORM pg_temp.trunc_register('visit', 'caze_id', 'cases');

    PERFORM pg_temp.trunc_register('maternalhistory', 'maternalhistory_id', 'id', 'cases');
    PERFORM pg_temp.trunc_register('porthealthinfo', 'porthealthinfo_id', 'id', 'cases');
    PERFORM pg_temp.trunc_register('hospitalization', 'hospitalization_id', 'id', 'cases');

    PERFORM pg_temp.trunc_register('epidata', 'epidata_id', 'id', 'cases');

    PERFORM pg_temp.trunc_register('symptoms', 'symptoms_id', 'id', 'cases');
    PERFORM pg_temp.trunc_register('therapy', 'therapy_id', 'id', 'cases');
    PERFORM pg_temp.trunc_register('clinicalcourse', 'clinicalcourse_id', 'id', 'cases');

    PERFORM pg_temp.trunc_register('contact', 'caze_id', 'cases');
    PERFORM pg_temp.trunc_register('task', 'caze_id', 'cases');
    PERFORM pg_temp.trunc_register('sormastosormasshareinfo', 'caze_id', 'cases');

    --### event participant child entities
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''samples'', id FROM samples WHERE associatedeventparticipant_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''eventparticipant'') AND associatedcontact_id is NULL AND associatedcase_id is NULL)';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''person'', mEp.person_id FROM eventparticipant mEp WHERE mEp.id IN (SELECT id FROM delete_cleanup WHERE table_name = ''eventparticipant'')
      and not exists (select 1 from cases c where c.person_id = mEp.person_id)
      and not exists (select 1 from contact ct where ct.person_id = mEp.person_id)
      and not exists (select 1 from eventparticipant ep LEFT JOIN events e ON ep.event_id = e.id where ep.person_id = person_id and ep.id <> mEp.id))';

    --### contact child entities
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''samples'', id FROM samples WHERE associatedcontact_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contact'') AND associatedeventparticipant_id is NULL AND associatedcase_id is NULL)';
    PERFORM pg_temp.trunc_register('healthconditions', 'healthconditions_id', 'id', 'contact');
    PERFORM pg_temp.trunc_register('epidata', 'epidata_id', 'id', 'contact');
    PERFORM pg_temp.trunc_register('task', 'contact_id', 'contact');
    PERFORM pg_temp.trunc_register('sormastosormasshareinfo', 'contact_id', 'contact');
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''contacts_visits'', contact_id FROM contacts_visits WHERE contact_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contact''))';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''visit'', visit_id FROM contacts_visits WHERE visit_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contacts_visits''))';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''person'', mC.person_id FROM contact mC WHERE mC.id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contact'')
      and not exists (select 1 from cases c where c.person_id = mC.person_id)
      and not exists (select 1 from contact ct where ct.person_id = mC.person_id and ct.id <> mC.id)
      and not exists (select 1 from eventparticipant ep LEFT JOIN events e ON ep.event_id = e.id where ep.person_id = mC.person_id))';

    --### visit child entities
    PERFORM pg_temp.trunc_register('symptoms', 'symptoms_id', 'id', 'visit');
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''contacts_visits'', visit_id FROM contacts_visits WHERE visit_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''visit''))';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''contact'', cv.contact_id FROM contacts_visits cv WHERE cv.contact_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contacts_visits'') AND not exists(SELECT id FROM delete_cleanup WHERE table_name = ''contact'' AND id = cv.contact_id))';

    --### samples child entities
    PERFORM pg_temp.trunc_register('pathogentest', 'sample_id', 'samples');
    PERFORM pg_temp.trunc_register('additionaltest', 'sample_id', 'samples');
    PERFORM pg_temp.trunc_register('sormastosormasshareinfo', 'sample_id', 'samples');

    --### epiData child entities
    PERFORM pg_temp.trunc_register('exposures', 'epidata_id', 'epidata');
    --### exposures child entities
    PERFORM pg_temp.trunc_register('location', 'location_id', 'id', 'exposures');

    --## DELETE ENTRIES
    PERFORM pg_temp.trunc_delete('exposures');
    PERFORM pg_temp.trunc_delete('location');
    PERFORM pg_temp.trunc_delete('sormastosormasshareinfo');
    PERFORM pg_temp.trunc_delete('additionaltest');
    PERFORM pg_temp.trunc_delete('pathogentest');
    PERFORM pg_temp.trunc_delete('contacts_visits', 'visit');
    PERFORM pg_temp.trunc_delete('contacts_visits', 'contact');
    PERFORM pg_temp.trunc_delete('task');
    PERFORM pg_temp.trunc_delete('samples');
    PERFORM pg_temp.trunc_delete('contact');
    PERFORM pg_temp.trunc_delete('visit');
    PERFORM pg_temp.trunc_delete('eventparticipant');
    PERFORM pg_temp.trunc_delete('cases');
    PERFORM pg_temp.trunc_delete('epidata');
    PERFORM pg_temp.trunc_delete('healthconditions');
    PERFORM pg_temp.trunc_delete('clinicalcourse');
    PERFORM pg_temp.trunc_delete('therapy');
    PERFORM pg_temp.trunc_delete('hospitalization');
    PERFORM pg_temp.trunc_delete('porthealthinfo');
    PERFORM pg_temp.trunc_delete('maternalhistory');
    PERFORM pg_temp.trunc_delete('symptoms');
    PERFORM pg_temp.trunc_delete('person');

END
$func$ LANGUAGE plpgsql;

SELECT pg_temp.truncate_rows();
SELECT table_name, count(*) AS delete_rows
FROM delete_cleanup
GROUP BY table_name
ORDER BY table_name;

-- Use rollback for a dry run to see how long the operation takes, if errors occur and what amount of data will be deleted.
ROLLBACK;

-- Use commit to really delete the data (cannot be undone).
-- COMMIT;

