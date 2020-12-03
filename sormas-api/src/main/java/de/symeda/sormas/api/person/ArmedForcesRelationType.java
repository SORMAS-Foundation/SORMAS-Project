package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ArmedForcesRelationType {

	UNKNOWN,
	NO_RELATION,
	// working for armed forces, but not as soldier
	CIVIL,
	SOLDIER_OR_RELATIVE;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
