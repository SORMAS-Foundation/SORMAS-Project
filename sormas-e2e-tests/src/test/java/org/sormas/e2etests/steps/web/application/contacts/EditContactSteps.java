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
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.DATE_OF_BIRTH_DAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.DATE_OF_BIRTH_MONTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.DATE_OF_BIRTH_YEAR_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.CONTACT_PERSON_TAB;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.GENERAL_SEARCH_INPUT;

import cucumber.api.java8.En;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.pojo.web.QuarantineOrder;
import org.sormas.e2etests.entities.services.ContactDocumentService;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class EditContactSteps implements En {
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Contact createdContact;
  public static Contact collectedContact;
  public static QuarantineOrder aQuarantineOrder;
  public static Contact editedContact;
  public static Contact aContact;
  public static final String userDirPath = System.getProperty("user.dir");

  @Inject
  public EditContactSteps(
      WebDriverHelpers webDriverHelpers,
      ContactService contactService,
      SoftAssert softly,
      AssertHelpers assertHelpers,
      ContactDocumentService contactDocumentService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I collect the UUID displayed on Contact event page",
        () -> collectedContact = collectContactUuid());

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
          TimeUnit.SECONDS.sleep(6);
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
        "I check the edited data is correctly displayed on Edit Contact page after editing",
        () -> {
          collectedContact = collectContactDataAfterEdit();
          ComparisonHelper.compareEqualEntities(editedContact, collectedContact);
        });

    When(
        "I open Contact Person tab",
        () -> {
          webDriverHelpers.waitForPageLoaded();
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
        });

    When(
        "^I change all contact fields and save$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
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
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONTACT_SAVED_POPUP);
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
    When(
        "^I click on ([^\"]*) radio button Contact Person tab$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(
                CONTACT_CLASSIFICATION_RADIO_BUTTON, buttonName));
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
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.scrollToElement(CREATE_CASE_FROM_CONTACT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_CASE_FROM_CONTACT_BUTTON);
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

  private Contact collectContactData() {
    String collectedDateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
    LocalDate parsedDateOfReport = LocalDate.parse(collectedDateOfReport, formatter);
    String collectedLastDateOfContact = webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE);
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

  private Contact collectContactDataDE() {
    Contact contactInfo = getContactInformationDE();

    return Contact.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .returningTraveler(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RETURNING_TRAVELER_OPTIONS))
        .reportDate(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(REPORT_DATE), DATE_FORMATTER_DE))
        .diseaseOfSourceCase(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .caseIdInExternalSystem(
            webDriverHelpers.getValueFromWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT))
        .dateOfLastContact(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE), DATE_FORMATTER_DE))
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

  private Contact collectContactDataAfterEdit() {
    webDriverHelpers.waitForPageLoaded();
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
    LocalDate localDate = LocalDate.parse(contactInfos[3].replace(")", ""), DATE_FORMATTER_DE);
    return Contact.builder()
        .firstName(contactInfos[0])
        .lastName(contactInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  public Contact collectComplexContactData() {
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

  private Contact collectContactUuid() {
    return Contact.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  private LocalDate getDateOfReportDE() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
  }

  private LocalDate dateOfLastContactDE() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(LAST_CONTACT_DATE);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
  }

  private void fillDateOfBirthDE(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }
}
