package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.AboutPage.DATA_DICTIONARY_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class AboutDirectorySteps implements En {
    public static final String userDirPath = System.getProperty("user.dir");

  @Inject
  public AboutDirectorySteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I click on Data Dictionary button$",
        () -> {
          webDriverHelpers.waitForPageLoaded();

          webDriverHelpers.clickOnWebElementBySelector(DATA_DICTIONARY_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DATA_DICTIONARY_BUTTON, 50);
          TimeUnit.SECONDS.sleep(5);
        });
  }

  public static void ReadXMLFilr()
  {
      try {

          FileInputStream excelFile = new FileInputStream(new File(userDirPath + "//downloads//sormas_datenbeschreibungsverzeichnis_2022-03-18_ (1)"));
          Workbook workbook = new XSSFWorkbook(excelFile);
          Sheet datatypeSheet = workbook.getSheetAt(0);
          Iterator<Row> iterator = datatypeSheet.iterator();

          while (iterator.hasNext()) {

              Row currentRow = iterator.next();
              Iterator<Cell> cellIterator = currentRow.iterator();

              while (cellIterator.hasNext()) {

                  Cell currentCell = cellIterator.next();
                  //getCellTypeEnum shown as deprecated for version 3.15
                  //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                  if (currentCell.getCellTypeEnum() == CellType.STRING) {
                      System.out.print(currentCell.getStringCellValue() + "--");
                  } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                      System.out.print(currentCell.getNumericCellValue() + "--");
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
