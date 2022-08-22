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

package org.sormas.e2etests.pages.application.immunizations;

import org.openqa.selenium.By;

public class ImmunizationsDirectoryPage {

  public static final By ADD_NEW_IMMUNIZATION_BUTTON =
      By.cssSelector("#immunizationNewImmunization");
  public static final By FIRST_IMMUNIZATION_ID_BUTTON =
      By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By IMMUNIZATION_ID_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Immunization Id')]");
  public static final By PERSON_ID_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Person Id')]");
  public static final By FIRST_NAME_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'First name')]");
  public static final By LAST_NAME_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Last name')]");
  public static final By DISEASE_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Disease')]");
  public static final By AGE_AND_BIRTHDATE_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Age and birthdate')]");
  public static final By SEX_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Sex')]");
  public static final By DISTRICT_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'District')]");
  public static final By MEANS_OF_IMMUNIZATION_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Means of immunization')]");
  public static final By MANAGEMENT_STATUS_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Management Status')]");
  public static final By IMMUNIZATION_STATUS_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Immunization status')]");
  public static final By IMMUNIZATION_START_DATE_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Start date')]");
  public static final By IMMUNIZATION_END_DATE_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'End date')]");
  public static final By TYPE_OF_LAST_VACCINE_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Type of last vaccine')]");
  public static final By DATE_OF_RECOVERY_COLUMN_HEADER =
      By.xpath("//table[@role='grid']//div[contains(text(), 'Date of recovery')]");
  public static final By RESULTS_IN_GRID = By.xpath("//tr[contains(@class,'v-grid-row-has-data')]");
  public static final By GENERAL_SEARCH_INPUT = By.cssSelector("input#nameAddressPhoneEmailLike");
  public static final By IMMUNIZATION_YEAR_FILTER_COMBOBOX =
      By.cssSelector("[id='birthdateYYYY'] [class='v-filterselect-button']");
  public static final By IMMUNIZATION_YEAR_FILTER_INPUT =
      By.cssSelector("[id='birthdateYYYY'] input");
  public static final By IMMUNIZATION_MONTH_FILTER_COMBOBOX =
      By.cssSelector("[id='birthdateMM'] [class='v-filterselect-button']");
  public static final By IMMUNIZATION_MONTH_FILTER_INPUT =
      By.cssSelector("[id='birthdateMM'] input");
  public static final By IMMUNIZATION_DAY_FILTER_COMBOBOX =
      By.cssSelector("[id='birthdateDD'] [class='v-filterselect-button']");
  public static final By IMMUNIZATION_DAY_FILTER_INPUT = By.cssSelector("[id='birthdateDD'] input");
  public static final By MEANS_OF_IMMUNIZATION_FILTER_COMBOBOX =
      By.cssSelector("#meansOfImmunization div");
  public static final By MANAGEMENT_STATUS_FILTER_COMBOBOX =
      By.cssSelector("#immunizationManagementStatus div");
  public static final By IMMUNIZATION_STATUS_FILTER_COMBOBOX =
      By.cssSelector("#immunizationStatus div");
  public static final By COMMUNITY_FILTER_COMBOBOX = By.cssSelector("#community div");
  public static final By FACILITY_CATEGORY_FILTER_COMBOBOX =
      By.cssSelector("#facilityTypeGroup div");
  public static final By FACILITY_TYPE_FILTER_COMBOBOX = By.cssSelector("#facilityType div");
  public static final By FACILITY_FILTER_COMBOBOX = By.cssSelector("#healthFacility div");
  public static final By DATA_TYPE_FILTER_COMBOBOX = By.cssSelector("#dateType div");
  public static final By REGION_FILTER_COMBOBOX =
      By.cssSelector("[id='region'] [class='v-filterselect-button']");
  public static final By DISTRICT_FILTER_COMBOBOX =
      By.cssSelector("[id='district'] [class='v-filterselect-button']");
  public static final By DATE_FROM_CALENDAR_INPUT = By.cssSelector("#dateFrom input");
  public static final By DATE_TO_CALENDAR_INPUT = By.cssSelector("#dateTo input");
}
