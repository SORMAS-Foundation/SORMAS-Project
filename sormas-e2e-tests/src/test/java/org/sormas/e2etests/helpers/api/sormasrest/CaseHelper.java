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
package org.sormas.e2etests.helpers.api.sormasrest;

import static org.sormas.e2etests.constants.api.Endpoints.CASES_PATH;
import static org.sormas.e2etests.constants.api.Endpoints.POST_PATH;
import static org.sormas.e2etests.constants.api.Endpoints.UUIDS_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.api.Case;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class CaseHelper {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;
  private final ApiState apiState;

  @Inject
  public CaseHelper(
      RestAssuredClient restAssuredClient, ObjectMapper objectMapper, ApiState apiState) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
    this.apiState = apiState;
  }

  public void postCases(String specificPath, String jsonBody) {
    final String json = jsonBody;
    restAssuredClient.sendRequest(
        Request.builder().method(Method.POST).path(CASES_PATH + specificPath).body(json).build());
  }

  public void postCasesQueryByUUID(String uuid) {
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .path(CASES_PATH + "query")
            .body("[\"" + uuid + "\"]")
            .build());
  }

  @SneakyThrows
  public void createCase(Case caze) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Case> listOfContacts = List.of(caze);
    objectMapper.writeValue(out, listOfContacts);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .path(CASES_PATH + "push")
            .body(out.toString())
            .build());
  }

  @SneakyThrows
  public void getCase(String caseId) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(CASES_PATH + caseId).build());
  }

  @SneakyThrows
  public void createMultipleCases(List<Case> casesList) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Case> personBody = casesList;
    objectMapper.writeValue(out, personBody);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .body(out.toString())
            .path(CASES_PATH + POST_PATH)
            .build());
  }

  public void getAllCasesUuid() {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(CASES_PATH + UUIDS_PATH).build());
    int totalPersons =
        apiState.getResponse().getBody().asString().replaceAll("\"", "").split(",").length;
    log.info("Total cases: " + totalPersons);
  }
}
