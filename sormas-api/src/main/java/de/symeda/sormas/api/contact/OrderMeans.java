package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum OrderMeans {

	VERBALLY, OFFICIAL_DOCUMENT;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
