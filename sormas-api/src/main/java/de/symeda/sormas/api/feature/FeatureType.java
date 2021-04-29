package de.symeda.sormas.api.feature;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * New server features are automatically added to the database in FeatureConfigurationService.createMissingFeatureConfigurations().
 */
public enum FeatureType {

	// FEATURE MODULES
	AGGREGATE_REPORTING(true, true, null),
	CAMPAIGNS(true, false, null),
	CASE_SURVEILANCE(true, true, null),
	CLINICAL_MANAGEMENT(true, true, null),
	CONTACT_TRACING(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE }),
	EVENT_SURVEILLANCE(true, true, null),
	SAMPLES_LAB(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE }),
	TASK_MANAGEMENT(true, true, null),
	WEEKLY_REPORTING(true, true, null),

	// FEATURE EXTENSIONS
	ASSIGN_TASKS_TO_HIGHER_LEVEL(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT }),
	CASE_FOLLOWUP(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE }),
	DOCUMENTS(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE }),
	EVENT_GROUPS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE }),
	LAB_MESSAGES(true,
		false,
		new FeatureType[] {
			SAMPLES_LAB }),
	MANUAL_EXTERNAL_MESSAGES(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE }),
	NATIONAL_CASE_SHARING(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE }),
	SURVEILLANCE_REPORTS(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE }),

	// ADDITIONAL FEATURES
	GDPR_CONSENT_POPUP(true, false, null),
	INFRASTRUCTURE_TYPE_AREA(true, false, null),
	OUTBREAKS(true, true, null),
	PERSON_MANAGEMENT(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE }),

	// REGION- AND DISEASE-BASED FEATURES
	LINE_LISTING(false, false, null),

	// NOTIFICATION CONFIGURATIONS
	EVENT_GROUPS_MODIFICATION_NOTIFICATIONS(true,
		false,
		new FeatureType[] {
			EVENT_GROUPS }),
	EVENT_PARTICIPANT_CASE_CONFIRMED_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE }),
	TASK_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT }),
	OTHER_NOTIFICATIONS(true, true, null),

	// TASK GENERATION FEATURES
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
			TASK_MANAGEMENT });

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
