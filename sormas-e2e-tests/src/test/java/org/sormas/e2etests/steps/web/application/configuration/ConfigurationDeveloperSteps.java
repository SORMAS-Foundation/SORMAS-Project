package org.sormas.e2etests.steps.web.application.configuration;

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_DEVELOPER_TAB;
import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.EXECUTE_AUTOMATIC_DELETION;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.UPLOAD_SUCCESS_POPUP;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class ConfigurationDeveloperSteps implements En {

  private final WebDriverHelpers webDriverHelpers;

  @SneakyThrows
  @Inject
  public ConfigurationDeveloperSteps(WebDriverHelpers webDriverHelpers) {

    this.webDriverHelpers = webDriverHelpers;

    When(
        "I navigate to Developer tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_DEVELOPER_TAB));

    When(
        "I click on Execute Automatic Deletion button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXECUTE_AUTOMATIC_DELETION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UPLOAD_SUCCESS_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(UPLOAD_SUCCESS_POPUP);
        });

    When(
        "I wait {int} seconds for system reaction",
        (Integer seconds) -> TimeUnit.SECONDS.sleep(seconds));

    When(
        "I check if Execute Automatic Deletion button is available",
        () -> {
          webDriverHelpers.scrollToElement(EXECUTE_AUTOMATIC_DELETION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXECUTE_AUTOMATIC_DELETION);
        });
  }
}
