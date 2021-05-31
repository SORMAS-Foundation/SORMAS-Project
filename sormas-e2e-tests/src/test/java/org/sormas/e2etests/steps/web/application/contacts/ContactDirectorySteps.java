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

import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;

public class ContactDirectorySteps implements En {

  protected BaseSteps baseSteps;
  protected WebDriverHelpers webDriverHelpers;

  @Inject
  public ContactDirectorySteps(WebDriverHelpers webDriverHelpers, BaseSteps baseSteps) {
    this.baseSteps = baseSteps;
    this.webDriverHelpers = webDriverHelpers;
    When(
        "I click on the NEW CONTACT button",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CONTACT_BUTTON, FIRST_NAME_OF_CONTACT_PERSON_INPUT));

    When(
        "I open the last created contact",
        () -> {
          String contactUUID = EditContactSteps.aContact.getUuid();
          searchAfterContactByMultipleOptions(contactUUID);
          openContactFromResultsByUUID(contactUUID);
        });

    Then(
        "I check that the last created contact was deleted",
        () -> {
              Truth.assertThat(webDriverHelpers.getNumberOfElements(CONTACT_GRID_RESULTS_ROWS))
              .isEqualTo(0);
        });
  }

  private void searchAfterContactByMultipleOptions(String idPhoneNameEmail) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
    webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, idPhoneNameEmail);
    webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
  }

  private void openContactFromResultsByUUID(String uuid) {
    By uuidLocator =
        By.xpath(String.format("%s", CONTACT_RESULTS_UUID_LOCATOR.replace("placeholder", uuid)));
    webDriverHelpers.waitUntilElementIsVisibleAndClickable((uuidLocator));
    webDriverHelpers.clickOnWebElementBySelector((uuidLocator));
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
  }
}
