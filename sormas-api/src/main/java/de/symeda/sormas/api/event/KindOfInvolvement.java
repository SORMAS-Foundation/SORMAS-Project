package de.symeda.sormas.api.event;

import de.symeda.sormas.api.I18nProperties;

public enum KindOfInvolvement {
	POTENTIALLY_EXPOSED,
	POTENTIAL_INDEX_CASE,
	OTHER;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
