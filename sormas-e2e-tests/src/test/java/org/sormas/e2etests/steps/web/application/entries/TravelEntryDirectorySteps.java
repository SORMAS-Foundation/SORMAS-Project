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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.DATE_TO_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.getCheckboxByUUID;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_DATA_IMPORT_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.CONVERTE_TO_CASE_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.DELETE_BULK;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.FIRST_NAME_IMPORTED_PERSON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.FIRST_RESULT_ID;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.IMPORT_SUCCESS_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.LAST_NAME_IMPORTED_PERSON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEGATIVE_TESTES_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_TRAVEL_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PERSON_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.RECOVERED_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.START_DATA_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_AGGREGATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_DATA_FILTER_OPTION_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_DIRECTORY_PAGE_APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_DIRECTORY_PAGE_SHOW_MORE_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_FIRST_RECORD_IN_TABLE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.TRAVEL_ENTRY_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.VACCINATED_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.WEEK_FROM_OPTION_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.WEEK_TO_OPTION_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.getCheckboxByIndex;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.BULK_ACTIONS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.BULK_EDIT_BUTTON;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class TravelEntryDirectorySteps implements En {
  public static final String userDirPath = System.getProperty("user.dir");
  private final WebDriverHelpers webDriverHelpers;
  public static String fullName;

  @Inject
  public TravelEntryDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      EnvironmentManager environmentManager,
      ApiState apiState,
      AssertHelpers assertHelpers,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on the Import button from Travel Entries directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(IMPORT_BUTTON);
        });

    When(
        "I close Import Travel Entries form",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_BUTTON);
        });

    When(
        "I close Data import popup for Travel Entries",
        () -> {
          TimeUnit.SECONDS.sleep(4);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CLOSE_DATA_IMPORT_POPUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_DATA_IMPORT_POPUP_BUTTON);
        });

    When(
        "I select the attached CSV file in the file picker from Travel Entries directory",
        () -> {
          webDriverHelpers.sendFile(FILE_PICKER, userDirPath + "/uploads/DEA_TestImport.csv");
        });

    When(
        "I select the German travel entry CSV file in the file picker",
        () -> {
          webDriverHelpers.sendFile(
              FILE_PICKER, userDirPath + "/uploads/Importvorlage_Einreise_22.04.01.csv");
        });

    When(
        "I click on the START DATA IMPORT button from the Import Travel Entries popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(START_DATA_IMPORT_BUTTON);
        });

    When(
        "I acquire the first name and last name imported person",
        () -> {
          String firstName = webDriverHelpers.getTextFromWebElement(FIRST_NAME_IMPORTED_PERSON);
          String lastName = webDriverHelpers.getTextFromWebElement(LAST_NAME_IMPORTED_PERSON);
          fullName = firstName + " " + lastName;
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
        "^I select chosen Travel Entry result",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.scrollToElement(
              getCheckboxByUUID(CreateNewTravelEntrySteps.TravelEntryUuid.getUuid()));
          webDriverHelpers.clickOnWebElementBySelector(
              getCheckboxByUUID(CreateNewTravelEntrySteps.TravelEntryUuid.getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click on the New Travel Entry button from Travel Entries directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_TRAVEL_ENTRY_BUTTON);
        });
    When(
        "^I select last created UI result in grid in Travel Entry Directory for Bulk Action$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.scrollToElement(
              getCheckboxByUUID(CreateNewTravelEntrySteps.TravelEntryUuid.getUuid()));
          webDriverHelpers.clickOnWebElementBySelector(
              getCheckboxByUUID(CreateNewTravelEntrySteps.TravelEntryUuid.getUuid()));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "^I select (\\d+) results in grid in Travel Entry Directory$",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          for (int i = 10; i < number + 10; i++) {
            webDriverHelpers.scrollToElement(getCheckboxByIndex(String.valueOf(i)));
            webDriverHelpers.clickOnWebElementBySelector(getCheckboxByIndex(String.valueOf(i)));
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    When(
        "I click on Enter Bulk Edit Mode from Travel Entry Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BULK_EDIT_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    And(
        "I click on Bulk Actions combobox in Travel Entry Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_EVENT_DIRECTORY));
    And(
        "I click on Delete button from Bulk Actions Combobox in Travel Entry Directory",
        () -> webDriverHelpers.clickOnWebElementBySelector(DELETE_BULK));
    When(
        "I filter by Person ID on Travel Entry directory page",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_FILTER_INPUT, CreateNewTravelEntrySteps.aTravelEntry.getUuid());
          System.out.println("UUID: " + CreateNewTravelEntrySteps.aTravelEntry.getUuid());
        });
    When(
        "I check if popup deletion message appeared",
        () -> {
          String expectedText = "Alle ausgew\u00E4hlten Einreisen wurden gel\u00F6scht";
          softly.assertEquals(
              webDriverHelpers.getTextFromPresentWebElement(
                  By.cssSelector(".v-Notification-description")),
              expectedText,
              "Bulk action went wrong");
          softly.assertAll();
        });
    When(
        "I filter by Person full name on Travel Entry directory page",
        () -> {
          TimeUnit.SECONDS.sleep(3); // waiting for grid refresh
          webDriverHelpers.fillAndSubmitInWebElement(PERSON_FILTER_INPUT, fullName);
          webDriverHelpers.clickOnWebElementBySelector(
              TRAVEL_ENTRY_DIRECTORY_PAGE_APPLY_FILTER_BUTTON);
        });
    When(
        "I open the imported person on Travel entry directory page",
        () -> {
          TimeUnit.SECONDS.sleep(3); // waiting for grid refresh
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_RESULT_ID);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_RESULT_ID);
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
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  CreateNewTravelEntrySteps.travelEntry.getReportDate().minusDays(number)));
        });
    And(
        "I fill Travel Entry to input to {int} days after UI Travel Entry created on Travel Entry directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(
                  CreateNewTravelEntrySteps.travelEntry.getReportDate().plusDays(number)));
        });

    And(
        "I fill Travel Entry to input to {int} days before UI Travel Entry created on Travel Entry directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_TO_COMBOBOX,
              formatter.format(
                  CreateNewTravelEntrySteps.travelEntry.getReportDate().minusDays(number)));
        });
    And(
        "I fill Travel Entry from input to {int} days after before UI Travel Entry created on Travel Entry directory page",
        (Integer number) -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_FROM_COMBOBOX,
              formatter.format(
                  CreateNewTravelEntrySteps.travelEntry.getReportDate().plusDays(number)));
        });
    Then(
        "I apply {string} to aggregation combobox on Travel Entry directory page",
        (String value) ->
            webDriverHelpers.selectFromCombobox(TRAVEL_ENTRY_AGGREGATION_COMBOBOX, value));
    Then(
        "I apply {string} to data filter option combobox on Travel Entry directory page",
        (String value) ->
            webDriverHelpers.selectFromCombobox(TRAVEL_ENTRY_DATA_FILTER_OPTION_COMBOBOX, value));
    Then(
        "I apply the last epi week for week from combobox on Travel Entry directory page",
        () -> {
          int week =
              CreateNewTravelEntrySteps.previousWeekDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + 1; //because weeks are counting since end of december previous year
          String lastEpiWeek = "Wo " + week + "-" + LocalDate.now().getYear();
          webDriverHelpers.selectFromCombobox(WEEK_FROM_OPTION_COMBOBOX, lastEpiWeek);
        });

    Then(
        "I apply the last epi week for week to combobox on Travel Entry directory page",
        () -> {
          int week =
              CreateNewTravelEntrySteps.previousWeekDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + 1; //because weeks are counting since end of december previous year
          String lastEpiWeek = "Wo " + week + "-" + LocalDate.now().getYear();
          webDriverHelpers.selectFromCombobox(WEEK_TO_OPTION_COMBOBOX, lastEpiWeek);
        });
    Then(
        "I apply the week before the last epi week for week to combobox on Travel Entry directory page",
        () -> {
          int week =
              CreateNewTravelEntrySteps.previousWeekDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
          String lastEpiWeek = "Wo " + week + "-" + LocalDate.now().getYear();
          webDriverHelpers.selectFromCombobox(WEEK_TO_OPTION_COMBOBOX, lastEpiWeek);
        });

    When(
        "I click on first filtered record in Travel Entry",
        () -> {
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.scrollToElement(TRAVEL_ENTRY_FIRST_RECORD_IN_TABLE);
          webDriverHelpers.doubleClickOnWebElementBySelector(TRAVEL_ENTRY_FIRST_RECORD_IN_TABLE);
        });
  }
}
