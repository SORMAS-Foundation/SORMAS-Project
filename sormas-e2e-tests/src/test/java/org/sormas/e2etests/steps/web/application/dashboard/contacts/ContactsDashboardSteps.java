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

package org.sormas.e2etests.steps.web.application.dashboard.contacts;

import static org.sormas.e2etests.pages.application.dashboard.Contacts.ContactsDashboardPage.CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD;
import static org.sormas.e2etests.pages.application.dashboard.Contacts.ContactsDashboardPage.CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD_DE;
import static org.sormas.e2etests.pages.application.dashboard.Contacts.ContactsDashboardPage.CONFIRMED_COUNTER_ON_CONTACTS_DASHBOARD;
import static org.sormas.e2etests.pages.application.dashboard.Contacts.ContactsDashboardPage.CONFIRMED_COUNTER_ON_CONTACTS_DASHBOARD_DE;
import static org.sormas.e2etests.pages.application.dashboard.Contacts.ContactsDashboardPage.UNDER_FU_CHART_ON_CONTACTS_DASHBOARD;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.ContactsDashboardPage.*;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class ContactsDashboardSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private int covid19ContactsCounterAfter;
  private int covid19ContactsCounterBefore;
  public static String confirmedContact_EN;
  public static String confirmedContact_DE;

  @Inject
  public ContactsDashboardSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I save value for COVID-19 contacts counter in Contacts Dashboard$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_COVID19_COUNTER, 40);
          covid19ContactsCounterBefore =
              Integer.parseInt(webDriverHelpers.getTextFromWebElement(CONTACTS_COVID19_COUNTER));
        });

    Then(
        "^I check that previous saved Contacts Dashboard contact counter for COVID-19 has been incremented$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONTACTS_COVID19_COUNTER);
          // TODO check if this sleep helps for Jenkins execution, otherwise remove it and create
          // proper handle
          TimeUnit.SECONDS.sleep(5);
          covid19ContactsCounterAfter =
              Integer.parseInt(webDriverHelpers.getTextFromWebElement(CONTACTS_COVID19_COUNTER));
          Assert.assertTrue(
              covid19ContactsCounterBefore < covid19ContactsCounterAfter,
              "COVID-19 contacts counter in Contacts dashboard hasn't  been incremented");
        });

    Then(
        "I get Confirmed Contact labels and value from Contact Dashboard with English language",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD);
          webDriverHelpers.getWebElement(CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD);
          confirmedContact_EN =
              webDriverHelpers.getWebElement(CONFIRMED_COUNTER_ON_CONTACTS_DASHBOARD).getText();

          webDriverHelpers.getWebElement(UNDER_FU_CHART_ON_CONTACTS_DASHBOARD);
        });
    Then(
        "I get Confirmed Contact labels and value from Contact Dashboard with Deutsch language",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD_DE);
          webDriverHelpers.getWebElement(CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD_DE);
          confirmedContact_DE =
              webDriverHelpers.getWebElement(CONFIRMED_COUNTER_ON_CONTACTS_DASHBOARD_DE).getText();

          webDriverHelpers.getWebElement(UNDER_FU_CHART_ON_CONTACTS_DASHBOARD);
        });
    And(
        "I compare English and German confirmed contacts counter",
        () -> {
          Assert.assertEquals(
              confirmedContact_EN,
              confirmedContact_DE,
              "Counters for confirmed contacts are not equal!");
        });

    When(
        "I click on the Contacts Radio button in Contact Dashboard",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONTACTS_RADIO_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACTS_RADIO_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
        });

    Then(
        "^I verify filter component ([^\"]*) in the Contacts Dashboard Page$",
        (String filterComponent) -> {
          switch (filterComponent) {
            case "Region":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  REGION_FILTER_COMBOBOX_CONTACTS_DASHBOARD);
              break;
            case "Disease":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  DISEASE_FILTER_COMBOBOX_CONTACTS_DASHBOARD);
              break;
            case "Reset Filters":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  RESET_FILTERS_BUTTON_CONTACTS_DASHBOARD);
              break;
            case "Apply Filters":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  APPLY_FILTERS_BUTTON_CONTACTS_DASHBOARD);
              break;
            default:
              throw new IllegalArgumentException("No valid options were provided");
          }
        });

    Then(
        "^I verify the ([^\"]*) Counter is displayed in the Contacts Dashboard Page$",
        (String counter) -> {
          switch (counter) {
            case "All Contacts":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  ALL_CONTACTS_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(ALL_CONTACTS_COUNTER),
                  "All Contacts counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            case "Under Follow-up":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  UNDER_FOLLOWUP_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(UNDER_FOLLOWUP_COUNTER),
                  "Under Follow-up counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            case "Stopped Follow-up":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  STOPPED_FOLLOWUP_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(STOPPED_FOLLOWUP_COUNTER),
                  "Stopped Follow-up counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            case "Visits":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(VISITS_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(VISITS_COUNTER),
                  "Visits counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            default:
              throw new IllegalArgumentException("No valid options were provided");
          }
        });

    Then(
        "^I verify the ([^\"]*) Metrics are displayed in the Contacts Dashboard Page$",
        (String metrics) -> {
          switch (metrics) {
            case "All Contacts":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  UNCONFIRMED_CONTACT_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(UNCONFIRMED_CONTACT_COUNTER),
                  "Unconfirmed contact counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONFIRMED_CONTACT_COUNTER),
                  "Confirmed contact counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(NOT_A_CONTACT_COUNTER),
                  "Not a contact counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(NEW_COUNTER),
                  "New counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(SYMPTOMATIC_COUNTER),
                  "Symptomatic counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_COVID19_COUNTER),
                  "Covid 19 counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_CHOLERA_COUNTER),
                  "Cholera counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_CONGENITAL_RUBELLA_COUNTER),
                  "Congenital Rubella counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_EBOLA_VIRUS_DISEASE_COUNTER),
                  "Ebola Virus counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_LASSA_COUNTER),
                  "Lassa counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_MONKEYPOX_COUNTER),
                  "Monkey Pox counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_INFLUENZA_NEW_SUBTYPE_COUNTER),
                  "Influenza New Subtype counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_PLAGUE_COUNTER),
                  "Plague counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_UNSPECIFIED_WHF_COUNTER),
                  "Unspecified WHF counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_HUMAN_RABIES_COUNTER),
                  "Human Rabies counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_OTHER_EPIDEMIC_DISEASE_COUNTER),
                  "Other epidemic disease counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTACTS_NOT_YET_DEFINED_COUNTER),
                  "Not yet defined counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            case "Under Follow-up":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COOPERATIVE_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(COOPERATIVE_COUNTER),
                  "Cooperative counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(UNCOOPERATIVE_COUNTER),
                  "Uncooperative counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(UNAVAILABLE_COUNTER),
                  "Unavailable counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(NEVER_VISITED_COUNTER),
                  "Never Visited counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            case "Stopped Follow-up":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  COMPLETED_FOLLOW_UP_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(COMPLETED_FOLLOW_UP_COUNTER),
                  "Completed Follow-up counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CANCELLED_FOLLOW_UP_COUNTER),
                  "Canceled Follow-up counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(LOST_TO_FOLLOW_UP_COUNTER),
                  "Lost to Follow-up counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONVERTED_TO_CASE_COUNTER),
                  "Converted to case counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            case "Visits":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  UNAVAILABLE_VISITS_COUNTER);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(UNAVAILABLE_VISITS_COUNTER),
                  "Unavailable Visits counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(UNCOOPERATIVE_VISITS_COUNTER),
                  "Uncooperative Visits counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(COOPERATIVE_VISITS_COUNTER),
                  "Cooperative Visits counter is not present in the Dashboard Contacts Page");
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(MISSED_VISITS_COUNTER),
                  "Missed Visits counter is not present in the Dashboard Contacts Page");
              softly.assertAll();
              break;
            default:
              throw new IllegalArgumentException("No valid options were provided");
          }
        });

    Then(
        "I verify the Contacts Per Case, min, max and average are displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_PER_CASE_MIN_COUNTER);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(CONTACTS_PER_CASE_MIN_COUNTER),
              "Contacts per Case Min counter is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(CONTACTS_PER_CASE_MAX_COUNTER),
              "Contacts per Case Max counter is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(CONTACTS_PER_CASE_AVERAGE_COUNTER),
              "Contacts per Case Average counter is not present in the Dashboard Contacts Page");
          softly.assertAll();
        });

    Then(
        "I verify that Contacts in Quarantine is displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_IN_QUARANTINE_COUNTER);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(CONTACTS_IN_QUARANTINE_COUNTER),
              "Contacts inn Quarantine counter is not present in the Dashboard Contacts Page");
          softly.assertAll();
        });

    Then(
        "I verify that New Cases not Previously Known to Be Contacts is displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_NEW_CASES_NOT_PREVIOUSLY_KNOWN_TO_BE_CONTACTS_COUNTER);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  CONTACTS_NEW_CASES_NOT_PREVIOUSLY_KNOWN_TO_BE_CONTACTS_COUNTER),
              "New Cases not Previously Known to Be Contacts counter is not present in the Dashboard Contacts Page");
          softly.assertAll();
        });

    Then(
        "I verify that Contacts placed in Quarantine is displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_PLACED_IN_QUARANTINE_COUNTER);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(CONTACTS_PLACED_IN_QUARANTINE_COUNTER),
              "Contacts placed in Quarantine counter is not present in the Dashboard Contacts Page");
          softly.assertAll();
        });

    Then(
        "I verify Follow-Up Status Chart Elements are displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              FOLLOWUP_STATUS_CHART_UNDER_FU);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(FOLLOWUP_STATUS_CHART_UNDER_FU),
              "FollowUp Status Chart Under FU is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(FOLLOWUP_STATUS_CHART_LOST_TO_FU),
              "FollowUp Status Chart Lost to FU is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(FOLLOWUP_STATUS_CHART_COMPLETED_FU),
              "FollowUp Status Chart Completed FU is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(FOLLOWUP_STATUS_CHART_CANCELED_FU),
              "FollowUp Status Chart Canceled FU is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DASHBOARD_GROUPING_DROPDOWN),
              "Grouping Dropdown is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DASHBOARD_DATA_DROPDOWN),
              "Data Dropdown is not present in the Dashboard Contacts Page");
          softly.assertAll();
        });

    And(
        "I click to Expand the Follow up status chart in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ENLARGE_FOLLOWUP_STATUS_CHART_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ENLARGE_FOLLOWUP_STATUS_CHART_BUTTON);
        });

    And(
        "I click to Collapse the Follow up status chart in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              COLLAPSE_FOLLOWUP_STATUS_CHART_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(COLLAPSE_FOLLOWUP_STATUS_CHART_BUTTON);
        });

    And(
        "I verify Follow up status Chart Context Menu and its contents in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CHART_CONTEXT_MENU);
          webDriverHelpers.clickOnWebElementBySelector(CHART_CONTEXT_MENU);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(PRINT_CHART_OPTION);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(PRINT_CHART_OPTION),
              "Print Chart option is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DOWNLOAD_PNG_IMAGE_OPTION),
              "Download PNG image option is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DOWNLOAD_JPEG_IMAGE_OPTION),
              "Download JPEG image option is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DOWNLOAD_PDF_DOCUMENT_OPTION),
              "Download PDF document option is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DOWNLOAD_SVG_VECTOR_IMAGE_OPTION),
              "Download SVG vector option is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DOWNLOAD_CSV_OPTION),
              "Download CSV option is not present in the Dashboard Contacts Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DOWNLOAD_XLS_OPTION),
              "Download XLS option is not present in the Dashboard Contacts Page");
          softly.assertAll();
        });

    And(
        "I click to Expand the Contact Map displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EXPAND_MAP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EXPAND_MAP_BUTTON);
        });

    And(
        "I click to Collapse the Contact Map displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COLLAPSE_MAP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(COLLAPSE_MAP_BUTTON);
        });

    Then(
        "I Verify Contact Map elements are displayed in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(DASHBOARD_MAP_KEYS);
          webDriverHelpers.clickOnWebElementBySelector(DASHBOARD_MAP_KEYS);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(DASHBOARD_MAP_LAYERS);
          webDriverHelpers.clickOnWebElementBySelector(DASHBOARD_MAP_LAYERS);
        });

    And(
        "I click on the Show All Diseases button in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SHOW_ALL_DISEASES_BUTTON_CONTACTS_DASHBOARD);
          webDriverHelpers.clickOnWebElementBySelector(SHOW_ALL_DISEASES_BUTTON_CONTACTS_DASHBOARD);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONTACTS_INFLUENZA_NEW_SUBTYPE_COUNTER);
        });

    And(
        "I click on the Show First Diseases button in the Contacts Dashboard Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SHOW_FIRST_DISEASES_BUTTON_CONTACTS_DASHBOARD);
          webDriverHelpers.clickOnWebElementBySelector(
              SHOW_FIRST_DISEASES_BUTTON_CONTACTS_DASHBOARD);
        });

    And(
        "^I Select the ([^\"]*) option from the Current period filter in Contact Dashboard$",
        (String option) -> {
          switch (option) {
            case "Custom":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_DASHBOARD_CUSTOM_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  CURRENT_PERIOD_DASHBOARD_CUSTOM_CONTACTS_DASHBOARD);
              break;
            case "Today":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_DASHBOARD_TODAY_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  CURRENT_PERIOD_DASHBOARD_TODAY_CONTACTS_DASHBOARD);
              break;
            case "Yesterday":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_DASHBOARD_YESTERDAY_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  CURRENT_PERIOD_DASHBOARD_YESTERDAY_CONTACTS_DASHBOARD);
              break;
            case "This week":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_DASHBOARD_THIS_WEEK_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  CURRENT_PERIOD_DASHBOARD_THIS_WEEK_CONTACTS_DASHBOARD);
              break;
            case "Last week":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_DASHBOARD_LAST_WEEK_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  CURRENT_PERIOD_DASHBOARD_LAST_WEEK_CONTACTS_DASHBOARD);
              break;
            case "This year":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(CURRENT_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  CURRENT_PERIOD_DASHBOARD_THIS_YEAR_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  CURRENT_PERIOD_DASHBOARD_THIS_YEAR_CONTACTS_DASHBOARD);
              break;
            default:
              throw new IllegalArgumentException("No valid options were provided");
          }
        });

    And(
        "^I Select the ([^\"]*) option from the Comparison period filter in Contact Dashboard$",
        (String option) -> {
          switch (option) {
            case "Day before":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  COMPARISON_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(COMPARISON_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  COMPARISON_PERIOD_DASHBOARD_DAY_BEFORE_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  COMPARISON_PERIOD_DASHBOARD_DAY_BEFORE_CONTACTS_DASHBOARD);
              break;
            case "Same day last year":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  COMPARISON_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(COMPARISON_PERIOD_CONTACTS_DASHBOARD);
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  COMPARISON_PERIOD_DASHBOARD_SAME_DAY_LAST_YEAR_CONTACTS_DASHBOARD);
              webDriverHelpers.clickOnWebElementBySelector(
                  COMPARISON_PERIOD_DASHBOARD_SAME_DAY_LAST_YEAR_CONTACTS_DASHBOARD);
              break;
            default:
              throw new IllegalArgumentException("No valid options were provided");
          }
        });

    And(
        "I select a region for the filter located in Contact Dashboard",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              REGION_FILTER_COMBOBOX_CONTACTS_DASHBOARD);
          webDriverHelpers.selectFromCombobox(REGION_FILTER_COMBOBOX_CONTACTS_DASHBOARD, "Berlin");
        });

    And(
        "I select a disease for the filter located in Contact Dashboard",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DISEASE_FILTER_COMBOBOX_CONTACTS_DASHBOARD);
          webDriverHelpers.selectFromCombobox(
              DISEASE_FILTER_COMBOBOX_CONTACTS_DASHBOARD, "COVID-19");
        });

    Then(
        "I click the Apply filter button in Contact Dashboard",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              APPLY_FILTERS_BUTTON_CONTACTS_DASHBOARD);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON_CONTACTS_DASHBOARD);
        });

    Then(
        "I click the Reset filter button in Contact Dashboard",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              RESET_FILTERS_BUTTON_CONTACTS_DASHBOARD);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTERS_BUTTON_CONTACTS_DASHBOARD);
        });
  }
}
