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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_CASE_ID_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_DOCUMENT_TEMPLATES;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.GENERATED_DOCUMENT_NAME;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UPLOAD_DOCUMENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_RESULTS_UUID_LOCATOR;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.MULTIPLE_OPTIONS_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.CONTACT_PERSON_TAB;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.GENERAL_SEARCH_INPUT;

import cucumber.api.java8.En;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.pojo.web.QuarantineOrder;
import org.sormas.e2etests.entities.services.ContactDocumentService;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.state.ApiState;
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

  @Inject
  public EditContactSteps(
      WebDriverHelpers webDriverHelpers,
      ContactService contactService,
      SoftAssert softly,
      ApiState apiState,
      AssertHelpers assertHelpers,
      ContactDocumentService contactDocumentService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I open the last created contact in Contact directory page",
        () -> {
          searchAfterContactByMultipleOptions(collectedContact.getUuid());
          openContactFromResultsByUUID(collectedContact.getUuid());
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
                  "additionalInformationOnContactType",
                  "typeOfContact",
                  "contactCategory",
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
                  "additionalInformationOnContactType",
                  "typeOfContact",
                  "contactCategory",
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
                  "additionalInformationOnContactType",
                  "typeOfContact",
                  "contactCategory",
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
                  "additionalInformationOnContactType",
                  "typeOfContact",
                  "contactCategory",
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
                  "additionalInformationOnContactType",
                  "typeOfContact",
                  "contactCategory",
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
        "^I click on ([^\"]*) radio button Contact Person tab$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(
                CONTACT_CLASSIFICATION_RADIO_BUTTON, buttonName));
    When(
        "I check the edited data is correctly displayed on Edit Contact page after editing",
        () -> {
          collectedContact = collectContactDataAfterEdit();
          ComparisonHelper.compareEqualEntities(editedContact, collectedContact);
        });

    When(
        "I open Contact Person tab",
        () -> {
          webDriverHelpers.scrollToElement(CONTACT_PERSON_TAB);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_PERSON_TAB);
        });

    When(
        "I delete the contact",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
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
          fillExternalId(editedContact.getExternalId());
          fillDateOfLastContact(editedContact.getDateOfLastContact());
          fillExternalToken(editedContact.getExternalToken());
          fillReportDate(editedContact.getReportDate());
          selectReportingDistrict(editedContact.getReportingDistrict());
          selectResponsibleRegion(editedContact.getResponsibleRegion());
          selectResponsibleDistrict(editedContact.getResponsibleDistrict());
          selectResponsibleCommunity(editedContact.getResponsibleCommunity());
          selectReturningTraveler(editedContact.getReturningTraveler());
          fillCaseIdExternalSystem(editedContact.getCaseIdInExternalSystem());
          fillCaseOrEventInformation(editedContact.getCaseOrEventInformation());
          selectIdentificationSource(editedContact.getIdentificationSource());
          fillIdentificationSource(editedContact.getIdentificationSourceDetails());
          selectContactType(editedContact.getTypeOfContact());
          fillAdditionalInformationOnContactType(
              editedContact.getAdditionalInformationOnContactType());
          selectContactCategory(editedContact.getContactCategory());
          selectRelationShipWithCase(editedContact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(editedContact.getDescriptionOfHowContactTookPlace());
          selectProhibitionToWork(editedContact.getProhibitionToWork());
          selectHomeBasedQuarantinePossible(editedContact.getHomeBasedQuarantinePossible());
          selectQuarantine(editedContact.getQuarantine());
          selectHighPriority(editedContact.getHighPriority());
          selectPreexistingConditionDiabetes(editedContact.getDiabetes());
          selectPreexistingConditionHiv(editedContact.getImmunodeficiencyIncludingHiv());
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
          selectImmunosuppressiveTherapy(editedContact.getImmunosuppressiveTherapy());
          selectActiveInCare(editedContact.getActiveInCare());
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
        "I click on checkbox to upload generated document to entity in Create Quarantine Order form in Edit Contact directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_CHECKBOX));
    When(
        "I select {string} Quarantine Order in Create Quarantine Order form in Edit Contact directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
        });
    When(
        "I check if downloaded file is correct for {string} Quarantine Order in Edit Contact directory",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          Path path =
              Paths.get(
                  userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name);
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(
                      Files.exists(path),
                      "Quarantine order document was not downloaded. Path used for check: "
                          + path.toAbsolutePath()),
              120);
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
        "I delete downloaded file created from {string} Document Template for Contact",
        (String name) -> {
          String uuid = apiState.getCreatedContact().getUuid();
          File toDelete =
              new File(
                  userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name);
          toDelete.deleteOnExit();
        });
    When(
        "^I click on CONFIRMED CONTACT radio button Contact Data tab for DE version$",
        () ->
            webDriverHelpers.clickWebElementByText(
                CONTACT_CLASSIFICATION_RADIO_BUTTON, "BEST\u00C4TIGTER KONTAKT"));

    When(
        "^I click SAVE button on Edit Contact Page$",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_EDIT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_SAVED_POPUP);
        });
    When(
        "^I click Create Case from Contact button$",
        () -> {
          webDriverHelpers.scrollToElement(CREATE_CASE_FROM_CONTACT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_CASE_FROM_CONTACT_BUTTON);
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
                  "additionalInformationOnContactType",
                  "contactCategory",
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

  private Contact collectContactDataAfterEdit() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    String classification =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CLASSIFICATION_OPTIONS);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    // TODO enable it back once 6803 is fixed
    //    LocalDate parsedDateOfFollowUp =
    //        LocalDate.parse(webDriverHelpers.getValueFromWebElement(FOLLOW_UP_UNTIL_DATE),
    // formatter);
    String identificationSource =
        webDriverHelpers.getValueFromWebElement(IDENTIFICATION_SOURCE_INPUT);

    return Contact.builder()
        .classification(classification)
        .multiDay(webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(MULTI_DAY_CONTACT_CHECKBOX))
        .diseaseOfSourceCase(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .externalToken(webDriverHelpers.getValueFromWebElement(EXTERNAL_TOKEN_INPUT))
        .reportDate(parsedDateOfReport)
        .reportingDistrict(webDriverHelpers.getValueFromCombobox(REPORTING_DISTRICT_COMBOBOX))
        .responsibleRegion(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(webDriverHelpers.getValueFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX))
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .caseIdInExternalSystem(
            webDriverHelpers.getValueFromWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT))
        .caseOrEventInformation(
            webDriverHelpers.getValueFromWebElement(CASE_OR_EVENT_INFORMATION_INPUT))
        .identificationSource(
            webDriverHelpers.getValueFromCombobox(CONTACT_IDENTIFICATION_SOURCE_DETAILS_COMBOBOX))
        .identificationSourceDetails(identificationSource)
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        .additionalInformationOnContactType(
            webDriverHelpers.getValueFromWebElement(
                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .contactCategory(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(
            webDriverHelpers.getValueFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .prohibitionToWork(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PROHIBITION_TO_WORK_OPTIONS))
        .homeBasedQuarantinePossible(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                HOME_BASED_QUARANTINE_OPTIONS))
        .quarantine(webDriverHelpers.getValueFromCombobox(QUARANTINE_COMBOBOX))
        .highPriority(webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(HIGH_PRIORITY_CHECKBOX))
        .diabetes(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIABETES_OPTIONS))
        .immunodeficiencyIncludingHiv(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HIV_OPTIONS))
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
        .immunosuppressiveTherapy(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                IMMUNOSUPPRESSIVE_THERAPY_OPTIONS))
        .activeInCare(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CARE_OVER_60_OPTIONS))
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
        .additionalInformationOnContactType(
            webDriverHelpers.getValueFromWebElement(
                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .contactCategory(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
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

  private void searchAfterContactByMultipleOptions(String idPhoneNameEmail) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
    webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, idPhoneNameEmail);
    webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
  }

  private void openContactFromResultsByUUID(String uuid) {
    By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, uuid));
    webDriverHelpers.clickOnWebElementBySelector((uuidLocator));
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(EditContactPage.UUID_INPUT);
  }
}
