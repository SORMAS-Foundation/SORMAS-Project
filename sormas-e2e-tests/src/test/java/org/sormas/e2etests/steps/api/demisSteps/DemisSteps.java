package org.sormas.e2etests.steps.api.demisSteps;

import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_DETAILED_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_DETAILED_TABLE_ROWS;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_TABLE_DATA;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_EYE_ICON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_UUID_TEXT;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.services.api.demis.DemisApiService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.sormas.e2etests.steps.web.application.messages.MessagesTableViewHeaders;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class DemisSteps implements En {

  public static String loginToken;
  public static String patientFirstName;
  public static String patientLastName;
  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;

  @Inject
  public DemisSteps(
      DemisApiService demisApiService,
      Faker faker,
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    Given(
        "API : Login to DEMIS server",
        () -> {
          loginToken = demisApiService.loginRequest();
          Assert.assertFalse(loginToken.isEmpty(), "DEMIS token wasn't received");
        });

    Given(
        "Send lab message with {string}",
        (String filename) -> {
          Assert.assertTrue(
              demisApiService.sendLabRequest(filename, loginToken),
              "Failed to send laboratory request");
        });

    Given(
        "I create and send Laboratory Notification",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFile(patientFirstName, patientLastName);
          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    Given(
        "I check if first and last name of patient request sent via Demis are correct",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> messagesTable = tableRowsData.get(0);
          softly.assertEquals(
              messagesTable.get(MessagesTableViewHeaders.VORNAME.toString()),
              patientFirstName,
              "First name is not correct");
          softly.assertEquals(
              messagesTable.get(MessagesTableViewHeaders.NACHNAME.toString()),
              patientLastName,
              "Last name is not correct");
          softly.assertAll();
        });

    Given(
        "I click on the eye icon next for the first fetched message",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(MESSAGE_EYE_ICON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MESSAGE_POPUP_HEADER);
        });

    Given(
        "I check if fetched message has UUID field",
        () -> {
          ;
          softly.assertFalse(
              webDriverHelpers.getValueFromWebElement(MESSAGE_UUID_TEXT).isEmpty(),
              "UUID is empty!");
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
          List<WebElement> tableData = table.findElements(MESSAGES_TABLE_DATA);
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
        MESSAGES_DETAILED_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(MESSAGES_DETAILED_TABLE_ROWS);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
        MESSAGES_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(MESSAGES_DETAILED_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(MESSAGES_DETAILED_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              if (webElement != null) {
                headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
              }
            });
    return headerHashmap;
  }
}
