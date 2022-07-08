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
  public static final By LINK_CASE_BUTTON = By.cssSelector("#linkImmunizationToCaseButton");
  public static final By SEARCH_SPECIFIC_CASE_INPUT =
      By.xpath("//div[@class='v-slot v-slot-vspace-2'][2]//input");
  public static final By SEARCH_SPECIFIC_CASE_BUTTON =
      By.cssSelector(".popupContent #actionConfirm");
  public static final By LINK_CASE_CASE_FOUND_HEADER = By.xpath("//*[text()='Case found']");
  public static final By OKAY_LINK_CASE_BUTTON = By.cssSelector(".popupContent #actionOkay");
  public static final By OPEN_CASE_BUTTON = By.id("openLinkedCaseToImmunizationButton");
  public static final By DATE_OF_FIRST_POSITIVE_TEST_RESULT_INPUT =
      By.cssSelector("[id='positiveTestResultDate'] input");
  public static final By DATE_OF_RECOVERY_INPUT = By.cssSelector("[id='recoveryDate'] input");
}
