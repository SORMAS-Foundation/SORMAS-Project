package de.symeda.sormas.api.person;

import de.symeda.sormas.api.I18nProperties;

public enum Sex {
	MALE,
	FEMALE
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
