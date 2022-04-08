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

package org.sormas.e2etests.steps.web.application.facilities;

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_FACILITIES_TAB;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.CLOSE_DETAILED_EXPORT_POPUP;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.CLOSE_FACILITIES_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.CLOSE_POPUP_FACILITIES_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.DETAILED_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.EXPORT_FACILITY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITIES_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.IMPORT_SUCCESSFUL_FACILITY_IMPORT_CSV;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.OVERWRITE_CHECKBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.SEARCH_FACILITY;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.START_DATA_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_ACTIONS_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_ACTIONS_TABLE_DATA;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_ACTIONS_TABLE_ROW;

import com.github.javafaker.Faker;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.csv.FacilityCSV;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

@Slf4j
public class ConfigurationFacilitiesSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;
  public static final String userDirPath = System.getProperty("user.dir");
  public static String[] APIHeader;
  public static String[] UIHeader;
  public static int randomNr;
  public static Faker faker;
  private String facilityName;
  private static String facilityFile;
  public static String cityName;
  public static String aFacilityName;
  public static String uploadFileDirectoryAndName;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @SneakyThrows
  @Inject
  public ConfigurationFacilitiesSteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;
    this.faker = faker;

    When(
        "I navigate to facilities tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_FACILITIES_TAB));

    When(
        "I click on the import button in facilities",
        () -> webDriverHelpers.clickOnWebElementBySelector(FACILITIES_IMPORT_BUTTON));

    When(
        "I click on overwrite existing entries with imported data checkbox",
        () -> webDriverHelpers.clickOnWebElementBySelector(OVERWRITE_CHECKBOX));

    When(
        "I pick the facilities test data file",
        () -> webDriverHelpers.sendFile(FILE_PICKER, uploadFileDirectoryAndName));

    When(
        "I click on the {string} button from the Import Facilities Entries popup",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(START_DATA_IMPORT_BUTTON, buttonName));

    When(
        "I select to export first record in facilities tab",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          facilityName = tableRowsData.get(0).get(FacilityTableColumsHeadres.NAME.toString());
          webDriverHelpers.fillInWebElement(SEARCH_FACILITY, facilityName);
          TimeUnit.SECONDS.sleep(2); // wait for filter
        });

    When(
        "I export selected facility to csv from facilities tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXPORT_FACILITY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DETAILED_EXPORT_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for download
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_DETAILED_EXPORT_POPUP);
        });

    When(
        "I check if csv file for facilities is imported successfully",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(IMPORT_SUCCESSFUL_FACILITY_IMPORT_CSV, 10);
        });

    When(
        "I read exported csv from facilities tab",
        () -> {
          facilityFile =
              "./downloads/sormas_einrichtungen_" + LocalDate.now().format(formatter) + "_.csv";
          FacilityCSV reader = parseCSVintoPOJOFacilityTab(facilityFile);
          writeCSVFromPOJOFacilityTab(reader);
          TimeUnit.SECONDS.sleep(5); // wait for reader
        });

    When(
        "I delete downloaded csv file for facilities in facility tab",
        () -> {
          Path path = Paths.get(facilityFile);
          Files.delete(path);
        });

    When(
        "I close import facilities popup window",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_POPUP_FACILITIES_BUTTON));

    When(
        "I close facilities popup window",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_FACILITIES_IMPORT_BUTTON));

    When(
        "I check if data from csv is correctly displayed in facilities tab",
        () -> {
          webDriverHelpers.fillInWebElement(SEARCH_FACILITY, aFacilityName);
          TimeUnit.SECONDS.sleep(2); // wait for filter
          List<Map<String, String>> tableRowsData = getTableRowsData();
          String facilityName =
              tableRowsData.get(0).get(FacilityTableColumsHeadres.NAME.toString());
          softly.assertEquals(facilityName, aFacilityName, "Facilitiy names are not equal");
          String aCity = tableRowsData.get(0).get(FacilityTableColumsHeadres.CITY.toString());
          softly.assertEquals(cityName, aCity, "Cities are not equal");
          softly.assertAll();
        });
  }

  public FacilityCSV parseCSVintoPOJOFacilityTab(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    FacilityCSV builder = null;
    try {
      CSVReader headerReader = new CSVReader(new FileReader(fileName));
      String[] nextLine;
      nextLine = headerReader.readNext();
      APIHeader = nextLine;
      nextLine = headerReader.readNext();
      UIHeader = nextLine;
    } catch (IOException e) {
      log.error("IOException csvReader: ", e);
    } catch (CsvException e) {
      log.error("CsvException header reader: ", e);
    }
    CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); // custom separator
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser) // custom CSV parser
            .withSkipLines(2) // skip the first two lines, headers info
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException csvReader: ", e);
    } catch (CsvException e) {
      log.error("CsvException csvReader: ", e);
    }
    for (int i = 0; i < r.size(); i++) {
      values = r.get(i);
    }
    try {
      builder =
          FacilityCSV.builder()
              .uuid(values[0])
              .name(values[1])
              .type(values[2])
              .region(values[3])
              .district(values[4])
              .community(values[5])
              .city(values[6])
              .postalCode(values[7])
              .street(values[8])
              .houseNumber(values[9])
              .additionalInformation(values[10])
              .areaType(values[11])
              .contactPersonFirstName(values[12])
              .contactPersonLastName(values[13])
              .contactPersonPhone(values[14])
              .contactPersonEmail(values[15])
              .latitude(values[16])
              .longitude(values[17])
              .externalID(values[18])
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception csvReader: ", e);
    }
    return builder;
  }

  public static void writeCSVFromPOJOFacilityTab(FacilityCSV facilityData) {

    uploadFileDirectoryAndName = userDirPath + "/uploads/testFile.csv";
    cityName = faker.harryPotter().location();
    Random rand = new Random();
    randomNr = rand.nextInt(1000);
    aFacilityName = faker.harryPotter().location() + randomNr;

    File file = new File(uploadFileDirectoryAndName);
    try {
      FileWriter outputfile = new FileWriter(file);
      CSVWriter writer =
          new CSVWriter(
              outputfile,
              ',',
              CSVWriter.NO_QUOTE_CHARACTER,
              CSVWriter.NO_ESCAPE_CHARACTER,
              CSVWriter.DEFAULT_LINE_END);
      List<String[]> data = new ArrayList<String[]>();

      String[] rowdata = {
        "\""
            + UUID.randomUUID().toString().substring(0, 26).toUpperCase()
            + "\";\""
            + aFacilityName
            + "\";\""
            + facilityData.getType()
            + "\";\""
            + facilityData.getRegion()
            + "\";\""
            + facilityData.getDistrict()
            + "\";\""
            + facilityData.getCommunity()
            + "\";\""
            + cityName
            + "\";\""
            + facilityData.getPostalCode()
            + "\";\""
            + facilityData.getStreet()
            + "\";\""
            + facilityData.getHouseNumber()
            + "\";\""
            + facilityData.getAdditionalInformation()
            + "\";\""
            + facilityData.getAreaType()
            + "\";\""
            + facilityData.getContactPersonFirstName()
            + "\";\""
            + facilityData.getContactPersonFirstName()
            + "\";\""
            + facilityData.getContactPersonPhone()
            + "\";\""
            + facilityData.getContactPersonEmail()
            + "\";\""
            + facilityData.getLatitude()
            + "\";\""
            + facilityData.getLongitude()
            + "\";\""
            + facilityData.getExternalID()
            + "\""
      };
      APIHeader[0] = "\"" + APIHeader[0] + "\"";
      data.add(APIHeader);
      data.add(UIHeader);
      data.add(rowdata);
      writer.writeAll(data);
      writer.close();
    } catch (IOException e) {
      log.error("IOException csvWriter: ", e);
    }
  }

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(EVENT_ACTIONS_TABLE_DATA);
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
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_ACTIONS_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(EVENT_ACTIONS_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_ACTIONS_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(EVENT_ACTIONS_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(EVENT_ACTIONS_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(EVENT_ACTIONS_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
