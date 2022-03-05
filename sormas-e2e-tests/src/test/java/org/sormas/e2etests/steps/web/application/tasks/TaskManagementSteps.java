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

package org.sormas.e2etests.steps.web.application.tasks;

import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.APPLY_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.RESET_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_POPUP;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;

import cucumber.api.java8.En;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.web.Task;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.testng.asserts.SoftAssert;

public class TaskManagementSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  private List<Task> taskTableRows;

  @Inject
  public TaskManagementSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      SoftAssert softly,
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(lastTaskEditButton);
          webDriverHelpers.isElementVisibleWithTimeout(TASK_POPUP, 5);
        });

    When(
        "^I search last created task by Case UUID$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GENERAL_SEARCH_INPUT, 50);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, EditCaseSteps.aCase.getUuid());
        });

    When(
        "^I am checking if the associated linked event appears in task management and click on it$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          String eventUuid = apiState.getCreatedEvent().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(GENERAL_SEARCH_INPUT, eventUuid);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              getByEventUuid(eventUuid));
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
        });

    When(
        "^I filter Task context by ([^\"]*)$",
        (String filterType) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.selectFromCombobox(TASK_CONTEXT_COMBOBOX, filterType);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I check displayed task's context is ([^\"]*)$",
        (String taskContext) -> {
          taskTableRows.forEach(
              data -> {
                softly.assertEquals(
                    data.getTaskContext(), taskContext, "Task context is not correct displayed");
              });
          softly.assertAll();
        });

    When(
        "^I filter Task status ([^\"]*)$",
        (String statusType) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.selectFromCombobox(TASK_STATUS_COMBOBOX, statusType);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I check displayed task's status is ([^\"]*)$",
        (String statusType) -> {
          taskTableRows.forEach(
              data -> {
                softly.assertEquals(
                    data.getTaskStatus(), statusType, "Task status is not correct displayed");
              });
          softly.assertAll();
        });

    When(
        "I reset filter from Tasks Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER);
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I search last created task by API using Contact UUID$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GENERAL_SEARCH_INPUT, 50);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, apiState.getCreatedContact().getUuid());
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I am checking if all the fields are correctly displayed in the Task Management table$",
        () -> {
          org.sormas.e2etests.entities.pojo.api.Task expectedTask = apiState.getCreatedTask();
          Task actualTask = taskTableRows.get(1);
          softly.assertTrue(
              apiState
                  .getCreatedContact()
                  .getUuid()
                  .toUpperCase()
                  .contains(getPartialUuidFromAssociatedLink(actualTask.getAssociatedLink())),
              "UUID is not correct displayed");
          softly.assertTrue(
              actualTask
                  .getAssociatedLink()
                  .contains(apiState.getCreatedContact().getPerson().getFirstName()),
              "First name is not correct displayed");
          softly.assertTrue(
              actualTask
                  .getAssociatedLink()
                  .contains(apiState.getCreatedContact().getPerson().getLastName().toUpperCase()),
              "Last name is not correct displayed");
          softly.assertEquals(
              actualTask.getTaskContext().toUpperCase(),
              expectedTask.getTaskContext(),
              "Task context is not correct displayed");
          softly.assertEquals(
              actualTask.getTaskType(),
              properties.getProperty("TaskType." + expectedTask.getTaskType()),
              "Task type is not correct displayed");
          softly.assertEquals(
              actualTask.getRegion(),
              apiState.getCreatedContact().getRegion().getCaption(),
              "Region is not correct displayed");
          softly.assertEquals(
              actualTask.getDistrict(),
              apiState.getCreatedContact().getDistrict().getCaption(),
              "District is not correct displayed");
          softly.assertEquals(
              actualTask.getPriority().toUpperCase(),
              expectedTask.getPriority(),
              "Priority is not correct displayed");
          softly.assertTrue(
              actualTask.getAssignedTo().contains(expectedTask.getAssigneeUser().getFirstName()),
              "Assigned to user first name is not correct displayed");
          softly.assertTrue(
              actualTask
                  .getAssignedTo()
                  .contains(expectedTask.getAssigneeUser().getLastName().toUpperCase()),
              "Assigned to user last name is not correct displayed");
          softly.assertEquals(
              actualTask.getCommentsOnTask(),
              expectedTask.getCreatorComment(),
              "Creator comment is not correct displayed");
          softly.assertEquals(
              actualTask.getCommentsOnExecution(),
              expectedTask.getAssigneeReply(),
              "Comments on execution is not correct displayed");
          softly.assertEquals(
              actualTask.getTaskStatus().toUpperCase(),
              expectedTask.getTaskStatus(),
              "Task status is not correct displayed");
          softly.assertAll();
        });

    When(
        "^I collect the task column objects$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
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
          webDriverHelpers.scrollToElementUntilIsVisible(TABLE_DATA);
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
    webDriverHelpers.scrollToElementUntilIsVisible(COLUMN_HEADERS_TEXT);
    baseSteps
        .getDriver()
        .findElements(COLUMN_HEADERS_TEXT)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    webDriverHelpers.scrollToElementUntilIsVisible(COLUMN_HEADERS_TEXT);
    return headerHashmap;
  }

  private LocalDateTime getLocalDateTimeFromColumns(String date) {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("M/d/yyyy h:m a").localizedBy(Locale.GERMAN);
    try {
      return LocalDateTime.parse(date.trim(), formatter);
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
