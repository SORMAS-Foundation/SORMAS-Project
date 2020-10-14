package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.HideForCountriesExcept;

public enum QuarantineType {

	HOME,
	INSTITUTIONELL,
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	HOSPITAL,
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	HOTEL,
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	ASYLUM_ACCOMMODATION,
	NONE,
	UNKNOWN,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
