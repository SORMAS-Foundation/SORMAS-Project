package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum SampleTestResultType {

	INDETERMINATE,
	PENDING,
	NEGATIVE,
	POSITIVE,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
}
