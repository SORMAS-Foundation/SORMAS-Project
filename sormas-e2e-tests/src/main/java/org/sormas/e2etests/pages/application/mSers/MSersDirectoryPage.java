package org.sormas.e2etests.pages.application.mSers;

import org.openqa.selenium.By;

public class MSersDirectoryPage {
  public static final By NEW_AGGREGATE_REPORT_BUTTON =
      By.cssSelector("div #aggregateReportNewAggregateReport");
  public static final By YEAR_FROM_COMOBOX = By.cssSelector("#yearFrom > div");
  public static final By YEAR_TO_COMOBOX = By.cssSelector("#yearTo > div");
  public static final By YEAR_FROM_INPUT = By.cssSelector("#yearFrom input");
  public static final By YEAR_TO_INPUT = By.cssSelector("#yearTo input");
  public static final By EPI_WEEK_FROM_COMOBOX = By.cssSelector("#epiWeekFrom > div");
  public static final By EPI_WEEK_TO_COMOBOX = By.cssSelector("#epiWeekTo > div");
  public static final By REPORT_DATA_BUTTON = By.cssSelector("#tab-aggregatereports-reportdata");
  public static final By RESULT_IN_GRID = By.xpath("//tr[contains(@class,'v-grid-row-has-data')]");
  public static final By DISPLAY_ONLY_DUPLICATE_REPORTS_CHECKBOX =
      By.xpath("//label[text()=\"Display only duplicate reports\"]");
  public static final By DELETE_ICON = By.xpath("(//div[@class=\"component-wrap\"]//div)[1]");
  public static final By EDIT_ICON = By.xpath("(//div[@class=\"component-wrap\"]//div)[1]");
  public static final By DISEASE_COMBOBOX = By.cssSelector("#disease div");

  public static By getEditButtonByIndex(int idx) {
    return By.xpath(
        String.format(
            "(//tr[contains(@class,'v-grid-row-has-data')]//span[@class=\"v-button-wrap\"])[%x]",
            idx));
  }

  public static final By REGION_FILTER_COMBOBOX = By.cssSelector("#region div");
  public static final By DISTRICT_FILTER_COMBOBOX = By.cssSelector("#district div");
  public static final By AGGREGATED_REPORT_APPLY_FILTER_BUTTON =
      By.cssSelector("[id='actionApplyFilters']");
  public static final By FIRST_AGGREGATED_REPORT_EDIT_BUTTON =
      By.cssSelector(".v-grid-row-has-data div");

  public static By getElementFromFirstRowAndSpecificColumnByColumnNumber(int column) {
    return By.xpath(String.format("//tbody//tr[" + 1 + "]//td[" + column + "]"));
  }
}
