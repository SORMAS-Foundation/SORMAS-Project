package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BACK_TO_CASES_LIST_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_DATA_TITLE;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.PERSON_INFORMATION_TITLE;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static recorders.StepsLogger.PROCESS_ID_STRING;

import cucumber.api.java8.En;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.ContactOutcome;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class CaseDetailedTableViewSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private static BaseSteps baseSteps;
  static final String DATE_FORMAT_DE = "dd.MM.yyyy";

  @Inject
  public CaseDetailedTableViewSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      SoftAssert softly,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    Then(
        "^I am checking if all the fields are correctly displayed in the Case directory Detailed table$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> detailedCaseDTableRow = tableRowsData.get(0);
          softly.assertEquals(
              detailedCaseDTableRow.size(), 41, "Case table rows count is not correct");

          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.DISEASE.toString())
                  .contains(DiseasesValues.CORONAVIRUS.getDiseaseCaption()),
              "Disease is not correct");

          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.CASE_CLASSIFICATION.toString())
                  .contains(CaseOutcome.NOT_YET_CLASSIFIED.getName()),
              "Case Classification is not correct");

          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.OUTCOME_OF_CASE.toString())
                  .contains(CaseOutcome.NO_OUTCOME.getName()),
              "Outcome is not correct");
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.INVESTIGATION_STATUS.toString())
                  .contains(apiState.getCreatedCase().getInvestigationStatus().toLowerCase()),
              "Investigation status is not correct");
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.FIRST_NAME.toString())
                  .contains(apiState.getLastCreatedPerson().getFirstName()),
              "First Name is not correct");
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.LAST_NAME.toString())
                  .contains(apiState.getLastCreatedPerson().getLastName()),
              "Last name is not correct");
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.NUMBER_OF_EVENTS.toString())
                  .contains("0"),
              "Number of events is not correctly displayed");
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.RESPONSIBLE_REGION.toString())
                  .contains(RegionsValues.VoreingestellteBundeslander.getName()),
              "Responsible region is not correct");
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.RESPONSIBLE_DISTRICT.toString())
                  .contains(DistrictsValues.VoreingestellterLandkreis.getName()),
              "Responsible district is not correct");
          softly.assertEquals(
              detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.HEALTH_FACILITY.toString()),
              "Standard Einrichtung - Details",
              "Health facility is not correct");
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.DATE_OF_REPORT.toString())
                  .contains(
                      getDateOfReportDateTime(apiState.getCreatedCase().getReportDate().toString())
                          .replace("/0", "/")),
              "Date of report is not correct. Found: "
                  + detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.DATE_OF_REPORT.toString()
                          + " and expecting to contain: "
                          + getDateOfReportDateTime(
                                  apiState.getCreatedCase().getReportDate().toString())
                              .replace("/0", "/")));
          softly.assertTrue(
              detailedCaseDTableRow
                  .get(CaseDetailedTableViewHeaders.FOLLOW_UP_STATUS.toString())
                  .contains(ContactOutcome.FOLLOW_UP.getOutcome()),
              "Follow up status is not correct");
          softly.assertEquals(
              detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.FOLLOW_UP_UNTIL.toString()),
              getFollowUpUntilCaseDate(
                  getDateOfReportDateTime(apiState.getCreatedCase().getReportDate().toString())),
              "Follow up until message is not correct");
          softly.assertEquals(
              detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.NUMBER_OF_VISITS.toString()),
              "0 (0 missed)",
              "Number of visits is not correctly displayed");
          String completenessValue =
              detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.COMPLETENESS.toString());
          if (!completenessValue.equalsIgnoreCase("-")) {
            softly.assertEquals(
                detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.COMPLETENESS.toString()),
                "10 %",
                "Completeness is not correctly displayed");
          }
          softly.assertEquals(
              detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.REPORTING_USER.toString()),
              environmentManager.getUserByRole(locale, UserRoles.RestUser.getRole()).getUserRole(),
              "Reporting user is not correct");
          softly.assertAll();
        });

    When(
        "I back to Case Directory using case list button",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(BACK_TO_CASES_LIST_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(BACK_TO_CASES_LIST_BUTTON);
        });

    When(
        "I check if Case date format displayed in Cases tab is correct for specified fields",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> detailedCaseDTableRow = tableRowsData.get(0);
          softly.assertTrue(
              checkDateFormatDE(detailedCaseDTableRow, "NACHVERFOLGUNG BIS"),
              "Date format is invalid in NACHVERFOLGUNG BIS field");
          softly.assertTrue(
              checkDateFormatDE(detailedCaseDTableRow, "MELDEDATUM"),
              "Date format is invalid in MELDEDATUM field");
          softly.assertAll();
        });

    When(
        "I check that Person ID column is between Investigation Status and First Name columns",
        () -> {
          Map<String, Integer> headers = extractColumnHeadersHashMap();
          Integer investigationStatusKey = headers.get("INVESTIGATION STATUS");
          Integer personIDKey = headers.get("PERSON ID");
          Integer firstNameKey = headers.get("FIRST NAME");
          softly.assertTrue(
              investigationStatusKey == personIDKey - 1 && firstNameKey == personIDKey + 1);
          softly.assertAll();
        });

    When(
        "I click on the first Person ID from Case Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_PERSON_ID);
        });

    When(
        "I check that I get navigated to the Edit Person page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(PERSON_INFORMATION_TITLE);
        });

    When(
        "I check that I get navigated to the Edit Case page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_DATA_TITLE);
        });

    When(
        "I double-click on any field in the first row from Case Directory that is not Person ID",
        () -> {
          webDriverHelpers.doubleClickOnWebElementBySelector(FIRST_ROW);
        });
  }

  private boolean checkDateFormatDE(Map<String, String> map, String row) {
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DE);
    dateFormat.setLenient(false);
    try {
      Assert.assertFalse(map.isEmpty(), String.format("The element: %s was empty or null", map));
      dateFormat.parse(map.get(row));
      return true;
    } catch (ParseException e) {
      e.printStackTrace();
      log.error(PROCESS_ID_STRING + e.getMessage());
      return false;
    }
  }

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(CASE_DETAILED_TABLE_DATA);
          tableData.forEach(
              dataText -> {
                webDriverHelpers.scrollToElementUntilIsVisible(dataText);
                indexWithData.put(atomicInt.getAndIncrement(), dataText.getText());
              });
          tableDataList.add(indexWithData);
        });
    List<Map<String, String>> tableObjects = new ArrayList<>();
    tableDataList.forEach(
        row -> {
          ConcurrentHashMap<String, String> objects = new ConcurrentHashMap<>();
          headers.forEach((headerText, index) -> objects.put(headerText, row.get(index)));
          tableObjects.add(objects);
        });
    return tableObjects;
  }

  private List<WebElement> getTableRows() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CASE_DETAILED_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(CASE_DETAILED_TABLE_ROWS);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CASE_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(CASE_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(CASE_DETAILED_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(CASE_DETAILED_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }

  /* Used in test: Check Case details in Detailed table view from Case directory
  Returns Case report date as displayed in the UI
  <parameter> dateTimeString: represents the date time value read from Case created through API <parameter>
  */

  private String getDateOfReportDateTime(String dateTimeString) {
    SimpleDateFormat outputFormat = new SimpleDateFormat("M/dd/yyyy h:mm a");
    // because API request is sending local GMT and UI displays GMT+2 (server GMT)
    outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

    // inputFormat is the format of teh dateTime as read from API created case
    DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    Date parsedDate = null;
    try {
      parsedDate = inputFormat.parse(dateTimeString);
    } catch (ParseException e) {
      e.printStackTrace();
      log.error(PROCESS_ID_STRING + e.getMessage());
    }
    return outputFormat.format(parsedDate);
  }

  private String getFollowUpUntilCaseDate(String dateOfReportDateDateTime) {
    SimpleDateFormat outputFormat = new SimpleDateFormat("M/dd/yyyy");
    DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
    ZoneId defaultZoneId = ZoneId.systemDefault();
    Date parsedDate = null;
    try {
      parsedDate = inputFormat.parse(dateOfReportDateDateTime);
    } catch (ParseException e) {
      e.printStackTrace();
      log.error(PROCESS_ID_STRING + e.getMessage());
    }
    LocalDate date = parsedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate addedDate = date.plusDays(14);
    Date finalDate = Date.from(addedDate.atStartOfDay(defaultZoneId).toInstant());
    String formattedDate = outputFormat.format(finalDate);
    return (formattedDate.contains("/0")) ? formattedDate.replace("/0", "/") : formattedDate;
  }
}
