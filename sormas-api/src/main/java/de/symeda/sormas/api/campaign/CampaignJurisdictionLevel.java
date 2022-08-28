package de.symeda.sormas.api.campaign;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;

public enum CampaignJurisdictionLevel {
	
	NONE,
	AREA,
	REGION,
	DISTRICT,
	COMMUNITY;

	public static CampaignJurisdictionLevel getByJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		switch (jurisdictionLevel) {

		case NONE:
			return NONE;
		case AREA:
			return AREA;
		case REGION:
			return REGION;
		case DISTRICT:
			return DISTRICT;
		case COMMUNITY:
			return COMMUNITY;
		default:
			return NONE;
		}
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
