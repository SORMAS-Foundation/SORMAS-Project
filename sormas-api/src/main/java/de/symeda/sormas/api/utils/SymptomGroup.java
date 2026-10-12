package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum SymptomGroup {

	GENERAL,
	RESPIRATORY,
	CARDIOVASCULAR,
	GASTROINTESTINAL,
	URINARY,
	NERVOUS_SYSTEM,
	SKIN,
	OTHER,
	CLINICAL_ASSESSMENT_AND_COURSE,
	CLINICAL_MEASUREMENTS,
	CLINICAL_PRESENTATION,
	ENT,
	EYE,
	HAEMATOLOGICAL,
	METABOLIC,
	MUSCULOSKELETAL,
	LOCALISATION, // LUX TB
	STAGE,
	GENITAL,
	SYSTEM_DISORDER, // LUX DIPHTHERIA - COMPLICATION DIAGNOSIS
	SITE_OF_INFECTION, // Gonococcal infection
	CONCURRENT_STI; // Gonococcal infection

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
