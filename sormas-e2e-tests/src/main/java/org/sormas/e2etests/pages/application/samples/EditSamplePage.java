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

package org.sormas.e2etests.pages.application.samples;

import org.openqa.selenium.By;

public class EditSamplePage {

  public static final By PATHOGEN_NEW_TEST_RESULT_BUTTON =
      By.cssSelector("[id='pathogenTestNewTest']");
  public static final By DELETE_SAMPLE_BUTTON = By.cssSelector("#delete");
  public static final By SAMPLE_DELETION_POPUP = By.cssSelector(".v-window .popupContent");
  public static final By SAMPLE_DELETION_POPUP_YES_BUTTON = By.id("actionConfirm");
}
