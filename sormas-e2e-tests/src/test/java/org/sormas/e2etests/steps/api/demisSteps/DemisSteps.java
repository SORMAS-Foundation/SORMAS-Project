package org.sormas.e2etests.steps.api.demisSteps;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CANCEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.getContactFirstAndLastName;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.PERSON_LIKE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.CREATE_NEW_CASE_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.CREATE_NEW_CONTACT_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PICK_OR_CREATE_PERSON_HEADER_DE;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.EVENT_DISTRICT;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.EVENT_REGION;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.NEW_EVENT_CREATED_DE_MESSAGE;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.NEW_EVENT_RADIOBUTTON_DE_MESSAGE;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.TITLE_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.CHOOSE_OR_CREATE_EVENT_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.ALL_QUICK_FILTER_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.APPLY_FILTER_MESSAGE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CASE_SAVED_POPUP_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CHOOSE_OR_CREATE_ENTRY_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_A_NEW_CASE_WITH_POSITIVE_TEST_CONTACT_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_A_NEW_CASE_WITH_POSITIVE_TEST_EVENT_PARTICIPANT_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_EVENT_PARTICIPANT_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_SAMPLE_CHECKBOX;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FIRST_PATHOGEN_LABORATORY_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FORWARDED_QUICK_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FORWARDED_QUICK_FILTER_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.LABORATORY_DETAILS_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.LABORATORY_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_DETAILED_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_DETAILED_TABLE_ROWS;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGES_TABLE_DATA;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_DATE_FROM_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_EYE_ICON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_TIME_FROM_COMBOBOX;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_UUID_TEXT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MULTIPLE_SAMPLES_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_CASE_FORM_DISEASE_VARIANT_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_FORM_FIRST_PATHOGEN_DISEASE_VARIANT_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_FORM_FIRST_PATHOGEN_LABORATORY_NAME;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_FORM_FIRST_PATHOGEN_TEST_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_FORM_LABORATORY_NAME;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_FORM_SECOND_PATHOGEN_DISEASE_VARIANT_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_FORM_SECOND_PATHOGEN_LABORATORY_NAME;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_FORM_SECOND_PATHOGEN_TEST_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEXT_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PATIENT_BIRTHDAY_FROM_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PATIENT_BIRTHDAY_TO_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_BACK_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_CANCEL_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_AND_OPEN_PHYSICIAN_REPORT_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PROCESSED_QUICK_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PROCESSED_QUICK_FILTER_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.RELATED_FORWARDED_MESSAGE_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SAVE_POPUP_CONTENT_SECOND_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SEARCH_MESSAGE_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UNCLEAR_QUICK_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UNCLEAR_QUICK_FILTER_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UNPROCESSED_QUICK_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UNPROCESSED_QUICK_FILTER_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UPDATE_THE_DISEASE_VARIANT_HEADER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.VERARBEITEN_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.checkMappedValueSelector;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAVE_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TYPE_OF_TEST_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.PCR_TEST_SPECIFICATION_INPUT;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.web.Event;
import org.sormas.e2etests.entities.services.EventService;
import org.sormas.e2etests.entities.services.api.demis.DemisApiService;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
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
  protected static Event newEvent;
  private final RunningConfiguration runningConfiguration;

  @Inject
  public DemisSteps(
      DemisApiService demisApiService,
      Faker faker,
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      SoftAssert softly,
      EventService eventService,
      RunningConfiguration runningConfiguration) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;
    this.runningConfiguration = runningConfiguration;

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
        "I filter by last created person via DEMIS API in Case Directory",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT,
              patientFirstName + " " + patientLastName);
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

    When(
        "I check if while creating new case from demis message there is a possibility to edit first and last name",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(CHOOSE_OR_CREATE_ENTRY_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          softly.assertTrue(webDriverHelpers.isElementEnabled(FIRST_NAME_INPUT));
          softly.assertTrue(webDriverHelpers.isElementEnabled(LAST_NAME_INPUT));
          softly.assertAll();
        });

    When(
        "I check if while creating new contact from demis message there is a possibility to edit first and last name",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(CHOOSE_OR_CREATE_ENTRY_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CONTACT_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          softly.assertTrue(webDriverHelpers.isElementEnabled(FIRST_NAME_INPUT));
          softly.assertTrue(webDriverHelpers.isElementEnabled(LAST_NAME_INPUT));
          softly.assertAll();
        });

    When(
        "I check if while creating new event participant from demis message there is a possibility to edit first and last name",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(CHOOSE_OR_CREATE_ENTRY_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(
                CREATE_NEW_EVENT_PARTICIPANT_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }

          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_RADIOBUTTON_DE_MESSAGE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          webDriverHelpers.fillInWebElement(TITLE_INPUT, faker.artist().name().toString());
          webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, "Berlin");
          webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, "SK Berlin Mitte");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          softly.assertTrue(webDriverHelpers.isElementEnabled(FIRST_NAME_INPUT));
          softly.assertTrue(webDriverHelpers.isElementEnabled(LAST_NAME_INPUT));
          softly.assertAll();
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
              break;
          }
        });

    When(
        "I check if {string} in received message is set to {string}",
        (String key, String value) -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> messagesTable = tableRowsData.get(0);
          switch (key) {
            case "laboratory name":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.MELDER_NAME.toString()),
                  value,
                  "Lab names are not equal");
              softly.assertAll();
              break;
            case "laboratory postal code":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.MELDER_POSTLEITZAHL.toString()),
                  value,
                  "Lab postal codes are not equal");
              softly.assertAll();
              break;
            case "postal code":
              softly.assertEquals(
                  messagesTable.get(MessagesTableViewHeaders.POSTLEITZAHL.toString()),
                  value,
                  "Postal codes are not equal");
              softly.assertAll();
              break;
          }
        });

    Given(
        "I create and send Laboratory Notification with patient's phone and email",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileWithTelcom(
                  patientFirstName, patientLastName);
          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    And(
        "^I filter by the name of the (\\d+) most recently created person in Messages Directory$",
        (Integer personNumber) -> {
          String personsFirstName = firstNames.get(personNumber - 1);
          String personsLastName = lastNames.get(personNumber - 1);
          webDriverHelpers.fillAndSubmitInWebElement(
              SEARCH_MESSAGE_INPUT, personsFirstName + " " + personsLastName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I click on \"([^\"]*)\" quick filter above the messages in Message directory page$",
        (String quickFilterOption) -> {
          switch (quickFilterOption) {
            case "Unverarbeitet":
              webDriverHelpers.clickOnWebElementBySelector(UNPROCESSED_QUICK_FILTER_BUTTON);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "Verarbeitet":
              webDriverHelpers.clickOnWebElementBySelector(PROCESSED_QUICK_FILTER_BUTTON);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "Unklar":
              webDriverHelpers.clickOnWebElementBySelector(UNCLEAR_QUICK_FILTER_BUTTON);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "Weitergeleitet":
              webDriverHelpers.clickOnWebElementBySelector(FORWARDED_QUICK_FILTER_BUTTON);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "Alle":
              webDriverHelpers.clickOnWebElementBySelector(FORWARDED_QUICK_FILTER_BUTTON);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
          }
        });

    Then(
        "^I check that \"([^\"]*)\" quick filter button is selected in Message directory page$",
        (String quickFilterOption) -> {
          switch (quickFilterOption) {
            case "Alle":
              webDriverHelpers.isElementVisibleWithTimeout(ALL_QUICK_FILTER_COUNTER, 4);
              break;
            case "Unverarbeitet":
              webDriverHelpers.isElementVisibleWithTimeout(UNPROCESSED_QUICK_FILTER_COUNTER, 4);
              break;
            case "Verarbeitet":
              webDriverHelpers.isElementVisibleWithTimeout(PROCESSED_QUICK_FILTER_COUNTER, 4);
              break;
            case "Unklar":
              webDriverHelpers.isElementVisibleWithTimeout(UNCLEAR_QUICK_FILTER_COUNTER, 4);
              break;
            case "Weitergeleitet":
              webDriverHelpers.isElementVisibleWithTimeout(FORWARDED_QUICK_FILTER_COUNTER, 4);
              break;
          }
        });

    And(
        "^I check that the Status column is filtered by \"([^\"]*)\" on Message directory page$",
        (String quickFilterOption) -> {
          switch (quickFilterOption) {
            case "Alle":
              if (!webDriverHelpers.getTextFromWebElement(ALL_QUICK_FILTER_COUNTER).equals("0")) {
                List<String> statusColumnData = getTableColumnDataByIndex(13, 10);
                for (int i = 1; i < statusColumnData.size(); i++) {
                  softly.assertEquals(
                      statusColumnData.get(i),
                      quickFilterOption,
                      "At least one record in the column is invalid!");
                  softly.assertAll();
                }
              }
              break;
            case "Unverarbeitet":
              if (!webDriverHelpers
                  .getTextFromWebElement(UNPROCESSED_QUICK_FILTER_COUNTER)
                  .equals("0")) {
                List<String> statusColumnData = getTableColumnDataByIndex(13, 10);
                for (int i = 1; i < statusColumnData.size(); i++) {
                  softly.assertEquals(
                      statusColumnData.get(i),
                      quickFilterOption,
                      "At least one record in the column is invalid!");
                  softly.assertAll();
                }
              }
              break;
            case "Verarbeitet":
              if (!webDriverHelpers
                  .getTextFromWebElement(PROCESSED_QUICK_FILTER_COUNTER)
                  .equals("0")) {
                List<String> statusColumnData = getTableColumnDataByIndex(13, 10);
                for (int i = 1; i < statusColumnData.size(); i++) {
                  softly.assertEquals(
                      statusColumnData.get(i),
                      quickFilterOption,
                      "At least one record in the column is invalid!");
                  softly.assertAll();
                }
              }
              break;
            case "Unklar":
              if (!webDriverHelpers
                  .getTextFromWebElement(UNCLEAR_QUICK_FILTER_COUNTER)
                  .equals("0")) {
                List<String> statusColumnData = getTableColumnDataByIndex(13, 10);
                for (int i = 1; i < statusColumnData.size(); i++) {
                  softly.assertEquals(
                      statusColumnData.get(i),
                      quickFilterOption,
                      "At least one record in the column is invalid!");
                  softly.assertAll();
                }
              }
              break;
            case "Weitergeleitet":
              if (!webDriverHelpers
                  .getTextFromWebElement(FORWARDED_QUICK_FILTER_COUNTER)
                  .equals("0")) {
                List<String> statusColumnData = getTableColumnDataByIndex(13, 10);
                for (int i = 1; i < statusColumnData.size(); i++) {
                  softly.assertEquals(
                      statusColumnData.get(i),
                      quickFilterOption,
                      "At least one record in the column is invalid!");
                  softly.assertAll();
                }
              }
              break;
          }
        });

    Given(
        "I create a new person from received message",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1);
          webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
        });

    Given(
        "I create a new contact form received message",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(CHOOSE_OR_CREATE_ENTRY_HEADER, 1);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_RADIOBUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          TimeUnit.SECONDS.sleep(1);

          webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX, "Bremen");
          webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX, "SK Bremen");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          TimeUnit.SECONDS.sleep(2);

          webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON);

          if (webDriverHelpers.isElementVisibleWithTimeout(UPDATE_THE_DISEASE_VARIANT_HEADER, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(POPUP_CONFIRM_BUTTON);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(
              CREATE_A_NEW_CASE_WITH_POSITIVE_TEST_CONTACT_HEADER_DE, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL);
          }
        });

    Given(
        "I check if contact tab was opened after create new contact from message",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  getContactFirstAndLastName(patientFirstName + " " + patientLastName)));
          softly.assertAll();
        });

    Given(
        "I check if event participant tab was opened after create new contact from message",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  getContactFirstAndLastName(patientFirstName + " " + patientLastName)));
          softly.assertAll();
        });

    Given(
        "I create a new event participant form received message",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(CHOOSE_OR_CREATE_ENTRY_HEADER, 1);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_EVENT_PARTICIPANT_RADIOBUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          TimeUnit.SECONDS.sleep(1);

          if (webDriverHelpers.isElementVisibleWithTimeout(CHOOSE_OR_CREATE_EVENT_HEADER_DE, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_CREATED_DE_MESSAGE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }

          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_RADIOBUTTON_DE_MESSAGE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          newEvent = eventService.buildGeneratedEventWithCorrectRegionAndDisctrictDE();
          fillTitle(newEvent.getTitle());
          selectResponsibleRegion(newEvent.getRegion());
          selectResponsibleDistrict(newEvent.getDistrict());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          TimeUnit.SECONDS.sleep(3);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);

          webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON);

          if (webDriverHelpers.isElementVisibleWithTimeout(
              CREATE_A_NEW_CASE_WITH_POSITIVE_TEST_EVENT_PARTICIPANT_HEADER_DE, 2)) {
            webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL);
          }
        });

    When(
        "I create and send Laboratory Notification with other facility name {string} and facility ID {string}",
        (String otherFacilityName, String otherFacilityId) -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileWithOtherFacility(
                  patientFirstName, patientLastName, otherFacilityId, otherFacilityName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    Then(
        "^I verify that labor is prefilled with \"([^\"]*)\" in New sample form while processing a DEMIS LabMessage$",
        (String laboratoryName) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(LABORATORY_INPUT),
              laboratoryName,
              "Laboratory field has incorrect value prefilled");
          softly.assertAll();
        });

    Then(
        "^I verify that labor description is prefilled with \"([^\"]*)\" in New sample form while processing a DEMIS LabMessage$",
        (String laboratoryDetails) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(LABORATORY_DETAILS_INPUT),
              laboratoryDetails,
              "Laboratory details field has incorrect value prefilled");
          softly.assertAll();
        });

    When(
        "^I create and send Laboratory Notification with two different facilities$",
        () -> {
          patientLastName = faker.name().lastName();
          patientFirstName = faker.name().firstName();
          String json =
              demisApiService.prepareLabNotificationFileWithTwoFacilities(
                  patientFirstName, patientLastName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    And(
        "^I select \"([^\"]*)\" as a Laboratory in New sample form while processing a DEMIS LabMessage$",
        (String labor) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LABORATORY_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(LABORATORY_INPUT, labor);
        });

    And(
        "^I select \"([^\"]*)\" as a Laboratory for pathogen in New sample form while processing a DEMIS LabMessage$",
        (String labor) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(FIRST_PATHOGEN_LABORATORY_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(FIRST_PATHOGEN_LABORATORY_INPUT, labor);
        });

    When(
        "^I create and send Laboratory Notification with two positive pathogens$",
        () -> {
          patientLastName = faker.name().lastName();
          patientFirstName = faker.name().firstName();
          String json =
              demisApiService.prepareLabNotificationFileWithTwoPathogens(
                  patientFirstName, patientLastName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    And(
        "I check if disease variant field is {string} in New case form while processing a DEMIS LabMessage",
        (String diseaseVariant) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NEW_CASE_FORM_DISEASE_VARIANT_INPUT);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(NEW_CASE_FORM_DISEASE_VARIANT_INPUT),
              diseaseVariant,
              "Disease variant is not empty");
          softly.assertAll();
        });

    And(
        "I verify that disease variant for {string} pathogen is prefilled with {string} in New Sample form while processing a DEMIS LabMessage",
        (String pathogen, String diseaseVariant) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              NEW_SAMPLE_FORM_FIRST_PATHOGEN_DISEASE_VARIANT_INPUT);
          switch (pathogen) {
            case "first":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(
                      NEW_SAMPLE_FORM_FIRST_PATHOGEN_DISEASE_VARIANT_INPUT),
                  diseaseVariant,
                  "The disease variant is incorrect");
              softly.assertAll();
              break;
            case "second":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(
                      NEW_SAMPLE_FORM_SECOND_PATHOGEN_DISEASE_VARIANT_INPUT),
                  diseaseVariant,
                  "The disease variant is incorrect");
              softly.assertAll();
              break;
          }
        });

    And(
        "^I fill laboratory name with \"([^\"]*)\" in New Sample form while processing a DEMIS LabMessage$",
        (String labName) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NEW_SAMPLE_FORM_LABORATORY_NAME);
          webDriverHelpers.clearAndFillInWebElement(NEW_SAMPLE_FORM_LABORATORY_NAME, labName);
        });

    And(
        "^I fill \"([^\"]*)\" pathogen laboratory name with \"([^\"]*)\" in New Sample form while processing a DEMIS LabMessage$",
        (String pathogen, String labName) -> {
          switch (pathogen) {
            case "first":
              webDriverHelpers.clearAndFillInWebElement(
                  NEW_SAMPLE_FORM_FIRST_PATHOGEN_LABORATORY_NAME, labName);
              break;
            case "second":
              webDriverHelpers.clearAndFillInWebElement(
                  NEW_SAMPLE_FORM_SECOND_PATHOGEN_LABORATORY_NAME, labName);
              break;
          }
        });

    When(
        "^I create and send Laboratory Notification with one positive pathogen$",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileWithOnePathogen(
                  patientFirstName, patientLastName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    When(
        "^I create and send Laboratory Notification with two samples$",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileWithTwoSamples(
                  patientFirstName, patientLastName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    Then(
        "^I check that multiple samples window pops up$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_SAMPLES_HEADER);
        });

    And(
        "^I confirm multiple samples window$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POPUP_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(POPUP_CONFIRM_BUTTON);
        });

    And(
        "^I pick a new sample in Pick or create sample popup during processing case$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CREATE_NEW_SAMPLE_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_SAMPLE_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    And(
        "^I create and send Laboratory Notification with one existing facility$",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileWithOneExistingFacility(
                  patientFirstName, patientLastName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    When(
        "^I create and send Laboratory Notification with multiple pathogen in one sample$",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileWithMultiplePathogenOneSample(
                  patientFirstName, patientLastName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    And(
        "^I verify that test type for \"([^\"]*)\" pathogen is prefilled with \"([^\"]*)\" in New Sample form while processing a DEMIS LabMessage$",
        (String pathogen, String testType) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              NEW_SAMPLE_FORM_FIRST_PATHOGEN_TEST_TYPE_INPUT);
          switch (pathogen) {
            case "first":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(
                      NEW_SAMPLE_FORM_FIRST_PATHOGEN_TEST_TYPE_INPUT),
                  testType,
                  "The disease variant is incorrect");
              softly.assertAll();
              break;
            case "second":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(
                      NEW_SAMPLE_FORM_SECOND_PATHOGEN_TEST_TYPE_INPUT),
                  testType,
                  "The disease variant is incorrect");
              softly.assertAll();
              break;
          }
        });

    When(
        "^I create and send Laboratory Notification for physician report$",
        () -> {
          patientFirstName = faker.name().firstName();
          patientLastName = faker.name().lastName();
          String json =
              demisApiService.prepareLabNotificationFileForPhysicianReport(
                  patientFirstName, patientLastName);

          Assert.assertTrue(
              demisApiService.sendLabRequest(json, loginToken),
              "Failed to send laboratory request");
        });

    And(
        "^I click on \"([^\"]*)\" button in new physician report form while processing a message$",
        (String option) -> {
          switch (option) {
            case "save and open case":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  POPUP_WINDOW_SAVE_AND_OPEN_PHYSICIAN_REPORT_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(
                  POPUP_WINDOW_SAVE_AND_OPEN_PHYSICIAN_REPORT_BUTTON);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
              break;
            case "cancel":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_CANCEL_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_CANCEL_BUTTON);
              break;
            case "back":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_BACK_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_BACK_BUTTON);
              break;
            case "save":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_SAVE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_SAVE_BUTTON);
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
              break;
          }
        });

    And(
        "^I click next button while processing a DEMIS LabMessage$",
        () -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(CASE_SAVED_POPUP_DE, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP_DE);
          }
          webDriverHelpers.scrollToElement(NEXT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEXT_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP_DE);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP_DE);
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

  private List<String> getTableColumnDataByIndex(int col, int maxRows) {
    List<String> list = new ArrayList<>();
    for (int i = 1; i < maxRows + 1; i++) {
      list.add(
          webDriverHelpers.getTextFromWebElement(
              By.xpath("//tbody//tr[" + i + "]//td[" + col + "]")));
    }
    return list;
  }

  private void fillTitle(String title) {
    webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(EVENT_REGION, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(EVENT_DISTRICT, responsibleDistrict);
  }
}
