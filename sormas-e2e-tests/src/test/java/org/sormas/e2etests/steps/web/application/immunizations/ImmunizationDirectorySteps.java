package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.ADD_NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FIRST_IMMUNIZATION_ID_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class ImmunizationDirectorySteps implements En {

  @Inject
  public ImmunizationDirectorySteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I click on the NEW IMMUNIZATION button$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.clickOnWebElementBySelector(ADD_NEW_IMMUNIZATION_BUTTON);
        });

    When(
        "I open first immunization from grid from Immunization tab",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_IMMUNIZATION_ID_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_IMMUNIZATION_ID_BUTTON);
        });
  }
}
