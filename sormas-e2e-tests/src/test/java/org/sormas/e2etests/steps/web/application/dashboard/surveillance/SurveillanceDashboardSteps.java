/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.steps.web.application.dashboard.surveillance;

import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.REFERENCE_DEFINITION_FULFILLED_CASES_NUMBER;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.TIME_PERIOD_COMBOBOX;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.TIME_PERIOD_YESTERDAY_BUTTON;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;
import org.testng.asserts.SoftAssert;

public class SurveillanceDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final SoftAssert softly;
  private int covid19DiseaseCounterBefore;
  private int covid19DiseaseCounterAfter;
  private int newCasesCounterBefore;
  private int newCasesCounterAfter;

  @Inject
  public SurveillanceDashboardSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;

    When(
        "^I save value for COVID disease counter in Surveillance Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
          covid19DiseaseCounterBefore =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      SurveillanceDashboardPage.COVID19_DISEASE_COUNTER));
        });

    Then(
        "^I check that previous saved Surveillance Dashboard counters for COVID-19 have been increment$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);

          newCasesCounterAfter =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER));

          covid19DiseaseCounterAfter =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      SurveillanceDashboardPage.COVID19_DISEASE_COUNTER));

          softly.assertTrue(
              newCasesCounterBefore < newCasesCounterAfter,
              "New cases counter for COVID-19 in Surveillance Dashboard has not been increased");
          softly.assertTrue(
              covid19DiseaseCounterBefore < covid19DiseaseCounterAfter,
              "COVID-19 disease counter in Surveillance Dashboard has not been increased");
          softly.assertEquals(
              newCasesCounterAfter,
              covid19DiseaseCounterAfter,
              "New cases counter for COVID-19 does not equal COVID-19 disease counter in Surveillance Dashboard");
          softly.assertAll();
        });

    When(
        "I select {string} in TabSheet of Surveillance Dashboard",
        (String tabSheetValue) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickWebElementByText(
              SurveillanceDashboardPage.TAB_SHEET_CAPTION, tabSheetValue);
          TimeUnit.SECONDS.sleep(10);
        });

    When(
        "^I save value for New Cases counter in Surveillance Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          String newCasesCounterValue =
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER);
          newCasesCounterBefore = Integer.parseInt(newCasesCounterValue);
        });

    When(
        "^I click on the Time Period combobox from Surveillance Dashboard$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TIME_PERIOD_COMBOBOX);
        });

    When(
        "^I choose yesterday from the Surveillance Dashboard Time Period combobox$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TIME_PERIOD_YESTERDAY_BUTTON);
        });

    When(
        "^I check that the number of cases fulfilling the reference definition is larger than 0$",
        () -> {
          Integer number =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      REFERENCE_DEFINITION_FULFILLED_CASES_NUMBER));
          softly.assertTrue(number > 0, "The number of cases fulfilling the reference definition is incorrect!");
          softly.assertAll();
        });
  }
}
