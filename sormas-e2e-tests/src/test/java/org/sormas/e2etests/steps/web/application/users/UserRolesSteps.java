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

import static org.sormas.e2etests.pages.application.users.UserManagementPage.SEARCH_USER_INPUT;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.ARCHIVE_CASES_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.ARCHIVE_CONTACTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.CAPTION_INPUT;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.NEW_USER_ROLE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.POPUP_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_MANAGEMENT_TAB;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_DISABLE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_LIST;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_TEMPLATE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.getUserRoleCaptionByText;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class UserRolesSteps implements En {
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public UserRolesSteps(final WebDriverHelpers webDriverHelpers) {
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
            case "Archive contacts":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(ARCHIVE_CONTACTS_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CONTACTS_CHECKBOX);
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
        (String arg0) -> {
          webDriverHelpers.doubleClickOnWebElementBySelector(getUserRoleCaptionByText("TestNatUser"));
        });

    And(
        "^I click on the user role Disable button$", () -> {
          webDriverHelpers.clickOnWebElementBySelector(USER_ROLE_DISABLE_BUTTON);
        });
  }
}
