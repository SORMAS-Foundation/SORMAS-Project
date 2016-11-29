package de.symeda.sormas.api.visit;

import de.symeda.sormas.api.I18nProperties;

public enum VisitStatus {
	UNAVAILABLE,
	UNCOOPERATIVE,
	COOPERATIVE,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
