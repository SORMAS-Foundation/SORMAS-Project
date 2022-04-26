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

public class DistrictsTabPage {

  public static final By DISTRICTS_NEW_ENTRY_BUTTON = By.cssSelector("div#actionNewEntry");
  public static final By CREATE_NEW_ENTRY_DISTRICTS_NAME_INPUT =
      By.cssSelector(".popupContent #name");
  public static final By CREATE_NEW_ENTRY_DISTRICTS_REGION_COMBOBOX =
      By.cssSelector(".popupContent #region input + div");
  public static final By CREATE_NEW_ENTRY_DISTRICTS_EPID_CODE_INPUT =
      By.cssSelector(".popupContent #epidCode");
  public static final By SAVE_NEW_ENTRY_DISTRICTS = By.cssSelector(".popupContent #commit");
  public static final By SEARCH_DISTRICT_INPUT = By.cssSelector("#search");
  public static final By RESET_FILTERS_DISTRICTS_BUTTON = By.cssSelector("#actionResetFilters");
  public static final By EDIT_DISTRICT_BUTTON = By.xpath("//span[@class='v-icon v-icon-edit']");
  public static final By ARCHIVE_DISTRICT_BUTTON =
      By.cssSelector(".popupContent #actionArchiveInfrastructure");
  public static final By CONFIRM_ARCHIVING_DISTRICT_TEXT =
      By.xpath("//*[contains(text(),'Archivieren best\u00E4tigen')]");
}
