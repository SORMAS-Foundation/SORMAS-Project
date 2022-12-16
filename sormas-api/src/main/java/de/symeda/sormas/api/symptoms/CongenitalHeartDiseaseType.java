package de.symeda.sormas.api.symptoms;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of congenital heart disease")
public enum CongenitalHeartDiseaseType {

	PDA,
	PPS,
	VSD,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
