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

import static org.sormas.e2etests.constants.api.Endpoints.CASES;

import io.restassured.http.Method;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.pojo.Request;

public class CaseHelper {

  private final RestAssuredClient restAssuredClient;

  @Inject
  public CaseHelper(RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
  }

  public void postCases(String specificPath, String jsonBody) {
    final String json = jsonBody;
    restAssuredClient.sendRequest(
        Request.builder().method(Method.POST).path(CASES + specificPath).body(json).build());
  }

  public void getAllCasesSince(Integer since) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.POST).path(CASES + "all/" + since).build());
  }
}
