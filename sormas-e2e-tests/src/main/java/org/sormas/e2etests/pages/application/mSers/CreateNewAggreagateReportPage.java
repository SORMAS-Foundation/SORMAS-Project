package org.sormas.e2etests.pages.application.mSers;

import org.openqa.selenium.By;

public class CreateNewAggreagateReportPage {
  public static final By REGION_COMBOBOX_POPUP =
      By.xpath(
          "//div[contains(@class,\"v-window v-widget\")]//span[text()=\"region\"]/..//following-sibling::div//input");
  public static final By DISTRICT_COMBOBOX_POPUP =
      By.xpath(
          "//div[contains(@class,\"v-window v-widget\")]//span[text()=\"district\"]/..//following-sibling::div//input");
  public static final By REGION_COMBOBOX_POPUP_DIV =
      By.xpath(
          "//div[contains(@class,\"v-window v-widget\")]//span[text()=\"region\"]/..//following-sibling::div//div");
  public static final By DISTRICT_COMBOBOX_POPUP_DIV =
      By.xpath(
          "//div[contains(@class,\"v-window v-widget\")]//span[text()=\"district\"]/..//following-sibling::div//div");
  public static final By DUPLICATE_DETECTION_TEXT =
      By.xpath(
          "//div[text()=\"Attention: Duplicate reports have been found for the above criteria. Diseases marked with red already have reports.\"]");

  public static By getDeathInputByDisease(String disease) {
    return By.xpath(String.format("//div[text()='%s']/../../..//input[@id=\"deaths\"]", disease));
  }

  public static By getLabConfirmationsInputByDisease(String disease) {
    return By.xpath(
        String.format("//div[text()='%s']/../../..//input[@id=\"labConfirmations\"]", disease));
  }

  public static By getCasesInputByDisease(String disease) {
    return By.xpath(String.format("//div[text()='%s']/../../..//input[@id=\"newCases\"]", disease));
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
  public static final By ERROR_MESSAGE = By.cssSelector(".v-Notification-caption");
  public static final By SNAKE_BITE_SUSPECTED_CASES_INPUT =
      By.xpath("(//div[text()=\"Snake Bite\"]/..//following-sibling::div//input)[1]");
  public static final By POPUP_HEADER = By.cssSelector(".popupContent .v-window-outerheader");
  public static final By POPUP_MESSAGE_WINDOW = By.cssSelector(".v-Notification-description");

  public static By getAgeGroupDiseaseByDiseaseAndAgeGroup(String disease, String ageGroup) {
    return By.xpath(
        String.format("//div[text()='%s']/following::div[text()='%s'][1]", disease, ageGroup));
  }

  public static final By getDeathInputByDiseaseAndAgeGroup(String disease, String ageGroup) {
    return By.xpath(
        String.format(
            "//div[text()='%s']//following::div[text()='%s'][1]/../following-sibling::div[@location='deaths']/div/input",
            disease, ageGroup));
  }

  public static final By getLabConfirmationsInputByDiseaseAndAgeGroup(
      String disease, String ageGroup) {
    return By.xpath(
        String.format(
            "//div[text()='%s']//following::div[text()='%s'][1]/../following-sibling::div[@location='labConfirmations']/div/input",
            disease, ageGroup));
  }

  public static final By getCasesInputByDiseaseAndAgeGroup(String disease, String ageGroup) {
    return By.xpath(
        String.format(
            "//div[text()='%s']//following::div[text()='%s'][1]/../following-sibling::div[@location='newCases']/div/input",
            disease, ageGroup));
  }
}
