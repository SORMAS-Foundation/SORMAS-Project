package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.I18nProperties;

/**
 * Contains the different types of date that are used to determine the start date of a case.
 * Normally, symptom onset (ONSET) date is considered first, then case reception (RECEPTION)
 * date, and finally case report (REPORT) date.
 */
public enum NewCaseDateType {

	MOST_RELEVANT,
	ONSET,
	RECEPTION,
	REPORT;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}	
	
}
