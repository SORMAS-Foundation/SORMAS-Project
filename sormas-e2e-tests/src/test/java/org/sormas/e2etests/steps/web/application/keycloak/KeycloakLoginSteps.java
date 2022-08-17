package org.sormas.e2etests.steps.web.application.keycloak;

import static org.sormas.e2etests.pages.application.keycloak.KeycloakLoginPage.LOGIN_KEYCLOAK_BUTTON;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakLoginPage.LOGOUT_BUTTON;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakLoginPage.PASSWORD_INPUT;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakLoginPage.USERNAME_INPUT;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakLoginPage.USERNAME_TEXT;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.envconfig.dto.EnvUser;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.LoginPage;
import org.testng.Assert;

public class KeycloakLoginSteps implements En {

  protected WebDriverHelpers webDriverHelpers;

  @Inject
  public KeycloakLoginSteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I navigate to Keycloak Administrator Console Login page$",
        () -> {
          String KEYCLOAK_ADMIN_PAGE =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/keycloak/auth/admin/master/console";
          webDriverHelpers.accessWebSite(KEYCLOAK_ADMIN_PAGE);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(USERNAME_INPUT);
        });
    Given(
        "^I log in as ([^\"]*) to Keycloak Administrator Console$",
        (String userRole) -> {
          String KEYCLOAK_ADMIN_PAGE =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/keycloak/auth/admin/master/console";
          webDriverHelpers.accessWebSite(KEYCLOAK_ADMIN_PAGE);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LoginPage.USER_NAME_INPUT);
          EnvUser user = runningConfiguration.getUserByRole(locale, userRole);
          webDriverHelpers.fillInWebElement(USERNAME_INPUT, user.getUsername());
          webDriverHelpers.fillInWebElement(PASSWORD_INPUT, user.getPassword());
          webDriverHelpers.clickOnWebElementBySelector(LOGIN_KEYCLOAK_BUTTON);
          webDriverHelpers.waitForPageLoaded();
        });
    Given(
        "^I am logged in with ([^\"]*) in Keycloak Administrator Page$",
        (String name) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getTextFromWebElement(USERNAME_TEXT).trim(),
                        name,
                        "Username is not correct")));
    And(
        "I click on logout button on Keycloak Administrator Console Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(USERNAME_TEXT);
          webDriverHelpers.clickWebElementByText(LOGOUT_BUTTON, "Sign Out");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LOGIN_KEYCLOAK_BUTTON);
        });
  }
}
