package de.symeda.sormas.api;

public enum Disease {
	EBOLA
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
