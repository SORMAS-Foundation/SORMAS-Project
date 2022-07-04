package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_PERSON_TAB;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPersonPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.cases.CreateNewCaseSteps;
import org.testng.asserts.SoftAssert;

public class EditImmunizationPersonSteps implements En {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_EN = DateTimeFormatter.ofPattern("M/d/yyyy");
  private final WebDriverHelpers webDriverHelpers;
  protected Immunization collectedImmunization;
  private Immunization previousCreatedImmunization;

  @Inject
  public EditImmunizationPersonSteps(
      final WebDriverHelpers webDriverHelpers, final SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit immunization person page",
        () -> {
          collectedImmunization = collectImmunizationPersonData();
          previousCreatedImmunization = CreateNewImmunizationSteps.immunization;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedImmunization,
              previousCreatedImmunization,
              List.of(
                  "firstName",
                  "lastName",
                  "presentConditionOfPerson",
                  "sex",
                  "primaryEmailAddress",
                  "dateOfBirth"));
        });

    Then(
        "I click on Person tab from Immunization page",
        () -> {
          webDriverHelpers.scrollToElement(IMMUNIZATION_PERSON_TAB);
          webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_PERSON_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(FIRST_NAME_INPUT);
        });

    When(
        "I click on Link Case button",
        () -> {
          webDriverHelpers.scrollToElement(LINK_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(LINK_CASE_BUTTON);
        });

    When(
        "I fill filed with collected case in Search specific case popup",
        () ->
            webDriverHelpers.fillInWebElement(
                SEARCH_SPECIFIC_CASE_INPUT, CreateNewCaseSteps.casesUUID.get(0)));

    When(
        "I click on Search case in Search specific case popup in immunization Link Case",
        () -> webDriverHelpers.doubleClickOnWebElementBySelector(SEARCH_SPECIFIC_CASE_BUTTON));

    When(
        "I check if case was found in Link Case",
        () -> webDriverHelpers.waitUntilIdentifiedElementIsPresent(LINK_CASE_CASE_FOUND_HEADER));

    When(
        "I click Okay in Case Found in Immunization Link Case popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(OKAY_LINK_CASE_BUTTON));

    When(
        "I check if Open Case button exists in Immunization edit page",
        () -> webDriverHelpers.isElementVisibleWithTimeout(OPEN_CASE_BUTTON, 2));

    When(
        "I check if Date of first positive result is equal with created pathogen test",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(DATE_OF_FIRST_POSITIVE_TEST_RESULT_INPUT),
              LocalDate.now().minusDays(10).format(DATE_FORMATTER_EN),
              "Dates of first positive result are not equal");
          softly.assertAll();
        });

    When(
        "I check if Date of recovery is equal with created case",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(DATE_OF_RECOVERY_INPUT),
              LocalDate.now().minusDays(1).format(DATE_FORMATTER_EN),
              "Dates of recovery are not equal");
          softly.assertAll();
        });

    When(
        "I click on Open Case button in Edit immunization",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(OPEN_CASE_BUTTON);
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
  }

  private Immunization collectImmunizationPersonData() {
    return Immunization.builder()
        .firstName(webDriverHelpers.getValueFromWebElement(FIRST_NAME_INPUT))
        .lastName(webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT))
        .dateOfBirth(getUserBirthDate())
        .presentConditionOfPerson(webDriverHelpers.getValueFromWebElement(PRESENT_CONDITION_INPUT))
        .sex(webDriverHelpers.getValueFromWebElement(SEX_INPUT))
        .primaryPhoneNumber(webDriverHelpers.getTextFromPresentWebElement(PHONE_FIELD))
        .primaryEmailAddress(webDriverHelpers.getTextFromPresentWebElement(EMAIL_FIELD))
        .build();
  }

  private LocalDate getUserBirthDate() {
    final String year = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_YEAR_INPUT);
    final String month = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_MONTH_INPUT);
    final String day = webDriverHelpers.getValueFromWebElement(DATE_OF_BIRTH_DAY_INPUT);
    final String date = month + "/" + day + "/" + year;
    return LocalDate.parse(date, DATE_FORMATTER);
  }
}
