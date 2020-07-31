package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ContactIdentificationSource {

	CASE_PERSON,
	CONTACT_PERSON,
	TRACING_APP,
	OTHER,
	UNKNOWN;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
