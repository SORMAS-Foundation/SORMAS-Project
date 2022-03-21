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
  public static final By CLOSE_POPUP_FACILITIES_BUTTON = By.id("actionCancel");
  public static final By CLOSE_FACILITIES_IMPORT_BUTTON = By.xpath("//div[@class='v-window-closebox']");
}
