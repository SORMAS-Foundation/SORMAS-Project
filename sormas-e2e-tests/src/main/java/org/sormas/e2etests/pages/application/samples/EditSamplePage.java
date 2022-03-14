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

  public static final By PATHOGEN_NEW_TEST_RESULT_BUTTON = By.cssSelector("[id='New test result']");
  public static final By NEW_TEST_RESULT_DE = By.cssSelector("[id='Neues Testresultat']");
  public static final By COLLECTED_DATE_TIME_COMBOBOX =
      By.cssSelector("[id='sampleDateTime_time'] [class='v-filterselect-button']");
  public static final By COMMENT_AREA_INPUT = By.cssSelector("[id='comment']");
  public static final By DATE_SAMPLE_COLLECTED = By.cssSelector("[id='sampleDateTime_date'] input");
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
  public static final By LABORATORY_NAME_INPUT = By.cssSelector("[id='labDetails']");
  public static final By RECEIVED_OPTION_BUTTON = By.cssSelector("[id='received'] label");
  public static final By LAB_SAMPLE_ID_INPUT = By.cssSelector("[id='labSampleID']");
  public static final By REASON_FOR_SAMPLING_TESTING_COMBOBOX =
      By.cssSelector("[id='samplingReason'] [class='v-filterselect-button']");
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
  public static final By SPECIFY_TEST_DETAILS_INPUT =
      By.cssSelector("[class='popupContent'] [id='testTypeText']");
  public static final By TYPING_ID_INPUT = By.cssSelector("[class='popupContent'] [id='typingId']");
  public static final By DELETE_SAMPLE_BUTTON = By.cssSelector("#delete");
  public static final By DELETE_PATHOGEN_TEST_RESULT =
      By.cssSelector("[class='popupContent'] [id='delete']");
  public static final By SAMPLE_DELETION_POPUP = By.cssSelector(".v-window .popupContent");
  public static final By SAMPLE_DELETION_POPUP_YES_BUTTON = By.id("actionConfirm");
  public static final By SAVE_SAMPLE_BUTTON = By.id("commit");
}
