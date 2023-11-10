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
  public static final By DATE_SAMPLE_SEND_INPUT_FIELD =
      By.xpath("//div[@id='shipmentDate' and not(contains(@class, 'v-required'))]");
  public static final By DATE_SAMPLE_RECEIVED_INPUT_FIELD =
      By.xpath("//div[@id='receivedDate' and not(contains(@class, 'v-required'))]");
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
  public static final By SAVE_SAMPLE_WITH_PATHOGEN_TEST_BUTTON =
      By.xpath("(//div[@class='popupContent']//div[@id='commit'])[2]");
  public static final By SAVE_EDIT_SAMPLE_BUTTON = By.cssSelector("[id='commit']");
  public static final By PATHOGEN_TEST_RESULT_COMBOBOX =
      By.cssSelector("[id='testResult'] [class='v-filterselect-button']");
  public static final By PATHOGEN_TEST_RESULT_INPUT = By.cssSelector("[id='testResult'] input");
  public static final By VIA_DEMIS_CHECKBOX = By.cssSelector("#viaLims > input");
  public static final By PATHOGEN_LABORATORY_COMBOBOX =
      By.cssSelector("[class='v-panel v-widget'] [location='lab'] [class='v-filterselect-button']");
  public static final By PATHOGEN_LABORATORY_INPUT =
      By.cssSelector("[location='lab'] [class='v-filterselect v-widget v-has-width'] input");
  public static final By REQUEST_PATHOGEN_OPTION_BUTTON =
      By.cssSelector("[id='pathogenTestingRequested'] label");
  public static final By ANTIGEN_DETECTION_TEST_OPTION_BUTTON =
      By.xpath("//label[text()='Antibody detection']");
  public static final By ISOLATION_TEST_OPTION_BUTTON = By.xpath("//label[text()='Isolation']");
  public static final By PCR_RTP_PCR_TEST_OPTION_BUTTON =
      By.xpath("//label[text()='PCR / RT-PCR']");
  public static final By FINAL_LABORATORY_RESULT_COMBOBOX =
      By.cssSelector("[id='pathogenTestResult'] [class='v-filterselect-button']");
  public static final By ADD_PATHOGEN_TEST_BUTTON = By.xpath("//div[@class='v-button v-widget']");
  public static final By HAEMOGLOBIN_IN_URINE_COMBOBOX =
      By.cssSelector("[id='haemoglobinuria'] [class='v-filterselect-button']");
  public static final By HAEMOGLOBIN_IN_URINE_INPUT =
      By.cssSelector("[id='haemoglobinuria'] input");
  public static final By PROTEIN_IN_URINE_COMBOBOX =
      By.cssSelector("[id='proteinuria'] [class='v-filterselect-button']");
  public static final By PROTEIN_IN_URINE_INPUT = By.cssSelector("[id='proteinuria'] input");
  public static final By CELLS_IN_URINE_COMBOBOX =
      By.cssSelector("[id='hematuria'] [class='v-filterselect-button']");
  public static final By CELLS_IN_URINE_INPUT = By.cssSelector("[id='hematuria'] input");
  public static final By PH_INPUT = By.cssSelector("[id='arterialVenousGasPH']");
  public static final By PCO2_INPUT = By.cssSelector("[id='arterialVenousGasPco2']");
  public static final By PAO2_INPUT = By.cssSelector("[id='arterialVenousGasPao2']");
  public static final By HCO3_INPUT = By.cssSelector("[id='arterialVenousGasHco3']");
  public static final By OXYGEN_INPUT = By.cssSelector("[id='gasOxygenTherapy']");
  public static final By SGPT_INPUT = By.cssSelector("[id='altSgpt']");
  public static final By TOTAL_BILIRUBIN_INPUT = By.cssSelector("[id='totalBilirubin']");
  public static final By SGOT_INPUT = By.cssSelector("[id='astSgot']");
  public static final By CONJ_BILIRUBIN_INPUT = By.cssSelector("[id='conjBilirubin']");
  public static final By CREATININE_INPUT = By.cssSelector("[id='creatinine']");
  public static final By WBC_INPUT = By.cssSelector("[id='wbcCount']");
  public static final By POTASSIUM_INPUT = By.xpath("(//div[@location='potassium'])//input");
  public static final By PLATELETS_INPUT = By.cssSelector("[id='platelets']");
  public static final By UREA_INPUT = By.cssSelector("[id='urea']");
  public static final By PROTHROMBIN_INPUT = By.cssSelector("[id='prothrombinTime']");
  public static final By HAEMOGLOBIN_INPUT = By.cssSelector("[id='haemoglobin']");
  public static final By OTHER_TESTS_INPUT = By.cssSelector("[id='otherTestResults']");
  public static final By PATHOGEN_CARD_TYPE_OF_TEST =
      By.xpath(
          "//div[@class='v-slot v-slot-side-component']//div[@class='v-label v-widget bold v-label-bold uppercase v-label-uppercase v-label-undef-w']");
  public static final By PATHOGEN_CARD_TEST_RESULT_COMMENT =
      By.xpath(
          "//div[@class='v-slot v-slot-side-component']//div[@class='v-label v-widget v-has-width']");
  public static final By PATHOGEN_CARD_DISEASE =
      By.xpath(
          "//div[@class='v-slot v-slot-side-component']//div[@class='v-label v-widget v-label-undef-w']");
  public static final By PATHOGEN_CARD_DATE_OF_RESULT =
      By.xpath(
          "//div[@class='v-slot v-slot-side-component']//div[@class='v-label v-widget align-right v-label-align-right v-label-undef-w']");
  public static final By PATHOGEN_CARD_TEST_RESULT =
      By.xpath(
          "//div[@class='v-slot v-slot-side-component']//div[@class='v-label v-widget bold v-label-bold uppercase v-label-uppercase critical v-label-critical v-label-undef-w']");
  public static final By NEW_TEST_RESULTS_BUTTON_FOR_PATHOGEN_TESTS =
      By.cssSelector("[id='New test result']");
  public static final By DATE_AND_TIME_OF_RESULTS =
      By.xpath("//div[contains(text(),'Date and time of result:')]");
  public static final By UPDATE_CASE_DISEASE_VARIANT =
      By.xpath("//*[text()='Update case disease variant']");
  public static final By DATE_AND_TIME_OF_RESULTS_INPUT_FIELD =
      By.xpath("//*[@id='testDateTime_date' and not(contains(@class, 'v-required'))]");
  public static final By GENERIC_ERROR_POPUP =
      By.xpath("//div[@class='v-Notification error v-Notification-error']");
}
