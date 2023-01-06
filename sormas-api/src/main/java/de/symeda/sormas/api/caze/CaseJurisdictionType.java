package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter criterium: Whether to show ALL cases or differentiate between cases where either\n\n"
	+ "the jurisdiction lies with the PLACE_OF_STAY of the associated person,\n\n"
	+ "or where a different location is RESPONSIBLE, that is not part of the person's home infrastructure hierarchy.")
public enum CaseJurisdictionType {

	ALL,
	RESPONSIBLE,
	PLACE_OF_STAY;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
