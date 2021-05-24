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

package org.sormas.e2etests.steps.web.application.samples;

import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.*;
import static org.sormas.e2etests.pages.application.samples.SampleManagementPage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.Sample;
import org.sormas.e2etests.services.SampleService;

public class CreateNewSampleSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/dd/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static Sample sample;
  public static String sampleId;
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public CreateNewSampleSteps(WebDriverHelpers webDriverHelpers, SampleService sampleService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new Sample with specific data$",
        () -> {
          sample = sampleService.buildGeneratedSample();
          webDriverHelpers.clickOnWebElementBySelector(INTERNAL_IN_HOUSE_TESTING);
          fillDateOfCollection(sample.getDateOfCollection());
          fillTimeOfCollection(sample.getTimeOfCollection());
          selectSampleType(sample.getSampleType());
          selectReasonForSample(sample.getReasonForSample());
          fillSampleID(sample.getSampleID());
          fillCommentsOnSample(sample.getCommentsOnSample());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "^I check the created Sample is correctly displayed on Edit Sample page",
        () -> {
          final Sample actualSample = collectSampleData();
          Truth.assertThat(sample).isEqualTo(actualSample);
        });

    When(
        "^I change all Sample fields and save$",
        () -> {
          sample = sampleService.buildEditSample();
          webDriverHelpers.clickOnWebElementBySelector(INTERNAL_IN_HOUSE_TESTING);
          fillDateOfCollection(sample.getDateOfCollection());
          fillTimeOfCollection(sample.getTimeOfCollection());
          selectSampleType(sample.getSampleType());
          selectReasonForSample(sample.getReasonForSample());
          fillSampleID(sample.getSampleID());
          fillCommentsOnSample(sample.getCommentsOnSample());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_SAMPLE_BUTTON);
        });

    When(
        "I collect the sample UUID displayed on create new sample page",
        () -> sampleId = collectSampleUuid());
  }

  public String collectSampleUuid() {
    return webDriverHelpers.getValueFromWebElement(SAMPLE_UUID);
  }

  public void fillDateOfCollection(LocalDate dateOfCollection) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_SAMPLE_COLLECTED, DATE_FORMATTER.format(dateOfCollection));
  }

  public void fillTimeOfCollection(LocalTime timeOfCollection) {
    webDriverHelpers.selectFromCombobox(
        COLLECTED_DATE_TIME_COMBOBOX, TIME_FORMATTER.format(timeOfCollection));
  }

  public void selectSampleType(String sampleType) {
    webDriverHelpers.selectFromCombobox(SAMPLE_TYPE_COMBOBOX, sampleType);
  }

  public void selectReasonForSample(String reasonForSample) {
    webDriverHelpers.selectFromCombobox(REASON_FOR_SAMPLING_TESTING_COMBOBOX, reasonForSample);
  }

  public void fillSampleID(long sampleID) {
    webDriverHelpers.clearAndFillInWebElement(FIELD_SAMPLE_ID, String.valueOf(sampleID));
  }

  public void fillCommentsOnSample(String commentsOnSample) {
    webDriverHelpers.clearAndFillInWebElement(COMMENT_AREA, commentsOnSample);
  }

  public Sample collectSampleData() {
    return Sample.builder()
        .dateOfCollection(getDateOfCollection())
        .timeOfCollection(getTimeOfCollection())
        .sampleType(getSampleType())
        .reasonForSample(getReasonForSample())
        .sampleID(Long.parseLong(getSampleID()))
        .commentsOnSample(getCommentsOnSample())
        .build();
  }

  public LocalDate getDateOfCollection() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_SAMPLE_COLLECTED), DATE_FORMATTER);
  }

  public LocalTime getTimeOfCollection() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromWebElement(COLLECTED_DATE_TIME_INPUT), TIME_FORMATTER);
  }

  public String getSampleType() {
    return webDriverHelpers.getValueFromWebElement(SAMPLE_TYPE_INPUT);
  }

  public String getReasonForSample() {
    return webDriverHelpers.getValueFromWebElement(REASON_FOR_SAMPLING_TESTING_INPUT);
  }

  public String getSampleID() {
    return webDriverHelpers.getValueFromWebElement(FIELD_SAMPLE_ID);
  }

  public String getCommentsOnSample() {
    return webDriverHelpers.getValueFromWebElement(COMMENT_AREA);
  }
}
