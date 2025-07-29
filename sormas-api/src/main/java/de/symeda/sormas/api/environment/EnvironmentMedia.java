package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EnvironmentMedia {

	WATER,
	SOIL_ROCK,
	AIR,
	BIOTA,
	VECTORS;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
