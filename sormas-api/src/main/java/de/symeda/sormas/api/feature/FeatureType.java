package de.symeda.sormas.api.feature;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum FeatureType {

	/**
	 * New server features are automatically added to the database in FeatueConfigurationFacadeEjb.createMissingFeatureConfigurations().
	 */
	LINE_LISTING(false, false),
	AGGREGATE_REPORTING(true, true),
	EVENT_SURVEILLANCE(true, true),
	WEEKLY_REPORTING(true, true),
	CLINICAL_MANAGEMENT(true, true),
	NATIONAL_CASE_SHARING(true, false),
	TASK_GENERATION_CASE_SURVEILLANCE(true, true),
	TASK_GENERATION_CONTACT_TRACING(true, true),
	TASK_GENERATION_EVENT_SURVEILLANCE(true, true),
	TASK_GENERATION_GENERAL(true, true);
	
	/**
	 * Server feature means that the feature only needs to be configured once per server since they define the way the system
	 * is supposed to operate.
	 */
	private boolean serverFeature;
	
	/**
	 * Is the feature enabled by default?
	 */
	private boolean enabledDefault;

	FeatureType(boolean serverFeature, boolean enabledDefault) {
		this.serverFeature = serverFeature;
		this.enabledDefault = enabledDefault;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
	public boolean isServerFeature() {
		return serverFeature;
	}
	
	public boolean isEnabledDefault() {
		return enabledDefault;
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
