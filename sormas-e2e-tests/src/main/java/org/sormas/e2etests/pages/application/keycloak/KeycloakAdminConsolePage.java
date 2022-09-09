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
package org.sormas.e2etests.pages.application.keycloak;

import org.openqa.selenium.By;

public class KeycloakAdminConsolePage {
  public static final By VIEW_ALL_USERS_BUTTON = By.id("viewAllUsers");
  public static final By USER_ID = By.cssSelector("td.clip a");
  public static final By NEXT_PAGE_BUTTON = By.cssSelector("button.next.ng-binding");
  public static final By USER_ENABLE_DISABLE_SWITCH = By.cssSelector("#userEnabled");
  public static final By USER_DISABLED =
      By.xpath("//input[@id=\"userEnabled\" and contains(@class,\"ng-empty\")]");

  public static By getUserIdByName(String name) {
    return By.xpath(String.format("//td[text()=\"%s\"]/../td/a", name));
  }
}
