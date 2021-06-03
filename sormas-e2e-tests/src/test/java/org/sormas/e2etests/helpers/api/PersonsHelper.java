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

import static org.sormas.e2etests.constants.api.Endpoints.PERSONS;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.pojo.api.Request;

public class PersonsHelper {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;

  @Inject
  public PersonsHelper(RestAssuredClient restAssuredClient, ObjectMapper objectMapper) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
  }

  public void getAllPersonUuid() {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(PERSONS + "uuids").build());
  }

  @SneakyThrows
  public void createNewPerson(Person person) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Person> personBody = List.of(person);
    objectMapper.writeValue(out, personBody);
    restAssuredClient.sendRequest(
        Request.builder().method(Method.POST).body(out.toString()).path(PERSONS + "push").build());
  }

  public void pushPerson(String specificPath, String jsonBody) {
    final String json = jsonBody;
    restAssuredClient.sendRequest(
        Request.builder().method(Method.POST).path(PERSONS + specificPath).body(json).build());
  }
}
