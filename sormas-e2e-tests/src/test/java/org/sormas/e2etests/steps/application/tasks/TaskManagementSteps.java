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

package org.sormas.e2etests.steps.application.tasks;

import static org.sormas.e2etests.pages.application.tasks.CreateNewTaskPage.TASK_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.EDIT_BUTTON_XPATH_BY_TEXT;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.NEW_TASK_BUTTON;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class TaskManagementSteps implements En {

  @Inject
  public TaskManagementSteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I click on the NEW TASK button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(NEW_TASK_BUTTON, TASK_TYPE_COMBOBOX));

    When(
        "^I open last created task$",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(
                By.xpath(
                    String.format(
                        EDIT_BUTTON_XPATH_BY_TEXT, CreateNewTaskSteps.task.getCommentsOnTask()))));
  }
}
