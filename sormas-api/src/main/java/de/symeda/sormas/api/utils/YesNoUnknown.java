package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.I18nProperties;

public enum YesNoUnknown {
	
	YES,
	NO,
	UNKNOWN;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
