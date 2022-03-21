package de.symeda.sormas.api.campaign;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Not used yet
 */
public enum CampaignPhase {

	PRE,
	INTRA,
	POST,
	ALL;

//	public String toString() {
//		return I18nProperties.getEnumCaption(this);
	//}
	
	public String toString() {
		return name().toUpperCase()+"-CAMPAIGN";
	}
}
