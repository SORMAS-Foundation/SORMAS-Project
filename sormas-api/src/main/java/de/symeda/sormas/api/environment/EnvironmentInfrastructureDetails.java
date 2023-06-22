package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EnvironmentInfrastructureDetails {

	SEPTIC_TANK,
	LATRIN,
	TOILET,
	MANHOLE,
	WELLS,
	SURFACE_WATER,
	OPEN_DRAIN,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
