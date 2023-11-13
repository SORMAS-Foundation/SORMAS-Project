/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum UserRight {

	//@formatter:off
	CASE_VIEW(UserRightGroup.CASE, UserRight._PERSON_VIEW),
	CASE_CREATE(UserRightGroup.CASE, UserRight._CASE_VIEW),
	CASE_EDIT(UserRightGroup.CASE, UserRight._CASE_VIEW, UserRight._PERSON_EDIT),
	CASE_ARCHIVE(UserRightGroup.CASE, UserRight._CASE_VIEW),
	CASE_DELETE(UserRightGroup.CASE, UserRight._CASE_VIEW, UserRight._TASK_DELETE, UserRight._SAMPLE_DELETE, UserRight._VISIT_DELETE, UserRight._PERSON_DELETE, UserRight._TREATMENT_DELETE, UserRight._PRESCRIPTION_DELETE, UserRight._CLINICAL_VISIT_DELETE, UserRight._IMMUNIZATION_DELETE, UserRight._DOCUMENT_DELETE),
	CASE_IMPORT(UserRightGroup.CASE, UserRight._CASE_VIEW),
	CASE_EXPORT(UserRightGroup.CASE, UserRight._CASE_VIEW),
	/*
	 * Edit the investigation status - either by setting a respective task to done or by manually changing it in the case
	 */
	CASE_INVESTIGATE(UserRightGroup.CASE, UserRight._CASE_EDIT),
	/*
	 * Edit the classification and outcome of a case
	 */
	CASE_CLASSIFY(UserRightGroup.CASE, UserRight._CASE_EDIT),
	CASE_CHANGE_DISEASE(UserRightGroup.CASE, UserRight._CASE_EDIT),
	CASE_CHANGE_EPID_NUMBER(UserRightGroup.CASE, UserRight._CASE_EDIT),
	CASE_TRANSFER(UserRightGroup.CASE, UserRight._CASE_EDIT),
	CASE_REFER_FROM_POE(UserRightGroup.CASE, UserRight._CASE_EDIT),
	CASE_MERGE(UserRightGroup.CASE, UserRight._CASE_EDIT),
	CASE_SHARE(UserRightGroup.CASE, UserRight._CASE_VIEW),
	CASE_RESPONSIBLE(UserRightGroup.CASE, UserRight._CASE_EDIT),

	IMMUNIZATION_VIEW(UserRightGroup.IMMUNIZATION, UserRight._PERSON_VIEW),
	IMMUNIZATION_CREATE(UserRightGroup.IMMUNIZATION, UserRight._IMMUNIZATION_VIEW),
	IMMUNIZATION_EDIT(UserRightGroup.IMMUNIZATION, UserRight._IMMUNIZATION_VIEW, UserRight._PERSON_EDIT),
	IMMUNIZATION_ARCHIVE(UserRightGroup.IMMUNIZATION, UserRight._IMMUNIZATION_VIEW),
	IMMUNIZATION_DELETE(UserRightGroup.IMMUNIZATION, UserRight._IMMUNIZATION_VIEW, UserRight._PERSON_DELETE),

	PERSON_VIEW(UserRightGroup.PERSON),
	PERSON_EDIT(UserRightGroup.PERSON, UserRight._PERSON_VIEW),
	PERSON_DELETE(UserRightGroup.PERSON, UserRight._PERSON_VIEW, UserRight._VISIT_DELETE),
	PERSON_EXPORT(UserRightGroup.PERSON, UserRight._PERSON_VIEW),
	PERSON_CONTACT_DETAILS_DELETE(UserRightGroup.PERSON, UserRight._PERSON_EDIT),
	PERSON_MERGE(UserRightGroup.PERSON, UserRight._PERSON_VIEW),

	SAMPLE_VIEW(UserRightGroup.SAMPLE),
	SAMPLE_CREATE(UserRightGroup.SAMPLE, UserRight._SAMPLE_VIEW),
	SAMPLE_EDIT(UserRightGroup.SAMPLE, UserRight._SAMPLE_VIEW),
	SAMPLE_DELETE(UserRightGroup.SAMPLE, UserRight._SAMPLE_VIEW, UserRight._PATHOGEN_TEST_DELETE, UserRight._ADDITIONAL_TEST_DELETE),
	SAMPLE_EXPORT(UserRightGroup.SAMPLE, UserRight._SAMPLE_VIEW),
	SAMPLE_TRANSFER(UserRightGroup.SAMPLE, UserRight._SAMPLE_EDIT),
	SAMPLE_EDIT_NOT_OWNED(UserRightGroup.SAMPLE, UserRight._SAMPLE_EDIT),
	PERFORM_BULK_OPERATIONS_CASE_SAMPLES(UserRightGroup.SAMPLE, UserRight._SAMPLE_EDIT),

	PATHOGEN_TEST_CREATE(UserRightGroup.SAMPLE, UserRight._SAMPLE_VIEW),
	PATHOGEN_TEST_EDIT(UserRightGroup.SAMPLE, UserRight._SAMPLE_EDIT),
	PATHOGEN_TEST_DELETE(UserRightGroup.SAMPLE, UserRight._SAMPLE_VIEW),

	ADDITIONAL_TEST_VIEW(UserRightGroup.SAMPLE, UserRight._SAMPLE_VIEW),
	ADDITIONAL_TEST_CREATE(UserRightGroup.SAMPLE, UserRight._ADDITIONAL_TEST_VIEW),
	ADDITIONAL_TEST_EDIT(UserRightGroup.SAMPLE, UserRight._ADDITIONAL_TEST_VIEW),
	ADDITIONAL_TEST_DELETE(UserRightGroup.SAMPLE, UserRight._ADDITIONAL_TEST_VIEW),

	CONTACT_VIEW(UserRightGroup.CONTACT, UserRight._CASE_VIEW, UserRight._PERSON_VIEW),
	CONTACT_CREATE(UserRightGroup.CONTACT, UserRight._CONTACT_VIEW),
	CONTACT_EDIT(UserRightGroup.CONTACT, UserRight._CONTACT_VIEW, UserRight._PERSON_EDIT),
	CONTACT_ARCHIVE(UserRightGroup.CONTACT, UserRight._CONTACT_VIEW),
	CONTACT_DELETE(UserRightGroup.CONTACT, UserRight._CONTACT_VIEW, UserRight._TASK_DELETE, UserRight._SAMPLE_DELETE, UserRight._VISIT_DELETE, UserRight._PERSON_DELETE, UserRight._DOCUMENT_DELETE),
	CONTACT_IMPORT(UserRightGroup.CONTACT, UserRight._CONTACT_VIEW),
	CONTACT_EXPORT(UserRightGroup.CONTACT, UserRight._CONTACT_VIEW),
	// users that are allowed to convert a contact to a case need to be allowed to create a case,
	CONTACT_CONVERT(UserRightGroup.CONTACT, UserRight._CONTACT_EDIT, UserRight._CASE_CREATE),
	// reassign or remove the case from an existing contact
	CONTACT_REASSIGN_CASE(UserRightGroup.CONTACT, UserRight._CONTACT_EDIT),
	CONTACT_MERGE(UserRightGroup.CONTACT, UserRight._CONTACT_EDIT),
	CONTACT_RESPONSIBLE(UserRightGroup.CONTACT, UserRight._CONTACT_EDIT),

	VISIT_CREATE(UserRightGroup.VISIT),
	VISIT_EDIT(UserRightGroup.VISIT),
	VISIT_DELETE(UserRightGroup.VISIT),
	VISIT_EXPORT(UserRightGroup.VISIT),

	TASK_VIEW(UserRightGroup.TASK),
	TASK_CREATE(UserRightGroup.TASK, UserRight._TASK_VIEW),
	TASK_EDIT(UserRightGroup.TASK, UserRight._TASK_VIEW),
	TASK_DELETE(UserRightGroup.TASK, UserRight._TASK_VIEW),
	TASK_EXPORT(UserRightGroup.TASK, UserRight._TASK_VIEW),
	TASK_ASSIGN(UserRightGroup.TASK, UserRight._TASK_EDIT),
	TASK_ARCHIVE(UserRightGroup.TASK, UserRight._TASK_VIEW),

	ACTION_CREATE(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	ACTION_DELETE(UserRightGroup.EVENT, UserRight._EVENT_VIEW, UserRight._DOCUMENT_DELETE),
	ACTION_EDIT(UserRightGroup.EVENT, UserRight._EVENT_VIEW),

	EVENT_VIEW(UserRightGroup.EVENT),
	EVENT_CREATE(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	EVENT_EDIT(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	EVENT_ARCHIVE(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	EVENT_DELETE(UserRightGroup.EVENT, UserRight._EVENT_VIEW, UserRight._EVENTPARTICIPANT_DELETE, UserRight._TASK_DELETE, UserRight._ACTION_DELETE, UserRight._DOCUMENT_DELETE),
	EVENT_IMPORT(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	EVENT_EXPORT(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	PERFORM_BULK_OPERATIONS_EVENT(UserRightGroup.EVENT, UserRight._EVENT_EDIT),
	EVENT_RESPONSIBLE(UserRightGroup.EVENT, UserRight._EVENT_EDIT),

	EVENTPARTICIPANT_VIEW(UserRightGroup.EVENT, UserRight._EVENT_VIEW, UserRight._PERSON_VIEW),
	EVENTPARTICIPANT_CREATE(UserRightGroup.EVENT, UserRight._EVENTPARTICIPANT_VIEW),
	EVENTPARTICIPANT_EDIT(UserRightGroup.EVENT, UserRight._EVENTPARTICIPANT_VIEW, UserRight._PERSON_EDIT),
	EVENTPARTICIPANT_ARCHIVE(UserRightGroup.EVENT, UserRight._EVENTPARTICIPANT_VIEW),
	EVENTPARTICIPANT_DELETE(UserRightGroup.EVENT, UserRight._EVENTPARTICIPANT_VIEW, UserRight._SAMPLE_DELETE, UserRight._PERSON_DELETE),
	EVENTPARTICIPANT_IMPORT(UserRightGroup.EVENT, UserRight._EVENTPARTICIPANT_VIEW),
	PERFORM_BULK_OPERATIONS_EVENTPARTICIPANT(UserRightGroup.EVENT, UserRight._EVENTPARTICIPANT_EDIT),

	EVENTGROUP_CREATE(UserRightGroup.EVENT, UserRight._EVENT_VIEW, UserRight._EVENTGROUP_LINK),
	EVENTGROUP_EDIT(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	EVENTGROUP_ARCHIVE(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	EVENTGROUP_DELETE(UserRightGroup.EVENT, UserRight._EVENT_VIEW),
	EVENTGROUP_LINK(UserRightGroup.EVENT, UserRight._EVENT_EDIT),

	USER_VIEW(UserRightGroup.USER),
	USER_CREATE(UserRightGroup.USER, UserRight._USER_VIEW),
	USER_EDIT(UserRightGroup.USER, UserRight._USER_VIEW),

	USER_ROLE_VIEW(UserRightGroup.USER),
	USER_ROLE_EDIT(UserRightGroup.USER, UserRight._USER_ROLE_VIEW),
	USER_ROLE_DELETE(UserRightGroup.USER, UserRight._USER_ROLE_VIEW),

	STATISTICS_ACCESS(UserRightGroup.STATISTICS),
	STATISTICS_EXPORT(UserRightGroup.STATISTICS, UserRight._STATISTICS_ACCESS),

	INFRASTRUCTURE_VIEW(UserRightGroup.INFRASTRUCTURE),
	INFRASTRUCTURE_CREATE(UserRightGroup.INFRASTRUCTURE, UserRight._INFRASTRUCTURE_VIEW),
	INFRASTRUCTURE_EDIT(UserRightGroup.INFRASTRUCTURE, UserRight._INFRASTRUCTURE_VIEW),
	INFRASTRUCTURE_ARCHIVE(UserRightGroup.INFRASTRUCTURE, UserRight._INFRASTRUCTURE_VIEW),
	INFRASTRUCTURE_IMPORT(UserRightGroup.INFRASTRUCTURE, UserRight._INFRASTRUCTURE_VIEW),
	INFRASTRUCTURE_EXPORT(UserRightGroup.INFRASTRUCTURE, UserRight._INFRASTRUCTURE_VIEW),
	POPULATION_MANAGE(UserRightGroup.INFRASTRUCTURE, UserRight._INFRASTRUCTURE_VIEW),

	DASHBOARD_SURVEILLANCE_VIEW(UserRightGroup.DASHBOARD, UserRight._CASE_VIEW),
	DASHBOARD_CONTACT_VIEW(UserRightGroup.DASHBOARD, UserRight._CONTACT_VIEW),
	DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS(UserRightGroup.DASHBOARD, UserRight._DASHBOARD_CONTACT_VIEW),
	DASHBOARD_CAMPAIGNS_VIEW(UserRightGroup.DASHBOARD, UserRight._CAMPAIGN_VIEW),
	DASHBOARD_SAMPLES_VIEW(UserRightGroup.DASHBOARD, UserRight._SAMPLE_VIEW),

	CASE_CLINICIAN_VIEW(UserRightGroup.CASE_MANAGEMENT, UserRight._CASE_VIEW),

	THERAPY_VIEW(UserRightGroup.CASE_MANAGEMENT, UserRight._CASE_VIEW),
	PRESCRIPTION_CREATE(UserRightGroup.CASE_MANAGEMENT, UserRight._THERAPY_VIEW),
	PRESCRIPTION_EDIT(UserRightGroup.CASE_MANAGEMENT, UserRight._THERAPY_VIEW),
	PRESCRIPTION_DELETE(UserRightGroup.CASE_MANAGEMENT, UserRight._THERAPY_VIEW),
	TREATMENT_CREATE(UserRightGroup.CASE_MANAGEMENT, UserRight._THERAPY_VIEW),
	TREATMENT_EDIT(UserRightGroup.CASE_MANAGEMENT, UserRight._THERAPY_VIEW),
	TREATMENT_DELETE(UserRightGroup.CASE_MANAGEMENT, UserRight._THERAPY_VIEW),

	CLINICAL_COURSE_VIEW(UserRightGroup.CASE_MANAGEMENT, UserRight._THERAPY_VIEW),
	CLINICAL_COURSE_EDIT(UserRightGroup.CASE_MANAGEMENT, UserRight._CLINICAL_COURSE_VIEW),
	CLINICAL_VISIT_CREATE(UserRightGroup.CASE_MANAGEMENT, UserRight._CLINICAL_COURSE_VIEW),
	CLINICAL_VISIT_EDIT(UserRightGroup.CASE_MANAGEMENT, UserRight._CLINICAL_COURSE_VIEW),
	CLINICAL_VISIT_DELETE(UserRightGroup.CASE_MANAGEMENT, UserRight._CLINICAL_COURSE_VIEW),

	PORT_HEALTH_INFO_VIEW(UserRightGroup.PORT_HEALTH, UserRight._CASE_VIEW),
	PORT_HEALTH_INFO_EDIT(UserRightGroup.PORT_HEALTH, UserRight._PORT_HEALTH_INFO_VIEW),

	WEEKLYREPORT_VIEW(UserRightGroup.AGGREGATED_REPORTING),
	WEEKLYREPORT_CREATE(UserRightGroup.AGGREGATED_REPORTING, UserRight._WEEKLYREPORT_VIEW),
	AGGREGATE_REPORT_VIEW(UserRightGroup.AGGREGATED_REPORTING),
	AGGREGATE_REPORT_EDIT(UserRightGroup.AGGREGATED_REPORTING, UserRight._AGGREGATE_REPORT_VIEW),
	AGGREGATE_REPORT_EXPORT(UserRightGroup.AGGREGATED_REPORTING, UserRight._AGGREGATE_REPORT_VIEW),

	SEE_PERSONAL_DATA_IN_JURISDICTION(UserRightGroup.DATA_PROTECTION),
	SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION(UserRightGroup.DATA_PROTECTION),
	SEE_SENSITIVE_DATA_IN_JURISDICTION(UserRightGroup.DATA_PROTECTION),
	SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION(UserRightGroup.DATA_PROTECTION),

	CAMPAIGN_VIEW(UserRightGroup.CAMPAIGN),
	CAMPAIGN_EDIT(UserRightGroup.CAMPAIGN, UserRight._CAMPAIGN_VIEW),
	CAMPAIGN_ARCHIVE(UserRightGroup.CAMPAIGN, UserRight._CAMPAIGN_VIEW),
	CAMPAIGN_DELETE(UserRightGroup.CAMPAIGN, UserRight._CAMPAIGN_VIEW, UserRight._CAMPAIGN_FORM_DATA_DELETE),

	CAMPAIGN_FORM_DATA_VIEW(UserRightGroup.CAMPAIGN),
	CAMPAIGN_FORM_DATA_EDIT(UserRightGroup.CAMPAIGN, UserRight._CAMPAIGN_FORM_DATA_VIEW),
	CAMPAIGN_FORM_DATA_ARCHIVE(UserRightGroup.CAMPAIGN, UserRight._CAMPAIGN_FORM_DATA_VIEW),
	CAMPAIGN_FORM_DATA_DELETE(UserRightGroup.CAMPAIGN, UserRight._CAMPAIGN_FORM_DATA_VIEW),
	CAMPAIGN_FORM_DATA_EXPORT(UserRightGroup.CAMPAIGN, UserRight._CAMPAIGN_FORM_DATA_VIEW),

	TRAVEL_ENTRY_MANAGEMENT_ACCESS(UserRightGroup.TRAVEL_ENTRY),
	TRAVEL_ENTRY_VIEW(UserRightGroup.TRAVEL_ENTRY, UserRight._TRAVEL_ENTRY_MANAGEMENT_ACCESS, UserRight._PERSON_VIEW),
	TRAVEL_ENTRY_CREATE(UserRightGroup.TRAVEL_ENTRY, UserRight._TRAVEL_ENTRY_VIEW),
	TRAVEL_ENTRY_EDIT(UserRightGroup.TRAVEL_ENTRY, UserRight._TRAVEL_ENTRY_VIEW, UserRight._PERSON_EDIT),
	TRAVEL_ENTRY_ARCHIVE(UserRightGroup.TRAVEL_ENTRY, UserRight._TRAVEL_ENTRY_VIEW),
	TRAVEL_ENTRY_DELETE(UserRightGroup.TRAVEL_ENTRY, UserRight._TRAVEL_ENTRY_VIEW, UserRight._TASK_DELETE, UserRight._DOCUMENT_DELETE, UserRight._PERSON_DELETE),

	ENVIRONMENT_VIEW(UserRightGroup.ENVIRONMENT),
	ENVIRONMENT_CREATE(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_VIEW),
	ENVIRONMENT_EDIT(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_VIEW),
	ENVIRONMENT_ARCHIVE(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_VIEW),
	ENVIRONMENT_DELETE(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_VIEW),
	ENVIRONMENT_IMPORT(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_CREATE),
	ENVIRONMENT_EXPORT(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_VIEW),

	ENVIRONMENT_SAMPLE_VIEW(UserRightGroup.ENVIRONMENT),
	ENVIRONMENT_SAMPLE_CREATE(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_SAMPLE_VIEW),
	ENVIRONMENT_SAMPLE_EDIT(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_SAMPLE_VIEW),
	ENVIRONMENT_SAMPLE_EDIT_DISPATCH(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_SAMPLE_EDIT),
	ENVIRONMENT_SAMPLE_EDIT_RECEIVAL(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_SAMPLE_EDIT),
	ENVIRONMENT_SAMPLE_DELETE(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_SAMPLE_VIEW),
	ENVIRONMENT_SAMPLE_IMPORT(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_SAMPLE_CREATE),
	ENVIRONMENT_SAMPLE_EXPORT(UserRightGroup.ENVIRONMENT, UserRight._ENVIRONMENT_SAMPLE_VIEW),
	
	DOCUMENT_VIEW(UserRightGroup.DOCUMENT),
	DOCUMENT_UPLOAD(UserRightGroup.DOCUMENT, UserRight._DOCUMENT_VIEW),
	DOCUMENT_DELETE(UserRightGroup.DOCUMENT, UserRight._DOCUMENT_VIEW),

	PERFORM_BULK_OPERATIONS(UserRightGroup.GENERAL),
	PERFORM_BULK_OPERATIONS_PSEUDONYM(UserRightGroup.GENERAL),
	QUARANTINE_ORDER_CREATE(UserRightGroup.GENERAL),
	SORMAS_REST(UserRightGroup.GENERAL),
	SORMAS_UI(UserRightGroup.GENERAL),

	DATABASE_EXPORT_ACCESS(UserRightGroup.EXPORT, UserRight._STATISTICS_ACCESS),
	EXPORT_DATA_PROTECTION_DATA(UserRightGroup.EXPORT),
	BAG_EXPORT(UserRightGroup.EXPORT),

	SEND_MANUAL_EXTERNAL_MESSAGES(UserRightGroup.EXTERNAL),
	MANAGE_EXTERNAL_SYMPTOM_JOURNAL(UserRightGroup.EXTERNAL),
	EXTERNAL_VISITS(UserRightGroup.EXTERNAL),

	SORMAS_TO_SORMAS_CLIENT(UserRightGroup.EXTERNAL),
	SORMAS_TO_SORMAS_SHARE(UserRightGroup.EXTERNAL),
	SORMAS_TO_SORMAS_PROCESS(UserRightGroup.EXTERNAL),

	EXTERNAL_SURVEILLANCE_SHARE(UserRightGroup.EXTERNAL),
	EXTERNAL_SURVEILLANCE_DELETE(UserRightGroup.EXTERNAL),

	EXTERNAL_MESSAGE_VIEW(UserRightGroup.EXTERNAL),
	EXTERNAL_MESSAGE_PROCESS(UserRightGroup.EXTERNAL, UserRight._EXTERNAL_MESSAGE_VIEW,
			UserRight._SAMPLE_CREATE, UserRight._SAMPLE_EDIT, UserRight._PATHOGEN_TEST_CREATE, UserRight._PATHOGEN_TEST_EDIT, UserRight._PATHOGEN_TEST_DELETE,
			UserRight._IMMUNIZATION_CREATE, UserRight._IMMUNIZATION_EDIT, UserRight._IMMUNIZATION_DELETE),
	EXTERNAL_MESSAGE_PUSH(UserRightGroup.EXTERNAL),
	EXTERNAL_MESSAGE_DELETE(UserRightGroup.EXTERNAL, UserRight._EXTERNAL_MESSAGE_VIEW),
	PERFORM_BULK_OPERATIONS_EXTERNAL_MESSAGES(UserRightGroup.EXTERNAL, UserRight._EXTERNAL_MESSAGE_VIEW),

	OUTBREAK_VIEW(UserRightGroup.CONFIGURATION),
	OUTBREAK_EDIT(UserRightGroup.CONFIGURATION, UserRight._OUTBREAK_VIEW),
	MANAGE_PUBLIC_EXPORT_CONFIGURATION(UserRightGroup.CONFIGURATION),
	DOCUMENT_TEMPLATE_MANAGEMENT(UserRightGroup.CONFIGURATION),
	LINE_LISTING_CONFIGURE(UserRightGroup.CONFIGURATION),
	DEV_MODE(UserRightGroup.CONFIGURATION),
	CUSTOMIZABLE_ENUM_MANAGEMENT(UserRightGroup.CONFIGURATION);

	//@formatter:on

	/*
	 * Hint for SonarQube issues:
	 * 1. java:S115: Violation of name convention for String constants of this class is accepted: Close as false positive.
	 */
	public static final String _SYSTEM = "SYSTEM";
	public static final String _CASE_CREATE = "CASE_CREATE";
	public static final String _CASE_VIEW = "CASE_VIEW";
	public static final String _CASE_EDIT = "CASE_EDIT";
	public static final String _CASE_TRANSFER = "CASE_TRANSFER";
	public static final String _CASE_REFER_FROM_POE = "CASE_REFER_FROM_POE";
	public static final String _CASE_INVESTIGATE = "CASE_INVESTIGATE";
	public static final String _CASE_CLASSIFY = "CASE_CLASSIFY";
	public static final String _CASE_CHANGE_DISEASE = "CASE_CHANGE_DISEASE";
	public static final String _CASE_CHANGE_EPID_NUMBER = "CASE_CHANGE_EPID_NUMBER";
	public static final String _CASE_DELETE = "CASE_DELETE";
	public static final String _CASE_IMPORT = "CASE_IMPORT";
	public static final String _CASE_EXPORT = "CASE_EXPORT";
	public static final String _CASE_SHARE = "CASE_SHARE";
	public static final String _CASE_ARCHIVE = "CASE_ARCHIVE";
	public static final String _CASE_MERGE = "CASE_MERGE";
	public static final String _IMMUNIZATION_VIEW = "IMMUNIZATION_VIEW";
	public static final String _IMMUNIZATION_CREATE = "IMMUNIZATION_CREATE";
	public static final String _IMMUNIZATION_EDIT = "IMMUNIZATION_EDIT";
	public static final String _IMMUNIZATION_DELETE = "IMMUNIZATION_DELETE";
	public static final String _IMMUNIZATION_ARCHIVE = "IMMUNIZATION_ARCHIVE";
	public static final String _PERSON_VIEW = "PERSON_VIEW";
	public static final String _PERSON_EDIT = "PERSON_EDIT";
	public static final String _PERSON_DELETE = "PERSON_DELETE";
	public static final String _PERSON_CONTACT_DETAILS_DELETE = "PERSON_CONTACT_DETAILS_DELETE";
	public static final String _PERSON_MERGE = "PERSON_MERGE";
	public static final String _PERSON_EXPORT = "PERSON_EXPORT";
	public static final String _SAMPLE_CREATE = "SAMPLE_CREATE";
	public static final String _SAMPLE_VIEW = "SAMPLE_VIEW";
	public static final String _SAMPLE_EDIT = "SAMPLE_EDIT";
	public static final String _SAMPLE_EDIT_NOT_OWNED = "SAMPLE_EDIT_NOT_OWNED";
	public static final String _SAMPLE_DELETE = "SAMPLE_DELETE";
	public static final String _SAMPLE_TRANSFER = "SAMPLE_TRANSFER";
	public static final String _SAMPLE_EXPORT = "SAMPLE_EXPORT";
	public static final String _PATHOGEN_TEST_CREATE = "PATHOGEN_TEST_CREATE";
	public static final String _PATHOGEN_TEST_EDIT = "PATHOGEN_TEST_EDIT";
	public static final String _PATHOGEN_TEST_DELETE = "PATHOGEN_TEST_DELETE";
	public static final String _ADDITIONAL_TEST_VIEW = "ADDITIONAL_TEST_VIEW";
	public static final String _ADDITIONAL_TEST_CREATE = "ADDITIONAL_TEST_CREATE";
	public static final String _ADDITIONAL_TEST_EDIT = "ADDITIONAL_TEST_EDIT";
	public static final String _ADDITIONAL_TEST_DELETE = "ADDITIONAL_TEST_DELETE";
	public static final String _CONTACT_CREATE = "CONTACT_CREATE";
	public static final String _CONTACT_IMPORT = "CONTACT_IMPORT";
	public static final String _CONTACT_VIEW = "CONTACT_VIEW";
	public static final String _CONTACT_ARCHIVE = "CONTACT_ARCHIVE";
	public static final String _CONTACT_EDIT = "CONTACT_EDIT";
	public static final String _CONTACT_DELETE = "CONTACT_DELETE";
	public static final String _CONTACT_CONVERT = "CONTACT_CONVERT";
	public static final String _CONTACT_EXPORT = "CONTACT_EXPORT";
	public static final String _CONTACT_REASSIGN_CASE = "CONTACT_REASSIGN_CASE";
	public static final String _CONTACT_MERGE = "CONTACT_MERGE";
	public static final String _MANAGE_EXTERNAL_SYMPTOM_JOURNAL = "MANAGE_EXTERNAL_SYMPTOM_JOURNAL";
	public static final String _VISIT_CREATE = "VISIT_CREATE";
	public static final String _VISIT_EDIT = "VISIT_EDIT";
	public static final String _VISIT_DELETE = "VISIT_DELETE";
	public static final String _VISIT_EXPORT = "VISIT_EXPORT";
	public static final String _TASK_CREATE = "TASK_CREATE";
	public static final String _TASK_VIEW = "TASK_VIEW";
	public static final String _TASK_EDIT = "TASK_EDIT";
	public static final String _TASK_ASSIGN = "TASK_ASSIGN";
	public static final String _TASK_DELETE = "TASK_DELETE";
	public static final String _TASK_EXPORT = "TASK_EXPORT";
	public static final String _TASK_ARCHIVE = "TASK_ARCHIVE";
	public static final String _ACTION_CREATE = "ACTION_CREATE";
	public static final String _ACTION_DELETE = "ACTION_DELETE";
	public static final String _ACTION_EDIT = "ACTION_EDIT";
	public static final String _EVENT_CREATE = "EVENT_CREATE";
	public static final String _EVENT_VIEW = "EVENT_VIEW";
	public static final String _EVENT_EDIT = "EVENT_EDIT";
	public static final String _EVENT_IMPORT = "EVENT_IMPORT";
	public static final String _EVENT_EXPORT = "EVENT_EXPORT";
	public static final String _EVENT_ARCHIVE = "EVENT_ARCHIVE";
	public static final String _EVENT_DELETE = "EVENT_DELETE";
	public static final String _EVENTPARTICIPANT_ARCHIVE = "EVENTPARTICIPANT_ARCHIVE";
	public static final String _EVENTPARTICIPANT_CREATE = "EVENTPARTICIPANT_CREATE";
	public static final String _EVENTPARTICIPANT_EDIT = "EVENTPARTICIPANT_EDIT";
	public static final String _EVENTPARTICIPANT_DELETE = "EVENTPARTICIPANT_DELETE";
	public static final String _EVENTPARTICIPANT_IMPORT = "EVENTPARTICIPANT_IMPORT";
	public static final String _EVENTPARTICIPANT_VIEW = "EVENTPARTICIPANT_VIEW";
	public static final String _EVENTGROUP_CREATE = "EVENTGROUP_CREATE";
	public static final String _EVENTGROUP_EDIT = "EVENTGROUP_EDIT";
	public static final String _EVENTGROUP_LINK = "EVENTGROUP_LINK";
	public static final String _EVENTGROUP_ARCHIVE = "EVENTGROUP_ARCHIVE";
	public static final String _EVENTGROUP_DELETE = "EVENTGROUP_DELETE";
	public static final String _WEEKLYREPORT_CREATE = "WEEKLYREPORT_CREATE";
	public static final String _WEEKLYREPORT_VIEW = "WEEKLYREPORT_VIEW";
	public static final String _USER_CREATE = "USER_CREATE";
	public static final String _USER_EDIT = "USER_EDIT";
	public static final String _USER_VIEW = "USER_VIEW";
	public static final String _USER_ROLE_EDIT = "USER_ROLE_EDIT";
	public static final String _USER_ROLE_DELETE = "USER_ROLE_DELETE";
	public static final String _USER_ROLE_VIEW = "USER_ROLE_VIEW";
	public static final String _SEND_MANUAL_EXTERNAL_MESSAGES = "SEND_MANUAL_EXTERNAL_MESSAGES";
	public static final String _STATISTICS_ACCESS = "STATISTICS_ACCESS";
	public static final String _STATISTICS_EXPORT = "STATISTICS_EXPORT";
	public static final String _DATABASE_EXPORT_ACCESS = "DATABASE_EXPORT_ACCESS";
	public static final String _PERFORM_BULK_OPERATIONS = "PERFORM_BULK_OPERATIONS";
	public static final String _PERFORM_BULK_OPERATIONS_EVENT = "PERFORM_BULK_OPERATIONS_EVENT";
	public static final String _MANAGE_PUBLIC_EXPORT_CONFIGURATION = "MANAGE_PUBLIC_EXPORT_CONFIGURATION";
	public static final String _PERFORM_BULK_OPERATIONS_CASE_SAMPLES = "PERFORM_BULK_OPERATIONS_CASE_SAMPLES";
	public static final String _PERFORM_BULK_OPERATIONS_EXTERNAL_MESSAGES = "PERFORM_BULK_OPERATIONS_EXTERNAL_MESSAGES";
	public static final String _PERFORM_BULK_OPERATIONS_PSEUDONYM = "PERFORM_BULK_OPERATIONS_PSEUDONYM";
	public static final String _INFRASTRUCTURE_CREATE = "INFRASTRUCTURE_CREATE";
	public static final String _INFRASTRUCTURE_EDIT = "INFRASTRUCTURE_EDIT";
	public static final String _INFRASTRUCTURE_VIEW = "INFRASTRUCTURE_VIEW";
	public static final String _INFRASTRUCTURE_EXPORT = "INFRASTRUCTURE_EXPORT";
	public static final String _INFRASTRUCTURE_IMPORT = "INFRASTRUCTURE_IMPORT";
	public static final String _INFRASTRUCTURE_ARCHIVE = "INFRASTRUCTURE_ARCHIVE";
	public static final String _DASHBOARD_SURVEILLANCE_VIEW = "DASHBOARD_SURVEILLANCE_VIEW";
	public static final String _DASHBOARD_CONTACT_VIEW = "DASHBOARD_CONTACT_VIEW";
	public static final String _DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS = "DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS";
	public static final String _DASHBOARD_CAMPAIGNS_VIEW = "DASHBOARD_CAMPAIGNS_VIEW";
	public static final String _DASHBOARD_SAMPLES_VIEW = "DASHBOARD_SAMPLES_VIEW";
	public static final String _CASE_CLINICIAN_VIEW = "CASE_CLINICIAN_VIEW";
	public static final String _THERAPY_VIEW = "THERAPY_VIEW";
	public static final String _PRESCRIPTION_CREATE = "PRESCRIPTION_CREATE";
	public static final String _PRESCRIPTION_EDIT = "PRESCRIPTION_EDIT";
	public static final String _PRESCRIPTION_DELETE = "PRESCRIPTION_DELETE";
	public static final String _TREATMENT_CREATE = "TREATMENT_CREATE";
	public static final String _TREATMENT_EDIT = "TREATMENT_EDIT";
	public static final String _TREATMENT_DELETE = "TREATMENT_DELETE";
	public static final String _CLINICAL_COURSE_VIEW = "CLINICAL_COURSE_VIEW";
	public static final String _CLINICAL_COURSE_EDIT = "CLINICAL_COURSE_EDIT";
	public static final String _CLINICAL_VISIT_CREATE = "CLINICAL_VISIT_CREATE";
	public static final String _CLINICAL_VISIT_EDIT = "CLINICAL_VISIT_EDIT";
	public static final String _CLINICAL_VISIT_DELETE = "CLINICAL_VISIT_DELETE";
	public static final String _PORT_HEALTH_INFO_VIEW = "PORT_HEALTH_INFO_VIEW";
	public static final String _PORT_HEALTH_INFO_EDIT = "PORT_HEALTH_INFO_EDIT";
	public static final String _POPULATION_MANAGE = "POPULATION_MANAGE";
	public static final String _DOCUMENT_TEMPLATE_MANAGEMENT = "DOCUMENT_TEMPLATE_MANAGEMENT";
	public static final String _QUARANTINE_ORDER_CREATE = "QUARANTINE_ORDER_CREATE";
	public static final String _LINE_LISTING_CONFIGURE = "LINE_LISTING_CONFIGURE";
	public static final String _AGGREGATE_REPORT_VIEW = "AGGREGATE_REPORT_VIEW";
	public static final String _AGGREGATE_REPORT_EXPORT = "AGGREGATE_REPORT_EXPORT";
	public static final String _AGGREGATE_REPORT_EDIT = "AGGREGATE_REPORT_EDIT";
	public static final String _SEE_PERSONAL_DATA_IN_JURISDICTION = "SEE_PERSONAL_DATA_IN_JURISDICTION";
	public static final String _SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION = "SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION";
	public static final String _SEE_SENSITIVE_DATA_IN_JURISDICTION = "SEE_SENSITIVE_DATA_IN_JURISDICTION";
	public static final String _SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION = "SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION";
	public static final String _CAMPAIGN_VIEW = "CAMPAIGN_VIEW";
	public static final String _CAMPAIGN_EDIT = "CAMPAIGN_EDIT";
	public static final String _CAMPAIGN_ARCHIVE = "CAMPAIGN_ARCHIVE";
	public static final String _CAMPAIGN_DELETE = "CAMPAIGN_DELETE";
	public static final String _CAMPAIGN_FORM_DATA_VIEW = "CAMPAIGN_FORM_DATA_VIEW";
	public static final String _CAMPAIGN_FORM_DATA_EDIT = "CAMPAIGN_FORM_DATA_EDIT";
	public static final String _CAMPAIGN_FORM_DATA_ARCHIVE = "CAMPAIGN_FORM_DATA_ARCHIVE";
	public static final String _CAMPAIGN_FORM_DATA_DELETE = "CAMPAIGN_FORM_DATA_DELETE";
	public static final String _CAMPAIGN_FORM_DATA_EXPORT = "CAMPAIGN_FORM_DATA_EXPORT";
	public static final String _BAG_EXPORT = "BAG_EXPORT";
	public static final String _SORMAS_TO_SORMAS_SHARE = "SORMAS_TO_SORMAS_SHARE";
	public static final String _SORMAS_TO_SORMAS_PROCESS = "SORMAS_TO_SORMAS_PROCESS";
	public static final String _EXTERNAL_SURVEILLANCE_SHARE = "EXTERNAL_SURVEILLANCE_SHARE";
	public static final String _EXTERNAL_SURVEILLANCE_DELETE = "EXTERNAL_SURVEILLANCE_DELETE";
	public static final String _EXTERNAL_MESSAGE_VIEW = "EXTERNAL_MESSAGE_VIEW";
	public static final String _EXTERNAL_MESSAGE_PROCESS = "EXTERNAL_MESSAGE_PROCESS";
	public static final String _EXTERNAL_MESSAGE_PUSH = "EXTERNAL_MESSAGE_PUSH";
	public static final String _EXTERNAL_MESSAGE_DELETE = "EXTERNAL_MESSAGE_DELETE";
	public static final String _TRAVEL_ENTRY_MANAGEMENT_ACCESS = "TRAVEL_ENTRY_MANAGEMENT_ACCESS";
	public static final String _TRAVEL_ENTRY_VIEW = "TRAVEL_ENTRY_VIEW";
	public static final String _TRAVEL_ENTRY_CREATE = "TRAVEL_ENTRY_CREATE";
	public static final String _TRAVEL_ENTRY_EDIT = "TRAVEL_ENTRY_EDIT";
	public static final String _TRAVEL_ENTRY_DELETE = "TRAVEL_ENTRY_DELETE";
	public static final String _TRAVEL_ENTRY_ARCHIVE = "TRAVEL_ENTRY_ARCHIVE";

	public static final String _ENVIRONMENT_VIEW = "ENVIRONMENT_VIEW";
	public static final String _ENVIRONMENT_CREATE = "ENVIRONMENT_CREATE";
	public static final String _ENVIRONMENT_EDIT = "ENVIRONMENT_EDIT";
	public static final String _ENVIRONMENT_ARCHIVE = "ENVIRONMENT_ARCHIVE";
	public static final String _ENVIRONMENT_DELETE = "ENVIRONMENT_DELETE";
	public static final String _ENVIRONMENT_IMPORT = "ENVIRONMENT_IMPORT";
	public static final String _ENVIRONMENT_EXPORT = "ENVIRONMENT_EXPORT";

	public static final String _ENVIRONMENT_SAMPLE_VIEW = "ENVIRONMENT_SAMPLE_VIEW";
	public static final String _ENVIRONMENT_SAMPLE_EDIT = "ENVIRONMENT_SAMPLE_EDIT";
	public static final String _ENVIRONMENT_SAMPLE_CREATE = "ENVIRONMENT_SAMPLE_CREATE";
	public static final String _ENVIRONMENT_SAMPLE_EDIT_DISPATCH = "ENVIRONMENT_SAMPLE_EDIT_DISPATCH";
	public static final String _ENVIRONMENT_SAMPLE_EDIT_RECEIVAL = "ENVIRONMENT_SAMPLE_EDIT_RECEIVAL";
	public static final String _ENVIRONMENT_SAMPLE_DELETE = "ENVIRONMENT_SAMPLE_DELETE";

	public static final String _DOCUMENT_VIEW = "DOCUMENT_VIEW";
	public static final String _DOCUMENT_UPLOAD = "DOCUMENT_UPLOAD";
	public static final String _DOCUMENT_DELETE = "DOCUMENT_DELETE";
	public static final String _EXPORT_DATA_PROTECTION_DATA = "EXPORT_DATA_PROTECTION_DATA";
	public static final String _OUTBREAK_VIEW = "OUTBREAK_VIEW";
	public static final String _OUTBREAK_EDIT = "OUTBREAK_EDIT";
	public static final String _SORMAS_REST = "SORMAS_REST";
	public static final String _SORMAS_UI = "SORMAS_UI";
	public static final String _SORMAS_TO_SORMAS_CLIENT = "SORMAS_TO_SORMAS_CLIENT";
	public static final String _EXTERNAL_VISITS = "EXTERNAL_VISITS";
	public static final String _DEV_MODE = "DEV_MODE";
	public static final String _CUSTOMIZABLE_ENUM_MANAGEMENT = "CUSTOMIZABLE_ENUM_MANAGEMENT";

	private static final Map<UserRight, Set<UserRight>> userRightDependencies = buildUserRightDependencies();

	private final UserRightGroup userRightGroup;

	private final String[] requiredUserRights;

	UserRight(UserRightGroup userRightGroup, String... requiredUserRights) {
		this.userRightGroup = userRightGroup;
		this.requiredUserRights = requiredUserRights;
	}

	public UserRightGroup getUserRightGroup() {
		return userRightGroup;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String getDescription() {
		return I18nProperties.getEnumDescription(this);
	}

	private static Map<UserRight, Set<UserRight>> buildUserRightDependencies() {
		Map<UserRight, Set<UserRight>> dependencies = new HashMap<>(UserRight.values().length);

		Stream.of(UserRight.values()).forEach(userRight -> {
			Set<UserRight> requiredUserRights = getRequiredUserRightsForCache(userRight, dependencies);

			dependencies.put(userRight, requiredUserRights);
		});

		return dependencies;
	}

	private static Set<UserRight> getRequiredUserRightsForCache(UserRight userRight, Map<UserRight, Set<UserRight>> dependencies) {
		Set<UserRight> requiredRights = Stream.of(userRight.requiredUserRights).map(UserRight::valueOf).collect(Collectors.toSet());

		Set<UserRight> requiredParentRights = requiredRights.stream()
			.map(r -> dependencies.containsKey(r) ? dependencies.get(r) : getRequiredUserRightsForCache(r, dependencies))
			.flatMap(Set::stream)
			.collect(Collectors.toSet());
		requiredRights.addAll(requiredParentRights);

		return requiredRights;
	}

	public static Set<UserRight> getRequiredUserRights(Set<UserRight> userRights) {
		return userRights.stream().map(UserRight::getRequiredUserRights).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	/**
	 * Appends the required user rights. The result also includes the passed user rights.
	 */
	public static Set<UserRight> getWithRequiredUserRights(UserRight... userRights) {
		return Stream.concat(Arrays.stream(userRights), Arrays.stream(userRights).map(UserRight::getRequiredUserRights).flatMap(Collection::stream))
			.collect(Collectors.toSet());
	}

	public Set<UserRight> getRequiredUserRights() {
		return userRightDependencies.get(this);
	}

	public static Set<UserRight> getUserRightsOfGroup(UserRightGroup userRightGroup) {
		return Arrays.stream(values()).filter(userRight -> userRight.getUserRightGroup() == userRightGroup).collect(Collectors.toSet());
	}

	/**
	 * Get which required user rights are not already in the passed set.
	 */
	public static Set<UserRight> requiredRightFromUserRights(UserRight userRight, Set<UserRight> userRights) {

		Set<UserRight> requiredRights = new HashSet<>();
		for (Map.Entry<UserRight, Set<UserRight>> entry : userRightDependencies.entrySet()) {
			UserRight keyRight = entry.getKey();
			if (entry.getValue().contains(userRight) && userRights.contains(keyRight)) {
				requiredRights.add(keyRight);
			}
		}
		return requiredRights;
	}
}
