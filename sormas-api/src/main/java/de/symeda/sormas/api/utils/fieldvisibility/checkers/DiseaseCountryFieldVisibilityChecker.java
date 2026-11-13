package de.symeda.sormas.api.utils.fieldvisibility.checkers;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.HideForDiseasesInCountries;
import de.symeda.sormas.api.utils.HideForDiseasesInCountriesExcept;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class DiseaseCountryFieldVisibilityChecker implements FieldVisibilityCheckers.FieldBasedChecker {

	private final Disease disease;
	private final String countryLocale;

	public DiseaseCountryFieldVisibilityChecker(Disease disease, String countryLocale) {
		this.disease = disease;
		this.countryLocale = countryLocale;
	}

	@Override
	public boolean isVisible(AccessibleObject field) {
		HideForDiseasesInCountries hideIn = field.getAnnotation(HideForDiseasesInCountries.class);
		if (hideIn != null && isListed(hideIn.diseases()) && isServerIn(hideIn.countries())) {
			return false;
		}

		HideForDiseasesInCountriesExcept hideExcept = field.getAnnotation(HideForDiseasesInCountriesExcept.class);
		return hideExcept == null || !isListed(hideExcept.diseases()) || isServerIn(hideExcept.countries());
	}

	private boolean isListed(Disease[] diseases) {
		return disease != null && Arrays.asList(diseases).contains(disease);
	}

	private boolean isServerIn(String[] countries) {
		// An unset server country counts as none of the listed countries
		return countryLocale != null && CountryHelper.isInCountries(countryLocale, countries);
	}
}
