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

package org.sormas.e2etests.pages.application.users;

import org.openqa.selenium.By;

public class EditUserPage {
  public static final By FIRST_NAME_OF_USER_INPUT = By.id("firstName");
  public static final By LAST_NAME_OF_USER_INPUT = By.id("lastName");
  public static final By CREATE_NEW_PASSWORD_BUTTON = By.id("userResetPassword");
  public static final By CONFIRM_NEW_PASSWORD_BUTTON = By.id("actionConfirm");
  public static final By CANCEL_NEW_PASSWORD_BUTTON = By.id("actionCancel");
  public static final By NEW_GENERATED_PASSWORD_TEXT =
      By.xpath("//div[@class='v-window-wrap']//div[contains(@class,\"h2 v-label-undef-w\")]");
  public static final By CLOSE_PASSWORD_POPUP_BUTTON =
      By.xpath("//div[@class='v-window-closebox']");
  public static final By EMAIL_ADDRESS_INPUT = By.id("userEmail");
  public static final By PHONE_INPUT = By.id("phone");
  public static final By COUNTRY_COMBOBOX_INPUT = By.cssSelector("#country input");
  public static final By REGION_COMBOBOX_INPUT = By.cssSelector(".v-window #region input");
  public static final By DISTRICT_COMBOBOX_INPUT = By.cssSelector(".v-window #district input");
  public static final By COMMUNITY_COMBOBOX_INPUT = By.cssSelector(".v-window #community input");
  public static final By FACILITY_CATEGORY_COMBOBOX_INPUT = By.cssSelector("#typeGroup input");
  public static final By FACILITY_TYPE_COMBOBOX_INPUT = By.cssSelector("#facilityType input");
  public static final By FACILITY_COMBOBOX_INPUT = By.cssSelector("#facility input");
  public static final By STREET_INPUT = By.id("street");
  public static final By HOUSE_NUMBER_INPUT = By.id("houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT = By.id("additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.id("postalCode");
  public static final By CITY_INPUT = By.id("city");
  public static final By AREA_TYPE_COMBOBOX_INPUT = By.cssSelector("#areaType input");
  public static final By FACILITY_NAME_DESCRIPTION_VALUE = By.id("facilityDetails");
  public static final By LANGUAGE_COMBOBOX_INPUT = By.cssSelector("#language input");
  public static final By COMMUNITY_CONTACT_PERSON_INPUT = By.cssSelector(".v-window #details");
  public static final By LATITUDE_INPUT = By.id("latitude");
  public static final By LONGITUDE_INPUT = By.id("longitude");
  public static final By LAT_LON_ACCURACY_INPUT = By.id("latLonAccuracy");
  public static final By ACTIVE_CHECKBOX = By.cssSelector(".v-window #active input");
  public static final By ACTIVE_LABEL = By.cssSelector(".v-window #active label");
  public static final By USER_NAME_INPUT = By.id("userName");
  public static final By USER_ROLE_CHECKBOX_TEXT = By.cssSelector("#userRoles [checked] + label");
  public static final By LIMITED_DISEASE_CHECKBOX_TEXT =
      By.cssSelector("#limitedDiseases [checked] + label");
  public static final By DISCARD_BUTTON = By.id("discard");
  public static final By SAVE_BUTTON_EDIT_USER = By.id("commit");
  public static final By NOTIFICATION_CAPTION_EDIT_USER =
      By.xpath("//div[@class='popupContent']//*[text()='Please check the input data']");
}
