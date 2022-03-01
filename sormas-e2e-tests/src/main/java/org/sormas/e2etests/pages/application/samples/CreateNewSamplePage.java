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

package org.sormas.e2etests.pages.application.samples;

import org.openqa.selenium.By;

public class CreateNewSamplePage {
  public static final By SAMPLE_PURPOSE_OPTIONS =
      By.cssSelector(".popupContent #samplePurpose .v-select-option");
  public static final By SAMPLE_UUID = By.cssSelector("[class='popupContent'] [id='uuid']");
  public static final By DATE_SAMPLE_COLLECTED = By.cssSelector("[id='sampleDateTime_date'] input");
  public static final By COLLECTED_DATE_TIME_INPUT =
      By.cssSelector("[id='sampleDateTime_time'] input");
  public static final By COLLECTED_DATE_TIME_COMBOBOX =
      By.cssSelector("[id='sampleDateTime_time'] [class='v-filterselect-button']");
  public static final By SAMPLE_TYPE_INPUT = By.cssSelector("[id='sampleMaterial'] input");
  public static final By SAMPLE_TYPE_COMBOBOX =
      By.cssSelector("[id='sampleMaterial'] [class='v-filterselect-button']");
  public static final By REASON_FOR_SAMPLING_TESTING_INPUT =
      By.cssSelector("[id='samplingReason'] input");
  public static final By REASON_FOR_SAMPLING_TESTING_COMBOBOX =
      By.cssSelector("[id='samplingReason'] [class='v-filterselect-button']");
  public static final By FIELD_SAMPLE_ID_INPUT = By.cssSelector("[id='fieldSampleID']");
  public static final By LABORATORY_COMBOBOX =
      By.cssSelector("[id='lab'] [class='v-filterselect-button']");
  public static final By LABORATORY_INPUT = By.cssSelector("[id='lab'] input");
  public static final By LABORATORY_NAME_INPUT = By.cssSelector("[id='labDetails']");
  public static final By LABORATORY_NAME_POPUP_INPUT =
      By.cssSelector(".popupContent [id='labDetails']");
  public static final By RECEIVED_OPTION_BUTTON = By.cssSelector("[id='received'] label");
  public static final By DATE_SAMPLE_RECEIVED = By.cssSelector("[id='receivedDate'] input");
  public static final By SPECIMEN_CONDITION_COMBOBOX =
      By.cssSelector("[id='specimenCondition'] [class='v-filterselect-button']");
  public static final By SPECIMEN_CONDITION_INPUT =
      By.cssSelector("[id='specimenCondition'] input");
  public static final By LAB_SAMPLE_ID_INPUT = By.cssSelector("[id='labSampleID']");
  public static final By COMMENT_AREA_INPUT = By.cssSelector("[id='comment']");
  public static final By SAMPLE_TEST_RESULT_BUTTON =
      By.cssSelector("[id='sampleIncludeTestOnCreation'] label");
  public static final By DATE_TEST_REPORT =
      By.cssSelector("[class='popupContent'] [id='reportDate'] input");
  public static final By TYPE_OF_TEST_COMBOBOX =
      By.cssSelector("[id='testType'] [class='v-filterselect-button']");
  public static final By TYPE_OF_TEST_INPUT = By.cssSelector("[id='testType'] input");
  public static final By TESTED_DISEASE_COMBOBOX =
      By.cssSelector("[id='testedDisease'] [class='v-filterselect-button']");
  public static final By TESTED_DISEASE_INPUT = By.cssSelector("[id='testedDisease'] input");
  public static final By DATE_OF_RESULT = By.cssSelector("[id='testDateTime_date'] input");
  public static final By TIME_OF_RESULT_INPUT = By.cssSelector("[id='testDateTime_time'] input");
  public static final By TIME_OF_RESULT_COMBOBOX =
      By.cssSelector("[id='testDateTime_time'] [class='v-filterselect-button']");
  public static final By RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS =
      By.cssSelector(".popupContent #testResultVerified .v-select-option");
  public static final By TEST_RESULTS_COMMENT_AREA_INPUT = By.cssSelector("[id='testResultText']");
  public static final By SAVE_SAMPLE_BUTTON =
      By.cssSelector("[class='popupContent'] [id='commit']");
  public static final By PATHOGEN_TEST_RESULT_COMBOBOX =
      By.cssSelector("[id='testResult'] [class='v-filterselect-button']");
  public static final By PATHOGEN_TEST_RESULT_INPUT = By.cssSelector("[id='testResult'] input");
  public static final By PATHOGEN_LABORATORY_COMBOBOX =
      By.cssSelector("[class='v-panel v-widget'] [location='lab'] [class='v-filterselect-button']");
  public static final By PATHOGEN_LABORATORY_INPUT =
      By.cssSelector(
          "[location='lab'] [class='v-filterselect v-widget v-required v-filterselect-required v-has-width'] input");
  public static final By REQUEST_PATHOGEN_OPTION_BUTTON =
      By.cssSelector("[id='pathogenTestingRequested'] label");
  public static final By ANTIGEN_DETECTION_TEST_OPTION_BUTTON =
      By.xpath("//*[@id=\"requestedPathogenTests\"]/span[2]");
  public static final By ISOLATION_TEST_OPTION_BUTTON =
      By.xpath("//*[@id=\"requestedPathogenTests\"]/span[6]");
  public static final By PCR_RTP_PCR_TEST_OPTION_BUTTON =
      By.xpath("//*[@id=\"requestedPathogenTests\"]/span[15]/label");
  public static final By FINAL_LABORATORY_RESULT_COMBOBOX =
      By.cssSelector("[id='pathogenTestResult'] [class='v-filterselect-button']");
}
