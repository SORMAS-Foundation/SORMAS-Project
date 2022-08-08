package org.sormas.e2etests.steps.web.application.mSers;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.DELETE_POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditContactsPage.RESPONSIBLE_REGION_INPUT;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.DELETE_ICON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.DISPLAY_ONLY_DUPLICATE_REPORTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.EDIT_ICON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.EPI_WEEK_FROM_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.EPI_WEEK_TO_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.NEW_AGGREGATE_REPORT_BUTTON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.REPORT_DATA_BUTTON;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.RESULT_IN_GRID;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_FROM_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_FROM_INPUT;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_TO_COMOBOX;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.YEAR_TO_INPUT;
import static org.sormas.e2etests.pages.application.mSers.MSersDirectoryPage.getEditButtonByIndex;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

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
        "I set Epi Year from filter to {string}",
        (String year) -> {
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clearComboboxInput(YEAR_FROM_INPUT);
          webDriverHelpers.selectFromCombobox(YEAR_FROM_COMOBOX, year);
        });
    When(
        "I set Epi Year to filter to {string}",
        (String year) -> {
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clearComboboxInput(YEAR_TO_INPUT);
          webDriverHelpers.selectFromCombobox(YEAR_TO_COMOBOX, year);
        });
    When(
        "I set Epi week from filter to {string}",
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
        "I set Epi week to filter to {string}",
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
  }
}
