package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.HideForCountriesExcept;

public enum ReportingType {

	DOCTOR,
	FORWARDING,
	HOSPITAL_OR_STATIONARY_CARE,
	COMMUNITY_FACILITY,
	@HideForCountriesExcept
	COMMUNITY_FACILITY_IFSG_ARTICLE_34,
	LABORATORY,
	OWN_DETERMINATION,
	NOT_DETERMINABLE,
	NOT_RAISED,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
