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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.BASIC_CASE_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CASE_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CONFIGURATION_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CUSTOM_CASE_DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CUSTOM_CASE_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.CUSTOM_CASE_EXPORT_DOWNLOAD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.DETAILED_CASE_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.EXPORT_CONFIGURATION_DATA_DISEASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.EXPORT_CONFIGURATION_DATA_FIRST_NAME_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.EXPORT_CONFIGURATION_DATA_LAST_NAME_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.NEW_EXPORT_CONFIGURATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.NEW_EXPORT_CONFIGURATION_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseImportExportPage.getCustomExportCheckboxByText;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.FileInputStream;
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
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.sormas.e2etests.entities.pojo.csv.CustomCaseExportCSV;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.enums.CaseClassification;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class CaseImportExportSteps implements En {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  String configurationName;

  @Inject
  public CaseImportExportSteps(
      WebDriverHelpers webDriverHelpers, ApiState apiState, SoftAssert softly) {

    When(
        "I click on the Export case button",
        () -> {
          TimeUnit.SECONDS.sleep(2); // Wait for filter
          webDriverHelpers.clickOnWebElementBySelector(CASE_EXPORT_BUTTON);
        });

    When(
        "I click on the Custom Case Export button",
        () -> webDriverHelpers.clickOnWebElementBySelector(CUSTOM_CASE_EXPORT_BUTTON));

    When(
        "I click on the Detailed Case Export button",
        () -> {
          String file_name = "sormas_f\u00E4lle_" + LocalDate.now().format(formatter) + "_.csv";
          if (webDriverHelpers.isFileExists(
              Paths.get(String.format("./downloads/%s", file_name)))) {
            FilesHelper.deleteFile(file_name);
          }
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.clickOnWebElementBySelector(DETAILED_CASE_EXPORT_BUTTON);
          TimeUnit.SECONDS.sleep(2);
          FilesHelper.waitForFileToDownload(file_name, 15);
        });

    When(
        "I click on the Basic Case Export button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(BASIC_CASE_EXPORT_BUTTON);
          TimeUnit.SECONDS.sleep(5); // wait for download
        });

    When(
        "I click on the New Export Configuration button in Custom Case Export popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_EXPORT_CONFIGURATION_BUTTON));

    When(
        "I fill Configuration Name field with ([^\"]*)",
        (String confName) -> {
          configurationName = confName + LocalDate.now().toString();
          webDriverHelpers.fillInWebElement(CONFIGURATION_NAME_INPUT, configurationName);
        });

    When(
        "I select specific data to export in Export Configuration",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXPORT_CONFIGURATION_DATA_DISEASE_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(
              EXPORT_CONFIGURATION_DATA_FIRST_NAME_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(
              EXPORT_CONFIGURATION_DATA_LAST_NAME_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(NEW_EXPORT_CONFIGURATION_SAVE_BUTTON);
        });

    And(
        "I select ([^\"]*) data to export in existing Export Configuration for Custom Case Export",
        (String customExportConfigurationCheckbox) ->
            webDriverHelpers.clickOnWebElementBySelector(
                getCustomExportCheckboxByText(customExportConfigurationCheckbox)));

    Then(
        "I click Save Button from Custom Case Export Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_EXPORT_CONFIGURATION_SAVE_BUTTON));

    When(
        "I download created custom case export file",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CUSTOM_CASE_EXPORT_DOWNLOAD_BUTTON);
          TimeUnit.SECONDS.sleep(5); // wait for download
        });

    When(
        "I check if downloaded data generated by custom case option is correct",
        () -> {
          String file = "./downloads/sormas_cases_" + LocalDate.now().format(formatter) + "_.csv";
          CustomCaseExportCSV reader = parseCustomCaseExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertEquals(
              reader.getDisease(),
              DiseasesValues.getCaptionForName(apiState.getCreatedCase().getDisease()),
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
        "I check if downloaded data generated by basic case option is correct",
        () -> {
          String file = "./downloads/sormas_cases_" + LocalDate.now().format(formatter) + "_.csv";
          Case reader = parseBasicCaseExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          String UuidXml = checkCsvEmptyCell(reader.getUuid());
          softly.assertEquals(UuidXml, apiState.getCreatedCase().getUuid(), "UUIDs are not equal");
          String DiseaseXml = checkCsvEmptyCell(reader.getDisease());
          softly.assertEquals(
              DiseaseXml,
              DiseasesValues.getCaptionForName(apiState.getCreatedCase().getDisease()),
              "Diseases are not equal");
          String caseClassificationXml = checkCsvEmptyCell(reader.getCaseClassification());
          softly.assertEquals(
              caseClassificationXml,
              CaseClassification.getUIValueForGivenAPIValue(
                  apiState.getCreatedCase().getCaseClassification()),
              "Cases Classification are not equal");
          String pointOfEntryXml = checkCsvEmptyCell(reader.getPointOfEntry());
          softly.assertEquals(
              pointOfEntryXml,
              apiState.getCreatedCase().getPointOfEntry(),
              "Point of entries of case are not equal");
          String firstNameXml = checkCsvEmptyCell(reader.getFirstName());
          softly.assertEquals(
              String.format(firstNameXml, Locale.GERMAN),
              String.format(apiState.getLastCreatedPerson().getFirstName(), Locale.GERMAN),
              "First names are not equal");
          String lastNameXml = checkCsvEmptyCell(reader.getLastName());
          softly.assertEquals(
              String.format(lastNameXml, Locale.GERMAN),
              String.format(apiState.getLastCreatedPerson().getLastName(), Locale.GERMAN),
              "Last names are not equal");
          softly.assertAll();
        });

    When(
        "I check if downloaded data generated by detailed case export is correct",
        () -> {
          String file = "./downloads/sormas_fälle_" + LocalDate.now().format(formatter) + "_.csv";
          Case reader = parseDetailedCaseExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertTrue(
              reader.getInvestigationStatus().equals("investigationStatus"),
              "There is no Investigation Status");
          softly.assertTrue(
              reader.getInvestigatedDate().equals("investigatedDate"),
              "There is no Investigated Date");
          softly.assertTrue(reader.getSequelae().equals("sequelae"), "There is no Seqielae Data");
          softly.assertTrue(
              reader.getProhibitionToWork().equals("prohibitionToWork"),
              "There is no Prohibition to Work Data");
          softly.assertTrue(
              reader.getQuarantineHelpNeeded().equals("quarantineHelpNeeded"),
              "There is no Quarantine Help Needed Data");
          softly.assertTrue(
              reader.getClinicianPhone().equals("clinicianPhone"),
              "There is no Clinician Phone Data");
          softly.assertTrue(
              reader.getClinicianEmail().equals("clinicianEmail"),
              "There is no Clinician Email Data");
          softly.assertTrue(
              reader.getReportingUserName().equals("reportingUserName"),
              "There is no Reporting User Name");
          softly.assertTrue(
              reader.getReportingUserRoles().equals("reportingUserRoles"),
              "There is no Reporting User Role");
          softly.assertTrue(
              reader.getFollowUpStatusChangeUserName().equals("followUpStatusChangeUserName"),
              "There is no Responsible User Name");
          softly.assertTrue(
              reader.getFollowUpStatusChangeUserRoles().equals("followUpStatusChangeUserRoles"),
              "There is no Responsible User Role");
          softly.assertAll();
        });

    When(
        "I check if downloaded data generated by custom case export is correct",
        () -> {
          String file = "./downloads/sormas_fälle_" + LocalDate.now().format(formatter) + "_.csv";
          Case reader = parseExtendCustomCaseExport(file);
          Path path = Paths.get(file);
          Files.delete(path);
          softly.assertTrue(
              reader.getInvestigationStatus().equals("investigationStatus"),
              "There is no Investigation Status");
          softly.assertTrue(
              reader.getInvestigatedDate().equals("investigatedDate"),
              "There is no Investigated Date");
          softly.assertTrue(reader.getSequelae().equals("sequelae"), "There is no Seqielae Data");
          softly.assertTrue(
              reader.getProhibitionToWork().equals("prohibitionToWork"),
              "There is no Prohibition to Work Data");
          softly.assertTrue(
              reader.getQuarantineHelpNeeded().equals("quarantineHelpNeeded"),
              "There is no Quarantine Help Needed Data");
          softly.assertTrue(
              reader.getClinicianPhone().equals("clinicianPhone"),
              "There is no Clinician Phone Data");
          softly.assertTrue(
              reader.getClinicianEmail().equals("clinicianEmail"),
              "There is no Clinician Email Data");
          softly.assertTrue(
              reader.getReportingUserName().equals("reportingUserName"),
              "There is no Reporting User Name");
          softly.assertTrue(
              reader.getReportingUserRoles().equals("reportingUserRoles"),
              "There is no Reporting User Role");
          softly.assertTrue(
              reader.getFollowUpStatusChangeUserName().equals("followUpStatusChangeUserName"),
              "There is no Responsible User Name");
          softly.assertTrue(
              reader.getFollowUpStatusChangeUserRoles().equals("followUpStatusChangeUserRoles"),
              "There is no Responsible User Role");
          softly.assertAll();
        });

    When(
        "I delete created custom case export file",
        () -> webDriverHelpers.clickOnWebElementBySelector(CUSTOM_CASE_DELETE_BUTTON));

    When(
        "I check if downloaded docx file is correct",
        () -> {
          String uuidFirstChars = EditCaseSteps.caseUuid.substring(0, 6);
          String file = String.format("./downloads/%s-preExistingConditions.docx", uuidFirstChars);
          FileInputStream docx = new FileInputStream(file);
          XWPFDocument document = new XWPFDocument(docx);
          XWPFWordExtractor document_extracted = new XWPFWordExtractor(document);
          String docx_string = document_extracted.getText();
          Assert.assertTrue(
              docx_string.contains("Diabetes: Ja"),
              "There is no expected Pre Existing Condition value in docx file!");
          Assert.assertTrue(
              docx_string.contains("Immunodeficiency including HIV: Ja"),
              "There is no expected Pre Existing Condition value in docx file!");
          Assert.assertTrue(
              docx_string.contains("Liver disease: Nein"),
              "There is no expected Pre Existing Condition value in docx file!");
          Assert.assertTrue(
              docx_string.contains("Malignancy: Unbekannt"),
              "There is no expected Pre Existing Condition value in docx file!");
          Assert.assertTrue(
              docx_string.contains("Chronic pulmonary disease\u00A0: Ja"),
              "There is no expected Pre Existing Condition value in docx file!");
          Assert.assertTrue(
              docx_string.contains("Renal disease: Ja"),
              "There is no expected Pre Existing Condition value in docx file!");
          Assert.assertTrue(
              docx_string.contains("Chronic neurologic / neuromuscular disease: Ja"),
              "There is no expected Pre Existing Condition value in docx file!");
          Assert.assertTrue(
              docx_string.contains("Chardiovascular disesase including hypertension: Ja"),
              "There is no expected Pre Existing Condition value in docx file!");
          Files.delete(Paths.get(file));
        });
  }

  private String checkCsvEmptyCell(String rowValue) {
    if (rowValue.length() == 0) rowValue = null;
    return rowValue;
  }

  public CustomCaseExportCSV parseCustomCaseExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    CustomCaseExportCSV builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(3) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseCustomCaseExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseCustomCaseExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder =
          CustomCaseExportCSV.builder()
              .disease(values[0])
              .firstName(String.format(values[1], Locale.GERMAN))
              .lastName(String.format(values[2], Locale.GERMAN))
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomCaseExport: {}", e.getCause());
    }
    return builder;
  }

  public Case parseBasicCaseExport(String fileName) {
    DateTimeFormatter formatterTest =
        DateTimeFormatter.ofPattern("M/d/yyyy h:m a").localizedBy(Locale.ENGLISH);
    List<String[]> r = null;
    String[] values = new String[] {};
    Case builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(2) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseCustomCaseExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseCustomCaseExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder =
          Case.builder()
              .uuid(values[0])
              .disease(values[3])
              .caseClassification(values[5])
              .outcomeOfCase(values[6])
              .investigationStatus(values[7])
              .firstName(String.format(values[9], Locale.GERMAN))
              .lastName(String.format(values[10], Locale.GERMAN))
              .responsibleDistrict(String.format(values[11], Locale.GERMAN))
              .pointOfEntry(values[13])
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomCaseExport: {}", e.getCause());
    }
    return builder;
  }

  public Case parseDetailedCaseExport(String fileName) {
    DateTimeFormatter formatterTest =
        DateTimeFormatter.ofPattern("M/d/yyyy h:m a").localizedBy(Locale.ENGLISH);
    List<String[]> r = null;
    String[] values = new String[] {};
    Case builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseCustomCaseExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseCustomCaseExport: {}", e.getCause());
    }
    try {
      values = r.get(1);

      builder =
          Case.builder()
              .uuid(values[2])
              .investigationStatus(values[40])
              .investigatedDate(values[41])
              .sequelae(values[44])
              .prohibitionToWork(values[49])
              .quarantineHelpNeeded(values[62])
              .symptoms_temperatureclinicianName(values[115])
              .clinicianPhone(values[316])
              .clinicianEmail(values[317])
              .reportingUserName(values[318])
              .reportingUserRoles(values[319])
              .followUpStatusChangeUserName(values[320])
              .followUpStatusChangeUserRoles(values[321])
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomCaseExport: {}", e.getCause());
    }
    return builder;
  }

  public Case parseExtendCustomCaseExport(String fileName) {
    DateTimeFormatter formatterTest =
        DateTimeFormatter.ofPattern("M/d/yyyy h:m a").localizedBy(Locale.ENGLISH);
    List<String[]> r = null;
    String[] values = new String[] {};
    Case builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseCustomCaseExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseCustomCaseExport: {}", e.getCause());
    }
    try {
      values = r.get(1);
      builder =
          Case.builder()
              .investigationStatus(values[0])
              .investigatedDate(values[1])
              .sequelae(values[2])
              .prohibitionToWork(values[3])
              .quarantineHelpNeeded(values[12])
              .clinicianPhone(values[22])
              .clinicianEmail(values[23])
              .reportingUserName(values[24])
              .reportingUserRoles(values[25])
              .followUpStatusChangeUserName(values[26])
              .followUpStatusChangeUserRoles(values[27])
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseCustomCaseExport: {}", e.getCause());
    }
    return builder;
  }
}
