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
  public static final By NEW_TASK_BUTTON = By.cssSelector("[id='taskNewTask']");
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
  public static final By BULK_DELETE_BUTTON = By.id("bulkActions-4");
  public static final By BULK_ARCHIVE_BUTTON = By.id("bulkActions-5");
  public static final By BULK_EDITING_BUTTON = By.id("bulkActions-3");
  public static final By CHANGE_ASSIGNEE_CHECKBOX = By.xpath("//label[text()='Change assignee']");
  public static final By CHANGE_PRIORITY_CHECKBOX = By.xpath("//label[text()='Change priority']");
  public static final By CHANGE_STATUS_CHECKBOX = By.xpath("//label[text()='Change task status']");
  public static final By TASK_ASSIGNEE_COMBOBOX = By.cssSelector("#assigneeUser div");
  public static final By TASK_RADIOBUTTON = By.cssSelector(".v-radiobutton");
  public static final By EDIT_TASK_MODAL_FORM = By.xpath("//*[@aria-modal='true']");
  public static final By EDIT_FIRST_SEARCH_RESULT = By.xpath("//table/tbody/tr[1]/td[1]");
  public static final By TASK_CONTEXT_FIRST_RESULT = By.xpath("//td[3]");
  public static final By ASSOCIATED_LINK_FIRST_RESULT = By.xpath("//td/a");
  public static final By TASK_EXPORT_BUTTON = By.id("export");
  public static final By BASIC_EXPORT_BUTTON = By.id("exportBasic");
  public static final By DETAILED_EXPORT_BUTTON = By.id("exportDetailed");
  public static final By CUSTOM_EXPORT_BUTTON = By.id("exportCustom");
  public static final By DETAILED_EXPORT_POPUP_LABEL =
      By.xpath("//div[contains(text(),'Detailed Export')]");
  public static final By NEW_CUSTOM_EXPORT_BUTTON = By.id("exportNewExportConfiguration");
  public static final By SAVE_CUSTOM_EXPORT_BUTTON = By.id("actionSave");
  public static final By CUSTOM_TASK_EXPORT_DOWNLOAD_BUTTON =
      By.xpath("//div[@class='popupContent']//div[contains(@id, '-download')]");
  public static final By CUSTOM_TASK_EXPORT_EDIT_BUTTON =
      By.xpath("//div[@class='popupContent']//div[contains(@id, '-edit')]");
  public static final By CUSTOM_TASK_EXPORT_DELETE_BUTTON =
      By.xpath(
          "//div[@class='popupContent']//div[contains(@id, '-delete') and not(contains(@id, 'null-delete'))]");
  public static final By CUSTOM_EXPORT_CONFIGURATION_NAME_INPUT =
      By.xpath("//div[@id='sormasui-1655777373-overlays']//input[@type='text']");
  public static final By FIRST_GRID_REGION_VALUE = By.xpath("//tbody/tr/td[5]");
  public static final By FIRST_GRID_DISTRICT_VALUE = By.xpath("//tbody/tr/td[6]");

  public static By getCheckboxByIndex(String idx) {
    return By.xpath(String.format("(//input[@type=\"checkbox\"])[%s]", idx));
  }

  public static By getCustomExportCheckboxByText(String text) {
    return By.xpath(
        String.format("//span[@class='v-checkbox v-widget']/label[contains(text(), '%s')]", text));
  }

  public static By getCustomExportByID(String id) {
    return By.xpath(
        String.format("//div[@class='popupContent']//div[contains(@id, '%s-download')]", id));
  }

  public static By getEditTaskButtonByNumber(Integer number) {
    return By.xpath(String.format("//table/tbody/tr[%x]/td[1]", number));
  }

  public static By getLastCreatedEditTaskButton(String text) {
    return By.xpath(
        String.format(
            "//td[contains(text(),'%s')]/../td/span[contains(@class, 'v-icon-edit')]", text));
  }
}
