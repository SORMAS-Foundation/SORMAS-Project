package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of drugs that can be used for treatments")
public enum TypeOfDrug {

	ANTIMICROBIAL,
	ANTIVIRAL,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
