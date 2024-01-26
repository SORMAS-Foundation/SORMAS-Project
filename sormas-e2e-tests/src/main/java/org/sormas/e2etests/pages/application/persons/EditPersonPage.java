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

package org.sormas.e2etests.pages.application.persons;

import org.openqa.selenium.By;

public class EditPersonPage {
  public static final By UUID_INPUT = By.cssSelector("#uuid");
  public static final By NEW_CASE_BUTTON = By.cssSelector("div[id^='New case']");
  public static final By USER_INFORMATION =
      By.cssSelector(".v-slot.v-slot-h2.v-slot-vspace-top-none.v-slot-primary");
  public static final By FIRST_NAME_INPUT = By.id("firstName");
  public static final By LAST_NAME_INPUT = By.id("lastName");
  public static final By DATE_OF_BIRTH_YEAR_COMBOBOX = By.cssSelector("#birthdateYYYY input+div");
  public static final By DATE_OF_BIRTH_MONTH_COMBOBOX = By.cssSelector("#birthdateMM input+div");
  public static final By DATE_OF_BIRTH_DAY_COMBOBOX = By.cssSelector("#birthdateDD input+div");
  public static final By SEX_INPUT = By.cssSelector("[id='sex'] input");
  public static final By SEX_COMBOBOX = By.cssSelector("[location='sex'] div[role='combobox'] div");
  public static final By SALUTATION_INPUT = By.cssSelector("[location=salutation] input");
  public static final By SALUTATION_COMBOBOX = By.cssSelector("[location=salutation] input + div");
  public static final By PRESENT_CONDITION_INPUT = By.cssSelector("#presentCondition input");
  public static final By PRESENT_CONDITION_COMBOBOX =
      By.cssSelector("#presentCondition input + div");
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
  public static final By REGION_COMBOBOX = By.cssSelector("#region > div");
  public static final By SECOND_REGION_COMBOBOX = By.xpath("(//div[@id='region']//div)[2]");
  public static final By REGION_INPUT = By.cssSelector("#region > input");
  public static final By DISTRICT_COMBOBOX = By.cssSelector("#district > div");
  public static final By SECOND_DISTRICT_COMBOBOX = By.xpath("(//div[@id='district']//div)[2]");
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
  public static final By BIRTH_NAME_INPUT = By.id("birthName");
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
  public static final By PERSON_DATA_SAVED = By.cssSelector(".v-Notification-caption");
  public static final By LINK_EVENT_BUTTON = By.xpath("//div[@id='Ereignis verkn\u00FCpfen']");
  public static final By PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE =
      By.xpath("//*[contains(text(),'The new event participant was created.')]");
  public static final By SEE_EVENTS_FOR_PERSON =
      By.cssSelector("div#See\\ events\\ for\\ this\\ person");
  public static final By INVALID_DATA_ERROR =
      By.cssSelector(".v-Notification.error.v-Notification-error");
  public static final By ERROR_INDICATOR =
      By.cssSelector(".v-errorindicator.v-errorindicator-info");
  public static final By SEE_SAMPLES_FOR_PERSON_BUTTON = By.id("See samples for this person");
  public static final By SEE_CASES_FOR_PERSON_BUTTON = By.id("See cases for this person");
  public static final By SEE_CASES_FOR_PERSON_BUTTON_DE =
      By.id("F\u00E4lle f\u00FCr diese Person ansehen");
  public static final By SEE_CONTACTS_FOR_PERSON_BUTTON = By.id("See contacts for this person");
  public static final By SEE_CONTACTS_FOR_PERSON_BUTTON_DE =
      By.id("Kontakte f\u00FCr diese Person ansehen");
  public static final By EYE_ICON_EDIT_PERSON =
      By.cssSelector("[class*='v-caption-on-top']>[class*='v-popupview']");
  public static final By GPS_LATITUDE_INPUT_EDIT_PERSON = By.id("latitude");
  public static final By GPS_LONGITUDE_INPUT_EDIT_PERSON = By.id("longitude");
  public static final By MAP_CONTAINER_EDIT_PERSON = By.cssSelector("[class*='leaflet-container']");
  public static final By EDIT_CASES_BUTTON = By.id("edit-case-0");
  public static final By EDIT_CASES_ICON_BUTTON = By.cssSelector("div[id^='edit-case']");
  public static final By VIEW_CASES_ICON_BUTTON = By.cssSelector("div[id^='view-case']");
  public static final By EDIT_CONTACTS_BUTTON = By.id("edit-contact-0");
  public static final By EDIT_CONTACTS_ICON_BUTTON = By.cssSelector("div[id^='edit-contact']");
  public static final By VIEW_CONTACTS_ICON_BUTTON = By.cssSelector("div[id^='view-contact']");
  public static final By EDIT_PARTICIPANT_ICON_BUTTON =
      By.cssSelector("div[id^='edit-participant']");
  public static final By CONFIRM_NAVIGATION_BUTTON = By.cssSelector(".popupContent #actionConfirm");
  public static final By PERSON_INFORMATION_TITLE =
      By.cssSelector("[location='personInformationHeadingLoc']");
  public static final By EVENT_PARTICIPANTS_DATA_TAB =
      By.cssSelector("#tab-events-eventparticipants");
  public static final By NEW_TRAVEL_ENTRY_BUTTON_DE = By.cssSelector("[id='Neue Einreise']");
  public static final By NO_TRAVEL_ENTRY_LABEL_DE =
      By.xpath("//div[text()=\"Es gibt keine Einreisen f\u00FCr diese Person\"]");
  public static final By IMMUNIZATION_ID_LABEL =
      By.xpath(
          "(//div[@class='v-label v-widget bold v-label-bold uppercase v-label-uppercase v-label-undef-w'])[1]");
  public static final By IMMUNIZATION_DISEASE_LABEL =
      By.xpath(
          "(//div[@class='v-label v-widget bold v-label-bold uppercase v-label-uppercase v-label-undef-w'])[2]");
  public static final By MEANS_OF_IMMUNIZATION_LABEL =
      By.xpath(
          "(//div[@location='immunizations']//div[@class=\"v-label v-widget v-label-undef-w\"])[1]");
  public static final By IMMUNIZATION_STATUS_LABEL =
      By.xpath(
          "(//div[@location='immunizations']//div[@class=\"v-label v-widget v-label-undef-w\"])[2]");
  public static final By MANAGEMENT_STATUS_LABEL =
      By.xpath(
          "(//div[@location='immunizations']//div[@class=\"v-label v-widget v-label-undef-w\"])[3]");
  public static final By IMMUNIZATION_PERIOD_LABEL =
      By.xpath(
          "(//div[@location='immunizations']//div[@class=\"v-label v-widget v-label-undef-w\"])[4]");
  public static final By FACILITY_CONTACT_PERSON_FIRST_NAME_CASE_PERSON_INPUT =
      By.cssSelector("#contactPersonFirstName");
  public static final By FACILITY_CONTACT_PERSON_LAST_NAME_CASE_PERSON_INPUT =
      By.cssSelector("#contactPersonLastName");
  public static final By FACILITY_CONTACT_PERSON_PHONE_CASE_PERSON_INPUT =
      By.cssSelector("#contactPersonPhone");
  public static final By FACILITY_CONTACT_PERSON_EMAIL_CASE_PERSON_INPUT =
      By.cssSelector("#contactPersonEmail");
  public static final By TRAVEL_ENTRY_ID_IN_TRAVEL_ENTRY_TAB =
      By.xpath("(//div[@location='travelEntries']//div[contains(@class, 'v-label')])[2]");
  public static final By DISEASE_IN_TRAVEL_ENTRY_TAB =
      By.xpath("(//div[@location='travelEntries']//div[contains(@class, 'v-label')])[4]");
  public static final By DATE_IN_TRAVEL_ENTRY_TAB =
      By.xpath("(//div[@location='travelEntries']//div[contains(@class, 'v-label')])[3]");
  public static final By DESCRIPTION_IN_TRAVEL_ENTRY_TAB =
      By.xpath("(//div[@location='travelEntries']//div[contains(@class, 'v-label')])[5]");
  public static final By GENERAL_COMMENT_FIELD =
      By.xpath(
          "(//textarea[@class='v-textarea v-widget resizable v-textarea-resizable caption-hidden v-textarea-caption-hidden v-has-width' and @id='additionalDetails'])");

  public static By getByPersonUuid(String personUuid) {
    return By.cssSelector("a[title='" + personUuid + "']");
  }

  public static By getByImmunizationUuid(String immunizationUuid) {
    return By.id("edit" + immunizationUuid);
  }

  public static By getByTravelEntryPersonUuid(String personUuid) {
    return By.id(String.format("edit%s", personUuid));
  }
}
