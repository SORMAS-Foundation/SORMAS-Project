package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of hospital ward where a person can be treated")
public enum HospitalWardType {

	PEDIATRIC_INPATIENT,
	NURSERY,
	EPU,
	CHER,
	OPD,
	EYE,
	ENT,
	CARDIOLOGY,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
