package org.sormas.e2etests.steps.web.application.dashboard.surveillance;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;

public class SurveillanceDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final SoftAssertions softly;
  private int covid19DiseaseCounterIntValueBefore;
  private int covid19DiseaseCounterIntValueAfter;
  private int newCasesCounterIntValueBefore;
  private int newCasesCounterIntValueAfter;

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
          covid19DiseaseCounterIntValueBefore = Integer.parseInt(covid19DiseaseCounterRawValue);
        });

    Then(
        "^I check that previous saved Surveillance Dashboard counters for COVID-19 have been increment$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);

          String newCasesCounterRawValueAfter =
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER);
          newCasesCounterIntValueAfter = Integer.parseInt(newCasesCounterRawValueAfter);

          String covid19DiseaseCounterRawValueAfter =
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COVID19_DISEASE_COUNTER);
          covid19DiseaseCounterIntValueAfter = Integer.parseInt(covid19DiseaseCounterRawValueAfter);

          softly.assertThat(newCasesCounterIntValueBefore).isLessThan(newCasesCounterIntValueAfter);
          softly
              .assertThat(covid19DiseaseCounterIntValueBefore)
              .isLessThan(covid19DiseaseCounterIntValueAfter);
          softly
              .assertThat(newCasesCounterIntValueAfter)
              .isEqualTo(covid19DiseaseCounterIntValueAfter);
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
          newCasesCounterIntValueBefore = Integer.parseInt(newCasesCounterValue);
        });
  }
}
