package org.sormas.e2etests.steps.web.application.dashboard.surveillance;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;

public class SurveillanceDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final SoftAssertions softly;
  private int covid19DiseaseCounterBefore;
  private int covid19DiseaseCounterAfter;
  private int newCasesCounterBefore;
  private int newCasesCounterAfter;

  @Inject
  public SurveillanceDashboardSteps(WebDriverHelpers webDriverHelpers, SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;

    When(
        "^I save value for COVID disease counter in Surveillance Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          String covid19DiseaseCounterRawValue =
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COVID19_DISEASE_COUNTER);
          covid19DiseaseCounterBefore = Integer.parseInt(covid19DiseaseCounterRawValue);
        });

    Then(
        "^I check that previous saved Surveillance Dashboard counters for COVID-19 have been increment$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);

          String newCasesCounterRawValueAfter =
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER);
          newCasesCounterAfter = Integer.parseInt(newCasesCounterRawValueAfter);

          String covid19DiseaseCounterRawValueAfter =
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COVID19_DISEASE_COUNTER);
          covid19DiseaseCounterAfter = Integer.parseInt(covid19DiseaseCounterRawValueAfter);

          softly.assertThat(newCasesCounterBefore).isLessThan(newCasesCounterAfter);
          softly.assertThat(covid19DiseaseCounterBefore).isLessThan(covid19DiseaseCounterAfter);
          softly.assertThat(newCasesCounterAfter).isEqualTo(covid19DiseaseCounterAfter);
          softly.assertAll();
        });

    When(
        "I select {string} in TabSheet of Surveillance Dashboard",
        (String tabSheetValue) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          webDriverHelpers.clickWebElementByText(
              SurveillanceDashboardPage.TAB_SHEET_CAPTION, tabSheetValue);
        });

    When(
        "^I save value for New Cases counter in Surveillance Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          String newCasesCounterValue =
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER);
          newCasesCounterBefore = Integer.parseInt(newCasesCounterValue);
        });
  }
}
