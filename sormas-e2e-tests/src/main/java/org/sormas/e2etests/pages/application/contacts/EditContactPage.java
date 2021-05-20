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

package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class EditContactPage {
  public static final By UUID_INPUT = By.id("uuid");
  public static final By USER_INFORMATION =
      By.cssSelector(".v-slot.v-slot-h2.v-slot-vspace-top-none.v-slot-primary");
  public static final By REPORT_DATE = By.cssSelector("#reportDateTime input");
  public static final By DISEASE_COMBOBOX = By.cssSelector("#disease input");
  public static final By CASE_ID_IN_EXTERNAL_SYSTEM_INPUT = By.cssSelector("#caseIdExternalSystem");
  public static final By DATE_OF_LAST_CONTACT_INPUT = By.cssSelector("#lastContactDate input");
  public static final By CASE_OR_EVENT_INFORMATION_INPUT =
      By.cssSelector("#caseOrEventInformation");
  public static final By RESPONSIBLE_REGION_COMBOBOX = By.cssSelector("#region input");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX = By.cssSelector("#district input");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX = By.cssSelector("#community input");
  public static final By ADDITIONAL_INFORMATION_OF_THE_TYPE_OF_CONTACT_INPUT =
      By.cssSelector("#contactProximityDetails");
  public static final By RELATIONSHIP_WITH_CASE_COMBOBOX = By.cssSelector("#relationToCase input");
  public static final By DESCRIPTION_OF_HOW_CONTACT_TOOK_PLACE_INPUT =
      By.cssSelector("#description");
  public static final By RETURNING_TRAVELER_OPTIONS = By.cssSelector("#returningTraveler label");
  public static final By TYPE_OF_CONTACT_OPTIONS = By.cssSelector("#contactProximity label");
  public static final By CONTACT_CATEGORY_OPTIONS = By.cssSelector("#contactCategory label");
}
