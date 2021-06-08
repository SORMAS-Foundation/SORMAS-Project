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

import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.events.EditEventPage.*;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.*;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;

import com.github.javafaker.Faker;
import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Event;
import org.sormas.e2etests.pojo.web.EventGroup;
import org.sormas.e2etests.pojo.web.Person;
import org.sormas.e2etests.services.EventService;
import org.sormas.e2etests.services.EventGroupService;

public class EditEventSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Event event;
  public static EventGroup groupEvent;
  public static Person person;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public EditEventSteps(WebDriverHelpers webDriverHelpers, EventService eventService, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I change the event status to ([^\"]*)",
        (String eventStatus) -> {
          selectEventStatus(eventStatus);
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
        });

    When("I collect the UUID displayed on Edit event page", () -> event = collectEventUuid());

    When(
        "I check the created data is correctly displayed in event edit page",
        () -> {
          event = collectEventData();
          Truth.assertThat(event.getUuid()).isEqualTo(CreateNewEventSteps.newEvent.getUuid());
          Truth.assertThat(event.getReportDate())
              .isEqualTo(CreateNewEventSteps.newEvent.getReportDate());
          Truth.assertThat(event.getEventDate())
              .isEqualTo(CreateNewEventSteps.newEvent.getEventDate());
          Truth.assertThat(event.getEventStatus())
              .isEqualTo(CreateNewEventSteps.newEvent.getEventStatus());
          Truth.assertThat(event.getInvestigationStatus())
              .isEqualTo(CreateNewEventSteps.newEvent.getInvestigationStatus());
          Truth.assertThat(event.getEventManagementStatus())
              .isEqualTo(CreateNewEventSteps.newEvent.getEventManagementStatus());
          Truth.assertThat(event.getRiskLevel())
              .isEqualTo(CreateNewEventSteps.newEvent.getRiskLevel());
          Truth.assertThat(event.getDisease()).isEqualTo(CreateNewEventSteps.newEvent.getDisease());
          Truth.assertThat(event.getTitle()).isEqualTo(CreateNewEventSteps.newEvent.getTitle());
          Truth.assertThat(event.getSourceType())
              .isEqualTo(CreateNewEventSteps.newEvent.getSourceType());
          Truth.assertThat(event.getEventLocation())
              .isEqualTo(CreateNewEventSteps.newEvent.getEventLocation());
        });

    When(
        "I change the fields of event and save",
        () -> {
          event = eventService.buildEditEvent();
          fillDateOfReport(event.getReportDate());
          fillStartData(event.getEventDate());
          event =
              event.toBuilder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
          selectEventStatus(event.getEventStatus());
          selectEventInvestigationStatusOptions(event.getInvestigationStatus());
          selectEventManagementStatusOption(event.getEventManagementStatus());
          selectRiskLevel(event.getRiskLevel());
          selectDisease(event.getDisease());
          fillTitle(event.getTitle());
          selectSourceType(event.getSourceType());
          selectTypeOfPlace(event.getEventLocation());
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_DATA_SAVED_MESSAGE);
        });

    When(
        "I check the modified event data is correctly displayed",
        () -> {
          final Event currentEvent = collectEventData();
          Truth.assertThat(event).isEqualTo(currentEvent);
        });

    When(
        "I add a participant to the event",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE);
          person = collectPersonUuid();
          selectResponsibleRegion("Region1");
          selectResponsibleDistrict("District11");
          webDriverHelpers.clickOnWebElementBySelector(POPUP_SAVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PERSON_DATA_SAVED);
        });

    When(
        "^I click on edit task icon of the first created task$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_TASK_O);
        });

    When(
        "^I click on link event group$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LINK_EVENT_GROUP);
        });

    When(
        "^I create a new event group$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_GROUP_RADIOBUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_POPUP);
          fillGroupEventName(faker.funnyName().name());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_POPUP);
        });
  }

  public Person collectPersonUuid() {
    return Person.builder().uuid(webDriverHelpers.getValueFromWebElement(POPUP_PERSON_ID)).build();
  }

  public Event collectEventUuid() {
    return Event.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  public Event collectEventData() {
    String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    LocalDate reportDate = LocalDate.parse(reportingDate, DATE_FORMATTER);
    String eventStartDate = webDriverHelpers.getValueFromWebElement(START_DATA_INPUT);
    LocalDate eventDate = LocalDate.parse(eventStartDate, DATE_FORMATTER);

    return Event.builder()
        .reportDate(reportDate)
        .eventDate(eventDate)
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .eventStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SELECTED_EVENT_STATUS))
        .investigationStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                SELECTED_EVENT_INVESTIGATION_STATUS))
        .eventManagementStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                SELECTED_EVENT_MANAGEMENT_STATUS))
        .riskLevel(webDriverHelpers.getValueFromWebElement(RISK_LEVEL_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .title(webDriverHelpers.getValueFromWebElement(TITLE_INPUT))
        .sourceType(webDriverHelpers.getValueFromWebElement(SOURCE_TYPE_INPUT))
        .eventLocation(webDriverHelpers.getValueFromWebElement(TYPE_OF_PLACE_INPUT))
        .build();
  }

  public void selectEventStatus(String eventStatus) {
    webDriverHelpers.clickWebElementByText(EVENT_STATUS_OPTIONS, eventStatus);
  }

  public void selectResponsibleRegion(String region) {
    webDriverHelpers.selectFromCombobox(POPUP_RESPONSIBLE_REGION_COMBOBOX, region);
  }

  public void selectResponsibleDistrict(String district) {
    webDriverHelpers.selectFromCombobox(POPUP_RESPONSIBLE_DISTRICT_COMBOBOX, district);
  }

  public void selectRiskLevel(String riskLevel) {
    webDriverHelpers.selectFromCombobox(RISK_LEVEL_COMBOBOX, riskLevel);
  }

  public void selectEventManagementStatusOption(String eventManagementStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_MANAGEMENT_STATUS_OPTIONS, eventManagementStatusOption);
  }

  public void fillStartData(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER.format(date));
  }

  public void selectEventInvestigationStatusOptions(String eventInvestigationStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_INVESTIGATION_STATUS_OPTIONS, eventInvestigationStatusOption);
  }

  public void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  public void fillTitle(String title) {
    webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
  }

  public void selectSourceType(String sourceType) {
    webDriverHelpers.selectFromCombobox(SOURCE_TYPE_COMBOBOX, sourceType);
  }

  public void selectTypeOfPlace(String typeOfPlace) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_PLACE_COMBOBOX, typeOfPlace);
  }

  public void fillDateOfReport(LocalDate date) {
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  public void fillGroupEventName(String groupEventName) {
    webDriverHelpers.fillInWebElement(GROUP_EVENT_NAME_POPUP_INPUT, groupEventName);
  }
}
