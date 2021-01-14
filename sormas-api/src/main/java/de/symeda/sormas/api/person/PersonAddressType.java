package de.symeda.sormas.api.person;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum PersonAddressType {

	HOME,
	PLACE_OF_RESIDENCE,
	PLACE_OF_EXPOSURE,
	PLACE_OF_WORK,
	PLACE_OF_ISOLATION,
	EVENT_LOCATION,
	OTHER_ADDRESS;

	public static PersonAddressType[] getValues(String countryCode) {
		if (!CountryHelper.COUNTRY_CODE_SWITZERLAND.equals(countryCode)) {
			return new PersonAddressType[] {
				HOME,
				PLACE_OF_WORK,
				PLACE_OF_ISOLATION,
				OTHER_ADDRESS };
		}

		return values();
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
