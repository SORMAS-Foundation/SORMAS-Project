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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.LEAVE_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.TOTAL_CASES_COUNTER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ARCHIVE_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CONFIRM_ARCHIVING_COMMUNITY_TEXT;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CONFIRM_ARCHIVING_YES_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CONFIRM_DEARCHIVING_COMMUNITY_TEXT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.NOTIFICATION_CAPTION_MESSAGE_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.NOTIFICATION_DESCRIPTION_MESSAGE_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.APPLY_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.BULK_ACTIONS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_STATUS_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.RESET_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.ACTION_CANCEL_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.*;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;
import static org.sormas.e2etests.steps.api.TravelEntrySteps.travelEntriesUUID;
import static org.sormas.e2etests.steps.web.application.events.EventDirectorySteps.eventsUUID;

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
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;
import org.sormas.e2etests.pages.application.tasks.TaskManagementPage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.sormas.e2etests.steps.web.application.events.EventsTableColumnsHeaders;
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
      Properties properties,
      RestAssuredClient restAssuredClient) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;
    EnvironmentManager manager = new EnvironmentManager(restAssuredClient);

    When(
        "^I click on the NEW TASK button$",
        () -> {
          webDriverHelpers.clickWhileOtherButtonIsDisplayed(NEW_TASK_BUTTON, TASK_TYPE_COMBOBOX);
        });

    And(
        "I click on SHOW MORE FILTERS BUTTON on Task directory page",
        () -> {
          TimeUnit.SECONDS.sleep(2); // weak performance
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SHOW_MORE_FILTERS);
          webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_FILTERS);
          TimeUnit.SECONDS.sleep(4);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I open last created task from Tasks Directory$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_FILTERS);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ASSIGNED_USER_FILTER_INPUT);
          String assignedUser = CreateNewTaskSteps.task.getAssignedTo();
          webDriverHelpers.fillInWebElement(ASSIGNED_USER_FILTER_INPUT, assignedUser);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(200);
          webDriverHelpers.scrollToElement(CreateNewTaskSteps.task.getCommentsOnExecution());
          webDriverHelpers.ClickAndWaitForNewFormLoaded(
              getLastCreatedEditTaskButton(CreateNewTaskSteps.task.getCommentsOnExecution()),
              EDIT_TASK_MODAL_FORM);
        });
    When(
        "^I filter out last created task from Tasks Directory$",
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
        "^I search task by last Case created via API UUID$",
        () -> {
          String lastCreatedCaseUUID = apiState.getCreatedCase().getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GENERAL_SEARCH_INPUT, 50);
          webDriverHelpers.fillAndSubmitInWebElement(GENERAL_SEARCH_INPUT, lastCreatedCaseUUID);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
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
        "^I am search the last created event by API in task management directory$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.fillAndSubmitInWebElement(GENERAL_SEARCH_INPUT, eventsUUID.get(0));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              getByEventUuid(eventsUUID.get(0)));
        });

    When(
        "^I am search the last created travel Entry by API in task management directory$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, travelEntriesUUID.get(0));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              getByEventUuid(travelEntriesUUID.get(0)));
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
        "I check if popup message is {string}",
        (String expectedText) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              NOTIFICATION_DESCRIPTION_MESSAGE_POPUP);
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(NOTIFICATION_DESCRIPTION_MESSAGE_POPUP),
              expectedText,
              "Bulk action went wrong");
          softly.assertAll();
        });

    When(
        "^I check that region and district are correct displayed for the last created event by API in task management$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          softly.assertEquals(
              manager.getRegionName(
                  apiState.getCreatedEvent().getEventLocation().getRegion().getUuid()),
              tableRowsData.get(0).get(EventsTableColumnsHeaders.REGION_HEADER.toString()),
              "Regions are not equal");
          softly.assertEquals(
              manager.getDistrictName(
                  apiState.getCreatedEvent().getEventLocation().getDistrict().getUuid()),
              tableRowsData.get(0).get(EventsTableColumnsHeaders.DISTRICT_HEADER.toString()),
              "Districts are not equal");
          softly.assertAll();
        });

    When(
        "^I check that region and district are correct displayed for the last created travel entry by API in task management$",
        () -> {
          softly.assertEquals(
              manager.getRegionName(
                  apiState.getCreatedTravelEntry().getResponsibleRegion().getUuid()),
              webDriverHelpers.getTextFromWebElement(FIRST_GRID_REGION_VALUE),
              "Regions are not equal");
          softly.assertEquals(
              manager.getDistrictName(
                  apiState.getCreatedTravelEntry().getResponsibleDistrict().getUuid()),
              webDriverHelpers.getTextFromWebElement(FIRST_GRID_DISTRICT_VALUE),
              "Districts are not equal");
          softly.assertAll();
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
        "^I check displayed tasks District and Region are taken from API created Case$",
        () -> {
          taskTableRows.forEach(
              data -> {
                softly.assertEquals(
                    data.getRegion(),
                    apiState.getCreatedCase().getRegion().getCaption(),
                    "Task region is not the same with Case region");
                softly.assertEquals(
                    data.getDistrict(),
                    apiState.getCreatedCase().getDistrict().getCaption(),
                    "Task district is not the same with Case district");
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
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              LEAVE_BULK_EDIT_MODE, 50);
          for (int i = 2; i <= number + 1; i++) {
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                getCheckboxByIndex(String.valueOf(i)));
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
        "I check if popup message for archiving is {string}",
        (String expectedText) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NOTIFICATION_CAPTION_MESSAGE_POPUP);
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(NOTIFICATION_CAPTION_MESSAGE_POPUP),
              expectedText,
              "Bulk action went wrong");
          softly.assertAll();
        });

    When(
        "I check if popup message for deleting is {string}",
        (String expectedText) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NOTIFICATION_CAPTION_MESSAGE_POPUP);
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(NOTIFICATION_CAPTION_MESSAGE_POPUP),
              expectedText,
              "Bulk action went wrong");
          softly.assertAll();
        });
    And(
        "I check if popup message from Edit Task Form after bulk edit is {string}",
        (String expectedText) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NOTIFICATION_POPUP);
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(NOTIFICATION_POPUP),
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
    And(
        "I click the Change assignee Checkbox in the Edit Task Form",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CHANGE_ASSIGNEE_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(CHANGE_ASSIGNEE_CHECKBOX);
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
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(TASK_EXPORT_BUTTON);
          webDriverHelpers.doubleClickOnWebElementBySelector(TASK_EXPORT_BUTTON);
        });

    When(
        "I click on the Detailed Task Export button",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DETAILED_EXPORT_BUTTON);
          String file = "./downloads/sormas_tasks_" + LocalDate.now().format(formatter) + "_.csv";

          Path file_path = Paths.get(file);
          if (webDriverHelpers.isFileExists(file_path)) {
            Files.delete(file_path);
          }
          TimeUnit.SECONDS.sleep(8); // wait for basic download if in parallel
          webDriverHelpers.clickOnWebElementBySelector(DETAILED_EXPORT_BUTTON);
          TimeUnit.SECONDS.sleep(8); // wait for download start
          webDriverHelpers.waitForFileExists(file_path, 120);
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
        "I fill Configuration Name field in Custom Task Export popup with ([^\"]*) name",
        (String customExportName) -> {
          String configurationName;
          if (customExportName.equals("generated")) {
            configurationName = String.format("generated_%s", LocalDate.now().toString());
          } else {
            configurationName = customExportName;
          }
          webDriverHelpers.fillInWebElement(
              CUSTOM_EXPORT_CONFIGURATION_NAME_INPUT, configurationName);
        });

    And(
        "I open last created Custom Export Configuration in Custom Export page",
        () -> webDriverHelpers.clickOnWebElementBySelector(CUSTOM_TASK_EXPORT_EDIT_BUTTON));

    And(
        "I add {string} data to export in existing Export Configuration for Custom Task Export",
        (String customExportConfigurationCheckbox) -> {
          System.out.println(getCustomExportCheckboxByText(customExportConfigurationCheckbox));
          webDriverHelpers.clickOnWebElementBySelector(
              getCustomExportCheckboxByText(customExportConfigurationCheckbox));
        });
    And(
        "I save Export Configuration for Custom Task Export",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_CUSTOM_EXPORT_BUTTON));

    When(
        "I download last created custom task export file",
        () -> {
          String file = "./downloads/sormas_tasks_" + LocalDate.now().format(formatter) + "_.csv";
          Path file_path = Paths.get(file);
          if (webDriverHelpers.isFileExists(file_path)) {
            Files.delete(file_path);
          }
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_TASK_EXPORT_DOWNLOAD_BUTTON);
          webDriverHelpers.waitForFileExists(file_path, 120);
          Assert.assertTrue(webDriverHelpers.isFileExists(file_path));
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
        "I delete last created custom task export config",
        () -> {
          String export_id =
              webDriverHelpers.getAttributeFromWebElement(CUSTOM_TASK_EXPORT_DELETE_BUTTON, "id");
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_TASK_EXPORT_DELETE_BUTTON);
          Assert.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(getCustomExportByID(export_id), 1));
        });

    And(
        "I click on edit task icon of the {int} displayed task on Task Directory page",
        (Integer taskNumber) -> {
          webDriverHelpers.clickOnWebElementBySelector(
              TaskManagementPage.getEditTaskButtonByNumber(taskNumber));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "I click on the Archive task button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CASE_BUTTON);
        });

    When(
        "I click on No option in popup window",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL_BUTTON));

    And(
        "^I click on yes in archive task popup window$",
        () -> {
          TimeUnit.SECONDS.sleep(2); // wait for popup to load
          webDriverHelpers.isElementVisibleWithTimeout(CONFIRM_ARCHIVING_COMMUNITY_TEXT, 10);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });

    And(
        "I apply {string} to combobox on Task Directory Page",
        (String taskParameter) -> {
          webDriverHelpers.selectFromCombobox(EVENT_STATUS_FILTER_COMBOBOX, taskParameter);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I open last created task from Tasks Directory without click on show more filters$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ASSIGNED_USER_FILTER_INPUT);
          String assignedUser = CreateNewTaskSteps.task.getAssignedTo();
          webDriverHelpers.fillInWebElement(ASSIGNED_USER_FILTER_INPUT, assignedUser);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          webDriverHelpers.ClickAndWaitForNewFormLoaded(
              getLastCreatedEditTaskButton(CreateNewTaskSteps.task.getCommentsOnExecution()),
              EDIT_TASK_MODAL_FORM);
        });

    When(
        "I click on De-Archive task button",
        () -> {
          webDriverHelpers.scrollToElement(ARCHIVE_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_CASE_BUTTON);
        });

    When(
        "I click on yes in de-archive task popup window",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONFIRM_DEARCHIVING_COMMUNITY_TEXT);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ARCHIVING_YES_BUTTON);
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
