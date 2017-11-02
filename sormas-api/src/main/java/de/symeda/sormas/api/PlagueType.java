package de.symeda.sormas.api;

public enum PlagueType {

	BUBONIC,
	PNEUMONIC,
	SEPTICAEMIC;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
}
