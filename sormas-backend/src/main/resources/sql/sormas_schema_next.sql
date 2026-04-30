-- TODO: meant to be merged into sormas_schema.sql - used to avoid conflicts with development branch

-- Surveys
ALTER TABLE surveys
    ADD COLUMN external_survey_id TEXT;

-- Survey tokens
ALTER TABLE surveytokens
    ADD COLUMN external_respondent_id TEXT;

-- Surveys
ALTER TABLE symptoms
    ADD COLUMN IF NOT EXISTS lossOfAppetite TEXT;
ALTER TABLE symptoms
    ADD COLUMN IF NOT EXISTS flatulence TEXT;
ALTER TABLE symptoms
    ADD COLUMN IF NOT EXISTS smellyBurps TEXT;
ALTER TABLE symptoms
    ADD COLUMN IF NOT EXISTS coughingAttacks TEXT;
ALTER TABLE symptoms
    ADD COLUMN IF NOT EXISTS coughingAtNight TEXT;
ALTER TABLE symptoms
    ADD COLUMN IF NOT EXISTS abdominalCramps TEXT;

-- ExternalMessage
ALTER TABLE externalmessage
    ADD COLUMN IF NOT EXISTS additionalDataType TEXT;
ALTER TABLE externalmessage
    ADD COLUMN IF NOT EXISTS additionalDataJson JSONB;


-- system configuration for surveys

DO
$$ DECLARE
general_category_id bigint;

BEGIN
-- Get GENERAL category id
-- General category should always exist
SELECT id
INTO general_category_id
FROM systemconfigurationcategory
WHERE name = 'GENERAL_CATEGORY';

INSERT INTO systemconfigurationvalue(config_key, config_value, value_description, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid)
VALUES ('NG_SUVEY_BASE_URI', null, 'i18n/infoSystemConfigurationValueDescriptionNgSurveyBaseURI', general_category_id, true,
        '', false, null,
        'i18n/systemConfigurationValueInvalidValue', now(), now(), nextval('entity_seq'), generate_base32_uuid());


INSERT INTO systemconfigurationvalue(config_key, config_value, value_description, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid)
VALUES ('NG_SUVEY_CRYPTED_TOKEN', null, 'i18n/infoSystemConfigurationValueDescriptionNgSurveyCryptedToken', general_category_id, true,
        '', true, null,
        'i18n/systemConfigurationValueInvalidValue', now(), now(), nextval('entity_seq'), generate_base32_uuid());

INSERT INTO systemconfigurationvalue(config_key, config_value, value_description, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid)
VALUES ('NG_SUVEY_FIELD_PREFIX', '_so', 'i18n/infoSystemConfigurationValueDescriptionNgSurveyFieldPrefix', general_category_id, true,
        '', true, null,
        'i18n/systemConfigurationValueInvalidValue', now(), now(), nextval('entity_seq'), generate_base32_uuid());

INSERT INTO systemconfigurationvalue(config_key, config_value, value_description, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid)
VALUES ('SURVEY_AS_EXTERNAL_MESSAGE_ADAPTER_JNDI_KEY', 'java:global/sormas-esante-adapter/SurveyExternalMessageAdapterFacadeEjb', 'i18n/infoSystemConfigurationValueDescriptionSurveyJDNI', general_category_id, true,
        '', true, null,
        'i18n/systemConfigurationValueInvalidValue', now(), now(), nextval('entity_seq'), generate_base32_uuid());



INSERT INTO systemconfigurationvalue(config_key, config_value, value_description, category_id, value_optional, value_pattern,
                                     value_encrypt, data_provider, validation_message, changedate, creationdate, id,
                                     uuid)
VALUES ('SURVEY_PERIOD_INTERVAL_DAYS', '4', 'i18n/infoSystemConfigurationValueDescriptionSurveyPeriodIntervalDays', general_category_id, true,
        '', true, null,
        'i18n/systemConfigurationValueInvalidValue', now(), now(), nextval('entity_seq'), generate_base32_uuid());



END $$
LANGUAGE plpgsql;

-- index to avoid full table scan when checking for survey duplicates
CREATE INDEX idx_externalmessage_report_id
    ON externalmessage (reportid);

INSERT INTO schema_version (version_number, comment)
VALUES (609, '#13832 - External Survey facade');