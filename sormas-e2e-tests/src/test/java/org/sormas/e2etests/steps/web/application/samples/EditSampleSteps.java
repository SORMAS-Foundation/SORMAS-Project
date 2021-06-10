package org.sormas.e2etests.steps.web.application.samples;

import static org.sormas.e2etests.pages.application.samples.EditSamplePage.PATHOGEN_NEW_TEST_RESULT_BTN;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class EditSampleSteps implements En {

  @Inject
  public EditSampleSteps(WebDriverHelpers webDriverHelpers) {

    When(
        "I click on the new pathogen test from the Edit Sample page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PATHOGEN_NEW_TEST_RESULT_BTN);
          webDriverHelpers.scrollToElement(PATHOGEN_NEW_TEST_RESULT_BTN);
          webDriverHelpers.clickOnWebElementBySelector(PATHOGEN_NEW_TEST_RESULT_BTN);
        });
  }
}
