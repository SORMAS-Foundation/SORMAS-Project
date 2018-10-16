package de.symeda.sormas.ui.dashboard.surveillance;

import de.symeda.sormas.api.I18nProperties;

public enum EpiCurveSurveillanceMode {
	
	CASE_STATUS,
	ALIVE_OR_DEAD;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
}