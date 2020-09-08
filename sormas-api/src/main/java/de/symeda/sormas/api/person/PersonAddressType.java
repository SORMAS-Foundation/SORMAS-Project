package de.symeda.sormas.api.person;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum PersonAddressType {

	HOME,
	PLACE_OF_RESIDENCE,
	PLACE_OF_EXPOSURE,
	PLACE_OF_WORK,
	EVENT_LOCATION,
	OTHER_ADDRESS;

	public static PersonAddressType[] getValues() {

		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(ConfigFacade.COUNTRY_CODE_SWITZERLAND)) {
			return new PersonAddressType[] {
				HOME,
				PLACE_OF_WORK,
				OTHER_ADDRESS };
		}

		return values();
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
