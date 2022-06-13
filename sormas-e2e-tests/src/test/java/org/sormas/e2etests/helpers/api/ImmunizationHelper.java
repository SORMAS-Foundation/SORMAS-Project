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

import static org.sormas.e2etests.constants.api.Endpoints.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import io.restassured.response.Response;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.Immunization;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.state.ApiState;

public class ImmunizationHelper {

  private final RestAssuredClient restAssuredClient;
  private final ApiState apiState;
  private final ObjectMapper objectMapper;

  @Inject
  public ImmunizationHelper(
      RestAssuredClient restAssuredClient, ObjectMapper objectMapper, ApiState apiState) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
    this.apiState = apiState;
  }

  public void getAllImmunizationsUUIDs() {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(IMMUNIZATIONS_PATH + UUIDS_PATH).build());
    int totalImmunizations =
        apiState.getResponse().getBody().asString().replaceAll("\"", "").split(",").length;
    System.out.println("Total immunizations: " + totalImmunizations);
  }

  public Response getImmunizationBasedOnUUID(String immunizationUUID) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(IMMUNIZATIONS_PATH + immunizationUUID).build());
    return apiState.getResponse();
  }

  @SneakyThrows
  public void createNewImmunization(Immunization immunization) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Immunization> immunizationBody = List.of(immunization);
    objectMapper.writeValue(out, immunizationBody);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .body(out.toString())
            .path(IMMUNIZATIONS_PATH + POST_PATH)
            .build());
  }

  @SneakyThrows
  public void createMultipleImmunizations(List<Immunization> immunizationsList) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    List<Immunization> immunizationsBody = immunizationsList;
    objectMapper.writeValue(out, immunizationsBody);
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.POST)
            .body(out.toString())
            .path(IMMUNIZATIONS_PATH + POST_PATH)
            .build());
  }
}
