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
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By EVENT_ACTIONS_TABLE_ROW =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By EVENT_ACTIONS_TABLE_DATA = By.tagName("td");
  public static final By BACK_TO_PARTICIPANT_LIST = By.id("tab-events-eventparticipants");
  public static By getByEventUuid(String eventUuid) {
    return By.cssSelector(String.format("a[title=%s]", eventUuid));
  }
}
