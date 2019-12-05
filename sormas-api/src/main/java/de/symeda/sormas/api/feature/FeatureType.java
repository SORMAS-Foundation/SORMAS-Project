package de.symeda.sormas.api.feature;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FeatureType {

	LINE_LISTING;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
