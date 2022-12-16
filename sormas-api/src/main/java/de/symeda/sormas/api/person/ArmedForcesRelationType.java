package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Relation of a person to the armed forces, CIVIL means working for armed forces, but not as a soldier")
public enum ArmedForcesRelationType {

	UNKNOWN,
	NO_RELATION,
	// working for armed forces, but not as soldier
	CIVIL,
	SOLDIER_OR_RELATIVE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
