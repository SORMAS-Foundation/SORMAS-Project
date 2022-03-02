package org.sormas.e2etests.pages.application.immunizations;

import org.openqa.selenium.By;

public class CreateNewImmunizationPage {
  public static final By DATE_OF_REPORT_INPUT = By.cssSelector(".v-window #reportDate input");
  public static final By DISEASE_COMBOBOX = By.cssSelector(".v-window #disease div");
  public static final By RESPONSIBLE_REGION_COMBOBOX =
      By.cssSelector(".v-window #responsibleRegion div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX =
      By.cssSelector(".v-window #responsibleDistrict div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX =
      By.cssSelector(".v-window #responsibleCommunity div");
  public static final By PLACE_OF_STAY_HOME =
      By.xpath("//div[@location='facilityOrHomeLoc']//label[contains(text(), 'Home')]");
  public static final By FIRST_NAME_INPUT =
      By.cssSelector(".v-window [location='firstName'] input");
  public static final By LAST_NAME_INPUT = By.cssSelector(".v-window [location='lastName'] input");
  public static final By SEX_COMBOBOX =
      By.cssSelector(".v-window [location='sex'] div[role='combobox'] div");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By CASE_ORIGIN_OPTIONS =
      By.cssSelector(".popupContent #caseOrigin .v-select-option");
  public static final By EXTERNAL_ID_INPUT = By.cssSelector(".popupContent #externalId");
  public static final By PLACE_OF_STAY =
      By.cssSelector(".popupContent div[location='facilityOrHomeLoc'] span.v-select-option label");
  public static final By PLACE_DESCRIPTION_INPUT =
      By.cssSelector(".popupContent #healthFacilityDetails");
  public static final By DATE_OF_BIRTH_YEAR_COMBOBOX =
      By.cssSelector(".popupContent #birthdateYYYY input+div");
  public static final By DATE_OF_BIRTH_MONTH_COMBOBOX =
      By.cssSelector(".popupContent #birthdateMM input+div");
  public static final By DATE_OF_BIRTH_DAY_COMBOBOX =
      By.cssSelector(".popupContent #birthdateDD input+div");
  public static final By PRESENT_CONDITION_OF_PERSON_COMBOBOX =
      By.cssSelector(".v-window [location='presentCondition'] div[role='combobox'] div");
  public static final By DATE_OF_SYMPTOM_ONSET_INPUT = By.cssSelector(".v-window #onsetDate input");
  public static final By PRIMARY_PHONE_NUMBER_INPUT = By.cssSelector(".v-window #phone");
  public static final By PRIMARY_EMAIL_ADDRESS_INPUT = By.cssSelector(".v-window #emailAddress");
  public static final By FACILITY_CATEGORY_COMBOBOX =
      By.cssSelector(".popupContent #typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector(".popupContent #type div");
  public static final By FACILITY_COMBOBOX = By.cssSelector(".popupContent #healthFacility div");
  public static final By FACILITY_DESCRIPTION = By.id("healthFacilityDetails");
  public static final By EVENT_STATUS_OPTIONS =
      By.cssSelector(".popupContent #eventStatus .v-select-option label");
  public static final By RISK_LEVEL_COMBOBOX = By.cssSelector(".popupContent #riskLevel div");
  public static final By EVENT_MANAGEMENT_STATUS_OPTIONS =
      By.cssSelector(".popupContent #eventManagementStatus .v-select-option label");
  public static final By MEANS_OF_IMMUNIZATIONS_COMBOBOX =
      By.cssSelector(".v-window #meansOfImmunization div");
  public static final By END_DATA_INPUT = By.cssSelector(".popupContent #endDate input");
  public static final By OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS_INPUT =
      By.xpath("//*[@id='overwriteImmunizationManagementStatus']/label");
  public static final By DISCARD_IMMUNIZATION_BUTTON = By.cssSelector("#discard");
  public static final By MANAGEMENT_STATUS =
      By.xpath("//div[@id='immunizationManagementStatus']//input");
}
