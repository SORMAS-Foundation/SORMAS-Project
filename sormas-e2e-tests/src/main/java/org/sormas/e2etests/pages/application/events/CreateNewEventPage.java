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

public class CreateNewEventPage {
  public static final By TITLE_INPUT = By.cssSelector(".popupContent #eventTitle");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By DISCARD_BUTTON = By.id("discard");
  public static final By EVENT_STATUS_OPTIONS =
      By.cssSelector(".popupContent #eventStatus .v-select-option label");
  public static final By RISK_LEVEL_COMBOBOX = By.cssSelector(".popupContent #riskLevel div");
  public static final By EVENT_MANAGEMENT_STATUS_OPTIONS =
      By.cssSelector(".popupContent #eventManagementStatus .v-select-option label");
  public static final By START_DATA_INPUT = By.cssSelector(".popupContent #startDate input");
  public static final By EVENT_INVESTIGATION_STATUS_OPTIONS =
      By.cssSelector(".popupContent #eventInvestigationStatus label");
  public static final By DISEASE_COMBOBOX = By.cssSelector(".popupContent #disease div");
  public static final By SOURCE_TYPE_COMBOBOX = By.cssSelector(".popupContent #srcType div");
  public static final By TYPE_OF_PLACE_COMBOBOX = By.cssSelector(".popupContent #typeOfPlace div");
  public static final By PRIMARY_MODE_OF_TRANSMISSION_INPUT =
      By.cssSelector(".popupContent #diseaseTransmissionMode");
  public static final By NOSOCOMIAL_INPUT = By.cssSelector(".popupContent #nosocomial");
  public static final By EVENT_INVESTIGATION_START_DATE_INPUT =
      By.cssSelector(".popupContent #eventInvestigationStartDate");
  public static final By EVENT_INVESTIGATION_END_DATE_INPUT =
      By.cssSelector(".popupContent #eventInvestigationEndDate");
  public static final By SOURCE_MEDIA_WEBSITE_INPUT =
      By.cssSelector(".popupContent #srcMediaWebsite");
  public static final By SOURCE_MEDIA_NAME_INPUT = By.cssSelector(".popupContent #srcMediaName");
  public static final By SOURCE_MEDIA_DETAILS_INPUT =
      By.cssSelector(".popupContent #srcMediaDetails");
  public static final By SOURCE_FIRST_NAME_INPUT = By.cssSelector(".popupContent #srcFirstName");
  public static final By SOURCE_LAST_NAME_INPUT = By.cssSelector(".popupContent #srcLastName");
  public static final By SOURCE_TEL_NO_INPUT = By.cssSelector(".popupContent #srcTelNo");
  public static final By SOURCE_EMAIL_INPUT = By.cssSelector(".popupContent #srcEmail");
  public static final By SOURCE_INSTITUTIONAL_PARTNER_COMBOBOX =
      By.cssSelector(".popupContent #srcInstitutionalPartnerType div");
  public static final By SOURCE_INSTITUTIONAL_PARTNER_DETAILS_INPUT =
      By.cssSelector(".popupContent #srcInstitutionalPartnerTypeDetails");
  public static final By SPECIFY_OTHER_EVENT_PLACE_INPUT =
      By.cssSelector(".popupContent #typeOfPlaceText");
  public static final By MEANS_OF_TRANSPORT_COMBOBOX =
      By.cssSelector(".popupContent #meansOfTransport div");
  public static final By CONNECTION_NUMBER_INPUT =
      By.cssSelector(".popupContent #connectionNumber");
  public static final By TRAVEL_DATE_INPUT = By.cssSelector(".popupContent #travelDate");
  public static final By FACILITY_CATEGORY_COMBOBOX =
      By.cssSelector(".popupContent #typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector(".popupContent #facilityType div");
  public static final By FACILITY_COMBOBOX = By.cssSelector(".popupContent #facility div");
  public static final By MEANS_OF_TRANSPORT_DETAILS_INPUT =
      By.cssSelector(".popupContent #meansOfTransportDetails");
  public static final By DISEASE_NAME_INPUT = By.cssSelector(".popupContent #diseaseDetails");
  public static final By REPORT_DATE_INPUT = By.cssSelector(".popupContent #reportDateTime input");
  public static final By NEW_EVENT_CREATED_MESSAGE =
      By.xpath("//*[contains(text(),'New event created')]");
}
