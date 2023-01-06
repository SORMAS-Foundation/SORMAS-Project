package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enum of possible pregnancy-trimesters")
public enum Trimester {

	FIRST,
	SECOND,
	THIRD,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
