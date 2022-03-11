/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class EditCasePage {
  public static final By CALCULATE_CASE_CLASSIFICATION_BUTTON =
      By.id("caseClassificationCalculationButton");
  public static final By FOLLOW_UP_TAB = By.cssSelector("[id='tab-cases-visits'] a");
  public static final By SYMPTOMS_TAB = By.cssSelector("[id='tab-cases-symptoms']");
  public static final By THERAPY_TAB = By.cssSelector("[id='tab-cases-therapy']");
  public static final By HOSPITALIZATION_TAB = By.cssSelector("[id='tab-cases-hospitalization']");
  public static final By BACK_TO_CASES_LIST_BUTTON = By.id("tab-cases");
  public static final By REGION_INPUT = By.cssSelector("#responsibleRegion input");
  public static final By DISTRICT_INPUT = By.cssSelector("#responsibleDistrict input");
  public static final By COMMUNITY_INPUT = By.cssSelector("#responsibleCommunity input");
  public static final By PLACE_OF_STAY_SELECTED_VALUE =
      By.cssSelector("#facilityOrHome input[checked] + label");
  public static final By PLACE_DESCRIPTION_INPUT = By.cssSelector("#healthFacilityDetails");
  public static final By EXTERNAL_ID_INPUT = By.id("externalID");
  public static final By UUID_INPUT = By.id("uuid");
  public static final By DISEASE_INPUT = By.cssSelector("#disease input");
  public static final By DISEASE_VARIANT_INPUT = By.cssSelector("#diseaseVariant input");
  public static final By USER_INFORMATION =
      By.cssSelector(".v-slot-view-header .v-slot-primary div");
  public static final By CASE_PERSON_TAB = By.cssSelector("div#tab-cases-person");
  public static final By NEW_TASK_BUTTON = By.cssSelector("div#taskNewTask");
  public static final By EDIT_TASK_BUTTON = By.cssSelector("div[id*='edit-task']");
  public static final By NEW_SAMPLE_BUTTON = By.cssSelector("[id='New sample']");
  public static final By EDIT_SAMPLE_BUTTON =
      By.cssSelector(
          "[location='samples'] [class='v-button v-widget link v-button-link compact v-button-compact']");
  public static final By REPORT_DATE_INPUT = By.cssSelector("#reportDate input");
  public static final By CASE_CLASSIFICATION_COMBOBOX = By.cssSelector("#caseClassification div");
  public static final By CASE_CLASSIFICATION_INPUT = By.cssSelector("#caseClassification input");
  public static final By CLINICAL_CONFIRMATION_COMBOBOX =
      By.cssSelector("#clinicalConfirmation div");
  public static final By EPIDEMIOLOGICAL_CONFIRMATION_COMBOBOX =
      By.cssSelector("#epidemiologicalConfirmation div");
  public static final By LABORATORY_DIAGNOSTIC_CONFIRMATION_COMBOBOX =
      By.cssSelector("#laboratoryDiagnosticConfirmation div");
  public static final By INVESTIGATION_STATUS_OPTIONS =
      By.cssSelector("#investigationStatus label");
  public static final By INVESTIGATED_DATE_FIELD = By.cssSelector("#investigatedDate");
  public static final By EXTERNAL_TOKEN_INPUT = By.id("externalToken");
  public static final By DISEASE_COMBOBOX = By.cssSelector("#disease div");
  public static final By REINFECTION_OPTIONS = By.cssSelector("#reInfection label");
  public static final By OUTCOME_OF_CASE_OPTIONS = By.cssSelector("#outcome label");
  public static final By DATE_OF_OUTCOME = By.cssSelector("#outcomeDate");
  public static final By SEQUELAE_OPTIONS = By.cssSelector("#sequelae label");
  public static final By SEQUELAE_DETAILS = By.cssSelector("#sequelaeDetails");
  public static final By PLACE_OF_STAY_CHECKBOX_LABEL =
      By.xpath("//*[@id='differentPlaceOfStayJurisdiction']/label");
  public static final By PLACE_OF_STAY_CHECKBOX_INPUT =
      By.xpath("//*[@id='differentPlaceOfStayJurisdiction']/input");
  public static final By PLACE_OF_STAY_REGION_COMBOBOX = By.cssSelector("#region div");
  public static final By PLACE_OF_STAY_DISTRICT_COMBOBOX = By.cssSelector("#district div");
  public static final By REPORTING_DISTRICT_COMBOBOX = By.cssSelector("#reportingDistrict div");
  public static final By CASE_IDENTIFICATION_SOURCE_COMBOBOX =
      By.cssSelector("#caseIdentificationSource div");
  public static final By PLACE_OF_STAY_OPTIONS =
      By.cssSelector("[location='facilityOrHomeLoc'] label");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector("#typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector("#facilityType div");
  public static final By FACILITY_HEALTH_COMBOBOX = By.cssSelector("#healthFacility div");
  public static final By FACILITY_ACTIVITY_COMBOBOX =
      By.cssSelector("[id='typeOfPlace'] [class='v-filterselect-button']");
  public static final By REGION_COMBOBOX = By.cssSelector("#responsibleRegion div");
  public static final By DISTRICT_COMBOBOX = By.cssSelector("#responsibleDistrict div");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector("#responsibleCommunity div");
  public static final By RESPONSIBLE_REGION_COMBOBOX = By.cssSelector("#responsibleRegion div");
  public static final By RESPONSIBLE_DISTRICT_COMBOBOX = By.cssSelector("#responsibleDistrict div");
  public static final By COMMUNITY_COMBOBOX_BY_PLACE_OF_STAY = By.cssSelector("#community div");
  public static final By RESPONSIBLE_COMMUNITY_COMBOBOX =
      By.cssSelector("#responsibleCommunity div");
  public static final By PROHIBITION_TO_WORK_OPTIONS = By.cssSelector("#prohibitionToWork label");
  public static final By HOME_BASED_QUARANTINE_POSSIBLE_OPTIONS =
      By.cssSelector("#quarantineHomePossible label");
  public static final By QUARANTINE_COMBOBOX = By.cssSelector("#quarantine div");
  public static final By QUARANTINE_DATE_FROM = By.cssSelector("#quarantineFrom");
  public static final By QUARANTINE_DATE_FROM_INPUT = By.cssSelector("#quarantineFrom input");
  public static final By QUARANTINE_DATE_TO = By.cssSelector("#quarantineTo");
  public static final By QUARANTINE_DATE_TO_INPUT = By.cssSelector("#quarantineTo input");
  public static final By QUARANTINE_CHANGE_COMMENT = By.cssSelector("#quarantineChangeComment");
  public static final By QUARANTINE_POPUP_MESSAGE =
      By.xpath("//div[@class='v-label v-widget v-has-width']");
  public static final By QUARANTINE_POPUP_SAVE_BUTTON =
      By.cssSelector(".popupContent #actionConfirm");
  public static final By QUARANTINE_POPUP_DISCARD_BUTTON =
      By.cssSelector(".popupContent #actionCancel");
  public static final By DISCARD_BUTTON_POPUP = By.cssSelector(".popupContent #discard");
  public static final By QUARANTINE_ORDERED_VERBALLY_CHECKBOX_LABEL =
      By.xpath("//*[@id='quarantineOrderedVerbally']/label");
  public static final By QUARANTINE_ORDERED_VERBALLY_CHECKBOX_INPUT =
      By.xpath("//*[@id='quarantineOrderedVerbally']/input");
  public static final By QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_LABEL =
      By.xpath("//*[@id='quarantineOrderedOfficialDocument']/label");
  public static final By QUARANTINE_ORDERED_BY_DOCUMENT_CHECKBOX_INPUT =
      By.xpath("//*[@id='quarantineOrderedOfficialDocument']/input");
  public static final By OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_LABEL =
      By.xpath("//*[@id='quarantineOfficialOrderSent']/label");
  public static final By OFFICIAL_QUARANTINE_ORDER_SENT_CHECKBOX_INPUT =
      By.xpath("//*[@id='quarantineOfficialOrderSent']/input");
  public static final By QUARANTINE_ORDERED_BY_DOCUMENT_DATE =
      By.cssSelector("#quarantineOrderedOfficialDocumentDate");
  public static final By DATE_OF_THE_VERBAL_ORDER =
      By.cssSelector("#quarantineOrderedVerballyDate");
  public static final By DATE_OFFICIAL_QUARANTINE_ORDER_WAS_SENT =
      By.cssSelector("#quarantineOfficialOrderSentDate");
  public static final By QUARANTINE_TYPE_DETAILS = By.cssSelector("#quarantineTypeDetails");
  public static final By REPORT_GPS_LATITUDE_INPUT = By.cssSelector("input#reportLat");
  public static final By REPORT_GPS_LONGITUDE_INPUT = By.cssSelector("input#reportLon");
  public static final By REPORT_GPS_ACCURACY_IN_M_INPUT =
      By.cssSelector("input#reportLatLonAccuracy");
  public static final By BLOOD_ORGAN_TISSUE_DONATION_IN_THE_LAST_6_MONTHS_OPTIONS =
      By.cssSelector("#bloodOrganOrTissueDonated label");
  public static final By VACCINATION_STATUS_FOR_THIS_DISEASE_COMBOBOX =
      By.cssSelector("#vaccinationStatus div");
  public static final By RESPONSIBLE_SURVEILLANCE_OFFICER_COMBOBOX =
      By.cssSelector("#surveillanceOfficer div");
  public static final By DATE_RECEIVED_AT_DISTRICT_LEVEL_INPUT =
      By.cssSelector("#districtLevelDate input");
  public static final By DATE_RECEIVED_AT_REGION_LEVEL_INPUT =
      By.cssSelector("#regionLevelDate input");
  public static final By DATE_RECEIVED_AT_NATIONAL_LEVEL_INPUT =
      By.cssSelector("#nationalLevelDate input");
  public static final By GENERAL_COMMENT_TEXTAREA = By.cssSelector("textarea#additionalDetails");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By ACTION_CANCEL = By.cssSelector(".popupContent #actionCancel");
  public static final By DELETE_BUTTON = By.id("delete");
  public static final By DELETE_POPUP_YES_BUTTON = By.cssSelector(".popupContent #actionConfirm");
  public static final By CASE_SAVED_POPUP = By.cssSelector(".v-Notification-caption");
  public static final By EXTRA_COMMENT_INPUT =
      By.cssSelector(".popupContent [class='v-textfield v-widget v-has-width']");
  public static final By CREATE_DOCUMENT_BUTTON = By.cssSelector("[id='Create']");
  public static final By CREATE_QUARANTINE_ORDER_BUTTON =
      By.cssSelector(".popupContent [id='Create']");
  public static final By EXTRA_COMMENT_TEXTAREA =
      By.cssSelector(".popupContent [class='v-textfield v-widget v-has-width']");
  public static final By QUARANTINE_ORDER_COMBOBOX =
      By.cssSelector(".popupContent div[role='combobox'] div");
  public static final By CLINICAL_COURSE_TAB = By.id("tab-cases-clinicalcourse");
  public static final By NEW_TRAVEL_ENTRY_BUTTON_DE = By.id("Neue Einreise");
}
