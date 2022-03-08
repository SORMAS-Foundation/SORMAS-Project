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

public class CreateNewCasePage {
  public static final By DATE_OF_REPORT_INPUT = By.cssSelector(".v-window #reportDate input");
  public static final By DISEASE_COMBOBOX = By.cssSelector(".v-window #disease div");
  public static final By RESPONSIBLE_REGION_COMBOBOX =
      By.cssSelector(".v-window #responsibleRegion div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX =
      By.cssSelector(".v-window #responsibleDistrict div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX =
      By.cssSelector(".v-window #responsibleCommunity div");
  public static final By PLACE_OF_STAY_HOME =
      By.xpath("//div[@location='facilityOrHomeLoc']//label[contains(text(), 'Home')]");
  public static final By FIRST_NAME_INPUT =
      By.cssSelector(".v-window [location='firstName'] input");
  public static final By LAST_NAME_INPUT = By.cssSelector(".v-window [location='lastName'] input");
  public static final By SEX_COMBOBOX =
      By.cssSelector(".v-window [location='sex'] div[role='combobox'] div");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By CASE_ORIGIN_OPTIONS =
      By.cssSelector(".popupContent #caseOrigin .v-select-option");
  public static final By EXTERNAL_ID_INPUT = By.cssSelector(".popupContent #externalID");
  public static final By PLACE_OF_STAY =
      By.cssSelector(".popupContent div[location='facilityOrHomeLoc'] span.v-select-option label");
  public static final By PLACE_DESCRIPTION_INPUT =
      By.cssSelector(".popupContent #healthFacilityDetails");
  public static final By DATE_OF_BIRTH_YEAR_COMBOBOX =
      By.cssSelector(".popupContent #birthdateYYYY input+div");
  public static final By DATE_OF_BIRTH_MONTH_COMBOBOX =
      By.cssSelector(".popupContent #birthdateMM input+div");
  public static final By DATE_OF_BIRTH_DAY_COMBOBOX =
      By.cssSelector(".popupContent #birthdateDD input+div");
  public static final By PRESENT_CONDITION_OF_PERSON_COMBOBOX =
      By.cssSelector(".v-window [location='presentCondition'] div[role='combobox'] div");
  public static final By DATE_OF_SYMPTOM_ONSET_INPUT = By.cssSelector(".v-window #onsetDate input");
  public static final By PRIMARY_PHONE_NUMBER_INPUT = By.cssSelector(".v-window #phone");
  public static final By PRIMARY_EMAIL_ADDRESS_INPUT = By.cssSelector(".v-window #emailAddress");
  public static final By CONTACT_CASE_SAVE_BUTTON =
      By.xpath("//div[contains(@class, 'popupContent')]//div[@id='commit']");
  public static final By ENTER_HOME_ADDRESS_CHECKBOX =
      By.cssSelector("[location='enterHomeAddressNow'] span.v-checkbox");
  public static final By CASE_DISEASE_VARIANT_COMBOBOX =
      By.cssSelector(".v-window #diseaseVariant div");
}
