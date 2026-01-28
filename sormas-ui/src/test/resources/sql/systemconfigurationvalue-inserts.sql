-- Migration script that is executed on startup of the tests extending AbstractBeanTest. Script is idempotent and can be reexecuted

CREATE SEQUENCE if not exists entity_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

DELETE
FROM systemconfigurationvalue;

INSERT INTO systemconfigurationcategory(id, uuid, changedate, creationdate, name, caption, description)
VALUES (1, RANDOM_UUID(), now(), now(), 'GENERAL_CATEGORY', 'i18n/General/categoryGeneral',
        'i18n/General/categoryGeneral')
ON CONFLICT DO NOTHING;


DELETE
FROM systemconfigurationvalue;


INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('COUNTRY_LOCALE', 'en', 1, true,
        '^[a-z]{2}(?:[-_][A-Z]{2})?$', false, null,
        'i18n/systemConfigurationValueValidation.COUNTRY_LOCALE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.COUNTRY_LOCALE', 'en')
ON CONFLICT DO NOTHING;

-- WARN: nigeria is added here instead of null, to avoid breaking tests
INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('COUNTRY_NAME', 'nigeria', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.COUNTRY_NAME', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.COUNTRY_NAME', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('COUNTRY_EPID_PREFIX', 'ng', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.COUNTRY_EPID_PREFIX', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.COUNTRY_EPID_PREFIX', 'ng')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('COUNTRY_CENTER_LATITUDE', '0', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.COUNTRY_CENTER_LATITUDE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.COUNTRY_CENTER_LATITUDE', '0')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('COUNTRY_CENTER_LONGITUDE', '0', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.COUNTRY_CENTER_LONGITUDE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.COUNTRY_CENTER_LONGITUDE', '0')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('MAP_USECOUNTRYCENTER', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.MAP_USECOUNTRYCENTER', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.MAP_USECOUNTRYCENTER', 'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('MAP_ZOOM', '1', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.MAP_ZOOM', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.MAP_ZOOM', '1')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('MAP_TILES_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.MAP_TILES_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.MAP_TILES_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('MAP_TILES_ATTRIBUTION', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.MAP_TILES_ATTRIBUTION', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.MAP_TILES_ATTRIBUTION', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CSV_SEPARATOR', ',', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.CSV_SEPARATOR', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CSV_SEPARATOR', ',')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('APP_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.APP_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.APP_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('APP_LEGACY_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.APP_LEGACY_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.APP_LEGACY_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('RSCRIPT_EXECUTABLE', null, 1, true,
        '^\/(?:[^\/\0]+\/)*[^\/\0]+$', false, null,
        'i18n/systemConfigurationValueValidation.RSCRIPT_EXECUTABLE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.RSCRIPT_EXECUTABLE', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('UI_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.UI_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.UI_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS_STATS_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.SORMAS_STATS_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS_STATS_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DOCUMENTS_PATH', 'target/tmp/documents', 1, true,
        '^\/(?:[^\/\0]+\/)+$', false, null,
        'i18n/systemConfigurationValueValidation.DOCUMENTS_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DOCUMENTS_PATH', '/opt/sormas/documents/')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('TEMP_PATH', 'target/tmp', 1, true,
        '^\/(?:[^\/\0]+\/)+$', false, null,
        'i18n/systemConfigurationValueValidation.TEMP_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.TEMP_PATH', '/opt/sormas/temp/')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('GENERATED_FILES_PATH', '/opt/sormas/generated/', 1, true,
        '^\/(?:[^\/\0]+\/)+$', false, null,
        'i18n/systemConfigurationValueValidation.GENERATED_FILES_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.GENERATED_FILES_PATH', '/opt/sormas/generated/')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CUSTOM_FILES_PATH', '/opt/sormas/custom/', 1, true,
        '^\/(?:[^\/\0]+\/)+$', false, null,
        'i18n/systemConfigurationValueValidation.CUSTOM_FILES_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CUSTOM_FILES_PATH', '/opt/sormas/custom/')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CREATE_DEFAULT_ENTITIES', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.CREATE_DEFAULT_ENTITIES', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CREATE_DEFAULT_ENTITIES', 'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SKIP_DEFAULT_PASSWORD_CHECK', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.SKIP_DEFAULT_PASSWORD_CHECK', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SKIP_DEFAULT_PASSWORD_CHECK', 'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DEV_MODE', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.DEV_MODE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DEV_MODE', 'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CASE_CLASSIFICATION_CALCULATION_MODE_OVERRIDE', '{"CHOLERA":"DISABLED","CORONAVIRUS":"MANUAL_AND_AUTOMATIC"}',
        1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.CASE_CLASSIFICATION_CALCULATION_MODE_OVERRIDE', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CASE_CLASSIFICATION_CALCULATION_MODE_OVERRIDE',
        '{"CHOLERA":"DISABLED","CORONAVIRUS":"MANUAL_AND_AUTOMATIC"}')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DEFAULT_CASE_CLASSIFICATION_CALCULATION_MODE', 'MANUAL_AND_AUTOMATIC', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.DEFAULT_CASE_CLASSIFICATION_CALCULATION_MODE', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DEFAULT_CASE_CLASSIFICATION_CALCULATION_MODE',
        'MANUAL_AND_AUTOMATIC')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('NEGATIVE_COVID_TESTS_MAX_AGE_DAYS', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.NEGATIVE_COVID_TESTS_MAX_AGE_DAYS', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.NEGATIVE_COVID_TESTS_MAX_AGE_DAYS', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DAYS_AFTER_SYSTEM_EVENT_GETS_DELETED', '90', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.DAYS_AFTER_SYSTEM_EVENT_GETS_DELETED', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DAYS_AFTER_SYSTEM_EVENT_GETS_DELETED', '90')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('NEGATIVE_COVID_TESTS_MAX_AGE_DAYS', null, 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.NEGATIVE_COVID_TESTS_MAX_AGE_DAYS', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.NEGATIVE_COVID_TESTS_MAX_AGE_DAYS', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('NAME_SIMILARITY_THRESHOLD', '0.65D', 1, true,
        '^[0-1](?:\.[0-9]{1,2})?D$', false, null,
        'i18n/systemConfigurationValueValidation.NAME_SIMILARITY_THRESHOLD', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.NAME_SIMILARITY_THRESHOLD', '0.65D')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DUPLICATE_CHECKS_EXCLUDE_PERSONS_ONLY_LINKED_TO_ARCHIVED_ENTRIES', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.DUPLICATE_CHECKS_EXCLUDE_PERSONS_ONLY_LINKED_TO_ARCHIVED_ENTRIES',
        now(), now(), nextval('entity_seq'),
        RANDOM_UUID(),
        'i18n/systemConfigurationValueDescription.DUPLICATE_CHECKS_EXCLUDE_PERSONS_ONLY_LINKED_TO_ARCHIVED_ENTRIES',
        'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DUPLICATECHECKS_NATIONAL_HEALTH_ID_OVERRIDES_CRITERIA', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.DUPLICATECHECKS_NATIONAL_HEALTH_ID_OVERRIDES_CRITERIA', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DUPLICATECHECKS_NATIONAL_HEALTH_ID_OVERRIDES_CRITERIA',
        'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INFRASTRUCTURE_SYNC_THRESHOLD', '1000', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.INFRASTRUCTURE_SYNC_THRESHOLD', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INFRASTRUCTURE_SYNC_THRESHOLD', '1000')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('STEP_SIZE_FOR_CSV_EXPORT', '5000', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.STEP_SIZE_FOR_CSV_EXPORT', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.STEP_SIZE_FOR_CSV_EXPORT', '5000')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DASHBOARD_MAP_MARKER_LIMIT', '-1', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.DASHBOARD_MAP_MARKER_LIMIT', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DASHBOARD_MAP_MARKER_LIMIT', '-1')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('AUDITOR_ATTRIBUTE_LOGGING', 'true', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.AUDITOR_ATTRIBUTE_LOGGING', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.AUDITOR_ATTRIBUTE_LOGGING', 'true')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('AUDIT_LOGGER_CONFIG', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.AUDIT_LOGGER_CONFIG', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.AUDIT_LOGGER_CONFIG', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('AUDIT_SOURCE_SITE', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.AUDIT_SOURCE_SITE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.AUDIT_SOURCE_SITE', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DOCGENERATION_NULL_REPLACEMENT', './.', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.DOCGENERATION_NULL_REPLACEMENT', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DOCGENERATION_NULL_REPLACEMENT', './.')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('DOCUMENT_UPLOAD_SIZE_LIMIT_MB', '20', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.DOCUMENT_UPLOAD_SIZE_LIMIT_MB', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.DOCUMENT_UPLOAD_SIZE_LIMIT_MB', '20')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('IMPORT_FILE_SIZE_LIMIT_MB', '20', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.IMPORT_FILE_SIZE_LIMIT_MB', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.IMPORT_FILE_SIZE_LIMIT_MB', '20')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('EMAIL_SENDER_ADDRESS', 'noreply@sormas.org', 1, true,
        '^[a-zA-Z0-9_!#$%&*+\/=?{|}~^.-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', false, null,
        'i18n/systemConfigurationValueValidation.EMAIL_SENDER_ADDRESS', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.EMAIL_SENDER_ADDRESS', 'noreply@sormas.org')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('EMAIL_SENDER_NAME', 'SORMAS Support', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.EMAIL_SENDER_NAME', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.EMAIL_SENDER_NAME', 'SORMAS Support')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SMS_SENDER_NAME', 'SORMAS', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SMS_SENDER_NAME', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SMS_SENDER_NAME', 'SORMAS')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SMS_AUTH_KEY', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SMS_AUTH_KEY', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SMS_AUTH_KEY', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SMS_AUTH_SECRET', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SMS_AUTH_SECRET', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SMS_AUTH_SECRET', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CUSTOM_BRANDING', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.CUSTOM_BRANDING', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CUSTOM_BRANDING', 'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CUSTOM_BRANDING_NAME', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.CUSTOM_BRANDING_NAME', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CUSTOM_BRANDING_NAME', 'SORMAS')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CUSTOM_BRANDING_LOGO_PATH', null, 1, true,
        '^(?:/[^/\0]+)+\.(?i:jpg|jpeg|png|gif|bmp|svg|webp)$', false, null,
        'i18n/systemConfigurationValueValidation.CUSTOM_BRANDING_LOGO_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CUSTOM_BRANDING_LOGO_PATH', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CUSTOM_BRANDING_USELOGINSIDEBAR', 'true', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.CUSTOM_BRANDING_USELOGINSIDEBAR', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CUSTOM_BRANDING_USELOGINSIDEBAR', 'true')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CUSTOM_BRANDING_LOGINBACKGROUND_PATH', null, 1, true,
        '^(?:/[^/\0]+)+\.(?i:jpg|jpeg|png|gif|bmp|svg|webp)$', false, null,
        'i18n/systemConfigurationValueValidation.CUSTOM_BRANDING_LOGINBACKGROUND_PATH', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CUSTOM_BRANDING_LOGINBACKGROUND_PATH', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('AUTHENTICATION_PROVIDER', 'SORMAS', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.AUTHENTICATION_PROVIDER', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.AUTHENTICATION_PROVIDER', 'SORMAS')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('AUTHENTICATION_PROVIDER_USER_SYNC_AT_STARTUP', 'false', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.AUTHENTICATION_PROVIDER_USER_SYNC_AT_STARTUP', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.AUTHENTICATION_PROVIDER_USER_SYNC_AT_STARTUP', 'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('AUTHENTICATION_PROVIDER_SYNCED_NEW_USER_ROLE', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.AUTHENTICATION_PROVIDER_SYNCED_NEW_USER_ROLE', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.AUTHENTICATION_PROVIDER_SYNCED_NEW_USER_ROLE', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('GEOCODING_SERVICE_URL_TEMPLATE', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.GEOCODING_SERVICE_URL_TEMPLATE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.GEOCODING_SERVICE_URL_TEMPLATE', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('GEOCODING_LONGITUDE_JSON_PATH', '$.features[0].geometry.coordinates[0]', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.GEOCODING_LONGITUDE_JSON_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.GEOCODING_LONGITUDE_JSON_PATH',
        '$.features[0].geometry.coordinates[0]')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('GEOCODING_LATITUDE_JSON_PATH', '$.features[0].geometry.coordinates[1]', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.GEOCODING_LATITUDE_JSON_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.GEOCODING_LATITUDE_JSON_PATH',
        '$.features[0].geometry.coordinates[1]')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('GEOCODING_EPSG4326_WKT',
        'Default: GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Long\",EAST],AXIS[\"Lat\",NORTH],AUTHORITY[\"EPSG\",\"4326\"]]',
        1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.GEOCODING_EPSG4326_WKT', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.GEOCODING_EPSG4326_WKT',
        'GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Long\",EAST],AXIS[\"Lat\",NORTH],AUTHORITY[\"EPSG\",\"4326\"]]')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CENTRAL_OIDC_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.CENTRAL_OIDC_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CENTRAL_OIDC_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CENTRAL_ETCD_HOST', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.CENTRAL_ETCD_HOST', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CENTRAL_ETCD_HOST', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CENTRAL_ETCD_CLIENT_NAME', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.CENTRAL_ETCD_CLIENT_NAME', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CENTRAL_ETCD_CLIENT_NAME', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CENTRAL_ETCD_CLIENT_PASSWORD', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.CENTRAL_ETCD_CLIENT_PASSWORD', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CENTRAL_ETCD_CLIENT_PASSWORD', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CENTRAL_ETCD_CA_PATH', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.CENTRAL_ETCD_CA_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CENTRAL_ETCD_CA_PATH', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('CENTRAL_LOCATION_SYNC', null, 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.CENTRAL_LOCATION_SYNC', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.CENTRAL_LOCATION_SYNC', 'false')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_PATH', '/opt/sormas/sormas2sormas/', 1, true,
        '^\/(?:[^\/\0]+\/)+$', false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_PATH', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_PATH', '/opt/sormas/sormas2sormas/')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_ID', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_ID', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_ID', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_KEYSTORE_NAME', '{host name}.sormas2sormas.keystore.p12', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_KEYSTORE_NAME', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_KEYSTORE_NAME',
        '{host name}.sormas2sormas.keystore.p12')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_KEYSTORE_PASS', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_KEYSTORE_PASS', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_KEYSTORE_PASS', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_ROOT_CA_ALIAS', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_ROOT_CA_ALIAS', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_ROOT_CA_ALIAS', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_TRUSTSTORE_NAME', 'sormas2sormas.truststore.p12', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_TRUSTSTORE_NAME', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_TRUSTSTORE_NAME',
        'sormas2sormas.truststore.p12')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_TRUSTSTORE_PASS', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_TRUSTSTORE_PASS', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_TRUSTSTORE_PASS', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_OIDC_REALM', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_OIDC_REALM', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_OIDC_REALM', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_OIDC_CLIENT_ID', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_OIDC_CLIENT_ID', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_OIDC_CLIENT_ID', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_OIDC_CLIENT_SECRET', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_OIDC_CLIENT_SECRET', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_OIDC_CLIENT_SECRET', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_ETCD_KEY_PREFIX', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_ETCD_KEY_PREFIX', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_ETCD_KEY_PREFIX', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS', 'true', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS', 'true')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_IGNORE_EXTERNAL_ID', 'true', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_IGNORE_EXTERNAL_ID', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_IGNORE_EXTERNAL_ID', 'true')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN', 'true', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN', 'true')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN', 'true', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN', 'true')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('SORMAS2SORMAS_DISTRICT_EXTERNAL_ID', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.SORMAS2SORMAS_DISTRICT_EXTERNAL_ID', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.SORMAS2SORMAS_DISTRICT_EXTERNAL_ID', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL', null, 1, true,
        '^(?:http[s]?:\/\/.)?(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$',
        false, null,
        'i18n/systemConfigurationValueValidation.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_VERSION_ENDPOINT', 'version', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_VERSION_ENDPOINT', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_VERSION_ENDPOINT',
        'version')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_SYMPTOM_JOURNAL_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_SYMPTOM_JOURNAL_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_SYMPTOM_JOURNAL_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_SYMPTOM_JOURNAL_AUTH_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_SYMPTOM_JOURNAL_AUTH_URL', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_SYMPTOM_JOURNAL_AUTH_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_SYMPTOM_JOURNAL_CLIENTID', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_SYMPTOM_JOURNAL_CLIENTID', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_SYMPTOM_JOURNAL_CLIENTID', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_SYMPTOM_JOURNAL_SECRET', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_SYMPTOM_JOURNAL_SECRET', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_SYMPTOM_JOURNAL_SECRET', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_USERNAME', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_USERNAME', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_USERNAME', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_PASSWORD', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_PASSWORD', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_PASSWORD', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_PROBANDS_URL', null, 1, true,
        '^https:\/\/(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$', false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_PROBANDS_URL', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_PROBANDS_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_AUTH_URL', null, 1, true,
        '(?:http[s]?:\/\/.)?(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)', false,
        null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_AUTH_URL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_AUTH_URL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_FRONTEND_AUTHURL', null, 1, true,
        '^(?:http[s]?:\/\/.)?(?:www\.)?[-a-zA-Z0-9@%._\+#=]{2,256}\.[a-z]{2,6}\b(?:[-a-zA-Z0-9@:%_\+.#?&\/\/=]*)$',
        false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_FRONTEND_AUTHURL', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_FRONTEND_AUTHURL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME_SECONDS', '21600', 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME_SECONDS', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME_SECONDS',
        '21600')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_EMAIL', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_EMAIL', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_EMAIL', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_PASSWORD', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_PASSWORD', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_PASSWORD', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_DEFAULTUSER_USERNAME', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_DEFAULTUSER_USERNAME', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_DEFAULTUSER_USERNAME', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_DEFAULTUSER_PASSWORD', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_DEFAULTUSER_PASSWORD', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_DEFAULTUSER_PASSWORD', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT', 'true', 1, true,
        '^(?i:true|false)$', false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT', 'true')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('INTERFACE_EXTERNAL_MESSAGE_ADAPTER_JNDI_NAME', null, 1, true,
        null, false, null,
        'i18n/systemConfigurationValueValidation.INTERFACE_EXTERNAL_MESSAGE_ADAPTER_JNDI_NAME', now(), now(),
        nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.INTERFACE_EXTERNAL_MESSAGE_ADAPTER_JNDI_NAME', null)
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('ALLOWED_FILE_EXTENSIONS',
        '.pdf,.txt,.doc,.docx,.odt,.xls,.xlsx,.ods,.ppt,.pptx,.odp,.jpg,.jpeg,.png,.gif,.msg,.html', 1, true,
        '^(?:\.[a-zA-Z0-9]+)(?:,\.[a-zA-Z0-9]+)*$', false, null,
        'i18n/systemConfigurationValueValidation.ALLOWED_FILE_EXTENSIONS', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.ALLOWED_FILE_EXTENSIONS',
        '.pdf,.txt,.doc,.docx,.odt,.xls,.xlsx,.ods,.ppt,.pptx,.odp,.jpg,.jpeg,.png,.gif,.msg,.html')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('MINIMUM_EMANCIPATED_AGE', '14', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.MINIMUM_EMANCIPATED_AGE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.MINIMUM_EMANCIPATED_AGE', '14')
ON CONFLICT DO NOTHING;

INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('MINIMUM_ADULT_AGE', '18', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.MINIMUM_ADULT_AGE', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.MINIMUM_ADULT_AGE', '18')
ON CONFLICT DO NOTHING;


INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid, value_description, default_value)
VALUES ('USE_DETERMINED_VACCINATION_STATUS', 'false', 1, true,
        '^(\d)+$', false, null,
        'i18n/systemConfigurationValueValidation.USE_DETERMINED_VACCINATION_STATUS', now(), now(), nextval('entity_seq'),
        RANDOM_UUID(), 'i18n/systemConfigurationValueDescription.USE_DETERMINED_VACCINATION_STATUS', 'false')
ON CONFLICT DO NOTHING;

