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

package org.sormas.e2etests.steps.web.application.users;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DETAILED_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DETAILED_TABLE_DATA;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DETAILED_TABLE_ROWS;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.LOGOUT_BUTTON;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.*;

import cucumber.api.java8.En;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.User;
import org.sormas.e2etests.entities.services.UserService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.LoginPage;
import org.sormas.e2etests.pages.application.users.CreateNewUserPage;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.CaseDetailedTableViewHeaders;
import org.testng.asserts.SoftAssert;

public class CreateNewUserSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static User user;
  public static User editUser;
  public static String userName;
  public static String userPass;
  private final BaseSteps baseSteps;

  @Inject
  public CreateNewUserSteps(
      WebDriverHelpers webDriverHelpers,
      UserService userService,
      BaseSteps baseSteps,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "I create new ([^\"]*) with limited disease to ([^\"]*)",
        (String role, String disease) -> {
          user = userService.buildGeneratedUserWithRoleAndDisease(role, disease);
          fillFirstName(user.getFirstName());
          fillLastName(user.getLastName());
          fillEmailAddress(user.getEmailAddress());
          fillPhoneNumber(user.getPhoneNumber());
          selectLanguage(user.getLanguage());
          selectCountry(user.getCountry());
          selectRegion(user.getRegion());
          selectDistrict(user.getDistrict());
          selectCommunity(user.getCommunity());
          selectFacilityCategory(user.getFacilityCategory());
          selectFacilityType(user.getFacilityType());
          selectFacility(user.getFacility());
          fillFacilityNameAndDescription(user.getFacilityNameAndDescription());
          fillStreet(user.getStreet());
          fillHouseNr(user.getHouseNumber());
          fillAdditionalInformation(user.getAdditionalInformation());
          fillPostalCode(user.getPostalCode());
          fillCity(user.getCity());
          selectAreaType(user.getAreaType());
          fillGpsLatitude(user.getGpsLatitude());
          fillGpsLongitude(user.getGpsLongitude());
          fillGpsAccuracy(user.getGpsAccuracy());
          selectActive(user.getActive());
          fillUserName(user.getUserName());
          selectUserRole(role);
          selectLimitedDisease(user.getLimitedDisease());
          userName = user.getUserName();
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          userPass = webDriverHelpers.getTextFromWebElement(PASSWORD_FIELD);
          closeNewPasswordPopUp();
        });

    When(
        "As a new created user with limited disease view I log in",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LoginPage.USER_NAME_INPUT);
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, userName);
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, userPass);
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOGOUT_BUTTON, 30);
        });

    When(
        "I check if user have limited disease view to ([^\"]*) only",
        (String disease) -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          for (int i = 0; i < tableRowsData.size(); i++) {
            Map<String, String> detailedCaseDTableRow = tableRowsData.get(i);
            softly.assertTrue(
                detailedCaseDTableRow
                    .get(CaseDetailedTableViewHeaders.DISEASE.toString())
                    .contains(disease),
                "Disease is not correct");
            softly.assertAll();
          }
        });

    When(
        "^I create a new user with ([^\"]*)$",
        (String role) -> {
          user = userService.buildGeneratedUserWithRole(role);
          fillFirstName(user.getFirstName());
          fillLastName(user.getLastName());
          fillEmailAddress(user.getEmailAddress());
          fillPhoneNumber(user.getPhoneNumber());
          selectLanguage(user.getLanguage());
          selectCountry(user.getCountry());
          selectRegion(user.getRegion());
          selectDistrict(user.getDistrict());
          selectCommunity(user.getCommunity());
          selectFacilityCategory(user.getFacilityCategory());
          selectFacilityType(user.getFacilityType());
          selectFacility(user.getFacility());
          fillFacilityNameAndDescription(user.getFacilityNameAndDescription());
          fillStreet(user.getStreet());
          fillHouseNr(user.getHouseNumber());
          fillAdditionalInformation(user.getAdditionalInformation());
          fillPostalCode(user.getPostalCode());
          fillCity(user.getCity());
          selectAreaType(user.getAreaType());
          fillGpsLatitude(user.getGpsLatitude());
          fillGpsLongitude(user.getGpsLongitude());
          fillGpsAccuracy(user.getGpsAccuracy());
          selectActive(user.getActive());
          fillUserName(user.getUserName());
          selectUserRole(role);
          selectLimitedDisease(user.getLimitedDisease());
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          closeNewPasswordPopUp();
        });

    And(
        "^I change user data and save the changes$",
        () -> {
          editUser = userService.buildEditUser();
          fillFirstName(editUser.getFirstName());
          fillLastName(editUser.getLastName());
          fillEmailAddress(editUser.getEmailAddress());
          fillPhoneNumber(editUser.getPhoneNumber());
          selectLanguage(editUser.getLanguage());
          selectCountry(editUser.getCountry());
          selectRegion(editUser.getRegion());
          selectDistrict(editUser.getDistrict());
          selectCommunity(editUser.getCommunity());
          selectFacilityCategory(editUser.getFacilityCategory());
          selectFacilityType(editUser.getFacilityType());
          selectFacility(editUser.getFacility());
          fillFacilityNameAndDescription(editUser.getFacilityNameAndDescription());
          fillStreet(editUser.getStreet());
          fillHouseNr(editUser.getHouseNumber());
          fillAdditionalInformation(editUser.getAdditionalInformation());
          fillPostalCode(editUser.getPostalCode());
          fillCity(editUser.getCity());
          selectAreaType(editUser.getAreaType());
          fillGpsLatitude(editUser.getGpsLatitude());
          fillGpsLongitude(editUser.getGpsLongitude());
          fillGpsAccuracy(editUser.getGpsAccuracy());
          fillUserName(editUser.getUserName());
          selectActive(user.getActive());
          selectActiveUserRole();
          selectUserRole(editUser.getUserRole());
          selectLimitedDisease(editUser.getLimitedDisease());
          webDriverHelpers.scrollToElement(CreateNewUserPage.SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CreateNewUserPage.SAVE_BUTTON);
        });
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_OF_USER_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_OF_USER_INPUT, lastName);
  }

  private void fillEmailAddress(String emailAddress) {
    webDriverHelpers.fillInWebElement(EMAIL_ADDRESS_INPUT, emailAddress);
  }

  private void fillPhoneNumber(String phoneNr) {
    webDriverHelpers.fillInWebElement(PHONE_INPUT, phoneNr);
  }

  private void selectLanguage(String language) {
    webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, language);
  }

  private void selectCountry(String country) {
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, country);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISTRICT_COMBOBOX);
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  private void selectCommunity(String community) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(COMMUNITY_COMBOBOX);
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  private void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_CATEGORY_COMBOBOX);
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  private void selectFacilityType(String facilityType) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE_COMBOBOX);
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void selectFacility(String facility) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_COMBOBOX);
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, facility);
  }

  private void fillFacilityNameAndDescription(String facilityName) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_DESCRIPTION, facilityName);
  }

  private void fillStreet(String street) {
    webDriverHelpers.fillInWebElement(STREET_INPUT, street);
  }

  private void fillHouseNr(String houseNr) {
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, houseNr);
  }

  private void fillAdditionalInformation(String additionalInformation) {
    webDriverHelpers.fillInWebElement(ADDITIONAL_INFORMATION_INPUT, additionalInformation);
  }

  private void fillPostalCode(String postalCode) {
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, postalCode);
  }

  private void fillCity(String city) {
    webDriverHelpers.fillInWebElement(CITY_INPUT, city);
  }

  private void selectAreaType(String areaType) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, areaType);
  }

  private void fillGpsLatitude(String gpsLatitude) {
    webDriverHelpers.fillInWebElement(LATITUDE_INPUT, gpsLatitude);
  }

  private void fillGpsLongitude(String gpsLongitude) {
    webDriverHelpers.fillInWebElement(LONGITUDE_INPUT, gpsLongitude);
  }

  private void fillGpsAccuracy(String gpsAccuracy) {
    webDriverHelpers.fillInWebElement(LAT_LON_ACCURACY_INPUT, gpsAccuracy);
  }

  private void selectActive(boolean active) {
    if (active && !webDriverHelpers.getWebElement(ACTIVE_CHECKBOX).isEnabled()) {
      webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(ACTIVE_CHECKBOX);
      webDriverHelpers.clickOnWebElementBySelector(ACTIVE_CHECKBOX);
    }
  }

  private void selectActiveUserRole() {
    webDriverHelpers.clickWebElementByText(
        USER_ROLE_CHECKBOX,
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(USER_ROLE_CHECKBOX));
  }

  private void fillUserName(String userName) {
    webDriverHelpers.fillInWebElement(USER_NAME_INPUT, userName);
  }

  private void selectUserRole(String role) {
    webDriverHelpers.clickWebElementByText(USER_ROLE_CHECKBOX, role);
  }

  private void closeNewPasswordPopUp() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CLOSE_DIALOG_BUTTON, 15);
    webDriverHelpers.clickOnWebElementBySelector(CLOSE_DIALOG_BUTTON);
  }

  private void selectLimitedDisease(String limitedDisease) {
    webDriverHelpers.scrollToElement(LIMITED_DISEASE_COMBOBOX);
    webDriverHelpers.selectFromCombobox(LIMITED_DISEASE_COMBOBOX, limitedDisease);
  }

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(CASE_DETAILED_TABLE_DATA);
          tableData.forEach(
              dataText -> {
                webDriverHelpers.scrollToElementUntilIsVisible(dataText);
                indexWithData.put(atomicInt.getAndIncrement(), dataText.getText());
              });
          tableDataList.add(indexWithData);
        });
    List<Map<String, String>> tableObjects = new ArrayList<>();
    tableDataList.forEach(
        row -> {
          ConcurrentHashMap<String, String> objects = new ConcurrentHashMap<>();
          headers.forEach((headerText, index) -> objects.put(headerText, row.get(index)));
          tableObjects.add(objects);
        });
    return tableObjects;
  }

  private List<WebElement> getTableRows() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CASE_DETAILED_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(CASE_DETAILED_TABLE_ROWS);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CASE_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(CASE_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(CASE_DETAILED_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(CASE_DETAILED_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
