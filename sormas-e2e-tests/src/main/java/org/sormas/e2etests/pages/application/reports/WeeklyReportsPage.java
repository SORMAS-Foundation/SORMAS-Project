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

package org.sormas.e2etests.pages.application.reports;

import org.openqa.selenium.By;

public class WeeklyReportsPage {

  public static final By YEAR_FILTER = By.cssSelector("#year > div");
  public static final By EPI_WEEK_FILTER = By.cssSelector("#epiWeek > div");
  public static final By LAST_EPI_WEEK_BUTTON = By.id("dashboardLastWeek");
  public static final By INFO_ICON = By.cssSelector(".v-icon-info_circle");
  public static final By GRID = By.cssSelector(".v-grid-tablewrapper");
  public static final String GRID_HEADER =
      "//thead[@class='v-grid-header']//div[contains(text(), '%s')]";
}
