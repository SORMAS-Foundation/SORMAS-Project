DROP TABLE IF EXISTS delete_cleanup;
CREATE TEMPORARY TABLE delete_cleanup
(
    table_name VARCHAR(255),
    id         BIGINT,
    CONSTRAINT delete_cleanup_pkey PRIMARY KEY (table_name, id)
);

DROP FUNCTION IF EXISTS pg_temp.trunc_register(varchar, varchar, varchar);
CREATE FUNCTION pg_temp.trunc_register(table_name varchar, fk_id varchar, fk_table varchar) RETURNS void AS
$func$
BEGIN
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''' || table_name || ''' , id FROM ' || quote_ident(table_name) ||
            ' WHERE ' || quote_ident(fk_id) || ' IN (SELECT id FROM delete_cleanup WHERE table_name = ''' || fk_table ||
            '''))';
END
$func$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS pg_temp.trunc_register(varchar, varchar, varchar, varchar);
CREATE FUNCTION pg_temp.trunc_register(table_name varchar, id_column varchar, fk_id varchar,
                                       fk_table varchar) RETURNS void AS
$func$
BEGIN
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''' || table_name || ''' , ' || quote_ident(id_column) || ' FROM ' ||
            quote_ident(fk_table) || ' WHERE ' || quote_ident(id_column) || ' IS NOT NULL AND ' || quote_ident(fk_id) ||
            ' IN (SELECT id FROM delete_cleanup WHERE table_name = ''' || fk_table || '''))';
END
$func$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS pg_temp.trunc_delete(varchar, varchar, varchar);
CREATE FUNCTION pg_temp.trunc_delete(table_name varchar, id_column varchar, id_table varchar) RETURNS void AS
$func$
BEGIN
    EXECUTE 'DELETE FROM ' || quote_ident(table_name) || ' WHERE ' || quote_ident(id_column) ||
            ' IN (SELECT id FROM delete_cleanup WHERE table_name = ''' || id_table || ''')';
END
$func$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS pg_temp.trunc_delete(varchar, varchar);
CREATE FUNCTION pg_temp.trunc_delete(table_name varchar, id_table varchar) RETURNS void AS
$func$
DECLARE
    id_column varchar := id_table || '_id';
BEGIN
    PERFORM pg_temp.trunc_delete(table_name, id_column, id_table);
END
$func$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS pg_temp.trunc_delete(varchar);
CREATE FUNCTION pg_temp.trunc_delete(table_name varchar) RETURNS void AS
$func$
BEGIN
    PERFORM pg_temp.trunc_delete(table_name, 'id', table_name);
END
$func$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS pg_temp.truncate_rows();
CREATE FUNCTION pg_temp.truncate_rows() RETURNS void AS
$func$
BEGIN
    --## GATHER ENTRIES TO BE DELETED
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''contact'',id FROM contact WHERE deleted = true)';

    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''samples'', id FROM samples WHERE associatedcontact_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contact'') AND associatedeventparticipant_id is NULL AND associatedcase_id is NULL)';
    PERFORM pg_temp.trunc_register('healthconditions', 'healthconditions_id', 'id', 'contact');
    PERFORM pg_temp.trunc_register('epidata', 'epidata_id', 'id', 'contact');
    PERFORM pg_temp.trunc_register('task', 'contact_id', 'contact');
    PERFORM pg_temp.trunc_register('sormastosormasshareinfo', 'contact_id', 'contact');
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''contacts_visits'', contact_id FROM contacts_visits WHERE contact_id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contact''))';
    EXECUTE 'INSERT INTO delete_cleanup (SELECT ''person'', mC.person_id FROM contact mC WHERE mC.id IN (SELECT id FROM delete_cleanup WHERE table_name = ''contact'')
      and not exists (select 1 from cases c where c.person_id = mC.person_id)
      and not exists (select 1 from contact ct where ct.person_id = mC.person_id and ct.id <> mC.id)
      and not exists (select 1 from eventparticipant ep LEFT JOIN events e ON ep.event_id = e.id where ep.person_id = mC.person_id))';

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
    PERFORM pg_temp.trunc_delete('contacts_visits', 'contact');
    PERFORM pg_temp.trunc_delete('task');
    PERFORM pg_temp.trunc_delete('samples');
    PERFORM pg_temp.trunc_delete('contact');
    PERFORM pg_temp.trunc_delete('epidata');
    PERFORM pg_temp.trunc_delete('healthconditions');
    PERFORM pg_temp.trunc_delete('person');

END
$func$ LANGUAGE plpgsql;

SELECT pg_temp.truncate_rows();
SELECT table_name, count(*) AS delete_rows
FROM delete_cleanup
GROUP BY table_name
ORDER BY table_name;
