package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Risk category of the recorded contact.")
public enum ContactCategory {

	HIGH_RISK,
	HIGH_RISK_MED,
	MEDIUM_RISK_MED,
	LOW_RISK,
	NO_RISK;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
