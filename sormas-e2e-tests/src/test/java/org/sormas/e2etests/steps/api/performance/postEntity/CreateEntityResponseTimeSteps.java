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
package org.sormas.e2etests.steps.api.performance.postEntity;

import static org.sormas.e2etests.constants.api.Endpoints.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java8.En;
import customreport.data.TableDataManager;
import io.restassured.http.Method;
import io.restassured.response.Response;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.api.*;
import org.sormas.e2etests.entities.services.api.*;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.api.CaseHelper;
import org.sormas.e2etests.helpers.api.ContactHelper;
import org.sormas.e2etests.helpers.api.PersonsHelper;
import org.testng.Assert;

@Slf4j
public class CreateEntityResponseTimeSteps implements En {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;

  @Inject
  public CreateEntityResponseTimeSteps(
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
        "API: I check response time for person creation is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Person createPersonObject = personApiService.buildGeneratedPerson();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Person> personBody = List.of(createPersonObject);
            objectMapper.writeValue(out, personBody);
            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder()
                        .method(Method.POST)
                        .body(out.toString())
                        .path(PERSONS_PATH + POST_PATH)
                        .build());
            long elapsedTime = response.getTime();
            validateResponseBody(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                PERSONS_PATH + POST_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              PERSONS_PATH + POST_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for event creation is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Event eventApiPojo = eventApiService.buildGeneratedEvent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Event> eventBody = List.of(eventApiPojo);
            objectMapper.writeValue(out, eventBody);
            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder()
                        .method(Method.POST)
                        .body(out.toString())
                        .path(EVENTS_PATH + POST_PATH)
                        .build());
            long elapsedTime = response.getTime();
            validateResponseBody(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                EVENTS_PATH + POST_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              EVENTS_PATH + POST_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for case creation is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Person personApiPojo = personApiService.buildGeneratedPerson();
            personsHelper.createNewPerson(personApiPojo);
            Case cazeApiPojo = caseApiService.buildGeneratedCase(personApiPojo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Case> caseBody = List.of(cazeApiPojo);
            objectMapper.writeValue(out, caseBody);

            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder()
                        .method(Method.POST)
                        .body(out.toString())
                        .path(CASES_PATH + POST_PATH)
                        .build());
            long elapsedTime = response.getTime();
            validateResponseBody(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                CASES_PATH + POST_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              CASES_PATH + POST_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for contact creation is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Person personApiPojo = personApiService.buildGeneratedPerson();
            personsHelper.createNewPerson(personApiPojo);
            Contact contactApiPojo = contactApiService.buildGeneratedContact(personApiPojo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Contact> contactBody = List.of(contactApiPojo);
            objectMapper.writeValue(out, contactBody);

            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder()
                        .method(Method.POST)
                        .body(out.toString())
                        .path(CONTACTS_PATH + POST_PATH)
                        .build());
            long elapsedTime = response.getTime();
            validateResponseBody(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                CONTACTS_PATH + POST_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              CONTACTS_PATH + POST_PATH,
              String.valueOf(averageElapsedTime),
              String.valueOf(maxWaitingTime));
        });

    When(
        "API: I check response time for sample creation is less than {int} milliseconds",
        (Integer maxWaitingTime) -> {
          List<Long> elapsedTimes = new ArrayList<>();
          for (int i = 0; i < 10; i++) {
            Person personApiPojo = personApiService.buildGeneratedPerson();
            personsHelper.createNewPerson(personApiPojo);
            Case cazeApiPojo = caseApiService.buildGeneratedCase(personApiPojo);
            caseHelper.createCase(cazeApiPojo);
            Sample sampleApiPojo = sampleApiService.buildGeneratedSample(cazeApiPojo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Sample> sampleBody = List.of(sampleApiPojo);
            objectMapper.writeValue(out, sampleBody);

            Response response =
                restAssuredClient.sendRequestAndGetResponse(
                    Request.builder()
                        .method(Method.POST)
                        .body(out.toString())
                        .path(SAMPLES_PATH + POST_PATH)
                        .build());
            long elapsedTime = response.getTime();
            validateResponseBody(response);
            elapsedTimes.add(elapsedTime);
          }
          long averageElapsedTime = calculateAverageTime(elapsedTimes);
          if (averageElapsedTime > Long.valueOf(maxWaitingTime)) {
            TableDataManager.addApiRowEntity(
                SAMPLES_PATH + POST_PATH,
                averageElapsedTime + " - FAILED",
                String.valueOf(maxWaitingTime));
            Assert.fail("Request takes more than " + maxWaitingTime + " milliseconds");
          }
          TableDataManager.addApiRowEntity(
              SAMPLES_PATH + POST_PATH,
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

  private void validateResponseBody(Response response) {
    String regexUpdatedResponseBody = response.getBody().asString().replaceAll("[^a-zA-Z0-9]", "");
    Assert.assertEquals(regexUpdatedResponseBody, "OK", "Request response body is not correct");
  }
}
