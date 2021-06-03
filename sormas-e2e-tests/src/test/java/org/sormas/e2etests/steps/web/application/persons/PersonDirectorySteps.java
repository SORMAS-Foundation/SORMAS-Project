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

package org.sormas.e2etests.steps.web.application.persons;

import static org.sormas.e2etests.pages.application.persons.EditPersonPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Person;
import org.sormas.e2etests.steps.web.application.contacts.EditContactPersonSteps;

public class PersonDirectorySteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  protected Person createdPerson;

  @Inject
  public PersonDirectorySteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    Then(
        "I open the last created person",
        () -> {
          createdPerson = EditContactPersonSteps.fullyDetailedPerson;
          searchAfterPersonByMultipleOptions(createdPerson.getUuid());
          openPersonFromResultsByUUID(createdPerson.getUuid());
        });
  }

  private void searchAfterPersonByMultipleOptions(String idPhoneNameEmail) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
    webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, idPhoneNameEmail);
    webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
  }

  private void openPersonFromResultsByUUID(String uuid) {
    By uuidLocator = By.cssSelector(String.format(PERSON_RESULTS_UUID_LOCATOR, uuid));
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(uuidLocator);
    webDriverHelpers.clickOnWebElementBySelector(uuidLocator);
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
  }
}
