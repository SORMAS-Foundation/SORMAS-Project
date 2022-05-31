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

import de.symeda.sormas.api.i18n.I18nProperties;

public enum UserRight {

	//@formatter:off
	CASE_CREATE,
	CASE_VIEW,
	CASE_EDIT,
	CASE_TRANSFER,
	CASE_REFER_FROM_POE,
	/*
	 * Edit the investigation status - either by setting a respective task to done or by manually changing it in the case
	 */
	CASE_INVESTIGATE,
	/*
	 * Edit the classification and outcome of a case
	 */
	CASE_CLASSIFY,
	CASE_CHANGE_DISEASE,
	CASE_CHANGE_EPID_NUMBER,
	CASE_DELETE,
	CASE_IMPORT,
	CASE_EXPORT,
	CASE_SHARE,
	CASE_ARCHIVE,
	CASE_MERGE,
	CASE_RESPONSIBLE,
	IMMUNIZATION_VIEW,
	IMMUNIZATION_CREATE,
	IMMUNIZATION_EDIT,
	IMMUNIZATION_DELETE,
	IMMUNIZATION_ARCHIVE,
	PERSON_VIEW,
	PERSON_EDIT,
	PERSON_DELETE,
	PERSON_CONTACT_DETAILS_DELETE,
	PERSON_EXPORT,
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
	CONTACT_ASSIGN,
	CONTACT_EDIT,
	CONTACT_DELETE,
	CONTACT_CLASSIFY,
	// users that are allowed to convert a contact to a case need to be allowed to create a case
	CONTACT_CONVERT,
	CONTACT_EXPORT,
	// reassign or remove the case from an existing contact
	CONTACT_REASSIGN_CASE,
	CONTACT_MERGE,
	CONTACT_RESPONSIBLE,
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
	ACTION_CREATE,
	ACTION_DELETE,
	ACTION_EDIT,
	EVENT_CREATE,
	EVENT_VIEW,
	EVENT_EDIT,
	EVENT_IMPORT,
	EVENT_EXPORT,
	EVENT_ARCHIVE,
	EVENT_DELETE,
	EVENT_RESPONSIBLE,
	EVENTPARTICIPANT_ARCHIVE,
	EVENTPARTICIPANT_CREATE,
	EVENTPARTICIPANT_EDIT,
	EVENTPARTICIPANT_DELETE,
	EVENTPARTICIPANT_IMPORT,
	EVENTPARTICIPANT_VIEW,
	EVENTGROUP_CREATE,
	EVENTGROUP_EDIT,
	EVENTGROUP_LINK,
	EVENTGROUP_ARCHIVE,
	EVENTGROUP_DELETE,
	WEEKLYREPORT_CREATE,
	WEEKLYREPORT_VIEW,
	USER_CREATE,
	USER_EDIT,
	USER_VIEW,
	SEND_MANUAL_EXTERNAL_MESSAGES,
	STATISTICS_ACCESS,
	STATISTICS_EXPORT,
	DATABASE_EXPORT_ACCESS,
	PERFORM_BULK_OPERATIONS,
	PERFORM_BULK_OPERATIONS_EVENT,
	PERFORM_BULK_OPERATIONS_EVENTPARTICIPANT,
	MANAGE_PUBLIC_EXPORT_CONFIGURATION,
	PERFORM_BULK_OPERATIONS_CASE_SAMPLES,
	PERFORM_BULK_OPERATIONS_PSEUDONYM,
	INFRASTRUCTURE_CREATE,
	INFRASTRUCTURE_EDIT,
	INFRASTRUCTURE_VIEW,
	INFRASTRUCTURE_EXPORT,
	INFRASTRUCTURE_IMPORT,
	INFRASTRUCTURE_ARCHIVE,
	DASHBOARD_SURVEILLANCE_VIEW,
	DASHBOARD_CONTACT_VIEW,
	DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS,
	DASHBOARD_CAMPAIGNS_VIEW,
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
	QUARANTINE_ORDER_CREATE,
	LINE_LISTING_CONFIGURE,
	AGGREGATE_REPORT_VIEW,
	AGGREGATE_REPORT_EXPORT,
	AGGREGATE_REPORT_EDIT,
	SEE_PERSONAL_DATA_IN_JURISDICTION,
	SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION,
	SEE_SENSITIVE_DATA_IN_JURISDICTION,
	SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION,
	CAMPAIGN_VIEW,
	CAMPAIGN_EDIT,
	CAMPAIGN_ARCHIVE,
	CAMPAIGN_DELETE,
	CAMPAIGN_FORM_DATA_VIEW,
	CAMPAIGN_FORM_DATA_EDIT,
	CAMPAIGN_FORM_DATA_ARCHIVE,
	CAMPAIGN_FORM_DATA_DELETE,
	CAMPAIGN_FORM_DATA_EXPORT,
	BAG_EXPORT,
	SORMAS_TO_SORMAS_SHARE,
	LAB_MESSAGES,
	PERFORM_BULK_OPERATIONS_LAB_MESSAGES,
	TRAVEL_ENTRY_MANAGEMENT_ACCESS,
	TRAVEL_ENTRY_VIEW,
	TRAVEL_ENTRY_CREATE,
	TRAVEL_ENTRY_EDIT,
	TRAVEL_ENTRY_DELETE,
	TRAVEL_ENTRY_ARCHIVE,
	EXPORT_DATA_PROTECTION_DATA,
	OUTBREAK_VIEW,
	OUTBREAK_EDIT,
	SORMAS_REST,
	SORMAS_UI,
	SORMAS_TO_SORMAS_CLIENT,
	EXTERNAL_VISITS,
	DEV_MODE;
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
	public static final String _CONTACT_ASSIGN = "CONTACT_ASSIGN";
	public static final String _CONTACT_EDIT = "CONTACT_EDIT";
	public static final String _CONTACT_DELETE = "CONTACT_DELETE";
	public static final String _CONTACT_CLASSIFY = "CONTACT_CLASSIFY";
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
	public static final String _SEND_MANUAL_EXTERNAL_MESSAGES = "SEND_MANUAL_EXTERNAL_MESSAGES";
	public static final String _STATISTICS_ACCESS = "STATISTICS_ACCESS";
	public static final String _STATISTICS_EXPORT = "STATISTICS_EXPORT";
	public static final String _DATABASE_EXPORT_ACCESS = "DATABASE_EXPORT_ACCESS";
	public static final String _PERFORM_BULK_OPERATIONS = "PERFORM_BULK_OPERATIONS";
	public static final String _PERFORM_BULK_OPERATIONS_EVENT = "PERFORM_BULK_OPERATIONS_EVENT";
	public static final String _MANAGE_PUBLIC_EXPORT_CONFIGURATION = "MANAGE_PUBLIC_EXPORT_CONFIGURATION";
	public static final String _PERFORM_BULK_OPERATIONS_CASE_SAMPLES = "PERFORM_BULK_OPERATIONS_CASE_SAMPLES";
	public static final String _PERFORM_BULK_OPERATIONS_LAB_MESSAGES = "PERFORM_BULK_OPERATIONS_LAB_MESSAGES";
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
	public static final String _LAB_MESSAGES = "LAB_MESSAGES";
	public static final String _TRAVEL_ENTRY_MANAGEMENT_ACCESS = "TRAVEL_ENTRY_MANAGEMENT_ACCESS";
	public static final String _TRAVEL_ENTRY_VIEW = "TRAVEL_ENTRY_VIEW";
	public static final String _TRAVEL_ENTRY_CREATE = "TRAVEL_ENTRY_CREATE";
	public static final String _TRAVEL_ENTRY_EDIT = "TRAVEL_ENTRY_EDIT";
	public static final String _TRAVEL_ENTRY_DELETE = "TRAVEL_ENTRY_DELETE";
	public static final String _TRAVEL_ENTRY_ARCHIVE = "TRAVEL_ENTRY_ARCHIVE";
	public static final String _EXPORT_DATA_PROTECTION_DATA = "EXPORT_DATA_PROTECTION_DATA";
	public static final String _OUTBREAK_VIEW = "OUTBREAK_VIEW";
	public static final String _OUTBREAK_EDIT = "OUTBREAK_EDIT";
	public static final String _SORMAS_REST = "SORMAS_REST";
	public static final String _SORMAS_UI = "SORMAS_UI";
	public static final String _SORMAS_TO_SORMAS_CLIENT = "SORMAS_TO_SORMAS_CLIENT";
	public static final String _EXTERNAL_VISITS = "EXTERNAL_VISITS";

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String getDescription() {
		return I18nProperties.getEnumDescription(this);
	}
}
