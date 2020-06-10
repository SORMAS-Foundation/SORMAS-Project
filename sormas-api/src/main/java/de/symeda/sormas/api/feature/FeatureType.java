package de.symeda.sormas.api.feature;

import de.symeda.sormas.api.i18n.I18nProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * New server features are automatically added to the database in FeatureConfigurationService.createMissingFeatureConfigurations().
 */
public enum FeatureType {

	LINE_LISTING(false, false, null),
	AGGREGATE_REPORTING(true, true, null),
	EVENT_SURVEILLANCE(true, true, null),
	WEEKLY_REPORTING(true, true, null),
	CLINICAL_MANAGEMENT(true, true, null),
	NATIONAL_CASE_SHARING(true, false, null),
	TASK_GENERATION_CASE_SURVEILLANCE(true, true, null),
	TASK_GENERATION_CONTACT_TRACING(true, true, null),
	TASK_GENERATION_EVENT_SURVEILLANCE(true, true, null),
	TASK_GENERATION_GENERAL(true, true, null),
	CAMPAIGNS(true, false, null),
	CASE_SURVEILANCE(true, true, null),
	CONTACT_TRACING(true, true, new FeatureType[]{CASE_SURVEILANCE}),
	SAMPLES_LAB(true, true, new FeatureType[]{CASE_SURVEILANCE, CONTACT_TRACING});

	/**
	 * Server feature means that the feature only needs to be configured once per server since they define the way the system
	 * is supposed to operate.
	 */
	private final boolean serverFeature;

	/**
	 * Is the feature enabled by default?
	 */
	private final boolean enabledDefault;

	private FeatureType[] dependentFeatures;

	FeatureType(boolean serverFeature, boolean enabledDefault, FeatureType[] dependentFeatures) {
		this.serverFeature = serverFeature;
		this.enabledDefault = enabledDefault;
		this.dependentFeatures = dependentFeatures;
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

	public boolean isDependent(){
		return dependentFeatures != null;
	}

	public boolean dependencyTriggered() {
		List<FeatureType> featureDependencyList = Arrays.asList(dependentFeatures);
		List<FeatureType> listOfEnabledDependentFeatures = new ArrayList<>();

		listOfEnabledDependentFeatures.addAll(checkDependency(Arrays.asList(dependentFeatures)));
		return listOfEnabledDependentFeatures.isEmpty();
	}

	public List<FeatureType> checkDependency(List<FeatureType> featureTypeList){
		List<FeatureType> listOfEnabledDependentFeatures = new ArrayList<>();
		for(FeatureType featureType : featureTypeList) {
			if (featureType.isDependent()){
				listOfEnabledDependentFeatures.addAll(checkDependency(Arrays.asList(featureType.dependentFeatures)));
			}

			if (featureType.isEnabledDefault() && !featureType.isDependent()){
				listOfEnabledDependentFeatures.add(featureType);
			}
		};
		return  listOfEnabledDependentFeatures;
	}
}
