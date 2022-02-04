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
  public static final By NEW_CONTACT_PAGE_BUTTON = By.id("contactNewContact");
  public static final By LINE_LISTING = By.cssSelector("[id='lineListing']");
  public static final By MULTIPLE_OPTIONS_SEARCH_INPUT = By.cssSelector("#contactOrCaseLike");
  public static final By APPLY_FILTERS_BUTTON = By.id("actionApplyFilters");
  public static final String CONTACT_RESULTS_UUID_LOCATOR = "[title = '%s']";
  public static final By CONTACT_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By CONTACT_DIRECTORY_DETAILED_RADIOBUTTON =
      By.cssSelector("div#contactsViewSwitcher span:nth-child(2) > label");
  public static final By CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT =
      By.cssSelector("input#contactOrCaseLike");
  public static final By CONTACTS_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By CONTACTS_DETAILED_FIRST_TABLE_ROW =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By CONTACTS_DETAILED_TABLE_DATA = By.cssSelector("[role=gridcell]");
  public static final By DISEASE_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(5)");
  public static final By FIRST_NAME_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(9)");
  public static final By LAST_NAME_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(10)");
  public static final By TYPE_OF_CONTACT_COLUMNS =
      By.cssSelector("[role=rowgroup] tr>td:nth-child(12)");
  public static final By FOLLOW_UP_VISITS_BUTTON =
      By.cssSelector("#contactsViewSwitcher span:nth-child(3)");
  public static final By FROM_INPUT = By.cssSelector("#fromReferenceDateField input");
  public static final By TO_INPUT = By.cssSelector("#toReferenceDateField input");
  public static final By GRID_HEADERS = By.xpath("//thead//tr//th");
  public static final String RESULTS_GRID_HEADER = "//div[contains(text(), '%s')]";
  public static final By FIRST_CONTACT_ID_BUTTON = By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By CONTACT_EPIDEMIOLOGICAL_DATA = By.cssSelector("#tab-contacts-epidata");
  public static final By CASE_LIKE = By.id("contactOrCaseLike");
  public static final By NEW_ENTRY_EPIDEMIOLOGICAL_DATA = By.id("actionNewEntry");
  public static final By TYPE_OF_ACTIVITY_DETAILS = By.id("exposureTypeDetails");
  public static final By TYPE_OF_GATHERING_DETAILS = By.id("gatheringDetails");
  public static final By TYPE_OF_PLACE_DETAILS = By.id("typeOfPlaceDetails");
  public static final By INDOORS_OPTION = By.cssSelector("#indoors .v-select-option");
  public static final By TYPE_OF_GATHERING_COMBOBOX =
      By.cssSelector(".v-window #gatheringType div");
  public static final By LATITUDE_INPUT = By.cssSelector(".v-window input#latitude");
  public static final By LONGITUDE_INPUT = By.cssSelector(".v-window input#longitude");
  public static final By LATLONACCURACY_INPUT = By.cssSelector(".v-window input#latLonAccuracy");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector(".v-window #typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector(".v-window #facilityType div");
  public static final By FACILITY_DETAILS_COMBOBOX = By.cssSelector(".v-window #facility div");
  public static final By FACILITY_NAME_AND_DESCRIPTION =
      By.cssSelector(".v-window input#facilityDetails");
  public static final By CONTACT_PERSON_FIRST_NAME =
      By.cssSelector(".v-window input#contactPersonFirstName");
  public static final By CONTACT_PERSON_LAST_NAME =
      By.cssSelector(".v-window input#contactPersonLastName");
  public static final By CONTACT_PERSON_PHONE_NUMBER =
      By.cssSelector(".v-window input#contactPersonPhone");
  public static final By CONTACT_PERSON_EMAIL_ADRESS =
      By.cssSelector(".v-window input#contactPersonEmail");
}
