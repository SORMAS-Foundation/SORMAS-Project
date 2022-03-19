package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.AboutPage.DATA_DICTIONARY_BUTTON;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.LANGUAGE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.SAVE_BUTTON;

import com.detectlanguage.DetectLanguage;
import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class AboutDirectorySteps implements En {
  public static final String userDirPath = System.getProperty("user.dir");
  public static final List<String> xlsxFileContentList = new ArrayList<>();
  public static String language;

  @Inject
  public AboutDirectorySteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {

    When(
        "I select {string} language from Combobox in User settings",
        (String chosenLanguage) -> {
          language = chosenLanguage;
          webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, chosenLanguage);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "I set on default language in User settings",
        () -> {
          String defaultLanguage = "";
          System.out.println("Settingn language" + language);
          switch (language) {
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
            case "Italiano":
              defaultLanguage = "Inglese";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
            case "Italiano (Svizzera)":
              defaultLanguage = "Inglese";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
            case "Suomi":
              defaultLanguage = "Englanti";
              webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, defaultLanguage);
              break;
          }
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
    When(
        "I click on Data Dictionary hyperlink and download XLSX file in About directory",
        () -> {
          webDriverHelpers.waitForPageLoaded();

          webDriverHelpers.clickOnWebElementBySelector(DATA_DICTIONARY_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DATA_DICTIONARY_BUTTON, 50);
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "^I read data from downloaded XLSX file$",
        () -> {
          ReadXMLFile();
        });

    When(
        "I detect language for XLSX file content",
        () -> {
          DetectLanguage.apiKey = "5e184341083ac27cad1fd06d6e208302";

          String[] receivedWordsArray = {xlsxFileContentList.get(16), xlsxFileContentList.get(17)};
          for (String word : receivedWordsArray) {
            String chosenUserLanguage = language.toLowerCase().substring(0, 2);
            System.out.println("Choosen language: " + chosenUserLanguage);
            String detectedLanguage = DetectLanguage.simpleDetect(word);
            System.out.println("Detected language: " + detectedLanguage);
            softly.assertEquals(
                chosenUserLanguage,
                detectedLanguage,
                "Language in xlsx file is different then chosen bu User");
            softly.assertAll();
          }
        });

    When(
        "I delete exported file from About Directory",
        () -> {
          File toDelete =
              new File(
                  userDirPath
                      + "//downloads//sormas_datenbeschreibungsverzeichnis_"
                      + LocalDate.now()
                      + "_.xlsx");
          toDelete.deleteOnExit();
        });
  }

  public static void ReadXMLFile() {
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
        System.out.println();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
