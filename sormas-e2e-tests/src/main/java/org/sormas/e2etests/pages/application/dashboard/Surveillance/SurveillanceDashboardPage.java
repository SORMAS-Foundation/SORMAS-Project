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
}
