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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_COMMUNITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DATA_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DISTRICT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_REGION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_TO_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_PARTICIPANTS_TAB;
import static org.sormas.e2etests.pages.application.events.EditEventPage.FIRST_EVENT_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.NEW_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.TITLE_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.APPLY_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.BULK_ACTIONS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.CREATED_PARTICIPANT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.CREATE_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.DATE_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.ENTER_BULK_EDIT_MODE_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENTS_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENTS_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENTS_TABLE_DATA;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENTS_TABLE_ROW;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_DISPLAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_GROUP_ID_IN_GRID;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_GROUP_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_INVESTIGATION_STATUS;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_MANAGEMENT_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_STATUS_FILTER_BUTTONS;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTERED_EVENT_LINK_EVENT_FORM;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTER_BY_DISEASE;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.FILTER_BY_REPORTING_USER;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.EventGroup;
import org.sormas.e2etests.entities.services.EventGroupService;
import org.sormas.e2etests.entities.services.EventService;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.EventReferenceDateOptions;
import org.sormas.e2etests.enums.RiskLevelValues;
import org.sormas.e2etests.enums.SourceTypeValues;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.events.EventDirectoryPage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class EventDirectorySteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;

  @Inject
  public EventDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      DataOperations dataOperations,
      AssertHelpers assertHelpers,
      EventGroupService eventGroupService,
      EventService eventService,
      SoftAssert softly,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "I fill EVENT ID filter by API",
        () -> {
          String eventUuid = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.fillInWebElement(
              SEARCH_EVENT_BY_FREE_TEXT,
              dataOperations.getPartialUuidFromAssociatedLink(eventUuid));
        });
    When(
        "I navigate to the last created through API Event page via URL",
        () -> {
          String eventLinkPath = "/sormas-ui/#!events/data/";
          String createdEventUUID = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.accessWebSite(
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + eventLinkPath
                  + createdEventUUID);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 50);
        });

    When(
        "I fill Event Group Id filter to one assigned to created event on Event Directory Page",
        () -> {
          String eventGroupId = EditEventSteps.groupEvent.getUuid();
          webDriverHelpers.fillInWebElement(
              EVENT_GROUP_INPUT, dataOperations.getPartialUuidFromAssociatedLink(eventGroupId));
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
          String eventLinkPath = "/sormas-ui/#!events/data/";
          String createdEventUUID = CreateNewEventSteps.newEvent.getUuid();
          webDriverHelpers.accessWebSite(
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + eventLinkPath
                  + createdEventUUID);
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
    And(
        "I apply {string} to combobox on Event Directory Page",
        (String eventParameter) -> {
          webDriverHelpers.selectFromCombobox(EVENT_DISPLAY_COMBOBOX, eventParameter);
          webDriverHelpers.waitForPageLoaded();
        });
    And(
        "I apply Date type filter to {string} on Event directory page",
        (String dataType) ->
            webDriverHelpers.selectFromCombobox(CASE_DATA_TYPE_FILTER_COMBOBOX, dataType));
    And(
        "I fill Event to input to {int} days after mocked Event created on Event directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedEvent().getReportDateTime().toInstant(),
                          ZoneId.systemDefault())
                      .plusDays(number)));
        });
    And(
        "I fill Event from input to {int} days before mocked Event created on Event directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  LocalDate.ofInstant(
                          apiState.getCreatedEvent().getReportDateTime().toInstant(),
                          ZoneId.systemDefault())
                      .minusDays(number)));
        });
    And(
        "I fill Event from input to {int} days after before mocked Event created on Event directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX, formatter.format(LocalDate.now().plusDays(number)));
        });

    And(
        "I apply mocked Person Id filter on Event directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(
                PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT, "TestName TestSurname"));
    And(
        "I filter by mocked EventId on Event directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(
                  "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_EVENT_BY_FREE_TEXT, partialUuid);
        });
    And(
        "I filter by mocked EventGroupId on Event directory page",
        () ->
            webDriverHelpers.fillAndSubmitInWebElement(EVENT_GROUP_INPUT, "TestName TestSurname"));
    When(
        "I select random Risk level filter among the filter options from API",
        () -> {
          String riskLevel = apiState.getCreatedEvent().getRiskLevel();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              FILTER_BY_RISK_LEVEL, RiskLevelValues.getCaptionForName(riskLevel));
        });
    When(
        "I fill Reporting User filter to {string} on Event Directory Page",
        (String reportingUser) -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(FILTER_BY_REPORTING_USER, reportingUser);
        });
    And(
        "I apply Region filter to {string} on Event directory page",
        (String region) ->
            webDriverHelpers.selectFromCombobox(CASE_REGION_FILTER_COMBOBOX, region));
    And(
        "I apply District filter to {string} on Event directory page",
        (String district) ->
            webDriverHelpers.selectFromCombobox(CASE_DISTRICT_FILTER_COMBOBOX, district));
    And(
        "I apply Event Management Status filter to {string} on Event directory page",
        (String managementStatus) ->
            webDriverHelpers.selectFromCombobox(EVENT_MANAGEMENT_FILTER, managementStatus));
    And(
        "I apply Event Investigation Status filter to {string} on Event directory page",
        (String investigationStatus) ->
            webDriverHelpers.selectFromCombobox(EVENT_INVESTIGATION_STATUS, investigationStatus));
    Then(
        "I apply Community filter to {string} on Event directory page",
        (String community) ->
            webDriverHelpers.selectFromCombobox(CASE_COMMUNITY_FILTER_COMBOBOX, community));

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
        "I select Report Date among Event Reference Date options",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              DATE_TYPE_COMBOBOX, EventReferenceDateOptions.REPORT_DATE.toString());
        });

    When(
        "I fill in a date range in Date of Event From Epi Week and ...To fields",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          eventService.timeRange = buildTimeRange();
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              eventService
                  .timeRange[0]
                  .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                  .toString());
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              eventService
                  .timeRange[1]
                  .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                  .toString());
        });

    When(
        "I check that the dates of displayed Event results are correct",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          List<Map<String, String>> tableRowsData = getTableRowsData();
          for (int i = 0; i < tableRowsData.size(); i++) {
            String dateCell =
                tableRowsData.get(i).get(EventsTableColumnsHeaders.REPORT_DATE_HEADER.toString());
            LocalDate date = getLocalDateFromColumns(dateCell.substring(0, dateCell.indexOf(" ")));
            softly.assertTrue(
                date.isAfter(eventService.timeRange[0].minusDays(1))
                    && date.isBefore(eventService.timeRange[1].plusDays(1)),
                "The date(s) of displayed events are out of the requested range");
          }
          softly.assertAll();
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

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(EVENTS_TABLE_DATA);
          tableData.forEach(
              dataText -> {
                webDriverHelpers.scrollToElementUntilIsVisible(dataText);
                indexWithData.put(atomicInt.getAndIncrement(), dataText.getText());
              });
          tableDataList.add(indexWithData);
        });
    List<Map<String, String>> tableObjects = new ArrayList<>();
    tableDataList.forEach(
        row -> {
          ConcurrentHashMap<String, String> objects = new ConcurrentHashMap<>();
          headers.forEach((headerText, index) -> objects.put(headerText, row.get(index)));
          tableObjects.add(objects);
        });
    return tableObjects;
  }

  private List<WebElement> getTableRows() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENTS_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(EVENTS_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENTS_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(EVENTS_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(EVENTS_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(EVENTS_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }

  private LocalDate getLocalDateFromColumns(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    try {
      return LocalDate.parse(date, formatter);
    } catch (Exception e) {
      throw new WebDriverException(
          String.format(
              "Unable to parse date: %s due to caught exception: %s", date, e.getMessage()));
    }
  }

  private LocalDate[] buildTimeRange() {
    LocalDate[] timeRange = new LocalDate[2];
    timeRange[0] = LocalDate.now().minusMonths(1);
    timeRange[1] = LocalDate.now();
    return timeRange;
  }
}
