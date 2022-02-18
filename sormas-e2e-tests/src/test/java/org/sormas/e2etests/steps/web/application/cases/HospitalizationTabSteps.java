package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.HospitalizationTabPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pojo.web.Hospitalization;
import org.sormas.e2etests.services.HospitalizationService;
import org.sormas.e2etests.state.ApiState;

public class HospitalizationTabSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Hospitalization hospitalization;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public HospitalizationTabSteps(
      WebDriverHelpers webDriverHelpers,
      HospitalizationService hospitalizationService,
      ApiState apiState,
      @Named("ENVIRONMENT_URL") String environmentUrl) {

    this.webDriverHelpers = webDriverHelpers;

    When(
        "I navigate to hospitalization tab for case created via api",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseHospitalizationPath = "/sormas-webdriver/#!cases/hospitalization/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(environmentUrl + caseHospitalizationPath + uuid);
        });

    When(
        "I complete all hospitalization fields and save",
        () -> {
          hospitalization = hospitalizationService.generateHospitalization();
          selectPatientAdmittedAtTheFacility(
              hospitalization.getWasPatientAdmittedAtTheFacilityAsAnInpatient());
          fillDateOfVisitOrAdmission(hospitalization.getDateOfVisitOrAdmission());
          selectReasonForHospitalization(hospitalization.getReasonForHospitalization());
          fillDateOfDischargeOrTransfer(hospitalization.getDateOfDischargeOrTransfer());
          selectStayInTheIntensiveCareUnit(hospitalization.getStayInTheIntensiveCareUnit());
          fillStartOfStayDate(hospitalization.getStartOfStayDate());
          fillEndOfStayDate(hospitalization.getEndOfStayDate());
          fillSpecifyReason(hospitalization.getSpecifyReason());
          selectIsolation(hospitalization.getIsolation());
          fillDateOfIsolation(hospitalization.getDateOfIsolation());
          selectWasThePatientHospitalizedPreviously(
              hospitalization.getWasThePatientHospitalizedPreviously());
          selectLeftAgainstMedicalAdvice(hospitalization.getLeftAgainstMedicalAdvice());

          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SUCCESSFUL_SAVE_POPUP);
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
          webDriverHelpers.clickWebElementByText(PATIENT_ADMITTED_AT_FACILITY_OPTIONS, option);
        });

    When(
        "I set specific Date of visit or admission",
        () -> {
          fillDateOfVisitOrAdmission(LocalDate.now().minusDays(1));
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
  }

  @SneakyThrows
  private void selectPatientAdmittedAtTheFacility(String yesNoUnknown) {
    webDriverHelpers.clickWebElementByText(PATIENT_ADMITTED_AT_FACILITY_OPTIONS, yesNoUnknown);
    TimeUnit.SECONDS.sleep(1);
  }

  private void fillDateOfVisitOrAdmission(LocalDate date) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_VISIT_OR_ADMISSION_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillDateOfDischargeOrTransfer(LocalDate date) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_DISCHARGE_OR_TRANSFER_INPUT, DATE_FORMATTER.format(date));
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

  private void fillStartOfStayDate(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_OF_STAY_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillEndOfStayDate(LocalDate date) {
    webDriverHelpers.clickOnWebElementBySelector(END_OF_STAY_DATE_INPUT);
    webDriverHelpers.fillInWebElement(END_OF_STAY_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectIsolation(String option) {
    webDriverHelpers.clickWebElementByText(ISOLATION_OPTIONS, option);
  }

  private void fillDateOfIsolation(LocalDate date) {
    webDriverHelpers.fillInWebElement(DATE_OF_ISOLATION_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectWasThePatientHospitalizedPreviously(String option) {
    webDriverHelpers.clickWebElementByText(WAS_THE_PATIENT_HOSPITALIZED_PREVIOUSLY_OPTIONS, option);
  }

  private void selectLeftAgainstMedicalAdvice(String option) {
    webDriverHelpers.clickWebElementByText(LEFT_AGAINST_MEDICAL_ADVICE_OPTIONS, option);
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
        .build();
  }
}
