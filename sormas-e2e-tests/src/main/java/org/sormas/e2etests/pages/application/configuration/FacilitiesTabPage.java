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

package org.sormas.e2etests.pages.application.configuration;

import org.openqa.selenium.By;

public class FacilitiesTabPage {

  public static final By FACILITIES_IMPORT_BUTTON = By.id("actionImport");
  public static final By OVERWRITE_CHECKBOX = By.xpath("//div[@class='v-slot v-align-middle'][1]");
  public static final By FILE_PICKER = By.cssSelector(".popupContent [class='gwt-FileUpload']");
  public static final By START_DATA_IMPORT_BUTTON = By.cssSelector("[class='v-button']");
  public static final By SEARCH_FACILITY = By.id("search");
  public static final By EXPORT_FACILITY_BUTTON = By.id("export");
  public static final By DETAILED_EXPORT_BUTTON = By.id("exportDetailed");
  public static final By CLOSE_DETAILED_EXPORT_POPUP =
      By.xpath("//div[@class='v-window-closebox']");
  public static final By IMPORT_SUCCESSFUL_FACILITY_IMPORT_CSV =
      By.xpath("//*[text()='Import successful!']");
  public static final By CLOSE_POPUP_FACILITIES_BUTTON = By.id("actionClose");
  public static final By CLOSE_FACILITIES_IMPORT_BUTTON =
      By.xpath("//div[@class='v-window-closebox']");
  public static final By FACILITIES_NEW_ENTRY_BUTTON = By.id("create");
  public static final By ENTER_BULK_EDIT_MODE_BUTTON_FACILITIES_CONFIGURATION =
      By.id("actionEnterBulkEditMode");
  public static final By FACILITY_CATEGORY_COMBOBOX_FACILITIES_CONFIGURATION =
      By.cssSelector("[id='typeGroup'] [class='v-filterselect-button']");
  public static final By FACILITY_TYPE_COMBOBOX_FACILITIES_CONFIGURATION =
      By.cssSelector("[id='type'] [class='v-filterselect-button']");
  public static final By COUNTRY_COMBOBOX_FACILITIES_CONFIGURATION =
      By.cssSelector("[id='country'] [class='v-filterselect-button']");
  public static final By REGION_COMBOBOX_FACILITIES_CONFIGURATION =
      By.cssSelector("[id='region'] [class='v-filterselect-button']");
  public static final By DISTRICT_COMBOBOX_FACILITIES_CONFIGURATION =
      By.cssSelector("[id='district'] [class='v-filterselect-button']");
  public static final By COMMUNITY_COMBOBOX_FACILITIES_CONFIGURATION =
      By.cssSelector("[id='community'] [class='v-filterselect-button']");
  public static final By RESET_FILTERS_BUTTON_FACILITIES_CONFIGURATION =
      By.id("actionResetFilters");
  public static final By FACILITY_NAME_INPUT = By.cssSelector("#name");
  public static final By REGION_COMBOBOX =
      By.cssSelector(".v-window [location='region'] [role='combobox'] div");
  public static final By DISTRICT_COMBOBOX =
      By.cssSelector(".v-window [location='district'] [role='combobox'] div");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector(".v-window #typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector(".v-window #type div");
  public static final By NEW_CASE_FACILITY_TYPE_COMBOBOX =
      By.cssSelector(".v-window #facilityType div");
  public static final By FACILITY_EXPOSURE_TYPE_COMBOBOX =
      By.cssSelector(".v-window #facilityType div");
  public static final By FACILITY_CONTACT_PERSON_FIRST_NAME_INPUT =
      By.cssSelector(".v-window #contactPersonFirstName");
  public static final By FACILITY_CONTACT_PERSON_LAST_NAME_INPUT =
      By.cssSelector(".v-window #contactPersonLastName");
  public static final By FACILITY_CONTACT_PERSON_PHONE_INPUT =
      By.cssSelector(".v-window #contactPersonPhone");
  public static final By FACILITY_CONTACT_PERSON_EMAIL_INPUT =
      By.cssSelector(".v-window #contactPersonEmail");
  public static final By EDIT_FIRST_FACILITY_BUTTON =
      By.xpath("(//span[@class='v-icon v-icon-edit'])[1]");
  public static final By ARCHIVE_FACILITY_BUTTON = By.cssSelector(".v-window #archiveDearchive");
  public static final By ACTION_CONFIRM_BUTTON = By.id("actionConfirm");
  public static final By RELEVANCE_STATUS_COMBOBOX_FACILITIES_CONFIGURATION =
      By.cssSelector("#relevanceStatus > div");
  public static final By FACILITY_GRID_RESULTS_ROWS = By.xpath("//tbody[@role='rowgroup']//tr");
}
