/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

public class NavBarPage {
  public static final By CASES_BUTTON = By.cssSelector("div#cases");
  public static final By CONTACTS_BUTTON = By.cssSelector("div#contacts");
  public static final By EVENTS_BUTTON = By.cssSelector("div#events");
  public static final By TASKS_BUTTON = By.cssSelector("div#tasks");
  public static final By PERSONS_BUTTON = By.cssSelector("div#persons");
  public static final By DASHBOARD_BUTTON = By.cssSelector("div#dashboard");
  public static final By SAMPLE_BUTTON = By.cssSelector("[id='samples']");
  public static final By USERS_BUTTON = By.cssSelector("div#users");
  public static final By CONFIRM_NAVIGATION = By.cssSelector(("[id=actionConfirm]"));
}
