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

import static org.sormas.e2etests.constants.api.Endpoints.CONTACTS_PATH;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ACTION_OKAY;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONFIRM_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_CASE_ID_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.WARNING_CASE_NOT_SHARED_SHARE_POPUP_DE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ACTION_CONFIRM_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_DOCUMENT_EMPTY_TEXT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_UPLOADED_TEST_FILE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONTACT_CASE_POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DELETE_LAST_UPDATED_CASE_DOCUMENT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DOWNLOAD_LAST_UPDATED_CASE_DOCUMENT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NEW_DOCUMENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.START_DATA_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BUTTONS_IN_VACCINATIONS_LOCATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_TEMPLATES;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_TEMPLATES_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_TEMPLATES_POPUP_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISCARD_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EPID_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXPECTED_FOLLOWUP_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXPECTED_FOLLOWUP_POPUP_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.FOLLOW_UP_COMMENT_FIELD;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERATED_DOCUMENT_NAME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERATED_DOCUMENT_NAME_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UPLOAD_DOCUMENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_INFO_ICON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_INFO_POPUP_TEXT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_VACCINATION_NAME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.getVaccinationCardVaccinationNameByIndex;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.CASE_OR_EVENT_INFORMATION_CONTACT_TEXT_AREA;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.EXTERNAL_TOKEN_CONTACT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RELATIONSHIP_WITH_CASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.ACTION_CONFIRM;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_RESULTS_UUID_LOCATOR;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.MULTIPLE_OPTIONS_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.CONTACT_PERSON_TAB;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_IMPORT_TRAVEL_ENTRY_POPUP;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON_FOR_POPUP_WINDOWS;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.CONFIRM_DEARCHIVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.DEARCHIVE_REASON_TEXT_AREA;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.GENERAL_SEARCH_INPUT;

import cucumber.api.java8.En;
import io.restassured.http.Method;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.pojo.web.QuarantineOrder;
import org.sormas.e2etests.entities.services.ContactDocumentService;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.enums.TaskTypeValues;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.pages.application.cases.SymptomsTabPage;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.vaccination.CreateNewVaccinationSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class EditContactSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Contact createdContact;
  public static Contact collectedContact;
  public static QuarantineOrder aQuarantineOrder;
  public static Contact editedContact;
  public static Contact aContact;
  public static final String userDirPath = System.getProperty("user.dir");
  public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter formatterDE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private static String currentUrl;
  private static String contactUUID;
  public static LocalDate lastContactDateForFollowUp;
  private final RestAssuredClient restAssuredClient;

  @Inject
  public EditContactSteps(
      WebDriverHelpers webDriverHelpers,
      ContactService contactService,
      SoftAssert softly,
      ApiState apiState,
      AssertHelpers assertHelpers,
      ContactDocumentService contactDocumentService,
      RestAssuredClient restAssuredClient) {
    this.webDriverHelpers = webDriverHelpers;
    this.restAssuredClient = restAssuredClient;

    When(
        "I search and open  last created contact in Contact directory page",
        () -> {
          String contactUUID = collectedContact.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, contactUUID);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, contactUUID));
          webDriverHelpers.clickOnWebElementBySelector((uuidLocator));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EditContactPage.UUID_INPUT);
        });

    When(
        "I check the created data for DE version is correctly displayed on Edit Contact page",
        () -> {
          collectedContact = collectContactDataDE();
          createdContact = CreateNewContactSteps.contact;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "returningTraveler",
                  "reportDate",
                  "diseaseOfSourceCase",
                  "caseIdInExternalSystem",
                  "dateOfLastContact",
                  "caseOrEventInformation",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "additionalInformationOnContactType",
                  "typeOfContact",
                  "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });

    And(
        "I click on checkbox to upload generated document to entities in Create Quarantine Order form in Contact directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX));
    When(
        "I check if generated document based on {string} appeared in Documents tab for UI created contact in Edit Contact directory",
        (String name) -> {
          String uuid = collectedContact.getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME)),
              120);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab for UI created contact in Edit Contact directory for DE",
        (String name) -> {
          String uuid = collectedContact.getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME_DE)),
              10);
        });
    When(
        "I check if generated document for Contact based on {string} was downloaded properly",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          String pathToFile = uuid.substring(0, 6).toUpperCase() + "-" + name;
          FilesHelper.waitForFileToDownload(pathToFile, 50);
        });
    When(
        "I check if generated document for contact based on {string} contains all required fields",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          String pathToFile =
              userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name;
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
        "I check that number of added Vaccinations is {int} on Edit Contact Page",
        (Integer expected) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(BUTTONS_IN_VACCINATIONS_LOCATION),
                        (int) expected,
                        "Number of vaccinations is different than expected")));
    When(
        "I close vaccination form in Edit Contact directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_POPUP));
    When(
        "I check the created data is correctly displayed on Edit Contact page",
        () -> {
          collectedContact = collectContactData();
          createdContact = CreateNewContactSteps.contact;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "returningTraveler",
                  "reportDate",
                  "diseaseOfSourceCase",
                  "caseIdInExternalSystem",
                  "dateOfLastContact",
                  "caseOrEventInformation",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  // field no longer available
                  //                  "additionalInformationOnContactType",
                  "typeOfContact",
                  // field no longer available
                  //                  "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });
    When(
        "I check the created data for duplicated contact is correctly displayed on Edit Contact page",
        () -> {
          collectedContact = collectContactData();
          createdContact = CreateNewContactSteps.duplicatedContact;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "returningTraveler",
                  "reportDate",
                  "diseaseOfSourceCase",
                  "caseIdInExternalSystem",
                  "dateOfLastContact",
                  "caseOrEventInformation",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  // field no longer available
                  //                  "additionalInformationOnContactType",
                  "typeOfContact",
                  // field no longer available
                  //                  "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });

    When(
        "I check the created data for existing person is correctly displayed on Edit Contact page",
        () -> {
          collectedContact = collectContactData();
          createdContact =
              CreateNewContactSteps.contact.toBuilder()
                  .firstName(apiState.getLastCreatedPerson().getFirstName())
                  .lastName(apiState.getLastCreatedPerson().getLastName())
                  .build();
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "returningTraveler",
                  "reportDate",
                  "diseaseOfSourceCase",
                  "caseIdInExternalSystem",
                  "dateOfLastContact",
                  "caseOrEventInformation",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  // field no longer available
                  //                  "additionalInformationOnContactType",
                  "typeOfContact",
                  // field no longer available
                  //                  "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });

    When(
        "I check the created data for existing person is correctly displayed on Edit Contact page based on Case",
        () -> {
          collectedContact = collectContactDataFromCase();
          createdContact =
              CreateNewContactSteps.contact.toBuilder()
                  .firstName(apiState.getLastCreatedPerson().getFirstName())
                  .lastName(apiState.getLastCreatedPerson().getLastName())
                  .build();

          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "returningTraveler",
                  "reportDate",
                  "dateOfLastContact",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  // field no longer available
                  // "additionalInformationOnContactType",
                  "typeOfContact",
                  // field no longer available
                  // "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });

    When(
        "I set Vaccination status to {string} on Edit Contact page",
        (String vaccination) -> {
          webDriverHelpers.selectFromCombobox(
              VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX, vaccination);
        });
    When(
        "I check the created data is correctly displayed on Edit Contact page related with CHOSEN SOURCE CASE",
        () -> {
          collectedContact = collectContactDataRelatedWithChooseSourceCase();
          createdContact = CreateNewContactSteps.contact;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "returningTraveler",
                  "reportDate",
                  "diseaseOfSourceCase",
                  "dateOfLastContact",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  // field no longer available
                  //                  "additionalInformationOnContactType",
                  "typeOfContact",
                  // field no longer available
                  //                  "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });

    When(
        "I check the created data is correctly displayed on Edit Contact page for DE version",
        () -> {
          collectedContact = collectContactDataDE();
          createdContact = CreateNewContactSteps.contact;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "returningTraveler",
                  "reportDate",
                  "diseaseOfSourceCase",
                  "caseIdInExternalSystem",
                  "dateOfLastContact",
                  "caseOrEventInformation",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "additionalInformationOnContactType",
                  "typeOfContact",
                  "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });

    When(
        "I search created task by Contact first and last name",
        () -> {
          createdContact = CreateNewContactSteps.contact;
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GENERAL_SEARCH_INPUT, 50);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT,
              createdContact.getFirstName() + " " + createdContact.getLastName());
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "I click to accept potential duplicate in Shares Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM));
    When(
        "^I click on ([^\"]*) radio button Contact Person tab$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(
                CONTACT_CLASSIFICATION_RADIO_BUTTON, buttonName));
    When(
        "I check the edited data is correctly displayed on Edit Contact page after editing",
        () -> {
          collectedContact = collectContactDataAfterEdit();
          ComparisonHelper.compareEqualFieldsOfEntities(
              editedContact,
              collectedContact,
              List.of(
                  "firstName",
                  "lastName",
                  "dateOfBirth",
                  "sex",
                  "primaryEmailAddress",
                  "primaryPhoneNumber",
                  "returningTraveler",
                  "reportDate",
                  "diseaseOfSourceCase",
                  "caseIdInExternalSystem",
                  "dateOfLastContact",
                  "caseOrEventInformation",
                  "responsibleDistrict",
                  "responsibleRegion",
                  "responsibleCommunity",
                  "typeOfContact",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace",
                  "uuid",
                  "classification",
                  "status",
                  "multiDay",
                  "dateOfFirstContact",
                  "externalToken",
                  "category",
                  "quarantine",
                  "highPriority",
                  "diabetes",
                  "liverDisease",
                  "malignancy",
                  "chronicPulmonaryDisease",
                  "renalDisease",
                  "chronicNeurologicalNeuromuscularDisease",
                  "cardiovascularDiseaseIncludingHypertension",
                  "additionalRelevantPreexistingConditions",
                  "vaccinationStatusForThisDisease",
                  "cancelFollowUp",
                  "overwriteFollowUp",
                  "dateOfFollowUpUntil",
                  "followUpStatusComment",
                  "responsibleContactOfficer",
                  "generalComment"));
        });

    When(
        "I open Contact Person tab",
        () -> {
          webDriverHelpers.scrollToElement(CONTACT_PERSON_TAB);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_PERSON_TAB);
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I delete the contact",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
          webDriverHelpers.selectFromCombobox(
              DELETE_CONTACT_REASON_POPUP, "Deletion request by another authority");
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
        });

    When(
        "I open the last created UI Contact",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_CASE_ID_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "I check that the value selected from Disease combobox is {string} on Edit Contact page",
        (String disease) -> {
          String chosenDisease = webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX);
          softly.assertEquals(chosenDisease, disease, "The disease is other then expected");
          softly.assertAll();
        });

    When(
        "^I change all contact fields and save$",
        () -> {
          editedContact = contactService.buildEditContact();
          selectContactClassification(editedContact.getClassification());
          selectMultiDayContact(editedContact.getMultiDay());
          fillDateOfFirstContact(editedContact.getDateOfFirstContact());
          selectDiseaseOfSourceCase(editedContact.getDiseaseOfSourceCase());
          // field no longer available
          //          fillExternalId(editedContact.getExternalId());
          fillDateOfLastContact(editedContact.getDateOfLastContact());
          fillExternalToken(editedContact.getExternalToken());
          fillReportDate(editedContact.getReportDate());
          // field no longer available
          //          selectReportingDistrict(editedContact.getReportingDistrict());
          selectResponsibleRegion(editedContact.getResponsibleRegion());
          selectResponsibleDistrict(editedContact.getResponsibleDistrict());
          selectResponsibleCommunity(editedContact.getResponsibleCommunity());
          selectReturningTraveler(editedContact.getReturningTraveler());
          fillCaseIdExternalSystem(editedContact.getCaseIdInExternalSystem());
          fillCaseOrEventInformation(editedContact.getCaseOrEventInformation());
          // field no longer available
          //          selectIdentificationSource(editedContact.getIdentificationSource());
          // field no longer available
          //          fillIdentificationSource(editedContact.getIdentificationSourceDetails());
          selectContactType(editedContact.getTypeOfContact());
          // field no longer available
          //          fillAdditionalInformationOnContactType(
          //              editedContact.getAdditionalInformationOnContactType());
          // field no longer available
          //          selectContactCategory(editedContact.getContactCategory());
          selectRelationShipWithCase(editedContact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(editedContact.getDescriptionOfHowContactTookPlace());
          // field no longer available
          //          selectProhibitionToWork(editedContact.getProhibitionToWork());
          // field no longer available
          //
          // selectHomeBasedQuarantinePossible(editedContact.getHomeBasedQuarantinePossible());
          selectQuarantine(editedContact.getQuarantine());
          selectHighPriority(editedContact.getHighPriority());
          selectPreexistingConditionDiabetes(editedContact.getDiabetes());
          // field no longer available
          //
          // selectPreexistingConditionHiv(editedContact.getImmunodeficiencyIncludingHiv());
          selectPreexistingConditionLiver(editedContact.getLiverDisease());
          selectPreexistingConditionMalignancy(editedContact.getMalignancy());
          selectPreexistingConditionChronicPulmonary(editedContact.getChronicPulmonaryDisease());
          selectPreexistingConditionRenal(editedContact.getRenalDisease());
          selectPreexistingConditionNeurologic(
              editedContact.getChronicNeurologicalNeuromuscularDisease());
          selectPreexistingConditionCardiovascular(
              editedContact.getCardiovascularDiseaseIncludingHypertension());
          fillAdditionalRelevantPreexistingConditions(
              editedContact.getAdditionalRelevantPreexistingConditions());
          selectVaccinationStatusForThisDisease(editedContact.getVaccinationStatusForThisDisease());
          // field no longer available
          //          selectImmunosuppressiveTherapy(editedContact.getImmunosuppressiveTherapy());
          // field no longer available
          //          selectActiveInCare(editedContact.getActiveInCare());
          clickCancelFollowUpButton();
          // TODO enable it back once 6803 is fixed
          // selectOverwriteFollowUp(editedContact.getOverwriteFollowUp());
          // fillDateOfFollowUpUntil(editedContact.getDateOfFollowUpUntil());
          fillFollowUpStatusComment(editedContact.getFollowUpStatusComment());
          selectResponsibleContactOfficer(editedContact.getResponsibleContactOfficer());
          fillGeneralComment(editedContact.getGeneralComment());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_EDIT_BUTTON);
        });

    When(
        "I click on the Create button from Contact Document Templates",
        () -> {
          webDriverHelpers.scrollToElement(EditContactPage.CREATE_DOCUMENT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EditContactPage.CREATE_DOCUMENT_BUTTON);
        });

    When(
        "I create and download a contact document from template",
        () -> {
          aQuarantineOrder = contactDocumentService.buildQuarantineOrder();
          aQuarantineOrder = aQuarantineOrder.toBuilder().build();
          selectQuarantineOrderTemplate(aQuarantineOrder.getDocumentTemplate());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXTRA_COMMENT_INPUT);
          fillExtraComment(aQuarantineOrder.getExtraComment());
          webDriverHelpers.clickOnWebElementBySelector(
              EditContactPage.CREATE_QUARANTINE_ORDER_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for download
        });

    And(
        "I verify that the contact document is downloaded and correctly named",
        () -> {
          String uuid = webDriverHelpers.getValueFromWebElement(EditContactPage.UUID_INPUT);
          Path path =
              Paths.get(
                  userDirPath
                      + "/downloads/"
                      + uuid.substring(0, 6).toUpperCase()
                      + "-"
                      + aQuarantineOrder.getDocumentTemplate());
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(
                      Files.exists(path),
                      "Contact document was not downloaded. Path used for check: "
                          + path.toAbsolutePath()),
              120);
        });
    And(
        "I click on Create button in Document Templates box in Edit Contact directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_TEMPLATES));
    And(
        "I click on Create button in Document Templates box for DE",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_TEMPLATES_DE));
    And(
        "I click on Create button in Document Templates popup for DE",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_TEMPLATES_POPUP_DE));
    And(
        "I click on checkbox to upload generated document to entity in Create Quarantine Order form in Edit Contact directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_CHECKBOX));
    When(
        "I select {string} Quarantine Order in Create Quarantine Order form in Edit Contact directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
          TimeUnit.SECONDS.sleep(3);
        });
    When(
        "I check if downloaded file is correct for {string} Quarantine Order in Edit Contact directory",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          String filePath = uuid.substring(0, 6).toUpperCase() + "-" + name;
          FilesHelper.waitForFileToDownload(filePath, 50);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab in Edit Contact directory",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME)),
              120);
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab in Edit Contact directory for DE",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME_DE)),
              10);
        });
    When(
        "I delete downloaded file created from {string} Document Template for Contact",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          String filePath = uuid.substring(0, 6).toUpperCase() + "-" + name;
          FilesHelper.deleteFile(filePath);
        });
    When(
        "I select {string} template in Document Template form",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
        });
    When(
        "^I click on CONFIRMED CONTACT radio button Contact Data tab for DE version$",
        () ->
            webDriverHelpers.clickWebElementByText(
                CONTACT_CLASSIFICATION_RADIO_BUTTON, "BEST\u00C4TIGTER KONTAKT"));

    When(
        "I select CONFIRMED CONTACT radio button on Contact Data tab for DE version",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIRMED_CONTACT_DE_BUTTON));

    When(
        "^I click on CONFIRMED CONTACT radio button Contact Data tab$",
        () ->
            webDriverHelpers.clickWebElementByText(
                CONTACT_CLASSIFICATION_RADIO_BUTTON, "CONFIRMED CONTACT"));

    When(
        "^I click SAVE button on Edit Contact Page$",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_EDIT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_SAVED_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });
    When(
        "^I click Link Event button on Edit Contact Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LINK_EVENT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_BUTTON);
        });
    When(
        "I select {int} event in Link Event popup and create and Event Participant",
        (Integer index) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(getByEventIndex(index));
          webDriverHelpers.clickOnWebElementBySelector(getByEventIndex(index));
          webDriverHelpers.waitForRowToBeSelected(getByEventIndex(index));
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_CONFIRM_BUTTON);
        });
    When(
        "I click Save in Add Event Participant form on Edit Contact Page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ADD_A_PARTICIPANT_HEADER);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "^I click Create Case from Contact button$",
        () -> {
          webDriverHelpers.scrollToElement(CREATE_CASE_FROM_CONTACT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_CASE_FROM_CONTACT_BUTTON);
        });
    When(
        "^I click Yes, for some in conversion to case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONVERT_SOME);
        });
    When(
        "^I click Yes, for all in conversion to case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_POPUP_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    When(
        "^I click No in conversion to case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_POPUP_BUTTON);
        });
    When(
        "^I click on checkbox to select all available options$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "^I check if there are entities assigned to new created case from contact$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LINKED_EVENT_INDICATIOR);
        });
    When(
        "^I check if there are no entities assigned to new created case from contact$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          softly.assertEquals(
              false, webDriverHelpers.isElementVisibleWithTimeout(LINKED_EVENT_INDICATIOR, 10));
          softly.assertAll();
        });

    When(
        "I check if Vaccination Status is set to {string} on Edit Contact page",
        (String expected) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditContactPage.UUID_INPUT);
          String vaccinationStatus =
              webDriverHelpers.getValueFromWebElement(VACCINATION_STATUS_INPUT);
          softly.assertEquals(
              expected, vaccinationStatus, "Vaccination status is different than expected");
          softly.assertAll();
        });
    When(
        "I check the created data for complex contact is correctly displayed on Edit Contact page",
        () -> {
          collectedContact = collectComplexContactData();
          createdContact = CreateNewContactSteps.contact;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedContact,
              createdContact,
              List.of(
                  "firstName",
                  "lastName",
                  "reportDate",
                  "dateOfLastContact",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  //                  "additionalInformationOnContactType",
                  //                  "contactCategory",
                  "relationshipWithCase",
                  "descriptionOfHowContactTookPlace"));
        });

    When(
        "I collect the contact person UUID displayed on Edit contact page",
        () -> aContact = collectContactPersonUuid());

    When(
        "I click on the Archive contact button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_CONTACT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CONTACT_BUTTON);
        });

    When(
        "I check if Archive contact popup is displayed correctly",
        () -> {
          String expectedString = "Archive contact";
          String actualString = webDriverHelpers.getTextFromWebElement(ARCHIVE_POPUP_WINDOW_HEADER);
          softly.assertEquals(actualString, expectedString, "Unexpected popup title displayed");
          softly.assertAll();
        });

    When(
        "I check if Archive button changed name to ([^\"]*)",
        (String actualLabel) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ARCHIVE_CONTACT_BUTTON_LABEL);
          webDriverHelpers.scrollToElement(ARCHIVE_CONTACT_BUTTON_LABEL);
          String expectedLabel =
              webDriverHelpers.getTextFromWebElement(ARCHIVE_CONTACT_BUTTON_LABEL);
          softly.assertEquals(
              actualLabel, expectedLabel, "Unexpected archive button label displayed");
          softly.assertAll();
        });

    When(
        "I check the end of processing date in the archive popup",
        () -> {
          String endOfProcessingDate;
          endOfProcessingDate =
              webDriverHelpers.getValueFromWebElement(END_OF_PROCESSING_DATE_POPUP_INPUT);
          softly.assertEquals(
              endOfProcessingDate,
              LocalDate.now().format(formatter),
              "End of processing date is invalid");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for response after confirm
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I filter by last created contact via api",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              MULTIPLE_OPTIONS_SEARCH_INPUT, apiState.getCreatedCase().getUuid().substring(0, 6));
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on ([^\"]*) button from New document in contact tab",
        (String buttonName) -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_DOCUMENT_BUTTON);
          webDriverHelpers.clickWebElementByText(START_DATA_IMPORT_BUTTON, buttonName);
        });

    When(
        "I upload ([^\"]*) file to the contact",
        (String fileType) -> {
          webDriverHelpers.sendFile(
              FILE_PICKER, userDirPath + "/uploads/testContact_" + fileType + "." + fileType);
          TimeUnit.SECONDS.sleep(2); // wait for upload file
        });

    When(
        "I check if ([^\"]*) file is available in contact documents",
        (String fileType) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(CASE_UPLOADED_TEST_FILE, fileType)), 5);
        });

    When(
        "I download last updated document file from contact tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DOWNLOAD_LAST_UPDATED_CASE_DOCUMENT);
          TimeUnit.SECONDS.sleep(3); // wait for download
        });

    When(
        "I delete last uploaded document file from contact tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DELETE_LAST_UPDATED_CASE_DOCUMENT);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_POPUP_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if last uploaded file was deleted from document files in contact tab",
        () -> {
          webDriverHelpers.checkWebElementContainsText(
              CASE_DOCUMENT_EMPTY_TEXT, "There are no documents for this Contact");
        });

    When(
        "I check if ([^\"]*) file for contact is downloaded correctly",
        (String fileType) -> {
          String filePath = "testContact_" + fileType + "." + fileType;
          FilesHelper.waitForFileToDownload(filePath, 50);
          FilesHelper.deleteFile(filePath);
        });

    When(
        "I navigate to follow-up visits tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(FOLLOW_UP_VISITS));

    And(
        "^I check if New task form is displayed correctly$",
        () -> {
          String expectedString = "Create new task";
          String actualString = webDriverHelpers.getTextFromWebElement(CREATE_NEW_TASK_FORM_HEADER);
          softly.assertEquals(actualString, expectedString, "Unexpected popup title displayed");
          softly.assertAll();
        });

    And(
        "^I check that values listed in the task type combobox are correct$",
        () -> {
          for (TaskTypeValues value : TaskTypeValues.values()) {
            webDriverHelpers.selectFromCombobox(
                TASK_TYPE_COMBOBOX, TaskTypeValues.getValueFor(value.toString()));
          }
        });

    And(
        "^I clear Due Date field in the New task form$",
        () -> webDriverHelpers.clearWebElement(NEW_TASK_DUE_DATE));

    Then(
        "^I check that all required fields are mandatory in the New task form$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(INPUT_DATA_ERROR_POPUP);
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Task type");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Due date");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Assigned to");
        });

    And(
        "^I click SAVE button on New Task form$",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_NEW_TASK_BUTTON));

    And(
        "^I check that required fields are marked as mandatory$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(TASK_TYPE_TITLE);
          webDriverHelpers.checkWebElementContainsText(TASK_TYPE_TITLE, "*");
          webDriverHelpers.checkWebElementContainsText(DUE_DATE_TITLE, "*");
          webDriverHelpers.checkWebElementContainsText(ASSIGNED_TO_TITLE, "*");
        });

    When(
        "^I close input data error popup in Contact Directory$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(INPUT_DATA_ERROR_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(INPUT_DATA_ERROR_POPUP);
        });

    And(
        "^I choose Other task as described in comments option from task type combobox in the New task form$",
        () ->
            webDriverHelpers.selectFromCombobox(
                TASK_TYPE_COMBOBOX, "other task as described in comments"));

    Then(
        "^I check that Comments on task field is mandatory in the New task form$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(COMMENTS_ON_TASK_TITLE);
          webDriverHelpers.checkWebElementContainsText(COMMENTS_ON_TASK_TITLE, "*");
        });

    When(
        "I check if collected contact UUID is the same in opened contact",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(UUID_INPUT),
              aContact.getUuid(),
              "UUIDs are not equal");
          softly.assertAll();
        });

    When(
        "^I check if relationship with case is set to ([^\"]*)$",
        (String option) -> {
          webDriverHelpers.scrollToElement(RELATIONSHIP_WITH_CASE_INPUT);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(RELATIONSHIP_WITH_CASE_INPUT),
              option,
              "Relationships with case are not equal");
          softly.assertAll();
        });
    When(
        "I check that text appearing in hover over Expected Follow-up is based on Report date on Edit Contact Page",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPECTED_FOLLOWUP_LABEL);
          webDriverHelpers.hoverToElement(EXPECTED_FOLLOWUP_LABEL);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(EXPECTED_FOLLOWUP_POPUP_TEXT);
          softly.assertEquals(
              displayedText,
              "Das erwartete Nachverfolgungs bis Datum f\u00FCr diesen Kontakt basiert auf seinem Meldedatum ("
                  + apiState
                      .getCreatedContact()
                      .getReportDateTime()
                      .toInstant()
                      .atZone(ZoneId.systemDefault())
                      .toLocalDate()
                      .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                  + ")",
              "Message is incorrect");
          softly.assertAll();
        });
    When(
        "I check that text appearing in hover over Expected Follow-up is based on Last Contact date on Edit Contact Page",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPECTED_FOLLOWUP_LABEL);
          webDriverHelpers.hoverToElement(EXPECTED_FOLLOWUP_LABEL);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(EXPECTED_FOLLOWUP_POPUP_TEXT);
          softly.assertEquals(
              displayedText,
              "Das erwartete Nachverfolgungs bis Datum f\u00FCr diesen Kontakt basiert auf seinem Datum des letzten Kontakts ("
                  + lastContactDateForFollowUp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                  + ")",
              "Message is incorrect");
          softly.assertAll();
        });

    When("I copy url of current contact", () -> currentUrl = webDriverHelpers.returnURL());

    When(
        "I click on Delete button from contact",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
        });

    When("I back to deleted contact by url", () -> webDriverHelpers.accessWebSite(currentUrl));
    When("I back to contact by url", () -> webDriverHelpers.accessWebSite(currentUrl));

    When(
        "I check if External token input on case edit page is disabled",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(EXTERNAL_TOKEN_CONTACT_INPUT),
              "External token input is enabled");
          softly.assertAll();
        });

    When(
        "I check if Case or event information text area on case edit page is disabled",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(CASE_OR_EVENT_INFORMATION_CONTACT_TEXT_AREA),
              "Case or event information text area is enabled");
          softly.assertAll();
        });

    And(
        "I set contact vaccination status to ([^\"]*)",
        (String vaccinationStatus) -> {
          webDriverHelpers.selectFromCombobox(VACCINATION_STATUS_COMBOBOX, vaccinationStatus);
          webDriverHelpers.clickOnWebElementBySelector(COMMIT_BUTTON);
        });

    And(
        "^I click on the NEW IMMUNIZATION button in Edit contact$",
        () -> {
          webDriverHelpers.scrollToElement(NEW_IMMUNIZATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_IMMUNIZATION_BUTTON);
        });

    When(
        "I copy uuid of current contact",
        () -> {
          webDriverHelpers.scrollToElement(EditContactPage.UUID_INPUT);
          contactUUID = webDriverHelpers.getValueFromWebElement(EditContactPage.UUID_INPUT);
        });

    When(
        "I check the end of processing date in the archive popup and select Archive contacts checkbox for DE version",
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
        "I click on De-Archive contact button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_CONTACT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CONTACT_BUTTON);
        });

    When(
        "I change the last contact date and report date time for today for DE version",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          webDriverHelpers.fillInWebElement(LAST_CONTACT_DATE, formatter.format(LocalDate.now()));
          webDriverHelpers.fillInWebElement(REPORT_DATE, formatter.format(LocalDate.now()));
        });
    When(
        "I change the date of last contact to {int} days ago for DE version",
        (Integer days) -> {
          webDriverHelpers.scrollToElement(LAST_CONTACT_DATE);
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          lastContactDateForFollowUp = LocalDate.now().minusDays(days);
          webDriverHelpers.fillInWebElement(
              LAST_CONTACT_DATE, formatter.format(lastContactDateForFollowUp));
        });
    When(
        "I filter with last created contact using contact UUID",
        () -> {
          TimeUnit.SECONDS.sleep(1); // long system reaction
          webDriverHelpers.clearAndFillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, contactUUID);
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(5); // wait for the system
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I fill De-Archive contact popup with ([^\"]*)",
        (String text) -> {
          webDriverHelpers.fillInWebElement(DEARCHIVE_REASON_TEXT_AREA, text);
          TimeUnit.SECONDS.sleep(1); // slow reaction from system side
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_DEARCHIVE_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on confirm button in de-archive contact popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP));

    When(
        "I click on discard button in de-archive contact popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL_POPUP));

    And(
        "^I fill follow-up status comment from Edit contact page$",
        () -> {
          editedContact = contactService.buildEditContact();
          fillFollowUpStatusComment(editedContact.getFollowUpStatusComment());
        });

    And(
        "^I set the last contact date to (\\d+) days before the vaccination date$",
        (Integer numberOfDays) -> {
          fillDateOfLastContactDE(LocalDate.now().minusDays(35 + numberOfDays));
        });

    And(
        "^I check the displayed message is correct after hovering over the Vaccination Card Info icon on Edit Contact Page for DE$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(VACCINATION_CARD_INFO_ICON);
          webDriverHelpers.hoverToElement(VACCINATION_CARD_INFO_ICON);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(VACCINATION_CARD_INFO_POPUP_TEXT);
          softly.assertEquals(
              displayedText,
              "Diese Impfung ist f\u00FCr diesen Kontakt nicht relevant, weil das Datum der Impfung nach dem Datum des letzten Kontaktes oder dem Kontakt-Meldedatum liegt.",
              "Message is incorrect");
          softly.assertAll();
        });

    And(
        "^I check that displayed vaccination name is equal to \"([^\"]*)\" on Edit contact page$",
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
        "^I check that displayed vaccination name is \"([^\"]*)\" on Edit contact page$",
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
        "^I click on the Edit Vaccination icon on vaccination card on Edit contact page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_VACCINATION_BUTTON));

    And(
        "^I click on Open case of this contact person on Edit contact page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(OPEN_CASE_OF_THIS_CONTACT_PERSON_LINK);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EPID_NUMBER_INPUT);
        });

    And(
        "^I check that follow-up status comment is correctly displayed on Edit contact page$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SymptomsTabPage.SAVE_BUTTON);
          String followUpStatusComment =
              webDriverHelpers.getValueFromWebElement(FOLLOW_UP_COMMENT_FIELD);
          softly.assertEquals(
              followUpStatusComment,
              EditContactSteps.editedContact.getFollowUpStatusComment()
                  + "\n[System] Follow-up automatically canceled because contact was converted to a case",
              "Follow-up status comment is incorrect!");
          softly.assertAll();
        });

    And(
        "^I click on the NEW TASK button from Edit Contact page$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                EditContactPage.NEW_TASK_BUTTON, TASK_TYPE_COMBOBOX));

    When(
        "I check if created contact is available in API",
        () -> {
          getContactByUUID(contactUUID);
        });

    When(
        "I check if popup with {string} title appears",
        (String title) -> {
          softly.assertTrue(webDriverHelpers.isElementVisibleWithTimeout(getHeaderText(title), 5));
          softly.assertAll();
        });

    When(
        "I click on okay button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_OKAY);
        });

    When(
        "I check if warning information with related to the associated case not being shared appears in share contact popup",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(
                  WARNING_CASE_NOT_SHARED_SHARE_POPUP_DE, 2));
          softly.assertAll();
        });

    When(
        "I click on discard button",
        () -> webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON_POPUP));
    When(
        "I check if popup with {string} header appears",
        (String text) -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(CONTACT_CAN_NOT_BE_SHARED_HEADER_DE, 2));
          softly.assertAll();
        });

    And(
        "^I remove tha last contact date on Edit Contact page$",
        () -> {
          webDriverHelpers.clearWebElement(LAST_CONTACT_DATE);
        });

    When(
        "I check if Follow up until date is ([^\"]*) days after last contact date of recently created contact",
        (Integer days) -> {
          TimeUnit.SECONDS.sleep(3);
          String date = webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE);
          softly.assertEquals(
              DateTimeFormatter.ofPattern("dd.MM.yyyy")
                  .format(EditContactSteps.collectedContact.getDateOfLastContact().plusDays(days)),
              date);
          softly.assertAll();
        });

    When(
        "I check if Follow up until date is ([^\"]*) days after last created API contact report date",
        (Integer days) -> {
          TimeUnit.SECONDS.sleep(3);
          String date = webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE);
          softly.assertEquals(
              DateTimeFormatter.ofPattern("dd.MM.yyyy")
                  .format(
                      apiState
                          .getCreatedContact()
                          .getReportDateTime()
                          .toInstant()
                          .atZone(ZoneId.systemDefault())
                          .toLocalDate()
                          .plusDays(days)),
              date);
          softly.assertAll();
        });

    And(
        "I check if vaccination name for vaccine number {int} in the vaccination card is {string}",
        (Integer vaccineNumber, String elementStatus) -> {
          switch (elementStatus) {
            case "greyed out":
              webDriverHelpers.isElementGreyedOut(
                  getVaccinationCardVaccinationNameByIndex(vaccineNumber));
              break;
            case "enabled":
              webDriverHelpers.isElementEnabled(
                  getVaccinationCardVaccinationNameByIndex(vaccineNumber));
              break;
          }
        });
  }

  private void selectContactClassification(String classification) {
    webDriverHelpers.clickWebElementByText(CONTACT_CLASSIFICATION_OPTIONS, classification);
  }

  private void selectMultiDayContact(String multiDayContact) {
    webDriverHelpers.clickWebElementByText(MULTI_DAY_CONTACT_LABEL, multiDayContact);
  }

  private void fillDateOfFirstContact(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(FIRST_DAY_CONTACT_DATE, formatter.format(date));
  }

  private void fillDateOfLastContact(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(LAST_CONTACT_DATE, formatter.format(date));
  }

  private void fillDateOfLastContactDE(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(LAST_CONTACT_DATE, formatterDE.format(date));
  }

  private void selectDiseaseOfSourceCase(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void fillExternalId(String text) {
    webDriverHelpers.clearAndFillInWebElement(EXTERNAL_ID_INPUT, text);
  }

  private void fillExternalToken(String text) {
    webDriverHelpers.clearAndFillInWebElement(EXTERNAL_TOKEN_INPUT, text);
  }

  private void fillReportDate(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(REPORT_DATE, formatter.format(date));
  }

  private void selectReportingDistrict(String district) {
    webDriverHelpers.selectFromCombobox(REPORTING_DISTRICT_COMBOBOX, district);
  }

  private void selectResponsibleRegion(String region) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, region);
  }

  private void selectResponsibleDistrict(String district) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, district);
  }

  private void selectResponsibleCommunity(String community) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, community);
  }

  private void selectReturningTraveler(String text) {
    webDriverHelpers.clickWebElementByText(RETURNING_TRAVELER_OPTIONS, text);
  }

  private void fillCaseIdExternalSystem(String date) {
    webDriverHelpers.clearAndFillInWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT, date);
  }

  private void fillCaseOrEventInformation(String information) {
    webDriverHelpers.clearAndFillInWebElement(CASE_OR_EVENT_INFORMATION_INPUT, information);
  }

  private void selectIdentificationSource(String source) {
    webDriverHelpers.selectFromCombobox(CONTACT_IDENTIFICATION_SOURCE_DETAILS_COMBOBOX, source);
  }

  private void fillIdentificationSource(String source) {
    webDriverHelpers.clearAndFillInWebElement(IDENTIFICATION_SOURCE_INPUT, source);
  }

  private void selectContactType(String proximity) {
    webDriverHelpers.clickWebElementByText(TYPE_OF_CONTACT_OPTIONS, proximity);
  }

  private void fillAdditionalInformationOnContactType(String information) {
    webDriverHelpers.clearAndFillInWebElement(
        ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT, information);
  }

  private void selectContactCategory(String category) {
    webDriverHelpers.clickWebElementByText(CONTACT_CATEGORY_OPTIONS, category);
  }

  private void selectRelationShipWithCase(String relationship) {
    webDriverHelpers.selectFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX, relationship);
  }

  private void fillDescriptionOfHowContactTookPlace(String text) {
    webDriverHelpers.clearAndFillInWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT, text);
  }

  private void selectProhibitionToWork(String text) {
    webDriverHelpers.clickWebElementByText(PROHIBITION_TO_WORK_OPTIONS, text);
  }

  private void selectHomeBasedQuarantinePossible(String text) {
    webDriverHelpers.clickWebElementByText(HOME_BASED_QUARANTINE_OPTIONS, text);
  }

  private void selectQuarantine(String text) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, text);
  }

  private void selectHighPriority(String text) {
    webDriverHelpers.clickWebElementByText(HIGH_PRIORITY_LABEL, text);
  }

  private void selectPreexistingConditionDiabetes(String text) {
    webDriverHelpers.clickWebElementByText(DIABETES_OPTIONS, text);
  }

  private void selectPreexistingConditionHiv(String text) {
    webDriverHelpers.clickWebElementByText(HIV_OPTIONS, text);
  }

  private void selectPreexistingConditionLiver(String text) {
    webDriverHelpers.clickWebElementByText(LIVER_OPTIONS, text);
  }

  private void selectPreexistingConditionMalignancy(String text) {
    webDriverHelpers.clickWebElementByText(MALIGNANCY_OPTIONS, text);
  }

  private void selectPreexistingConditionChronicPulmonary(String text) {
    webDriverHelpers.clickWebElementByText(PULMONARY_OPTIONS, text);
  }

  private void selectPreexistingConditionRenal(String text) {
    webDriverHelpers.clickWebElementByText(RENAL_OPTIONS, text);
  }

  private void selectPreexistingConditionNeurologic(String text) {
    webDriverHelpers.clickWebElementByText(NEUROLOGIC_OPTIONS, text);
  }

  private void selectPreexistingConditionCardiovascular(String text) {
    webDriverHelpers.clickWebElementByText(CARDIOVASCULAR_OPTIONS, text);
  }

  private void fillAdditionalRelevantPreexistingConditions(String source) {
    webDriverHelpers.clearAndFillInWebElement(ADDITIONAL_RELEVANT_PRE_CONDITIONS_TEXT, source);
  }

  private void selectVaccinationStatusForThisDisease(String vaccine) {
    webDriverHelpers.selectFromCombobox(VACCINATION_STATUS_COMBOBOX, vaccine);
  }

  private void selectImmunosuppressiveTherapy(String text) {
    webDriverHelpers.clickWebElementByText(IMMUNOSUPPRESSIVE_THERAPY_OPTIONS, text);
  }

  private void selectActiveInCare(String text) {
    webDriverHelpers.clickWebElementByText(CARE_OVER_60_OPTIONS, text);
  }

  private void clickCancelFollowUpButton() {
    if (editedContact.isCancelFollowUp()) {
      webDriverHelpers.clickOnWebElementBySelector(CANCEL_FOLLOW_UP_BUTTON);
    }
  }

  private void selectOverwriteFollowUp(String text) {
    webDriverHelpers.clickWebElementByText(OVERWRITE_FOLLOW_UP_LABEL, text);
  }

  private void fillDateOfFollowUpUntil(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(FOLLOW_UP_UNTIL_DATE, formatter.format(date));
  }

  private void fillFollowUpStatusComment(String source) {
    webDriverHelpers.clearAndFillInWebElement(FOLLOW_UP_STATUS_TEXT, source);
  }

  private void selectResponsibleContactOfficer(String vaccine) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_STATUS_OFFICER_COMBOBOX, vaccine);
  }

  private void fillGeneralComment(String source) {
    webDriverHelpers.clearAndFillInWebElement(GENERAL_COMMENT_TEXT, source);
  }

  private boolean isFollowUpIsVisible() {
    return webDriverHelpers.isElementVisibleWithTimeout(CANCEL_FOLLOW_UP_BUTTON, 1);
  }

  private boolean isFollowUpNotVisible() {
    return !isFollowUpIsVisible();
  }

  private Contact collectContactDataDE() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    String collectedLastDateOfContact = webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatterDE);
    LocalDate parsedLastDateOfContact = LocalDate.parse(collectedLastDateOfContact, formatterDE);
    Contact contactInfo = getContactInformationDE();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .reportDate(parsedDateOfReport)
        .diseaseOfSourceCase(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .caseIdInExternalSystem(
            webDriverHelpers.getValueFromWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT))
        .dateOfLastContact(parsedLastDateOfContact)
        .caseOrEventInformation(
            webDriverHelpers.getValueFromWebElement(CASE_OR_EVENT_INFORMATION_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        .additionalInformationOnContactType(
            webDriverHelpers.getValueFromWebElement(
                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        .contactCategory(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  private Contact collectContactData() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    String collectedLastDateOfContact = webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    LocalDate parsedLastDateOfContact = LocalDate.parse(collectedLastDateOfContact, formatter);
    Contact contactInfo = getContactInformation();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .reportDate(parsedDateOfReport)
        .diseaseOfSourceCase(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .caseIdInExternalSystem(
            webDriverHelpers.getValueFromWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT))
        .dateOfLastContact(parsedLastDateOfContact)
        .caseOrEventInformation(
            webDriverHelpers.getValueFromWebElement(CASE_OR_EVENT_INFORMATION_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        // field no longer available
        //        .additionalInformationOnContactType(
        //            webDriverHelpers.getValueFromWebElement(
        //                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        // field no longer available
        //        .contactCategory(
        //
        // webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  private Contact collectContactDataFromCase() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    String collectedLastDateOfContact = webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    LocalDate parsedLastDateOfContact = LocalDate.parse(collectedLastDateOfContact, formatter);
    Contact contactInfo = getContactInformation();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .reportDate(parsedDateOfReport)
        .dateOfLastContact(parsedLastDateOfContact)
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        // field no longer available
        //        .additionalInformationOnContactType(
        //            webDriverHelpers.getValueFromWebElement(
        //                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        // field no longer available
        //        .contactCategory(
        //
        // webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  private Contact collectContactDataRelatedWithChooseSourceCase() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    String collectedLastDateOfContact = webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    LocalDate parsedLastDateOfContact = LocalDate.parse(collectedLastDateOfContact, formatter);
    Contact contactInfo = getContactInformation();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .reportDate(parsedDateOfReport)
        .diseaseOfSourceCase(webDriverHelpers.getTextFromPresentWebElement(DISEASE_VALUE))
        .dateOfLastContact(parsedLastDateOfContact)
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        // field no longer available
        //        .additionalInformationOnContactType(
        //            webDriverHelpers.getValueFromWebElement(
        //                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        // field no longer available
        //        .contactCategory(
        //
        // webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  private Contact collectContactDataAfterEdit() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    String classification =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CLASSIFICATION_OPTIONS);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    // TODO enable it back once 6803 is fixed
    //    LocalDate parsedDateOfFollowUp =
    //        LocalDate.parse(webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE),
    // formatter);
    // field no longer available
    //    String identificationSource =
    //        webDriverHelpers.getValueFromWebElement(IDENTIFICATION_SOURCE_INPUT);

    return Contact.builder()
        .classification(classification)
        .multiDay(webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(MULTI_DAY_CONTACT_CHECKBOX))
        .diseaseOfSourceCase(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        //        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .externalToken(webDriverHelpers.getValueFromWebElement(EXTERNAL_TOKEN_INPUT))
        .reportDate(parsedDateOfReport)
        //
        // .reportingDistrict(webDriverHelpers.getValueFromCombobox(REPORTING_DISTRICT_COMBOBOX))
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .caseIdInExternalSystem(
            webDriverHelpers.getValueFromWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT))
        .caseOrEventInformation(
            webDriverHelpers.getValueFromWebElement(CASE_OR_EVENT_INFORMATION_INPUT))
        //       .identificationSource(
        //
        // webDriverHelpers.getValueFromCombobox(CONTACT_IDENTIFICATION_SOURCE_DETAILS_COMBOBOX))
        //        .identificationSourceDetails(identificationSource)
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        //        .additionalInformationOnContactType(
        //            webDriverHelpers.getValueFromWebElement(
        //                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        //        .contactCategory(
        //
        // webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        //        .prohibitionToWork(
        //
        // webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PROHIBITION_TO_WORK_OPTIONS))
        //        .homeBasedQuarantinePossible(
        //            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
        //                HOME_BASED_QUARANTINE_OPTIONS))
        .quarantine(webDriverHelpers.getValueFromCombobox(QUARANTINE_COMBOBOX))
        .highPriority(webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(HIGH_PRIORITY_CHECKBOX))
        .diabetes(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIABETES_OPTIONS))
        //        .immunodeficiencyIncludingHiv(
        //            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HIV_OPTIONS))
        .liverDisease(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LIVER_OPTIONS))
        .malignancy(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(MALIGNANCY_OPTIONS))
        .chronicPulmonaryDisease(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PULMONARY_OPTIONS))
        .renalDisease(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RENAL_OPTIONS))
        .chronicNeurologicalNeuromuscularDisease(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(NEUROLOGIC_OPTIONS))
        .cardiovascularDiseaseIncludingHypertension(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CARDIOVASCULAR_OPTIONS))
        .additionalRelevantPreexistingConditions(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_RELEVANT_PRE_CONDITIONS_TEXT))
        .vaccinationStatusForThisDisease(
            webDriverHelpers.getValueFromCombobox(VACCINATION_STATUS_COMBOBOX))
        //        .immunosuppressiveTherapy(
        //            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
        //                IMMUNOSUPPRESSIVE_THERAPY_OPTIONS))
        //        .activeInCare(
        //
        // webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CARE_OVER_60_OPTIONS))
        // TODO enable it back once 6803 is fixed
        //            .overwriteFollowUp(
        //
        // webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(OVERWRITE_FOLLOW_UP_CHECKBOX))
        //        .dateOfFollowUpUntil(parsedDateOfFollowUp)
        .cancelFollowUp(isFollowUpNotVisible())
        .followUpStatusComment(webDriverHelpers.getValueFromWebElement(FOLLOW_UP_STATUS_TEXT))
        .responsibleContactOfficer(
            webDriverHelpers.getValueFromCombobox(RESPONSIBLE_STATUS_OFFICER_COMBOBOX))
        .generalComment(webDriverHelpers.getValueFromWebElement(GENERAL_COMMENT_TEXT))
        /**
         * The following fields are set from existent contact because due to app problems we
         * couldn't scrap them from UI
         */
        .dateOfLastContact(editedContact.getDateOfLastContact())
        .dateOfFirstContact(editedContact.getDateOfFirstContact())
        .build();
  }

  private Contact getContactInformation() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    String contactInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] contactInfos = contactInfo.split(" ");
    LocalDate localDate = LocalDate.parse(contactInfos[3].replace(")", ""), formatter);
    return Contact.builder()
        .firstName(contactInfos[0])
        .lastName(contactInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  private Contact getContactInformationDE() {
    String contactInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] contactInfos = contactInfo.split(" ");
    LocalDate localDate = LocalDate.parse(contactInfos[3].replace(")", ""), formatterDE);
    return Contact.builder()
        .firstName(contactInfos[0])
        .lastName(contactInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  private Contact collectComplexContactData() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    String collectedLastDateOfContact = webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE);
    LocalDate parsedLastDateOfContact = LocalDate.parse(collectedLastDateOfContact, formatter);
    Contact contactInfo = getContactInformation();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .reportDate(parsedDateOfReport)
        .dateOfLastContact(parsedLastDateOfContact)
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        // field no longer available
        //        .additionalInformationOnContactType(
        //            webDriverHelpers.getValueFromWebElement(
        //                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        //        .contactCategory(
        //
        // webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  private void selectQuarantineOrderTemplate(String templateName) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, templateName);
  }

  private void fillExtraComment(String extraComment) {
    webDriverHelpers.fillInAndLeaveWebElement(EditContactPage.EXTRA_COMMENT_TEXTAREA, extraComment);
  }

  private Contact collectContactPersonUuid() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 40);
    return Contact.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  @SneakyThrows
  public void getContactByUUID(String contactUUID) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(CONTACTS_PATH + "/" + contactUUID).build());
  }
}
