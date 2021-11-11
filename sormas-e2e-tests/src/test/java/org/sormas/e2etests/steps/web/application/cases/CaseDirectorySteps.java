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
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.*;
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
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(GRID_HEADERS, 41);
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
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });

    When(
        "^I open the last created Case via API",
        () -> {
          String caseUUID = apiState.getCreatedCase().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(NAME_UUID_EPID_NUMBER_LIKE_INPUT, caseUUID);
          By caseLocator = By.cssSelector(String.format(CASE_RESULTS_UUID_LOCATOR, caseUUID));
          assertHelpers.assertWithPoll20Second(
              () -> Truth.assertThat(webDriverHelpers.isElementVisibleWithTimeout(caseLocator, 5)));
          webDriverHelpers.clickOnWebElementBySelector(caseLocator);
        });

    Then(
        "I check that number of displayed cas" + "es results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Truth.assertThat(webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS))
                        .isEqualTo(number)));

    When(
        "^I search for cases created with the API using Person's name",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_RESET_FILTERS_BUTTON);
          int maximumNumberOfRows = 23;
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              CASE_GRID_RESULTS_ROWS, maximumNumberOfRows);
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
              apiState.getLastCreatedPerson().getFirstName()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName());
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, apiState.getCreatedCases().size());
          Truth.assertThat(apiState.getCreatedCases().size())
              .isEqualTo(
                  Integer.parseInt(
                      webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER)));
        });

    Then(
        "I apply Outcome of case filter {string}",
        (String outcomeFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_OUTCOME_FILTER_COMBOBOX, CaseOutcome.getValueFor(outcomeFilterOption));
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
        });

    And(
        "I check that all displayed cases have {string} in grid Case Classification column",
        (String expectedValue) -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CASE_GRID_RESULTS_ROWS, CaseOutcome.getValueFor(expectedValue));
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertThat(
                          apiState.getCreatedCases().stream()
                              .filter(sample -> sample.getOutcome().contentEquals("NO_OUTCOME"))
                              .count())
                      .isEqualTo(
                          Integer.valueOf(
                              webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER))));
        });
    // TODO refactor method to use a specific outcome once the other fix is done

    Then(
        "I apply Disease filter {string}",
        (String diseaseFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DISEASE_FILTER_COMBOBOX, DiseasesValues.getCaptionFor(diseaseFilterOption));
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
        });

    Then(
        "I check that all displayed cases have {string} in grid Disease column",
        (String expectedValue) -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CASE_GRID_RESULTS_ROWS, DiseasesValues.getCaptionFor(expectedValue));
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertThat(
                          apiState.getCreatedCases().stream()
                              .filter(
                                  sample ->
                                      sample
                                          .getDisease()
                                          .contentEquals(
                                              DiseasesValues.CORONAVIRUS.getDiseaseName()))
                              .count())
                      .isEqualTo(
                          Integer.valueOf(
                              webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER))));
        });
  }
}
