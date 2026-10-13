package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.patch.mapping.ValueMapperDefault;

public enum Trimester {

	FIRST,
	SECOND,
	THIRD,
	@ValueMapperDefault
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
