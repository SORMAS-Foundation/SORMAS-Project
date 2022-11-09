package org.sormas.e2etests.steps.web.application.contacts;

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
import org.sormas.e2etests.enums.TestDataUser;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;

public class ContactsDetailedTableViewSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private static BaseSteps baseSteps;

  @Inject
  public ContactsDetailedTableViewSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      DataOperations dataOperations,
      SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    Then(
        "^I am checking if all the fields are correctly displayed in the Contacts directory Detailed table$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> detailedContactDTableRow = tableRowsData.get(0);
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.CONTACT_ID.toString()))
              .containsIgnoringCase(
                  dataOperations.getPartialUuidFromAssociatedLink(
                      apiState.getCreatedContact().getUuid()));
          softly
              .assertThat(
                  detailedContactDTableRow.get(ContactsDetailedTableViewHeaders.DISEASE.toString()))
              .containsIgnoringCase(ContactOutcome.CORONAVIRUS.getOutcome());
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.CONTACT_CLASSIFICATION.toString()))
              .containsIgnoringCase(ContactOutcome.UNCONFIRMED.getOutcome());
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.CONTACT_STATUS.toString()))
              .containsIgnoringCase("Active Contact");
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.FIRST_NAME_OF_CONTACT_PERSON.toString()))
              .containsIgnoringCase(apiState.getLastCreatedPerson().getFirstName());
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.LAST_NAME_OF_CONTACT_PERSON.toString()))
              .containsIgnoringCase(apiState.getLastCreatedPerson().getLastName());
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.DISTRICT.toString()))
              .containsIgnoringCase(apiState.getCreatedContact().getDistrict().getCaption());
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.RELATIONSHIP_WITH_CASE.toString()))
              .containsIgnoringCase(ContactOutcome.SAME_HOUSEHOLD.getOutcome());
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.FOLLOW_UP_STATUS.toString()))
              .containsIgnoringCase(ContactOutcome.FOLLOW_UP.getOutcome());
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.NUMBER_OF_VISITS.toString()))
              .containsIgnoringCase("0 (0 missed)");
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.PENDING_TASKS.toString()))
              .containsIgnoringCase("1");
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.COMPLETENESS.toString()))
              .containsIgnoringCase("25 %");
          softly
              .assertThat(
                  detailedContactDTableRow.get(
                      ContactsDetailedTableViewHeaders.REPORTING_USER.toString()))
              .containsIgnoringCase(TestDataUser.REST_AUTOMATION.getUserRole());
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
          List<WebElement> tableData = table.findElements(CONTACTS_DETAILED_TABLE_DATA);
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
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
        CONTACTS_DETAILED_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(CONTACTS_DETAILED_FIRST_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
        CONTACTS_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(CONTACTS_DETAILED_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(CONTACTS_DETAILED_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
