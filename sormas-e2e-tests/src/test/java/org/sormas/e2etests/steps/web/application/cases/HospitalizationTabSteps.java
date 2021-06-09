package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.HospitalizationTabPage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
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
        "I navigate to hospitalization tab using of created case via api",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseHospitalizationPath = "/sormas-ui/#!cases/hospitalization/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(environmentUrl + caseHospitalizationPath + uuid);
        });

    When(
        "I change all hospitalization fields and save",
        () -> {
          hospitalization = hospitalizationService.generateHospitalization();
          fillDateOfVisitOrAdmission(hospitalization.getDateOfVisitOrAdmission());
          selectReasonForHospitalization(hospitalization.getReasonForHospitalization());
          fillDateOfDischargeOrTransfer(hospitalization.getDateOfDischargeOrTransfer());
          selectStayInTheIntensiveCareUnit(hospitalization.getStayInTheIntensiveCareUnit());
          fillStartOfStayDate(hospitalization.getStartOfStayDate());
          selectIsolation(hospitalization.getIsolation());
          fillDateOfIsolation(hospitalization.getDateOfIsolation());
          selectWasThePatientHospitalizedPreviously(
              hospitalization.getWasThePatientHospitalizedPreviously());
          selectWasPatientAdmittedAtTheFacilityAsAnInpatient(
              hospitalization.getWasPatientAdmittedAtTheFacilityAsAnInpatient());
          selectLeftAgainstMedicalAdvice(hospitalization.getLeftAgainstMedicalAdvice());
          fillEndOfStayDate(hospitalization.getEndOfStayDate());
          fillSpecifyReason(hospitalization.getSpecifyReason());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SUCCESSFUL_SAVE_POPUP);
        });

    When(
        "I check the edited and saved data is correctly displayed on Hospitalization tab page",
        () -> {
          Hospitalization actualHospitalization = collectHospitalizationData();
          Truth.assertThat(actualHospitalization).isEqualTo(hospitalization);
        });
  }

  public void fillDateOfVisitOrAdmission(LocalDate date) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_VISIT_OR_ADMISSION_INPUT, DATE_FORMATTER.format(date));
  }

  public void fillDateOfDischargeOrTransfer(LocalDate date) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_DISCHARGE_OR_TRANSFER_INPUT, DATE_FORMATTER.format(date));
  }

  public void selectReasonForHospitalization(String reason) {
    webDriverHelpers.selectFromCombobox(REASON_FOR_HOSPITALIZATION_COMBOBOX, reason);
  }

  public void fillSpecifyReason(String text) {
    webDriverHelpers.fillInWebElement(SPECIFY_REASON_INPUT, text);
  }

  public void selectStayInTheIntensiveCareUnit(String option) {
    webDriverHelpers.clickWebElementByText(STAY_IN_THE_INTENSIVE_CARE_UNIT_OPTIONS, option);
  }

  public void fillStartOfStayDate(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_OF_STAY_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  public void fillEndOfStayDate(LocalDate date) {
    webDriverHelpers.fillInWebElement(END_OF_STAY_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  public void selectIsolation(String option) {
    webDriverHelpers.clickWebElementByText(ISOLATION_OPTIONS, option);
  }

  public void fillDateOfIsolation(LocalDate date) {
    webDriverHelpers.fillInWebElement(DATE_OF_ISOLATION_INPUT, DATE_FORMATTER.format(date));
  }

  public void selectWasThePatientHospitalizedPreviously(String option) {
    webDriverHelpers.clickWebElementByText(WAS_THE_PATIENT_HOSPITALIZED_PREVIOUSLY_OPTIONS, option);
  }

  public void selectWasPatientAdmittedAtTheFacilityAsAnInpatient(String option) {
    webDriverHelpers.clickWebElementByText(WAS_THE_PATIENT_ADMITTED_AS_IMPATIENT_OPTIONS, option);
  }

  public void selectLeftAgainstMedicalAdvice(String option) {
    webDriverHelpers.clickWebElementByText(LEFT_AGAINST_MEDICAL_ADVICE_OPTIONS, option);
  }

  public Hospitalization collectHospitalizationData() {
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
