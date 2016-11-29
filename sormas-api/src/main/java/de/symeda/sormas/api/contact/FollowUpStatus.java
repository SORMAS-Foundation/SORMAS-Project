package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.I18nProperties;

public enum FollowUpStatus {
	FOLLOW_UP,
	COMPLETED,
	CANCELED,
	LOST,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
