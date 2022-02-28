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
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_PARTICIPANTS_TAB;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.TITLE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.APPLY_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.BULK_ACTIONS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.CREATED_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.CREATE_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.ENTER_BULK_EDIT_MODE_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENTS_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_GROUP_ID_IN_GRID;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_GROUP_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_STATUS_FILTER_BUTTONS;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTERED_EVENT_LINK_EVENT_FORM;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTER_BY_DISEASE;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTER_BY_RISK_LEVEL;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTER_BY_SOURCE_TYPE;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTER_BY_TYPE_OF_PLACE;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FIRST_CHECKBOX_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FIRST_EVENT_GROUP;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FIRST_EVENT_ID_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.GROUP_EVENTS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.GROUP_ID_COLUMN;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.ID_FIELD_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.LINKED_EVENT_GROUP_ID;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.LINK_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.LINK_EVENT_BUTTON_EDIT_PAGE;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.MORE_BUTTON_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.RESET_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.SAVE_BUTTON_IN_LINK_FORM;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.SEARCH_EVENT_BY_FREE_TEXT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.SEARCH_EVENT_BY_FREE_TEXT_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.TOTAL_EVENTS_COUNTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.UNLINK_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.RiskLevelValues;
import org.sormas.e2etests.enums.SourceTypeValues;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.events.EventDirectoryPage;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pojo.web.EventGroup;
import org.sormas.e2etests.services.EventGroupService;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class EventDirectorySteps implements En {

  @Inject
  public EventDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      DataOperations dataOperations,
      AssertHelpers assertHelpers,
      EnvironmentManager environmentManager,
      EventGroupService eventGroupService) {
    When(
        "I fill EVENT ID filter by API",
        () -> {
          String eventUuid = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.fillInWebElement(
              SEARCH_EVENT_BY_FREE_TEXT,
              dataOperations.getPartialUuidFromAssociatedLink(eventUuid));
        });

    When(
        "I click checkbox to choose all Event results on Event Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_CHECKBOX_EVENT_DIRECTORY);
          webDriverHelpers.waitForPageLoaded();
        });
    And(
        "I click on Bulk Actions combobox on Event Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_EVENT_DIRECTORY));

    And(
        "I click on Group Events from Bulk Actions combobox on Event Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(GROUP_EVENTS_EVENT_DIRECTORY));
    When(
        "I navigate to the last created Event page via URL",
        () -> {
          String createdEventUUID = CreateNewEventSteps.newEvent.getUuid();
          String LAST_CREATED_EVENT_PAGE_URL =
              environmentUrl + "/sormas-webdriver/#!events/data/" + createdEventUUID;
          webDriverHelpers.accessWebSite(LAST_CREATED_EVENT_PAGE_URL);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 50);
        });

    When(
        "^I click on ([^\"]*) Radiobutton on Event Directory Page$",
        (String buttonName) -> {
          webDriverHelpers.clickWebElementByText(EVENTS_RADIO_BUTTON, buttonName);
          webDriverHelpers.waitForPageLoaded();
        });
    When(
        "I check that name appearing in hover is equal to name of linked Event group",
        () -> {
          EventGroup createdGroup = EditEventSteps.groupEvent;
          EventGroup collectedGroup =
              EventGroup.builder()
                  .name(webDriverHelpers.getWebElement(GROUP_ID_COLUMN).getAttribute("title"))
                  .build();
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedGroup, createdGroup, List.of("name"));
          webDriverHelpers.waitForPageLoaded();
        });

    When(
        "^I click on Link Event button on Event Directory Page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_BUTTON));
    When(
        "^I click on Link Event button on Edit Event Page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_BUTTON_EDIT_PAGE));

    When(
        "^I click on Unlink Event button on Event Directory Page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(UNLINK_EVENT_BUTTON));

    When(
        "^I fill Id filter with Id of last created event in Link Event to group form$",
        () ->
            webDriverHelpers.fillInWebElement(
                ID_FIELD_FILTER, apiState.getCreatedEvent().getUuid()));
    When(
        "^I click on filtered Event in Link Event to group form$",
        () -> webDriverHelpers.clickOnWebElementBySelector(FILTERED_EVENT_LINK_EVENT_FORM));
    When(
        "^I click on first Event Group on the list in Link Event form$",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_EVENT_GROUP));

    When(
        "^I click on SAVE button in Link Event to group form$",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_IN_LINK_FORM));

    When(
        "^I click on Linked Group Id on Edit Event Page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINKED_EVENT_GROUP_ID));
    When(
        "^I click on Group Id in Events result on Event Directory Page$",
        () -> webDriverHelpers.clickOnWebElementBySelector(EVENT_GROUP_ID_IN_GRID));

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
        "I filter by last created group in Event Directory Page",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.fillInWebElement(EVENT_GROUP_INPUT, EditEventSteps.groupEvent.getUuid());
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
        "I click on the first row from event participant",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_EVENT_PARTICIPANT);
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
        "I hover to Event Groups column of the Event result",
        () -> {
          webDriverHelpers.scrollToElement(GROUP_ID_COLUMN);
          webDriverHelpers.hoverToElement(GROUP_ID_COLUMN);
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
        "I click on the More button on Event directory page",
        () -> webDriverHelpers.clickOnWebElementBySelector(MORE_BUTTON_EVENT_DIRECTORY));
    When(
        "I click Enter Bulk Edit Mode on Event directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ENTER_BULK_EDIT_MODE_EVENT_DIRECTORY);
          webDriverHelpers.waitForPageLoaded();
        });
    When(
        "I click on the created event participant from the list",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATED_PARTICIPANT));

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
