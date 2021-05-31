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
import org.sormas.e2etests.helpers.api.ContactHelper;
import org.sormas.e2etests.pojo.api.Contact;
import org.sormas.e2etests.services.api.ContactApiService;
import org.sormas.e2etests.state.ApiState;

public class ContactSteps implements En {

  @Inject
  public ContactSteps(
      ContactHelper contactHelper, ContactApiService contactApiService, ApiState apiState) {

    When(
        "API: I create a new contact",
        () -> {
          Contact contact = contactApiService.buildGeneratedContact(apiState.getEditPerson());
          contactHelper.push(contact);
          apiState.setCreatedContact(contact);
        });
  }
}
