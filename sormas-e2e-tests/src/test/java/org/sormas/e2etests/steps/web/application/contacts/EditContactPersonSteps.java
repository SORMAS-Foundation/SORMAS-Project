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

import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.*;
import static org.sormas.e2etests.pages.application.contacts.PersonContactDetailsPage.PERSON_CONTACT_DETAILS_POPUP;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Person;
import org.sormas.e2etests.services.PersonService;
import org.sormas.e2etests.steps.BaseSteps;

public class EditContactPersonSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Person aPerson;
  protected static Person newGeneratedPerson;
  public static Person fullyDetailedPerson;

  @Inject
  public EditContactPersonSteps(
      WebDriverHelpers webDriverHelpers,
      PersonService personService,
      final SoftAssertions softly,
      BaseSteps baseSteps) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit Contact Person page",
        () -> {
          aPerson = collectPersonData();
          softly
              .assertThat(aPerson.getFirstName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getFirstName());
          softly
              .assertThat(aPerson.getLastName())
              .isEqualToIgnoringCase(CreateNewContactSteps.contact.getLastName());
          softly
              .assertThat(aPerson.getDateOfBirth())
              .isEqualTo(CreateNewContactSteps.contact.getDateOfBirth());
          softly.assertThat(aPerson.getSex()).isEqualTo(CreateNewContactSteps.contact.getSex());
          softly
              .assertThat(aPerson.getNationalHealthId())
              .isEqualTo(CreateNewContactSteps.contact.getNationalHealthId());
          softly
              .assertThat(aPerson.getPassportNumber())
              .isEqualTo(CreateNewContactSteps.contact.getPassportNumber());
          softly
              .assertThat(aPerson.getEmailAddress())
              .isEqualTo(CreateNewContactSteps.contact.getPrimaryEmailAddress());
          softly
              .assertThat(aPerson.getPhoneNumber())
              .isEqualTo(CreateNewContactSteps.contact.getPrimaryPhoneNumber());
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
          selectEducation(newGeneratedPerson.getEducation());
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
          fillCommunityContactPerson(newGeneratedPerson.getCommunityContactPerson());
          fillBirthName(newGeneratedPerson.getBirthName());
          fillNickName(newGeneratedPerson.getNickname());
          fillMotherMaidenName(newGeneratedPerson.getMotherMaidenName());
          fillMotherName(newGeneratedPerson.getMotherName());
          fillFatherName(newGeneratedPerson.getFatherName());
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          // Workaround created until #5535 is fixed
          baseSteps.getDriver().navigate().refresh();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 120);
          Person contactInfo = getPersonInformation();
          fullyDetailedPerson =
              personService.updateExistentPerson(
                  newGeneratedPerson,
                  contactInfo.getFirstName(),
                  contactInfo.getLastName(),
                  contactInfo.getUuid(),
                  CreateNewContactSteps.contact.getPassportNumber(),
                  CreateNewContactSteps.contact.getNationalHealthId(),
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

  private void fillExternalToken(String token) {
    webDriverHelpers.fillInWebElement(EXTERNAL_TOKEN_INPUT, token);
  }

  private void selectTypeOfOccupation(String occupation) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_OCCUPATION_COMBOBOX, occupation);
  }

  private void selectStaffOfArmedForces(String armedForces) {
    webDriverHelpers.selectFromCombobox(STAFF_OF_ARMED_FORCES_COMBOBOX, armedForces);
  }

  private void selectEducation(String education) {
    webDriverHelpers.selectFromCombobox(EDUCATION_COMBOBOX, education);
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

  private void fillCommunityContactPerson(String name) {
    webDriverHelpers.fillInWebElement(COMMUNITY_CONTACT_PERSON_INPUT, name);
  }

  private void fillBirthName(String name) {
    webDriverHelpers.fillInWebElement(BIRTH_NAME_INPUT, name);
  }

  private void fillNickName(String name) {
    webDriverHelpers.fillInWebElement(NICKNAME_INPUT, name);
  }

  private void fillMotherMaidenName(String name) {
    webDriverHelpers.fillInWebElement(MOTHER_MAIDEN_NAME_INPUT, name);
  }

  private void fillMotherName(String name) {
    webDriverHelpers.fillInWebElement(MOTHER_NAME_INPUT, name);
  }

  private void fillFatherName(String name) {
    webDriverHelpers.fillInWebElement(FATHER_NAME_INPUT, name);
  }

  private void fillNamesOfGuardians(String name) {
    webDriverHelpers.fillInWebElement(NAMES_OF_GUARDIANS_INPUT, name);
  }

  public Person collectPersonData() {
    Person contactInfo = getPersonInformation();

    return Person.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .dateOfBirth(contactInfo.getDateOfBirth())
        .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
        .nationalHealthId(webDriverHelpers.getValueFromWebElement(NATIONAL_HEALTH_ID_INPUT))
        .passportNumber(webDriverHelpers.getValueFromWebElement(PASSPORT_NUMBER_INPUT))
        .emailAddress(webDriverHelpers.getTextFromPresentWebElement(EMAIL_FIELD))
        .phoneNumber(webDriverHelpers.getTextFromPresentWebElement(PHONE_FIELD))
        .build();
  }

  public Person getPersonInformation() {
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
}
