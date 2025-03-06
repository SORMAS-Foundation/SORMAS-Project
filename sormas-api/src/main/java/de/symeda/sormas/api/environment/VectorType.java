package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum VectorType {

	MOSQUITOS,
	TICKS;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
