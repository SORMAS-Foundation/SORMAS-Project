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

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.Event;

public class EditEventSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Event event;

  @Inject
  public EditEventSteps(WebDriverHelpers webDriverHelpers) {
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
  }

  public Event collectEventUuid() {
    return Event.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  public Event collectEventData() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
    String reportingDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
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
                EVENT_INVESTIGATION_STATUS_INPUT))
        .eventManagementStatus(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                EVENT_MANAGEMENT_STATUS_INPUT))
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

  public void selectRiskLevel(String riskLevel) {
    webDriverHelpers.selectFromCombobox(RISK_LEVEL_COMBOBOX, riskLevel);
  }

  public void selectEventManagementStatusOption(String eventManagementStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_MANAGEMENT_STATUS_OPTIONS, eventManagementStatusOption);
  }

  public void fillStartData(String startData) {
    webDriverHelpers.clickWebElementByText(START_DATA_INPUT, startData);
  }

  public void fillSignalEvolutionDate(String signalEvolutionDate) {
    webDriverHelpers.clickWebElementByText(SIGNAL_EVOLUTION_DATE_INPUT, signalEvolutionDate);
  }

  public void selectEventInvestigationStatusOptions(String eventInvestigationStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_INVESTIGATION_STATUS_OPTIONS, eventInvestigationStatusOption);
  }

  public void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  public void fillExternalIdInput(String externalId) {
    webDriverHelpers.clickWebElementByText(EXTERNAL_ID_INPUT, externalId);
  }

  public void fillInternalIdInput(String internalIdInput) {
    webDriverHelpers.fillInWebElement(INTERNAL_ID_INPUT, internalIdInput);
  }

  public void fillExternalTokenInput(String externalToken) {
    webDriverHelpers.fillInWebElement(EXTERNAL_TOKEN_INPUT, externalToken);
  }

  public void fillTitle(String title) {
    webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
  }

  public void fillDescriptionInput(String description) {
    webDriverHelpers.fillInWebElement(DESCRIPTION_INPUT, description);
  }

  public void selectSourceType(String sourceType) {
    webDriverHelpers.selectFromCombobox(SOURCE_TYPE_COMBOBOX, sourceType);
  }

  public void selectSourceInstitutionalPartner(String institutionalPartner) {
    webDriverHelpers.selectFromCombobox(SOURCE_INSTITUTIONAL_PARTNER, institutionalPartner);
  }

  public void selectTypeOfPlace(String typeOfPlace) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_PLACE_COMBOBOX, typeOfPlace);
  }

  public void selectMeansOfTransport(String meansOfTransport) {
    webDriverHelpers.selectFromCombobox(MEANS_OF_TRANSPORT, meansOfTransport);
  }

  public void selectCountryCombobox(String country) {
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, country);
  }

  public void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }
}
