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

package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class CaseDirectoryPage {
  public static final By NEW_CASE_BUTTON = By.cssSelector("div#caseNewCase");
  public static final By FIRST_CASE_ID_BUTTON = By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By NAME_UUID_EPID_NUMBER_LIKE_INPUT =
      By.cssSelector("input#nameUuidEpidNumberLike");
  public static final By CASE_OUTCOME_FILTER_COMBOBOX =
      By.cssSelector("[id='outcome'] [class='v-filterselect-button']");
  public static final By CASE_CLASSIFICATION_FILTER_COMBOBOX =
      By.cssSelector("[id='caseClassification'] [class='v-filterselect-button']");
  public static final By CASE_DISEASE_FILTER_COMBOBOX =
      By.cssSelector("[id='disease'] [class='v-filterselect-button']");
  public static final By CASE_APPLY_FILTERS_BUTTON = By.cssSelector("[id='actionApplyFilters']");
  public static final By CASE_RESET_FILTERS_BUTTON = By.cssSelector("[id='actionResetFilters']");
  public static final String CASE_RESULTS_UUID_LOCATOR = "[title = '%s']";
  public static final By CASE_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By LINE_LISTING_BUTTON = By.id("lineListing");
  public static final By GRID_RESULTS_DISEASE =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(5)");
  public static final By GRID_RESULTS_FIRST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(10)");
  public static final By GRID_RESULTS_LAST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(11)");
  public static final By GRID_RESULTS_DISTRICT =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(12)");
  public static final By GRID_RESULTS_HEALTH_FACILITY =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(13)");
  public static final By GRID_RESULTS_DATE_OF_REPORT =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(15)");
  public static final By CASE_CLASSIFICATION_COLUMNS =
      By.cssSelector("[role=rowgroup] tr>td:nth-child(7)");
}
