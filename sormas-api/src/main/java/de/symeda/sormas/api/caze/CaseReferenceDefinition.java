package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Whether the case fulfills the definition of a confirmed case according to an external reporting tool. Specific to Germany")
public enum CaseReferenceDefinition {

	FULFILLED,
	NOT_FULFILLED;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
