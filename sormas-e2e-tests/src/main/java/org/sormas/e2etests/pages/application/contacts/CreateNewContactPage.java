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

package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class CreateNewContactPage {
  public static final By FIRST_NAME_OF_CONTACT_PERSON_INPUT =
      By.cssSelector(".v-window [location='firstName'] input");
  public static final By LAST_NAME_OF_CONTACT_PERSON_INPUT =
      By.cssSelector(".v-window [location='lastName'] input");
  public static final By SEX_COMBOBOX =
      By.cssSelector(".v-window [location='sex'] [role='combobox'] div");
  public static final By DISEASE_OF_SOURCE_CASE_COMBOBOX =
      By.cssSelector(".v-window [location='disease'] [role='combobox'] div");
  public static final By RESPONSIBLE_REGION_COMBOBOX =
      By.cssSelector(".v-window [location='region'] [role='combobox'] div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX =
      By.cssSelector(".v-window [location='district'] [role='combobox'] div");
  public static final By SAVE_BUTTON = By.id("commit");
}
