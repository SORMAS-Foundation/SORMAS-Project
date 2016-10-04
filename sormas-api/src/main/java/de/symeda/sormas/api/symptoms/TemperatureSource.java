package de.symeda.sormas.api.symptoms;

import de.symeda.sormas.api.I18nProperties;

public enum TemperatureSource {
	AXILLARY, 
	ORAL, 
	RECTAL;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
