package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum WaterUse {

	DRINKING_HOUSEHOLD_NEEDS,
	RECREATION,
	INDUSTRY_COMMERCE,
	AGRICULTURE,
	THERMOELECTRICITY_ENERGY,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
