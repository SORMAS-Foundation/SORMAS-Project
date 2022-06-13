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

public class EditContactPersonPage {
  public static final By UUID_INPUT = By.cssSelector("#uuid");
  public static final By CONTACT_PERSON_TAB = By.cssSelector("div#tab-contacts-person");
  public static final By USER_INFORMATION =
      By.cssSelector(".v-slot.v-slot-h2.v-slot-vspace-top-none.v-slot-primary");
  public static final By SEX_INPUT = By.cssSelector("#sex input");
  public static final By EMAIL_FIELD =
      By.xpath(
          "(//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Email')])[1]/../following-sibling::td//div");
  public static final By PHONE_FIELD =
      By.xpath(
          "(//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Phone')])[1]/../following-sibling::td//div");
  public static final By FIRST_NAME_INPUT = By.cssSelector("#firstName");
  public static final By DATE_OF_BIRTH_YEAR_COMBOBOX = By.cssSelector("#birthdateYYYY input+div");
  public static final By DATE_OF_BIRTH_MONTH_COMBOBOX = By.cssSelector("#birthdateMM input+div");
  public static final By DATE_OF_BIRTH_DAY_COMBOBOX = By.cssSelector("#birthdateDD input+div");
  public static final By SEX_COMBOBOX = By.cssSelector("[location='sex'] div[role='combobox'] div");
  public static final By SALUTATION_COMBOBOX = By.cssSelector("[location=salutation] input + div");
  public static final By PRESENT_CONDITION_COMBOBOX =
      By.cssSelector("#presentCondition input + div");
  public static final By EXTERNAL_ID_INPUT = By.cssSelector("#externalId ");
  public static final By EXTERNAL_TOKEN_INPUT = By.cssSelector("#externalToken");
  public static final By TYPE_OF_OCCUPATION_COMBOBOX =
      By.cssSelector("[location=occupationType] input+div");
  public static final By STAFF_OF_ARMED_FORCES_COMBOBOX =
      By.cssSelector("[location=armedForcesRelationType] input+div");
  public static final By REGION_COMBOBOX = By.cssSelector("#region > div");
  public static final By DISTRICT_COMBOBOX = By.cssSelector("#district > div");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector("#community > div");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector("#typeGroup > div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector("#facilityType > div");
  public static final By FACILITY_COMBOBOX = By.cssSelector("#facility > div");
  public static final By FACILITY_NAME_AND_DESCRIPTION_INPUT = By.cssSelector("#facilityDetails");
  public static final By STREET_INPUT = By.cssSelector("#street");
  public static final By HOUSE_NUMBER_INPUT = By.cssSelector("#houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT = By.cssSelector("#additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.cssSelector("#postalCode");
  public static final By CITY_INPUT = By.cssSelector("#city");
  public static final By AREA_TYPE_COMBOBOX = By.cssSelector("#areaType > div");
  public static final By CONTACT_PERSON_FIRST_NAME_INPUT =
      By.cssSelector("#contactPersonFirstName");
  public static final By CONTACT_PERSON_LAST_NAME_INPUT = By.cssSelector("#contactPersonLastName");
  public static final By BIRTH_NAME_INPUT = By.cssSelector("#birthName");
  public static final By NAMES_OF_GUARDIANS_INPUT = By.cssSelector("#namesOfGuardians");
  public static final By CONTACT_INFORMATION_NEW_ENTRY_BUTTON =
      By.cssSelector("#personContactDetails #actionNewEntry");
  public static final By SAVE_BUTTON = By.cssSelector("#commit");
  public static final By PERSON_DATA_SAVED_POPUP = By.cssSelector(".v-Notification-caption");
}
