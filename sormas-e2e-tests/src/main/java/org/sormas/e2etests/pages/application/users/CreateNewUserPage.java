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

public class CreateNewUserPage {

  public static final By FIRST_NAME_OF_USER_INPUT = By.id("firstName");
  public static final By LAST_NAME_OF_USER_INPUT = By.id("lastName");
  public static final By EMAIL_ADDRESS_INPUT = By.id("userEmail");
  public static final By PHONE_INPUT = By.id("phone");
  public static final By COUNTRY_COMBOBOX = By.cssSelector(".v-window #country div");
  public static final By REGION_COMBOBOX = By.cssSelector(".v-window #region div");
  public static final By SECOND_REGION_COMBOBOX =
      By.xpath(
          "//div[@tabindex='-1']/div/div/div/div[@location='region']//div[@id='region']/div[@class='v-filterselect-button']");
  public static final By DISTRICT_COMBOBOX = By.cssSelector(".v-window #district div");
  public static final By SECOND_DISTRICT_COMBOBOX =
      By.xpath(
          "//div[@tabindex='-1']/div/div/div/div[@location='district']//div[@id='district']/div[@class='v-filterselect-button']\n");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector("#community div");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector("#typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector("#facilityType div");
  public static final By FACILITY_COMBOBOX = By.cssSelector("#facility div");
  public static final By STREET_INPUT = By.id("street");
  public static final By HOUSE_NUMBER_INPUT = By.id("houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT = By.id("additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.id("postalCode");
  public static final By CITY_INPUT = By.id("city");
  public static final By AREA_TYPE_COMBOBOX = By.cssSelector("#areaType div");
  public static final By LANGUAGE_COMBOBOX = By.cssSelector("#language div");
  public static final By FACILITY_NAME_DESCRIPTION = By.id("facilityDetails");
  public static final By LATITUDE_INPUT = By.id("latitude");
  public static final By LONGITUDE_INPUT = By.id("longitude");
  public static final By LAT_LON_ACCURACY_INPUT = By.id("latLonAccuracy");
  public static final By ACTIVE_CHECKBOX = By.cssSelector(".v-window #active label");
  public static final By USER_NAME_INPUT = By.id("userName");
  public static final By USER_ROLE_CHECKBOX = By.cssSelector("#userRoles label");
  public static final By LIMITED_DISEASE_COMBOBOX = By.cssSelector("#limitedDisease > div");
  public static final By DISCARD_BUTTON = By.id("discard");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By PASSWORD_FIELD =
      By.xpath(
          "//div[contains(@class, 'popupContent')]//div[@class='v-label v-widget h2 v-label-h2 v-label-undef-w']");
  public static final By CLOSE_DIALOG_BUTTON = By.className("v-window-closebox");
  public static final By USER_INPUT_SEARCH = By.id("search");
  public static final By ENABLE_BULK_ACTIONS_VALUES = By.id("bulkActions-3");
  public static final By DISABLE_BULK_ACTIONS_VALUES = By.id("bulkActions-4");
  public static final By CONFIRM_POP_UP = By.id("actionConfirm");
  public static final By CLOSE_POP_UP = By.id("actionClose");
  public static final By AMOUNT_OF_CHOSEN_USERS =
      By.xpath(
          "//div[@class='v-label v-widget v-label-undef-w bold v-label-bold vspace-top-none v-label-vspace-top-none align-right v-label-align-right']");
  public static final By AMOUNT_ACTIVE_INACTIVE_USERS =
      By.xpath(
          "//div[@class='v-label v-widget bold v-label-bold vspace-top-none v-label-vspace-top-none align-right v-label-align-right v-label-undef-w']");
  public static final By ENABLE_DISABLE_CONFIRMATION_POPUP =
      By.xpath("//div[@class='popupContent']/div[@class='gwt-HTML']");
  public static final By USERS_COUNTER =
      By.xpath("//div[text()='No. of users:']//following::div/div[contains(@class,'bold')]");
  public static final By ACTIVE_USER_COMBOBOX =
      By.cssSelector("[id='active'] [class='v-filterselect-button']");
  public static final By SURVEILLANCE_REGION =
      By.xpath("(//div[@location='region']//div[@class='v-captionwrapper'])[2]/div[2]/div");
  public static final By SURVEILLANCE_DISTRICT =
      By.xpath("(//div[@location='district']//div[@class='v-captionwrapper'])[2]/div[2]/div");
  public static final By USER_ROLE_COMBOBOX = By.cssSelector("[id='userRoles'] div");
  public static final By REGION_FILTER_COMBOBOX = By.cssSelector("#region div");
  public static final By TABLE_USER_NAME = By.xpath("//table//td[5]");

  public static By getTableRowByIndex(int index) {
    return By.xpath(
        String.format("//label[text()='Selects row number %s.']//parent::span/input", index));
  }
}
