/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.api.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * New server features are automatically added to the database in FeatureConfigurationService.createMissingFeatureConfigurations().
 */
public enum FeatureType {

	// FEATURE MODULES
	AGGREGATE_REPORTING(true, true, null, null),
	CAMPAIGNS(true, false, null, null),
	CASE_SURVEILANCE(true, true, null, ImmutableMap.of(FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT, Boolean.TRUE)),
	CLINICAL_MANAGEMENT(true, true, null, null),
	CONTACT_TRACING(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		ImmutableMap.of(
			FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT,
			Boolean.TRUE,
			FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE,
			Boolean.FALSE)),
	EVENT_SURVEILLANCE(true, true, null, null),
	SAMPLES_LAB(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE },
		null),
	ADDITIONAL_TESTS(true,
		false,
		new FeatureType[] {
			SAMPLES_LAB },
		null),
	TASK_MANAGEMENT(true, true, null, ImmutableMap.of(FeatureTypeProperty.ALLOW_FREE_EDITING, Boolean.FALSE)),
	WEEKLY_REPORTING(true, true, null, null),
	IMMUNIZATION_MANAGEMENT(true, true, null, ImmutableMap.of(FeatureTypeProperty.REDUCED, Boolean.FALSE)),
	TRAVEL_ENTRIES(true, false, null, null),

	DASHBOARD(true, true, null, null),

	// FEATURE EXTENSIONS
	ASSIGN_TASKS_TO_HIGHER_LEVEL(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null),
	CASE_FOLLOWUP(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE },
		ImmutableMap.of(FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE, Boolean.FALSE)),
	DOCUMENTS(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE },
		null),
	DOCUMENTS_MULTI_UPLOAD(true,
		true,
		new FeatureType[] {
			DOCUMENTS },
		null),
	EVENT_GROUPS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null),
	EVENT_HIERARCHIES(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null),
	LAB_MESSAGES(true,
		false,
		new FeatureType[] {
			SAMPLES_LAB },
		null),
	MANUAL_EXTERNAL_MESSAGES(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	NATIONAL_CASE_SHARING(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	SURVEILLANCE_REPORTS(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	SORMAS_TO_SORMAS_ACCEPT_REJECT(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE,
			CONTACT_TRACING,
			EVENT_SURVEILLANCE },
		null),
	SORMAS_TO_SORMAS_SHARE_CASES_WITH_CONTACTS_AND_SAMPLES(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			CONTACT_TRACING,
			SAMPLES_LAB },
		null),
	SORMAS_TO_SORMAS_SHARE_EVENTS(true,
		false,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null),
	SORMAS_TO_SORMAS_SHARE_LAB_MESSAGES(true,
		false,
		new FeatureType[] {
			LAB_MESSAGES },
		null),
	IMMUNIZATION_STATUS_AUTOMATION(true,
		true,
		new FeatureType[] {
			IMMUNIZATION_MANAGEMENT },
		null),
	PERSON_DUPLICATE_CUSTOM_SEARCH(true, false, null, null),
	EDIT_INFRASTRUCTURE_DATA(true, true, null, null),

	// SHOW/HIDE VIEW TAB FEATURES
	VIEW_TAB_CASES_HOSPITALIZATION(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	VIEW_TAB_CASES_SYMPTOMS(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	VIEW_TAB_CASES_EPIDEMIOLOGICAL_DATA(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	VIEW_TAB_CASES_THERAPY(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	VIEW_TAB_CASES_FOLLOW_UP(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	VIEW_TAB_CASES_CLINICAL_COURSE(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null),
	VIEW_TAB_CONTACTS_EPIDEMIOLOGICAL_DATA(true,
		true,
		new FeatureType[] {
			CONTACT_TRACING },
		null),
	VIEW_TAB_CONTACTS_FOLLOW_UP_VISITS(true,
		true,
		new FeatureType[] {
			CONTACT_TRACING },
		null),

	// ADDITIONAL FEATURES
	GDPR_CONSENT_POPUP(true, false, null, null),
	INFRASTRUCTURE_TYPE_AREA(true, false, null, null),
	OUTBREAKS(true, true, null, null),
	PERSON_MANAGEMENT(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE },
		null),

	// REGION- AND DISEASE-BASED FEATURES
	LINE_LISTING(false, false, null, null),

	// NOTIFICATION CONFIGURATIONS
	EVENT_GROUPS_MODIFICATION_NOTIFICATIONS(true,
		false,
		new FeatureType[] {
			EVENT_GROUPS },
		null),
	EVENT_PARTICIPANT_CASE_CONFIRMED_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null),
	EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null),
	TASK_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null),
	OTHER_NOTIFICATIONS(true, true, null, null),

	// TASK GENERATION FEATURES
	TASK_GENERATION_CASE_SURVEILLANCE(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null),
	TASK_GENERATION_CONTACT_TRACING(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null),
	TASK_GENERATION_EVENT_SURVEILLANCE(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null),
	TASK_GENERATION_GENERAL(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null),
	DELETE_PERMANENT(true, false, null, null);

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
	private final Map<FeatureTypeProperty, Object> supportedPropertyDefaults;

	FeatureType(
		boolean serverFeature,
		boolean enabledDefault,
		FeatureType[] dependentFeatures,
		Map<FeatureTypeProperty, Object> supportedPropertyDefaults) {
		this.serverFeature = serverFeature;
		this.enabledDefault = enabledDefault;
		this.dependentFeatures = dependentFeatures;
		this.supportedPropertyDefaults = supportedPropertyDefaults;
	}

	@Override
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

	public Map<FeatureTypeProperty, Object> getSupportedPropertyDefaults() {
		return supportedPropertyDefaults;
	}

	public Set<FeatureTypeProperty> getSupportedProperties() {
		return supportedPropertyDefaults.keySet();
	}
}
