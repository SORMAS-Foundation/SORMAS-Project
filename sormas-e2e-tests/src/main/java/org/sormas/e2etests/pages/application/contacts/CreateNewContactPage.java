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

public class CreateNewContactPage {

  public static final By FIRST_NAME_OF_CONTACT_PERSON_INPUT =
      By.cssSelector(".v-window [location='firstName'] input");
  public static final By LAST_NAME_OF_CONTACT_PERSON_INPUT =
      By.cssSelector(".v-window [location='lastName'] input");
  public static final By DATE_OF_BIRTH_YEAR_COMBOBOX =
      By.cssSelector(".popupContent #birthdateYYYY input+div");
  public static final By DATE_OF_BIRTH_MONTH_COMBOBOX =
      By.cssSelector(".popupContent #birthdateMM input+div");
  public static final By DATE_OF_BIRTH_DAY_COMBOBOX =
      By.cssSelector(".popupContent #birthdateDD input+div");
  public static final By SEX_COMBOBOX =
      By.cssSelector(".v-window [location='sex'] [role='combobox'] div");
  public static final By PRIMARY_PHONE_NUMBER_INPUT = By.cssSelector(".v-window #phone");
  public static final By PRIMARY_EMAIL_ADDRESS_INPUT = By.cssSelector(".v-window #emailAddress");
  public static final By DATE_OF_REPORT_INPUT = By.cssSelector(".v-window #reportDateTime input");
  public static final By DISEASE_OF_SOURCE_CASE_COMBOBOX =
      By.cssSelector(".v-window [location='disease'] [role='combobox'] div");
  public static final By CASE_ID_IN_EXTERNAL_SYSTEM_INPUT =
      By.cssSelector(".v-window [id='caseIdExternalSystem']");
  public static final By DATE_OF_LAST_CONTACT_INPUT =
      By.cssSelector(".v-window [id='lastContactDate'] input");
  public static final By CASE_OR_EVENT_INFORMATION_INPUT =
      By.cssSelector(".v-window [id='caseOrEventInformation']");
  public static final By RELATIONSHIP_WITH_CASE_COMBOBOX =
      By.cssSelector(".v-window [id='relationToCase'] div");
  public static final By DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT =
      By.cssSelector(".v-window [id='description']");
  public static final By RESPONSIBLE_REGION_COMBOBOX = By.cssSelector(".v-window #region div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX = By.cssSelector(".v-window #district div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX =
      By.cssSelector(".v-window #community div");
  public static final By ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT =
      By.cssSelector(".v-window #contactProximityDetails");
  public static final By TYPE_OF_CONTACT_TRAVELER =
      By.cssSelector(".popupContent #returningTraveler > span label");
  public static final By TYPE_OF_CONTACT_OPTIONS =
      By.cssSelector(".popupContent #contactProximity > span label");
  public static final By CONTACT_CATEGORY_OPTIONS =
      By.cssSelector(".popupContent #contactCategory> span label");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By MULTI_DAY_CONTACT_LABEL = By.xpath("//*[@id='multiDayContact']/label");
  public static final By FIRST_DAY_CONTACT_DATE = By.cssSelector("#firstContactDate input");
  public static final By CHOOSE_CASE_BUTTON = By.id("contactChooseCase");
  public static final By SOURCE_CASE_WINDOW_CONTACT_DE =
      By.xpath("//div[contains(@class, 'popupContent')]//input[@placeholder='Suche...']");
  public static final By SOURCE_CASE_WINDOW_CONTACT =
      By.xpath("//div[contains(@class, 'popupContent')]//input[@placeholder='Search...']");
  public static final By SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION =
      By.xpath("//div[contains(@class, 'popupContent')]//table//tbody//tr[1]");
  public static final By SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON =
      By.xpath(
          "//div[contains(@class, 'popupContent')]//span[contains(text(), 'Confirm')]//ancestor::div[@id='commit']");
  public static final By SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON_DE =
      By.xpath(
          "//div[contains(@class, 'popupContent')]//span[contains(text(), 'Best\u00E4tigen')]//ancestor::div[@id='commit']");
}
