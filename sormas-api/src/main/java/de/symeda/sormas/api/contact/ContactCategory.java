package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ContactCategory {

	HIGH_RISK,
	HIGH_RISK_MED,
	MEDIUM_RISK_MED,
	LOW_RISK,
	NO_RISK;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
