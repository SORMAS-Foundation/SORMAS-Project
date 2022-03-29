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

package org.sormas.e2etests.steps.web.application.persons;

import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.CONTACT_PERSON_FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.CONTACT_PERSON_LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPersonPage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.ADDITIONAL_INFORMATION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.AREA_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.AREA_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.BIRTH_NAME_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.CITY_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DATE_OF_BIRTH_DAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DATE_OF_BIRTH_MONTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DATE_OF_BIRTH_YEAR_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EDIT_CASES_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EDIT_CONTACTS_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EMAIL_FIELD;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.ERROR_INDICATOR;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EXTERNAL_TOKEN_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_CATEGORY_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_NAME_AND_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.HOUSE_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.INVALID_DATA_ERROR;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.NAMES_OF_GUARDIANS_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PERSON_CONTACT_DETAILS_CONTACT_INFORMATION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PERSON_CONTACT_DETAILS_TYPE_OF_DETAILS_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PHONE_FIELD;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.POSTAL_CODE_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PRESENT_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PRESENT_CONDITION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.SALUTATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.SALUTATION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.SEE_CASES_FOR_PERSON_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.SEE_CONTACTS_FOR_PERSON_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.SEE_EVENTS_FOR_PERSON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.SEX_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.STAFF_OF_ARMED_FORCES_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.STAFF_OF_ARMED_FORCES_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.STREET_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.TYPE_OF_OCCUPATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.TYPE_OF_OCCUPATION_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.getByImmunizationUuid;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.ElementClickInterceptedException;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Person;
import org.sormas.e2etests.entities.services.PersonService;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.contacts.EditContactPersonSteps;
import org.sormas.e2etests.steps.web.application.events.EditEventSteps;
import org.testng.Assert;

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
      AssertHelpers assertHelpers,
      ApiState apiState,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check that previous created person is correctly displayed in Edit Person page",
        () -> {
          previousCreatedPerson = EditContactPersonSteps.fullyDetailedPerson;
          collectedPerson = collectPersonData();
          ComparisonHelper.compareEqualEntities(previousCreatedPerson, collectedPerson);
        });

    When(
        "I check that previous edited person is correctly displayed in Edit Person page",
        () -> {
          collectedPerson = collectPersonData();
          ComparisonHelper.compareDifferentFieldsOfEntities(
              previousCreatedPerson,
              collectedPerson,
              List.of(
                  "firstName",
                  "lastName",
                  "externalId",
                  "externalToken",
                  "street",
                  "houseNumber",
                  "city",
                  "postalCode",
                  "contactPersonFirstName",
                  "contactPersonLastName",
                  "birthName",
                  "nameOfGuardians"));
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
          fillExternalId(newGeneratedPerson.getExternalId());
          fillExternalToken(newGeneratedPerson.getExternalToken());
          selectTypeOfOccupation(newGeneratedPerson.getTypeOfOccupation());
          TimeUnit.SECONDS.sleep(3);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          selectStaffOfArmedForces(newGeneratedPerson.getStaffOfArmedForces());
          selectRegion(newGeneratedPerson.getRegion());
          selectDistrict(newGeneratedPerson.getDistrict());
          selectCommunity(newGeneratedPerson.getCommunity());
          selectFacilityCategory(newGeneratedPerson.getFacilityCategory());
          selectFacilityType(newGeneratedPerson.getFacilityType());
          selectFacility(newGeneratedPerson.getFacility());
          fillFacilityNameAndDescription(newGeneratedPerson.getFacilityNameAndDescription());
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
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
        "I click on See Cases for this Person button from Edit Person page",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
          webDriverHelpers.clickOnWebElementBySelector(SEE_CASES_FOR_PERSON_BUTTON);
          //          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_NAVIGATION_BUTTON);
          //          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
        });
    Then(
        "I click on See CONTACTS for this Person button from Edit Person page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SEE_CONTACTS_FOR_PERSON_BUTTON);
          //    webDriverHelpers.clickOnWebElementBySelector(CONFIRM_NAVIGATION_BUTTON);
          //    webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
        });

    Then(
        "I click on Edit Case button from Cases card on Edit Person page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_CASES_BUTTON);
          //  webDriverHelpers.clickOnWebElementBySelector(CONFIRM_NAVIGATION_BUTTON);
          //  webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
        });
    Then(
        "I click on Edit Contact button from Contacts card on Edit Person page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_CONTACTS_BUTTON);
          //  webDriverHelpers.clickOnWebElementBySelector(CONFIRM_NAVIGATION_BUTTON);
          //  webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
        });
    Then(
        "I click on Edit Immunization button for Immunization created through API from Immunization card on Edit Person page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              getByImmunizationUuid(apiState.getCreatedImmunization().getUuid()));
          //   webDriverHelpers.clickOnWebElementBySelector(CONFIRM_NAVIGATION_BUTTON);
          //   webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
        });

    Then(
        "I click on save button from Edit Person page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
          webDriverHelpers.clickOnWebElementBySelector(UUID_INPUT);
          previousCreatedPerson = collectedPerson;
        });

    When(
        "I check if event is available at person information",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEE_EVENTS_FOR_PERSON);
          webDriverHelpers.clickOnWebElementBySelector(SEE_EVENTS_FOR_PERSON);
          final String eventUuid = EditEventSteps.collectedEvent.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(eventUuid));
        });

    When(
        "I navigate via URL to last Person created from edit Event page",
        () -> {
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.accessWebSite(
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!persons/data/"
                  + personUuid);
        });

    When(
        "I clear the mandatory Person fields",
        () -> {
          webDriverHelpers.clearWebElement(FIRST_NAME_INPUT);
          webDriverHelpers.clearWebElement(LAST_NAME_INPUT);
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, "");
        });

    When(
        "I clear Region and District fields from Person",
        () -> {
          webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, "");
          webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, "");
        });

    When(
        "^I check that an invalid data error message appears$",
        () -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertTrue(
                      webDriverHelpers.isElementVisibleWithTimeout(INVALID_DATA_ERROR, 10),
                      "Missing mandatory fields error message is not displayed"));
          webDriverHelpers.clickOnWebElementBySelector(INVALID_DATA_ERROR);
        });

    Then(
        "I fill in the home address, facility category and type in the Home Address section of the Edit Person Page",
        () -> {
          newGeneratedPerson = personService.buildGeneratedPerson();
          selectFacilityCategory(newGeneratedPerson.getFacilityCategory());
          selectFacilityType(newGeneratedPerson.getFacilityType());
          fillStreet(newGeneratedPerson.getStreet());
          fillHouseNumber(newGeneratedPerson.getHouseNumber());
          fillAdditionalInformation(newGeneratedPerson.getAdditionalInformation());
          fillPostalCode(newGeneratedPerson.getPostalCode());
          fillCity(newGeneratedPerson.getCity());
          selectAreaType(newGeneratedPerson.getAreaType());
        });

    When(
        "^I check that an error highlight appears above the facility combobox$",
        () -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertTrue(
                      webDriverHelpers.isElementVisibleWithTimeout(ERROR_INDICATOR, 10),
                      "Facility highlight error message wasn't displayed"));
        });
  }

  private void fillFirstName(String firstName) {
    try {
      webDriverHelpers.clearAndFillInWebElement(FIRST_NAME_INPUT, firstName);
    } catch (ElementClickInterceptedException elementClickInterceptedException) {
      webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
      webDriverHelpers.clearAndFillInWebElement(FIRST_NAME_INPUT, firstName);
    }
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

  private void fillBirthName(String name) {
    webDriverHelpers.clearAndFillInWebElement(BIRTH_NAME_INPUT, name);
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
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .externalToken(webDriverHelpers.getValueFromWebElement(EXTERNAL_TOKEN_INPUT))
        .typeOfOccupation(webDriverHelpers.getValueFromWebElement(TYPE_OF_OCCUPATION_INPUT))
        .staffOfArmedForces(webDriverHelpers.getValueFromWebElement(STAFF_OF_ARMED_FORCES_INPUT))
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
        .birthName(webDriverHelpers.getValueFromWebElement(BIRTH_NAME_INPUT))
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

  private Person getPersonInformation() {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("M/d/yyyy").localizedBy(Locale.ENGLISH);
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(USER_INFORMATION, 60);
    String contactInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
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
