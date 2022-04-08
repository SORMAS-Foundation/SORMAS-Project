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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.BASIC_CASE_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CASE_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CONFIGURATION_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CUSTOM_CASE_DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CUSTOM_CASE_EXPORT_DOWNLOAD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.EXPORT_CONFIGURATION_DATA_DISEASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.NEW_EXPORT_CONFIGURATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.NEW_EXPORT_CONFIGURATION_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactImportExportPage.CUSTOM_CONTACT_EXPORT;
import static org.sormas.e2etests.pages.application.contacts.ContactImportExportPage.EXPORT_CONFIGURATION_DATA_FIRST_NAME_CHECKBOX_CONTACT;
import static org.sormas.e2etests.pages.application.contacts.ContactImportExportPage.EXPORT_CONFIGURATION_DATA_ID_CHECKBOX_CONTACT;
import static org.sormas.e2etests.pages.application.contacts.ContactImportExportPage.EXPORT_CONFIGURATION_DATA_LAST_NAME_CHECKBOX_CONTACT;

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
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.csv.CustomContactExportCSV;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class ContactImportExportSteps implements En {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  String configurationName;

  @Inject
  public ContactImportExportSteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      SoftAssert softly,
      AssertHelpers assertHelpers) {

    When(
        "I click on the Export contact button",
        () -> {
          TimeUnit.SECONDS.sleep(2); // Wait for filter
          webDriverHelpers.clickOnWebElementBySelector(CASE_EXPORT_BUTTON);
        });

    When(
        "I click on the Custom Contact Export button",
        () -> webDriverHelpers.clickOnWebElementBySelector(CUSTOM_CONTACT_EXPORT));

    When(
        "I click on the Basic Contact Export button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BASIC_CASE_EXPORT_BUTTON);
        });

    When(
        "I click on the New Export Configuration button in Custom Contact Export popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_EXPORT_CONFIGURATION_BUTTON));

    When(
        "I fill Configuration Name field with {string} in Custom Contact Export popup",
        (String confName) -> {
          configurationName = confName + LocalDate.now().toString();
          webDriverHelpers.fillInWebElement(CONFIGURATION_NAME_INPUT, configurationName);
        });

    When(
        "I select specific data to export in Export Configuration for Custom Contact Export",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXPORT_CONFIGURATION_DATA_DISEASE_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(
              EXPORT_CONFIGURATION_DATA_ID_CHECKBOX_CONTACT);
          webDriverHelpers.clickOnWebElementBySelector(
              EXPORT_CONFIGURATION_DATA_FIRST_NAME_CHECKBOX_CONTACT);
          webDriverHelpers.clickOnWebElementBySelector(
              EXPORT_CONFIGURATION_DATA_LAST_NAME_CHECKBOX_CONTACT);
          webDriverHelpers.clickOnWebElementBySelector(NEW_EXPORT_CONFIGURATION_SAVE_BUTTON);
        });

    When(
        "I download created custom contact export file",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_CASE_EXPORT_DOWNLOAD_BUTTON);
        });

    When(
        "I check if downloaded data generated by custom contact option is correct",
        () -> {
          String file =
              "./downloads/sormas_kontakte_" + LocalDate.now().format(formatter) + "_.csv";
          Path path = Paths.get(file);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertTrue(
                      Files.exists(path),
                      "Custom contact document was not downloaded. Path used for check: "
                          + path.toAbsolutePath()));
          CustomContactExportCSV reader = parseCustomContactExport(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getUuid(), apiState.getCreatedContact().getUuid(), "UUIDs are not equal");
          softly.assertEquals(
              reader.getDisease(),
              DiseasesValues.getCaptionForName(apiState.getCreatedContact().getDisease()),
              "Diseases are not equal");
          softly.assertEquals(
              String.format(reader.getFirstName(), Locale.GERMAN),
              String.format(apiState.getLastCreatedPerson().getFirstName(), Locale.GERMAN),
              "First names are not equal");
          softly.assertEquals(
              String.format(reader.getLastName(), Locale.GERMAN),
              String.format(apiState.getLastCreatedPerson().getLastName(), Locale.GERMAN),
              "Last names are not equal");
          softly.assertAll();
        });

    When(
        "I check if downloaded data generated by basic contact option is correct",
        () -> {
          String file =
              "./downloads/sormas_kontakte_" + LocalDate.now().format(formatter) + "_.csv";

          Path path = Paths.get(file);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertTrue(
                      Files.exists(path),
                      "Basic contact document was not downloaded. Path used for check: "
                          + path.toAbsolutePath()));
          Contact reader = parseBasicContactExport(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getUuid(), apiState.getCreatedContact().getUuid(), "UUIDs are not equal");
          softly.assertAll();
        });

    When(
        "I delete created custom contact export file",
        () -> webDriverHelpers.clickOnWebElementBySelector(CUSTOM_CASE_DELETE_BUTTON));
  }

  public CustomContactExportCSV parseCustomContactExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    CustomContactExportCSV builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(3) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseCustomContactExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseCustomContactExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder =
          CustomContactExportCSV.builder()
              .uuid(values[0])
              .disease(values[1])
              .firstName(String.format(values[2], Locale.GERMAN))
              .lastName(String.format(values[3], Locale.GERMAN))
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomContactExport: {}", e.getCause());
    }
    return builder;
  }

  public Contact parseBasicContactExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    Contact builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(2) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseBasicContactExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseBasicContactExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder = Contact.builder().uuid(values[0]).build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseBasicContactExport: {}", e.getCause());
    }
    return builder;
  }
}
