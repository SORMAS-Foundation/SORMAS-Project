/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.steps.web.application.events;

import static org.sormas.e2etests.pages.application.actions.CreateNewActionPage.NEW_ACTION_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.*;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventActionsPage.CREATE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.TOTAL_EVENTS_COUNTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.*;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.EVENT_PARTICIPANTS_TAB;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.events.EditEventPage;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pojo.web.*;
import org.sormas.e2etests.pojo.web.Event;
import org.sormas.e2etests.pojo.web.EventGroup;
import org.sormas.e2etests.pojo.web.EventParticipant;
import org.sormas.e2etests.pojo.web.Person;
import org.sormas.e2etests.services.EventDocumentService;
import org.sormas.e2etests.services.EventGroupService;
import org.sormas.e2etests.services.EventParticipantService;
import org.sormas.e2etests.services.EventService;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class EditEventSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static EventParticipant participant;
  public static Event collectedEvent;
  public static Event createdEvent;
  public static EventGroup groupEvent;
  public static Person person;
  public static EventHandout aEventHandout;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final String userDirPath = System.getProperty("user.dir");

  @Inject
  public EditEventSteps(
      WebDriverHelpers webDriverHelpers,
      EventService eventService,
      EventDocumentService eventDocumentService,
      Faker faker,
      EventGroupService eventGroupService,
      SoftAssert softly,
      EventParticipantService eventParticipant,
      AssertHelpers assertHelpers,
      @Named("ENVIRONMENT_URL") String environmentUrl,
      ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I change the event status to ([^\"]*)",
        (String eventStatus) -> {
          selectEventStatus(eventStatus);
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
        });

    // TODO refactor this
    When(
        "I collect the UUID displayed on Edit event page",
        () -> collectedEvent = collectEventUuid());

    When(
        "I check the created data is correctly displayed in event edit page",
        () -> {
          collectedEvent = collectEventData();
          createdEvent = CreateNewEventSteps.newEvent;

          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedEvent,
              createdEvent,
              List.of(
                  "uuid",
                  "reportDate",
                  "eventDate",
                  "eventStatus",
                  "investigationStatus",
                  "eventManagementStatus",
                  "riskLevel",
                  "disease",
                  "title",
                  "sourceType",
                  "eventLocation"));
        });

    When(
        "I change the fields of event and save",
        () -> {
          collectedEvent = eventService.buildEditEvent();
          fillDateOfReport(collectedEvent.getReportDate());
          fillStartData(collectedEvent.getEventDate());
          collectedEvent =
              collectedEvent.toBuilder()
                  .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
                  .build();
          selectEventStatus(collectedEvent.getEventStatus());
          selectEventInvestigationStatusOptions(collectedEvent.getInvestigationStatus());
          selectEventManagementStatusOption(collectedEvent.getEventManagementStatus());
          selectRiskLevel(collectedEvent.getRiskLevel());
          selectDisease(collectedEvent.getDisease());
          fillTitle(collectedEvent.getTitle());
          selectSourceType(collectedEvent.getSourceType());
          selectTypeOfPlace(collectedEvent.getEventLocation());
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
        });

    When(
        "I check the modified event data is correctly displayed",
        () -> {
          final Event currentEvent = collectEventData();
          ComparisonHelper.compareEqualEntities(collectedEvent, currentEvent);
        });

    When(
        "I add a participant to the event",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, GenderValues.getRandomGender());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_REGION_COMBOBOX, RegionsValues.VoreingestellteBundeslander.getName());
          webDriverHelpers.selectFromCombobox(
              PARTICIPANT_DISTRICT_COMBOBOX, DistrictsValues.VoreingestellterLandkreis.getName());
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP, 15)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_RADIO_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
          }
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE);
          person = collectPersonUuid();
          selectResponsibleRegion("Region1");
          selectResponsibleDistrict("District11");
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PERSON_DATA_SAVED);
        });

    When(
        "I add empty participant data",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
        });

    When(
        "I add participant first name only",
        () -> {
          participant = eventParticipant.buildGeneratedEventParticipant();
          fillFirstName(participant.getFirstName());
        });

    When(
        "I add participant first and last name only",
        () -> {
          participant = eventParticipant.buildGeneratedEventParticipant();
          fillFirstName(participant.getFirstName());
          fillLastName(participant.getLastName());
        });

    When(
        "I check if error display correctly expecting first name error",
        () -> {
          webDriverHelpers.checkWebElementContainsText(ERROR_MESSAGE_TEXT, "First name");
          webDriverHelpers.clickOnWebElementBySelector(ERROR_MESSAGE_TEXT);
        });

    When(
        "I check if error display correctly expecting last name error",
        () -> {
          webDriverHelpers.checkWebElementContainsText(ERROR_MESSAGE_TEXT, "Last name");
          webDriverHelpers.clickOnWebElementBySelector(ERROR_MESSAGE_TEXT);
        });

    When(
        "I check if error display correctly expecting sex error",
        () -> {
          webDriverHelpers.checkWebElementContainsText(ERROR_MESSAGE_TEXT, "Sex");
          webDriverHelpers.clickOnWebElementBySelector(ERROR_MESSAGE_TEXT);
        });

    When(
        "I save changes in participant window",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PICK_OR_CREATE_POPUP_SAVE_BUTTON);
        });

    When(
        "I discard changes in participant window",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON);
        });

    When(
        "^I click on edit task icon of the first created task$",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_FIRST_TASK));

    When(
        "^I click on link event group$",
        () -> {
          webDriverHelpers.scrollToElement(LINK_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_GROUP_BUTTON);
        });

    When(
        "I choose select event group Radiobutton",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(SELECT_EVENT_GROUP_RADIOBUTTON);
        });

    When(
        "I select the first row from table and I click on save button",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(FIRST_GROUP_ID);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
        });

    When(
        "I unlinked the first chosen group by click on Unlink event group button",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.scrollToElement(UNLINK_EVENT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(UNLINK_EVENT_BUTTON);
        });

    When(
        "I click on Edit event group button from event groups box",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.scrollToElement(EDIT_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_EDIT_EVENT_GROUP);
        });

    When(
        "I click on Edit event button for the first event in Events section",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(EDIT_EVENT_GROUP_BUTTON);
        });

    When(
        "I click on the Navigate to event directory filtered on this event group",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.scrollToElement(NAVIGATE_TO_EVENT_DIRECTORY_EVENT_GROUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              NAVIGATE_TO_EVENT_DIRECTORY_EVENT_GROUP_BUTTON);
        });

    When(
        "^I create a new event group$",
        () -> {
          groupEvent = eventGroupService.buildGroupEvent();
          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_GROUP_RADIOBUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
          groupEvent =
              groupEvent.toBuilder()
                  .uuid(webDriverHelpers.getValueFromWebElement(GROUP_EVENT_UUID))
                  .build();
          fillGroupEventName(groupEvent.getName());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_FOR_POPUP_WINDOWS);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_GROUP_EVENT_CREATED_MESSAGE);
        });

    When(
        "^I am checking event group name and id is correctly displayed$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getByEventUuid(groupEvent.getUuid()));
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getGroupEventName(groupEvent.getName()));
        });

    When(
        "I open the last created event via api",
        () -> {
          String LAST_CREATED_EVENT_URL =
              environmentUrl
                  + "/sormas-webdriver/#!events/data/"
                  + apiState.getCreatedEvent().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_EVENT_URL);
        });

    When(
        "I click on New Action button from Event tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_ACTION_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NEW_ACTION_POPUP);
        });

    When(
        "I navigate to Event Action tab for created Event",
        () -> {
          String LAST_CREATED_EVENT_ACTIONS_URL =
              environmentUrl
                  + "/sormas-webdriver/#!events/eventactions/"
                  + apiState.getCreatedEvent().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_EVENT_ACTIONS_URL);
          webDriverHelpers.waitForPageLoaded();
        });

    Then(
        "I click on New Action from Event Actions tab",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CREATE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_BUTTON);
          webDriverHelpers.waitForPageLoaded();
        });

    Then(
        "I click on Event Actions tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_ACTIONS_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CREATE_BUTTON);
        });

    When(
        "I click on the Create button from Event Document Templates",
        () -> webDriverHelpers.clickOnWebElementBySelector(EditEventPage.CREATE_DOCUMENT_BUTTON));

    When(
        "I create an event document from template",
        () -> {
          aEventHandout = eventDocumentService.buildEventHandout();
          aEventHandout = aEventHandout.toBuilder().build();
          selectEventHandoutTemplate(aEventHandout.getDocumentTemplate());
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.CREATE_EVENT_HANDOUT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.CANCEL_EVENT_HANDOUT_BUTTON);
        });
    And(
        "I check that number of displayed Event Participants is {int}",
        (Integer number) -> {
          webDriverHelpers.clickOnWebElementBySelector(EditEventPage.EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.waitForPageLoaded();
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(
                          webDriverHelpers.getTextFromPresentWebElement(TOTAL_EVENTS_COUNTER)),
                      number.intValue(),
                      "Number of displayed actions is not correct"));
        });
    And(
        "I check that number of actions in Edit Event Tab is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        Integer.parseInt(
                            webDriverHelpers.getTextFromPresentWebElement(TOTAL_ACTIONS_COUNTER)),
                        number.intValue(),
                        "Number of displayed actions is not correct")));

    And(
        "I verify that the event document is downloaded and correctly named",
        () -> {
          String uuid = webDriverHelpers.getValueFromWebElement(EditEventPage.UUID_INPUT);
          Path path =
              Paths.get(
                  userDirPath
                      + "/downloads/"
                      + uuid.substring(0, 6)
                      + "-"
                      + aEventHandout.getDocumentTemplate());
          softly.assertTrue(
              Files.exists(path),
              "The document with expected name was not downloaded. Searched after path: "
                  + path.toAbsolutePath());
          softly.assertAll();
        });
  }

  private Person collectPersonUuid() {
    return Person.builder().uuid(webDriverHelpers.getValueFromWebElement(POPUP_PERSON_ID)).build();
  }

  private Event collectEventUuid() {
    return Event.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  private Event collectEventData() {
    String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    LocalDate reportDate = LocalDate.parse(reportingDate, DATE_FORMATTER);
    String eventStartDate = webDriverHelpers.getValueFromWebElement(START_DATA_INPUT);
    LocalDate eventDate = LocalDate.parse(eventStartDate, DATE_FORMATTER);

    return Event.builder()
        .reportDate(reportDate)
        .eventDate(eventDate)
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .eventStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(EVENT_STATUS_OPTIONS))
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                EVENT_INVESTIGATION_STATUS_OPTIONS))
        .eventManagementStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                EVENT_MANAGEMENT_STATUS_OPTIONS))
        .riskLevel(webDriverHelpers.getValueFromWebElement(RISK_LEVEL_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .title(webDriverHelpers.getValueFromWebElement(TITLE_INPUT))
        .sourceType(webDriverHelpers.getValueFromWebElement(SOURCE_TYPE_INPUT))
        .eventLocation(webDriverHelpers.getValueFromWebElement(TYPE_OF_PLACE_INPUT))
        .build();
  }

  private void selectEventStatus(String eventStatus) {
    webDriverHelpers.clickWebElementByText(EVENT_STATUS_OPTIONS, eventStatus);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, lastName);
  }

  private void selectResponsibleRegion(String region) {
    webDriverHelpers.selectFromCombobox(POPUP_RESPONSIBLE_REGION_COMBOBOX, region);
  }

  private void selectResponsibleDistrict(String district) {
    webDriverHelpers.selectFromCombobox(POPUP_RESPONSIBLE_DISTRICT_COMBOBOX, district);
  }

  private void selectRiskLevel(String riskLevel) {
    webDriverHelpers.selectFromCombobox(RISK_LEVEL_COMBOBOX, riskLevel);
  }

  private void selectEventManagementStatusOption(String eventManagementStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_MANAGEMENT_STATUS_OPTIONS, eventManagementStatusOption);
  }

  private void fillStartData(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectEventInvestigationStatusOptions(String eventInvestigationStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_INVESTIGATION_STATUS_OPTIONS, eventInvestigationStatusOption);
  }

  private void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void fillTitle(String title) {
    webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
  }

  private void selectSourceType(String sourceType) {
    webDriverHelpers.selectFromCombobox(SOURCE_TYPE_COMBOBOX, sourceType);
  }

  private void selectTypeOfPlace(String typeOfPlace) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_PLACE_COMBOBOX, typeOfPlace);
  }

  private void fillDateOfReport(LocalDate date) {
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillGroupEventName(String groupEventName) {
    webDriverHelpers.fillInWebElement(GROUP_EVENT_NAME_POPUP_INPUT, groupEventName);
  }

  private EventGroup collectEventGroupUuid() {
    return EventGroup.builder()
        .uuid(webDriverHelpers.getValueFromWebElement(GROUP_EVENT_UUID))
        .build();
  }

  private void selectEventHandoutTemplate(String templateName) {
    webDriverHelpers.selectFromCombobox(EVENT_HANDOUT_COMBOBOX, templateName);
  }
}
