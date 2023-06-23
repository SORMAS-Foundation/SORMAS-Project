package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum WaterType {

	WASTEWATER,
	GROUNDWATER,
	SURFACE_WATER,
	PRECIPITATION,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
