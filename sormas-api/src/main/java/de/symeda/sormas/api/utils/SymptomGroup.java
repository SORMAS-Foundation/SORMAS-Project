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
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
