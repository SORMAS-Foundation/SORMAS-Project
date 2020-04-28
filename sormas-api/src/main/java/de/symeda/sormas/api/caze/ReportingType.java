package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ReportingType {

	DOCTOR, FORWARDING, HOSPITAL_OR_STATIONARY_CARE, COMMUNITY_FACILITY, LABORATORY, OWN_DETERMINATION,
	NOT_DETERMINABLE, NOT_RAISED, OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
