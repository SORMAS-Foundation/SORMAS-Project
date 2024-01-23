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

import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.CLOSE_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.BASIC_EXPORT_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.DETAILED_EXPORT_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.EXPORT_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.FINAL_LABORATORY_RESULT;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.LABORATORY_SEARCH_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.PENDING_TEST_TABLE_RESULTS;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.POSITIVE_TEST_TABLE_RESULTS;
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
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.TOTAL_SAMPLE_COUNTER;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.events.EditEventSteps.eventParticipantsUUIDList;
import static org.sormas.e2etests.steps.web.application.events.EventDirectorySteps.userDirPath;

import com.google.common.truth.Truth;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.web.Sample;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class SamplesDirectorySteps implements En {

  @Inject
  public SamplesDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      ApiState apiState,
      AssertHelpers assertHelpers,
      RestAssuredClient restAssuredClient,
      SoftAssert softly) {

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
        "I select Test result filter value with the value for pathogen test result of last created via API Sample in Sample Directory for DE version",
        () -> {
          String testResult =
              PathogenTestResults.valueOf(apiState.getCreatedSample().getPathogenTestResult())
                  .getPathogenResultsDE();
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
        "I select a Test result value different than the test result of the last created via API Sample Pathogen test result for DE version",
        () -> {
          String apiTestResult = apiState.getCreatedSample().getPathogenTestResult();
          webDriverHelpers.selectFromCombobox(
              TEST_RESULTS_SEARCH_COMBOBOX,
              PathogenTestResults.geRandomResultNameDifferentThanDE(apiTestResult));
        });

    When(
        "I select Specimen condition filter value with value for specimen condition of the last created via API Sample in Sample Directory",
        () -> {
          String specimenCondition = apiState.getCreatedSample().getSpecimenCondition();
          webDriverHelpers.selectFromCombobox(
              SPECIMEN_CONDITION_SEARCH_COMBOBOX, SpecimenConditions.getForName(specimenCondition));
        });

    When(
        "I select Specimen condition filter value with value for specimen condition of the last created via API Sample in Sample Directory for De version",
        () -> {
          String specimenCondition =
              SpecimenConditions.valueOf(apiState.getCreatedSample().getSpecimenCondition())
                  .getConditionDE();
          webDriverHelpers.selectFromCombobox(
              SPECIMEN_CONDITION_SEARCH_COMBOBOX, specimenCondition);
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
        "I select Case classification filter value with value for case classification of the last created via API Case in Sample Directory for DE version",
        () -> {
          String caseClassification =
              CaseClassification.valueOf(apiState.getCreatedCase().getCaseClassification())
                  .getClassificationUIvalueDE();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_CLASIFICATION_SEARCH_COMBOBOX, caseClassification);
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
        "I select a Case classification value different than the case classification value of last created via API Case in Sample Directory for DE version",
        () -> {
          String apiCaseSpecification = apiState.getCreatedCase().getCaseClassification();
          webDriverHelpers.selectFromCombobox(
              SAMPLE_CLASIFICATION_SEARCH_COMBOBOX,
              CaseClassification.getRandomUIClassificationDifferentThanDE(apiCaseSpecification));
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
        "I set Disease filter to disease value of last created via API Case in Sample Directory for De version",
        () -> {
          String disease =
              DiseasesValues.valueOf(apiState.getCreatedCase().getDisease()).getDiseaseCaptionDE();
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
          String regionUUID = apiState.getCreatedCase().getRegion().getUuid();
          EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
          String regionName = environmentManager.getRegionName(regionUUID);
          webDriverHelpers.selectFromCombobox(SAMPLE_REGION_SEARCH_COMBOBOX, regionName);
        });

    When(
        "I change Region filter to {string} option in Sample directory",
        (String region) -> {
          webDriverHelpers.selectFromCombobox(SAMPLE_REGION_SEARCH_COMBOBOX, region);
        });

    When(
        "I select District filter value with the district value of the last created via API Case in Sample Directory",
        () -> {
          String districtUUID = apiState.getCreatedCase().getDistrict().getUuid();
          EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
          String districtName = environmentManager.getDistrictName(districtUUID);
          webDriverHelpers.selectFromCombobox(SAMPLE_DISTRICT_SEARCH_COMBOBOX, districtName);
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
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT, CreateNewSampleSteps.sampleId);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilWebElementHasAttributeWithValue(
              SEARCH_RESULT_SAMPLE, "title", CreateNewSampleSteps.sampleId);
        });

    When(
        "I filter the Sample for {string} event participant by {string} Final laboratory result",
        (String result, String testResult) -> {
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.selectFromCombobox(TEST_RESULTS_SEARCH_COMBOBOX, testResult);
          switch (result) {
            case "First":
              webDriverHelpers.fillAndSubmitInWebElement(
                  SAMPLE_SEARCH_INPUT, eventParticipantsUUIDList.get(0));
              TimeUnit.SECONDS.sleep(2); // wait for reaction
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "Second":
              webDriverHelpers.fillAndSubmitInWebElement(
                  SAMPLE_SEARCH_INPUT, eventParticipantsUUIDList.get(1));
              TimeUnit.SECONDS.sleep(2); // wait for reaction
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
          }
        });

    When(
        "I check that all Sample has {string} result in in Sample directory",
        (String result) -> {
          Integer totalSampleCounter =
              Integer.valueOf(webDriverHelpers.getTextFromPresentWebElement(TOTAL_SAMPLE_COUNTER));
          Integer totalOccurrence = 0;
          switch (result) {
            case "Positive":
              totalOccurrence = webDriverHelpers.getNumberOfElements(POSITIVE_TEST_TABLE_RESULTS);
              break;
            case "Pending":
              totalOccurrence = webDriverHelpers.getNumberOfElements(PENDING_TEST_TABLE_RESULTS);
              break;
          }
          softly.assertEquals(totalSampleCounter, totalOccurrence);
          softly.assertAll();
        });

    When(
        "I am opening the last created via API Sample by url navigation",
        () -> {
          String CREATED_SAMPLE_VIA_API_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
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
        "I select {string} filter from quick filter for DE version",
        (String searchCriteria) -> {
          switch (searchCriteria) {
            case "Nicht versendet":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_NOT_SHIPPED);
              webDriverHelpers.clickOnWebElementBySelector(SAMPLE_NOT_SHIPPED);
              break;
            case "Versandt":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_SHIPPED);
              webDriverHelpers.clickOnWebElementBySelector(SAMPLE_SHIPPED);
              break;
            case "Erhalten":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_RECEIVED);
              webDriverHelpers.clickOnWebElementBySelector(SAMPLE_RECEIVED);
              break;
            case "An ein anderes Labor weitergeleitet":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_REFFERED_TO_OTHER_LAB);
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
        "^I validate that number of displayed samples is correct for applied Voreingestelltes Labor filter",
        () -> {
          EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
          String labName = LaboratoryValues.VOREINGESTELLTES_LABOR.getCaptionEnglish();
          webDriverHelpers.selectFromCombobox(LABORATORY_SEARCH_COMBOBOX, labName);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_BUTTON);
          webDriverHelpers.waitUntilAListOfElementsHasText(FINAL_LABORATORY_RESULT, labName);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertWithMessage("Total number of sample results is not correct")
                      .that(
                          apiState.getCreatedSamples().stream()
                              .filter(
                                  sample ->
                                      sample
                                          .getLab()
                                          .getUuid()
                                          .contentEquals(
                                              environmentManager.getLaboratoryUUID(
                                                  RegionsValues.VoreingestellteBundeslander
                                                      .getName(),
                                                  labName)))
                              .count())
                      .isEqualTo(webDriverHelpers.getNumberOfElements(SAMPLE_GRID_RESULTS_ROWS)));
        });

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

    And(
        "I click Export button in Sample Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(EXPORT_SAMPLE_BUTTON));

    And(
        "I click on Basic Export button in Sample Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BASIC_EXPORT_SAMPLE_BUTTON);
          TimeUnit.SECONDS.sleep(5); // time for file to be downloaded
        });
    And(
        "I click on Detailed Export button in Sample Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DETAILED_EXPORT_SAMPLE_BUTTON);
          TimeUnit.SECONDS.sleep(5); // time for file to be downloaded
        });
    When(
        "I close popup after export in Sample directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_POPUP_BUTTON);
        });
    When(
        "I check if downloaded data generated by basic export option is correct",
        () -> {
          String file = userDirPath + "/downloads/sormas_samples_" + LocalDate.now() + "_.csv";
          Sample reader = parseBasicSampleExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getUuid().toLowerCase(),
              apiState.getCreatedSample().getUuid().toLowerCase(),
              "UUIDs are not equal");
          softly.assertAll();
        });
    When(
        "I check if downloaded data generated by detailed export option is correct",
        () -> {
          String file = userDirPath + "/downloads/sormas_samples_" + LocalDate.now() + "_.csv";
          Sample reader = parseDetailedSampleExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getUuid().toLowerCase(),
              apiState.getCreatedSample().getUuid().toLowerCase(),
              "UUIDs are not equal");
          softly.assertAll();
        });
    When(
        "I delete exported file from Sample Directory",
        () -> {
          String filePath = "sormas_samples_" + LocalDate.now() + "_.csv";
          FilesHelper.deleteFile(filePath);
        });
  }

  public Sample parseBasicSampleExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Sample builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(2) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseSampleExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseSampleExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder = Sample.builder().uuid(values[0]).build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseSampleExport: {}", e.getCause());
    }
    return builder;
  }

  public Sample parseDetailedSampleExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Sample builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(2) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseSampleExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseSampleExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder = Sample.builder().uuid(values[1]).build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseSampleExport: {}", e.getCause());
    }
    return builder;
  }
}
