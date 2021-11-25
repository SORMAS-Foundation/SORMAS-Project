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
import javax.inject.Named;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;

public class ContactDirectorySteps implements En {

  protected WebDriverHelpers webDriverHelpers;

  @Inject
  public ContactDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      AssertHelpers assertHelpers,
      DataOperations dataOperations,
      @Named("ENVIRONMENT_URL") String environmentUrl)
      throws InterruptedException {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I navigate to the last created contact via the url$",
        () -> {
          String LAST_CREATED_CONTACT_URL =
              environmentUrl
                  + "/sormas-ui/#!contacts/data/"
                  + apiState.getCreatedContact().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CONTACT_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "I click on the NEW CONTACT button",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CONTACT_BUTTON, FIRST_NAME_OF_CONTACT_PERSON_INPUT));

    When(
        "I click on the DETAILED radiobutton from Contact directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_DIRECTORY_DETAILED_RADIOBUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(RESULTS_GRID_HEADER, "Sex")), 20);
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(GRID_HEADERS, 18);
        });

    When(
        "I filter by Contact uuid",
        () -> {
          String contactUuid = apiState.getCreatedContact().getUuid();
          By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, contactUuid));
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(uuidLocator);
          Thread.sleep(5000); // mandatory refresh to have the grid refreshed
        });

    When(
        "^I click on Line Listing button$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING));

    When(
        "I open the last created contact",
        () -> {
          searchAfterContactByMultipleOptions(apiState.getCreatedContact().getUuid());
          openContactFromResultsByUUID(apiState.getCreatedContact().getUuid());
        });

    Then(
        "I check that number of displayed contact results is (\\d+)",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Truth.assertThat(
                            webDriverHelpers.getNumberOfElements(CONTACT_GRID_RESULTS_ROWS))
                        .isEqualTo(number)));
  }

  private void searchAfterContactByMultipleOptions(String idPhoneNameEmail) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
    webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, idPhoneNameEmail);
    webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
  }

  private void openContactFromResultsByUUID(String uuid) {
    By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, uuid));
    webDriverHelpers.clickOnWebElementBySelector((uuidLocator));
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
  }
}
