package de.symeda.sormas.api.person;

import de.symeda.sormas.api.I18nProperties;

public enum OccupationType {
	FARMER, 
	BUTCHER, 
	HUNTER_MEAT_TRADER, 
	MINER, 
	RELIGIOUS_LEADER, 
	HOUSEWIFE, 
	PUPIL_STUDENT, 
	CHILD, BUSINESSMAN_WOMAN, 
	TRANSPORTER, 
	HEALTHCARE_WORKER,
	TRADITIONAL_SPIRITUAL_HEALER,
	OTHER
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
