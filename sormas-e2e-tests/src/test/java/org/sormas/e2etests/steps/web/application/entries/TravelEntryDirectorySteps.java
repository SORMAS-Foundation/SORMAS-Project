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

package org.sormas.e2etests.steps.web.application.entries;

import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;

public class TravelEntryDirectorySteps implements En {
  public static final String userDirPath = System.getProperty("user.dir");

  @Inject
  public TravelEntryDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      EnvironmentManager environmentManager,
      ApiState apiState,
      AssertHelpers assertHelpers) {

    When(
        "I click on the Import button from Travel Entries directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(IMPORT_BUTTON);
        });

    When(
        "I pick the travel entry CSV file",
        () -> {
          webDriverHelpers.sendFile(
              FILE_PICKER, userDirPath + "/uploads/Importvorlage_Einreise_21.11.04.csv");
        });

    When(
        "I click on the START DATA IMPORT button from the popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(START_DATA_IMPORT_BUTTON);
        });

    When(
        "I select to create new person from the popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON);
        });

    When(
        "I confirm the save popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(COMMIT_BUTTON);
        });

    When(
        "I check that an import success notification appears",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(IMPORT_SUCCESS);
        });

    When(
        "I click on the New Travel Entry button from Travel Entries directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_TRAVEL_ENTRY_BUTTON);
        });
  }
}
