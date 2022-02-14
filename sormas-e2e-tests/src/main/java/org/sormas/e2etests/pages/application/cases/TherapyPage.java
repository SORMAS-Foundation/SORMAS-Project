package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class TherapyPage {

  public static final By THERAPY_POPUP_SAVE_BUTTON = By.cssSelector(".popupContent #commit");
  public static final By THERAPY_POPUP_DISCARD_BUTTON = By.cssSelector(".popupContent #discard");
  public static final By ROUTE_SPECIFICATION = By.cssSelector(".popupContent #routeDetails");
  public static final By TYPE_OF_DRUG_HORIZONTAL_CHECKBOX =
      By.cssSelector(".popupContent #typeOfDrug .v-select-option");

  public static final By NEW_PRESCRIPTION_BUTTON = By.cssSelector("#prescriptionNewPrescription");
  public static final By PRESCRIPTION_TYPE_COMBOBOX =
      By.cssSelector(".popupContent #prescriptionType div");
  public static final By PRESCRIPTION_DETAILS =
      By.cssSelector(".popupContent #prescriptionDetails");
  public static final By PRESCRIPTION_DATE =
      By.cssSelector(".popupContent #prescriptionDate input");
  public static final By PRESCRIBING_CLINICIAN =
      By.cssSelector(".popupContent #prescribingClinician");
  public static final By PRESCRIPTION_TREATMENT_START_DATE =
      By.cssSelector(".popupContent #prescriptionStart input");
  public static final By PRESCRIPTION_TREATMENT_END_DATE =
      By.cssSelector(".popupContent #prescriptionEnd input");
  public static final By PRESCRIPTION_FREQUENCY = By.cssSelector(".popupContent #frequency");
  public static final By PRESCRIPTION_DOSE = By.cssSelector(".popupContent #dose");
  public static final By PRESCRIPTION_ROUTE_COMBOBOX = By.cssSelector(".popupContent #route div");
  public static final By PRESCRIPTION_ADDITIONAL_NOTES =
      By.cssSelector(".popupContent #additionalNotes");
  public static final By PRESCRIPTION_EDIT_BUTTON =
      By.xpath("//td//span[contains(@class, 'v-icon-edit')]");

  public static final By NEW_TREATMENT_BUTTON = By.cssSelector("#treatmentNewTreatment");
  public static final By TREATMENT_TYPE_COMBOBOX =
      By.cssSelector(".popupContent #treatmentType div");
  public static final By TREATMENT_DETAILS = By.cssSelector(".popupContent #treatmentDetails");
  public static final By TREATMENT_DATE =
      By.cssSelector(".popupContent #treatmentDateTime_date input");
  public static final By TREATMENT_TIME =
      By.cssSelector("[id='treatmentDateTime_time'] [class='v-filterselect-button']");
  public static final By TREATMENT_EXECUTING_STAFF_MEMBER =
      By.cssSelector(".popupContent #executingClinician");
  public static final By TREATMENT_DOSE = By.cssSelector(".popupContent #dose");
  public static final By TREATMENT_ROUTE_COMBOBOX = By.cssSelector(".popupContent #route div");
  public static final By TREATMENT_ADDITIONAL_NOTES =
      By.cssSelector(".popupContent #additionalNotes");
  public static final By TREATMENT_EDIT_BUTTON =
      By.xpath("//div[4]//td//span[contains(@class, 'v-icon-edit')]");
}
