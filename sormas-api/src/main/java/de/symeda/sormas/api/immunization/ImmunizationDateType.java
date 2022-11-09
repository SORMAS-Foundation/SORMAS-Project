package de.symeda.sormas.api.immunization;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ImmunizationDateType {

	REPORT_DATE,
	IMMUNIZATION_END,
	VALID_UNTIL,
	RECOVERY_DATE,
	LAST_VACCINATION_DATE,
	FIRST_VACCINATION_DATE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
