package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.ADD_NEW_IMMUNIZATION_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.AGE_AND_BIRTHDATE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.COMMUNITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DATA_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DATE_FROM_CALENDAR_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DATE_OF_RECOVERY_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DATE_TO_CALENDAR_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DISEASE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DISTRICT_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.DISTRICT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FACILITY_CATEGORY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FACILITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FACILITY_TYPE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FIRST_IMMUNIZATION_ID_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.FIRST_NAME_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.GENERAL_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_DAY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_DAY_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_END_DATE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_ID_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_MONTH_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_MONTH_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_START_DATE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_STATUS_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_STATUS_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_YEAR_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.IMMUNIZATION_YEAR_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.LAST_NAME_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.MANAGEMENT_STATUS_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.MANAGEMENT_STATUS_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.MEANS_OF_IMMUNIZATION_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.MEANS_OF_IMMUNIZATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.ONLY_SHOW_PERSONS_WITH_OVERDUE_VACCINATION_LABEL;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.PERSON_ID_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.REGION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.RESULTS_IN_GRID;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.SEX_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.ImmunizationsDirectoryPage.TYPE_OF_LAST_VACCINE_COLUMN_HEADER;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class ImmunizationDirectorySteps implements En {

  private final WebDriverHelpers webDriverHelpers;

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");

  @Inject
  public ImmunizationDirectorySteps(
      final WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      ApiState apiState,
      AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;

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
        "^I check that the row number (\\d+) contains \"([^\"]*)\" in column number (\\d+)$",
        (Integer rowNumber, String expectedResult, Integer columnNumber) -> {
          String actualResult =
              webDriverHelpers.getTextFromWebElement(
                  By.xpath("//tbody//tr[" + rowNumber + "]//td[" + columnNumber + "]"));
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      actualResult,
                      expectedResult,
                      "Visible text different than expected"),
              10);
        });

    And(
        "^I filter by \"([^\"]*)\" as a Person's name on general text filter$",
        (String name) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GENERAL_SEARCH_INPUT, 50);
          webDriverHelpers.fillAndSubmitInWebElement(GENERAL_SEARCH_INPUT, name);
          TimeUnit.SECONDS.sleep(2);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(90);
        });

    And(
        "^I click on the RESET FILTERS button from Immunization$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              RESET_FILTERS_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(3);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });

    And(
        "^I check that row number (\\d+) contains correct birthdate in column number (\\d+)$",
        (Integer rowNumber, Integer columnNumber) -> {
          String ageAndBirthdate =
              webDriverHelpers.getTextFromWebElement(
                  By.xpath("//tbody//tr[" + rowNumber + "]//td[" + columnNumber + "]"));
          String actualBirthdate = getActualBirthDate(ageAndBirthdate);

          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      actualBirthdate,
                      getExpectedBirthDate(),
                      "Birthdate is different than expected"),
              10);
        });

    And(
        "^I check that row number (\\d+) contains correct age in column number (\\d+)$",
        (Integer rowNumber, Integer columnNumber) -> {
          String ageAndBirthdate =
              webDriverHelpers.getTextFromWebElement(
                  By.xpath("//tbody//tr[" + rowNumber + "]//td[" + columnNumber + "]"));
          LocalDate expectedBirthDate = LocalDate.parse(getExpectedBirthDate(), DATE_FORMATTER);
          String expectedAge = Integer.toString(calculateAge(expectedBirthDate, LocalDate.now()));
          String actualAge = getActualAge(ageAndBirthdate);

          assertHelpers.assertWithPoll(
              () -> Assert.assertEquals(actualAge, expectedAge, "Age is different than expected"),
              10);
        });

    And(
        "^I click SHOW MORE FILTERS button on Immunization directory page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SHOW_MORE_LESS_FILTERS);
          webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_LESS_FILTERS);
        });

    And(
        "I apply {string} filter to {string} on Immunization directory page",
        (String filterOption, String filterValue) -> {
          switch (filterOption) {
            case "Year":
              webDriverHelpers.selectFromCombobox(IMMUNIZATION_YEAR_FILTER_COMBOBOX, filterValue);
              break;
            case "Month":
              webDriverHelpers.selectFromCombobox(IMMUNIZATION_MONTH_FILTER_COMBOBOX, filterValue);
              break;
            case "Day":
              webDriverHelpers.selectFromCombobox(IMMUNIZATION_DAY_FILTER_COMBOBOX, filterValue);
              break;
            case "Region":
              webDriverHelpers.selectFromCombobox(REGION_FILTER_COMBOBOX, filterValue);
              break;
            case "District":
              webDriverHelpers.selectFromCombobox(DISTRICT_FILTER_COMBOBOX, filterValue);
              break;
            case "Means of immunization":
              webDriverHelpers.selectFromCombobox(
                  MEANS_OF_IMMUNIZATION_FILTER_COMBOBOX, filterValue);
              break;
            case "Management status":
              webDriverHelpers.selectFromCombobox(MANAGEMENT_STATUS_FILTER_COMBOBOX, filterValue);
              break;
            case "Immunization status":
              webDriverHelpers.selectFromCombobox(IMMUNIZATION_STATUS_FILTER_COMBOBOX, filterValue);
              break;
            case "Community":
              webDriverHelpers.selectFromCombobox(COMMUNITY_FILTER_COMBOBOX, filterValue);
              break;
            case "Facility category":
              webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_FILTER_COMBOBOX, filterValue);
              break;
            case "Facility type":
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_FILTER_COMBOBOX, filterValue);
              break;
            case "Facility":
              webDriverHelpers.selectFromCombobox(FACILITY_FILTER_COMBOBOX, filterValue);
              break;
            case "Immunization reference date":
              webDriverHelpers.selectFromCombobox(DATA_TYPE_FILTER_COMBOBOX, filterValue);
              break;
          }
        });

    And(
        "I set {int} day ago from today as a Immunization reference {string} on Immunization directory page",
        (Integer day, String dateFromOrTo) -> {
          switch (dateFromOrTo) {
            case "Date From":
              webDriverHelpers.fillInWebElement(
                  DATE_FROM_CALENDAR_INPUT,
                  DATE_FORMATTER_DE.format(LocalDate.now().minusDays(day)));
              break;
            case "Date To":
              webDriverHelpers.fillInWebElement(
                  DATE_TO_CALENDAR_INPUT, DATE_FORMATTER_DE.format(LocalDate.now().minusDays(day)));
              break;
          }
        });

    And(
        "I check that the row number {int} contains {int} day ago from today date in column number {int}",
        (Integer rowNumber, Integer day, Integer columnNumber) -> {
          String actualDate =
              webDriverHelpers.getTextFromWebElement(
                  By.xpath("//tbody//tr[" + rowNumber + "]//td[" + columnNumber + "]"));
          String expectedDate = DATE_FORMATTER.format(LocalDate.now().minusDays(day));

          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(actualDate, expectedDate, "Date is different than expected"),
              10);
        });

    And(
        "^I click on checkbox to only show persons with overdue immunization$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ONLY_SHOW_PERSONS_WITH_OVERDUE_VACCINATION_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              ONLY_SHOW_PERSONS_WITH_OVERDUE_VACCINATION_LABEL);
        });

    And(
        "^I check that the row number (\\d+) contains any date before current day in column (\\d+)$",
        (Integer rowNumber, Integer columnNumber) -> {
          String actualResult =
              webDriverHelpers.getTextFromWebElement(
                  By.xpath("//tbody//tr[" + rowNumber + "]//td[" + columnNumber + "]"));
          LocalDate actualDate = LocalDate.parse(actualResult, DATE_FORMATTER);

          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertTrue(actualDate.isBefore(LocalDate.now()), "The date is incorrect!"),
              10);
        });
  }

  private String getExpectedBirthDate() {
    final String year = webDriverHelpers.getValueFromWebElement(IMMUNIZATION_YEAR_FILTER_INPUT);
    final String month = webDriverHelpers.getValueFromWebElement(IMMUNIZATION_MONTH_FILTER_INPUT);
    final String day = webDriverHelpers.getValueFromWebElement(IMMUNIZATION_DAY_FILTER_INPUT);
    final String date = month + "/" + day + "/" + year;
    return date;
  }

  private String getActualAge(String string) {
    if (string != null) {
      string = string.substring(0, string.indexOf(" "));
    }
    return string;
  }

  private String getActualBirthDate(String string) {
    if (string != null) {
      string = string.substring(string.lastIndexOf("(") + 1, string.indexOf(")"));
    }
    return string;
  }

  private static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
    if ((birthDate != null) && (currentDate != null)) {
      return Period.between(birthDate, currentDate).getYears();
    } else {
      return 0;
    }
  }
}
