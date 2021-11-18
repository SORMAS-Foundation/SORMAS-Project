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

package org.sormas.e2etests.steps.web.application.users;

import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.users.CreateNewUserPage;
import org.sormas.e2etests.pojo.User;
import org.sormas.e2etests.services.UserService;

public class CreateNewUserSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static User user;
  public static User editUser;

  @Inject
  public CreateNewUserSteps(WebDriverHelpers webDriverHelpers, UserService userService) {
    this.webDriverHelpers = webDriverHelpers;
    When(
        "^I create a new user with ([^\"]*)$",
        (String rights) -> {
          user = userService.buildGeneratedUser();
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
          selectUserRole(rights);
          selectLimitedDisease(user.getLimitedDisease());
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
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

  public void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_OF_USER_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_OF_USER_INPUT, lastName);
  }

  public void fillEmailAddress(String emailAddress) {
    webDriverHelpers.fillInWebElement(EMAIL_ADDRESS_INPUT, emailAddress);
  }

  public void fillPhoneNumber(String phoneNr) {
    webDriverHelpers.fillInWebElement(PHONE_INPUT, phoneNr);
  }

  public void selectLanguage(String language) {
    webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, language);
  }

  public void selectCountry(String country) {
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, country);
  }

  public void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  public void selectDistrict(String district) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISTRICT_COMBOBOX);
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  public void selectCommunity(String community) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(COMMUNITY_COMBOBOX);
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  public void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_CATEGORY_COMBOBOX);
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  public void selectFacilityType(String facilityType) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE_COMBOBOX);
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  public void selectFacility(String facility) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_COMBOBOX);
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, facility);
  }

  public void fillFacilityNameAndDescription(String facilityName) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_DESCRIPTION, facilityName);
  }

  public void fillStreet(String street) {
    webDriverHelpers.fillInWebElement(STREET_INPUT, street);
  }

  public void fillHouseNr(String houseNr) {
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, houseNr);
  }

  public void fillAdditionalInformation(String additionalInformation) {
    webDriverHelpers.fillInWebElement(ADDITIONAL_INFORMATION_INPUT, additionalInformation);
  }

  public void fillPostalCode(String postalCode) {
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, postalCode);
  }

  public void fillCity(String city) {
    webDriverHelpers.fillInWebElement(CITY_INPUT, city);
  }

  public void selectAreaType(String areaType) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, areaType);
  }

  public void fillCommunityContactPerson(String communityContactPerson) {
    webDriverHelpers.fillInWebElement(COMMUNITY_CONTACT_PERSON_INPUT, communityContactPerson);
  }

  public void fillGpsLatitude(String gpsLatitude) {
    webDriverHelpers.fillInWebElement(LATITUDE_INPUT, gpsLatitude);
  }

  public void fillGpsLongitude(String gpsLongitude) {
    webDriverHelpers.fillInWebElement(LONGITUDE_INPUT, gpsLongitude);
  }

  public void fillGpsAccuracy(String gpsAccuracy) {
    webDriverHelpers.fillInWebElement(LAT_LON_ACCURACY_INPUT, gpsAccuracy);
  }

  public void selectActive(String active) {
    webDriverHelpers.clickWebElementByText(ACTIVE_CHECKBOX, active);
  }

  public void selectActiveUserRole() {
    webDriverHelpers.clickWebElementByText(
        USER_ROLE_CHECKBOX,
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(USER_ROLE_CHECKBOX));
  }

  public void fillUserName(String userName) {
    webDriverHelpers.fillInWebElement(USER_NAME_INPUT, userName);
  }

  public void selectUserRole(String role) {
    webDriverHelpers.clickWebElementByText(USER_ROLE_CHECKBOX, role);
  }

  public void closeNewPasswordPopUp() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CLOSE_DIALOG_BUTTON, 15);
    webDriverHelpers.clickOnWebElementBySelector(CLOSE_DIALOG_BUTTON);
  }

  public void selectLimitedDisease(String limitedDisease) {
    webDriverHelpers.scrollToElement(LIMITED_DISEASE_COMBOBOX);
    webDriverHelpers.selectFromCombobox(LIMITED_DISEASE_COMBOBOX, limitedDisease);
  }
}
