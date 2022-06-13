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
package org.sormas.e2etests.steps.api;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.api.Contact;
import org.sormas.e2etests.entities.pojo.api.Task;
import org.sormas.e2etests.entities.services.api.TaskApiService;
import org.sormas.e2etests.helpers.api.TaskHelper;
import org.sormas.e2etests.state.ApiState;

public class TasksSteps implements En {

  @Inject
  public TasksSteps(TaskHelper taskHelper, TaskApiService taskApiService, ApiState apiState) {

    When(
        "API: I create a new task",
        () -> {
          Task task = taskApiService.buildGeneratedTask();
          Contact contact = Contact.builder().uuid(apiState.getCreatedContact().getUuid()).build();
          task = task.toBuilder().contact(contact).build();
          taskHelper.createTask(task);
          apiState.setCreatedTask(task);
        });
  }
}
