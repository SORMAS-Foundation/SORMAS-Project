package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CaseOrigin {

	IN_COUNTRY,
	POINT_OF_ENTRY;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
