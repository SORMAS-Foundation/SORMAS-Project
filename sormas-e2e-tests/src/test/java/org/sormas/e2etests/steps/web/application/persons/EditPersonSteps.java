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

package org.sormas.e2etests.steps.web.application.persons;

import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.comparators.PersonComparator;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Person;
import org.sormas.e2etests.services.PersonService;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.contacts.EditContactPersonSteps;
import org.sormas.e2etests.steps.web.application.events.EditEventSteps;

public class EditPersonSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Person previousCreatedPerson = null;
  protected Person collectedPerson;
  public static Person newGeneratedPerson;

  @Inject
  public EditPersonSteps(
      WebDriverHelpers webDriverHelpers,
      PersonService personService,
      BaseSteps baseSteps,
      PersonComparator personComparator,
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check that previous created person is correctly displayed in Edit Person page",
        () -> {
          previousCreatedPerson = EditContactPersonSteps.fullyDetailedPerson;
          collectedPerson = collectPersonData();
          personComparator.comparePersonsAreEqual(previousCreatedPerson, collectedPerson);
        });

    When(
        "I check that previous edited person is correctly displayed in Edit Person page",
        () -> {
          collectedPerson = collectPersonData();
          personComparator.checkPersonAreDifferent(previousCreatedPerson, collectedPerson);
        });

    Then(
        "While on Person edit page, I will edit all fields with new values",
        () -> {
          newGeneratedPerson = personService.buildGeneratedPerson();
          fillFirstName(newGeneratedPerson.getFirstName());
          fillLastName(newGeneratedPerson.getLastName());
          fillSalutation(newGeneratedPerson.getSalutation());
          fillDateOfBirth(newGeneratedPerson.getDateOfBirth());
          selectSex(newGeneratedPerson.getSex());
          selectPresentConditionOfPerson(newGeneratedPerson.getPresentConditionOfPerson());
          fillNationalHealthId(newGeneratedPerson.getNationalHealthId());
          fillNationalHealthId(newGeneratedPerson.getNationalHealthId());
          fillPassportNumber(newGeneratedPerson.getPassportNumber());
          fillExternalId(newGeneratedPerson.getExternalId());
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
        "I click on save button from Edit Person page",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
          // Workaround created until #5535 is fixed
          baseSteps.getDriver().navigate().refresh();
          webDriverHelpers.waitForPageLoaded();
          previousCreatedPerson = collectedPerson;
        });

    When(
        "I check if event is available at person information",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEE_EVENTS_FOR_PERSON);
          webDriverHelpers.clickOnWebElementBySelector(SEE_EVENTS_FOR_PERSON);
          final String eventUuid = EditEventSteps.event.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(eventUuid));
        });

    When(
        "I navigate via URL to last Person created from edit Event page",
        () -> {
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.accessWebSite(
              environmentUrl + "/sormas-ui/#!persons/data/" + personUuid);
        });
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.clearAndFillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.clearAndFillInWebElement(LAST_NAME_INPUT, lastName);
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

  private void fillNationalHealthId(String nationalHealthId) {
    webDriverHelpers.clearAndFillInWebElement(NATIONAL_HEALTH_ID_INPUT, nationalHealthId);
  }

  private void fillPassportNumber(String passportNumber) {
    webDriverHelpers.clearAndFillInWebElement(PASSPORT_NUMBER_INPUT, passportNumber);
  }

  private void fillExternalId(String id) {
    webDriverHelpers.clearAndFillInWebElement(EXTERNAL_ID_INPUT, id);
  }

  private void fillExternalToken(String token) {
    webDriverHelpers.clearAndFillInWebElement(EXTERNAL_TOKEN_INPUT, token);
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
    webDriverHelpers.clearAndFillInWebElement(FACILITY_NAME_AND_DESCRIPTION_INPUT, description);
  }

  private void fillStreet(String street) {
    webDriverHelpers.clearAndFillInWebElement(STREET_INPUT, street);
  }

  private void fillHouseNumber(String houseNumber) {
    webDriverHelpers.clearAndFillInWebElement(HOUSE_NUMBER_INPUT, houseNumber);
  }

  private void fillAdditionalInformation(String info) {
    webDriverHelpers.clearAndFillInWebElement(ADDITIONAL_INFORMATION_INPUT, info);
  }

  private void fillPostalCode(String code) {
    webDriverHelpers.clearAndFillInWebElement(POSTAL_CODE_INPUT, code);
  }

  private void fillCity(String city) {
    webDriverHelpers.clearAndFillInWebElement(CITY_INPUT, city);
  }

  private void selectAreaType(String areaType) {
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, areaType);
  }

  private void fillContactPersonFirstName(String first) {
    webDriverHelpers.clearAndFillInWebElement(CONTACT_PERSON_FIRST_NAME_INPUT, first);
  }

  private void fillContactPersonLastName(String last) {
    webDriverHelpers.clearAndFillInWebElement(CONTACT_PERSON_LAST_NAME_INPUT, last);
  }

  private void fillCommunityContactPerson(String name) {
    webDriverHelpers.clearAndFillInWebElement(COMMUNITY_CONTACT_PERSON_INPUT, name);
  }

  private void fillBirthName(String name) {
    webDriverHelpers.clearAndFillInWebElement(BIRTH_NAME_INPUT, name);
  }

  private void fillNickName(String name) {
    webDriverHelpers.clearAndFillInWebElement(NICKNAME_INPUT, name);
  }

  private void fillMotherMaidenName(String name) {
    webDriverHelpers.clearAndFillInWebElement(MOTHER_MAIDEN_NAME_INPUT, name);
  }

  private void fillMotherName(String name) {
    webDriverHelpers.clearAndFillInWebElement(MOTHER_NAME_INPUT, name);
  }

  private void fillFatherName(String name) {
    webDriverHelpers.clearAndFillInWebElement(FATHER_NAME_INPUT, name);
  }

  private void fillNamesOfGuardians(String name) {
    webDriverHelpers.clearAndFillInWebElement(NAMES_OF_GUARDIANS_INPUT, name);
  }

  public Person collectPersonData() {
    Person contactInfo = getPersonInformation();

    return Person.builder()
        .firstName(contactInfo.getFirstName())
        .lastName(contactInfo.getLastName())
        .dateOfBirth(contactInfo.getDateOfBirth())
        .uuid(contactInfo.getUuid())
        .salutation(webDriverHelpers.getValueFromWebElement(SALUTATION_INPUT))
        .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
        .presentConditionOfPerson(webDriverHelpers.getValueFromWebElement(PRESENT_CONDITION_INPUT))
        .nationalHealthId(webDriverHelpers.getValueFromWebElement(NATIONAL_HEALTH_ID_INPUT))
        .passportNumber(webDriverHelpers.getValueFromWebElement(PASSPORT_NUMBER_INPUT))
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .externalToken(webDriverHelpers.getValueFromWebElement(EXTERNAL_TOKEN_INPUT))
        .typeOfOccupation(webDriverHelpers.getValueFromWebElement(TYPE_OF_OCCUPATION_INPUT))
        .staffOfArmedForces(webDriverHelpers.getValueFromWebElement(STAFF_OF_ARMED_FORCES_INPUT))
        .education(webDriverHelpers.getValueFromWebElement(EDUCATION_INPUT))
        .region(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .district(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .community(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .facilityCategory(webDriverHelpers.getValueFromWebElement(FACILITY_CATEGORY_INPUT))
        .facilityType(webDriverHelpers.getValueFromWebElement(FACILITY_TYPE_INPUT))
        .facility(webDriverHelpers.getValueFromWebElement(FACILITY_INPUT))
        .facilityNameAndDescription(
            webDriverHelpers.getValueFromWebElement(FACILITY_NAME_AND_DESCRIPTION_INPUT))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .additionalInformation(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromWebElement(AREA_TYPE_INPUT))
        .contactPersonFirstName(
            webDriverHelpers.getValueFromWebElement(CONTACT_PERSON_FIRST_NAME_INPUT))
        .contactPersonLastName(
            webDriverHelpers.getValueFromWebElement(CONTACT_PERSON_LAST_NAME_INPUT))
        .communityContactPerson(
            webDriverHelpers.getValueFromWebElement(COMMUNITY_CONTACT_PERSON_INPUT))
        .birthName(webDriverHelpers.getValueFromWebElement(BIRTH_NAME_INPUT))
        .nickname(webDriverHelpers.getValueFromWebElement(NICKNAME_INPUT))
        .motherMaidenName(webDriverHelpers.getValueFromWebElement(MOTHER_MAIDEN_NAME_INPUT))
        .motherName(webDriverHelpers.getValueFromWebElement(MOTHER_NAME_INPUT))
        .fatherName(webDriverHelpers.getValueFromWebElement(FATHER_NAME_INPUT))
        .nameOfGuardians(webDriverHelpers.getValueFromWebElement(NAMES_OF_GUARDIANS_INPUT))
        .personContactDetailsContactInformation(
            webDriverHelpers.getTextFromPresentWebElement(
                PERSON_CONTACT_DETAILS_CONTACT_INFORMATION_INPUT))
        .personContactDetailsTypeOfContactDetails(
            webDriverHelpers
                .getTextFromPresentWebElement(PERSON_CONTACT_DETAILS_TYPE_OF_DETAILS_INPUT)
                .trim())
        .phoneNumber(webDriverHelpers.getTextFromWebElement(PHONE_FIELD))
        .emailAddress(webDriverHelpers.getTextFromWebElement(EMAIL_FIELD))
        .build();
  }

  public Person getPersonInformation() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(USER_INFORMATION, 60);
    String contactInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String uuid = webDriverHelpers.getValueFromWebElement(UUID_INPUT);
    String[] personInfos = contactInfo.split(" ");
    LocalDate localDate = LocalDate.parse(personInfos[3].replace(")", ""), formatter);
    return Person.builder()
        .firstName(personInfos[0])
        .lastName(personInfos[1])
        .dateOfBirth(localDate)
        .uuid(uuid)
        .build();
  }
}
