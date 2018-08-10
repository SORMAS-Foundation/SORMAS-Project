package de.symeda.sormas.api.event;

import de.symeda.sormas.api.I18nProperties;

public enum TypeOfPlace {
	FESTIVITIES,
	HOME,
	HOSPITAL,
	MEANS_OF_TRANSPORT,
	PUBLIC_PLACE,
	UNKNOWN,
	OTHER;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
