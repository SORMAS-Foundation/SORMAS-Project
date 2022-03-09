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

public class EditTravelEntryPage {

  public static final By FIRST_NAME_INPUT = By.cssSelector("[location='firstName'] input");
  public static final By LAST_NAME_INPUT = By.cssSelector("[location='lastName'] input");
  public static final By SEX_COMBOBOX = By.cssSelector("[location='sex'] [role='combobox'] div");
  public static final By RESPONSIBLE_REGION_COMBOBOX = By.cssSelector("#responsibleRegion div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX = By.cssSelector("#responsibleDistrict div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX =
      By.cssSelector("#responsibleCommunity div");
  public static final By DISEASE_COMBOBOX = By.cssSelector("#disease div");
  public static final By POINT_OF_ENTRY_COMBOBOX = By.cssSelector("#pointOfEntry div");
  public static final By POINT_OF_ENTRY_DETAILS_INPUT = By.cssSelector("#pointOfEntryDetails");
  public static final By TRAVEL_ENTRY_PERSON_TAB = By.id("tab-travelEntries-person");
}
