package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.AboutPage.*;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.LANGUAGE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.SAVE_BUTTON;

import com.detectlanguage.DetectLanguage;
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
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.sormas.e2etests.pages.application.dashboard.Surveillance.SurveillanceDashboardPage;
import org.testng.asserts.SoftAssert;

@Slf4j
public class AboutDirectorySteps implements En {
  public static final List<String> xlsxFileContentList = new ArrayList<>();
  public static String language;
  public static final String DATA_PROTECTION_DICTIONARY_FILE_PATH =
      String.format("sormas_data_protection_dictionary_%s_.xlsx", LocalDate.now());
  public static final String DATA_DICTIONARY_FILE_PATH =
      String.format("sormas_data_dictionary_%s_.xlsx", LocalDate.now());
  public static final String DEUTSCH_DATA_DICTIONARY_FILE_PATH =
      String.format("sormas_datenbeschreibungsverzeichnis_%s_.xlsx", LocalDate.now());
  public static final String CASE_CLASSIFICATION_HTML_FILE_PATH = "classification_rules.html";

  @Inject
  public AboutDirectorySteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {

    When(
        "I check that current Sormas version is shown on About directory page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SORMAS_VERSION_LINK);
        });

    When(
        "I select {string} language from Combobox in User settings",
        (String chosenLanguage) -> {
          language = chosenLanguage;
          webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, chosenLanguage);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I set on default language as English in User settings",
        () -> {
          String languageDerivedFromUserChoose = language;
          String defaultLanguage = "";
          switch (languageDerivedFromUserChoose) {
            case "Fran\u00E7ais":
              defaultLanguage = "Anglais";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
            case "Fran\u00E7ais (Suisse)":
              defaultLanguage = "Anglais";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
            case "Deutsch":
              defaultLanguage = "English";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
            case "Deutsch (Schweiz)":
              defaultLanguage = "English";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
            case "Espa\u00F1ol (Ecuador)":
              defaultLanguage = "Ingl\u00E9s";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
            case "Espa\u00F1ol (Cuba)":
              defaultLanguage = "English";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
          }
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(5);
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
            default:
              throw new Exception("No XLSX path provided!");
          }
        });

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
            case "Case Classification Html":
              FilesHelper.deleteFile(CASE_CLASSIFICATION_HTML_FILE_PATH);
              break;
            default:
              throw new Exception("No XLSX path provided!");
          }
        });

    When(
        "I detect and check language that was defined in User Settings for XLSX file content",
        () -> {
          DetectLanguage.apiKey = "5e184341083ac27cad1fd06d6e208302";
          String[] receivedWordsFromArray = {
            xlsxFileContentList.get(16), xlsxFileContentList.get(17)
          };
          for (String word : receivedWordsFromArray) {
            String chosenUserLanguage = language.toLowerCase().substring(0, 2);
            String detectedLanguage = DetectLanguage.simpleDetect(word);
            softly.assertEquals(
                chosenUserLanguage,
                detectedLanguage,
                "Language in xlsx file is different then chosen bu User");
            softly.assertAll();
          }
        });

    When(
        "^I click on Sormas version in About directory and i get redirected to github$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SORMAS_VERSION_LINK);
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
              webDriverHelpers
                  .returnURL()
                  .contains("https://github.com/hzi-braunschweig/SORMAS-Project/releases/tag"),
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
              "https://github.com/hzi-braunschweig/SORMAS-Project",
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
              "https://github.com/hzi-braunschweig/SORMAS-Project/releases",
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
        "^I check that Surveillance Dashboard header is correctly displayed in Urdu language$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.isElementVisibleWithTimeout(
              SurveillanceDashboardPage.SURVEILLANCE_DASHBOARD_NAME_URDU, 5);
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
}
