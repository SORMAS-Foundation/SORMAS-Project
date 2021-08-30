package de.symeda.sormas.api.infrastructure.area;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum AreaType {

	URBAN,
	RURAL,
	UNKNOWN;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
