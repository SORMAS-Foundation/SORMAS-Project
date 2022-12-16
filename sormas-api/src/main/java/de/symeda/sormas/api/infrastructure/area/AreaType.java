package de.symeda.sormas.api.infrastructure.area;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enum for type of area.")
public enum AreaType {

	URBAN,
	RURAL,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
