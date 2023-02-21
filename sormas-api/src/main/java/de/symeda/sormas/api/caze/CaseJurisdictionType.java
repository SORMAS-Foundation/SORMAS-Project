package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CaseJurisdictionType {

	ALL,
	RESPONSIBLE,
	PLACE_OF_STAY;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
