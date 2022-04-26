package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.CONTACT_DATA_TITLE;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.ContactOutcome;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class ContactsDetailedTableViewSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private static BaseSteps baseSteps;

  @Inject
  public ContactsDetailedTableViewSteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      ApiState apiState,
      DataOperations dataOperations,
      SoftAssert softly,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    Then(
        "^I am checking if all the fields are correctly displayed in the Contacts directory Detailed table$",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> detailedContactDTableRow = tableRowsData.get(0);
          softly.assertEquals(
              detailedContactDTableRow.get(ContactsDetailedTableViewHeaders.CONTACT_ID.toString()),
              dataOperations
                  .getPartialUuidFromAssociatedLink(apiState.getCreatedContact().getUuid())
                  .toUpperCase(),
              "UUID from associated link is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(ContactsDetailedTableViewHeaders.DISEASE.toString()),
              ContactOutcome.CORONAVIRUS.getOutcome(),
              "Disease value is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.CONTACT_CLASSIFICATION.toString()),
              ContactOutcome.UNCONFIRMED.getOutcome(),
              "Contact classification is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.CONTACT_STATUS.toString()),
              "Active contact",
              "Contact status is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.FIRST_NAME_OF_CONTACT_PERSON.toString()),
              apiState.getLastCreatedPerson().getFirstName(),
              "First name of contact person is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.LAST_NAME_OF_CONTACT_PERSON.toString()),
              apiState.getLastCreatedPerson().getLastName(),
              "Last name of contact person is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(ContactsDetailedTableViewHeaders.DISTRICT.toString()),
              apiState.getCreatedContact().getDistrict().getCaption(),
              "District value is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.RELATIONSHIP_WITH_CASE.toString()),
              ContactOutcome.SAME_HOUSEHOLD.getOutcome(),
              "Relationship with case is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.FOLLOW_UP_STATUS.toString()),
              ContactOutcome.FOLLOW_UP.getOutcome(),
              "Follow up status is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.NUMBER_OF_VISITS.toString()),
              "0 (0 missed)",
              "Number of visits is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.PENDING_TASKS.toString()),
              "1");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.COMPLETENESS.toString()),
              "25 %",
              "Completeness level is not correct");
          softly.assertEquals(
              detailedContactDTableRow.get(
                  ContactsDetailedTableViewHeaders.REPORTING_USER.toString()),
              environmentManager.getUserByRole(locale, UserRoles.RestUser.getRole()).getUserRole(),
              "Reporting user is not correct");
          softly.assertAll();
        });

    When(
        "I check that Person ID column is between Contact Status and First Name of Contact Person columns",
        () -> {
          Map<String, Integer> headers = extractColumnHeadersHashMap();
          Integer investigationStatusKey = headers.get("CONTACT STATUS");
          Integer personIDKey = headers.get("PERSON ID");
          Integer firstNameKey = headers.get("FIRST NAME OF CONTACT PERSON");
          softly.assertTrue(
              investigationStatusKey == personIDKey - 1 && firstNameKey == personIDKey + 1);
          softly.assertAll();
        });

    When(
        "I click on the first Person ID from Contacts Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_PERSON_ID);
        });

    When(
        "I check that I get navigated to the Edit Contact page",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONTACT_DATA_TITLE);
        });

    When(
        "I click on the first Contact ID from Contacts Directory",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(
              By.xpath("//*[contains(text(),'Confirm navigation')]"), 5)) {
            webDriverHelpers.clickOnWebElementBySelector(By.id("actionCancel"));
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          }
          webDriverHelpers.clickOnWebElementBySelector(FIRST_CONTACT_ID);
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
