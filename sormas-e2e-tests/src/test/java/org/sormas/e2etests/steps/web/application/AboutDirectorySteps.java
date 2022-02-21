package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.AboutPage.DATA_DICTIONARY_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class AboutDirectorySteps implements En {

  @Inject
  public AboutDirectorySteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I click on Data Dictionary button$",
        () -> {
          webDriverHelpers.waitForPageLoaded();

          webDriverHelpers.clickOnWebElementBySelector(DATA_DICTIONARY_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DATA_DICTIONARY_BUTTON, 50);
        });
  }
}
