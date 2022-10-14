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

import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CANCEL;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.*;
import static org.sormas.e2etests.pages.application.users.UserManagementPage.*;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;

public class UserManagementSteps implements En {
  public static int numberOfUsers;
  protected WebDriverHelpers webDriverHelpers;

  @Inject
  public UserManagementSteps(WebDriverHelpers webDriverHelpers, AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I click on the NEW USER button$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_USER_BUTTON);
          webDriverHelpers.clickWhileOtherButtonIsDisplayed(
              NEW_USER_BUTTON, FIRST_NAME_OF_USER_INPUT);
        });

    Then(
        "^I set active inactive filter to ([^\"]*) in User Management directory$",
        (String activeInactive) -> {
          webDriverHelpers.selectFromCombobox(ACTIVE_INACTIVE_COMBOBOX, activeInactive);
          TimeUnit.SECONDS.sleep(2); // needed for table to refresh
        });

    When("^I select first user from list$", () -> selectFirstElementFromList());

    When(
        "^I search for created user$",
        () -> {
          searchForUser(CreateNewUserSteps.user.getUserName());
          selectFirstElementFromList();
        });

    When(
        "I search for created user in the User Management Page",
        () -> {
          searchForUser(CreateNewUserSteps.user.getUserName());
        });

    When(
        "^I search for recently edited user$",
        () -> {
          searchForUser(CreateNewUserSteps.editUser.getUserName());
          selectFirstElementFromList();
        });

    When(
        "^I count the number of users displayed in User Directory$",
        () ->
            numberOfUsers = Integer.parseInt(webDriverHelpers.getTextFromWebElement(USER_NUMBER)));

    When(
        "^I click on Sync Users button$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SYNC_USERS_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SYNC_POPUP_BUTTON);
        });
    When(
        "^I click on Sync button from Sync Users popup$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SYNC_POPUP_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ACTION_CANCEL);
        });
    When(
        "^I check if sync message is correct in German$",
        () ->
            assertHelpers.assertWithPoll(
                () ->
                    Assert.assertTrue(
                        webDriverHelpers.isElementVisibleWithTimeout(SYNC_SUCCESS_DE, 5),
                        "Sync of users failed"),
                10));

    When(
        "^I verify that the Active value is ([^\"]*) in the User Management Page",
        (String option) -> {
          switch (option) {
            case "Checked":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  ACTIVE_CHECKBOX_USER_MANAGEMENT);
              break;
            case "Unchecked":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  INACTIVE_CHECKBOX_USER_MANAGEMENT);
              break;
            default:
              throw new IllegalArgumentException("No valid Switch options were provided");
          }
        });

    Then(
        "I Verify the number of Active, Inactive and Total users in the User Management Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              USERS_COUNTER_USER_MANAGEMENT);
          Integer numberOfTotalUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(ACTIVE_INACTIVE_COMBOBOX, "Active");
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOADING_INDICATOR);
          webDriverHelpers.waitUntilIdentifiedElementDisappear(LOADING_INDICATOR);
          Integer numberOfActiveUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(ACTIVE_INACTIVE_COMBOBOX, "Inactive");
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOADING_INDICATOR);
          webDriverHelpers.waitUntilIdentifiedElementDisappear(LOADING_INDICATOR);
          Integer numberOfInactiveUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(
                      numberOfTotalUsers == numberOfActiveUsers + numberOfInactiveUsers,
                      "Sync of users failed in User Management Page"),
              10);
        });

    Then(
        "I Verify The User Role filter in the User Management Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              USERS_COUNTER_USER_MANAGEMENT);
          Integer numberOfTotalUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(USER_ROLES_COMBOBOX, "National User");
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOADING_INDICATOR);
          webDriverHelpers.waitUntilIdentifiedElementDisappear(LOADING_INDICATOR);
          Integer numberOfSpecificUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(USER_ROLES_COMBOBOX, "");
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertFalse(
                      numberOfTotalUsers == numberOfSpecificUsers,
                      "User Roles Filer ComboBox failed in User Management Page"),
              10);
        });

    Then(
        "I Verify Region filter in the User Management Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              USERS_COUNTER_USER_MANAGEMENT);
          Integer numberOfTotalUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(REGION_COMBOBOX_USER_MANAGEMENT, "Bayern");
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOADING_INDICATOR);
          webDriverHelpers.waitUntilIdentifiedElementDisappear(LOADING_INDICATOR);
          Integer numberOfSpecificUsers =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(USERS_COUNTER_USER_MANAGEMENT));
          webDriverHelpers.selectFromCombobox(REGION_COMBOBOX_USER_MANAGEMENT, "");
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertFalse(
                      numberOfTotalUsers == numberOfSpecificUsers,
                      "User Roles Filer ComboBox failed in User Management Page"),
              10);
        });
  }

  private void searchForUser(String userName) {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SEARCH_USER_INPUT);
    webDriverHelpers.fillAndSubmitInWebElement(SEARCH_USER_INPUT, userName);
  }

  private void selectFirstElementFromList() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(FIRST_EDIT_BUTTON_FROM_LIST);
    webDriverHelpers.clickOnWebElementBySelector(FIRST_EDIT_BUTTON_FROM_LIST);
  }
}
