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

package org.sormas.e2etests.pages.application;

import org.openqa.selenium.By;

public class LoginPage {
  public static final By USER_NAME_INPUT = By.id("username");
  public static final By USER_PASSWORD_INPUT = By.id("password");
  public static final By LOGIN_BUTTON = By.id("Login.doLogIn");
  public static final By FAILED_LOGIN_ERROR_MESSAGE =
      By.xpath("//div[contains(@class, 'v-Notification-warning')]");
  public static final By LOGIN_KEYCLOAK_BUTTON = By.id("kc-login");
  public static final By PASSWORD_CONFIRM_INPUT = By.id("password-confirm");
  public static final By PASSWORD_NEW_INPUT = By.id("password-new");
  public static final By SUBMIT_BUTTON = By.cssSelector("div #kc-form-buttons input");
  public static final By ERROR_MESSAGE = By.cssSelector("div .alert-error");
}
