package de.symeda.sormas.api.campaign;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;

public enum CampaignJurisdictionLevel {

	AREA,
	REGION,
	DISTRICT,
	COMMUNITY;

	public static CampaignJurisdictionLevel getByJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		switch (jurisdictionLevel) {

		case NATION:
		case NONE:
			return AREA;
		case REGION:
			return REGION;
		case DISTRICT:
			return DISTRICT;
		case COMMUNITY:
			return COMMUNITY;
		default:
			throw new UnsupportedOperationException();
		}
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
