package de.symeda.sormas.api.caze.porthealthinfo;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of overland conveyance a person can use")
public enum ConveyanceType {

	CAR,
	BUS,
	MOTORBIKE,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
