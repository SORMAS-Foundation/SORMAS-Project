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
  public static final By EVENT_STATUS_OPTIONS =
      By.cssSelector("#eventStatus .v-select-option label");
  public static final By RISK_LEVEL_COMBOBOX = By.cssSelector(".popupContent #riskLevel div");
  public static final By EVENT_MANAGEMENT_STATUS_OPTIONS =
      By.cssSelector(".popupContent #eventManagementStatus .v-select-option label");
  public static final By MULTI_DAY_EVENT_CHECKBOX =
      By.cssSelector(".popupContent #multiDayEvent input");
  public static final By START_DATA_INPUT = By.cssSelector(".popupContent #startDate input");
  public static final By SIGNAL_EVOLUTION_DATE_INPUT =
      By.cssSelector(".popupContent #evolutionDate input");
  public static final By EVENT_INVESTIGATION_STATUS_OPTIONS =
      By.cssSelector(".popupContent #eventInvestigationStatus label");
  public static final By DISEASE_INPUT = By.cssSelector(".popupContent #disease div");
  public static final By EXTERNAL_ID_INPUT = By.cssSelector(".popupContent #externalId");
  public static final By INTERNAL_ID_INPUT = By.cssSelector(".popupContent #internalId");
  public static final By EXTERNAL_TOKEN_INPUT = By.cssSelector(".popupContent #externalToken");
  public static final By DESCRIPTION_INPUT = By.cssSelector(".popupContent #eventDesc");
  public static final By SOURCE_TYPE_COMBOBOX = By.cssSelector(".popupContent #srcType div");
  public static final By TYPE_OF_PLACE_COMBOBOX = By.cssSelector(".popupContent #typeOfPlace div");
  public static final By COUNTRY_COMBOBOX = By.cssSelector(".popupContent #country div");
  public static final By REGION_COMBOBOX = By.cssSelector(".popupContent #region div");
  public static final By DISTRICT_COMBOBOX = By.cssSelector(".popupContent #district div");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector(".popupContent #community div");
  public static final By STREET_INPUT = By.cssSelector(".popupContent #street");
  public static final By HOUSE_NUMBER_INPUT = By.cssSelector(".popupContent #houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT =
      By.cssSelector(".popupContent #additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.cssSelector(".popupContent #postalCode");
  public static final By AREA_TYPE_COMBOBOX = By.cssSelector(".popupContent #areaType div");
  public static final By COMMUNITY_CONTACT_PERSON_COMBOBOX =
      By.cssSelector(".popupContent #details");
  public static final By CITY_INPUT = By.cssSelector(".popupContent #city");
  public static final By GPS_LATITUDE_INPUT = By.cssSelector(".popupContent #latitude");
  public static final By GPS_LONGITUDE_INPUT = By.cssSelector(".popupContent #longitude");
}
