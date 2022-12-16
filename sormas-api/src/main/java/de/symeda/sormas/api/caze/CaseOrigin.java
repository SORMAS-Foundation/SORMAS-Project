package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Whether the case originated IN_COUNTRY or at a POINT_OF_ENTRY")
public enum CaseOrigin {

	IN_COUNTRY,
	POINT_OF_ENTRY;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
