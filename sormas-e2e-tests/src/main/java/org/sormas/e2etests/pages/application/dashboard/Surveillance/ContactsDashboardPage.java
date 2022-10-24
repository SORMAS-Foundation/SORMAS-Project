/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.pages.application.dashboard.Surveillance;

import org.openqa.selenium.By;

public class ContactsDashboardPage {

  public static final By CONTACTS_RADIO_BUTTON =
      By.cssSelector("[role='radiogroup']>:nth-child(2)");
  public static final By CURRENT_PERIOD_CONTACTS_DASHBOARD = By.id("currentPeriod");
  public static final By CURRENT_PERIOD_DASHBOARD_CUSTOM_CONTACTS_DASHBOARD =
      By.id("dashboardCustom");
  public static final By CURRENT_PERIOD_DASHBOARD_TODAY_CONTACTS_DASHBOARD =
      By.id("dashboardToday");
  public static final By CURRENT_PERIOD_DASHBOARD_YESTERDAY_CONTACTS_DASHBOARD =
      By.id("dashboardYesterday");
  public static final By CURRENT_PERIOD_DASHBOARD_THIS_WEEK_CONTACTS_DASHBOARD =
      By.id("dashboardThisWeek");
  public static final By CURRENT_PERIOD_DASHBOARD_LAST_WEEK_CONTACTS_DASHBOARD =
      By.id("dashboardLastWeek");
  public static final By CURRENT_PERIOD_DASHBOARD_THIS_YEAR_CONTACTS_DASHBOARD =
      By.id("dashboardThisYear");
  public static final By COMPARISON_PERIOD_CONTACTS_DASHBOARD = By.id("comparisonPeriod");
  public static final By COMPARISON_PERIOD_DASHBOARD_DAY_BEFORE_CONTACTS_DASHBOARD =
      By.id("dashboardDayBefore");
  public static final By COMPARISON_PERIOD_DASHBOARD_SAME_DAY_LAST_YEAR_CONTACTS_DASHBOARD =
      By.id("dashboardSameDayLastYear");
  public static final By REGION_FILTER_COMBOBOX_CONTACTS_DASHBOARD =
      By.cssSelector("[location='regionFilter']>div>div");
  public static final By DISEASE_FILTER_COMBOBOX_CONTACTS_DASHBOARD =
      By.cssSelector("[location='diseaseFilter']>div>div");
  public static final By RESET_FILTERS_BUTTON_CONTACTS_DASHBOARD = By.id("actionResetFilters");
  public static final By APPLY_FILTERS_BUTTON_CONTACTS_DASHBOARD = By.id("actionApplyFilters");
  public static final By SHOW_ALL_DISEASES_BUTTON_CONTACTS_DASHBOARD =
      By.id("dashboardShowAllDiseases");
  public static final By SHOW_FIRST_DISEASES_BUTTON_CONTACTS_DASHBOARD =
      By.id("dashboardShowFirstDiseases");
  public static final By CONTACTS_DASHBOARD_NAME = By.cssSelector("[class*=\"h1 v-label-h1\"]");
  public static final By ALL_CONTACTS_COUNTER =
      By.cssSelector("[location='firstLoc']>div>div>:nth-child(1)>div>div>div>:nth-child(2)>div");
  public static final By UNDER_FOLLOWUP_COUNTER =
      By.cssSelector("[location='secondLoc']>div>div>:nth-child(1)>div>div>div>:nth-child(2)>div");
  public static final By STOPPED_FOLLOWUP_COUNTER =
      By.cssSelector(
          "[location='thirdLoc']>div>:nth-child(1)>:nth-child(1)>div>div>div>:nth-child(2)>div");
  public static final By VISITS_COUNTER =
      By.cssSelector(
          "[location='fourthLoc']>div>:nth-child(1)>:nth-child(1)>div>div>div>:nth-child(2)>div");
  public static final By UNCONFIRMED_CONTACT_COUNTER =
      By.cssSelector("[location='firstLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(1)");
  public static final By CONFIRMED_CONTACT_COUNTER =
      By.cssSelector("[location='firstLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(2)");
  public static final By NOT_A_CONTACT_COUNTER =
      By.cssSelector("[location='firstLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(3)");
  public static final By NEW_COUNTER =
      By.cssSelector("[location='firstLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(4)");
  public static final By SYMPTOMATIC_COUNTER =
      By.cssSelector("[location='firstLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(5)");
  public static final By COOPERATIVE_COUNTER =
      By.cssSelector(
          "[location='secondLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(1)>:nth-child(1)");
  public static final By UNCOOPERATIVE_COUNTER =
      By.cssSelector(
          "[location='secondLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(2)>:nth-child(1)");
  public static final By UNAVAILABLE_COUNTER =
      By.cssSelector(
          "[location='secondLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(3)>:nth-child(1)");
  public static final By NEVER_VISITED_COUNTER =
      By.cssSelector(
          "[location='secondLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(4)>:nth-child(1)");
  public static final By COMPLETED_FOLLOW_UP_COUNTER =
      By.cssSelector(
          "[location='thirdLoc']>div>:nth-child(2)>div>:nth-child(1)>:nth-child(1)>:nth-child(1)>div>div>:nth-child(3)>div");
  public static final By CANCELLED_FOLLOW_UP_COUNTER =
      By.cssSelector(
          "[location='thirdLoc']>div>:nth-child(2)>div>:nth-child(2)>:nth-child(1)>:nth-child(1)>div>div>:nth-child(3)>div");
  public static final By LOST_TO_FOLLOW_UP_COUNTER =
      By.cssSelector(
          "[location='thirdLoc']>div>:nth-child(2)>div>:nth-child(3)>:nth-child(1)>:nth-child(1)>div>div>:nth-child(3)>div");
  public static final By CONVERTED_TO_CASE_COUNTER =
      By.cssSelector(
          "[location='thirdLoc']>div>:nth-child(2)>div>:nth-child(4)>:nth-child(1)>:nth-child(1)>div>div>:nth-child(3)>div");
  public static final By UNAVAILABLE_VISITS_COUNTER =
      By.cssSelector(
          "[location='fourthLoc']>div>:nth-child(2)>div>:nth-child(1)>div>:nth-child(1)>div>div>:nth-child(2)");
  public static final By UNCOOPERATIVE_VISITS_COUNTER =
      By.cssSelector(
          "[location='fourthLoc']>div>:nth-child(2)>div>:nth-child(2)>div>:nth-child(1)>div>div>:nth-child(2)");
  public static final By COOPERATIVE_VISITS_COUNTER =
      By.cssSelector(
          "[location='fourthLoc']>div>:nth-child(2)>div>:nth-child(3)>div>:nth-child(1)>div>div>:nth-child(2)");
  public static final By MISSED_VISITS_COUNTER =
      By.cssSelector(
          "[location='fourthLoc']>div>:nth-child(2)>div>:nth-child(5)>div>:nth-child(1)>div>div>:nth-child(2)");
  public static final By CONTACTS_COVID19_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(2)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_CHOLERA_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(3)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_CONGENITAL_RUBELLA_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(4)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_EBOLA_VIRUS_DISEASE_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(5)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_LASSA_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(6)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_MONKEYPOX_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(7)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_INFLUENZA_NEW_SUBTYPE_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(8)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_PLAGUE_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(9)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_UNSPECIFIED_WHF_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(10)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_HUMAN_RABIES_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(11)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_OTHER_EPIDEMIC_DISEASE_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(12)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_NOT_YET_DEFINED_COUNTER =
      By.cssSelector(
          "[location='firstLoc']>div>:nth-child(2)>div>:nth-child(13)>div>:nth-child(1)>:nth-child(5)");
  public static final By CONTACTS_PER_CASE_MIN_COUNTER =
      By.xpath(
          "(//div[@class='v-label v-widget primary v-label-primary large-alt v-label-large-alt bold "
              + "v-label-bold vspace-5 v-label-vspace-5 hspace-right-3 v-label-hspace-right-3 v-label-undef-w'])[1]");
  public static final By CONTACTS_PER_CASE_MAX_COUNTER =
      By.xpath(
          "(//div[@class='v-label v-widget primary v-label-primary large-alt v-label-large-alt bold"
              + " v-label-bold vspace-5 v-label-vspace-5 hspace-right-3 v-label-hspace-right-3 v-label-undef-w'])[2]");
  public static final By CONTACTS_PER_CASE_AVERAGE_COUNTER =
      By.xpath(
          "//div[@class='v-horizontallayout v-layout v-horizontal v-widget"
              + " highlighted-statistics-component v-horizontallayout-highlighted-statistics-component v-has-width']"
              + "//div[@class='v-slot']//div[@class='v-label v-widget primary v-label-primary large-alt v-label-large-alt"
              + " bold v-label-bold vspace-5 v-label-vspace-5 v-label-undef-w']");
  public static final By CONTACTS_IN_QUARANTINE_COUNTER =
      By.xpath(
          "(//div[@class='v-label v-widget primary v-label-primary large-alt v-label-large-alt bold"
              + " v-label-bold vspace-5 v-label-vspace-5 hspace-right-3 v-label-hspace-right-3 v-label-undef-w'])[3]");
  public static final By CONTACTS_PLACED_IN_QUARANTINE_COUNTER =
      By.xpath(
          "(//div[@class='v-label v-widget primary v-label-primary large-alt v-label-large-alt"
              + " bold v-label-bold vspace-5 v-label-vspace-5 hspace-right-3 v-label-hspace-right-3"
              + " v-label-undef-w'])[4]");
  public static final By CONTACTS_NEW_CASES_NOT_PREVIOUSLY_KNOWN_TO_BE_CONTACTS_COUNTER =
      By.xpath(
          "(//div[@class='v-label v-widget primary v-label-primary large-alt"
              + " v-label-large-alt bold v-label-bold vspace-5 v-label-vspace-5 v-label-undef-w'])[2]");
  public static final By FOLLOWUP_STATUS_CHART_UNDER_FU =
      By.xpath(
          "//body[1]/div[1]/div[1]/div[2]/div[1]"
              + "/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/div[1]"
              + "/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/*[name()='svg']/*[name()='g']/*[name()='g']/*[name()='g']"
              + "/*[name()='g']/*[name()='text']/*[name()='tspan']");
  public static final By FOLLOWUP_STATUS_CHART_LOST_TO_FU =
      By.cssSelector(
          "body>div:nth-child(1)>div:nth-child(1)"
              + ">div:nth-child(2)>div:nth-child(1)>div:nth-child(2)>div:nth-child(1)>div:nth-child(1)>div:nth-child(1)"
              + ">div:nth-child(2)>div:nth-child(1)>div:nth-child(1)>div:nth-child(2)>div:nth-child(1)>div:nth-child(4)"
              + ">div:nth-child(1)>div:nth-child(1)>div:nth-child(1)>div:nth-child(1)>div:nth-child(1)>div:nth-child(1)"
              + ">div:nth-child(1)>div:nth-child(1)>div:nth-child(2)>div:nth-child(1)>div:nth-child(1)>svg:nth-child(1)"
              + ">g:nth-child(21)>g:nth-child(2)>g:nth-child(1)>g:nth-child(2)>text:nth-child(1)>tspan:nth-child(1)");
  public static final By FOLLOWUP_STATUS_CHART_COMPLETED_FU =
      By.cssSelector(
          "body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1)"
              + " > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) >"
              + " div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(4) >"
              + " div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) >"
              + " div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) >"
              + " div:nth-child(1) > svg:nth-child(1) > g:nth-child(21) > g:nth-child(2) > g:nth-child(1) >"
              + " g:nth-child(3) > text:nth-child(1) > tspan:nth-child(1)");
  public static final By FOLLOWUP_STATUS_CHART_CANCELED_FU =
      By.cssSelector(
          "body > div:nth-child(1) >"
              + " div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) >"
              + " div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) >"
              + " div:nth-child(2) > div:nth-child(1) > div:nth-child(4) > div:nth-child(1) > div:nth-child(1) >"
              + " div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) >"
              + " div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > svg:nth-child(1) >"
              + " g:nth-child(21) > g:nth-child(2) > g:nth-child(1) > g:nth-child(4) > text:nth-child(1) > tspan:nth-child(1)");

  public static final By CHART_CONTEXT_MENU = By.cssSelector("[class='highcharts-button-symbol']");
  public static final By PRINT_CHART_OPTION = By.xpath("(//li[@class='highcharts-menu-item'])[1]");
  public static final By DOWNLOAD_PNG_IMAGE_OPTION =
      By.xpath("(//li[@class='highcharts-menu-item'])[2]");
  public static final By DOWNLOAD_JPEG_IMAGE_OPTION =
      By.xpath("(//li[@class='highcharts-menu-item'])[3]");
  public static final By DOWNLOAD_PDF_DOCUMENT_OPTION =
      By.xpath("(//li[@class='highcharts-menu-item'])[4]");
  public static final By DOWNLOAD_SVG_VECTOR_IMAGE_OPTION =
      By.xpath("(//li[@class='highcharts-menu-item'])[5]");
  public static final By DOWNLOAD_CSV_OPTION = By.xpath("(//li[@class='highcharts-menu-item'])[6]");
  public static final By DOWNLOAD_XLS_OPTION = By.xpath("(//li[@class='highcharts-menu-item'])[7]");
  public static final By ENLARGE_FOLLOWUP_STATUS_CHART_BUTTON = By.id("expandEpiCurve");
  public static final By COLLAPSE_FOLLOWUP_STATUS_CHART_BUTTON = By.id("collapseEpiCurve");
  public static final By EXPAND_MAP_BUTTON = By.id("expandMap");
  public static final By COLLAPSE_MAP_BUTTON = By.id("collapseMap");
  public static final By DASHBOARD_GROUPING_DROPDOWN = By.id("dashboardGrouping");
  public static final By DASHBOARD_DATA_DROPDOWN = By.id("dashboardGrouping");
  public static final By DASHBOARD_MAP_KEYS = By.id("dashboardMapKey");
  public static final By DASHBOARD_MAP_LAYERS = By.id("dashboardMapLayers");
}
