package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

public enum YesNoUnknownHoriz {
	
	YES,
	NO,
	UNKNOWN;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};

}
