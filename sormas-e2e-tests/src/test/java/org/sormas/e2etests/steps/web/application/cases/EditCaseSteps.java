/*
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
 */

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.enums.CaseOutcome.DECEASED;
import static org.sormas.e2etests.enums.CaseOutcome.FACILITY_OTHER;
import static org.sormas.e2etests.enums.CaseOutcome.INVESTIGATION_DISCARDED;
import static org.sormas.e2etests.enums.CaseOutcome.INVESTIGATION_DONE;
import static org.sormas.e2etests.enums.CaseOutcome.INVESTIGATION_PENDING;
import static org.sormas.e2etests.enums.CaseOutcome.PLACE_OF_STAY_FACILITY;
import static org.sormas.e2etests.enums.CaseOutcome.PLACE_OF_STAY_HOME;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_HOME;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_INSTITUTIONAL;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_NONE;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_OTHER;
import static org.sormas.e2etests.enums.CaseOutcome.QUARANTINE_UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.RECOVERED;
import static org.sormas.e2etests.enums.CaseOutcome.SEQUELAE_NO;
import static org.sormas.e2etests.enums.CaseOutcome.SEQUELAE_UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.SEQUELAE_YES;
import static org.sormas.e2etests.enums.CaseOutcome.UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.VACCINATED_STATUS_UNKNOWN;
import static org.sormas.e2etests.enums.CaseOutcome.VACCINATED_STATUS_UNVACCINATED;
import static org.sormas.e2etests.enums.CaseOutcome.VACCINATED_STATUS_VACCINATED;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CLOSE_WINDOW_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_INFO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONFIRM_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONTACTS_DATA_TAB;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.EPIDEMIOLOGICAL_DATA_TAB;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_NO_POPUP_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CANCEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CONFIRM;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ADD_A_PARTICIPANT_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ARCHIVE_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ARCHIVE_RELATED_CONTACTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BACK_TO_CASES_LIST_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BLOOD_ORGAN_TISSUE_DONATION_IN_THE_LAST_6_MONTHS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BUTTONS_IN_VACCINATIONS_LOCATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CANCEL_FOLLOW_UP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_CLASSIFICATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_CLASSIFICATION_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_CLASSIFICATION_SPAN;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_CONFIRMATION_BASIS_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_IDENTIFICATION_SOURCE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_ORIGIN;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_PERSON_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CHANGE_DISEASE_CONFIRMATION_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CLINICAL_CONFIRMATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CLINICAL_COURSE_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_COMBOBOX_BY_PLACE_OF_STAY;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CONFIRM_ACTION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_TEMPLATES;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_TEMPLATES_POPUP_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_QUARANTINE_ORDER_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_QUARANTINE_ORDER_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CURRENT_HOSPITALIZATION_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OFFICIAL_QUARANTINE_ORDER_WAS_SENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_FOLLOW_UP_STATUS_CHANGE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_FOLLOW_UP_STATUS_CHANGE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_OUTCOME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_OUTCOME_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_OF_THE_VERBAL_ORDER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DATE_RECEIVED_AT_REGION_LEVEL_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_VACCINATION_REASON_POPUP_DE_VERSION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISCARD_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_VARIANT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_REPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_TRAVEL_ENTRY_FROM_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_VACCINATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EPID_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ERROR_DESCRIPTION_REQUEST_PROCESSED;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ERROR_IN_HANDOVER_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ERROR_REVOKE_DESCRIPTION_REQUEST_PROCESSED;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ERROR_REVOKE_IN_HANDOVER_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXPECTED_FOLLOWUP_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXPECTED_FOLLOWUP_POPUP_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXPECTED_FOLLOWUP_VALUE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXPECTED_FOLLOW_UP_UNTIL_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_TOKEN_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTRA_COMMENT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_HEALTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_HEALTH_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FOLLOW_UP_COMMENT_FIELD;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FOLLOW_UP_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FOLLOW_UP_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FOLLOW_UP_UNTIL_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERAL_COMMENT_TEXTAREA;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERAL_COMMENT_TEXT_AREA;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERATED_DOCUMENT_NAME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERATED_DOCUMENT_NAME_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.HOME_BASED_QUARANTINE_POSSIBLE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.HOSPITALIZATION_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.IMMUNIZATION_CARD_IMMUNIZATION_PERIOD_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.IMMUNIZATION_CARD_IMMUNIZATION_STATUS_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.IMMUNIZATION_CARD_IMMUNIZATION_UUID;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.IMMUNIZATION_CARD_MANAGEMENT_STATUS_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.IMMUNIZATION_CARD_MEANS_OF_IMMUNIZATION_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INFECTION_SETTINGS_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INFRASTRUCTURE_DATA_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INTERNAL_TOKEN_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INVESTIGATED_DATE_FIELD;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INVESTIGATION_STATUS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.LAB_MESSAGE_WINDOW_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.LINKED_EVENT_TITLE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.LINK_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.LINK_EVENT_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.LOST_TO_FOLLOW_UP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_SAMPLE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_TRAVEL_ENTRY_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NOSOCOMIAL_OUTBRAKE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.OUTCOME_OF_CASE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.OVERWRITE_FOLLOW_UP_UNTIL_DATE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_SELECTED_VALUE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.POINT_OF_ENTRY_DETAILS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.POINT_OF_ENTRY_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.POPUPS_INPUTS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PREGNANCY_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PREVIOUS_INFECTION_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PROHIBITION_TO_WORK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_CHANGE_COMMENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_FROM;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_FROM_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_TO;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_DATE_TO_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_BY_DOCUMENT_DATE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_VERBALLY_CHECKBOX_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDERED_VERBALLY_CHECKBOX_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDER_POPUP_SAMPLE_FIELD;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_POPUP_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_POPUP_MESSAGE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_TYPE_DETAILS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REASON_FOR_DELETION_DETAILS_TEXTAREA;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFERENCE_DEFINITION_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFER_CASE_FROM_POINT_OF_ENTRY;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFER_CASE_FROM_POINT_OF_ENTRY_DISTRICT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFER_CASE_FROM_POINT_OF_ENTRY_POPUP_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFER_CASE_FROM_POINT_OF_ENTRY_REGION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFER_CASE_FROM_POINT_OF_ENTRY_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REINFECTION_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REJECT_SHARED_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORTER_FACILITY_DETAILS_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORTER_FACILITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORTING_TOOL_MESSAGE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_GPS_ACCURACY_IN_M_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_GPS_LATITUDE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_GPS_LONGITUDE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_USER_FOR_FOLLOW_UP_STATUS_CHANGE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESPONSIBLE_USER_FOR_FOLLOW_UP_STATUS_CHANGE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.RESUME_FOLLOW_UP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REVOKE_CASE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAHRE_SAMPLES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_AND_OPEN_HOSPITALIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SEND_TO_REPORTING_TOOL_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SEQUELAE_DETAILS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SEQUELAE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SHARE_IMMUNIZATION_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SHARE_PENDING_WARNING_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SHARE_REPORTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SHOW_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SURVEILLANCE_OFFICER_FIELD_ABOVE_GENERAL_COMMENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SYMPTOMS_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.TRIMESTER_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UPLOAD_DOCUMENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_INFO_ICON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_INFO_POPUP_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_VACCINATION_DATE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_VACCINATION_NAME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_UPDATE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.WINDOW_CLOSE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.checkIfTextExists;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.getByImmunizationUuid;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.getEditTaskButtonByNumber;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.getPreExistingConditionComboboxToSelectValue_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.getPreExistingConditionComboboxWithValue_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.getPreExistingConditionCombobox_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePersonPage.DATE_OF_DEATH_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePersonPage.SEE_CASES_FOR_THIS_PERSON_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_TO_BODY_FLUIDS_OPTONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_TO_CASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.COUNTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.END_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_ROLE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.HANDLING_SAMPLES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.INDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.LONG_FACE_TO_FACE_CONTACT_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OPEN_SAVED_EXPOSURE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OTHER_PROTECTIVE_MEASURES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OUTDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.PERCUTANEOUS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RISK_AREA_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SHORT_DISTANCE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.START_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SUBCONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_PLACE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_MASK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_PPE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.SymptomsTabPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_WINDOW_CONTACT_DE;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.ACTION_CANCEL_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.END_OF_PROCESSING_DATE_POPUP_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.FOLLOW_UP_UNTIL_DATE;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_IMPORT_TRAVEL_ENTRY_POPUP;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.CONFIRM_DEARCHIVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.DEARCHIVE_REASON_TEXT_AREA;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.REASON_FOR_DELETION_EXCLAMATION_MARK;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.REASON_FOR_DELETION_MESSAGE;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.getReasonForDeletionDetailsFieldLabel;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.getVaccinationByIndex;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EVENT_PARTICIPANTS_DATA_TAB;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PRESENT_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAMPLE_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_REASON_POPUP;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_REASON_POPUP_FOR_DE;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.contacts.ContactDirectorySteps.exposureData;

import cucumber.api.java8.En;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.QuarantineOrder;
import org.sormas.e2etests.entities.pojo.web.Vaccination;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.entities.services.CaseDocumentService;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.enums.CaseClassification;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.pages.application.events.EditEventPage;
import org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.contacts.EditContactSteps;
import org.sormas.e2etests.steps.web.application.immunizations.EditImmunizationSteps;
import org.sormas.e2etests.steps.web.application.samples.CreateNewSampleSteps;
import org.sormas.e2etests.steps.web.application.vaccination.CreateNewVaccinationSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class EditCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Case aCase;
  private static Case createdCase;
  private static Case editedCase;
  public static QuarantineOrder aQuarantineOrder;
  private static Case specificCaseData;
  private static LocalDate dateFollowUp;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  public static final String userDirPath = System.getProperty("user.dir");
  public static String caseUuid;
  public static List<String> externalUUID = new ArrayList<>();
  public static LocalDate dateOfDeath;

  @SneakyThrows
  @Inject
  public EditCaseSteps(
      WebDriverHelpers webDriverHelpers,
      CaseService caseService,
      CaseDocumentService caseDocumentService,
      SoftAssert softly,
      AssertHelpers assertHelpers,
      ApiState apiState,
      DataOperations dataOperations,
      RunningConfiguration runningConfiguration) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I fill the Date of outcome to yesterday",
        () -> {
          webDriverHelpers.fillInWebElement(
              DATE_OF_OUTCOME_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(1)));
        });

    When(
        "I select ([^\"]*) as Outcome Of Case Status",
        (String caseStatus) -> {
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, CaseOutcome.getValueFor(caseStatus).toUpperCase());
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I fill the specific Date of outcome",
        () -> {
          webDriverHelpers.fillInWebElement(
              DATE_OF_OUTCOME_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(1)));
        });

    When(
        "I check that the value selected from Disease combobox is {string} on Edit Case page",
        (String disease) -> {
          String chosenDisease = webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX);
          softly.assertEquals(chosenDisease, disease, "The disease is other then expected");
          softly.assertAll();
        });

    And(
        "I click on save button from Edit Case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I click only on save button from Edit Case page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON));

    And(
        "I check if Current Hospitalization popup is displayed",
        () -> webDriverHelpers.isElementVisibleWithTimeout(CURRENT_HOSPITALIZATION_POPUP, 10));

    When(
        "I click on Save and open hospitalization in current hospitalization popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_AND_OPEN_HOSPITALIZATION_BUTTON));

    Then(
        "I click on Clinical Course tab from Edit Case page",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLINICAL_COURSE_TAB));

    When(
        "I click on save button from Edit Case page with current hospitalization",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on INFO button on Case Edit page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_INFO_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CASE_CLOSE_WINDOW_BUTTON);
        });

    When(
        "I change Epidemiological confirmation Combobox to {string} option",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX, option);
        });

    When(
        "I check that Case Classification has {string} value",
        (String caseClassificationValue) -> {
          String caseClassificationComboboxValue =
              (webDriverHelpers.getTextFromWebElement(CASE_CLASSIFICATION_SPAN));
          softly.assertEquals(
              caseClassificationValue.toUpperCase(),
              caseClassificationComboboxValue,
              "The case classification field has unexpected value ");
          softly.assertAll();
        });

    And(
        "I navigate to follow-up tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(FOLLOW_UP_TAB));

    And(
        "I navigate to symptoms tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(SYMPTOMS_TAB));

    When(
        "I navigate to Hospitalization tab in Cases",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          webDriverHelpers.clickOnWebElementBySelector(HOSPITALIZATION_TAB);
        });

    And(
        "I navigate to case person tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CASE_PERSON_TAB));

    And(
        "I set the present condition of person value to \"([^\"]*)\" on Case Person page",
        (String presentCondition) -> {
          webDriverHelpers.selectFromCombobox(PRESENT_CONDITION_COMBOBOX, presentCondition);
        });

    And(
        "I click on Save button on Case Person page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    And(
        "^I set the Date of death to (\\d+) days ago$",
        (Integer dateOfDeathAgo) -> {
          dateOfDeath = LocalDate.now().minusDays(dateOfDeathAgo);
          System.out.print(dateOfDeath);
          fillDateOfDeath(dateOfDeath, Locale.GERMAN);
        });

    And(
        "^I set previous infection date (\\d+) days from report date to case in person tab$",
        (Integer daysBeforeReportDate) -> {
          webDriverHelpers.scrollToElement(PREVIOUS_INFECTION_DATE_INPUT);
          LocalDate infectionDate = LocalDate.now().minusDays(daysBeforeReportDate);
          fillPreviousInfectionDate(infectionDate, Locale.GERMAN);
        });

    And(
        "I navigate to case tab",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          webDriverHelpers.clickOnWebElementBySelector(CASE_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
        });

    And(
        "I click on Create button in Document Templates box in Edit Case directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_TEMPLATES));
    And(
        "I click on checkbox to upload generated document to entity in Create Quarantine Order form in Edit Case directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_CHECKBOX));
    When(
        "I select {string} Quarantine Order in Create Quarantine Order form in Edit Case directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(POPUPS_INPUTS, 5);
        });
    When(
        "Sample name timestamp is correct in Create Quarantine Order form from Edit Case directory",
        () -> {
          String sampleFieldValue =
              webDriverHelpers.getValueFromWebElement(QUARANTINE_ORDER_POPUP_SAMPLE_FIELD);
          String sampleDate =
              CreateNewSampleSteps.sample
                  .getDateOfCollection()
                  .format(DateTimeFormatter.ofPattern("M/d/yyyy"));
          Assert.assertTrue(
              sampleFieldValue.startsWith(sampleDate),
              "Sample field date doesn't start with " + sampleDate);
        });
    When(
        "I select {string} Quarantine Order in Create Quarantine Order form in Case directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
        });
    When(
        "I check if downloaded file is correct for {string} Quarantine Order in Edit Case directory",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          String filePath = uuid.substring(0, 6).toUpperCase() + "-" + name;
          FilesHelper.waitForFileToDownload(filePath, 120);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for API created case in Edit Case directory",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME)),
              120);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for API created case in Edit Case directory for DE",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME_DE)),
              120);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for UI created case in Edit Case directory",
        (String name) -> {
          String uuid = EditCaseSteps.aCase.getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME)),
              120);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for UI created case in Edit Case directory for DE",
        (String name) -> {
          String uuid = EditCaseSteps.aCase.getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME_DE)),
              120);
        });
    When(
        "I delete downloaded file created from {string} Document Template",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          String filePath = uuid.substring(0, 6).toUpperCase() + "-" + name;
          FilesHelper.deleteFile(filePath);
        });
    And(
        "I click on Create button in Create Quarantine Order form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_BUTTON);
        });
    And(
        "I click on Create button in Create Quarantine Order form DE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_TEMPLATES_POPUP_DE);
          TimeUnit.SECONDS.sleep(3);
        });

    When(
        "I check that the region of the case is set to ([^\"]*) and district is set to ([^\"]*)",
        (String region, String district) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(REGION_COMBOBOX),
              region,
              "The region is different than expected");
          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX),
              district,
              "The district is different than expected");
          softly.assertAll();
        });

    When(
        "I check if generated document for Case based on {string} was downloaded properly",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          String pathToFile = uuid.substring(0, 6).toUpperCase() + "-" + name;
          FilesHelper.waitForFileToDownload(pathToFile, 120);
        });
    When(
        "I check if generated document for Case based on {string} contains all required fields",
        (String name) -> {
          String uuid = apiState.getCreatedCase().getUuid();
          String pathToFile =
              userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name;
          FileInputStream fis = new FileInputStream(pathToFile);
          XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
          List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
          String[] line = paragraphList.get(29).getText().split(":");
          softly.assertEquals(
              line[0], "Report Date", "Report date label is different than expected");
          line = paragraphList.get(31).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccination Date", "Vaccination date label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination
                  .getVaccinationDate()
                  .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
              "Vaccination date value is different than expected");
          line = paragraphList.get(32).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccine name", "Vaccination name label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineName(),
              "Vaccination name value is different than expected");
          line = paragraphList.get(33).getText().split(":");
          softly.assertEquals(
              line[0],
              "Vaccine name Details",
              "Vaccination name Details label is different than expected");
          line = paragraphList.get(34).getText().split(":");
          softly.assertEquals(
              line[0],
              "Vaccine Manufacturer",
              "Vaccination Manufacturer label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineManufacturer(),
              "Vaccination Manufacturer label is different than expected");
          line = paragraphList.get(35).getText().split(":");
          softly.assertEquals(
              line[0],
              "Vaccine Manufacturer details",
              "Vaccination Manufacturer details label is different than expected");
          line = paragraphList.get(36).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccine Type", "Vaccination Type label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineType(),
              "Vaccination Type value is different than expected");
          line = paragraphList.get(37).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccine Dose", "Vaccination Dose label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineDose(),
              "Vaccination Dose value is different than expected");
          line = paragraphList.get(38).getText().split(":");
          softly.assertEquals(line[0], "INN", "INN label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getInn(),
              "INN value is different than expected");
          line = paragraphList.get(39).getText().split(":");
          softly.assertEquals(line[0], "Batch", "Batch label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getBatchNumber(),
              "Batch value is different than expected");
          line = paragraphList.get(40).getText().split(":");
          softly.assertEquals(line[0], "UNII Code", "UNII Code label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getUniiCode(),
              "UNII Code value is different than expected");
          line = paragraphList.get(41).getText().split(":");
          softly.assertEquals(line[0], "ATC Code", "ATC Code label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getAtcCode(),
              "ATC Code value is different than expected");
          line = paragraphList.get(42).getText().split(":");
          softly.assertEquals(
              line[0],
              "Vaccination Info Source",
              "Vaccination Info Source label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccinationInfoSource(),
              "Vaccination Info Source value is different than expected");
          softly.assertAll();
        });
    When(
        "I check the created data is correctly displayed on Edit case page",
        () -> {
          aCase = collectCasePersonData();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  //   "externalId",
                  // "epidNumber",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "^I check the created data for Facility is correctly displayed on Edit case page$",
        () -> {
          aCase = collectCasePersonDataWithFacility();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "I check the created data for duplicated case is correctly displayed on Edit case page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
          aCase = collectCasePersonData();
          createdCase = CreateNewCaseSteps.oneCase;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "I check the created data for existing person is correctly displayed on Edit case page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
          aCase = collectCasePersonDataForExistingPerson();
          createdCase =
              CreateNewCaseSteps.caze.toBuilder()
                  .firstName(apiState.getLastCreatedPerson().getFirstName())
                  .lastName(apiState.getLastCreatedPerson().getLastName())
                  .dateOfBirth(
                      LocalDate.of(
                          apiState.getLastCreatedPerson().getBirthdateYYYY(),
                          apiState.getLastCreatedPerson().getBirthdateMM(),
                          apiState.getLastCreatedPerson().getBirthdateDD()))
                  .build();

          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  // "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "I check the created data is correctly displayed on Edit case page for DE version",
        () -> {
          aCase = collectCasePersonDataDE();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    When(
        "I select Investigation Status ([^\"]*)",
        (String investigationStatus) -> {
          webDriverHelpers.clickWebElementByText(
              INVESTIGATION_STATUS_OPTIONS,
              CaseOutcome.getValueFor("INVESTIGATION " + investigationStatus).toUpperCase());
          editedCase =
              Case.builder()
                  .investigationStatus("Investigation " + investigationStatus)
                  .build(); // TODO: Create POJO updater class
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I select Case Classification Confirmed",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              By.xpath("//*[contains(text(),'Confirmed case')]"));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I select {string} as Basis for Confirmation",
        (String basis) ->
            webDriverHelpers.selectFromCombobox(CASE_CONFIRMATION_BASIS_COMBOBOX, basis));

    When(
        "In created case I select Outcome Of Case Status to ([^\"]*)",
        (String caseStatus) -> {
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, CaseOutcome.getValueFor(caseStatus).toUpperCase());
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I select German Investigation Status ([^\"]*)",
        (String option) -> {
          String investigationStatus = new String();
          switch (option) {
            case "Done":
              investigationStatus = INVESTIGATION_DONE.getNameDE();
              break;
            case "Pending":
              investigationStatus = INVESTIGATION_PENDING.getNameDE();
              break;
            case "Discarded":
              investigationStatus = INVESTIGATION_DISCARDED.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(
              INVESTIGATION_STATUS_OPTIONS, investigationStatus.toUpperCase());
          editedCase =
              Case.builder()
                  .investigationStatus(investigationStatus)
                  .build(); // TODO: Create POJO updater class
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if date of investigation filed is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(INVESTIGATED_DATE_FIELD));

    When(
        "I select Outcome Of Case Status ([^\"]*)",
        (String caseStatus) -> {
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, CaseOutcome.getValueFor(caseStatus).toUpperCase());
          editedCase = editedCase.toBuilder().outcomeOfCase(caseStatus).build();
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I select German Outcome Of Case Status ([^\"]*)",
        (String option) -> {
          String outcomeOfCaseStatus = new String();
          switch (option) {
            case "Deceased":
              outcomeOfCaseStatus = DECEASED.getNameDE();
              break;
            case "Recovered":
              outcomeOfCaseStatus = RECOVERED.getNameDE();
              break;
            case "Unknown":
              outcomeOfCaseStatus = UNKNOWN.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, outcomeOfCaseStatus.toUpperCase());
          editedCase = editedCase.toBuilder().outcomeOfCase(outcomeOfCaseStatus).build();
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I check if date of outcome filed is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_OUTCOME));

    When(
        "I click on ([^\"]*) option in Sequelae",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(
              SEQUELAE_OPTIONS, CaseOutcome.getValueFor(option).toUpperCase());
          editedCase = editedCase.toBuilder().sequelae(option).build();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the German option for ([^\"]*) in Sequelae",
        (String option) -> {
          String sequelaeStatus = new String();
          switch (option) {
            case "Yes":
              sequelaeStatus = SEQUELAE_YES.getNameDE();
              break;
            case "No":
              sequelaeStatus = SEQUELAE_NO.getNameDE();
              break;
            case "Unknown":
              sequelaeStatus = SEQUELAE_UNKNOWN.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(SEQUELAE_OPTIONS, sequelaeStatus.toUpperCase());
          editedCase = editedCase.toBuilder().sequelae(sequelaeStatus).build();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if Sequelae Details field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEQUELAE_DETAILS));

    When(
        "I click on Place of stay of this case differs from its responsible jurisdiction",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PLACE_OF_STAY_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .differentPlaceOfStayJurisdiction(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          PLACE_OF_STAY_CHECKBOX_INPUT))
                  .build();
        });

    When(
        "I check if region combobox is available and I select Responsible Region",
        () -> {
          aCase = caseService.buildEditGeneratedCase();
          webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX, aCase.getRegion());
          editedCase = editedCase.toBuilder().region(aCase.getRegion()).build();
        });

    When(
        "I check if district combobox is available and i select Responsible District",
        () -> {
          aCase = caseService.buildEditGeneratedCase();
          webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX, aCase.getDistrict());
          editedCase = editedCase.toBuilder().district(aCase.getDistrict()).build();
        });

    When(
        "I check if community combobox is available",
        () ->
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                COMMUNITY_COMBOBOX_BY_PLACE_OF_STAY));

    When(
        "I click on ([^\"]*) as place of stay",
        (String placeOfStay) -> {
          webDriverHelpers.clickWebElementByText(
              PLACE_OF_STAY_OPTIONS, CaseOutcome.getValueFor(placeOfStay).toUpperCase());
          editedCase = editedCase.toBuilder().placeOfStay(placeOfStay).build();
        });

    When(
        "I click on ([^\"]*) as place of stay in Case Edit tab",
        (String placeOfStay) -> {
          webDriverHelpers.clickWebElementByText(
              PLACE_OF_STAY_OPTIONS, CaseOutcome.getValueFor(placeOfStay).toUpperCase());
        });

    When(
        "I click on {string} as place of stay in Case Edit tab for DE version",
        (String placeOfStay) -> {
          webDriverHelpers.clickWebElementByText(
              PLACE_OF_STAY_OPTIONS, CaseOutcome.getValueForDE(placeOfStay).toUpperCase());
        });

    When(
        "I click on ([^\"]*) as German place of stay",
        (String option) -> {
          String placeOfStay = new String();
          switch (option) {
            case "Facility":
              placeOfStay = PLACE_OF_STAY_FACILITY.getNameDE();
              break;
            case "Home":
              placeOfStay = PLACE_OF_STAY_HOME.getNameDE();
              break;
          }
          webDriverHelpers.clickWebElementByText(PLACE_OF_STAY_OPTIONS, placeOfStay.toUpperCase());
          editedCase = editedCase.toBuilder().placeOfStay(placeOfStay).build();
        });

    When(
        "I check if Facility Category combobox is available",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_CATEGORY_COMBOBOX);
          editedCase =
              editedCase.toBuilder()
                  .facilityCategory(
                      webDriverHelpers.getValueFromCombobox(FACILITY_CATEGORY_COMBOBOX))
                  .build();
        });

    When(
        "I check if Facility Type combobox is available",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE_COMBOBOX);
          editedCase =
              editedCase.toBuilder()
                  .facilityType(webDriverHelpers.getValueFromCombobox(FACILITY_TYPE_COMBOBOX))
                  .build();
        });

    When(
        "I set Facility as a ([^\"]*)",
        (String facility) -> {
          webDriverHelpers.selectFromCombobox(
              FACILITY_HEALTH_COMBOBOX, CaseOutcome.getValueFor(facility));
          editedCase = editedCase.toBuilder().facility(facility).build();
        });

    When(
        "In Case Edit tab I set Facility as a ([^\"]*)",
        (String facility) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE_COMBOBOX);
          webDriverHelpers.selectFromCombobox(FACILITY_HEALTH_COMBOBOX, facility);
        });

    When(
        "I set Facility in German as a ([^\"]*)",
        (String option) -> {
          String facility = new String();
          switch (option) {
            case "Other facility":
              facility = FACILITY_OTHER.getNameDE();
              break;
          }
          webDriverHelpers.selectFromCombobox(FACILITY_HEALTH_COMBOBOX, facility);
          editedCase = editedCase.toBuilder().facility(facility).build();
        });

    When(
        "I set Vaccination status to {string} on Edit Case page",
        (String vaccination) -> {
          webDriverHelpers.selectFromCombobox(
              VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX, vaccination);
        });
    When(
        "I check if Vaccination Status is set to {string} on Edit Case page",
        (String expected) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
          String vaccinationStatus =
              webDriverHelpers.getValueFromWebElement(VACCINATION_STATUS_INPUT);
          softly.assertEquals(
              vaccinationStatus, expected, "Vaccination status is different than expected");
          softly.assertAll();
        });
    When(
        "I set Facility to {string} from New Entry popup",
        (String facility) -> {
          webDriverHelpers.selectFromCombobox(FACILITY_ACTIVITY_COMBOBOX, facility);
        });
    When(
        "And I click on Discard button from New Entry popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON_POPUP);
        });

    When(
        "I set Facility Type to {string} from New Entry popup",
        (String facilityType) -> {
          webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
        });

    When(
        "I fill Facility name and description filed by ([^\"]*)",
        (String description) -> {
          webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, description);
          editedCase = editedCase.toBuilder().facilityNameAndDescription(description).build();
        });

    When(
        "I check if Facility name and description field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(PLACE_DESCRIPTION_INPUT));

    When(
        "I set Quarantine ([^\"]*)",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, CaseOutcome.getValueFor(option));
          editedCase = editedCase.toBuilder().quarantine(option).build();
        });

    When(
        "I set German Quarantine ([^\"]*)",
        (String option) -> {
          String quarantine = new String();
          switch (option) {
            case "Home":
              quarantine = QUARANTINE_HOME.getNameDE();
              break;
            case "Institutional":
              quarantine = QUARANTINE_INSTITUTIONAL.getNameDE();
              break;
            case "None":
              quarantine = QUARANTINE_NONE.getNameDE();
              break;
            case "Unknown":
              quarantine = QUARANTINE_UNKNOWN.getNameDE();
              break;
            case "Other":
              quarantine = QUARANTINE_OTHER.getNameDE();
              break;
          }
          webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, quarantine);
          editedCase = editedCase.toBuilder().quarantine(quarantine).build();
        });

    When(
        "I set place for Quarantine as ([^\"]*)",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, CaseOutcome.getValueFor(option));
        });

    When(
        "I set Start date of Quarantine ([^\"]*) days ago",
        (Integer days) -> {
          webDriverHelpers.scrollToElement(QUARANTINE_DATE_TO_INPUT);
          webDriverHelpers.fillInWebElement(
              QUARANTINE_DATE_FROM_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(days)));
        });

    When(
        "I set End date of Quarantine to ([^\"]*) days",
        (Integer days) -> {
          webDriverHelpers.scrollToElement(QUARANTINE_DATE_TO_INPUT);
          webDriverHelpers.fillInWebElement(
              QUARANTINE_DATE_TO_INPUT, DATE_FORMATTER.format(LocalDate.now().plusDays(days)));
        });

    When(
        "I check if ([^\"]*) quarantine popup is displayed",
        (String option) -> {
          webDriverHelpers.waitForElementPresent(QUARANTINE_COMBOBOX, 2);
          String quarantineText;
          String expectedTextReduce = "Are you sure you want to reduce the quarantine?";
          String expectedTextExtend = "Are you sure you want to extend the quarantine?";
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_COMBOBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(QUARANTINE_POPUP_MESSAGE);
          quarantineText = webDriverHelpers.getTextFromWebElement(QUARANTINE_POPUP_MESSAGE);
          if (option.equals("Reduce")) softly.assertEquals(quarantineText, expectedTextReduce);
          else if (option.equals("Extend")) softly.assertEquals(quarantineText, expectedTextExtend);
          softly.assertAll();
        });

    When(
        "I check if Quarantine End date stayed reduce to ([^\"]*) days",
        (Integer days) -> {
          String date = webDriverHelpers.getValueFromWebElement(QUARANTINE_DATE_TO_INPUT);
          LocalDate endDate = LocalDate.now().plusDays(days);
          softly.assertEquals(DATE_FORMATTER.format(endDate), date);
          softly.assertAll();
        });
    When(
        "I check if Quarantine Follow up until date was extended to ([^\"]*) day",
        (Integer days) -> {
          String date = webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE);
          softly.assertEquals(DATE_FORMATTER.format(dateFollowUp.plusDays(days)), date);
          softly.assertAll();
        });

    When(
        "I set the quarantine end to a date ([^\"]*) day after the Follow-up until date",
        (Integer days) -> {
          dateFollowUp =
              LocalDate.parse(
                  webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE), DATE_FORMATTER);
          webDriverHelpers.scrollToElement(QUARANTINE_DATE_TO_INPUT);
          webDriverHelpers.fillInWebElement(
              QUARANTINE_DATE_TO_INPUT, DATE_FORMATTER.format(dateFollowUp.plusDays(days)));
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_DATE_FROM_INPUT);
        });

    When(
        "I fill Quarantine change comment field",
        () -> {
          webDriverHelpers.scrollToElement(QUARANTINE_CHANGE_COMMENT);
          webDriverHelpers.fillInWebElement(QUARANTINE_CHANGE_COMMENT, dateFollowUp.toString());
        });

    When(
        "I check if Quarantine change comment field was saved correctly",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          String commentText = webDriverHelpers.getValueFromWebElement(QUARANTINE_CHANGE_COMMENT);
          softly.assertEquals(commentText, dateFollowUp.toString());
          softly.assertAll();
        });

    When(
        "I click on yes quarantine popup button",
        () -> webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_POPUP_SAVE_BUTTON));

    When(
        "I click on yes Extend follow up period popup button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_POPUP_SAVE_BUTTON);
        });

    When(
        "I discard changes in quarantine popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_POPUP_DISCARD_BUTTON));

    When(
        "I check if Quarantine start field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(QUARANTINE_DATE_FROM));

    When(
        "I check if Quarantine end field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(QUARANTINE_DATE_TO));

    When(
        "I select Quarantine ordered verbally checkbox",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(QUARANTINE_ORDERED_VERBALLY_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .quarantineOrderedVerbally(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          QUARANTINE_ORDERED_VERBALLY_CHECKBOX_INPUT))
                  .build();
        });

    When(
        "I check if Date of verbal order field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_THE_VERBAL_ORDER));

    When(
        "I select Quarantine ordered by official document checkbox",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .quarantineOrderedByDocument(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_INPUT))
                  .build();
        });

    When(
        "I check if Date of the official document ordered field is available",
        () ->
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                QUARANTINE_ORDERED_BY_DOCUMENT_DATE));

    When(
        "I select Official quarantine order sent",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_LABEL);
          editedCase =
              editedCase.toBuilder()
                  .quarantineOrderSet(
                      webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(
                          OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_INPUT))
                  .build();
        });
    When(
        "I check if Date official quarantine order was sent field is available",
        () ->
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                DATE_OFFICIAL_QUARANTINE_ORDER_WAS_SENT));

    When(
        "I check if Quarantine details field is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(QUARANTINE_TYPE_DETAILS));

    When(
        "I set Vaccination Status as ([^\"]*)",
        (String vaccinationStatus) -> {
          webDriverHelpers.selectFromCombobox(
              VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX,
              CaseOutcome.getValueFor(vaccinationStatus));
          editedCase = editedCase.toBuilder().vaccinationStatus(vaccinationStatus).build();
        });

    When(
        "I set German Vaccination Status as ([^\"]*)",
        (String option) -> {
          String vaccinationStatus = new String();
          switch (option) {
            case "vaccinated":
              vaccinationStatus = VACCINATED_STATUS_VACCINATED.getNameDE();
              break;
            case "unvaccinated":
              vaccinationStatus = VACCINATED_STATUS_UNVACCINATED.getNameDE();
              break;
            case "unknown":
              vaccinationStatus = VACCINATED_STATUS_UNKNOWN.getNameDE();
              break;
          }
          webDriverHelpers.selectFromCombobox(
              VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX, vaccinationStatus);
          editedCase = editedCase.toBuilder().vaccinationStatus(vaccinationStatus).build();
        });

    When(
        "I check if the specific data is correctly displayed",
        () -> {
          specificCaseData = collectSpecificData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              specificCaseData,
              editedCase,
              List.of(
                  "investigationStatus",
                  "outomeOfCase",
                  "sequelae",
                  "differentPlaceOfStayJurisdiction",
                  "placeOfStay",
                  "region",
                  "district",
                  "facilityNameAndDescription",
                  "facility",
                  "facilityCategory",
                  "facilityType",
                  "quarantine",
                  "vaccinationStatus"));
        });

    When(
        "I collect the case person UUID displayed on Edit case page",
        () -> aCase = collectCasePersonUuid());

    When(
        "I get the case person UUID displayed on Edit case page",
        () -> {
          caseUuid = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
        });

    When(
        "I check case created from created contact is correctly displayed on Edit Case page",
        () -> {
          aCase = collectCasePersonData();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription"));
        });

    When(
        "I check case created from created contact is correctly displayed on Edit Case page for DE",
        () -> {
          aCase = collectCasePersonDataDE();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeDescription"));
        });

    When(
        "I am checking all Exposure data created by UI is saved and displayed in Cases",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(START_OF_EXPOSURE_INPUT);
          String contactToCaseUIvalue =
              (webDriverHelpers.getValueFromCombobox(CONTACT_TO_CASE_COMBOBOX)).toUpperCase();
          String contactToCase =
              apiState.getLastCreatedPerson().getFirstName().toUpperCase()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName().toUpperCase()
                  + " "
                  + "("
                  + dataOperations.getPartialUuidFromAssociatedLink(
                      apiState.getCreatedContact().getUuid().toUpperCase())
                  + ")";

          softly.assertEquals(
              contactToCase,
              contactToCaseUIvalue,
              "The First Name,Last Name and Contact ID for CONTACT TO SOURCE CASE field in Exposure form is different than data filled in Contact to case");
          softly.assertAll();
          Exposure actualExposureData = collectExposureDataCase();
          ComparisonHelper.compareEqualEntities(exposureData, actualExposureData);
        });

    When(
        "I click on New Task from Case page",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_TASK_BUTTON));

    When(
        "I click on first edit Task",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_TASK_BUTTON));

    When(
        "I click on New Sample",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_SAMPLE_BUTTON));

    When(
        "I click on New Sample in German",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_SAMPLE_BUTTON_DE));

    When(
        "I click on view Sample",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SHOW_SAMPLE_BUTTON);
        });

    When(
        "I click on the Create button from Case Document Templates",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_BUTTON));

    When(
        "I click on the Create button from Case Document Templates in DE",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_BUTTON_DE));

    When(
        "I change the Case Classification field for {string} value",
        (String caseClassificationValue) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_CLASSIFICATION_FILTER_COMBOBOX,
              CaseClassification.getCaptionValueFor(caseClassificationValue));
        });

    When(
        "I create and download a case document from template",
        () -> {
          aQuarantineOrder = caseDocumentService.buildQuarantineOrder();
          aQuarantineOrder = aQuarantineOrder.toBuilder().build();
          selectQuarantineOrderTemplate(aQuarantineOrder.getDocumentTemplate());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXTRA_COMMENT_INPUT);
          fillExtraComment(aQuarantineOrder.getExtraComment());
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_BUTTON);
          //  webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
        });

    And(
        "I verify that the case document is downloaded and correctly named",
        () -> {
          String uuid = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
          String filePath =
              uuid.substring(0, 6).toUpperCase() + "-" + aQuarantineOrder.getDocumentTemplate();
          FilesHelper.waitForFileToDownload(filePath, 120);
        });

    When(
        "I open last edited case by link",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseLinkPath = "/sormas-ui/#!cases/data/";
          String uuid = aCase.getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + caseLinkPath + uuid);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(REPORT_DATE_INPUT);
        });
    When(
        "I check that text appearing in hover over Expected Follow-up is based on Report date",
        () -> {
          webDriverHelpers.waitForElementPresent(EXPECTED_FOLLOWUP_LABEL, 2);
          webDriverHelpers.hoverToElement(EXPECTED_FOLLOWUP_LABEL);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(EXPECTED_FOLLOWUP_POPUP_TEXT);
          softly.assertEquals(
              displayedText,
              "Das erwartete Nachverfolgungs bis Datum f\u00FCr diesen Fall basiert auf seinem Meldedatum ("
                  + apiState
                      .getCreatedCase()
                      .getReportDate()
                      .toInstant()
                      .atZone(ZoneId.systemDefault())
                      .toLocalDate()
                      .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                  + ")",
              "Message is incorrect");
          softly.assertAll();
        });
    When(
        "I check that text appearing in hover over Expected Follow-up is based on Symptoms collection date",
        () -> {
          TimeUnit.SECONDS.sleep(4);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPECTED_FOLLOWUP_LABEL);
          webDriverHelpers.hoverToElement(EXPECTED_FOLLOWUP_LABEL);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(EXPECTED_FOLLOWUP_POPUP_TEXT);
          softly.assertEquals(
              displayedText,
              "Das erwartete Nachverfolgungs bis Datum f\u00FCr diesen Fall basiert auf seinem fr\u00FChesten Probenentnahme-Datum ("
                  + CreateNewSampleSteps.sampleCollectionDateForFollowUpDate.format(
                      DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                  + ")",
              "Message is incorrect");
          softly.assertAll();
        });
    When(
        "I check that text appearing in hover based over Symptoms onset date over Expected Follow-up consists of date days is equal to symptoms onset date",
        () -> {
          webDriverHelpers.waitForElementPresent(EXPECTED_FOLLOWUP_LABEL, 2);
          webDriverHelpers.scrollToElement(EXPECTED_FOLLOWUP_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPECTED_FOLLOWUP_LABEL);
          webDriverHelpers.hoverToElement(EXPECTED_FOLLOWUP_LABEL);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(EXPECTED_FOLLOWUP_POPUP_TEXT);
          softly.assertEquals(
              displayedText,
              "Das erwartete Nachverfolgungs bis Datum f\u00FCr diesen Fall basiert auf seinem Symptom Startdatum ("
                  + SymptomsTabSteps.dateOfSymptomsForFollowUpDate.format(
                      DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                  + ")",
              "Message is incorrect");
          softly.assertAll();
        });
    When(
        "I check that date appearing in Expected Follow-up based on Symptoms Onset date consists of date {int} ahead of symptoms onset date",
        (Integer expected) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPECTED_FOLLOWUP_LABEL);
          String displayedText = webDriverHelpers.getValueFromWebElement(EXPECTED_FOLLOWUP_VALUE);
          softly.assertEquals(
              displayedText,
              SymptomsTabSteps.dateOfSymptomsForFollowUpDate
                  .plusDays(expected)
                  .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
              "Message is incorrect");
          softly.assertAll();
        });
    When(
        "I check that External Token field is visible on Edit Case page",
        () -> {
          boolean elementVisible =
              webDriverHelpers.isElementVisibleWithTimeout(EXTERNAL_TOKEN_INPUT, 10);
          softly.assertTrue(elementVisible, "External Token field is not visible!");
          softly.assertAll();
        });

    When(
        "I open last edited case by API via URL navigation",
        () -> {
          String caseLinkPath = "/sormas-ui/#!cases/data/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + caseLinkPath + uuid);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "I change all Case fields and save",
        () -> {
          aCase = caseService.buildEditGeneratedCase();
          aCase =
              aCase.toBuilder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
          fillDateOfReport(aCase.getDateOfReport());
          selectCaseClassification(aCase.getCaseClassification());
          selectClinicalConfirmation(aCase.getClinicalConfirmation());
          selectEpidemiologicalConfirmation(aCase.getEpidemiologicalConfirmation());
          selectLaboratoryDiagnosticConfirmation(aCase.getLaboratoryDiagnosticConfirmation());
          selectInvestigationStatus(aCase.getInvestigationStatus());
          fillExternalToken(aCase.getExternalToken());
          selectDisease(aCase.getDisease());
          selectOutcomeOfCase(aCase.getOutcomeOfCase());
          selectSequelae(aCase.getSequelae());
          selectRegion(aCase.getRegion());
          selectDistrict(aCase.getDistrict());
          selectCommunity(aCase.getCommunity());
          fillPlaceDescription(aCase.getPlaceDescription());
          selectResponsibleRegion(aCase.getResponsibleRegion());
          selectResponsibleDistrict(aCase.getResponsibleDistrict());
          selectResponsibleCommunity(aCase.getResponsibleCommunity());
          selectQuarantine(aCase.getQuarantine());
          fillReportGpsLatitude(aCase.getReportGpsLatitude());
          fillReportGpsLongitude(aCase.getReportGpsLongitude());
          fillReportGpsAccuracyInM(aCase.getReportGpsAccuracyInM());
          selectVaccinationStatusForThisDisease(aCase.getVaccinationStatusForThisDisease());
          selectResponsibleSurveillanceOfficer(aCase.getResponsibleSurveillanceOfficer());
          fillDateReceivedAtDistrictLevel(aCase.getDateReceivedAtDistrictLevel());
          fillDateReceivedAtRegionLevel(aCase.getDateReceivedAtRegionLevel());
          fillDateReceivedAtNationalLevel(aCase.getDateReceivedAtNationalLevel());
          fillGeneralComment(aCase.getGeneralComment());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I check the edited data is correctly displayed on Edit case page",
        () -> {
          editedCase = collectCaseData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              editedCase,
              aCase,
              List.of(
                  "dateOfReport",
                  "caseClassification",
                  "clinicalConfirmation",
                  "epidemiologicalConfirmation",
                  "laboratoryDiagnosticConfirmation",
                  "investigationStatus",
                  "externalToken",
                  "disease",
                  "outcomeOfCase",
                  "sequelae",
                  "region",
                  "district",
                  "community",
                  "placeDescription",
                  "responsibleJurisdiction",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "quarantine",
                  "reportGpsLatitude",
                  "reportGpsLongitude",
                  "reportGpsAccuracyInM",
                  "vaccinationStatusForThisDisease",
                  "responsibleSurveillanceOfficer",
                  "dateReceivedAtDistrictLevel",
                  "dateReceivedAtRegionLevel",
                  "dateReceivedAtNationalLevel",
                  "generalComment"));
        });

    When(
        "I delete the case",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
          webDriverHelpers.selectFromCombobox(
              DELETE_SAMPLE_REASON_POPUP, "Entity created without legal reason");
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CASE_APPLY_FILTERS_BUTTON);
        });

    When(
        "I delete the case for DE",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
          webDriverHelpers.selectFromCombobox(
              DELETE_SAMPLE_REASON_POPUP_FOR_DE,
              "L\u00F6schen auf Anforderung der betroffenen Person nach DSGVO");
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CASE_APPLY_FILTERS_BUTTON);
        });

    When(
        "I navigate to epidemiological data tab in Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I navigate to Event Participants tab in Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          TimeUnit.SECONDS.sleep(6);
        });
    When(
        "I navigate to Contacts tab in Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACTS_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the New Travel Entry button from Edit case page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_TRAVEL_ENTRY_BUTTON_DE);
        });

    When(
        "I click on edit travel entry button form case epidemiological tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_TRAVEL_ENTRY_FROM_CASE_BUTTON);
        });

    When(
        "I check that case classification is set to not yet classified in German on Edit case page",
        () -> {
          String caseClassification =
              webDriverHelpers.getValueFromCombobox(CASE_CLASSIFICATION_COMBOBOX);
          softly.assertEquals(
              caseClassification,
              "0. Nicht klassifiziert",
              "The case classification is incorrect!");
          softly.assertAll();
        });

    When(
        "I check that case reference definition is not editable on Edit case page",
        () -> {
          String referenceReadOnlyAttribute =
              webDriverHelpers.getAttributeFromWebElement(REFERENCE_DEFINITION_TEXT, "readonly");
          softly.assertNotNull(
              referenceReadOnlyAttribute,
              "The case reference definition shouldn't be editable, but it is!");
          softly.assertAll();
        });

    When(
        "I check that case reference definition is set to not fulfilled in German on Edit case page",
        () -> {
          String caseReference = webDriverHelpers.getValueFromWebElement(REFERENCE_DEFINITION_TEXT);
          softly.assertEquals(
              caseReference, "Nicht erf\u00FCllt", "The case reference definition is incorrect!");
          softly.assertAll();
        });

    When(
        "I check that case reference definition is set to fulfilled in German on Edit case page",
        () -> {
          String caseReference = webDriverHelpers.getValueFromWebElement(REFERENCE_DEFINITION_TEXT);
          softly.assertEquals(
              caseReference, "Erf\u00FCllt", "The case reference definition is incorrect!");
          softly.assertAll();
        });

    When(
        "I search and chose the last case uuid created via UI in the CHOOSE CASE Contact window",
        () -> {
          webDriverHelpers.fillInWebElement(
              SOURCE_CASE_WINDOW_CONTACT_DE, EditCaseSteps.aCase.getUuid());
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitForRowToBeSelected(SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE);
        });

    When(
        "I check that case classification is set to one of the confirmed classifications in German on Edit case page",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitForElementPresent(CASE_CLASSIFICATION_COMBOBOX, 3);
          String caseClassification =
              webDriverHelpers.getValueFromCombobox(CASE_CLASSIFICATION_COMBOBOX);
          softly.assertTrue(
              Arrays.asList(
                      "A. Klinisch diagnostiziert",
                      "B. Klinisch-epidemiologisch best\u00E4tigt",
                      "C. Klinisch-labordiagnostisch best\u00E4tigt",
                      "D. Labordiagnostisch bei nicht erf\u00FCllter Klinik",
                      "E. Labordiagnostisch bei unbekannter Klinik")
                  .contains(caseClassification),
              "The case classification is incorrect!");
          softly.assertAll();
        });

    And(
        "^I select \"([^\"]*)\" as Outcome of Case in Edit case page$",
        (String outcomeStatus) -> {
          webDriverHelpers.clickWebElementByText(
              OUTCOME_OF_CASE_OPTIONS, CaseOutcome.getValueFor(outcomeStatus).toUpperCase());
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I set pregnancy to ([^\"]*)",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(PREGNANCY_OPTIONS, option);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check that trimester field is present",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(TRIMESTER_OPTIONS));

    When(
        "I click on the Archive case button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CASE_BUTTON);
        });

    When(
        "I check the end of processing date in the archive popup and select Archive contacts checkbox",
        () -> {
          String endOfProcessingDate;
          endOfProcessingDate =
              webDriverHelpers.getValueFromWebElement(END_OF_PROCESSING_DATE_POPUP_INPUT);
          softly.assertEquals(
              endOfProcessingDate,
              LocalDate.now().format(DATE_FORMATTER),
              "End of processing date is invalid");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_RELATED_CONTACTS_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check the end of processing date in the archive popup and select Archive contacts checkbox for DE",
        () -> {
          String endOfProcessingDate;
          endOfProcessingDate =
              webDriverHelpers.getValueFromWebElement(END_OF_PROCESSING_DATE_POPUP_INPUT);

          softly.assertEquals(
              endOfProcessingDate,
              LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
              "End of processing date is invalid");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_RELATED_CONTACTS_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check the end of processing date in the archive popup and not select Archive contacts checkbox",
        () -> {
          String endOfProcessingDate;
          endOfProcessingDate =
              webDriverHelpers.getValueFromWebElement(END_OF_PROCESSING_DATE_POPUP_INPUT);
          softly.assertEquals(
              endOfProcessingDate,
              LocalDate.now().format(DATE_FORMATTER),
              "End of processing date is invalid");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    And(
        "I check if Infrastructure Data Has Change popup is displayed",
        () -> webDriverHelpers.isElementVisibleWithTimeout(INFRASTRUCTURE_DATA_POPUP, 1));

    And(
        "I click on TRANSFER CASE in Infrastructure Data Has Change popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    Then(
        "^I check that follow-up status is set to Under follow-up in German on Edit case page$",
        () -> {
          String caseFollowUpStatus =
              webDriverHelpers.getValueFromWebElement(FOLLOW_UP_STATUS_INPUT);
          softly.assertEquals(
              caseFollowUpStatus, "In der Nachverfolgung", "The follow-up status is incorrect!");
          softly.assertAll();
        });
    When(
        "I click to edit {int} vaccination on Edit Case page",
        (Integer index) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getVaccinationByIndex(String.valueOf(index + 1)));
          webDriverHelpers.clickOnWebElementBySelector(
              getVaccinationByIndex(String.valueOf(index + 1)));
        });
    When(
        "I close vaccination form in Edit Case directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_POPUP));
    When(
        "I check that number of added Vaccinations is {int} on Edit Case Page",
        (Integer expected) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(BUTTONS_IN_VACCINATIONS_LOCATION),
                        (int) expected,
                        "Number of vaccinations is different than expected")));
    When(
        "^I click on the Cancel Follow-up button from Edit case page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(CANCEL_FOLLOW_UP_BUTTON));

    Then(
        "^I check that Date of Follow-up Status Change and Responsible User are correctly displayed on Edit case page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DATE_OF_FOLLOW_UP_STATUS_CHANGE);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              RESPONSIBLE_USER_FOR_FOLLOW_UP_STATUS_CHANGE);
          String responsibleUserForFollowUpStatusChange;
          responsibleUserForFollowUpStatusChange =
              webDriverHelpers.getValueFromWebElement(
                  RESPONSIBLE_USER_FOR_FOLLOW_UP_STATUS_CHANGE_INPUT);
          softly.assertEquals(
              getDateOfFollowUpStatusChangeDE(),
              LocalDate.now(),
              "Date of follow-up status change is invalid!");

          softly.assertEquals(
              responsibleUserForFollowUpStatusChange, "Nat USER", "Responsible User is invalid!");

          softly.assertAll();
        });

    Then(
        "^I provide follow-up status comment from Edit case page$",
        () -> fillFollowUpStatusComment("Follow-up status comment"));

    When(
        "^I click on the Resume Follow-up button from Edit case page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(RESUME_FOLLOW_UP_BUTTON));

    And(
        "^I click on the Lost to Follow-up button from Edit case page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LOST_TO_FOLLOW_UP_BUTTON));

    And(
        "^I check that Expected Follow-up Until Date is correctly displayed on Edit case page$",
        () -> {
          LocalDate calculatedExpectedFollowUpDate = getDateOfReportDE().plusDays(14);

          softly.assertEquals(
              getExpectedFollowUpUntilDateDE(),
              calculatedExpectedFollowUpDate,
              "Expected follow-up until date is invalid!");
          softly.assertAll();
        });

    When(
        "^I select Overwrite Follow-up Until Date checkbox on Edit case page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(OVERWRITE_FOLLOW_UP_UNTIL_DATE_LABEL));

    And(
        "^I set the Follow-up Until Date to exceed the Expected Follow-up Until Date on Edit case page$",
        () -> {
          LocalDate dateExceedingExpectedDate = getExpectedFollowUpUntilDateDE().plusDays(7);
          fillFollowUpUntilDateDE(dateExceedingExpectedDate);
        });

    Then(
        "^I check if the Follow-up Until Date is correctly displayed on Edit case page$",
        () -> {
          LocalDate dateExceedingExpectedDate = getExpectedFollowUpUntilDateDE().plusDays(7);
          softly.assertEquals(
              getFollowUpUntilDateDE(),
              dateExceedingExpectedDate,
              "Follow-up until date is invalid!");
        });
    When(
        "^I check that Point Of Entry information is displayed as read-only on Edit case page$",
        () -> {
          String referenceReadOnlyAttribute =
              webDriverHelpers.getAttributeFromWebElement(POINT_OF_ENTRY_TEXT, "readonly");
          softly.assertNotNull(
              referenceReadOnlyAttribute,
              "The case reference definition shouldn't be editable, but it is!");
          softly.assertAll();
        });

    And(
        "I refer case from Point Of Entry with Place of Stay ZUHAUSE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(REFER_CASE_FROM_POINT_OF_ENTRY);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              REFER_CASE_FROM_POINT_OF_ENTRY_POPUP_DE);
          webDriverHelpers.selectFromCombobox(REFER_CASE_FROM_POINT_OF_ENTRY_REGION, "Saarland");
          webDriverHelpers.selectFromCombobox(
              REFER_CASE_FROM_POINT_OF_ENTRY_DISTRICT, "LK Saarlouis");
          webDriverHelpers.clickWebElementByText(PLACE_OF_STAY_OPTIONS, "ZUHAUSE");
          webDriverHelpers.clickOnWebElementBySelector(REFER_CASE_FROM_POINT_OF_ENTRY_SAVE_BUTTON);
        });

    And(
        "I refer case from Point Of Entry with Place of Stay EINRICHTUNG",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(REFER_CASE_FROM_POINT_OF_ENTRY);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              REFER_CASE_FROM_POINT_OF_ENTRY_POPUP_DE);
          webDriverHelpers.selectFromCombobox(REFER_CASE_FROM_POINT_OF_ENTRY_REGION, "Brandenburg");
          webDriverHelpers.selectFromCombobox(REFER_CASE_FROM_POINT_OF_ENTRY_DISTRICT, "LK Barnim");
          webDriverHelpers.clickWebElementByText(PLACE_OF_STAY_OPTIONS, "EINRICHTUNG");
          webDriverHelpers.selectFromCombobox(
              REFER_CASE_FROM_POINT_OF_ENTRY_FACILITY_CATEGORY, "Medizinische Einrichtung");
          webDriverHelpers.selectFromCombobox(
              REFER_CASE_FROM_POINT_OF_ENTRY_FACILITY_TYPE, "Krankenhaus");
          webDriverHelpers.selectFromCombobox(
              REFER_CASE_FROM_POINT_OF_ENTRY_HEALTH_FACILITY, "share");

          webDriverHelpers.clickOnWebElementBySelector(REFER_CASE_FROM_POINT_OF_ENTRY_SAVE_BUTTON);
        });

    And(
        "^I check that Point Of Entry and Place Of Stay ZUHAUSE information is correctly display on Edit case page$",
        () -> {
          String referenceReadOnlyAttribute =
              webDriverHelpers.getAttributeFromWebElement(POINT_OF_ENTRY_TEXT, "readonly");
          softly.assertNotNull(
              referenceReadOnlyAttribute,
              "The case reference definition shouldn't be editable, but it is!");

          softly.assertEquals(
              webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                  PLACE_OF_STAY_SELECTED_VALUE),
              "ZUHAUSE",
              "Place of stay is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX),
              "Saarland",
              "Place of stay region is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX),
              "LK Saarlouis",
              "Place of stay district is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_TEXT),
              "Anderer Flughafen",
              "Point of entry is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_DETAILS),
              "Narita",
              "Point of entry details are not correct");

          softly.assertAll();
        });

    And(
        "^I check that Point Of Entry and Place Of Stay EINRICHTUNG information is correctly display on Edit case page$",
        () -> {
          String referenceReadOnlyAttribute =
              webDriverHelpers.getAttributeFromWebElement(POINT_OF_ENTRY_TEXT, "readonly");
          softly.assertNotNull(
              referenceReadOnlyAttribute,
              "The case reference definition shouldn't be editable, but it is!");

          softly.assertEquals(
              webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                  PLACE_OF_STAY_SELECTED_VALUE),
              "EINRICHTUNG",
              "Place of stay is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX),
              "Brandenburg",
              "Place of stay region is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX),
              "LK Barnim",
              "Place of stay district is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_TEXT),
              "Anderer Flughafen",
              "Point of entry is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_DETAILS),
              "other",
              "Point of entry details are not correct");

          softly.assertAll();
        });

    And(
        "^I check that Case Origin is set to Point Of Entry$",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(CASE_ORIGIN),
              "Einreiseort",
              "Point of entry is not correct");
          softly.assertAll();
        });

    Then(
        "^I check that differing Point Of Entry is correctly displayed on Edit case page$",
        () -> {
          String referenceReadOnlyAttribute =
              webDriverHelpers.getAttributeFromWebElement(POINT_OF_ENTRY_TEXT, "readonly");
          softly.assertNotNull(
              referenceReadOnlyAttribute,
              "The case reference definition shouldn't be editable, but it is!");

          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_TEXT),
              "Anderer Einreiseort",
              "Point of entry is not correct");

          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_DETAILS),
              "Automated test dummy description "
                  + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
              "Point of entry details are not correct");

          softly.assertAll();
        });
    When(
        "I click on the Archive case button and confirm popup",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CASE_BUTTON);
          webDriverHelpers.scrollToElement(CONFIRM_ACTION);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ACTION);
        });
    When(
        "I check that {string} button is readonly on Edit case page",
        (String button) -> {
          switch (button) {
            case "Discard":
              softly.assertEquals(
                  webDriverHelpers.isElementEnabled(DISCARD_BUTTON),
                  false,
                  "Discard button is editable state!");
              break;
            case "Save":
              softly.assertEquals(
                  webDriverHelpers.isElementEnabled(SAVE_BUTTON),
                  false,
                  "Save button is editable state!");
              break;
          }
        });
    When(
        "I check if editable fields are enabled for the case in view",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitForElementPresent(BACK_TO_CASES_LIST_BUTTON, 3);
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(INVESTIGATION_STATUS_OPTIONS),
              true,
              "Investigation status option is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISEASE_COMBOBOX),
              true,
              "Disease combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(OUTCOME_OF_CASE_OPTIONS),
              true,
              "Outcome of case is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(FACILITY_CATEGORY_COMBOBOX),
              true,
              "Facility category is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_COMMUNITY_COMBOBOX),
              true,
              "Responsible community combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_DISTRICT_COMBOBOX),
              true,
              "Responsible district is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_REGION_COMBOBOX),
              true,
              "Responsible region combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(FACILITY_TYPE_COMBOBOX),
              true,
              "Facility type combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(EditEventPage.EDIT_EVENT_PAGE_SAVE_BUTTON),
              true,
              "Save button is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISCARD_BUTTON),
              true,
              "Discard button is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DELETE_BUTTON),
              true,
              "Delete button is not editable state but it should be since archived entities default value is true!");
          softly.assertAll();
        });

    And(
        "I fill general comment in case edit page with ([^\"]*)",
        (String comment) -> {
          webDriverHelpers.fillInWebElement(EditContactPage.GENERAL_COMMENT_TEXT, comment);
        });

    And(
        "I fill in the Internal Token field in Edit Case page with ([^\"]*)",
        (String token) -> {
          webDriverHelpers.scrollToElementUntilIsVisible(INTERNAL_TOKEN_INPUT);
          webDriverHelpers.fillInWebElement(INTERNAL_TOKEN_INPUT, token);
        });

    When(
        "I check if Type of sample has not a ([^\"]*) option",
        (String option) -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(SAMPLE_TYPE_COMBOBOX, option),
              "Type of sample is incorrect");
          softly.assertAll();
        });

    When(
        "I check if Sample card has empty and no buttons are available on Edit Case Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(SAMPLES_CARD_EMPTY_MESSAGE),
              "Element is not present");
          softly.assertAll();
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(NEW_SAMPLE_BUTTON, 2),
              "The new sample button is present");
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(SEE_SAMPLE_BUTTON, 2),
              "The see sample for this person button is present");
          softly.assertAll();
        });

    When(
        "I check if Sample card has available {string} button on Edit Case Page",
        (String button) -> {
          TimeUnit.SECONDS.sleep(2); // waiting for page loaded
          switch (button) {
            case "see sample for this person":
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(SEE_SAMPLE_BUTTON), "Element is not present");
              break;
            case "see samples for this person":
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(SEE_SAMPLE_BUTTON), "Element is not present");
              break;
          }
          softly.assertAll();
        });

    When(
        "I click on the {string} button on Edit Case Page",
        (String button) -> {
          TimeUnit.SECONDS.sleep(3); // waiting for page loaded;
          switch (button) {
            case "See samples for this person":
              webDriverHelpers.scrollToElement(SEE_SAMPLE_BUTTON);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SEE_SAMPLE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(SEE_SAMPLE_BUTTON);
              break;
            case "See cases for this person":
              webDriverHelpers.scrollToElement(SEE_CASES_FOR_THIS_PERSON_BUTTON);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  SEE_CASES_FOR_THIS_PERSON_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(SEE_CASES_FOR_THIS_PERSON_BUTTON);
              break;
            case "See contacts for this person":
              webDriverHelpers.scrollToElement(SEE_CONTACTS_FOR_THIS_PERSON_BUTTON);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  SEE_CONTACTS_FOR_THIS_PERSON_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(SEE_CONTACTS_FOR_THIS_PERSON_BUTTON);
              break;
          }
        });

    When(
        "I click on the NEW IMMUNIZATION button in Edit case",
        () -> {
          webDriverHelpers.scrollToElement(NEW_IMMUNIZATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_IMMUNIZATION_BUTTON);
        });

    When(
        "I click on Edit Immunization button on Edit Case",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_IMMUNIZATION_BUTTON));

    And(
        "^I click on the NEW IMMUNIZATION button from Edit case page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NEW_IMMUNIZATION_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(NEW_IMMUNIZATION_BUTTON);
        });

    And(
        "^I click on save button in New Immunization form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EditImmunizationPage.UUID);
        });

    And(
        "^I navigate to linked immunization on Edit case page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(
              getByImmunizationUuid(EditImmunizationSteps.collectedImmunization.getUuid()));

          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              EditImmunizationPage.DATE_OF_REPORT_INPUT);
        });

    When(
        "I click on Delete button from case",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
        });

    When(
        "I click on Restore button from case",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
        });

    When(
        "I check Delete button from case is enabled",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          softly.assertTrue(
              webDriverHelpers.isElementEnabled(DELETE_BUTTON),
              "Delete case button is not enabled");
          softly.assertAll();
        });

    When(
        "I check if EPID number input is disabled in Edit Case",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(EPID_NUMBER_INPUT), "EPID number input is enabled");
          softly.assertAll();
        });

    When(
        "I check if General comment test area is disabled in Edit Case",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(GENERAL_COMMENT_TEXT_AREA),
              "General comment test area is enabled");
          softly.assertAll();
        });

    When(
        "I check if {string} field is available in Confirm deletion popup in Edit Case",
        (String label) ->
            webDriverHelpers.isElementVisibleWithTimeout(
                getReasonForDeletionDetailsFieldLabel(label), 1));

    And(
        "^I validate immunization period is present on immunization card$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAVE_BUTTON);
          String displayedPeriod =
              webDriverHelpers.getTextFromWebElement(IMMUNIZATION_CARD_IMMUNIZATION_PERIOD_LABEL);
          LocalDate startDate = EditImmunizationSteps.collectedImmunization.getStartDate();
          LocalDate endDate = EditImmunizationSteps.collectedImmunization.getEndDate();
          DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          softly.assertEquals(
              "Immunization period: "
                  + dateFormatter.format(startDate)
                  + " - "
                  + dateFormatter.format(endDate),
              displayedPeriod,
              "Immunization period is not equal");
          softly.assertAll();
        });

    And(
        "^I validate immunization status is present on immunization card$",
        () -> {
          String actualImmunizationStatus =
              webDriverHelpers.getTextFromWebElement(IMMUNIZATION_CARD_IMMUNIZATION_STATUS_LABEL);
          String expectedImmunizationStatus =
              EditImmunizationSteps.collectedImmunization.getImmunizationStatus();
          softly.assertEquals(
              "Immunization status: " + expectedImmunizationStatus,
              actualImmunizationStatus,
              "Immunization status is not equal");
          softly.assertAll();
        });

    And(
        "^I validate management status is present on immunization card$",
        () -> {
          String actualManagementStatus =
              webDriverHelpers.getTextFromWebElement(IMMUNIZATION_CARD_MANAGEMENT_STATUS_LABEL);
          String expectedManagementStatus =
              EditImmunizationSteps.collectedImmunization.getManagementStatus();
          softly.assertEquals(
              "Management status: " + expectedManagementStatus,
              actualManagementStatus,
              "Management status is not equal");
          softly.assertAll();
        });

    And(
        "^I validate means of immunization is present on immunization card$",
        () -> {
          String actualMeansOfImmunization =
              webDriverHelpers.getTextFromWebElement(IMMUNIZATION_CARD_MEANS_OF_IMMUNIZATION_LABEL);
          String expectedMeansOfImmunization =
              EditImmunizationSteps.collectedImmunization.getMeansOfImmunization();
          softly.assertEquals(
              "Means of immunization: " + expectedMeansOfImmunization,
              actualMeansOfImmunization,
              "Means of immunization is not equal");
          softly.assertAll();
        });

    And(
        "^I validate immunization UUID is present on immunization card$",
        () -> {
          String actualImmunizationUUID =
              webDriverHelpers.getTextFromWebElement(IMMUNIZATION_CARD_IMMUNIZATION_UUID);
          String expectedImmunizationUUID = EditImmunizationSteps.collectedImmunization.getUuid();
          softly.assertEquals(
              expectedImmunizationUUID.substring(0, 6),
              actualImmunizationUUID,
              "Means of immunization is not equal");
          softly.assertAll();
        });

    And(
        "I check elements order on page before General comment field in DE",
        () -> {
          Assert.assertTrue(
              webDriverHelpers.isElementEnabled(SURVEILLANCE_OFFICER_FIELD_ABOVE_GENERAL_COMMENT));
        });

    When(
        "I change disease to {string} in the case tab",
        (String disease) -> {
          webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CHANGE_DISEASE_CONFIRMATION_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM);
        });

    When(
        "I check the end of processing date in the archive popup and select Archive cases checkbox",
        () -> {
          String endOfProcessingDate;
          endOfProcessingDate =
              webDriverHelpers.getValueFromWebElement(END_OF_PROCESSING_DATE_POPUP_INPUT);
          softly.assertEquals(
              endOfProcessingDate,
              LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
              "End of processing date is invalid");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_RELATED_CONTACTS_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on De-Archive case button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CASE_BUTTON);
        });

    When(
        "I click on confirm button in de-archive case popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP));

    When(
        "I click on discard button in de-archive case popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL_POPUP));

    When(
        "I fill De-Archive case popup with ([^\"]*)",
        (String text) -> {
          webDriverHelpers.fillInWebElement(DEARCHIVE_REASON_TEXT_AREA, text);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_DEARCHIVE_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I change date of case report for today for DE version",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_OF_REPORT_NO_POPUP_INPUT, formatter.format(LocalDate.now()));
        });

    When(
        "I check if exclamation mark with message {string} appears while trying to de-archive without reason",
        (String message) -> {
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          String hoverMessage;
          webDriverHelpers.hoverToElement(REASON_FOR_DELETION_EXCLAMATION_MARK);
          hoverMessage = webDriverHelpers.getTextFromWebElement(REASON_FOR_DELETION_MESSAGE);
          softly.assertEquals(message, hoverMessage, "Messages are not equal");
          softly.assertAll();
        });

    And(
        "^I set current date as a date of report on Edit case page for DE version$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(REPORT_DATE_INPUT);
          fillDateOfReportDE(LocalDate.now());
        });

    And(
        "^I check that displayed vaccination card has correct vaccination date and name$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              EditCasePage.SAVE_BUTTON);
          softly.assertEquals(
              CreateNewVaccinationSteps.vaccination.getVaccinationDate(),
              collectVaccinationData().getVaccinationDate(),
              "Vaccination date is incorrect");
          softly.assertEquals(
              "Impfstoffname: " + CreateNewVaccinationSteps.vaccination.getVaccineName(),
              collectVaccinationData().getVaccineName(),
              "Vaccination name is incorrect");
          softly.assertAll();
        });

    Then(
        "^I check if an edit icon is available on vaccination card on Edit Case page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EDIT_VACCINATION_BUTTON);
        });

    Then(
        "^I check that vaccination entry is greyed out in the vaccination card$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.isElementGreyedOut(VACCINATION_CARD_VACCINATION_NAME);
          webDriverHelpers.isElementGreyedOut(VACCINATION_CARD_VACCINATION_DATE);
        });

    And(
        "^I check the displayed message is correct after hovering over the Vaccination Card Info icon on Edit Case Page for DE$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(VACCINATION_CARD_INFO_ICON);
          webDriverHelpers.hoverToElement(VACCINATION_CARD_INFO_ICON);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(VACCINATION_CARD_INFO_POPUP_TEXT);
          softly.assertEquals(
              displayedText,
              "Diese Impfung ist f\u00FCr diesen Fall nicht relevant, weil das Datum der Impfung nach dem Datum des Symptombeginns oder dem Fall-Meldedatum liegt.",
              "Message is incorrect");
          softly.assertAll();
        });

    And(
        "^I click on the Edit Vaccination icon on vaccination card on Edit Case page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_VACCINATION_BUTTON);
        });

    And(
        "^I check that the vaccination card displays \"([^\"]*)\" in place of the vaccination date$",
        (String vaccinationDateDescription) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(REPORT_DATE_INPUT);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(VACCINATION_CARD_VACCINATION_DATE),
              vaccinationDateDescription,
              "Vaccination date description is incorrect");
          softly.assertAll();
        });

    Then(
        "^I click Link Event button on Edit Case Page for DE$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LINK_EVENT_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_BUTTON_DE);
        });

    Then(
        "^I click Link Event button on Edit Case Page$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LINK_EVENT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_BUTTON);
        });

    And(
        "^I click SAVE in Add Event Participant form on Edit Case Page for DE$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ADD_A_PARTICIPANT_HEADER_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
        });

    And(
        "^I click SAVE button on Create New Event form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "I check if disease is set for {string} in Case Edit Directory",
        (String disease) -> {
          webDriverHelpers.scrollToElement(DISEASE_INPUT);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(DISEASE_INPUT), disease, "Incorrect disease");
          softly.assertAll();
        });

    And(
        "^I check the created data for different place of stay region and district are correctly displayed on Edit case page$",
        () -> {
          aCase = collectCasePersonDataWithDifferentPlaceOfStay();
          System.out.print(collectCasePersonDataWithDifferentPlaceOfStay().getPlaceOfStayRegion());
          System.out.print(
              collectCasePersonDataWithDifferentPlaceOfStay().getPlaceOfStayDistrict());
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "placeOfStayRegion",
                  "placeOfStayDistrict",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    And(
        "^I check that the responsible jurisdiction region is different from the place of stay region$",
        () -> {
          softly.assertNotEquals(
              collectCasePersonDataWithDifferentPlaceOfStay().getPlaceOfStayRegion(),
              collectCasePersonDataWithDifferentPlaceOfStay().getResponsibleRegion(),
              "The responsible jurisdiction region is not different from the place of stay region!");
          softly.assertAll();
        });

    And(
        "^I check that the responsible jurisdiction district is different from the place of stay district$",
        () -> {
          softly.assertNotEquals(
              collectCasePersonDataWithDifferentPlaceOfStay().getPlaceOfStayDistrict(),
              collectCasePersonDataWithDifferentPlaceOfStay().getResponsibleDistrict(),
              "The responsible jurisdiction district is not different from the place of stay district!");
          softly.assertAll();
        });

    And(
        "^I check the facility and place of stay created data are correctly displayed on Edit case page$",
        () -> {
          aCase = collectCasePersonDataWithFacilityAndDifferentPlaceOfStay();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "placeOfStay",
                  "placeOfStayRegion",
                  "placeOfStayDistrict",
                  "facility",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    And(
        "^I check the point of entry and place of stay created data are correctly displayed on Edit case page$",
        () -> {
          aCase = collectCasePersonDataWithPointOfEntryAndDifferentPlaceOfStay();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              createdCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "placeOfStayRegion",
                  "placeOfStayDistrict",
                  "pointOfEntry",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    And(
        "I set case vaccination status to ([^\"]*)",
        (String vaccinationStatus) -> {
          webDriverHelpers.selectFromCombobox(VACCINATION_STATUS_COMBOBOX, vaccinationStatus);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
    When(
        "I click to share samples of the case in Share popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAHRE_SAMPLES_CHECKBOX));
    When(
        "I click on share immunizations of the case in Share popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHARE_IMMUNIZATION_CHECKBOX));

    When(
        "I click to share reports of the case in Share popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHARE_REPORTS_CHECKBOX));

    And(
        "^I check that displayed vaccination name is equal to \"([^\"]*)\" on Edit case page$",
        (String expectedName) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(VACCINATION_CARD_VACCINATION_NAME);
          String name = webDriverHelpers.getTextFromWebElement(VACCINATION_CARD_VACCINATION_NAME);
          softly.assertEquals(
              name,
              "Impfstoffname: " + expectedName,
              "Vaccination name is different than expected!");
          softly.assertAll();
        });

    And(
        "I check that displayed vaccination name is {string} on Edit case page",
        (String activationState) -> {
          switch (activationState) {
            case "enabled":
              Assert.assertTrue(
                  webDriverHelpers.isElementEnabled(VACCINATION_CARD_VACCINATION_NAME),
                  "Vaccination name is not enabled!");
              break;
            case "greyed out":
              webDriverHelpers.isElementGreyedOut(VACCINATION_CARD_VACCINATION_NAME);
              break;
          }
        });

    And(
        "^I check that follow-up status comment is correctly displayed on Edit case page$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(FOLLOW_UP_COMMENT_FIELD);
          String actualFollowUpStatusComment =
              webDriverHelpers.getValueFromWebElement(FOLLOW_UP_COMMENT_FIELD);
          String expectedFollowUpStatusComment =
              EditContactSteps.editedContact.getFollowUpStatusComment();
          softly.assertEquals(
              actualFollowUpStatusComment,
              expectedFollowUpStatusComment,
              "Follow-up status comment is incorrect!");
          softly.assertAll();
        });

    And(
        "I check that {string} Pre-existing condition is visible on page",
        (String preExistingCondition) ->
            Assert.assertTrue(
                webDriverHelpers.isElementEnabled(
                    getPreExistingConditionCombobox_DE(preExistingCondition))));
    And(
        "I check that {string} Pre-existing condition have {string} selected",
        (String preExistingCondition, String value) ->
            Assert.assertTrue(
                webDriverHelpers.isElementEnabled(
                    getPreExistingConditionComboboxWithValue_DE(preExistingCondition, value))));

    And(
        "I select the Pre-existing condition {string} as {string} on Edit Case page",
        (String preExistingCondition, String value) -> {
          webDriverHelpers.scrollToElement(
              getPreExistingConditionComboboxToSelectValue_DE(preExistingCondition));
          webDriverHelpers.clickWebElementByText(
              getPreExistingConditionComboboxToSelectValue_DE(preExistingCondition),
              value.toUpperCase());
        });

    And(
        "I select {string} from documents templates list",
        (String templateName) -> {
          selectQuarantineOrderTemplate(templateName);
        });
    Then(
        "I click download in case document create page in DE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_BUTTON_DE);
          TimeUnit.SECONDS.sleep(10);
        });

    And(
        "^I click on edit task icon of the (\\d+) displayed task on Edit Case page$",
        (Integer taskNumber) -> {
          webDriverHelpers.clickOnWebElementBySelector(getEditTaskButtonByNumber(taskNumber - 1));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    And(
        "^I check that the Archive case button is not available$",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ARCHIVE_CASE_BUTTON, 2),
              "Archive case button is visible!");
          softly.assertAll();
        });

    And(
        "^I check if date of report is set for (\\d+) day ago from today on Edit Case page for DE version$",
        (Integer days) -> {
          String actualReportDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
          String expectedReportDate =
              (LocalDate.now().minusDays(days)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
          Assert.assertEquals(actualReportDate, expectedReportDate, "Report date is incorrect!");
        });

    And(
        "^I choose the reason of deletion in popup for Vaccination for DE version$",
        () -> {
          webDriverHelpers.selectFromCombobox(
              DELETE_VACCINATION_REASON_POPUP_DE_VERSION, "Anderer Grund");
          webDriverHelpers.fillInWebElement(REASON_FOR_DELETION_DETAILS_TEXTAREA, "Other reason");
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
        });

    And(
        "^I choose \"([^\"]*)\" in Vaccination Status update popup for DE version$",
        (String option) -> {
          webDriverHelpers.isElementVisibleWithTimeout(VACCINATION_STATUS_UPDATE_POPUP_HEADER, 5);
          switch (option) {
            case "JA":
              webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
              break;
            case "NEIN":
              webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL);
              break;
          }
          TimeUnit.SECONDS.sleep(3); // wait for reaction
        });

    And(
        "^I check that vaccination is removed from vaccination card on Edit Case page$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          boolean elementVisible =
              webDriverHelpers.isElementVisibleWithTimeout(EDIT_VACCINATION_BUTTON, 5);
          softly.assertFalse(elementVisible, "Vaccination ID is visible!");
          softly.assertAll();
        });

    When(
        "I check if element with text {string} is present in Case Edit",
        (String text) -> {
          Assert.assertTrue(webDriverHelpers.isElementPresent(checkIfTextExists(text)));
        });
    And(
        "I validate last created via API Event data is displayed under Linked Events section",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LINKED_EVENT_TITLE);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(LINKED_EVENT_TITLE),
              apiState.getCreatedEvent().getEventTitle(),
              "Event title is not correct");
          softly.assertAll();
        });
    When(
        "I check if reject share case button in Edit Case is unavailable",
        () -> {
          softly.assertFalse(webDriverHelpers.isElementPresent(REJECT_SHARED_CASE_BUTTON));
          softly.assertAll();
        });
    When(
        "I click on revoke share button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(REJECT_SHARED_CASE_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });
    When(
        "I click on Ja button in Revoke case popup",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(REVOKE_CASE_POPUP_HEADER, 2));
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });
    When(
        "I check if popup with error with handover displays",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_IN_HANDOVER_HEADER_DE, 3));
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_DESCRIPTION_REQUEST_PROCESSED, 3));
          softly.assertAll();
        });
    When(
        "I check if popup with revoke error with handover displays",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_REVOKE_IN_HANDOVER_HEADER_DE, 3));
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(
                  ERROR_REVOKE_DESCRIPTION_REQUEST_PROCESSED, 3));
          softly.assertAll();
        });

    When(
        "I check if share warning is displayed",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.isElementPresent(SHARE_PENDING_WARNING_DE);
        });

    When(
        "I check if popup with error with handover header displays",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_IN_HANDOVER_HEADER_DE, 3));
          softly.assertAll();
        });

    // TODO -> refactor, bad approach to keep logic here for 2 pages
    And(
        "^I check if editable fields are read only for shared case/contact$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EditCasePage.UUID_INPUT);
          webDriverHelpers.isElementGreyedOut(EditCasePage.UUID_INPUT);
          webDriverHelpers.isElementGreyedOut(EditCasePage.SAVE_BUTTON);
        });

    And(
        "^I click on Send to reporting tool button on Edit Case page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SEND_TO_REPORTING_TOOL_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SEND_TO_REPORTING_TOOL_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ACTION);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(REPORTING_TOOL_MESSAGE);
        });

    When(
        "I check that Reporting tool in Survnet box contain {string} entry",
        (String entry) -> {
          softly.assertTrue(
              webDriverHelpers.isElementPresent(checkTextInReportingToolComponent(entry)),
              "Element is not present");
          softly.assertAll();
        });

    And(
        "^I collect case external UUID from Edit Case page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EXTERNAL_ID_INPUT);
          externalUUID.add(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT));
        });

    And(
        "^I click on edit Report on Edit Case page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EDIT_REPORT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_REPORT_BUTTON);
        });

    Then(
        "^I check that Reporter Facility in Edit report form is set to \"([^\"]*)\"$",
        (String reporterFacility) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(REPORTER_FACILITY_INPUT),
              reporterFacility,
              "Reporter Facility is incorrect");
          softly.assertAll();
        });

    And(
        "^I check that Reporter Facility Details in Edit report form is set to \"([^\"]*)\"$",
        (String reporterFacilityDetails) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(REPORTER_FACILITY_DETAILS_INPUT),
              reporterFacilityDetails,
              "Reporter Facility Details are incorrect");
          softly.assertAll();
        });

    And(
        "^I check if external message window appears and close it$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LAB_MESSAGE_WINDOW_HEADER_DE);
          webDriverHelpers.clickOnWebElementBySelector(WINDOW_CLOSE_BUTTON);
        });

    And(
        "^I check that the value selected from Disease variant combobox is \"([^\"]*)\" on Edit Case page$",
        (String expectedDiseaseVariant) -> {
          String prefilledDiseaseVariant =
              webDriverHelpers.getValueFromWebElement(DISEASE_VARIANT_INPUT);
          softly.assertEquals(
              prefilledDiseaseVariant,
              expectedDiseaseVariant,
              "The disease variant is incorrectly");
          softly.assertAll();
        });

    And(
        "^I click the Resulted from nosocomial outbreak checkbox on Edit Case page for DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NOSOCOMIAL_OUTBRAKE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(NOSOCOMIAL_OUTBRAKE_LABEL);
        });

    When(
        "I check if Follow up until date is ([^\"]*) days after last created API case report date",
        (Integer days) -> {
          webDriverHelpers.waitForElementPresent(FOLLOW_UP_UNTIL_DATE, 3);
          String date = webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE);
          softly.assertEquals(
              DateTimeFormatter.ofPattern("dd.MM.yyyy")
                  .format(
                      apiState
                          .getCreatedCase()
                          .getReportDate()
                          .toInstant()
                          .atZone(ZoneId.systemDefault())
                          .toLocalDate()
                          .plusDays(days)),
              date);
          softly.assertAll();
        });

    When(
        "I check if Follow up until date is ([^\"]*) days before last created API case report date",
        (Integer days) -> {
          TimeUnit.SECONDS.sleep(3);
          String date = webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE);
          softly.assertEquals(
              DateTimeFormatter.ofPattern("dd.MM.yyyy")
                  .format(
                      apiState
                          .getCreatedCase()
                          .getReportDate()
                          .toInstant()
                          .atZone(ZoneId.systemDefault())
                          .toLocalDate()
                          .minusDays(days)),
              date);
          softly.assertAll();
        });

    And(
        "^I select \"([^\"]*)\" from the infection settings on Edit Case page$",
        (String infectionOption) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(INFECTION_SETTINGS_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(INFECTION_SETTINGS_INPUT, infectionOption);
        });
  }

  private Vaccination collectVaccinationData() {
    return Vaccination.builder()
        .vaccinationDate(getVaccinationDate())
        .vaccineName(webDriverHelpers.getTextFromWebElement(VACCINATION_CARD_VACCINATION_NAME))
        .build();
  }

  private LocalDate getVaccinationDate() {
    String dateOfReport = webDriverHelpers.getTextFromWebElement(VACCINATION_CARD_VACCINATION_DATE);
    if (!dateOfReport.isEmpty()) {
      return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
    }
    return null;
  }

  private Case collectCasePersonDataWithFacilityAndDifferentPlaceOfStay() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeOfStayRegion(webDriverHelpers.getValueFromWebElement(PLACE_OF_STAY_REGION_INPUT))
        .placeOfStayDistrict(webDriverHelpers.getValueFromWebElement(PLACE_OF_STAY_DISTRICT_INPUT))
        .facility(webDriverHelpers.getValueFromWebElement(FACILITY_HEALTH_INPUT))
        .build();
  }

  private Case collectCasePersonDataWithPointOfEntryAndDifferentPlaceOfStay() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .placeOfStayRegion(webDriverHelpers.getValueFromWebElement(PLACE_OF_STAY_REGION_INPUT))
        .placeOfStayDistrict(webDriverHelpers.getValueFromWebElement(PLACE_OF_STAY_DISTRICT_INPUT))
        .pointOfEntry(webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_TEXT))
        .build();
  }

  private Case collectCasePersonDataWithDifferentPlaceOfStay() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .placeOfStayRegion(webDriverHelpers.getValueFromWebElement(PLACE_OF_STAY_REGION_INPUT))
        .placeOfStayDistrict(webDriverHelpers.getValueFromWebElement(PLACE_OF_STAY_DISTRICT_INPUT))
        .build();
  }

  private Case collectCasePersonUuid() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 40);
    return Case.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  private Case collectCasePersonData() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        // field that is no longer available
        // .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .build();
  }

  private Case collectCasePersonDataDE() {
    Case userInfo = getUserInformationDE();

    return Case.builder()
        .dateOfReport(getDateOfReportDE())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .diseaseVariant(webDriverHelpers.getValueFromWebElement(DISEASE_VARIANT_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .build();
  }

  private Case collectCaseData() {
    return Case.builder()
        .dateOfReport(getDateOfReport())
        .caseClassification(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                CASE_CLASSIFICATION_RADIOBUTTON))
        .clinicalConfirmation(webDriverHelpers.getValueFromCombobox(CLINICAL_CONFIRMATION_COMBOBOX))
        .epidemiologicalConfirmation(
            webDriverHelpers.getValueFromCombobox(EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX))
        .laboratoryDiagnosticConfirmation(
            webDriverHelpers.getValueFromCombobox(LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX))
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                INVESTIGATION_STATUS_OPTIONS))
        .externalToken(webDriverHelpers.getValueFromWebElement(EXTERNAL_TOKEN_INPUT))
        .disease(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .outcomeOfCase(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTCOME_OF_CASE_OPTIONS))
        .sequelae(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SEQUELAE_OPTIONS))
        .region(webDriverHelpers.getValueFromCombobox(REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        .quarantine(webDriverHelpers.getValueFromCombobox(QUARANTINE_COMBOBOX))
        .reportGpsLatitude(webDriverHelpers.getValueFromWebElement(REPORT_GPS_LATITUDE_INPUT))
        .reportGpsLongitude(webDriverHelpers.getValueFromWebElement(REPORT_GPS_LONGITUDE_INPUT))
        .reportGpsAccuracyInM(
            webDriverHelpers.getValueFromWebElement(REPORT_GPS_ACCURACY_IN_M_INPUT))
        .dateReceivedAtDistrictLevel(getDateReceivedAtDistrictLevel())
        .dateReceivedAtRegionLevel(getDateReceivedAtRegionLevel())
        .dateReceivedAtNationalLevel(getDateReceivedAtNationalLevel())
        .generalComment(webDriverHelpers.getValueFromWebElement(GENERAL_COMMENT_TEXTAREA))
        .vaccinationStatusForThisDisease(
            webDriverHelpers.getValueFromCombobox(VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX))
        .responsibleSurveillanceOfficer(
            webDriverHelpers.getValueFromCombobox(RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX))
        .build();
  }

  public Exposure collectExposureDataCase() {
    return Exposure.builder()
        .startOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE_INPUT), DATE_FORMATTER))
        .endOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE_INPUT), DATE_FORMATTER))
        .exposureDescription(webDriverHelpers.getValueFromWebElement(EXPOSURE_DESCRIPTION_INPUT))
        .typeOfActivity(
            TypeOfActivityExposure.fromString(
                webDriverHelpers.getValueFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX)))
        .exposureDetailsRole(
            ExposureDetailsRole.fromString(
                webDriverHelpers.getValueFromCombobox(EXPOSURE_DETAILS_ROLE_COMBOBOX)))
        .riskArea(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RISK_AREA_OPTIONS)))
        .indoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INDOORS_OPTIONS)))
        .outdoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTDOORS_OPTIONS)))
        .wearingMask(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_MASK_OPTIONS)))
        .wearingPpe(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_PPE_OPTIONS)))
        .otherProtectiveMeasures(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    OTHER_PROTECTIVE_MEASURES_OPTIONS)))
        .shortDistance(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHORT_DISTANCE_OPTIONS)))
        .longFaceToFaceContact(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    LONG_FACE_TO_FACE_CONTACT_OPTIONS)))
        .percutaneous(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERCUTANEOUS_OPTIONS)))
        .contactToBodyFluids(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    CONTACT_TO_BODY_FLUIDS_OPTONS)))
        .handlingSamples(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    HANDLING_SAMPLES_OPTIONS)))
        .typeOfPlace(
            TypeOfPlace.fromString(webDriverHelpers.getValueFromCombobox(TYPE_OF_PLACE_COMBOBOX)))
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .build();
  }

  private Case collectCasePersonDataForExistingPerson() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        // .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .build();
  }

  private Case collectSpecificData() {
    return Case.builder()
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                INVESTIGATION_STATUS_OPTIONS))
        .outcomeOfCase(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTCOME_OF_CASE_OPTIONS))
        .sequelae(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SEQUELAE_OPTIONS))
        .differentPlaceOfStayJurisdiction(
            webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(PLACE_OF_STAY_CHECKBOX_INPUT))
        .placeOfStay(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PLACE_OF_STAY_OPTIONS))
        .facilityCategory(webDriverHelpers.getValueFromCombobox(FACILITY_CATEGORY_COMBOBOX))
        .facilityType(webDriverHelpers.getValueFromCombobox(FACILITY_TYPE_COMBOBOX))
        .facility(webDriverHelpers.getValueFromCombobox(FACILITY_HEALTH_COMBOBOX))
        .facilityNameAndDescription(
            webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .quarantine(webDriverHelpers.getValueFromCombobox(QUARANTINE_COMBOBOX))
        .vaccinationStatus(
            webDriverHelpers.getValueFromCombobox(VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX))
        .region(webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX))
        .build();
  }

  private Case collectCasePersonDataWithFacility() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .facility(webDriverHelpers.getValueFromWebElement(FACILITY_HEALTH_INPUT))
        .build();
  }

  private LocalDate getDateOfReport() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getDateOfReportDE() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
  }

  private void fillDateOfDeath(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_DEATH_INPUT, formatter.format(date));
  }

  private LocalDate getDateReceivedAtDistrictLevel() {
    String dateOfReport =
        webDriverHelpers.getValueFromWebElement(DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getDateReceivedAtRegionLevel() {
    String dateOfReport =
        webDriverHelpers.getValueFromWebElement(DATE_RECEIVED_AT_REGION_LEVEL_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getDateReceivedAtNationalLevel() {
    String dateOfReport =
        webDriverHelpers.getValueFromWebElement(DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private Case getUserInformation() {
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), DATE_FORMATTER);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  private Case getUserInformationDE() {
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), DATE_FORMATTER_DE);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  private LocalDate getDateOfFollowUpStatusChangeDE() {
    String dateOfFollowUpStatusChange =
        webDriverHelpers.getValueFromWebElement(DATE_OF_FOLLOW_UP_STATUS_CHANGE_INPUT);
    return LocalDate.parse(dateOfFollowUpStatusChange, DATE_FORMATTER_DE);
  }

  private LocalDate getExpectedFollowUpUntilDateDE() {
    String expectedFollowUpUntilDate =
        webDriverHelpers.getValueFromWebElement(EXPECTED_FOLLOW_UP_UNTIL_DATE_INPUT);
    return LocalDate.parse(expectedFollowUpUntilDate, DATE_FORMATTER_DE);
  }

  private LocalDate getFollowUpUntilDateDE() {
    String followUpUntilDate = webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE_INPUT);
    return LocalDate.parse(followUpUntilDate, DATE_FORMATTER_DE);
  }

  private void fillDateOfReport(LocalDate date) {
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillPreviousInfectionDate(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(PREVIOUS_INFECTION_DATE_INPUT, formatter.format(date));
  }

  private void fillDateOfReportDE(LocalDate date) {
    DateTimeFormatter formatter;
    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, formatter.format(date));
  }

  private void fillFollowUpUntilDateDE(LocalDate newDate) {
    webDriverHelpers.fillInWebElement(
        FOLLOW_UP_UNTIL_DATE_INPUT, DATE_FORMATTER_DE.format(newDate));
  }

  private void selectCaseClassification(String caseClassification) {
    webDriverHelpers.clickWebElementByText(CASE_CLASSIFICATION_RADIOBUTTON, caseClassification);
  }

  private void selectClinicalConfirmation(String clinicalConfirmation) {
    webDriverHelpers.selectFromCombobox(CLINICAL_CONFIRMATION_COMBOBOX, clinicalConfirmation);
  }

  private void selectEpidemiologicalConfirmation(String epidemiologicalConfirmation) {
    webDriverHelpers.selectFromCombobox(
        EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX, epidemiologicalConfirmation);
  }

  private void selectLaboratoryDiagnosticConfirmation(String laboratoryDiagnosticConfirmation) {
    webDriverHelpers.selectFromCombobox(
        LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX, laboratoryDiagnosticConfirmation);
  }

  private void selectInvestigationStatus(String investigationStatus) {
    webDriverHelpers.clickWebElementByText(INVESTIGATION_STATUS_OPTIONS, investigationStatus);
  }

  private void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  private void fillExternalToken(String externalToken) {
    webDriverHelpers.fillInWebElement(EXTERNAL_TOKEN_INPUT, externalToken);
  }

  private void fillFollowUpStatusComment(String comment) {
    webDriverHelpers.fillInWebElement(FOLLOW_UP_COMMENT_FIELD, comment);
  }

  private void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void selectReinfection(String reinfection) {
    webDriverHelpers.clickWebElementByText(REINFECTION_OPTIONS, reinfection);
  }

  private void selectOutcomeOfCase(String outcomeOfCase) {
    webDriverHelpers.clickWebElementByText(OUTCOME_OF_CASE_OPTIONS, outcomeOfCase);
  }

  private void selectCaseIdentificationSource(String caseIdentificationSource) {
    webDriverHelpers.selectFromCombobox(
        CASE_IDENTIFICATION_SOURCE_COMBOBOX, caseIdentificationSource);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  private void selectCommunity(String community) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  private void fillPlaceDescription(String placeDescription) {
    webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, placeDescription);
  }

  private void selectResponsibleRegion(String responsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, responsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void selectSequelae(String sequelae) {
    webDriverHelpers.clickWebElementByText(SEQUELAE_OPTIONS, sequelae);
  }

  private void selectProhibitionToWork(String prohibitionToWork) {
    webDriverHelpers.clickWebElementByText(PROHIBITION_TO_WORK_OPTIONS, prohibitionToWork);
  }

  private void selectHomeBasedQuarantinePossible(String homeBasedQuarantinePossible) {
    webDriverHelpers.clickWebElementByText(
        HOME_BASED_QUARANTINE_POSSIBLE_OPTIONS, homeBasedQuarantinePossible);
  }

  private void selectQuarantine(String quarantine) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, quarantine);
  }

  private void fillReportGpsLatitude(String reportGpsLatitude) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_LATITUDE_INPUT, reportGpsLatitude);
  }

  private void fillReportGpsLongitude(String reportGpsLongitude) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_LONGITUDE_INPUT, reportGpsLongitude);
  }

  private void fillReportGpsAccuracyInM(String reportGpsAccuracyInM) {
    webDriverHelpers.fillInWebElement(REPORT_GPS_ACCURACY_IN_M_INPUT, reportGpsAccuracyInM);
  }

  private void selectBloodOrganTissueDonationInTheLast6Months(
      String bloodOrganTissueDonationInTheLast6Months) {
    webDriverHelpers.clickWebElementByText(
        BLOOD_ORGAN_TISSUE_DONATION_IN_THE_LAST_6_MONTHS_OPTIONS,
        bloodOrganTissueDonationInTheLast6Months);
  }

  private void selectVaccinationStatusForThisDisease(String vaccinationStatusForThisDisease) {
    webDriverHelpers.selectFromCombobox(
        VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX, vaccinationStatusForThisDisease);
  }

  private void selectResponsibleSurveillanceOfficer(String responsibleSurveillanceOfficer) {
    webDriverHelpers.selectFromCombobox(
        RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX, responsibleSurveillanceOfficer);
  }

  private void fillDateReceivedAtDistrictLevel(LocalDate dateReceivedAtDistrictLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtDistrictLevel));
  }

  private void fillDateReceivedAtRegionLevel(LocalDate dateReceivedAtRegionLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_REGION_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtRegionLevel));
  }

  private void fillDateReceivedAtNationalLevel(LocalDate dateReceivedAtNationalLevel) {
    webDriverHelpers.fillInWebElement(
        DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT, DATE_FORMATTER.format(dateReceivedAtNationalLevel));
  }

  private void fillGeneralComment(String generalComment) {
    webDriverHelpers.fillInWebElement(GENERAL_COMMENT_TEXTAREA, generalComment);
  }

  private void selectQuarantineOrderTemplate(String templateName) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, templateName);
  }

  private void fillExtraComment(String extraComment) {
    webDriverHelpers.fillInAndLeaveWebElement(EditCasePage.EXTRA_COMMENT_TEXTAREA, extraComment);
  }

  public static By checkTextInReportingToolComponent(String text) {
    return By.xpath(
        String.format(
            "//div[contains(@location,'externalSurvToolGateway')]//div[contains(text(), '%s')]",
            text));
  }
}
