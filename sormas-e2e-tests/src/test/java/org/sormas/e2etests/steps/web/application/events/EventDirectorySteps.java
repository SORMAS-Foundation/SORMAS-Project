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

package org.sormas.e2etests.steps.web.application.events;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.events.EditEventPage.*;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.*;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.events.EventDirectoryPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class EventDirectorySteps implements En {

  @Inject
  public EventDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      DataOperations dataOperations,
      AssertHelpers assertHelpers,
      EnvironmentManager environmentManager) {

    When(
        "I fill EVENT ID filter by API",
        () -> {
          String eventUuid = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.fillInWebElement(
              SEARCH_EVENT_BY_FREE_TEXT,
              dataOperations.getPartialUuidFromAssociatedLink(eventUuid));
        });

    When(
        "I click on the NEW EVENT button",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                EventDirectoryPage.NEW_EVENT_BUTTON, TITLE_INPUT));

    When(
        "I select random Risk level filter among the filter options from API",
        () -> {
          String riskLevel = apiState.getCreatedEvent().getRiskLevel();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_RISK_LEVEL, RiskLevelValues.getCaptionForName(riskLevel));
        });

    When(
        "I select random Risk level filter among the filter options",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_RISK_LEVEL, RiskLevelValues.getRandomRiskLevelCaption());
        });

    When(
        "I select random Disease filter among the filter options from API",
        () -> {
          String disease = apiState.getCreatedEvent().getDisease();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_DISEASE, DiseasesValues.getCaptionForName(disease));
        });

    When(
        "I select random Disease filter among the filter options",
        () -> {
          String disease = DiseasesValues.getRandomDiseaseCaption();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(FILTER_BY_DISEASE, disease);
        });

    When(
        "I click on Show more filters in Events",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(
                EventDirectoryPage.EVENT_SHOW_MORE_FILTERS));

    When(
        "I select Signal filter from quick filter",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.clickOnWebElementBySelector(EventDirectoryPage.EVENT_SIGNAL);
        });

    When(
        "I select Event filter from quick filter",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.clickOnWebElementBySelector(EventDirectoryPage.EVENT_EVENT);
        });

    When(
        "I select Screening filter from quick filter",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.clickOnWebElementBySelector(EventDirectoryPage.EVENT_SCREENING);
        });

    When(
        "I select Cluster filter from quick filter",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.clickOnWebElementBySelector(EventDirectoryPage.EVENT_CLUSTER);
        });

    When(
        "I select Dropped filter from quick filter",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.clickOnWebElementBySelector(EventDirectoryPage.EVENT_DROPPED);
        });

    When(
        "I select Source Type among the filter options from API",
        () -> {
          String sourceType = apiState.getCreatedEvent().getSrcType();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_SOURCE_TYPE, SourceTypeValues.getCaptionForName((sourceType)));
        });

    When(
        "I select random Source Type among the filter options",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_SOURCE_TYPE, SourceTypeValues.getRandomSourceTypeCaption());
        });

    When(
        "I select Type of Place field among the filter options from API",
        () -> {
          String sourceTypeOfPlace = apiState.getCreatedEvent().getTypeOfPlace();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_TYPE_OF_PLACE, TypeOfPlace.getValueFor(sourceTypeOfPlace));
        });

    When(
        "I select random Type of Place field among the filter options",
        () -> {
          String typeOfPlace = TypeOfPlace.getRandomTypeOfPlace();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_TYPE_OF_PLACE, TypeOfPlace.getValueFor(typeOfPlace));
        });

    When(
        "^I check if it appears under ([^\"]*) filter in event directory",
        (String eventStatus) -> {
          final String eventUuid = CreateNewEventSteps.newEvent.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(eventUuid));
          webDriverHelpers.clickWebElementByText(EVENT_STATUS_FILTER_BUTTONS, eventStatus);
          TimeUnit.SECONDS.sleep(3); // TODO check in Jenkins if is a stable fix
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_STATUS_FILTER_BUTTONS);
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_PARTICIPANTS_TAB);
        });

    When(
        "^I search for specific event in event directory",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(RESET_FILTER, 35);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER);
          final String eventUuid = CreateNewEventSteps.newEvent.getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SEARCH_EVENT_BY_FREE_TEXT_INPUT, 20);
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_EVENT_BY_FREE_TEXT_INPUT, eventUuid);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
        });

    When(
        "I click on the searched event",
        () -> {
          final String eventUuid = CreateNewEventSteps.newEvent.getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              getByEventUuid(eventUuid));
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "I check if participant appears in the event participants list",
        () -> {
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(personUuid));
        });

    When(
        "I am accessing the event tab using the created event via api",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.EVENTS_BUTTON);
          final String eventUuid = apiState.getCreatedEvent().getUuid();
          final String eventLinkPath = "/sormas-webdriver/#!events/data/";
          webDriverHelpers.accessWebSite(
              environmentManager.getEnvironmentUrlForMarket(locale) + eventLinkPath + eventUuid);
        });

    When(
        "I apply on the APPLY FILTERS button from Event",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              APPLY_FILTERS_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(10);
        });

    When(
        "I click on the RESET FILTERS button from Event",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              RESET_FILTERS_BUTTON, 30);
          TimeUnit.SECONDS.sleep(10);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(10);
        });

    When(
        "I click on the created event participant from the list",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATED_PARTICIPANT);
        });

    When(
        "I click on New Task from event tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_TASK_BUTTON));

    When(
        "I open the first event from events list",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_EVENT_ID_BUTTON));

    And(
        "I click Create Case for Event Participant",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_CASE_BUTTON));

    Then(
        "I check that number of displayed Event results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS),
                        number.intValue(),
                        "Number of displayed cases is not correct")));

    Then(
        "I check the number of displayed Event results from All button is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        Integer.parseInt(
                            webDriverHelpers.getTextFromPresentWebElement(TOTAL_EVENTS_COUNTER)),
                        number.intValue(),
                        "Number of displayed cases is not correct")));
  }
}
