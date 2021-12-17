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
package org.sormas.e2etests.helpers.api;

import static org.sormas.e2etests.constants.api.Endpoints.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import io.restassured.response.Response;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.pojo.api.Request;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class PersonsHelper {

  private final RestAssuredClient restAssuredClient;
  private final ApiState apiState;
  private final ObjectMapper objectMapper;

  @Inject
  public PersonsHelper(
      RestAssuredClient restAssuredClient, ObjectMapper objectMapper, ApiState apiState) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
    this.apiState = apiState;
  }

  public void getAllPersonUuid() {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(PERSONS_PATH + UUIDS_PATH).build());
    int totalPersons =
        apiState.getResponse().getBody().asString().replaceAll("\"", "").split(",").length;
    log.info("Total persons: " + totalPersons);
  }

  public Response getPersonBasedOnUUID(String personUUID) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(PERSONS_PATH + personUUID).build());
    return apiState.getResponse();
  }

  @SneakyThrows
  public void createNewPerson(Person person) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Person> personBody = List.of(person);
    objectMapper.writeValue(out, personBody);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .body(out.toString())
            .path(PERSONS_PATH + POST_PATH)
            .build());
  }

  @SneakyThrows
  public void createMultiplePersons(List<Person> personList) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Person> personBody = personList;
    objectMapper.writeValue(out, personBody);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .body(out.toString())
            .path(PERSONS_PATH + POST_PATH)
            .build());
  }
}
