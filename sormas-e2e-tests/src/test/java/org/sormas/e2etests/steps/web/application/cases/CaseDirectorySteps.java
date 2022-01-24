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

import com.github.javafaker.Faker;
import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.*;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.enums.CaseOrigin;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class CaseDirectorySteps implements En {

  @Inject
  public CaseDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      DataOperations dataOperations,
      ApiState apiState,
      AssertHelpers assertHelpers,
      Faker faker) {

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

    And(
        "I filter by Random CaseID on Case directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(UUID.randomUUID().toString());
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
          //                  webDriverHelpers.clickOnWebElementBySelector(
          //                          CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          //                  TimeUnit.SECONDS.sleep(3); // needed for table refresh
          //                  webDriverHelpers.waitForPageLoadingSpinnerToDisappear(3);
        });
    When(
        "^I open the last created Case via API",
        () -> {
          String caseUUID = apiState.getCreatedCase().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(NAME_UUID_EPID_NUMBER_LIKE_INPUT, caseUUID);
          By caseLocator = By.cssSelector(String.format(CASE_RESULTS_UUID_LOCATOR, caseUUID));
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(caseLocator);
          webDriverHelpers.clickOnWebElementBySelector(caseLocator);
        });

    Then(
        "I check that number of displayed cases results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS),
                        number.intValue(),
                        "Number of displayed cases is not correct")));

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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, apiState.getCreatedCases().size());
          Assert.assertEquals(
              apiState.getCreatedCases().size(),
              Integer.parseInt(webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER)),
              "Total number of displayed cases doesn't match with the number of cases created via api");
        });

    And(
        "I apply Person Id filter",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
              apiState.getLastCreatedPerson().getFirstName()
                  + " "
                  + apiState.getLastCreatedPerson().getLastName());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Random Person Id filter",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
              faker.name().firstName() + " " + faker.name().lastName());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Random Person Id filter",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
              faker.name().firstName() + " " + faker.name().lastName());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    Then(
        "I apply Outcome of case filter {string}",
        (String outcomeFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_OUTCOME_FILTER_COMBOBOX, CaseOutcome.getValueFor(outcomeFilterOption));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
        });
    And(
        "I apply Case classification filter {string}",
        (String caseClassification) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_CLASSIFICATION_FILTER_COMBOBOX,
              caseClassification);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Follow-up filter {string}",
        (String caseClassification) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_FOLLOWUP_FILTER_COMBOBOX, FollowUpStatus.getValueFor(caseClassification));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Present Condition filter",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_PRESENT_CONDITION_COMBOBOX,
              PresentCondition.getValueFor(apiState.getLastCreatedPerson().getPresentCondition()));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    And(
        "I click APPLY BUTTON in Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });

    Then(
        "I apply Case origin {string}",
        (String caseOrigin) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_ORIGIN_FILTER_COMBOBOX, CaseOrigin.getValueFor(caseOrigin));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    Then(
        "I apply Community {string}",
        (String community) -> {
          webDriverHelpers.selectFromCombobox(CASE_COMMUNITY_FILTER_COMBOBOX, community);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    And(
        "I apply Region filter {string}",
        (String region) -> {
          webDriverHelpers.selectFromCombobox(CASE_REGION_FILTER_COMBOBOX, region);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Surveillance Officer filter {string}",
        (String survoff) -> {
          webDriverHelpers.selectFromCombobox(CASE_SURVOFF_FILTER_COMBOBOX, survoff);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Reporting User filter {string}",
        (String reportingUser) -> {
          webDriverHelpers.fillInWebElement(CASE_REPORTING_USER_FILTER, reportingUser);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Vaccination Status filter to {string}",
        (String vaccinationStatus) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_VACCINATION_STATUS_FILTER_COMBOBOX, vaccinationStatus);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Quarantine filter to {string}",
        (String quarantine) -> {
          webDriverHelpers.selectFromCombobox(CASE_QUARANTINE_FILTER_COMBOBOX, quarantine);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Reinfection filter to {string}",
        (String reinfection) -> {
          webDriverHelpers.selectFromCombobox(CASE_REINFECTION_FILTER_COMBOBOX, reinfection);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Date type filter to {string}",
        (String dataType) -> {
          webDriverHelpers.selectFromCombobox(CASE_DATA_TYPE_FILTER_COMBOBOX, dataType);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    And(
        "I fill Cases from input to day before mocked case created",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX, formatter.format(LocalDate.now().minusDays(3)));
        });
    And(
        "I fill Cases from input to days after before mocked case created",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX, formatter.format(LocalDate.now().plusDays(1)));
        });
    And(
        "I click All button in Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click on Investigation pending button on Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(INVESTIGATION_PENDING_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click on Investigation done button on Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(INVESTIGATION_DONE_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });
    And(
        "I click on Investigation discarded button on Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(INVESTIGATION_DISCARDED_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });

    And(
        "I click {string} checkbox",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Only cases without geo coordinates"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITHOUT_GEO_COORDINATES_CHECKBOX);
              break;
            case ("Only cases without responsible officer"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_WITHOUT_RESPONSIBLE_OFFICER_CHECKBOX);
              break;
            case ("Only cases with extended quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_EXTENDED_QUARANTINE_CHECKBOX);
              break;
            case ("Only cases with reduced quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_REDUCED_QUARANTINE_CHECKBOX);
              break;
            case ("Help needed in quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_HELP_NEEDED_IN_QUARANTINE_CHECKBOX);
              break;
            case ("Only cases with events"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_EVENTS_CHECKBOX);
              break;
            case ("Only cases from other instances"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_FROM_OTHER_INSTANCES_CHECKBOX);
              break;
            case ("Only cases with reinfection"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_REINFECTION_CHECKBOX);
              break;
            case ("Include cases from other jurisdictions"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_FROM_OTHER_JURISDICTIONS_CHECKBOX);
              break;
            case ("Only cases with fulfilled reference definition"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_WITH_FULFILLED_REFERENCE_DEFINITION_CHECKBOX);
              break;
            case ("Only port health cases without a facility"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITHOUT_FACILITY_CHECKBOX);
              break;
          }
        });

    And(
        "I fill Cases to input to day after mocked case created",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX, formatter.format(LocalDate.now().plusDays(5)));
        });
    And(
        "I apply Year filter different than person has",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_YEAR_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      apiState.getLastCreatedPerson().getBirthdateYYYY(), 1900, 2002)
                  .toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Year filter of last created person",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_YEAR_FILTER, apiState.getLastCreatedPerson().getBirthdateYYYY().toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply {string} to combobox on Case Directory Page",
        (String caseParameter) -> {
          webDriverHelpers.selectFromCombobox(CASE_DISPLAY_FILTER_COMBOBOX, caseParameter);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    And(
        "I apply Month filter different than person has",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_MONTH_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      apiState.getLastCreatedPerson().getBirthdateMM(), 1, 12)
                  .toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Month filter of last created person",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_MONTH_FILTER, apiState.getLastCreatedPerson().getBirthdateMM().toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Day filter different than person has",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DAY_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      apiState.getLastCreatedPerson().getBirthdateDD(), 1, 27)
                  .toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Day filter of last created person",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DAY_FILTER, apiState.getLastCreatedPerson().getBirthdateDD().toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Facility category filter {string}",
        (String facilityCategory) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_FACILITY_CATEGORY_FILTER_COMBOBOX,
              FacilityCategory.getValueFor(facilityCategory));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Facility type filter to {string}",
        (String facilityType) -> {
          webDriverHelpers.selectFromCombobox(CASE_FACILITY_TYPE_FILTER_COMBOBOX, facilityType);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Facility filter to {string}",
        (String facility) -> {
          webDriverHelpers.selectFromCombobox(CASE_FACILITY_FILTER_COMBOBOX, facility);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply District filter {string}",
        (String district) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DISTRICT_FILTER_COMBOBOX, DistrictsValues.getValueFor(district));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    Then(
        "I apply Case origin {string}",
        (String caseOrigin) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_ORIGIN_FILTER_COMBOBOX, CaseOrigin.getValueFor(caseOrigin));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    Then(
        "I apply Community {string}",
        (String community) -> {
          webDriverHelpers.selectFromCombobox(CASE_COMMUNITY_FILTER_COMBOBOX, community);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    And(
        "I apply Region filter {string}",
        (String region) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_REGION_FILTER_COMBOBOX, RegionsValues.getValueFor(region));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Surveillance Officer filter {string}",
        (String survoff) -> {
          webDriverHelpers.selectFromCombobox(CASE_SURVOFF_FILTER_COMBOBOX, survoff);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Reporting User filter {string}",
        (String reportingUser) -> {
          webDriverHelpers.fillInWebElement(CASE_REPORTING_USER_FILTER, reportingUser);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Year filter {string}",
        (String year) -> {
          webDriverHelpers.selectFromCombobox(CASE_YEAR_FILTER, year);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Year filter of last created person",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_YEAR_FILTER, apiState.getLastCreatedPerson().getBirthdateYYYY().toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    And(
        "I apply Month filter {string}",
        (String month) -> {
          webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Month filter of last created person",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_MONTH_FILTER, apiState.getLastCreatedPerson().getBirthdateMM().toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Day filter {string}",
        (String day) -> {
          webDriverHelpers.selectFromCombobox(CASE_DAY_FILTER, day);
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Day filter of last created person",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DAY_FILTER, apiState.getLastCreatedPerson().getBirthdateDD().toString());
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply Facility category filter {string}",
        (String facilityCategory) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_REGION_FILTER_COMBOBOX, FacilityCategory.getValueFor(facilityCategory));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });
    And(
        "I apply District filter {string}",
        (String district) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DISTRICT_FILTER_COMBOBOX, DistrictsValues.getValueFor(district));
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);

        });

    And(
        "I check that all displayed cases have {string} in grid Case Classification column",
        (String expectedValue) -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CASE_GRID_RESULTS_ROWS, CaseOutcome.getValueFor(expectedValue));
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertWithMessage(
                          expectedValue
                              + " value is not displayed in grid Case Classification column")
                      .that(
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
          // webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
        });
    And(
        "I click SHOW MORE FILTERS button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_LESS_FILTERS);
        });

    Then(
        "I check that all displayed cases have {string} in grid Disease column",
        (String expectedValue) -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CASE_GRID_RESULTS_ROWS, DiseasesValues.getCaptionFor(expectedValue));
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertWithMessage(
                          expectedValue + " value is not displayed in grid Disease column")
                      .that(
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

  private Number getRandomNumberForBirthDateDifferentThanCreated(Number created, int min, int max) {
    Faker faker = new Faker();
    Number replacement = created;
    while (created.equals(replacement)) {
      replacement = faker.number().numberBetween(min, max);
    }
    return replacement;
  }
}
