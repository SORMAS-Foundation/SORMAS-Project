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
import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;

public class TaskManagementSteps implements En {

  @Inject
  public TaskManagementSteps(WebDriverHelpers webDriverHelpers, ApiState apiState) {

    When(
        "^I click on the NEW TASK button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(NEW_TASK_BUTTON, TASK_TYPE_COMBOBOX));

    When(
        "^I open last created task$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                By.xpath(
                    String.format(
                        EDIT_BUTTON_XPATH_BY_TEXT, CreateNewTaskSteps.task.getCommentsOnTask())),
                TASK_TYPE_COMBOBOX));

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
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(GENERAL_SEARCH_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(GENERAL_SEARCH_INPUT, eventUuid);
          // webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(eventUuid));
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
        });
  }
}
