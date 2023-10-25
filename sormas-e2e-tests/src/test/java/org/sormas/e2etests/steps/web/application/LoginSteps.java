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

import static org.sormas.e2etests.pages.application.LoginPage.*;
import static org.sormas.e2etests.pages.application.NavBarPage.*;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.LOGOUT_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.users.CreateNewUserSteps.userName;
import static org.sormas.e2etests.steps.web.application.users.CreateNewUserSteps.userPass;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.envconfig.dto.EnvUser;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.strings.LanguageDetectorHelper;
import org.sormas.e2etests.pages.application.LoginPage;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;
import org.sormas.e2etests.steps.web.application.users.EditUserSteps;

@Slf4j
public class LoginSteps implements En {

  @Inject
  public LoginSteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      AssertHelpers assertHelpers) {

    Given(
        "^I am logged in$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LOGOUT_BUTTON, 60);
        });

    When(
        "I check error message for disabled user is present",
        () ->
            assertHelpers.assertWithPoll20Second(
                () -> {
                  org.testng.Assert.assertTrue(
                      webDriverHelpers.isElementVisibleWithTimeout(ERROR_MESSAGE, 5),
                      "Error message is not visible");
                }));
    Given(
        "^I navigate to SORMAS login page$",
        () -> {
          webDriverHelpers.accessWebSite(runningConfiguration.getEnvironmentUrlForMarket(locale));
        });

    Given(
        "^I navigate to ([^\"]*) via URL append$",
        (String path) -> {
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + path);
          TimeUnit.SECONDS.sleep(2);
        });

    Given(
        "I click on the Log In button",
        () -> webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON));

    And(
        "I try to log in with {string} and password {string}",
        (String userName, String password) -> {
          webDriverHelpers.accessWebSite(runningConfiguration.getEnvironmentUrlForMarket(locale));
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              LoginPage.USER_NAME_INPUT, 100);
          log.info("Filling username");
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, userName);
          log.info("Filling password");
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, password);
          log.info("Click on Login button");
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
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
          if (webDriverHelpers.isElementVisibleWithTimeout(GDPR_CHECKBOX, 10)) {
            webDriverHelpers.clickOnWebElementBySelector(GDPR_CHECKBOX);
            if (webDriverHelpers.isElementVisibleWithTimeout(ACTION_CONFIRM_GDPR_POPUP, 5)) {
              webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_GDPR_POPUP);
            } else {
              webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_GDPR_POPUP_DE);
            }
          }
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOGOUT_BUTTON, 50);
        });

    When(
        "I navigate to {string} environment",
        (String env) -> {
          locale = env;
          webDriverHelpers.accessWebSite(runningConfiguration.getEnvironmentUrlForMarket(locale));
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "I navigate to {string} environment in new driver tab",
        (String env) -> {
          locale = env;
          webDriverHelpers.accessWebSiteWithNewTab(
              runningConfiguration.getEnvironmentUrlForMarket(locale));
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "I back to tab number {int}",
        (Integer tabNo) -> {
          webDriverHelpers.switchToTheTabNumber(tabNo);
          TimeUnit.SECONDS.sleep(5);
        });
    Then(
        "I login with last edited user",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              LoginPage.USER_NAME_INPUT, 100);
          log.info("Filling username");
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_NAME_INPUT, EditUserSteps.collectedUser.getUserName());
          log.info("Filling password");
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_PASSWORD_INPUT, EditUserSteps.collectedUser.getPassword());
          log.info("Click on Login button");
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
        });

    Then(
        "I login with new created user with chosen new role",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              LoginPage.USER_NAME_INPUT, 100);
          log.info("Filling username");
          webDriverHelpers.fillInWebElement(LoginPage.USER_NAME_INPUT, userName);
          log.info("Filling password");
          webDriverHelpers.fillInWebElement(LoginPage.USER_PASSWORD_INPUT, userPass);
          log.info("Click on Login button");
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
        });
    Then(
        "I login with last edited user on Keycloak Enabled Environment",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              LoginPage.USER_NAME_INPUT, 100);
          log.info("Filling username");
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_NAME_INPUT, EditUserSteps.collectedUser.getUserName());
          log.info("Filling password");
          webDriverHelpers.fillInWebElement(
              LoginPage.USER_PASSWORD_INPUT, EditUserSteps.collectedUser.getPassword());
          log.info("Click on Login button");
          webDriverHelpers.clickOnWebElementBySelector(LoginPage.LOGIN_BUTTON);
          webDriverHelpers.waitForPageLoaded();
        });
    When(
        "I check that German word for Configuration is present in the left main menu",
        () -> {
          webDriverHelpers.checkWebElementContainsText(
              NavBarPage.CONFIGURATION_BUTTON, "Einstellungen");
        });
    Then(
        "I check that ([^\"]*) language is selected in User Settings",
        (String expectedLanguageText) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              USER_SETTINGS_LANGUAGE_COMBOBOX_TEXT);
          String selectedLanguageText =
              (webDriverHelpers
                      .getValueFromWebElement(USER_SETTINGS_LANGUAGE_COMBOBOX_TEXT)
                      .isEmpty())
                  ? LanguageDetectorHelper.scanLanguage(
                      webDriverHelpers.getTextFromWebElement(DASHBOARD_BUTTON))
                  : webDriverHelpers.getValueFromWebElement(USER_SETTINGS_LANGUAGE_COMBOBOX_TEXT);
          Assert.assertEquals(
              "Selected language is not correct", expectedLanguageText, selectedLanguageText);
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_USER_SETTINGS_BUTTON);
        });
    And(
        "I click on logout button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LOGOUT_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LoginPage.LOGIN_BUTTON);
        });

    Then(
        "Login failed message should be displayed",
        () -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  org.testng.Assert.assertTrue(
                      webDriverHelpers.isElementVisibleWithTimeout(FAILED_LOGIN_ERROR_MESSAGE, 5),
                      "Login failed error message is not displayed"));
        });

    Then(
        "Login page should be displayed",
        () -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  org.testng.Assert.assertTrue(
                      webDriverHelpers.isElementVisibleWithTimeout(LOGIN_BUTTON, 5),
                      "Login page is not displayed"));
        });

    Then(
        "^I check that Login page is correctly displayed in ([^\"]*) language$",
        (String language) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LOGIN_BUTTON, 30);
          LanguageDetectorHelper.checkLanguage(
              webDriverHelpers.getTextFromWebElement(APPLICATION_DESCRIPTION_TEXT), language);
        });

    And(
        "^I check if GDPR message appears and close it if it appears$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          if (webDriverHelpers.isElementVisibleWithTimeout(GDPR_MESSAGE_DE, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(
                DO_NOT_SHOW_THIS_AGAIN_GDPR_MESSAGE_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON_DE);
          }
        });
  }
}
