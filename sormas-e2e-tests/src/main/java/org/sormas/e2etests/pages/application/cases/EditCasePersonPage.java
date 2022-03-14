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

public class EditCasePersonPage {
  public static final By FIRST_NAME_INPUT = By.cssSelector("#firstName");
  public static final By LAST_NAME_INPUT = By.cssSelector("#lastName");
  public static final By PRESENT_CONDITION_INPUT = By.cssSelector("#presentCondition input");
  public static final By SEX_INPUT = By.cssSelector("#sex input");
  public static final By EMAIL_FIELD =
      By.xpath(
          "//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Email')]/../following-sibling::td//div");
  public static final By PHONE_FIELD =
      By.xpath(
          "//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Phone')]/../following-sibling::td//div");
  public static final By DATE_OF_BIRTH_YEAR_INPUT = By.cssSelector("#birthdateYYYY input");
  public static final By DATE_OF_BIRTH_MONTH_INPUT = By.cssSelector("#birthdateMM input");
  public static final By DATE_OF_BIRTH_DAY_INPUT = By.cssSelector("#birthdateDD input");
  public static final By PRESENT_CONDITION_COMBOBOX = By.cssSelector("#presentCondition div");
  public static final By DATE_OF_DEATH_INPUT = By.cssSelector("#deathDate input");
  public static final By CASE_OF_DEATH = By.cssSelector("#causeOfDeath");
  public static final By CASE_OF_DEATH_COMBOBOX = By.cssSelector("#causeOfDeath div");
  public static final By DEATH_PLACE_TYPE_COMBOBOX = By.cssSelector("#deathPlaceType div");
  public static final By DEATH_PLACE_DESCRIPTION = By.cssSelector("#deathPlaceDescription");
  public static final By DATE_OF_BURIAL_INPUT = By.cssSelector("#burialDate input");
  public static final By BURIAL_CONDUCTOR_INPUT = By.cssSelector("#burialConductor input");
  public static final By BURIAL_PLACE_DESCRIPTION = By.cssSelector("#burialPlaceDescription");
  public static final By FACILITY_COMBOBOX = By.cssSelector("#facility div");
  public static final By FACILITY_DETAILS_INPUT = By.cssSelector("#facilityDetails");
  public static final By STREET_INPUT = By.cssSelector("#street");
  public static final By HOUSE_NUMBER_INPUT = By.cssSelector("#houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT = By.cssSelector("#additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.cssSelector("#postalCode");
  public static final By CITY_INPUT = By.cssSelector("#city");
  public static final By AREA_TYPE_COMBOBOX = By.cssSelector("#areaType div");
  public static final By COUNTRY_COMBOBOX = By.cssSelector("#country div");
  public static final By GEOCODE_BUTTON = By.cssSelector("#geocodeButton");
}
