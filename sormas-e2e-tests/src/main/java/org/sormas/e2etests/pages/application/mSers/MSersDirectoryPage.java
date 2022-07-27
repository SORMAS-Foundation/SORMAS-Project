package org.sormas.e2etests.pages.application.mSers;

import org.openqa.selenium.By;

public class MSersDirectoryPage {
  public static final By NEW_AGGREGATE_REPORT_BUTTON =
      By.cssSelector("div #aggregateReportNewAggregateReport");
  public static final By YEAR_FROM_COMOBOX = By.cssSelector("#yearFrom > div");
  public static final By EPI_WEEK_FROM_COMOBOX = By.cssSelector("#epiWeekFrom > div");
  public static final By REPORT_DATA_BUTTON = By.cssSelector("#tab-aggregatereports-reportdata");
  public static final By RESULT_IN_GRID = By.xpath("//tr[contains(@class,'v-grid-row-has-data')]");

  public static By getEditButtonByIndex(int idx) {
    return By.xpath(
        String.format(
            "(//tr[contains(@class,'v-grid-row-has-data')]//span[@class=\"v-button-wrap\"])[%x]",
            idx));
  }
}
