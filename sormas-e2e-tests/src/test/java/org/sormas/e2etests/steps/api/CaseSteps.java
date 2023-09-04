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
package org.sormas.e2etests.steps.api;

import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.api.Case;
import org.sormas.e2etests.entities.services.api.CaseApiService;
import org.sormas.e2etests.helpers.api.sormasrest.CaseHelper;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class CaseSteps implements En {

  @Inject
  public CaseSteps(CaseHelper caseHelper, ApiState apiState, CaseApiService caseApiService) {

    When(
        "API: I create a new case",
        () -> {
          Case caze = caseApiService.buildGeneratedCase(apiState.getLastCreatedPerson());
          caseHelper.createCase(caze);
          apiState.setCreatedCase(caze);
        });

    When(
        "API: I create a new case with creation date {int} days ago",
        (Integer creationTime) -> {
          Case caze =
              caseApiService.buildGeneratedCaseWithCreationDate(
                  apiState.getLastCreatedPerson(), creationTime);
          caseHelper.createCase(caze);
          apiState.setCreatedCase(caze);
        });

    When(
        "API: I create a new case with {string} region and {string} district and {string} facility",
        (String region, String district, String facility) -> {
          Case caze =
              caseApiService.buildGeneratedCaseWithParamRegionAndDistrictAndFacility(
                  apiState.getLastCreatedPerson(), region, district, facility);
          caseHelper.createCase(caze);
          apiState.setCreatedCase(caze);
        });

    When(
        "API: I create a new case Point Of Entry type with {string} region and {string} district",
        (String region, String district) -> {
          Case caze =
              caseApiService.buildGeneratedCaseTypePointOfEntry(
                  apiState.getLastCreatedPerson(), region, district);
          caseHelper.createCase(caze);
          apiState.setCreatedCase(caze);
        });

    When(
        "API: I create a new case classified as {string}",
        (String caseClassification) -> {
          Case caze =
              caseApiService.buildCaseWithClassification(
                  apiState.getLastCreatedPerson(), caseClassification);
          caseHelper.createCase(caze);
          apiState.setCreatedCase(caze);
        });

    When(
        "API: I create {int} cases",
        (Integer numberOfCases) -> {
          List<Case> casesList = new ArrayList<>();
          for (int i = 0; i < numberOfCases; i++) {
            casesList.add(
                caseApiService.buildGeneratedCase(apiState.getLastCreatedPersonsList().get(i)));
          }
          log.info("Pushing {} Cases", numberOfCases);
          caseHelper.createMultipleCases(casesList);
          apiState.setCreatedCases(casesList);
        });

    When("API: I receive all cases ids", caseHelper::getAllCasesUuid);
  }
}
