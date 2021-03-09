package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ScreeningType {

	ON_HOSPITAL_ADMISSION,
	ON_CARE_HOME_ADMISSION,
	ON_ASYLUM_ADMISSION,
	ON_ENTRY_FROM_RISK_AREA,
	HEALTH_SECTOR_EMPLOYEE,
	EDUCATIONAL_INSTITUTIONS,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
