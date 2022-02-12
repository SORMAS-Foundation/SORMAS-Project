package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.CHRONIC_NEUROLOGICAL_NEUROMUSCULAR_DISEASE_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.CHRONIC_PULMONARY_DISEASE_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.CLEAR_ALL_OPTION;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.DIABETES_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.EDIT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.IMMUNODEFICIENCY_INCLUDING_HIV_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.LIVER_DISEASE_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.MALIGNANCY_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.NEW_CLINICAL_ASSESEMENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.RENAL_DISEASE_RADIO_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.SAVE_CLINICAL_VISIT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.ClinicalCourseTabCasePage.SET_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.CURRENT_BODY_TEMPERATURE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.DATE_OF_VISIT_INPUT;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.SOURCE_OF_BODY_TEMPERATURE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.TIME_OF_VISIT_INPUT;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.VISIT_REMARKS;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Visit;
import org.sormas.e2etests.services.ClinicalCourseVisitService;
import org.sormas.e2etests.state.ApiState;

public class ClinicalCourseTabCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Visit visit;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  @SneakyThrows
  @Inject
  public ClinicalCourseTabCaseSteps(
      WebDriverHelpers webDriverHelpers,
      Faker faker,
      ApiState apiState,
      ClinicalCourseVisitService clinicalCourseVisitService,
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    Then(
        "I click on New Clinical Assesement button on Clinical Course page",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_CLINICAL_ASSESEMENT_BUTTON));

    When(
        "^I set Diabetes radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(DIABETES_RADIO_BUTTON, buttonName));
    When(
        "^I set Immunodeficiency including HIV radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(
                IMMUNODEFICIENCY_INCLUDING_HIV_RADIO_BUTTON, buttonName));
    When(
        "^I set Liver disease radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(LIVER_DISEASE_RADIO_BUTTON, buttonName));
    When(
        "^I set Malignancy radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(MALIGNANCY_RADIO_BUTTON, buttonName));
    When(
        "^I set Chronic pulmonary disease radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(
                CHRONIC_PULMONARY_DISEASE_RADIO_BUTTON, buttonName));
    When(
        "^I set Renal disease radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(RENAL_DISEASE_RADIO_BUTTON, buttonName));
    When(
        "^I set Chronic neurological/neuromuscular disease radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(
                CHRONIC_NEUROLOGICAL_NEUROMUSCULAR_DISEASE_RADIO_BUTTON, buttonName));
    When(
        "^I set Cardiovascular disease including hypertension radio button to ([^\"]*)$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(
                CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION_RADIO_BUTTON, buttonName));
    Then(
        "I click Save button on Clinical Course Tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON));
    And(
        "I check if Case saved popup appeared and close it",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_SAVED_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    And(
        "I set Date and Time of visit  on Clinical Course form",
        () -> {
          LocalTime time = LocalTime.of(faker.number().numberBetween(10, 23), 30);
          LocalDate date = LocalDate.now().minusDays(faker.number().numberBetween(1, 10));
          fillDateOfVisit(date);
          fillTimeOfVisit(time);
        });

    When(
        "I fill the specific data of clinical visit with ([^\"]*) option to all symptoms",
        (String parameter) -> {
          visit = clinicalCourseVisitService.buildClinicalCourseVisit();
          fillDateOfVisit(visit.getDateOfVisit());
          fillTimeOfVisit(visit.getTimeOfVisit());
          fillVisitRemarks(visit.getVisitRemarks(), VISIT_REMARKS);
          selectCurrentTemperature(visit.getCurrentBodyTemperature());
          selectSourceOfTemperature(visit.getSourceOfBodyTemperature());
          webDriverHelpers.clickOnWebElementBySelector(CLEAR_ALL_OPTION);
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.clickWebElementByText(SET_OPTIONS, parameter);
          TimeUnit.SECONDS.sleep(2);
        });
    When(
        "I click Save button in New Clinical Assessement popup",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_CLINICAL_VISIT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_CLINICAL_VISIT_BUTTON);
        });
    And(
        "I click on Edit Clinical Visit button",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_BUTTON));
  }

  private void fillDateOfVisit(LocalDate dateOfVisit) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_OF_VISIT_INPUT, DATE_FORMATTER.format(dateOfVisit));
  }

  private void fillTimeOfVisit(LocalTime timeOfVisit) {
    webDriverHelpers.selectFromCombobox(TIME_OF_VISIT_INPUT, TIME_FORMATTER.format(timeOfVisit));
  }

  private void fillVisitRemarks(String remarks, By element) {
    webDriverHelpers.clearAndFillInWebElement(element, remarks);
  }

  private void selectCurrentTemperature(String currentTemperature) {
    webDriverHelpers.selectFromCombobox(CURRENT_BODY_TEMPERATURE_COMBOBOX, currentTemperature);
  }

  private void selectSourceOfTemperature(String sourceTemperature) {
    webDriverHelpers.selectFromCombobox(SOURCE_OF_BODY_TEMPERATURE_COMBOBOX, sourceTemperature);
  }
}
