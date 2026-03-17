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

INSERT INTO schema_version (version_number, comment)
VALUES (609, '#13832 - External Survey facade');