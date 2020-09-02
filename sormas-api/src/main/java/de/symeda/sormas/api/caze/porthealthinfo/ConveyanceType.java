package de.symeda.sormas.api.caze.porthealthinfo;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ConveyanceType {

	CAR,
	BUS,
	MOTORBIKE,
	OTHER;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
