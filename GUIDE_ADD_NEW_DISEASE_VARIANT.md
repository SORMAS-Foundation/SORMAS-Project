# How to add a new disease variant?

This guide explains how to add a new disease variant to SORMAS.

## I. Preparation of the data

First step is to retrieve the identifiers of the diseases.
An exhaustive list can be found inside the enum ```de.symeda.sormas.api.Disease```.

## II. Persist the data

1. Get into pgAdmin or into the postgreSQL shell to be able to execute the following queries into the PostgreSQL database.
2. Replace the variable ```{{ DISEASE ID }}``` in the following query with the Disease ID taken from the Java enum indicated in step I.
3. Replace the variable ```{{ VARIANT NAME }}``` in the following query with the name of the disease variant.
4. Replace the ```[...]``` by all other rows you need to add.
5. Execute the generated query.
6. Let's verify that the table ```diseasevariant``` is well completed with your data.

Here is the SQL query to execute:

```sql
INSERT INTO diseasevariant (id, uuid, creationdate, changedate, disease, name)
VALUES
  (nextval('entity_seq'), gen_random_uuid(), now(), now(), '{{ DISEASE ID }}', '{{ VARIANT NAME }}'),
  [...];
```

Here is an example of the query with sample data:

```sql
INSERT INTO diseasevariant (id, uuid, creationdate, changedate, disease, name)
VALUES
  (nextval('entity_seq'), gen_random_uuid(), now(), now(), 'YELLOW_FEVER', 'Yellow Fever Variant 1'),
  (nextval('entity_seq'), gen_random_uuid(), now(), now(), 'YELLOW_FEVER', 'Yellow Fever Variant 2'),
  (nextval('entity_seq'), gen_random_uuid(), now(), now(), 'DENGUE', 'Dengue Variant 1'),
  (nextval('entity_seq'), gen_random_uuid(), now(), now(), 'MALARIA', 'Malaria Variant 1'),
  (nextval('entity_seq'), gen_random_uuid(), now(), now(), 'MALARIA', 'Malaria Variant 2');
```

In case PostgreSQL complains about unknown function ```gen_random_uuid()```, it means the ```pgcrypto``` extension is not enabled.
To enable it, please login with a superadmin account on your PostgreSQL server and execute the following query:

```sql
create extension pgcrypto;
```

That's it. The forms in the web application and Android app will now have their ```Disease variant``` fields filled.
