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

public class EditEventPage {
  public static final By EVENT_PARTICIPANTS_TAB =
      By.cssSelector("#tab-events-eventparticipants span");
  public static final By UUID_INPUT = By.id("uuid");
  public static final By TITLE_INPUT = By.cssSelector("#eventTitle");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By EVENT_STATUS_OPTIONS =
      By.cssSelector("#eventStatus .v-select-option label");
  public static final By SELECTED_EVENT_STATUS = By.cssSelector("#eventStatus input:checked");
  public static final By RISK_LEVEL_INPUT = By.cssSelector(" #riskLevel input");
  public static final By SELECTED_EVENT_MANAGEMENT_STATUS =
      By.cssSelector("#eventManagementStatus input:checked");
  public static final By START_DATA_INPUT = By.cssSelector(" #startDate input");
  public static final By SELECTED_EVENT_INVESTIGATION_STATUS =
      By.cssSelector("#eventInvestigationStatus input:checked");
  public static final By DISEASE_INPUT = By.cssSelector("#disease input");
  public static final By SOURCE_TYPE_INPUT = By.cssSelector(" #srcType input");
  public static final By TYPE_OF_PLACE_INPUT = By.cssSelector(" #typeOfPlace input");
  public static final By REPORT_DATE_INPUT = By.cssSelector(" #reportDateTime input");
  public static final By EVENT_DATA_SAVED_MESSAGE =
      By.xpath("//*[contains(text(),'Event data saved')]");
}
