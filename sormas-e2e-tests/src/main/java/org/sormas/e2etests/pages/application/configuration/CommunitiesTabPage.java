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

public class CommunitiesTabPage {

  public static final By COMMUNITIES_NEW_ENTRY_BUTTON = By.cssSelector("div#actionNewEntry");
  public static final By CREATE_NEW_ENTRY_COMMUNITIES_NAME_INPUT =
      By.cssSelector(".popupContent #name");
  public static final By CREATE_NEW_ENTRY_COMMUNITIES_REGION_COMBOBOX =
      By.cssSelector(".popupContent #region input + div");
  public static final By CREATE_NEW_ENTRY_COMMUNITIES_DISTRICT_COMBOBOX =
      By.cssSelector(".popupContent #district input + div");
  public static final By SAVE_NEW_ENTRY_COMMUNITIES = By.cssSelector(".popupContent #commit");
  public static final By SEARCH_COMMUNITY_INPUT = By.cssSelector("#search");
  public static final By RESET_FILTERS_COMMUNITIES_BUTTON = By.cssSelector("#actionResetFilters");
  public static final By EDIT_COMMUNITY_BUTTON = By.xpath("//span[@class='v-icon v-icon-edit']");
  public static final By ARCHIVE_COMMUNITY_BUTTON =
      By.cssSelector(".popupContent #actionArchiveInfrastructure");
  public static final By DEARCHIVE_COMMUNITY_BUTTON =
      By.cssSelector(".popupContent #actionDearchiveInfrastructure");
  public static final By CONFIRM_ARCHIVING_COMMUNITY_TEXT =
      By.xpath("//*[contains(text(),'Confirm archiving')]");
  public static final By CONFIRM_DEARCHIVING_COMMUNITY_TEXT =
      By.xpath("//*[contains(text(),'Confirm de-archiving')]");
  public static final By CONFIRM_ARCHIVING_YES_BUTTON =
      By.cssSelector(".popupContent #actionConfirm");
  public static final By COMMUNITY_FILTER_COMBOBOX = By.cssSelector("#relevanceStatus > div");
}
