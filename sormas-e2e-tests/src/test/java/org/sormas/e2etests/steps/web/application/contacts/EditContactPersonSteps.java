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

import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.*;
import static org.sormas.e2etests.pages.application.contacts.PersonContactDetailsPage.PERSON_CONTACT_DETAILS_POPUP;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.pojo.web.Person;
import org.sormas.e2etests.entities.services.PersonService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class EditContactPersonSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Person aPerson;
  protected static Person newGeneratedPerson;
  public static Person fullyDetailedPerson;
  public static Contact createdContact;

  @Inject
  public EditContactPersonSteps(
      WebDriverHelpers webDriverHelpers,
      PersonService personService,
      SoftAssert softly,
      BaseSteps baseSteps) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data for DE version is correctly displayed on Edit Contact Person page",
        () -> {
          aPerson = collectPersonDataDE();
          createdContact = CreateNewContactSteps.contact;
          softly.assertEquals(
              aPerson.getFirstName(), createdContact.getFirstName(), "First name is not correct");
          softly.assertEquals(
              aPerson.getLastName(),
              createdContact.getLastName().toUpperCase(),
              "Last name is not correct");
          softly.assertEquals(
              aPerson.getDateOfBirth(),
              createdContact.getDateOfBirth(),
              "Date of birth is not correct");
          softly.assertEquals(aPerson.getSex(), createdContact.getSex(), "Sex is not correct");
          softly.assertEquals(
              aPerson.getEmailAddress(),
              createdContact.getPrimaryEmailAddress(),
              "Primary email address is not correct");
          softly.assertEquals(
              aPerson.getPhoneNumber(),
              createdContact.getPrimaryPhoneNumber(),
              "Phone number is not correct");
          softly.assertAll();
        });

    When(
        "I check the created data is correctly displayed on Edit Contact Person page",
        () -> {
          aPerson = collectPersonData();
          createdContact = CreateNewContactSteps.contact;
          softly.assertEquals(
              aPerson.getFirstName(), createdContact.getFirstName(), "First name is not correct");
          softly.assertEquals(
              aPerson.getLastName(),
              createdContact.getLastName().toUpperCase(),
              "Last name is not correct");
          softly.assertEquals(
              aPerson.getDateOfBirth(),
              createdContact.getDateOfBirth(),
              "Date of birth is not correct");
          softly.assertEquals(aPerson.getSex(), createdContact.getSex(), "Sex is not correct");
          softly.assertEquals(
              aPerson.getEmailAddress(),
              createdContact.getPrimaryEmailAddress(),
              "Primary email address is not correct");
          softly.assertEquals(
              aPerson.getPhoneNumber(),
              createdContact.getPrimaryPhoneNumber(),
              "Phone number is not correct");
          softly.assertAll();
        });

    Then(
        "I complete all default empty fields from Contact Person tab",
        () -> {
          newGeneratedPerson = personService.buildGeneratedPerson();
          fillSalutation(newGeneratedPerson.getSalutation());
          fillDateOfBirth(newGeneratedPerson.getDateOfBirth());
          selectSex(newGeneratedPerson.getSex());
          selectPresentConditionOfPerson(newGeneratedPerson.getPresentConditionOfPerson());
          fillExternalId(newGeneratedPerson.getExternalId());
          fillExternalToken(newGeneratedPerson.getExternalToken());
          fillExternalToken(newGeneratedPerson.getExternalToken());
          selectTypeOfOccupation(newGeneratedPerson.getTypeOfOccupation());
          selectStaffOfArmedForces(newGeneratedPerson.getStaffOfArmedForces());
          selectRegion(newGeneratedPerson.getRegion());
          selectDistrict(newGeneratedPerson.getDistrict());
          selectCommunity(newGeneratedPerson.getCommunity());
          selectFacilityCategory(newGeneratedPerson.getFacilityCategory());
          selectFacilityType(newGeneratedPerson.getFacilityType());
          selectFacility(newGeneratedPerson.getFacility());
          fillFacilityNameAndDescription(newGeneratedPerson.getFacilityNameAndDescription());
          fillStreet(newGeneratedPerson.getStreet());
          fillHouseNumber(newGeneratedPerson.getHouseNumber());
          fillAdditionalInformation(newGeneratedPerson.getAdditionalInformation());
          fillPostalCode(newGeneratedPerson.getPostalCode());
          fillCity(newGeneratedPerson.getCity());
          selectAreaType(newGeneratedPerson.getAreaType());
          fillContactPersonFirstName(newGeneratedPerson.getContactPersonFirstName());
          fillContactPersonLastName(newGeneratedPerson.getContactPersonLastName());
          fillBirthName(newGeneratedPerson.getBirthName());
          fillNamesOfGuardians(newGeneratedPerson.getNameOfGuardians());
        });

    Then(
        "I click on new entry button from Contact Information section",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_INFORMATION_NEW_ENTRY_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(PERSON_CONTACT_DETAILS_POPUP);
        });

    Then(
        "I click on save button from Contact Person tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          Person contactInfo = getPersonInformation();
          fullyDetailedPerson =
              personService.updateExistentPerson(
                  newGeneratedPerson,
                  contactInfo.getFirstName(),
                  contactInfo.getLastName(),
                  contactInfo.getUuid(),
                  CreateNewContactSteps.contact.getPrimaryEmailAddress(),
                  CreateNewContactSteps.contact.getPrimaryPhoneNumber());
        });
  }

  private void fillSalutation(String salutation) {
    webDriverHelpers.selectFromCombobox(SALUTATION_COMBOBOX, salutation);
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

  private void selectPresentConditionOfPerson(String condition) {
    webDriverHelpers.selectFromCombobox(PRESENT_CONDITION_COMBOBOX, condition);
  }

  private void fillExternalId(String id) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, id);
  }

  @SneakyThrows
  private void fillExternalToken(String token) {
    webDriverHelpers.fillInWebElement(EXTERNAL_TOKEN_INPUT, token);
    webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
    TimeUnit.SECONDS.sleep(5); // fix for weird behaviour in UI
  }

  @SneakyThrows
  private void selectTypeOfOccupation(String occupation) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_OCCUPATION_COMBOBOX, occupation);
    TimeUnit.SECONDS.sleep(1); // fix for weird behaviour in UI
  }

  private void selectStaffOfArmedForces(String armedForces) {
    webDriverHelpers.selectFromCombobox(STAFF_OF_ARMED_FORCES_COMBOBOX, armedForces);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  private void selectCommunity(String community) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  private void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  private void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void selectFacility(String facility) {
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, facility);
  }

  private void fillFacilityNameAndDescription(String description) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_AND_DESCRIPTION_INPUT, description);
  }

  private void fillStreet(String street) {
    webDriverHelpers.fillInWebElement(STREET_INPUT, street);
  }

  private void fillHouseNumber(String houseNumber) {
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, houseNumber);
  }

  private void fillAdditionalInformation(String info) {
    webDriverHelpers.fillInWebElement(ADDITIONAL_INFORMATION_INPUT, info);
  }

  private void fillPostalCode(String code) {
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, code);
  }

  private void fillCity(String city) {
    webDriverHelpers.fillInWebElement(CITY_INPUT, city);
  }

  private void selectAreaType(String areaType) {
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, areaType);
  }

  private void fillContactPersonFirstName(String first) {
    webDriverHelpers.fillInWebElement(CONTACT_PERSON_FIRST_NAME_INPUT, first);
  }

  private void fillContactPersonLastName(String last) {
    webDriverHelpers.fillInWebElement(CONTACT_PERSON_LAST_NAME_INPUT, last);
  }

  private void fillBirthName(String name) {
    webDriverHelpers.fillInWebElement(BIRTH_NAME_INPUT, name);
  }

  private void fillNamesOfGuardians(String name) {
    webDriverHelpers.fillInWebElement(NAMES_OF_GUARDIANS_INPUT, name);
  }

  private Person collectPersonData() {
    Person contactInfo = getPersonInformation();

    return Person.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .dateOfBirth(contactInfo.getDateOfBirth())
        .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
        .emailAddress(webDriverHelpers.getTextFromPresentWebElement(EMAIL_FIELD))
        .phoneNumber(webDriverHelpers.getTextFromPresentWebElement(PHONE_FIELD))
        .build();
  }

  private Person collectPersonDataDE() {
    Person contactInfo = getPersonInformationDE();

    return Person.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .dateOfBirth(contactInfo.getDateOfBirth())
        .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
        .emailAddress(webDriverHelpers.getTextFromPresentWebElement(EMAIL_FIELD))
        .phoneNumber(webDriverHelpers.getTextFromPresentWebElement(PHONE_FIELD))
        .build();
  }

  private Person getPersonInformation() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    String contactInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String uuid = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
    String[] personInfo = contactInfo.split(" ");
    LocalDate localDate = LocalDate.parse(personInfo[3].replace(")", ""), formatter);
    return Person.builder()
        .firstName(personInfo[0])
        .lastName(personInfo[1])
        .dateOfBirth(localDate)
        .uuid(uuid)
        .build();
  }

  private Person getPersonInformationDE() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
    String contactInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String uuid = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
    String[] personInfo = contactInfo.split(" ");
    LocalDate localDate = LocalDate.parse(personInfo[3].replace(")", ""), formatter);
    return Person.builder()
        .firstName(personInfo[0])
        .lastName(personInfo[1])
        .dateOfBirth(localDate)
        .uuid(uuid)
        .build();
  }
}
