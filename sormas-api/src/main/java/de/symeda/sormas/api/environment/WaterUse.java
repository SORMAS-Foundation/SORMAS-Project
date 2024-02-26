package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum WaterUse {

	DRINKING_HOUSEHOLD_NEEDS(null),
	RECREATION(null),
	INDUSTRY_COMMERCE(null),
	AGRICULTURE(null),
	THERMOELECTRICITY_ENERGY(null),
	OTHER(null),
	UNKNOWN(null);

	private WaterUse parent;

	WaterUse(WaterUse parent) {
		this.parent = parent;
	}

	public WaterUse getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
