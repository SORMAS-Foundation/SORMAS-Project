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
  public static By JURISDICTION_LEVEL_COMBOBOX = By.cssSelector(".popupContent #jurisdictionLevel");
  public static By POPUP_SAVE_BUTTON = By.cssSelector(".popupContent #commit");
  public static By POPUP_DISCARD_BUTTON = By.cssSelector(".popupContent #discard");
}
