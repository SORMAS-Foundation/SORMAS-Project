/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.UUID_INPUT;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.web.Event;
import org.sormas.e2etests.entities.services.EventService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

@Slf4j
public class CreateNewEventSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Event newEvent;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Inject
  public CreateNewEventSteps(
      WebDriverHelpers webDriverHelpers, EventService eventService, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new event with specific data for DE version$",
        () -> {
          newEvent = eventService.buildGeneratedEventDE();
          fillDateOfReport(newEvent.getReportDate(), Locale.GERMAN);
          fillStartData(newEvent.getEventDate(), Locale.GERMAN);
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
          selectResponsibleRegion(newEvent.getRegion());
          selectResponsibleDistrict(newEvent.getDistrict());
          selectResponsibleCommunity(newEvent.getCommunity());
          newEvent =
              newEvent.toBuilder()
                  .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
                  .build();
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(NEW_EVENT_CREATED_DE_MESSAGE);
        });

    When(
        "^I create a new event with specific data$",
        () -> {
          newEvent = eventService.buildGeneratedEvent();
          fillDateOfReport(newEvent.getReportDate(), Locale.ENGLISH);
          fillStartData(newEvent.getEventDate(), Locale.ENGLISH);
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
          selectResponsibleRegion(newEvent.getRegion());
          selectResponsibleDistrict(newEvent.getDistrict());
          selectResponsibleCommunity(newEvent.getCommunity());
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
        "I check if downloaded data generated by basic event export option is correct",
        () -> {
          String file =
              "./downloads/sormas_ereignisse_" + LocalDate.now().format(formatter) + "_.csv";
          Event reader = parseBasicEventExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getUuid().toLowerCase(),
              newEvent.getUuid().toLowerCase(),
              "UUIDs are not equal");
          softly.assertEquals(
              reader.getEventStatus().toLowerCase(),
              newEvent.getEventStatus().toLowerCase(),
              "Event statuses are not equal");
          softly.assertEquals(
              reader.getRiskLevel().toLowerCase(),
              newEvent.getRiskLevel().toLowerCase(),
              "Risk levels are not equal");
          softly.assertEquals(
              reader.getInvestigationStatus().toLowerCase(),
              newEvent.getInvestigationStatus().toLowerCase(),
              "Investigation statuses are not equal");
          softly.assertEquals(
              reader.getEventManagementStatus().toLowerCase(),
              newEvent.getEventManagementStatus().toLowerCase(),
              "Event management statuses are not equal");
          softly.assertEquals(
              reader.getDisease().toLowerCase(),
              newEvent.getDisease().toLowerCase(),
              "Diseases statuses are not equal");
          softly.assertAll();
        });

    When(
        "I check if downloaded data generated by detailed event export option is correct",
        () -> {
          String file =
              "./downloads/sormas_ereignisse_" + LocalDate.now().format(formatter) + "_.csv";
          Event reader = parseDetailedEventExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getUuid().toLowerCase(),
              newEvent.getUuid().toLowerCase(),
              "UUIDs are not equal");
          softly.assertEquals(
              reader.getEventStatus().toLowerCase(),
              newEvent.getEventStatus().toLowerCase(),
              "Event statuses are not equal");
          softly.assertEquals(
              reader.getRiskLevel().toLowerCase(),
              newEvent.getRiskLevel().toLowerCase(),
              "Risk levels are not equal");
          softly.assertEquals(
              reader.getInvestigationStatus().toLowerCase(),
              newEvent.getInvestigationStatus().toLowerCase(),
              "Investigation statuses are not equal");
          softly.assertEquals(
              reader.getEventManagementStatus().toLowerCase(),
              newEvent.getEventManagementStatus().toLowerCase(),
              "Event management statuses are not equal");
          softly.assertEquals(
              reader.getDisease().toLowerCase(),
              newEvent.getDisease().toLowerCase(),
              "Diseases statuses are not equal");
          softly.assertAll();
        });
  }

  private Event collectEventUuid() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 30);
    return Event.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  private void selectEventStatus(String eventStatus) {
    webDriverHelpers.clickWebElementByText(EVENT_STATUS_OPTIONS, eventStatus);
  }

  private void selectRiskLevel(String riskLevel) {
    webDriverHelpers.selectFromCombobox(RISK_LEVEL_COMBOBOX, riskLevel);
  }

  private void selectEventManagementStatusOption(String eventManagementStatusOption) {
    webDriverHelpers.clickWebElementByText(
        EVENT_MANAGEMENT_STATUS_OPTIONS, eventManagementStatusOption);
  }

  private void fillStartData(LocalDate date, Locale locale) {
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER_DE.format(date));
    else webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER.format(date));
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

  private void selectMeansOfTransport(String meansOfTransport) {
    webDriverHelpers.selectFromCombobox(MEANS_OF_TRANSPORT_COMBOBOX, meansOfTransport);
  }

  private void selectSourceInstitutionalPartner(String institutionalPartner) {
    webDriverHelpers.selectFromCombobox(
        SOURCE_INSTITUTIONAL_PARTNER_COMBOBOX, institutionalPartner);
  }

  private void fillDateOfReport(LocalDate date, Locale locale) {
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER_DE.format(date));
    else webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(EVENT_REGION, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(EVENT_DISTRICT, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(EVENT_COMMUNITY, responsibleCommunity);
  }

  public Event parseBasicEventExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Event builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(2) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseBasicEventExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseBasicEventExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder =
          Event.builder()
              .uuid(values[0])
              .eventStatus(values[4])
              .riskLevel(values[5])
              .investigationStatus(values[6])
              .eventManagementStatus(values[7])
              .disease(values[11])
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseBasicEventExport: {}", e.getCause());
    }
    return builder;
  }

  public Event parseDetailedEventExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Event builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(3) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseDetailedEventExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseDetailedEventExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder =
          Event.builder()
              .uuid(values[0])
              .eventStatus(values[2])
              .eventManagementStatus(values[3])
              .riskLevel(values[5])
              .investigationStatus(values[7])
              .disease(values[10])
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomCaseExport: {}", e.getCause());
    }
    return builder;
  }
}
