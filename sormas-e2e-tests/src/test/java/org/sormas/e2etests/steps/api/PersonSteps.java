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
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.entities.services.api.PersonApiService;
import org.sormas.e2etests.helpers.api.sormasrest.PersonsHelper;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class PersonSteps implements En {

  @Inject
  public PersonSteps(
      PersonsHelper personsHelper, PersonApiService personApiService, ApiState apiState) {

    When(
        "API: I receive the person",
        () -> {
          List<String> personUuids = apiState.getResponse().jsonPath().get();
        });

    When(
        "API: I create a new person",
        () -> {
          Person createPersonObject = personApiService.buildGeneratedPerson();
          apiState.setLastCreatedPerson(createPersonObject);
          personsHelper.createNewPerson(createPersonObject);
        });

    When(
        "API: I create a new person with {string} region and {string} district",
        (String region, String district) -> {
          Person createPersonObject =
              personApiService.buildGeneratedPersonParamRegionAndDistrict(region, district);
          apiState.setLastCreatedPerson(createPersonObject);
          personsHelper.createNewPerson(createPersonObject);
        });

    When(
        "API: I create {int} persons",
        (Integer numberOfPersons) -> {
          List<Person> personList = new ArrayList<>();
          for (int i = 0; i < numberOfPersons; i++) {
            personList.add(personApiService.buildGeneratedPerson());
          }
          log.info("Pushing {} Persons", numberOfPersons);
          personsHelper.createMultiplePersons(personList);
          apiState.setLastCreatedPersonsList(personList);
        });

    When("API: I receive all person ids", personsHelper::getAllPersonUuid);
  }
}
