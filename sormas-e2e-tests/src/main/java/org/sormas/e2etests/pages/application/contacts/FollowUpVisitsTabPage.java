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

package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class FollowUpVisitsTabPage {

  public static final By NEW_VISIT_BUTTON = By.id("visitNewVisit");
  public static final By CONTACTS_LIST_BUTTON = By.id("tab-contacts");
  public static final By FIRST_VISIT_BUTTON =
      By.cssSelector(".v-grid-body tr:nth-child(1) .v-icon.v-icon-edit");
  public static final By LATEST_VISIT_ORIGIN = By.xpath("//tbody//tr[1]//td[9]");
}
