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

package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.LOGOUT_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.dto.EnvUser;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.LoginPage;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;

@Slf4j
public class LoginSteps implements En {

  @Inject
  public LoginSteps(WebDriverHelpers webDriverHelpers, RunningConfiguration runningConfiguration) {

    Given(
        "^I am logged in with name ([^\"]*)$",
        (String name) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LOGOUT_BUTTON, 60);
        });

    Given(
        "^I navigate to SORMAS login page$",
        () -> {
          webDriverHelpers.accessWebSite(runningConfiguration.getEnvironmentUrlForMarket(locale));
        });

    Given(
        "I click on the Log In button",
        () -> webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON));

    And(
        "I log in with National User",
        () -> {
          EnvUser user =
              runningConfiguration.getUserByRole(locale, UserRoles.NationalUser.getRole());
          webDriverHelpers.accessWebSite(runningConfiguration.getEnvironmentUrlForMarket(locale));
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              LoginPage.USER_NAME_INPUT, 100);
          log.info("Filling username");
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, user.getUsername());
          log.info("Filling password");
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, user.getPassword());
          log.info("Click on Login button");
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LOGOUT_BUTTON, 100);
        });

    Given(
        "^I log in as a ([^\"]*)$",
        (String userRole) -> {
          webDriverHelpers.accessWebSite(runningConfiguration.getEnvironmentUrlForMarket(locale));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LoginPage.USER_NAME_INPUT);
          EnvUser user = runningConfiguration.getUserByRole(locale, userRole);
          log.info("Filling username");
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, user.getUsername());
          log.info("Filling password");
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, user.getPassword());
          log.info("Clicking on login button");
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOGOUT_BUTTON, 50);
        });

    When(
        "I check that German word for Configuration is present in the left main menu",
        () -> {
          webDriverHelpers.checkWebElementContainsText(
              NavBarPage.CONFIGURATION_BUTTON, "Einstellungen");
        });
  }
}
