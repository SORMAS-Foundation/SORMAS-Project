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

import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.CONTACT_CREATED_POPUP;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Contact;
import org.sormas.e2etests.services.ContactService;

public class CreateNewContactSteps implements En {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Contact contact;

  @Inject
  public CreateNewContactSteps(WebDriverHelpers webDriverHelpers, ContactService contactService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new contact$",
        () -> {
          contact = contactService.buildGeneratedContact();
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          fillDateOfBirth(contact.getDateOfBirth());
          selectSex(contact.getSex());
          fillNationalHealthId(contact.getNationalHealthId());
          fillPassportNumber(contact.getPassportNumber());
          fillPrimaryPhoneNumber(contact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(contact.getPrimaryEmailAddress());
          selectReturningTraveler(contact.getReturningTraveler());
          fillDateOfReport(contact.getReportDate());
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          fillCaseIdInExternalSystem(contact.getCaseIdInExternalSystem());
          fillDateOfLastContact(contact.getDateOfLastContact());
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
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CREATED_POPUP);
        });
  }

  public void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_OF_CONTACT_PERSON_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_OF_CONTACT_PERSON_INPUT, lastName);
  }

  public void fillDateOfBirth(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  public void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  public void fillNationalHealthId(String nationalHealthId) {
    webDriverHelpers.fillInWebElement(NATIONAL_HEALTH_ID_INPUT, nationalHealthId);
  }

  public void fillPassportNumber(String passportNumber) {
    webDriverHelpers.fillInWebElement(PASSPORT_NUMBER_INPUT, passportNumber);
  }

  public void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  public void fillPrimaryEmailAddress(String primaryEmail) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryEmail);
  }

  public void selectReturningTraveler(String option) {
    webDriverHelpers.clickWebElementByText(TYPE_OF_CONTACT_TRAVELER, option);
  }

  public void fillDateOfReport(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  public void fillDiseaseOfSourceCase(String diseaseOrCase) {
    webDriverHelpers.selectFromCombobox(DISEASE_OF_SOURCE_CASE_COMBOBOX, diseaseOrCase);
  }

  public void fillCaseIdInExternalSystem(String externalId) {
    webDriverHelpers.fillInWebElement(CASE_ID_IN_EXTERNAL_SYSTEM_INPUT, externalId);
  }

  public void fillDateOfLastContact(LocalDate date) {
    webDriverHelpers.fillInWebElement(DATE_OF_LAST_CONTACT_INPUT, formatter.format(date));
  }

  public void fillCaseOrEventInformation(String caseOrEventInfo) {
    webDriverHelpers.fillInWebElement(CASE_OR_EVENT_INFORMATION_INPUT, caseOrEventInfo);
  }

  public void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  public void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  public void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  public void selectTypeOfContact(String typeOfContact) {
    webDriverHelpers.clickWebElementByText(TYPE_OF_CONTACT_OPTIONS, typeOfContact);
  }

  public void selectContactCategory(String category) {
    webDriverHelpers.clickWebElementByText(CONTACT_CATEGORY_OPTIONS, category);
  }

  public void fillAdditionalInformationOnTheTypeOfContact(String description) {
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT, description);
  }

  public void fillRelationshipWithCase(String relationshipWithCase) {
    webDriverHelpers.selectFromCombobox(RELATIONSHIP_WITH_CASE_COMBOBOX, relationshipWithCase);
  }

  public void fillDescriptionOfHowContactTookPlace(String descriptionOfHowContactTookPlace) {
    webDriverHelpers.fillInWebElement(
        DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT, descriptionOfHowContactTookPlace);
  }
}
