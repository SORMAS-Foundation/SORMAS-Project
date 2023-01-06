package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter for relevant status of entities.")
public enum EntityRelevanceStatus {

	ACTIVE,
	ARCHIVED,
	ALL;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
