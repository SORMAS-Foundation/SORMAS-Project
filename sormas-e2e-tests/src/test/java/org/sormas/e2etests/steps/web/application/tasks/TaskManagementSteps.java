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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.TOTAL_CASES_COUNTER;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_DIRECTORY_PAGE_SHOW_MORE_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.APPLY_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.BULK_ACTIONS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.RESET_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.*;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.web.Task;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class TaskManagementSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  private List<Task> taskTableRows;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Inject
  public TaskManagementSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      AssertHelpers assertHelpers,
      SoftAssert softly,
      Properties properties) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "^I click on the NEW TASK button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(NEW_TASK_BUTTON, TASK_TYPE_COMBOBOX));
    And(
        "I click on SHOW MORE FILTERS BUTTON on Task directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              TRAVEL_ENTRY_DIRECTORY_PAGE_SHOW_MORE_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });

    When(
        "^I open last created task from Tasks Directory$",
        () -> {
          By lastTaskEditButton =
              By.xpath(
                  String.format(
                      EDIT_BUTTON_XPATH_BY_TEXT, CreateNewTaskSteps.task.getCommentsOnExecution()));
          webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_FILTERS);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ASSIGNED_USER_FILTER_INPUT);
          String assignedUser = CreateNewTaskSteps.task.getAssignedTo();
          webDriverHelpers.fillInWebElement(ASSIGNED_USER_FILTER_INPUT, assignedUser);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(lastTaskEditButton, 40);
          webDriverHelpers.clickElementSeveralTimesUntilNextElementIsDisplayed(
              lastTaskEditButton, TASK_STATUS_OPTIONS, 6);
        });

    When(
        "^I search last created task by Case UUID and open it$",
        () -> {
          String lastCreatedCaseWithTaskUUID = EditCaseSteps.aCase.getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GENERAL_SEARCH_INPUT, 50);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, lastCreatedCaseWithTaskUUID);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);

          By lastTaskEditButton =
              By.xpath(
                  String.format(
                      EDIT_BUTTON_XPATH_BY_TEXT, CreateNewTaskSteps.task.getCommentsOnTask()));
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(lastTaskEditButton, 20);
          webDriverHelpers.clickOnWebElementBySelector(lastTaskEditButton);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              COMMENTS_ON_EXECUTION_TEXTAREA);
        });

    When(
        "^I am checking if the associated linked event appears in task management and click on it$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
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
        "^I check displayed task's context of first result is ([^\"]*)$",
        (String taskContext) -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(TASK_CONTEXT_FIRST_RESULT),
              taskContext,
              "Task context is not correct displayed");
          softly.assertAll();
        });
    When(
        "^I click on associated link to Travel Entry$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ASSOCIATED_LINK_FIRST_RESULT);
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
        "I click on Enter Bulk Edit Mode from Tasks Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BULK_EDIT_BUTTON);
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
        "^I open last created task by API using Contact UUID$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GENERAL_SEARCH_INPUT, 50);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, apiState.getCreatedContact().getUuid());
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              EDIT_FIRST_SEARCH_RESULT);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_FIRST_SEARCH_RESULT);
        });
    When(
        "^I select first (\\d+) results in grid in Task Directory$",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          for (int i = 2; i <= number + 1; i++) {
            webDriverHelpers.scrollToElement(getCheckboxByIndex(String.valueOf(i)));
            webDriverHelpers.clickOnWebElementBySelector(getCheckboxByIndex(String.valueOf(i)));
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click yes on the CONFIRM REMOVAL popup from Task Directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(POPUP_YES_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I check if popup message is {string}",
        (String expectedText) -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(
                  By.cssSelector(".v-Notification-description")),
              expectedText,
              "Bulk action went wrong");
          softly.assertAll();
        });
    When(
        "I check if popup message after bulk edit is {string}",
        (String expectedText) -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(
                  By.cssSelector(".v-Notification-caption")),
              expectedText,
              "Bulk edit went wrong");
          softly.assertAll();
        });

    And(
        "I click on Bulk Actions combobox in Task Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_EVENT_DIRECTORY));
    And(
        "I click on Delete button from Bulk Actions Combobox in Task Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_DELETE_BUTTON));
    And(
        "I click on Archive button from Bulk Actions Combobox in Task Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ARCHIVE_BUTTON));
    And(
        "I click on Edit button from Bulk Actions Combobox in Task Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_EDITING_BUTTON));
    When(
        "I click to bulk change assignee for selected tasks",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CHANGE_ASSIGNEE_CHECKBOX);
          webDriverHelpers.selectFromCombobox(TASK_ASSIGNEE_COMBOBOX, "Surveillance SUPERVISOR");
        });
    When(
        "I click to bulk change priority for selected tasks",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CHANGE_PRIORITY_CHECKBOX);
          webDriverHelpers.clickWebElementByText(TASK_RADIOBUTTON, "HIGH");
        });
    When(
        "I click to bulk change status for selected tasks",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CHANGE_STATUS_CHECKBOX);
          webDriverHelpers.clickWebElementByText(TASK_RADIOBUTTON, "DONE");
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

    Then(
        "I check that number of displayed tasks results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        Integer.parseInt(
                            webDriverHelpers.getTextFromPresentWebElement(TOTAL_CASES_COUNTER)),
                        number.intValue(),
                        "Number of displayed tasks is not correct")));

    When(
        "^I collect the task column objects$",
        () -> {
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

    And(
        "I click Export button in Task Directory",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(TASK_EXPORT_BUTTON);
        });

    When(
        "I click on the Detailed Task Export button",
        () -> {
          String file = "./downloads/sormas_tasks_" + LocalDate.now().format(formatter) + "_.csv";

          Path file_path = Paths.get(file);
          if (webDriverHelpers.isFileExists(file_path)) {
            Files.delete(file_path);
          }
          TimeUnit.SECONDS.sleep(8); // wait for basic download if in parallel
          webDriverHelpers.clickOnWebElementBySelector(DETAILED_EXPORT_BUTTON);
          TimeUnit.SECONDS.sleep(8); // wait for download start
          webDriverHelpers.waitForFileExists(file_path, 90);
        });

    When(
        "I click on the Custom Event Export button",
        () -> {
          TimeUnit.SECONDS.sleep(8); // wait for basic download if in parallel
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_EXPORT_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for load
        });

    When(
        "I click on the New Export Configuration button in Custom Task Export popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_CUSTOM_EXPORT_BUTTON));

    Then(
        "I fill Configuration Name field in Custom Task Export popup",
        () -> {
          String configurationName = LocalDate.now().toString();
          webDriverHelpers.fillInWebElement(
              CUSTOM_EXPORT_CONFIGURATION_NAME_INPUT, configurationName);
        });

    When(
        "I select specific data to export in Export Configuration for Custom Task Export",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_EXPORT_TASK_CONTEXT_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_CUSTOM_EXPORT_BUTTON);
        });

    And(
        "I open edit last created Custom Export Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(CUSTOM_TASK_EXPORT_EDIT_BUTTON));

    And(
        "I add specific data to export in existing Export Configuration for Custom Task Export",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_EXPORT_TASK_TYPE_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_CUSTOM_EXPORT_BUTTON);
        });

    When(
        "I download last created custom task export file",
        () -> {
          String file = "./downloads/sormas_tasks_" + LocalDate.now().format(formatter) + "_.csv";
          Path file_path = Paths.get(file);
          if (webDriverHelpers.isFileExists(file_path)) {
            Files.delete(file_path);
          }
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_TASK_EXPORT_DOWNLOAD_BUTTON);
          webDriverHelpers.waitForFileExists(file_path, 90);
        });

    When(
        "I check if downloaded data generated by detailed task export option is correct",
        () -> {
          String file = "./downloads/sormas_tasks_" + LocalDate.now().format(formatter) + "_.csv";
          Task reader = parseDetailedTaskExport(file);
          Path path = Paths.get(file);
          Files.delete(path);

          softly.assertEquals(
              reader.getTaskContext().toLowerCase(),
              String.format("taskContext").toLowerCase(),
              "Task Contexts are not equal");
          softly.assertEquals(
              reader.getTaskType().toLowerCase(),
              String.format("taskType").toLowerCase(),
              "Task types are not equal");
          softly.assertEquals(
              reader.getPriority().toLowerCase(),
              String.format("priority").toLowerCase(),
              "Priority are not equal");
          softly.assertEquals(
              reader.getTaskStatus().toLowerCase(),
              String.format("taskStatus").toLowerCase(),
              "Taks statuses are not equal");

          softly.assertAll();
        });

    When(
        "I check if downloaded data generated by new custom task export option is correct",
        () -> {
          String file = "./downloads/sormas_tasks_" + LocalDate.now().format(formatter) + "_.csv";
          Task reader = parseNewCustomTaskExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getTaskContext().toLowerCase(),
              String.format("taskContext").toLowerCase(),
              "Task Contexts field missed with expected");
          softly.assertAll();
        });

    When(
        "I check if downloaded data generated by edited custom task export option is correct",
        () -> {
          String file = "./downloads/sormas_tasks_" + LocalDate.now().format(formatter) + "_.csv";
          Task reader = parseEditCustomTaskExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getTaskContext().toLowerCase(),
              String.format("taskContext").toLowerCase(),
              "Task Contexts field missed with expected");
          softly.assertEquals(
              reader.getTaskType().toLowerCase(),
              String.format("taskType").toLowerCase(),
              "Task Contexts field missed with expected");
          softly.assertAll();
        });

    And(
        "I delete all created custom task export configs",
        () -> {
          while (webDriverHelpers.isElementVisibleWithTimeout(
              CUSTOM_TASK_EXPORT_DELETE_BUTTON, 1)) {
            TimeUnit.SECONDS.sleep(2);
            webDriverHelpers.clickOnWebElementBySelector(CUSTOM_TASK_EXPORT_DELETE_BUTTON);
            TimeUnit.SECONDS.sleep(2);
          }
        });
  }

  public Task parseDetailedTaskExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Task builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(0) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseDetailedTaskExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseDetailedTaskExport: {}", e.getCause());
    }
    try {
      values = r.get(0);
      builder =
          Task.builder()
              .taskContext(values[1])
              .taskType(values[5])
              .priority(values[6])
              .taskStatus(values[9])
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomTaskExport: {}", e.getCause());
    }
    return builder;
  }

  public Task parseNewCustomTaskExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Task builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseDetailedTaskExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseDetailedTaskExport: {}", e.getCause());
    }
    try {
      values = r.get(0);
      builder = Task.builder().taskContext(values[0]).build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomTaskExport: {}", e.getCause());
    }
    return builder;
  }

  public Task parseEditCustomTaskExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Task builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseDetailedTaskExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseDetailedTaskExport: {}", e.getCause());
    }
    try {
      values = r.get(0);
      builder = Task.builder().taskContext(values[0]).taskType(values[1]).build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomTaskExport: {}", e.getCause());
    }
    return builder;
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

  @SneakyThrows
  private LocalDateTime getLocalDateTimeFromColumns(String date) {
    if (date.isEmpty()) {
      throw new Exception(String.format("Provided date to be parsed: %s, is empty!", date));
    }
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("M/d/yyyy h:m a").localizedBy(Locale.ENGLISH);
    try {
      log.info("Parsing date: [{}]", date);
      return LocalDateTime.parse(date.trim(), formatter);
    } catch (Exception e) {
      throw new WebDriverException(
          String.format(
              "Unable to parse date: [ %s ] due to caught exception: %s", date, e.getMessage()));
    }
  }

  private String getPartialUuidFromAssociatedLink(String associatedLink) {
    return StringUtils.left(associatedLink, 6);
  }
}
