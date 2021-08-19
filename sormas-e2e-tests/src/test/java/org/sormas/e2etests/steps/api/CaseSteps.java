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
import org.sormas.e2etests.enums.APITestData.POSTCase_ErrorMessages;
import org.sormas.e2etests.helpers.api.CaseHelper;
import org.sormas.e2etests.helpers.api.CommunityHelper;
import org.sormas.e2etests.helpers.api.CountryHelper;
import org.sormas.e2etests.helpers.api.PersonsHelper;
import org.sormas.e2etests.pojo.api.Case;
import org.sormas.e2etests.services.api.CaseApiService;
import org.sormas.e2etests.services.api.PostCaseBodyService;
import org.sormas.e2etests.services.api.PostPersonBodyService;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.state.BodyResources;

public class CaseSteps implements En {

  @Inject
  public CaseSteps(
      CaseHelper caseHelper,
      PersonsHelper personHelper,
      ApiState apiState,
      CaseApiService caseApiService,
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

    Given(
        "I try to enter all lower case healthFacility for a new case",
        () -> {
          bodyResources.setBody("[" + bodyService.generatePostCaseBodyWithInvalidFacility() + "]");
        });

    When(
        "I create a new case",
        () -> {
          caseHelper.postCases("push", bodyResources.getBody());
        });

    Then(
        "I get the error message TOO_OLD",
        () -> {
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");
          assertThat(apiState.getResponse().getBody().asString())
              .ignoringCase()
              .equals(String.valueOf(POSTCase_ErrorMessages.TOO_OLD));
        });

    Then(
        "I get a general error message ERROR",
        () -> {
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");
          assertThat(apiState.getResponse().getBody().asString())
              .ignoringCase()
              .equals(String.valueOf(POSTCase_ErrorMessages.ERROR));
        });

    Then(
        "I get 200 OK response back",
        () -> {
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");
          assertThat(apiState.getResponse().getBody().asString()).contains(String.valueOf("OK"));
        });

    Then(
        "I get the error message Unknown disease and all the valid values are shown",
        () -> {
          assertThat(apiState.getResponse().getStatusCode()).toString().equalsIgnoreCase("400");
          assertThat(apiState.getResponse().getBody().asString())
              .contains(String.valueOf(POSTCase_ErrorMessages.UNKNOWN_DISEASE));
        });

    Then(
        "I can query case by UUID",
        () -> {
          String caseUUID = bodyResources.getCaseUUID();
          caseHelper.postCasesQueryByUUID(caseUUID);
          assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");
          assertThat(apiState.getResponse().jsonPath().get("uuid").toString()).contains(caseUUID);
        });

    When(
        "API: I create a new case",
        () -> {
          Case caze = caseApiService.buildGeneratedCase(apiState.getLastCreatedPerson());
          caseHelper.createCase(caze);
          apiState.setCreatedCase(caze);
        });
  }
}
