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
package org.sormas.e2etests.steps.api.performance.getEntities;

import static org.sormas.e2etests.constants.api.Endpoints.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java8.En;
import customreport.data.TableDataManager;
import io.restassured.http.Method;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.api.*;
import org.sormas.e2etests.entities.services.api.*;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.api.sormasrest.CaseHelper;
import org.sormas.e2etests.helpers.api.sormasrest.ContactHelper;
import org.sormas.e2etests.helpers.api.sormasrest.PersonsHelper;
import org.testng.Assert;

@Slf4j
public class GetEntityResponseTimeSteps implements En {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;

  @Inject
  public GetEntityResponseTimeSteps(
      PersonsHelper personsHelper,
      ContactHelper contactHelper,
      CaseHelper caseHelper,
      PersonApiService personApiService,
      EventApiService eventApiService,
      CaseApiService caseApiService,
      ContactApiService contactApiService,
      SampleApiService sampleApiService,
      RestAssuredClient restAssuredClient,
      ObjectMapper objectMapper) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;

    When(
        "API: I check response time for get all persons uuids is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 3; i++) {
            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder().method(Method.GET).path(PERSONS_PATH + UUIDS_PATH).build());
            long elapsedTime = response.getTime();
            validateResponseStatus(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                PERSONS_PATH + UUIDS_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              PERSONS_PATH + UUIDS_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for get all events uuids is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder().method(Method.GET).path(EVENTS_PATH + UUIDS_PATH).build());
            long elapsedTime = response.getTime();
            validateResponseStatus(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                EVENTS_PATH + UUIDS_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              EVENTS_PATH + UUIDS_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for get all cases uuids is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder().method(Method.GET).path(CASES_PATH + UUIDS_PATH).build());
            long elapsedTime = response.getTime();
            validateResponseStatus(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                CASES_PATH + UUIDS_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              CASES_PATH + UUIDS_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for get all contacts uuids is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder().method(Method.GET).path(CONTACTS_PATH + UUIDS_PATH).build());
            long elapsedTime = response.getTime();
            validateResponseStatus(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                CONTACTS_PATH + UUIDS_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              CONTACTS_PATH + UUIDS_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for get all samples uuids is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder().method(Method.GET).path(SAMPLES_PATH + UUIDS_PATH).build());
            long elapsedTime = response.getTime();
            validateResponseStatus(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                SAMPLES_PATH + UUIDS_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              SAMPLES_PATH + UUIDS_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });
  }

  private long calculateAverageTime(List<Long> list) {
    long sum = 0;
    for (long value : list) {
      sum = sum + value;
    }
    return sum / list.size();
  }

  private void validateResponseStatus(Response response) {
    Assert.assertEquals(response.getStatusCode(), 200, "Request response status is not correct");
  }
}
