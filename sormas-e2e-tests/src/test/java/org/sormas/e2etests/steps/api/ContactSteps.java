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

import cucumber.api.java8.En;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.api.ContactHelper;
import org.sormas.e2etests.helpers.api.PersonsHelper;
import org.sormas.e2etests.pojo.api.Case;
import org.sormas.e2etests.pojo.api.Contact;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.services.api.ContactApiService;
import org.sormas.e2etests.services.api.PersonApiService;
import org.sormas.e2etests.state.ApiState;

@Slf4j
public class ContactSteps implements En {

  @Inject
  public ContactSteps(
      ContactHelper contactHelper,
      ContactApiService contactApiService,
      ApiState apiState,
      PersonsHelper personsHelper,
      PersonApiService personApiService) {

    When(
        "API: I create a new contact",
        () -> {
          Contact contact =
              contactApiService.buildGeneratedContact(apiState.getLastCreatedPerson());
          contactHelper.push(contact);
          apiState.setCreatedContact(contact);
        });

    When(
        "API: I create a new contact linked to the previous created case",
        () -> {
          Contact contact =
              contactApiService.buildGeneratedContactWithLinkedCase(
                  apiState.getLastCreatedPerson(), apiState.getCreatedCase());
          contactHelper.push(contact);
          apiState.setCreatedContact(contact);
        });

    When(
        "API: I create and link 2 Contacts to each case from previous created cases",
        () -> {
          for (Case caze : apiState.getCreatedCases()) {
            Person person1 = personApiService.buildGeneratedPerson();
            personsHelper.createNewPerson(person1);
            Person person2 = personApiService.buildGeneratedPerson();
            personsHelper.createNewPerson(person2);
            Contact contact1 = contactApiService.buildGeneratedContactWithLinkedCase(person1, caze);
            contactHelper.push(contact1);
            Contact contact2 = contactApiService.buildGeneratedContactWithLinkedCase(person2, caze);
            contactHelper.push(contact2);
          }
        });

    When("API: I receive all contacts ids", contactHelper::getAllContactsUuid);
  }
}
