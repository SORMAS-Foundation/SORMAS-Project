package org.sormas.e2etests.steps.api.demisSteps;

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.PERSON_LIKE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.CREATE_NEW_CASE_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PICK_OR_CREATE_PERSON_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.APPLY_FILTER_MESSAGE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CHOOSE_OR_CREATE_ENTRY_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_DETAILED_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_DETAILED_TABLE_ROWS;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_TABLE_DATA;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_DATE_FROM_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_EYE_ICON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_TIME_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_UUID_TEXT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PATIENT_BIRTHDAY_FROM_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PATIENT_BIRTHDAY_TO_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.RELATED_FORWARDED_MESSAGE_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SAVE_POPUP_CONTENT_SECOND_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SEARCH_MESSAGE_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UPDATE_THE_DISEASE_VARIANT_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.VERARBEITEN_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.checkMappedValueSelector;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TYPE_OF_TEST_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.PCR_TEST_SPECIFICATION_INPUT;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
  public static List<String> firstNames = new ArrayList<>();
  public static List<String> lastNames = new ArrayList<>();
  public static Map<String, String> collectedMessagesTable;
  public static String reportId;

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

    Given(
        "I create and send Laboratory Notification with Loinc code ([^\"]*)$",
        (String code) -> {
          Random random = new Random();
          int sec = random.nextInt(60 - 1) + 1; // because of tests works in parallel
          TimeUnit.SECONDS.sleep(sec);
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileWithLoinc(
                  patientFirstName, patientLastName, code);
          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    Given(
        "I filter by last created person via API in Messages Directory",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              SEARCH_MESSAGE_INPUT, patientFirstName + " " + patientLastName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    Given(
        "I check if Demis Value is mapped to ([^\"]*)$",
        (String mappedValue) ->
            Assert.assertTrue(
                webDriverHelpers.isElementPresent(checkMappedValueSelector(mappedValue)),
                "Element is wrongly mapped or it does not exists"));

    Given(
        "I click on Verarbeiten button in Messages Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(VERARBEITEN_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(RELATED_FORWARDED_MESSAGE_HEADER, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(POPUP_CONFIRM_BUTTON);
          }
        });

    Given(
        "I create a new person and a new case from received message",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(CHOOSE_OR_CREATE_ENTRY_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          webDriverHelpers.fillInWebElement(
              DATE_OF_REPORT_INPUT, formatter.format(LocalDate.now()));
          webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, "Bremen");
          webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, "SK Bremen");
          webDriverHelpers.clickWebElementByText(PLACE_OF_STAY_OPTIONS, "ZUHAUSE");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          TimeUnit.SECONDS.sleep(2);

          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT_SECOND_BUTTON);

          if (webDriverHelpers.isElementVisibleWithTimeout(UPDATE_THE_DISEASE_VARIANT_HEADER, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(POPUP_CONFIRM_BUTTON);
          }
        });

    Given(
        "I search the case by last created person via Demis message",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_LIKE_SEARCH_INPUT, patientFirstName + " " + patientLastName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    Given(
        "I check if Demis Value is mapped to {string} and {string}",
        (String pathogenTest, String pcrTestValue) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(TYPE_OF_TEST_INPUT),
              pathogenTest,
              "Sormas pathogen tests are not equal");
          if (!pcrTestValue.isEmpty()) {
            softly.assertEquals(
                webDriverHelpers.getValueFromWebElement(PCR_TEST_SPECIFICATION_INPUT),
                pcrTestValue,
                "PCR Test values are not equal");
          }
          softly.assertAll();
        });

    And(
        "^I collect first and last name of the person from Laboratory Notification$",
        () -> {
          firstNames.add(patientFirstName);
          lastNames.add(patientLastName);
        });

    Given(
        "I collect message data from searched record in Messages directory",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          collectedMessagesTable = tableRowsData.get(0);
        });

    When(
        "I click on the RESET FILTERS button for Messages",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              RESET_FILTERS_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTERS_BUTTON);
        });

    When(
        "I search created message by ([^\"]*)$",
        (String option) -> {
          switch (option) {
            case "UUID":
              webDriverHelpers.fillAndSubmitInWebElement(
                  SEARCH_MESSAGE_INPUT,
                  collectedMessagesTable.get(MessagesTableViewHeaders.UUID.toString()));
              TimeUnit.SECONDS.sleep(2); // wait for reaction
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "laboratory name":
              webDriverHelpers.fillAndSubmitInWebElement(
                  SEARCH_MESSAGE_INPUT,
                  collectedMessagesTable.get(MessagesTableViewHeaders.MELDER_NAME.toString()));
              TimeUnit.SECONDS.sleep(2); // wait for reaction
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "laboratory postal code":
              webDriverHelpers.fillAndSubmitInWebElement(
                  SEARCH_MESSAGE_INPUT,
                  collectedMessagesTable.get(
                      MessagesTableViewHeaders.MELDER_POSTLEITZAHL.toString()));
              TimeUnit.SECONDS.sleep(2); // wait for reaction
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "date and time":
              LocalDateTime time = LocalDateTime.now().minusMinutes(1);
              LocalDateTime lastQuarter =
                  time.truncatedTo(ChronoUnit.HOURS).plusMinutes(15 * (time.getMinute() / 15));
              String dateTime =
                  collectedMessagesTable.get(MessagesTableViewHeaders.DATUM_DER_MELDUNG.toString());
              String[] date = dateTime.split("\\s+");
              webDriverHelpers.fillInWebElement(MESSAGE_DATE_FROM_INPUT, date[0]);
              webDriverHelpers.selectFromCombobox(
                  MESSAGE_TIME_FROM_COMBOBOX,
                  lastQuarter.format(DateTimeFormatter.ofPattern("HH:mm")));
              webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_MESSAGE);
              TimeUnit.SECONDS.sleep(2); // wait for reaction
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "birthday date":
              webDriverHelpers.fillInWebElement(
                  PATIENT_BIRTHDAY_FROM_INPUT,
                  collectedMessagesTable.get(MessagesTableViewHeaders.GEBURTSDATUM.toString()));
              webDriverHelpers.fillInWebElement(
                  PATIENT_BIRTHDAY_TO_INPUT,
                  collectedMessagesTable.get(MessagesTableViewHeaders.GEBURTSDATUM.toString()));
              webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER_MESSAGE);
              TimeUnit.SECONDS.sleep(2); // wait for reaction
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
          }
        });

    When(
        "I check if searched message has correct ([^\"]*)$",
        (String rowName) -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> messagesTable = tableRowsData.get(0);
          switch (rowName) {
            case "UUID":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.UUID.toString()),
                  collectedMessagesTable.get(MessagesTableViewHeaders.UUID.toString()),
                  "UUIDs are not equal");
              softly.assertAll();
              break;
            case "laboratory name":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.MELDER_NAME.toString()),
                  collectedMessagesTable.get(MessagesTableViewHeaders.MELDER_NAME.toString()),
                  "Laboratory names are not equal");
              softly.assertAll();
              break;
            case "laboratory postal code":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.MELDER_POSTLEITZAHL.toString()),
                  collectedMessagesTable.get(
                      MessagesTableViewHeaders.MELDER_POSTLEITZAHL.toString()),
                  "Laboratory postal codes are not equal");
              softly.assertAll();
              break;
            case "date and time":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.DATUM_DER_MELDUNG.toString()),
                  collectedMessagesTable.get(MessagesTableViewHeaders.DATUM_DER_MELDUNG.toString()),
                  "Date and time values are not equal");
              softly.assertAll();
            case "birthday date":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.GEBURTSDATUM.toString()),
                  collectedMessagesTable.get(MessagesTableViewHeaders.GEBURTSDATUM.toString()),
                  "Birthday dates are not equal");
              softly.assertAll();
          }
        });

    Given(
        "I create and send Laboratory Notification with different report ID",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          reportId = faker.code().imei();
          String json = demisApiService.prepareLabNotificationFileWithDifferentReportId(patientFirstName, patientLastName, reportId);
          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
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
