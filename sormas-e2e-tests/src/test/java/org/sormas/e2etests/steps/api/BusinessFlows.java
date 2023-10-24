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

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.api.Case;
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.entities.pojo.api.Sample;
import org.sormas.e2etests.entities.services.api.CaseApiService;
import org.sormas.e2etests.entities.services.api.PersonApiService;
import org.sormas.e2etests.entities.services.api.SampleApiService;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.helpers.api.sormasrest.CaseHelper;
import org.sormas.e2etests.helpers.api.sormasrest.PersonsHelper;
import org.sormas.e2etests.helpers.api.sormasrest.SampleHelper;
import org.sormas.e2etests.state.ApiState;

public class BusinessFlows implements En {

  @Inject
  public BusinessFlows(
      CaseHelper caseHelper,
      CaseApiService caseApiService,
      PersonApiService personApiService,
      SampleHelper sampleHelper,
      SampleApiService sampleApiService,
      ApiState apiState,
      PersonsHelper personsHelper,
      Faker faker) {

    When(
        "API: I create {int} new cases",
        (Integer numberOfCases) -> {
          List<Case> caseList = new ArrayList<>();
          Person person = personApiService.buildGeneratedPerson();
          personsHelper.createNewPerson(person);
          apiState.setLastCreatedPerson(person);
          for (int i = 0; i < numberOfCases; i++) {
            Case caze = caseApiService.buildGeneratedCase(apiState.getLastCreatedPerson());
            caseHelper.createCase(caze);
            caseList.add(caze);
          }
          apiState.setCreatedCases(caseList);
        });

    When(
        "API: I create {int} new cases with chosen {string} region",
        (Integer numberOfCases, String region) -> {
          List<Case> caseList = new ArrayList<>();
          Person person = personApiService.buildGeneratedPerson();
          personsHelper.createNewPerson(person);
          apiState.setLastCreatedPerson(person);
          RegionsValues regionName = RegionsValues.Berlin;
          // System.out.print(" getUuidDE " + regionName.getUuidDE() + "\n");
          // System.out.print(" getUuidMain " + regionName.getUuidMain() + "\n");
          // System.out.print("ssss ss " + regionName.getName() + "\n");
          DistrictsValues districtName = DistrictsValues.SKBerlinCharlottenburgWilmersdorf;
          // System.out.print("getUuidDE   " + districtName.getUuidDE() + "\n");
          // System.out.print(" getUuidMain " + districtName.getUuidMain() + "\n");
          // System.out.print(" dsdsd " + districtName.getName() + "\n");

          //   RegionsValues regionName = null;
          //  DistrictsValues districtName = null;
          // switch (region) {
          //  case "Berlin":
          //   regionName = RegionsValues.Berlin;
          //  districtName = DistrictsValues.SKBerlinCharlottenburgWilmersdorf;
          // break;
          // case "Voreingestellte Bundesl\u00E4nder":
          //  regionName = RegionsValues.VoreingestellteBundeslander;
          // districtName = DistrictsValues.VoreingestellterLandkreis;
          // break;
          // }
          for (int i = 0; i < numberOfCases; i++) {
            Case caze =
                caseApiService.buildGeneratedCaseWithCustomDistrictRegion(
                    apiState.getLastCreatedPerson(), regionName, districtName);
            caseHelper.createCase(caze);
            caseList.add(caze);
          }
          apiState.setCreatedCases(caseList);
        });

    When(
        "API: I create {int} new cases with a new sample foreach of them",
        (Integer numberOfCasesAndSamples) -> {
          List<Sample> sampleList = new ArrayList<>();
          Person person = personApiService.buildGeneratedPerson();
          apiState.setLastCreatedPerson(person);
          personsHelper.createNewPerson(person);
          for (int i = 0; i < numberOfCasesAndSamples; i++) {

            Case caze = caseApiService.buildGeneratedCase(apiState.getLastCreatedPerson());
            caseHelper.createCase(caze);
            apiState.setCreatedCase(caze);

            Sample sample = sampleApiService.buildGeneratedSample(apiState.getCreatedCase());
            sampleHelper.createSample(sample);
            sampleList.add(sample);
          }
          apiState.setCreatedSamples(sampleList);
        });
  }
}
