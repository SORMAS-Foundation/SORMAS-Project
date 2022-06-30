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
package org.sormas.e2etests.helpers.api.demis;

import static org.sormas.e2etests.constants.api.Endpoints.TASKS_PATH;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.entities.pojo.api.Task;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;

public class DemisHelper {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;
  private final RunningConfiguration runningConfiguration;
  private RequestSpecification requestSpecification;
  private final boolean logRestAssuredInfo;

  @Inject
  public DemisHelper(
      RunningConfiguration runningConfiguration,
      RestAssuredClient restAssuredClient,
      ObjectMapper objectMapper,
      @Named("LOG_RESTASSURED") boolean logRestAssuredInfo) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
    this.runningConfiguration = runningConfiguration;
    this.logRestAssuredInfo = logRestAssuredInfo;
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
  public void loginRequest() {
    // restAssuredClient.sendRequest();
//    final ByteArrayOutputStream out = new ByteArrayOutputStream();
//    List<Task> listOfContacts = List.of(task);
//    objectMapper.writeValue(out, listOfContacts);
//    restAssuredClient.sendRequest(
//        Request.builder()
//            .method(Method.POST)
//            .path(TASKS_PATH + "push")
//            .body(out.toString())
//            .build());
  }
}
