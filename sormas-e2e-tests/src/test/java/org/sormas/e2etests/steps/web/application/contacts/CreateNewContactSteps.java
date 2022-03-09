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

import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.CONTACT_CREATED_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;

public class CreateNewContactSteps implements En {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Contact contact;

  @Inject
  public CreateNewContactSteps(
      WebDriverHelpers webDriverHelpers, ContactService contactService, ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I fill a new contact form for DE version$",
        () -> {
          contact = contactService.buildGeneratedContactDE();
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          fillDateOfBirth(contact.getDateOfBirth(), Locale.GERMAN);
          selectSex(contact.getSex());
          fillPrimaryPhoneNumber(contact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(contact.getPrimaryEmailAddress());
          selectReturningTraveler(contact.getReturningTraveler());
          fillDateOfReport(contact.getReportDate(), Locale.GERMAN);
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          fillCaseIdInExternalSystem(contact.getCaseIdInExternalSystem());
          selectMultiDayContact();
          fillDateOfFirstContact(contact.getDateOfFirstContact(), Locale.GERMAN);
          fillDateOfLastContact(contact.getDateOfLastContact(), Locale.GERMAN);
          fillCaseOrEventInformation(contact.getCaseOrEventInformation());
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
          selectResponsibleCommunity(contact.getResponsibleCommunity());
          selectTypeOfContact(contact.getTypeOfContact());
          fillAdditionalInformationOnTheTypeOfContact(
              contact.getAdditionalInformationOnContactType());
          selectContactCategory(contact.getContactCategory().toUpperCase());
          fillRelationshipWithCase(contact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(contact.getDescriptionOfHowContactTookPlace());
        });

    When(
        "^I fill a new contact form$",
        () -> {
          contact = contactService.buildGeneratedContact();
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          fillDateOfBirth(contact.getDateOfBirth(), Locale.ENGLISH);
          selectSex(contact.getSex());
          fillPrimaryPhoneNumber(contact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(contact.getPrimaryEmailAddress());
          selectReturningTraveler(contact.getReturningTraveler());
          fillDateOfReport(contact.getReportDate(), Locale.ENGLISH);
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          fillCaseIdInExternalSystem(contact.getCaseIdInExternalSystem());
          selectMultiDayContact();
          fillDateOfFirstContact(contact.getDateOfFirstContact(), Locale.ENGLISH);
          fillDateOfLastContact(contact.getDateOfLastContact(), Locale.ENGLISH);
          fillCaseOrEventInformation(contact.getCaseOrEventInformation());
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
          selectResponsibleCommunity(contact.getResponsibleCommunity());
          selectTypeOfContact(contact.getTypeOfContact());
          fillAdditionalInformationOnTheTypeOfContact(
              contact.getAdditionalInformationOnContactType());
          selectContactCategory(contact.getContactCategory().toUpperCase());
          fillRelationshipWithCase(contact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(contact.getDescriptionOfHowContactTookPlace());
        });
    When(
        "^I click CHOOSE CASE button$",
        () -> webDriverHelpers.clickOnWebElementBySelector(CHOOSE_CASE_BUTTON));
    When(
        "^I search for the last case uuid in the CHOOSE SOURCE Contact window$",
        () -> {
          webDriverHelpers.fillInWebElement(
              SOURCE_CASE_WINDOW_CONTACT, apiState.getCreatedCase().getUuid());
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });
    When(
        "^I open the first found result in the CHOOSE SOURCE Contact window$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitForRowToBeSelected(SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON);
        });
    When(
        "^I click on SAVE new contact button$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CREATED_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_OF_CONTACT_PERSON_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_OF_CONTACT_PERSON_INPUT, lastName);
  }

  private void fillDateOfBirth(LocalDate localDate, Locale locale) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX, localDate.getMonth().getDisplayName(TextStyle.FULL, locale));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  private void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  private void fillPrimaryEmailAddress(String primaryEmail) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryEmail);
  }

  private void selectReturningTraveler(String option) {
    webDriverHelpers.clickWebElementByText(TYPE_OF_CONTACT_TRAVELER, option);
  }

  private void fillDateOfReport(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.clearAndFillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  private void fillDiseaseOfSourceCase(String diseaseOrCase) {
    webDriverHelpers.selectFromCombobox(DISEASE_OF_SOURCE_CASE_COMBOBOX, diseaseOrCase);
  }

  private void fillCaseIdInExternalSystem(String externalId) {
    webDriverHelpers.fillInWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT, externalId);
  }

  private void fillDateOfLastContact(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_LAST_CONTACT_INPUT, formatter.format(date));
  }

  private void fillCaseOrEventInformation(String caseOrEventInfo) {
    webDriverHelpers.fillInWebElement(CASE_OR_EVENT_INFORMATION_INPUT, caseOrEventInfo);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void selectTypeOfContact(String typeOfContact) {
    webDriverHelpers.clickWebElementByText(TYPE_OF_CONTACT_OPTIONS, typeOfContact);
  }

  private void selectContactCategory(String category) {
    webDriverHelpers.clickWebElementByText(CONTACT_CATEGORY_OPTIONS, category);
  }

  private void fillAdditionalInformationOnTheTypeOfContact(String description) {
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT, description);
  }

  private void fillRelationshipWithCase(String relationshipWithCase) {
    webDriverHelpers.selectFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX, relationshipWithCase);
  }

  private void fillDescriptionOfHowContactTookPlace(String descriptionOfHowContactTookPlace) {
    webDriverHelpers.fillInWebElement(
        DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT, descriptionOfHowContactTookPlace);
  }

  private void selectMultiDayContact() {
    webDriverHelpers.clickOnWebElementBySelector(MULTI_DAY_CONTACT_LABEL);
  }

  public void fillDateOfFirstContact(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.clearAndFillInWebElement(FIRST_DAY_CONTACT_DATE, formatter.format(date));
  }
}
