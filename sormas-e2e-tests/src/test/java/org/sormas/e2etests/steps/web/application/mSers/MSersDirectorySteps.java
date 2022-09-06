package org.sormas.e2etests.steps.web.application.mSers;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.CASE_CONTACT_EXPORT;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_REGION_INPUT;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.DELETE_ICON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.DISPLAY_ONLY_DUPLICATE_REPORTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.EDIT_ICON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.EPI_WEEK_FROM_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.EPI_WEEK_TO_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.FACILITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.GROUPING_COMBOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.GROUPING_COMBOBOX_INPUT;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.NEW_AGGREGATE_REPORT_BUTTON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.POINT_OF_ENTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.REPORT_DATA_BUTTON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.RESULT_IN_GRID;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.SHOW_ROWS_FOR_DISEASES_LABEL;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_FROM_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_FROM_INPUT;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_TO_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_TO_INPUT;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.getColumnSelectorByName;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.getEditButtonByIndex;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import cucumber.api.java8.En;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.web.AggregateReport;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class MSersDirectorySteps implements En {

  protected WebDriverHelpers webDriverHelpers;

  @Inject
  public MSersDirectorySteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check if Region combobox is set to {string} and is not editable on mSERS directory page",
        (String region) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(RESPONSIBLE_REGION_INPUT), region);
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_REGION_INPUT),
              "Region combobox is not disabled");
          softly.assertAll();
        });
    When(
        "I check if District combobox is set to {string} and is not editable on mSERS directory page",
        (String region) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(RESPONSIBLE_DISTRICT_INPUT), region);
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_DISTRICT_INPUT),
              "Region combobox is not disabled");
          softly.assertAll();
        });
    When(
        "^I click on the NEW AGGREGATE REPORT button$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NEW_AGGREGATE_REPORT_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(NEW_AGGREGATE_REPORT_BUTTON);
        });

    When(
        "I select ([^\"]*) disease from Disease combobox in mSers directory page",
        (String disease) -> webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease));
    When(
        "I select ([^\"]*) from Region combobox in mSers directory page",
        (String disease) -> webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, disease));
    When(
        "I select ([^\"]*) from District combobox in mSers directory page",
        (String disease) -> webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, disease));
    When(
        "I select ([^\"]*) from Facility combobox in mSers directory page",
        (String disease) -> webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, disease));
    When(
        "I select ([^\"]*) from Point Of Entry combobox in mSers directory page",
        (String disease) -> webDriverHelpers.selectFromCombobox(POINT_OF_ENTRY_COMBOBOX, disease));
    When(
        "I click to Export aggregate report",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_CONTACT_EXPORT);
          String fileName = "sormas_aggregate_reports_" + LocalDate.now() + "_.csv";
          FilesHelper.waitForFileToDownload(fileName, 30);
        });
    When(
        "I check if exported aggregate report for last created report is correct",
        () -> {
          String fileName = "./downloads/sormas_aggregate_reports_" + LocalDate.now() + "_.csv";
          AggregateReport reader = parseOneDiseaseExport(fileName);
          softly.assertEquals(
              reader.getAcuteViralHepatitisCases(),
              CreateNewAggregateReportSteps.report.getAcuteViralHepatitisCases(),
              "Cases for ARI are different!");
          softly.assertAll();
        });
      When(
              "I delete exported report",
              () -> {
                  String fileName = "sormas_aggregate_reports_" + LocalDate.now() + "_.csv";
                  FilesHelper.deleteFile(fileName);
              });
    When(
        "^I click on checkbox to display only duplicate reports$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.clickOnWebElementBySelector(DISPLAY_ONLY_DUPLICATE_REPORTS_CHECKBOX);
        });

    When(
        "^I check if there are delete and edit buttons for report and duplicates in the grid$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(DELETE_ICON, 5),
              "Delete icon is not visible");
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(EDIT_ICON, 5),
              "Edit icon is not visible");
          softly.assertAll();
        });
    When(
        "I set Epi Year from filter to {string} in mSers directory page",
        (String year) -> {
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clearComboboxInput(YEAR_FROM_INPUT);
          webDriverHelpers.selectFromCombobox(YEAR_FROM_COMOBOX, year);
        });
    When(
        "I set Epi Year to filter to {string} in mSers directory page",
        (String year) -> {
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clearComboboxInput(YEAR_TO_INPUT);
          webDriverHelpers.selectFromCombobox(YEAR_TO_COMOBOX, year);
        });
    When(
        "I set Epi week from filter to {string} in mSers directory page",
        (String year) -> {
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.selectFromCombobox(EPI_WEEK_FROM_COMOBOX, year);
        });
    When(
        "I delete first duplicated result in grid",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DELETE_ICON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DELETE_POPUP_YES_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_POPUP_YES_BUTTON);
        });
    When(
        "I set Epi week to filter to {string} in mSers directory page",
        (String year) -> {
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.selectFromCombobox(EPI_WEEK_TO_COMOBOX, year);
        });
    When(
        "I click to edit {int} result in mSers directory page",
        (Integer ide) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getEditButtonByIndex(ide));
          webDriverHelpers.doubleClickOnWebElementBySelector(getEditButtonByIndex(ide));
        });
    When(
        "I navigate to Report data tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(REPORT_DATA_BUTTON));
    When(
        "I click Show 0-rows for grouping checkbox",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHOW_ROWS_FOR_DISEASES_LABEL));
    When(
        "I check if there number of results in grid in mSers directory is {int}",
        (Integer expected) ->
            assertHelpers.assertWithPoll(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(RESULT_IN_GRID),
                        expected.intValue(),
                        "Number of results visible in grid different than expected"),
                10));
    When(
        "I check that number of results in grid in mSers directory greater than {int}",
        (Integer expected) -> {
          softly.assertTrue(
              webDriverHelpers.getNumberOfElements(RESULT_IN_GRID) > expected,
              "There are less results than expected");
          softly.assertAll();
        });

    When(
        "I check that ([^\"]*) filter is visible in mSers directory",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          boolean elementVisible;
          switch (option) {
            case "Grouping":
              selector = GROUPING_COMBOBOX;
              break;
            case "Region":
              selector = REGION_COMBOBOX;
              break;
            case "District":
              selector = DISTRICT_COMBOBOX;
              break;
            case "Facility":
              selector = FACILITY_COMBOBOX;
              break;
            case "Point of Entry":
              selector = POINT_OF_ENTRY_COMBOBOX;
              break;
            case "Disease":
              selector = DISEASE_COMBOBOX;
              break;
            case "Epi Year from":
              selector = YEAR_FROM_COMOBOX;
              break;
            case "Epi Week from":
              selector = EPI_WEEK_FROM_COMOBOX;
              break;
            case "Epi Year to":
              selector = YEAR_TO_COMOBOX;
              break;
            case "Epi Week to":
              selector = EPI_WEEK_TO_COMOBOX;
              break;
          }
          elementVisible = webDriverHelpers.isElementVisibleWithTimeout(selector, 2);
          softly.assertTrue(elementVisible, option + " is not visible!");
          softly.assertAll();
        });

    When(
        "I check that ([^\"]*) is visible as a column header in mSers directory",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(getColumnSelectorByName(option), 2),
              option + " is not visible!");
          softly.assertAll();
        });

    When(
        "I select {string} from Grouping combobox in mSers directory",
        (String year) -> {
          webDriverHelpers.clearComboboxInput(GROUPING_COMBOBOX_INPUT);
          webDriverHelpers.selectFromCombobox(GROUPING_COMBOBOX, year);
        });
  }

  public AggregateReport parseOneDiseaseExport(String fileName) {
    List<String[]> r = null;
    String[] values = new String[] {};
    AggregateReport builder = null;
    CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
    try (CSVReader reader =
        new CSVReaderBuilder(new FileReader(fileName))
            .withCSVParser(csvParser)
            .withSkipLines(2) // parse only data
            .build()) {
      r = reader.readAll();
    } catch (IOException e) {
      log.error("IOException parseOneDiseaseExport: {}", e.getCause());
    } catch (CsvException e) {
      log.error("CsvException parseOneDiseaseExport: {}", e.getCause());
    }
    try {
      for (int i = 0; i < r.size(); i++) {
        values = r.get(i);
      }
      builder =
          AggregateReport.builder()
              .year(values[3])
              .epiWeek(values[4])
              .acuteViralHepatitisCases(Integer.parseInt(values[6]))
              .build();
    } catch (NullPointerException e) {
      log.error("Null pointer exception parseOneDiseaseExport: {}", e.getCause());
    }
    return builder;
  }
}
