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
package org.sormas.e2etests.pages.application.configuration;

import org.openqa.selenium.By;

public class SubcontinentsTabPage {
  public static final By NUMBER_OF_SUBCONTINENTS =
      By.xpath(
          "//div[@class='v-label v-widget bold v-label-bold vspace-top-none v-label-vspace-top-none align-right v-label-align-right v-label-undef-w']");
  public static final By SUBCONTINENTS_TABLE_DATA = By.tagName("td");
  public static final By SUBCONTINENTS_TABLE_ROW =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By SUBCONTINENTS_COLUMN_HEADERS =
      By.cssSelector("thead" + " .v-grid-column-default-header-content");
  public static final By IMPORT_BUTTON_SUBCONTINENTS_CONFIGURATION = By.id("actionImport");
  public static final By IMPORT_DEFAULT_BUTTON_SUBCONTINENTS_CONFIGURATION =
      By.id("actionImportAllSubcontinents");
  public static final By EXPORT_BUTTON_SUBCONTINENTS_CONFIGURATION = By.id("export");
  public static final By NEW_ENTRY_BUTTON_SUBCONTINENTS_CONFIGURATION = By.id("actionNewEntry");
  public static final By ENTER_BULK_EDIT_MODE_BUTTON_SUBCONTINENTS_CONFIGURATION =
      By.id("actionEnterBulkEditMode");
  public static final By SEARCH_INPUT_SUBCONTINENTS_CONFIGURATION = By.id("search");
  public static final By CONTINENT_NAME_COMBO_BOX_SUBCONTINENTS_CONFIGURATION = By.id("continent");
  public static final By RESET_FILTERS_BUTTON_SUBCONTINENTS_CONFIGURATION =
      By.id("actionResetFilters");
  public static final By RELEVANCE_STATUS_COMBO_BOX_SUBCONTINENTS_CONFIGURATION =
      By.id("relevanceStatus");
}
