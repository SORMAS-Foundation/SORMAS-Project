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

package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class ContactDirectoryPage {
  public static final By NEW_CONTACT_BUTTON = By.id("contactNewContact");
  public static final By LINE_LISTING = By.cssSelector("[id='lineListing']");
  public static final By MULTIPLE_OPTIONS_SEARCH_INPUT = By.cssSelector("#nameUuidCaseLike");
  public static final By APPLY_FILTERS_BUTTON = By.id("actionApplyFilters");
  public static final String CONTACT_RESULTS_UUID_LOCATOR = "[title = '%s']";
  public static final By CONTACT_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By CONTACT_DIRECTORY_DETAILED_RADIOBUTTON =
      By.cssSelector("div#contactsViewSwitcher span:nth-child(2) > label");
  public static final By CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT =
      By.cssSelector("input#nameUuidCaseLike");

  public static final By CONTACTS_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By CONTACTS_DETAILED_FIRST_TABLE_ROW =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By CONTACTS_DETAILED_TABLE_DATA = By.tagName("td");
  public static final By DISEASE_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(5)");
  public static final By FIRST_NAME_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(8)");
  public static final By LAST_NAME_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(9)");
  public static final By TYPE_OF_CONTACT_COLUMNS =
      By.cssSelector("[role=rowgroup] tr>td:nth-child(11)");
}
