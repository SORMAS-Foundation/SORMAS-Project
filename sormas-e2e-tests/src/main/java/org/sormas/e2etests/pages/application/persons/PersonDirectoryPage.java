/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.pages.application.persons;

import org.openqa.selenium.By;

public class PersonDirectoryPage {

  public static final By MULTIPLE_OPTIONS_SEARCH_INPUT =
      By.cssSelector("#nameAddressPhoneEmailLike");
  public static final By APPLY_FILTERS_BUTTON = By.cssSelector("#actionApplyFilters");
  public static final By RESET_FILTERS_BUTTON = By.cssSelector("[id='actionResetFilters']");
  public static final String PERSON_RESULTS_UUID_LOCATOR = "[title = '%s']";
  public static final By SEARCH_PERSON_BY_FREE_TEXT = By.id("nameAddressPhoneEmailLike");
  public static final By ALL_BUTTON = By.id("All");
  public static final By BIRTH_YEAR_COMBOBOX =
      By.cssSelector("[id='birthdateYYYY'] [class='v-filterselect-button']");
  public static final By BIRTH_MONTH_COMBOBOX =
      By.cssSelector("[id='birthdateMM'] [class='v-filterselect-button']");
  public static final By BIRTH_DAY_COMBOBOX =
      By.cssSelector("[id='birthdateDD'] [class='v-filterselect-button']");
  public static final By PRESENT_CONDITION =
      By.cssSelector("[id='presentCondition'] [class='v-filterselect-button']");
  public static final By REGIONS_COMBOBOX =
      By.cssSelector("[id='region'] [class='v-filterselect-button']");
  public static final By DISTRICTS_COMBOBOX =
      By.cssSelector("[id='district'] [class='v-filterselect-button']");
  public static final By COMMUNITY_PERSON_COMBOBOX =
      By.cssSelector("[id='community'] [class='v-filterselect-button']");
  public static final By CASE_PERSON_ID_COLUMN_HEADERS =
      By.cssSelector("v-grid-column-header-content v-grid-column-default-header-content");
}
