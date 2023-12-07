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

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONFIRM_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LINE_LISTING_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_SEARCH_LOCATOR_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SELECT_MATCHING_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.CONTACT_CREATED_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DISCARD_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PICK_OR_CREATE_PERSON_HEADER_DE;
import static org.sormas.e2etests.steps.web.application.cases.EditCaseSteps.caseUuid;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class CreateNewContactSteps implements En {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static Contact contact;
  private final SoftAssert softly;
  public static Contact collectedContactUUID;
  protected static Contact duplicatedContact;
  public static Contact samePersonDataContact;
  private final Faker faker;

  @Inject
  public CreateNewContactSteps(
      WebDriverHelpers webDriverHelpers,
      ContactService contactService,
      ApiState apiState,
      Faker faker,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;
    this.faker = faker;
    Random r = new Random();
    char c = (char) (r.nextInt(26) + 'a');
    String firstName = faker.name().firstName() + c;
    String lastName = faker.name().lastName() + c;
    LocalDate dateOfBirth =
        LocalDate.of(
            faker.number().numberBetween(1900, 2002),
            faker.number().numberBetween(1, 12),
            faker.number().numberBetween(1, 27));
    String sex = GenderValues.getRandomGenderDE();
    duplicatedContact =
        contactService.buildGeneratedContactWithParametrizedPersonData(
            firstName, lastName, dateOfBirth);
    samePersonDataContact =
        contactService.buildGeneratedContactWithParametrizedPersonDataDE(
            firstName, lastName, dateOfBirth, sex);

    When(
        "^I fill a new contact form for duplicated contact with same person data$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          fillFirstName(duplicatedContact.getFirstName());
          fillLastName(duplicatedContact.getLastName());
          fillDateOfBirth(duplicatedContact.getDateOfBirth(), Locale.ENGLISH);
          selectSex(duplicatedContact.getSex());
          fillPrimaryPhoneNumber(duplicatedContact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(duplicatedContact.getPrimaryEmailAddress());
          selectReturningTraveler(duplicatedContact.getReturningTraveler());
          fillDateOfReport(duplicatedContact.getReportDate(), Locale.ENGLISH);
          fillDiseaseOfSourceCase(duplicatedContact.getDiseaseOfSourceCase());
          fillCaseIdInExternalSystem(duplicatedContact.getCaseIdInExternalSystem());
          fillDateOfLastContact(duplicatedContact.getDateOfLastContact(), Locale.ENGLISH);
          fillCaseOrEventInformation(duplicatedContact.getCaseOrEventInformation());
          selectResponsibleRegion(duplicatedContact.getResponsibleRegion());
          selectResponsibleDistrict(duplicatedContact.getResponsibleDistrict());
          selectResponsibleCommunity(duplicatedContact.getResponsibleCommunity());
          selectTypeOfContact(duplicatedContact.getTypeOfContact());
          // field no longer available
          //          fillAdditionalInformationOnTheTypeOfContact(
          //              duplicatedContact.getAdditionalInformationOnContactType());
          //          selectContactCategory(duplicatedContact.getContactCategory().toUpperCase());
          fillRelationshipWithCase(duplicatedContact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(
              duplicatedContact.getDescriptionOfHowContactTookPlace());
        });
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
        "^I fill a new contact form for DE version without multicontact$",
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
        "^I fill a new contact form for DE version without person data$",
        () -> {
          contact = contactService.buildGeneratedContactDE();
          selectSex(contact.getSex());
          fillPrimaryPhoneNumber(contact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(contact.getPrimaryEmailAddress());
          selectReturningTraveler(contact.getReturningTraveler());
          fillDateOfReport(contact.getReportDate(), Locale.GERMAN);
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          fillCaseIdInExternalSystem(contact.getCaseIdInExternalSystem());
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
        "I fill a new contact form for DE version with mandatory data with {string} as a region and {string} as a disctrict",
        (String reg, String disct) -> {
          contact = contactService.buildGeneratedContactDE();
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          selectSex(contact.getSex());
          fillDateOfReport(contact.getReportDate(), Locale.GERMAN);
          selectResponsibleRegion(reg);
          selectResponsibleDistrict(disct);
        });

    When(
        "^I fill a new contact form with same person data for DE version$",
        () -> {
          fillFirstName(samePersonDataContact.getFirstName());
          fillLastName(samePersonDataContact.getLastName());
          fillDateOfBirth(samePersonDataContact.getDateOfBirth(), Locale.GERMAN);
          selectSex(samePersonDataContact.getSex());
          fillPrimaryPhoneNumber(samePersonDataContact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(samePersonDataContact.getPrimaryEmailAddress());
          selectReturningTraveler(samePersonDataContact.getReturningTraveler());
          fillDateOfReport(samePersonDataContact.getReportDate(), Locale.GERMAN);
          selectMultiDayContact();
          fillDateOfFirstContact(samePersonDataContact.getDateOfFirstContact(), Locale.GERMAN);
          fillDateOfLastContact(samePersonDataContact.getDateOfLastContact(), Locale.GERMAN);
          selectResponsibleRegion(samePersonDataContact.getResponsibleRegion());
          selectResponsibleDistrict(samePersonDataContact.getResponsibleDistrict());
          selectResponsibleCommunity(samePersonDataContact.getResponsibleCommunity());
          selectTypeOfContact(samePersonDataContact.getTypeOfContact());
          fillAdditionalInformationOnTheTypeOfContact(
              samePersonDataContact.getAdditionalInformationOnContactType());
          selectContactCategory(samePersonDataContact.getContactCategory().toUpperCase());
          fillRelationshipWithCase(samePersonDataContact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(
              samePersonDataContact.getDescriptionOfHowContactTookPlace());
        });

    When(
        "I fill a new contact form with same person data with {string} region and {string} district for DE version",
        (String region, String district) -> {
          fillFirstName(samePersonDataContact.getFirstName());
          fillLastName(samePersonDataContact.getLastName());
          fillDateOfBirth(samePersonDataContact.getDateOfBirth(), Locale.GERMAN);
          selectSex(samePersonDataContact.getSex());
          selectResponsibleRegion(region);
          selectResponsibleDistrict(district);
        });

    When(
        "^I fill a new contact form with specific person data$",
        () -> {
          contact =
              contactService.buildGeneratedContactWithParametrizedPersonData(
                  firstName, lastName, dateOfBirth);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
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
          fillRelationshipWithCase(contact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(contact.getDescriptionOfHowContactTookPlace());
        });
    When(
        "^I fill a new contact form$",
        () -> {
          contact = contactService.buildGeneratedContact();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
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
          fillCaseOrEventInformation(contact.getCaseOrEventInformation());
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
          selectResponsibleCommunity(contact.getResponsibleCommunity());
          fillDateOfLastContact(contact.getDateOfLastContact(), Locale.ENGLISH);
          selectTypeOfContact(contact.getTypeOfContact());
          //          fillAdditionalInformationOnTheTypeOfContact(
          //              contact.getAdditionalInformationOnContactType());
          //          selectContactCategory(contact.getContactCategory().toUpperCase());
          fillRelationshipWithCase(contact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(contact.getDescriptionOfHowContactTookPlace());
        });

    When(
        "I fill only mandatory fields with and set relationship with case to ([^\"]*)",
        (String option) -> {
          contact = contactService.buildGeneratedContact();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          selectSex(contact.getSex());
          fillDateOfReport(contact.getReportDate(), Locale.ENGLISH);
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
          fillRelationshipWithCaseEqual(option);
        });

    When(
        "^I fill a mandatory fields for a new contact form for DE$",
        () -> {
          contact = contactService.buildGeneratedContactDE();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          selectSex(contact.getSex());
          fillDateOfReport(contact.getReportDate(), Locale.GERMAN);
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
        });

    When(
        "^I fill a mandatory fields for a new contact form$",
        () -> {
          contact = contactService.buildGeneratedContact();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          selectSex(contact.getSex());
        });

    When(
        "^I fill a new contact form with chosen data without personal data$",
        () -> {
          contact = contactService.buildGeneratedContact();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          fillPrimaryPhoneNumber(contact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(contact.getPrimaryEmailAddress());
          selectReturningTraveler(contact.getReturningTraveler());
          fillDateOfReport(contact.getReportDate(), Locale.ENGLISH);
          fillDateOfLastContact(contact.getDateOfLastContact(), Locale.ENGLISH);
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
          selectResponsibleCommunity(contact.getResponsibleCommunity());
          selectTypeOfContact(contact.getTypeOfContact());
          // field no longer available
          //          fillAdditionalInformationOnTheTypeOfContact(
          //              contact.getAdditionalInformationOnContactType());
          // field no longer available
          // selectContactCategory(contact.getContactCategory().toUpperCase());
          fillRelationshipWithCase(contact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(contact.getDescriptionOfHowContactTookPlace());
        });

    When(
        "^I fill a new contact form with chosen data without personal data on Contact directory page$",
        () -> {
          contact = contactService.buildGeneratedContact();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
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
          // field no longer available
          //          fillAdditionalInformationOnTheTypeOfContact(
          //              contact.getAdditionalInformationOnContactType());
          // field no longer available
          //  selectContactCategory(contact.getContactCategory().toUpperCase());
          fillRelationshipWithCase(contact.getRelationshipWithCase());
          fillDescriptionOfHowContactTookPlace(contact.getDescriptionOfHowContactTookPlace());
        });

    When(
        "^I click on the person search button in create new contact form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PERSON_SEARCH_LOCATOR_BUTTON);
        });
    When(
        "^I click on the clear button in new contact form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PERSON_SEARCH_LOCATOR_BUTTON);
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
        "^I search for the last created case uuid by UI in the CHOOSE SOURCE Contact window$",
        () -> {
          webDriverHelpers.fillInWebElement(SOURCE_CASE_WINDOW_CONTACT, caseUuid);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });
    When(
        "^I search for the last case uuid in the CHOOSE SOURCE Contact window for DE$",
        () -> {
          webDriverHelpers.fillInWebElement(
              SOURCE_CASE_WINDOW_CONTACT_DE, apiState.getCreatedCase().getUuid());
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });
    When(
        "^I Pick a new person in Pick or create person popup during contact creation$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
    When(
        "^I Pick a new person in Pick or create person popup during contact creation for DE$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
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
        "^I open the first found result in the CHOOSE SOURCE Contact window for De$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitForRowToBeSelected(SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE);
        });
    When(
        "^I click on SAVE new contact button$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONTACT_CREATED_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CREATED_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });
    When(
        "^I click on SAVE new contact button and choose same person in duplicate detection$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(SELECT_MATCHING_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });
    When(
        "^I click on SAVE new contact case button$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
    When(
        "^I click on SAVE new contact button in the CHOOSE SOURCE popup of Create Contact window$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_CONFIRM_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
        });

    When(
        "^I check if default disease value for contacts in the Line listing is set for ([^\"]*)$",
        (String disease) -> {
          String getDisease = webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX);
          softly.assertEquals(disease, getDisease, "Diseases are not equal");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_DISCARD_BUTTON);
        });

    And(
        "^I change a Report Date to the current date for DE$",
        () -> {
          DateTimeFormatter formatter;
          formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          webDriverHelpers.clearAndFillInWebElement(
              DATE_OF_REPORT_INPUT, formatter.format(LocalDate.now()));
        });

    And(
        "^I fill a mandatory fields for a new contact and date of report to yesterday$",
        () -> {
          contact = contactService.buildGeneratedContact();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          selectSex(contact.getSex());
          fillDateOfReport(LocalDate.now().minusDays(1), Locale.ENGLISH);
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
        });

    And(
        "^I change disease to \"([^\"]*)\" in the Edit contact page$",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(EditContactPage.DISEASE_COMBOBOX, option);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    And(
        "^I click on SAVE new contact button and choose create new person in duplication detection$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });

    Then(
        "^I check the wording of the last two entries for type of contact$",
        () -> {
          webDriverHelpers.checkWebElementContainsText(
              TYPE_OF_CONTACT_MEDICAL_PERSONEL_SAME_ROOM_CHECKBOX,
              "Medical personnel at safe proximity (> 2 meter) or with protective equipment");
          webDriverHelpers.checkWebElementContainsText(
              TYPE_OF_CONTACT_MEDICAL_PERSONEL_WITHOUT_DIRECT_CONTACT_CHECKBOX,
              "Medical personnel at safe proximity (> 2 meter), without direct contact with secretions or excretions of the patient and without aerosol exposure");
        });

    When(
        "^I click on Discard button in Create New Contact form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_TASK_BUTTON);
        });

    And(
        "^I fill a new Contact form with specific data for DE version with date (\\d+) days ago$",
        (Integer daysAgo) -> {
          contact = contactService.buildGeneratedContactDE();
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          selectSex(contact.getSex());
          fillDateOfReport(LocalDate.now().minusDays(daysAgo), Locale.GERMAN);
          fillDiseaseOfSourceCase(contact.getDiseaseOfSourceCase());
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
        });

    And(
        "^I choose same contact in duplicate detection and save for DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SELECT_EXISTING_CONTACT_DE);
          webDriverHelpers.clickOnWebElementBySelector(SELECT_EXISTING_CONTACT_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    And(
        "^I choose create new contact in Pick or create entry form for DE$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CONTACT_CONFIRMATION_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON_POPUP);
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

  private void fillDateOfDateOfBirthDE(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN));
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

  private void fillRelationshipWithCaseEqual(String relationshipWithCase) {
    webDriverHelpers.selectFromComboboxEqual(RELATIONSHIP_WITH_CASE_COMBOBOX, relationshipWithCase);
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
