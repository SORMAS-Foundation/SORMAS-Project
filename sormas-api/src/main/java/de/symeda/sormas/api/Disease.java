package de.symeda.sormas.api;

public enum Disease {
	EVD,
	LASSA,
	AVIAN_INFLUENCA,
	CSM,
	CHOLERA,
	MEASLES
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
