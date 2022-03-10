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

import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.*;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.web.ContactsLineListing;
import org.sormas.e2etests.entities.services.ContactsLineListingService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class ContactsLineListingSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static ContactsLineListing contactsLineListing;

  @Inject
  public ContactsLineListingSteps(
      WebDriverHelpers webDriverHelpers,
      ContactsLineListingService contactsLineListingService,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new Contact with specific data for DE version through Line Listing$",
        () -> {
          contactsLineListing = contactsLineListingService.buildGeneratedLineListingContactsDE();
          selectDisease(contactsLineListing.getDisease());
          selectRegion(contactsLineListing.getRegion());
          selectDistrict(contactsLineListing.getDistrict());
          fillDateOfReport(contactsLineListing.getDateOfReport(), Locale.GERMAN);
          fillDateOfLastContact(contactsLineListing.getDateOfLastContact(), Locale.GERMAN);
          selectTypeOfContact(contactsLineListing.getTypeOfContact());
          selectRelationshipWithCase(contactsLineListing.getRelationshipWithCase());
          fillFirstName(contactsLineListing.getFirstName());
          fillLastName(contactsLineListing.getLastName());
          filldateOfBirth(contactsLineListing.getDateOfBirth(), Locale.GERMAN);
          selectSex(contactsLineListing.getSex());
        });

    When(
        "^I create a new Contact with specific data through Line Listing$",
        () -> {
          contactsLineListing = contactsLineListingService.buildGeneratedLineListingContacts();
          selectDisease(contactsLineListing.getDisease());
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
          // TODO remove this logic once problem is investigated in Jenkins
          // start
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath("//*[@class='v-Notification-caption']"));
          webDriverHelpers.clickOnWebElementBySelector(
              By.xpath("//*[@class='v-Notification-caption']"));
          // end
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(25);
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
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DISEASE_COLUMNS);
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_REPORT_INPUT, formatter.format(dateOfReport));
    else
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_REPORT_INPUT, DATE_FORMATTER.format(dateOfReport));
  }

  private void fillDateOfLastContact(LocalDate dateOfLastContact, Locale locale) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_LAST_CONTACT_INPUT, formatter.format(dateOfLastContact));
    else
      webDriverHelpers.clearAndFillInWebElement(
          LINE_LISTING_DATE_LAST_CONTACT_INPUT, DATE_FORMATTER.format(dateOfLastContact));
  }

  private void fillFirstDateOfReport(LocalDate dateOfReport) {
    webDriverHelpers.clearAndFillInWebElement(
        LINE_LISTING_FIRST_DATE_OF_REPORT_INPUT, DATE_FORMATTER.format(dateOfReport));
  }

  private void fillSecondDateOfReport(LocalDate dateOfReport) {
    webDriverHelpers.clearAndFillInWebElement(
        LINE_LISTING_SECOND_DATE_OF_REPORT_INPUT, DATE_FORMATTER.format(dateOfReport));
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
