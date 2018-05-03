package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.I18nProperties;

public enum YesNoUnknown {
	
	YES,
	NO,
	UNKNOWN;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static YesNoUnknown valueOf(Boolean value) {
		if (value == null) {
			return null;
		} else if (Boolean.TRUE.equals(value)) {
			return YES;
		} else {
			return NO;
		}
	}
}
