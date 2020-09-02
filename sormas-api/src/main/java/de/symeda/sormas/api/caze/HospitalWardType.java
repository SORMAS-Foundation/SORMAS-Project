package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum HospitalWardType {

	PEDIATRIC_INPATIENT,
	NURSERY,
	EPU,
	CHER,
	OPD,
	EYE,
	ENT,
	CARDIOLOGY,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
