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

import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.AFP_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.ANTHRAX_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CASE_STATUS_MAP_POINTS;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CHOLERA_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CONFIRMED_COUNTER_LABEL_ON_SURVEILLANCE_DASHBOARD;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CONFIRMED_COUNTER_LABEL_ON_SURVEILLANCE_DASHBOARD_DE;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CONFIRMED_COUNTER_ON_SURVEILLANCE_DASHBOARD;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CONFIRMED_COUNTER_ON_SURVEILLANCE_DASHBOARD_DE;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CONTACT_STATUS_MAP_POINTS;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.COVID_19_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.CRS_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.DASHBOARD_LAYERS_BUTTON;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_CASES;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.DASHBOARD_LAYERS_SHOW_CONTACTS;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.DASHBOARD_TODAY;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.DATE_TYPE;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.DENGUE_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.EVD_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.GUINEA_WORM_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.LASSA_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.MEASLES_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.MENINGITIS_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.MONKEYPOX_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.NEW_FLU_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.PLAGUE_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.POLIO_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.RABIES_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.REFERENCE_DEFINITION_FULFILLED_CASES_NUMBER;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.REGION_COMBOBOX_DROPDOWN;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.TIME_PERIOD_COMBOBOX;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.TIME_PERIOD_YESTERDAY_BUTTON;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.VHF_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.YELLOW_FEVER_BOX_IN_CAROUSEL_SLIDER_BAR;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.ZOOM_OUT_BUTTON_ON_MAP;

import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class SurveillanceDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final SoftAssert softly;
  private int covid19DiseaseCounterBefore;
  private int covid19DiseaseCounterAfter;
  private int newCasesCounterBefore;
  private int newCasesCounterAfter;
  private static BaseSteps baseSteps;
  public static String confirmedCases_EN;
  public static String confirmedCases_DE;
  public static List<Integer> numberOfCases = new ArrayList<Integer>();
  public static List<Integer> numberOfContacts = new ArrayList<Integer>();

  @Inject
  public SurveillanceDashboardSteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;
    this.baseSteps = baseSteps;

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
        "^I validate contacts button is clickable on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CONTACTS_BUTTON);
        });
    Then(
        "^I validate filter components presence on Surveillance Dashboard Page$",
        () -> {
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
        "^I validate presence of diseases metrics on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_METRICS);
        });
    Then(
        "^I validate presence of diseases slider on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_SLIDER);
        });
    Then(
        "^I validate presence of Epidemiological Curve on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.EPIDEMIOLOGICAL_CURVE);
        });
    Then(
        "^I validate presence of maps on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.STATUS_MAP);
        });
    Then(
        "^I validate show all diseases button is available and clickable on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.SHOW_ALL_DISEASES);
        });
    Then(
        "^I validate only 6 disease categories are displayed on Surveillance Dashboard Page$",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASE_CATEGORIES),
              6,
              "Number of displayed diseases boxes on surveillance dashboard is not correct");
        });
    Then(
        "^I click on show all diseases on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.SHOW_ALL_DISEASES);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.SHOW_ALL_DISEASES);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              SurveillanceDashboardPage.SHOW_ALL_DISEASES, 7);
        });
    Then(
        "^I validate presence of all diseases on Surveillance Dashboard Page$",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASE_CATEGORIES),
              20,
              "Number of displayed diseases boxes on surveillance dashboard is not correct");
        });
    Then(
        "^I validate name of diseases is shown on Surveillance Dashboard Page$",
        () -> {
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
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.FIRST_DISEASE_BOX),
              "",
              "First disease box is not displayed or empty");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.SECOND_DISEASE_BOX),
              "",
              "Second disease box is not displayed or empty");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.THIRD_DISEASE_BOX),
              "",
              "Third disease box is not displayed or empty");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.FOURTH_DISEASE_BOX),
              "",
              "Fourth disease box is not displayed or empty");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.FIFTH_DISEASE_BOX),
              "",
              "Fifth disease box is not displayed or empty");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.SIXTH_DISEASE_BOX),
              "",
              "Sixth disease box is not displayed or empty");
          softly.assertAll();
        });
    Then(
        "^I validate total data of diseases is shown on Surveillance Dashboard Page$",
        () -> {
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
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_FIRST_DISEASE_BOX),
              "",
              "Total data in first disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_SECOND_DISEASE_BOX),
              "",
              "Total data in second disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_THIRD_DISEASE_BOX),
              "",
              "Total data in third disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_FOURTH_DISEASE_BOX),
              "",
              "Total data in fourth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_FIFTH_DISEASE_BOX),
              "",
              "Total data in fifth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.TOTAL_DATA_SIXTH_DISEASE_BOX),
              "",
              "Total data in sixth disease box is empty of in not displayed");
          softly.assertAll();
        });
    Then(
        "^I validate compared data of diseases is shown on Surveillance Dashboard Page$",
        () -> {
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
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_FIRST_DISEASE_BOX),
              "",
              "Compared data in first disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_SECOND_DISEASE_BOX),
              "",
              "Compared data in second disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_THIRD_DISEASE_BOX),
              "",
              "Compared data in third third box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_FOURTH_DISEASE_BOX),
              "",
              "Compared data in fourth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_FIFTH_DISEASE_BOX),
              "",
              "Compared data in fifth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.COMPARED_DATA_SIXTH_DISEASE_BOX),
              "",
              "Compared data in sixth disease box is empty of in not displayed");
          softly.assertAll();
        });
    Then(
        "^I validate last report of diseases is shown on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.LAST_REPORT),
              6,
              "Number of displayed entries for last report on surveillance dashboard is not correct");
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
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_FIRST_DISEASE_BOX),
              "",
              "Last report data in first disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_SECOND_DISEASE_BOX),
              "",
              "Last report data in second disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_THIRD_DISEASE_BOX),
              "",
              "Last report data in third disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_FOURTH_DISEASE_BOX),
              "",
              "Last report data in fourth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_FIFTH_DISEASE_BOX),
              "",
              "Last report data in fifth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.LAST_REPORT_SIXTH_DISEASE_BOX),
              "",
              "Last report data in sixth disease box is empty of in not displayed");
          softly.assertAll();
        });
    Then(
        "^I validate fatalities of diseases is shown on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NavBarPage.DASHBOARD_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.FATALITIES),
              7,
              "Number of displayed entries for fatalities displayed on surveillance dashboard is not correct");
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
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_FIRST_DISEASE_BOX),
              "",
              "Fatalities data in first disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_SECOND_DISEASE_BOX),
              "",
              "Fatalities data in second disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_THIRD_DISEASE_BOX),
              "",
              "Fatalities data in third disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_FOURTH_DISEASE_BOX),
              "",
              "Fatalities data in fourth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_FIFTH_DISEASE_BOX),
              "",
              "Fatalities data in fifth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.FATALITIES_SIXTH_DISEASE_BOX),
              "",
              "Fatalities data in sixth disease box is empty of in not displayed");
          softly.assertAll();
        });
    Then(
        "^I validate number of events of diseases is shown on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.NUMBER_OF_EVENTS),
              6,
              "Number of displayed entries for number of events displayed on surveillance dashboard is not correct");
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
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_FIRST_DISEASE_BOX),
              "",
              "Number of events data in first disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_SECOND_DISEASE_BOX),
              "",
              "Number of events data in second disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_THIRD_DISEASE_BOX),
              "",
              "Number of events data in third disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_FOURTH_DISEASE_BOX),
              "",
              "Number of events data in fourth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_FIFTH_DISEASE_BOX),
              "",
              "Number of events data in fifth disease box is empty of in not displayed");
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(
                  SurveillanceDashboardPage.NUMBER_OF_EVENTS_SIXTH_DISEASE_BOX),
              "",
              "Number of events data in sixth disease box is empty of in not displayed");
          softly.assertAll();
        });
    Then(
        "^I switch to burden information table on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.DASHBOARD_BUTTON);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.BURDEN_TABLE_VIEW_SWITCH);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.DISEASE_BURDEN_INFORMATION);
        });
    Then(
        "^I validate that all the headers are present in the burden information table on Surveillance Dashboard Page$",
        () -> {
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
        "^I validate diseases presence in the data table on Surveillance Dashboard Page$",
        () -> {
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
        "^I validate switching back to disease boxes is working on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.BURDEN_TILE_VIEW_SWITCH);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FIRST_DISEASE_BOX);
        });
    Then(
        "^I validate all diseases are displayed in the carousel slider options on Surveillance Dashboard Page$",
        () -> {
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
        "^I validate counter is present on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_CASES_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR);
        });
    Then(
        "^I validate presence of left statistics charts on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.STATISTICS_CHARTS);
        });
    Then(
        "^I validate presence of cases metrics on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_MAIN_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_NOT_A_CASE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_CONFIRMED_BOX);
          // field no longer available
          //          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
          //              SurveillanceDashboardPage.CASES_METRICS_CONFIRMED_NO_SYMPTOMS_BOX);
          // field no longer available
          //          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
          //              SurveillanceDashboardPage.CASES_METRICS_CONFIRMED_UNKNOWN_SYMPTOMS_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_PROBABLE_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_SUSPECT_BOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CASES_METRICS_NOT_YET_CLASSIFIED_BOX);
        });
    Then(
        "^I validate presence of fatalities counter on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.FATALITIES_COUNTER);
        });
    Then(
        "^I validate presence of events counter on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.NEW_EVENTS_COUNTER);
        });
    Then(
        "^I validate presence of events metrics on Surveillance Dashboard Page$",
        () -> {
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
        "^I validate presence of test results counter on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_COUNTER);
        });
    Then(
        "^I validate presence of test results metrics on Surveillance Dashboard Page$",
        () -> {
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
        "^I validate presence of legend data on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.TEST_RESULTS_PENDING);
        });
    Then(
        "^I validate presence of chart on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATISTICS_CHARTS),
              2,
              "Statistic chart is missing or not displayed");
        });
    Then(
        "^I validate presence of chart download button on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_DOWNLOAD_BUTTON);
        });
    Then(
        "^I validate chart download options on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2); // wait for chart loaded
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_DOWNLOAD_BUTTON);
          webDriverHelpers.scrollToElement(SurveillanceDashboardPage.LEGEND_CHART_DOWNLOAD_BUTTON);
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
        "^I validate presence of chart buttons on Surveillance Dashboard Page$",
        () -> {
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
        "^I click on legend case status on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_CASE_STATUS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.LEGEND_DATA_CASE_STATUS_BUTTON);
        });
    Then(
        "^I check case status chart on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_CONFIRMED);
          // fields no longer available
          //          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
          //              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_CONFIRMED_NO_SYMPTOMS);
          //          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
          //
          // SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_CONFIRMED_UNKNOWN_SYMPTOMS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_NOT_YET_CLASSIFIED);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_PROBABLE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_CASE_STATUS_SUSPECT);
        });
    Then(
        "^I click on legend alive or dead on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_DATA_DEAD_OR_ALIVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.LEGEND_DATA_DEAD_OR_ALIVE_BUTTON);
        });
    Then(
        "^I check alive or dead chart on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_ALIVE_OR_DEAD_DEAD);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_ALIVE_OR_DEAD_ALIVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.LEGEND_CHART_ALIVE_OR_DEAD_UNKNOWN);
        });
    Then(
        "^I validate presence of map options on Surveillance Dashboard Page$",
        () -> {
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
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.EXIT_FULL_SCREEN_BUTTON_ON_MAP);
          // TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.EXIT_FULL_SCREEN_BUTTON_ON_MAP);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.EXPAND_MAP_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.COLLAPSE_MAP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.COLLAPSE_MAP_BUTTON);
        });
    Then(
        "^I validate presence of Map key options on Surveillance Dashboard Page$",
        () -> {
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
        "I validate presence of Layers options on Surveillance Dashboard Page$",
        () -> {
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

    Then(
        "I expand Epidemiological curve on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.EXPAND_EPI_CURVE);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.EXPAND_EPI_CURVE);
        });
    Then(
        "I verify that only epi curve chart is displayed on Surveillance Dashboard Page",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATUS_MAP),
              0,
              "Case status map should not be visible when epidemiological curve is expanded");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASES_LAYOUT),
              0,
              "Diseases layout should not be visible when epidemiological curve is expanded");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATISTICS_CHARTS),
              1,
              "Only one statistics chart should be visible when epidemiological curve is expanded");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.CURVE_AND_MAP_LAYOUT),
              1,
              "Curve and map layout should be visible when epidemiological curve is expanded");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CURVE_AND_MAP_LAYOUT);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.COLLAPSE_EPI_CURVE);
        });
    Then(
        "I expand Case status map on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.EXPAND_MAP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.EXPAND_MAP_BUTTON);
        });
    Then(
        "I verify only Case status map is displayed on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.STATUS_MAP);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATUS_MAP),
              1,
              "Case status map should be visible when it's expanded");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASES_LAYOUT),
              0,
              "Diseases layout should not be visible when status map is expanded");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATISTICS_CHARTS),
              0,
              "No statistics chart should be visible when status map is expanded");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.CURVE_AND_MAP_LAYOUT),
              1,
              "Curve and map layout should be visible when status map is expanded");
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.COLLAPSE_MAP_BUTTON);
        });
    Then(
        "I select Difference in Number of Cases hide overview on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.HIDE_OVERVIEW);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.HIDE_OVERVIEW);
        });
    Then(
        "I verify that Overview data is hidden on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATUS_MAP),
              1,
              "Case status map should be visible when hide overview is selected");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.DISEASES_LAYOUT),
              0,
              "Diseases layout should not be visible when hide overview is selected");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.STATISTICS_CHARTS),
              1,
              "Only curve statistics chart should be visible when hide overview is selected");
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(SurveillanceDashboardPage.CURVE_AND_MAP_LAYOUT),
              1,
              "Curve and map layout should be visible when hide overview is selected");
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.HIDE_OVERVIEW);
        });
    Then(
        "^I apply filter compare: today -> yesterday on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.CURRENT_PERIOD);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.DASHBOARD_TODAY);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.COMPARISON_PERIOD);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.DASHBOARD_DAY_BEFORE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.APPLY_FILTERS);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.APPLY_FILTERS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.APPLY_FILTERS);
        });
    Then(
        "^I verify filter works on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.APPLY_FILTERS);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.APPLY_FILTERS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.APPLY_FILTERS);
        });
    Then(
        "^I apply date filter on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.CURRENT_PERIOD);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.CURRENT_PERIOD);
          webDriverHelpers.clickOnWebElementBySelector(
              SurveillanceDashboardPage.DASHBOARD_THIS_WEEK);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.CURRENT_PERIOD);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.APPLY_FILTERS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.APPLY_FILTERS);
        });
    Then(
        "^I apply region filter on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.selectFromCombobox(
              REGION_COMBOBOX_DROPDOWN, "Voreingestellte Bundesl\u00E4nder");
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.APPLY_FILTERS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.APPLY_FILTERS);
        });
    Then(
        "^I click on reset filters on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.RESET_FILTERS);
          webDriverHelpers.clickOnWebElementBySelector(SurveillanceDashboardPage.RESET_FILTERS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.RESET_FILTERS);
        });
    Then(
        "^I verify that filters were reset on Surveillance Dashboard Page$",
        () -> {
          TimeUnit.SECONDS.sleep(10);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SurveillanceDashboardPage.RESET_FILTERS);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(REGION_COMBOBOX),
              "Region",
              "Default value of region combobox should be Region");
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(DATE_TYPE),
              "Most relevant date",
              "Default value of date type combobox should be Most relevant date");
        });
    Then(
        "I get Confirmed labels and value from Surveillance Dashboard with English language",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONFIRMED_COUNTER_LABEL_ON_SURVEILLANCE_DASHBOARD);
          confirmedCases_EN =
              webDriverHelpers.getWebElement(CONFIRMED_COUNTER_ON_SURVEILLANCE_DASHBOARD).getText();
        });

    Then(
        "I get Confirmed labels and value from Surveillance Dashboard with Deutsch language",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONFIRMED_COUNTER_LABEL_ON_SURVEILLANCE_DASHBOARD_DE);
          confirmedCases_DE =
              webDriverHelpers
                  .getWebElement(CONFIRMED_COUNTER_ON_SURVEILLANCE_DASHBOARD_DE)
                  .getText();
        });
    And(
        "I compare English and German confirmed counter",
        () -> {
          Assert.assertEquals(
              confirmedCases_EN, confirmedCases_DE, "Counters for confirmed cases are not equal!");
        });
    Then(
        "^I check that the Total number of COVID-19 cases excludes those marked \"not a case\" in German$",
        () -> {
          int covidTotalCases =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      SurveillanceDashboardPage.TOTAL_DATA_FIRST_DISEASE_BOX));
          int covidNewCases =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(SurveillanceDashboardPage.CASE_COUNTER));
          int covidNotACases =
              Integer.parseInt(
                  webDriverHelpers.getTextFromWebElement(
                      SurveillanceDashboardPage.CASES_METRICS_NOT_A_CASE_COUNTER_DE));

          softly.assertEquals(
              covidTotalCases,
              covidNewCases - covidNotACases,
              "The total number of COVID-19 cases does not exclude those marked \"not a case\" correctly!");
          softly.assertAll();
        });

    And(
        "I choose {string} in a disease filter on Surveillance Dashboard",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          switch (option.toUpperCase()) {
            case "AFP":
              webDriverHelpers.clickOnWebElementBySelector(AFP_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "ANTHRAX":
              webDriverHelpers.clickOnWebElementBySelector(ANTHRAX_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "COVID-19":
              webDriverHelpers.clickOnWebElementBySelector(COVID_19_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "CHOLERA":
              webDriverHelpers.clickOnWebElementBySelector(CHOLERA_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "CRS":
              webDriverHelpers.clickOnWebElementBySelector(CRS_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "DENGUE":
              webDriverHelpers.clickOnWebElementBySelector(DENGUE_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "EVD":
              webDriverHelpers.clickOnWebElementBySelector(EVD_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "GUINEA WORM":
              webDriverHelpers.clickOnWebElementBySelector(GUINEA_WORM_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "RABIES":
              webDriverHelpers.clickOnWebElementBySelector(RABIES_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "NEW FLU":
              webDriverHelpers.clickOnWebElementBySelector(NEW_FLU_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "LASSA":
              webDriverHelpers.clickOnWebElementBySelector(LASSA_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "MEASLES":
              webDriverHelpers.clickOnWebElementBySelector(MEASLES_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "MENINGITIS":
              webDriverHelpers.clickOnWebElementBySelector(MENINGITIS_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "MONKEYPOX":
              webDriverHelpers.clickOnWebElementBySelector(MONKEYPOX_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "PLAGUE":
              webDriverHelpers.clickOnWebElementBySelector(PLAGUE_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "POLIO":
              webDriverHelpers.clickOnWebElementBySelector(POLIO_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "VHF":
              webDriverHelpers.clickOnWebElementBySelector(VHF_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
            case "YELLOW FEVER":
              webDriverHelpers.clickOnWebElementBySelector(YELLOW_FEVER_BOX_IN_CAROUSEL_SLIDER_BAR);
              break;
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    And(
        "^I count the number of cases displayed on the Case Status Map$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          List<WebElement> pointsOnMap = getPointsTable(CASE_STATUS_MAP_POINTS);
          int pointsCounter = pointsOnMap.size();
          numberOfCases.add(pointsCounter);
        });

    And(
        "^I click the zoom out button (\\d+) times on the Case Status Map$",
        (Integer numberOfZooms) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ZOOM_OUT_BUTTON_ON_MAP);
          int counter = 0;
          while (counter < numberOfZooms) {
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(ZOOM_OUT_BUTTON_ON_MAP);
            webDriverHelpers.clickOnWebElementBySelector(ZOOM_OUT_BUTTON_ON_MAP);
            counter++;
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    And(
        "^I check that number of cases on the Case Status Map for yesterday has increased by \"([^\"]*)\"$",
        (Integer numberOfCasesAdded) -> {
          int previousNumberOfPoints = numberOfCases.get(0);
          int currentNumbersOfPoints = numberOfCases.get(2);
          softly.assertEquals(
              currentNumbersOfPoints,
              previousNumberOfPoints + numberOfCasesAdded,
              "Number of points on the Case Status Map has not increased by " + numberOfCasesAdded);
          softly.assertAll();
        });

    And(
        "^I check that number of contacts on the Case Status Map for yesterday has increased by \"([^\"]*)\"$",
        (Integer numberOfCasesAdded) -> {
          int previousNumberOfPoints = numberOfContacts.get(0);
          int currentNumbersOfPoints = numberOfContacts.get(2);
          softly.assertEquals(
              currentNumbersOfPoints,
              previousNumberOfPoints + numberOfCasesAdded,
              "Number of points on the Case Status Map has not increased by " + numberOfCasesAdded);
          softly.assertAll();
        });

    And(
        "^I choose today from the Surveillance Dashboard Time Period combobox$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DASHBOARD_TODAY);
        });

    And(
        "^I check that number of cases on the Case Status Map for today has not changed$",
        () -> {
          int previousNumberOfPoints = numberOfCases.get(1);
          int currentNumbersOfPoints = numberOfCases.get(3);
          softly.assertEquals(
              currentNumbersOfPoints,
              previousNumberOfPoints,
              "Number of points on the Case Status Map has changed!");
          softly.assertAll();
        });

    And(
        "^I check that number of contacts on the Case Status Map for today has not changed$",
        () -> {
          int previousNumberOfPoints = numberOfContacts.get(1);
          int currentNumbersOfPoints = numberOfContacts.get(3);
          softly.assertEquals(
              currentNumbersOfPoints,
              previousNumberOfPoints,
              "Number of points on the Case Status Map has changed!");
          softly.assertAll();
        });

    And(
        "^I click Layers button on Surveillance Dashboard Page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DASHBOARD_LAYERS_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DASHBOARD_LAYERS_SHOW_CASES);
        });

    And(
        "^I click checkbox to select Show contacts from Layers on the Case Status Map$",
        () -> webDriverHelpers.clickOnWebElementBySelector(DASHBOARD_LAYERS_SHOW_CONTACTS));

    And(
        "^I click checkbox to unselect Show cases from Layers on the Case Status Map$",
        () -> webDriverHelpers.clickOnWebElementBySelector(DASHBOARD_LAYERS_SHOW_CASES));

    And(
        "^I count the number of contacts displayed on the Case Status Map$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          List<WebElement> pointsOnMap = getPointsTable(CONTACT_STATUS_MAP_POINTS);
          int pointsCounter = pointsOnMap.size();
          numberOfContacts.add(pointsCounter);
        });
  }

  private List<WebElement> getPointsTable(By selector) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(ZOOM_OUT_BUTTON_ON_MAP);
    return baseSteps.getDriver().findElements(selector);
  }
}
