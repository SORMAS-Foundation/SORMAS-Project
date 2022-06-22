package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.ADD_NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FIRST_IMMUNIZATION_ID_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.state.ApiState;

public class ImmunizationDirectorySteps implements En {

  @Inject
  public ImmunizationDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      ApiState apiState) {

    When(
        "^I click on the NEW IMMUNIZATION button$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ADD_NEW_IMMUNIZATION_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(ADD_NEW_IMMUNIZATION_BUTTON);
        });

    When(
        "I open first immunization from grid from Immunization tab",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_IMMUNIZATION_ID_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_IMMUNIZATION_ID_BUTTON);
        });

    When(
        "^I navigate to last created immunization by API via URL$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.IMMUNIZATIONS_BUTTON);
          final String eventUuid = apiState.getCreatedImmunization().getUuid();
          final String eventLinkPath = "/sormas-ui/#!immunizations/data/";
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + eventLinkPath + eventUuid);
          webDriverHelpers.waitForPageLoaded();
        });
  }
}
