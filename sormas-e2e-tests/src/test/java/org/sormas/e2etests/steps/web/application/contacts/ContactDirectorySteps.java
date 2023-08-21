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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ACTION_OKAY;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ALL_RESULTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CONNECTION_NUMBER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISPLAY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_MEANS_OF_TRANSPORT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_MEANS_OF_TRANSPORT_DETAILS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_SEAT_NUMBER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ENTER_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FACILITY_ACTIVITY_AS_CASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.LEAVE_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MORE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHARE_OPTION_BULK_ACTION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getMergeDuplicatesButtonById;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CLOSE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_CASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_CASE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.IMPORT_CASE_CONTACTS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.IMPORT_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.*;
import static org.sormas.e2etests.pages.application.cases.LineListingPopup.LINE_LISTING_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.CLOSE_DETAILED_EXPORT_POPUP;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.IMPORT_SUCCESSFUL_FACILITY_IMPORT_CSV;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ACTION_MERGE_CONTACT_DIRECTORY;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ACTIVE_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ALL_BUTTON_CONTACT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.BULK_ACTIONS_CONTACT_VALUES;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.BULK_CREATE_QUARANTINE_ORDER;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.BULK_DELETE_BUTTON_CONTACT_PAGE;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_FROM_OTHER_INSTANCES_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_ONLY_HIGH_PRIOROTY_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_EXTENDED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_HELP_NEEDED_IN_QUARANTINE_ORDERED_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_NO_QUARANTINE_ORDERED_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_QUARANTINE_ORDERED_BY_OFFICIAL_DOCUMENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_QUARANTINE_ORDERED_VERBALLY_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_REDUCED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_CASE_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DATA_TAB;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DIRECTORY_DETAILED_PAGE_CONFIRM_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DIRECTORY_DETAILED_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DISEASE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DISEASE_VARIANT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DISPLAY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_FOLLOW_UP_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_MERGE_DUPLICATES;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_RESULTS_UUID_LOCATOR;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_VACCINATION_STATUS_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONVERTED_TO_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CREATE_CASE_FROM_POSITIVE_TEST_RESULT_HEADER_DE;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.DROPPED_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.EPIDEMIOLOGICAL_DATA_TAB;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.FIRST_CONTACT_ID_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.GRID_HEADERS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.GRID_RESULTS_COUNTER_CONTACT_DIRECTORY;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.LINE_LISTING;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.MULTIPLE_OPTIONS_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.NEW_ENTRY_EPIDEMIOLOGICAL_DATA;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.PERSON_LIKE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.RELATIONSHIP_WITH_CASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.RESULTS_GRID_HEADER;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.getCheckboxByUUID;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.getContactsByUUID;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.getVaccinationStatusContactsByText;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.getContactIDPathByIndex;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.AREA_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_EMAIL_ADRESS;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_FIRST_NAME;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_LAST_NAME;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_PHONE_NUMBER;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.EXPOSURE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_DETAILS_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_NAME_AND_DESCRIPTION;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.GPS_ACCURACY_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.GPS_LATITUDE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.GPS_LONGITUDE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_ACTIVITY_DETAILS;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_GATHERING_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_GATHERING_DETAILS;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_PLACE_DETAILS;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.IMPORT_SUCCESS_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PICK_OR_CREATE_PERSON_HEADER_DE;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.CONFIRM_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.contacts.EditContactSteps.aContact;
import static org.sormas.e2etests.steps.web.application.contacts.EditContactSteps.collectedContact;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
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
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.csv.DetailedContactCSV;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.pojo.web.EpidemiologicalData;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfGathering;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class ContactDirectorySteps implements En {

  protected WebDriverHelpers webDriverHelpers;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Exposure exposureData;
  public static EpidemiologicalData dataSavedFromCheckbox;
  public static EpidemiologicalData specificCaseData;
  public static Contact contact;
  public static List<String> contactUUID = new ArrayList<>();
  public static final String userDirPath = System.getProperty("user.dir");
  public static Faker faker;

  public static String contactID1;
  public static String contactID2;
  public static String leadingContactUUID;
  private static String contactCSVName;
  private static String detailedContactCSVFile;
  private static String[] detailedContactHeader1, detailedContactHeader2, detailedContactHeader3;
  private static String uploadFileDirectoryAndName;
  public static String firstName;
  public static String lastName;
  public static String contactUUIDFromCSV;

  @Inject
  public ContactDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      AssertHelpers assertHelpers,
      ContactService contactService,
      DataOperations dataOperations,
      Faker faker,
      SoftAssert softly,
      RunningConfiguration runningConfiguration) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;

    When(
        "^I open the last created contact via API$",
        () -> {
          String LAST_CREATED_CONTACT_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!contacts/data/"
                  + apiState.getCreatedContact().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CONTACT_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
        });

    When(
        "I open the last created contact via API from {string}",
        (String environmentIdentifier) -> {
          String LAST_CREATED_CONTACT_URL =
              runningConfiguration.getEnvironmentUrlForMarket(environmentIdentifier)
                  + "/sormas-webdriver/#!contacts/data/"
                  + apiState.getCreatedContact().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CONTACT_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
        });

    When(
        "I collect uuid of the contact",
        () -> {
          contactUUID.add(webDriverHelpers.getValueFromWebElement(UUID_INPUT));
        });
    When(
        "^I navigate to the last created UI contact via the url$",
        () -> {
          String LAST_CREATED_CONTACT_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!contacts/data/"
                  + collectedContact.getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CONTACT_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });
    When(
        "I apply Id of last created Contact on Contact Directory Page",
        () -> {
          String contactUuid = collectedContact.getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
        });
    When(
        "I apply filter by duplicated contact Person data on Contact Directory Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
          webDriverHelpers.fillInWebElement(
              PERSON_LIKE_SEARCH_INPUT,
              CreateNewContactSteps.duplicatedContact.getFirstName()
                  + ' '
                  + CreateNewContactSteps.duplicatedContact.getLastName());
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
        });
    When(
        "^I select last created API result in grid in Contact Directory for Bulk Action$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.scrollToElement(
              getCheckboxByUUID(apiState.getCreatedContact().getUuid()));
          webDriverHelpers.clickOnWebElementBySelector(
              getCheckboxByUUID(apiState.getCreatedContact().getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click Enter Bulk Edit Mode on Contact directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ENTER_BULK_EDIT_MODE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });
    When(
        "^I select last created UI result in grid in Contact Directory for Bulk Action$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.scrollToElement(getCheckboxByUUID(collectedContact.getUuid()));
          webDriverHelpers.clickOnWebElementBySelector(
              getCheckboxByUUID(collectedContact.getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the NEW CONTACT button",
        () -> {
          webDriverHelpers.clickWhileOtherButtonIsDisplayed(
              NEW_CONTACT_BUTTON, FIRST_NAME_OF_CONTACT_PERSON_INPUT);
        });
    And(
        "I click on Create Quarantine Order from Bulk Actions combobox on Contact Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_CREATE_QUARANTINE_ORDER));

    And(
        "I click on Bulk Actions combobox on Contact Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS));
    When(
        "I click on save Contact button",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(EditCasePage.CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
            if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_CASE_POPUP_HEADER, 1)) {
              webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            }
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
        });
    When(
        "I collect the leading contact UUID displayed on Contact Directory Page",
        () -> leadingContactUUID = getContactIDByIndex(1));
    And(
        "I click on Merge button of leading case in Merge Duplicate Contact page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              getMergeDuplicatesButtonById(collectedContact.getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
        });
    And(
        "I click on Merge button of leading duplicated line listing Contact in Merge Duplicate Contact page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              getMergeDuplicatesButtonById(leadingContactUUID));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
        });
    When(
        "I check that Share option is not visible in Bulk Actions dropdown in Contact Directory for DE specific",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(SHARE_OPTION_BULK_ACTION_COMBOBOX, 3),
              "Share is visible!");
          softly.assertAll();
        });
    And(
        "I click on Merge button of first leading Contact in Merge Duplicate Contact page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_MERGE_CONTACT_DIRECTORY);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
        });
    And(
        "I filter by Case ID used during Contact creation",
        () -> {
          webDriverHelpers.fillInWebElement(
              MULTIPLE_OPTIONS_SEARCH_INPUT, apiState.getCreatedCase().getUuid());
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
        });
    When(
        "I click on the DETAILED radiobutton from Contact directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_DIRECTORY_DETAILED_RADIOBUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(RESULTS_GRID_HEADER, "Sex")), 20);
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(GRID_HEADERS, 18);
        });

    When(
        "I filter by Contact uuid",
        () -> {
          String contactUuid = apiState.getCreatedContact().getUuid();
          By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, contactUuid));
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(uuidLocator);
        });

    When(
        "I apply Id of last api created Contact on Contact Directory Page",
        () -> {
          String contactUuid = apiState.getCreatedContact().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
        });
    When(
        "I click on the Import button from Contact directory",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(IMPORT_CASE_CONTACTS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(IMPORT_CASE_CONTACTS_BUTTON);
        });
    When(
        "I click on the {string} button from the Import Contact popup",
        (String buttonName) -> {
          webDriverHelpers.clickWebElementByText(IMPORT_POPUP_BUTTON, buttonName);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click to create new person from the Contact Import popup if Pick or create popup appears",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(COMMIT_BUTTON, 20)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(COMMIT_BUTTON);
          }
        });
    When(
        "I check that an import success notification appears in the Import Contact popup for DE",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(IMPORT_SUCCESS_DE);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CLOSE);
        });
    And(
        "I filter by {string} as a Person's full name on Contact Directory Page",
        (String fullName) ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT, fullName));
    When(
        "I close Import Contact form",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
        });
    When(
        "I click on the More button on Contact directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(MORE_BUTTON);
        });
    When(
        "I click on Merge Duplicates on Contact directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_MERGE_DUPLICATES);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(ACTION_OKAY);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_OKAY);
          TimeUnit.SECONDS.sleep(2);
        });
    When(
        "I click Leave Bulk Edit Mode on Contact directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LEAVE_BULK_EDIT_MODE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });
    And(
        "I click on Link to Event from Bulk Actions combobox on Contact Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_CONTACT_VALUES));

    When(
        "I click checkbox to choose all Contact results on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_RESULTS_CHECKBOX);
        });

    When(
        "I click on New Entry in Exposure Details Known",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_ENTRY_EPIDEMIOLOGICAL_DATA);
        });

    When(
        "I select all options in Type of activity from Combobox in Exposure for Epidemiological data tab in Contacts",
        () -> {
          String[] ListOfTypeOfActivityExposure =
              TypeOfActivityExposure.ListOfTypeOfActivityExposure;
          for (String value : ListOfTypeOfActivityExposure) {
            webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, value);
          }
        });

    When(
        "I select all Type of gathering from Combobox in Exposure for Epidemiological data tab in Contacts",
        () -> {
          for (TypeOfGathering value : TypeOfGathering.values()) {
            if (value != TypeOfGathering.valueOf("OTHER")) {
              webDriverHelpers.selectFromCombobox(TYPE_OF_GATHERING_COMBOBOX, value.toString());
            }
          }
        });

    When(
        "I check all Type of place from Combobox in Exposure for Epidemiological data tab in Contacts",
        () -> {
          for (TypeOfPlace value : TypeOfPlace.values()) {
            webDriverHelpers.selectFromCombobox(
                TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(value.toString()));
          }
        });

    When(
        "I click on edit Exposure vision button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_SAVED_EXPOSURE_BUTTON);
        });

    When(
        "I select ([^\"]*) option in Type of activity from Combobox in Exposure form",
        (String typeOfactivity) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, typeOfactivity);
        });

    When(
        "I select ([^\"]*) option in Type of activity from Combobox in Activity as Case form",
        (String typeOfactivity) -> {
          webDriverHelpers.selectFromCombobox(ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX, typeOfactivity);
        });

    When(
        "I fill Location form for Type of place by chosen {string} options in Exposure for Epidemiological data",
        (String searchCriteria) -> {
          exposureData = contactService.buildGeneratedExposureDataContactForRandomInputs();
          switch (searchCriteria) {
            case "HOME":
              webDriverHelpers.selectFromCombobox(
                  TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(searchCriteria));
              fillLocation(exposureData);
              break;
            case "OTHER":
              webDriverHelpers.selectFromCombobox(
                  TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(searchCriteria));
              fillLocation(exposureData);
              webDriverHelpers.fillInWebElement(
                  TYPE_OF_PLACE_DETAILS, exposureData.getTypeOfPlaceDetails());
              break;
            case "FACILITY":
              webDriverHelpers.selectFromCombobox(
                  TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(searchCriteria));
              fillLocation(exposureData);
              webDriverHelpers.selectFromCombobox(
                  FACILITY_CATEGORY_COMBOBOX, exposureData.getFacilityCategory());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_TYPE_COMBOBOX, exposureData.getFacilityType());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacility());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              String emailAddress = exposureData.getContactPersonEmail();
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
          }
        });

    When(
        "I fill Location form for Type of place field by {string} options in Case directory for DE version",
        (String searchCriteria) -> {
          exposureData = contactService.buildGeneratedExposureDataContactForRandomInputsDE();
          switch (searchCriteria) {
            case "Unbekannt":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              break;
            case "Sonstiges":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              webDriverHelpers.fillInWebElement(TYPE_OF_PLACE_DETAILS, faker.book().title());
              fillLocationDE(exposureData);
              break;
            case "Transportmittel":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              webDriverHelpers.selectFromCombobox(CASE_MEANS_OF_TRANSPORT, "Sonstiges");
              webDriverHelpers.fillInWebElement(
                  CASE_MEANS_OF_TRANSPORT_DETAILS, faker.book().publisher());
              webDriverHelpers.fillInWebElement(
                  CASE_CONNECTION_NUMBER, String.valueOf(faker.number().numberBetween(1, 200)));
              webDriverHelpers.fillInWebElement(
                  CASE_SEAT_NUMBER, String.valueOf(faker.number().numberBetween(1, 200)));
              fillLocationDE(exposureData);
              break;
            case "Einrichtung":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(
                  FACILITY_CATEGORY_COMBOBOX, exposureData.getFacilityCategory());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_TYPE_COMBOBOX, exposureData.getFacilityType());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              String emailAddress = exposureData.getContactPersonEmail();
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
          }
        });
    When(
        "I fill Location form for Type of place field by {string} options in Case as Activity directory for DE version",
        (String searchCriteria) -> {
          exposureData = contactService.buildGeneratedExposureDataContactForRandomInputsDE();
          String emailAddress = exposureData.getContactPersonEmail();
          switch (searchCriteria) {
            case "Einrichtung (\u00A7 23 IfSG)":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, "Krankenhaus");
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
            case "Gemeinschaftseinrichtung (\u00A7 33 IfSG)":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, "Schule");
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
            case "Sonstiges":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              webDriverHelpers.fillInWebElement(TYPE_OF_PLACE_DETAILS, faker.book().title());
              exposureData = contactService.buildGeneratedExposureDataContactForRandomInputsDE();
              fillLocationDE(exposureData);
              break;
          }
        });

    When(
        "I fill Location form for Type of place field by {string} options in Case as Activity directory",
        (String searchCriteria) -> {
          exposureData = contactService.buildGeneratedExposureDataContactForRandomInputs();
          String emailAddress = exposureData.getContactPersonEmail();
          switch (searchCriteria) {
            case "Facility (\u00A7 23 IfSG)":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, "Hospital");
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
            case "Facility (\u00A7 36 IfSG)":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, "Other Care facility");
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
            case "Community facility (\u00A7 33 IfSG)":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, "School");
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
            case "Other":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              webDriverHelpers.fillInWebElement(TYPE_OF_PLACE_DETAILS, faker.book().title());
              fillLocationDE(exposureData);
              break;
            case "Unknown":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              break;
          }
        });

    When(
        "I am checking all Location data in Activity as Case are saved and displayed",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_ACTIVITY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_ACTIVITY_BUTTON);
          Exposure actualLocationData = collectLocationData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              actualLocationData,
              exposureData,
              List.of(
                  "continent",
                  "subcontinent",
                  "country",
                  "exposureRegion",
                  "district",
                  "community",
                  "street",
                  "houseNumber",
                  "additionalInformation",
                  "postalCode",
                  "city",
                  "areaType"));
        });

    When(
        "I click on save button in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });
    When(
        "I select a Type of activity ([^\"]*) option in Exposure for Epidemiological data tab in Contacts",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, option);
        });

    When(
        "I select a type of gathering ([^\"]*) from Combobox in Exposure for Epidemiological data tab in Contacts",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_GATHERING_COMBOBOX, option);
        });

    When(
        "I fill a type of gathering details in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.fillInWebElement(TYPE_OF_GATHERING_DETAILS, faker.chuckNorris().fact());
        });

    When(
        "I fill a Type of activity details field in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.fillInWebElement(TYPE_OF_ACTIVITY_DETAILS, faker.book().title());
        });

    When(
        "I click on the Epidemiological Data navbar field",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    And(
        "I filter by mocked ContactID on Contact directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(
                  "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
        });
    And(
        "I click SHOW MORE FILTERS button on Contact directory page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_LESS_FILTERS));
    Then(
        "I apply Disease of source filter {string} on Contact Directory Page",
        (String diseaseFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_DISEASE_FILTER_COMBOBOX, DiseasesValues.getCaptionFor(diseaseFilterOption));
        });
    Then(
        "I apply Contact classification filter to {string} on Contact Directory Page",
        (String contactClassification) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_CLASSIFICATION_FILTER_COMBOBOX, contactClassification);
        });
    Then(
        "I apply Disease variant filter to {string} on Contact Directory Page",
        (String diseaseVariant) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_DISEASE_VARIANT_FILTER_COMBOBOX, diseaseVariant);
        });
    And(
        "I apply Classification of source case filter to {string} on Contact Directory Page",
        (String classification) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_CASE_CLASSIFICATION_FILTER_COMBOBOX, classification);
        });
    And(
        "I apply Follow-up status filter to {string} on Contact Directory Page",
        (String followUp) -> {
          webDriverHelpers.selectFromCombobox(CONTACT_FOLLOW_UP_FILTER_COMBOBOX, followUp);
        });
    And(
        "I click APPLY BUTTON in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              CONTACT_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click APPLY BUTTON in Merge Duplicates View on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              CONTACT_DIRECTORY_DETAILED_PAGE_CONFIRM_FILTER_BUTTON);
          // TODO -> replace with wait for counter to change implementation
          TimeUnit.SECONDS.sleep(2);
        });

    And(
        "I click {string} checkbox on Contact directory page",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Quarantine ordered verbally?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_VERBALLY_CHECKBOX);
              break;
            case ("Quarantine ordered by official document?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_BY_OFFICIAL_DOCUMENT_CHECKBOX);
              break;
            case ("No quarantine ordered"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_NO_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Help needed in quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_HELP_NEEDED_IN_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Only high priority contacts"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_ONLY_HIGH_PRIOROTY_CHECKBOX);
              break;
            case ("Only contacts with extended quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_EXTENDED_QUARANTINE_CHECKBOX);
              break;
            case ("Only contacts with reduced quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_REDUCED_QUARANTINE_CHECKBOX);
              break;
            case ("Only contacts from other instances"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_FROM_OTHER_INSTANCES_CHECKBOX);
              break;
          }
        });

    And(
        "I click {string} checkbox on Contact directory page for DE version",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Quarant\u00E4ne m\u00FCndlich verordnet?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_VERBALLY_CHECKBOX);
              break;
            case ("Quarant\u00E4ne schriftlich verordnet?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_BY_OFFICIAL_DOCUMENT_CHECKBOX);
              break;
            case ("Keine Quarant\u00E4ne verordnet"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_NO_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Ma\u00DFnahmen zur Gew\u00E4hrleistung der Versorgung"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_HELP_NEEDED_IN_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Nur Kontakte mit hoher Priorit\u00E4t"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_ONLY_HIGH_PRIOROTY_CHECKBOX);
              break;
            case ("Nur Kontakte mit verl\u00E4ngerter Quarant\u00E4ne"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_EXTENDED_QUARANTINE_CHECKBOX);
              break;
            case ("Nur Kontakte mit verk\u00FCrzter Quarant\u00E4ne"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_REDUCED_QUARANTINE_CHECKBOX);
              break;
            case ("Nur Kontakte von anderen Instanzen"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_FROM_OTHER_INSTANCES_CHECKBOX);
              break;
          }
        });
    And(
        "I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
              ContactsLineListingSteps.duplicatedContactLineListingDE.getFirstName()
                  + " "
                  + ContactsLineListingSteps.duplicatedContactLineListingDE.getLastName());
        });
    When(
        "^I click on Line Listing button$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LINE_LISTING);
          webDriverHelpers.doubleClickOnWebElementBySelector(LINE_LISTING);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LINE_LISTING_SAVE_BUTTON);
        });

    And(
        "I click on All button in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON_CONTACT);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });
    And(
        "I click on Converted to case pending button on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONVERTED_TO_CASE_BUTTON);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });
    And(
        "I click on Active contact button in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTIVE_CONTACT_BUTTON);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });
    And(
        "I click on Dropped button on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DROPPED_BUTTON);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });

    When(
        "I fill all the data in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.clickWebElementByText(EXPOSURE_DETAILS_KNOWN_OPTIONS, "YES");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
          exposureData = contactService.buildGeneratedExposureDataForContact();
          fillExposure(exposureData);
        });
    When(
        "I click on the Epidemiological Data button tab in Contact form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the Contact tab in Contacts",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I am checking all Exposure data is saved and displayed in Contacts",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(START_OF_EXPOSURE_INPUT);
          Exposure actualExposureData = collectExposureData();
          ComparisonHelper.compareEqualEntities(exposureData, actualExposureData);
        });

    When(
        "I click on Residing or working in an area with high risk of transmission of the disease in Contact with ([^\"]*) option",
        (String option) -> {
          dataSavedFromCheckbox =
              EpidemiologicalData.builder()
                  .residingAreaWithRisk(YesNoUnknownOptions.valueOf(option))
                  .build();
          webDriverHelpers.clickWebElementByText(RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS, option);
        });

    When(
        "I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission in Contact with ([^\"]*) option",
        (String option) -> {
          dataSavedFromCheckbox =
              dataSavedFromCheckbox.toBuilder()
                  .largeOutbreaksArea(YesNoUnknownOptions.valueOf(option))
                  .build();
          webDriverHelpers.clickWebElementByText(
              RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS, option);
        });
    And(
        "I click on Delete button from Bulk Actions Combobox in Contact Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_DELETE_BUTTON_CONTACT_PAGE));

    When(
        "I am checking if options in checkbox for Contact are displayed correctly",
        () -> {
          specificCaseData = collectSpecificData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              specificCaseData,
              dataSavedFromCheckbox,
              List.of("residingAreaWithRisk", "largeOutbreaksArea"));
        });

    When(
        "I am checking all Location data in Exposure are saved and displayed",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EDIT_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_SAVED_EXPOSURE_BUTTON);
          Exposure actualLocationData = collectLocationData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              actualLocationData,
              exposureData,
              List.of(
                  "continent",
                  "subcontinent",
                  "country",
                  "exposureRegion",
                  "district",
                  "community",
                  "street",
                  "houseNumber",
                  "additionalInformation",
                  "postalCode",
                  "city",
                  "areaType"));
        });

    When(
        "I search after last created contact via API by name and uuid then open",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
          webDriverHelpers.fillInWebElement(
              MULTIPLE_OPTIONS_SEARCH_INPUT, apiState.getCreatedContact().getUuid());
          webDriverHelpers.fillInWebElement(
              PERSON_LIKE_SEARCH_INPUT,
              apiState.getCreatedContact().getPerson().getFirstName()
                  + " "
                  + apiState.getCreatedContact().getPerson().getLastName());
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          openContactFromResultsByUUID(apiState.getCreatedContact().getUuid());
        });

    When(
        "I open the first contact from contacts list",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_CONTACT_ID_BUTTON));

    Then(
        "I check that number of displayed contact results is (\\d+)",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GRID_RESULTS_COUNTER_CONTACT_DIRECTORY, 50);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(
                          webDriverHelpers.getTextFromPresentWebElement(
                              GRID_RESULTS_COUNTER_CONTACT_DIRECTORY)),
                      number.intValue(),
                      "Number of displayed contacts is not correct"));
        });

    When(
        "I choose ([^\"]*) form combobox on Contact Directory Page",
        (String contactType) -> {
          webDriverHelpers.selectFromCombobox(CONTACT_DISPLAY_FILTER_COMBOBOX, contactType);
          TimeUnit.SECONDS.sleep(3); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    Then(
        "I get two last contacts ID from cases list",
        () -> {
          contactID1 = getContactIDByIndex(1);
          contactID2 = getContactIDByIndex(2);
        });
    And(
        "I open {int} contact in order from list",
        (Integer index) -> {
          webDriverHelpers.getWebElement(getContactIDPathByIndex(index)).click();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EditContactPage.GENERAL_COMMENT_TEXT_AREA);
        });

    Then(
        "I compare previous first contact ID on the list with actually second contact ID on list",
        () -> {
          Assert.assertEquals(
              contactID1,
              getContactIDByIndex(2),
              "Edited contact do not move previous first contact to second place on list.");
        });

    When(
        "^I set Relationship with case on ([^\"]*)$",
        (String option) -> {
          webDriverHelpers.selectFromComboboxEqual(RELATIONSHIP_WITH_CASE_COMBOBOX, option);
        });

    When(
        "I filter by last collected from UI specific Contact uuid",
        () -> {
          String contactUuid = aContact.getUuid();
          By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, contactUuid));
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(uuidLocator);
        });

    When(
        "I filter for SAMPLE TOKEN in Contacts Directory",
        () -> {
          webDriverHelpers.fillInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, "SAMPLE TOKEN");
          webDriverHelpers.clickOnWebElementBySelector(
              CONTACT_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    And(
        "I apply {string} to combobox on Contact Directory Page",
        (String caseParameter) ->
            webDriverHelpers.selectFromCombobox(CASE_DISPLAY_FILTER_COMBOBOX, caseParameter));

    When(
        "I prepare detailed contact CSV with {string} as a disease and {string} as a present condition",
        (String disease, String pCondition) -> {
          long timestamp = System.currentTimeMillis();
          detailedContactCSVFile = "./uploads/sormas_contacts_sordev_10361.csv";
          Map<String, Object> reader;
          reader = parseCSVintoPOJODetailedContactCSV(detailedContactCSVFile);
          contactCSVName = "detailedContactCSVTestFile" + timestamp + ".csv";
          writeCSVFromMapDetailedContact(reader, contactCSVName, disease, pCondition);
        });

    When(
        "I select created CSV file with detailed contact",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FILE_PICKER);
          webDriverHelpers.sendFile(FILE_PICKER, userDirPath + "/uploads/" + contactCSVName);
        });

    When(
        "I click on the {string} button from the Import Detailed Contact popup",
        (String buttonName) -> {
          webDriverHelpers.clickWebElementByText(IMPORT_POPUP_BUTTON, buttonName);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_CASE_POPUP_HEADER, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
        });

    When(
        "I search for created detailed contact by first and last name of the person",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_LIKE_SEARCH_INPUT, firstName + " " + lastName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if csv file for detailed contact is imported successfully",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(IMPORT_SUCCESSFUL_FACILITY_IMPORT_CSV, 10);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CLOSE);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_DETAILED_EXPORT_POPUP);
        });

    When(
        "I check if disease is set for {string} in Contact Edit Directory",
        (String disease) -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISEASE_COMBOBOX);
          webDriverHelpers.scrollToElement(DISEASE_INPUT);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(DISEASE_INPUT), disease, "Incorrect disease");
          softly.assertAll();
        });

    When(
        "I delete created csv file for detailed contact import",
        () -> {
          Path path = Paths.get(userDirPath + "/uploads/" + contactCSVName);
          Files.delete(path);
        });

    Then(
        "I set contact vaccination status filter to ([^\"]*)",
        (String vaccinationStatus) ->
            webDriverHelpers.selectFromCombobox(
                CONTACT_VACCINATION_STATUS_FILTER_COMBOBOX, vaccinationStatus));

    And(
        "I apply contact filters",
        () -> webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON));

    Then(
        "I check that created Contact is visible with ([^\"]*) status",
        (String vaccinationStatus) -> {
          String contactUuid = apiState.getCreatedContact().getUuid();
          Assert.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(getContactsByUUID(contactUuid), 5),
              "There is no contact with expected uuid");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getVaccinationStatusContactsByText(vaccinationStatus));
          Assert.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(
                  getVaccinationStatusContactsByText(vaccinationStatus), 5),
              "There is no contact with expected status");
        });

    When(
        "I confirm when a pop-up appears asking user about creating a Case from it in DE",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              CREATE_CASE_FROM_POSITIVE_TEST_RESULT_HEADER_DE);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
        });

    And(
        "^I click to CONFIRM FILTERS on Merge Duplicate Contact page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(By.id("actionConfirmFilters"));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(500);
        });
  }

  public Map<String, Object> parseCSVintoPOJODetailedContactCSV(String fileName) {

    List<String[]> r = null;
    String[] values = new String[] {};
    ObjectMapper mapper = new ObjectMapper();
    DetailedContactCSV detailedContactCSV = new DetailedContactCSV();

    try {

      CSVReader headerReader = new CSVReader(new FileReader(fileName));
      String[] nextLine;
      nextLine = headerReader.readNext();
      detailedContactHeader1 = nextLine;
      nextLine = headerReader.readNext();
      detailedContactHeader2 = nextLine;
      nextLine = headerReader.readNext();
      detailedContactHeader3 = nextLine;
    } catch (IOException e) {
      log.error("IOException csvReader: ", e);
    } catch (CsvException e) {
      log.error("CsvException header reader: ", e);
    }
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build(); // custom separator
    // Convert POJO to Map
    Map<String, Object> detailedContactPojo =
        mapper.convertValue(detailedContactCSV, new TypeReference<Map<String, Object>>() {});

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

    String[] keys = detailedContactPojo.keySet().toArray(new String[0]);

    try {
      for (int i = 0; i < keys.length; i++) {
        detailedContactPojo.put(keys[i], values[i]);
      }

    } catch (NullPointerException e) {
      log.error("Null pointer exception csvReader: ", e);
    }
    return detailedContactPojo;
  }

  @SneakyThrows
  public static void writeCSVFromMapDetailedContact(
      Map<String, Object> detailedContact,
      String createdFileName,
      String disease,
      String pCondition) {
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
      contactUUIDFromCSV = generateShortUUID();
      String personUUID = generateShortUUID();
      int lRandom = ThreadLocalRandom.current().nextInt(8999999, 9999999 + 1);
      detailedContact.computeIfPresent("uuid", (k, v) -> v = contactUUIDFromCSV);
      detailedContact.computeIfPresent("personUuid", (k, v) -> v = personUUID);
      detailedContact.computeIfPresent("personFirstName", (k, v) -> v = firstName);
      detailedContact.computeIfPresent("personLastName", (k, v) -> v = lastName);
      detailedContact.computeIfPresent("disease", (k, v) -> v = disease);
      detailedContact.computeIfPresent("personPresentCondition", (k, v) -> v = pCondition);
      String[] rowdata = detailedContact.values().toArray(new String[0]);
      ArrayList<String> sArray = new ArrayList<String>();
      for (String s : rowdata) {
        sArray.add("\"" + s + "\"");
      }
      detailedContactHeader1[0] = "\"" + detailedContactHeader1[0] + "\"";
      data.add(detailedContactHeader1);
      data.add(detailedContactHeader2);
      data.add(detailedContactHeader3);
      data.add(sArray.toArray(new String[0]));
      writer.writeAll(data);
      writer.close();
    } catch (IOException e) {
      log.error("IOException csvWriter: ", e);
    }
  }

  private void openContactFromResultsByUUID(String uuid) {
    By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, uuid));
    webDriverHelpers.clickOnWebElementBySelector((uuidLocator));
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 40);
  }

  private void fillLocation(Exposure exposureData) {
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.selectFromCombobox(EXPOSURE_REGION_COMBOBOX, exposureData.getExposureRegion());
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, exposureData.getDistrict());
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, exposureData.getCommunity());
    webDriverHelpers.fillInWebElement(STREET_INPUT, exposureData.getStreet());
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, exposureData.getHouseNumber());
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION_INPUT, exposureData.getAdditionalInformation());
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, exposureData.getPostalCode());
    webDriverHelpers.fillInWebElement(CITY_INPUT, exposureData.getCity());
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, exposureData.getAreaType());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE_INPUT, exposureData.getLatitude());
    webDriverHelpers.fillInWebElement(GPS_LONGITUDE_INPUT, exposureData.getLongitude());
    webDriverHelpers.fillInWebElement(GPS_ACCURACY_INPUT, exposureData.getLatLonAccuracy());
  }

  private void fillLocationDE(Exposure exposureData) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.selectFromCombobox(EXPOSURE_REGION_COMBOBOX, exposureData.getExposureRegion());
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, exposureData.getDistrict());
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, exposureData.getCommunity());
    webDriverHelpers.fillInWebElement(STREET_INPUT, exposureData.getStreet());
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, exposureData.getHouseNumber());
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION_INPUT, exposureData.getAdditionalInformation());
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, exposureData.getPostalCode());
    webDriverHelpers.fillInWebElement(CITY_INPUT, exposureData.getCity());
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, exposureData.getAreaType());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE_INPUT, exposureData.getLatitude());
    webDriverHelpers.fillInWebElement(GPS_LONGITUDE_INPUT, exposureData.getLongitude());
    webDriverHelpers.fillInWebElement(GPS_ACCURACY_INPUT, exposureData.getLatLonAccuracy());
  }

  private Exposure collectLocationData() {
    return Exposure.builder()
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .exposureRegion(webDriverHelpers.getValueFromCombobox(EXPOSURE_REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .additionalInformation(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromCombobox(AREA_TYPE_COMBOBOX))
        .latitude(webDriverHelpers.getValueFromWebElement(GPS_LATITUDE_INPUT))
        .longitude(webDriverHelpers.getValueFromWebElement(GPS_LONGITUDE_INPUT))
        .latLonAccuracy(webDriverHelpers.getValueFromWebElement(GPS_ACCURACY_INPUT))
        .build();
  }

  private void fillExposure(Exposure exposureData) {
    webDriverHelpers.fillInWebElement(
        START_OF_EXPOSURE_INPUT, formatter.format(exposureData.getStartOfExposure()));
    webDriverHelpers.fillInWebElement(
        END_OF_EXPOSURE_INPUT, formatter.format(exposureData.getEndOfExposure()));
    webDriverHelpers.fillInWebElement(
        EXPOSURE_DESCRIPTION_INPUT, exposureData.getExposureDescription());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_ACTIVITY_COMBOBOX, exposureData.getTypeOfActivity().getActivity());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_DETAILS_ROLE_COMBOBOX, exposureData.getExposureDetailsRole().getRole());
    webDriverHelpers.clickWebElementByText(
        RISK_AREA_OPTIONS, exposureData.getRiskArea().toString());
    webDriverHelpers.clickWebElementByText(INDOORS_OPTIONS, exposureData.getIndoors().toString());
    webDriverHelpers.clickWebElementByText(OUTDOORS_OPTIONS, exposureData.getOutdoors().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_MASK_OPTIONS, exposureData.getWearingMask().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_PPE_OPTIONS, exposureData.getWearingPpe().toString());
    webDriverHelpers.clickWebElementByText(
        OTHER_PROTECTIVE_MEASURES_OPTIONS, exposureData.getOtherProtectiveMeasures().toString());
    webDriverHelpers.clickWebElementByText(
        SHORT_DISTANCE_OPTIONS, exposureData.getShortDistance().toString());
    webDriverHelpers.clickWebElementByText(
        LONG_FACE_TO_FACE_CONTACT_OPTIONS, exposureData.getLongFaceToFaceContact().toString());
    webDriverHelpers.clickWebElementByText(
        PERCUTANEOUS_OPTIONS, exposureData.getPercutaneous().toString());
    webDriverHelpers.clickWebElementByText(
        CONTACT_TO_BODY_FLUIDS_OPTONS, exposureData.getContactToBodyFluids().toString());
    webDriverHelpers.clickWebElementByText(
        HANDLING_SAMPLES_OPTIONS, exposureData.getHandlingSamples().toString());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_PLACE_COMBOBOX, exposureData.getTypeOfPlace().getUiValue());
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
  }

  private Exposure collectExposureData() {
    return Exposure.builder()
        .startOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE_INPUT), formatter))
        .endOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE_INPUT), formatter))
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

  private EpidemiologicalData collectSpecificData() {
    return EpidemiologicalData.builder()
        .residingAreaWithRisk(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS)))
        .largeOutbreaksArea(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS)))
        .build();
  }

  private String getContactIDByIndex(int index) {
    return webDriverHelpers.getTextFromWebElement(getContactIDPathByIndex(index));
  }
}
