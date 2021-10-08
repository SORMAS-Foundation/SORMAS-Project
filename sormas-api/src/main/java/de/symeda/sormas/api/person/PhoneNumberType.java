package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PhoneNumberType {

	LANDLINE,
	MOBILE,
	WORK,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
