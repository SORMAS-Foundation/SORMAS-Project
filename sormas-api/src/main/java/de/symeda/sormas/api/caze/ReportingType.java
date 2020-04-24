package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ReportingType {

	NOT_RAISED, OTHER, DOCTOR, LABORATORY, OWN_DETERMINATION, HOSPITAL_OR_STATIONARY_CARE, NOT_DETERMINABLE, FORWARDING,
	COMMUNITY_FACILITY;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
