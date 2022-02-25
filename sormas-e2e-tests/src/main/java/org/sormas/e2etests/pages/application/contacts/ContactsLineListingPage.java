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

public class ContactsLineListingPage {
  public static final By LINE_LISTING_DISEASE_COMBOBOX =
      By.cssSelector("[id='lineListingSharedInfoField'] [id='disease'] div");
  public static final By LINE_LISTING_REGION_COMBOBOX =
      By.cssSelector("[id='lineListingSharedInfoField'] [id='region'] div");
  public static final By LINE_LISTING_DISTRICT_COMBOBOX =
      By.cssSelector("[id='lineListingSharedInfoField'] [id='district'] div");
  public static final By LINE_LISTING_DATE_REPORT_INPUT =
      By.cssSelector("[id='dateOfReport'] input");
  public static final By LINE_LISTING_SECOND_DATE_REPORT_INPUT =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='dateOfReport'] input");
  public static final By LINE_LISTING_DATE_LAST_CONTACT_INPUT =
      By.cssSelector("[id='lastDate'] input");
  public static final By LINE_LISTING_SECOND_DATE_LAST_CONTACT_INPUT =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='lastDate'] input");
  public static final By LINE_LISTING_TYPE_OF_CONTACT_COMBOBOX =
      By.cssSelector("[id='typeOfContact'] div");
  public static final By LINE_LISTING_SECOND_TYPE_OF_CONTACT_COMBOBOX =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='typeOfContact'] div");
  public static final By LINE_LISTING_RELATIONSHIP_TO_CASE_COMBOBOX =
      By.cssSelector("[id='relationToCase'] div");
  public static final By LINE_LISTING_SECOND_RELATIONSHIP_TO_CASE_COMBOBOX =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='relationToCase'] div");
  public static final By LINE_LISTING_FIRST_NAME_INPUT = By.cssSelector("[id='firstName']");
  public static final By LINE_LISTING_LAST_NAME_INPUT = By.cssSelector("[id='lastName']");
  public static final By LINE_LISTING_BIRTHDATE_YEAR_COMBOBOX =
      By.cssSelector("[id='dateOfBirthYear'] div");
  public static final By LINE_LISTING_SECOND_BIRTHDATE_YEAR_COMBOBOX =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='dateOfBirthYear'] div");
  public static final By LINE_LISTING_BIRTHDATE_MONTH_COMBOBOX =
      By.cssSelector("[id='dateOfBirthMonth'] div");
  public static final By LINE_LISTING_SECOND_BIRTHDATE_MONTH_COMBOBOX =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='dateOfBirthMonth'] div");
  public static final By LINE_LISTING_BIRTHDATE_DAY_COMBOBOX =
      By.cssSelector("[id='dateOfBirthDay'] div");
  public static final By LINE_LISTING_SECOND_BIRTHDATE_DAY_COMBOBOX =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='dateOfBirthDay'] div");
  public static final By LINE_LISTING_SEX_COMBOBOX = By.cssSelector("[id='sex'] div");
  public static final By LINE_LISTING_SECOND_SEX_COMBOBOX =
      By.cssSelector("[id='lineListingContactLineField_1'] [id='sex'] div");
  public static final By ADD_LINE = By.cssSelector("[id='lineListingAddLine']");
  public static final By LINE_LISTING_ACTION_SAVE = By.cssSelector("[id='actionSave']");
}
