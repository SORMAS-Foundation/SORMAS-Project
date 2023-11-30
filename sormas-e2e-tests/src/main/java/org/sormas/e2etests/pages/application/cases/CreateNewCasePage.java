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
  public static final By LINE_LISTING_DISEASE_COMBOBOX = By.cssSelector("#lineListingDisease div");
  public static final By LINE_LISTING_DISCARD_BUTTON = By.cssSelector(".v-window #actionDiscard");
  public static final By DISEASE_VARIANT_COMBOBOX = By.cssSelector(".v-window #diseaseVariant div");
  public static final By RESPONSIBLE_REGION_COMBOBOX =
      By.cssSelector(".v-window #responsibleRegion div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX =
      By.cssSelector(".v-window #responsibleDistrict div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX =
      By.cssSelector(".v-window #responsibleCommunity div");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector("#typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector("#type div");
  public static final By NEW_CASE_FACILITY_TYPE_COMBOBOX =
      By.cssSelector(".v-window #facilityType div");
  public static final By FACILITY_COMBOBOX = By.cssSelector("#healthFacility div");
  public static final By FACILITY_NAME_COMBOBOX = By.cssSelector("#facility div");
  public static final By PLACE_OF_STAY_HOME =
      By.xpath("//div[@location='facilityOrHomeLoc']//label[contains(text(), 'Home')]");
  public static final By FIRST_NAME_INPUT =
      By.cssSelector(".v-window [location='firstName'] input");
  public static final By FIRST_NAME_NO_POPUP_INPUT = By.cssSelector("#firstName");
  public static final By LAST_NAME_INPUT = By.cssSelector(".v-window [location='lastName'] input");
  public static final By LAST_NAME_NO_POPUP_INPUT = By.cssSelector("#lastName");
  public static final By SEX_COMBOBOX =
      By.cssSelector(".v-window [location='sex'] div[role='combobox'] div");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By CONFIRM_BUTTON_POPUP = By.cssSelector(".popupContent #commit");
  public static final By CASE_ORIGIN_OPTIONS =
      By.cssSelector(".popupContent #caseOrigin .v-select-option");
  public static final By EXTERNAL_ID_INPUT = By.cssSelector(".popupContent #externalID");
  public static final By EPID_NUMBER_INPUT = By.cssSelector(".popupContent #epidNumber");
  public static final By PLACE_OF_STAY =
      By.cssSelector(".popupContent div[location='facilityOrHomeLoc'] span.v-select-option label");
  public static final By REINFECTION_SPAN =
      By.cssSelector(".popupContent div[location='reInfection'] span.v-select-option label");
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
  public static final By CONTACT_CASE_SAVE_BUTTON = By.xpath("//div[@id='commit']");
  public static final By CONTACT_CASE_POPUP_SAVE_BUTTON = By.cssSelector(".popupContent #commit");
  public static final By ENTER_HOME_ADDRESS_CHECKBOX =
      By.cssSelector("[location='enterHomeAddressNow'] span.v-checkbox");
  public static final By CASE_DISEASE_VARIANT_COMBOBOX =
      By.cssSelector(".v-window #diseaseVariant div");
  public static final By PERSON_SEARCH_LOCATOR_BUTTON = By.id("personSearchLoc");
  public static final By UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT =
      By.id("nameUuidExternalIdExternalTokenLike");
  public static final By FIRST_NAME_LIKE_INPUT =
      By.xpath("//div[@class= 'filters-container']//div[contains(@location, 'firstName')]//input");
  public static final By LAST_NAME_LIKE_INPUT =
      By.xpath("//div[@class= 'filters-container']//div[contains(@location, 'lastName')]//input");

  public static final By NAME_UUID_EXTERNAL_ID_TOKEN_LIKE =
      By.id("nameUuidExternalIdExternalTokenLike");
  public static final By PERSON_CASE_WINDOW_SEARCH_CASE_BUTTON = By.id("actionSearch");
  public static final By CREATE_A_NEW_CASE_CONFIRMATION_BUTTON =
      By.xpath("//div[@class='v-select-optiongroup v-widget']/span");
  public static final By PICK_AN_EXISTING_CASE_CONFIRMATION_BUTTON =
      By.xpath("//*[text()='Pick an existing case']");
  public static final By CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON =
      By.xpath("//*[text()='Create a new person']");
  public static final By CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON_DE =
      By.xpath("//*[text()='Eine neue Person anlegen']");
  public static final By SELECT_PERSON_WINDOW_CONFIRM_BUTTON =
      By.xpath("(//div[@class='popupContent']//div[@id='commit'])[2]");
  public static final By PERSON_CASE_WINDOW_SEARCH_FIRST_RESULT_OPTION =
      By.xpath(
          "//div[@class='v-grid v-widget v-has-width']//div[@class='v-grid-tablewrapper']/table/tbody[@class='v-grid-body']/tr[@class='v-grid-row v-grid-row-has-data']");
  public static final By NEW_DOCUMENT_BUTTON = By.id("documentUploadDocument");
  public static final By START_DATA_IMPORT_BUTTON = By.cssSelector("[class='v-button']");
  public static final String CASE_UPLOADED_TEST_FILE = "//div[contains(text(), test_%1$s.%1$s)]";
  public static final By CASE_DOCUMENT_EMPTY_TEXT =
      By.xpath("//div[@location='documents']//div[@class='v-label v-widget v-label-undef-w']");
  public static final By DOWNLOAD_LAST_UPDATED_CASE_DOCUMENT =
      By.xpath(
          "//div[@location='documents']//div[@class='v-button v-widget caption-overflow-label v-button-caption-overflow-label']");
  public static final By DELETE_LAST_UPDATED_CASE_DOCUMENT =
      By.xpath(
          "(//div[@location='documents']//div[@class='v-button v-widget caption-overflow-label v-button-caption-overflow-label'])[2]");
  public static final By ACTION_CONFIRM_POPUP_BUTTON =
      By.cssSelector(".popupContent #actionConfirm");
  public static final By NICKNAME_ATTRIBUTE =
      By.xpath("//span[@class=\"v-captiontext\" and text()=\"Spitzname\"]");
  public static final By PASSPORT_NUMBER_ATTRIBUTE =
      By.xpath("//span[@class=\"v-captiontext\" and text()=\"Reisepassnummer\"]");
  public static final By NATIONAL_HEALTH_ID_ATTRIBUTE =
      By.xpath("//span[@class=\"v-captiontext\" and text()=\"Krankenversicherungsnummer\"]");
  public static final By DIFFERENT_POINT_OF_ENTRY_JURISDICTION =
      By.cssSelector(".popupContent #differentPointOfEntryJurisdiction");
  public static final By POINT_OF_ENTRY_REGION_BUTTON =
      By.cssSelector(".popupContent #pointOfEntryRegion div[role='button']");
  public static final By POINT_OF_ENTRY_DISTRICT_BUTTON =
      By.cssSelector(".popupContent #pointOfEntryDistrict div[role='button']");
  public static final By POINT_OF_ENTRY_COMBOBOX =
      By.cssSelector(".popupContent #pointOfEntry div[role='button'");
  public static final By POINT_OF_ENTRY_DETAILS =
      By.cssSelector(".popupContent #pointOfEntryDetails");
  public static final By DATE_OF_REPORT_NO_POPUP_INPUT = By.cssSelector("#reportDate input");
  public static final By RESPONSIBLE_JURISDICTION_LABEL =
      By.cssSelector("[location='responsibleJurisdictionHeadingLoc'] div");
  public static final By PLACE_OF_STAY_LABEL =
      By.cssSelector("[location='placeOfStayHeadingLoc'] div");
  public static final By DIFFERENT_PLACE_OF_STAY_CHECKBOX_LABEL =
      By.xpath("//*[@id='differentPlaceOfStayJurisdiction']/label");
  public static final By PLACE_OF_STAY_REGION_COMBOBOX = By.cssSelector(".v-window #region div");
  public static final By PLACE_OF_STAY_DISTRICT_COMBOBOX =
      By.cssSelector(".v-window #district div");
  public static final By CREATE_NEW_CASE_CONFIRMATION_BUTTON_DE =
      By.xpath("//*[text()='Neuen Fall erstellen']");
  public static final By SELECT_MATCHING_PERSON_CHECKBOX_DE =
      By.xpath("//label[text()='W\u00E4hlen Sie eine passende Person']");
  public static final By SELECT_EXISTING_CASE_DE =
      By.xpath("//label[text()='Einen vorhandenen Fall w\u00E4hlen']");
  public static final By CATEGORY_SAVED_POPUP = By.cssSelector(".v-Notification-caption");
}
