# Rubella Luxembourg admin configuration

Perform these steps against Luxembourg's SORMAS server admin UI (Configuration > Disease Configuration) before any manual QA on this branch's other changes.

## Disease.RUBELLA (#14288)

- Set `caseSurveillanceEnabled` to true.
- Set `incubationPeriodEnabled` to true.
- Set `minIncubationPeriod` to 12.
- Set `maxIncubationPeriod` to 23.
- Decide and record whether `aggregateReportingEnabled` stays on alongside case surveillance - the two do not conflict.

## Feature toggle (#14289)

- Confirm `FeatureType.SURVEILLANCE_REPORTS` is enabled on Luxembourg's server. If it is already on, no action needed.

## What does not need any admin action (#14289)

- Rubella and Congenital Rubella already show as two separate entries in case lists, pickers, and dashboards.
- New cases already default to case classification "Not yet classified" for every disease.
- The classification info button activates automatically once Task 7 (#14296) registers a Rubella/CRS ruleset.

## Exposure and travel history (#14292)

- Set `DiseaseConfigurationDto.exposureCategories` for Rubella to Airborne and Mother-to-child transmission.
- Confirm the "Hospital" caption on `ClusterType.NOSOCOMIAL` reads correctly for Luxembourg's ECDC report - no enum change needed, caption only.
- Confirm in the running UI that the four Activities-as-a-case TYPE OF PLACE values (School, Education and childcare, Nursing home, Asylum seekers shelter) are available for Rubella cases - already true for Luxembourg, no code change.
- "Place of residence of patient at time of disease onset" uses the existing free-text `residenceAtOnset` field - confirmed 2026-08-27, no country dropdown needed. No admin action, recorded here for reference only.
