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

import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.enums.LabCaption;
import org.sormas.e2etests.enums.PathogenTestResults;
import org.sormas.e2etests.enums.SpecimenConditions;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;

public class SamplesDirectorySteps implements En {

  @Inject
  public SamplesDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      @Named("ENVIRONMENT_URL") String environmentUrl,
      ApiState apiState,
      AssertHelpers assertHelpers) {

    When(
        "^I search last created Sample by Case ID$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_SEARCH_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT, EditCaseSteps.aCase.getUuid());
          webDriverHelpers.waitUntilNumberOfElementsIsReduceToGiven(SEARCH_RESULT_SAMPLE, 2);
        });

    When(
        "^I open created Sample$",
        () -> webDriverHelpers.clickOnWebElementBySelector(SEARCH_RESULT_SAMPLE));

    When(
        "^I search for Sample using Sample UUID from the created Sample",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT, CreateNewSampleSteps.sampleId);
          webDriverHelpers.waitUntilWebElementHasAttributeWithValue(
              SEARCH_RESULT_SAMPLE, "title", CreateNewSampleSteps.sampleId);
        });

    When(
        "I am accessing the created sample via api",
        () -> {
          String CREATED_SAMPLE_VIA_API_URL =
              environmentUrl + "/sormas-ui/#!samples/data/" + apiState.getCreatedSample().getUuid();
          webDriverHelpers.accessWebSite(CREATED_SAMPLE_VIA_API_URL);
          webDriverHelpers.waitForPageLoaded();
        });

    When(
        "^I search for samples created with the API",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER_BUTTON);
          int maximumNumberOfRows = 23;
          webDriverHelpers.waitUntilNumberOfElementsIsExactlyOrLess(
              SEARCH_RESULT_SAMPLE, maximumNumberOfRows);
          Thread.sleep(2000); // reset filter acts chaotic, to be modified in the future
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT, apiState.getEditPerson().getFirstName());
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
          webDriverHelpers.waitUntilNumberOfElementsIsExactlyOrLess(
              SEARCH_RESULT_SAMPLE, apiState.getCreatedSamples().size());
          Truth.assertThat(apiState.getCreatedSamples().size())
              .isEqualTo(webDriverHelpers.getNumberOfElements(SAMPLE_GRID_RESULTS_ROWS));
        });

    Then(
        "^I check the displayed test results filter dropdown",
        () ->
            Arrays.stream(PathogenTestResults.values())
                .forEach(
                    vPathogen -> {
                      webDriverHelpers.selectFromCombobox(
                          TEST_RESULTS_SEARCH_COMBOBOX, vPathogen.getPathogenResults());
                      webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
                      webDriverHelpers.waitUntilAListOfElementsHasText(
                          FINAL_LABORATORY_RESULT, vPathogen.getPathogenResults());

                      Truth.assertThat(
                              apiState.getCreatedSamples().stream()
                                  .filter(
                                      sample ->
                                          sample
                                              .getPathogenTestResult()
                                              .contentEquals(vPathogen.toString()))
                                  .count())
                          .isEqualTo(
                              webDriverHelpers.getNumberOfElements(SAMPLE_GRID_RESULTS_ROWS));
                    }));

    Then(
        "^I check the displayed specimen condition filter dropdown",
        () ->
            Arrays.stream(SpecimenConditions.values())
                .forEach(
                    aSpecimen -> {
                      webDriverHelpers.selectFromCombobox(
                          SPECIMEN_CONDITION_SEARCH_COMBOBOX, aSpecimen.getCondition());
                      webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
                      webDriverHelpers.waitUntilAListOfElementsHasText(
                          FINAL_LABORATORY_RESULT, aSpecimen.getCondition());
                      assertHelpers.assertWithPoll15Second(
                          () ->
                              Truth.assertThat(
                                      apiState.getCreatedSamples().stream()
                                          .filter(
                                              sample ->
                                                  sample
                                                      .getSpecimenCondition()
                                                      .contentEquals(aSpecimen.toString()))
                                          .count())
                                  .isEqualTo(
                                      webDriverHelpers.getNumberOfElements(
                                          SAMPLE_GRID_RESULTS_ROWS)));
                    }));

    Then(
        "^I check the displayed Laboratory filter dropdown",
        () ->
            Arrays.stream(LabCaption.values())
                .forEach(
                    caption -> {
                      webDriverHelpers.selectFromCombobox(
                          LABORATORY_SEARCH_COMBOBOX, caption.getCaptionEnglish());
                      webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
                      webDriverHelpers.waitUntilAListOfElementsHasText(
                          FINAL_LABORATORY_RESULT, caption.getCaptionEnglish());
                      assertHelpers.assertWithPoll15Second(
                          () ->
                              Truth.assertThat(
                                      apiState.getCreatedSamples().stream()
                                          .filter(
                                              sample ->
                                                  sample
                                                      .getLab()
                                                      .getUuid()
                                                      .contentEquals(caption.getUuidValue()))
                                          .count())
                                  .isEqualTo(
                                      webDriverHelpers.getNumberOfElements(
                                          SAMPLE_GRID_RESULTS_ROWS)));
                    }));

    Then(
        "I search after the last created Sample via API",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT, apiState.getCreatedSample().getUuid());
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
        });

    Then(
        "I check that number of displayed sample results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll15Second(
                () ->
                    Truth.assertThat(webDriverHelpers.getNumberOfElements(SAMPLE_GRID_RESULTS_ROWS))
                        .isEqualTo(number)));
  }
}
