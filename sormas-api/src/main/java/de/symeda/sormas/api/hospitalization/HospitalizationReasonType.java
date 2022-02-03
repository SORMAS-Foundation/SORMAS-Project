package de.symeda.sormas.api.hospitalization;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.HideForCountriesExcept;

public enum HospitalizationReasonType {

	REPORTED_DISEASE,
	@HideForCountriesExcept
	ISOLATION,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
