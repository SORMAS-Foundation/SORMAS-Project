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
package org.sormas.e2etests.steps.api.negativeSteps;

import static org.sormas.e2etests.constants.api.Endpoints.PERSONS_PATH;
import static org.sormas.e2etests.constants.api.Endpoints.UUIDS_PATH;

import cucumber.api.java8.En;
import io.restassured.response.Response;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class ApiCallsWithWrongUserSteps implements En {

  Response response;

  @Inject
  public ApiCallsWithWrongUserSteps(RestAssuredClient restAssuredClient, ApiState apiState) {

    When(
        "API: I GET persons uuids without user credentials",
        () -> {
          response =
              restAssuredClient
                  .getCustomisableClient("", "")
                  .get(PERSONS_PATH + UUIDS_PATH)
                  .then()
                  .extract()
                  .response();
          apiState.setResponse(response);
        });

    When(
        "API: I GET persons uuids with invalid user credentials",
        () -> {
          response =
              restAssuredClient
                  .getCustomisableClient("eeeee", "macarena")
                  .get(PERSONS_PATH + UUIDS_PATH)
                  .then()
                  .extract()
                  .response();
          apiState.setResponse(response);
        });
  }
}
