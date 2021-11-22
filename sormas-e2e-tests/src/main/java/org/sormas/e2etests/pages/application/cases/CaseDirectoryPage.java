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
  public static final By TOTAL_CASES_COUNTER = By.cssSelector(".badge");
  public static final By CASE_DIRECTORY_DETAILED_RADIOBUTTON =
      By.cssSelector("#casesViewSwitcher span:nth-child(2)");
  public static final By CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT =
      By.cssSelector("input#caseLike");
  public static final By CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON =
      By.cssSelector("div#actionApplyFilters");
  public static final By CASE_DETAILED_TABLE_DATA = By.tagName("td");
  public static final By CASE_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By CASE_DETAILED_TABLE_ROWS =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By FIRST_CASE_ID_BUTTON = By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By NAME_UUID_EPID_NUMBER_LIKE_INPUT = By.cssSelector("input#caseLike");
  public static final By PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT =
      By.cssSelector("#personLike");
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
  public static final By GRID_HEADERS = By.xpath("//thead//tr//th");
  public static final By GRID_RESULTS_DISEASE =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(5)");
  public static final By GRID_RESULTS_FIRST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(11)");
  public static final By GRID_RESULTS_LAST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(12)");
  public static final By GRID_RESULTS_DISTRICT =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(13)");
  public static final By GRID_RESULTS_HEALTH_FACILITY =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(14)");
  public static final By CASE_CLASSIFICATION_COLUMNS =
      By.cssSelector("[role=rowgroup] tr>td:nth-child(7)");
  public static final String RESULTS_GRID_HEADER = "//div[contains(text(), '%s')]";
  // TODO refactor the other headers based on the last one added
}
