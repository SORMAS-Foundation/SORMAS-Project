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

package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class EpidemiologicalDataCasePage {
  public static final By EXPOSURE_DETAILS_KNOWN =
      By.cssSelector("#exposureDetailsKnown span:nth-child(1)");
  public static final By EXPOSURE_DETAILS_NEW_ENTRY = By.cssSelector("#exposures #actionNewEntry");
  public static final By EXPOSURE_UUID = By.cssSelector(".v-window #uuid");
  public static final By START_OF_EXPOSURE = By.cssSelector(".v-window #startDate");
  public static final By END_OF_EXPOSURE = By.cssSelector(".v-window #endDate");
  public static final By EXPOSURE_DESCRIPTION = By.cssSelector(".v-window #description");
  public static final By TYPE_OF_ACTIVITY = By.cssSelector(".v-window #exposureType");
  public static final By EXPOSURE_DETAILS_ROLE = By.cssSelector(".v-window div#exposureRole");
  public static final By RISK_AREA = By.cssSelector(".v-window #riskArea .v-select-option");
  public static final By INDOORS = By.cssSelector(".v-window #indoors .v-select-option");
  public static final By OUTDOORS = By.cssSelector(".v-window #outdoors .v-select-option");
  public static final By WEARING_MASK = By.cssSelector(".v-window #wearingMask .v-select-option");
  public static final By WEARING_PPE = By.cssSelector(".v-window #wearingPpe .v-select-option");
  public static final By OTHER_PROTECTIVE_MEASURES =
      By.cssSelector(".v-window #otherProtectiveMeasures .v-select-option");
  public static final By SHORT_DISTANCE =
      By.cssSelector(".v-window #shortDistance .v-select-option");
  public static final By LONG_FACE_TO_FACE_CONTACT =
      By.cssSelector(".v-window #longFaceToFaceContact .v-select-option");
  public static final By ANIMAL_MARKET = By.cssSelector(".v-window #animalMarket .v-select-option");
  public static final By PERCUTANEOUS = By.cssSelector(".v-window #percutaneous .v-select-option");
  public static final By CONTACT_TO_BODY_FLUIDS =
      By.cssSelector(".v-window #contactToBodyFluids .v-select-option");
  public static final By HANDLING_SAMPLES =
      By.cssSelector(".v-window #handlingSamples .v-select-option");
  public static final By CONTACT_TO_SOURCE_CASE = By.cssSelector(".v-window #contactToCase");
  public static final By TYPE_OF_PLACE = By.cssSelector(" .v-window #typeOfPlace");
  public static final By CONTINENT = By.cssSelector(".v-window #continent");
  public static final By SUBCONTINENT = By.cssSelector(".v-window #subcontinent");
  public static final By COUNTRY = By.cssSelector(".v-window #country");
  public static final By EXPOSURE_REGION = By.cssSelector(".v-window #region");
  public static final By DISTRICT = By.cssSelector(".v-window #district");
  public static final By COMMUNITY = By.cssSelector(".v-window #community");
  public static final By STREET = By.cssSelector(".v-window #street");
  public static final By HOUSE_NUMBER = By.cssSelector(".v-window #houseNumber");
  public static final By ADDITIONAL_INFORMATION =
      By.cssSelector(".v-window #additionalInformation");
  public static final By POSTAL_CODE = By.cssSelector(".v-window #postalCode");
  public static final By CITY = By.cssSelector(".v-window #city");
  public static final By AREA_TYPE = By.cssSelector(".v-window #areaType");
  public static final By COMMUNITY_CONTACT_PERSON = By.cssSelector(".v-window #details");
  public static final By DONE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By DISCARD_BUTTON = By.cssSelector(".v-window #discard");

  public static final By ACTIVITY_DETAILS_KNOWN =
      By.cssSelector("#activityAsCaseDetailsKnown span:nth-child(1)v");
  public static final By ACTIVITY_DETAILS_NEW_ENTRY =
      By.cssSelector("#activitiesAsCase #actionNewEntry");
  public static final By AcC_START_OF_ACTIVITY = By.cssSelector(".v-window #startDate");
  public static final By AcC_END_OF_ACTIVITY = By.cssSelector(".v-window #endDate");
  public static final By AcC_DESCRIPTION = By.cssSelector(".v-window #description");
  public static final By AcC_ACTIVITY_AS_CASE_TYPE =
      By.cssSelector(".v-window #activityAsCaseType");
  public static final By AcC_ROLE = By.cssSelector(".v-window #role");
  public static final By AcC_TYPE_OF_PLACE = By.cssSelector(".v-window #typeOfPlace");
  public static final By AcC_CONTINENT = By.cssSelector(".v-window #continent");
  public static final By AcC_SUBCONTINENT = By.cssSelector(".v-window #subcontinent");
  public static final By AcC_COUNTRY = By.cssSelector(".v-window #country");
  public static final By AcC_REGION = By.cssSelector(".v-window #region");
  public static final By AcC_DISTRICT = By.cssSelector(".v-window #district");
  public static final By AcC_COMMUNITY = By.cssSelector(".v-window #community");
  public static final By AcC_STREET = By.cssSelector(".v-window #street");
  public static final By AcC_HOUSE_NUMBER = By.cssSelector(".v-window #houseNumber");
  public static final By AcC_ADDITIONAL_INFORMATION =
      By.cssSelector(".v-window #additionalInformation");
  public static final By AcC_POSTAL_CODE = By.cssSelector(".v-window #postalCode");
  public static final By AcC_CITY = By.cssSelector(".v-window #city");
  public static final By AcC_AREA_TYPE = By.cssSelector(".v-window #areaType");
  public static final By AcC_DETAILS = By.cssSelector(".v-window #details");
  public static final By AcC_DONE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By AcC_DISCARD_BUTTON = By.cssSelector(".v-window #discard");

  public static final By RESIDING_AREA_WITH_RISK =
      By.cssSelector("#highTransmissionRiskArea span:nth-child(1)");

  public static final By LARGE_OUTBREAKS_AREA =
      By.cssSelector("#largeOutbreaksArea span:nth-child(1)");

  public static final By SAVE_BUTTON_EPIDEMIOLOGICAL_DATA = By.cssSelector("#commit");
  public static final By DISCARD_BUTTON_EPIDEMIOLOGICAL_DATA = By.cssSelector("#commit");
}
