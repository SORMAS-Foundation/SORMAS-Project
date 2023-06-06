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

public class NavBarPage {
  public static final By CASES_BUTTON = By.cssSelector("div#cases");
  public static final By CONTACTS_BUTTON = By.cssSelector("div#contacts");
  public static final By EVENTS_BUTTON = By.cssSelector("div#events");
  public static final By TASKS_BUTTON = By.cssSelector("div#tasks");
  public static final By PERSONS_BUTTON = By.cssSelector("div#persons");
  public static final By DASHBOARD_BUTTON = By.cssSelector("div#dashboard");
  public static final By SAMPLE_BUTTON = By.cssSelector("[id='samples']");
  public static final By IMMUNIZATIONS_BUTTON = By.cssSelector("#immunizations");
  public static final By STATISTICS_BUTTON = By.cssSelector("#statistics");
  public static final By USERS_BUTTON = By.cssSelector("div#user");
  public static final By CONFIRM_NAVIGATION = By.cssSelector(("[id=actionConfirm]"));
  public static final By REPORTS_BUTTON = By.cssSelector("div#reports");
  public static final By CONFIGURATION_BUTTON = By.cssSelector("div#configuration");
  public static final By ABOUT_BUTTON = By.cssSelector("div#about");
  public static final By USER_SETTINGS_BUTTON = By.cssSelector("[id='actionSettings-2']");
  public static final By ENTRIES_BUTTON = By.cssSelector("div#travelEntries");
  public static final By LOGOUT_BUTTON = By.cssSelector("[id='actionLogout-2']");
  public static final By USER_SETTINGS_LANGUAGE_COMBOBOX_TEXT = By.cssSelector("#language input");
  public static final By DISCARD_USER_SETTINGS_BUTTON = By.id("discard");
  public static final By GDPR_CHECKBOX = By.cssSelector(".popupContent input + label");
  public static final By ACTION_CONFIRM_GDPR_POPUP = By.cssSelector(".popupContent #Confirm");
  public static final By ACTION_CONFIRM_GDPR_POPUP_DE =
      By.cssSelector(".popupContent #Best\u00E4tigen");
  public static final By MSERS_BUTTON = By.cssSelector("div #aggregatereports");
  public static final By LOGOUT_KEYCLOAK_BUTTON = By.cssSelector("[id='actionLogout-2']");
  public static final By SHARE_REQUESTS_BUTTON = By.id("shareRequests");
  public static final By MESSAGES_BUTTON = By.id("messages");
  public static final By ERROR_NOTIFICATION_CAPTION =
      By.xpath("//*[contains(text(),'An error has occurred')]");
  public static final By ERROR_NOTIFICATION_DESCRIPTION =
      By.xpath(
          "//*[contains(text(),'An unexpected error occurred. Please contact your supervisor or administrator and inform them about it.')]");
  public static final By ERROR_NOTIFICATION_CAPTION_DE =
      By.xpath("//*[contains(text(),'Ein Fehler ist aufgetreten')]");
  public static final By ERROR_NOTIFICATION_DESCRIPTION_DE =
      By.xpath(
          "//*[contains(text(),'Ein unerwarteter Fehler ist aufgetreten. Bitte kontaktieren Sie Ihre IT-Administration und informieren Sie sie dar\u00FCber.')]");
}
