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
  public static final By CURRENT_PERIOD = By.id("currentPeriod");
  public static final By COMPARISON_PERIOD = By.id("comparisonPeriod");
  public static final By DATE_TYPE = By.id("dateType");
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
  public static final By SHOW_ALL_DISEASES = By.cssSelector("div#dashboardShowAllDiseases");
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
  public static final By DIS = By.xpath("");
}
