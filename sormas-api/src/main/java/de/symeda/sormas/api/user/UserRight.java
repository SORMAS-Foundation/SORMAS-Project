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
	CASE_CREATE(UserRightGroup.CASE),
	CASE_VIEW(UserRightGroup.CASE),
	CASE_EDIT(UserRightGroup.CASE),
	CASE_TRANSFER(UserRightGroup.CASE),
	CASE_REFER_FROM_POE(UserRightGroup.CASE),
	/*
	 * Edit the investigation status - either by setting a respective task to done or by manually changing it in the case
	 */
	CASE_INVESTIGATE(UserRightGroup.CASE),
	/*
	 * Edit the classification and outcome of a case
	 */
	CASE_CLASSIFY(UserRightGroup.CASE),
	CASE_CHANGE_DISEASE(UserRightGroup.CASE),
	CASE_CHANGE_EPID_NUMBER(UserRightGroup.CASE),
	CASE_DELETE(UserRightGroup.CASE),
	CASE_IMPORT(UserRightGroup.CASE),
	CASE_EXPORT(UserRightGroup.CASE),
	CASE_SHARE(UserRightGroup.CASE),
	CASE_ARCHIVE(UserRightGroup.CASE),
	CASE_MERGE(UserRightGroup.CASE),
	CASE_RESPONSIBLE(UserRightGroup.CASE),
	IMMUNIZATION_VIEW(UserRightGroup.IMMUNIZATION),
	IMMUNIZATION_CREATE(UserRightGroup.IMMUNIZATION),
	IMMUNIZATION_EDIT(UserRightGroup.IMMUNIZATION),
	IMMUNIZATION_DELETE(UserRightGroup.IMMUNIZATION),
	IMMUNIZATION_ARCHIVE(UserRightGroup.IMMUNIZATION),
	PERSON_VIEW(UserRightGroup.PERSON),
	PERSON_EDIT(UserRightGroup.PERSON),
	PERSON_DELETE(UserRightGroup.PERSON),
	PERSON_CONTACT_DETAILS_DELETE(UserRightGroup.PERSON),
	PERSON_EXPORT(UserRightGroup.PERSON),
	SAMPLE_CREATE(UserRightGroup.SAMPLE),
	SAMPLE_VIEW(UserRightGroup.SAMPLE),
	SAMPLE_EDIT(UserRightGroup.SAMPLE),
	SAMPLE_EDIT_NOT_OWNED(UserRightGroup.SAMPLE),
	SAMPLE_DELETE(UserRightGroup.SAMPLE),
	SAMPLE_TRANSFER(UserRightGroup.SAMPLE),
	SAMPLE_EXPORT(UserRightGroup.SAMPLE),
	PATHOGEN_TEST_CREATE(UserRightGroup.SAMPLE),
	PATHOGEN_TEST_EDIT(UserRightGroup.SAMPLE),
	PATHOGEN_TEST_DELETE(UserRightGroup.SAMPLE),
	ADDITIONAL_TEST_VIEW(UserRightGroup.SAMPLE),
	ADDITIONAL_TEST_CREATE(UserRightGroup.SAMPLE),
	ADDITIONAL_TEST_EDIT(UserRightGroup.SAMPLE),
	ADDITIONAL_TEST_DELETE(UserRightGroup.SAMPLE),
	CONTACT_CREATE(UserRightGroup.CONTACT),
	CONTACT_IMPORT(UserRightGroup.CONTACT),
	CONTACT_VIEW(UserRightGroup.CONTACT),
	CONTACT_ARCHIVE(UserRightGroup.CONTACT),
	CONTACT_ASSIGN(UserRightGroup.CONTACT),
	CONTACT_EDIT(UserRightGroup.CONTACT),
	CONTACT_DELETE(UserRightGroup.CONTACT),
	CONTACT_CLASSIFY(UserRightGroup.CONTACT),
	// users that are allowed to convert a contact to a case need to be allowed to create a case
	CONTACT_CONVERT(UserRightGroup.CONTACT),
	CONTACT_EXPORT(UserRightGroup.CONTACT),
	// reassign or remove the case from an existing contact
	CONTACT_REASSIGN_CASE(UserRightGroup.CONTACT),
	CONTACT_MERGE(UserRightGroup.CONTACT),
	CONTACT_RESPONSIBLE(UserRightGroup.CONTACT),
	MANAGE_EXTERNAL_SYMPTOM_JOURNAL(UserRightGroup.EXTERNAL),
	VISIT_CREATE(UserRightGroup.VISIT),
	VISIT_EDIT(UserRightGroup.VISIT),
	VISIT_DELETE(UserRightGroup.VISIT),
	VISIT_EXPORT(UserRightGroup.VISIT),
	TASK_CREATE(UserRightGroup.TASK),
	TASK_VIEW(UserRightGroup.TASK),
	TASK_EDIT(UserRightGroup.TASK),
	TASK_ASSIGN(UserRightGroup.TASK),
	TASK_DELETE(UserRightGroup.TASK),
	TASK_EXPORT(UserRightGroup.TASK),
	ACTION_CREATE(UserRightGroup.EVENT),
	ACTION_DELETE(UserRightGroup.EVENT),
	ACTION_EDIT(UserRightGroup.EVENT),
	EVENT_CREATE(UserRightGroup.EVENT),
	EVENT_VIEW(UserRightGroup.EVENT),
	EVENT_EDIT(UserRightGroup.EVENT),
	EVENT_IMPORT(UserRightGroup.EVENT),
	EVENT_EXPORT(UserRightGroup.EVENT),
	EVENT_ARCHIVE(UserRightGroup.EVENT),
	EVENT_DELETE(UserRightGroup.EVENT),
	EVENT_RESPONSIBLE(UserRightGroup.EVENT),
	EVENTPARTICIPANT_ARCHIVE(UserRightGroup.EVENT),
	EVENTPARTICIPANT_CREATE(UserRightGroup.EVENT),
	EVENTPARTICIPANT_EDIT(UserRightGroup.EVENT),
	EVENTPARTICIPANT_DELETE(UserRightGroup.EVENT),
	EVENTPARTICIPANT_IMPORT(UserRightGroup.EVENT),
	EVENTPARTICIPANT_VIEW(UserRightGroup.EVENT),
	EVENTGROUP_CREATE(UserRightGroup.EVENT),
	EVENTGROUP_EDIT(UserRightGroup.EVENT),
	EVENTGROUP_LINK(UserRightGroup.EVENT),
	EVENTGROUP_ARCHIVE(UserRightGroup.EVENT),
	EVENTGROUP_DELETE(UserRightGroup.EVENT),
	WEEKLYREPORT_CREATE(UserRightGroup.AGGREGATED_REPORTING),
	WEEKLYREPORT_VIEW(UserRightGroup.AGGREGATED_REPORTING),
	USER_CREATE(UserRightGroup.USER),
	USER_EDIT(UserRightGroup.USER),
	USER_VIEW(UserRightGroup.USER),
	USER_ROLE_EDIT(UserRightGroup.USER),
	USER_ROLE_DELETE(UserRightGroup.USER),
	SEND_MANUAL_EXTERNAL_MESSAGES(UserRightGroup.EXTERNAL),
	STATISTICS_ACCESS(UserRightGroup.STATISTICS),
	STATISTICS_EXPORT(UserRightGroup.STATISTICS),
	DATABASE_EXPORT_ACCESS(UserRightGroup.EXPORT),
	PERFORM_BULK_OPERATIONS(UserRightGroup.GENERAL),
	PERFORM_BULK_OPERATIONS_EVENT(UserRightGroup.EVENT),
	PERFORM_BULK_OPERATIONS_EVENTPARTICIPANT(UserRightGroup.EVENT),
	MANAGE_PUBLIC_EXPORT_CONFIGURATION(UserRightGroup.CONFIGURATION),
	PERFORM_BULK_OPERATIONS_CASE_SAMPLES(UserRightGroup.SAMPLE),
	PERFORM_BULK_OPERATIONS_PSEUDONYM(UserRightGroup.GENERAL),
	INFRASTRUCTURE_CREATE(UserRightGroup.INFRASTRUCTURE),
	INFRASTRUCTURE_EDIT(UserRightGroup.INFRASTRUCTURE),
	INFRASTRUCTURE_VIEW(UserRightGroup.INFRASTRUCTURE),
	INFRASTRUCTURE_EXPORT(UserRightGroup.INFRASTRUCTURE),
	INFRASTRUCTURE_IMPORT(UserRightGroup.INFRASTRUCTURE),
	INFRASTRUCTURE_ARCHIVE(UserRightGroup.INFRASTRUCTURE),
	DASHBOARD_SURVEILLANCE_VIEW(UserRightGroup.DASHBOARD),
	DASHBOARD_CONTACT_VIEW(UserRightGroup.DASHBOARD),
	DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS(UserRightGroup.DASHBOARD),
	DASHBOARD_CAMPAIGNS_VIEW(UserRightGroup.DASHBOARD),
	CASE_CLINICIAN_VIEW(UserRightGroup.CASE_MANAGEMENT),
	THERAPY_VIEW(UserRightGroup.CASE_MANAGEMENT),
	PRESCRIPTION_CREATE(UserRightGroup.CASE_MANAGEMENT),
	PRESCRIPTION_EDIT(UserRightGroup.CASE_MANAGEMENT),
	PRESCRIPTION_DELETE(UserRightGroup.CASE_MANAGEMENT),
	TREATMENT_CREATE(UserRightGroup.CASE_MANAGEMENT),
	TREATMENT_EDIT(UserRightGroup.CASE_MANAGEMENT),
	TREATMENT_DELETE(UserRightGroup.CASE_MANAGEMENT),
	CLINICAL_COURSE_VIEW(UserRightGroup.CASE_MANAGEMENT),
	CLINICAL_COURSE_EDIT(UserRightGroup.CASE_MANAGEMENT),
	CLINICAL_VISIT_CREATE(UserRightGroup.CASE_MANAGEMENT),
	CLINICAL_VISIT_EDIT(UserRightGroup.CASE_MANAGEMENT),
	CLINICAL_VISIT_DELETE(UserRightGroup.CASE_MANAGEMENT),
	PORT_HEALTH_INFO_VIEW(UserRightGroup.PORT_HEALTH),
	PORT_HEALTH_INFO_EDIT(UserRightGroup.PORT_HEALTH),
	POPULATION_MANAGE(UserRightGroup.INFRASTRUCTURE),
	DOCUMENT_TEMPLATE_MANAGEMENT(UserRightGroup.CONFIGURATION),
	QUARANTINE_ORDER_CREATE(UserRightGroup.GENERAL),
	LINE_LISTING_CONFIGURE(UserRightGroup.CONFIGURATION),
	AGGREGATE_REPORT_VIEW(UserRightGroup.AGGREGATED_REPORTING),
	AGGREGATE_REPORT_EXPORT(UserRightGroup.AGGREGATED_REPORTING),
	AGGREGATE_REPORT_EDIT(UserRightGroup.AGGREGATED_REPORTING),
	SEE_PERSONAL_DATA_IN_JURISDICTION(UserRightGroup.DATA_PROTECTION),
	SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION(UserRightGroup.DATA_PROTECTION),
	SEE_SENSITIVE_DATA_IN_JURISDICTION(UserRightGroup.DATA_PROTECTION),
	SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION(UserRightGroup.DATA_PROTECTION),
	CAMPAIGN_VIEW(UserRightGroup.CAMPAIGN),
	CAMPAIGN_EDIT(UserRightGroup.CAMPAIGN),
	CAMPAIGN_ARCHIVE(UserRightGroup.CAMPAIGN),
	CAMPAIGN_DELETE(UserRightGroup.CAMPAIGN),
	CAMPAIGN_FORM_DATA_VIEW(UserRightGroup.CAMPAIGN),
	CAMPAIGN_FORM_DATA_EDIT(UserRightGroup.CAMPAIGN),
	CAMPAIGN_FORM_DATA_ARCHIVE(UserRightGroup.CAMPAIGN),
	CAMPAIGN_FORM_DATA_DELETE(UserRightGroup.CAMPAIGN),
	CAMPAIGN_FORM_DATA_EXPORT(UserRightGroup.CAMPAIGN),
	BAG_EXPORT(UserRightGroup.EXPORT),
	SORMAS_TO_SORMAS_SHARE(UserRightGroup.EXTERNAL),
	EXTERNAL_MESSAGE_VIEW(UserRightGroup.EXTERNAL),
	EXTERNAL_MESSAGE_PROCESS(UserRightGroup.EXTERNAL),
	EXTERNAL_MESSAGE_DELETE(UserRightGroup.EXTERNAL),
	PERFORM_BULK_OPERATIONS_EXTERNAL_MESSAGES(UserRightGroup.EXTERNAL),
	TRAVEL_ENTRY_MANAGEMENT_ACCESS(UserRightGroup.TRAVEL_ENTRY),
	TRAVEL_ENTRY_VIEW(UserRightGroup.TRAVEL_ENTRY),
	TRAVEL_ENTRY_CREATE(UserRightGroup.TRAVEL_ENTRY),
	TRAVEL_ENTRY_EDIT(UserRightGroup.TRAVEL_ENTRY),
	TRAVEL_ENTRY_DELETE(UserRightGroup.TRAVEL_ENTRY),
	TRAVEL_ENTRY_ARCHIVE(UserRightGroup.TRAVEL_ENTRY),
	EXPORT_DATA_PROTECTION_DATA(UserRightGroup.EXPORT),
	OUTBREAK_VIEW(UserRightGroup.CONFIGURATION),
	OUTBREAK_EDIT(UserRightGroup.CONFIGURATION),
	SORMAS_REST(UserRightGroup.GENERAL),
	SORMAS_UI(UserRightGroup.GENERAL),
	SORMAS_TO_SORMAS_CLIENT(UserRightGroup.EXTERNAL),
	EXTERNAL_VISITS(UserRightGroup.EXTERNAL),
	DEV_MODE(UserRightGroup.CONFIGURATION);
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
	public static final String _EXTERNAL_MESSAGE_VIEW = "EXTERNAL_MESSAGE_VIEW";
	public static final String _EXTERNAL_MESSAGE_PROCESS = "EXTERNAL_MESSAGE_PROCESS";
	public static final String _EXTERNAL_MESSAGE_DELETE = "EXTERNAL_MESSAGE_DELETE";
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

	private final UserRightGroup userRightGroup;

	UserRight(UserRightGroup userRightGroup) {
		this.userRightGroup = userRightGroup;
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
}
