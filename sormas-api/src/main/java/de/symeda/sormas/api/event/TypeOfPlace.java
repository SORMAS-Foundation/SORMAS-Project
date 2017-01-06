package de.symeda.sormas.api.event;

import de.symeda.sormas.api.I18nProperties;

public enum TypeOfPlace {
	HOME,
	UNKNOWN,
	PUBLIC_PLACE,
	HOSPITAL,
	FESTIVITY,
	MEANS_OF_TRANSPORT,
	OTHER;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
