package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ContactDateType {

	REPORT_DATE,
	LAST_CONTACT_DATE;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
