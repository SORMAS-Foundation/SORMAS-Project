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

import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class CreateNewContactSteps implements En {

  @Inject
  public CreateNewContactSteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I create a new contact$",
        () -> {
          String timestamp = String.valueOf(System.currentTimeMillis());
          webDriverHelpers.fillInWebElement(
              FIRST_NAME_OF_CONTACT_PERSON_INPUT,
              "FIRSTNAMEAUTOMATION_" + timestamp + Faker.instance().name().firstName());
          webDriverHelpers.fillInWebElement(
              LAST_NAME_OF_CONTACT_PERSON_INPUT,
              "LAST_NAMEAUTOMATION_" + timestamp + Faker.instance().name().lastName());
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, "Male");
          webDriverHelpers.selectFromCombobox(DISEASE_OF_SOURCE_CASE_COMBOBOX, "COVID-19");
          webDriverHelpers.selectFromCombobox(
              RESPONSIBLE_REGION_COMBOBOX, "Voreingestellte Bundesländer");
          webDriverHelpers.selectFromCombobox(
              RESPONSIBLE_DISTRICT_COMBOBOX, "Voreingestellter Landkreis");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID);
        });
  }
}
