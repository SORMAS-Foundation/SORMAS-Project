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

package org.sormas.e2etests.steps.web.application.events;

import cucumber.api.java8.En;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Action;
import org.sormas.e2etests.pojo.web.Event;
import org.sormas.e2etests.pojo.web.EventActionTableEntry;
import org.sormas.e2etests.pojo.web.Task;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.sormas.e2etests.steps.web.application.tasks.ColumnHeaders;
import org.sormas.e2etests.steps.web.application.tasks.CreateNewTaskSteps;

import javax.inject.Inject;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.function.Predicate.not;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.*;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.TABLE_DATA;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.TABLE_ROW;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;

public class EventActionsTableSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  private List<EventActionTableEntry> eventActionTableEntryList;

  @Inject
  public EventActionsTableSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      Action action,
      Event event,
      SoftAssertions softly,
      Properties properties) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;



    When(
        "^I search last created Action by API using Event UUID and wait for (\\d+) results to be displayed$",
        (Integer displayedResults) -> {
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEARCH_EVENT_BY_FREE_TEXT_INPUT);
            webDriverHelpers.fillAndSubmitInWebElement(
                    SEARCH_EVENT_BY_FREE_TEXT_INPUT, event.getUuid());
          webDriverHelpers.waitUntilNumberOfElementsIsReduceToGiven(TABLE_ROW, displayedResults);
        });

    When(
        "^I am checking if all the fields are correctly displayed in the Event directory Actions table$",
        () -> {
          EventActionTableEntry eventActionTableEntry =
              eventActionTableEntryList.stream()
                  .findFirst()
                  .orElseThrow();
          softly
              .assertThat(eventActionTableEntry.getEventId())
              .containsIgnoringCase(
                  getPartialUuidFromAssociatedLink(event.getUuid()));
          softly
              .assertThat(eventActionTableEntry.getActionTitle())
              .containsIgnoringCase(action.getTitle());
          softly
              .assertThat(eventActionTableEntry.getActionStatus())
              .containsIgnoringCase(action.getActionStatus());
          softly
                  .assertThat(eventActionTableEntry.getActionPriority())
                  .containsIgnoringCase(action.getPriority());
//          softly
//                  .assertThat(eventActionTableEntry.getActionCreationDate())
//                  .containsIgnoringCase(action.getDate());
          softly
                  .assertThat(eventActionTableEntry.getEventTitle())
                  .containsIgnoringCase(event.getTitle());

          softly.assertAll();
        });

    When(
        "^I collect the event actions from table view$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          eventActionTableEntryList = new ArrayList<>();
          tableRowsData.forEach(
              tableRow ->
                      eventActionTableEntryList.add(
                              EventActionTableEntry.builder()
                                      .eventId(tableRow.get(EventActionsTableColumnsHeaders.EVENT_ID.toString()))
                                      .actionChangeDate(getLocalDateTimeFromColumns(EventActionsTableColumnsHeaders.ACTION_CHANGE_DATE.toString()))
                                      .actionStatus(tableRow.get(EventActionsTableColumnsHeaders.ACTION_STATUS.toString()))
                                      .actionCreationDate(getLocalDateTimeFromColumns(EventActionsTableColumnsHeaders.ACTION_CREATION_DATE.toString()))
                                      .dateOfEvent(getLocalDateTimeFromColumns(EventActionsTableColumnsHeaders.DATE_OF_EVENT.toString()))
                                      .actionPriority(tableRow.get(EventActionsTableColumnsHeaders.ACTION_PRIORITY.toString()))
                                      .eventInvestigationStatus(tableRow.get(EventActionsTableColumnsHeaders.EVENT_INVESTIGATION_STATUS.toString()))
                                      .eventManagementStatus(tableRow.get(EventActionsTableColumnsHeaders.EVENT_MANAGEMENT_STATUS.toString()))
                                      .actionTitle(tableRow.get(EventActionsTableColumnsHeaders.ACTION_STATUS.toString()))
                                      .eventRiskLevel(tableRow.get(EventActionsTableColumnsHeaders.EVENT_RISK_LEVEL.toString()))
                                      .actionLastModifiedBy(tableRow.get(EventActionsTableColumnsHeaders.ACTION_LAST_MODIFIED_BY.toString()))
                                      .eventReportingUser(tableRow.get(EventActionsTableColumnsHeaders.EVENT_REPORTING_USER.toString()))
                                      .eventResponsibleUser(tableRow.get(EventActionsTableColumnsHeaders.EVENT_RESPONSIBLE_USER.toString()))
                                      .evolutionDateOfEvent(getLocalDateTimeFromColumns(EventActionsTableColumnsHeaders.EVOLUTION_DATE_OF_EVENT.toString()))
                                      .actionPriority(tableRow.get(EventActionsTableColumnsHeaders.ACTION_PRIORITY.toString()))
                                      .eventStatus(tableRow.get(EventActionsTableColumnsHeaders.EVENT_STATUS.toString()))
                                      .build()));

        });
  }

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(TABLE_DATA);
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
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_ACTIONS_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_ACTIONS_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(EVENT_ACTIONS_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(EVENT_ACTIONS_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }

    private LocalDateTime getLocalDateTimeFromColumns(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        return LocalDateTime.parse(date, formatter);
    }


    private String getPartialUuidFromAssociatedLink(String associatedLink) {
    return StringUtils.left(associatedLink, 6);
  }
}
