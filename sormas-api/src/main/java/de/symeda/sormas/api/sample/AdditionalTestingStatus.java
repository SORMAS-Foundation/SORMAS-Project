package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of the additional requested test.")
public enum AdditionalTestingStatus {

	NOT_REQUESTED,
	REQUESTED,
	PERFORMED;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
