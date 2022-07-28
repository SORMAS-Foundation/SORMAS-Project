package org.sormas.e2etests.pages.application.mSers;

import org.openqa.selenium.By;

public class CreateNewAggreagateReportPage {
  public static final By REGION_COMBOBOX_POPUP =
      By.xpath(
          "//div[contains(@class,\"v-window v-widget\")]//span[text()=\"region\"]/..//following-sibling::div//input");
  public static final By DISTRICT_COMBOBOX_POPUP =
      By.xpath(
          "//div[contains(@class,\"v-window v-widget\")]//span[text()=\"district\"]/..//following-sibling::div//input");

  public static By getDeathInputByDisease(String disease) {
    return By.xpath(String.format("//div[text()='%s']/../..//input[@id=\"deaths\"]", disease));
  }

  public static By getLabConfirmationsInputByDisease(String disease) {
    return By.xpath(
        String.format("//div[text()='%s']/../..//input[@id=\"labConfirmations\"]", disease));
  }

  public static By getCasesInputByDisease(String disease) {
    return By.xpath(String.format("//div[text()='%s']/../..//input[@id=\"newCases\"]", disease));
  }

  public static final By YEAR_COMBOBOX_POPUP =
      By.xpath("(//div[contains(@class,\"v-window v-widget\")]//div[@role='combobox'])[1]//div");
  public static final By YEAR_INPUT_POPUP =
      By.xpath("(//div[contains(@class,\"v-window v-widget\")]//div[@role='combobox'])[1]//input");
  public static final By EPI_WEEK_COMBOBOX_POPUP =
      By.xpath("(//div[contains(@class,\"v-window v-widget\")]//div[@role='combobox'])[2]//div");
  public static final By EPI_WEEK_INPUT_POPUP =
      By.xpath("(//div[contains(@class,\"v-window v-widget\")]//div[@role='combobox'])[2]//input");
  public static final By WEEK_RADIOBUTTON =
      By.cssSelector("[class='v-radiobutton v-select-option']");
  public static final By DELETE_AGGREGATED_REPORT_BUTTON = By.id("actionDelete");
}
