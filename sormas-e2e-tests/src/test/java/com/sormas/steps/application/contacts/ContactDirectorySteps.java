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

package com.sormas.steps.application.contacts;

import static com.sormas.pages.application.contacts.ContactDirectoryPage.NEW_CONTACT_BUTTON;
import static com.sormas.pages.application.contacts.CreateNewContactPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;

import com.sormas.helpers.WebDriverHelpers;
import cucumber.api.java8.En;
import javax.inject.Inject;

public class ContactDirectorySteps implements En {

  @Inject
  public ContactDirectorySteps(WebDriverHelpers webDriverHelpers) {

    When(
        "I click on the NEW CONTACT button",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CONTACT_BUTTON, FIRST_NAME_OF_CONTACT_PERSON_INPUT));
  }
}
