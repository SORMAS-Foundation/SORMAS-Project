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

package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import org.sormas.e2etests.entities.pojo.web.Task;
import org.sormas.e2etests.enums.immunizations.StatusValues;

public class TaskService {
  private final Faker faker;

  @Inject
  public TaskService(Faker faker) {
    this.faker = faker;
  }

  private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");

  public Task buildGeneratedTask() {
    Date date = new Date(System.currentTimeMillis());
    return Task.builder()
        .taskContext("GENERAL")
        .taskType("other task as described in comments")
        .suggestedStartDate(LocalDate.now())
        .suggestedStartTime(LocalTime.of(11, 30))
        .dueDateDate(LocalDate.now().plusDays(1))
        .dueDateTime(LocalTime.of(11, 30))
        .assignedTo("Surveillance OFFICER - Surveillance Officer")
        .priority("Normal")
        .commentsOnTask("Task comment - " + formatter.format(date))
        .commentsOnExecution("Execution comment - " + formatter.format(date))
        .taskStatus(StatusValues.PENDING.getValue())
        .build();
  }

  public Task buildEditTask(String currentTaskContext, String currentStatus) {
    Date date = new Date(System.currentTimeMillis());
    return Task.builder()
        .taskContext(currentTaskContext)
        .taskType("contact tracing")
        .suggestedStartDate(LocalDate.now().plusDays(1))
        .suggestedStartTime(LocalTime.of(12, 30))
        .dueDateDate(LocalDate.now().plusDays(2))
        .dueDateTime(LocalTime.of(13, 30))
        .assignedTo("Surveillance OFFICER - Surveillance Officer")
        .priority("High")
        .commentsOnTask("Task comment - " + formatter.format(date))
        .commentsOnExecution("Execution comment - " + formatter.format(date))
        .taskStatus(currentStatus)
        .build();
  }

  public Task buildGeneratedTaskForEvent() {
    return buildGeneratedTask().toBuilder()
        .taskContext("EVENT")
        .taskType("vaccination activities")
        .assignedTo("National USER - National User")
        .build();
  }
}
