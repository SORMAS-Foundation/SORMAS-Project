package de.symeda.sormas.api.infrastructure.pointofentry;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enum for point-of-entry into a country")
public enum PointOfEntryType {

	AIRPORT,
	SEAPORT,
	GROUND_CROSSING,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
