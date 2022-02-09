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
  public static final By CREATE_NEW_PERSON_RADIO_BUTTON =
      By.xpath("//label[contains(text(),'Create a new person')]");
  public static final By EVENT_PARTICIPANTS_TAB = By.id("tab-events-eventparticipants");
  public static final By PICK_OR_CREATE_POPUP_SAVE_BUTTON = By.cssSelector("#commit");
  public static final By ERROR_MESSAGE_TEXT = By.cssSelector("p.v-Notification-description");
  public static final By DISCARD_BUTTON = By.id("discard");
}
