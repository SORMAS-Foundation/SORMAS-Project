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

package org.sormas.e2etests.pages.application.users;

import org.openqa.selenium.By;

public class UserManagementPage {
  public static final By NEW_USER_BUTTON = By.id("userNewUser");
  public static final By SEARCH_USER_INPUT = By.id("search");
  public static final By FIRST_EDIT_BUTTON_FROM_LIST =
      By.cssSelector(".v-grid-body tr:nth-child(1) .v-icon.v-icon-edit");
  public static final By ACTIVE_INACTIVE_COMBOBOX = By.cssSelector("#active div");
  public static final By USER_NUMBER =
      By.xpath("(//div[contains(@class,'v-label v-widget bold')])[2]");
  public static final By SYNC_USERS_BUTTON = By.id("syncUsers");
  public static final By SYNC_POPUP_BUTTON = By.id("import-step-1");
  public static final By SYNC_SUCCESS_DE = By.xpath("//*[text()='Synchronisation erfolgreich!']");
  public static final By USER_ROLES_TAB = By.id("tab-user-userroles");
  public static final By USER_RIGHTS_COMBOBOX =
      By.cssSelector("[id='userRights'] [class='v-filterselect-button']");
  public static final By JURISDICTION_LEVEL_COMBOBOX =
      By.cssSelector("[id='jurisdictionLevel'] [class='v-filterselect-button']");
  public static final By ENABLED_USER_COMBOBOX =
      By.cssSelector("[id='enabled'] [class='v-filterselect-button']");
  public static final By USER_ROLE_TABLE_GRID = By.xpath("//table[@role='grid']");
  public static final By EXPORT_USER_ROLES = By.id("exportUserRoles");
}
