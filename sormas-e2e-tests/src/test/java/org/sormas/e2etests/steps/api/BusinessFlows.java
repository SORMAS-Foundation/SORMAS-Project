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

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.helpers.api.CaseHelper;
import org.sormas.e2etests.helpers.api.PersonsHelper;
import org.sormas.e2etests.helpers.api.SampleHelper;
import org.sormas.e2etests.pojo.api.Case;
import org.sormas.e2etests.pojo.api.Lab;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.pojo.api.Sample;
import org.sormas.e2etests.services.api.CaseApiService;
import org.sormas.e2etests.services.api.PersonApiService;
import org.sormas.e2etests.services.api.SampleApiService;
import org.sormas.e2etests.state.ApiState;

public class BusinessFlows implements En {
  private final int number;

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
    number = 10;

    When(
        "API: I create several new cases",
        () -> {
          List<Case> caseList = new ArrayList<>();
          String uuid = UUID.randomUUID().toString();
          Person person = personApiService.buildGeneratedPerson();
          person = person.toBuilder().firstName(person.getFirstName() + uuid).build();
          for (int i = 0; i < number; i++) {
            person =
                person.toBuilder()
                    .uuid(UUID.randomUUID().toString())
                    .lastName(faker.name().lastName())
                    .build();
            apiState.setEditPerson(person);
            personsHelper.createNewPerson(person);

            Case caze = caseApiService.buildGeneratedCase(apiState.getEditPerson());
            caze =
                caze.toBuilder()
                    .outcome(CaseOutcome.getRandomOutcome())
                    .disease(Disease.getRandomDisease())
                    .caseClassification(CaseClasification.getRandomClassification())
                    .build();
            caseHelper.createCase(caze);
            caseList.add(caze);
          }
          apiState.setCreatedCases(caseList);
        });

    When(
        "API: I create several new cases with a new sample foreach of them",
        () -> {
          List<Sample> sampleList = new ArrayList<>();
          String uuid = UUID.randomUUID().toString();
          Person person = personApiService.buildGeneratedPerson();
          person = person.toBuilder().firstName(person.getFirstName() + uuid).build();
          for (int i = 0; i < number; i++) {
            person =
                person.toBuilder()
                    .uuid(UUID.randomUUID().toString())
                    .lastName(faker.name().lastName())
                    .build();
            apiState.setEditPerson(person);
            personsHelper.createNewPerson(person);

            Case caze = caseApiService.buildGeneratedCase(apiState.getEditPerson());
            caseHelper.createCase(caze);
            apiState.setCreatedCase(caze);

            Sample sample = sampleApiService.buildGeneratedSample(apiState.getCreatedCase());
            sample =
                sample.toBuilder()
                    .labSampleID(UUID.randomUUID().toString())
                    .receivedDate(new Date())
                    .received(true)
                    .pathogenTestResult(PathogenTestResults.getRandomResult())
                    .specimenCondition(SpecimenConditions.getRandomCondition())
                    .lab(Lab.builder().uuid(LabUuid.getRandomUuid()).build())
                    .build();
            sampleHelper.createSample(sample);
            sampleList.add(sample);
          }
          apiState.setCreatedSamples(sampleList);
        });
  }
}
