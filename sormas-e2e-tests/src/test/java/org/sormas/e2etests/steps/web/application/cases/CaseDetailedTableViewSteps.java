package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
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
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.CaseOutcome;
import org.sormas.e2etests.enums.ContactOutcome;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;

@Slf4j
public class CaseDetailedTableViewSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private static BaseSteps baseSteps;

  @Inject
  public CaseDetailedTableViewSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      DataOperations dataOperations,
      SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    Then(
        "^I am checking if all the fields are correctly displayed in the Case directory Detailed table$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> detailedCaseDTableRow = tableRowsData.get(0);
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.CASE_ID.toString()))
              .containsIgnoringCase(
                  dataOperations.getPartialUuidFromAssociatedLink(
                      apiState.getCreatedCase().getUuid()));
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.DISEASE.toString()))
              .containsIgnoringCase(CaseOutcome.CORONAVIRUS.getOutcome());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.CASE_CLASSIFICATION.toString()))
              .containsIgnoringCase(CaseOutcome.NOT_CLASSIFIED.getOutcome());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.OUTCOME_OF_CASE.toString()))
              .containsIgnoringCase(CaseOutcome.NO_OUTCOME.getOutcome());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.INVESTIGATION_STATUS.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getInvestigationStatus());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.FIRST_NAME.toString()))
              .containsIgnoringCase(apiState.getLastCreatedPerson().getFirstName());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.LAST_NAME.toString()))
              .containsIgnoringCase(apiState.getLastCreatedPerson().getLastName());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.NUMBER_OF_EVENTS.toString()))
              .containsIgnoringCase("0");
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.RESPONSIBLE_REGION.toString()))
              .containsIgnoringCase(CaseOutcome.RESPONSIBLE_REGION.getOutcome());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.RESPONSIBLE_DISTRICT.toString()))
              .containsIgnoringCase(CaseOutcome.RESPONSIBLE_DISTRICT.getOutcome());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.HEALTH_FACILITY.toString()))
              .containsIgnoringCase(CaseOutcome.HEALTH_FACILITY.getOutcome());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.DATE_OF_REPORT.toString()))
              .containsIgnoringCase(
                  getDetaOfReportDateTime(apiState.getCreatedCase().getReportDate().toString()));
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.FOLLOW_UP_STATUS.toString()))
              .containsIgnoringCase(ContactOutcome.FOLLOW_UP.getOutcome());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.FOLLOW_UP_UNTIL.toString()))
              .containsIgnoringCase(
                  getFollowUpUntilCaseDate(
                      getDetaOfReportDateTime(
                          apiState.getCreatedCase().getReportDate().toString())));
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.NUMBER_OF_VISITS.toString()))
              .containsIgnoringCase("0 (0 missed)");
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.COMPLETENESS.toString()))
              .containsIgnoringCase("-");
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.REPORTING_USER.toString()))
              .containsIgnoringCase("Rest AUTOMATION");
          softly.assertAll();
        });
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

  public String getDetaOfReportDateTime(String dateTimeString) {
    SimpleDateFormat outputFormat = new SimpleDateFormat("M/dd/yyyy h:mm a");
    // because API request is sending local GMT and UI displays GMT+2 (server GMT)
    outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));

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

  public String getFollowUpUntilCaseDate(String dateOfReportDateDateTime) {
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

    return outputFormat.format(finalDate);
  }
}
