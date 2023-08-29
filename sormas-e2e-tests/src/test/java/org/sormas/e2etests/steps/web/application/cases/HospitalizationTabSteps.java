package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.FACILITY_HEALTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_SELECTED_VALUE;
import static org.sormas.e2etests.pages.application.cases.HospitalizationTabPage.*;
import static org.sormas.e2etests.pages.application.cases.SymptomsTabPage.CASE_TAB;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.Hospitalization;
import org.sormas.e2etests.entities.services.HospitalizationService;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class HospitalizationTabSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Hospitalization hospitalization;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static String reasonForCurrentHospitalization;

  @Inject
  public HospitalizationTabSteps(
      WebDriverHelpers webDriverHelpers,
      HospitalizationService hospitalizationService,
      ApiState apiState,
      SoftAssert softly,
      RunningConfiguration runningConfiguration) {

    this.webDriverHelpers = webDriverHelpers;

    When(
        "I navigate to hospitalization tab for case created via api",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseHospitalizationPath = "/sormas-webdriver/#!cases/hospitalization/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + caseHospitalizationPath
                  + uuid);
        });

    When(
        "I complete all hospitalization fields and save",
        () -> {
          hospitalization = hospitalizationService.generateHospitalization();
          selectPatientAdmittedAtTheFacility(
              hospitalization.getWasPatientAdmittedAtTheFacilityAsAnInpatient());
          fillDateOfVisitOrAdmission(hospitalization.getDateOfVisitOrAdmission(), Locale.ENGLISH);
          selectReasonForHospitalization(hospitalization.getReasonForHospitalization());
          fillDateOfDischargeOrTransfer(
              hospitalization.getDateOfDischargeOrTransfer(), Locale.ENGLISH);
          selectStayInTheIntensiveCareUnit(hospitalization.getStayInTheIntensiveCareUnit());
          fillStartOfStayDate(hospitalization.getStartOfStayDate(), Locale.ENGLISH);
          fillEndOfStayDate(hospitalization.getEndOfStayDate(), Locale.ENGLISH);
          fillSpecifyReason(hospitalization.getSpecifyReason());
          selectIsolation(hospitalization.getIsolation());
          fillDateOfIsolation(hospitalization.getDateOfIsolation(), Locale.ENGLISH);
          selectWasThePatientHospitalizedPreviously(
              hospitalization.getWasThePatientHospitalizedPreviously());
          selectLeftAgainstMedicalAdvice(hospitalization.getLeftAgainstMedicalAdvice());
          fillDescription(hospitalization.getDescription());

          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SUCCESSFUL_SAVE_POPUP);
        });

    When(
        "I complete all hospitalization fields for Current Hospitalization and save it for DE",
        () -> {
          hospitalization = hospitalizationService.generateCurrentHospitalizationForDE();
          selectPatientAdmittedAtTheFacility(
              hospitalization.getWasPatientAdmittedAtTheFacilityAsAnInpatient());
          fillDateOfVisitOrAdmission(hospitalization.getDateOfVisitOrAdmission(), Locale.GERMAN);
          fillDateOfDischargeOrTransfer(
              hospitalization.getDateOfDischargeOrTransfer(), Locale.GERMAN);
          reasonForCurrentHospitalization = hospitalization.getReasonForHospitalization();
          selectReasonForHospitalization(reasonForCurrentHospitalization);
          selectStayInTheIntensiveCareUnit(hospitalization.getStayInTheIntensiveCareUnit());
          fillStartOfStayDate(hospitalization.getStartOfStayDate(), Locale.GERMAN);
          fillEndOfStayDate(hospitalization.getEndOfStayDate(), Locale.GERMAN);
          selectWasThePatientHospitalizedPreviously(
              hospitalization.getWasThePatientHospitalizedPreviously());
          fillSpecifyReason(hospitalization.getSpecifyReason());
          selectIsolation(hospitalization.getIsolation());
          fillDateOfIsolation(hospitalization.getDateOfIsolation(), Locale.GERMAN);
          selectLeftAgainstMedicalAdvice(hospitalization.getLeftAgainstMedicalAdvice());
          fillDescription(hospitalization.getDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SUCCESSFUL_SAVE_POPUP);
          TimeUnit.SECONDS.sleep(6);
        });

    When(
        "I check the edited and saved data is correctly displayed on Hospitalization tab page",
        () -> {
          Hospitalization actualHospitalization = collectHospitalizationData();
          ComparisonHelper.compareEqualEntities(actualHospitalization, hospitalization);
        });

    When(
        "I set Patient Admitted at the facility as an inpatient as ([^\"]*)",
        (String option) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              PATIENT_ADMITTED_AT_FACILITY_OPTIONS);
          webDriverHelpers.clickWebElementByText(PATIENT_ADMITTED_AT_FACILITY_OPTIONS, option);
        });

    When(
        "I set specific Date of visit or admission",
        () -> {
          fillDateOfVisitOrAdmission(LocalDate.now().minusDays(1), Locale.ENGLISH);
        });

    When(
        "I save data in Hospitalization",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I check if error in Hospitalization data is available",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(BLUE_ERROR_EXCLAMATION_MARK);
        });

    When(
        "I check if Place of stay in hospital popup is displayed",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(PLACE_OF_STAY_IN_HOSPITAL_POPUP, 10);
        });

    When(
        "I choose Facility in Place of stay in hospital popup in Case Hospitalization as ([^\"]*)",
        (String facility) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FACILITY_POPUP_CHECKBOX);
          webDriverHelpers.selectFromCombobox(FACILITY_POPUP_CHECKBOX, facility);
        });

    When(
        "I save the data in Place of stay in hospital popup",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(
                PLACE_OF_STAY_IN_HOSPITAL_POPUP_SAVE_BUTTON));

    When(
        "From hospitalization tab I click on the Case tab button",
        () -> webDriverHelpers.clickOnWebElementBySelector(CASE_TAB));

    When(
        "I check if place of stay data was updated in the Case edit tab with ([^\"]*)",
        (String facility) -> {
          Case collectedData = collectPlaceOfStayData();
          softly.assertEquals(
              collectedData.getPlaceOfStay().toLowerCase(Locale.ROOT),
              "facility",
              "facility types are not equal");
          softly.assertEquals(collectedData.getFacility(), facility, "facilities are not equal");
          softly.assertAll();
        });

    And(
        "I set Reason for hospitalization as {string}",
        (String reasonForHospitalization) -> {
          webDriverHelpers.selectFromCombobox(
              REASON_FOR_HOSPITALIZATION_COMBOBOX, reasonForHospitalization);
        });

    Then(
        "^I check if description text field is available in Current Hospitalization tab$",
        () -> webDriverHelpers.waitUntilIdentifiedElementIsPresent(DESCRIPTION_INPUT));

    When(
        "I set Was the patient hospitalized previously option to {string}",
        (String option) ->
            webDriverHelpers.clickWebElementByText(
                WAS_THE_PATIENT_HOSPITALIZED_PREVIOUSLY_OPTIONS, option));

    And(
        "^I click on New entry to add a previous hospitalization$",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_ENTRY_LINK));

    And(
        "^I check if Previous Hospitalization Popup is displayed$",
        () -> webDriverHelpers.isElementVisibleWithTimeout(PREVIOUS_HOSPITALIZATION_POPUP, 10));
    Then(
        "I check Hospitalization tab have Current hospitalization heading",
        () ->
            webDriverHelpers.waitUntilIdentifiedElementIsPresent(CURRENT_HOSPITALIZATION_HEADING));

    Then(
        "I check Hospitalization tab have Previous hospitalization heading",
        () ->
            webDriverHelpers.waitUntilIdentifiedElementIsPresent(PREVIOUS_HOSPITALIZATION_HEADING));
  }

  @SneakyThrows
  private void selectPatientAdmittedAtTheFacility(String yesNoUnknown) {
    webDriverHelpers.clickWebElementByText(PATIENT_ADMITTED_AT_FACILITY_OPTIONS, yesNoUnknown);
    TimeUnit.SECONDS.sleep(1);
  }

  private void fillDateOfVisitOrAdmission(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_VISIT_OR_ADMISSION_INPUT, formatter.format(date));
  }

  private void fillDateOfDischargeOrTransfer(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_DISCHARGE_OR_TRANSFER_INPUT, formatter.format(date));
  }

  private void selectReasonForHospitalization(String reason) {
    webDriverHelpers.selectFromCombobox(REASON_FOR_HOSPITALIZATION_COMBOBOX, reason);
  }

  private void fillSpecifyReason(String text) {
    webDriverHelpers.fillInWebElement(SPECIFY_REASON_INPUT, text);
  }

  private void selectStayInTheIntensiveCareUnit(String option) {
    webDriverHelpers.clickWebElementByText(STAY_IN_THE_INTENSIVE_CARE_UNIT_OPTIONS, option);
  }

  private void fillStartOfStayDate(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(START_OF_STAY_DATE_INPUT, formatter.format(date));
  }

  private void fillEndOfStayDate(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(END_OF_STAY_DATE_INPUT, formatter.format(date));
  }

  private void selectIsolation(String option) {
    webDriverHelpers.clickWebElementByText(ISOLATION_OPTIONS, option);
  }

  private void fillDateOfIsolation(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_ISOLATION_INPUT, formatter.format(date));
  }

  private void selectWasThePatientHospitalizedPreviously(String option) {
    webDriverHelpers.clickWebElementByText(WAS_THE_PATIENT_HOSPITALIZED_PREVIOUSLY_OPTIONS, option);
  }

  private void selectLeftAgainstMedicalAdvice(String option) {
    webDriverHelpers.clickWebElementByText(LEFT_AGAINST_MEDICAL_ADVICE_OPTIONS, option);
  }

  private void fillDescription(String option) {
    webDriverHelpers.fillInWebElement(DESCRIPTION_INPUT, option);
  }

  private Hospitalization collectHospitalizationData() {
    return Hospitalization.builder()
        .dateOfVisitOrAdmission(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(DATE_OF_VISIT_OR_ADMISSION_INPUT),
                DATE_FORMATTER))
        .dateOfDischargeOrTransfer(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(DATE_OF_DISCHARGE_OR_TRANSFER_INPUT),
                DATE_FORMATTER))
        .reasonForHospitalization(
            webDriverHelpers.getValueFromCombobox(REASON_FOR_HOSPITALIZATION_COMBOBOX))
        .specifyReason(webDriverHelpers.getValueFromWebElement(SPECIFY_REASON_INPUT))
        .stayInTheIntensiveCareUnit(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                STAY_IN_THE_INTENSIVE_CARE_UNIT_OPTIONS))
        .startOfStayDate(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(START_OF_STAY_DATE_INPUT), DATE_FORMATTER))
        .endOfStayDate(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(END_OF_STAY_DATE_INPUT), DATE_FORMATTER))
        .isolation(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ISOLATION_OPTIONS))
        .dateOfIsolation(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(DATE_OF_ISOLATION_INPUT), DATE_FORMATTER))
        .wasThePatientHospitalizedPreviously(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                WAS_THE_PATIENT_HOSPITALIZED_PREVIOUSLY_OPTIONS))
        .wasPatientAdmittedAtTheFacilityAsAnInpatient(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                WAS_THE_PATIENT_ADMITTED_AS_IMPATIENT_OPTIONS))
        .leftAgainstMedicalAdvice(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                LEFT_AGAINST_MEDICAL_ADVICE_OPTIONS))
        .description(webDriverHelpers.getValueFromWebElement(DESCRIPTION_INPUT))
        .build();
  }

  private Case collectPlaceOfStayData() {
    return Case.builder()
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .facility(webDriverHelpers.getValueFromCombobox(FACILITY_HEALTH_COMBOBOX))
        .build();
  }
}
