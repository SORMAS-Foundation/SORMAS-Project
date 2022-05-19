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

package org.sormas.e2etests.pages.application.vaccinations;

import org.openqa.selenium.By;

public class CreateNewVaccinationPage {
  public static final By SAVE_VACCINATION_BUTTON =
      By.cssSelector("[class='popupContent'] [id='commit']");
  public static final By VACCINATION_DATE =
      By.cssSelector("[class='popupContent'] [id='vaccinationDate'] input");
  public static final By VACCINATION_DATE_REPORT =
      By.cssSelector("[class='popupContent'] [id='reportDate'] input");
  public static final By DELETE_VACCINATION_BUTTON =
      By.cssSelector("[class='popupContent'] [id='delete']");
  public static final By REMOVE_REASON_COMBOBOX =
      By.xpath("/html/body/div[2]/div[5]/div/div/div[3]/div/div/div[1]/div/div[3]/div/div[2]/div");
  public static final By VACCINE_NAME_COMBOBOX = By.cssSelector(".v-window #vaccineName div");
  public static final By VACCINE_INFO_SOURCE_COMBOBOX =
      By.cssSelector(".v-window #vaccinationInfoSource div");
}
