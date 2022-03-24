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

import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.REFERENCE_DEFINITION_FULFILLED_CASES_NUMBER;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.TIME_PERIOD_COMBOBOX;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.TIME_PERIOD_YESTERDAY_BUTTON;

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
          softly.assertTrue(
              number > 0, "The number of cases fulfilling the reference definition is incorrect!");
          softly.assertAll();
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
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.SHOW_ALL_DISEASES);
        });
    Then(
        "^I validate only 6 disease categories are displayed$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASE_CATEGORIES),
              6,
              "Number of displayed cases is not correct");
        });
    Then(
        "^I click on show all diseases$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.SHOW_ALL_DISEASES);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.SHOW_ALL_DISEASES);
          // webDriverHelpers.javaScriptClickElement(SurveillanceDashboardPage.SHOW_ALL_DISEASES);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              SurveillanceDashboardPage.SHOW_ALL_DISEASES, 7);
        });
    Then(
        "^I validate presence of all diseases$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
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
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.BURDEN_TABLE_VIEW_SWITCH);
          webDriverHelpers.waitForPageLoaded();
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
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.BURDEN_TILE_VIEW_SWITCH);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FIRST_DISEASE_BOX);
        });
    Then(
        "^I validate all diseases are displayed in the carousel slider options$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          // TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.AFP_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.ANTHRAX_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COVID_19_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CHOLERA_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CRS_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DENGUE_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.EVD_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.GUINEA_WORM_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.RABIES_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_FLU_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LASSA_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.MEASLES_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.MENINGITIS_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.MONKEYPOX_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.PLAGUE_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.POLIO_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.VHF_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.YELLOW_FEVER_BOX_IN_CAROUSEL_SLIDER_BAR);
        });
    Then(
        "^I validate counter is present$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          // TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_CASES_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR);
        });
    Then(
        "^I validate presence of left statistics charts$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          // TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.STATISTICS_CHARTS);
        });
    Then(
        "^I validate presence of cases metrics$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_MAIN_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_NOT_A_CASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_CONFIRMED_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_CONFIRMED_NO_SYMPTOMS_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_CONFIRMED_UNKNOWN_SYMPTOMS_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_PROBABLE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_SUSPECT_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_NOT_YET_CLASSIFIED_BOX);
        });
    Then(
        "^I validate presence of fatalities counter$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_COUNTER);
        });
    Then(
        "^I validate presence of events counter$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_COUNTER);
        });
    Then(
        "^I validate presence of events metrics$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_TYPE_CLUSTER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_TYPE_EVENT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_TYPE_SIGNAL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_TYPE_DROPPED);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_TYPE_SCREENING);
        });
    Then(
        "^I validate presence of test results counter$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_COUNTER);
        });
    Then(
        "^I validate presence of test results metrics$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_INDETERMINATE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_POSITIVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_NEGATIVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_PENDING);
        });
    Then(
        "^I validate presence of legend data$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_PENDING);
        });
    Then(
        "^I validate presence of chart$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA);
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATISTICS_CHARTS),
              2,
              "Statistic chart is missing or not displayed");
        });
    Then(
        "^I validate presence of chart download button$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_DOWNLOAD_BUTTON);
        });
    Then(
        "^I validate chart download options$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.LEGEND_CHART_DOWNLOAD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DOWNLOAD_CHART_OPTION_PRINT_CHART);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DOWNLOAD_CHART_OPTION_DOWNLOAD_PNG_IMAGE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DOWNLOAD_CHART_OPTION_DOWNLOAD_JPEG_IMAGE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DOWNLOAD_CHART_OPTION_DOWNLOAD_PDF_DOCUMENT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DOWNLOAD_CHART_OPTION_DOWNLOAD_SVG_VECTOR_IMAGE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DOWNLOAD_CHART_OPTION_DOWNLOAD_CSV);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DOWNLOAD_CHART_OPTION_DOWNLOAD_XLS);
        });
    Then(
        "^I validate presence of chart buttons$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_EXPAND_EPI_CURVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_GROUPING_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.LEGEND_DATA_GROUPING_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_GROUPING_DAY);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_GROUPING_EPI_WEEK);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_GROUPING_MONTH);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_GROUPING_ALWAYS_SHOW_AT_LEAST_7_ENTRIES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_CASE_STATUS_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_DEAD_OR_ALIVE_BUTTON);
        });
    Then(
        "^I click on legend case status$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_CASE_STATUS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.LEGEND_DATA_CASE_STATUS_BUTTON);
        });
    Then(
        "^I check case status chart$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_CONFIRMED);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_CONFIRMED_NO_SYMPTOMS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_CONFIRMED_UNKNOWN_SYMPTOMS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_NOT_YET_CLASSIFIED);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_PROBABLE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_SUSPECT);
        });
    Then(
        "^I click on legend alive or dead$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_DEAD_OR_ALIVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.LEGEND_DATA_DEAD_OR_ALIVE_BUTTON);
        });
    Then(
        "^I check alive or dead chart$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_ALIVE_OR_DEAD_DEAD);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_ALIVE_OR_DEAD_ALIVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_ALIVE_OR_DEAD_UNKNOWN);
        });
    Then(
        "^I validate presence of map options$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.ZOOM_IN_BUTTON_ON_MAP);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.ZOOM_OUT_BUTTON_ON_MAP);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FULL_SCREEN_BUTTON_ON_MAP);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.EXPAND_MAP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.ZOOM_IN_BUTTON_ON_MAP);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.ZOOM_OUT_BUTTON_ON_MAP);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.FULL_SCREEN_BUTTON_ON_MAP);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.EXIT_FULL_SCREEN_BUTTON_ON_MAP);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.EXPAND_MAP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.COLLAPSE_MAP_BUTTON);
        });
    Then(
        "^I validate presence of Map key options$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_ONLY_NOT_YET_CLASSIFIED_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_SUSPECT_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_PROBABLE_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_CONFIRMED_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_NOT_YET_CLASSIFIED);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_SUSPECT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_PROBABLE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_MAP_KEY_CONFIRMED);
        });
    Then(
        "I validate presence of Layers options$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_ALL_CASES);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_CONFIRMED_CASES_ONLY);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_CONTACTS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_EVENTS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_REGIONS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_EPIDEMIOLOGICAL_SITUATION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DASHBOARD_LAYERS_HIDE_OTHER_COUNTRIES);
        });
  }
}
