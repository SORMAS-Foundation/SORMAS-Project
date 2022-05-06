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

package org.sormas.e2etests.pages.application.entries;

import org.openqa.selenium.By;

public class CreateNewTravelEntryPage {

  public static final By FIRST_NAME_OF_CONTACT_PERSON_INPUT =
      By.cssSelector(".v-window [location='firstName'] input");
  public static final By LAST_NAME_OF_CONTACT_PERSON_INPUT =
      By.cssSelector(".v-window [location='lastName'] input");
  public static final By SEX_COMBOBOX =
      By.cssSelector(".v-window [location='sex'] [role='combobox'] div");
  public static final By RESPONSIBLE_REGION_COMBOBOX =
      By.cssSelector(".v-window #responsibleRegion div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX =
      By.cssSelector(".v-window #responsibleDistrict div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX =
      By.cssSelector(".v-window #responsibleCommunity div");
  public static final By DISEASE_COMBOBOX = By.cssSelector(" #disease div");
  public static final By POINT_OF_ENTRY_COMBOBOX = By.cssSelector(".v-window #pointOfEntry div");
  public static final By POINT_OF_ENTRY_DETAILS_INPUT =
      By.cssSelector(".v-window #pointOfEntryDetails");
  public static final By SAVE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By PICK_OR_CREATE_PERSON_TITLE_DE =
      By.xpath("//div[@class='v-window-header']");
  public static final By SAVE_POPUP_CONTENT = By.cssSelector(".popupContent #commit");
  public static final By PICK_A_EXISTING_PERSON_LABEL_DE =
      By.xpath("//*[text()='W\u00E4hlen Sie eine passende Person']");
  public static final By PICK_A_EXISTING_CASE_LABEL_DE =
      By.xpath("//*[text()='Einen vorhandenen Fall w\u00E4hlen']");
  public static final By ARRIVAL_DATE = By.cssSelector("#dateOfArrival input");
  public static final By FIRST_TRAVEL_ENTRY_ID_BUTTON =
      By.cssSelector(".v-grid-row-has-data a[title]");
}
