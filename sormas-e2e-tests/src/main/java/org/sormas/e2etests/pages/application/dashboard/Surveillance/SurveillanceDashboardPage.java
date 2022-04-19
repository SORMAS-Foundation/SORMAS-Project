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

package org.sormas.e2etests.pages.application.dashboard.Surveillance;

import org.openqa.selenium.By;

public class SurveillanceDashboardPage {

  public static final By SURVEILLANCE_DASHBOARD_NAME =
      By.xpath("//div[contains(text(),'Surveillance Dashboard')]");
  public static final By SURVEILLANCE_BUTTON =
      By.xpath("(//div[contains(@class,'v-select-optiongroup')]//span)[1]");
  public static final By CONTACTS_BUTTON =
      By.xpath("(//div[contains(@class,'v-select-optiongroup')]//span)[2]");
  public static final By LOGOUT_BUTTON =
      By.cssSelector("#actionLogout span.v-menubar-menuitem-caption");
  public static final By COVID19_DISEASE_COUNTER =
      By.cssSelector("div.v-verticallayout-background-disease-coronavirus > div > div > div");
  public static final By CASE_COUNTER =
      By.cssSelector("[location='case'] > div > div > div > div > div");
  public static final By TAB_SHEET_CAPTION = By.cssSelector(".v-tabsheet > div > div > div > div");
  public static final By TIME_PERIOD_COMBOBOX = By.id("currentPeriod");
  public static final By TIME_PERIOD_YESTERDAY_BUTTON = By.id("dashboardYesterday");
  public static final By REFERENCE_DEFINITION_FULFILLED_CASES_NUMBER =
      By.xpath(
          "/html/body/div[1]/div/div[2]/div/div[2]/div/div/div/div[2]/div/div/div[3]/div/div[2]/div/div/div[2]/div/div/div/div[7]/div/div[2]/div");
  public static final By CURRENT_PERIOD = By.id("currentPeriod");
  public static final By COMPARISON_PERIOD = By.id("comparisonPeriod");
  public static final By DATE_TYPE = By.cssSelector("div#dateType > .v-filterselect-input");
  public static final By REGION_COMBOBOX =
      By.cssSelector("div:nth-of-type(3) > div[role='combobox'] > .v-filterselect-input");
  public static final By RESET_FILTERS = By.cssSelector("div#actionResetFilters");
  public static final By APPLY_FILTERS = By.cssSelector("div#actionApplyFilters");
  public static final By DISEASE_METRICS =
      By.cssSelector(
          ".v-has-width.v-layout.v-margin-left.v-margin-right.v-vertical.v-verticallayout.v-verticallayout-vspace-top-4.v-widget.vspace-top-4");
  public static final By DISEASE_SLIDER =
      By.cssSelector(
          "div:nth-of-type(3) > .v-has-height.v-has-width.v-widget > .highcharts-container > .highcharts-root > .highcharts-background");
  public static final By EPIDEMIOLOGICAL_CURVE =
      By.cssSelector(
          "div:nth-of-type(1) > .v-has-height.v-has-width.v-layout.v-margin-bottom.v-margin-left.v-margin-right.v-margin-top.v-vertical.v-verticallayout.v-widget > .v-expand > div:nth-of-type(1) > .v-has-width.v-horizontal.v-horizontallayout.v-horizontallayout-vspace-4.v-layout.v-widget.vspace-4 > .v-expand > .v-align-bottom.v-slot.v-slot-h2.v-slot-vspace-4.v-slot-vspace-top-none");
  public static final By STATUS_MAP = By.cssSelector("[id^='leaflet_']");
  public static final By SHOW_ALL_DISEASES = By.cssSelector("#dashboardShowAllDiseases");
  public static final By DISEASE_CATEGORIES_COUNTER =
      By.cssSelector("[class='v-verticallayout v-layout v-vertical v-widget v-has-width']");
  public static final By DISEASE_CATEGORIES =
      By.cssSelector("[class='col-lg-6 col-xs-12 '][location='burden'] >div>div>div>div>div");
  public static final By AFP_DISEASE_BOX = By.xpath("//div[contains(text(),'AFP')]");
  public static final By ANTHRAX_DISEASE_BOX = By.xpath("//div[contains(text(),'Anthrax')]");
  public static final By COVID_19_DISEASE_BOX = By.xpath("//div[contains(text(),'COVID-19')]");
  public static final By CHOLERA_DISEASE_BOX = By.xpath("//div[contains(text(),'Cholera')]");
  public static final By CRS_DISEASE_BOX = By.xpath("//div[contains(text(),'CRS')]");
  public static final By DENGUE_DISEASE_BOX = By.xpath("//div[contains(text(),'Dengue')]");
  public static final By FIRST_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By SECOND_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By THIRD_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By FOURTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By FIFTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[5]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By SIXTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[6]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By TOTAL_DATA_FIRST_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]");
  public static final By TOTAL_DATA_SECOND_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]");
  public static final By TOTAL_DATA_THIRD_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]");
  public static final By TOTAL_DATA_FOURTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]");
  public static final By TOTAL_DATA_FIFTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[5]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]");
  public static final By TOTAL_DATA_SIXTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[6]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]");
  public static final By COMPARED_DATA_FIRST_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]");
  public static final By COMPARED_DATA_SECOND_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]");
  public static final By COMPARED_DATA_THIRD_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]");
  public static final By COMPARED_DATA_FOURTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]");
  public static final By COMPARED_DATA_FIFTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[5]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]");
  public static final By COMPARED_DATA_SIXTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[6]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]");
  public static final By NUMBER_OF_EVENTS = By.xpath("//div[contains(text(),'Number of events')]");
  public static final By FATALITIES = By.xpath("//div[contains(text(),'Fatalities')]");
  public static final By LAST_REPORT = By.xpath("//div[contains(text(),'Last report:')]");
  public static final By LAST_REPORT_FIRST_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]");
  public static final By LAST_REPORT_SECOND_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]");
  public static final By LAST_REPORT_THIRD_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]");
  public static final By LAST_REPORT_FOURTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[4]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]");
  public static final By LAST_REPORT_FIFTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[5]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]");
  public static final By LAST_REPORT_SIXTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]");
  public static final By FATALITIES_FIRST_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]");
  public static final By FATALITIES_SECOND_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]");
  public static final By FATALITIES_THIRD_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]");
  public static final By FATALITIES_FOURTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[4]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]");
  public static final By FATALITIES_FIFTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[5]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]");
  public static final By FATALITIES_SIXTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]");
  public static final By NUMBER_OF_EVENTS_FIRST_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/div[1]/div[1]/div[2]/div[1]");
  public static final By NUMBER_OF_EVENTS_SECOND_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]/div[1]/div[2]/div[1]");
  public static final By NUMBER_OF_EVENTS_THIRD_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/div[2]/div[1]/div[3]/div[1]/div[1]/div[2]/div[1]");
  public static final By NUMBER_OF_EVENTS_FOURTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[4]/div[2]/div[1]/div[3]/div[1]/div[1]/div[2]/div[1]");
  public static final By NUMBER_OF_EVENTS_FIFTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[5]/div[2]/div[1]/div[3]/div[1]/div[1]/div[2]/div[1]");
  public static final By NUMBER_OF_EVENTS_SIXTH_DISEASE_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[1]/div[3]/div[1]/div[1]/div[2]/div[1]");
  public static final By DISEASE_BURDEN_INFORMATION =
      By.xpath("//div[contains(text(),'Disease Burden Information')]");
  public static final By BURDEN_TABLE_VIEW_SWITCH = By.xpath("//div[@id='showTableView']");
  public static final By BURDEN_TILE_VIEW_SWITCH = By.xpath("//div[@id='showTileView']");
  public static final By DISEASE_BURDEN_BOX_DISEASES = By.xpath("//thead/tr[1]/th[1]/div[1]");
  public static final By DISEASE_BURDEN_BOX_NEW_CASES =
      By.xpath("//div[contains(text(),'New cases')]");
  public static final By DISEASE_BURDEN_BOX_PREVIOUS_CASES =
      By.xpath("//div[contains(text(),'Previous cases')]");
  public static final By DISEASE_BURDEN_BOX_DYNAMIC = By.xpath("//div[contains(text(),'Dynamic')]");
  public static final By DISEASE_BURDEN_BOX_NUMBER_OF_EVENTS =
      By.xpath("//div[contains(text(),'Number of events')]");
  public static final By DISEASE_BURDEN_BOX_OUTBREAK_DISTRICTS =
      By.xpath("//div[contains(text(),'Outbreak districts')]");
  public static final By DISEASE_BURDEN_BOX_FATALITIES = By.xpath("//thead/tr[1]/th[7]/div[1]");
  public static final By DISEASE_BURDEN_BOX_CFR = By.xpath("//div[contains(text(),'CFR')]");
  public static final By DISEASE_BURDEN_BOX_FIRST_DISEASE =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/table[1]/tbody[1]/tr[1]/td[1]");
  public static final By DISEASE_BURDEN_BOX_SECOND_DISEASE =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/table[1]/tbody[1]/tr[2]/td[1]");
  public static final By DISEASE_BURDEN_BOX_THIRD_DISEASE =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/table[1]/tbody[1]/tr[3]/td[1]");
  public static final By DISEASE_BURDEN_BOX_FOURTH_DISEASE =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/table[1]/tbody[1]/tr[4]/td[1]");
  public static final By DISEASE_BURDEN_BOX_FIFTH_DISEASE =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/table[1]/tbody[1]/tr[5]/td[1]");
  public static final By DISEASE_BURDEN_BOX_SIXTH_DISEASE =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/table[1]/tbody[1]/tr[6]/td[1]");
  public static final By AFP_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'AFP')]");
  public static final By ANTHRAX_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Anthrax')]");
  public static final By COVID_19_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'COVID-19')]");
  public static final By CHOLERA_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Cholera')]");
  public static final By CRS_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'CRS')]");
  public static final By DENGUE_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Dengue')]");
  public static final By EVD_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'EVD')]");
  public static final By GUINEA_WORM_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Guinea Worm')]");
  public static final By RABIES_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Rabies')]");
  public static final By NEW_FLU_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'New Flu')]");
  public static final By LASSA_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Lassa')]");
  public static final By MEASLES_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Measles')]");
  public static final By MENINGITIS_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Meningitis')]");
  public static final By MONKEYPOX_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Monkeypox')]");
  public static final By PLAGUE_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Plague')]");
  public static final By POLIO_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Polio')]");
  public static final By VHF_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'VHF')]");
  public static final By YELLOW_FEVER_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath("//span[contains(text(),'Yellow Fever')]");
  public static final By NEW_CASES_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By NEW_EVENTS_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By TEST_RESULTS_COUNTER_BOX_IN_CAROUSEL_SLIDER_BAR =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By STATISTICS_CHARTS = By.cssSelector("[id^='highchart_']");
  public static final By DIFFERENCE_IN_NUMBER_OF_CASES_GRAPH =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]");
  public static final By CASES_METRICS_MAIN_BOX =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]");
  public static final By CASES_METRICS_CONFIRMED_NO_SYMPTOMS_BOX =
      By.xpath("//div[contains(text(),'Confirmed no symptoms')]");
  public static final By CASES_METRICS_CONFIRMED_BOX =
      By.xpath("//div[contains(text(),'Confirmed')]");
  public static final By CASES_METRICS_CONFIRMED_UNKNOWN_SYMPTOMS_BOX =
      By.xpath("//div[contains(text(),'Confirmed unknown symptoms')]");
  public static final By CASES_METRICS_PROBABLE_BOX =
      By.xpath("//div[contains(text(),'Probable')]");
  public static final By CASES_METRICS_SUSPECT_BOX = By.xpath("//div[contains(text(),'Suspect')]");
  public static final By CASES_METRICS_NOT_A_CASE_BOX =
      By.xpath("//div[contains(text(),'Not A Case')]");
  public static final By CASES_METRICS_NOT_YET_CLASSIFIED_BOX =
      By.xpath("//div[contains(text(),'Not Yet Classified')]");
  public static final By FATALITIES_COUNTER =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[2]");
  public static final By NEW_EVENTS_COUNTER =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By NEW_EVENTS_TYPE_CLUSTER = By.xpath("//div[contains(text(),'Cluster')]");
  public static final By NEW_EVENTS_TYPE_EVENT = By.xpath("//div[contains(text(),'Event')]");
  public static final By NEW_EVENTS_TYPE_SIGNAL = By.xpath("//div[contains(text(),'Signal')]");
  public static final By NEW_EVENTS_TYPE_SCREENING =
      By.xpath("//div[contains(text(),'Screening')]");
  public static final By NEW_EVENTS_TYPE_DROPPED = By.xpath("//div[contains(text(),'Dropped')]");
  public static final By TEST_RESULTS_COUNTER =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]");
  public static final By TEST_RESULTS_POSITIVE = By.xpath("//div[contains(text(),'Positive')]");
  public static final By TEST_RESULTS_NEGATIVE = By.xpath("//div[contains(text(),'Negative')]");
  public static final By TEST_RESULTS_PENDING = By.xpath("//div[contains(text(),'Pending')]");
  public static final By TEST_RESULTS_INDETERMINATE =
      By.xpath("//div[contains(text(),'Indeterminate')]");
  public static final By LEGEND_DATA =
      By.xpath(
          "/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[3]/div[1]/div[3]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/*[name()='svg'][1]/*[name()='rect'][1]");
  public static final By LEGEND_CHART_DOWNLOAD_BUTTON =
      By.xpath(
          "//*[name()='g' and contains(@class,'highcharts')]//*[name()='g' and contains(@class,'highcharts')]//*[name()='path' and contains(@class,'highcharts')]");
  public static final By DOWNLOAD_CHART_OPTION_PRINT_CHART =
      By.xpath("//li[contains(text(),'Print chart')]");
  public static final By DOWNLOAD_CHART_OPTION_DOWNLOAD_PNG_IMAGE =
      By.xpath("//li[contains(text(),'Download PNG image')]");
  public static final By DOWNLOAD_CHART_OPTION_DOWNLOAD_JPEG_IMAGE =
      By.xpath("//li[contains(text(),'Download JPEG image')]");
  public static final By DOWNLOAD_CHART_OPTION_DOWNLOAD_PDF_DOCUMENT =
      By.xpath("//li[contains(text(),'Download PDF document')]");
  public static final By DOWNLOAD_CHART_OPTION_DOWNLOAD_SVG_VECTOR_IMAGE =
      By.xpath("//li[contains(text(),'Download SVG vector image')]");
  public static final By DOWNLOAD_CHART_OPTION_DOWNLOAD_CSV =
      By.xpath("//li[contains(text(),'Download CSV')]");
  public static final By DOWNLOAD_CHART_OPTION_DOWNLOAD_XLS =
      By.xpath("//li[contains(text(),'Download XLS')]");
  public static final By LEGEND_DATA_EXPAND_EPI_CURVE_BUTTON =
      By.xpath("//div[@id='expandEpiCurve']");
  public static final By LEGEND_DATA_DEAD_OR_ALIVE_BUTTON =
      By.xpath("//label[contains(text(),'Alive or dead')]/parent::*");
  public static final By LEGEND_DATA_CASE_STATUS_BUTTON =
      By.xpath("//label[contains(text(),'Case status')]/parent::*");
  public static final By LEGEND_DATA_GROUPING_BUTTON = By.xpath("//div[@id='dashboardGrouping']");
  public static final By LEGEND_DATA_GROUPING_DAY =
      By.xpath("//label[contains(text(),'Day')]/preceding::input[1]");
  public static final By LEGEND_DATA_GROUPING_EPI_WEEK =
      By.xpath("//label[contains(text(),'Epi Week')]/preceding::input[1]");
  public static final By LEGEND_DATA_GROUPING_MONTH =
      By.xpath("//label[contains(text(),'Month')]/preceding::input[1]");
  public static final By LEGEND_DATA_GROUPING_ALWAYS_SHOW_AT_LEAST_7_ENTRIES =
      By.xpath("//label[contains(text(),'Always show at least 7 entries')]");
  public static final By LEGEND_CHART_CASE_STATUS_CONFIRMED =
      By.cssSelector(
          "g.highcharts-legend-item.highcharts-column-series.highcharts-color-undefined.highcharts-series-5 > text > tspan");
  public static final By LEGEND_CHART_CASE_STATUS_CONFIRMED_UNKNOWN_SYMPTOMS =
      By.cssSelector(
          "g.highcharts-legend-item.highcharts-column-series.highcharts-color-undefined.highcharts-series-3 > text > tspan");
  public static final By LEGEND_CHART_CASE_STATUS_SUSPECT =
      By.cssSelector(
          "g.highcharts-legend-item.highcharts-column-series.highcharts-color-undefined.highcharts-series-1 > text > tspan");
  public static final By LEGEND_CHART_CASE_STATUS_CONFIRMED_NO_SYMPTOMS =
      By.cssSelector(
          "g.highcharts-legend-item.highcharts-column-series.highcharts-color-undefined.highcharts-series-4 > text > tspan");
  public static final By LEGEND_CHART_CASE_STATUS_PROBABLE =
      By.cssSelector(
          "g.highcharts-legend-item.highcharts-column-series.highcharts-color-undefined.highcharts-series-2 > text > tspan");
  public static final By LEGEND_CHART_CASE_STATUS_NOT_YET_CLASSIFIED =
      By.cssSelector(
          "g.highcharts-legend-item.highcharts-column-series.highcharts-color-undefined.highcharts-series-0 > text > tspan");
  public static final By LEGEND_CHART_ALIVE_OR_DEAD_UNKNOWN =
      By.xpath("//*/text()[normalize-space(.)='Unknown']/parent::*");
  public static final By LEGEND_CHART_ALIVE_OR_DEAD_DEAD =
      By.xpath("//*/text()[normalize-space(.)='Dead']/parent::*");
  public static final By LEGEND_CHART_ALIVE_OR_DEAD_ALIVE =
      By.xpath("//*/text()[normalize-space(.)='Alive']/parent::*");
  public static final By ZOOM_IN_BUTTON_ON_MAP = By.cssSelector("[title='Zoom in']");
  public static final By ZOOM_OUT_BUTTON_ON_MAP = By.cssSelector("[title='Zoom out']");
  public static final By FULL_SCREEN_BUTTON_ON_MAP = By.cssSelector("[title='View Fullscreen']");
  public static final By EXIT_FULL_SCREEN_BUTTON_ON_MAP =
      By.cssSelector("[title='Exit Fullscreen']");
  public static final By EXPAND_MAP_BUTTON = By.cssSelector("#expandMap");
  public static final By COLLAPSE_MAP_BUTTON = By.cssSelector("#collapseMap");
  public static final By DASHBOARD_MAP_KEY_BUTTON = By.cssSelector("#dashboardMapKey");
  public static final By DASHBOARD_MAP_KEY_ONLY_NOT_YET_CLASSIFIED_CASES =
      By.xpath("//div[contains(text(),'Only Not Yet Classified Cases')]");
  public static final By DASHBOARD_MAP_KEY_SUSPECT_CASES =
      By.xpath("//div[contains(text(),'> 1 Suspect Cases')]");
  public static final By DASHBOARD_MAP_KEY_PROBABLE_CASES =
      By.xpath("//div[contains(text(),'> 1 Probable Cases')]");
  public static final By DASHBOARD_MAP_KEY_CONFIRMED_CASES =
      By.xpath("//div[contains(text(),'> 1 Confirmed Cases')]");
  public static final By DASHBOARD_MAP_KEY_NOT_YET_CLASSIFIED =
      By.xpath(
          "//body/div[2]/div[2]/div[@class='popupContent']/div/div[4]/div/div[1]/div/div[@class='v-slot v-slot-small']/div[.='Not Yet Classified']");
  public static final By DASHBOARD_MAP_KEY_SUSPECT =
      By.xpath(
          "//body/div[2]/div[2]/div[@class='popupContent']/div/div[4]/div/div[2]/div/div[@class='v-slot v-slot-small']/div[.='Suspect']");
  public static final By DASHBOARD_MAP_KEY_PROBABLE =
      By.xpath(
          "//body/div[2]/div[2]/div[@class='popupContent']/div/div[4]/div/div[3]/div/div[@class='v-slot v-slot-small']/div[.='Probable']");
  public static final By DASHBOARD_MAP_KEY_CONFIRMED =
      By.xpath(
          "//body/div[2]/div[2]/div[@class='popupContent']/div/div[4]/div/div[@class='v-slot']/div/div[@class='v-slot v-slot-small']/div[.='Confirmed']");
  public static final By DASHBOARD_LAYERS_BUTTON = By.cssSelector("#dashboardMapLayers");
  public static final By DASHBOARD_LAYERS_SHOW_CASES =
      By.xpath("//span[@id='dashboardShowCases']/label[.='Show cases']");
  public static final By DASHBOARD_LAYERS_SHOW_ALL_CASES =
      By.xpath(
          "//body/div[2]/div[2]/div/div/div[3]/div[@role='radiogroup']//label[.='Show all cases']");
  public static final By DASHBOARD_LAYERS_SHOW_CONFIRMED_CASES_ONLY =
      By.xpath(
          "//body/div[2]/div[2]/div/div/div[3]/div[@role='radiogroup']//label[.='Show confirmed cases only']");
  public static final By DASHBOARD_LAYERS_SHOW_CONTACTS =
      By.xpath("//span[@id='dashboardShowContacts']/label[.='Show contacts']");
  public static final By DASHBOARD_LAYERS_SHOW_EVENTS =
      By.xpath("//span[@id='dashboardShowEvents']/label[.='Show events']");
  public static final By DASHBOARD_LAYERS_SHOW_REGIONS =
      By.xpath("//span[@id='dashboardShowRegions']/label[.='Show regions']");
  public static final By DASHBOARD_LAYERS_HIDE_OTHER_COUNTRIES =
      By.xpath("//span[@id='dashboardHideOtherCountries']/label[.='Hide other countries']");
  public static final By DASHBOARD_LAYERS_SHOW_EPIDEMIOLOGICAL_SITUATION =
      By.xpath(
          "//span[@id='dashboardMapShowEpiSituation']/label[.='Show epidemiological situation']");
  public static final By DISEASES_LAYOUT = By.cssSelector("[location='burden'] .v-csslayout");
  public static final By CURVE_AND_MAP_LAYOUT =
      By.cssSelector(".v-slot.v-slot-curve-and-map-layout");
  public static final By COLLAPSE_EPI_CURVE = By.cssSelector("#collapseEpiCurve");
  public static final By HIDE_OVERVIEW = By.cssSelector("#hideOverview");
  public static final By EXPAND_EPI_CURVE = By.cssSelector("#expandEpiCurve");
  public static final By DASHBOARD_TODAY = By.cssSelector("#dashboardToday");
  public static final By DASHBOARD_DAY_BEFORE = By.cssSelector("#dashboardDayBefore");
  public static final By DASHBOARD_THIS_WEEK = By.cssSelector("#dashboardThisWeek");
  public static final By DASHBOARD_LAST_WEEK = By.cssSelector("#dashboardLastWeek");
  public static final By REGION_COMBOBOX_DROPDOWN =
      By.cssSelector("[location='regionFilter'] > div > div");
  public static final By DATE_TYPE_COMBOBOX_DROPDOWN = By.cssSelector("#dateType div");
}
