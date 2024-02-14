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

package org.sormas.e2etests.steps.web.application.events;

import static org.sormas.e2etests.pages.application.actions.CreateNewActionPage.NEW_ACTION_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ALL_RESULTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONFIRM_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ACTION_CONFIRM_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LINE_LISTING_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_SEARCH_LOCATOR_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CLOSE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BUTTONS_IN_VACCINATIONS_LOCATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_FIRST_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERATED_DOCUMENT_NAME_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SELECT_MATCHING_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.ACTION_CANCEL_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.ARCHIVE_POPUP_WINDOW_HEADER;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.END_OF_PROCESSING_DATE_POPUP_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.REPORT_DATE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_IMPORT_TRAVEL_ENTRY_POPUP;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.IMPORT_SUCCESS_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PICK_OR_CREATE_PERSON_HEADER_DE;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.EVENT_IDENTIFICATION_SOURCE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.START_DATA_TIME;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CASE_CONTROL_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.COHORT_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.COMPLIANT_PATHOGEN_FINE_TYPING_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CONFIRM_EVENT_DELETE_ACTION_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CONTACT_TO_CONTAMINATED_MATERIALS_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CONTACT_TO_SICK_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.COUNTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.COUNTRY_COMBOBOX_DIABLED;
import static org.sormas.e2etests.pages.application.events.EditEventPage.COUNTRY_COMBOBOX_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.COUNTRY_INFO_ICON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.COUNTRY_INFO_POPUP_TEXT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CREATE_CASE_IN_EVENT_PARTICIPANT_LIST_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CREATE_CONTACTS_BULK_EDIT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CREATE_QUARANTINE_ORDER_EVENT_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.CREATE_QUARANTINE_ORDER_EVENT_PARTICIPANT_BY_TEXT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.DEFAULT_COMBOBOX_VALUE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.DELETE_BULK_EDIT_BUTTON_EVENT_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.DELETE_EVENT_ACTION_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.DESCRIPTIVE_ANALYSIS_OF_ASCETAINED_DATA_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EDIT_EVENT_ACTION;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EDIT_EVENT_GROUP_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EDIT_EVENT_PAGE_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EDIT_FIRST_TASK;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EPIDEMIOLOGICAL_EVIDENCE_OPTIONS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENTS_ACTIONS_TAB;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_ACTIONS_TAB;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_CLUSTER_EDIT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_DATA_SAVED_MESSAGE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_DATE_OF_REPORT_EXCLAMATION_MARK;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_DATE_OF_REPORT_EXCLAMATION_MARK_MESSAGE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_ERROR_POPUP_FIRST_MESSAGE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_ERROR_POPUP_MESSAGE_WITH_INPUT_DATA_TITLE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_ERROR_POPUP_SECOND_MESSAGE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_HANDOUT_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_INVESTIGATION_STATUS_OPTIONS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_MANAGEMENT_STATUS_OPTIONS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_PARTICIPANT_HEADER;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_PARTICIPANT_STATUS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_STATUS_OPTIONS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EXPLORATIVE_SURVEY_OF_AFFECTED_PEOPLE_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EXPRESSED_BY_THE_DISEASE_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EXPRESSED_BY_THE_HEALTH_DEPARTMENT_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EYE_ICON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.FIRST_GROUP_ID;
import static org.sormas.e2etests.pages.application.events.EditEventPage.GPS_LATITUDE_INPUT_EDIT_EVENT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.GPS_LONGITUDE_INPUT_EDIT_EVENT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.GROUP_EVENT_NAME_POPUP_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.GROUP_EVENT_UUID;
import static org.sormas.e2etests.pages.application.events.EditEventPage.IMPRESSION_TEST_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.LABORATORY_DIAGNOSTIC_EVIDENCE_OPTIONS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.LINE_LISTING_HEADER;
import static org.sormas.e2etests.pages.application.events.EditEventPage.LINK_EVENT_GROUP_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.MAP_CONTAINER;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NAVIGATE_TO_EVENT_DATA_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NAVIGATE_TO_EVENT_DIRECTORY_EVENT_GROUP_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NAVIGATE_TO_EVENT_DIRECTORY_LIST_GROUP_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NAVIGATE_TO_EVENT_GROUP_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NAVIGATE_TO_EVENT_PARTICIPANTS_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_ACTION_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_EVENT_GROUP_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_GROUP_EVENT_CREATED_MESSAGE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_IMMUNIZATION_CARD_WITH_COVID;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NO_EVENT_ACTION_CAPTION;
import static org.sormas.e2etests.pages.application.events.EditEventPage.OTHER_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.PATHOGEN_FINE_TYPING_COMPLIANT_WITH_THE_ONE_OF_CASES_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.PLACE_OF_STAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.PRIMARY_MODE_OF_TRANSMISSION_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.REASON_FOR_DELETION_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.REASON_FOR_DELETION_MARK;
import static org.sormas.e2etests.pages.application.events.EditEventPage.REASON_FOR_DELETION_MESSAGE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.RISK_LEVEL_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.RISK_LEVEL_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON_FOR_EDIT_EVENT_GROUP;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON_FOR_POPUP_WINDOWS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SELECT_EVENT_GROUP_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SOURCE_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SOURCE_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SPATIAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.START_DATA_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SUSPICION_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.TEMPORAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.TITLE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.TOTAL_ACTIONS_COUNTER;
import static org.sormas.e2etests.pages.application.events.EditEventPage.TYPE_OF_PLACE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.TYPE_OF_PLACE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.UNDO_DELETION_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.UNLINK_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.VACCINATION_STATUS_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.VERIFICATION_OF_AT_LEAST_TWO_INFECTED_OR_DISEASED_PERSONS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.VERIFICATION_ON_MATERIALS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.WATER_SAMPLE_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.events.EditEventPage.getEventParticipantUUIDbyIndex;
import static org.sormas.e2etests.pages.application.events.EditEventPage.getGroupEventName;
import static org.sormas.e2etests.pages.application.events.EventActionsPage.CREATE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.BULK_ACTIONS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_GROUP_ID_NAME_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.SEARCH_EVENT_BY_FREE_TEXT_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.TOTAL_EVENTS_COUNTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getCheckboxByIndex;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.ADD_PARTICIPANT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.ARCHIVE_EVENT_PARTICIPANT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.CONFIRM_ACTION;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.CONFIRM_BUTTON_FOR_SELECT_PERSON_FROM_ADD_PARTICIPANTS_WINDOW;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.CONFIRM_DEARCHIVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.CONFIRM_NAVIGATION_POPUP;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.CREATE_NEW_PERSON_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.DEARCHIVE_REASON_TEXT_AREA;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.DELETE_EVENT_PARTICIPANT_BUTTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.ERROR_MESSAGE_TEXT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANTS;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANTS_GRID;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANTS_TAB;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANT_DATA_TAB;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANT_DISPLAY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANT_PERSON_TAB;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANT_UUID;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANT_UUID_INPUT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_TAB;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.GENERAL_COMMENT_TEXT_AREA;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.INVOLVEMENT_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.PARTICIPANT_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.PARTICIPANT_FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.PARTICIPANT_LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.PARTICIPANT_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.PASSPORT_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.PICK_OR_CREATE_PERSON_POPUP;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.PICK_OR_CREATE_POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.POPUP_CANCEL_ACTION_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.SEARCH_FOR_PERSON_BUTTON_IN_ADD_PARTICIPANT_POPUP_WINDOW;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.SELECT_FIRST_PERSON_IN_SEARCHED_LIST_FROM_ADD_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.SELECT_PERSON_ID_INPUT_AT_ADD_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.SELECT_PERSON_SEARCH_BUTTON_AT_ADD_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.SEX_COMBOBOX_REQUIRED;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.UNSAVED_CHANGES_HEADER;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.UNSAVED_CHANGES_HEADER_DE;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.getEventParticipantByPersonUuid;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.getVaccinationByIndex;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DATE_OF_BIRTH_DAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DATE_OF_BIRTH_MONTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DATE_OF_BIRTH_YEAR_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PERSON_DATA_SAVED;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.POPUP_PERSON_ID;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.POPUP_RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.POPUP_RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.POPUP_SAVE;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.SEE_EVENTS_FOR_PERSON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_REASON_POPUP;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.github.javafaker.Faker;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Event;
import org.sormas.e2etests.entities.pojo.web.EventGroup;
import org.sormas.e2etests.entities.pojo.web.EventHandout;
import org.sormas.e2etests.entities.pojo.web.EventParticipant;
import org.sormas.e2etests.entities.pojo.web.Person;
import org.sormas.e2etests.entities.services.EventDocumentService;
import org.sormas.e2etests.entities.services.EventGroupService;
import org.sormas.e2etests.entities.services.EventParticipantService;
import org.sormas.e2etests.entities.services.EventService;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.pages.application.events.EditEventPage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.contacts.CreateNewContactSteps;
import org.sormas.e2etests.steps.web.application.contacts.EditContactSteps;
import org.sormas.e2etests.steps.web.application.persons.PersonDirectorySteps;
import org.sormas.e2etests.steps.web.application.vaccination.CreateNewVaccinationSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class EditEventSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static EventParticipant participant;
  public static Event collectedEvent;
  public static Event createdEvent;
  public static EventGroup groupEvent;
  public static Person person;
  public static String eventParticpantId;
  public static EventHandout aEventHandout;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  public static final String userDirPath = System.getProperty("user.dir");
  private static String currentUrl;
  private static String eventParticipantUUID;
  private static String eventUUID;
  public static List<String> externalEventUUID = new ArrayList<>();
  public static List<String> eventParticipantsUUIDList = new ArrayList<>();
  LocalDate dateOfBirth;
  List<Person> eventParticipantList = new ArrayList<>();

  @Inject
  public EditEventSteps(
      WebDriverHelpers webDriverHelpers,
      EventService eventService,
      EventDocumentService eventDocumentService,
      Faker faker,
      EventGroupService eventGroupService,
      SoftAssert softly,
      EventParticipantService eventParticipant,
      AssertHelpers assertHelpers,
      RunningConfiguration runningConfiguration,
      ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;
    Random r = new Random();
    char c = (char) (r.nextInt(26) + 'a');
    String firstName = faker.name().firstName() + c;
    String lastName = faker.name().lastName() + c;
    String sex = GenderValues.getRandomGenderDE();

    When(
        "^I change the event status to ([^\"]*)",
        (String eventStatus) -> {
          selectEventStatus(eventStatus);
          webDriverHelpers.scrollToElement(EDIT_EVENT_PAGE_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_PAGE_SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
        });

    When(
        "I check CLUSTER option on edit Event page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_CLUSTER_EDIT);
        });

    When(
        "I select {string} option from Primary Mode Of Transmission Combobox on edit Event page",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(PRIMARY_MODE_OF_TRANSMISSION_COMBOBOX, option);
        });

    When(
        "I click on Epidemiological evidence with ([^\"]*) option",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(EPIDEMIOLOGICAL_EVIDENCE_OPTIONS, option);
        });

    When(
        "I tick the all options for Study on Epidemiological evidence for De version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_CONTROL_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              COHORT_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I check that all options for Study on Epidemiological evidence appears and there are checked for De version",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CASE_CONTROL_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              COHORT_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I tick the all options for Explorative survey of affected people on Epidemiological evidence for De version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              EXPLORATIVE_SURVEY_OF_AFFECTED_PEOPLE_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              CONTACT_TO_SICK_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              CONTACT_TO_CONTAMINATED_MATERIALS_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I check the all options for Explorative survey of affected people on Epidemiological evidence appears and there are checked for De version",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EXPLORATIVE_SURVEY_OF_AFFECTED_PEOPLE_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONTACT_TO_SICK_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONTACT_TO_CONTAMINATED_MATERIALS_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I tick the all options for Descriptive analysis of ascertained data on Epidemiological evidence for De version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              DESCRIPTIVE_ANALYSIS_OF_ASCETAINED_DATA_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(TEMPORAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(SPATIAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I check the all options for Descriptive analysis of ascertained data on Epidemiological evidence appears and there are checked for De version",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              DESCRIPTIVE_ANALYSIS_OF_ASCETAINED_DATA_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              TEMPORAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SPATIAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I tick the all options for Suspicion on Epidemiological evidence for De version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              SUSPICION_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              EXPRESSED_BY_THE_DISEASE_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              EXPRESSED_BY_THE_HEALTH_DEPARTMENT_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I check the all options for Suspicion on Epidemiological evidence are visible and clickable for De version",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SUSPICION_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EXPRESSED_BY_THE_DISEASE_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EXPRESSED_BY_THE_HEALTH_DEPARTMENT_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE);
        });

    When(
        "I click on Laboratory diagnostic evidence with ([^\"]*) option",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(LABORATORY_DIAGNOSTIC_EVIDENCE_OPTIONS, option);
        });

    When(
        "I tick the all options for Verification of at least two infected or diseased persons on Laboratory diagnostic evidence for De version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              VERIFICATION_OF_AT_LEAST_TWO_INFECTED_OR_DISEASED_PERSONS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              COMPLIANT_PATHOGEN_FINE_TYPING_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
        });

    When(
        "I check the all options for Verification of at least two infected or diseased persons on Laboratory diagnostic evidence appears and there are checked for De version",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              VERIFICATION_OF_AT_LEAST_TWO_INFECTED_OR_DISEASED_PERSONS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              COMPLIANT_PATHOGEN_FINE_TYPING_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
        });

    When(
        "I tick the all options for Verification on materials on Laboratory diagnostic evidence for De version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              VERIFICATION_ON_MATERIALS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              IMPRESSION_TEST_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              WATER_SAMPLE_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              OTHER_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              PATHOGEN_FINE_TYPING_COMPLIANT_WITH_THE_ONE_OF_CASES_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
        });

    When(
        "I check the all options for Verification on materials on Laboratory diagnostic evidence appears and there are checked for De version",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              VERIFICATION_ON_MATERIALS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              IMPRESSION_TEST_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              WATER_SAMPLE_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              OTHER_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PATHOGEN_FINE_TYPING_COMPLIANT_WITH_THE_ONE_OF_CASES_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE);
        });
    When(
        "^I select first (\\d+) results in grid in Event Participant Directory$",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          for (int i = 3; i <= number + 2; i++) {
            webDriverHelpers.scrollToElement(getCheckboxByIndex(String.valueOf(i)));
            webDriverHelpers.clickOnWebElementBySelector(getCheckboxByIndex(String.valueOf(i)));
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I select {string} Quarantine Order in Create Quarantine Order form in Event Participant directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for {int} result in Event Participant directory for DE",
        (String name, Integer index) -> {
          String uuid =
              webDriverHelpers.getTextFromWebElement(getEventParticipantUUIDbyIndex(index));
          webDriverHelpers.scrollToElement(getEventParticipantUUIDbyIndex(index));
          webDriverHelpers.clickOnWebElementBySelector(getEventParticipantUUIDbyIndex(index));
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME_DE)),
              120);
        });
    // TODO refactor this
    When(
        "I collect the UUID displayed on Edit event page",
        () -> collectedEvent = collectEventUuid());

    When(
        "I collect the UUID displayed on Edit event participant page",
        () -> eventParticipantUUID = webDriverHelpers.getValueFromWebElement(UUID_INPUT));

    When(
        "I check the created data for DE version is correctly displayed in event edit page",
        () -> {
          collectedEvent = collectEventDataDE();
          createdEvent = CreateNewEventSteps.newEvent;

          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedEvent,
              createdEvent,
              List.of(
                  "uuid",
                  "reportDate",
                  "eventDate",
                  "eventStatus",
                  "investigationStatus",
                  "eventManagementStatus",
                  "riskLevel",
                  "disease",
                  "title",
                  "sourceType",
                  "eventLocation"));
        });

    When(
        "I check the created data is correctly displayed in event edit page",
        () -> {
          collectedEvent = collectEventData();
          createdEvent = CreateNewEventSteps.newEvent;

          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedEvent,
              createdEvent,
              List.of(
                  "uuid",
                  "reportDate",
                  "eventDate",
                  "eventStatus",
                  "investigationStatus",
                  "eventManagementStatus",
                  "riskLevel",
                  "disease",
                  "title",
                  "sourceType",
                  "eventLocation"));
        });

    When(
        "I check the only mandatory created data is correctly displayed in event edit page",
        () -> {
          collectedEvent = collectEventMandatoryData();
          createdEvent = CreateNewEventSteps.newEvent;

          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedEvent, createdEvent, List.of("uuid", "reportDate", "title"));
        });

    When(
        "I change the report event date for minus {int} day from today",
        (Integer day) -> {
          webDriverHelpers.scrollToElement(REPORT_DATE_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              REPORT_DATE_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(day)));
          webDriverHelpers.selectFromCombobox(START_DATA_TIME, "01:15");
          TimeUnit.SECONDS.sleep(1); // wait for reaction
        });

    When(
        "I check if date of report has an error exclamation mark with correct error message",
        () -> {
          String expectedText = "Date of report has to be after or on the same day as Start date";
          webDriverHelpers.hoverToElement(EVENT_DATE_OF_REPORT_EXCLAMATION_MARK);
          TimeUnit.SECONDS.sleep(1);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(EVENT_DATE_OF_REPORT_EXCLAMATION_MARK_MESSAGE);
          softly.assertEquals(expectedText, displayedText, "Error messages are not equal");
          softly.assertAll();
        });

    When(
        "^I check if error popup is displayed with message ([^\"]*)$",
        (String message) -> {
          String displayedStr =
              webDriverHelpers.getTextFromWebElement(
                  EVENT_ERROR_POPUP_MESSAGE_WITH_INPUT_DATA_TITLE);
          softly.assertEquals(message, displayedStr, "Error messages are not equal");
          softly.assertAll();
        });

    When(
        "^I check if error popup contains Date of report has to be after or on the same day as Start date$",
        () -> {
          String expectedStr = "Date of report has to be after or on the same day as Start date";
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EVENT_ERROR_POPUP_FIRST_MESSAGE);
          String displayedStr =
              webDriverHelpers.getTextFromWebElement(EVENT_ERROR_POPUP_FIRST_MESSAGE);
          softly.assertEquals(expectedStr, displayedStr, "Error messages are not equal");
          softly.assertAll();
        });

    When(
        "^I check if error popup contains Start date has to be before or on the same day as Date of report$",
        () -> {
          String expectedStr = "Start date has to be before or on the same day as Date of report";
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EVENT_ERROR_POPUP_SECOND_MESSAGE);
          String displayedStr =
              webDriverHelpers.getTextFromWebElement(EVENT_ERROR_POPUP_SECOND_MESSAGE);
          softly.assertEquals(expectedStr, displayedStr, "Error messages are not equal");
          softly.assertAll();
        });

    When(
        "I check if date of report is set for {int} day ago from today",
        (Integer day) -> {
          String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
          LocalDate reportDate = LocalDate.parse(reportingDate, DATE_FORMATTER);
          softly.assertEquals(reportDate, LocalDate.now().minusDays(day));
          softly.assertAll();
        });

    When(
        "I set the date of event for today",
        () -> {
          webDriverHelpers.scrollToElement(START_DATA_INPUT);
          webDriverHelpers.fillInWebElement(
              START_DATA_INPUT, DATE_FORMATTER.format(LocalDate.now()));
        });

    When(
        "I change the fields of event and save",
        () -> {
          collectedEvent = eventService.buildEditEvent();
          fillDateOfReport(collectedEvent.getReportDate());
          fillStartData(collectedEvent.getEventDate());
          collectedEvent =
              collectedEvent.toBuilder()
                  .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
                  .build();
          selectEventStatus(collectedEvent.getEventStatus());
          selectEventInvestigationStatusOptions(collectedEvent.getInvestigationStatus());
          selectEventManagementStatusOption(collectedEvent.getEventManagementStatus());
          selectRiskLevel(collectedEvent.getRiskLevel());
          selectDisease(collectedEvent.getDisease());
          fillTitle(collectedEvent.getTitle());
          selectSourceType(collectedEvent.getSourceType());
          selectTypeOfPlace(collectedEvent.getEventLocation());
          webDriverHelpers.scrollToElement(EDIT_EVENT_PAGE_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_PAGE_SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
        });

    When(
        "I check the modified event data is correctly displayed",
        () -> {
          final Event currentEvent = collectEventData();
          ComparisonHelper.compareEqualEntities(collectedEvent, currentEvent);
        });

    When(
        "I add only required data for event participant creation",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, GenderValues.getRandomGender());
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_RADIO_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }
        });
    When(
        "I collect the event participant person UUID displayed on Edit Event Participant page",
        () -> person = collectPersonUuid());
    When(
        "I add only required data for event participant creation for DE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, GenderValues.getRandomGenderDE());
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_RADIO_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }
        });
    And(
        "I click on Create Quarantine Order from Bulk Actions combobox on Event Participant Directory Page",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(
                CREATE_QUARANTINE_ORDER_EVENT_PARTICIPANT));
    And(
        "I click on Create Quarantine Order from Bulk Actions combobox on Event Participant Directory Page by button text",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              CREATE_QUARANTINE_ORDER_EVENT_PARTICIPANT_BY_TEXT);
        });
    And(
        "I click on checkbox to upload generated document to entities in Create Quarantine Order form in Event Participant directory for DE",
        () -> webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX));
    When(
        "I add only person data for event participant creation for DE with built person shared for all entities",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(
              PARTICIPANT_FIRST_NAME_INPUT,
              PersonDirectorySteps.personSharedForAllEntities.getFirstName());
          webDriverHelpers.fillInWebElement(
              PARTICIPANT_LAST_NAME_INPUT,
              PersonDirectorySteps.personSharedForAllEntities.getLastName());
          webDriverHelpers.selectFromCombobox(
              SEX_COMBOBOX,
              GenderValues.getValueForDE(PersonDirectorySteps.personSharedForAllEntities.getSex()));
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }
        });
    When(
        "I add same person data as one used for Contact creation for event participant",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(
              PARTICIPANT_FIRST_NAME_INPUT, EditContactSteps.collectedContact.getFirstName());
          webDriverHelpers.fillInWebElement(
              PARTICIPANT_LAST_NAME_INPUT, EditContactSteps.collectedContact.getLastName());
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, CreateNewContactSteps.contact.getSex());
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(SELECT_MATCHING_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }
        });
    When(
        "I click on ADD PARTICIPANT button",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
        });
    When(
        "I click on Add Participant button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
        });
    When(
        "I add Participant to an Event with same person data",
        () -> {
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, firstName);
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, lastName);
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_REGION_COMBOBOX, RegionsValues.VoreingestellteBundeslander.getName());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_DISTRICT_COMBOBOX, DistrictsValues.VoreingestellterLandkreis.getName());
        });

    When(
        "I add Participant to an Event with the new person data",
        () -> {
          String fName = faker.name().firstName();
          String lName = faker.name().lastName();
          String sexEn = GenderValues.getRandomGender();
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, fName);
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, lName);
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sexEn);
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_REGION_COMBOBOX, RegionsValues.VoreingestellteBundeslander.getName());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_DISTRICT_COMBOBOX, DistrictsValues.VoreingestellterLandkreis.getName());
        });

    When(
        "I click on save button in Add Participant form",
        () -> {
          webDriverHelpers.waitForElementPresent(EDIT_EVENT_PAGE_SAVE_BUTTON, 3);
          int attempts = 0;
          do {
            webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_PAGE_SAVE_BUTTON);
            attempts++;
          } while (attempts < 5
              && webDriverHelpers.isElementPresent(By.cssSelector(".v-window-wrap")));
        });
    When(
        "I add a participant to the event",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, GenderValues.getRandomGender());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_REGION_COMBOBOX, RegionsValues.VoreingestellteBundeslander.getName());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_DISTRICT_COMBOBOX, DistrictsValues.VoreingestellterLandkreis.getName());
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_RADIO_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE);

          person = collectPersonUuid();
          eventParticipantList.add(person);
          selectResponsibleRegion("Region1");
          selectResponsibleDistrict("District11");
          dateOfBirth =
              LocalDate.of(
                  faker.number().numberBetween(1900, 2002),
                  faker.number().numberBetween(1, 12),
                  faker.number().numberBetween(1, 27));

          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANT_PERSON_TAB);

          if (webDriverHelpers.isElementVisibleWithTimeout(UNSAVED_CHANGES_HEADER, 10))
            webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);

          webDriverHelpers.selectFromCombobox(
              DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(dateOfBirth.getYear()));
          webDriverHelpers.selectFromCombobox(
              DATE_OF_BIRTH_MONTH_COMBOBOX,
              dateOfBirth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
          webDriverHelpers.selectFromCombobox(
              DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(dateOfBirth.getDayOfMonth()));
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PERSON_DATA_SAVED);
        });

    When(
        "I click on Event Participant Person tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANT_PERSON_TAB);
          if (webDriverHelpers.isElementVisibleWithTimeout(UNSAVED_CHANGES_HEADER, 2))
            webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
          if (webDriverHelpers.isElementVisibleWithTimeout(UNSAVED_CHANGES_HEADER_DE, 2))
            webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
        });

    When(
        "I click on Event Participant Data tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANT_DATA_TAB);
        });

    When(
        "I click on Event Participants tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS);
        });

    When(
        "I add a participant to the event in DE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, GenderValues.getRandomGenderDE());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_REGION_COMBOBOX, RegionsValues.VoreingestellteBundeslander.getName());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_DISTRICT_COMBOBOX, DistrictsValues.VoreingestellterLandkreis.getName());
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_RADIO_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }

          person = collectPersonUuid();
        });

    Then(
        "I set participant vaccination status to ([^\"]*)",
        (String vaccinationStatus) -> {
          webDriverHelpers.selectFromCombobox(VACCINATION_STATUS_COMBOBOX, vaccinationStatus);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_PAGE_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
        });

    When(
        "I check that the value selected from Disease combobox is {string} on Edit Event page",
        (String disease) -> {
          String chosenDisease =
              webDriverHelpers.getValueFromCombobox(EditEventPage.DISEASE_COMBOBOX);
          softly.assertEquals(chosenDisease, disease, "The disease is other then expected");
          softly.assertAll();
        });

    When(
        "^I click on the person search button in add new event participant form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PERSON_SEARCH_LOCATOR_BUTTON);
        });

    When(
        "I fill birth fields for participant in event participant list",
        () -> {
          webDriverHelpers.selectFromCombobox(
              DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(dateOfBirth.getYear()));
          String month = String.valueOf(dateOfBirth.getMonth());
          webDriverHelpers.selectFromCombobox(
              DATE_OF_BIRTH_MONTH_COMBOBOX,
              month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase());
          webDriverHelpers.selectFromCombobox(
              DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(dateOfBirth.getDayOfMonth()));
        });

    When(
        "I click on Apply filters button in event participant list",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I add empty participant data",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
        });

    When(
        "I add participant first name only",
        () -> {
          participant = eventParticipant.buildGeneratedEventParticipant();
          fillFirstName(participant.getFirstName());
        });

    When(
        "I add participant responsible region and responsible district only",
        () -> {
          participant = eventParticipant.buildGeneratedEventParticipant();
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          selectResponsibleRegion(participant.getResponsibleRegion());
          selectResponsibleDistrict(participant.getResponsibleDistrict());
        });
    When(
        "I edit participants responsible region and responsible district",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          selectResponsibleRegion(RegionsValues.VoreingestellteBundeslander.getName());
          selectResponsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName());
        });

    When(
        "I add participant first and last name only",
        () -> {
          participant = eventParticipant.buildGeneratedEventParticipant();
          fillFirstName(participant.getFirstName());
          fillLastName(participant.getLastName());
        });

    When(
        "I check if error display correctly expecting first name error",
        () -> {
          webDriverHelpers.checkWebElementContainsText(ERROR_MESSAGE_TEXT, "First name");
          webDriverHelpers.clickOnWebElementBySelector(ERROR_MESSAGE_TEXT);
        });

    When(
        "I check if error display correctly expecting last name error",
        () -> {
          webDriverHelpers.checkWebElementContainsText(ERROR_MESSAGE_TEXT, "Last name");
          webDriverHelpers.clickOnWebElementBySelector(ERROR_MESSAGE_TEXT);
        });

    When(
        "I check if error display correctly expecting sex error",
        () -> {
          webDriverHelpers.checkWebElementContainsText(ERROR_MESSAGE_TEXT, "Sex");
          webDriverHelpers.clickOnWebElementBySelector(ERROR_MESSAGE_TEXT);
        });

    When(
        "I save changes in participant window",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
        });

    When(
        "I click on SAVE button in edit event form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
        });

    When(
        "I confirm navigation popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_NAVIGATION_POPUP);
        });

    When(
        "I click on Create Contacts button from bulk actions menu in Event Participant Tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_CONTACTS_BULK_EDIT_BUTTON);
        });

    When(
        "I click on Delete button from bulk actions menu in Event Participant Tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BULK_EDIT_BUTTON_EVENT_PARTICIPANT);
        });

    When(
        "I click on Create quarantine order documents from bulk actions menu in Event Participant Tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_QUARANTINE_ORDER_EVENT_PARTICIPANT);
        });

    When(
        "I click checkbox to choose all Event Participants results in Event Participant Tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_RESULTS_CHECKBOX);
        });
    And(
        "I click on Bulk Actions combobox in Event Parcitipant Tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_EVENT_DIRECTORY));
    When(
        "I discard changes in participant window",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON);
        });

    When(
        "^I click on edit task icon of the first created task$",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_FIRST_TASK));

    When(
        "^I click on link event group$",
        () -> {
          webDriverHelpers.scrollToElement(LINK_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_GROUP_BUTTON);
        });

    When(
        "I choose select event group Radiobutton",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SELECT_EVENT_GROUP_RADIOBUTTON);
        });

    When(
        "I select the first row from table and I click on save button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_GROUP_ID);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
          webDriverHelpers.clickOnWebElementBySelector(EVENT_DATA_SAVED_MESSAGE);
        });

    When(
        "I unlinked the first chosen group by click on Unlink event group button",
        () -> {
          webDriverHelpers.scrollToElement(UNLINK_EVENT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(UNLINK_EVENT_BUTTON);
          TimeUnit.SECONDS.sleep(3); // waiting for unlinked
        });

    When(
        "I click on Edit event group button from event groups box",
        () -> {
          webDriverHelpers.scrollToElement(EDIT_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_EDIT_EVENT_GROUP);
        });

    When(
        "I click on Edit event button for the first event in Events section",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_GROUP_BUTTON);
        });

    When(
        "I click on first Edit event button for in Events section",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_FIRST_TASK_BUTTON);
        });

    When(
        "I click on the Navigate to event directory filtered on this event group",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              NAVIGATE_TO_EVENT_DIRECTORY_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              NAVIGATE_TO_EVENT_DIRECTORY_EVENT_GROUP_BUTTON);
        });

    When(
        "I navigate to EVENTS LIST from edit event page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              NAVIGATE_TO_EVENT_DIRECTORY_LIST_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              NAVIGATE_TO_EVENT_DIRECTORY_LIST_GROUP_BUTTON);
        });

    When(
        "I navigate to EVENT from edit event page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NAVIGATE_TO_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NAVIGATE_TO_EVENT_GROUP_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "I navigate to EVENT PARTICIPANT from edit event page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              NAVIGATE_TO_EVENT_PARTICIPANTS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NAVIGATE_TO_EVENT_PARTICIPANTS_BUTTON);
        });
    When(
        "I navigate to EVENT DATA from edit event page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NAVIGATE_TO_EVENT_DATA_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NAVIGATE_TO_EVENT_DATA_BUTTON);
        });
    When(
        "I check that number of added Vaccinations is {int} on Edit Event Participant Page",
        (Integer expected) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(BUTTONS_IN_VACCINATIONS_LOCATION),
                        (int) expected,
                        "Number of vaccinations is different than expected")));
    When(
        "I click to edit {int} vaccination on Edit Event Participant page",
        (Integer index) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getVaccinationByIndex(String.valueOf(index)));
          webDriverHelpers.clickOnWebElementBySelector(
              getVaccinationByIndex(String.valueOf(index)));
        });
    When(
        "I close vaccination form in Edit Event Participant directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_POPUP));
    When(
        "I close Import Event Participant form",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
        });
    When(
        "I click to create new person from the Event Participant Import popup if Pick or create popup appears",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(COMMIT_BUTTON, 10)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(COMMIT_BUTTON);
          }
        });
    When(
        "I check that an import success notification appears in the Import Event Participant popup for DE",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(IMPORT_SUCCESS_DE);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CLOSE);
        });
    When(
        "I click Delete button on Edit Event page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
        });
    When(
        "I click on No option in Confirm deletion on Edit Event Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL_POPUP));
    When(
        "I click on Yes option in Confirm deletion on Edit Event Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_POPUP_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
        });
    When(
        "I check that error message is equal to {string} in Reason for Deletion in popup",
        (String expected) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(REASON_FOR_DELETION_MARK);
          webDriverHelpers.hoverToElement(REASON_FOR_DELETION_MARK);
          String message = webDriverHelpers.getTextFromWebElement(REASON_FOR_DELETION_MESSAGE);
          softly.assertEquals(message, expected, "Error messages are not equal");
          softly.assertAll();
        });
    When(
        "I check if Reason for deletion is set to {string} on Edit Event Page",
        (String expected) -> {
          webDriverHelpers.scrollToElement(REASON_FOR_DELETION_INPUT);
          String collectedReason =
              webDriverHelpers.getValueFromWebElement(REASON_FOR_DELETION_INPUT);
          softly.assertEquals(expected, collectedReason, "Reasons for deletion are not equal");
          softly.assertAll();
        });
    When(
        "I check if Delete button on Edit Event Page is changed to Undo Deletion",
        () -> webDriverHelpers.waitUntilIdentifiedElementIsPresent(UNDO_DELETION_BUTTON));

    When(
        "I set Reason for deletion to {string} on Edit Event Page",
        (String text) -> {
          webDriverHelpers.selectFromCombobox(DELETE_SAMPLE_REASON_POPUP, text);
        });
    When(
        "I check if generated document for Event Participant based on {string} was downloaded properly",
        (String name) -> {
          String eventParticipantData =
              webDriverHelpers.getTextFromWebElement(EVENT_PARTICIPANT_HEADER);
          String uuid =
              eventParticipantData.substring(
                  eventParticipantData.indexOf("(") + 1, eventParticipantData.indexOf(")"));
          String filePath = uuid.toUpperCase() + "-" + name;
          FilesHelper.waitForFileToDownload(filePath, 120);
        });
    When(
        "I check if generated document for Event Participant based on {string} contains all required fields",
        (String name) -> {
          String eventParticipantData =
              webDriverHelpers.getTextFromWebElement(EVENT_PARTICIPANT_HEADER);
          String uuid =
              eventParticipantData.substring(
                  eventParticipantData.indexOf("(") + 1, eventParticipantData.indexOf(")"));
          String pathToFile = userDirPath + "/downloads/" + uuid.toUpperCase() + "-" + name;
          FileInputStream fis = new FileInputStream(pathToFile);
          XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
          List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
          String[] line = paragraphList.get(26).getText().split(":");
          softly.assertEquals(
              line[0], "Report Date", "Report date label is different than expected");
          line = paragraphList.get(28).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccination Date", "Vaccination date label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination
                  .getVaccinationDate()
                  .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
              "Vaccination date value is different than expected");
          line = paragraphList.get(29).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccine name", "Vaccination name label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineName(),
              "Vaccination name value is different than expected");
          line = paragraphList.get(30).getText().split(":");
          softly.assertEquals(
              line[0],
              "Vaccine name Details",
              "Vaccination name Details label is different than expected");
          line = paragraphList.get(31).getText().split(":");
          softly.assertEquals(
              line[0],
              "Vaccine Manufacturer",
              "Vaccination Manufacturer label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineManufacturer(),
              "Vaccination Manufacturer label is different than expected");
          line = paragraphList.get(32).getText().split(":");
          softly.assertEquals(
              line[0],
              "Vaccine Manufacturer details",
              "Vaccination Manufacturer details label is different than expected");
          line = paragraphList.get(33).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccine Type", "Vaccination Type label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineType(),
              "Vaccination Type value is different than expected");
          line = paragraphList.get(34).getText().split(":");
          softly.assertEquals(
              line[0], "Vaccine Dose", "Vaccination Dose label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getVaccineDose(),
              "Vaccination Dose value is different than expected");
          line = paragraphList.get(35).getText().split(":");
          softly.assertEquals(line[0], "INN", "INN label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getInn(),
              "INN value is different than expected");
          line = paragraphList.get(36).getText().split(":");
          softly.assertEquals(line[0], "Batch", "Batch label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getBatchNumber(),
              "Batch value is different than expected");
          line = paragraphList.get(37).getText().split(":");
          softly.assertEquals(line[0], "UNII Code", "UNII Code label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getUniiCode(),
              "UNII Code value is different than expected");
          line = paragraphList.get(38).getText().split(":");
          softly.assertEquals(line[0], "ATC Code", "ATC Code label is different than expected");
          softly.assertEquals(
              line[1].trim(),
              CreateNewVaccinationSteps.vaccination.getAtcCode(),
              "ATC Code value is different than expected");
          line = paragraphList.get(39).getText().split(":");
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
        "I delete downloaded file created from {string} Document Template for Event Participant",
        (String name) -> {
          String eventParticipantData =
              webDriverHelpers.getTextFromWebElement(EVENT_PARTICIPANT_HEADER);
          String uuid =
              eventParticipantData.substring(
                  eventParticipantData.indexOf("(") + 1, eventParticipantData.indexOf(")"));
          String filePath = uuid.toUpperCase() + "-" + name;
          FilesHelper.deleteFile(filePath);
        });
    When(
        "^I create a new event group$",
        () -> {
          groupEvent = eventGroupService.buildGroupEvent();
          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_GROUP_RADIOBUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
          groupEvent =
              groupEvent.toBuilder()
                  .uuid(webDriverHelpers.getValueFromWebElement(GROUP_EVENT_UUID))
                  .build();
          fillGroupEventName(groupEvent.getName());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_GROUP_EVENT_CREATED_MESSAGE);
          webDriverHelpers.clickOnWebElementBySelector(NEW_GROUP_EVENT_CREATED_MESSAGE);
        });

    When(
        "^I am checking event group name and id is correctly displayed$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getByEventUuid(groupEvent.getUuid()));
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getGroupEventName(groupEvent.getName()));
        });

    When(
        "I open the last created event via api",
        () -> {
          String LAST_CREATED_EVENT_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!events/data/"
                  + apiState.getCreatedEvent().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_EVENT_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              EditEventPage.UUID_INPUT);
        });

    When(
        "I navigate to Event Actions tab from Event tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENTS_ACTIONS_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EVENTS_ACTIONS_TAB);
        });

    When(
        "I click on to Edit Event Actions from Event Actions tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_ACTION);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EDIT_EVENT_ACTION);
        });

    And(
        "^I check that the Delete button is not available from Edit Event Actions page$",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(DELETE_EVENT_ACTION_BUTTON, 2),
              "Delete event action button is visible!");
          softly.assertAll();
        });

    When(
        "I click on DELETE button from Edit Event Actions page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DELETE_EVENT_ACTION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_EVENT_ACTION_BUTTON);
        });

    When(
        "I click on Confirm DELETE button Edit Event Actions",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONFIRM_EVENT_DELETE_ACTION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_EVENT_DELETE_ACTION_BUTTON);
        });

    When(
        "I check that Event Actions hasn't got any actions on Event Action page",
        () -> {
          String caption = webDriverHelpers.getTextFromWebElement(NO_EVENT_ACTION_CAPTION);
          softly.assertEquals(caption, "There are no actions for this Event");
          softly.assertAll();
        });

    When(
        "I click on New Action button from Event tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_ACTION_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NEW_ACTION_POPUP);
        });

    When(
        "I navigate to Event Action tab for created Event",
        () -> {
          String LAST_CREATED_EVENT_ACTIONS_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!events/eventactions/"
                  + apiState.getCreatedEvent().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_EVENT_ACTIONS_URL);
          webDriverHelpers.waitForPageLoaded();
        });
    Then(
        "I check that SEE EVENTS FOR THIS PERSON button appears on Edit Person page",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEE_EVENTS_FOR_PERSON);
        });

    Then(
        "I click on New Action from Event Actions tab",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CREATE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_BUTTON);
        });

    Then(
        "I click on Event Actions tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_ACTIONS_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CREATE_BUTTON);
        });

    When(
        "I click on the Create button from Event Document Templates",
        () -> {
          webDriverHelpers.scrollToElement(EditEventPage.CREATE_DOCUMENT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.CREATE_DOCUMENT_BUTTON);
        });

    When(
        "I create and download an event document from template",
        () -> {
          aEventHandout = eventDocumentService.buildEventHandout();
          aEventHandout = aEventHandout.toBuilder().build();
          selectEventHandoutTemplate(aEventHandout.getDocumentTemplate());
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.CREATE_EVENT_HANDOUT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.CANCEL_EVENT_HANDOUT_BUTTON);
        });

    When(
        "I search last created groups Event by {string} option filter in Event Group Directory",
        (String searchCriteria) -> {
          String searchText = "";
          switch (searchCriteria) {
            case "GROUP_ID":
              searchText = groupEvent.getUuid();
              break;
            case "GROUP_TITLE":
              searchText = groupEvent.getName();
              break;
          }
          webDriverHelpers.fillInWebElement(EVENT_GROUP_ID_NAME_INPUT, searchText);
        });
    And(
        "I check that number of displayed Event Participants is {int}",
        (Integer number) -> {
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.EVENT_PARTICIPANTS_TAB);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(
                          webDriverHelpers.getTextFromPresentWebElement(TOTAL_EVENTS_COUNTER)),
                      number.intValue(),
                      "Number of displayed actions is not correct"));
        });
    And(
        "I check that number of actions in Edit Event Tab is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        Integer.parseInt(
                            webDriverHelpers.getTextFromPresentWebElement(TOTAL_ACTIONS_COUNTER)),
                        number.intValue(),
                        "Number of displayed actions is not correct")));

    And(
        "I verify that the event document is downloaded and correctly named",
        () -> {
          String uuid = webDriverHelpers.getValueFromWebElement(EditEventPage.UUID_INPUT);
          String filePath =
              uuid.substring(0, 6).toUpperCase() + "-" + aEventHandout.getDocumentTemplate();
          FilesHelper.waitForFileToDownload(filePath, 120);
        });
    When(
        "I set Place of stay to {string}, Facility Category to {string} and  Facility Type to {string} in Edit Event directory",
        (String placeOfStay, String facilityCategory, String facilityType) -> {
          selectPlaceOfStay(placeOfStay);
          selectFacilityCategory(facilityCategory);
          selectFacilityType(facilityType);
        });

    When(
        "I click on Save Button in Edit Event directory",
        () -> {
          webDriverHelpers.scrollToElement(EDIT_EVENT_PAGE_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_PAGE_SAVE_BUTTON);
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
        });

    When(
        "^I add a participant created by API create person$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SEARCH_FOR_PERSON_BUTTON_IN_ADD_PARTICIPANT_POPUP_WINDOW);
          webDriverHelpers.fillInWebElement(
              SELECT_PERSON_ID_INPUT_AT_ADD_PARTICIPANT, apiState.getLastCreatedPerson().getUuid());
          webDriverHelpers.clickOnWebElementBySelector(
              SELECT_PERSON_SEARCH_BUTTON_AT_ADD_PARTICIPANT);
          webDriverHelpers.clickOnWebElementBySelector(
              SELECT_FIRST_PERSON_IN_SEARCHED_LIST_FROM_ADD_PARTICIPANT);
          webDriverHelpers.clickOnWebElementBySelector(
              CONFIRM_BUTTON_FOR_SELECT_PERSON_FROM_ADD_PARTICIPANTS_WINDOW);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.javaScriptClickElement(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          TimeUnit.SECONDS.sleep(2);
        });

    When(
        "^I delete an event participant created by API create person$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              getEventParticipantByPersonUuid(apiState.getLastCreatedPerson().getUuid()));
          webDriverHelpers.doubleClickOnWebElementBySelector(
              getEventParticipantByPersonUuid(apiState.getLastCreatedPerson().getUuid()));
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_EVENT_PARTICIPANT_BUTTTON);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.selectFromCombobox(
              DELETE_SAMPLE_REASON_POPUP, "Entity created without legal reason");
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
          //
          // webDriverHelpers.clickOnWebElementBySelector(CONFIRM_DELETION_OF_EVENT_PARTICIPANT);
          //          if (webDriverHelpers.isElementVisibleWithTimeout(POPUP_DISCARD_CHANGES_BUTTON,
          // 30)) {
          //            webDriverHelpers.clickOnWebElementBySelector(POPUP_DISCARD_CHANGES_BUTTON);
          //          }
        });

    Then(
        "^I click the Cancel Action button from the Unsaved Changes pop-up located in the Event Participant Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_CANCEL_ACTION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(POPUP_CANCEL_ACTION_BUTTON);
        });

    When(
        "^I check if participant appears in the participants list of event created with API$",
        () -> {
          final String personUuid = apiState.getLastCreatedPerson().getUuid();
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(personUuid));
        });

    When(
        "I click on the Archive event participant button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
        });

    When(
        "I click on the De-Archive event button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
        });

    When(
        "I click on the De-Archive event participant button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
        });

    When(
        "I fill De-Archive event popup with ([^\"]*)",
        (String text) -> {
          webDriverHelpers.fillInWebElement(DEARCHIVE_REASON_TEXT_AREA, text);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_DEARCHIVE_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the Archive event button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
        });

    When(
        "I confirm Archive event popup",
        () -> {
          webDriverHelpers.scrollToElement(CONFIRM_DEARCHIVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_DEARCHIVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(5);
        });

    When(
        "I check if Archive event popup is displayed correctly",
        () -> {
          String expectedString = "Archive event participant";
          String actualString = webDriverHelpers.getTextFromWebElement(ARCHIVE_POPUP_WINDOW_HEADER);
          softly.assertEquals(actualString, expectedString, "Unexpected popup title displayed");
          softly.assertAll();
        });

    When(
        "I click on the Event participant tab",
        () -> {
          webDriverHelpers.scrollToElement(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          if (webDriverHelpers.isElementVisibleWithTimeout(POPUP_YES_BUTTON, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(POPUP_YES_BUTTON);
          }
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_PARTICIPANTS_GRID);
        });

    When(
        "I choose ([^\"]*) from combobox in the Event participant tab",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(EVENT_PARTICIPANT_DISPLAY_FILTER_COMBOBOX, option);
          TimeUnit.SECONDS.sleep(3); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When("I back to the Event tab", () -> webDriverHelpers.clickOnWebElementBySelector(EVENT_TAB));

    When(
        "I check if participant added form UI appears in the event participants list",
        () -> {
          final String personUuid = eventParticipantList.get(0).getUuid();
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(personUuid));
        });
    When(
        "I check if Country combobox on Edit Event page is disabled",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(COUNTRY_COMBOBOX_DIABLED);
        });
    When(
        "I set Country combobox to {string} from Edit Event Page",
        (String country) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(COUNTRY_COMBOBOX);
          webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, country);
        });
    When(
        "I set Country combobox to empty value from Edit Event Page",
        () -> {
          webDriverHelpers.clearComboboxInput(COUNTRY_COMBOBOX_INPUT);
          TimeUnit.MILLISECONDS.sleep(500);
          webDriverHelpers.clickOnWebElementBySelector(DEFAULT_COMBOBOX_VALUE);
        });
    When(
        "I clear Region and District fields from Edit Event Directory",
        () -> {
          webDriverHelpers.clearComboboxInput(DISTRICT_INPUT);
          webDriverHelpers.clearComboboxInput(REGION_INPUT);
        });

    When(
        "I set Region to {string} and District to {string} in Event Participant edit page",
        (String aRegion, String aDistrict) -> {
          webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, aRegion);
          webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, aDistrict);
        });

    When(
        "I check that message appearing in hover of Info icon is equal to expected on Edit Event page",
        () -> {
          String expected =
              "Changing the country is not permitted because at least one event participant in this event does not have a responsible region and/or responsible district set.";
          webDriverHelpers.hoverToElement(COUNTRY_INFO_ICON);
          String displayedText = webDriverHelpers.getTextFromWebElement(COUNTRY_INFO_POPUP_TEXT);
          softly.assertEquals(expected, displayedText, "Message for info popup is incorrect");
          softly.assertAll();
        });
    When(
        "I check if participant added form API appears in the event participants list",
        () -> {
          final String personUuid = eventParticipantList.get(1).getUuid();
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(personUuid));
        });

    When(
        "^I collect the event participant UUID displayed in event participants list$",
        () -> {
          eventParticpantId = collectEventParticipantUuid();
        });

    When(
        "I navigate to a specific Event Participant of an Event based on UUID",
        () -> {
          String LAST_CREATED_EVENT_EVENT_PARTICIPANT_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-ui/#!events/eventparticipants/data/"
                  + eventParticpantId;
          webDriverHelpers.accessWebSite(LAST_CREATED_EVENT_EVENT_PARTICIPANT_URL);
          webDriverHelpers.waitForPageLoaded();
        });

    When(
        "^I click on New Sample and discard changes is asked$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_SAMPLE_BUTTON);
        });

    When(
        "^I confirm popup window$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ACTION);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "I check if editable fields are read only for an archived event",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitForElementPresent(EVENT_PARTICIPANTS_TAB, 15);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_PARTICIPANTS_TAB);
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(EVENT_STATUS_OPTIONS),
              true,
              "Event status option is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RISK_LEVEL_INPUT),
              true,
              "Risk level input is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(TITLE_INPUT),
              true,
              "Title input is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(EVENT_MANAGEMENT_STATUS_OPTIONS),
              true,
              "Event management status option is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(EVENT_INVESTIGATION_STATUS_OPTIONS),
              true,
              "Event investigation status option is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RISK_LEVEL_COMBOBOX),
              true,
              "Risk level combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(NEW_TASK_BUTTON),
              true,
              "New task button is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(NEW_ACTION_BUTTON),
              true,
              "New action button is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(LINK_EVENT_GROUP_BUTTON),
              true,
              "Link event group button is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISEASE_COMBOBOX),
              true,
              "Disease combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(PLACE_OF_STAY_COMBOBOX),
              true,
              "Place of stay combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(SOURCE_TYPE_COMBOBOX),
              true,
              "Source type combobox is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(EVENT_STATUS_OPTIONS),
              true,
              "Event status options is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(EDIT_EVENT_PAGE_SAVE_BUTTON),
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
    Then(
        "I check that checkbox Event Identification source with selected {string} have HTML value: {string}",
        (final String text, final String expected) -> {
          Assert.assertTrue(
              webDriverHelpers.checkCheckboxIsCheckedByHTMLFromParent(
                  EVENT_IDENTIFICATION_SOURCE_COMBOBOX, text, expected));
        });
    When(
        "I add only first and last name data and check is sex combobox required for event participant creation",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SEX_COMBOBOX_REQUIRED);
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
        });

    And(
        "I check event is it archived",
        () -> {
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
          String archiveDearchive =
              webDriverHelpers.getTextFromWebElement(ARCHIVE_EVENT_PARTICIPANT_BUTTON);
          Assert.assertEquals(archiveDearchive, "De-Archive", "Event is not archived.");
        });

    When(
        "I copy url of current event participant", () -> currentUrl = webDriverHelpers.returnURL());

    When(
        "I click on Delete button from event participant",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
        });

    When(
        "I back to deleted event participant by url",
        () -> webDriverHelpers.accessWebSite(currentUrl));

    When(
        "I check if Passport number input on event participant edit page is disabled",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(PASSPORT_NUMBER_INPUT),
              "Passport number input is enabled");
          softly.assertAll();
        });

    When(
        "I check if General comment on event participant edit page is disabled",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(GENERAL_COMMENT_TEXT_AREA),
              "General comment text area is enabled");
          softly.assertAll();
        });

    When(
        "I check if Involvement description input on event participant edit page is disabled",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(INVOLVEMENT_DESCRIPTION_INPUT),
              "General comment text area is enabled");
          softly.assertAll();
        });

    And(
        "I set Disease combobox to {string} value from Edit Event Page",
        (String option) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISEASE_COMBOBOX);
          webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, option);
        });

    And(
        "^I check that New immunization card is not available$",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(NEW_IMMUNIZATION_CARD_WITH_COVID, 2),
              "Contacts With Source Case box is visible!");
          softly.assertAll();
        });

    And(
        "^I click on the NEW IMMUNIZATION button in Edit event participant$",
        () -> {
          webDriverHelpers.scrollToElement(NEW_IMMUNIZATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_IMMUNIZATION_BUTTON);
        });

    When(
        "I check the end of processing date in the archive popup and select Archive event checkbox for DE version",
        () -> {
          String endOfProcessingDate;
          endOfProcessingDate =
              webDriverHelpers.getValueFromWebElement(END_OF_PROCESSING_DATE_POPUP_INPUT);
          softly.assertEquals(
              endOfProcessingDate,
              LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
              "End of processing date is invalid");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I change date of event report for today for DE version",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          webDriverHelpers.fillInWebElement(REPORT_DATE, formatter.format(LocalDate.now()));
        });

    When(
        "I click on confirm button in de-archive event popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP));

    When(
        "I click on discard button in de-archive event popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL_POPUP));

    When(
        "I check the end of processing date in the archive popup and select Archive event participant for DE version",
        () -> {
          String endOfProcessingDate;
          endOfProcessingDate =
              webDriverHelpers.getValueFromWebElement(END_OF_PROCESSING_DATE_POPUP_INPUT);
          softly.assertEquals(
              endOfProcessingDate,
              LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
              "End of processing date is invalid");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    And(
        "^I set event Date on Edit Event Page to (\\d+) days before the vaccination date for DE$",
        (Integer daysBeforeVaccinationDate) -> {
          LocalDate eventStartDate = LocalDate.now().minusDays(21 + daysBeforeVaccinationDate);
          fillStartDataDE(eventStartDate);
        });

    Then(
        "^I Verify The Eye Icon opening the Map is ([^\"]*) in the Edit Event Page",
        (String elementStatus) -> {
          switch (elementStatus) {
            case "disabled":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(EYE_ICON);
              softly.assertFalse(
                  webDriverHelpers.isElementEnabledAtAttributeLevel(EYE_ICON),
                  "Eye Icon is not disabled in the Edit Event Page");
              softly.assertAll();
              break;
            case "enabled":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(EYE_ICON);
              softly.assertTrue(
                  webDriverHelpers.isElementEnabledAtAttributeLevel(EYE_ICON),
                  "Eye Icon is not Enabled in the Edit Event Page");
              softly.assertAll();
              break;
          }
        });

    And(
        "^I Add the GPS Latitude and Longitude Values in the Edit Event Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(GPS_LATITUDE_INPUT_EDIT_EVENT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(GPS_LONGITUDE_INPUT_EDIT_EVENT);
          webDriverHelpers.fillInWebElement(
              GPS_LATITUDE_INPUT_EDIT_EVENT,
              String.valueOf(faker.number().randomDouble(7, 10, 89)));
          webDriverHelpers.fillInWebElement(
              GPS_LONGITUDE_INPUT_EDIT_EVENT,
              String.valueOf(faker.number().randomDouble(7, 10, 89)));
          webDriverHelpers.submitInWebElement(GPS_LATITUDE_INPUT_EDIT_EVENT);
        });

    And(
        "^I click on the The Eye Icon located in the Edit Event Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EYE_ICON);
          webDriverHelpers.clickOnWebElementBySelector(EYE_ICON);
        });

    Then(
        "^I verify that the Map Container is now Visible in the Edit Event Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EYE_ICON);
          softly.assertTrue(
              webDriverHelpers.isElementEnabled(MAP_CONTAINER),
              "Map Container is not displayed/enabled in edit Event Page");
          softly.assertAll();
        });

    When(
        "I click on Create in Case ID row in event participant list",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(
                CREATE_CASE_IN_EVENT_PARTICIPANT_LIST_BUTTON));
    When(
        "I check if citizenship and country of birth is not present in Detailed Event Participant export file",
        () -> {
          String fileName = "sormas_ereignisteilnehmer_" + LocalDate.now() + "_.csv";
          FilesHelper.waitForFileToDownload(fileName, 20);
          String[] headers =
              parseDetailedEventParticipantExportHeaders(userDirPath + "/downloads/" + fileName);
          FilesHelper.deleteFile(fileName);
          softly.assertFalse(
              Arrays.stream(headers).anyMatch("citizenship"::equals),
              "Citizenship is present in file");
          softly.assertFalse(
              Arrays.stream(headers).anyMatch("countryOfBirth"::equals),
              "Country of birth is present in file");
          softly.assertAll();
        });

    And(
        "I check event participant filter dropdown on event participant page when event is active",
        () -> {
          Assert.assertEquals(
              webDriverHelpers.getValueFromCombobox(EVENT_PARTICIPANT_STATUS),
              "Active event participants",
              "Default option is not 'All event participants'");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Active and archived event participants"),
              "There is no 'Active and archived event participants' option in drop list.");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Active event participants"),
              "There is no 'Active event participants' option in drop list.");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Archived event participants"),
              "There is no 'Archived event participants' option in drop list.");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Deleted event participants"),
              "There is no 'Deleted event participants' option in drop list.");
        });

    And(
        "I check event participant filter dropdown on event participant page when event is archived",
        () -> {
          Assert.assertEquals(
              webDriverHelpers.getValueFromCombobox(EVENT_PARTICIPANT_STATUS),
              "Active and archived event participants",
              "Default option is not 'Active and archived event participants'");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Active and archived event participants"),
              "There is no 'Active and archived event participants' option in drop list.");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Active event participants"),
              "There is no 'Active event participants' option in drop list.");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Archived event participants"),
              "There is no 'Archived event participants' option in drop list.");
          Assert.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  EVENT_PARTICIPANT_STATUS, "Deleted event participants"),
              "There is no 'Deleted event participants' option in drop list.");
        });

    When(
        "I copy uuid of current event",
        () -> {
          webDriverHelpers.scrollToElement(EditEventPage.UUID_INPUT);
          eventUUID = webDriverHelpers.getValueFromWebElement(EditEventPage.UUID_INPUT);
        });

    When(
        "I filter by last created event via api",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_EVENT_BY_FREE_TEXT_INPUT, eventUUID);
        });

    And(
        "^I save Add participant form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_RADIO_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }
        });

    And(
        "^I collect event participant uuid$",
        () -> {
          String eventParticipantUuid =
              webDriverHelpers.getValueFromWebElement(EVENT_PARTICIPANT_UUID_INPUT);
          eventParticipantsUUIDList.add(eventParticipantUuid);
        });

    When(
        "I check if Create Contacts Line listing window appears",
        () -> {
          softly.assertTrue(webDriverHelpers.isElementVisibleWithTimeout(LINE_LISTING_HEADER, 2));
          softly.assertAll();
        });

    When(
        "I click on discard button in line listing",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_DISCARD_BUTTON));

    And(
        "^I remove the event date on Edit Event page$",
        () -> {
          webDriverHelpers.clearWebElement(START_DATA_INPUT);
        });

    And(
        "^I set event date field to (\\d+) days before today on Event Edit page for DE$",
        (Integer days) -> {
          webDriverHelpers.scrollToElement(START_DATA_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              START_DATA_INPUT, DATE_FORMATTER.format(LocalDate.now().minusDays(days)));
        });

    And(
        "^I collect event external UUID from Edit Event page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EVENT_EXTERNAL_ID_INPUT);
          externalEventUUID.add(webDriverHelpers.getValueFromWebElement(EVENT_EXTERNAL_ID_INPUT));
        });
  }

  private String collectEventParticipantUuid() {
    return webDriverHelpers.getAttributeFromWebElement(EVENT_PARTICIPANT_UUID, "title");
  }

  private Person collectPersonUuid() {
    return Person.builder().uuid(webDriverHelpers.getValueFromWebElement(POPUP_PERSON_ID)).build();
  }

  private Event collectEventUuid() {
    return Event.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  private Event collectEventData() {
    String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    LocalDate reportDate = LocalDate.parse(reportingDate, DATE_FORMATTER);
    String eventStartDate = webDriverHelpers.getValueFromWebElement(START_DATA_INPUT);
    LocalDate eventDate = LocalDate.parse(eventStartDate, DATE_FORMATTER);

    return Event.builder()
        .reportDate(reportDate)
        .eventDate(eventDate)
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .eventStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(EVENT_STATUS_OPTIONS))
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                EVENT_INVESTIGATION_STATUS_OPTIONS))
        .eventManagementStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                EVENT_MANAGEMENT_STATUS_OPTIONS))
        .riskLevel(webDriverHelpers.getValueFromWebElement(RISK_LEVEL_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .title(webDriverHelpers.getValueFromWebElement(TITLE_INPUT))
        .sourceType(webDriverHelpers.getValueFromWebElement(SOURCE_TYPE_INPUT))
        .eventLocation(webDriverHelpers.getValueFromWebElement(TYPE_OF_PLACE_INPUT))
        .build();
  }

  private Event collectEventMandatoryData() {
    String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    LocalDate reportDate = LocalDate.parse(reportingDate, DATE_FORMATTER);

    return Event.builder()
        .reportDate(reportDate)
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .title(webDriverHelpers.getValueFromWebElement(TITLE_INPUT))
        .build();
  }

  private Event collectEventDataDE() {
    String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    LocalDate reportDate = LocalDate.parse(reportingDate, DATE_FORMATTER_DE);
    String eventStartDate = webDriverHelpers.getValueFromWebElement(START_DATA_INPUT);
    LocalDate eventDate = LocalDate.parse(eventStartDate, DATE_FORMATTER_DE);

    return Event.builder()
        .reportDate(reportDate)
        .eventDate(eventDate)
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .eventStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(EVENT_STATUS_OPTIONS))
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                EVENT_INVESTIGATION_STATUS_OPTIONS))
        .eventManagementStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                EVENT_MANAGEMENT_STATUS_OPTIONS))
        .riskLevel(webDriverHelpers.getValueFromWebElement(RISK_LEVEL_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .title(webDriverHelpers.getValueFromWebElement(TITLE_INPUT))
        .sourceType(webDriverHelpers.getValueFromWebElement(SOURCE_TYPE_INPUT))
        .eventLocation(webDriverHelpers.getValueFromWebElement(TYPE_OF_PLACE_INPUT))
        .build();
  }

  private void selectEventStatus(String eventStatus) {
    webDriverHelpers.clickWebElementByText(EVENT_STATUS_OPTIONS, eventStatus);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, lastName);
  }

  private void selectResponsibleRegion(String region) {
    webDriverHelpers.selectFromCombobox(POPUP_RESPONSIBLE_REGION_COMBOBOX, region);
  }

  private void selectResponsibleDistrict(String district) {
    webDriverHelpers.selectFromCombobox(POPUP_RESPONSIBLE_DISTRICT_COMBOBOX, district);
  }

  private void selectRiskLevel(String riskLevel) {
    webDriverHelpers.selectFromCombobox(RISK_LEVEL_COMBOBOX, riskLevel);
  }

  private void selectEventManagementStatusOption(String eventManagementStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_MANAGEMENT_STATUS_OPTIONS, eventManagementStatusOption);
  }

  private void fillStartData(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillStartDataDE(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER_DE.format(date));
  }

  private void selectEventInvestigationStatusOptions(String eventInvestigationStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_INVESTIGATION_STATUS_OPTIONS, eventInvestigationStatusOption);
  }

  private void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  private void selectPlaceOfStay(String placeOfStay) {
    webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_COMBOBOX, placeOfStay);
  }

  private void fillTitle(String title) {
    webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
  }

  private void selectSourceType(String sourceType) {
    webDriverHelpers.selectFromCombobox(SOURCE_TYPE_COMBOBOX, sourceType);
  }

  private void selectTypeOfPlace(String typeOfPlace) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_PLACE_COMBOBOX, typeOfPlace);
  }

  private void fillDateOfReport(LocalDate date) {
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillGroupEventName(String groupEventName) {
    webDriverHelpers.fillInWebElement(GROUP_EVENT_NAME_POPUP_INPUT, groupEventName);
  }

  private EventGroup collectEventGroupUuid() {
    return EventGroup.builder()
        .uuid(webDriverHelpers.getValueFromWebElement(GROUP_EVENT_UUID))
        .build();
  }

  private void selectEventHandoutTemplate(String templateName) {
    webDriverHelpers.selectFromCombobox(EVENT_HANDOUT_COMBOBOX, templateName);
  }

  public String[] parseDetailedEventParticipantExportHeaders(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseDetailedContactExportHeaders: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseDetailedContactExportHeaders: {}", e.getCause());
    }
    try {
      values = r.get(1);
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseDetailedContactExportHeaders: {}", e.getCause());
    }
    return values;
  }
}
