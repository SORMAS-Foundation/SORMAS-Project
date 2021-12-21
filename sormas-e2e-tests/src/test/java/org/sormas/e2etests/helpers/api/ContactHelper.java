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

import static org.sormas.e2etests.constants.api.Endpoints.CONTACTS_PATH;
import static org.sormas.e2etests.constants.api.Endpoints.UUIDS_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.pojo.api.Contact;
import org.sormas.e2etests.pojo.api.Request;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class ContactHelper {

  private final RestAssuredClient restAssuredClient;
  private final ApiState apiState;
  private final ObjectMapper objectMapper;

  @Inject
  public ContactHelper(
      RestAssuredClient restAssuredClient, ObjectMapper objectMapper, ApiState apiState) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
    this.apiState = apiState;
  }

  @SneakyThrows
  public void push(Contact contact) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Contact> listOfContacts = List.of(contact);
    objectMapper.writeValue(out, listOfContacts);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .path(CONTACTS_PATH + "push")
            .body(out.toString())
            .build());
  }

  public void getAllContactsUuid() {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(CONTACTS_PATH + UUIDS_PATH).build());
    int totalPersons =
        apiState.getResponse().getBody().asString().replaceAll("\"", "").split(",").length;
    log.info("Total persons: " + totalPersons);
  }
}
