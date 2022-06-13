package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class ClinicalCourseTabCasePage {
  public static final By NEW_CLINICAL_ASSESEMENT_BUTTON = By.id("clinicalVisitNewClinicalVisit");
  public static final By DIABETES_RADIO_BUTTON =
      By.cssSelector("[id='diabetes'] [class='v-checkbox v-select-option']");
  public static final By IMMUNODEFICIENCY_INCLUDING_HIV_RADIO_BUTTON =
      By.cssSelector("[id='immunodeficiencyIncludingHiv'] [class='v-checkbox v-select-option']");
  public static final By LIVER_DISEASE_RADIO_BUTTON =
      By.cssSelector("[id='chronicLiverDisease'] [class='v-checkbox v-select-option']");
  public static final By MALIGNANCY_RADIO_BUTTON =
      By.cssSelector("[id='malignancyChemotherapy'] [class='v-checkbox v-select-option']");
  public static final By CHRONIC_PULMONARY_DISEASE_RADIO_BUTTON =
      By.cssSelector("[id='chronicPulmonaryDisease'] [class='v-checkbox v-select-option']");
  public static final By RENAL_DISEASE_RADIO_BUTTON =
      By.cssSelector("[id='chronicKidneyDisease'] [class='v-checkbox v-select-option']");
  public static final By CHRONIC_NEUROLOGICAL_NEUROMUSCULAR_DISEASE_RADIO_BUTTON =
      By.cssSelector("[id='chronicNeurologicCondition'] [class='v-checkbox v-select-option']");
  public static final By CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION_RADIO_BUTTON =
      By.cssSelector(
          "[id='cardiovascularDiseaseIncludingHypertension'] [class='v-checkbox v-select-option']");

  public static final By SAVE_BUTTON = By.id("commit");
  public static final By SAVE_CLINICAL_VISIT_BUTTON = By.cssSelector(".popupContent #commit");
  public static final By EDIT_BUTTON = By.cssSelector("[class='v-icon v-icon-edit']");
  public static final By CLEAR_ALL_OPTION = By.cssSelector("[id='actionClearAll']");
  public static final By SET_OPTIONS =
      By.cssSelector("[class='v-button v-widget link v-button-link']");
}
