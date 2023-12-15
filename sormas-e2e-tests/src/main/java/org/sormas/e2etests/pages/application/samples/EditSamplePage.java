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

public class EditSamplePage {

  public static final By UUID_FIELD = By.id("uuid");
  public static final By PATHOGEN_NEW_TEST_RESULT_BUTTON = By.cssSelector("[id='New test result']");
  public static final By ADDIITONAL_NEW_TEST_RESULT_BUTTON =
      By.cssSelector("[location='additionalTests'] [id='New test result']");
  public static final By NEW_TEST_RESULT_DE = By.cssSelector("[id='Neues Testresultat']");
  public static final By COLLECTED_DATE_TIME_COMBOBOX =
      By.cssSelector("[id='sampleDateTime_time'] [class='v-filterselect-button']");
  public static final By COMMENT_AREA_INPUT = By.cssSelector("[id='comment']");
  public static final By DATE_SAMPLE_COLLECTED = By.cssSelector("[id='sampleDateTime_date'] input");
  public static final By DATE_SAMPLE_WAS_SENT_INPUT = By.cssSelector("[id='shipmentDate'] input");
  public static final By PATHOGEN_TEST_RESULT_INPUT =
      By.cssSelector("[id='pathogenTestResult'] input");

  public static final By DATE_SAMPLE_RECEIVED = By.cssSelector("[id='receivedDate'] input");
  public static final By FIELD_SAMPLE_ID_INPUT = By.cssSelector("[id='fieldSampleID']");
  public static final By LABORATORY_COMBOBOX =
      By.cssSelector("[id='lab'] [class='v-filterselect-button']");
  public static final By COLLECTED_DATE_TIME_INPUT =
      By.cssSelector("[id='sampleDateTime_time'] input");
  public static final By LABORATORY_INPUT = By.cssSelector("[id='lab'] input");
  public static final By REASON_FOR_SAMPLING_TESTING_INPUT =
      By.cssSelector("[id='samplingReason'] input");
  public static final By SAMPLE_TYPE_INPUT = By.cssSelector("[id='sampleMaterial'] input");
  public static final By SPECIMEN_CONDITION_INPUT =
      By.cssSelector("[id='specimenCondition'] input");
  public static final By SPECIMEN_CONDITION_NOT_MANDATORY_COMBOBOX =
      By.xpath("//div[@id='specimenCondition' and not(contains(@class, 'v-required'))]");
  public static final By LABORATORY_NAME_INPUT = By.cssSelector("[id='labDetails']");
  public static final By RECEIVED_OPTION_BUTTON = By.cssSelector("[id='received'] label");
  public static final By LAB_SAMPLE_ID_INPUT = By.cssSelector("[id='labSampleID']");
  public static final By REASON_FOR_SAMPLING_TESTING_COMBOBOX =
      By.cssSelector("[id='samplingReason'] [class='v-filterselect-button']");
  public static final By EXTERNAL_LAB_TESTING_RADIOBUTTON =
      By.xpath("//label[contains(text(), 'External lab testing')]");
  public static final By INTERNAL_LAB_TESTING_RADIOBUTTON =
      By.xpath("//label[contains(text(), 'Internal/in-house testing')]");
  public static final By SAMPLE_TYPE_COMBOBOX =
      By.cssSelector("[id='sampleMaterial'] [class='v-filterselect-button']");
  public static final By SPECIMEN_CONDITION_COMBOBOX =
      By.cssSelector("[id='specimenCondition'] [class='v-filterselect-button']");
  public static final By FOUR_FOLD_INCREASE_ANTIBODY_TITER =
      By.cssSelector("[class='popupContent'] [id='fourFoldIncreaseAntibodyTiter']");
  public static final By CQ_CT_VALUE_INPUT =
      By.cssSelector("[class='popupContent'] [id='cqValue']");
  public static final By PCR_TEST_SPECIFICATION_COMBOBOX =
      By.cssSelector("[class='popupContent'] [id='pcrTestSpecification']");
  public static final By PCR_TEST_SPECIFICATION_COMBOBOX_DIV =
      By.cssSelector("[class='popupContent'] [id='pcrTestSpecification'] div");
  public static final By SPECIFY_TEST_DETAILS_INPUT =
      By.cssSelector("[class='popupContent'] [id='testTypeText']");
  public static final By TYPING_ID_INPUT = By.cssSelector("[class='popupContent'] [id='typingId']");
  public static final By DELETE_SAMPLE_BUTTON = By.cssSelector("#deleteRestore");
  public static final By DISCARD_SAMPLE_BUTTON = By.cssSelector("#discard");
  public static final By DELETE_PATHOGEN_TEST_RESULT =
      By.cssSelector("[class='popupContent'] [id='deleteRestore']");
  public static final By SAMPLE_DELETION_POPUP_YES_BUTTON = By.id("actionConfirm");
  public static final By SAVE_SAMPLE_BUTTON = By.id("commit");
  public static final By DELETE_SAMPLE_REASON_POPUP =
      By.xpath(
          "//div[@class='popupContent']//*[text()='Reason for deletion']/../following-sibling::div//div");
  public static final By DELETE_SAMPLE_REASON_POPUP_FOR_DE =
      By.xpath(
          "//div[@class='popupContent']//*[text()='Grund des L\u00F6schens']/../following-sibling::div//div");
  public static final By EDIT_PATHOGEN_TEST =
      By.cssSelector(".v-align-right.v-slot.v-slot-compact.v-slot-link > div[role='button']");
  public static final By TESTED_DISEASE_VARIANT =
      By.cssSelector(".popupContent [id='testedDiseaseVariant'] div");
  public static final By PCR_TEST_SPECIFICATION_INPUT =
      By.cssSelector("[class='popupContent'] [id='pcrTestSpecification'] input");
  public static final By SEE_SAMPLES_FOR_THIS_PERSON_BUTTON_DE =
      By.id("Proben f\u00FCr diese Person ansehen");
  public static final By BACK_TO_CASE_DE_BUTTON =
      By.xpath(
          "//div[@class='v-link v-widget v-caption v-link-v-caption']//span[contains(text(), 'Fall')]");
}
