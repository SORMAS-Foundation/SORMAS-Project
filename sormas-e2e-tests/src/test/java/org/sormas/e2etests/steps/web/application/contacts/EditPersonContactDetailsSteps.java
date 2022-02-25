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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.EditPersonContactDetailsPage.*;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Person;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.persons.EditPersonSteps;

public class EditPersonContactDetailsSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private Person newCreatederson;

  @Inject
  public EditPersonContactDetailsSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    Then(
        "I edit all Person primary contact details and save",
        () -> {
          newCreatederson = EditPersonSteps.newGeneratedPerson;
          webDriverHelpers.clickOnWebElementBySelector(PRIMARY_CONTACT_DETAILS_EDIT_EMAIL_FIELD);
          fillContactInformationInput(newCreatederson.getEmailAddress());
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);

          webDriverHelpers.clickOnWebElementBySelector(PRIMARY_CONTACT_DETAILS_EDIT_PHONE_FIELD);
          fillContactInformationInput(newCreatederson.getPhoneNumber());
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);

          webDriverHelpers.clickOnWebElementBySelector(PRIMARY_CONTACT_DETAILS_EDIT_OTHER_FIELD);
          fillContactInformationInput(newCreatederson.getPersonContactDetailsContactInformation());
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });
  }

  private void fillContactInformationInput(String data) {
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONTACT_INFORMATION_INPUT);
    webDriverHelpers.clearAndFillInWebElement(CONTACT_INFORMATION_INPUT, data);
  }
}
