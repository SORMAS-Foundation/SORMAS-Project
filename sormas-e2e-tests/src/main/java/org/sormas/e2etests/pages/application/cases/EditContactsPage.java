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

package org.sormas.e2etests.pages.application.cases;

import java.util.Locale;
import org.openqa.selenium.By;

public class EditContactsPage {
  public static final By CONTACTS_TAB_BUTTON = By.id("tab-cases-contacts");
  public static final By NEW_CONTACT_BUTTON = By.id("contactNewContact");
  public static final String CONTACT_RESULTS_UUID_LOCATOR = "[title = '%s']";
  public static final By RESPONSIBLE_REGION_COMBOBOX = By.cssSelector("#region input + div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX = By.cssSelector("#district input + div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX = By.cssSelector("#community input + div");
  public static final By RELATIONSHIP_WITH_CASE_COMBOBOX =
      By.cssSelector("#relationToCase input + div");
  public static final By RESPONSIBLE_REGION_INPUT = By.cssSelector("#region input");
  public static final By RESPONSIBLE_DISTRICT_INPUT = By.cssSelector("#district input");
  public static final By RESPONSIBLE_COMMUNITY_INPUT = By.cssSelector("#community input");
  public static final By RELATIONSHIP_WITH_CASE_INPUT = By.cssSelector("#relationToCase input");
  public static final By CASE_CONTACT_EXPORT = By.cssSelector("#export");
  public static final By DETAILED_EXPORT_CASE_CONTACT_BUTTON = By.id("exportDetailed");
  public static final By CLOSE_POPUP_BUTTON = By.cssSelector(".v-window-closebox");
  public static final By IMPORT_CASE_CONTACTS_BUTTON = By.id("actionImport");
  public static final By IMPORT_POPUP_BUTTON = By.cssSelector("[class='v-button']");
  public static final By COMMIT_BUTTON = By.id("commit");
  public static final By IMPORT_SUCCESS = By.xpath("//*[text()='Import successful!']");
  public static final By RESULTS_IN_GRID_IMPORT_POPUP =
      By.xpath(
          " //div[contains(@class, 'popupContent')]//tr[contains(@class, 'v-grid-row-has-data')]");
  public static final By FIRST_RESULT_IN_GRID_IMPORT_POPUP =
      By.xpath(
          " //div[contains(@class, 'popupContent')]//tr[contains(@class, 'v-grid-row-has-data')]//td");

  public static final By EXTERNAL_TOKEN_CONTACT_INPUT = By.cssSelector("#externalToken");
  public static final By CASE_OR_EVENT_INFORMATION_CONTACT_TEXT_AREA =
      By.cssSelector("#caseOrEventInformation");
  public static final By CONTACT_LINKED_TO_EVENT_POPUP =
      By.xpath("//*[text()='All contacts have been linked to the selected event.']");

  public static By getContactByUUID(String uuid) {
    return By.xpath(String.format("//a[text()=\"%s\"]", uuid.substring(0, 6).toUpperCase()));
  }

  public static By getContactFirstAndLastName(String name) {
    String[] firstAndLastName = name.split("\\s+");
    return By.xpath(
        String.format(
            "//div[@class='v-slot v-slot-h2 v-slot-primary v-slot-vspace-none v-slot-vspace-top-none v-slot-caption-truncated']/div[contains(text(), '%s')]",
            firstAndLastName[0] + " " + firstAndLastName[1].toUpperCase(Locale.GERMAN)));
  }
}
