package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.THERAPY_TAB;
import static org.sormas.e2etests.pages.application.cases.TherapyPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Therapy;
import org.sormas.e2etests.entities.services.TherapyService;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class EditTherapySteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Therapy therapyData;
  public static Therapy prescriptionType;
  public static Therapy treatmentType;
  public static Therapy collectedTherapyData;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  @SneakyThrows
  @Inject
  public EditTherapySteps(WebDriverHelpers webDriverHelpers, TherapyService therapyService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I am accessing the Therapy tab of created case",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(THERAPY_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NEW_PRESCRIPTION_BUTTON);
        });

    When(
        "I create and fill Prescriptions with specific data for drug intake",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          webDriverHelpers.clickOnWebElementBySelector(NEW_PRESCRIPTION_BUTTON);
          therapyData = therapyService.buildPrescriptionDrugIntake();
          selectPrescriptionType(therapyData.getPrescriptionType());
          fillPrescriptionDetails(therapyData.getPrescriptionDetails());
          fillDateOfReportPrescription(therapyData.getPrescriptionDate());
          fillPrescribingClinicianPrescription(therapyData.getPrescribingClinician());
          fillTreatmentStartDatePrescription(therapyData.getPrescriptionStartDate());
          fillTreatmentEndDatePrescription(therapyData.getPrescriptionEndDate());
          fillFrequencyPrescription(therapyData.getPrescriptionFrequency());
          fillDosePrescription(therapyData.getPrescriptionDose());
          selectRoutePrescription(therapyData.getPrescriptionRoute());
          fillAdditionalNotesPrescription(therapyData.getPrescriptionAdditionalNotes());
        });

    When(
        "I create and fill Treatment with specific data for drug intake",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(NEW_TREATMENT_BUTTON);
          therapyData = therapyService.buildTreatmentDrugIntake();
          selectTreatmentType(therapyData.getTreatmentType());
          fillTreatmentDetails(therapyData.getTreatmentDetails());
          fillTreatmentDate(therapyData.getTreatmentDate());
          fillTreatmentTime(therapyData.getTreatmentTime());
          fillTreatmentStaffMember(therapyData.getTreatmentExecutingStaffMember());
          fillTreatmentDose(therapyData.getTreatmentDose());
          selectTreatmentRoute(therapyData.getTreatmentRoute());
          fillAdditionalNotesTreatment(therapyData.getTreatmentAdditionalNotes());
        });

    When(
        "I choose ([^\"]*) option as a Type of drug",
        (String options) ->
            webDriverHelpers.clickWebElementByText(
                TYPE_OF_DRUG_HORIZONTAL_CHECKBOX, options.toUpperCase()));

    When(
        "I choose ([^\"]*) option as a Prescription type",
        (String options) -> {
          webDriverHelpers.selectFromCombobox(PRESCRIPTION_TYPE_COMBOBOX, options);
          prescriptionType = Therapy.builder().prescriptionType(options).build();
        });

    When(
        "I choose ([^\"]*) option as a Treatment type",
        (String options) -> {
          webDriverHelpers.selectFromCombobox(TREATMENT_TYPE_COMBOBOX, options);
          treatmentType = Therapy.builder().treatmentType(options).build();
        });

    When(
        "I check if created data is correctly displayed in Perscription section",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PRESCRIPTION_EDIT_BUTTON);
          collectedTherapyData = collectTherapyDataPrescription();
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedTherapyData,
              therapyData,
              List.of(
                  "prescriptionDetails",
                  "prescriptionDate",
                  "prescribingClinician",
                  "prescriptionStartDate",
                  "prescriptionEndDate",
                  "prescriptionFrequency",
                  "prescriptionDose",
                  "prescriptionRoute",
                  "prescriptionAdditionalNotes"));
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedTherapyData, prescriptionType, List.of("prescriptionType"));
        });

    When(
        "I check if created data is correctly displayed in Treatment section",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TREATMENT_EDIT_BUTTON);
          collectedTherapyData = collectTherapyDataTreatment();
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedTherapyData,
              therapyData,
              List.of(
                  "treatmentDetails",
                  "treatmentDate",
                  "treatmentTime",
                  "treatmentExecutingStaffMember",
                  "treatmentDose",
                  "treatmentRoute",
                  "treatmentAdditionalNotes"));
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedTherapyData, treatmentType, List.of("treatmentType"));
        });

    When(
        "I click on Save button from New Prescription popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(THERAPY_POPUP_SAVE_BUTTON));

    When(
        "I click on Save button from New Treatment popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(TREATMENT_POPUP_SAVE_BUTTON));
  }

  private void selectPrescriptionType(String prescriptionType) {
    webDriverHelpers.selectFromCombobox(PRESCRIPTION_TYPE_COMBOBOX, prescriptionType);
  }

  private void selectTreatmentType(String treatmentType) {
    webDriverHelpers.selectFromCombobox(TREATMENT_TYPE_COMBOBOX, treatmentType);
  }

  private void fillPrescriptionDetails(String prescriptionDetails) {
    webDriverHelpers.fillInWebElement(PRESCRIPTION_DETAILS, prescriptionDetails);
  }

  private void fillTreatmentDetails(String treatmentDetails) {
    webDriverHelpers.fillInWebElement(TREATMENT_DETAILS, treatmentDetails);
  }

  private void fillDateOfReportPrescription(LocalDate date) {
    webDriverHelpers.fillInWebElement(PRESCRIPTION_DATE, DATE_FORMATTER.format(date));
  }

  private void fillTreatmentDate(LocalDate date) {
    webDriverHelpers.fillInWebElement(TREATMENT_DATE, DATE_FORMATTER.format(date));
  }

  private void fillTreatmentTime(LocalTime time) {
    webDriverHelpers.selectFromCombobox(TREATMENT_TIME, TIME_FORMATTER.format(time));
  }

  private void fillPrescribingClinicianPrescription(String prescribingClinician) {
    webDriverHelpers.fillInWebElement(PRESCRIBING_CLINICIAN, prescribingClinician);
  }

  private void fillTreatmentStaffMember(String staffMember) {
    webDriverHelpers.fillInWebElement(TREATMENT_EXECUTING_STAFF_MEMBER, staffMember);
  }

  private void fillTreatmentStartDatePrescription(LocalDate startDate) {
    webDriverHelpers.fillInWebElement(
        PRESCRIPTION_TREATMENT_START_DATE, DATE_FORMATTER.format(startDate));
  }

  private void fillTreatmentEndDatePrescription(LocalDate endDate) {
    webDriverHelpers.fillInWebElement(
        PRESCRIPTION_TREATMENT_END_DATE, DATE_FORMATTER.format(endDate));
  }

  private void fillFrequencyPrescription(String frequency) {
    webDriverHelpers.fillInWebElement(PRESCRIPTION_FREQUENCY, frequency);
  }

  private void fillDosePrescription(String dose) {
    webDriverHelpers.fillInWebElement(PRESCRIPTION_DOSE, dose);
  }

  private void fillTreatmentDose(String dose) {
    webDriverHelpers.fillInWebElement(TREATMENT_DOSE, dose);
  }

  private void selectRoutePrescription(String route) {
    webDriverHelpers.selectFromCombobox(PRESCRIPTION_ROUTE_COMBOBOX, route);
  }

  private void selectTreatmentRoute(String route) {
    webDriverHelpers.selectFromCombobox(TREATMENT_ROUTE_COMBOBOX, route);
  }

  private void fillAdditionalNotesPrescription(String notes) {
    webDriverHelpers.fillInWebElement(PRESCRIPTION_ADDITIONAL_NOTES, notes);
  }

  private void fillAdditionalNotesTreatment(String notes) {
    webDriverHelpers.fillInWebElement(TREATMENT_ADDITIONAL_NOTES, notes);
  }

  private LocalDate getPrescriptionDate() {
    String date = webDriverHelpers.getValueFromWebElement(PRESCRIPTION_DATE);
    return LocalDate.parse(date, DATE_FORMATTER);
  }

  private LocalDate getTreatmentDate() {
    String date = webDriverHelpers.getValueFromWebElement(TREATMENT_DATE);
    return LocalDate.parse(date, DATE_FORMATTER);
  }

  private LocalDate getPrescriptionStartDate() {
    String dateOfReport =
        webDriverHelpers.getValueFromWebElement(PRESCRIPTION_TREATMENT_START_DATE);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getPrescriptionEndDate() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(PRESCRIPTION_TREATMENT_END_DATE);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalTime getTreatmentTime() {
    return LocalTime.parse(webDriverHelpers.getValueFromCombobox(TREATMENT_TIME), TIME_FORMATTER);
  }

  private Therapy collectTherapyDataPrescription() {
    return Therapy.builder()
        .prescriptionType(webDriverHelpers.getValueFromCombobox(PRESCRIPTION_TYPE_COMBOBOX))
        .prescriptionDetails(webDriverHelpers.getValueFromWebElement(PRESCRIPTION_DETAILS))
        .prescriptionDate(getPrescriptionDate())
        .prescribingClinician(webDriverHelpers.getValueFromWebElement(PRESCRIBING_CLINICIAN))
        .prescriptionStartDate(getPrescriptionStartDate())
        .prescriptionEndDate(getPrescriptionEndDate())
        .prescriptionFrequency(webDriverHelpers.getValueFromWebElement(PRESCRIPTION_FREQUENCY))
        .prescriptionDose(webDriverHelpers.getValueFromWebElement(PRESCRIPTION_DOSE))
        .prescriptionRoute(webDriverHelpers.getValueFromCombobox(PRESCRIPTION_ROUTE_COMBOBOX))
        .prescriptionAdditionalNotes(
            webDriverHelpers.getValueFromWebElement(PRESCRIPTION_ADDITIONAL_NOTES))
        .build();
  }

  private Therapy collectTherapyDataTreatment() {
    return Therapy.builder()
        .treatmentType(webDriverHelpers.getValueFromCombobox(TREATMENT_TYPE_COMBOBOX))
        .treatmentDetails(webDriverHelpers.getValueFromWebElement(TREATMENT_DETAILS))
        .treatmentDate(getTreatmentDate())
        .treatmentTime(getTreatmentTime())
        .treatmentExecutingStaffMember(
            webDriverHelpers.getValueFromWebElement(TREATMENT_EXECUTING_STAFF_MEMBER))
        .treatmentDose(webDriverHelpers.getValueFromWebElement(TREATMENT_DOSE))
        .treatmentRoute(webDriverHelpers.getValueFromCombobox(TREATMENT_ROUTE_COMBOBOX))
        .treatmentAdditionalNotes(
            webDriverHelpers.getValueFromWebElement(TREATMENT_ADDITIONAL_NOTES))
        .build();
  }
}
