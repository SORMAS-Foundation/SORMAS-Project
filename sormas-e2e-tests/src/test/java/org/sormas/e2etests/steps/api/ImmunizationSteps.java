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
import java.util.Random;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.api.Immunization;
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.entities.services.api.ImmunizationApiService;
import org.sormas.e2etests.helpers.api.ImmunizationHelper;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class ImmunizationSteps implements En {

  private final Random random = new Random();

  @Inject
  public ImmunizationSteps(
      ImmunizationHelper immunizationHelper,
      ImmunizationApiService immunizationApiService,
      ApiState apiState,
      Faker faker) {

    When(
        "API: I create {int} new immunizations for last created person",
        (Integer numberOfImmunizations) -> {
          String lastCreatedUserUUID = apiState.getLastCreatedPerson().getUuid();
          Person person = Person.builder().uuid(lastCreatedUserUUID).build();
          int immunizationAttempts = 1;
          while (immunizationAttempts <= numberOfImmunizations) {
            Immunization immunization =
                immunizationApiService.buildGeneratedImmunizationForPerson(person);
            apiState.setCreatedImmunization(immunization);
            immunizationHelper.createNewImmunization(immunization);
            immunizationAttempts++;
          }
        });

    When(
        "API: I create 1-5 new immunizations for each person from last created persons list",
        () -> {
          for (Person person : apiState.getLastCreatedPersonsList()) {
            Person personToImmunize = Person.builder().uuid(person.getUuid()).build();
            int numberOfImmunizations = random.nextInt(6);
            List<Immunization> immunizationList = new ArrayList<>();
            for (int i = 0; i < numberOfImmunizations; i++) {
              immunizationList.add(
                  immunizationApiService.buildGeneratedImmunizationForPerson(personToImmunize));
            }
            immunizationHelper.createMultipleImmunizations(immunizationList);
          }
        });

    When("API: I receive all immunizations ids", immunizationHelper::getAllImmunizationsUUIDs);
  }
}
