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

package org.sormas.e2etests.pages.application.persons;

import org.openqa.selenium.By;

public class EditPersonPage {
  public static final By UUID_INPUT = By.cssSelector("#uuid");
  public static final By USER_INFORMATION =
      By.cssSelector(".v-slot.v-slot-h2.v-slot-vspace-top-none.v-slot-primary");
  public static final By FIRST_NAME_INPUT = By.id("firstName");
  public static final By LAST_NAME_INPUT = By.id("lastName");
  public static final By DATE_OF_BIRTH_YEAR_COMBOBOX = By.cssSelector("#birthdateYYYY input+div");
  public static final By DATE_OF_BIRTH_MONTH_COMBOBOX = By.cssSelector("#birthdateMM input+div");
  public static final By DATE_OF_BIRTH_DAY_COMBOBOX = By.cssSelector("#birthdateDD input+div");
  public static final By SEX_INPUT = By.cssSelector("#sex input");
  public static final By SEX_COMBOBOX = By.cssSelector("[location='sex'] div[role='combobox'] div");
  public static final By SALUTATION_INPUT = By.cssSelector("[location=salutation] input");
  public static final By SALUTATION_COMBOBOX = By.cssSelector("[location=salutation] input + div");
  public static final By PRESENT_CONDITION_INPUT = By.cssSelector("#presentCondition input");
  public static final By PRESENT_CONDITION_COMBOBOX =
      By.cssSelector("#presentCondition input + div");
  public static final By PASSPORT_NUMBER_INPUT = By.cssSelector("#passportNumber");
  public static final By NATIONAL_HEALTH_ID_INPUT = By.cssSelector("#nationalHealthId");
  public static final By EXTERNAL_ID_INPUT = By.cssSelector("#externalId ");
  public static final By EXTERNAL_TOKEN_INPUT = By.cssSelector("#externalToken");
  public static final By TYPE_OF_OCCUPATION_COMBOBOX =
      By.cssSelector("[location=occupationType] input+div");
  public static final By TYPE_OF_OCCUPATION_INPUT =
      By.cssSelector("[location=occupationType] input");
  public static final By STAFF_OF_ARMED_FORCES_COMBOBOX =
      By.cssSelector("[location=armedForcesRelationType] input+div");
  public static final By STAFF_OF_ARMED_FORCES_INPUT =
      By.cssSelector("[location=armedForcesRelationType] input");
  public static final By EDUCATION_COMBOBOX = By.cssSelector("[location=educationType] input+div");
  public static final By EDUCATION_INPUT = By.cssSelector("[location=educationType] input");
  public static final By REGION_COMBOBOX = By.cssSelector("#region > div");
  public static final By REGION_INPUT = By.cssSelector("#region > input");
  public static final By DISTRICT_COMBOBOX = By.cssSelector("#district > div");
  public static final By DISTRICT_INPUT = By.cssSelector("#district > input");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector("#community > div");
  public static final By COMMUNITY_INPUT = By.cssSelector("#community > input");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector("#typeGroup > div");
  public static final By FACILITY_CATEGORY_INPUT = By.cssSelector("#typeGroup > input");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector("#facilityType > div");
  public static final By FACILITY_TYPE_INPUT = By.cssSelector("#facilityType > input");
  public static final By FACILITY_COMBOBOX = By.cssSelector("#facility > div");
  public static final By FACILITY_INPUT = By.cssSelector("#facility > input");
  public static final By FACILITY_NAME_AND_DESCRIPTION_INPUT = By.id("facilityDetails");
  public static final By STREET_INPUT = By.id("street");
  public static final By HOUSE_NUMBER_INPUT = By.id("houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT = By.id("additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.id("postalCode");
  public static final By CITY_INPUT = By.id("city");
  public static final By AREA_TYPE_COMBOBOX = By.cssSelector("#areaType > div");
  public static final By AREA_TYPE_INPUT = By.cssSelector("#areaType > input");
  public static final By CONTACT_PERSON_FIRST_NAME_INPUT = By.id("contactPersonFirstName");
  public static final By CONTACT_PERSON_LAST_NAME_INPUT = By.id("contactPersonLastName");
  public static final By COMMUNITY_CONTACT_PERSON_INPUT = By.id("details");
  public static final By BIRTH_NAME_INPUT = By.id("birthName");
  public static final By NICKNAME_INPUT = By.id("nickname");
  public static final By MOTHER_MAIDEN_NAME_INPUT = By.id("mothersMaidenName");
  public static final By MOTHER_NAME_INPUT = By.id("mothersName");
  public static final By FATHER_NAME_INPUT = By.id("fathersName");
  public static final By NAMES_OF_GUARDIANS_INPUT = By.id("namesOfGuardians");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By PERSON_DATA_SAVED_POPUP = By.cssSelector(".v-Notification-caption");
  public static final By EMAIL_FIELD =
      By.xpath(
          "(//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Email')])[1]/../following-sibling::td//div");
  public static final By PHONE_FIELD =
      By.xpath(
          "(//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Phone')])[1]/../following-sibling::td//div");
  public static final By PERSON_CONTACT_DETAILS_CONTACT_INFORMATION_INPUT =
      By.xpath(
          "(//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Other')])[1]/../following-sibling::td//div");
  public static final By PERSON_CONTACT_DETAILS_TYPE_OF_DETAILS_INPUT =
      By.xpath(
          "(//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Other')])[1]");
  public static final By PRIMARY_CONTACT_DETAILS_EDIT_OTHER_FIELD =
      By.xpath(
          "(//div[@class='v-table-cell-wrapper' and contains(text(),'Other')])[1]//preceding::div[contains(@id,'edit')][1]");
  public static final By PRIMARY_CONTACT_DETAILS_EDIT_EMAIL_FIELD =
      By.xpath(
          "(//div[@class='v-table-cell-wrapper' and contains(text(),'Email')])[1]//preceding::div[contains(@id,'edit')][1]");
  public static final By PRIMARY_CONTACT_DETAILS_EDIT_PHONE_FIELD =
      By.xpath(
          "(//div[@class='v-table-cell-wrapper' and contains(text(),'Phone')])[1]//preceding::div[contains(@id,'edit')][1]");
  public static final By POPUP_PERSON_ID = By.cssSelector("#uuid");
  public static final By POPUP_SAVE = By.cssSelector("#commit");
  public static final By POPUP_RESPONSIBLE_REGION_COMBOBOX = By.cssSelector("#region div");
  public static final By POPUP_RESPONSIBLE_DISTRICT_COMBOBOX = By.cssSelector("#district div");
  public static final By PERSON_DATA_SAVED = By.xpath("//*[contains(text(),'Person data saved')]");
  public static final By PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE =
      By.xpath(
          "//*[contains(text(),'The case person was added as an event participant to the selected event.')]");
  public static final By SEE_EVENTS_FOR_PERSON =
      By.cssSelector("div#See\\ events\\ for\\ this\\ person");

  public static By getByPersonUuid(String personUuid) {
    return By.cssSelector("a[title='" + personUuid + "']");
  }
}
