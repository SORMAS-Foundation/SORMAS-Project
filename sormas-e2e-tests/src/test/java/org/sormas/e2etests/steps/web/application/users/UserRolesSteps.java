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

import static org.sormas.e2etests.pages.application.users.UserRolesPage.NEW_USER_ROLE_BUTTON;
import static org.sormas.e2etests.pages.application.users.UserRolesPage.USER_ROLE_TEMPLATE_COMBOBOX;

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
  }
}
