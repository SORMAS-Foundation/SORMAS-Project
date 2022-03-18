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
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Task;
import org.sormas.e2etests.entities.services.TaskService;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class CreateNewTaskSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static Task task;
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public CreateNewTaskSteps(WebDriverHelpers webDriverHelpers, TaskService taskService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new task with specific data$",
        () -> {
          task = taskService.buildGeneratedTask();
          fillAllFields(task);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
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
          final Task actualTask = collectTaskData();
          ComparisonHelper.compareEqualEntities(task, actualTask);
        });

    When(
        "^I change all Task's fields and save$",
        () -> {
          task = taskService.buildEditTask("CASE", getStatus());
          fillAllFields(task);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
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

  private void selectTaskType(String taskType) {
    webDriverHelpers.selectFromCombobox(TASK_TYPE_COMBOBOX, taskType);
  }

  private void fillSuggestedStartDate(LocalDate suggestedStartDate) {
    webDriverHelpers.clearAndFillInWebElement(
        SUGGESTED_START_DATE_INPUT, DATE_FORMATTER.format(suggestedStartDate));
  }

  private void fillSuggestedStartTime(LocalTime suggestedStartTime) {
    webDriverHelpers.selectFromCombobox(
        SUGGESTED_START_TIME_COMBOBOX, TIME_FORMATTER.format(suggestedStartTime));
  }

  private void fillDueDateDate(LocalDate dueDateDate) {
    webDriverHelpers.clearAndFillInWebElement(
        DUE_DATE_DATE_INPUT, DATE_FORMATTER.format(dueDateDate));
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
