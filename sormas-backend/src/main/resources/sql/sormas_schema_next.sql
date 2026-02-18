-- TODO: meant to be merged into sormas_schema.sql - used to avoid conflicts with development branch

-- Add fields used for
ALTER TABLE surveys
    ADD COLUMN external_survey_id TEXT;
ALTER TABLE surveytokens
    ADD COLUMN external_respondent_id TEXT;

INSERT INTO schema_version (version_number, comment)
VALUES (609, '#13832 - External Survey facade');