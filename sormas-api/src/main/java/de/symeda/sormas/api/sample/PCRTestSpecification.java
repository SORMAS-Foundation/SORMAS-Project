package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PCRTestSpecification {

	VARIANT_SPECIFIC,
	N501Y_MUTATION_DETECTION;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
