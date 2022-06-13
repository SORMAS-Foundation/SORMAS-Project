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

package org.sormas.e2etests.pages.application.actions;

import org.openqa.selenium.By;

public class EditActionPage {

  public static final By EDIT_ACTION_POPUP = By.cssSelector(".v-window .popupContent");
  public static final By DATE_INPUT = By.cssSelector("#date_date > input");
  public static final By PRIORITY_COMBOBOX = By.cssSelector("#priority > div");
  public static final By MEASURE_COMBOBOX = By.cssSelector("#actionMeasure > div");
  public static final By TITLE_INPUT = By.id("title");
  public static final By DESCRIPTION_IFRAME = By.cssSelector("#description > iframe");
  public static final By DESCRIPTION_INPUT = By.cssSelector("body");
  public static final By ACTION_STATUS_OPTIONS = By.cssSelector("#actionStatus label");
}
