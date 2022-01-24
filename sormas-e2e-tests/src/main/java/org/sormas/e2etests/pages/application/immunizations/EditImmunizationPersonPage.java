package org.sormas.e2etests.pages.application.immunizations;

import org.openqa.selenium.By;

public class EditImmunizationPersonPage {

  public static final By FIRST_NAME_INPUT = By.cssSelector("#firstName");
  public static final By LAST_NAME_INPUT = By.cssSelector("#lastName");
  public static final By PRESENT_CONDITION_INPUT = By.cssSelector("#presentCondition input");
  public static final By SEX_INPUT = By.cssSelector("#sex input");
  public static final By EMAIL_FIELD =
      By.xpath(
          "//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Email')]/../following-sibling::td//div");
  public static final By PHONE_FIELD =
      By.xpath(
          "//tr[contains(@class, 'v-table-row')]//div[@class='v-table-cell-wrapper' and contains(text(),'Phone')]/../following-sibling::td//div");
  public static final By DATE_OF_BIRTH_YEAR_INPUT = By.cssSelector("#birthdateYYYY input");
  public static final By DATE_OF_BIRTH_MONTH_INPUT = By.cssSelector("#birthdateMM input");
  public static final By DATE_OF_BIRTH_DAY_INPUT = By.cssSelector("#birthdateDD input");
}
