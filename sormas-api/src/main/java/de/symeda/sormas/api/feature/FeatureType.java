package de.symeda.sormas.api.feature;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FeatureType {

	/**
	 * New module features are automatically added to the database in StartupShutdownService.createMissingFeatureConfigurations().
	 * The corresponding feature configurations are set to be enabled by default.
	 */
	
	LINE_LISTING(false),
	AGGREGATE_REPORTING(true),
	EVENT_SURVEILLANCE(true),
	WEEKLY_REPORTING(true),
	CLINICAL_MANAGEMENT(true);
	
	private boolean moduleFeature;
	
	FeatureType(boolean moduleFeature) {
		this.moduleFeature = moduleFeature;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	public boolean isModuleFeature() {
		return moduleFeature;
	}
	
	public static List<FeatureType> getAllModuleFeatures() {
		List<FeatureType> moduleFeatures = new ArrayList<>();
		for (FeatureType featureType : values()) {
			if (featureType.isModuleFeature()) {
				moduleFeatures.add(featureType);
			}
		}
		
		return moduleFeatures;
	}
	
}
