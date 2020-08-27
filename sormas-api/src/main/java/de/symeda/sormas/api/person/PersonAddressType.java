package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PersonAddressType {

	HOME,
	PLACE_OF_RESIDENCE,
	PLACE_OF_EXPOSURE,
	PLACE_OF_WORK,
	EVENT_LOCATION,
	OTHER_ADDRESS;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
