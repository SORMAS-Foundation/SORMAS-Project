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

public class UserRolesPage {
  public static By USER_RIGHTS_INPUT = By.cssSelector("#userRights input");
  public static By NEW_USER_ROLE_BUTTON = By.cssSelector("div #userRoleNewUserRole");
  public static By USER_ROLE_TEMPLATE_COMBOBOX =
      By.cssSelector(".popupContent #templateUserRole div");
  public static By CAPTION_INPUT = By.cssSelector(".popupContent #caption");
  public static By POPUP_SAVE_BUTTON = By.cssSelector(".popupContent #commit");
  public static By POPUP_DISCARD_BUTTON = By.cssSelector(".popupContent #discard");
  public static By ARCHIVE_CASES_CHECKBOX = By.xpath("//label[text()='Archive cases']");
  public static By ARCHIVE_CONTACTS_CHECKBOX = By.xpath("//label[text()='Archive contacts']");
  public static By SAVE_BUTTON = By.cssSelector("#commit");
  public static By USER_ROLE_LIST = By.cssSelector("#tab-user-userroles");

  public static By getUserRoleCaptionByText(String caption) {
    return By.xpath(String.format("//td[contains(text(), '%s')]", caption));
  }

  public static By USER_MANAGEMENT_TAB = By.cssSelector("div#tab-user-users");
  public static By USER_ROLE_DISABLE_BUTTON = By.cssSelector("#actionDisable");
  public static By USER_ROLE_ENABLE_BUTTON = By.cssSelector("#actionEnable");
  public static By ENABLED_DISABLED_SEARCH_INPUT = By.cssSelector("#enabled input");
  public static By ENABLED_DISABLED_SEARCH_COMBOBOX = By.cssSelector("#enabled div");
  public static By DELETE_USER_ROLE_BUTTON = By.cssSelector("#delete");
  public static By DELETE_CONFIRMATION_BUTTON = By.cssSelector(".popupContent #actionConfirm");
  public static By CANNOT_DELETE_USER_ROLE_POPUP =
      By.xpath("//div[contains(text(), 'Cannot delete user role')]");
  public static By CANNOT_DELETE_USER_ROLE_POPUP_OKAY_BUTTON = By.cssSelector("#actionOkay");
}
