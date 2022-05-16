package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.AboutPage.CASE_CLASSIFICATION_RULES_HYPERLINK;
import static org.sormas.e2etests.pages.application.AboutPage.DATA_DICTIONARY_BUTTON;
import static org.sormas.e2etests.pages.application.AboutPage.FULL_CHANGELOG_HYPERLINK;
import static org.sormas.e2etests.pages.application.AboutPage.OFFICIAL_SORMAS_WEBSITE_HYPERLINK;
import static org.sormas.e2etests.pages.application.AboutPage.SORMAS_GITHUB_HYPERLINK;
import static org.sormas.e2etests.pages.application.AboutPage.SORMAS_VERSION_HYPERLINK;
import static org.sormas.e2etests.pages.application.AboutPage.SORMAS_VERSION_HYPERLINK_TARGET;
import static org.sormas.e2etests.pages.application.AboutPage.SORMAS_VERSION_LINK;
import static org.sormas.e2etests.pages.application.AboutPage.WHATS_NEW_HYPERLINK;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.LANGUAGE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.SAVE_BUTTON;

import com.detectlanguage.DetectLanguage;
import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sormas.e2etests.common.MoreResources;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class AboutDirectorySteps implements En {
  public static final String userDirPath = System.getProperty("user.dir");
  public static final List<String> xlsxFileContentList = new ArrayList<>();
  public static String language;
  private static final Logger log = LoggerFactory.getLogger(MoreResources.class);

  @Inject
  public AboutDirectorySteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {

    When(
        "I check that current Sormas version is shown on About directory page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SORMAS_VERSION_HYPERLINK);
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
        "I click on Data Dictionary hyperlink and download XLSX file in About directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DATA_DICTIONARY_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DATA_DICTIONARY_BUTTON, 50);
          TimeUnit.SECONDS.sleep(3); // waiting for DATA_DICTIONARY_BUTTON
        });

    When(
        "^I read data from downloaded XLSX Data Dictionary file$",
        () -> {
          readXlsxFile();
          TimeUnit.SECONDS.sleep(5); // waiting for xlsx file is read
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
        "I delete exported xlsx file from user downloads directory",
        () -> {
          File toDelete =
              new File(
                  userDirPath
                      + "//downloads//sormas_datenbeschreibungsverzeichnis_"
                      + LocalDate.now()
                      + "_.xlsx");
          toDelete.deleteOnExit();
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
          webDriverHelpers.clickOnWebElementBySelector(CASE_CLASSIFICATION_RULES_HYPERLINK);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CASE_CLASSIFICATION_RULES_HYPERLINK, 50);
        });
    When(
        "^I delete the downloaded Case Classification Rules html and Data Dictionary xlsx file from download directory$",
        () -> {
          File html = new File(userDirPath + "//downloads//classification_rules.html");
          File xlsx =
              new File(
                  userDirPath
                      + "//downloads//sormas_data_dictionary_"
                      + LocalDate.now()
                      + "_.xlsx");
          html.deleteOnExit();
          xlsx.deleteOnExit();
        });
  }

  private static void readXlsxFile() {
    try {
      FileInputStream excelFile =
          new FileInputStream(
              new File(
                  userDirPath
                      + "//downloads//sormas_datenbeschreibungsverzeichnis_"
                      + LocalDate.now()
                      + "_.xlsx"));
      Workbook workbook = new XSSFWorkbook(excelFile);
      Sheet datatypeSheet = workbook.getSheetAt(0);
      Iterator<Row> iterator = datatypeSheet.iterator();

      while (iterator.hasNext()) {

        Row currentRow = iterator.next();
        Iterator<Cell> cellIterator = currentRow.iterator();

        while (cellIterator.hasNext()) {

          Cell currentCell = cellIterator.next();
          if (currentCell.getCellTypeEnum() == CellType.STRING) {
            xlsxFileContentList.add(currentCell.getStringCellValue() + ",");
          } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
            xlsxFileContentList.add(currentCell.getNumericCellValue() + ",");
          }
        }
      }
      log.info("All data is read properly from chosen xlsx file");
    } catch (IOException e) {
      log.error("Exception caught: File not found", e);
    }
  }
}
