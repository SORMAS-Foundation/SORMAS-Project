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

import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.DISEASE_COLUMNS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.FIRST_CONTACT_ID_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.FIRST_NAME_COLUMNS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.LAST_NAME_COLUMNS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.PERSON_LIKE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.TYPE_OF_CONTACT_COLUMNS;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.CONTACT_CHOOSE_CASE;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_ACTION_SAVE;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_BIRTHDATE_DAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_BIRTHDATE_MONTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_BIRTHDATE_YEAR_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_DATE_LAST_CONTACT_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_DATE_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_DISEASE_OF_SOURCE_CASE;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_RELATIONSHIP_TO_CASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_SELECTED_SOURCE_CASE_NAME_AND_ID_TEXT;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.LINE_LISTING_TYPE_OF_CONTACT_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.getLineListingDateReportInputByIndex;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.CONTACT_SAVED_POPUP;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.ContactsLineListing;
import org.sormas.e2etests.entities.services.ContactsLineListingService;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class ContactsLineListingSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static ContactsLineListing contactsLineListing;
  public static ContactsLineListing duplicatedContactLineListing;
  public static ContactsLineListing duplicatedContactLineListingDE;

  @Inject
  public ContactsLineListingSteps(
      WebDriverHelpers webDriverHelpers,
      ContactsLineListingService contactsLineListingService,
      SoftAssert softly,
      ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;
    duplicatedContactLineListing = contactsLineListingService.buildGeneratedLineListingContacts();
    duplicatedContactLineListingDE =
        contactsLineListingService.buildGeneratedLineListingContactsDE();
    When(
        "^I create a new Contact with specific data for DE version through Line Listing$",
        () -> {
          contactsLineListing = contactsLineListingService.buildGeneratedLineListingContactsDE();
          selectRegion(contactsLineListing.getRegion());
          selectDistrict(contactsLineListing.getDistrict());
          fillDateOfReport(contactsLineListing.getDateOfReport(), Locale.GERMAN);
          fillDateOfLastContact(contactsLineListing.getDateOfLastContact(), Locale.GERMAN);
          selectTypeOfContact(contactsLineListing.getTypeOfContact());
          selectRelationshipWithCase(contactsLineListing.getRelationshipWithCase());
          fillFirstName(contactsLineListing.getFirstName());
          fillLastName(contactsLineListing.getLastName());
            selectBirthYear(contactsLineListing.getBirthYear());
            selectBirthMonth(contactsLineListing.getBirthMonth());
            selectBirthDay(contactsLineListing.getBirthDay());
          selectSex(contactsLineListing.getSex());
        });
    When(
        "^I create a new Contact with specific data through Line Listing with duplicated data$",
        () -> {
          selectRegion(duplicatedContactLineListing.getRegion());
          selectDistrict(duplicatedContactLineListing.getDistrict());
          fillDateOfReport(duplicatedContactLineListing.getDateOfReport(), Locale.ENGLISH);
          fillDateOfLastContact(
              duplicatedContactLineListing.getDateOfLastContact(), Locale.ENGLISH);
          selectTypeOfContact(duplicatedContactLineListing.getTypeOfContact());
          selectRelationshipWithCase(duplicatedContactLineListing.getRelationshipWithCase());
          fillFirstName(duplicatedContactLineListing.getFirstName());
          fillLastName(duplicatedContactLineListing.getLastName());
          selectBirthYear(duplicatedContactLineListing.getBirthYear());
          selectBirthMonth(duplicatedContactLineListing.getBirthMonth());
          selectBirthDay(duplicatedContactLineListing.getBirthDay());
          selectSex(duplicatedContactLineListing.getSex());
        });
    When(
        "^I create a new Contact with specific data through Line Listing with duplicated data for De$",
        () -> {
          selectRegion(duplicatedContactLineListingDE.getRegion());
          selectDistrict(duplicatedContactLineListingDE.getDistrict());
          fillDateOfReport(duplicatedContactLineListingDE.getDateOfReport(), Locale.GERMAN);
          fillDateOfLastContact(
              duplicatedContactLineListingDE.getDateOfLastContact(), Locale.GERMAN);
          selectTypeOfContact(duplicatedContactLineListingDE.getTypeOfContact());
          selectRelationshipWithCase(duplicatedContactLineListingDE.getRelationshipWithCase());
          fillFirstName(duplicatedContactLineListingDE.getFirstName());
          fillLastName(duplicatedContactLineListingDE.getLastName());
          selectBirthYear(duplicatedContactLineListingDE.getBirthYear());
          selectBirthMonth(duplicatedContactLineListingDE.getBirthMonth());
          selectBirthDay(duplicatedContactLineListingDE.getBirthDay());
          selectSex(duplicatedContactLineListingDE.getSex());
        });

    When(
        "^I create a new Contact with specific data through Line Listing$",
        () -> {
          createNewContactTroughLineListing(contactsLineListingService, false);
        });

    When(
        "^I create a new Contact with specific data through Line Listing when disease prefilled$",
        () -> {
          createNewContactTroughLineListing(contactsLineListingService, true);
        });

    When(
        "^I create a new Contacts from Event Participants using Line Listing$",
        () -> {
          contactsLineListing = contactsLineListingService.buildGeneratedLineListingContacts();
          selectRegion(contactsLineListing.getRegion());
          selectDistrict(contactsLineListing.getDistrict());
          fillFirstDateOfReport(contactsLineListing.getDateOfReport());
          fillSecondDateOfReport(contactsLineListing.getDateOfReport());
        });

    When(
        "I save the new contact using line listing feature",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_ACTION_SAVE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(25);
          if (webDriverHelpers.isElementVisibleWithTimeout(CONTACT_SAVED_POPUP, 5)) {
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONTACT_SAVED_POPUP);
            webDriverHelpers.clickOnWebElementBySelector(CONTACT_SAVED_POPUP);
          }
          TimeUnit.SECONDS.sleep(2);
        });
    When(
        "I save the new contacts from Event Participants using line listing feature in Event Participant tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_ACTION_SAVE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(25);
        });

    When(
        "I check that contact created from Line Listing is saved and displayed in results grid",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(PERSON_LIKE_SEARCH_INPUT);
          String caseName =
              contactsLineListing.getFirstName() + " " + contactsLineListing.getLastName();
          webDriverHelpers.fillInWebElement(PERSON_LIKE_SEARCH_INPUT, caseName);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(FIRST_CONTACT_ID_BUTTON);

          softly.assertTrue(
              contactsLineListing
                  .getDisease()
                  .toUpperCase()
                  .contains(getDiseaseDirectoryPage().toUpperCase()),
              String.format(
                  "Disease value is not correct: expecting: %s, but found: %s",
                  contactsLineListing.getDisease(), getDiseaseDirectoryPage()));
          softly.assertEquals(
              getTypeOfContactDirectoryPage(),
              contactsLineListing.getTypeOfContact(),
              "Type of contact is not correct");
          softly.assertEquals(
              getFirstNameDirectoryPage(),
              contactsLineListing.getFirstName(),
              "First name is not correct");
          softly.assertEquals(
              getLastNameDirectoryPage(),
              contactsLineListing.getLastName(),
              "Last name is not correct");
          softly.assertAll();
        });

    When(
        "^I click Choose Case button from Contact Directory Line Listing popup window$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONTACT_CHOOSE_CASE);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CHOOSE_CASE);
        });

    When(
        "^I check the name and uuid of selected case information is correctly displayed in new Contact Line Listing popup window$",
        () -> {
          String displayedCaseNameAndId =
              webDriverHelpers.getTextFromWebElement(
                  LINE_LISTING_SELECTED_SOURCE_CASE_NAME_AND_ID_TEXT);
          String caseNameAndId =
              "Selected source case:\n"
                  + apiState.getLastCreatedPerson().getFirstName()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName()
                  + " ("
                  + apiState.getCreatedCase().getUuid().substring(0, 6).toUpperCase()
                  + ")";
          softly.assertEquals(
              displayedCaseNameAndId, caseNameAndId, "Person name or ID is not correct");
          softly.assertAll();
        });

    When(
        "^I check disease dropdown is automatically filled with disease of selected Case in new Contact Line Listing popup window$",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(LINE_LISTING_DISEASE_OF_SOURCE_CASE),
              DiseasesValues.getCaptionForName(apiState.getCreatedCase().getDisease()),
              "Displayed disease is not correct");
          softly.assertAll();
        });
  }

  private void createNewContactTroughLineListing(
      ContactsLineListingService contactsLineListingService, boolean diseasePrefilled) {
    contactsLineListing = contactsLineListingService.buildGeneratedLineListingContacts();
    if (!diseasePrefilled) {
      selectDisease(contactsLineListing.getDisease());
    }
    selectRegion(contactsLineListing.getRegion());
    selectDistrict(contactsLineListing.getDistrict());
    fillDateOfReport(contactsLineListing.getDateOfReport(), Locale.ENGLISH);
    fillDateOfLastContact(contactsLineListing.getDateOfLastContact(), Locale.ENGLISH);
    selectTypeOfContact(contactsLineListing.getTypeOfContact());
    selectRelationshipWithCase(contactsLineListing.getRelationshipWithCase());
    fillFirstName(contactsLineListing.getFirstName());
    fillLastName(contactsLineListing.getLastName());
    selectBirthYear(contactsLineListing.getBirthYear());
    selectBirthMonth(contactsLineListing.getBirthMonth());
    selectBirthDay(contactsLineListing.getBirthDay());
    selectSex(contactsLineListing.getSex());
  }

  private void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_DISEASE_COMBOBOX, disease);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_DISTRICT_COMBOBOX, district);
  }

  private void fillDateOfReport(LocalDate dateOfReport, Locale locale) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_REPORT_INPUT, formatter.format(dateOfReport));
    else
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_REPORT_INPUT, DATE_FORMATTER.format(dateOfReport));
  }

  private void fillDateOfLastContact(LocalDate dateOfLastContact, Locale locale) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_LAST_CONTACT_INPUT, formatter.format(dateOfLastContact));
    else
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_LAST_CONTACT_INPUT, DATE_FORMATTER.format(dateOfLastContact));
  }

  private void fillFirstDateOfReport(LocalDate dateOfReport) {
    webDriverHelpers.clearAndFillInWebElement(
        getLineListingDateReportInputByIndex("1"), DATE_FORMATTER.format(dateOfReport));
  }

  private void fillSecondDateOfReport(LocalDate dateOfReport) {
    webDriverHelpers.clearAndFillInWebElement(
        getLineListingDateReportInputByIndex("2"), DATE_FORMATTER.format(dateOfReport));
  }

  private void selectTypeOfContact(String typeOfContact) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_TYPE_OF_CONTACT_COMBOBOX, typeOfContact);
  }

  private void selectRelationshipWithCase(String relationshipWithCase) {
    webDriverHelpers.selectFromCombobox(
        LINE_LISTING_RELATIONSHIP_TO_CASE_COMBOBOX, relationshipWithCase);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(LINE_LISTING_FIRST_NAME_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LINE_LISTING_LAST_NAME_INPUT, lastName);
  }

  private void selectBirthYear(String year) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_BIRTHDATE_YEAR_COMBOBOX, year);
  }

  private void selectBirthMonth(String month) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_BIRTHDATE_MONTH_COMBOBOX, month);
  }

  private void selectBirthDay(String day) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_BIRTHDATE_DAY_COMBOBOX, day);
  }

  private void filldateOfBirth(LocalDate localDate, Locale locale) {
    webDriverHelpers.selectFromCombobox(
        LINE_LISTING_BIRTHDATE_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        LINE_LISTING_BIRTHDATE_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, locale));
    webDriverHelpers.selectFromCombobox(
        LINE_LISTING_BIRTHDATE_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_SEX_COMBOBOX, sex);
  }

  private String getDiseaseDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(DISEASE_COLUMNS, 0);
  }

  private String getTypeOfContactDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(TYPE_OF_CONTACT_COLUMNS, 0);
  }

  private String getFirstNameDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(FIRST_NAME_COLUMNS, 0);
  }

  private String getLastNameDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(LAST_NAME_COLUMNS, 0);
  }
}
