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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ALLBUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ALL_RESULTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS_VALUES;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_FROM_OTHER_INSTANCES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_FROM_OTHER_JURISDICTIONS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_HELP_NEEDED_IN_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITHOUT_FACILITY_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITHOUT_GEO_COORDINATES_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITHOUT_RESPONSIBLE_OFFICER_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_EVENTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_EXTENDED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_FULFILLED_REFERENCE_DEFINITION_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_REDUCED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASES_WITH_REINFECTION_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_COMMUNITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DATA_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DAY_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISEASE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISEASE_VARIANT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISPLAY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISTRICT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FACILITY_CATEGORY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FACILITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FACILITY_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_FOLLOWUP_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_MONTH_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_ORIGIN_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_OUTCOME_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_PRESENT_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_QUARANTINE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_REGION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_REINFECTION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_REPORTING_USER_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_RESULTS_UUID_LOCATOR;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_SURVOFF_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_VACCINATION_STATUS_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_YEAR_FILTER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_TO_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ENTER_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_CASE_ID_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_RESULT_IN_GRID;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.GRID_HEADERS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.INVESTIGATION_DISCARDED_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.INVESTIGATION_DONE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.INVESTIGATION_PENDING_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.LINE_LISTING_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MORE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NAME_UUID_EPID_NUMBER_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NEW_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NEW_EVENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.RESULTS_GRID_HEADER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SEARCH_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.TOTAL_CASES_COUNTER;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;

import com.github.javafaker.Faker;
import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.FacilityCategory;
import org.sormas.e2etests.enums.FollowUpStatus;
import org.sormas.e2etests.enums.PresentCondition;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class CaseDirectorySteps implements En {
  Faker faker = new Faker();

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
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_CASE_ID_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_CASE_ID_BUTTON);
        });

    When(
        "^Search for Case using Case UUID from the created Task",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, EditCaseSteps.aCase.getUuid());
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click on the DETAILED button from Case directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_DIRECTORY_DETAILED_RADIOBUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(RESULTS_GRID_HEADER, "Sex")), 20);
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(GRID_HEADERS, 41);
        });
    When(
        "I click on the More button on Case directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(MORE_BUTTON);
        });
    When(
        "I click Enter Bulk Edit Mode on Case directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ENTER_BULK_EDIT_MODE);
        });
    When(
        "I click checkbox to choose all Case results",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_RESULTS_CHECKBOX);
        });
    And(
        "I click on Bulk Actions combobox on Case Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS));

    And(
        "I click on Link to Event from Bulk Actions combobox on Case Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_VALUES));

    And(
        "I click on New Event option in Link to Event Form",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_CHECKBOX));
    And(
        "I fill Event Id filter in Link to Event form with last created via API Event uuid",
        () -> {
          String eventUuid = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.fillInWebElement(SEARCH_BUTTON, eventUuid);
        });
    And(
        "I click first result in grid on Link to Event form",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_RESULT_IN_GRID));

    When(
        "I filter by CaseID on Case directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(apiState.getCreatedCase().getUuid());
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
        });
    When(
        "I filter by CaseID of last created UI Case on Case directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(EditCaseSteps.aCase.getUuid());
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_CASE_ID_BUTTON);
        });

    When(
        "^I open the last created Case via API",
        () -> {
          String caseUUID = apiState.getCreatedCase().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(NAME_UUID_EPID_NUMBER_LIKE_INPUT, caseUUID);
          By caseLocator = By.cssSelector(String.format(CASE_RESULTS_UUID_LOCATOR, caseUUID));
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(caseLocator);
          webDriverHelpers.clickOnWebElementBySelector(caseLocator);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });

    Then(
        "I check that number of displayed cases results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        Integer.parseInt(
                            webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER)),
                        number.intValue(),
                        "Number of displayed cases is not correct")));

    And(
        "I filter by mocked CaseID on Case directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(
                  "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
        });
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

    Then(
        "I apply Outcome of case filter {string}",
        (String outcomeFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CASE_OUTCOME_FILTER_COMBOBOX, CaseOutcome.getValueFor(outcomeFilterOption));
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
    And(
        "I apply uuid filter for last created via API Person in Case directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
                apiState.getLastCreatedPerson().getUuid()));
    And(
        "I apply Person Id filter to one attached to last created UI Case on Case directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
                EditCaseSteps.aCase.getFirstName() + " " + EditCaseSteps.aCase.getLastName()));
    And(
        "I apply mocked Person Id filter on Case directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT, "TestName TestSurname"));
    Then(
        "I apply Disease filter {string} on Case directory page",
        (String diseaseFilterOption) ->
            webDriverHelpers.selectFromCombobox(
                CASE_DISEASE_FILTER_COMBOBOX, DiseasesValues.getCaptionFor(diseaseFilterOption)));
    Then(
        "I apply Disease Variant filter {string} on Case directory page",
        (String diseaseFilterOption) ->
            webDriverHelpers.selectFromCombobox(
                CASE_DISEASE_VARIANT_FILTER_COMBOBOX, diseaseFilterOption));
    And(
        "I click SHOW MORE FILTERS button on Case directory page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_LESS_FILTERS));

    Then(
        "I apply Outcome of case filter {string} on Case directory page",
        (String outcomeFilterOption) ->
            webDriverHelpers.selectFromCombobox(CASE_OUTCOME_FILTER_COMBOBOX, outcomeFilterOption));
    And(
        "I apply Case classification filter {string} on Case directory page",
        (String caseClassification) ->
            webDriverHelpers.selectFromCombobox(
                CASE_CLASSIFICATION_FILTER_COMBOBOX, caseClassification));
    And(
        "I apply Follow-up filter {string} on Case directory page",
        (String caseClassification) ->
            webDriverHelpers.selectFromCombobox(
                CASE_FOLLOWUP_FILTER_COMBOBOX, FollowUpStatus.getValueFor(caseClassification)));
    And(
        "I apply Present Condition filter on Case directory page to condition of last created person",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_PRESENT_CONDITION_COMBOBOX,
                PresentCondition.getValueFor(
                    apiState.getLastCreatedPerson().getPresentCondition())));
    And(
        "I apply Present Condition filter on Case directory page to condition of person attached to created Case",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_PRESENT_CONDITION_COMBOBOX,
                CreateNewCaseSteps.caze.getPresentConditionOfPerson()));
    And(
        "I apply Present Condition filter to {string} on Case directory page",
        (String presentCondition) ->
            webDriverHelpers.selectFromCombobox(CASE_PRESENT_CONDITION_COMBOBOX, presentCondition));

    And(
        "I apply Present Condition filter on Case directory page to different than actual",
        () -> {
          String differentPresentCondition = apiState.getLastCreatedPerson().getPresentCondition();
          while (differentPresentCondition.equals(
              apiState.getLastCreatedPerson().getPresentCondition())) {
            differentPresentCondition = PresentCondition.getRandomPresentCondition();
          }
          webDriverHelpers.selectFromCombobox(
              CASE_PRESENT_CONDITION_COMBOBOX,
              PresentCondition.getValueFor(differentPresentCondition));
        });
    And(
        "I click APPLY BUTTON in Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });

    Then(
        "I apply Case origin {string} on Case directory page",
        (String caseOrigin) ->
            webDriverHelpers.selectFromCombobox(CASE_ORIGIN_FILTER_COMBOBOX, caseOrigin));
    Then(
        "I apply Community {string} on Case directory page",
        (String community) ->
            webDriverHelpers.selectFromCombobox(CASE_COMMUNITY_FILTER_COMBOBOX, community));

    And(
        "I apply Region filter {string} on Case directory page",
        (String region) ->
            webDriverHelpers.selectFromCombobox(CASE_REGION_FILTER_COMBOBOX, region));
    And(
        "I apply Surveillance Officer filter {string} on Case directory page",
        (String survoff) ->
            webDriverHelpers.selectFromCombobox(CASE_SURVOFF_FILTER_COMBOBOX, survoff));
    And(
        "I apply Reporting User filter {string} on Case directory page",
        (String reportingUser) ->
            webDriverHelpers.fillInWebElement(CASE_REPORTING_USER_FILTER, reportingUser));
    And(
        "I apply Vaccination Status filter to {string} on Case directory page",
        (String vaccinationStatus) ->
            webDriverHelpers.selectFromCombobox(
                CASE_VACCINATION_STATUS_FILTER_COMBOBOX, vaccinationStatus));
    And(
        "I apply Quarantine filter to {string} on Case directory page",
        (String quarantine) ->
            webDriverHelpers.selectFromCombobox(CASE_QUARANTINE_FILTER_COMBOBOX, quarantine));
    And(
        "I apply Reinfection filter to {string} on Case directory page",
        (String reinfection) -> {
          webDriverHelpers.selectFromCombobox(CASE_REINFECTION_FILTER_COMBOBOX, reinfection);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
        });
    And(
        "I apply Date type filter to {string} on Case directory page",
        (String dataType) ->
            webDriverHelpers.selectFromCombobox(CASE_DATA_TYPE_FILTER_COMBOBOX, dataType));

    And(
        "I fill Cases from input to {int} days before mocked Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedCase().getReportDate().toInstant(),
                          ZoneId.systemDefault())
                      .minusDays(number)));
        });
    And(
        "I fill Cases from input to {int} days before UI Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(CreateNewCaseSteps.caze.getDateOfReport().minusDays(number)));
        });
    And(
        "I fill Cases from input to {int} days before mocked Cases created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedCases().get(0).getReportDate().toInstant(),
                          ZoneId.systemDefault())
                      .minusDays(number)));
        });
    And(
        "I fill Cases from input to {int} days after before mocked Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX, formatter.format(LocalDate.now().plusDays(number)));
        });
    And(
        "I fill Cases from input to {int} days after before UI Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(CreateNewCaseSteps.caze.getDateOfReport().plusDays(number)));
        });
    And(
        "I click All button in Case Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALLBUTTON);
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
        "I click {string} checkbox on Case directory page",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Only cases without geo coordinates"):
            case ("Nur F\u00E4lle ohne Geo-Koordinaten"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITHOUT_GEO_COORDINATES_CHECKBOX);
              break;
            case ("Only cases without responsible officer"):
            case ("Nur F\u00E4lle ohne verantwortlichen Beauftragten"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_WITHOUT_RESPONSIBLE_OFFICER_CHECKBOX);
              break;
            case ("Only cases with extended quarantine"):
            case ("Nur F\u00E4lle mit verl\u00E4ngerter Isolation"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_EXTENDED_QUARANTINE_CHECKBOX);
              break;
            case ("Only cases with reduced quarantine"):
            case ("Nur F\u00E4lle mit verk\u00FCrzter Isolation"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_REDUCED_QUARANTINE_CHECKBOX);
              break;
            case ("Help needed in quarantine"):
            case ("Ma\u00DFnahmen zur Gew\u00E4hrleistung der Versorgung"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_HELP_NEEDED_IN_QUARANTINE_CHECKBOX);
              break;
            case ("Only cases with events"):
            case ("Nur F\u00E4lle mit Ereignissen"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_EVENTS_CHECKBOX);
              break;
            case ("Only cases from other instances"):
            case ("Nur F\u00E4lle von anderen Instanzen"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_FROM_OTHER_INSTANCES_CHECKBOX);
              break;
            case ("Only cases with reinfection"):
            case ("Nur F\u00E4lle mit Reinfektion"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITH_REINFECTION_CHECKBOX);
              break;
            case ("Include cases from other jurisdictions"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_FROM_OTHER_JURISDICTIONS_CHECKBOX);
              break;
            case ("Only cases with fulfilled reference definition"):
            case ("Nur F\u00E4lle mit erf\u00FCllter Referenzdefinition"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CASES_WITH_FULFILLED_REFERENCE_DEFINITION_CHECKBOX);
              break;
            case ("Only port health cases without a facility"):
            case ("Nur Einreisef\u00E4lle ohne zugewiesene Einrichtung"):
              webDriverHelpers.clickOnWebElementBySelector(CASES_WITHOUT_FACILITY_CHECKBOX);
              break;
          }
        });

    And(
        "I fill Cases to input to {int} days after mocked Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedCase().getReportDate().toInstant(),
                          ZoneId.systemDefault())
                      .plusDays(number)));
        });
    And(
        "I fill Cases to input to {int} days after UI Case created on Case directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(CreateNewCaseSteps.caze.getDateOfReport().plusDays(number)));
        });
    And(
        "I apply Year filter different than Person has on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_YEAR_FILTER,
                getRandomNumberForBirthDateDifferentThanCreated(
                        apiState.getLastCreatedPerson().getBirthdateYYYY(), 1900, 2002)
                    .toString()));
    And(
        "I apply Year filter of last api created Person on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_YEAR_FILTER, apiState.getLastCreatedPerson().getBirthdateYYYY().toString()));
    And(
        "I apply Year filter of Person attached to last created UI Case on Case directory page",
        () -> {
          String year = Integer.toString(CreateNewCaseSteps.caze.getDateOfBirth().getYear());
          webDriverHelpers.selectFromCombobox(CASE_YEAR_FILTER, year);
        });
    And(
        "I apply Month filter of Person attached to last created UI Case on Case directory page",
        () -> {
          String month = Integer.toString(CreateNewCaseSteps.caze.getDateOfBirth().getMonthValue());
          webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month);
        });
    And(
        "I apply Day filter of Person attached to last created UI Case on Case directory page",
        () -> {
          String day = Integer.toString(CreateNewCaseSteps.caze.getDateOfBirth().getDayOfMonth());
          webDriverHelpers.selectFromCombobox(CASE_DAY_FILTER, day);
        });
    And(
        "I apply Year filter other than Person attached has to last created UI Case on Case directory page",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_YEAR_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      CreateNewCaseSteps.caze.getDateOfBirth().getYear(), 1900, 2002)
                  .toString());
        });
    And(
        "I apply Month filter other than Person attached has to last created UI Case on Case directory page",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_MONTH_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      CreateNewCaseSteps.caze.getDateOfBirth().getMonthValue(), 1, 12)
                  .toString());
        });
    And(
        "I apply Day filter other than Person attached has to last created UI Case on Case directory page",
        () -> {
          webDriverHelpers.selectFromCombobox(
              CASE_DAY_FILTER,
              getRandomNumberForBirthDateDifferentThanCreated(
                      CreateNewCaseSteps.caze.getDateOfBirth().getDayOfMonth(), 1, 28)
                  .toString());
        });
    And(
        "I apply {string} to combobox on Case Directory Page",
        (String caseParameter) ->
            webDriverHelpers.selectFromCombobox(CASE_DISPLAY_FILTER_COMBOBOX, caseParameter));

    And(
        "I apply Month filter different than Person has on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_MONTH_FILTER,
                getRandomNumberForBirthDateDifferentThanCreated(
                        apiState.getLastCreatedPerson().getBirthdateMM(), 1, 12)
                    .toString()));
    And(
        "I apply Month filter of last api created Person on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_MONTH_FILTER, apiState.getLastCreatedPerson().getBirthdateMM().toString()));
    And(
        "I apply Day filter different than Person has on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_DAY_FILTER,
                getRandomNumberForBirthDateDifferentThanCreated(
                        apiState.getLastCreatedPerson().getBirthdateDD(), 1, 27)
                    .toString()));
    And(
        "I apply Day filter of last api created Person on Case directory page",
        () ->
            webDriverHelpers.selectFromCombobox(
                CASE_DAY_FILTER, apiState.getLastCreatedPerson().getBirthdateDD().toString()));
    And(
        "I apply Facility category filter {string} on Case directory page",
        (String facilityCategory) ->
            webDriverHelpers.selectFromCombobox(
                CASE_FACILITY_CATEGORY_FILTER_COMBOBOX,
                FacilityCategory.getValueFor(facilityCategory)));
    And(
        "I apply Facility type filter to {string} on Case directory page",
        (String facilityType) ->
            webDriverHelpers.selectFromCombobox(CASE_FACILITY_TYPE_FILTER_COMBOBOX, facilityType));
    And(
        "I apply Facility filter to {string} on Case directory page",
        (String facility) ->
            webDriverHelpers.selectFromCombobox(CASE_FACILITY_FILTER_COMBOBOX, facility));
    And(
        "I apply District filter {string} on Case directory page",
        (String district) ->
            webDriverHelpers.selectFromCombobox(
                CASE_DISTRICT_FILTER_COMBOBOX, DistrictsValues.getValueFor(district)));

    And(
        "I apply Year filter {string} on Case directory page",
        (String year) -> webDriverHelpers.selectFromCombobox(CASE_YEAR_FILTER, year));

    And(
        "I apply Month filter {string} on Case directory page",
        (String month) -> webDriverHelpers.selectFromCombobox(CASE_MONTH_FILTER, month));

    And(
        "I apply Day filter {string} on Case directory page",
        (String day) -> webDriverHelpers.selectFromCombobox(CASE_DAY_FILTER, day));
  }

  private Number getRandomNumberForBirthDateDifferentThanCreated(Number created, int min, int max) {
    Number replacement = created;
    while (created.equals(replacement)) {
      replacement = faker.number().numberBetween(min, max);
    }
    return replacement;
  }
}
