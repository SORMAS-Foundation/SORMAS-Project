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
  public static final By USER_ID =
      By.cssSelector("#options-menu-top-pagination > div.pf-c-options-menu > div > span > b");
  public static final By RESULT_IN_TABLE = By.cssSelector("table tbody tr");
  public static final By ITEMS_PER_PAGE_BUTTON = By.id("users:title-top-toggle");
  public static final By ONE_HUNDRED_PER_PAGE_BUTTON =
      By.xpath("//button[contains(text(), '100')]");
  public static final By NEXT_PAGE_BUTTON =
      By.xpath("//*[@id='users:title-top-pagination']/nav/div[2]/button");
  public static final By USER_ENABLE_DISABLE_SWITCH = By.cssSelector("#userEnabled");
  public static final By USER_DISABLED = By.xpath("//span[contains(text(), 'Disabled')]");
  public static final By SEARCH_USER_INPUT = By.xpath("//input[@placeholder='Search user']");
  public static final By SEARCH_BUTTON = By.xpath("//button[@aria-label='Search']");

  public static By getUserIdByName(String name) {
    return By.xpath(String.format("//td[text()=\"%s\"]/../td/a", name));
  }
}
