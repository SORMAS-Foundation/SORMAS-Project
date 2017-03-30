package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.I18nProperties;

public enum ContactRelation {
	
	SAME_HOUSEHOLD,
	FAMILY_MEMBER_OR_FRIEND,
	SAME_ENVIRONMENT,
	MEDICAL_CARE,
	OTHER;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
