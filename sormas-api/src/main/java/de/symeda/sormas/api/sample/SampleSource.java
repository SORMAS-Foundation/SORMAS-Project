package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum SampleSource {

	HUMAN,
	ANIMAL,
	ENVIRONMENT;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
