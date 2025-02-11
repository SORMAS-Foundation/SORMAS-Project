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

import static de.symeda.sormas.api.user.UserRight.ACTION_CREATE;
import static de.symeda.sormas.api.user.UserRight.ACTION_DELETE;
import static de.symeda.sormas.api.user.UserRight.ACTION_EDIT;
import static de.symeda.sormas.api.user.UserRight.ADDITIONAL_TEST_CREATE;
import static de.symeda.sormas.api.user.UserRight.ADDITIONAL_TEST_DELETE;
import static de.symeda.sormas.api.user.UserRight.ADDITIONAL_TEST_EDIT;
import static de.symeda.sormas.api.user.UserRight.ADDITIONAL_TEST_VIEW;
import static de.symeda.sormas.api.user.UserRight.AGGREGATE_REPORT_EDIT;
import static de.symeda.sormas.api.user.UserRight.AGGREGATE_REPORT_EXPORT;
import static de.symeda.sormas.api.user.UserRight.AGGREGATE_REPORT_VIEW;
import static de.symeda.sormas.api.user.UserRight.BAG_EXPORT;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_DELETE;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_EDIT;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_FORM_DATA_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_FORM_DATA_DELETE;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_FORM_DATA_EDIT;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_FORM_DATA_EXPORT;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_FORM_DATA_VIEW;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_FORM_DATA_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_VIEW;
import static de.symeda.sormas.api.user.UserRight.CAMPAIGN_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.CASE_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.CASE_CHANGE_DISEASE;
import static de.symeda.sormas.api.user.UserRight.CASE_CHANGE_EPID_NUMBER;
import static de.symeda.sormas.api.user.UserRight.CASE_CLASSIFY;
import static de.symeda.sormas.api.user.UserRight.CASE_CLINICIAN_VIEW;
import static de.symeda.sormas.api.user.UserRight.CASE_CREATE;
import static de.symeda.sormas.api.user.UserRight.CASE_DELETE;
import static de.symeda.sormas.api.user.UserRight.CASE_EDIT;
import static de.symeda.sormas.api.user.UserRight.CASE_EXPORT;
import static de.symeda.sormas.api.user.UserRight.CASE_IMPORT;
import static de.symeda.sormas.api.user.UserRight.CASE_INVESTIGATE;
import static de.symeda.sormas.api.user.UserRight.CASE_MERGE;
import static de.symeda.sormas.api.user.UserRight.CASE_REFER_FROM_POE;
import static de.symeda.sormas.api.user.UserRight.CASE_RESPONSIBLE;
import static de.symeda.sormas.api.user.UserRight.CASE_SHARE;
import static de.symeda.sormas.api.user.UserRight.CASE_TRANSFER;
import static de.symeda.sormas.api.user.UserRight.CASE_VIEW;
import static de.symeda.sormas.api.user.UserRight.CASE_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.CLINICAL_COURSE_EDIT;
import static de.symeda.sormas.api.user.UserRight.CLINICAL_COURSE_VIEW;
import static de.symeda.sormas.api.user.UserRight.CLINICAL_VISIT_CREATE;
import static de.symeda.sormas.api.user.UserRight.CLINICAL_VISIT_DELETE;
import static de.symeda.sormas.api.user.UserRight.CLINICAL_VISIT_EDIT;
import static de.symeda.sormas.api.user.UserRight.CONTACT_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.CONTACT_CONVERT;
import static de.symeda.sormas.api.user.UserRight.CONTACT_CREATE;
import static de.symeda.sormas.api.user.UserRight.CONTACT_DELETE;
import static de.symeda.sormas.api.user.UserRight.CONTACT_EDIT;
import static de.symeda.sormas.api.user.UserRight.CONTACT_EXPORT;
import static de.symeda.sormas.api.user.UserRight.CONTACT_IMPORT;
import static de.symeda.sormas.api.user.UserRight.CONTACT_MERGE;
import static de.symeda.sormas.api.user.UserRight.CONTACT_REASSIGN_CASE;
import static de.symeda.sormas.api.user.UserRight.CONTACT_RESPONSIBLE;
import static de.symeda.sormas.api.user.UserRight.CONTACT_VIEW;
import static de.symeda.sormas.api.user.UserRight.CONTACT_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.CUSTOMIZABLE_ENUM_MANAGEMENT;
import static de.symeda.sormas.api.user.UserRight.DASHBOARD_CAMPAIGNS_VIEW;
import static de.symeda.sormas.api.user.UserRight.DASHBOARD_CONTACT_VIEW;
import static de.symeda.sormas.api.user.UserRight.DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS;
import static de.symeda.sormas.api.user.UserRight.DASHBOARD_SAMPLES_VIEW;
import static de.symeda.sormas.api.user.UserRight.DASHBOARD_SURVEILLANCE_VIEW;
import static de.symeda.sormas.api.user.UserRight.DATABASE_EXPORT_ACCESS;
import static de.symeda.sormas.api.user.UserRight.DEV_MODE;
import static de.symeda.sormas.api.user.UserRight.DOCUMENT_DELETE;
import static de.symeda.sormas.api.user.UserRight.DOCUMENT_TEMPLATE_MANAGEMENT;
import static de.symeda.sormas.api.user.UserRight.DOCUMENT_UPLOAD;
import static de.symeda.sormas.api.user.UserRight.DOCUMENT_VIEW;
import static de.symeda.sormas.api.user.UserRight.EDIT_NEWS;
import static de.symeda.sormas.api.user.UserRight.EMAIL_TEMPLATE_MANAGEMENT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_CREATE;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_DELETE;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_EDIT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_EXPORT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_IMPORT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_PATHOGEN_TEST_CREATE;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_PATHOGEN_TEST_DELETE;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_PATHOGEN_TEST_EDIT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_CREATE;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_DELETE;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_EDIT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_EDIT_DISPATCH;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_EDIT_RECEIVAL;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_EXPORT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_IMPORT;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_SAMPLE_VIEW;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_VIEW;
import static de.symeda.sormas.api.user.UserRight.ENVIRONMENT_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.EVENTGROUP_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.EVENTGROUP_CREATE;
import static de.symeda.sormas.api.user.UserRight.EVENTGROUP_DELETE;
import static de.symeda.sormas.api.user.UserRight.EVENTGROUP_EDIT;
import static de.symeda.sormas.api.user.UserRight.EVENTGROUP_LINK;
import static de.symeda.sormas.api.user.UserRight.EVENTGROUP_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.EVENTPARTICIPANT_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.EVENTPARTICIPANT_CREATE;
import static de.symeda.sormas.api.user.UserRight.EVENTPARTICIPANT_DELETE;
import static de.symeda.sormas.api.user.UserRight.EVENTPARTICIPANT_EDIT;
import static de.symeda.sormas.api.user.UserRight.EVENTPARTICIPANT_IMPORT;
import static de.symeda.sormas.api.user.UserRight.EVENTPARTICIPANT_VIEW;
import static de.symeda.sormas.api.user.UserRight.EVENTPARTICIPANT_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.EVENT_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.EVENT_CREATE;
import static de.symeda.sormas.api.user.UserRight.EVENT_DELETE;
import static de.symeda.sormas.api.user.UserRight.EVENT_EDIT;
import static de.symeda.sormas.api.user.UserRight.EVENT_EXPORT;
import static de.symeda.sormas.api.user.UserRight.EVENT_IMPORT;
import static de.symeda.sormas.api.user.UserRight.EVENT_RESPONSIBLE;
import static de.symeda.sormas.api.user.UserRight.EVENT_VIEW;
import static de.symeda.sormas.api.user.UserRight.EVENT_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.EXPORT_DATA_PROTECTION_DATA;
import static de.symeda.sormas.api.user.UserRight.EXTERNAL_EMAIL_ATTACH_DOCUMENTS;
import static de.symeda.sormas.api.user.UserRight.EXTERNAL_EMAIL_SEND;
import static de.symeda.sormas.api.user.UserRight.EXTERNAL_MESSAGE_DELETE;
import static de.symeda.sormas.api.user.UserRight.EXTERNAL_MESSAGE_PROCESS;
import static de.symeda.sormas.api.user.UserRight.EXTERNAL_MESSAGE_VIEW;
import static de.symeda.sormas.api.user.UserRight.EXTERNAL_VISITS;
import static de.symeda.sormas.api.user.UserRight.GRANT_SPECIAL_CASE_ACCESS;
import static de.symeda.sormas.api.user.UserRight.IMMUNIZATION_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.IMMUNIZATION_CREATE;
import static de.symeda.sormas.api.user.UserRight.IMMUNIZATION_DELETE;
import static de.symeda.sormas.api.user.UserRight.IMMUNIZATION_EDIT;
import static de.symeda.sormas.api.user.UserRight.IMMUNIZATION_VIEW;
import static de.symeda.sormas.api.user.UserRight.IMMUNIZATION_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.INFRASTRUCTURE_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.INFRASTRUCTURE_CREATE;
import static de.symeda.sormas.api.user.UserRight.INFRASTRUCTURE_EDIT;
import static de.symeda.sormas.api.user.UserRight.INFRASTRUCTURE_EXPORT;
import static de.symeda.sormas.api.user.UserRight.INFRASTRUCTURE_IMPORT;
import static de.symeda.sormas.api.user.UserRight.INFRASTRUCTURE_VIEW;
import static de.symeda.sormas.api.user.UserRight.INFRASTRUCTURE_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.LINE_LISTING_CONFIGURE;
import static de.symeda.sormas.api.user.UserRight.MANAGE_EXTERNAL_SYMPTOM_JOURNAL;
import static de.symeda.sormas.api.user.UserRight.MANAGE_PUBLIC_EXPORT_CONFIGURATION;
import static de.symeda.sormas.api.user.UserRight.OUTBREAK_EDIT;
import static de.symeda.sormas.api.user.UserRight.OUTBREAK_VIEW;
import static de.symeda.sormas.api.user.UserRight.PATHOGEN_TEST_CREATE;
import static de.symeda.sormas.api.user.UserRight.PATHOGEN_TEST_DELETE;
import static de.symeda.sormas.api.user.UserRight.PATHOGEN_TEST_EDIT;
import static de.symeda.sormas.api.user.UserRight.PERFORM_BULK_OPERATIONS;
import static de.symeda.sormas.api.user.UserRight.PERFORM_BULK_OPERATIONS_PSEUDONYM;
import static de.symeda.sormas.api.user.UserRight.PERSON_CONTACT_DETAILS_DELETE;
import static de.symeda.sormas.api.user.UserRight.PERSON_DELETE;
import static de.symeda.sormas.api.user.UserRight.PERSON_EDIT;
import static de.symeda.sormas.api.user.UserRight.PERSON_EXPORT;
import static de.symeda.sormas.api.user.UserRight.PERSON_MERGE;
import static de.symeda.sormas.api.user.UserRight.PERSON_VIEW;
import static de.symeda.sormas.api.user.UserRight.POPULATION_MANAGE;
import static de.symeda.sormas.api.user.UserRight.PORT_HEALTH_INFO_EDIT;
import static de.symeda.sormas.api.user.UserRight.PORT_HEALTH_INFO_VIEW;
import static de.symeda.sormas.api.user.UserRight.PRESCRIPTION_CREATE;
import static de.symeda.sormas.api.user.UserRight.PRESCRIPTION_DELETE;
import static de.symeda.sormas.api.user.UserRight.PRESCRIPTION_EDIT;
import static de.symeda.sormas.api.user.UserRight.QUARANTINE_ORDER_CREATE;
import static de.symeda.sormas.api.user.UserRight.SAMPLE_CREATE;
import static de.symeda.sormas.api.user.UserRight.SAMPLE_DELETE;
import static de.symeda.sormas.api.user.UserRight.SAMPLE_EDIT;
import static de.symeda.sormas.api.user.UserRight.SAMPLE_EDIT_NOT_OWNED;
import static de.symeda.sormas.api.user.UserRight.SAMPLE_EXPORT;
import static de.symeda.sormas.api.user.UserRight.SAMPLE_TRANSFER;
import static de.symeda.sormas.api.user.UserRight.SAMPLE_VIEW;
import static de.symeda.sormas.api.user.UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION;
import static de.symeda.sormas.api.user.UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION;
import static de.symeda.sormas.api.user.UserRight.SEE_SENSITIVE_DATA_IN_JURISDICTION;
import static de.symeda.sormas.api.user.UserRight.SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_CREATE;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_DELETE;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_EDIT;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_EXPORT;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_IMPORT;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_PROCESS;
import static de.symeda.sormas.api.user.UserRight.SELF_REPORT_VIEW;
import static de.symeda.sormas.api.user.UserRight.SEND_MANUAL_EXTERNAL_MESSAGES;
import static de.symeda.sormas.api.user.UserRight.SORMAS_REST;
import static de.symeda.sormas.api.user.UserRight.SORMAS_UI;
import static de.symeda.sormas.api.user.UserRight.STATISTICS_ACCESS;
import static de.symeda.sormas.api.user.UserRight.STATISTICS_EXPORT;
import static de.symeda.sormas.api.user.UserRight.TASK_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.TASK_ASSIGN;
import static de.symeda.sormas.api.user.UserRight.TASK_CREATE;
import static de.symeda.sormas.api.user.UserRight.TASK_DELETE;
import static de.symeda.sormas.api.user.UserRight.TASK_EDIT;
import static de.symeda.sormas.api.user.UserRight.TASK_EXPORT;
import static de.symeda.sormas.api.user.UserRight.TASK_VIEW;
import static de.symeda.sormas.api.user.UserRight.TASK_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.THERAPY_VIEW;
import static de.symeda.sormas.api.user.UserRight.TRAVEL_ENTRY_ARCHIVE;
import static de.symeda.sormas.api.user.UserRight.TRAVEL_ENTRY_CREATE;
import static de.symeda.sormas.api.user.UserRight.TRAVEL_ENTRY_DELETE;
import static de.symeda.sormas.api.user.UserRight.TRAVEL_ENTRY_EDIT;
import static de.symeda.sormas.api.user.UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS;
import static de.symeda.sormas.api.user.UserRight.TRAVEL_ENTRY_VIEW;
import static de.symeda.sormas.api.user.UserRight.TRAVEL_ENTRY_VIEW_ARCHIVED;
import static de.symeda.sormas.api.user.UserRight.TREATMENT_CREATE;
import static de.symeda.sormas.api.user.UserRight.TREATMENT_DELETE;
import static de.symeda.sormas.api.user.UserRight.TREATMENT_EDIT;
import static de.symeda.sormas.api.user.UserRight.USER_CREATE;
import static de.symeda.sormas.api.user.UserRight.USER_EDIT;
import static de.symeda.sormas.api.user.UserRight.USER_ROLE_DELETE;
import static de.symeda.sormas.api.user.UserRight.USER_ROLE_EDIT;
import static de.symeda.sormas.api.user.UserRight.USER_ROLE_VIEW;
import static de.symeda.sormas.api.user.UserRight.USER_VIEW;
import static de.symeda.sormas.api.user.UserRight.VIEW_NEWS;
import static de.symeda.sormas.api.user.UserRight.VISIT_CREATE;
import static de.symeda.sormas.api.user.UserRight.VISIT_DELETE;
import static de.symeda.sormas.api.user.UserRight.VISIT_EDIT;
import static de.symeda.sormas.api.user.UserRight.VISIT_EXPORT;
import static de.symeda.sormas.api.user.UserRight.WEEKLYREPORT_CREATE;
import static de.symeda.sormas.api.user.UserRight.WEEKLYREPORT_VIEW;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * These are also used as user groups in the server realm
 */
public enum DefaultUserRole {

	ADMIN(false, false, false, false, JurisdictionLevel.NONE, Collections.emptySet(), Collections.emptySet()),
	NATIONAL_USER(false,
		false,
		false,
		false,
		JurisdictionLevel.NATION,
		new HashSet<>(Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE)),
		new HashSet<>(Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE))),
	SURVEILLANCE_SUPERVISOR(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		new HashSet<>(
			Arrays.asList(
				NotificationType.CASE_CLASSIFICATION_CHANGED,
				NotificationType.CASE_DISEASE_CHANGED,
				NotificationType.CASE_INVESTIGATION_DONE,
				NotificationType.CASE_LAB_RESULT_ARRIVED,
				NotificationType.CONTACT_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.CONTACT_VISIT_COMPLETED,
				NotificationType.CONTACT_SYMPTOMATIC,
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				NotificationType.EVENT_GROUP_CREATED,
				NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
				NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP)),
		new HashSet<>(
			Arrays.asList(
				NotificationType.CASE_CLASSIFICATION_CHANGED,
				NotificationType.CASE_DISEASE_CHANGED,
				NotificationType.CASE_INVESTIGATION_DONE,
				NotificationType.CASE_LAB_RESULT_ARRIVED,
				NotificationType.CONTACT_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.CONTACT_VISIT_COMPLETED,
				NotificationType.CONTACT_SYMPTOMATIC,
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				NotificationType.EVENT_GROUP_CREATED,
				NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
				NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP))),
	ADMIN_SUPERVISOR(true, false, false, false, JurisdictionLevel.REGION, Collections.emptySet(), Collections.emptySet()), // FIXME : remove this when user rights management is doable by users
	SURVEILLANCE_OFFICER(false,
		true,
		false,
		false,
		JurisdictionLevel.DISTRICT,
		new HashSet<>(
			Arrays.asList(
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				NotificationType.EVENT_GROUP_CREATED,
				NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
				NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP)),
		new HashSet<>(
			Arrays.asList(
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				NotificationType.EVENT_GROUP_CREATED,
				NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
				NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP))),
	HOSPITAL_INFORMANT(false, false, true, false, JurisdictionLevel.HEALTH_FACILITY, Collections.emptySet(), Collections.emptySet()),
	COMMUNITY_OFFICER(false, true, false, false, JurisdictionLevel.COMMUNITY, Collections.emptySet(), Collections.emptySet()),
	COMMUNITY_INFORMANT(false, false, true, false, JurisdictionLevel.COMMUNITY, Collections.emptySet(), Collections.emptySet()),
	CASE_SUPERVISOR(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		new HashSet<>(
			Arrays.asList(
				NotificationType.CASE_CLASSIFICATION_CHANGED,
				NotificationType.CASE_DISEASE_CHANGED,
				NotificationType.CASE_INVESTIGATION_DONE,
				NotificationType.CASE_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.CONTACT_VISIT_COMPLETED,
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED)),
		new HashSet<>(
			Arrays.asList(
				NotificationType.CASE_CLASSIFICATION_CHANGED,
				NotificationType.CASE_DISEASE_CHANGED,
				NotificationType.CASE_INVESTIGATION_DONE,
				NotificationType.CASE_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.CONTACT_VISIT_COMPLETED,
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED))),
	CASE_OFFICER(false, true, false, false, JurisdictionLevel.DISTRICT, Collections.emptySet(), Collections.emptySet()),
	CONTACT_SUPERVISOR(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		new HashSet<>(
			Arrays.asList(
				NotificationType.CASE_CLASSIFICATION_CHANGED,
				NotificationType.CASE_DISEASE_CHANGED,
				NotificationType.CONTACT_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.CONTACT_VISIT_COMPLETED,
				NotificationType.CONTACT_SYMPTOMATIC)),
		new HashSet<>(
			Arrays.asList(
				NotificationType.CASE_CLASSIFICATION_CHANGED,
				NotificationType.CASE_DISEASE_CHANGED,
				NotificationType.CONTACT_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.CONTACT_VISIT_COMPLETED,
				NotificationType.CONTACT_SYMPTOMATIC))),
	CONTACT_OFFICER(false, true, false, false, JurisdictionLevel.DISTRICT, Collections.emptySet(), Collections.emptySet()),
	EVENT_OFFICER(true,
		false,
		false,
		false,
		JurisdictionLevel.REGION,
		new HashSet<>(
			Arrays.asList(
				NotificationType.EVENT_PARTICIPANT_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				NotificationType.EVENT_GROUP_CREATED,
				NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
				NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP)),
		new HashSet<>(
			Arrays.asList(
				NotificationType.EVENT_PARTICIPANT_LAB_RESULT_ARRIVED,
				NotificationType.TASK_START,
				NotificationType.TASK_DUE,
				NotificationType.TASK_UPDATED_ASSIGNEE,
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				NotificationType.EVENT_GROUP_CREATED,
				NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
				NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP))),
	LAB_USER(false,
		false,
		false,
		false,
		JurisdictionLevel.LABORATORY,
		Collections.singleton(NotificationType.LAB_SAMPLE_SHIPPED),
		Collections.singleton(NotificationType.LAB_SAMPLE_SHIPPED)),
	EXTERNAL_LAB_USER(false,
		false,
		false,
		false,
		JurisdictionLevel.EXTERNAL_LABORATORY,
		Collections.singleton(NotificationType.LAB_SAMPLE_SHIPPED),
		Collections.singleton(NotificationType.LAB_SAMPLE_SHIPPED)),
	NATIONAL_OBSERVER(false, false, false, false, JurisdictionLevel.NATION, Collections.emptySet(), Collections.emptySet()),
	STATE_OBSERVER(false, false, false, false, JurisdictionLevel.REGION, Collections.emptySet(), Collections.emptySet()),
	DISTRICT_OBSERVER(false, false, false, false, JurisdictionLevel.DISTRICT, Collections.emptySet(), Collections.emptySet()),
	NATIONAL_CLINICIAN(false, false, false, false, JurisdictionLevel.NATION, Collections.emptySet(), Collections.emptySet()),
	POE_INFORMANT(false, false, false, true, JurisdictionLevel.POINT_OF_ENTRY, Collections.emptySet(), Collections.emptySet()),
	POE_SUPERVISOR(true,
		false,
		false,
		true,
		JurisdictionLevel.REGION,
		new HashSet<>(Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE)),
		new HashSet<>(Arrays.asList(NotificationType.TASK_START, NotificationType.TASK_DUE, NotificationType.TASK_UPDATED_ASSIGNEE))),
	POE_NATIONAL_USER(false, false, false, true, JurisdictionLevel.NATION, Collections.emptySet(), Collections.emptySet()),
	ENVIRONMENTAL_SURVEILLANCE_USER(false, false, false, false, JurisdictionLevel.DISTRICT, Collections.emptySet(), Collections.emptySet()),
	IMPORT_USER(false, false, false, false, JurisdictionLevel.NONE, Collections.emptySet(), Collections.emptySet()),
	REST_EXTERNAL_VISITS_USER(false, false, false, false, JurisdictionLevel.NATION, Collections.emptySet(), Collections.emptySet()),
	SORMAS_TO_SORMAS_CLIENT(false, false, false, false, JurisdictionLevel.NATION, Collections.emptySet(), Collections.emptySet()),
	BAG_USER(false, false, false, false, JurisdictionLevel.NONE, Collections.emptySet(), Collections.emptySet());

	private final boolean supervisor;
	private final boolean hasOptionalHealthFacility;
	private final boolean hasAssociatedDistrictUser;
	private final boolean portHealthUser;

	private final JurisdictionLevel jurisdictionLevel;

	private final Set<NotificationType> emailNotificationTypes;
	private final Set<NotificationType> smsNotificationTypes;

	DefaultUserRole(
		boolean supervisor,
		boolean hasOptionalHealthFacility,
		boolean hasAssociatedDistrictUser,
		boolean portHealthUser,
		JurisdictionLevel jurisdictionLevel,
		Set<NotificationType> emailNotificationTypes,
		Set<NotificationType> smsNotificationTypes) {

		this.supervisor = supervisor;
		this.hasOptionalHealthFacility = hasOptionalHealthFacility;
		this.hasAssociatedDistrictUser = hasAssociatedDistrictUser;
		this.portHealthUser = portHealthUser;
		this.jurisdictionLevel = jurisdictionLevel;
		this.emailNotificationTypes = emailNotificationTypes;
		this.smsNotificationTypes = smsNotificationTypes;
	}

	public static boolean hasOptionalHealthFacility(Collection<DefaultUserRole> roles) {

		for (DefaultUserRole role : roles) {
			if (role.hasOptionalHealthFacility) {
				return true;
			}
		}
		return false;
	}

	public boolean isSupervisor() {
		return supervisor;
	}

	public boolean hasAssociatedDistrictUser() {
		return hasAssociatedDistrictUser;
	}

	public boolean isPortHealthUser() {
		return portHealthUser;
	}

	public Set<NotificationType> getEmailNotificationTypes() {
		return emailNotificationTypes;
	}

	public Set<NotificationType> getSmsNotificationTypes() {
		return smsNotificationTypes;
	}

	public Set<UserRight> getDefaultUserRights() {
		Set<UserRight> userRights = new HashSet<>();
		switch (this) {
		case ADMIN:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					CASE_CHANGE_DISEASE,
					CASE_CHANGE_EPID_NUMBER,
					CASE_DELETE,
					CASE_IMPORT,
					CASE_EXPORT,
					CASE_SHARE,
					CASE_ARCHIVE,
					CASE_VIEW_ARCHIVED,
					CASE_MERGE,
					GRANT_SPECIAL_CASE_ACCESS,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					IMMUNIZATION_DELETE,
					IMMUNIZATION_ARCHIVE,
					IMMUNIZATION_VIEW_ARCHIVED,
					DASHBOARD_ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_ARCHIVE,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_DELETE,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EXPORT,
					PERSON_VIEW,
					PERSON_EDIT,
					PERSON_DELETE,
					PERSON_CONTACT_DETAILS_DELETE,
					PERSON_MERGE,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_EDIT_NOT_OWNED,
					SAMPLE_DELETE,
					SAMPLE_TRANSFER,
					SAMPLE_EXPORT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					PATHOGEN_TEST_DELETE,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_CREATE,
					ADDITIONAL_TEST_EDIT,
					ADDITIONAL_TEST_DELETE,
					CONTACT_CREATE,
					CONTACT_IMPORT,
					CONTACT_VIEW,
					CONTACT_ARCHIVE,
					CONTACT_VIEW_ARCHIVED,
					CONTACT_EDIT,
					CONTACT_DELETE,
					CONTACT_CONVERT,
					CONTACT_EXPORT,
					CONTACT_REASSIGN_CASE,
					CONTACT_MERGE,
					VISIT_CREATE,
					VISIT_EDIT,
					VISIT_DELETE,
					VISIT_EXPORT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_DELETE,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					ACTION_CREATE,
					ACTION_DELETE,
					ACTION_EDIT,
					EVENT_CREATE,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENT_IMPORT,
					EVENT_EXPORT,
					EVENT_ARCHIVE,
					EVENT_VIEW_ARCHIVED,
					EVENT_DELETE,
					EVENTPARTICIPANT_ARCHIVE,
					EVENTPARTICIPANT_CREATE,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_DELETE,
					EVENTPARTICIPANT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTGROUP_CREATE,
					EVENTGROUP_EDIT,
					EVENTGROUP_LINK,
					EVENTGROUP_ARCHIVE,
					EVENTGROUP_VIEW_ARCHIVED,
					EVENTGROUP_DELETE,
					WEEKLYREPORT_VIEW,
					USER_CREATE,
					USER_EDIT,
					USER_VIEW,
					USER_ROLE_VIEW,
					USER_ROLE_EDIT,
					USER_ROLE_DELETE,
					SEND_MANUAL_EXTERNAL_MESSAGES,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					DATABASE_EXPORT_ACCESS,
					PERFORM_BULK_OPERATIONS,
					PERFORM_BULK_OPERATIONS_PSEUDONYM,
					MANAGE_PUBLIC_EXPORT_CONFIGURATION,
					INFRASTRUCTURE_CREATE,
					INFRASTRUCTURE_EDIT,
					INFRASTRUCTURE_VIEW,
					INFRASTRUCTURE_VIEW_ARCHIVED,
					INFRASTRUCTURE_EXPORT,
					INFRASTRUCTURE_IMPORT,
					INFRASTRUCTURE_ARCHIVE,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					CASE_CLINICIAN_VIEW,
					THERAPY_VIEW,
					PRESCRIPTION_CREATE,
					PRESCRIPTION_EDIT,
					PRESCRIPTION_DELETE,
					TREATMENT_CREATE,
					TREATMENT_EDIT,
					TREATMENT_DELETE,
					CLINICAL_COURSE_VIEW,
					CLINICAL_COURSE_EDIT,
					CLINICAL_VISIT_CREATE,
					CLINICAL_VISIT_EDIT,
					CLINICAL_VISIT_DELETE,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					POPULATION_MANAGE,
					DOCUMENT_TEMPLATE_MANAGEMENT,
					LINE_LISTING_CONFIGURE,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					CAMPAIGN_VIEW,
					CAMPAIGN_EDIT,
					CAMPAIGN_ARCHIVE,
					CAMPAIGN_VIEW_ARCHIVED,
					CAMPAIGN_DELETE,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					CAMPAIGN_FORM_DATA_ARCHIVE,
					CAMPAIGN_FORM_DATA_VIEW_ARCHIVED,
					CAMPAIGN_FORM_DATA_DELETE,
					CAMPAIGN_FORM_DATA_EXPORT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					TRAVEL_ENTRY_DELETE,
					TRAVEL_ENTRY_ARCHIVE,
					TRAVEL_ENTRY_VIEW_ARCHIVED,
					ENVIRONMENT_VIEW,
					ENVIRONMENT_CREATE,
					ENVIRONMENT_EDIT,
					ENVIRONMENT_ARCHIVE,
					ENVIRONMENT_VIEW_ARCHIVED,
					ENVIRONMENT_DELETE,
					ENVIRONMENT_IMPORT,
					ENVIRONMENT_EXPORT,
					ENVIRONMENT_SAMPLE_VIEW,
					ENVIRONMENT_SAMPLE_CREATE,
					ENVIRONMENT_SAMPLE_EDIT,
					ENVIRONMENT_SAMPLE_EDIT_DISPATCH,
					ENVIRONMENT_SAMPLE_EDIT_RECEIVAL,
					ENVIRONMENT_SAMPLE_DELETE,
					ENVIRONMENT_SAMPLE_IMPORT,
					ENVIRONMENT_SAMPLE_EXPORT,
					ENVIRONMENT_PATHOGEN_TEST_CREATE,
					ENVIRONMENT_PATHOGEN_TEST_EDIT,
					ENVIRONMENT_PATHOGEN_TEST_DELETE,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					DOCUMENT_DELETE,
					SURVEY_VIEW,
					SURVEY_CREATE,
					SURVEY_EDIT,
					SURVEY_DELETE,
					SURVEY_TOKEN_VIEW,
					SURVEY_TOKEN_CREATE,
					SURVEY_TOKEN_EDIT,
					SURVEY_TOKEN_DELETE,
					SURVEY_TOKEN_IMPORT,
					EXPORT_DATA_PROTECTION_DATA,
					OUTBREAK_VIEW,
					OUTBREAK_EDIT,
					EMAIL_TEMPLATE_MANAGEMENT,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI,
					DEV_MODE,
					CUSTOMIZABLE_ENUM_MANAGEMENT));
			break;
		case ADMIN_SUPERVISOR:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					CASE_CHANGE_DISEASE,
					CASE_CHANGE_EPID_NUMBER,
					CASE_DELETE,
					CASE_EXPORT,
					CASE_SHARE,
					CASE_MERGE,
					CASE_RESPONSIBLE,
					CLINICAL_VISIT_DELETE,
					TREATMENT_DELETE,
					THERAPY_VIEW,
					CLINICAL_COURSE_VIEW,
					PRESCRIPTION_DELETE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					IMMUNIZATION_DELETE,
					PERSON_VIEW,
					PERSON_EDIT,
					PERSON_DELETE,
					PERSON_CONTACT_DETAILS_DELETE,
					PERSON_EXPORT,
					PERSON_MERGE,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_EDIT_NOT_OWNED,
					SAMPLE_DELETE,
					SAMPLE_TRANSFER,
					SAMPLE_EXPORT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					PATHOGEN_TEST_DELETE,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_DELETE,
					CONTACT_CREATE,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_CONVERT,
					CONTACT_EXPORT,
					CONTACT_REASSIGN_CASE,
					CONTACT_MERGE,
					MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
					VISIT_EXPORT,
					VISIT_DELETE,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					TASK_DELETE,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					ACTION_CREATE,
					ACTION_EDIT,
					ACTION_DELETE,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENT_EXPORT,
					EVENT_ARCHIVE,
					EVENT_VIEW_ARCHIVED,
					EVENT_DELETE,
					EVENTPARTICIPANT_CREATE,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTPARTICIPANT_DELETE,
					EVENTGROUP_CREATE,
					EVENTGROUP_EDIT,
					EVENTGROUP_LINK,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					MANAGE_PUBLIC_EXPORT_CONFIGURATION,
					PERFORM_BULK_OPERATIONS,
					INFRASTRUCTURE_VIEW,
					INFRASTRUCTURE_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					QUARANTINE_ORDER_CREATE,
					LINE_LISTING_CONFIGURE,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					CAMPAIGN_FORM_DATA_EXPORT,
					CAMPAIGN_FORM_DATA_EXPORT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					TRAVEL_ENTRY_DELETE,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					DOCUMENT_DELETE,
					OUTBREAK_VIEW,
					OUTBREAK_EDIT,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case BAG_USER:
			userRights.addAll(Arrays.asList(BAG_EXPORT, SORMAS_REST));
			break;
		case CASE_OFFICER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_CHANGE_EPID_NUMBER,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_CREATE,
					ADDITIONAL_TEST_EDIT,
					TASK_VIEW,
					TASK_EDIT,
					TASK_EXPORT,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					CASE_CLINICIAN_VIEW,
					THERAPY_VIEW,
					PRESCRIPTION_CREATE,
					PRESCRIPTION_EDIT,
					TREATMENT_CREATE,
					TREATMENT_EDIT,
					CLINICAL_COURSE_VIEW,
					CLINICAL_COURSE_EDIT,
					CLINICAL_VISIT_CREATE,
					CLINICAL_VISIT_EDIT,
					PORT_HEALTH_INFO_VIEW,
					QUARANTINE_ORDER_CREATE,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case CASE_SUPERVISOR:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					CASE_CHANGE_DISEASE,
					CASE_CHANGE_EPID_NUMBER,
					CASE_EXPORT,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_TRANSFER,
					SAMPLE_EXPORT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_CREATE,
					ADDITIONAL_TEST_EDIT,
					CONTACT_VIEW,
					CONTACT_EXPORT,
					VISIT_EXPORT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					EVENT_VIEW,
					EVENT_EXPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					CASE_CLINICIAN_VIEW,
					THERAPY_VIEW,
					PRESCRIPTION_CREATE,
					PRESCRIPTION_EDIT,
					PRESCRIPTION_DELETE,
					TREATMENT_CREATE,
					TREATMENT_EDIT,
					TREATMENT_DELETE,
					CLINICAL_COURSE_VIEW,
					CLINICAL_COURSE_EDIT,
					CLINICAL_VISIT_CREATE,
					CLINICAL_VISIT_EDIT,
					CLINICAL_VISIT_DELETE,
					PORT_HEALTH_INFO_VIEW,
					QUARANTINE_ORDER_CREATE,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					CAMPAIGN_FORM_DATA_EXPORT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case COMMUNITY_INFORMANT:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					TASK_VIEW,
					TASK_EDIT,
					TASK_EXPORT,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					WEEKLYREPORT_CREATE,
					WEEKLYREPORT_VIEW,
					PORT_HEALTH_INFO_VIEW,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case COMMUNITY_OFFICER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					CONTACT_CREATE,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_CONVERT,
					CONTACT_REASSIGN_CASE,
					MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
					VISIT_CREATE,
					VISIT_EDIT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					ACTION_CREATE,
					ACTION_EDIT,
					EVENT_CREATE,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENTPARTICIPANT_CREATE,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTGROUP_CREATE,
					EVENTGROUP_EDIT,
					EVENTGROUP_LINK,
					WEEKLYREPORT_CREATE,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					QUARANTINE_ORDER_CREATE,
					AGGREGATE_REPORT_VIEW,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case CONTACT_OFFICER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_CHANGE_EPID_NUMBER,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_VIEW,
					CONTACT_CREATE,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_CONVERT,
					CONTACT_REASSIGN_CASE,
					CONTACT_RESPONSIBLE,
					MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
					VISIT_CREATE,
					VISIT_EDIT,
					TASK_VIEW,
					TASK_EDIT,
					TASK_EXPORT,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					PORT_HEALTH_INFO_VIEW,
					QUARANTINE_ORDER_CREATE,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case CONTACT_SUPERVISOR:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_CHANGE_EPID_NUMBER,
					CASE_EXPORT,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_VIEW,
					SAMPLE_EXPORT,
					CONTACT_CREATE,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_CONVERT,
					CONTACT_EXPORT,
					CONTACT_REASSIGN_CASE,
					MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
					VISIT_CREATE,
					VISIT_EDIT,
					VISIT_EXPORT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENT_EXPORT,
					EVENT_ARCHIVE,
					EVENT_VIEW_ARCHIVED,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTPARTICIPANT_EDIT,
					EVENTGROUP_CREATE,
					EVENTGROUP_LINK,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					PERFORM_BULK_OPERATIONS,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS,
					DASHBOARD_CAMPAIGNS_VIEW,
					PORT_HEALTH_INFO_VIEW,
					QUARANTINE_ORDER_CREATE,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					CAMPAIGN_FORM_DATA_EXPORT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case DISTRICT_OBSERVER:
			userRights.addAll(
				Arrays.asList(
					CASE_VIEW,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					PERSON_VIEW,
					SAMPLE_VIEW,
					CONTACT_VIEW,
					TASK_VIEW,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					INFRASTRUCTURE_VIEW,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					DOCUMENT_VIEW,
					OUTBREAK_VIEW,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case EVENT_OFFICER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_CHANGE_EPID_NUMBER,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_EDIT_NOT_OWNED,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					CONTACT_VIEW,
					TASK_VIEW,
					TASK_EDIT,
					TASK_EXPORT,
					ACTION_CREATE,
					ACTION_EDIT,
					EVENT_CREATE,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENTPARTICIPANT_CREATE,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTGROUP_CREATE,
					EVENTGROUP_EDIT,
					EVENTGROUP_LINK,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case EXTERNAL_LAB_USER:
			userRights.addAll(
				Arrays.asList(
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_TRANSFER,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_CREATE,
					ADDITIONAL_TEST_EDIT,
					TASK_VIEW,
					TASK_EDIT,
					TASK_EXPORT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case HOSPITAL_INFORMANT:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					TASK_VIEW,
					TASK_EDIT,
					TASK_EXPORT,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					WEEKLYREPORT_CREATE,
					WEEKLYREPORT_VIEW,
					PORT_HEALTH_INFO_VIEW,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case IMPORT_USER:
			userRights.addAll(
				Arrays.asList(
					CASE_VIEW,
					CASE_IMPORT,
					CONTACT_VIEW,
					CONTACT_IMPORT,
					EVENT_VIEW,
					EVENT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTPARTICIPANT_IMPORT,
					PERSON_VIEW));
			break;
		case LAB_USER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_CLASSIFY,
					CASE_CHANGE_EPID_NUMBER,
					CASE_EXPORT,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_TRANSFER,
					SAMPLE_EXPORT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_CREATE,
					ADDITIONAL_TEST_EDIT,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_EXPORT,
					VISIT_EXPORT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					EVENT_VIEW,
					EVENT_EXPORT,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					CASE_CLINICIAN_VIEW,
					PORT_HEALTH_INFO_VIEW,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					ENVIRONMENT_SAMPLE_VIEW,
					ENVIRONMENT_SAMPLE_EDIT,
					ENVIRONMENT_SAMPLE_EDIT_DISPATCH,
					ENVIRONMENT_SAMPLE_EDIT_RECEIVAL,
					ENVIRONMENT_SAMPLE_EXPORT,
					ENVIRONMENT_PATHOGEN_TEST_CREATE,
					ENVIRONMENT_PATHOGEN_TEST_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case NATIONAL_CLINICIAN:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					CASE_CHANGE_DISEASE,
					CASE_CHANGE_EPID_NUMBER,
					CASE_EXPORT,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_TRANSFER,
					SAMPLE_EXPORT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_CREATE,
					ADDITIONAL_TEST_EDIT,
					CONTACT_VIEW,
					CONTACT_EXPORT,
					VISIT_EXPORT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					EVENT_VIEW,
					EVENT_EXPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					INFRASTRUCTURE_VIEW,
					INFRASTRUCTURE_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					CASE_CLINICIAN_VIEW,
					THERAPY_VIEW,
					PRESCRIPTION_CREATE,
					PRESCRIPTION_EDIT,
					PRESCRIPTION_DELETE,
					TREATMENT_CREATE,
					TREATMENT_EDIT,
					TREATMENT_DELETE,
					CLINICAL_COURSE_VIEW,
					CLINICAL_COURSE_EDIT,
					CLINICAL_VISIT_CREATE,
					CLINICAL_VISIT_EDIT,
					CLINICAL_VISIT_DELETE,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					OUTBREAK_VIEW,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case NATIONAL_OBSERVER:
			userRights.addAll(
				Arrays.asList(
					CASE_VIEW,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					PERSON_VIEW,
					SAMPLE_VIEW,
					CONTACT_VIEW,
					TASK_VIEW,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					INFRASTRUCTURE_VIEW,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					ENVIRONMENT_VIEW,
					ENVIRONMENT_SAMPLE_VIEW,
					DOCUMENT_VIEW,
					OUTBREAK_VIEW,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case NATIONAL_USER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					CASE_CHANGE_DISEASE,
					CASE_CHANGE_EPID_NUMBER,
					CASE_DELETE,
					CASE_EXPORT,
					CASE_SHARE,
					CLINICAL_COURSE_VIEW,
					CLINICAL_VISIT_DELETE,
					TREATMENT_DELETE,
					THERAPY_VIEW,
					PRESCRIPTION_DELETE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					IMMUNIZATION_DELETE,
					DASHBOARD_ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_ARCHIVE,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_DELETE,
					ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EXPORT,
					PERSON_VIEW,
					PERSON_EDIT,
					PERSON_DELETE,
					PERSON_CONTACT_DETAILS_DELETE,
					PERSON_EXPORT,
					PERSON_MERGE,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_DELETE,
					SAMPLE_TRANSFER,
					SAMPLE_EXPORT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					PATHOGEN_TEST_DELETE,
					ADDITIONAL_TEST_VIEW,
					ADDITIONAL_TEST_DELETE,
					CONTACT_CREATE,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_DELETE,
					CONTACT_CONVERT,
					CONTACT_EXPORT,
					CONTACT_REASSIGN_CASE,
					MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
					VISIT_CREATE,
					VISIT_EDIT,
					VISIT_DELETE,
					VISIT_EXPORT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_DELETE,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					ACTION_CREATE,
					ACTION_EDIT,
					ACTION_DELETE,
					EVENT_CREATE,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENT_EXPORT,
					EVENT_DELETE,
					EVENTPARTICIPANT_CREATE,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_DELETE,
					EVENTPARTICIPANT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTGROUP_CREATE,
					EVENTGROUP_EDIT,
					EVENTGROUP_LINK,
					EVENTGROUP_DELETE,
					WEEKLYREPORT_VIEW,
					SEND_MANUAL_EXTERNAL_MESSAGES,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					DATABASE_EXPORT_ACCESS,
					MANAGE_PUBLIC_EXPORT_CONFIGURATION,
					INFRASTRUCTURE_VIEW,
					INFRASTRUCTURE_VIEW_ARCHIVED,
					INFRASTRUCTURE_EXPORT,
					INFRASTRUCTURE_ARCHIVE,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					QUARANTINE_ORDER_CREATE,
					LINE_LISTING_CONFIGURE,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_ARCHIVE,
					CAMPAIGN_VIEW_ARCHIVED,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					CAMPAIGN_FORM_DATA_ARCHIVE,
					CAMPAIGN_FORM_DATA_VIEW_ARCHIVED,
					CAMPAIGN_FORM_DATA_EXPORT,
					EXTERNAL_MESSAGE_VIEW,
					EXTERNAL_MESSAGE_PROCESS,
					EXTERNAL_MESSAGE_DELETE,
					PERFORM_BULK_OPERATIONS,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					TRAVEL_ENTRY_DELETE,
					ENVIRONMENT_VIEW,
					ENVIRONMENT_CREATE,
					ENVIRONMENT_EDIT,
					ENVIRONMENT_ARCHIVE,
					ENVIRONMENT_VIEW_ARCHIVED,
					ENVIRONMENT_DELETE,
					ENVIRONMENT_IMPORT,
					ENVIRONMENT_EXPORT,
					ENVIRONMENT_SAMPLE_VIEW,
					ENVIRONMENT_SAMPLE_CREATE,
					ENVIRONMENT_SAMPLE_EDIT,
					ENVIRONMENT_SAMPLE_EDIT_DISPATCH,
					ENVIRONMENT_SAMPLE_EDIT_RECEIVAL,
					ENVIRONMENT_SAMPLE_DELETE,
					ENVIRONMENT_SAMPLE_IMPORT,
					ENVIRONMENT_SAMPLE_EXPORT,
					ENVIRONMENT_PATHOGEN_TEST_CREATE,
					ENVIRONMENT_PATHOGEN_TEST_EDIT,
					ENVIRONMENT_PATHOGEN_TEST_DELETE,
					SELF_REPORT_VIEW,
					SELF_REPORT_CREATE,
					SELF_REPORT_EDIT,
					SELF_REPORT_DELETE,
					SELF_REPORT_ARCHIVE,
					SELF_REPORT_PROCESS,
					SELF_REPORT_IMPORT,
					SELF_REPORT_EXPORT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					DOCUMENT_DELETE,
					OUTBREAK_VIEW,
					OUTBREAK_EDIT,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI,
					EDIT_NEWS,
					VIEW_NEWS));
			break;
		case POE_INFORMANT:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					TASK_VIEW,
					TASK_EDIT,
					TASK_EXPORT,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST));
			break;
		case POE_NATIONAL_USER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_REFER_FROM_POE,
					CASE_EXPORT,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					EVENT_EXPORT,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					INFRASTRUCTURE_VIEW,
					INFRASTRUCTURE_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					OUTBREAK_VIEW,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case POE_SUPERVISOR:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_REFER_FROM_POE,
					CASE_EXPORT,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					EVENT_VIEW,
					EVENT_EXPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					INFRASTRUCTURE_VIEW,
					INFRASTRUCTURE_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					CAMPAIGN_FORM_DATA_EXPORT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					OUTBREAK_VIEW,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case ENVIRONMENTAL_SURVEILLANCE_USER:
			userRights.addAll(
				Arrays.asList(
					ENVIRONMENT_VIEW,
					ENVIRONMENT_CREATE,
					ENVIRONMENT_EDIT,
					ENVIRONMENT_ARCHIVE,
					ENVIRONMENT_VIEW_ARCHIVED,
					ENVIRONMENT_DELETE,
					ENVIRONMENT_IMPORT,
					ENVIRONMENT_EXPORT,
					ENVIRONMENT_SAMPLE_VIEW,
					ENVIRONMENT_SAMPLE_CREATE,
					ENVIRONMENT_SAMPLE_EDIT,
					ENVIRONMENT_SAMPLE_EDIT_DISPATCH,
					ENVIRONMENT_SAMPLE_EDIT_RECEIVAL,
					ENVIRONMENT_SAMPLE_DELETE,
					ENVIRONMENT_SAMPLE_IMPORT,
					ENVIRONMENT_SAMPLE_EXPORT,
					ENVIRONMENT_PATHOGEN_TEST_CREATE,
					ENVIRONMENT_PATHOGEN_TEST_EDIT,
					ENVIRONMENT_PATHOGEN_TEST_DELETE,
					PERFORM_BULK_OPERATIONS,
					TASK_VIEW,
					TASK_CREATE,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case REST_EXTERNAL_VISITS_USER:
			userRights.addAll(
				Arrays.asList(
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION,
					SORMAS_REST,
					EXTERNAL_VISITS));
			break;
		case SORMAS_TO_SORMAS_CLIENT:
			userRights.addAll(
				Arrays.asList(
					CASE_VIEW,
					PERSON_VIEW,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION,
					SORMAS_REST,
					UserRight.SORMAS_TO_SORMAS_CLIENT));
			break;
		case STATE_OBSERVER:
			userRights.addAll(
				Arrays.asList(
					CASE_VIEW,
					CASE_SHARE,
					IMMUNIZATION_VIEW,
					PERSON_VIEW,
					SAMPLE_VIEW,
					CONTACT_VIEW,
					TASK_VIEW,
					EVENT_VIEW,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					INFRASTRUCTURE_VIEW,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CONTACT_VIEW,
					DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					DOCUMENT_VIEW,
					OUTBREAK_VIEW,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case SURVEILLANCE_OFFICER:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					CASE_CHANGE_EPID_NUMBER,
					CASE_RESPONSIBLE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					PERSON_VIEW,
					PERSON_EDIT,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					CONTACT_CREATE,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_CONVERT,
					CONTACT_REASSIGN_CASE,
					MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					ACTION_CREATE,
					ACTION_EDIT,
					EVENT_CREATE,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENT_RESPONSIBLE,
					EVENTPARTICIPANT_CREATE,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTGROUP_CREATE,
					EVENTGROUP_EDIT,
					EVENTGROUP_LINK,
					WEEKLYREPORT_CREATE,
					WEEKLYREPORT_VIEW,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		case SURVEILLANCE_SUPERVISOR:
			userRights.addAll(
				Arrays.asList(
					CASE_CREATE,
					CASE_VIEW,
					CASE_EDIT,
					CASE_TRANSFER,
					CASE_REFER_FROM_POE,
					CASE_INVESTIGATE,
					CASE_CLASSIFY,
					CASE_CHANGE_DISEASE,
					CASE_CHANGE_EPID_NUMBER,
					CASE_EXPORT,
					CASE_SHARE,
					CASE_RESPONSIBLE,
					IMMUNIZATION_VIEW,
					IMMUNIZATION_CREATE,
					IMMUNIZATION_EDIT,
					IMMUNIZATION_DELETE,
					PERSON_VIEW,
					PERSON_EDIT,
					PERSON_DELETE,
					SAMPLE_CREATE,
					SAMPLE_VIEW,
					SAMPLE_EDIT,
					SAMPLE_EDIT_NOT_OWNED,
					SAMPLE_TRANSFER,
					SAMPLE_EXPORT,
					PATHOGEN_TEST_CREATE,
					PATHOGEN_TEST_EDIT,
					PATHOGEN_TEST_DELETE,
					CONTACT_CREATE,
					CONTACT_VIEW,
					CONTACT_EDIT,
					CONTACT_CONVERT,
					CONTACT_EXPORT,
					CONTACT_REASSIGN_CASE,
					MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
					VISIT_EXPORT,
					VISIT_DELETE,
					TASK_CREATE,
					TASK_VIEW,
					TASK_EDIT,
					TASK_ASSIGN,
					TASK_EXPORT,
					TASK_ARCHIVE,
					TASK_VIEW_ARCHIVED,
					ACTION_CREATE,
					ACTION_EDIT,
					EVENT_CREATE,
					EVENT_VIEW,
					EVENT_EDIT,
					EVENT_EXPORT,
					EVENT_ARCHIVE,
					EVENT_VIEW_ARCHIVED,
					EVENT_RESPONSIBLE,
					EVENTPARTICIPANT_CREATE,
					EVENTPARTICIPANT_EDIT,
					EVENTPARTICIPANT_IMPORT,
					EVENTPARTICIPANT_VIEW,
					EVENTPARTICIPANT_VIEW_ARCHIVED,
					EVENTGROUP_CREATE,
					EVENTGROUP_EDIT,
					EVENTGROUP_LINK,
					WEEKLYREPORT_VIEW,
					STATISTICS_ACCESS,
					STATISTICS_EXPORT,
					PERFORM_BULK_OPERATIONS,
					INFRASTRUCTURE_VIEW,
					INFRASTRUCTURE_EXPORT,
					DASHBOARD_SURVEILLANCE_VIEW,
					DASHBOARD_CAMPAIGNS_VIEW,
					DASHBOARD_SAMPLES_VIEW,
					PORT_HEALTH_INFO_VIEW,
					PORT_HEALTH_INFO_EDIT,
					QUARANTINE_ORDER_CREATE,
					LINE_LISTING_CONFIGURE,
					AGGREGATE_REPORT_VIEW,
					AGGREGATE_REPORT_EXPORT,
					AGGREGATE_REPORT_EDIT,
					SEE_PERSONAL_DATA_IN_JURISDICTION,
					SEE_SENSITIVE_DATA_IN_JURISDICTION,
					CAMPAIGN_VIEW,
					CAMPAIGN_FORM_DATA_VIEW,
					CAMPAIGN_FORM_DATA_EDIT,
					CAMPAIGN_FORM_DATA_EXPORT,
					EXTERNAL_MESSAGE_VIEW,
					EXTERNAL_MESSAGE_PROCESS,
					EXTERNAL_MESSAGE_DELETE,
					TRAVEL_ENTRY_MANAGEMENT_ACCESS,
					TRAVEL_ENTRY_VIEW,
					TRAVEL_ENTRY_CREATE,
					TRAVEL_ENTRY_EDIT,
					SELF_REPORT_VIEW,
					SELF_REPORT_CREATE,
					SELF_REPORT_EDIT,
					SELF_REPORT_DELETE,
					SELF_REPORT_ARCHIVE,
					SELF_REPORT_PROCESS,
					SELF_REPORT_IMPORT,
					SELF_REPORT_EXPORT,
					DOCUMENT_VIEW,
					DOCUMENT_UPLOAD,
					OUTBREAK_VIEW,
					OUTBREAK_EDIT,
					EXTERNAL_EMAIL_SEND,
					EXTERNAL_EMAIL_ATTACH_DOCUMENTS,
					SORMAS_REST,
					SORMAS_UI));
			break;
		default:
			throw new IllegalArgumentException(this.toString());
		}
		return userRights;
	}

	public boolean hasOptionalHealthFacility() {
		return hasOptionalHealthFacility;
	}

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	/**
	 * Expects the roles have been validated.
	 *
	 * @param roles
	 * @return
	 */
	public static JurisdictionLevel getJurisdictionLevel(Collection<DefaultUserRole> roles) {

		boolean laboratoryJurisdictionPresent = false;
		for (DefaultUserRole role : roles) {
			final JurisdictionLevel jurisdictionLevel = role.getJurisdictionLevel();
			if (roles.size() == 1 || (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY)) {
				return jurisdictionLevel;
			} else if (jurisdictionLevel == JurisdictionLevel.LABORATORY) {
				laboratoryJurisdictionPresent = true;
			}
		}

		return laboratoryJurisdictionPresent ? JurisdictionLevel.LABORATORY : JurisdictionLevel.NONE;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String toShortString() {
		return I18nProperties.getEnumCaptionShort(this);
	}

	public UserRoleDto toUserRole() {
		UserRoleDto userRole = UserRoleDto.build();

		userRole.setCaption(I18nProperties.getEnumCaption(this));
		userRole.setPortHealthUser(isPortHealthUser());
		userRole.setLinkedDefaultUserRole(this);
		userRole.setHasAssociatedDistrictUser(hasAssociatedDistrictUser());
		userRole.setHasOptionalHealthFacility(DefaultUserRole.hasOptionalHealthFacility(Collections.singleton(this)));
		userRole.setEnabled(true);
		userRole.setJurisdictionLevel(getJurisdictionLevel());
		userRole.setSmsNotificationTypes(getSmsNotificationTypes());
		userRole.setEmailNotificationTypes(getEmailNotificationTypes());
		userRole.setUserRights(getDefaultUserRights());

		return userRole;
	}

	public static DefaultUserRole getByCaption(String caption) {
		Optional<DefaultUserRole> defaultUserRole =
			Arrays.stream(values()).filter(dur -> dur.name().equals(caption) || I18nProperties.getEnumCaption(dur).equals(caption)).findAny();
		return defaultUserRole.orElse(null);
	}
}
