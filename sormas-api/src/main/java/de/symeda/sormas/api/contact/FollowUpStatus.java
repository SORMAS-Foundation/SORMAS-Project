package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.I18nProperties;

public enum FollowUpStatus {
	FOLLOW_UP,
	COMPLETED,
	CANCELED,
	LOST,
	NO_FOLLOW_UP,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
