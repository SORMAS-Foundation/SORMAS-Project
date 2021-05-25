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

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.api.CaseHelper;
import org.sormas.e2etests.helpers.api.CommunityHelper;
import org.sormas.e2etests.helpers.api.CountryHelper;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.utils.TestUtils;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.sormas.e2etests.constants.api.ResourceFiles.POST_CASES_JSON_BODY;

public class CaseSteps implements En {

  @Inject
  public CaseSteps(
      CaseHelper caseHelper,
      ApiState apiState,
      CommunityHelper communityHelper,
      CountryHelper countryHelper) {
      String json = TestUtils.readJsonToString(POST_CASES_JSON_BODY);
      AtomicReference<String> communitiesAllSince = new AtomicReference<>();
      AtomicReference<String> countriesAllSince = new AtomicReference<>();
      AtomicReference<String> casesAllSince = new AtomicReference<>();

    When(
        "I create a new case",
        () -> {
          caseHelper.postCases("push", json);
          Truth.assertWithMessage(
              "Response status code is not a succesfull one",
              apiState.getResponse().getStatusCode(),
              is(startsWith("2")));
        });

    Then(
        "I get the cases created since {int}",
        (Integer since) -> {
          caseHelper.getAllCasesSince(since);
            Truth.assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");

            casesAllSince.set(apiState.getResponse().getBody().prettyPrint());
            System.out.println(casesAllSince);
        });

    When(
        "I get al communities since {int}",
        (Integer int1) -> {
          communityHelper.getCommunitiesSince(int1);

          Truth.assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");

          communitiesAllSince.set(apiState.getResponse().getBody().prettyPrint());
          System.out.println(communitiesAllSince);
        });

    When(
        "I get all countries since {int}",
        (Integer since) -> {
          countryHelper.getAllCountriesSince(since);
            Truth.assertThat(apiState.getResponse().getStatusCode()).toString().startsWith("2");

            countriesAllSince.set(apiState.getResponse().getBody().prettyPrint());
            System.out.println(countriesAllSince);
        });
  }
}
