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

import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.*;
import static org.sormas.e2etests.pages.application.contacts.ContactsLineListingPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.ContactsLineListing;
import org.sormas.e2etests.services.ContactsLineListingService;

public class ContactsLineListingSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static ContactsLineListing contactsLineListing;

  @Inject
  public ContactsLineListingSteps(
      WebDriverHelpers webDriverHelpers,
      ContactsLineListingService contactsLineListingService,
      final SoftAssertions softly)
      throws InterruptedException {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new Contact with specific data through Line Listing$",
        () -> {
          contactsLineListing = contactsLineListingService.buildGeneratedLineListingContacts();
          selectDisease(contactsLineListing.getDisease());
          selectRegion(contactsLineListing.getRegion());
          selectDistrict(contactsLineListing.getDistrict());
          fillDateOfReport(contactsLineListing.getDateOfReport());
          fillDateOfLastContact(contactsLineListing.getDateOfLastContact());
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
        "I save the new contact using line listing feature",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_ACTION_SAVE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
        });

    When(
        "I check that contact created from Line Listing is saved and displayed in results grid",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DISEASE_COLUMNS);
          softly.assertThat(contactsLineListing.getDisease()).isEqualTo(getDiseaseDirectoryPage());
          softly
              .assertThat(contactsLineListing.getTypeOfContact())
              .isEqualTo(getTypeOfContactDirectoryPage());
          softly
              .assertThat(contactsLineListing.getFirstName())
              .isEqualTo(getFirstNameDirectoryPage());
          softly
              .assertThat(contactsLineListing.getLastName())
              .isEqualTo(getLastNameDirectoryPage());
        });
  }

  public void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_DISEASE_COMBOBOX, disease);
  }

  public void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_REGION_COMBOBOX, region);
  }

  public void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_DISTRICT_COMBOBOX, district);
  }

  public void fillDateOfReport(LocalDate dateOfReport) {
    webDriverHelpers.clearAndFillInWebElement(
        LINE_LISTING_DATE_REPORT_INPUT, DATE_FORMATTER.format(dateOfReport));
  }

  public void fillDateOfLastContact(LocalDate dateOfLastContact) {
    webDriverHelpers.clearAndFillInWebElement(
        LINE_LISTING_DATE_LAST_CONTACT_INPUT, DATE_FORMATTER.format(dateOfLastContact));
  }

  public void selectTypeOfContact(String typeOfContact) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_TYPE_OF_CONTACT_COMBOBOX, typeOfContact);
  }

  public void selectRelationshipWithCase(String relationshipWithCase) {
    webDriverHelpers.selectFromCombobox(
        LINE_LISTING_RELATIONSHIP_TO_CASE_COMBOBOX, relationshipWithCase);
  }

  public void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(LINE_LISTING_FIRST_NAME_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LINE_LISTING_LAST_NAME_INPUT, lastName);
  }

  public void selectBirthYear(String year) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_BIRTHDATE_YEAR_COMBOBOX, year);
  }

  public void selectBirthMonth(String month) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_BIRTHDATE_MONTH_COMBOBOX, month);
  }

  public void selectBirthDay(String day) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_BIRTHDATE_DAY_COMBOBOX, day);
  }

  public void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(LINE_LISTING_SEX_COMBOBOX, sex);
  }

  public String getDiseaseDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(DISEASE_COLUMNS, 0);
  }

  public String getTypeOfContactDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(FIRST_NAME_COLUMNS, 0);
  }

  public String getFirstNameDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(LAST_NAME_COLUMNS, 0);
  }

  public String getLastNameDirectoryPage() {
    return webDriverHelpers.getTextFromListElement(TYPE_OF_CONTACT_COLUMNS, 0);
  }
}
