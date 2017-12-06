package de.symeda.sormas.api;

public enum CaseMeasure {

	CASE_COUNT,
	CASE_INCIDENCE
	;	
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
