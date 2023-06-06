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
package org.sormas.e2etests.steps.api.commonSteps;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.parsers.HTMLParser;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class ResponseChecksSteps implements En {

  @Inject
  public ResponseChecksSteps(ApiState apiState, RestAssuredClient restAssuredClient) {

    Then(
        "API: I check that POST call body for bulk request is {string}",
        (String expectedBody) -> {
          try {
            String[] responseBodyList;
            responseBodyList =
                apiState
                    .getResponse()
                    .getBody()
                    .asString()
                    .replace("[", "")
                    .replace("]", "")
                    .split(",");
            for (String postBody : responseBodyList)
              Assert.assertEquals(
                  postBody.replaceAll("[^a-zA-Z0-9]", ""),
                  expectedBody,
                  "Request response body is not correct");
          } catch (Exception any) {
            throw new Exception("Unable to check response body due to: " + any.getMessage());
          }
        });

    Then(
        "API: I check that POST call status code is {int}",
        (Integer expectedStatus) -> {
          int responseStatusCode = apiState.getResponse().getStatusCode();
          String responseBody = apiState.getResponse().getBody().asString();

          if (responseStatusCode == expectedStatus) {

            if (responseBody.contains("html")) {
              responseBody = String.format("[{\"statusCode\":%s}]", responseStatusCode);
            }

            String regexUpdatedResponseBody = responseBody.replaceAll("[^a-zA-Z0-9]", "");
            Assert.assertEquals(
                regexUpdatedResponseBody,
                String.format("statusCode%s", expectedStatus),
                "Request response body is not correct");
          } else {
            String errorMessage = HTMLParser.getTextFromHtml(responseBody, "body > h1");
            String developmentErrorCause = "";
            switch (responseStatusCode) {
              case 400:
                developmentErrorCause = "Validation failed, invalid request";
                break;
              case 401:
                developmentErrorCause = "Unauthorized request";
                break;
              case 403:
                developmentErrorCause = "User does not have access";
                break;
              case 409:
                developmentErrorCause =
                    "Conflict - tried to update data of an entity that was updated in the mean-time";
                break;
              case 422:
                developmentErrorCause = "Other exceptions (will be defined by developers)";
                break;
            }
            Assert.fail(
                String.format(
                    "Request failed with status code [%s] and message [%s] which relates to [%s]",
                    responseStatusCode, errorMessage, developmentErrorCause));
          }
        });

    Then(
        "API: I check that GET call status code is {int}",
        (Integer expectedStatus) -> {
          int responseStatusCode = apiState.getResponse().getStatusCode();
          Assert.assertEquals(
              responseStatusCode, expectedStatus.intValue(), "Request status code is not correct");
        });
  }
}
