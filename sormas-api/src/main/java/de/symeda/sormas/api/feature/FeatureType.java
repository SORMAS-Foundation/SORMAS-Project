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

import static de.symeda.sormas.api.common.DeletableEntityType.CASE;
import static de.symeda.sormas.api.common.DeletableEntityType.CONTACT;
import static de.symeda.sormas.api.common.DeletableEntityType.EVENT;
import static de.symeda.sormas.api.common.DeletableEntityType.EVENT_PARTICIPANT;
import static de.symeda.sormas.api.common.DeletableEntityType.IMMUNIZATION;
import static de.symeda.sormas.api.common.DeletableEntityType.TRAVEL_ENTRY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * New server features are automatically added to the database in FeatureConfigurationService.createMissingFeatureConfigurations().
 */
public enum FeatureType {

	// FEATURE MODULES
	AGGREGATE_REPORTING(true, true, null, null, null),
	CAMPAIGNS(true, false, null, null, null),
	CASE_SURVEILANCE(true,
		true,
		null,
		null,
		ImmutableMap
			.of(FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT, Boolean.TRUE, FeatureTypeProperty.HIDE_JURISDICTION_FIELDS, Boolean.FALSE)),
	CLINICAL_MANAGEMENT(true, true, null, null, null),
	CONTACT_TRACING(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		ImmutableMap.of(
			FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT,
			Boolean.TRUE,
			FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE,
			Boolean.FALSE)),
	EVENT_SURVEILLANCE(true, true, null, null, null),
	SAMPLES_LAB(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE },
		null,
		null),
	ADDITIONAL_TESTS(true,
		false,
		new FeatureType[] {
			SAMPLES_LAB },
		null,
		null),
	TASK_MANAGEMENT(true, true, null, null, ImmutableMap.of(FeatureTypeProperty.ALLOW_FREE_EDITING, Boolean.FALSE)),
	WEEKLY_REPORTING(true, true, null, null, null),
	IMMUNIZATION_MANAGEMENT(true, true, null, null, ImmutableMap.of(FeatureTypeProperty.REDUCED, Boolean.FALSE)),
	TRAVEL_ENTRIES(true, false, null, null, null),

	DASHBOARD_SURVEILLANCE(true, true, null, null, null),
	DASHBOARD_CONTACTS(true, true, null, null, null),
	DASHBOARD_CAMPAIGNS(true, true, null, null, null),
	DASHBOARD_SAMPLES(true, true, null, null, null),
	LIMITED_SYNCHRONIZATION(true,
		false,
		null,
		null,
		ImmutableMap.of(FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES, Boolean.FALSE, FeatureTypeProperty.MAX_CHANGE_DATE_PERIOD, -1)),
	ENVIRONMENT_MANAGEMENT(true, false, null, null, null),

	// FEATURE EXTENSIONS
	ASSIGN_TASKS_TO_HIGHER_LEVEL(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null,
		null),
	CASE_FOLLOWUP(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		ImmutableMap.of(FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE, Boolean.FALSE)),
	DOCUMENTS(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE },
		null,
		null),
	DOCUMENTS_MULTI_UPLOAD(true,
		true,
		new FeatureType[] {
			DOCUMENTS },
		null,
		null),
	EVENT_GROUPS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null,
		null),
	EVENT_HIERARCHIES(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null,
		null),
	EXTERNAL_MESSAGES(true,
		false,
		new FeatureType[] {
			SAMPLES_LAB },
		null,
		ImmutableMap.of(FeatureTypeProperty.FETCH_MODE, Boolean.FALSE)),
	MANUAL_EXTERNAL_MESSAGES(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	NATIONAL_CASE_SHARING(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	SURVEILLANCE_REPORTS(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	SORMAS_TO_SORMAS_ACCEPT_REJECT(true,
		false,
		new FeatureType[] {
			CASE_SURVEILANCE,
			CONTACT_TRACING,
			EVENT_SURVEILLANCE },
		null,
		null),
	SORMAS_TO_SORMAS_SHARE_CASES(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			CONTACT_TRACING,
			SAMPLES_LAB },
		null,
		ImmutableMap.of(
			FeatureTypeProperty.SHARE_ASSOCIATED_CONTACTS,
			Boolean.FALSE,
			FeatureTypeProperty.SHARE_SAMPLES,
			Boolean.TRUE,
			FeatureTypeProperty.SHARE_IMMUNIZATIONS,
			Boolean.TRUE,
			FeatureTypeProperty.SHARE_REPORTS,
			Boolean.TRUE)),
	SORMAS_TO_SORMAS_SHARE_CONTACTS(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			CONTACT_TRACING,
			SAMPLES_LAB },
		null,
		ImmutableMap.of(FeatureTypeProperty.SHARE_SAMPLES, Boolean.TRUE, FeatureTypeProperty.SHARE_IMMUNIZATIONS, Boolean.TRUE)),
	SORMAS_TO_SORMAS_SHARE_EVENTS(true,
		false,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null,
		ImmutableMap.of(FeatureTypeProperty.SHARE_SAMPLES, Boolean.TRUE, FeatureTypeProperty.SHARE_IMMUNIZATIONS, Boolean.TRUE)),
	SORMAS_TO_SORMAS_SHARE_EXTERNAL_MESSAGES(true,
		false,
		new FeatureType[] {
			EXTERNAL_MESSAGES },
		null,
		null),
	IMMUNIZATION_STATUS_AUTOMATION(true,
		true,
		new FeatureType[] {
			IMMUNIZATION_MANAGEMENT },
		null,
		null),
	PERSON_DUPLICATE_CUSTOM_SEARCH(true, false, null, null, null),
	EDIT_INFRASTRUCTURE_DATA(true, true, null, null, null),
	AUTOMATIC_ARCHIVING(true,
		true,
		null,
		Arrays.asList(CASE, CONTACT, EVENT, EVENT_PARTICIPANT, IMMUNIZATION, TRAVEL_ENTRY),
		ImmutableMap.of(FeatureTypeProperty.THRESHOLD_IN_DAYS, 90)),
	EDIT_ARCHIVED_ENTITIES(true, true, null, null, null),

	// SHOW/HIDE VIEW TAB FEATURES
	VIEW_TAB_CASES_HOSPITALIZATION(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	VIEW_TAB_CASES_SYMPTOMS(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	VIEW_TAB_CASES_EPIDEMIOLOGICAL_DATA(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	VIEW_TAB_CASES_THERAPY(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	VIEW_TAB_CASES_FOLLOW_UP(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	VIEW_TAB_CASES_CLINICAL_COURSE(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE },
		null,
		null),
	VIEW_TAB_CONTACTS_EPIDEMIOLOGICAL_DATA(true,
		true,
		new FeatureType[] {
			CONTACT_TRACING },
		null,
		null),
	VIEW_TAB_CONTACTS_FOLLOW_UP_VISITS(true,
		true,
		new FeatureType[] {
			CONTACT_TRACING },
		null,
		null),

	// ADDITIONAL FEATURES
	GDPR_CONSENT_POPUP(true, false, null, null, null),
	INFRASTRUCTURE_TYPE_AREA(true, false, null, null, null),
	OUTBREAKS(true, true, null, null, null),
	PERSON_MANAGEMENT(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			EVENT_SURVEILLANCE },
		null,
		null),

	// REGION- AND DISEASE-BASED FEATURES
	LINE_LISTING(false, false, null, null, null),

	// NOTIFICATION CONFIGURATIONS
	EVENT_GROUPS_MODIFICATION_NOTIFICATIONS(true,
		false,
		new FeatureType[] {
			EVENT_GROUPS },
		null,
		null),
	EVENT_PARTICIPANT_CASE_CONFIRMED_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null,
		null),
	EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			EVENT_SURVEILLANCE },
		null,
		null),
	TASK_NOTIFICATIONS(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null,
		null),
	OTHER_NOTIFICATIONS(true, true, null, null, null),

	// TASK GENERATION FEATURES
	TASK_GENERATION_CASE_SURVEILLANCE(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null,
		null),
	TASK_GENERATION_CONTACT_TRACING(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null,
		null),
	TASK_GENERATION_EVENT_SURVEILLANCE(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null,
		null),
	TASK_GENERATION_GENERAL(true,
		true,
		new FeatureType[] {
			TASK_MANAGEMENT },
		null,
		null),
	CASE_AND_CONTACT_BULK_ACTIONS(true,
		true,
		new FeatureType[] {
			CASE_SURVEILANCE,
			CONTACT_TRACING },
		null,
		ImmutableMap.of(FeatureTypeProperty.S2S_SHARING, Boolean.FALSE)),
	EXTERNAL_EMAILS(true, false, null, null, null);

	public static final FeatureType[] SURVEILLANCE_FEATURE_TYPES = {
		FeatureType.CASE_SURVEILANCE,
		FeatureType.EVENT_SURVEILLANCE,
		FeatureType.AGGREGATE_REPORTING };

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
	private final List<DeletableEntityType> entityTypes;
	private final Map<FeatureTypeProperty, Object> supportedPropertyDefaults;

	FeatureType(
		boolean serverFeature,
		boolean enabledDefault,
		FeatureType[] dependentFeatures,
		List<DeletableEntityType> entityTypes,
		Map<FeatureTypeProperty, Object> supportedPropertyDefaults) {
		this.serverFeature = serverFeature;
		this.enabledDefault = enabledDefault;
		this.dependentFeatures = dependentFeatures;
		this.entityTypes = entityTypes;
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

	public List<DeletableEntityType> getEntityTypes() {
		return entityTypes;
	}
}
