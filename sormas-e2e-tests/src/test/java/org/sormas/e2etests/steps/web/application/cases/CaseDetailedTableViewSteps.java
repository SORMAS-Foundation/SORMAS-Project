package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.*;

import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.ContactOutcome;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;

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
          //          softly
          //              .assertThat(
          //
          // detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.DISEASE.toString()))
          //              .containsIgnoringCase();
          //          softly
          //              .assertThat(
          //                  detailedCaseDTableRow.get(
          //                          CaseDetailedTableViewHeaders.CASE_CLASSIFICATION.toString()))
          //              .containsIgnoringCase();
          //          softly
          //              .assertThat(
          //                  detailedCaseDTableRow.get(
          //                          CaseDetailedTableViewHeaders.OUTCOME_OF_CASE.toString()))
          //              .containsIgnoringCase();
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.INVESTIGATION_STATUS.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getInvestigationStatus());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.FIRST_NAME.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getPerson().getFirstName());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.LAST_NAME.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getPerson().getLastName());
          //          softly
          //              .assertThat(
          //                  detailedCaseDTableRow.get(
          //                          CaseDetailedTableViewHeaders.SEX.toString()))
          //              .containsIgnoringCase();
          //          softly
          //              .assertThat(
          //                  detailedCaseDTableRow.get(
          //                          CaseDetailedTableViewHeaders.AGE_AND_BIRTH_DATE.toString()))
          //              .containsIgnoringCase();
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.NUMBER_OF_EVENTS.toString()))
              .containsIgnoringCase("0");
          //          softly
          //              .assertThat(
          //                  detailedCaseDTableRow.get(
          //
          // CaseDetailedTableViewHeaders.DATE_OF_SYMPTOM_ONSET.toString()))
          //              .containsIgnoringCase();
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.RESPONSIBLE_REGION.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getRegion().getCaption());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.RESPONSIBLE_DISTRICT.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getDistrict().getCaption());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.RESPONSIBLE_COMMUNITY.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getCommunity().getUuid());
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.HEALTH_FACILITY.toString()))
              .containsIgnoringCase(apiState.getCreatedCase().getHealthFacility().getUuid());
          //            softly
          //                    .assertThat(
          //                            detailedCaseDTableRow.get(
          //
          // CaseDetailedTableViewHeaders.DATE_OF_REPORT.toString()))
          //                    .containsIgnoringCase();
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.FOLLOW_UP_STATUS.toString()))
              .containsIgnoringCase(ContactOutcome.FOLLOW_UP.getOutcome());
          //            softly
          //                    .assertThat(
          //                            detailedCaseDTableRow.get(
          //
          // CaseDetailedTableViewHeaders.FOLLOW_UP_UNTIL.toString()))
          //                    .containsIgnoringCase();
          softly
              .assertThat(
                  detailedCaseDTableRow.get(
                      CaseDetailedTableViewHeaders.NUMBER_OF_VISITS.toString()))
              .containsIgnoringCase("0(1 missed)");
          softly
              .assertThat(
                  detailedCaseDTableRow.get(CaseDetailedTableViewHeaders.COMPLETENESS.toString()))
              .containsIgnoringCase("15%");
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
    return baseSteps.getDriver().findElements(CASE_DETAILED_FIRST_TABLE_ROW);
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
}
