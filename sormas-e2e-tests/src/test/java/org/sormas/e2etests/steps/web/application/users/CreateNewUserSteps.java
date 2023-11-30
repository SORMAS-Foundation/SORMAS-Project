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

import static org.sormas.e2etests.pages.application.NavBarPage.ACTION_CONFIRM_GDPR_POPUP_DE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.LOGOUT_BUTTON;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.*;
import static org.sormas.e2etests.pages.application.users.UserManagementPage.FIRST_EDIT_BUTTON_FROM_LIST;
import static org.sormas.e2etests.pages.application.users.UserManagementPage.NEW_USER_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserManagementPage.getUserRoleLabelByCaption;

import cucumber.api.java8.En;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.common.User;
import org.sormas.e2etests.entities.services.UserService;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.LoginPage;
import org.sormas.e2etests.pages.application.users.CreateNewUserPage;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.CaseDetailedTableViewHeaders;
import org.testng.asserts.SoftAssert;

public class CreateNewUserSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static User user;
  public static List<User> createdUsers;
  public static User editUser;
  public static String userName;
  public static String userPass;
  private final BaseSteps baseSteps;
  public static HashMap<String, String> userWithRegion = new HashMap<String, String>();

  @Inject
  public CreateNewUserSteps(
      WebDriverHelpers webDriverHelpers,
      UserService userService,
      BaseSteps baseSteps,
      AssertHelpers assertHelpers,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "^I search after users that were created on the same period of time$",
        () -> {
          String userNameFromEditUser = user.getUserName().substring(0, 19);
          webDriverHelpers.fillInWebElement(USER_INPUT_SEARCH, userNameFromEditUser);
          TimeUnit.SECONDS.sleep(5); // wait for page loaded
        });

    When(
        "I click Enter Bulk Edit Mode on Users directory page",
        () -> webDriverHelpers.clickOnWebElementBySelector(ENTER_BULK_EDIT_MODE));

    When(
        "I click checkbox to choose first {int} User results",
        (Integer index) -> {
          for (int i = 2; i <= index + 1; i++) {
            webDriverHelpers.clickOnWebElementBySelector(getTableRowByIndex(i));
          }
        });

    When(
        "I pick {string} value for Active filter in User Directory",
        (String activeValue) -> {
          webDriverHelpers.selectFromCombobox(ACTIVE_USER_COMBOBOX, activeValue);
          TimeUnit.SECONDS.sleep(5); // waiting for all users to pick
        });

    When(
        "I click on Bulk Actions combobox on User Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS);
          TimeUnit.SECONDS.sleep(3); // waiting for all users to pick
        });

    When(
        "I click on {string} from Bulk Actions combobox on User Directory Page",
        (String action) -> {
          switch (action) {
            case "Enable":
              webDriverHelpers.clickOnWebElementBySelector(ENABLE_BULK_ACTIONS_VALUES);
              webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POP_UP);
              break;
            case "Disable":
              webDriverHelpers.clickOnWebElementBySelector(DISABLE_BULK_ACTIONS_VALUES);
              webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POP_UP);
              break;
          }
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ENABLE_DISABLE_CONFIRMATION_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(ENABLE_DISABLE_CONFIRMATION_POPUP);
        });

    And(
        "I check that created users are displayed in results grid",
        () -> {
          By userNamesList = By.xpath("//tr/td[6]");
          for (User user : createdUsers) {
            webDriverHelpers.verifyListContainsText(userNamesList, user.getUserName());
          }
        });

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
          selectRestrictDiseases(user.getLimitedDisease());
          userName = user.getUserName();
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          userPass = webDriverHelpers.getTextFromWebElement(PASSWORD_FIELD);
          closeNewPasswordPopUp();
        });

    When(
        "I create new Surveillance Officer with region set to ([^\"]*)",
        (String region) -> {
          user = userService.buildGeneratedUserWithRole("Surveillance Officer");
          fillFirstName(user.getFirstName());
          fillLastName(user.getLastName());
          fillEmailAddress(user.getEmailAddress());
          fillPhoneNumber(user.getPhoneNumber());
          selectLanguage("English");
          selectCountry(user.getCountry());
          fillUserFieldsWithSpecificRegion(region);
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
          selectUserRole("Surveillance Officer");
          selectSecondRegion(region);
          selectSecondDistrict("SK Berlin Charlottenburg-Wilmersdorf");
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
          selectRestrictDiseases(user.getLimitedDisease());
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          closeNewPasswordPopUp();
        });

    When(
        "I create {int} new users with National User via UI",
        (Integer users) -> {
          createdUsers = new ArrayList<>();
          for (int i = 0; i < users; i++) {
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_USER_BUTTON, FIRST_NAME_OF_USER_INPUT);
            user = userService.buildGeneratedUserWithRole("National User");
            createdUsers.add(user);
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
            selectUserRole("National User");
            selectRestrictDiseases(user.getLimitedDisease());
            webDriverHelpers.scrollToElement(SAVE_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
            closeNewPasswordPopUp();
          }
        });

    When(
        "I create a new disabled National User in the Create New User page",
        () -> {
          webDriverHelpers.clickWhileOtherButtonIsDisplayed(
              NEW_USER_BUTTON, FIRST_NAME_OF_USER_INPUT);
          user = userService.buildGeneratedUserWithRole("National User");
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
          fillUserName(user.getUserName());
          selectUserRole("National User");
          selectRestrictDiseases(user.getLimitedDisease());
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
          changeRestrictDiseases(user.getLimitedDisease(), editUser.getLimitedDisease());
          webDriverHelpers.scrollToElement(CreateNewUserPage.SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CreateNewUserPage.SAVE_BUTTON);
        });
    When(
        "I create new ([^\"]*) for test",
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
          userName = user.getUserName();
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          userPass = webDriverHelpers.getTextFromWebElement(PASSWORD_FIELD);
          closeNewPasswordPopUp();
        });

    When(
        "^I create new ([^\"]*) user for test on DE specific$",
        (String role) -> {
          user = userService.buildGeneratedUserWithRole(role);
          fillFirstName(user.getFirstName());
          fillLastName(user.getLastName());
          selectActive(user.getActive());
          fillUserName(user.getUserName());
          selectUserRole(role);
          userName = user.getUserName();
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          userPass = webDriverHelpers.getTextFromWebElement(PASSWORD_FIELD);
          closeNewPasswordPopUp();
        });

    When(
        "As a new created user I log in",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LoginPage.USER_NAME_INPUT);
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, userName);
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, userPass);
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOGOUT_BUTTON, 30);
        });
    When(
        "I login first time as a new created user from keycloak instance",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LoginPage.USER_NAME_INPUT);
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, userName);
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, userPass);
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          userPass = userPass + "3!";
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.fillInWebElement(LoginPage.PASSWORD_NEW_INPUT, userPass);
          webDriverHelpers.fillInWebElement(LoginPage.PASSWORD_CONFIRM_INPUT, userPass);
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.SUBMIT_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOGOUT_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_GDPR_POPUP_DE);
        });
    When(
        "I login first time as a last edited user from keycloak instance",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LoginPage.USER_NAME_INPUT);
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_NAME_INPUT, EditUserSteps.collectedUser.getUserName());
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_PASSWORD_INPUT, EditUserSteps.collectedUser.getPassword());
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          String newPassword = EditUserSteps.collectedUser.getPassword() + "3!";
          EditUserSteps.collectedUser =
              EditUserSteps.collectedUser.toBuilder().password(newPassword).build();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.fillInWebElement(
              LoginPage.PASSWORD_NEW_INPUT, EditUserSteps.collectedUser.getPassword());
          webDriverHelpers.fillInWebElement(
              LoginPage.PASSWORD_CONFIRM_INPUT, EditUserSteps.collectedUser.getPassword());
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.SUBMIT_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOGOUT_BUTTON, 30);
        });

    When(
        "As a new created user on Keycloak enabled instance I log in",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LoginPage.USER_NAME_INPUT);
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, userName);
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, userPass);
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
        });

    And(
        "I create new user for test with {string} jurisdiction and {string} roles",
        (String jurisdiction, String role) -> {
          user = userService.buildGeneratedUserWithRole("Clinician");
          fillFirstName(user.getFirstName());
          fillLastName(user.getLastName());
          fillEmailAddress(user.getEmailAddress());
          fillPhoneNumber(user.getPhoneNumber());
          selectLanguage(user.getLanguage());
          switch (jurisdiction) {
            case "Bayern":
              selectCountry("Germany");
              selectRegion("Bayern");
              selectDistrict("LK Ansbach");
              selectCommunity("Aurach");
              break;
            case "Saarland":
              selectCountry("Germany");
              selectRegion("Saarland");
              selectDistrict("LK Saarlouis");
              selectCommunity("Lebach");
              break;
          }
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

          List<String> roles = Arrays.asList(role.split(","));
          for (String temp : roles) {
            selectUserRole(temp);
            if (temp.equals("Clinician")) {
              selectSecondRegion(jurisdiction);
            }
          }

          userName = user.getUserName();
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          userWithRegion.put(
              String.format(user.getFirstName() + " " + user.getLastName().toUpperCase()),
              jurisdiction);
          closeNewPasswordPopUp();
        });

    When(
        "I set user role to {string}",
        (String userRole) -> webDriverHelpers.selectFromCombobox(USER_ROLE_COMBOBOX, userRole));

    When(
        "I set region filter to {string}",
        (String region) -> webDriverHelpers.selectFromCombobox(REGION_FILTER_COMBOBOX, region));

    When(
        "I search user {string}",
        (String uName) -> {
          webDriverHelpers.fillAndSubmitInWebElement(USER_INPUT_SEARCH, uName);
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I filter last created user",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(USER_INPUT_SEARCH, userName);
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I open first user from the list",
        () -> {
          selectFirstElementFromList();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAVE_BUTTON);
        });
    When(
        "I set last created user to inactive",
        () -> {
          webDriverHelpers.scrollToElement(ACTIVE_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(ACTIVE_CHECKBOX);
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I check if displayed user name is equal with searched {string}",
        (String uname) -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(TABLE_USER_NAME),
              uname,
              "Users name are not equal");
          softly.assertAll();
        });

    And(
        "^I check that \"([^\"]*)\" user role checkbox is not available in Create New User form$",
        (String userRole) -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(
                  getUserRoleLabelByCaption("TestNatUser"), 3),
              userRole + " user role is available!");
          softly.assertAll();
        });

    And(
        "^I create new \"([^\"]*)\" with english language for test$",
        (String role) -> {
          user = userService.buildGeneratedUserWithRole(role);
          fillFirstName(user.getFirstName());
          fillLastName(user.getLastName());
          fillEmailAddress(user.getEmailAddress());
          fillPhoneNumber(user.getPhoneNumber());
          selectActive(user.getActive());
          fillUserName(user.getUserName());
          selectUserRole(role);
          userName = user.getUserName();
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          userPass = webDriverHelpers.getTextFromWebElement(PASSWORD_FIELD);
          closeNewPasswordPopUp();
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

  private void selectSecondRegion(String region) {
    webDriverHelpers.selectFromCombobox(SECOND_REGION_COMBOBOX, region);
  }

  private void selectSecondDistrict(String region) {
    webDriverHelpers.selectFromCombobox(SECOND_DISTRICT_COMBOBOX, region);
  }

  private void selectSurveillanceRegion(String surveillanceRegion) {
    webDriverHelpers.selectFromCombobox(SURVEILLANCE_REGION, surveillanceRegion);
  }

  private void selectSurveillanceDistrict(String surveillanceDistrict) {
    webDriverHelpers.selectFromCombobox(SURVEILLANCE_DISTRICT, surveillanceDistrict);
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
    webDriverHelpers.scrollToElement(POSTAL_CODE_INPUT);
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, postalCode);
  }

  private void fillCity(String city) {
    webDriverHelpers.fillInWebElement(CITY_INPUT, city);
  }

  private void selectAreaType(String areaType) {
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
    webDriverHelpers.scrollToElement(USER_NAME_INPUT);
    webDriverHelpers.fillInWebElement(USER_NAME_INPUT, userName);
  }

  private void selectUserRole(String role) {
    webDriverHelpers.scrollToElement(USER_ROLE_CHECKBOX);
    webDriverHelpers.clickWebElementByText(USER_ROLE_CHECKBOX, role);
  }

  @SneakyThrows
  private void closeNewPasswordPopUp() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CLOSE_DIALOG_BUTTON, 15);
    webDriverHelpers.clickOnWebElementBySelector(CLOSE_DIALOG_BUTTON);
    TimeUnit.SECONDS.sleep(1);
  }

  private void selectRestrictDiseases(String limitedDisease) {
    webDriverHelpers.scrollToElement(RESTRICT_DISEASES_CHECKBOX);
    webDriverHelpers.clickOnWebElementBySelector(RESTRICT_DISEASES_CHECKBOX);
    webDriverHelpers.clickWebElementByText(LIMITED_DISEASE_CHECKBOX, limitedDisease);
  }

  private void changeRestrictDiseases(String previousLimitedDisease, String limitedDisease) {
    webDriverHelpers.scrollToElement(RESTRICT_DISEASES_CHECKBOX);
    webDriverHelpers.doubleClickOnWebElementBySelector(RESTRICT_DISEASES_CHECKBOX);
    webDriverHelpers.clickWebElementByText(LIMITED_DISEASE_CHECKBOX, previousLimitedDisease);
    webDriverHelpers.clickWebElementByText(LIMITED_DISEASE_CHECKBOX, limitedDisease);
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

  private void selectFirstElementFromList() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(FIRST_EDIT_BUTTON_FROM_LIST);
    webDriverHelpers.clickOnWebElementBySelector(FIRST_EDIT_BUTTON_FROM_LIST);
  }

  private void fillUserFieldsWithSpecificRegion(String region) {
    switch (region) {
      case "Bayern":
        selectRegion("Bayern");
        selectDistrict("LK Ansbach");
        selectCommunity("Aurach");
        break;
      case "Saarland":
        selectRegion("Saarland");
        selectDistrict("LK Saarlouis");
        selectCommunity("Lebach");
        break;
      case "Berlin":
        selectRegion("Berlin");
        selectDistrict("SK Berlin Charlottenburg-Wilmersdorf");
        selectCommunity("Charlottenburg-Nord");
        break;
      case "Baden-W\u00FCrttemberg":
        selectRegion("Baden-W\u00FCrttemberg");
        selectDistrict("LK Alb-Donau-Kreis");
        selectCommunity("Allmendingen");
        break;
    }
  }
}
