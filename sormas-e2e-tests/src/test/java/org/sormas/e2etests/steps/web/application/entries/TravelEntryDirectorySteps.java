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

import cucumber.api.java8.En;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

import javax.inject.Inject;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_TO_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CONVERTE_TO_CASE_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.IMPORT_SUCCESS_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEGATIVE_TESTES_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_TRAVEL_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PERSON_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.RECOVERED_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.START_DATA_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_AGGREGATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_DIRECTORY_PAGE_APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_DIRECTORY_PAGE_SHOW_MORE_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.VACCINATED_ENTRIES;

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
        "I select the German travel entry CSV file in the file picker",
        () -> {
          webDriverHelpers.sendFile(
              FILE_PICKER, userDirPath + "/uploads/Importvorlage_Einreise_21.11.04.csv");
        });

    When(
        "I click on the START DATA IMPORT button from the Import Travel Entries popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(START_DATA_IMPORT_BUTTON);
        });

    When(
        "I select to create new person from the Import Travel Entries popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
        });

    When(
        "I confirm the save Travel Entries Import popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(COMMIT_BUTTON);
        });

    When(
        "I check that an import success notification appears in the Import Travel Entries popup",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(IMPORT_SUCCESS_DE);
        });

    When(
        "I click on the New Travel Entry button from Travel Entries directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_TRAVEL_ENTRY_BUTTON);
        });
    When(
        "I filter by Person ID on Travel Entry directory page",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_FILTER_INPUT, CreateNewTravelEntrySteps.aTravelEntry.getUuid());
        });
    And(
        "I click {string} checkbox on Travel Entry directory page",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Nur genesene Einreisende"):
              webDriverHelpers.clickOnWebElementBySelector(RECOVERED_ENTRIES);
              break;
            case ("Nur geimpfte Einreisende"):
              webDriverHelpers.clickOnWebElementBySelector(VACCINATED_ENTRIES);
              break;
            case ("Nur negativ getestete Einreisende"):
              webDriverHelpers.clickOnWebElementBySelector(NEGATIVE_TESTES_ENTRIES);
              break;
            case ("Nur in F\u00E4lle konvertierte Einreisen"):
              webDriverHelpers.clickOnWebElementBySelector(CONVERTE_TO_CASE_ENTRIES);
              break;
          }
        });
    Then(
        "I check that number of displayed Travel Entry results is {int}",
        (Integer number) -> {
          TimeUnit.SECONDS.sleep(4);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getNumberOfElements(TRAVEL_ENTRY_GRID_RESULTS_ROWS),
                      number.intValue(),
                      "Number of displayed travel entries is not correct"));
        });
    And(
        "I click APPLY BUTTON in Travel Entry Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              TRAVEL_ENTRY_DIRECTORY_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });
    And(
        "I click on SHOW MORE FILTERS BUTTON Travel Entry directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              TRAVEL_ENTRY_DIRECTORY_PAGE_SHOW_MORE_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });
    And(
        "I fill Travel Entry from input to {int} days before UI Travel Entry created on Travel Entry directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  CreateNewTravelEntrySteps.travelEntry.getReportDate().minusDays(number)));
        });
    And(
        "I fill Travel Entry to input to {int} days after UI Travel Entry created on Travel Entry directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(
                  CreateNewTravelEntrySteps.travelEntry.getReportDate().plusDays(number)));
        });
    And(
        "I fill Travel Entry from input to {int} days after before UI Travel Entry created on Travel Entry directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  CreateNewTravelEntrySteps.travelEntry.getReportDate().plusDays(number)));
        });
    Then(
        "I apply {string} to aggregation combobox on Travel Entry directory page",
        (String value) ->
            webDriverHelpers.selectFromCombobox(TRAVEL_ENTRY_AGGREGATION_COMBOBOX, value));
  }
}
