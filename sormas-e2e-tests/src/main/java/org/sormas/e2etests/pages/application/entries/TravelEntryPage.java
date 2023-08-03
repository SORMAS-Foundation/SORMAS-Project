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
  public static final By SELECT_ANOTHER_PERSON_DE =
      By.xpath("//*[text()='Eine andere Person w\u00E4hlen']");
  public static final By IMPORT_SUCCESS_DE = By.xpath("//*[text()='Import erfolgreich!']");
  public static final By NEW_TRAVEL_ENTRY_BUTTON = By.cssSelector("div#travelEntryNewTravelEntry");
  public static final By EPI_DATA_CASE_NEW_TRAVEL_ENTRY_DE_BUTTON = By.id("Neue Einreise");
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

  public static final By CLOSE_DATA_IMPORT_POPUP_BUTTON = By.id("actionClose");
  public static final By CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON =
      By.xpath("//div[@class='v-window-closebox']");
  public static final By FIRST_NAME_IMPORTED_PERSON =
      By.xpath("//span[text()='Vorname']/../following-sibling::div");
  public static final By LAST_NAME_IMPORTED_PERSON =
      By.xpath("//span[text()='Nachname']/../following-sibling::div");
  public static final By FIRST_RESULT_ID = By.xpath("//table/tbody/tr[2]/td[1]");
  public static final By TRAVEL_ENTRY_DATA_FILTER_OPTION_COMBOBOX =
      By.cssSelector("[id='dateFilterOption'] [class='v-filterselect-button']");
  public static final By WEEK_FROM_OPTION_COMBOBOX =
      By.cssSelector("[id='weekFrom'] [class='v-filterselect-button']");
  public static final By WEEK_TO_OPTION_COMBOBOX =
      By.cssSelector("[id='weekTo'] [class='v-filterselect-button']");
  public static final By DELETE_TRAVEL_ENTRY_POPUP =
      By.xpath("//div[@class='popupContent']//div[@class='v-filterselect-button']");
  public static final By ENTRY_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By ENTRY_IMPORT_TEMPLATE_LABEL =
      By.xpath("//div[contains(text(),'Importvorlage herunterladen')]");
  public static final By PICK_OR_CREATE_PERSON_HEADER_DE =
      By.xpath("//*[contains(text(),'Person ausw\u00E4hlen oder erstellen')]");
  public static final By TRAVEL_ENTRIES_IMPORT_SUCCESSFUL_HEADER_DE =
      By.xpath("//div/b[text()='Import erfolgreich!']");
  public static final By CLOSE_IMPORT_TRAVEL_ENTRY_POPUP =
      By.cssSelector(".popupContent .v-window-closebox");
  public static final By TRAVEL_ENTRY_DELETE_POPUP = By.cssSelector(".v-Notification-caption");
}
