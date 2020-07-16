package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum TracingApp {

	CORONA_WARN_APP,
	OTHER,
	UNKNOWN;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
