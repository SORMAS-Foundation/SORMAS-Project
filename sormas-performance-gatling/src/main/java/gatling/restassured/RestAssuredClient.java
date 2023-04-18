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
package gatling.restassured;

import gatling.restassured.dtos.Request;
import gatling.utils.TestingSpecs;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import io.restassured.specification.RequestSpecification;

@Slf4j
public class RestAssuredClient {
  private RequestSpecification requestSpecification;

  private RequestSpecification request() {
    RestAssured.baseURI = TestingSpecs.getTestingEnvironment();
    requestSpecification =
            RestAssured.given()
                    .auth()
                    .preemptive()
                    .basic(TestingSpecs.getUsername(), TestingSpecs.getPassword());
    return requestSpecification
            .config(
                    RestAssured.config()
                            .encoderConfig(
                                    EncoderConfig.encoderConfig()
                                            .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);
  }

  @SneakyThrows
  public Response sendRequest(Request request) {
    Response response;
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
        throw new IllegalAccessException("Incorrect provided API method");
    }
    return response;
  }
}
