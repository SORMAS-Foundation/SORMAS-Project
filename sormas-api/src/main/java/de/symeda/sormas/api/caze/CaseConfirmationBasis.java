package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CaseConfirmationBasis {

	CLINICAL_CONFIRMATION,
	EPIDEMIOLOGICAL_CONFIRMATION,
	LABORATORY_DIAGNOSTIC_CONFIRMATION;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
