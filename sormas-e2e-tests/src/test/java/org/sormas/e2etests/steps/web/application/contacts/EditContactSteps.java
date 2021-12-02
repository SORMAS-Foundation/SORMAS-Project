/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static org.sormas.e2etests.pages.application.contacts.EditContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.CONTACT_PERSON_TAB;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Contact;
import org.sormas.e2etests.services.ContactService;

public class EditContactSteps implements En {
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Contact aContact;
  public static Contact editedContact;

  @Inject
  public EditContactSteps(WebDriverHelpers webDriverHelpers, ContactService contactService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit Contact page",
        () -> {
          aContact = collectContactData();
          SoftAssertions softly = new SoftAssertions();
          softly
              .assertThat(aContact.getFirstName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getFirstName());
          softly
              .assertThat(aContact.getLastName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getLastName());
          softly
              .assertThat(aContact.getReturningTraveler())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getReturningTraveler());
          softly
              .assertThat(aContact.getReportDate())
              .isEqualTo(CreateNewContactSteps.contact.getReportDate());
          softly
              .assertThat(aContact.getDiseaseOfSourceCase())
              .isEqualTo(CreateNewContactSteps.contact.getDiseaseOfSourceCase());
          softly
              .assertThat(aContact.getCaseIdInExternalSystem())
              .isEqualTo(CreateNewContactSteps.contact.getCaseIdInExternalSystem());
          softly
              .assertThat(aContact.getDateOfLastContact())
              .isEqualTo(CreateNewContactSteps.contact.getDateOfLastContact());
          softly
              .assertThat(aContact.getCaseOrEventInformation())
              .isEqualTo(CreateNewContactSteps.contact.getCaseOrEventInformation());
          softly
              .assertThat(aContact.getResponsibleRegion())
              .isEqualTo(CreateNewContactSteps.contact.getResponsibleRegion());
          softly
              .assertThat(aContact.getResponsibleDistrict())
              .isEqualTo(CreateNewContactSteps.contact.getResponsibleDistrict());
          softly
              .assertThat(aContact.getResponsibleCommunity())
              .isEqualTo(CreateNewContactSteps.contact.getResponsibleCommunity());
          softly
              .assertThat(
                  aContact
                      .getAdditionalInformationOnContactType()
                      .equalsIgnoreCase(
                          CreateNewContactSteps.contact.getAdditionalInformationOnContactType()))
              .isTrue();
          softly
              .assertThat(aContact.getTypeOfContact())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getTypeOfContact());
          softly
              .assertThat(aContact.getContactCategory())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getContactCategory());
          softly
              .assertThat(aContact.getRelationshipWithCase())
              .isEqualTo(CreateNewContactSteps.contact.getRelationshipWithCase());
          softly
              .assertThat(aContact.getDescriptionOfHowContactTookPlace())
              .isEqualTo(CreateNewContactSteps.contact.getDescriptionOfHowContactTookPlace());
          softly.assertAll();
        });

    When(
        "I check the edited data is correctly displayed on Edit Contact page after editing",
        () -> {
          aContact = collectContactDataAfterEdit();
          SoftAssertions softly = new SoftAssertions();
          softly.assertThat(editedContact).isEqualTo(aContact);
          softly.assertAll();
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
  }

  public void selectContactClassification(String classification) {
    webDriverHelpers.clickWebElementByText(CONTACT_CLASSIFICATION_OPTIONS, classification);
  }

  public void selectMultiDayContact(String multiDayContact) {
    webDriverHelpers.clickWebElementByText(MULTI_DAY_CONTACT_LABEL, multiDayContact);
  }

  public void fillDateOfFirstContact(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(FIRST_DAY_CONTACT_DATE, formatter.format(date));
  }

  public void fillDateOfLastContact(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(LAST_CONTACT_DATE, formatter.format(date));
  }

  public void selectDiseaseOfSourceCase(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  public void fillExternalId(String text) {
    webDriverHelpers.clearAndFillInWebElement(EXTERNAL_ID_INPUT, text);
  }

  public void fillExternalToken(String text) {
    webDriverHelpers.clearAndFillInWebElement(EXTERNAL_TOKEN_INPUT, text);
  }

  public void fillReportDate(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(REPORT_DATE, formatter.format(date));
  }

  public void selectReportingDistrict(String district) {
    webDriverHelpers.selectFromCombobox(REPORTING_DISTRICT_COMBOBOX, district);
  }

  public void selectResponsibleRegion(String region) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, region);
  }

  public void selectResponsibleDistrict(String district) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, district);
  }

  public void selectResponsibleCommunity(String community) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, community);
  }

  public void selectReturningTraveler(String text) {
    webDriverHelpers.clickWebElementByText(RETURNING_TRAVELER_OPTIONS, text);
  }

  public void fillCaseIdExternalSystem(String date) {
    webDriverHelpers.clearAndFillInWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT, date);
  }

  public void fillCaseOrEventInformation(String information) {
    webDriverHelpers.clearAndFillInWebElement(CASE_OR_EVENT_INFORMATION_INPUT, information);
  }

  public void selectIdentificationSource(String source) {
    webDriverHelpers.selectFromCombobox(CONTACT_IDENTIFICATION_SOURCE_DETAILS_COMBOBOX, source);
  }

  public void fillIdentificationSource(String source) {
    webDriverHelpers.clearAndFillInWebElement(IDENTIFICATION_SOURCE_INPUT, source);
  }

  public void selectContactType(String proximity) {
    webDriverHelpers.clickWebElementByText(TYPE_OF_CONTACT_OPTIONS, proximity);
  }

  public void fillAdditionalInformationOnContactType(String information) {
    webDriverHelpers.clearAndFillInWebElement(
        ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT, information);
  }

  public void selectContactCategory(String category) {
    webDriverHelpers.clickWebElementByText(CONTACT_CATEGORY_OPTIONS, category);
  }

  public void selectRelationShipWithCase(String relationship) {
    webDriverHelpers.selectFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX, relationship);
  }

  public void fillDescriptionOfHowContactTookPlace(String text) {
    webDriverHelpers.clearAndFillInWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT, text);
  }

  public void selectProhibitionToWork(String text) {
    webDriverHelpers.clickWebElementByText(PROHIBITION_TO_WORK_OPTIONS, text);
  }

  public void selectHomeBasedQuarantinePossible(String text) {
    webDriverHelpers.clickWebElementByText(HOME_BASED_QUARANTINE_OPTIONS, text);
  }

  public void selectQuarantine(String text) {
    webDriverHelpers.selectFromCombobox(QUARANTINE_COMBOBOX, text);
  }

  public void selectHighPriority(String text) {
    webDriverHelpers.clickWebElementByText(HIGH_PRIORITY_LABEL, text);
  }

  public void selectPreexistingConditionDiabetes(String text) {
    webDriverHelpers.clickWebElementByText(DIABETES_OPTIONS, text);
  }

  public void selectPreexistingConditionHiv(String text) {
    webDriverHelpers.clickWebElementByText(HIV_OPTIONS, text);
  }

  public void selectPreexistingConditionLiver(String text) {
    webDriverHelpers.clickWebElementByText(LIVER_OPTIONS, text);
  }

  public void selectPreexistingConditionMalignancy(String text) {
    webDriverHelpers.clickWebElementByText(MALIGNANCY_OPTIONS, text);
  }

  public void selectPreexistingConditionChronicPulmonary(String text) {
    webDriverHelpers.clickWebElementByText(PULMONARY_OPTIONS, text);
  }

  public void selectPreexistingConditionRenal(String text) {
    webDriverHelpers.clickWebElementByText(RENAL_OPTIONS, text);
  }

  public void selectPreexistingConditionNeurologic(String text) {
    webDriverHelpers.clickWebElementByText(NEUROLOGIC_OPTIONS, text);
  }

  public void selectPreexistingConditionCardiovascular(String text) {
    webDriverHelpers.clickWebElementByText(CARDIOVASCULAR_OPTIONS, text);
  }

  public void fillAdditionalRelevantPreexistingConditions(String source) {
    webDriverHelpers.clearAndFillInWebElement(ADDITIONAL_RELEVANT_PRE_CONDITIONS_TEXT, source);
  }

  public void selectVaccinationStatusForThisDisease(String vaccine) {
    webDriverHelpers.selectFromCombobox(VACCINATION_STATUS_COMBOBOX, vaccine);
  }

  public void selectImmunosuppressiveTherapy(String text) {
    webDriverHelpers.clickWebElementByText(IMMUNOSUPPRESSIVE_THERAPY_OPTIONS, text);
  }

  public void selectActiveInCare(String text) {
    webDriverHelpers.clickWebElementByText(CARE_OVER_60_OPTIONS, text);
  }

  public void clickCancelFollowUpButton() {
    if (editedContact.isCancelFollowUp()) {
      webDriverHelpers.clickOnWebElementBySelector(CANCEL_FOLLOW_UP_BUTTON);
    }
  }

  public void selectOverwriteFollowUp(String text) {
    webDriverHelpers.clickWebElementByText(OVERWRITE_FOLLOW_UP_LABEL, text);
  }

  public void fillDateOfFollowUpUntil(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(FOLLOW_UP_UNTIL_DATE, formatter.format(date));
  }

  public void fillFollowUpStatusComment(String source) {
    webDriverHelpers.clearAndFillInWebElement(FOLLOW_UP_STATUS_TEXT, source);
  }

  public void selectResponsibleContactOfficer(String vaccine) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_STATUS_OFFICER_COMBOBOX, vaccine);
  }

  public void fillGeneralComment(String source) {
    webDriverHelpers.clearAndFillInWebElement(GENERAL_COMMENT_TEXT, source);
  }

  public boolean isFollowUpIsVisible() {
    return webDriverHelpers.isElementVisibleWithTimeout(CANCEL_FOLLOW_UP_BUTTON, 1);
  }

  public boolean isFollowUpNotVisible() {
    return !isFollowUpIsVisible();
  }

  public Contact collectContactData() {
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

  public Contact collectContactDataAfterEdit() {
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

  public Contact getContactInformation() {
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
}
