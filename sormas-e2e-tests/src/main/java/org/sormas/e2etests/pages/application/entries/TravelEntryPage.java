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

public class TravelEntryPage {
  public static final By IMPORT_BUTTON = By.cssSelector("div#Import");
  public static final By START_DATA_IMPORT_BUTTON =
      By.xpath("/html/body/div[2]/div[3]/div/div/div[3]/div/div/div[5]/div/form/div/div");
  public static final By COMMIT_BUTTON = By.cssSelector("div#commit");
  public static final By NEW_PERSON_RADIOBUTTON_DE =
      By.xpath("//*[text()='Eine neue Person anlegen']");
  public static final By IMPORT_SUCCESS_DE = By.xpath("//*[text()='Import erfolgreich!']");
  public static final By NEW_TRAVEL_ENTRY_BUTTON = By.cssSelector("div#travelEntryNewTravelEntry");
}
