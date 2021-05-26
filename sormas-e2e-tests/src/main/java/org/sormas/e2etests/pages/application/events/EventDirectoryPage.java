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
  public static final By SEARCH_SPECIFIC_EVENT_BUTTON = By.id("eventSearchSpecificEvent");
  public static final By SORT_BY_ALL = By.id("all");
  public static final By SORT_BY_SIGNAL = By.id("status-Signal");
  public static final By SORT_BY_EVENT = By.id("status-Event");
  public static final By SORT_BY_SCREENING = By.id("status-Screening");
  public static final By SORT_BY_CLUSTER = By.id("status-Cluster");
  public static final By SORT_BY_DROPPED = By.id("status-Dropped");
  public static final By SEARCH_EVENT_BY_FREE_TEXT = By.id("freeText");
  public static final By RESET_FILTER = By.id("actionResetFilters");
  public static final By APPLY_FILTER = By.id("actionApplyFilters");

  public static By getByEventUuid(String eventUuid) {
    return By.cssSelector("a[title='" + eventUuid + "']");
  }

  public static By getByEventStatus(String eventStatus) {
    switch (eventStatus) {
      case "SIGNAL":
        return SORT_BY_SIGNAL;
      case "EVENT":
        return SORT_BY_EVENT;
      case "SCREENING":
        return SORT_BY_SCREENING;
      case "CLUSTER":
        return SORT_BY_CLUSTER;
      case "DROPPED":
        return SORT_BY_DROPPED;
      default:
        return SORT_BY_ALL;
    }
  }
}
