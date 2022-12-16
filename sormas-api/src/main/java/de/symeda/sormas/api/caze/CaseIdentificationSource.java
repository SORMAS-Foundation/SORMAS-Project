package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reason why the case was reported to SORMAS")
public enum CaseIdentificationSource {

	UNKNOWN,
	OUTBREAK_INVESTIGATION,
	CONTACT_TRACKING_APP,
	SUSPICION_REPORT,
	CONTACT_TRACING,
	SCREENING,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
