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

public class PreviousHospitalizationPage {
  public static final By DATE_OF_VISIT_OR_ADMISSION_INPUT =
      By.cssSelector(".v-window #admissionDate input");
  public static final By DATE_OF_DISCHARGE_OR_TRANSFER_INPUT =
      By.cssSelector(".v-window #dischargeDate input");
  public static final By REGION_COMBOBOX =
      By.cssSelector(".v-window [location='region'] [role='combobox'] div");
  public static final By DISTRICT_COMBOBOX =
      By.cssSelector(".v-window [location='district'] [role='combobox'] div");
  public static final By COMMUNITY_COMBOBOX =
      By.cssSelector(".v-window [location='community'] [role='combobox'] div");
  public static final By HOSPITAL_COMBOBOX =
      By.cssSelector(".v-window [location='healthFacility'] [role='combobox'] div");
  public static final By ISOLATION_OPTIONS = By.cssSelector(".v-window #isolated label");
  public static final By FACILITY_NAME_DESCRIPTION_INPUT =
      By.cssSelector(".v-window #healthFacilityDetails");
  public static final By REASON_FOR_HOSPITALIZATION_COMBOBOX =
      By.cssSelector(".v-window [location='hospitalizationReason'] [role='combobox'] div");
  public static final By STAY_IN_THE_INTENSIVE_CARE_UNIT_OPTIONS =
      By.cssSelector(".v-window #intensiveCareUnit label");
  public static final By SPECIFY_REASON_INPUT =
      By.cssSelector(".v-window #otherHospitalizationReason");
  public static final By DESCRIPTION_INPUT = By.cssSelector(".v-window #description");
  public static final By START_OF_STAY_DATE_INPUT =
      By.cssSelector(".v-window #intensiveCareUnitStart input");
  public static final By END_OF_STAY_DATE_INPUT =
      By.cssSelector(".v-window #intensiveCareUnitEnd input");
  public static final By DONE_BUTTON = By.cssSelector(".v-window #commit");
}
