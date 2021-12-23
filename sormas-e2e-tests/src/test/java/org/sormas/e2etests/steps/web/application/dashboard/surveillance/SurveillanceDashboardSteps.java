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

          covid19DiseaseCounterBefore =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      SurveillanceDashboardPage.COVID19_DISEASE_COUNTER));
        });

    Then(
        "^I check that previous saved Surveillance Dashboard counters for COVID-19 have been increment$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);

          newCasesCounterAfter =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER));

          covid19DiseaseCounterAfter =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      SurveillanceDashboardPage.COVID19_DISEASE_COUNTER));

          softly
              .assertThat(newCasesCounterBefore)
              .withFailMessage(
                  "New cases counter for COVID-19 in Surveillance Dashboard has not been increased")
              .isLessThan(newCasesCounterAfter);
          softly
              .assertThat(covid19DiseaseCounterBefore)
              .withFailMessage(
                  "COVID-19 disease counter in Surveillance Dashboard has not been increased")
              .isLessThan(covid19DiseaseCounterAfter);
          softly
              .assertThat(newCasesCounterAfter)
              .withFailMessage(
                  "New cases counter for COVID-19 does not equal COVID-19 disease counter in Surveillance Dashboard")
              .isEqualTo(covid19DiseaseCounterAfter);
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
