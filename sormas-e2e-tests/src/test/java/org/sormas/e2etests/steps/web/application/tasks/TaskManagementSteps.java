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

import static java.util.function.Predicate.*;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_POPUP;
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;

import cucumber.api.java8.En;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Task;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;

public class TaskManagementSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  private Task actualTask;
  private int specificTaskRowIndex;

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
          webDriverHelpers.clickOnWebElementBySelector(
              By.xpath(
                  String.format(
                      EDIT_BUTTON_XPATH_BY_TEXT, CreateNewTaskSteps.task.getCommentsOnTask())));
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
        "^I search last created task by API using Contact UUID and wait for (\\d+) results to be displayed$",
        (Integer displayedResults) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(GENERAL_SEARCH_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              GENERAL_SEARCH_INPUT, apiState.getCreatedContact().getUuid());
          webDriverHelpers.waitUntilElementsHasText(
              RESULTS_COUNTER, String.valueOf(displayedResults));
        });

    When(
        "^I am checking if all the fields are correctly displayed in the Task Management table$",
        () -> {
          org.sormas.e2etests.pojo.api.Task expectedTask = apiState.getCreatedTask();

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
          WebElement taskRow =
              baseSteps
                  .getDriver()
                  .findElement(By.xpath(String.format(TABLE_ROW, specificTaskRowIndex)));
          actualTask =
              Task.builder()
                  .associatedLink(
                      taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 2))).getText())
                  .taskContext(
                      taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 3))).getText())
                  .taskType(taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 4))).getText())
                  .region(taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 5))).getText())
                  .district(taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 6))).getText())
                  .priority(taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 7))).getText())
                  .suggestedStartDateTime(
                      getLocalDateTimeFromColumns(
                          taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 8))).getText()))
                  .dueDateDateTime(
                      getLocalDateTimeFromColumns(
                          taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 9))).getText()))
                  .assignedTo(
                      taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 10))).getText())
                  .commentsOnExecution(
                      taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 11))).getText())
                  .taskStatus(
                      taskRow.findElement(By.xpath(String.format(TABLE_COLUMN, 14))).getText())
                  .build();
        });

    Then(
        "I identify the last created task row",
        () -> {
          List<WebElement> allTableRows =
              baseSteps.getDriver().findElements(By.xpath("//tr[@role='row']"));
          specificTaskRowIndex = allTableRows.size();
        });
  }

  private LocalDateTime getLocalDateTimeFromColumns(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
    return LocalDateTime.parse(date, formatter);
  }

  private String getPartialUuidFromAssociatedLink(String associatedLink) {
    return StringUtils.left(associatedLink, 6);
  }
}
