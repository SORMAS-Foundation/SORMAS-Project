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

import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePersonPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.enums.CaseClassification;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.api.demisSteps.DemisSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class EditCasePersonSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Case collectedCase;
  public static Case createdCase;
  private static Case addressData;

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");

  @Inject
  public EditCasePersonSteps(
      final WebDriverHelpers webDriverHelpers, CaseService caseService, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I set death date for person ([^\"]*) month ago",
        (String dateOfDeath) -> {
          LocalDate date = LocalDate.now().minusMonths(Long.parseLong(dateOfDeath));
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(DATE_OF_DEATH_INPUT, formatter.format(date));
        });

    When(
        "I set death date for person ([^\"]*) days ago",
        (String dateOfDeath) -> {
          LocalDate date = LocalDate.now().minusDays(Long.parseLong(dateOfDeath));
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.clearWebElement(DATE_OF_DEATH_INPUT);
          webDriverHelpers.fillInWebElement(DATE_OF_DEATH_INPUT, formatter.format(date));
        });

    When(
        "I change Cause of death to ([^\"]*)",
        (String causeOfDeath) ->
            webDriverHelpers.selectFromCombobox(CASE_OF_DEATH_COMBOBOX, causeOfDeath));

    When(
        "I check if date of outcome is updated for ([^\"]*) month ago",
        (String dateOfDeath) -> {
          String date = webDriverHelpers.getValueFromWebElement(DATE_OF_OUTCOME_INPUT);
          LocalDate deathDate = LocalDate.now().minusMonths(Long.parseLong(dateOfDeath));
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          softly.assertEquals(formatter.format(deathDate), date, "Date is not equal");
          softly.assertAll();
        });

    When(
        "I check if date of outcome is updated for ([^\"]*) days ago",
        (String dateOfDeath) -> {
          String date = webDriverHelpers.getValueFromWebElement(DATE_OF_OUTCOME_INPUT);
          LocalDate deathDate = LocalDate.now().minusDays(Long.parseLong(dateOfDeath));
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          softly.assertEquals(formatter.format(deathDate), date, "Date is not equal");
          softly.assertAll();
        });

    When(
        "I check the created data is correctly displayed on Edit case person page",
        () -> {
          collectedCase = collectCasePersonData();
          createdCase = CreateNewCaseSteps.caze;
          ComparisonHelper.compareEqualFieldsOfEntities(
              createdCase,
              collectedCase,
              List.of(
                  "firstName",
                  "lastName",
                  "presentConditionOfPerson",
                  "sex",
                  "primaryEmailAddress",
                  "dateOfBirth"));
        });

    Then(
        "From Case page I click on Calculate Case Classification button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CALCULATE_CASE_CLASSIFICATION_BUTTON);
        });

    Then(
        "For the current Case the Case Classification value should be {string}",
        (String expectedCaseClassification) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 30);
          String caseClassificationValue =
              webDriverHelpers.getValueFromWebElement(CASE_CLASSIFICATION_INPUT);
          Assert.assertEquals(
              caseClassificationValue,
              CaseClassification.getDeUIValueFor(expectedCaseClassification),
              "Case classification value is wrong");
        });

    When(
        "I set Present condition of Person to ([^\"]*) in Case Person tab",
        (String condition) -> {
          webDriverHelpers.selectFromCombobox(PRESENT_CONDITION_COMBOBOX, condition);
        });

    When(
        "I set Present condition of Person to ([^\"]*) in Person tab",
        (String condition) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEE_CASES_FOR_THIS_PERSON_BUTTON);
          webDriverHelpers.selectFromCombobox(PRESENT_CONDITION_COMBOBOX, condition);
        });

    When(
        "I check if death data fields are available in Case Person tab",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_DEATH_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CASE_OF_DEATH);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DEATH_PLACE_TYPE_COMBOBOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DEATH_PLACE_DESCRIPTION);
        });

    When(
        "I check if buried data fields are available in Case Person tab",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_BURIAL_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(BURIAL_CONDUCTOR_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(BURIAL_PLACE_DESCRIPTION);
        });

    When(
        "I fill specific address data in Case Person tab",
        () -> {
          addressData = caseService.buildAddress();
          selectCountry(addressData.getCountry());
          selectRegion(addressData.getRegion());
          selectDistrict(addressData.getDistrict());
          selectCommunity(addressData.getCommunity());
          selectFacilityCategory(addressData.getFacilityCategory());
          selectFacilityType(addressData.getFacilityType());
          selectFacility(addressData.getFacility());
          fillFacilityNameAndDescription(addressData.getFacilityNameAndDescription());
          fillStreet(addressData.getStreet());
          fillHouseNumber(addressData.getHouseNumber());
          fillAdditionalInformation(addressData.getAdditionalInformation());
          fillPostalCode(addressData.getPostalCode());
          fillCity(addressData.getCity());
          selectAreaType(addressData.getAreaType());
        });

    When(
        "I click on Geocode button to get GPS coordinates in Case Person Tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(GEOCODE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on save button to Save Person data in Case Person Tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on New Contact button in Case Person Tab",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NEW_CONTACT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_CONTACT_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if saved Person data is correct",
        () -> {
          collectedCase =
              collectSpecificCasePersonData(); // TODO: Get and check GPS coordinates for DE
          ComparisonHelper.compareEqualFieldsOfEntities(
              addressData,
              collectedCase,
              List.of(
                  "country",
                  "region",
                  "district",
                  "community",
                  "facilityCategory",
                  "facilityType",
                  "facility",
                  "street",
                  "houseNumber",
                  "postalCode",
                  "city",
                  "areaType"));
        });

    When(
        "I set Facility Category to {string} and  Facility Type to {string}",
        (String facilityCategory, String facilityType) -> {
          selectFacilityCategory(facilityCategory);
          selectFacilityType(facilityType);
        });

    When(
        "I set Region to {string} and District to {string}",
        (String aRegion, String aDistrict) -> {
          selectRegion(aRegion);
          selectDistrict(aDistrict);
        });

    When(
        "^I set case person's sex as ([^\"]*)$",
        (String sex) -> {
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
        });
    When(
        "I check that Type of Contacts details with ([^\"]*) as a option is visible on Edit Case Person Page",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          Boolean elementVisible = true;
          switch (option) {
            case "Primary telephone":
              selector = TELEPHONE_PRIMARY;
              break;
            case "Primary email address":
              selector = EMAIL_PRIMARY;
              break;
          }
          webDriverHelpers.isElementVisibleWithTimeout(selector, 5);
          softly.assertTrue(elementVisible, option + " is not visible!");
          softly.assertAll();
        });

    When(
        "I check that ([^\"]*) is not visible",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          Boolean elementVisible = true;
          switch (option) {
            case "Passport Number":
              selector = PASSPORT_NUMBER_INPUT;
              break;
            case "National Health ID":
              selector = NATIONAL_HEALTH_ID_INPUT;
              break;
            case "Education":
              selector = EDUCATION_COMBOBOX;
              break;
            case "Community Contact Person":
              selector = COMMUNITY_CONTACT_PERSON_INPUT;
              break;
            case "Nickname":
              selector = NICKNAME_INPUT;
              break;
            case "Mother's Maiden Name":
              selector = MOTHERS_MAIDEN_NAME_INPUT;
              break;
            case "Mother's Name":
              selector = MOTHERS_NAME_INPUT;
              break;
            case "Father's Name":
              selector = FATHERS_NAME_INPUT;
              break;
          }
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertFalse(elementVisible, option + " is visible!");
          softly.assertAll();
        });

    When(
        "I check if Present condition of person combobox has value {string}",
        (String option) -> {
          softly.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(PRESENT_CONDITION_COMBOBOX, option));
          softly.assertAll();
        });

    When(
        "I check if Present condition of person combobox has no value {string}",
        (String option) -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(PRESENT_CONDITION_COMBOBOX, option));
          softly.assertAll();
        });

    When(
        "I check if {string} field is present in case person",
        (String option) -> {
          By selector = null;
          switch (option) {
            case "Date of burial":
              selector = DATE_OF_BURIAL_INPUT;
              break;
            case "Cause of death":
              selector = CASE_OF_DEATH_COMBOBOX;
              break;
            case "Burial conductor":
              selector = BURIAL_CONDUCTOR_INPUT;
              break;
            case "Burial place description":
              selector = BURIAL_PLACE_DESCRIPTION;
              break;
          }
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(selector);
        });
    When(
        "I check that ([^\"]*) is not visible in Contact Information section for DE version",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          switch (option) {
            case "Citizenship":
              selector = CITIZENSHIP_LABEL_DE;
              break;
            case "Country of birth":
              selector = COUNTRY_OF_BIRTH_LABEL_DE;
              break;
          }
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(selector, 3), option + " is visible!");
          softly.assertAll();
        });
    When(
        "I set Present condition of person to {string}",
        (String option) -> webDriverHelpers.selectFromCombobox(PRESENT_CONDITION_COMBOBOX, option));

    And(
        "^I check if person last name for case person tab is \"([^\"]*)\"$",
        (String lastName) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT),
              lastName,
              "Last names is incorrect!");
          softly.assertAll();
        });

    And(
        "I check that first and last name are equal to data form {int} result in laboratory notification",
        (Integer resultNumber) -> {
          softly.assertEquals(
              DemisSteps.firstNames.get(resultNumber - 1),
              webDriverHelpers.getValueFromWebElement(FIRST_NAME_INPUT),
              "First name is incorrect!");
          softly.assertAll();
          softly.assertEquals(
              DemisSteps.lastNames.get(resultNumber - 1),
              webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT),
              "Last name is incorrect!");
          softly.assertAll();
        });

    And(
        "^I check if editable fields are read only for person case/contact tab$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
          webDriverHelpers.isElementGreyedOut(UUID_INPUT);
          webDriverHelpers.isElementGreyedOut(FIRST_NAME_INPUT);
          webDriverHelpers.isElementGreyedOut(LAST_NAME_INPUT);
          webDriverHelpers.isElementGreyedOut(SAVE_BUTTON);
        });
  }

  private void selectCountry(String country) {
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, country);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX, district);
  }

  private void selectCommunity(String community) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX_BY_PLACE_OF_STAY, community);
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

  private void fillFacilityNameAndDescription(String facilityDescription) {
    webDriverHelpers.fillInWebElement(FACILITY_DETAILS_INPUT, facilityDescription);
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

  private Case collectCasePersonData() {
    webDriverHelpers.scrollToElement(CASE_PERSON_TAB);
    webDriverHelpers.clickOnWebElementBySelector(CASE_PERSON_TAB);
    return Case.builder()
        .firstName(webDriverHelpers.getValueFromWebElement(FIRST_NAME_INPUT))
        .lastName(webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT))
        .dateOfBirth(getUserBirthDate())
        .presentConditionOfPerson(webDriverHelpers.getValueFromWebElement(PRESENT_CONDITION_INPUT))
        .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
        .primaryPhoneNumber(webDriverHelpers.getTextFromPresentWebElement(PHONE_FIELD))
        .primaryEmailAddress(webDriverHelpers.getTextFromPresentWebElement(EMAIL_FIELD))
        .build();
  }

  private Case collectSpecificCasePersonData() {
    return Case.builder()
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .region(webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX_BY_PLACE_OF_STAY))
        .facilityCategory(webDriverHelpers.getValueFromCombobox(FACILITY_CATEGORY_COMBOBOX))
        .facilityType(webDriverHelpers.getValueFromCombobox(FACILITY_TYPE_COMBOBOX))
        .facility(webDriverHelpers.getValueFromCombobox(FACILITY_COMBOBOX))
        .facilityNameAndDescription(webDriverHelpers.getValueFromWebElement(FACILITY_DETAILS_INPUT))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromCombobox(AREA_TYPE_COMBOBOX))
        .build();
  }

  private LocalDate getUserBirthDate() {
    final String year = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_YEAR_INPUT);
    final String month = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_MONTH_INPUT);
    final String day = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_DAY_INPUT);
    final String date = month + "/" + day + "/" + year;
    return LocalDate.parse(date, DATE_FORMATTER);
  }
}
