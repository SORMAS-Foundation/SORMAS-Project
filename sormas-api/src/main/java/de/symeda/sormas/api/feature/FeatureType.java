package de.symeda.sormas.api.feature;

import java.util.ArrayList;
import java.util.List;

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
	TASK_MANAGEMENT(true, true, null),
	TASK_GENERATION_CASE_SURVEILLANCE(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT }),
	TASK_GENERATION_CONTACT_TRACING(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT }),
	TASK_GENERATION_EVENT_SURVEILLANCE(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT }),
	TASK_GENERATION_GENERAL(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT }),
	CAMPAIGNS(true, false, null),
	CASE_SURVEILANCE(true, true, null),
	CONTACT_TRACING(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE }),
	SAMPLES_LAB(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			CONTACT_TRACING,
			EVENT_SURVEILLANCE }),
	INFRASTRUCTURE_TYPE_AREA(true, false, null),
	CASE_FOLLOWUP(true, false, null),
	TASK_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT }),
	MANUAL_EXTERNAL_MESSAGES(true, true, null),
	OTHER_NOTIFICATIONS(true, true, null),
	DOCUMENTS(true, false, null),
	OUTBREAKS(true, true, null),
	LAB_MESSAGES(true, false, null),
	ASSIGN_TASKS_TO_HIGHER_LEVEL(true, true, null),
	SURVEILLANCE_REPORTS(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE }),
	PERSON_MANAGEMENT(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE });

	/**
	 * Server feature means that the feature only needs to be configured once per server since they define the way the system
	 * is supposed to operate.
	 */
	private final boolean serverFeature;

	/**
	 * Is the feature enabled by default?
	 */
	private final boolean enabledDefault;

	private final FeatureType[] dependentFeatures;

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

	public boolean isDependent() {
		return dependentFeatures != null;
	}

	public FeatureType[] getDependentFeatures() {
		return dependentFeatures;
	}

}
