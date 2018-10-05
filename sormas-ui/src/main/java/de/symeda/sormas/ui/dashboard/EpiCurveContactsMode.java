package de.symeda.sormas.ui.dashboard;

import de.symeda.sormas.api.I18nProperties;

public enum EpiCurveContactsMode {
	
	CONTACT_STATUS,
	CONTACT_CLASSIFICATION,
	FOLLOW_UP_STATUS;	
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
}