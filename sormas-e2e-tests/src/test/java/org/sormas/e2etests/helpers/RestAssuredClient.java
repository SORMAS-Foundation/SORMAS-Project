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
package org.sormas.e2etests.helpers;

import static recorders.StepsLogger.setIsScreenshotEnabled;

import com.google.inject.Provider;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.sormas.e2etests.pojo.api.Request;
import org.sormas.e2etests.state.ApiState;

public class RestAssuredClient {
  private final Provider<RequestSpecification> requestSpecification;
  private final String environmentUrl;
  private final ApiState apiState;

  @Inject
  public RestAssuredClient(
      Provider<RequestSpecification> requestSpecification,
      @Named("ENVIRONMENT_URL") String environmentUrl,
      ApiState apiState) {
    this.requestSpecification = requestSpecification;
    this.environmentUrl = environmentUrl;
    this.apiState = apiState;
  }

  private RequestSpecification request() {
    RestAssured.baseURI = environmentUrl + "/sormas-rest";
    return requestSpecification
        .get()
        .config(
            RestAssured.config()
                .encoderConfig(
                    EncoderConfig.encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
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
}
