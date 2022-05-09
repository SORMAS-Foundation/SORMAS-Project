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
  public static final By PERSON_FILTER_INPUT = By.id("nameUuidExternalIDLike");
  public static final By RECOVERED_ENTRIES = By.id("onlyRecoveredEntries");
  public static final By VACCINATED_ENTRIES = By.id("onlyVaccinatedEntries");
  public static final By NEGATIVE_TESTES_ENTRIES = By.id("onlyEntriesTestedNegative");
  public static final By CONVERTE_TO_CASE_ENTRIES = By.id("onlyEntriesConvertedToCase");
  public static final By TRAVEL_ENTRY_DIRECTORY_PAGE_APPLY_FILTER_BUTTON =
      By.id("actionApplyFilters");
  public static final By TRAVEL_ENTRY_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By TRAVEL_ENTRY_DIRECTORY_PAGE_SHOW_MORE_FILTERS_BUTTON =
      By.id("showHideMoreFilters");
  public static final By TRAVEL_ENTRY_AGGREGATION_COMBOBOX =
      By.cssSelector("[id='relevanceStatus'] [class='v-filterselect-button']");
  public static final By TRAVEL_ENTRY_FIRST_RECORD_IN_TABLE = By.cssSelector("[role='gridcell'] a");
  public static final By DELETE_BULK = By.id("bulkActions-3");

  public static By getCheckboxByIndex(String idx) {
    return By.xpath(String.format("(//td//input[@type=\"checkbox\"])[%s]", idx));
  }
}
