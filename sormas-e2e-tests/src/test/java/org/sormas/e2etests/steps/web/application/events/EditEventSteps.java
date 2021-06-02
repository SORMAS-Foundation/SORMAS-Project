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

import static org.sormas.e2etests.pages.application.events.EditEventPage.*;
import static org.sormas.e2etests.pages.application.events.EventParticipantsPage.*;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;

import com.github.javafaker.Faker;
import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.Event;
import org.sormas.e2etests.pojo.web.Person;

public class EditEventSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final Faker faker;
  public static Event event;
  public static Person person;

  @Inject
  public EditEventSteps(WebDriverHelpers webDriverHelpers, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;

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
        "I add a participant to the event",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(ADD_PARTICIPANT_BUTTON);
          webDriverHelpers.fillInWebElement(PARTICIPANT_FIRST_NAME_INPUT, faker.name().firstName());
          webDriverHelpers.fillInWebElement(PARTICIPANT_LAST_NAME_INPUT, faker.name().lastName());
          webDriverHelpers.clickOnWebElementBySelector(EDIT_PERSON_POPUP_SAVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE);
          person = collectPersonUuid();
          selectResponsibleRegion("Region1");
          selectResponsibleDistrict("District11");
          webDriverHelpers.clickOnWebElementBySelector(EDIT_PERSON_POPUP_SAVE);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PERSON_DATA_SAVED);
        });
  }

  public Person collectPersonUuid() {
    return Person.builder()
        .uuid(webDriverHelpers.getValueFromWebElement(EDIT_PERSON_POPUP_PERSON_ID))
        .build();
  }

  public Event collectEventUuid() {
    return Event.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  public Event collectEventData() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
    String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    LocalDate reportDate = LocalDate.parse(reportingDate, formatter);
    String eventStartDate = webDriverHelpers.getValueFromWebElement(START_DATA_INPUT);
    LocalDate eventDate = LocalDate.parse(eventStartDate, formatter);

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
    webDriverHelpers.selectFromCombobox(EDIT_PERSON_POPUP_RESPONSIBLE_REGION_COMBOBOX, region);
  }

  public void selectResponsibleDistrict(String district) {
    webDriverHelpers.selectFromCombobox(EDIT_PERSON_POPUP_RESPONSIBLE_DISTRICT_COMBOBOX, district);
  }
}
