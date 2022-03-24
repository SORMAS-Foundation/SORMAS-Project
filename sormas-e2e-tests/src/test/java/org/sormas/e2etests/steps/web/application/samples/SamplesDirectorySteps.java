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

package org.sormas.e2etests.steps.web.application.samples;

import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.FINAL_LABORATORY_RESULT;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.LABORATORY_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.RESET_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_CLASIFICATION_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_DISEASE_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_DISTRICT_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_NOT_SHIPPED;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_RECEIVED;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_REFFERED_TO_OTHER_LAB;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_REGION_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_SHIPPED;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SEARCH_RESULT_SAMPLE;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SPECIMEN_CONDITION_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.TEST_RESULTS_SEARCH_COMBOBOX;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.enums.CaseClassification;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.LaboratoryValues;
import org.sormas.e2etests.enums.PathogenTestResults;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.SpecimenConditions;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.testng.Assert;

public class SamplesDirectorySteps implements En {

  @Inject
  public SamplesDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      EnvironmentManager environmentManager,
      ApiState apiState,
      AssertHelpers assertHelpers) {

    When(
        "I click on apply filters button from Sample Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });

    When(
        "I click on reset filters button from Sample Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER_BUTTON);
        });

    When(
        "I fill full name of last created via API Person into Sample Directory",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT,
              apiState.getLastCreatedPerson().getFirstName()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName());
        });

    When(
        "I select Test result filter value with the value for pathogen test result of last created via API Sample in Sample Directory",
        () -> {
          String testResult = apiState.getCreatedSample().getPathogenTestResult();
          webDriverHelpers.selectFromCombobox(
              TEST_RESULTS_SEARCH_COMBOBOX,
              testResult.substring(0, 1).toUpperCase() + testResult.substring(1).toLowerCase());
        });

    When(
        "I select a Test result value different than the test result of the last created via API Sample Pathogen test result",
        () -> {
          String apiTestResult = apiState.getCreatedSample().getPathogenTestResult();
          webDriverHelpers.selectFromCombobox(
              TEST_RESULTS_SEARCH_COMBOBOX,
              PathogenTestResults.geRandomResultNameDifferentThan(apiTestResult));
        });

    When(
        "I select Specimen condition filter value with value for specimen condition of the last created via API Sample in Sample Directory",
        () -> {
          String specimenCondition = apiState.getCreatedSample().getSpecimenCondition();
          webDriverHelpers.selectFromCombobox(
              SPECIMEN_CONDITION_SEARCH_COMBOBOX, SpecimenConditions.getForName(specimenCondition));
        });

    When(
        "I select {string} Specimen condition option among the filter options",
        (String specimenCondition) -> {
          webDriverHelpers.selectFromCombobox(
              SPECIMEN_CONDITION_SEARCH_COMBOBOX, specimenCondition);
        });

    When(
        "I select Case classification filter value with value for case classification of the last created via API Case in Sample Directory",
        () -> {
          String caseSpecification = apiState.getCreatedCase().getCaseClassification();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_CLASIFICATION_SEARCH_COMBOBOX,
              CaseClassification.getUIValueForGivenAPIValue(caseSpecification));
        });

    When(
        "I select a Case classification value different than the case classification value of last created via API Case in Sample Directory",
        () -> {
          String apiCaseSpecification = apiState.getCreatedCase().getCaseClassification();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_CLASIFICATION_SEARCH_COMBOBOX,
              CaseClassification.getRandomUIClassificationDifferentThan(apiCaseSpecification));
        });

    When(
        "I set Disease filter to disease value of last created via API Case in Sample Directory",
        () -> {
          String disease = apiState.getCreatedCase().getDisease();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_DISEASE_SEARCH_COMBOBOX, DiseasesValues.getCaptionForName(disease));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
        });

    When(
        "I select Disease filter value different than the disease value of the last created via API case in Sample Directory",
        () -> {
          String apiDisease = apiState.getCreatedCase().getDisease();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_DISEASE_SEARCH_COMBOBOX,
              DiseasesValues.getRandomDiseaseCaptionDifferentThan(apiDisease));
        });

    When(
        "I select Region filter value with the region value of the last created via API Case in Sample Directory",
        () -> {
          String region = apiState.getCreatedCase().getRegion().getUuid();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_REGION_SEARCH_COMBOBOX, RegionsValues.getValueFor(region));
        });

    When(
        "I change Region filter to {string} option in Sample directory",
        (String region) -> {
          webDriverHelpers.selectFromCombobox(SAMPLE_REGION_SEARCH_COMBOBOX, region);
        });

    When(
        "I select District filter value with the district value of the last created via API Case in Sample Directory",
        () -> {
          String district = apiState.getCreatedCase().getDistrict().getUuid();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_DISTRICT_SEARCH_COMBOBOX, DistrictsValues.getNameByUUID(district));
        });

    When(
        "I change District filter to {string} option in Sample directory",
        (String district) -> {
          webDriverHelpers.selectFromCombobox(SAMPLE_DISTRICT_SEARCH_COMBOBOX, district);
        });

    When(
        "I select Laboratory filter value with the uuid value of the last created via API Sample in Sample Directory",
        () -> {
          String laboratory = apiState.getCreatedSample().getLab().getCaption();
          webDriverHelpers.selectFromCombobox(LABORATORY_SEARCH_COMBOBOX, laboratory);
        });

    When(
        "I change Laboratory filter to {string} option in Sample directory",
        (String laboratory) -> {
          webDriverHelpers.selectFromCombobox(LABORATORY_SEARCH_COMBOBOX, laboratory);
        });

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
        "I am opening the last created via API Sample by url navigation",
        () -> {
          String CREATED_SAMPLE_VIA_API_URL =
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!samples/data/"
                  + apiState.getCreatedSample().getUuid();
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
              SAMPLE_SEARCH_INPUT,
              apiState.getLastCreatedPerson().getFirstName()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName());
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
          webDriverHelpers.waitUntilNumberOfElementsIsExactlyOrLess(
              SEARCH_RESULT_SAMPLE, apiState.getCreatedSamples().size());
          Assert.assertEquals(
              apiState.getCreatedSamples().size(),
              webDriverHelpers.getNumberOfElements(SAMPLE_GRID_RESULTS_ROWS),
              "Number of created samples is wrong");
        });

    Then(
        "I select {string} filter from quick filter",
        (String searchCriteria) -> {
          switch (searchCriteria) {
            case "Not shipped":
              webDriverHelpers.clickOnWebElementBySelector(SAMPLE_NOT_SHIPPED);
              break;
            case "Shipped":
              webDriverHelpers.clickOnWebElementBySelector(SAMPLE_SHIPPED);
              break;
            case "Received":
              webDriverHelpers.clickOnWebElementBySelector(SAMPLE_RECEIVED);
              break;
            case "Referred to other lab":
              webDriverHelpers.clickOnWebElementBySelector(SAMPLE_REFFERED_TO_OTHER_LAB);
              break;
          }
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
                      assertHelpers.assertWithPoll20Second(
                          () ->
                              Truth.assertWithMessage(
                                      "Total number of displayed samples results is not correct")
                                  .that(
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
            Arrays.stream(LaboratoryValues.values())
                .forEach(
                    caption -> {
                      webDriverHelpers.selectFromCombobox(
                          LABORATORY_SEARCH_COMBOBOX, caption.getCaptionEnglish());
                      webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
                      webDriverHelpers.waitUntilAListOfElementsHasText(
                          FINAL_LABORATORY_RESULT, caption.getCaptionEnglish());
                      assertHelpers.assertWithPoll20Second(
                          () ->
                              Truth.assertWithMessage(
                                      "Total number of sample results is not correct")
                                  .that(
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
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(SAMPLE_GRID_RESULTS_ROWS),
                        number.intValue(),
                        "Displayed number of sample results is not correct")));
  }
}
