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

package org.sormas.e2etests.pages.application.events;

import org.openqa.selenium.By;

public class EventDirectoryPage {
  public static final By NEW_EVENT_BUTTON = By.id("eventNewEvent");
  public static final By SEARCH_EVENT_BY_FREE_TEXT_INPUT = By.id("freeText");
  public static final By RESET_FILTER = By.id("actionResetFilters");
  public static final By APPLY_FILTER = By.id("actionApplyFilters");
  public static final By EVENT_STATUS_FILTER_BUTTONS =
      By.xpath("//*[@id='status-Signal']/span/../../..//span[@class='v-button-wrap']/span/../..");
  public static final By EVENT_ACTIONS_RADIOBUTTON =
      By.cssSelector("div#eventsViewSwitcher span:nth-child(2)");
  public static final By FILTER_BY_GENERAL_INPUT = By.cssSelector("input#freeText");
  public static final By EVENT_ACTIONS_COLUMN_HEADERS =
      By.cssSelector("thead" + " .v-grid-column-default-header-content");
  public static final By EVENT_ACTIONS_TABLE_ROW =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By EVENT_ACTIONS_TABLE_DATA = By.tagName("td");
  public static final By SEARCH_EVENT_BY_FREE_TEXT = By.id("freeText");
  public static final By FILTER_BY_RISK_LEVEL =
      By.cssSelector("[id='riskLevel'] [class='v-filterselect-button']");
  public static final By FILTER_BY_REPORTING_USER =
      By.cssSelector("[id='reportingUserRole'] [class='v-filterselect-button']");
  public static final By EVENT_MANAGEMENT_FILTER =
      By.cssSelector("[id='eventManagementStatus'] [class='v-filterselect-button']");
  public static final By EVENT_INVESTIGATION_STATUS =
      By.cssSelector("[id='eventInvestigationStatus'] [class='v-filterselect-button']");
  public static final By EVENT_DISPLAY_COMBOBOX =
      By.cssSelector("[id='relevanceStatusFilter'] [class='v-filterselect-button']");
  public static final By FILTER_BY_DISEASE =
      By.cssSelector("[id='disease'] [class='v-filterselect-button']");
  public static final By DISTRICT_COMBOBOX =
      By.cssSelector("[id='district'] [class='v-filterselect-button']");
  public static final By EVENT_SHOW_MORE_FILTERS = By.id("showHideMoreFilters");
  public static final By FILTER_BY_SOURCE_TYPE =
      By.cssSelector("[id='srcType'] [class='v-filterselect-button']");
  public static final By FILTER_BY_TYPE_OF_PLACE =
      By.cssSelector("[id='typeOfPlace'] [class='v-filterselect-button']");
  public static final By EVENT_SIGNAL = By.id("status-Signal");
  public static final By EVENT_EVENT = By.id("status-Event");
  public static final By EVENT_SCREENING = By.id("status-Screening");
  public static final By EVENT_CLUSTER = By.id("status-Cluster");
  public static final By EVENT_DROPPED = By.id("status-Dropped");
  public static final By CREATED_PARTICIPANT = By.cssSelector("[role='gridcell'] a");
  public static final By DATE_TYPE_COMBOBOX =
      By.cssSelector("[id='dateType'] [class='v-filterselect-button']");
  public static final By EVENTS_COLUMN_HEADERS =
      By.cssSelector("thead" + " .v-grid-column-default-header-content");
  public static final By EVENTS_TABLE_ROW = By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By EVENTS_TABLE_DATA = By.tagName("td");
  public static final By EVENT_PARTICIPANT_INPUT = By.id("freeTextEventParticipants");
  public static final By EVENT_GROUP_INPUT = By.id("freeTextEventGroups");
  public static final By EVENT_GROUP_ID_NAME_INPUT = By.id("freeText");
  public static final By EVENT_GROUP_FREE_TEXT_EVENT_INPUT = By.id("freeTextEvent");
  public static final By EVENTS_RADIO_BUTTON = By.cssSelector(".v-radiobutton");
  public static final By LINK_EVENT_BUTTON = By.id("Link event");
  public static final By LINK_EVENT_BUTTON_EDIT_PAGE = By.id("Link event group");
  public static final By UNLINK_EVENT_BUTTON = By.id("unlink-event-0");
  public static final By ID_FIELD_FILTER = By.id("search");
  public static final By LINKED_EVENT_GROUP_ID =
      By.xpath("//div[@location = 'event-groups']//div[contains(@class, 'v-slot')]//a");
  public static final By SAVE_BUTTON_IN_LINK_FORM = By.cssSelector(".popupContent #commit");
  public static final By FILTERED_EVENT_LINK_EVENT_FORM = By.xpath("//tr[@role='row']");
  public static final By FIRST_EVENT_GROUP = By.xpath("//tr[@role='row']");
  /*public static By getByEventUuid(String eventUuid) {
    return By.cssSelector("a[title=" + eventUuid + "]");
  }*/

  public static final By EVENT_GROUP_ID_IN_GRID = By.xpath("//tr[@role='row']//td[15]/a");
  public static final By FIRST_EVENT_ID_BUTTON = By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By CREATE_CASE_BUTTON =
      By.xpath("//td//span[contains(@class, 'v-icon-edit')]");
  public static final By TOTAL_EVENTS_COUNTER = By.cssSelector(".badge");
  public static final By MORE_BUTTON_EVENT_DIRECTORY = By.id("more");
  public static final By ENTER_BULK_EDIT_MODE_EVENT_DIRECTORY = By.id("actionEnterBulkEditMode");
  public static final By FIRST_CHECKBOX_EVENT_DIRECTORY =
      By.xpath("//th[@role='columnheader']//input[@type='checkbox']/../..");
  public static final By BULK_ACTIONS_EVENT_DIRECTORY = By.id("bulkActions-2");
  public static final By GROUP_EVENTS_EVENT_DIRECTORY = By.id("bulkActions-7");
  public static final By BULK_EDIT_EVENT_DIRECTORY = By.id("bulkActions-3");
  public static final By CHANGE_EVENT_MANAGEMENT_STATUS_CHECKBOX =
      By.xpath("//label[text()='Change event management status']");
  public static final By EVENT_MANAGEMENT_STATUS_COMBOBOX =
      By.cssSelector("#eventManagementStatus .v-select-option");
  public static final By GROUP_ID_COLUMN = By.xpath("(//td//a)[2]");
  public static final By EXPORT_PARTICIPANT_BUTTON = By.id("export");
  public static final By BASIC_EXPORT_PARTICIPANT_BUTTON = By.id("exportBasic");
  public static final By CLOSE_POPUP_BUTTON = By.cssSelector(".v-window-closebox");
  public static final By IMPORT_BUTTON = By.cssSelector("div#actionImport");
  public static final By IMPORT_POPUP_BUTTON = By.cssSelector("[class='v-button']");
  public static final By IMPORT_SUCCESS = By.xpath("//*[text()='Import successful!']");
  public static final By IMPORT_POPUP_CLOSE_BUTTON = By.cssSelector(".popupContent #actionCancel");
  public static final By IMPORT_WINDOW_CLOSE_BUTTON = By.cssSelector("[class='v-window-closebox']");
  public static final By EVENT_REGION_COMBOBOX_INPUT = By.cssSelector("#region div");
  public static final By EVENT_DISTRICT_COMBOBOX_INPUT = By.cssSelector("#district div");
  public static final By EVENT_COMMUNITY_COMBOBOX_INPUT = By.cssSelector("#community div");
  public static final By EVENT_STATUS_FILTER_COMBOBOX =
      By.cssSelector("#relevanceStatusFilter div");
  public static final By EVENT_GROUP_ID_SORT = By.xpath("//div[text()='Group id']");
  public static final By EVENT_GROUP_NAME_SORT = By.xpath("//div[text()='Group name']");
  public static final By EVENT_EXPORT_BUTTON = By.id("export");
  public static final By EVENT_EXPORT_BASIC_BUTTON = By.id("exportBasic");
  public static final By DETAILED_EXPORT_PARTICIPANT_BUTTON = By.id("exportDetailed");
  public static final By IMPORT_PARTICIPANT_BUTTON = By.id("actionImport");
  public static final By COMMIT_BUTTON = By.id("commit");
  public static final By CUSTOM_EXPORT_PARTICIPANT_BUTTON = By.id("exportCustom");
  public static final By DETAILED_EVENT_EXPORT_BUTTON = By.id("exportDetailed");
  public static final By BASIC_EVENT_EXPORT_BUTTON = By.id("exportBasic");
  public static final By CONFIRM_POPUP_BUTTON = By.cssSelector(".popupContent #actionConfirm");
  public static final By RESPONSIBLE_USER_INFO_ICON =
      By.xpath("//span[@class='v-icon v-icon-info_circle']");
  public static final By RESPONSIBLE_USER_INFO_POPUP_TEXT =
      By.xpath("//div[@class='v-tooltip-text']");

  public static By getCheckboxByIndex(String idx) {
    return By.xpath(String.format("(//input[@type=\"checkbox\"])[%s]", idx));
  }

  public static By getCheckboxByUUID(String uuid) {
    return By.xpath(
        String.format(
            "//td//a[text()=\"%s\"]/../preceding-sibling::td//input[@type=\"checkbox\"]",
            uuid.substring(0, 6).toUpperCase()));
  }

  public static By getByEventUuid(String eventUuid) {
    return By.xpath(String.format("//a[@title='%s']", eventUuid));
  }
}
