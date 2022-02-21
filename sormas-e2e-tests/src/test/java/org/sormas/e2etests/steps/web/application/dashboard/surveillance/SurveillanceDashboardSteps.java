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
    Then(
        "^I validate name of diseases is shown$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FIRST_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.SECOND_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.THIRD_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FOURTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FIFTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.SIXTH_DISEASE_BOX);
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.FIRST_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.SECOND_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.THIRD_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.FOURTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.FIFTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.SIXTH_DISEASE_BOX),
              "");
        });
    Then(
        "^I validate total data of diseases is shown$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TOTAL_DATA_FIRST_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TOTAL_DATA_SECOND_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TOTAL_DATA_THIRD_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TOTAL_DATA_FOURTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TOTAL_DATA_FIFTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TOTAL_DATA_SIXTH_DISEASE_BOX);
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_FIRST_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_SECOND_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_THIRD_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_FOURTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_FIFTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_SIXTH_DISEASE_BOX),
              "");
        });
    Then(
        "^I validate compared data of diseases is shown$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COMPARED_DATA_FIRST_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COMPARED_DATA_SECOND_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COMPARED_DATA_THIRD_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COMPARED_DATA_FOURTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COMPARED_DATA_FIFTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COMPARED_DATA_SIXTH_DISEASE_BOX);
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_FIRST_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_SECOND_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_THIRD_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_FOURTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_FIFTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_SIXTH_DISEASE_BOX),
              "");
        });
    Then(
        "^I validate last report of diseases is shown$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.LAST_REPORT),
              6,
              "Number of displayed cases is not correct");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LAST_REPORT_FIRST_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LAST_REPORT_SECOND_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LAST_REPORT_THIRD_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LAST_REPORT_FOURTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LAST_REPORT_FIFTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LAST_REPORT_SIXTH_DISEASE_BOX);
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_FIRST_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_SECOND_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_THIRD_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_FOURTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_FIFTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_SIXTH_DISEASE_BOX),
              "");
        });
    Then(
        "^I validate fatalities of diseases is shown$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.FATALITIES),
              7,
              "Number of displayed cases is not correct");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_FIRST_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_SECOND_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_THIRD_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_FOURTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_FIFTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_SIXTH_DISEASE_BOX);
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_FIRST_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_SECOND_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_THIRD_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_FOURTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_FIFTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_SIXTH_DISEASE_BOX),
              "");
        });
    Then(
        "^I validate number of events of diseases is shown$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.NUMBER_OF_EVENTS),
              6,
              "Number of displayed cases is not correct");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NUMBER_OF_EVENTS_FIRST_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NUMBER_OF_EVENTS_SECOND_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NUMBER_OF_EVENTS_THIRD_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NUMBER_OF_EVENTS_FOURTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NUMBER_OF_EVENTS_FIFTH_DISEASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NUMBER_OF_EVENTS_SIXTH_DISEASE_BOX);
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_FIRST_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_SECOND_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_THIRD_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_FOURTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_FIFTH_DISEASE_BOX),
              "");
          Assert.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_SIXTH_DISEASE_BOX),
              "");
        });
    Then(
        "^I switch to burden information table$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.BURDEN_TABLE_VIEW_SWITCH);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_INFORMATION);
        });
    Then(
        "^I validate that all the headers are present in the burden information table$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_DISEASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_NEW_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_PREVIOUS_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_DYNAMIC);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_NUMBER_OF_EVENTS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_OUTBREAK_DISTRICTS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_FATALITIES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_CFR);
        });
    Then(
        "^I validate diseases presence in the data table$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_FIRST_DISEASE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_SECOND_DISEASE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_THIRD_DISEASE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_FOURTH_DISEASE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_FIFTH_DISEASE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_BOX_SIXTH_DISEASE);
        });
    Then(
        "^I validate switching back to disease boxes is working$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.BURDEN_TILE_VIEW_SWITCH);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FIRST_DISEASE_BOX);
        });
  }
}
