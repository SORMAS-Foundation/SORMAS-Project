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
package org.sormas.e2etests.helpers.api;

import static org.sormas.e2etests.constants.api.Endpoints.TASKS_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.entities.pojo.api.Task;
import org.sormas.e2etests.helpers.RestAssuredClient;

public class TaskHelper {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;

  @Inject
  public TaskHelper(RestAssuredClient restAssuredClient, ObjectMapper objectMapper) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
  }

  @SneakyThrows
  public void createTask(Task task) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Task> listOfContacts = List.of(task);
    objectMapper.writeValue(out, listOfContacts);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .path(TASKS_PATH + "push")
            .body(out.toString())
            .build());
  }
}
