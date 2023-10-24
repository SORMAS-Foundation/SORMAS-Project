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

package de.symeda.sormas.api.importexport;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum DatabaseTable {

	CASES(DatabaseTableType.SORMAS, "cases", dependingOnFeature(FeatureType.CASE_SURVEILANCE)),
	HOSPITALIZATIONS(DatabaseTableType.SORMAS, CASES, "hospitalizations"),
	PREVIOUSHOSPITALIZATIONS(DatabaseTableType.SORMAS, HOSPITALIZATIONS, "previous_hospitalizations"),
	THERAPIES(DatabaseTableType.SORMAS, CASES, "therapies", dependingOnFeature(FeatureType.CLINICAL_MANAGEMENT)),
	PRESCRIPTIONS(DatabaseTableType.SORMAS, THERAPIES, "prescriptions"),
	TREATMENTS(DatabaseTableType.SORMAS, THERAPIES, "treatments"),
	CLINICAL_COURSES(DatabaseTableType.SORMAS, CASES, "clinical_courses", dependingOnFeature(FeatureType.CLINICAL_MANAGEMENT)),
	CLINICAL_VISITS(DatabaseTableType.SORMAS, CLINICAL_COURSES, "clinical_visits"),
	PORT_HEALTH_INFO(DatabaseTableType.SORMAS, CASES, "port_health_info"),
	MATERNAL_HISTORIES(DatabaseTableType.SORMAS, CASES, "maternal_histories"),
	SURVEILLANCE_REPORTS(DatabaseTableType.SORMAS, CASES, "surveillance_reports", dependingOnFeature(FeatureType.SURVEILLANCE_REPORTS)),

	EPIDATA(DatabaseTableType.SORMAS, "epidemiological_data", dependingOnFeature(FeatureType.CASE_SURVEILANCE, FeatureType.CONTACT_TRACING)),
	EXPOSURES(DatabaseTableType.SORMAS, EPIDATA, "exposures"),
	ACTIVITIES_AS_CASE(DatabaseTableType.SORMAS, EPIDATA, "activities_as_case"),

	HEALTH_CONDITIONS(DatabaseTableType.SORMAS,
		"health_conditions",
		dependingOnFeature(FeatureType.CASE_SURVEILANCE, FeatureType.CONTACT_TRACING, FeatureType.IMMUNIZATION_MANAGEMENT)),

	CONTACTS(DatabaseTableType.SORMAS, "contacts", dependingOnFeature(FeatureType.CONTACT_TRACING)),
	VISITS(DatabaseTableType.SORMAS, "visits", dependingOnFeature(FeatureType.CONTACT_TRACING, FeatureType.CASE_FOLLOWUP)),
	CONTACTS_VISITS(DatabaseTableType.SORMAS, VISITS, "contacts_visits"),

	SYMPTOMS(DatabaseTableType.SORMAS,
		"symptoms",
		dependingOnFeature(FeatureType.CASE_SURVEILANCE, FeatureType.CONTACT_TRACING, FeatureType.CLINICAL_MANAGEMENT)),

	EVENTS(DatabaseTableType.SORMAS, "events", dependingOnFeature(FeatureType.EVENT_SURVEILLANCE)),
	EVENTS_EVENTGROUPS(DatabaseTableType.SORMAS, EVENTS, "events_eventgroups"),
	EVENTGROUPS(DatabaseTableType.SORMAS, EVENTS, "eventgroups", dependingOnFeature(FeatureType.EVENT_GROUPS)),
	EVENTPARTICIPANTS(DatabaseTableType.SORMAS, EVENTS, "event_participants"),
	ACTIONS(DatabaseTableType.SORMAS, EVENTS, "actions"),

	TRAVEL_ENTRIES(DatabaseTableType.SORMAS, "travel_entries", dependingOnFeature(FeatureType.TRAVEL_ENTRIES)),

	IMMUNIZATIONS(DatabaseTableType.SORMAS, "immunizations", dependingOnFeature(FeatureType.IMMUNIZATION_MANAGEMENT)),
	VACCINATIONS(DatabaseTableType.SORMAS, IMMUNIZATIONS, "vaccinations"),

	SAMPLES(DatabaseTableType.SORMAS, "samples", dependingOnFeature(FeatureType.SAMPLES_LAB)),
	PATHOGEN_TESTS(DatabaseTableType.SORMAS, SAMPLES, "pathogen_tests"),
	ADDITIONAL_TESTS(DatabaseTableType.SORMAS, SAMPLES, "additional_tests", dependingOnFeature(FeatureType.ADDITIONAL_TESTS)),

	TASKS(DatabaseTableType.SORMAS, "tasks", dependingOnFeature(FeatureType.TASK_MANAGEMENT)),
	TASK_OBSERVER(DatabaseTableType.SORMAS, TASKS, "task_observer"),

	PERSONS(DatabaseTableType.SORMAS,
		"persons",
		dependingOnFeature(FeatureType.CASE_SURVEILANCE, FeatureType.CONTACT_TRACING, FeatureType.EVENT_SURVEILLANCE)),
	PERSON_CONTACT_DETAILS(DatabaseTableType.SORMAS, PERSONS, "person_contact_details"),
	PERSON_LOCATIONS(DatabaseTableType.SORMAS, PERSONS, "person_locations"),

	LOCATIONS(DatabaseTableType.SORMAS, "locations", null),

	OUTBREAKS(DatabaseTableType.SORMAS, "outbreaks", dependingOnFeature(FeatureType.OUTBREAKS)),
	CONTINENTS(DatabaseTableType.INFRASTRUCTURE, "continents", null),
	SUBCONTINENTS(DatabaseTableType.INFRASTRUCTURE, "subcontinent", null),
	COUNTRIES(DatabaseTableType.INFRASTRUCTURE, "countries", null),
	AREAS(DatabaseTableType.INFRASTRUCTURE, "areas", dependingOnFeature(FeatureType.INFRASTRUCTURE_TYPE_AREA)),
	REGIONS(DatabaseTableType.INFRASTRUCTURE, "regions", null),
	DISTRICTS(DatabaseTableType.INFRASTRUCTURE, "districts", null),
	COMMUNITIES(DatabaseTableType.INFRASTRUCTURE, "communities", null),
	FACILITIES(DatabaseTableType.INFRASTRUCTURE, "facilities", null),
	POINTS_OF_ENTRY(DatabaseTableType.INFRASTRUCTURE, "points_of_entry", null),
	CUSTOMIZABLE_ENUM_VALUES(DatabaseTableType.CONFIGURATION, "customizable_enum_values", null),

	CAMPAIGNS(DatabaseTableType.SORMAS, "campaigns", dependingOnFeature(FeatureType.CAMPAIGNS)),
	CAMPAIGN_CAMPAIGNFORMMETA(DatabaseTableType.SORMAS, CAMPAIGNS, "campaign_campaignformmeta"),
	CAMPAIGN_FORM_META(DatabaseTableType.SORMAS, CAMPAIGNS, "campaign_from_meta"),
	CAMPAIGN_FORM_DATA(DatabaseTableType.SORMAS, CAMPAIGNS, "campaign_form_data"),
	CAMPAIGN_DIAGRAM_DEFINITIONS(DatabaseTableType.SORMAS, CAMPAIGNS, "campaign_diagram_definitions"),

	EXTERNAL_MESSAGES(DatabaseTableType.EXTERNAL, "external_messages", dependingOnFeature(FeatureType.EXTERNAL_MESSAGES)),
	SAMPLE_REPORTS(DatabaseTableType.EXTERNAL, EXTERNAL_MESSAGES, "sample_reports"),
	TEST_REPORTS(DatabaseTableType.EXTERNAL, EXTERNAL_MESSAGES, "test_reports"),

	SORMAS_TO_SORMAS_ORIGIN_INFO(DatabaseTableType.EXTERNAL, null, "sormas_to_sormas_origin_info", dependingOnS2S()),
	SORMAS_TO_SORMAS_SHARE_INFO(DatabaseTableType.EXTERNAL, null, "sormas_to_sormas_share_info", dependingOnS2S()),
	SORMAS_TO_SORMAS_SHARE_REQUESTS(DatabaseTableType.EXTERNAL, null, "sormas_to_sormas_share_requests", dependingOnS2S()),
	SHARE_REQUEST_INFO(DatabaseTableType.EXTERNAL, null, "share_request_info", dependingOnS2S()),
	SHARE_REQUEST_INFO_SHARE_INFO(DatabaseTableType.EXTERNAL, SHARE_REQUEST_INFO, "sharerequestinfo_shareinfo"),

	EXTERNAL_SHARE_INFO(DatabaseTableType.EXTERNAL,
		null,
		"external_share_info",
		dependingOnConfiguration(ConfigFacade::isExternalSurveillanceToolGatewayConfigured)),

	USERS(DatabaseTableType.SORMAS, "users", null),
	USER_ROLES(DatabaseTableType.SORMAS, USERS, "userroles"),
	USERS_USERROLES(DatabaseTableType.SORMAS, USERS, "users_userroles"),
	USERROLES_USERRIGHTS(DatabaseTableType.SORMAS, USERS, "userroles_userrights"),
	USERROLES_EMAILNOTIFICATIONTYPES(DatabaseTableType.SORMAS, USERS, "userroles_emailnotificationtypes"),
	USERROLES_SMSNOTIFICATIONTYPES(DatabaseTableType.SORMAS, USERS, "userroles_smsnotificationtypes"),

	POPULATION_DATA(DatabaseTableType.INFRASTRUCTURE, "population_data", null),
	AGGREGATE_REPORTS(DatabaseTableType.SORMAS, "aggregate_reports", dependingOnFeature(FeatureType.AGGREGATE_REPORTING)),
	WEEKLY_REPORTS(DatabaseTableType.SORMAS, "weekly_reports", dependingOnFeature(FeatureType.WEEKLY_REPORTING)),
	WEEKLY_REPORT_ENTRIES(DatabaseTableType.SORMAS, WEEKLY_REPORTS, "weekly_report_entries"),

	DOCUMENTS(DatabaseTableType.SORMAS, "documents", dependingOnFeature(FeatureType.DOCUMENTS)),

	EXPORT_CONFIGURATIONS(DatabaseTableType.CONFIGURATION,
		"export_configurations",
		dependingOnFeature(
			FeatureType.CASE_SURVEILANCE,
			FeatureType.CONTACT_TRACING,
			FeatureType.EVENT_SURVEILLANCE,
			FeatureType.SAMPLES_LAB,
			FeatureType.TASK_MANAGEMENT,
			FeatureType.CASE_FOLLOWUP)),
	FEATURE_CONFIGURATIONS(DatabaseTableType.CONFIGURATION, "feature_configurations", null),
	DISEASE_CONFIGURATIONS(DatabaseTableType.CONFIGURATION, "disease_configurations", null),
	DELETION_CONFIGURATIONS(DatabaseTableType.CONFIGURATION, "deletion_configurations", null);

	private static BiFunction<List<FeatureConfigurationDto>, ConfigFacade, Boolean> dependingOnFeature(FeatureType... featureTypes) {
		return (featureConfigurations, configFacade) -> featureConfigurations.stream()
			.anyMatch(cc -> ArrayUtils.contains(featureTypes, cc.getFeatureType()) && cc.isEnabled());
	}

	private static BiFunction<List<FeatureConfigurationDto>, ConfigFacade, Boolean> dependingOnS2S() {
		return (featureConfigurations, configFacade) -> configFacade.isS2SConfigured();
	}

	private static BiFunction<List<FeatureConfigurationDto>, ConfigFacade, Boolean> dependingOnConfiguration(
		Function<ConfigFacade, Boolean> isConfigured) {
		return (featureConfigurations, configFacade) -> isConfigured.apply(configFacade);
	}

	private final DatabaseTableType databaseTableType;
	private final DatabaseTable parentTable;
	private final String fileName;
	private final BiFunction<List<FeatureConfigurationDto>, ConfigFacade, Boolean> enabledSupplier;

	DatabaseTable(
		DatabaseTableType databaseTableType,
		String fileName,
		BiFunction<List<FeatureConfigurationDto>, ConfigFacade, Boolean> enabledSupplier) {
		this(databaseTableType, null, fileName, enabledSupplier);
	}

	DatabaseTable(DatabaseTableType databaseTableType, DatabaseTable parentTable, String fileName) {
		this(databaseTableType, parentTable, fileName, null);
	}

	DatabaseTable(
		DatabaseTableType databaseTableType,
		DatabaseTable parentTable,
		String fileName,
		BiFunction<List<FeatureConfigurationDto>, ConfigFacade, Boolean> enabledSupplier) {
		this.databaseTableType = databaseTableType;
		this.parentTable = parentTable;
		this.fileName = fileName;
		this.enabledSupplier = enabledSupplier;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public DatabaseTableType getDatabaseTableType() {
		return databaseTableType;
	}

	public DatabaseTable getParentTable() {
		return parentTable;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isEnabled(List<FeatureConfigurationDto> featureConfigurations, ConfigFacade configFacade) {
		if (enabledSupplier != null) {
			return enabledSupplier.apply(featureConfigurations, configFacade);
		} else if (parentTable != null) {
			return parentTable.isEnabled(featureConfigurations, configFacade);
		}

		return true;
	}
}
