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

package org.sormas.e2etests.pages.application.tasks;

import org.openqa.selenium.By;

public class TaskManagementPage {
  public static final By NEW_TASK_BUTTON = By.cssSelector("div#taskNewTask");
  public static final By GENERAL_SEARCH_INPUT = By.cssSelector("input#freeText");
  public static final String EDIT_BUTTON_XPATH_BY_TEXT =
      "//td[contains(text(),'%s')]/../td/span[contains(@class, 'v-icon-edit')]";
  public static final By COLUMN_HEADERS_TEXT =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By TABLE_ROW = By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By TABLE_DATA = By.tagName("td");
  public static final By TASK_CONTEXT_COMBOBOX = By.cssSelector("#taskContext div");
  public static final By TASK_STATUS_COMBOBOX = By.cssSelector("#taskStatus div");
  public static final By SHOW_MORE_FILTERS = By.cssSelector("#showHideMoreFilters");
  public static final By ASSIGNED_USER_FILTER_INPUT = By.cssSelector("#assigneeUserLike");
  public static final By APPLY_FILTERS_BUTTON = By.cssSelector("#actionApplyFilters");
  public static final By BULK_EDIT_BUTTON = By.id("actionEnterBulkEditMode");

  public static By getCheckboxByIndex(String idx) {
    return By.xpath(String.format("//input[@type=\"checkbox\"])[%s]", idx));
  }
}
