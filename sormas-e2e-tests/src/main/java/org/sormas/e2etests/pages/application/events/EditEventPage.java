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

public class EditEventPage {
  public static final By EVENT_PARTICIPANTS_TAB =
      By.cssSelector("#tab-events-eventparticipants span");
  public static final By FIRST_EVENT_PARTICIPANT = By.xpath("//table/tbody/tr[1]/td[1]//a");
  public static final By EVENT_ACTIONS_TAB = By.cssSelector("#tab-events-eventactions span");
  public static final By UUID_INPUT = By.id("uuid");
  public static final By TITLE_INPUT = By.cssSelector("#eventTitle");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By SAVE_BUTTON_FOR_POPUP_WINDOWS = By.cssSelector(".popupContent #commit");
  public static final By EVENT_STATUS_OPTIONS =
      By.cssSelector("#eventStatus .v-select-option label");
  public static final By RISK_LEVEL_INPUT = By.cssSelector(" #riskLevel input");
  public static final By START_DATA_INPUT = By.cssSelector(" #startDate input");
  public static final By DISEASE_INPUT = By.cssSelector("#disease input");
  public static final By SOURCE_TYPE_INPUT = By.cssSelector(" #srcType input");
  public static final By TYPE_OF_PLACE_INPUT = By.cssSelector("#typeOfPlace input");
  public static final By REPORT_DATE_INPUT = By.cssSelector("#reportDateTime input");
  public static final By EVENT_DATA_SAVED_MESSAGE = By.cssSelector(".v-Notification-caption");
  public static final By RISK_LEVEL_COMBOBOX = By.cssSelector("#riskLevel div");
  public static final By EVENT_MANAGEMENT_STATUS_OPTIONS =
      By.cssSelector("#eventManagementStatus .v-select-option label");
  public static final By EVENT_INVESTIGATION_STATUS_OPTIONS =
      By.cssSelector("#eventInvestigationStatus label");
  public static final By DISEASE_COMBOBOX = By.cssSelector("#disease div");
  public static final By SOURCE_TYPE_COMBOBOX = By.cssSelector("#srcType div");
  public static final By TYPE_OF_PLACE_COMBOBOX = By.cssSelector(" #typeOfPlace div");
  public static final By NEW_ACTION_BUTTON = By.id("actionNewAction");
  public static final By NEW_TASK_BUTTON = By.id("taskNewTask");
  public static final By EDIT_FIRST_TASK = By.cssSelector("#edit-task-0");
  public static final By LINK_EVENT_GROUP_BUTTON = By.cssSelector("div#Link\\ event\\ group");
  public static final By NEW_EVENT_GROUP_RADIOBUTTON =
      By.xpath("//*[contains(text(),'New event group')]/..");
  public static final By SELECT_EVENT_GROUP_RADIOBUTTON =
      By.xpath("//*[contains(text(),'Select event group')]/..");
  public static final By GROUP_EVENT_NAME_POPUP_INPUT = By.cssSelector(".popupContent #name");
  public static final By GROUP_EVENT_UUID =
      By.xpath("//*[contains(text(),'Group id')]/../following-sibling::input[1]");
  public static final By NEW_GROUP_EVENT_CREATED_MESSAGE =
      By.xpath("//*[contains(text(),'New event group created')]");
  public static final By CREATE_DOCUMENT_BUTTON = By.cssSelector("[id='Create']");
  public static final By EVENT_HANDOUT_COMBOBOX =
      By.cssSelector(".popupContent div[role='combobox'] div");
  public static final By EVENT_SAVED_POPUP = By.cssSelector(".v-Notification-caption");
  public static final By CREATE_EVENT_HANDOUT_BUTTON =
      By.cssSelector(".popupContent [id='Create']");
  public static final By CANCEL_EVENT_HANDOUT_BUTTON =
      By.cssSelector(".popupContent [id='Cancel']");
  public static final By UNLINK_EVENT_BUTTON = By.id("unlink-event-1");
  public static final By EDIT_EVENT_GROUP_BUTTON = By.id("add-event-0");
  public static final By NAVIGATE_TO_EVENT_DIRECTORY_EVENT_GROUP_BUTTON = By.id("list-events-0");
  public static final By SAVE_BUTTON_FOR_EDIT_EVENT_GROUP = By.id("commit");
  public static final By FIRST_GROUP_ID = By.xpath("//table/tbody/tr[1]/td[2]");
  public static final By TOTAL_ACTIONS_COUNTER = By.cssSelector(".badge");
  public static final By CREATE_CONTACTS_BULK_EDIT_BUTTON = By.id("bulkActions-3");

  public static By getGroupEventName(String groupEventName) {
    return By.xpath("//*[contains(text(),'" + groupEventName + "')]");
  }
}
