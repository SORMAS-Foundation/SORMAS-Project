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
package org.sormas.e2etests.helpers;

import static org.sormas.e2etests.steps.BaseSteps.locale;
import static recorders.StepsLogger.setIsScreenshotEnabled;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class RestAssuredClient {
  private RequestSpecification requestSpecification;
  private final ApiState apiState;
  private final RunningConfiguration runningConfiguration;
  private final boolean logRestAssuredInfo;

  @Inject
  public RestAssuredClient(
      RunningConfiguration runningConfiguration,
      @Named("LOG_RESTASSURED") boolean logRestAssuredInfo,
      ApiState apiState) {
    this.logRestAssuredInfo = logRestAssuredInfo;
    this.apiState = apiState;
    this.runningConfiguration = runningConfiguration;
  }

  private RequestSpecification request() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    final String restEndpoint = "/sormas-rest";
    RestAssured.baseURI = runningConfiguration.getEnvironmentUrlForMarket(locale) + restEndpoint;
    Filter filters[];
    if (logRestAssuredInfo) {
      filters =
          new Filter[] {
            new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured()
          };
    } else {
      filters = new Filter[] {new AllureRestAssured()};
    }
    requestSpecification =
        RestAssured.given()
            .auth()
            .preemptive()
            .basic(
                runningConfiguration
                    .getUserByRole(locale, UserRoles.RestUser.getRole())
                    .getUsername(),
                runningConfiguration
                    .getUserByRole(locale, UserRoles.RestUser.getRole())
                    .getPassword());

    return requestSpecification
        .config(
            RestAssured.config()
                .encoderConfig(
                    EncoderConfig.encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .filters(Arrays.asList(filters).get(0));
  }

  @SneakyThrows
  public void sendRequest(Request request) {
    Response response;
    setIsScreenshotEnabled(false);
    RequestSpecification authorization = request();
    switch (request.getMethod()) {
      case POST:
        response =
            authorization
                .body(request.getBody())
                .post(request.getPath())
                .then()
                .extract()
                .response();
        break;
      case GET:
        response = authorization.get(request.getPath()).then().extract().response();
        break;
      case PUT:
        response =
            authorization
                .body(request.getBody())
                .put(request.getPath())
                .then()
                .extract()
                .response();
        break;
      case PATCH:
        response =
            authorization
                .body(request.getBody())
                .patch(request.getPath())
                .then()
                .extract()
                .response();
        break;
      case DELETE:
        response = authorization.delete(request.getPath()).then().extract().response();
        break;
      default:
        throw new IllegalAccessException("Incorrect calling method");
    }
    apiState.setResponse(response);
  }

  @SneakyThrows
  public Response sendRequestAndGetResponse(Request request) {
    setIsScreenshotEnabled(false);
    RequestSpecification authorization = request();
    switch (request.getMethod()) {
      case POST:
        return authorization.body(request.getBody()).post(request.getPath());
      case GET:
        return authorization.get(request.getPath());
      case PUT:
        return authorization.body(request.getBody()).put(request.getPath());
      case PATCH:
        return authorization.body(request.getBody()).patch(request.getPath());
      case DELETE:
        return authorization.delete(request.getPath());
      default:
        throw new IllegalAccessException("Incorrect calling method");
    }
  }
}
