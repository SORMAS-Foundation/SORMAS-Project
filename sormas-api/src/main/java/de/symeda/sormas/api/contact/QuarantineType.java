package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum QuarantineType {

	INSTITUTIONELL,
	HOME,
	NONE,
	UNKNOWN,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
