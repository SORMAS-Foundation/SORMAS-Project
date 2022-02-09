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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.PersonContactDetailsPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Person;

public class PersonContactDetailsSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Person newGeneratedPerson;

  @Inject
  public PersonContactDetailsSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    Then(
        "I complete all fields from Person Contact Details popup and save",
        () -> {
          newGeneratedPerson = EditContactPersonSteps.newGeneratedPerson;
          selectTypeOfContactDetails(
              newGeneratedPerson.getPersonContactDetailsTypeOfContactDetails());
          fillContactInformationInput(
              newGeneratedPerson.getPersonContactDetailsContactInformation());
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });

    Then(
        "I enter an incorrect phone number and confirm",
        () -> {
          selectTypeOfContactDetails("Phone");
          fillContactInformationInput("ABCdef!@#.");
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });

    Then(
        "I enter an incorrect email and confirm",
        () -> {
          selectTypeOfContactDetails("Email");
          fillContactInformationInput("1234567890");
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });
  }

  private void selectTypeOfContactDetails(String type) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_CONTACT_DETAILS_COMBOBOX, type);
  }

  private void fillContactInformationInput(String data) {
    webDriverHelpers.fillInWebElement(CONTACT_INFORMATION_INPUT, data);
  }
}
