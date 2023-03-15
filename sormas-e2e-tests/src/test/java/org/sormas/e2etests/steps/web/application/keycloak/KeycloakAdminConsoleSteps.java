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
package org.sormas.e2etests.steps.web.application.keycloak;

import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.ITEMS_PER_PAGE_BUTTON;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.NEXT_PAGE_BUTTON;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.ONE_HUNDRED_PER_PAGE_BUTTON;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.RESULT_IN_TABLE;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.SEARCH_BUTTON;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.SEARCH_USER_INPUT;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.USER_DISABLED;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.users.CreateNewUserSteps;
import org.sormas.e2etests.steps.web.application.users.UserManagementSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class KeycloakAdminConsoleSteps implements En {

  protected WebDriverHelpers webDriverHelpers;
  int numberOfUsers;

  @Inject
  public KeycloakAdminConsoleSteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      AssertHelpers assertHelpers,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I navigate to Users tab in Keycloak Administrator Console$",
        () -> {
          String KEYCLOAK_USERS_TAB =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/keycloak/admin/master/console/#/SORMAS/users";
          webDriverHelpers.accessWebSite(KEYCLOAK_USERS_TAB);
        });
    When(
        "^I count the number of users displayed in Users tab in Keycloak Administrator Console$",
        () -> {
          numberOfUsers = 0;
          webDriverHelpers.clickOnWebElementBySelector(ITEMS_PER_PAGE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ONE_HUNDRED_PER_PAGE_BUTTON);
          TimeUnit.SECONDS.sleep(2);
          numberOfUsers = webDriverHelpers.getNumberOfElements(RESULT_IN_TABLE);

          do {
            webDriverHelpers.clickOnWebElementBySelector(NEXT_PAGE_BUTTON);
            TimeUnit.SECONDS.sleep(2);
            numberOfUsers += webDriverHelpers.getNumberOfElements(RESULT_IN_TABLE);
          } while (webDriverHelpers.isElementEnabled(NEXT_PAGE_BUTTON));
          numberOfUsers += webDriverHelpers.getNumberOfElements(RESULT_IN_TABLE);
        });
    When(
        "^I check that number of users from SORMAS is at least equal to number of users in Keycloak Administrator Console$",
        () -> {
          softly.assertTrue(
              Integer.valueOf(numberOfUsers) >= UserManagementSteps.numberOfUsers,
              "Number of users is not correct!");
          softly.assertAll();
        });

    When(
        "^I search for last created user from SORMAS in grid in Keycloak Admin Page$",
        () -> {
          webDriverHelpers.fillInWebElement(
              SEARCH_USER_INPUT,
              CreateNewUserSteps.user.getFirstName() + " " + CreateNewUserSteps.user.getLastName());
          webDriverHelpers.clickOnWebElementBySelector(SEARCH_BUTTON);
        });
    When(
        "^I check if user is disabled in Keycloak Admin Page$",
        () -> {
          boolean visible = webDriverHelpers.isElementVisibleWithTimeout(USER_DISABLED, 3);
          assertHelpers.assertWithPoll(() -> Assert.assertTrue(visible, "User is enabled!"), 5);
        });
  }
}
