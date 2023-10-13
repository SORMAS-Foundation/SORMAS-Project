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

import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_VACCINATION_NAME;
import static org.sormas.e2etests.pages.application.users.UserManagementPage.SEARCH_USER_INPUT;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.ARCHIVE_CASES_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.ARCHIVE_CONTACTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.CANNOT_DELETE_USER_ROLE_POPUP;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.CANNOT_DELETE_USER_ROLE_POPUP_OKAY_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.CAN_BE_RESPONSIBLE_FOR_A_CASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.CAPTION_INPUT;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.DELETE_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.DELETE_USER_ROLE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EDIT_CASE_CLASSIFICATION_AND_OUTCOME_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EDIT_CASE_DISEASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EDIT_CASE_EPID_NUMBER_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX_VALUE;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EDIT_EXISTING_CASES_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EDIT_EXISTING_CASES_CHECKBOX_VALUE;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.ENABLED_DISABLED_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.EXPORT_USER_ROLES_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.NEW_USER_ROLE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.POPUP_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.REFER_CASE_FROM_POINT_OF_ENTRY_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.TRANSFER_CASES_TO_ANOTHER_REGION_DISTRICT_FACILITY_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_MANAGEMENT_TAB;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_RIGHTS_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_RIGHTS_INPUT;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_DISABLE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_ENABLE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_LIST;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_TEMPLATE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.WORK_WITH_MESSAGE_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.getUserRoleCaptionByText;

import cucumber.api.java8.En;

import java.nio.channels.Selector;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.web.Vaccination;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.testng.asserts.SoftAssert;

public class UserRolesSteps implements En {
  public static final String USER_ROLES_FILE_PATH =
      String.format("sormas_user_roles_%s_.xlsx", LocalDate.now());
  protected WebDriverHelpers webDriverHelpers;

  @Inject
  public UserRolesSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on New user role button on User Roles Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NEW_USER_ROLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_USER_ROLE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              USER_ROLE_TEMPLATE_COMBOBOX);
        });

    And(
        "^I choose \"([^\"]*)\" as the user role template$",
        (String userTemplate) -> {
          webDriverHelpers.selectFromCombobox(USER_ROLE_TEMPLATE_COMBOBOX, userTemplate);
        });

    And(
        "^I click SAVE button on Create New User Role form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE_BUTTON);
        });

    And(
        "^I fill caption input as \"([^\"]*)\" on Create New User Role form$",
        (String caption) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CAPTION_INPUT);
          webDriverHelpers.fillInWebElement(CAPTION_INPUT, caption);
        });

    And(
        "^I click checkbox to choose \"([^\"]*)\"$",
        (String checkboxLabel) -> {
          switch (checkboxLabel) {
            case "Archive cases":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(ARCHIVE_CASES_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CASES_CHECKBOX);
              webDriverHelpers.scrollToElement(ARCHIVE_CASES_CHECKBOX);
              break;
            case "Archive contacts":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(ARCHIVE_CONTACTS_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CONTACTS_CHECKBOX);
              webDriverHelpers.scrollToElement(ARCHIVE_CONTACTS_CHECKBOX);
              break;
          }
        });

    And(
        "^I click checkbox to uncheck \"([^\"]*)\"$",
        (String checkboxLabel) -> {
            Boolean checkboxState;
          switch (checkboxLabel) {
              // TODO -wrote method that check if checkbox is checked !! and refactor below
            case "Edit existing cases":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(EDIT_EXISTING_CASES_CHECKBOX);
              checkboxState = webDriverHelpers.isElementChecked(EDIT_EXISTING_CASES_CHECKBOX_VALUE);
              System.out.print(checkboxState);
                uncheckCheckbox(checkboxState, EDIT_EXISTING_CASES_CHECKBOX);
              webDriverHelpers.scrollToElement(EDIT_EXISTING_CASES_CHECKBOX);
              System.out.print(
                  webDriverHelpers.isElementChecked(EDIT_EXISTING_CASES_CHECKBOX_VALUE));
              TimeUnit.SECONDS.sleep(5);
              break;
            case "Edit case investigation status":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                  EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX);
              Boolean value1 =
                webDriverHelpers.isElementChecked(EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX_VALUE);
                if (value1 == true)
                    webDriverHelpers.clickOnWebElementBySelector(EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX);
              webDriverHelpers.scrollToElement(EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX);
              break;
            case "Edit case disease status":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(EDIT_CASE_DISEASE_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(EDIT_CASE_DISEASE_CHECKBOX);
              webDriverHelpers.scrollToElement(EDIT_CASE_DISEASE_CHECKBOX);
              break;
            case "Transfer cases to another region/district/facility":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                  TRANSFER_CASES_TO_ANOTHER_REGION_DISTRICT_FACILITY_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(
                  TRANSFER_CASES_TO_ANOTHER_REGION_DISTRICT_FACILITY_CHECKBOX);
              webDriverHelpers.scrollToElement(
                  TRANSFER_CASES_TO_ANOTHER_REGION_DISTRICT_FACILITY_CHECKBOX);
              break;
            case "Edit case classification and outcome":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                  EDIT_CASE_CLASSIFICATION_AND_OUTCOME_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(
                  EDIT_CASE_CLASSIFICATION_AND_OUTCOME_CHECKBOX);
              webDriverHelpers.scrollToElement(EDIT_CASE_CLASSIFICATION_AND_OUTCOME_CHECKBOX);
              break;
            case "Edit case epid number":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(EDIT_CASE_EPID_NUMBER_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(EDIT_CASE_EPID_NUMBER_CHECKBOX);
              webDriverHelpers.scrollToElement(EDIT_CASE_EPID_NUMBER_CHECKBOX);
              break;
            case "Refer case from point of entry":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                  REFER_CASE_FROM_POINT_OF_ENTRY_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(REFER_CASE_FROM_POINT_OF_ENTRY_CHECKBOX);
              webDriverHelpers.scrollToElement(REFER_CASE_FROM_POINT_OF_ENTRY_CHECKBOX);
              break;
            case "Can be responsible for a case":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                  CAN_BE_RESPONSIBLE_FOR_A_CASE_CHECKBOX);
              Boolean value332 =
                  webDriverHelpers.isElementChecked(CAN_BE_RESPONSIBLE_FOR_A_CASE_CHECKBOX);
              System.out.print(value332);
              // webDriverHelpers.clickOnWebElementBySelector(CAN_BE_RESPONSIBLE_FOR_A_CASE_CHECKBOX);
              webDriverHelpers.scrollToElement(CAN_BE_RESPONSIBLE_FOR_A_CASE_CHECKBOX);
              break;
            case "Work with message":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(WORK_WITH_MESSAGE_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(WORK_WITH_MESSAGE_CHECKBOX);
              webDriverHelpers.scrollToElement(WORK_WITH_MESSAGE_CHECKBOX);
              break;
          }
        });

    And(
        "^I click SAVE button on User Role Page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    And(
        "^I back to the User role list$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(USER_ROLE_LIST);
          webDriverHelpers.clickOnWebElementBySelector(USER_ROLE_LIST);
        });

    And(
        "^I check that \"([^\"]*)\" is displayed in the User role column$",
        (String userRoleCaption) -> {
          webDriverHelpers.isElementVisibleWithTimeout(
              getUserRoleCaptionByText(userRoleCaption), 5);
        });

    And(
        "^I click DISCARD button on Create New User Role form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(POPUP_DISCARD_BUTTON);
        });

    And(
        "^I click on User Management tab from User Roles Page$",
        () -> {
          webDriverHelpers.scrollToElement(USER_MANAGEMENT_TAB);
          webDriverHelpers.clickOnWebElementBySelector(USER_MANAGEMENT_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SEARCH_USER_INPUT);
        });

    And(
        "^I double click on \"([^\"]*)\" from user role list$",
        (String userRole) -> {
          webDriverHelpers.scrollToElement(getUserRoleCaptionByText(userRole));
          webDriverHelpers.doubleClickOnWebElementBySelector(getUserRoleCaptionByText(userRole));
        });

    And(
        "I click on the user role {string} button",
        (String disableEnable) -> {
          switch (disableEnable) {
            case "Disable":
              webDriverHelpers.scrollToElement(USER_ROLE_DISABLE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(USER_ROLE_DISABLE_BUTTON);
              break;
            case "Enable":
              webDriverHelpers.scrollToElement(USER_ROLE_ENABLE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(USER_ROLE_ENABLE_BUTTON);
              break;
          }
        });

    And(
        "^I check that \"([^\"]*)\" is not available in the user role template dropdown menu$",
        (String userRole) -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  USER_ROLE_TEMPLATE_COMBOBOX, userRole),
              "Provided user role is available in the user role template dropdown menu!");
          softly.assertAll();
        });

    And(
        "I filter user roles by {string}",
        (String disableEnableSearch) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(ENABLED_DISABLED_SEARCH_COMBOBOX);
          webDriverHelpers.selectFromCombobox(
              ENABLED_DISABLED_SEARCH_COMBOBOX, disableEnableSearch);
        });

    And(
        "^I click on delete user role button$",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_USER_ROLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_USER_ROLE_BUTTON);
        });

    And(
        "^I confirm user role deletion$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DELETE_CONFIRMATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_CONFIRMATION_BUTTON);
        });

    And(
        "^I check if Cannot delete user role popup message is displayed$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CANNOT_DELETE_USER_ROLE_POPUP);
        });

    And(
        "^I confirm Cannot delete user role popup message$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              CANNOT_DELETE_USER_ROLE_POPUP_OKAY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CANNOT_DELETE_USER_ROLE_POPUP_OKAY_BUTTON);
        });

    And(
        "^I filter user roles by \"([^\"]*)\" user rights$",
        (String userRight) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(USER_RIGHTS_COMBOBOX);
          webDriverHelpers.selectFromCombobox(USER_RIGHTS_COMBOBOX, userRight);
        });

    And(
        "^I check if the \"([^\"]*)\" user role exist and delete it$",
        (String userRole) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(USER_RIGHTS_INPUT);
          webDriverHelpers.scrollInTable(12);

          if (webDriverHelpers.isElementVisibleWithTimeout(getUserRoleCaptionByText(userRole), 5)) {
            webDriverHelpers.doubleClickOnWebElementBySelector(getUserRoleCaptionByText(userRole));
            webDriverHelpers.scrollToElement(DELETE_USER_ROLE_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(DELETE_USER_ROLE_BUTTON);
            webDriverHelpers.waitUntilIdentifiedElementIsPresent(DELETE_CONFIRMATION_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(DELETE_CONFIRMATION_BUTTON);
          }
        });

    And(
        "^I verify that the \"([^\"]*)\" user role exist and delete it$",
        (String userRole) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(USER_RIGHTS_INPUT);
          webDriverHelpers.scrollInTable(12);
          webDriverHelpers.isElementVisibleWithTimeout(getUserRoleCaptionByText("TestNatUser"), 5);
          webDriverHelpers.doubleClickOnWebElementBySelector(getUserRoleCaptionByText(userRole));
          webDriverHelpers.scrollToElement(DELETE_USER_ROLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_USER_ROLE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DELETE_CONFIRMATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_CONFIRMATION_BUTTON);
        });

    And(
        "^I check if the \"([^\"]*)\" user role cannot be deleted while assigned$",
        (String userRole) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(USER_RIGHTS_INPUT);
          webDriverHelpers.scrollInTable(12);

          if (webDriverHelpers.isElementVisibleWithTimeout(
              getUserRoleCaptionByText("TestNatUser"), 5)) {
            webDriverHelpers.doubleClickOnWebElementBySelector(getUserRoleCaptionByText(userRole));
            webDriverHelpers.scrollToElement(DELETE_USER_ROLE_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(DELETE_USER_ROLE_BUTTON);
            webDriverHelpers.waitUntilIdentifiedElementIsPresent(DELETE_CONFIRMATION_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(DELETE_CONFIRMATION_BUTTON);
            webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                CANNOT_DELETE_USER_ROLE_POPUP);
            webDriverHelpers.clickOnWebElementBySelector(CANNOT_DELETE_USER_ROLE_POPUP_OKAY_BUTTON);
          }
        });

    And(
        "I click on the Export User Roles Button and verify User role file is downloaded and contains data in the User Role Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              EXPORT_USER_ROLES_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EXPORT_USER_ROLES_BUTTON);
          FilesHelper.waitForFileToDownload(USER_ROLES_FILE_PATH, 30);
          FilesHelper.validateFileIsNotEmpty(USER_ROLES_FILE_PATH);
        });

    And(
        "^I check if the \"([^\"]*)\" user role exist and change it to enabled$",
        (String userRole) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(USER_RIGHTS_INPUT);
          webDriverHelpers.scrollInTable(20);

          if (webDriverHelpers.isElementVisibleWithTimeout(getUserRoleCaptionByText(userRole), 7)) {
            webDriverHelpers.doubleClickOnWebElementBySelector(getUserRoleCaptionByText(userRole));
            TimeUnit.SECONDS.sleep(3);
            webDriverHelpers.scrollToElement(DISCARD_BUTTON);
            System.out.print("Zescrollowalem");
            if (webDriverHelpers.isElementVisibleWithTimeout(USER_ROLE_ENABLE_BUTTON, 7)) {
              System.out.print("wszedlem do drugiego ifa");
              webDriverHelpers.scrollToElement(USER_ROLE_ENABLE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(USER_ROLE_ENABLE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
            }
          }
        });
  }

    private void uncheckCheckbox(Boolean checkboxState,By checkboxName) {
      if(checkboxState == true)
          webDriverHelpers.clickOnWebElementBySelector(checkboxName);
    }
}
