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
package org.sormas.e2etests.steps.api;

import static com.google.common.truth.Truth.assertThat;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.enums.APITestData.ErrorMessages;
import org.sormas.e2etests.helpers.api.CaseHelper;
import org.sormas.e2etests.helpers.api.CommunityHelper;
import org.sormas.e2etests.helpers.api.CountryHelper;
import org.sormas.e2etests.helpers.api.PersonsHelper;
import org.sormas.e2etests.services.API.PostCaseBodyService;
import org.sormas.e2etests.services.API.PostPersonBodyService;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.state.BodyResources;

public class CaseSteps implements En {

  @Inject
  public CaseSteps(
      CaseHelper caseHelper,
      PersonsHelper personHelper,
      ApiState apiState,
      CommunityHelper communityHelper,
      CountryHelper countryHelper,
      BodyResources bodyResources,
      PostCaseBodyService bodyService,
      PostPersonBodyService personBodyService) {

    Given(
        "I create a person",
        () -> {
          bodyResources.setBody("[" + personBodyService.generatePostPersonBody() + "]");
          personHelper.pushPerson("push", bodyResources.getBody());
          System.out.println(
              "Create person response code = " + apiState.getResponse().getStatusCode());
          System.out.println(
              "Create person response body = " + apiState.getResponse().getBody().prettyPrint());
        });

    Given(
        "I get a person from the system",
        (String reportDate) -> {
          bodyResources.setBody(bodyService.generatePostCaseBodyTooOld());
        });

    Given(
        "I try to enter invalid user for a new case",
        () -> {
          bodyResources.setBody("[" + bodyService.generatePostCaseBodyTooOld() + "]");
        });

    Given(
        "I try to enter valid data for a new case",
        () -> {
          bodyResources.setBody("[" + bodyService.generatePostCaseBodyValid() + "]");
        });

    Given(
        "I try to enter invalid disease for a new case",
        () -> {
          bodyResources.setBody("[" + bodyService.generatePostCaseBodyUnknownDisease() + "]");
        });

    When(
        "I create a new case",
        () -> {
          caseHelper.postCases("push", bodyResources.getBody());
          System.out.println("response status code = " + apiState.getResponse().getStatusCode());
          System.out.println("response body = " + apiState.getResponse().getBody().prettyPrint());
        });

    Then(
        "I get the cases created since {int}",
        (Integer since) -> {
          caseHelper.getAllCasesSince(since);
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");

          apiState.setCasesAllSince(apiState.getResponse().getBody().prettyPrint());
          System.out.println("Last cases pushed = " + apiState.getCasesAllSince());
        });

    Then(
        "I get the error message TOO_OLD",
        () -> {
          System.out.println("Response status code = " + apiState.getResponse().getStatusCode());
          System.out.println("Response body = " + apiState.getResponse().getBody().asString());
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");
          assertThat(apiState.getResponse().getBody().asString())
              .ignoringCase()
              .equals(String.valueOf(ErrorMessages.TOO_OLD));
        });

    Then(
        "I get successful response back",
        () -> {
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");
          //            assertThat(apiState.getResponse().getBody().asString())
          //                    .ignoringCase()
          //                    .equals(String.valueOf(ErrorMessages.TOO_OLD));

          assertThat(apiState.getResponse().getBody().asString()).contains(String.valueOf("OK"));
        });

    Then(
        "I get the error message Unknown disease",
        () -> {
          System.out.println("Response status code = " + apiState.getResponse().getStatusCode());
          System.out.println("Response body = " + apiState.getResponse().getBody().asString());
          assertThat(apiState.getResponse().getStatusCode()).toString().equalsIgnoreCase("400");
          assertThat(apiState.getResponse().getBody().asString())
              .contains(String.valueOf(ErrorMessages.UNKNOWN_DISEASE));
        });

    When(
        "I get al communities since {int}",
        (Integer int1) -> {
          communityHelper.getCommunitiesSince(int1);

          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");
          apiState.setCommunitiesAllSince(apiState.getResponse().getBody().prettyPrint());
          System.out.println(apiState.getCommunitiesAllSince());
        });

    When(
        "I get all facilities from region {word}",
        (String regionUUID) -> {
          countryHelper.getAllFacilitiesFromRegion(regionUUID);
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");

          apiState.setFacilitiesFromRegion(apiState.getResponse().getBody().prettyPrint());
          System.out.println(apiState.getFacilitiesFromRegion());
        });
  }
}
