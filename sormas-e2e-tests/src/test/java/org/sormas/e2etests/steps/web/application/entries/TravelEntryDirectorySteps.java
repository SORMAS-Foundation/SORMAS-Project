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
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.*;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.BULK_ACTIONS_EVENT_DIRECTORY;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAMPLE_DELETION_POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.tasks.TaskManagementPage.BULK_EDIT_BUTTON;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class TravelEntryDirectorySteps implements En {
  public static final String userDirPath = System.getProperty("user.dir");
  private final WebDriverHelpers webDriverHelpers;
  public static String fullName;
  private static BaseSteps baseSteps;
  static Map<String, Integer> headersMap;

  @Inject
  public TravelEntryDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      ApiState apiState,
      AssertHelpers assertHelpers,
      BaseSteps baseSteps,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "I click on the Import button from Travel Entries directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(IMPORT_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FILE_PICKER);
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
        "I click on the New Travel Entry button from Epidemiological data tab in Case directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPI_DATA_CASE_NEW_TRAVEL_ENTRY_DE_BUTTON);
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
          for (int i = 4; i < number + 4; i++) {
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
              PERSON_FILTER_INPUT, CreateNewTravelEntrySteps.aTravelEntry.getPersonUuid());
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
        "I choose the reason of deletion in popup for Travel Entry",
        () -> {
          webDriverHelpers.selectFromCombobox(
              DELETE_TRAVEL_ENTRY_POPUP, "Entit\u00E4t ohne Rechtsgrund angelegt");
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(TRAVEL_ENTRY_DELETE_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(TRAVEL_ENTRY_DELETE_POPUP);
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
          webDriverHelpers.waitForSpinnerNotVisible(15);
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
              CreateNewTravelEntrySteps.previousWeekDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
          //  + 1; // because weeks are counting since end of december previous year

          LocalDate newYearSuprise = LocalDate.now().minusDays(7);
          String lastEpiWeek = "Wo " + week + "-" + newYearSuprise.getYear();
          webDriverHelpers.selectFromCombobox(WEEK_FROM_OPTION_COMBOBOX, lastEpiWeek);
        });

    Then(
        "I apply the last epi week for week to combobox on Travel Entry directory page",
        () -> {
          int week =
              CreateNewTravelEntrySteps.previousWeekDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                  + 1; // because weeks are counting since end of december previous year
          if (week == 53) week = 1;
          LocalDate newYearSuprise = LocalDate.now().minusDays(7);
          String lastEpiWeek = "Wo " + week + "-" + LocalDate.now().getYear();
          webDriverHelpers.selectFromCombobox(WEEK_TO_OPTION_COMBOBOX, lastEpiWeek);
        });
    Then(
        "I apply the week before the last epi week for week to combobox on Travel Entry directory page",
        () -> {
          int week =
              CreateNewTravelEntrySteps.previousWeekDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
          if (week == 53) week = 1;

          LocalDate newYearSuprise = LocalDate.now().minusDays(7);

          String lastEpiWeek = "Wo " + week + "-" + newYearSuprise.getYear();
          webDriverHelpers.selectFromCombobox(WEEK_TO_OPTION_COMBOBOX, lastEpiWeek);
        });

    When(
        "I click on first filtered record in Travel Entry",
        () -> {
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.scrollToElement(TRAVEL_ENTRY_FIRST_RECORD_IN_TABLE);
          webDriverHelpers.doubleClickOnWebElementBySelector(TRAVEL_ENTRY_FIRST_RECORD_IN_TABLE);
        });

    When(
        "I check that the Entries table structure is correct (DE specific)",
        () -> {
          headersMap = extractColumnHeadersHashMap();
          String headers = headersMap.toString();
          System.out.println(headers);
          softly.assertTrue(
              headers.contains("EINREISE-ID=0"),
              "The TRAVEL ENTRY ID column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("EXTERNE ID=1"),
              "The EXTERNAL ID column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("VORNAME DER PERSON=2"),
              "The PERSON FIRST NAME column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("NACHNAME DER PERSON=3"),
              "The PERSON LAST NAME column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("HEIMAT LANDKREIS/KREISFREIE STADT=4"),
              "The HOME DISTRICT column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("EINREISEORT=5"),
              "The POINT OF ENTRY NAME column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("GENESEN=6"), "The RECOVERED column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("GEIMPFT=7"), "The VACCINATED column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("NEGATIV GETESTET=8"),
              "The TESTED NEGATIVE column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("QUARANT\u00c4NE ENDE=9"),
              "The QUARANTINE END column is not correctly displayed!");
          softly.assertAll();
        });

    When(
        "I check that ([^\"]*) is visible",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          switch (option) {
            case "Person Name, External ID or Travel Entry ID Free Text Filter":
              selector = PERSON_FILTER_INPUT;
              break;
            case "Recovered Checkbox Filter":
              selector = RECOVERED_ENTRIES;
              break;
            case "Vaccinated Checkbox Filter":
              selector = VACCINATED_ENTRIES;
              break;
            case "Negatively Tested Checkbox Filter":
              selector = NEGATIVE_TESTES_ENTRIES;
              break;
            case "Converted to Case Checkbox Filter":
              selector = CONVERTE_TO_CASE_ENTRIES;
              break;
          }
          webDriverHelpers.scrollToElementUntilIsVisible(selector);
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(selector, 2),
              option + " is not visible!");
          softly.assertAll();
        });

    When(
        "I check if import Travel Entry popup has not import option in DE version",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ENTRY_IMPORT_TEMPLATE_LABEL, 1),
              "Download import template option is available but it shouldn't");
          softly.assertAll();
        });

    When(
        "I select the specific German travel entry CSV file in the file picker with {string} file name",
        (String fileName) -> {
          webDriverHelpers.sendFile(FILE_PICKER, userDirPath + "/uploads/" + fileName);
        });

    When(
        "I select to create new person from the Import Travel Entries popup DE and Save popup if needed",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 10)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I check if the New Travel Entry button is displayed in Travel Entries directory",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_TRAVEL_ENTRY_BUTTON));

    When(
        "I check if csv file for travel entry is imported successfully",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(
                  TRAVEL_ENTRIES_IMPORT_SUCCESSFUL_HEADER_DE, 20),
              "CSV file has been not imported");
          softly.assertAll();
        });

    When(
        "I close import popup in Travel Entry",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_IMPORT_TRAVEL_ENTRY_POPUP));
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(ENTRY_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(ENTRY_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(ENTRY_DETAILED_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(ENTRY_DETAILED_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
