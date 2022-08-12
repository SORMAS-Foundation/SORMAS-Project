package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.ADD_NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.AGE_AND_BIRTHDATE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DATE_OF_RECOVERY_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DISEASE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DISTRICT_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FIRST_IMMUNIZATION_ID_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FIRST_NAME_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_END_DATE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_ID_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_START_DATE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_STATUS_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.LAST_NAME_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.MANAGEMENT_STATUS_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.MEANS_OF_IMMUNIZATION_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.PERSON_ID_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.RESULTS_IN_GRID;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.SEX_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.TYPE_OF_LAST_VACCINE_COLUMN_HEADER;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import org.openqa.selenium.By;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class ImmunizationDirectorySteps implements En {

  @Inject
  public ImmunizationDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      ApiState apiState,
      AssertHelpers assertHelpers) {

    When(
        "^I click on the NEW IMMUNIZATION button$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(70);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ADD_NEW_IMMUNIZATION_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(ADD_NEW_IMMUNIZATION_BUTTON);
        });

    When(
        "I open first immunization from grid from Immunization tab",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_IMMUNIZATION_ID_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_IMMUNIZATION_ID_BUTTON);
        });

    When(
        "^I navigate to last created immunization by API via URL$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.IMMUNIZATIONS_BUTTON);
          final String eventUuid = apiState.getCreatedImmunization().getUuid();
          final String eventLinkPath = "/sormas-ui/#!immunizations/data/";
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + eventLinkPath + eventUuid);
          webDriverHelpers.waitForPageLoaded();
        });

    And(
        "^I check the grid for mandatory columns on the Immunization directory page$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              IMMUNIZATION_ID_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PERSON_ID_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_NAME_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(LAST_NAME_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISEASE_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(AGE_AND_BIRTHDATE_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEX_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DISTRICT_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              MEANS_OF_IMMUNIZATION_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(MANAGEMENT_STATUS_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(IMMUNIZATION_STATUS_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              IMMUNIZATION_START_DATE_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              IMMUNIZATION_END_DATE_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              TYPE_OF_LAST_VACCINE_COLUMN_HEADER);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_RECOVERY_COLUMN_HEADER);
        });

    And(
        "^I sort all rows by \"([^\"]*)\" in Immunization Directory$",
        (String option) -> {
          switch (option) {
            case "Immunization ID":
              webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_ID_COLUMN_HEADER);
              break;
            case "Person ID":
              webDriverHelpers.clickOnWebElementBySelector(PERSON_ID_COLUMN_HEADER);
              break;
            case "First name":
              webDriverHelpers.clickOnWebElementBySelector(FIRST_NAME_COLUMN_HEADER);
              break;
            case "Last name":
              webDriverHelpers.clickOnWebElementBySelector(LAST_NAME_COLUMN_HEADER);
              break;
            case "Disease":
              webDriverHelpers.clickOnWebElementBySelector(DISEASE_COLUMN_HEADER);
              break;
            case "Age and birthdate":
              webDriverHelpers.clickOnWebElementBySelector(AGE_AND_BIRTHDATE_COLUMN_HEADER);
              break;
            case "Sex":
              webDriverHelpers.clickOnWebElementBySelector(SEX_COLUMN_HEADER);
              break;
            case "District":
              webDriverHelpers.clickOnWebElementBySelector(DISTRICT_COLUMN_HEADER);
              break;
            case "Means of immunization":
              webDriverHelpers.clickOnWebElementBySelector(MEANS_OF_IMMUNIZATION_COLUMN_HEADER);
              break;
            case "Management Status":
              webDriverHelpers.clickOnWebElementBySelector(MANAGEMENT_STATUS_COLUMN_HEADER);
              break;
            case "Immunization status":
              webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_STATUS_COLUMN_HEADER);
              break;
            case "Start date":
              webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_START_DATE_COLUMN_HEADER);
              break;
            case "End date":
              webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_END_DATE_COLUMN_HEADER);
              break;
            case "Type of last vaccine":
              webDriverHelpers.clickOnWebElementBySelector(TYPE_OF_LAST_VACCINE_COLUMN_HEADER);
              break;
            case "Date of recovery":
              webDriverHelpers.clickOnWebElementBySelector(DATE_OF_RECOVERY_COLUMN_HEADER);
              break;
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });

    And(
        "^I check that number of displayed immunization results in grid is more than (\\d+)$",
        (Integer expected) -> {
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(
                      webDriverHelpers.getNumberOfElements(RESULTS_IN_GRID) > expected,
                      "Number of results visible in grid different than expected"),
              10);
        });

    And(
        "^I check that the row number (\\d+) contains \"([^\"]*)\" in disease field$",
        (Integer rowNumber, String expectedDisease) -> {
          String actualDisease =
              webDriverHelpers.getTextFromWebElement(
                  By.xpath("//tbody//tr[" + rowNumber + "]//td[" + 5 + "]"));
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      actualDisease,
                      expectedDisease,
                      "Number of results visible in grid different than expected"),
              10);
        });
  }
}
