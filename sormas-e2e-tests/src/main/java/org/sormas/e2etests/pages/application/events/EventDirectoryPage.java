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

  /*public static By getByEventUuid(String eventUuid) {
    return By.cssSelector("a[title=" + eventUuid + "]");
  }*/
  public static final By FIRST_EVENT_ID_BUTTON = By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By CREATE_CASE_BUTTON =
      By.xpath("//td//span[contains(@class, 'v-icon-edit')]");

  public static By getByEventUuid(String eventUuid) {
    return By.xpath(String.format("//a[@title='%s']", eventUuid));
  }
}