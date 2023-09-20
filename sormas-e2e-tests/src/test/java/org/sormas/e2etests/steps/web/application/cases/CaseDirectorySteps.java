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

import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ACTION_OKAY;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ACTION_RESET_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ACTION_SEARCH_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ALLBUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ALL_RESULTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS_ARCHIVE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS_DE_ARCHIVE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS_VALUES;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_CREATE_QUARANTINE_ORDER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_EDIT_INFORMATION;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_MODE_SUCCESS_IMAGE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_OPERATION_PROGRESS_BAR;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_RESTORE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CANCEL_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_FROM_OTHER_INSTANCES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_FROM_OTHER_JURISDICTIONS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_HELP_NEEDED_IN_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITHOUT_FACILITY_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITHOUT_GEO_COORDINATES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITHOUT_RESPONSIBLE_OFFICER_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_EVENTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_EXTENDED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_FULFILLED_REFERENCE_DEFINITION_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_REDUCED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_REINFECTION_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_ARCHIVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_COMMUNITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DATA_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DAY_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISEASE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISEASE_VARIANT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISPLAY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISTRICT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_EPIDEMIOLOGICAL_DATA_TAB;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FACILITY_CATEGORY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FACILITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FACILITY_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FOLLOWUP_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_MONTH_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_ORIGIN_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_OUTCOME_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_OWNERSHIP_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_PRESENT_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_QUARANTINE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_REGION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_REINFECTION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_REPORTING_USER_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_SURVOFF_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_VACCINATION_STATUS_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_YEAR_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CLOSE_FORM_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONFIRM_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONFIRM_RESTORATION_WINDOWS_HEADER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_TO_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DETAILED_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DOWNLOAD_DATA_DICTIONARY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DOWNLOAD_IMPORT_GUIDE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ENTER_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ENTER_BULK_EDIT_MODE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.EPI_DATA_TAB;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.EXCLAMATION_MARK_MESSAGE_PICK_OR_CREATE_PERSON_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.EXCLAMATION_MARK_PICK_OR_CREATE_PERSON_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_CASE_ID;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_CASE_ID_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_RESULT_IN_GRID;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.GRID_HEADERS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.INVESTIGATION_DISCARDED_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.INVESTIGATION_DONE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.INVESTIGATION_PENDING_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.LEAVE_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.LINE_LISTING_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MERGE_DUPLICATES_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MERGE_DUPLICATES_WARNING_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MORE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NAME_UUID_EPID_NUMBER_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NEW_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NEW_CASE_DATE_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NEW_EVENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.POPUP_NOTIFICATION_CAPTION;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.POPUP_NOTIFICATION_DESCRIPTION;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.POTENTIAL_DUPLICATE_POPUP_DE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PROGRESSBAR_TOTAL_NUMBER_OF_CASES_LABEL;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PROGRESSBAR_TOTAL_NUMBER_OF_SKIPPED_CASES_LABEL;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PROGRESSBAR_TOTAL_NUMBER_OF_SUCCESSFUL_CASES_LABEL;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.REJECT_SHARED_CASE_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.REJECT_SHARED_CASE_POPUP_TEXT_AREA;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.RELEVANT_STATUS_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.RELEVANT_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.RESULTS_GRID_HEADER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SEARCH_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SEND_TO_REPORTING_TOOL_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHARE_OPTION_BULK_ACTION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.TOTAL_CASES_COUNTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getCaseResultsUuidLocator;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getCaseUUIDBasedOnRowInTable;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getCheckboxByIndex;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getCheckboxInputById;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getMergeDuplicatesButtonById;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getResultByIndex;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getVaccinationStatusCasesByText;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CLOSE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CONFIRM;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ARCHIVE_RELATED_CONTACTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BACK_TO_CASES_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CONFIRM_ACTION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_CASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_CASE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REFERENCE_DEFINITION_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORTING_TOOLS_FOR_SURVNET_USER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORTING_TOOL_MESSAGE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.getCaseIDPathByIndex;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.FIRST_RESULT_IN_GRID_IMPORT_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.IMPORT_CASE_CONTACTS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.IMPORT_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_AS_CASE_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_AS_CASE_NEW_ENTRY_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_AS_CASE_OPTIONS;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.CLOSE_DETAILED_EXPORT_POPUP;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.IMPORT_SUCCESSFUL_FACILITY_IMPORT_CSV;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.PERSON_LIKE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.getCheckboxByUUID;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_CASE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.ARRIVAL_DATE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.UUID_LABEL;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.IMPORT_SUCCESS_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.SELECT_ANOTHER_PERSON_DE;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.BULK_DELETE_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.common.truth.Truth;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.csv.DetailedCaseCSV;
import org.sormas.e2etests.entities.pojo.csv.DetailedCaseCSVSymptoms;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.FacilityCategory;
import org.sormas.e2etests.enums.FollowUpStatus;
import org.sormas.e2etests.enums.PresentCondition;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class CaseDirectorySteps implements En {

  public static Faker faker;
  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  public static final String userDirPath = System.getProperty("user.dir");
  private final DateTimeFormatter formatterDataDictionary =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static String caseID1;
  public static String caseID2;
  public static String leadingCaseUUID;
  public static String[] detailedCaseHeader1, detailedCaseHeader2, detailedCaseHeader3;
  public static String caseCSVName;
  private static String detailedCaseCSVFile;
  public static String uploadFileDirectoryAndName;
  public static String caseUUIDFromCSV;
  private static String firstName;
  private static String lastName;
  private static String receivedCaseUUID;
  public static List<String> listOfCheckedCases = new ArrayList<>();

  @Inject
  public CaseDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      DataOperations dataOperations,
      ApiState apiState,
      AssertHelpers assertHelpers,
      SoftAssert softly,
      Faker faker,
      RunningConfiguration runningConfiguration) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;
    this.faker = faker;

    When(
        "^I click on the NEW CASE button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CASE_BUTTON, DATE_OF_REPORT_INPUT));

    When(
        "^I click on Case Line Listing button$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_BUTTON));

    When(
        "^I click Only cases with fulfilled reference definition checkbox in Cases directory additional filters$",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(
                CASES_WITH_FULFILLED_REFERENCE_DEFINITION_CHECKBOX));

    When(
        "^I open last created case",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_CASE_ID_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_CASE_ID_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if downloaded zip file for Quarantine Order is correct",
        () -> {
          String fileName = "sormas_documents_" + LocalDate.now() + "_.zip";
          FilesHelper.waitForFileToDownload(fileName, 120);
          FilesHelper.deleteFile(fileName);
        });
    When(
        "I check if downloaded zip file for Quarantine Order is correct for DE version",
        () -> {
          String fileName = "sormas_dokumente_" + LocalDate.now() + "_.zip";
          FilesHelper.waitForFileToDownload(fileName, 120);
          FilesHelper.deleteFile(fileName);
        });
    When(
        "I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window",
        () -> {
          webDriverHelpers.fillInWebElement(
              SOURCE_CASE_WINDOW_CASE_INPUT, apiState.getCreatedCase().getUuid());
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });

    When(
        "^Search for Case using Case UUID from the created Task",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, EditCaseSteps.aCase.getUuid());
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click on the DETAILED button from Case directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_DIRECTORY_DETAILED_RADIOBUTTON);
          TimeUnit.SECONDS.sleep(4);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(RESULTS_GRID_HEADER, "Sex")), 20);
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(GRID_HEADERS, 39);
        });
    When(
        "I click on the Import button from Case directory",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(IMPORT_CASE_CONTACTS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(IMPORT_CASE_CONTACTS_BUTTON);
        });
    When(
        "I select the {string} CSV file in the file picker",
        (String fileName) -> {
          TimeUnit.SECONDS.sleep(3);
          webDriverHelpers.sendFile(FILE_PICKER, userDirPath + "/uploads/" + fileName);
        });
    When(
        "I check that an import success notification appears in the Import Case popup",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(IMPORT_SUCCESS_DE);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CLOSE);
        });
    When(
        "I close Import Cases form",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
        });
    When(
        "I click on the {string} button from the Import Case popup",
        (String buttonName) -> {
          webDriverHelpers.clickWebElementByText(IMPORT_POPUP_BUTTON, buttonName);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click to create new person from the Case Import popup",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(COMMIT_BUTTON, 20)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(COMMIT_BUTTON);
          }
        });
    When(
        "I click on the More button on Case directory page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(MORE_BUTTON);
        });
    When(
        "I click on Merge Duplicates on Case directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(MERGE_DUPLICATES_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ACTION_OKAY);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_OKAY);
        });
    When(
        "I click on Merge Duplicates on Case directory for DE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(MERGE_DUPLICATES_BUTTON);
        });
    When(
        "I check if message about long loading times appear for DE",
        () -> {
          String mergeDuplicatesWarning =
              "Die Berechnung und Anzeige m\u00F6glicher Duplikat-F\u00E4lle ist eine sehr komplexe Aufgabe und kann viel Zeit in Anspruch nehmen.";
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              MERGE_DUPLICATES_WARNING_POPUP);
          Assert.assertTrue(
              webDriverHelpers
                  .getTextFromPresentWebElement(MERGE_DUPLICATES_WARNING_POPUP)
                  .contains(mergeDuplicatesWarning),
              "Merge duplicates warning is not correctly displayed");
        });
    When(
        "I click Enter Bulk Edit Mode on Case directory page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ENTER_BULK_EDIT_MODE);
          webDriverHelpers.clickOnWebElementBySelector(ENTER_BULK_EDIT_MODE);
          if (webDriverHelpers.isElementVisibleWithTimeout(BULK_EDIT_INFORMATION, 10)) {
            webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });
    When(
        "I click Leave Bulk Edit Mode on Case directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LEAVE_BULK_EDIT_MODE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });
    When(
        "I click checkbox to choose all Case results",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_RESULTS_CHECKBOX);
        });

    And(
        "I click on close button in Create Quarantine Order form",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_FORM_BUTTON));

    When(
        "^I select first (\\d+) results in grid in Case Directory$",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          for (int i = 2; i <= number + 1; i++) {
            webDriverHelpers.scrollToElement(getCheckboxByIndex(String.valueOf(i)));
            webDriverHelpers.clickOnWebElementBySelector(getCheckboxByIndex(String.valueOf(i)));
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    And(
        "I filter by Person's full name of last created duplicated line listing case on Case Directory Page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
                CaseLineListingSteps.duplicateCaseLineListingDe.getFirstName()
                    + " "
                    + CaseLineListingSteps.duplicateCaseLineListingDe.getLastName()));
    When(
        "^I select last created UI result in grid in Case Directory for Bulk Action$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.scrollToElement(getCheckboxByUUID(EditCaseSteps.aCase.getUuid()));
          webDriverHelpers.clickOnWebElementBySelector(
              getCheckboxByUUID(EditCaseSteps.aCase.getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "^I select last created API result in grid in Case Directory for Bulk Action$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.scrollToElement(getCheckboxByUUID(apiState.getCreatedCase().getUuid()));
          webDriverHelpers.clickOnWebElementBySelector(
              getCheckboxByUUID(apiState.getCreatedCase().getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I select two last created API result in grid in Case Directory for Bulk Action$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          for (int i = 0; i <= 1; i++) {
            webDriverHelpers.scrollToElement(
                getCheckboxByUUID(apiState.getCreatedCases().get(i).getUuid()));
            webDriverHelpers.clickOnWebElementBySelector(
                getCheckboxByUUID(apiState.getCreatedCases().get(i).getUuid()));
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
        });

    When(
        "I click on the Epidemiological data button tab in Case form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    And(
        "I click on Bulk Actions combobox on Case Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS));

    And(
        "I click on Link to Event from Bulk Actions combobox on Case Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_VALUES));

    And(
        "I click on Create Quarantine Order from Bulk Actions combobox on Case Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_CREATE_QUARANTINE_ORDER));

    And(
        "I click on checkbox to upload generated document to entities in Create Quarantine Order form in Case directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX));
    And(
        "I click on New Event option in Link to Event Form",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_CHECKBOX));
    And(
        "I fill Event Id filter in Link to Event form with last created via API Event uuid",
        () -> {
          String eventUuid = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.fillInWebElement(SEARCH_BUTTON, eventUuid);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click first result in grid on Link to Event form",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_RESULT_IN_GRID));

    And(
        "I click on Delete button from Bulk Actions Combobox in Case Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_DELETE_BUTTON));
    When(
        "I filter by CaseID on Case directory page",
        () -> {
          webDriverHelpers.fillInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, apiState.getCreatedCase().getUuid());
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    When(
        "I filter by CaseID of last created UI Case on Case directory page",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, EditCaseSteps.aCase.getUuid());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_CASE_ID_BUTTON);
        });

    When(
        "^I open the last created Case via API",
        () -> {
          String LAST_CREATED_CASE_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!cases/data/"
                  + apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CASE_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EditCasePage.UUID_INPUT);
        });

    When(
        "I open last created Case via API on {string} instance",
        (String instance) -> {
          String LAST_CREATED_CASE_URL =
              runningConfiguration.getEnvironmentUrlForMarket(instance)
                  + "/sormas-webdriver/#!cases/data/"
                  + apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CASE_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EditCasePage.UUID_INPUT);
        });

    Then(
        "I check that created Case is visible with ([^\"]*) status",
        (String vaccinationStatus) -> {
          String caseUUID = apiState.getCreatedCase().getUuid();
          Assert.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(getCaseResultsUuidLocator(caseUUID), 5),
              "There is no case with expected status");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getVaccinationStatusCasesByText(vaccinationStatus));
          Assert.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(
                  getVaccinationStatusCasesByText(vaccinationStatus), 5),
              "There is no case with expected status");
        });

    When(
        "^I navigate to the last created case via the url$",
        () -> {
          String LAST_CREATED_CASE_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!cases/data/"
                  + apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CASE_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
        });

    When(
        "^I navigate to the last created case via the url without check if uuid is enabled$",
        () -> {
          String LAST_CREATED_CASE_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!cases/data/"
                  + apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CASE_URL);
          webDriverHelpers.isElementVisibleWithTimeout(UUID_INPUT, 2);
        });

    Then(
        "I check that number of displayed cases results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        Integer.parseInt(
                            webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER)),
                        number.intValue(),
                        "Number of displayed cases is not correct")));

    And(
        "I filter by mocked CaseID on Case directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(
                  "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
        });
    When(
        "^I search for cases created with the API using Person's name",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_RESET_FILTERS_BUTTON);
          int maximumNumberOfRows = 23;
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              CASE_GRID_RESULTS_ROWS, maximumNumberOfRows);
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
              apiState.getLastCreatedPerson().getFirstName()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName());
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, apiState.getCreatedCases().size());
          Assert.assertEquals(
              apiState.getCreatedCases().size(),
              Integer.parseInt(webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER)),
              "Total number of displayed cases doesn't match with the number of cases created via api");
        });

    Then(
        "I apply Outcome of case filter {string}",
        (String outcomeFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_OUTCOME_FILTER_COMBOBOX, CaseOutcome.getValueFor(outcomeFilterOption));
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
        });
    And(
        "I navigate to Epidemiological Data tab on Edit Case Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPI_DATA_TAB);
        });
    When(
        "^I click on ([^\"]*) Radiobutton on Epidemiological Data Page$",
        (String buttonName) -> {
          webDriverHelpers.clickWebElementByText(ACTIVITY_AS_CASE_OPTIONS, buttonName);
        });
    Then(
        "I click on new entry button from Epidemiological Data tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTIVITY_AS_CASE_NEW_ENTRY_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_LABEL);
        });

    Then(
        "I click on new entry button from Epidemiological Data tab for DE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTIVITY_AS_CASE_NEW_ENTRY_BUTTON_DE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ARRIVAL_DATE);
        });

    And(
        "I check that all displayed cases have {string} in grid Case Classification column",
        (String expectedValue) -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CASE_GRID_RESULTS_ROWS, CaseOutcome.getValueFor(expectedValue));
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertWithMessage(
                          expectedValue
                              + " value is not displayed in grid Case Classification column")
                      .that(
                          apiState.getCreatedCases().stream()
                              .filter(sample -> sample.getOutcome().contentEquals("NO_OUTCOME"))
                              .count())
                      .isEqualTo(
                          Integer.valueOf(
                              webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER))));
        });
    // TODO refactor method to use a specific outcome once the other fix is done

    Then(
        "I apply Disease filter {string}",
        (String diseaseFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DISEASE_FILTER_COMBOBOX, DiseasesValues.getCaptionFor(diseaseFilterOption));
        });

    Then(
        "I check that all displayed cases have {string} in grid Disease column",
        (String expectedValue) -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CASE_GRID_RESULTS_ROWS, DiseasesValues.getCaptionFor(expectedValue));
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertWithMessage(
                          expectedValue + " value is not displayed in grid Disease column")
                      .that(
                          apiState.getCreatedCases().stream()
                              .filter(
                                  sample ->
                                      sample
                                          .getDisease()
                                          .contentEquals(
                                              DiseasesValues.CORONAVIRUS.getDiseaseName()))
                              .count())
                      .isEqualTo(
                          Integer.valueOf(
                              webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER))));
        });
    And(
        "I apply uuid filter for last created via API Person in Case directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
                apiState.getLastCreatedPerson().getUuid()));
    When(
        "I click to select another person from Pick or create person popup for DE",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SELECT_ANOTHER_PERSON_DE);
        });
    When(
        "I check if name and name prefilled in Pick or create person are equal to one used in case creation",
        () -> {
          softly.assertEquals(
              CaseReinfectionSteps.caze.getFirstName()
                  + " "
                  + CaseReinfectionSteps.caze.getLastName(),
              webDriverHelpers.getValueFromWebElement(UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT),
              "Prefilled date name is not equal");
          softly.assertAll();
        });
    And(
        "I apply Person Id filter to one attached to last created UI Case on Case directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
                EditCaseSteps.aCase.getFirstName() + " " + EditCaseSteps.aCase.getLastName()));
    And(
        "I apply mocked Person Id filter on Case directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT, "TestName TestSurname"));
    And(
        "I filter by {string} as a Person's full name on Case Directory Page",
        (String fullName) ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT, fullName));
    Then(
        "I apply Disease filter {string} on Case directory page",
        (String diseaseFilterOption) ->
            webDriverHelpers.selectFromCombobox(
                CASE_DISEASE_FILTER_COMBOBOX, DiseasesValues.getCaptionFor(diseaseFilterOption)));
    Then(
        "I apply Disease Variant filter {string} on Case directory page",
        (String diseaseFilterOption) ->
            webDriverHelpers.selectFromCombobox(
                CASE_DISEASE_VARIANT_FILTER_COMBOBOX, diseaseFilterOption));
    And(
        "I click SHOW MORE FILTERS button on Case directory page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_LESS_FILTERS));

    Then(
        "I apply Outcome of case filter {string} on Case directory page",
        (String outcomeFilterOption) ->
            webDriverHelpers.selectFromCombobox(CASE_OUTCOME_FILTER_COMBOBOX, outcomeFilterOption));
    And(
        "I apply Case classification filter {string} on Case directory page",
        (String caseClassification) ->
            webDriverHelpers.selectFromCombobox(
                CASE_CLASSIFICATION_FILTER_COMBOBOX, caseClassification));
    And(
        "I apply Follow-up filter {string} on Case directory page",
        (String caseClassification) ->
            webDriverHelpers.selectFromCombobox(
                CASE_FOLLOWUP_FILTER_COMBOBOX, FollowUpStatus.getValueFor(caseClassification)));
    And(
        "I apply Present Condition filter on Case directory page to condition of last created person",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_PRESENT_CONDITION_COMBOBOX,
                PresentCondition.getValueFor(
                    apiState.getLastCreatedPerson().getPresentCondition())));
    And(
        "I apply Present Condition filter on Case directory page to condition of person attached to created Case",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_PRESENT_CONDITION_COMBOBOX,
                CreateNewCaseSteps.caze.getPresentConditionOfPerson()));
    And(
        "I apply Present Condition filter to {string} on Case directory page",
        (String presentCondition) ->
            webDriverHelpers.selectFromCombobox(CASE_PRESENT_CONDITION_COMBOBOX, presentCondition));

    And(
        "I apply Present Condition filter on Case directory page to different than actual",
        () -> {
          String differentPresentCondition = apiState.getLastCreatedPerson().getPresentCondition();
          while (differentPresentCondition.equals(
              apiState.getLastCreatedPerson().getPresentCondition())) {
            differentPresentCondition = PresentCondition.getRandomPresentCondition();
          }
          webDriverHelpers.selectFromCombobox(
              CASE_PRESENT_CONDITION_COMBOBOX,
              PresentCondition.getValueFor(differentPresentCondition));
        });
    And(
        "I click APPLY BUTTON in Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });

    Then(
        "I apply Case origin {string} on Case directory page",
        (String caseOrigin) ->
            webDriverHelpers.selectFromCombobox(CASE_ORIGIN_FILTER_COMBOBOX, caseOrigin));
    Then(
        "I apply Community {string} on Case directory page",
        (String community) ->
            webDriverHelpers.selectFromCombobox(CASE_COMMUNITY_FILTER_COMBOBOX, community));
    And(
        "I apply Region filter {string} on Case directory page",
        (String region) ->
            webDriverHelpers.selectFromCombobox(CASE_REGION_FILTER_COMBOBOX, region));
    And(
        "I apply Surveillance Officer filter {string} on Case directory page",
        (String survoff) ->
            webDriverHelpers.selectFromCombobox(CASE_SURVOFF_FILTER_COMBOBOX, survoff));
    And(
        "I apply Reporting User filter {string} on Case directory page",
        (String reportingUser) ->
            webDriverHelpers.fillInWebElement(CASE_REPORTING_USER_FILTER, reportingUser));
    And(
        "I apply Vaccination Status filter to {string} on Case directory page",
        (String vaccinationStatus) ->
            webDriverHelpers.selectFromCombobox(
                CASE_VACCINATION_STATUS_FILTER_COMBOBOX, vaccinationStatus));
    And(
        "I apply Quarantine filter to {string} on Case directory page",
        (String quarantine) ->
            webDriverHelpers.selectFromCombobox(CASE_QUARANTINE_FILTER_COMBOBOX, quarantine));
    And(
        "I apply Reinfection filter to {string} on Case directory page",
        (String reinfection) -> {
          webDriverHelpers.selectFromCombobox(CASE_REINFECTION_FILTER_COMBOBOX, reinfection);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
        });
    And(
        "I apply Date type filter to {string} on Case directory page",
        (String dataType) ->
            webDriverHelpers.selectFromCombobox(CASE_DATA_TYPE_FILTER_COMBOBOX, dataType));
    And(
        "I apply Report on onset date type filter to {string} on Merge duplicate cases page",
        (String dataType) ->
            webDriverHelpers.selectFromCombobox(DATE_TYPE_FILTER_COMBOBOX, dataType));

    And(
        "I fill date from input to today on Merge Duplicate Cases page",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              NEW_CASE_DATE_FROM_COMBOBOX, formatter.format(LocalDate.now()));
        });
    And(
        "I click to CONFIRM FILTERS on Merge Duplicate Cases page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(By.id("actionConfirmFilters"));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(500);
        });
    And(
        "I click on Merge button of leading case in Merge Duplicate Cases page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              getMergeDuplicatesButtonById(EditCaseSteps.aCase.getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
        });
    When(
        "I check that Share option is not visible in Bulk Actions dropdown in Case Directory for DE specific",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(SHARE_OPTION_BULK_ACTION_COMBOBOX, 3),
              "Share is visible!");
          softly.assertAll();
        });
    When(
        "I collect the leading case UUID displayed on Case Directory Page",
        () -> leadingCaseUUID = getCaseIDByIndex(1));
    And(
        "I click on Merge button of leading case created through line listing in Merge Duplicate Cases page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              getMergeDuplicatesButtonById(leadingCaseUUID));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
        });
    And(
        "I fill Cases from input to {int} days before mocked Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedCase().getReportDate().toInstant(),
                          ZoneId.systemDefault())
                      .minusDays(number)));
        });

    And(
        "I fill Cases from input to {int} days before mocked two Case created on Case directory page via api",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedCases().get(0).getReportDate().toInstant(),
                          ZoneId.systemDefault())
                      .minusDays(number)));
        });

    And(
        "I fill Cases from input to {int} days before UI Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(CreateNewCaseSteps.caze.getDateOfReport().minusDays(number)));
        });
    And(
        "I fill Cases from input to {int} days before mocked Cases created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedCases().get(0).getReportDate().toInstant(),
                          ZoneId.systemDefault())
                      .minusDays(number)));
        });
    And(
        "I fill Cases from input to {int} days after before mocked Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX, formatter.format(LocalDate.now().plusDays(number)));
        });
    And(
        "I fill Cases from input to {int} days after before UI Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(CreateNewCaseSteps.caze.getDateOfReport().plusDays(number)));
        });
    And(
        "I click All button in Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALLBUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click on Investigation pending button on Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(INVESTIGATION_PENDING_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click on Investigation done button on Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(INVESTIGATION_DONE_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click on Investigation discarded button on Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(INVESTIGATION_DISCARDED_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });

    And(
        "I click {string} checkbox on Case directory page",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Only cases without geo coordinates"):
            case ("Nur F\u00E4lle ohne Geo-Koordinaten"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITHOUT_GEO_COORDINATES_CHECKBOX);
              break;
            case ("Only cases without responsible officer"):
            case ("Nur F\u00E4lle ohne verantwortlichen Benutzer"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_WITHOUT_RESPONSIBLE_OFFICER_CHECKBOX);
              break;
            case ("Only cases with extended quarantine"):
            case ("Nur F\u00E4lle mit verl\u00E4ngerter Isolation"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_EXTENDED_QUARANTINE_CHECKBOX);
              break;
            case ("Only cases with reduced quarantine"):
            case ("Nur F\u00E4lle mit verk\u00FCrzter Isolation"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_REDUCED_QUARANTINE_CHECKBOX);
              break;
            case ("Help needed in quarantine"):
            case ("Ma\u00DFnahmen zur Gew\u00E4hrleistung der Versorgung"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_HELP_NEEDED_IN_QUARANTINE_CHECKBOX);
              break;
            case ("Only cases with events"):
            case ("Nur F\u00E4lle mit Ereignissen"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_EVENTS_CHECKBOX);
              break;
            case ("Only cases from other instances"):
            case ("Nur F\u00E4lle von anderen Instanzen"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_FROM_OTHER_INSTANCES_CHECKBOX);
              break;
            case ("Only cases with reinfection"):
            case ("Nur F\u00E4lle mit Reinfektion"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_REINFECTION_CHECKBOX);
              break;
            case ("Include cases from other jurisdictions"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_FROM_OTHER_JURISDICTIONS_CHECKBOX);
              break;
            case ("Only cases with fulfilled reference definition"):
            case ("Nur F\u00E4lle mit erf\u00FCllter Referenzdefinition"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_WITH_FULFILLED_REFERENCE_DEFINITION_CHECKBOX);
              break;
            case ("Only port health cases without a facility"):
            case ("Nur Einreisef\u00E4lle ohne zugewiesene Einrichtung"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITHOUT_FACILITY_CHECKBOX);
              break;
          }
        });
    And(
        "I fill Cases to input to {int} days after mocked Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedCase().getReportDate().toInstant(),
                          ZoneId.systemDefault())
                      .plusDays(number)));
        });
    And(
        "I fill Cases to input to {int} days after UI Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(CreateNewCaseSteps.caze.getDateOfReport().plusDays(number)));
        });
    When(
        "I click to Confirm action in Merge Duplicates Cases popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(500);
        });
    And(
        "I apply Year filter different than Person has on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_YEAR_FILTER,
                getRandomNumberForBirthDateDifferentThanCreated(
                        apiState.getLastCreatedPerson().getBirthdateYYYY(), 1900, 2002)
                    .toString()));
    And(
        "I apply Year filter of last api created Person on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_YEAR_FILTER, apiState.getLastCreatedPerson().getBirthdateYYYY().toString()));
    And(
        "I apply Year filter of Person attached to last created UI Case on Case directory page",
        () -> {
          String year = Integer.toString(CreateNewCaseSteps.caze.getDateOfBirth().getYear());
          webDriverHelpers.selectFromCombobox(CASE_YEAR_FILTER, year);
        });
    And(
        "I apply Month filter of Person attached to last created UI Case on Case directory page",
        () -> {
          DateFormatSymbols dFs = new DateFormatSymbols(Locale.GERMAN);
          String month =
              dFs.getMonths()[CreateNewCaseSteps.caze.getDateOfBirth().getMonthValue() - 1];
          webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month);
        });
    And(
        "I apply Day filter of Person attached to last created UI Case on Case directory page",
        () -> {
          String day = Integer.toString(CreateNewCaseSteps.caze.getDateOfBirth().getDayOfMonth());
          webDriverHelpers.selectFromCombobox(CASE_DAY_FILTER, day);
        });
    And(
        "I apply Year filter other than Person attached has to last created UI Case on Case directory page",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_YEAR_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      CreateNewCaseSteps.caze.getDateOfBirth().getYear(), 1900, 2002)
                  .toString());
        });
    And(
        "I apply Month filter other than Person attached has to last created UI Case on Case directory page",
        () -> {
          DateFormatSymbols dFs = new DateFormatSymbols(Locale.GERMAN);
          String month =
              dFs.getMonths()[CreateNewCaseSteps.caze.getDateOfBirth().getMonthValue() - 2];
          webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month);
        });
    And(
        "I apply Day filter other than Person attached has to last created UI Case on Case directory page",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DAY_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      CreateNewCaseSteps.caze.getDateOfBirth().getDayOfMonth(), 1, 28)
                  .toString());
        });
    And(
        "I apply {string} to combobox on Case Directory Page",
        (String caseParameter) -> {
          webDriverHelpers.checkIfElementExistsInCombobox(
              CASE_DISPLAY_FILTER_COMBOBOX, caseParameter);
          webDriverHelpers.selectFromCombobox(CASE_DISPLAY_FILTER_COMBOBOX, caseParameter);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_CASE_ID);
        });

    When(
        "I validate the existence of {string} Reporting Tools entries in Survnet box",
        (String number) -> {
          int numberInt = Integer.parseInt(number);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EditCasePage.SEND_TO_REPORTING_TOOL_BUTTON);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(REPORTING_TOOLS_FOR_SURVNET_USER),
              numberInt,
              "Number of sent entries to Survnet is not correct");
          softly.assertAll();
        });

    And(
        "I apply {string} to ownership combobox on Case Directory Page",
        (String caseParameter) -> {
          webDriverHelpers.checkIfElementExistsInCombobox(
              CASE_OWNERSHIP_FILTER_COMBOBOX, caseParameter);
          webDriverHelpers.selectFromCombobox(CASE_OWNERSHIP_FILTER_COMBOBOX, caseParameter);
          TimeUnit.SECONDS.sleep(2);
        });

    And(
        "I apply Month filter different than Person has on Case directory page",
        () -> {
          DateFormatSymbols dFs = new DateFormatSymbols();
          String month = dFs.getMonths()[apiState.getLastCreatedPerson().getBirthdateMM() - 2];
          webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month);
        });
    And(
        "I apply Month filter of last api created Person on Case directory page",
        () -> {
          DateFormatSymbols dFs = new DateFormatSymbols();
          String month = dFs.getMonths()[apiState.getLastCreatedPerson().getBirthdateMM() - 1];
          webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month);
        });
    And(
        "I apply Day filter different than Person has on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_DAY_FILTER,
                getRandomNumberForBirthDateDifferentThanCreated(
                        apiState.getLastCreatedPerson().getBirthdateDD(), 1, 27)
                    .toString()));
    And(
        "I apply Day filter of last api created Person on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_DAY_FILTER, apiState.getLastCreatedPerson().getBirthdateDD().toString()));
    And(
        "I apply Facility category filter {string} on Case directory page",
        (String facilityCategory) ->
            webDriverHelpers.selectFromCombobox(
                CASE_FACILITY_CATEGORY_FILTER_COMBOBOX,
                FacilityCategory.getValueFor(facilityCategory)));
    And(
        "I apply Facility type filter to {string} on Case directory page",
        (String facilityType) ->
            webDriverHelpers.selectFromCombobox(CASE_FACILITY_TYPE_FILTER_COMBOBOX, facilityType));
    And(
        "I apply Facility filter to {string} on Case directory page",
        (String facility) ->
            webDriverHelpers.selectFromCombobox(CASE_FACILITY_FILTER_COMBOBOX, facility));
    And(
        "I apply District filter {string} on Case directory page",
        (String district) ->
            webDriverHelpers.selectFromCombobox(
                CASE_DISTRICT_FILTER_COMBOBOX, DistrictsValues.getNameValueFor(district)));

    And(
        "I apply Year filter {string} on Case directory page",
        (String year) -> webDriverHelpers.selectFromCombobox(CASE_YEAR_FILTER, year));

    And(
        "I apply Month filter {string} on Case directory page",
        (String month) -> webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month));

    And(
        "I apply Day filter {string} on Case directory page",
        (String day) -> webDriverHelpers.selectFromCombobox(CASE_DAY_FILTER, day));

    And(
        "I check that only cases with fulfilled reference definition are being shown in Cases directory",
        () -> {
          Integer totalResults =
              Integer.parseInt(webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER));
          for (int i = 0; totalResults > 0 && i < totalResults && i < 5; i++) {
            By result = getResultByIndex(String.valueOf(i + 1));
            webDriverHelpers.scrollToElement(result);
            webDriverHelpers.clickOnWebElementBySelector(result);
            String caseReference =
                webDriverHelpers.getValueFromWebElement(REFERENCE_DEFINITION_TEXT);
            softly.assertEquals(caseReference, "Erf\u00FCllt");
            webDriverHelpers.clickOnWebElementBySelector(BACK_TO_CASES_BUTTON);
          }
          softly.assertAll();
        });

    When(
        "I click on the import button for Cases in Case tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(IMPORT_BUTTON));

    When(
        "I click on the detailed button from import Case tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(DETAILED_IMPORT_BUTTON));

    When(
        "I click on the Download Import Guide button in Import Cases",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DOWNLOAD_IMPORT_GUIDE_BUTTON);
        });

    When(
        "And I click on the Download Data Dictionary button in Import Cases",
        () -> webDriverHelpers.clickOnWebElementBySelector(DOWNLOAD_DATA_DICTIONARY_BUTTON));

    When(
        "I check if Import Guide for cases was downloaded correctly",
        () -> {
          String fileName = "SORMAS_Import_Guide.pdf";
          FilesHelper.waitForFileToDownload(fileName, 30);
          FilesHelper.deleteFile(fileName);
        });

    When(
        "I check if Data Dictionary for cases was downloaded correctly",
        () -> {
          String fileName =
              "sormas_data_dictionary_"
                  + LocalDate.now().format(formatterDataDictionary)
                  + "_.xlsx";
          FilesHelper.waitForFileToDownload(fileName, 20);
          FilesHelper.deleteFile(fileName);
        });
    When(
        "I check if citizenship and country of birth is not present in Detailed Case export file",
        () -> {
          String fileName = "sormas_f\u00E4lle_" + LocalDate.now() + "_.csv";
          String[] headers = parseDetailedCaseExportHeaders(userDirPath + "/downloads/" + fileName);
          FilesHelper.deleteFile(fileName);
          softly.assertFalse(
              Arrays.stream(headers).anyMatch("citizenship"::equals),
              "Citizenship is present in file");
          softly.assertFalse(
              Arrays.stream(headers).anyMatch("countryOfBirth"::equals),
              "Country of birth is present in file");
          softly.assertAll();
        });
    When(
        "I check that ([^\"]*) is visible in Pick or Create Person popup for De",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          Boolean elementVisible = true;
          switch (option) {
            case "External Id":
              selector = UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT;
              break;
            case "Reset":
              selector = ACTION_RESET_POPUP;
              break;
            case "Search":
              selector = ACTION_SEARCH_POPUP;
              break;
          }
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertTrue(elementVisible, option + " is not visible!");
          softly.assertAll();
        });
    When(
        "I click on Reset filters in Pick or create Person popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_RESET_POPUP);
        });
    When(
        "I click on Search in Pick or create Person popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_SEARCH_POPUP);
        });
    When(
        "I check that error message is equal to {string} in Pick or Create person in popup",
        (String expected) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              EXCLAMATION_MARK_PICK_OR_CREATE_PERSON_POPUP);
          webDriverHelpers.hoverToElement(EXCLAMATION_MARK_PICK_OR_CREATE_PERSON_POPUP);
          String message =
              webDriverHelpers.getTextFromWebElement(
                  EXCLAMATION_MARK_MESSAGE_PICK_OR_CREATE_PERSON_POPUP);
          softly.assertEquals(message, expected, "Error messages are not equal");
          softly.assertAll();
        });
    When(
        "I fill first and last name with last created peron data in Pick or Create person in popup",
        () -> {
          webDriverHelpers.fillInWebElement(
              UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT,
              apiState.getLastCreatedPerson().getFirstName()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName());
        });
    When(
        "I click on first result in Pick or create Person popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_RESULT_IN_GRID_IMPORT_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
    When(
        "I click on the Archive bulk cases on Case Directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_ARCHIVE);
        });

    When(
        "I click on the De-Archive bulk cases on Case Directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_DE_ARCHIVE);
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "I confirm archive bulk cases and select Archive related contacts checkbox",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_RELATED_CONTACTS_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(CASE_ARCHIVED_POPUP);
        });

    Then(
        "I get two last cases ID from cases list",
        () -> {
          caseID1 = getCaseIDByIndex(1);
          caseID2 = getCaseIDByIndex(2);
        });
    And(
        "I open {int} case in order from list",
        (Integer index) -> {
          webDriverHelpers.getWebElement(getCaseIDPathByIndex(index)).click();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EditContactPage.GENERAL_COMMENT_TEXT_AREA);
        });

    Then(
        "I compare previous first case ID on the list with actually second case ID on list",
        () -> {
          Assert.assertEquals(
              caseID1,
              getCaseIDByIndex(2),
              "Edited case do not move previous first case to second place on list.");
        });

    When(
        "I prepare detailed case CSV with {string} as a disease and {string} as a present condition",
        (String disease, String pCondition) -> {
          long timestamp = System.currentTimeMillis();
          detailedCaseCSVFile = "./uploads/sormas_cases_sordev_10361.csv";
          Map<String, Object> reader;
          reader = parseCSVintoPOJODetailedCaseCSV(detailedCaseCSVFile);
          caseCSVName = "detailedCaseCSVTestFile" + timestamp + ".csv";
          writeCSVFromMapDetailedCase(reader, caseCSVName, disease, pCondition);
        });

    When(
        "I select created CSV file with detailed case",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FILE_PICKER);
          webDriverHelpers.sendFile(FILE_PICKER, userDirPath + "/uploads/" + caseCSVName);
        });

    When(
        "I check if csv file for detailed case is imported successfully",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(IMPORT_SUCCESSFUL_FACILITY_IMPORT_CSV, 10);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CLOSE);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_DETAILED_EXPORT_POPUP);
        });

    When(
        "I click on the {string} button from the Import Detailed Case popup",
        (String buttonName) -> {
          webDriverHelpers.clickWebElementByText(IMPORT_POPUP_BUTTON, buttonName);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_CASE_POPUP_HEADER, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
        });

    When(
        "I search for created detailed case by first and last name of the person",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_LIKE_SEARCH_INPUT, firstName + " " + lastName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I delete created csv file for detailed case import",
        () -> {
          Path path = Paths.get(userDirPath + "/uploads/" + caseCSVName);
          Files.delete(path);
        });

    Then(
        "I set case vaccination status filter to ([^\"]*)",
        (String vaccinationStatus) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_VACCINATION_STATUS_FILTER_COMBOBOX, vaccinationStatus);
        });

    And(
        "I apply case filters",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
        });

    When(
        "I search case by user by name {string}",
        (String firstAndLastName) -> {
          webDriverHelpers.fillAndSubmitInWebElement(PERSON_LIKE_SEARCH_INPUT, firstAndLastName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on Okay button in Potential duplicate popup",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(POTENTIAL_DUPLICATE_POPUP_DE, 5);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM);
        });

    When(
        "I fill comment field in Reject share request popup and click confirm",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(REJECT_SHARED_CASE_HEADER_DE);
          webDriverHelpers.clickOnWebElementBySelector(REJECT_SHARED_CASE_POPUP_TEXT_AREA);
          webDriverHelpers.fillInWebElement(
              REJECT_SHARED_CASE_POPUP_TEXT_AREA, faker.book().title());
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });

    And(
        "^I filter Cases by collected case uuid$",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, CreateNewCaseSteps.casesUUID.get(0));
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    And(
        "^I open the last created case via API and check if Edit case page is read only$",
        () -> {
          String LAST_CREATED_CASE_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!cases/data/"
                  + apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CASE_URL);
          webDriverHelpers.isElementGreyedOut(EditCasePage.UUID_INPUT);
          webDriverHelpers.isElementGreyedOut(EditCasePage.SAVE_BUTTON);
        });

    And(
        "I open the last created case with collected UUID by url on {string} instance",
        (String instance) -> {
          String LAST_CREATED_CASE_URL =
              runningConfiguration.getEnvironmentUrlForMarket(instance)
                  + "/sormas-ui/#!cases/data/"
                  + CreateNewCaseSteps.casesUUID.get(0);
          webDriverHelpers.accessWebSite(LAST_CREATED_CASE_URL);
        });

    When(
        "^I select (\\d+) last created UI result in grid in Case Directory for Bulk Action$",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          for (int i = 0; i < number; i++) {
            webDriverHelpers.scrollToElement(
                getCheckboxByUUID(CreateNewCaseSteps.casesUUID.get(i)));
            webDriverHelpers.clickOnWebElementBySelector(
                getCheckboxByUUID(CreateNewCaseSteps.casesUUID.get(i)));
          }
        });

    And(
        "^I click Send to reporting tool button on Case Directory page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SEND_TO_REPORTING_TOOL_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SEND_TO_REPORTING_TOOL_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ACTION);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(REPORTING_TOOL_MESSAGE);
        });

    And(
        "^I check that Relevance Status Filter is set to \"([^\"]*)\" on Case Directory page$",
        (String relevanceStatus) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(RELEVANT_STATUS_INPUT),
              relevanceStatus,
              "Relevance status is incorrect!");
          softly.assertAll();
        });

    And(
        "I click on {string} option in Enter bulk edit mode window",
        (String option) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(ENTER_BULK_EDIT_MODE_POPUP_HEADER);
          switch (option) {
            case "Yes":
              webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
              break;
            case "No":
              webDriverHelpers.clickOnWebElementBySelector(CANCEL_POPUP);
              break;
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });

    When(
        "I check if popup message for archive is {string} in Case Directory page",
        (String expectedText) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POPUP_NOTIFICATION_CAPTION);
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(POPUP_NOTIFICATION_CAPTION),
              expectedText,
              "Bulk archive action went wrong");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(POPUP_NOTIFICATION_CAPTION);
        });

    And(
        "^I check that warning message appears that no cases are selected$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POPUP_NOTIFICATION_DESCRIPTION);
        });

    And(
        "I click checkboxes to choose first {int} cases from Case Directory page",
        (Integer numberOfCases) -> {
          listOfCheckedCases.clear();
          for (int i = 2; i <= numberOfCases + 1; i++) {
            if (!webDriverHelpers.isElementPresent(getCheckboxInputById(i))) {
              webDriverHelpers.scrollInTable(15);
              TimeUnit.SECONDS.sleep(2); // wait for an element to be attached to the DOM
            }
            webDriverHelpers.clickOnWebElementBySelector(getCheckboxInputById(i));
            TimeUnit.SECONDS.sleep(1);
            listOfCheckedCases.add(
                webDriverHelpers.getTextFromWebElement(getCaseUUIDBasedOnRowInTable(i)));
          }
        });

    When(
        "I check if popup deletion message appeared for {string}",
        (String language) -> {
          String expectedText;
          if (language.equalsIgnoreCase("DE")) {
            expectedText = "Alle ausgew\u00E4hlten Einreisen wurden gel\u00F6scht";
          } else if (language.equalsIgnoreCase("EN")) {
            expectedText = "All selected eligible cases have been deleted";
          } else {
            expectedText = null;
          }
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POPUP_NOTIFICATION_CAPTION);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(POPUP_NOTIFICATION_CAPTION),
              expectedText,
              "Bulk action went wrong");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(POPUP_NOTIFICATION_CAPTION);
        });

    When(
        "I check if popup send message appeared for {string}",
        (String language) -> {
          String expectedText;
          if (language.equalsIgnoreCase("DE")) {
            expectedText =
                "Alle ausgew\u00E4hlten Eintr\u00E4ge wurden an die Meldesoftware gesendet.";
          } else if (language.equalsIgnoreCase("EN")) {
            expectedText = "All selected entries have been sent to the reporting tool.";
          } else {
            expectedText = null;
          }
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POPUP_NOTIFICATION_CAPTION);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(POPUP_NOTIFICATION_CAPTION),
              expectedText,
              "Bulk action went wrong");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(POPUP_NOTIFICATION_CAPTION);
        });

    And(
        "^I check that a bulk progress operation window appears on Case Directory page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(BULK_OPERATION_PROGRESS_BAR);
        });

    And(
        "^I check that total number of cases for bulk operation is (\\d+)$",
        (Integer totalNumberOfCases) -> {
          int lastIndexOfSlash =
              webDriverHelpers
                  .getTextFromWebElement(PROGRESSBAR_TOTAL_NUMBER_OF_CASES_LABEL)
                  .lastIndexOf("/");
          int lastIndexOfSpace =
              webDriverHelpers
                  .getTextFromWebElement(PROGRESSBAR_TOTAL_NUMBER_OF_CASES_LABEL)
                  .lastIndexOf(" ");
          softly.assertEquals(
              webDriverHelpers
                  .getTextFromWebElement(PROGRESSBAR_TOTAL_NUMBER_OF_CASES_LABEL)
                  .substring(lastIndexOfSlash + 1, lastIndexOfSpace),
              totalNumberOfCases.toString(),
              "Total number of cases is incorrect");
          softly.assertAll();
        });

    And(
        "^I wait until the bulk progress operation is done and check numbers of (\\d+) successful and (\\d+) skipped cases$",
        (Integer successfulCases, Integer skippedCases) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              BULK_MODE_SUCCESS_IMAGE, 300);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(
                  PROGRESSBAR_TOTAL_NUMBER_OF_SUCCESSFUL_CASES_LABEL),
              successfulCases + " Successful",
              "Number of successful cases is incorrect");
          softly.assertAll();
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(
                  PROGRESSBAR_TOTAL_NUMBER_OF_SKIPPED_CASES_LABEL),
              skippedCases + " Skipped",
              "Number of skipped cases is incorrect");
          softly.assertAll();
        });

    And(
        "^I click on close progress operation window$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_FORM_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(90);
        });

    Then(
        "^I click on Restore button from Bulk Actions Combobox in Case Directory$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BULK_RESTORE_BUTTON);
        });

    And(
        "^I check that Confirm Restoration popup appears and confirm popup$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONFIRM_RESTORATION_WINDOWS_HEADER);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
        });

    And(
        "^I set the Relevance Status Filter to \"([^\"]*)\" on Case Directory page$",
        (String status) -> {
          webDriverHelpers.selectFromCombobox(RELEVANT_STATUS_COMBOBOX, status);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
        });
  }

  private List<String> getTableColumnDataByIndex(int col, int maxRows) {
    List<String> list = new ArrayList<>();
    for (int i = 1; i < maxRows + 1; i++) {
      list.add(
          webDriverHelpers.getTextFromWebElement(
              By.xpath("//tbody//tr[" + i + "]//td[" + col + "]")));
    }
    return list;
  }

  private Number getRandomNumberForBirthDateDifferentThanCreated(Number created, int min, int max) {
    Number replacement = created;
    while (created.equals(replacement)) {
      replacement = faker.number().numberBetween(min, max);
    }
    return replacement;
  }

  private String getCaseIDByIndex(int index) {
    return webDriverHelpers.getTextFromWebElement(getCaseIDPathByIndex(index));
  }

  public Map<String, Object> parseCSVintoPOJODetailedCaseCSV(String fileName) {

    List<String[]> r = null;
    String[] values = new String[] {};
    ObjectMapper mapper = new ObjectMapper();
    DetailedCaseCSV detailedCaseCSV = new DetailedCaseCSV();
    DetailedCaseCSVSymptoms detailedCaseCSVSymptoms = new DetailedCaseCSVSymptoms();
    try {

      CSVReader headerReader = new CSVReader(new FileReader(fileName));
      String[] nextLine;
      nextLine = headerReader.readNext();
      detailedCaseHeader1 = nextLine;
      nextLine = headerReader.readNext();
      detailedCaseHeader2 = nextLine;
      nextLine = headerReader.readNext();
      detailedCaseHeader3 = nextLine;
    } catch (IOException e) {
      log.error("IOException csvReader: ", e);
    } catch (CsvException e) {
      log.error("CsvException header reader: ", e);
    }
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build(); // custom separator
    // Convert POJOs to Maps
    Map<String, Object> detailedCasePojo =
        mapper.convertValue(detailedCaseCSV, new TypeReference<Map<String, Object>>() {});
    Map<String, Object> detailedCaseSymptompsPojo =
        mapper.convertValue(detailedCaseCSVSymptoms, new TypeReference<Map<String, Object>>() {});
    //
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser) // custom CSV parser
            .withSkipLines(3) // skip the first three lines, headers info
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException csvReader: ", e);
    } catch (CsvException e) {
      log.error("CsvException csvReader: ", e);
    }
    for (int i = 0; i < r.size(); i++) {
      values = r.get(i);
    }

    String[] keys1 = detailedCasePojo.keySet().toArray(new String[0]);
    String[] keys2 = detailedCaseSymptompsPojo.keySet().toArray(new String[0]);

    try {
      for (int i = 0; i < keys1.length; i++) {
        detailedCasePojo.put(keys1[i], values[i]);
      }

      for (int i = 0; i < keys2.length; i++) {
        detailedCaseSymptompsPojo.put(keys2[i], values[i + keys1.length]);
      }

      detailedCasePojo.putAll(detailedCaseSymptompsPojo);

    } catch (NullPointerException e) {
      log.error("Null pointer exception csvReader: ", e);
    }
    return detailedCasePojo;
  }

  @SneakyThrows
  public static void writeCSVFromMapDetailedCase(
      Map<String, Object> detailedCase, String createdFileName, String disease, String pCondition) {
    uploadFileDirectoryAndName = userDirPath + "/uploads/" + createdFileName;

    File file = new File(uploadFileDirectoryAndName);
    try {
      FileWriter outputfile = new FileWriter(file);
      CSVWriter writer =
          new CSVWriter(
              outputfile,
              ',',
              CSVWriter.NO_QUOTE_CHARACTER,
              CSVWriter.NO_ESCAPE_CHARACTER,
              CSVWriter.DEFAULT_LINE_END);
      List<String[]> data = new ArrayList<String[]>();
      firstName = faker.name().firstName();
      lastName = faker.name().lastName();
      caseUUIDFromCSV = generateShortUUID();
      String personUUID = generateShortUUID();
      String epidNumber = generateShortUUID();
      int lRandom = ThreadLocalRandom.current().nextInt(8999999, 9999999 + 1);
      detailedCase.computeIfPresent("id", (k, v) -> v = String.valueOf(lRandom));
      detailedCase.computeIfPresent("uuid", (k, v) -> v = caseUUIDFromCSV);
      detailedCase.computeIfPresent("epidNumber", (k, v) -> v = epidNumber);
      detailedCase.computeIfPresent("personUuid", (k, v) -> v = personUUID);
      detailedCase.computeIfPresent("personFirstName", (k, v) -> v = firstName);
      detailedCase.computeIfPresent("personLastName", (k, v) -> v = lastName);
      detailedCase.computeIfPresent("disease", (k, v) -> v = disease);
      detailedCase.computeIfPresent("personPresentCondition", (k, v) -> v = pCondition);
      String[] rowdata = detailedCase.values().toArray(new String[0]);
      ArrayList<String> sArray = new ArrayList<String>();
      for (String s : rowdata) {
        sArray.add("\"" + s + "\"");
      }
      detailedCaseHeader1[0] = "\"" + detailedCaseHeader1[0] + "\"";
      data.add(detailedCaseHeader1);
      data.add(detailedCaseHeader2);
      data.add(detailedCaseHeader3);
      data.add(sArray.toArray(new String[0]));
      writer.writeAll(data);
      writer.close();
    } catch (IOException e) {
      log.error("IOException csvWriter: ", e);
    }
  }

  public String[] parseDetailedCaseExportHeaders(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseDetailedCaseExportHeaders: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseDetailedCaseExportHeaders: {}", e.getCause());
    }
    try {
      values = r.get(1);
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseDetailedCaseExportHeaders: {}", e.getCause());
    }
    return values;
  }
}
