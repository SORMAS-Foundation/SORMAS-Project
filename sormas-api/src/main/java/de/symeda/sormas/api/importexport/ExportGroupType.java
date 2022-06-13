package de.symeda.sormas.api.importexport;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExportGroupType {

	CORE,
	SENSITIVE,
	PERSON,
	HOSPITALIZATION,
	EPIDEMIOLOGICAL,
	VACCINATION,
	CLINICAL_COURSE,
	THERAPY,
	FOLLOW_UP,
	ADDITIONAL,
	LOCATION,
	EVENT,
	EVENT_GROUP,
	EVENT_SOURCE;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
