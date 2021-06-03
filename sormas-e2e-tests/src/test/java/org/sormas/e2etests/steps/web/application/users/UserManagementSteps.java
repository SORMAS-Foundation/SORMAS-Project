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
import static org.sormas.e2etests.pages.application.users.UserManagementPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class UserManagementSteps implements En {

  @Inject
  public UserManagementSteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I click on the NEW USER button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_USER_BUTTON, FIRST_NAME_OF_USER_INPUT));

    When(
        "^I select fists user from list$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_EDIT_BUTTON_FROM_LIST);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_EDIT_BUTTON_FROM_LIST);
        });

    When(
        "^I search for created user$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEARCH_USER_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              SEARCH_USER_INPUT, CreateNewUserSteps.user.getUserName());
          webDriverHelpers.checkWebElementContainsText(
              USER_NAME_GRID_CELL, CreateNewUserSteps.user.getUserName());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_EDIT_BUTTON_FROM_LIST);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_EDIT_BUTTON_FROM_LIST);
        });
  }
}
