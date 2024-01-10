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

import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Task;
import org.sormas.e2etests.entities.services.TaskService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.users.CreateNewUserSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class CreateNewTaskSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static final DateTimeFormatter DATE_FORMATTER_DE =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");
  public static Task task;
  public static String user;
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public CreateNewTaskSteps(
      WebDriverHelpers webDriverHelpers, TaskService taskService, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new task with specific data$",
        () -> {
          task = taskService.buildGeneratedTask();
          fillAllFields(task);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "^I fill a new task form with specific data$",
        () -> {
          task = taskService.buildGeneratedTask();
          fillAllFields(task);
        });
    When(
        "^I fill a new task form with specific data for DE version$",
        () -> {
          task = taskService.buildGeneratedTaskDE();
          fillAllFieldsDE(task);
        });
    When(
        "^I add observers to a task$",
        () -> {
          task = taskService.buildGeneratedTask();
          fillAllFields(task);
        });
    When(
        "^I create a new task with specific data for an event$",
        () -> {
          task = taskService.buildGeneratedTaskForEvent();
          fillAllFields(task);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "^I check the created task is correctly displayed on Edit task page",
        () -> {
         // TimeUnit.SECONDS.sleep(3); // waiting for page loaded
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
              By.cssSelector("v-window v-widget"));
          final Task actualTask = collectTaskData();
          ComparisonHelper.compareEqualEntities(task, actualTask);
        });
    When(
        "^I click on Save button in New Task form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I change all Task's fields and save$",
        () -> {
          task = taskService.buildEditTask("CASE", getStatus());
          fillAllFields(task);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });

    When(
        "I select {string} user from Observed by combobox on Edit Task page",
        (String chosenUser) -> {
          user = chosenUser;
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(OBSERVER_USER_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(OBSERVER_USER_INPUT, user);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I select {string} user from Observed by combobox in new Task form",
        (String chosenUser) -> {
          webDriverHelpers.fillAndSubmitInWebElement(OBSERVED_BY_COMBOBOX, chosenUser);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });
    When(
        "I delete {string} user from Observed by in new Task form",
        (String chosenUser) -> {
          webDriverHelpers.clickOnWebElementBySelector(getDeleteIconByUser(chosenUser));
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });

    When(
        "I check that respected user is selected on Edit Task page",
        () -> {
          String currentUser = webDriverHelpers.getTextFromWebElement(SELECTED_OBSERVER_USER);
          softly.assertEquals(currentUser, user, "The respected user is not selected");
          softly.assertAll();
        });
    When(
        "I check that ([^\"]*) is not visible in Observed By on Edit Task Page",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = getDeleteIconByUser(option);
          Boolean elementVisible = true;
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertFalse(elementVisible, option + " is visible!");
          softly.assertAll();
        });
    When(
        "I check that ([^\"]*) is visible in Observed By on Edit Task Page",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          By selector = getDeleteIconByUser(option);
          Boolean elementVisible = true;
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertTrue(elementVisible, option + " is visible!");
          softly.assertAll();
        });
    And(
        "I check that there is only user with ([^\"]*) region for task",
        (String expectedRegion) -> {
          CreateNewUserSteps.userWithRegion.forEach(
              (userName, userRegion) -> {
                userName = String.format(userName + " (0)");
                if (userRegion.equals(expectedRegion)) {
                  Assert.assertTrue(
                      webDriverHelpers.checkIfElementExistsInCombobox(
                          ASSIGNED_TO_COMBOBOX, userName),
                      "There is no expected user name in list");

                } else {
                  Assert.assertFalse(
                      webDriverHelpers.checkIfElementExistsInCombobox(
                          ASSIGNED_TO_COMBOBOX, userName),
                      "There is user from another region");
                }
              });
        });
    And(
        "I check that Pending button exist on task edit page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PENDING_TASK_STATUS_OPTION);
          Assert.assertTrue(webDriverHelpers.isElementEnabled(PENDING_TASK_STATUS_OPTION));
        });
    And(
        "I create a new task with {string} as a assigned user",
        (String user) -> {
          task = taskService.buildGeneratedTaskWithSpecificUserAssigned(user);
          fillAllFieldsWithoutComments(task);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
  }

  private void fillAllFields(Task task) {
    selectTaskType(task.getTaskType());
    fillSuggestedStartDate(task.getSuggestedStartDate());
    fillSuggestedStartTime(task.getSuggestedStartTime());
    fillDueDateDate(task.getDueDateDate());
    fillDueDateTime(task.getDueDateTime());
    selectAssignedTo(task.getAssignedTo());
    selectPriority(task.getPriority());
    fillCommentsOnTask(task.getCommentsOnTask());
    fillCommentsOnExecution(task.getCommentsOnExecution());
  }

  private void fillAllFieldsWithoutComments(Task task) {
    selectTaskType(task.getTaskType());
    fillSuggestedStartDate(task.getSuggestedStartDate());
    fillSuggestedStartTime(task.getSuggestedStartTime());
    fillDueDateDate(task.getDueDateDate());
    fillDueDateTime(task.getDueDateTime());
    selectAssignedTo(task.getAssignedTo());
    selectPriority(task.getPriority());
  }

  private void fillAllFieldsDE(Task task) {
    selectTaskType(task.getTaskType());
    fillSuggestedStartDateDE(task.getSuggestedStartDate());
    fillSuggestedStartTime(task.getSuggestedStartTime());
    fillDueDateDateDE(task.getDueDateDate());
    fillDueDateTime(task.getDueDateTime());
    selectAssignedTo(task.getAssignedTo());
    selectPriority(task.getPriority());
    fillCommentsOnTask(task.getCommentsOnTask());
  }

  private void selectTaskType(String taskType) {
    webDriverHelpers.selectFromCombobox(TASK_TYPE_COMBOBOX, taskType);
  }

  private void fillSuggestedStartDate(LocalDate suggestedStartDate) {
    webDriverHelpers.clearAndFillInWebElement(
        SUGGESTED_START_DATE_INPUT, DATE_FORMATTER.format(suggestedStartDate));
  }

  private void fillSuggestedStartDateDE(LocalDate suggestedStartDate) {
    webDriverHelpers.clearAndFillInWebElement(
        SUGGESTED_START_DATE_INPUT, DATE_FORMATTER_DE.format(suggestedStartDate));
  }

  private void fillSuggestedStartTime(LocalTime suggestedStartTime) {
    webDriverHelpers.selectFromCombobox(
        SUGGESTED_START_TIME_COMBOBOX, TIME_FORMATTER.format(suggestedStartTime));
  }

  private void fillDueDateDate(LocalDate dueDateDate) {
    webDriverHelpers.clearAndFillInWebElement(
        DUE_DATE_DATE_INPUT, DATE_FORMATTER.format(dueDateDate));
  }

  private void fillDueDateDateDE(LocalDate dueDateDate) {
    webDriverHelpers.clearAndFillInWebElement(
        DUE_DATE_DATE_INPUT, DATE_FORMATTER_DE.format(dueDateDate));
  }

  private void fillDueDateTime(LocalTime dueDateTime) {
    webDriverHelpers.selectFromCombobox(DUE_DATE_TIME_COMBOBOX, TIME_FORMATTER.format(dueDateTime));
  }

  private void selectAssignedTo(String assignedTo) {
    webDriverHelpers.selectFromCombobox(ASSIGNED_TO_COMBOBOX, assignedTo);
  }

  private void selectPriority(String priority) {
    webDriverHelpers.selectFromCombobox(PRIORITY_COMBOBOX, priority);
  }

  private void fillCommentsOnTask(String commentsOnTask) {
    webDriverHelpers.clearAndFillInWebElement(COMMENTS_ON_TASK_TEXTAREA, commentsOnTask);
  }

  private void fillCommentsOnExecution(String commentsOnExecution) {
    webDriverHelpers.clearAndFillInWebElement(COMMENTS_ON_EXECUTION_TEXTAREA, commentsOnExecution);
  }

  private void selectTaskStatus(String taskStatus) {
    webDriverHelpers.clickWebElementByText(TASK_STATUS_OPTIONS, taskStatus);
  }

  private Task collectTaskData() {
    return Task.builder()
        .taskContext(getDisabledTaskContext())
        .taskType(webDriverHelpers.getValueFromWebElement(TASK_TYPE_INPUT))
        .suggestedStartDate(getSuggestedStartDate())
        .suggestedStartTime(getSuggestedStartTime())
        .dueDateDate(getDueDateDate())
        .dueDateTime(getDueDateTime())
        .assignedTo(getAssignedToWithoutNoTasks())
        .priority(getPriority())
        .commentsOnTask(getCommentsOnTask())
        .commentsOnExecution(getCommentsOnExecution())
        .taskStatus(getStatus())
        .build();
  }

  private LocalDate getSuggestedStartDate() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(SUGGESTED_START_DATE_INPUT), DATE_FORMATTER);
  }

  private LocalDate getDueDateDate() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DUE_DATE_DATE_INPUT), DATE_FORMATTER);
  }

  private LocalTime getDueDateTime() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromWebElement(DUE_DATE_TIME_INPUT), TIME_FORMATTER);
  }

  private LocalTime getSuggestedStartTime() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromWebElement(SUGGESTED_START_TIME_INPUT), TIME_FORMATTER);
  }

  private String getAssignedToWithoutNoTasks() {
    return webDriverHelpers
        .getValueFromWebElement(ASSIGNED_TO_INPUT)
        .replaceAll("\\((.*)", "")
        .trim();
  }

  private String getPriority() {
    return webDriverHelpers.getValueFromWebElement(PRIORITY_INPUT);
  }

  private String getCommentsOnTask() {
    return webDriverHelpers.getValueFromWebElement(COMMENTS_ON_TASK_TEXTAREA);
  }

  private String getCommentsOnExecution() {
    return webDriverHelpers.getValueFromWebElement(COMMENTS_ON_EXECUTION_TEXTAREA);
  }

  private String getStatus() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(TASK_STATUS_OPTIONS);
  }

  private String getDisabledTaskContext() {
    return webDriverHelpers.getCheckedDisabledOptionFromHorizontalOptionGroup(
        SELECTED_TASK_CONTEXT);
  }
}
