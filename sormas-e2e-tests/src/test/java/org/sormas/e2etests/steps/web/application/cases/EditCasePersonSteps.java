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
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.enums.CaseClassification;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;

public class EditCasePersonSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Case collectedCase;
  protected Case createdCase;
  private static Case addressData;

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM/d/yyyy");

  @Inject
  public EditCasePersonSteps(final WebDriverHelpers webDriverHelpers, CaseService caseService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I set death date for person ([^\"]*) month ago",
        (String dateOfDeath) -> {
          LocalDate date = LocalDate.now().minusMonths(Long.parseLong(dateOfDeath));
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(DATE_OF_DEATH_INPUT, formatter.format(date));
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
              CaseClassification.getUIValueFor(expectedCaseClassification),
              "Case classification value is wrong");
        });

    When(
        "I set Present condition of Person to ([^\"]*) in Case Person tab",
        (String condition) ->
            webDriverHelpers.selectFromCombobox(PRESENT_CONDITION_COMBOBOX, condition));

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
        () -> webDriverHelpers.clickOnWebElementBySelector(GEOCODE_BUTTON));

    When(
        "I click on save button to Save Person data in Case Person Tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
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
