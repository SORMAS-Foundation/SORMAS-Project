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

package org.sormas.e2etests.pages.application.configuration;

import org.openqa.selenium.By;

public class OutbreaksTabPage {

  public static final By DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION =
      By.cssSelector("[class='v-grid-cell']");
  public static final By REGION_DISEASE_MATRIX_OUTBREAKS_CONFIGURATION =
      By.cssSelector("[class='v-button']");
  public static final By SET_OUTBREAK_RADIO_BUTTONS =
      By.cssSelector("[role='radiogroup']>:nth-child(1)");
  public static final By SAVE_OUTBREAK_CONFIGURATION_BUTTON = By.id("commit");
  public static final By DISCARD_OUTBREAK_CONFIGURATION_BUTTON = By.id("discard");
}
