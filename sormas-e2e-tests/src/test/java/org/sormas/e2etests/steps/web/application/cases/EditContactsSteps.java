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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.EditContactsPage.*;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RELATIONSHIP_WITH_CASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.CONTACT_CATEGORY_OPTIONS;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.LAST_CONTACT_DATE;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.TYPE_OF_CONTACT_OPTIONS;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

@Slf4j
public class EditContactsSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  String LAST_CREATED_CASE_CONTACTS_TAB_URL;
  protected Contact contact;
  public static Contact collectedContact;
  protected String contactUUID;

  @Inject
  public EditContactsSteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      ContactService contactService,
      SoftAssert softly,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I open the Case Contacts tab of the created case via api",
        () -> {
          LAST_CREATED_CASE_CONTACTS_TAB_URL =
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!cases/contacts/"
                  + apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CASE_CONTACTS_TAB_URL);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NEW_CONTACT_BUTTON);
        });

    Then(
        "I click on new contact button from Case Contacts tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_CONTACT_BUTTON));

    When(
        "^I create a new contact from Cases Contacts tab$",
        () -> {
          contact = contactService.buildGeneratedContact();
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          fillDateOfBirth(contact.getDateOfBirth());
          selectSex(contact.getSex());
          fillPrimaryPhoneNumber(contact.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(contact.getPrimaryEmailAddress());
          selectReturningTraveler(contact.getReturningTraveler());
          fillDateOfReport(contact.getReportDate());
          fillDateOfLastContact(contact.getDateOfLastContact());
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
          contactUUID = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
        });

    Then(
        "I verify that created contact from Case Contacts tab is correctly displayed",
        () -> {
          openContactFromResultsByUUID(contactUUID);
          collectedContact = collectContactData();

          ComparisonHelper.compareEqualFieldsOfEntities(
              contact,
              collectedContact,
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

    Then(
        "I check the linked contact information is correctly displayed",
        () -> {
          log.info("Waiting for contact UUID to be displayed");
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.cssSelector(
                  String.format(
                      CONTACT_RESULTS_UUID_LOCATOR, apiState.getCreatedContact().getUuid())));
          //          log.info("Collecting contact ID");
          //          String contactId =
          // webDriverHelpers.getValueFromTableRowUsingTheHeader("Contact ID", 0);
          //          log.info("Collecting contact disease");
          //          String contactDisease =
          //              (webDriverHelpers.getValueFromTableRowUsingTheHeader("Disease",
          // 0).equals("COVID-19"))
          //                  ? "CORONAVIRUS"
          //                  : "Not expected string!";
          //          log.info("Collecting contact classification");
          //          String contactClassification =
          //              (webDriverHelpers
          //                      .getValueFromTableRowUsingTheHeader("Contact classification", 0)
          //                      .equals("Unconfirmed contact"))
          //                  ? "UNCONFIRMED"
          //                  : "Not expected string!";
          //          log.info("Collecting contact first name");
          //          String firstName =
          //              webDriverHelpers.getValueFromTableRowUsingTheHeader(
          //                  "First name of contact person", 0);
          //          log.info("Collecting contact last name");
          //          String lastName =
          //              webDriverHelpers.getValueFromTableRowUsingTheHeader("Last name of contact
          // person", 0);

          log.info("Collecting contact ID");
          String contactId =
              webDriverHelpers.getTextFromWebElement(By.xpath("//tbody/tr[1]//td[2]/a"));
          log.info("Collecting contact disease");
          String contactDisease =
              (webDriverHelpers
                      .getTextFromWebElement(By.xpath("//tbody/tr[1]//td[6]"))
                      .equals("COVID-19"))
                  ? "CORONAVIRUS"
                  : "Not expected string!";
          log.info("Collecting contact classification");
          String contactClassification =
              (webDriverHelpers
                      .getTextFromWebElement(By.xpath("//tbody/tr[1]//td[7]"))
                      .equals("Unconfirmed contact"))
                  ? "UNCONFIRMED"
                  : "Not expected string!";
          log.info("Collecting contact first name");
          String firstName =
              webDriverHelpers.getTextFromWebElement(By.xpath("//tbody/tr[1]//td[10]"));
          log.info("Collecting contact last name");
          String lastName =
              webDriverHelpers.getTextFromWebElement(By.xpath("//tbody/tr[1]//td[11]"));

          softly.assertTrue(
              apiState.getCreatedContact().getUuid().substring(0, 6).equalsIgnoreCase(contactId),
              "UUID doesn't match");
          softly.assertTrue(
              apiState.getCreatedContact().getDisease().equalsIgnoreCase(contactDisease),
              "Disease doesn't match");
          softly.assertTrue(
              apiState
                  .getCreatedContact()
                  .getContactClassification()
                  .equalsIgnoreCase(contactClassification),
              "Classification doesn't match");
          softly.assertTrue(
              apiState.getCreatedContact().getPerson().getFirstName().equalsIgnoreCase(firstName),
              "First name doesn't match");
          softly.assertTrue(
              apiState.getCreatedContact().getPerson().getLastName().equalsIgnoreCase(lastName),
              "Last name doesn't match");
          softly.assertAll();
        });
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_OF_CONTACT_PERSON_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_OF_CONTACT_PERSON_INPUT, lastName);
  }

  private void fillDateOfBirth(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  private void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  private void fillPrimaryEmailAddress(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryPhoneNumber);
  }

  private void selectReturningTraveler(String option) {
    webDriverHelpers.clickWebElementByText(TYPE_OF_CONTACT_TRAVELER, option);
  }

  private void fillDateOfReport(LocalDate date) {
    webDriverHelpers.clearAndFillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  private void fillDateOfLastContact(LocalDate date) {
    webDriverHelpers.fillInWebElement(DATE_OF_LAST_CONTACT_INPUT, formatter.format(date));
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

  private void openContactFromResultsByUUID(String uuid) {
    By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, uuid));
    webDriverHelpers.clickOnWebElementBySelector((uuidLocator));
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
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
        .dateOfLastContact(parsedLastDateOfContact)
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_COMMUNITY_INPUT))
        .additionalInformationOnContactType(
            webDriverHelpers.getValueFromWebElement(
                ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT))
        .typeOfContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TYPE_OF_CONTACT_OPTIONS))
        .contactCategory(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_CATEGORY_OPTIONS))
        .relationshipWithCase(webDriverHelpers.getValueFromWebElement(RELATIONSHIP_WITH_CASE_INPUT))
        .descriptionOfHowContactTookPlace(
            webDriverHelpers.getValueFromWebElement(DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  private Contact getContactInformation() {
    String contactData = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] contactInfo = contactData.split(" ");
    LocalDate localDate = LocalDate.parse(contactInfo[3].replace(")", ""), formatter);
    return Contact.builder()
        .firstName(contactInfo[0])
        .lastName(contactInfo[1])
        .dateOfBirth(localDate)
        .build();
  }
}
