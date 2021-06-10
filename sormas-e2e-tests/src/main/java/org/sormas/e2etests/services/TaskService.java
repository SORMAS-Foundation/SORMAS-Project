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

package org.sormas.e2etests.services;

import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import org.sormas.e2etests.pojo.web.Task;

public class TaskService {

  @Inject
  public TaskService() {}

  public Task buildGeneratedTask() {
    long currentTimeMillis = System.currentTimeMillis();
    return Task.builder()
        .taskContext("GENERAL")
        .taskType("other task as described in comments")
        .suggestedStartDate(LocalDate.now())
        .suggestedStartTime(LocalTime.of(11, 30))
        .dueDateDate(LocalDate.now().plusDays(1))
        .dueDateTime(LocalTime.of(11, 30))
        .assignedTo("Surveillance OFFICER - Surveillance Officer")
        .priority("Normal")
        .commentsOnTask(currentTimeMillis + "Comment on task")
        .commentsOnExecution(currentTimeMillis + "Comment on execution")
        .taskStatus("PENDING")
        .build();
  }

  public Task buildEditTask() {
    long currentTimeMillis = System.currentTimeMillis();
    return Task.builder()
        .taskContext("GENERAL")
        .taskType("other task as described in comments")
        .suggestedStartDate(LocalDate.now().plusDays(3))
        .suggestedStartTime(LocalTime.of(12, 30))
        .dueDateDate(LocalDate.now().plusDays(5))
        .dueDateTime(LocalTime.of(13, 30))
        .assignedTo("Surveillance OFFICER - Surveillance Officer")
        .priority("High")
        .commentsOnTask(currentTimeMillis + "Comment on task")
        .commentsOnExecution(currentTimeMillis + "Comment on execution")
        .taskStatus("DONE")
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
