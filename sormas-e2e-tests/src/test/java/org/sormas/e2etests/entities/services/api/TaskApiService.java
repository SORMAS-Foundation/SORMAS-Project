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

package org.sormas.e2etests.entities.services.api;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.util.Date;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.api.AssigneeUser;
import org.sormas.e2etests.entities.pojo.api.Task;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;

public class TaskApiService {

  private final Faker faker;
  private RestAssuredClient restAssuredClient;

  @Inject
  public TaskApiService(Faker faker, RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
    this.faker = faker;
  }

  public Task buildGeneratedTask() {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Task.builder()
        .uuid(UUID.randomUUID().toString())
        .taskContext("CONTACT")
        .taskType("WEEKLY_REPORT_GENERATION")
        .priority("NORMAL")
        .dueDate(new Date())
        .suggestedStart(new Date())
        .taskStatus("PENDING")
        .assigneeUser(
            AssigneeUser.builder()
                .caption("Contact OFFICER - Kontaktbeauftragte*r")
                .firstName("Contact")
                .lastName("Officer")
                .uuid(environmentManager.getUserUUIDByFullName("Contact Officer"))
                .build())
        .assigneeReply(faker.music().instrument())
        .creatorComment(faker.book().title())
        .build();
  }
}
