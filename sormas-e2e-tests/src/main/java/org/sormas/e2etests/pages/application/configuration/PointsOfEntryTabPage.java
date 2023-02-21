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

public class PointsOfEntryTabPage {
  public static final By POE_IMPORT_BUTTON = By.id("actionImport");
  public static final By POE_EXPORT_BUTTON = By.id("export");
  public static final By POE_NEW_ENTRY_BUTTON = By.id("export");
  public static final By POE_ENTER_BULK_EDIT_MODE_BUTTON = By.id("actionEnterBulkEditMode");
  public static final By POE_SEARCH_INPUT = By.id("search");
  public static final By POE_COUNTRY_FILTER_COMBOBOX =
      By.cssSelector("[id='country'] [class='v-filterselect-button']");
  public static final By POE_REGION_FILTER_COMBOBOX =
      By.cssSelector("[id='region'] [class='v-filterselect-button']");
  public static final By POE_DISTRICT_FILTER_COMBOBOX =
      By.cssSelector("[id='district'] [class='v-filterselect-button']");
  public static final By POINT_OF_ENTRY_TYPE_FILTER_COMBOBOX =
      By.cssSelector("[id='pointOfEntryType'] [class='v-filterselect-button']");
  public static final By POE_ACTIVE_FILTER_COMBOBOX =
      By.cssSelector("[id='active'] [class='v-filterselect-button']");
  public static final By POE_RESET_FILTERS_BUTTON = By.id("actionResetFilters");
  public static final By POE_RELEVANCE_STATUS_COMBOBOX =
      By.cssSelector("[id='relevanceStatus'] [class='v-filterselect-button']");
}
