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

package org.sormas.e2etests.steps.web.application.tasks;

import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_POPUP;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;

import cucumber.api.java8.En;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Task;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;

public class TaskManagementSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  private List<Task> taskTableRows;

  @Inject
  public TaskManagementSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      SoftAssertions softly,
      Properties properties) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "^I click on the NEW TASK button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(NEW_TASK_BUTTON, TASK_TYPE_COMBOBOX));

    When(
        "^I open last created task$",
        () -> {
          By lastTaskEditButton =
              By.xpath(
                  String.format(
                      EDIT_BUTTON_XPATH_BY_TEXT, CreateNewTaskSteps.task.getCommentsOnTask()));
          do {
            webDriverHelpers.scrollInTable(10);
          } while (!webDriverHelpers.isElementVisibleWithTimeout(lastTaskEditButton, 2));
          webDriverHelpers.clickOnWebElementBySelector(lastTaskEditButton);
          webDriverHelpers.isElementVisibleWithTimeout(TASK_POPUP, 5);
        });

    When(
        "^I search last created task by Case UUID$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(GENERAL_SEARCH_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, EditCaseSteps.aCase.getUuid());
        });

    When(
        "^I am checking if the associated linked event appears in task management and click on it$",
        () -> {
          String eventUuid = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(GENERAL_SEARCH_INPUT, eventUuid);
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
        });

    When(
        "^I search last created task by API using Contact UUID$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(GENERAL_SEARCH_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, apiState.getCreatedContact().getUuid());
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
        });

    When(
        "^I am checking if all the fields are correctly displayed in the Task Management table$",
        () -> {
          org.sormas.e2etests.pojo.api.Task expectedTask = apiState.getCreatedTask();
          Task actualTask = taskTableRows.get(1);
          softly
              .assertThat(apiState.getCreatedContact().getUuid())
              .containsIgnoringCase(
                  getPartialUuidFromAssociatedLink(actualTask.getAssociatedLink()));
          softly
              .assertThat(actualTask.getAssociatedLink())
              .containsIgnoringCase(apiState.getCreatedContact().getPerson().getFirstName());
          softly
              .assertThat(actualTask.getAssociatedLink())
              .containsIgnoringCase(apiState.getCreatedContact().getPerson().getLastName());
          softly
              .assertThat(actualTask.getTaskContext())
              .isEqualToIgnoringCase(expectedTask.getTaskContext());
          softly
              .assertThat(actualTask.getTaskType())
              .isEqualTo(properties.getProperty("TaskType." + expectedTask.getTaskType()));
          softly
              .assertThat(actualTask.getRegion())
              .isEqualTo(apiState.getCreatedContact().getRegion().getCaption());
          softly
              .assertThat(actualTask.getDistrict())
              .isEqualTo(apiState.getCreatedContact().getDistrict().getCaption());
          softly
              .assertThat(actualTask.getPriority())
              .isEqualToIgnoringCase(expectedTask.getPriority());
          softly
              .assertThat(actualTask.getAssignedTo())
              .containsIgnoringCase(expectedTask.getAssigneeUser().getFirstName());
          softly
              .assertThat(actualTask.getAssignedTo())
              .containsIgnoringCase(expectedTask.getAssigneeUser().getLastName());
          softly
              .assertThat(actualTask.getCommentsOnTask())
              .isEqualTo(expectedTask.getCreatorComment());
          softly
              .assertThat(actualTask.getCommentsOnExecution())
              .isEqualTo(expectedTask.getAssigneeReply());
          softly
              .assertThat(actualTask.getTaskStatus())
              .isEqualToIgnoringCase(expectedTask.getTaskStatus());
          softly.assertAll();
        });

    When(
        "^I collect the task column objects$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          taskTableRows = new ArrayList<>();
          tableRowsData.forEach(
              tableRow ->
                  taskTableRows.add(
                      Task.builder()
                          .associatedLink(tableRow.get(ColumnHeaders.ASSOCIATED_LINK.toString()))
                          .taskContext(tableRow.get(ColumnHeaders.TASK_CONTEXT.toString()))
                          .taskType(tableRow.get(ColumnHeaders.TASK_TYPE.toString()))
                          .region(tableRow.get(ColumnHeaders.REGION.toString()))
                          .district(tableRow.get(ColumnHeaders.DISTRICT.toString()))
                          .priority(tableRow.get(ColumnHeaders.PRIORITY.toString()))
                          .suggestedStartDateTime(
                              getLocalDateTimeFromColumns(
                                  tableRow.get(ColumnHeaders.SUGGESTED_START.toString())))
                          .dueDateDateTime(
                              getLocalDateTimeFromColumns(
                                  tableRow.get(ColumnHeaders.DUE_DATE.toString())))
                          .assignedTo(tableRow.get(ColumnHeaders.ASSIGNED_TO.toString()))
                          .commentsOnExecution(
                              tableRow.get(ColumnHeaders.COMMENTS_ON_EXECUTION.toString()))
                          .commentsOnTask(tableRow.get(ColumnHeaders.COMMENTS_ON_TASK.toString()))
                          .taskStatus(tableRow.get(ColumnHeaders.TASK_STATUS.toString()))
                          .createdBy(tableRow.get(ColumnHeaders.CREATED_BY.toString()))
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
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COLUMN_HEADERS_TEXT);
    return baseSteps.getDriver().findElements(TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COLUMN_HEADERS_TEXT);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(COLUMN_HEADERS_TEXT);
    baseSteps
        .getDriver()
        .findElements(COLUMN_HEADERS_TEXT)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }

  private LocalDateTime getLocalDateTimeFromColumns(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
    try {
      return LocalDateTime.parse(date, formatter);
    } catch (Exception e) {
      throw new WebDriverException(String.format("Unable to parse date: %s", date));
    }
  }

  private String getPartialUuidFromAssociatedLink(String associatedLink) {
    return StringUtils.left(associatedLink, 6);
  }
}
