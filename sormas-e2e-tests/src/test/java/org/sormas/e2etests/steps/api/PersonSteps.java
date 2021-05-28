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
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.api.PersonsHelper;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.state.ApiState;

public class PersonSteps implements En {

  @Inject
  public PersonSteps(PersonsHelper personsHelper, ApiState apiState, Faker faker) {

    When(
        "API: I receive the person",
        () -> {
          List<String> personUuids = apiState.getResponse().jsonPath().get();
        });

    When(
        "API: I create a new person",
        () -> {
          Person createPersonObject =
              Person.builder()
                  .uuid(UUID.randomUUID().toString())
                  .firstName(faker.name().firstName())
                  .lastName(faker.name().lastName())
                  .build();
          apiState.setEditPerson(createPersonObject);
          personsHelper.createNewPerson(createPersonObject);
        });

    When("API: I receive all person ids", personsHelper::getAllPersonUuid);
  }
}
