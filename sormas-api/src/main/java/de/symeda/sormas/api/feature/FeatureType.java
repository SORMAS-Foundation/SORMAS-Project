package de.symeda.sormas.api.feature;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FeatureType {

	/**
	 * New server features are automatically added to the database in StartupShutdownService.createMissingFeatureConfigurations().
	 * The corresponding feature configurations are set to be enabled by default.
	 */
	
	LINE_LISTING(false),
	AGGREGATE_REPORTING(true),
	EVENT_SURVEILLANCE(true),
	WEEKLY_REPORTING(true),
	CLINICAL_MANAGEMENT(true),
	NATIONAL_CASE_SHARING(true);
	
	/**
	 * Server feature means that the feature only needs to be configured once per server since they define the way the system
	 * is supposed to operate.
	 */
	private boolean serverFeature;
	
	FeatureType(boolean serverFeature) {
		this.serverFeature = serverFeature;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	public boolean isServerFeature() {
		return serverFeature;
	}
	
	public static List<FeatureType> getAllServerFeatures() {
		List<FeatureType> serverFeatures = new ArrayList<>();
		for (FeatureType featureType : values()) {
			if (featureType.isServerFeature()) {
				serverFeatures.add(featureType);
			}
		}
		
		return serverFeatures;
	}
	
}
