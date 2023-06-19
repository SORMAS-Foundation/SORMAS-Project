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

import static org.sormas.e2etests.constants.api.Endpoints.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import io.restassured.response.Response;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.helpers.RestAssuredClient;

public class UserHelper {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;
  Response response;

  @Inject
  public UserHelper(RestAssuredClient restAssuredClient, ObjectMapper objectMapper) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
  }

  @SneakyThrows
  public void getUserByRightsPermissions(String userUuid) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(USERS_PATH + "/rights" + "/" + userUuid).build());
  }

  @SneakyThrows
  public void getUserByRightsPermissions2(String userUuid) {

    response =
        restAssuredClient
            .getCustomisableClient("automation_admin", ">KO_mr+;#Y0xtc,L")
            .get(USERS_PATH + "/rights" + "/" + userUuid)
            .then()
            .extract()
            .response();
  }
  ;
}
