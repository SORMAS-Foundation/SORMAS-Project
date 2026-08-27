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
