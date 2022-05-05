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

public class EventParticipantsPage {
  public static final By ADD_PARTICIPANT_BUTTON = By.cssSelector("div#eventParticipantAddPerson");
  public static final By PARTICIPANT_FIRST_NAME_INPUT = By.cssSelector(".popupContent #firstName");
  public static final By PARTICIPANT_LAST_NAME_INPUT = By.cssSelector(".popupContent #lastName");
  public static final By PARTICIPANT_REGION_COMBOBOX = By.cssSelector(".popupContent #region div");
  public static final By PARTICIPANT_DISTRICT_COMBOBOX =
      By.cssSelector(".popupContent #district div");
  public static final By SEX_COMBOBOX =
      By.cssSelector(".v-window [location='sex'] [role='combobox'] div");
  public static final By PICK_OR_CREATE_PERSON_POPUP =
      By.xpath("//*[contains(text(),'Pick or create person')]");
  public static final By PICK_OR_CREATE_CONTACT_POPUP =
      By.xpath("//*[contains(text(),'Pick or create contact')]");
  public static final By CREATE_NEW_PERSON_RADIO_BUTTON =
      By.xpath("//label[contains(text(),'Create a new person')]");
  public static final By EVENT_PARTICIPANTS_TAB = By.id("tab-events-eventparticipants");
  public static final By PICK_OR_CREATE_POPUP_SAVE_BUTTON = By.cssSelector("#commit");
  public static final By ERROR_MESSAGE_TEXT = By.cssSelector("p.v-Notification-description");
  public static final By DISCARD_BUTTON = By.id("discard");
  public static final By APPLY_FILTERS_BUTTON = By.id("actionApplyFilters");
  public static final By EXPORT_EVENT_PARTICIPANT_CONFIGURATION_DATA_REGION_CHECKBOX =
      By.xpath("//label[text()='Region']");
  public static final By SEARCH_FOR_PERSON_BUTTON_IN_ADD_PARTICIPANT_POPUP_WINDOW =
      By.id("personSearchLoc");
  public static final By SELECT_PERSON_ID_INPUT_AT_ADD_PARTICIPANT =
      By.cssSelector("input#uuidExternalIdExternalTokenLike");
  public static final By SELECT_PERSON_SEARCH_BUTTON_AT_ADD_PARTICIPANT = By.id("actionSearch");
  public static final By SELECT_FIRST_PERSON_IN_SEARCHED_LIST_FROM_ADD_PARTICIPANT =
      By.cssSelector("[scroll] [aria-live] .v-grid-body tr:nth-of-type(1)");
  public static final By PERSON_ID_IN_EVENT_PARTICIPANTS_COLUMN_HEADER =
      By.cssSelector(
          "th:nth-of-type(2) > .v-grid-column-default-header-content.v-grid-column-header-content");
  public static final By CASE_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By PERSON_ID_IN_EVENT_PARTICIPANTS_RESULTS_ROWS =
      By.cssSelector("tr[role='row'] > td:nth-of-type(2) a");
  public static final By CONFIRM_BUTTON_FOR_SELECT_PERSON_FROM_ADD_PARTICIPANTS_WINDOW =
      By.cssSelector("[scroll] [role='dialog']:nth-of-type(5) #commit");
  public static final By DELETE_EVENT_PARTICIPANT_BUTTTON = By.cssSelector("div#delete");
  public static final By CONFIRM_DELETION_OF_EVENT_PARTICIPANT = By.id("actionConfirm");
  public static final By POPUP_DISCARD_CHANGES_BUTTON = By.id("actionCancel");

  public static final By getEventsByCaseUuid(String uuid) {
    return By.cssSelector(String.format("[title = '%s']", uuid));
  }
}
