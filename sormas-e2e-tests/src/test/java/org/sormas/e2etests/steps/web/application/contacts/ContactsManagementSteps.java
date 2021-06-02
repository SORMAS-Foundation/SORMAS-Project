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

import static org.sormas.e2etests.pages.application.contacts.ContactManagementPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.application.contacts.EditContactSteps;

public class ContactsManagementSteps implements En {
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public ContactsManagementSteps(WebDriverHelpers webDriverHelpers) {

    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I search for Contact using Contact UUID from the last created Contact",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_SEARCH_INPUT, EditContactSteps.aContact.getUuid());
          webDriverHelpers.waitUntilWebElementHasAttributeWithValue(
              SEARCH_RESULT_CONTACT, "title", EditContactSteps.aContact.getUuid());
        });

    When(
        "^I open the first displayed Contact result$",
        () -> webDriverHelpers.clickOnWebElementBySelector(SEARCH_RESULT_CONTACT));
  }
}
