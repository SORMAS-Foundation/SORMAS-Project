package org.sormas.e2etests.steps.web.application.keycloak;

import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.NEXT_PAGE_BUTTON;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.USER_ID;
import static org.sormas.e2etests.pages.application.keycloak.KeycloakAdminConsolePage.VIEW_ALL_USERS_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.users.UserManagementSteps;
import org.testng.Assert;

public class KeycloakAdminConsoleSteps implements En {

  protected WebDriverHelpers webDriverHelpers;
  int numberOfUsers;

  @Inject
  public KeycloakAdminConsoleSteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I navigate to Users tab in Keycloak Administrator Console$",
        () -> {
          String KEYCLOAK_USERS_TAB =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/keycloak/auth/admin/master/console/#/realms/SORMAS/users";
          webDriverHelpers.accessWebSite(KEYCLOAK_USERS_TAB);
        });
    When(
        "^I click View all users button$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(VIEW_ALL_USERS_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEXT_PAGE_BUTTON);
        });
    When(
        "^I count the number of users displayed in Users tab in Keycloak Administrator Console$",
        () -> {
          numberOfUsers = 0;
          do {
            numberOfUsers += webDriverHelpers.getNumberOfElements(USER_ID);
            webDriverHelpers.clickOnWebElementBySelector(NEXT_PAGE_BUTTON);
            TimeUnit.SECONDS.sleep(2);
          } while (webDriverHelpers.isElementEnabled(NEXT_PAGE_BUTTON));
          numberOfUsers += webDriverHelpers.getNumberOfElements(USER_ID);
        });
    When(
        "^I check that number of users from SORMAS is equal to number of users in Keycloak Administrator Console$",
        () ->
            assertHelpers.assertWithPoll(
                () ->
                    Assert.assertEquals(
                        numberOfUsers,
                        UserManagementSteps.numberOfUsers,
                        "Number of users in Keycloak is not equal to number of users in SORMAS"),
                5));
  }
}
