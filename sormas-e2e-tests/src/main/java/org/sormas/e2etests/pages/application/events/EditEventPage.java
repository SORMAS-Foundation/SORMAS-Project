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

package org.sormas.e2etests.pages.application.events;

import org.openqa.selenium.By;

public class EditEventPage {
  public static final By EVENT_PARTICIPANTS_TAB =
      By.cssSelector("#tab-events-eventparticipants span");
  public static final By UUID_INPUT = By.id("uuid");
  public static final By TITLE_INPUT = By.cssSelector("#eventTitle");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By DISCARD_BUTTON = By.id("discard");
  public static final By EVENT_STATUS_OPTIONS =
      By.cssSelector("#eventStatus .v-select-option label");
  public static final By SELECTED_EVENT_STATUS = By.cssSelector("#eventStatus input:checked");
  public static final By RISK_LEVEL_COMBOBOX = By.cssSelector(" #riskLevel div");
  public static final By RISK_LEVEL_INPUT = By.cssSelector(" #riskLevel input");
  public static final By EVENT_MANAGEMENT_STATUS_OPTIONS =
      By.cssSelector(" #eventManagementStatus .v-select-option label");
  public static final By EVENT_MANAGEMENT_STATUS_INPUT =
      By.cssSelector("#eventManagementStatus input:checked");
  public static final By MULTI_DAY_EVENT_CHECKBOX = By.cssSelector(" #multiDayEvent input");
  public static final By START_DATA_INPUT = By.cssSelector(" #startDate input");
  public static final By SIGNAL_EVOLUTION_DATE_INPUT = By.cssSelector("#evolutionDate input");
  public static final By EVENT_INVESTIGATION_STATUS_OPTIONS =
      By.cssSelector("#eventInvestigationStatus label");
  public static final By EVENT_INVESTIGATION_STATUS_INPUT =
      By.cssSelector("#eventInvestigationStatus input:checked");
  public static final By DISEASE_COMBOBOX = By.cssSelector("#disease div");
  public static final By DISEASE_INPUT = By.cssSelector("#disease input");
  public static final By EXTERNAL_ID_INPUT = By.cssSelector(" #externalId");
  public static final By INTERNAL_ID_INPUT = By.cssSelector(" #internalId");
  public static final By EXTERNAL_TOKEN_INPUT = By.cssSelector(" #externalToken");
  public static final By DESCRIPTION_INPUT = By.cssSelector(" #eventDesc");
  public static final By SOURCE_TYPE_COMBOBOX = By.cssSelector(" #srcType div");
  public static final By SOURCE_TYPE_INPUT = By.cssSelector(" #srcType input");
  public static final By TYPE_OF_PLACE_COMBOBOX = By.cssSelector(" #typeOfPlace div");
  public static final By TYPE_OF_PLACE_INPUT = By.cssSelector(" #typeOfPlace input");
  public static final By COUNTRY_COMBOBOX = By.cssSelector(" #country div");
  public static final By REGION_COMBOBOX = By.cssSelector(" #region div");
  public static final By DISTRICT_COMBOBOX = By.cssSelector(" #district div");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector(" #community div");
  public static final By STREET_INPUT = By.cssSelector(" #street");
  public static final By HOUSE_NUMBER_INPUT = By.cssSelector(" #houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT = By.cssSelector("#additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.cssSelector("#postalCode");
  public static final By AREA_TYPE_COMBOBOX = By.cssSelector("#areaType div");
  public static final By COMMUNITY_CONTACT_PERSON_COMBOBOX = By.cssSelector("#details");
  public static final By CITY_INPUT = By.cssSelector("#city");
  public static final By GPS_LATITUDE_INPUT = By.cssSelector("#latitude");
  public static final By GPS_LONGITUDE_INPUT = By.cssSelector(" #longitude");
  public static final By PRIMARY_MODE_OF_TRANSMISSION = By.cssSelector(" #diseaseTransmissionMode");
  public static final By NOSOCOMIAL = By.cssSelector(" #nosocomial");
  public static final By EVENT_INVESTIGATION_START_DATE =
      By.cssSelector(" #eventInvestigationStartDate");
  public static final By EVENT_INVESTIGATION_END_DATE =
      By.cssSelector(" #eventInvestigationEndDate");
  public static final By SOURCE_MEDIA_WEBSITE = By.cssSelector(" #srcMediaWebsite");
  public static final By SOURCE_MEDIA_NAME = By.cssSelector(" #srcMediaName");
  public static final By SOURCE_MEDIA_DETAILS = By.cssSelector(" #srcMediaDetails");
  public static final By SOURCE_FIRST_NAME = By.cssSelector(" #srcFirstName");
  public static final By SOURCE_LAST_NAME = By.cssSelector("#srcLastName");
  public static final By SOURCE_TEL_NO = By.cssSelector(" #srcTelNo");
  public static final By SOURCE_EMAIL = By.cssSelector("#srcEmail");
  public static final By SOURCE_INSTITUTIONAL_PARTNER =
      By.cssSelector("#srcInstitutionalPartnerType div");
  public static final By SOURCE_INSTITUTIONAL_PARTNER_DETAILS =
      By.cssSelector(" #srcInstitutionalPartnerTypeDetails");
  public static final By SPECIFY_OTHER_EVENT_PLACE =
      By.cssSelector(".popupContent #typeOfPlaceText");
  public static final By MEANS_OF_TRANSPORT = By.cssSelector(" #meansOfTransport div");
  public static final By CONNECTION_NUMBER = By.cssSelector(" #connectionNumber");
  public static final By TRAVEL_DATE = By.cssSelector(" #travelDate");
  public static final By FACILITY_CATEGORY = By.cssSelector(" #typeGroup div");
  public static final By FACILITY_TYPE = By.cssSelector(" #facilityType div");
  public static final By FACILITY = By.cssSelector(" #facility div");
  public static final By MEANS_OF_TRANSPORT_DETAILS = By.cssSelector(" #meansOfTransportDetails");
  public static final By DISEASE_NAME = By.cssSelector(" #diseaseDetails");
  public static final By REPORT_DATE = By.cssSelector(" #reportDateTime input");
  public static final By EVENT_SAVED_MESSAGE = By.xpath("//*[contains(text(),'Event saved')]");
  public static final By EVENT_DATA_SAVED_MESSAGE =
      By.xpath("//*[contains(text(),'Event data saved')]");
}
