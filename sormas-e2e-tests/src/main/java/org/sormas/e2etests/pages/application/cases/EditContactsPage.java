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

import org.openqa.selenium.By;

public class EditContactsPage {
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
}
