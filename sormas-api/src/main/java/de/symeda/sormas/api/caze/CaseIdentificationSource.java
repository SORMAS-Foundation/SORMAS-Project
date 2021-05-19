package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CaseIdentificationSource {

	UNKNOWN,
	OUTBREAK_INVESTIGATION,
	CONTACT_TRACKING_APP,
	SUSPICION_REPORT,
	CONTACT_TRACING,
	SCREENING,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
