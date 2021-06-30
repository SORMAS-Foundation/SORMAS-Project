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

package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.enums.TestDataUser.*;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import javax.inject.Named;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.LoginPage;
import org.sormas.e2etests.pages.application.MainPage;

public class LoginSteps implements En {

  @Inject
  public LoginSteps(
      WebDriverHelpers webDriverHelpers, @Named("ENVIRONMENT_URL") String environmentUrl) {

    When(
        "^I fill the username with ([^\"]*)$",
        (String userName) ->
            webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, userName));

    When(
        "^I fill the password with ([^\"]*)$",
        (String password) ->
            webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, password));

    Given(
        "^I am logged in with name ([^\"]*)$",
        (String name) ->
            webDriverHelpers.checkWebElementContainsText(MainPage.LOGOUT_BUTTON, name));

    Given(
        "^I navigate to SORMAS login page$", () -> webDriverHelpers.accessWebSite(environmentUrl));

    Given(
        "I click on the Log In button",
        () -> webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON));

    And(
        "I log in with the user",
        () -> {
          webDriverHelpers.accessWebSite(environmentUrl);
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, "NatUser");
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, "NatUser38118");
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
        });

    Given(
        "I log in as a ([^\"]*)",
        (String userRole) -> {
          webDriverHelpers.accessWebSite(environmentUrl);
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_NAME_INPUT, gertUserByRole(userRole).getUsername());
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_PASSWORD_INPUT, gertUserByRole(userRole).getPassword());
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
        });
  }
}
