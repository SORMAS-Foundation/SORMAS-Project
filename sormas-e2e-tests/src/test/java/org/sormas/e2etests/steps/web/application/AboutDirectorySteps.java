package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.AboutPage.*;
import static org.sormas.e2etests.pages.application.NavBarPage.*;
import static org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage.SURVEILLANCE_DASHBOARD_NAME;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.LANGUAGE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.SAVE_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.helpers.strings.LanguageDetectorHelper;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class AboutDirectorySteps implements En {

  public static final List<String> xlsxFileContentList = new ArrayList<>();
  public static final String DATA_PROTECTION_DICTIONARY_FILE_PATH =
      String.format("sormas_data_protection_dictionary_%s_.xlsx", LocalDate.now());
  public static final String DATA_DICTIONARY_FILE_PATH =
      String.format("sormas_data_dictionary_%s_.xlsx", LocalDate.now());
  public static final String DEUTSCH_DATA_DICTIONARY_FILE_PATH =
      String.format("sormas_datenbeschreibungsverzeichnis_%s_.xlsx", LocalDate.now());
  public static final String DEUTSCH_DATA_PROTECTION_DICTIONARY_FILE_PATH =
      String.format("sormas_datenschutzbeschreibungsverzeichnis_%s_.xlsx", LocalDate.now());
  public static final String CASE_CLASSIFICATION_HTML_FILE_PATH = "classification_rules.html";
  private static final String RELEASE_PAGE =
      "https://github.com/sormas-foundation/SORMAS-Project/releases";
  private static AssertHelpers assertHelpers;

  @Inject
  public AboutDirectorySteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, AssertHelpers assertHelpers) {
    this.assertHelpers = assertHelpers;

    When(
        "I check that current Sormas version is shown on About directory page",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(SORMAS_VERSION_LINK));

    When(
        "I select {string} language from Combobox in User settings",
        (String chosenLanguage) -> {
          webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, chosenLanguage);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I check that ([^\"]*) file size is bigger than 0 bytes",
        (String dictionaryName) -> {
          switch (dictionaryName) {
            case "Data Protection Dictionary":
              FilesHelper.validateFileIsNotEmpty(DATA_PROTECTION_DICTIONARY_FILE_PATH);
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_CAPTION, 3));
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_DESCRIPTION, 3));
              softly.assertAll();
              break;
            case "Data Dictionary":
              FilesHelper.validateFileIsNotEmpty(DATA_DICTIONARY_FILE_PATH);
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_CAPTION, 3));
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_DESCRIPTION, 3));
              softly.assertAll();
              break;
            case "Deutsch Data Dictionary":
              FilesHelper.validateFileIsNotEmpty(DEUTSCH_DATA_DICTIONARY_FILE_PATH);
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_CAPTION_DE, 3));
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(
                      ERROR_NOTIFICATION_DESCRIPTION_DE, 3));
              softly.assertAll();
              break;
            case "Deutsch Data Protection Dictionary":
              FilesHelper.validateFileIsNotEmpty(DEUTSCH_DATA_PROTECTION_DICTIONARY_FILE_PATH);
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_CAPTION_DE, 3));
              softly.assertFalse(
                  webDriverHelpers.isElementVisibleWithTimeout(
                      ERROR_NOTIFICATION_DESCRIPTION_DE, 3));
              softly.assertAll();
              break;
            default:
              throw new Exception("No XLSX file downloaded!");
          }
        });

    When(
        "^I click on ([^\"]*) hyperlink and download XLSX file from About directory$",
        (String dictionaryName) -> {
          switch (dictionaryName) {
            case "Data Protection Dictionary":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  DATA_PROTECTION_DICTIONARY_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(DATA_PROTECTION_DICTIONARY_BUTTON);
              FilesHelper.waitForFileToDownload(DATA_PROTECTION_DICTIONARY_FILE_PATH, 30);
              break;
            case "Data Dictionary":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  DATA_DICTIONARY_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(DATA_DICTIONARY_BUTTON);
              FilesHelper.waitForFileToDownload(DATA_DICTIONARY_FILE_PATH, 30);
              break;
            case "Deutsch Data Dictionary":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  DATA_DICTIONARY_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(DATA_DICTIONARY_BUTTON);
              FilesHelper.waitForFileToDownload(DEUTSCH_DATA_DICTIONARY_FILE_PATH, 30);
              break;
            case "Deutsch Data Protection Dictionary":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  DATA_PROTECTION_DICTIONARY_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(DATA_PROTECTION_DICTIONARY_BUTTON);
              FilesHelper.waitForFileToDownload(DEUTSCH_DATA_PROTECTION_DICTIONARY_FILE_PATH, 30);
              break;
            default:
              throw new Exception("No XLSX path provided!");
          }
        });

    When(
        "^I validate data from downloaded XLSX ([^\"]*) file$",
        (String dictionaryName) -> {
          switch (dictionaryName) {
            case "Data Protection Dictionary":
              readXlsxDictionaryFile(DATA_PROTECTION_DICTIONARY_FILE_PATH);
              break;
            case "Data Dictionary":
              readXlsxDictionaryFile(DATA_DICTIONARY_FILE_PATH);
              break;
            case "Deutsch Data Dictionary":
              readXlsxDictionaryFile(DEUTSCH_DATA_DICTIONARY_FILE_PATH);
              break;
            case "Deutsch Data Protection Dictionary":
              readXlsxDictionaryFile(DEUTSCH_DATA_PROTECTION_DICTIONARY_FILE_PATH);
              break;
            default:
              throw new Exception("No XLSX path provided!");
          }
        });
    When(
        "^I check if Data Dictionary contains entries name in English$",
        () -> checkNameOfTheTabsMatchFieldIdEntries(DATA_DICTIONARY_FILE_PATH));
    When(
        "^I check if Data Dictionary contains sheets names in ([^\"]*)$",
        (String language) -> checkNameOfSheetsAreInGerman(DATA_DICTIONARY_FILE_PATH, language));
    Then(
        "^I delete ([^\"]*) downloaded file from About Directory$",
        (String dictionaryName) -> {
          switch (dictionaryName) {
            case "Data Protection Dictionary":
              FilesHelper.deleteFile(DATA_PROTECTION_DICTIONARY_FILE_PATH);
              break;
            case "Data Dictionary":
              FilesHelper.deleteFile(DATA_DICTIONARY_FILE_PATH);
              break;
            case "Deutsch Data Dictionary":
              FilesHelper.deleteFile(DEUTSCH_DATA_DICTIONARY_FILE_PATH);
              break;
            case "Deutsch Data Protection Dictionary":
              FilesHelper.deleteFile(DEUTSCH_DATA_PROTECTION_DICTIONARY_FILE_PATH);
              break;
            case "Case Classification Html":
              FilesHelper.deleteFile(CASE_CLASSIFICATION_HTML_FILE_PATH);
              break;
            default:
              throw new Exception("No XLSX path provided!");
          }
        });

    When(
        "^I check if last downloaded XLSX from About Directory content is translated into ([^\"]*)$",
        (String language) -> {
          String[] receivedWordsFromArray = {
            xlsxFileContentList.get(3), xlsxFileContentList.get(17)
          };
          for (String word : receivedWordsFromArray) {
            LanguageDetectorHelper.checkLanguage(word, language);
          }
        });

    When(
        "^I click on Sormas version in About directory and i get redirected to github$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SORMAS_VERSION_LINK);
          String hrefValue = "";
          try {
            hrefValue = webDriverHelpers.getAttributeFromWebElement(SORMAS_VERSION_LINK, "href");
          } catch (Exception any) {
            Assert.fail("Sormas version it doesn't  contain a clickable hyperlink");
          }
          Assert.assertTrue(
              hrefValue.contains("github.com/sormas-foundation/SORMAS-Project"),
              "Sormas version hyperlink value is wrong");
          webDriverHelpers.clickOnWebElementBySelector(SORMAS_VERSION_LINK);
          TimeUnit.SECONDS.sleep(1);
          String link =
              webDriverHelpers.getAttributeFromWebElement(SORMAS_VERSION_HYPERLINK_TARGET, "href");
          webDriverHelpers.switchToOtherWindow();
          softly.assertEquals(
              link, webDriverHelpers.returnURL(), "Sormas version link is not the correct");
          softly.assertAll();
          webDriverHelpers.closeActiveWindow();
        });

    When(
        "^I click on What's new in About directory and i get redirected to Sormas what's new page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(WHATS_NEW_HYPERLINK);
          webDriverHelpers.clickOnWebElementBySelector(WHATS_NEW_HYPERLINK);
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.switchToOtherWindow();
          softly.assertTrue(
              webDriverHelpers.returnURL().contains(RELEASE_PAGE),
              "What's new link is not the correct");
          softly.assertAll();
          webDriverHelpers.closeActiveWindow();
        });
    When(
        "^I click on Official SORMAS Website in About directory and i get redirected to the offical Sormas website$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OFFICIAL_SORMAS_WEBSITE_HYPERLINK);
          webDriverHelpers.clickOnWebElementBySelector(OFFICIAL_SORMAS_WEBSITE_HYPERLINK);
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.switchToOtherWindow();
          softly.assertEquals(
              "https://sormas.org/",
              webDriverHelpers.returnURL(),
              "Official sormas website link is not correct");
          softly.assertAll();
          webDriverHelpers.closeActiveWindow();
        });
    When(
        "^I click on SORMAS Github in About directory and i get redirected to github page of sormas$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SORMAS_GITHUB_HYPERLINK);
          webDriverHelpers.clickOnWebElementBySelector(SORMAS_GITHUB_HYPERLINK);
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.switchToOtherWindow();
          softly.assertEquals(
              "https://github.com/sormas-foundation/SORMAS-Project",
              webDriverHelpers.returnURL(),
              "Sormas github link is not correct");
          softly.assertAll();
          webDriverHelpers.closeActiveWindow();
        });
    When(
        "^I click on Full Changelog in About directory and i get redirected to github project release page of sormas$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FULL_CHANGELOG_HYPERLINK);
          webDriverHelpers.clickOnWebElementBySelector(FULL_CHANGELOG_HYPERLINK);
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.switchToOtherWindow();
          softly.assertEquals(
              RELEASE_PAGE,
              webDriverHelpers.returnURL(),
              "Sormas full changelog link is not correct");
          softly.assertAll();
          webDriverHelpers.closeActiveWindow();
        });
    When(
        "^I click on Case Classification Rules hyperlink and download HTML file in About directory$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CASE_CLASSIFICATION_RULES_HYPERLINK, 15);
          webDriverHelpers.clickOnWebElementBySelector(CASE_CLASSIFICATION_RULES_HYPERLINK);
        });
    When(
        "I check if Data Dictionary in {string} record has no {string} as a disease",
        (String recordName, String disease) -> {
          softly.assertFalse(
              readXlsxFile(DATA_DICTIONARY_FILE_PATH, recordName, disease),
              disease + " exists in " + recordName);
          softly.assertAll();
        });

    Then(
        "^I check that Surveillance Dashboard header is correctly displayed in ([^\"]*) language$",
        (String language) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SURVEILLANCE_DASHBOARD_NAME, 30);
          LanguageDetectorHelper.checkLanguage(
              webDriverHelpers.getTextFromWebElement(SURVEILLANCE_DASHBOARD_NAME), language);
        });

    Then(
        "^I check that the Survnet Converter version is not an unavailable on About directory$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SURVNET_CONVERTER_VERSION_LABEL);

          softly.assertFalse(
              webDriverHelpers
                  .getTextFromWebElement(SURVNET_CONVERTER_VERSION_LABEL)
                  .contains("SORMAS (unavailable)"),
              "The Sormas Converter is unavailable");
          softly.assertAll();
        });

    And(
        "^I check that the Survnet Converter version is correctly displayed on About directory$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SORMAS_VERSION_LINK);
          String sormasVersion =
              webDriverHelpers.getTextFromWebElement(SORMAS_VERSION_LINK).substring(9, 15);

          softly.assertTrue(
              webDriverHelpers
                  .getTextFromWebElement(SURVNET_CONVERTER_VERSION_LABEL)
                  .contains(sormasVersion),
              "Survnet Converter has wrong version");
          softly.assertAll();
        });
  }

  @SneakyThrows
  private static boolean readXlsxFile(String fileName, String recordName, String disease) {
    List<String> diseaseList = new ArrayList<String>();
    try {
      Workbook workbook = FilesHelper.getExcelFile(fileName);
      Sheet sheet = workbook.getSheetAt(0);
      for (Row row : sheet) {
        for (Cell cell : row) {
          if (cell.getStringCellValue().equals(recordName)) {
            String[] items = row.getCell(8).toString().split("\\s*,\\s*");
            for (String item : items) diseaseList.add(item);
          }
        }
      }
    } catch (Exception any) {
      throw new Exception(String.format("Unable to read Excel File due to: %s", any.getMessage()));
    }
    if (diseaseList.contains(disease)) return true;
    else return false;
  }

  @SneakyThrows
  private static void readXlsxDictionaryFile(String fileName) {
    try {
      FilesHelper.validateFileIsNotEmpty(fileName);
      Workbook workbook = FilesHelper.getExcelFile(fileName);
      Sheet datatypeSheet = workbook.getSheetAt(0);
      Iterator<Row> iterator = datatypeSheet.iterator();

      while (iterator.hasNext()) {

        Row currentRow = iterator.next();
        Iterator<Cell> cellIterator = currentRow.iterator();

        while (cellIterator.hasNext()) {

          Cell currentCell = cellIterator.next();
          if (currentCell.getCellType() == CellType.STRING) {
            xlsxFileContentList.add(currentCell.getStringCellValue() + ",");
          } else if (currentCell.getCellType() == CellType.NUMERIC) {
            xlsxFileContentList.add(currentCell.getNumericCellValue() + ",");
          }
        }
      }
      log.info("All data is read properly from chosen xlsx file");
    } catch (Exception any) {
      throw new Exception(String.format("Unable to read Excel File due to: %s", any.getMessage()));
    }
  }

  @SneakyThrows
  private static void checkNameOfTheTabsMatchFieldIdEntries(String fileName) {
    try {
      FilesHelper.validateFileIsNotEmpty(fileName);
      Workbook workbook = FilesHelper.getExcelFile(fileName);
      for (int i = 0; i < 5; i++) {
        Sheet datatypeSheet = workbook.getSheetAt(i);
        Iterator<Row> iterator = datatypeSheet.iterator();
        Row currentRow = iterator.next();
        currentRow = iterator.next();
        Iterator<Cell> cellIterator = currentRow.iterator();
        Cell currentCell = cellIterator.next();
        currentCell.getStringCellValue();
        System.out.println(currentCell.getStringCellValue());
        assertHelpers.assertWithPoll20Second(
            () ->
                Assert.assertEquals(
                    datatypeSheet.getSheetName().replaceAll("\\s+", "").toUpperCase(),
                    currentCell
                        .getStringCellValue()
                        .substring(0, datatypeSheet.getSheetName().replaceAll("\\s+", "").length())
                        .toUpperCase(),
                    "Name of sheet is not visible in cell"));
      }
      log.info("All data is read properly from chosen xlsx file");
    } catch (Exception any) {
      throw new Exception(String.format("Unable to read Excel File due to: %s", any.getMessage()));
    }
  }

  @SneakyThrows
  private static void checkNameOfSheetsAreInGerman(String fileName, String language) {
    try {
      FilesHelper.validateFileIsNotEmpty(fileName);
      Workbook workbook = FilesHelper.getExcelFile(fileName);
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        System.out.println(workbook.getSheetName(i));
        LanguageDetectorHelper.checkLanguage(workbook.getSheetName(i), language);
      }
      log.info("All data is read properly from chosen xlsx file");
    } catch (Exception any) {
      throw new Exception(String.format("Unable to read Excel File due to: %s", any.getMessage()));
    }
  }
}
