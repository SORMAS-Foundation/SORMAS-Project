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

  public static final By VACCINATION_DATE_INPUT =
      By.cssSelector(".v-window #vaccinationDate input");
  public static final By VACCINATION_NAME_COMBOBOX = By.cssSelector(".v-window #vaccineName div");
  public static final By VACCINATION_MANUFACTURER_COMBOBOX =
      By.cssSelector(".v-window #vaccineManufacturer div");
  public static final By VACCINATION_NAME_INPUT = By.cssSelector(".v-window #vaccineName input");
  public static final By VACCINATION_MANUFACTURER_INPUT =
      By.cssSelector(".v-window #vaccineManufacturer input");
  public static final By VACCINATION_TYPE_INPUT = By.cssSelector(".v-window #vaccineType");
  public static final By VACCINATION_INFO_SOURCE_COMBOBOX =
      By.cssSelector(".v-window #vaccinationInfoSource div");
  public static final By VACCINATION_INFO_SOURCE_INPUT =
      By.cssSelector(".v-window #vaccinationInfoSource input");
  public static final By VACCINE_DOSE_INPUT = By.cssSelector(".v-window #vaccineDose");
  public static final By INN_INPUT = By.cssSelector(".v-window #vaccineInn");
  public static final By UNII_CODE_INPUT = By.cssSelector(".v-window #vaccineUniiCode");
  public static final By BATCH_NUMBER_INPUT = By.cssSelector(".v-window #vaccineBatchNumber");
  public static final By ATC_CODE_INPUT = By.cssSelector(".v-window #vaccineAtcCode");
  public static final By SAVE_VACCINATION_FORM_BUTTON = By.cssSelector(".v-window #commit");
  public static final By NEW_VACCINATION_DE_BUTTON = By.id("Neue Impfung");
  public static final By NEXT_PAGE_VACCINATION_TAB = By.id("nextPage");
}
