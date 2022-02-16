/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          webDriverHelpers.clickWebElementByText(
              SurveillanceDashboardPage.TAB_SHEET_CAPTION, tabSheetValue);
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "^I save value for New Cases counter in Surveillance Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          String newCasesCounterValue =
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER);
          newCasesCounterBefore = Integer.parseInt(newCasesCounterValue);
        });

    Then(
        "^I validate contacts button is clickable$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CONTACTS_BUTTON);
        });
    Then(
        "^I validate filter components presence$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CURRENT_PERIOD);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COMPARISON_PERIOD);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DATE_TYPE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.REGION_COMBOBOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.RESET_FILTERS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.APPLY_FILTERS);
        });
    Then(
        "^I validate presence of diseases metrics$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_METRICS);
        });
    Then(
        "^I validate presence of diseases slider$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_SLIDER);
        });
    Then(
        "^I validate presence of Epidemiological Curve$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.EPIDEMIOLOGICAL_CURVE);
        });
    Then(
        "^I validate presence of maps$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.STATUS_MAP);
        });
    Then(
        "^I validate show all diseases button is available and clickable$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.SHOW_ALL_DISEASES);
        });
    Then(
        "^I validate only 6 disease categories are displayed$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASE_CATEGORIES),
              6,
              "Number of displayed cases is not correct");
        });
    Then(
        "^I click on show all diseases$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.SHOW_ALL_DISEASES);
        });
    Then(
        "^I validate presence of all diseases$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASE_CATEGORIES),
              20,
              "Number of displayed cases is not correct");
        });
  }
}
