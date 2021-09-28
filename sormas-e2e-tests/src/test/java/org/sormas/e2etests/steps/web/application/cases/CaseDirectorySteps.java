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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.util.Arrays;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.*;
import org.sormas.e2etests.enums.CaseClasification;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;

public class CaseDirectorySteps implements En {

  @Inject
  public CaseDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      DataOperations dataOperations,
      ApiState apiState,
      AssertHelpers assertHelpers) {

    When(
        "^I click on the NEW CASE button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CASE_BUTTON, DATE_OF_REPORT_INPUT));

    When(
        "^I click on Case Line Listing button$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_BUTTON));

    When(
        "^I open last created case",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_CASE_ID_BUTTON));

    When(
        "^Search for Case using Case UUID from the created Task",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                NAME_UUID_EPID_NUMBER_LIKE_INPUT, EditCaseSteps.aCase.getUuid()));
    When(
        "I click on the DETAILED button from Case directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_DIRECTORY_DETAILED_RADIOBUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(RESULTS_GRID_HEADER, "Sex")), 20);
        });

    When(
        "I filter by CaseID on Case directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(apiState.getCreatedCase().getUuid());
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
        });

    When(
        "^I open the last created Case via API",
        () -> {
          String caseUUID = apiState.getCreatedCase().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(NAME_UUID_EPID_NUMBER_LIKE_INPUT, caseUUID);
          By caseLocator = By.cssSelector(String.format(CASE_RESULTS_UUID_LOCATOR, caseUUID));
          assertHelpers.assertWithPoll15Second(
              () -> Truth.assertThat(webDriverHelpers.isElementVisibleWithTimeout(caseLocator, 5)));
          webDriverHelpers.clickOnWebElementBySelector(caseLocator);
        });

    Then(
        "I check that number of displayed cas" + "es results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll15Second(
                () ->
                    Truth.assertThat(webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS))
                        .isEqualTo(number)));

    When(
        "^I search for cases created with the API",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_RESET_FILTERS_BUTTON);
          int maximumNumberOfRows = 23;
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              CASE_GRID_RESULTS_ROWS, maximumNumberOfRows);
          webDriverHelpers.fillAndSubmitInWebElement(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, apiState.getLastCreatedPerson().getFirstName());
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, apiState.getCreatedCases().size());
          //          Truth.assertThat(apiState.getCreatedCases().size())
          //              .isEqualTo(webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS));
        });

    Then(
        "^I check the displayed Case Outcome filter dropdown",
        () ->
            Arrays.stream(CaseOutcome.values())
                .forEach(
                    outcome -> {
                      webDriverHelpers.selectFromCombobox(
                          CASE_OUTCOME_FILTER_COMBOBOX, outcome.getOutcome());
                      webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
                      webDriverHelpers.waitUntilAListOfElementsHasText(
                          CASE_GRID_RESULTS_ROWS, outcome.getOutcome());
                      assertHelpers.assertWithPoll15Second(
                          () ->
                              Truth.assertThat(
                                      apiState.getCreatedCases().stream()
                                          .filter(
                                              sample ->
                                                  sample
                                                      .getOutcome()
                                                      .contentEquals(outcome.toString()))
                                          .count())
                                  .isEqualTo(
                                      webDriverHelpers.getNumberOfElements(
                                          CASE_GRID_RESULTS_ROWS)));
                    }));

    Then(
        "^I check the displayed Case Classification filter dropdown",
        () ->
            Arrays.stream(CaseClasification.values())
                .forEach(
                    clasification -> {
                      webDriverHelpers.selectFromCombobox(
                          CASE_CLASSIFICATION_FILTER_COMBOBOX, clasification.getClassification());
                      webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
                      webDriverHelpers.waitUntilAListOfElementsHasText(
                          CASE_GRID_RESULTS_ROWS, clasification.getClassification());
                      assertHelpers.assertWithPoll15Second(
                          () ->
                              Truth.assertThat(
                                      apiState.getCreatedCases().stream()
                                          .filter(
                                              sample ->
                                                  sample
                                                      .getCaseClassification()
                                                      .contentEquals(clasification.toString()))
                                          .count())
                                  .isEqualTo(
                                      webDriverHelpers.getNumberOfElements(
                                          CASE_GRID_RESULTS_ROWS)));
                    }));

    Then(
        "^I check the displayed Disease filter dropdown",
        () ->
            Arrays.stream(DiseasesValues.values())
                .forEach(
                    aDisease -> {
                      webDriverHelpers.selectFromCombobox(
                          CASE_DISEASE_FILTER_COMBOBOX, aDisease.getDiseaseName());
                      webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
                      webDriverHelpers.waitUntilAListOfElementsHasText(
                          CASE_GRID_RESULTS_ROWS, aDisease.getDiseaseName());
                      assertHelpers.assertWithPoll15Second(
                          () ->
                              Truth.assertThat(
                                      apiState.getCreatedCases().stream()
                                          .filter(
                                              sample ->
                                                  sample
                                                      .getDisease()
                                                      .contentEquals(aDisease.toString()))
                                          .count())
                                  .isEqualTo(
                                      webDriverHelpers.getNumberOfElements(
                                          CASE_GRID_RESULTS_ROWS)));
                    }));
  }
}
