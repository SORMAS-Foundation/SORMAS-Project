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

import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.*;
import static org.sormas.e2etests.pages.application.events.EditEventPage.UUID_INPUT;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Event;
import org.sormas.e2etests.services.EventService;

public class CreateNewEventSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Event newEvent;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public CreateNewEventSteps(WebDriverHelpers webDriverHelpers, EventService eventService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new event",
        () -> {
          String timestamp = String.valueOf(System.currentTimeMillis());
          webDriverHelpers.fillInWebElement(
              TITLE_INPUT, "EVENT_AUTOMATION" + timestamp + Faker.instance().name().name());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_CREATED_MESSAGE);
        });

    When(
        "^I create a new event with specific data$",
        () -> {
          newEvent = eventService.buildGeneratedEvent();
          fillDateOfReport(newEvent.getReportDate());
          fillStartData(newEvent.getEventDate());
          selectEventStatus(newEvent.getEventStatus());
          selectEventInvestigationStatusOptions(newEvent.getInvestigationStatus());
          selectEventInvestigationStatusOptions(
              newEvent.getInvestigationStatus()); // remove after bug 5547 is fixed the duplication
          selectEventManagementStatusOption(newEvent.getEventManagementStatus());
          selectRiskLevel(newEvent.getRiskLevel());
          selectDisease(newEvent.getDisease());
          fillTitle(newEvent.getTitle());
          selectSourceType(newEvent.getSourceType());
          selectTypeOfPlace(newEvent.getEventLocation());
          newEvent =
              newEvent.toBuilder()
                  .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
                  .build();
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_CREATED_MESSAGE);
        });

    When(
        "^I create a new event with status ([^\"]*)",
        (String eventStatus) -> {
          newEvent = collectEventUuid();
          String timestamp = String.valueOf(System.currentTimeMillis());
          webDriverHelpers.fillInWebElement(TITLE_INPUT, "EVENT_AUTOMATION" + timestamp);
          selectEventStatus(eventStatus);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_CREATED_MESSAGE);
        });

    When(
        "^I validate all fields are rendered correctly on create a new event popup window",
        () -> {
          String timestamp = String.valueOf(System.currentTimeMillis());
          webDriverHelpers.fillInWebElement(TITLE_INPUT, "EVENT_AUTOMATION_" + timestamp);
          selectEventStatus("SIGNAL");
          selectEventStatus("EVENT");
          selectEventStatus("SCREENING");
          selectEventStatus("CLUSTER");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PRIMARY_MODE_OF_TRANSMISSION_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NOSOCOMIAL_INPUT);
          selectEventStatus("DROPPED");
          selectEventManagementStatusOption("PENDING");
          selectEventManagementStatusOption("ONGOING");
          selectEventManagementStatusOption("DONE");
          selectEventManagementStatusOption("CLOSED");
          selectRiskLevel("Low risk");
          selectRiskLevel("Moderate risk");
          selectRiskLevel("High risk");
          selectRiskLevel("Unknown");
          webDriverHelpers.scrollToElement(DISEASE_COMBOBOX);
          selectEventInvestigationStatusOptions("INVESTIGATION PENDING");
          selectEventInvestigationStatusOptions("ONGOING INVESTIGATION");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EVENT_INVESTIGATION_START_DATE_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EVENT_INVESTIGATION_END_DATE_INPUT);
          selectEventInvestigationStatusOptions("INVESTIGATION DONE");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EVENT_INVESTIGATION_START_DATE_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EVENT_INVESTIGATION_END_DATE_INPUT);
          selectEventInvestigationStatusOptions("INVESTIGATION DISCARDED");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EVENT_INVESTIGATION_START_DATE_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              EVENT_INVESTIGATION_END_DATE_INPUT);
          selectDisease("Acute Flaccid Paralysis");
          selectDisease("Anthrax");
          selectDisease("COVID-19");
          selectDisease("Cholera");
          selectDisease("Congenital Rubella");
          selectDisease("Dengue Fever");
          selectDisease("Ebola Virus Disease");
          selectDisease("Guinea Worm");
          selectDisease("Human Rabies");
          selectDisease("Influenza (New subtype)");
          selectDisease("Lassa");
          selectDisease("Measles");
          selectDisease("Meningitis (CSM)");
          selectDisease("Monkeypox");
          selectDisease("Not Yet Defined");
          selectDisease("Other Epidemic Disease");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISEASE_NAME_INPUT);
          selectDisease("Plague");
          selectDisease("Poliomyelitis");
          selectDisease("Unspecified VHF");
          selectDisease("Yellow Fever");
          selectSourceType("Not applicable");
          selectSourceType("Mathematical model");
          selectSourceType("Media/News");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_MEDIA_WEBSITE_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_MEDIA_NAME_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_MEDIA_DETAILS_INPUT);
          selectSourceType("Hotline/Person");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_FIRST_NAME_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_LAST_NAME_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_TEL_NO_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_EMAIL_INPUT);
          selectSourceType("Institutional partner");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_FIRST_NAME_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_LAST_NAME_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_TEL_NO_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SOURCE_EMAIL_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SOURCE_INSTITUTIONAL_PARTNER_COMBOBOX);
          selectSourceInstitutionalPartner("Other");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SOURCE_INSTITUTIONAL_PARTNER_DETAILS_INPUT);
          selectTypeOfPlace("Facility");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_CATEGORY_COMBOBOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_TYPE_COMBOBOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_COMBOBOX);
          selectTypeOfPlace("Festivities");
          selectTypeOfPlace("Home");
          selectTypeOfPlace("Means of transport");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(MEANS_OF_TRANSPORT_COMBOBOX);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONNECTION_NUMBER_INPUT);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(TRAVEL_DATE_INPUT);
          selectMeansOfTransport("Local public transport");
          selectMeansOfTransport("Bus");
          selectMeansOfTransport("Ship/Ferry");
          selectMeansOfTransport("Plane");
          selectMeansOfTransport("Train");
          selectMeansOfTransport("Other");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(MEANS_OF_TRANSPORT_DETAILS_INPUT);
          selectTypeOfPlace("Public place");
          selectTypeOfPlace("Scattered");
          selectTypeOfPlace("Unknown");
          selectTypeOfPlace("Other");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SPECIFY_OTHER_EVENT_PLACE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON);
        });
  }

  public Event collectEventUuid() {
    return Event.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  public void selectEventStatus(String eventStatus) {
    webDriverHelpers.clickWebElementByText(EVENT_STATUS_OPTIONS, eventStatus);
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

  public void selectMeansOfTransport(String meansOfTransport) {
    webDriverHelpers.selectFromCombobox(MEANS_OF_TRANSPORT_COMBOBOX, meansOfTransport);
  }

  public void selectSourceInstitutionalPartner(String institutionalPartner) {
    webDriverHelpers.selectFromCombobox(
        SOURCE_INSTITUTIONAL_PARTNER_COMBOBOX, institutionalPartner);
  }

  public void fillDateOfReport(LocalDate date) {
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }
}
