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

import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.*;

import cucumber.api.java8.En;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.web.EventActionTableEntry;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class EventActionsTableSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  private EventActionTableEntry eventActionTableEntry;

  @Inject
  public EventActionsTableSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    Then(
        "I search last created Event by API using EVENT UUID and wait for (\\d+) entries in the table",
        (Integer expectedEntries) -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              FILTER_BY_GENERAL_INPUT,
              getPartialUuidFromAssociatedLink(apiState.getCreatedEvent().getUuid()));
          webDriverHelpers.waitUntilNumberOfElementsIsExactly(
              EVENT_ACTIONS_TABLE_ROW, expectedEntries);
        });

    Then(
        "^I am checking if all the fields are correctly displayed in the Event directory Actions table$",
        () -> {
          softly.assertEquals(
              eventActionTableEntry.getEventId(),
              getPartialUuidFromAssociatedLink(apiState.getCreatedEvent().getUuid()).toUpperCase(),
              "Event ID is not correct");
          softly.assertEquals(
              eventActionTableEntry.getActionTitle(),
              apiState.getCreatedAction().getTitle(),
              "Action title is not correct");
          softly.assertEquals(
              eventActionTableEntry.getActionCreationDate().toString().substring(0, 10),
              apiState.getCreatedAction().getDate().toString(),
              "Action creation date is not correct");
          softly.assertEquals(
              eventActionTableEntry.getActionChangeDate().toString().substring(0, 10),
              apiState.getCreatedAction().getDate().toString(),
              "Action change date is not correct");
          softly.assertEquals(
              eventActionTableEntry.getActionStatus().toUpperCase(),
              apiState.getCreatedAction().getActionStatus(),
              "Action status is not correct");
          softly.assertEquals(
              eventActionTableEntry.getActionPriority(),
              apiState.getCreatedAction().getPriority(),
              "Priority is not correct");
          softly.assertEquals(
              eventActionTableEntry.getActionLastModifiedBy(),
              "National USER",
              "Last modified by user is not correct");
          softly.assertAll();
        });

    Then(
        "^I collect the event actions from table view$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          eventActionTableEntry =
              EventActionTableEntry.builder()
                  .eventId(
                      tableRowsData.get(0).get(EventActionsTableColumnsHeaders.EVENT_ID.toString()))
                  .actionTitle(
                      tableRowsData
                          .get(0)
                          .get(EventActionsTableColumnsHeaders.ACTION_TITLE.toString()))
                  .actionCreationDate(
                      getLocalDateTimeFromColumns(
                          tableRowsData
                              .get(0)
                              .get(
                                  EventActionsTableColumnsHeaders.ACTION_CREATION_DATE.toString())))
                  .actionChangeDate(
                      getLocalDateTimeFromColumns(
                          tableRowsData
                              .get(0)
                              .get(EventActionsTableColumnsHeaders.ACTION_CHANGE_DATE.toString())))
                  .actionStatus(
                      tableRowsData
                          .get(0)
                          .get(EventActionsTableColumnsHeaders.ACTION_STATUS.toString()))
                  .actionPriority(
                      tableRowsData
                          .get(0)
                          .get(EventActionsTableColumnsHeaders.ACTION_PRIORITY.toString()))
                  .actionLastModifiedBy(
                      tableRowsData
                          .get(0)
                          .get(EventActionsTableColumnsHeaders.ACTION_LAST_MODIFIED_BY.toString()))
                  .build();
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
          List<WebElement> tableData = table.findElements(EVENT_ACTIONS_TABLE_DATA);
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
    return baseSteps.getDriver().findElements(EVENT_ACTIONS_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_ACTIONS_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(EVENT_ACTIONS_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(EVENT_ACTIONS_COLUMN_HEADERS);
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy h:mm a");
    try {
      return LocalDateTime.parse(date, formatter);
    } catch (Exception e) {
      throw new WebDriverException(
          String.format(
              "Unable to parse date: %s due to caught exception: %s", date, e.getMessage()));
    }
  }

  private String getPartialUuidFromAssociatedLink(String associatedLink) {
    return StringUtils.left(associatedLink, 6);
  }
}
