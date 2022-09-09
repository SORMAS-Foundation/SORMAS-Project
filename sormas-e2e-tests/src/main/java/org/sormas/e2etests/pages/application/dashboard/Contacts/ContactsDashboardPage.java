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

package org.sormas.e2etests.pages.application.dashboard.Contacts;

import org.openqa.selenium.By;

public class ContactsDashboardPage {

  public static final By CONTACTS_DASHBOARD_NAME =
      By.xpath("//div[@class='v-slot v-slot-h1 v-slot-vspace-none']/div");
  public static final By CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD =
      By.xpath("//div[contains(text(),'Confirmed contact')]/parent::div/parent::div");
  public static final By CONFIRMED_COUNTER_ON_CONTACTS_DASHBOARD =
      By.xpath("//div[contains(text(),'Confirmed contact')]/parent::div/parent::div/div[1]/div");
  public static final By CONFIRMED_COUNTER_LABEL_ON_CONTACTS_DASHBOARD_DE =
      By.xpath("//div[contains(text(),'Best\u00E4tigter Kontakt')]/parent::div/parent::div");
  public static final By CONFIRMED_COUNTER_ON_CONTACTS_DASHBOARD_DE =
      By.xpath(
          "//div[contains(text(),'Best\u00E4tigter Kontakt')]/parent::div/parent::div/div[1]/div");
  public static final By UNDER_FU_CHART_ON_CONTACTS_DASHBOARD =
      By.cssSelector(
          "svg > g.highcharts-legend > g > g > g.highcharts-legend-item.highcharts-column-series.highcharts-color-undefined.highcharts-series-0 > text > tspan");
}
