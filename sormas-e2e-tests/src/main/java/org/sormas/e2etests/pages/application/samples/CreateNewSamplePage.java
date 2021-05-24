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

package org.sormas.e2etests.pages.application.samples;

import org.openqa.selenium.By;

public class CreateNewSamplePage {
  public static final By SAMPLE_UUID = By.cssSelector("[class='popupContent'] [id='uuid']");
  public static final By INTERNAL_IN_HOUSE_TESTING =
      By.xpath("//label[contains(text(), 'Internal/in-house testing')]");
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
  public static final By FIELD_SAMPLE_ID = By.cssSelector("[id='fieldSampleID']");
  public static final By COMMENT_AREA = By.cssSelector("[id='comment']");
  public static final By SAVE_SAMPLE_BUTTON =
      By.cssSelector("[class='popupContent'] [id='commit']");
}
