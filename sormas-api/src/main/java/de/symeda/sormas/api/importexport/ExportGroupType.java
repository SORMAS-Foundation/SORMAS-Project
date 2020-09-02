package de.symeda.sormas.api.importexport;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExportGroupType {

	CORE,
	SENSITIVE,
	PERSON,
	HOSPITALIZATION,
	EPIDEMIOLOGICAL,
	VACCINATION,
	CASE_MANAGEMENT,
	ADDITIONAL;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
