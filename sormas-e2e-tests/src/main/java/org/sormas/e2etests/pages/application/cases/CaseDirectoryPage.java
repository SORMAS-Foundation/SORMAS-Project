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

public class CaseDirectoryPage {
  public static final By NEW_CASE_BUTTON = By.cssSelector("div#caseNewCase");
  public static final By TOTAL_CASES_COUNTER = By.cssSelector(".badge");
  public static final By CASE_DIRECTORY_DETAILED_RADIOBUTTON =
      By.cssSelector("#casesViewSwitcher span:nth-child(2)");
  public static final By CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT =
      By.cssSelector("input#caseLike");
  public static final By CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON =
      By.cssSelector("div#actionApplyFilters");
  public static final By CASE_DETAILED_TABLE_DATA = By.tagName("td");
  public static final By CASE_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By CASE_DETAILED_TABLE_ROWS =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By FIRST_CASE_ID_BUTTON = By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By NAME_UUID_EPID_NUMBER_LIKE_INPUT = By.cssSelector("input#caseLike");
  public static final By EPI_DATA_TAB = By.id("tab-cases-epidata");
  public static final By PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT =
      By.cssSelector("#personLike");
  public static final By CASE_OUTCOME_FILTER_COMBOBOX =
      By.cssSelector("[id='outcome'] [class='v-filterselect-button']");
  public static final By CASE_CLASSIFICATION_FILTER_COMBOBOX =
      By.cssSelector("[id='caseClassification'] [class='v-filterselect-button']");
  public static final By CASE_DISEASE_FILTER_COMBOBOX =
      By.cssSelector("[id='disease'] [class='v-filterselect-button']");
  public static final By CASE_DISEASE_VARIANT_FILTER_COMBOBOX =
      By.cssSelector("[id='diseaseVariant'] [class='v-filterselect-button']");
  public static final By CASE_APPLY_FILTERS_BUTTON = By.cssSelector("[id='actionApplyFilters']");
  public static final By CASE_RESET_FILTERS_BUTTON = By.cssSelector("[id='actionResetFilters']");
  public static final By ACTION_RESET_POPUP = By.cssSelector(".popupContent #actionReset");
  public static final By ACTION_SEARCH_POPUP = By.cssSelector(".popupContent #actionSearch");
  public static final By EXCLAMATION_MARK_PICK_OR_CREATE_PERSON_POPUP =
      By.cssSelector(".popupContent span[class='v-errorindicator v-errorindicator-error']");
  public static final By EXCLAMATION_MARK_MESSAGE_PICK_OR_CREATE_PERSON_POPUP =
      By.xpath("//div[@class='v-errormessage v-errormessage-error']");

  public static final By getCaseResultsUuidLocator(String uuid) {
    return By.cssSelector(String.format("[title = '%s']", uuid));
  }

  public static final By CASE_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By LINE_LISTING_BUTTON = By.id("lineListing");
  public static final By GRID_HEADERS = By.xpath("//thead//tr//th");
  public static final By GRID_RESULTS_DISEASE =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(4)");
  public static final By GRID_RESULTS_FIRST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(10)");
  public static final By GRID_RESULTS_LAST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(11)");
  public static final By GRID_RESULTS_DISTRICT =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(12)");
  public static final By GRID_RESULTS_HEALTH_FACILITY =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(13)");
  public static final By CASE_CLASSIFICATION_COLUMNS =
      By.cssSelector("[role=rowgroup] tr>td:nth-child(7)");
  public static final String RESULTS_GRID_HEADER = "//div[contains(text(), '%s')]";
  public static final By CASE_FOLLOWUP_FILTER_COMBOBOX =
      By.cssSelector("[id='followUpStatus'] [class='v-filterselect-button']");
  public static final By SHOW_MORE_LESS_FILTERS = By.id("showHideMoreFilters");
  public static final By CASE_ORIGIN_FILTER_COMBOBOX =
      By.cssSelector("[id='caseOrigin'] [class='v-filterselect-button']");
  public static final By CASE_COMMUNITY_FILTER_COMBOBOX =
      By.cssSelector("[id='community'] [class='v-filterselect-button']");
  public static final By CASE_PRESENT_CONDITION_COMBOBOX =
      By.cssSelector("[id='presentCondition'] [class='v-filterselect-button']");
  public static final By CASE_REGION_FILTER_COMBOBOX =
      By.cssSelector("[id='region'] [class='v-filterselect-button']");
  public static final By CASE_FACILITY_CATEGORY_FILTER_COMBOBOX =
      By.cssSelector("[id='facilityTypeGroup'] [class='v-filterselect-button']");
  public static final By CASE_FACILITY_TYPE_FILTER_COMBOBOX =
      By.cssSelector("[id='facilityType'] [class='v-filterselect-button']");
  public static final By CASE_FACILITY_FILTER_COMBOBOX =
      By.cssSelector("[id='healthFacility'] [class='v-filterselect-button']");
  public static final By CASE_SURVOFF_FILTER_COMBOBOX =
      By.cssSelector("[id='surveillanceOfficer'] [class='v-filterselect-button']");
  public static final By CASE_VACCINATION_STATUS_FILTER_COMBOBOX =
      By.cssSelector("[id='vaccinationStatus'] [class='v-filterselect-button']");
  public static final By CASE_QUARANTINE_FILTER_COMBOBOX =
      By.cssSelector("[id='quarantineType'] [class='v-filterselect-button']");
  public static final By CASE_REINFECTION_FILTER_COMBOBOX =
      By.cssSelector("[id='reinfectionStatus'] [class='v-filterselect-button']");
  public static final By CASE_DATA_TYPE_FILTER_COMBOBOX =
      By.cssSelector("[id='dateType'] [class='v-filterselect-button']");
  public static final By DATE_TYPE_FILTER_COMBOBOX =
      By.cssSelector("[id='newCaseDateType'] [class='v-filterselect-button']");
  public static final By CASE_DISPLAY_FILTER_COMBOBOX =
      By.cssSelector("[id='relevanceStatus'] [class='v-filterselect-button']");
  public static final By CASE_OWNERSHIP_FILTER_COMBOBOX =
      By.cssSelector("[id='ownershipStatus'] [class='v-filterselect-button']");
  public static final By BULK_ACTIONS = By.id("bulkActions-2");
  public static final By BULK_ACTIONS_VALUES = By.id("bulkActions-10");
  public static final By BULK_ACTIONS_ARCHIVE = By.xpath("//span[contains(text(),'Archive')]");
  public static final By BULK_ACTIONS_DE_ARCHIVE =
      By.xpath("//span[contains(text(),'De-Archive')]\n");
  public static final By BULK_CREATE_QUARANTINE_ORDER =
      By.xpath("//span[contains(text(),'Create quarantine order documents')]");
  public static final By CASE_REPORTING_USER_FILTER = By.cssSelector("[id='reportingUserLike']");
  public static final By CASE_YEAR_FILTER =
      By.cssSelector("[id='birthdateYYYY'] [class='v-filterselect-button']");
  public static final By CASE_MONTH_FILTER =
      By.cssSelector("[id='birthdateMM'] [class='v-filterselect-button']");
  public static final By CASE_DAY_FILTER =
      By.cssSelector("[id='birthdateDD'] [class='v-filterselect-button']");
  public static final By CASE_DISTRICT_FILTER_COMBOBOX =
      By.cssSelector("[id='district'] [class='v-filterselect-button']");
  public static final By CASES_WITHOUT_GEO_COORDINATES_CHECKBOX = By.id("mustHaveNoGeoCoordinates");
  public static final By CASES_WITHOUT_RESPONSIBLE_OFFICER_CHECKBOX =
      By.id("withoutResponsibleOfficer");
  public static final By CASES_WITH_EXTENDED_QUARANTINE_CHECKBOX = By.id("withExtendedQuarantine");
  public static final By CASES_WITH_REDUCED_QUARANTINE_CHECKBOX = By.id("withReducedQuarantine");
  public static final By CASES_HELP_NEEDED_IN_QUARANTINE_CHECKBOX =
      By.id("onlyQuarantineHelpNeeded");
  public static final By CASES_WITH_EVENTS_CHECKBOX = By.id("onlyCasesWithEvents");
  public static final By CASES_FROM_OTHER_INSTANCES_CHECKBOX =
      By.id("onlyContactsFromOtherInstances");
  public static final By CASES_WITH_REINFECTION_CHECKBOX = By.id("onlyCasesWithReinfection");
  public static final By CASES_FROM_OTHER_JURISDICTIONS_CHECKBOX =
      By.id("includeCasesFromOtherJurisdictions");
  public static final By CASES_WITH_FULFILLED_REFERENCE_DEFINITION_CHECKBOX =
      By.id("onlyShowCasesWithFulfilledReferenceDefinition");
  public static final By CASES_WITHOUT_FACILITY_CHECKBOX =
      By.id("mustBePortHealthCaseWithoutFacility");
  public static final By ALLBUTTON = By.id("all");
  public static final By INVESTIGATION_PENDING_BUTTON = By.id("Investigation pending");
  public static final By INVESTIGATION_DONE_BUTTON = By.id("Investigation done");
  public static final By INVESTIGATION_DISCARDED_BUTTON = By.id("Investigation discarded");
  public static final By DATE_FROM_COMBOBOX = By.cssSelector("#dateFrom input");
  public static final By NEW_CASE_DATE_FROM_COMBOBOX = By.cssSelector("#newCaseDateFrom input");
  public static final By DATE_TO_COMBOBOX = By.cssSelector("#dateTo input");
  public static final By MORE_BUTTON = By.id("more");
  public static final By ACTION_OKAY = By.id("actionOkay");
  public static final By MERGE_DUPLICATES_BUTTON = By.id("caseMergeDuplicates");
  public static final By ENTER_BULK_EDIT_MODE = By.id("actionEnterBulkEditMode");

  public static final By BULK_EDIT_INFORMATION =
      By.xpath("//div[@class='v-window-header' and text()='Massenbearbeitung aktivieren']");
  public static final By LEAVE_BULK_EDIT_MODE = By.id("actionLeaveBulkEditMode");
  public static final By ALL_RESULTS_CHECKBOX =
      By.xpath("//th[@role='columnheader']//input[@type='checkbox']/../..");
  public static final By ALL_RESULTS_CHECKBOXES_LIST =
      By.xpath("//table[@role='grid']//input[@tabindex='0']");
  public static final By NEW_EVENT_CHECKBOX =
      By.xpath("//div[@class='v-select-optiongroup v-widget']/span");
  public static final By FIRST_RESULT_IN_GRID =
      By.xpath("//div[contains(@class, 'popupContent')]//tr[@role='row']");
  public static final By SEARCH_BUTTON = By.id("search");
  public static final By CASE_EPIDEMIOLOGICAL_DATA_TAB = By.cssSelector("#tab-cases-epidata");
  public static final By EPIDEMIOLOGICAL_DATA_TAB = By.cssSelector("#tab-cases-epidata");
  public static final By CONTACTS_DATA_TAB = By.cssSelector("#tab-cases-contacts");
  public static final By FIRST_PERSON_ID = By.xpath("//td[9]//a");
  public static final By FIRST_CASE_ID = By.xpath("//td[1]//a");
  public static final By IMPORT_BUTTON = By.id("actionImport");
  public static final By DETAILED_IMPORT_BUTTON = By.id("importDetailed");
  public static final By DOWNLOAD_IMPORT_GUIDE_BUTTON = By.id("import-step-1");
  public static final By DOWNLOAD_DATA_DICTIONARY_BUTTON = By.id("importDownloadDataDictionary");
  public static final By FACILITY_ACTIVITY_AS_CASE_COMBOBOX =
      By.cssSelector(".v-window #typeOfPlace div");
  public static final By MERGE_DUPLICATES_WARNING_POPUP =
      By.xpath("//div[@class='v-window-wrap']//div[@class='v-slot']/div");
  public static final By CASE_MEANS_OF_TRANSPORT =
      By.cssSelector(".v-window #meansOfTransport div");
  public static final By CASE_MEANS_OF_TRANSPORT_DETAILS = By.id("meansOfTransportDetails");
  public static final By CASE_CONNECTION_NUMBER = By.id("connectionNumber");
  public static final By CASE_SEAT_NUMBER = By.id("seatNumber");
  public static final By UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX =
      By.xpath("//div[@class='v-slot v-slot-force-caption-checkbox']/span/label");
  public static final By CLOSE_FORM_BUTTON = By.xpath("//div[@class='v-window-closebox']");
  public static final By REINFECTION_STATUS_COMBOBOX =
      By.cssSelector("[id='reinfectionStatus'] [class='v-filterselect-button']");
  public static final By CREATE_NEW_PERSON_CHECKBOX =
      By.xpath("//div[@class='v-select-optiongroup v-widget']/span/label");
  public static final By POPUP_WINDOW_HEADER = By.xpath("//div[@class='v-window-closebox']");
  public static final By ACTION_REJECT_BUTTON = By.cssSelector("#actionReject");
  public static final By POTENTIAL_DUPLICATE_POPUP_DE =
      By.xpath("//*[contains(text(), 'Potentielle Duplikate erkannt')]");
  public static final By CASE_ARCHIVED_POPUP = By.cssSelector(".v-Notification-caption");

  public static By getActionAcceptButtonByCaseDescription(String description) {
    return By.xpath(
        String.format(
            "//td[contains(text(), 'Fall')]/../*[contains(text(), '%s')]/..//div[@id='actionAccept']",
            description));
  }

  public static By getActionAcceptButtonByContactDescription(String description) {
    return By.xpath(
        String.format(
            "//td[contains(text(), 'Kontakt')]/../*[contains(text(), '%s')]/..//div[@id='actionAccept']",
            description));
  }

  public static final By REJECT_SHARED_CASE_POPUP_TEXT_AREA =
      By.cssSelector(".popupContent .v-widget textarea");
  public static final By RECEIVED_CASE_POPUP_UUID = By.xpath("//div[@class='popupContent']//td[1]");

  public static By getVaccinationStatusCasesByText(String status) {
    return By.xpath(String.format("//td[contains(text(), '%s')]", status));
  }

  public static By getCheckboxByIndex(String idx) {
    return By.xpath(String.format("(//input[@type=\"checkbox\"])[%s]", idx));
  }

  public static By getResultByIndex(String rowNumber) {
    return By.xpath(String.format("//tr[%s]//a", rowNumber));
  }

  public static final By CASE_CLOSE_WINDOW_BUTTON =
      By.xpath("//div[contains(@class,'v-window-closebox')]");
  public static final By CASE_INFO_BUTTON = By.cssSelector("[id='info']");
  // TODO refactor the other headers based on the last one added
  public static By getMergeDuplicatesButtonById(String uuid) {
    return By.xpath(
        String.format(
            "//td//a//span[text()='%s']/../../../../../..//div[@id=\"actionMerge\"]",
            uuid.substring(0, 6).toUpperCase()));
  }

  public static By CONFIRM_POPUP = By.cssSelector(".popupContent #actionConfirm");
  public static final By CANCEL_POPUP = By.cssSelector(".popupContent #actionCancel");
  public static final By ENTER_BULK_EDIT_MODE_POPUP_HEADER =
      By.cssSelector(
          "[class='popupContent'] [class='v-window-outerheader'] div[class='v-window-header']");
  public static final By DE_ARCHIVE_EDIT_MODE_POPUP_HEADER =
      By.cssSelector(
          "[class='popupContent'] [class='v-window-outerheader'] div[class='v-window-header']");
  public static By SHARE_OPTION_BULK_ACTION_COMBOBOX =
      By.xpath("//span[text()=\"\u00DCbergeben\"]");
  public static By REJECT_SHARED_CASE_HEADER_DE =
      By.xpath("//div[@class='popupContent']//*[contains(text(), 'Anfrage ablehnen')]");

  public static By getMergeButtonForCaseForTargetSystem(String firstName, String lastName) {
    return By.xpath(
        String.format(
            "(//td[contains(text(), '"
                + firstName
                + "')]/../td[contains(text(), '"
                + lastName
                + "')]/..//div[@id='actionMerge'])[1]"));
  }

  public static By getMergeButtonForCaseForSourceSystem(String firstName, String lastName) {
    return By.xpath(
        String.format(
            "(//td[contains(text(), '"
                + firstName
                + "')]/../td[contains(text(), '"
                + lastName
                + "')]/..//div[@id='actionMerge'])[2]"));
  }

  public static By getBulkModeCaseCheckboxByIndex(int index) {
    return By.xpath(String.format("(//td/span/input[@type='checkbox'])[%x]", index));
  }

  public static By CONFIRM_YOUR_CHOICE_HEADER_DE =
      By.xpath("//div[@class='popupContent']//div[contains(text(), 'Ihre Wahl best\u00E4tigen')]");
  public static By MERGE_DUPLICATED_CASES_WARNING_POPUP_DE =
      By.xpath("//p[contains(text(), 'Dieser Fall kann nicht mehr bearbeitet werden')]");
  public static By ERROR_MESSAGE_HEADER_DE =
      By.xpath("//div[@class='popupContent']//h1[contains(text(), 'Ein Fehler ist aufgetreten')]");

  public static By MERGE_MESSAGE_HEADER_DE =
      By.xpath(
          "//*[contains(text(), 'F\u00E4lle wurden zusammengef\u00FChrt und der doppelte Fall gel\u00F6scht')]");
  public static By WARNING_CASE_NOT_SHARED_SHARE_POPUP_DE =
      By.xpath(
          "//div[contains(text(), 'Wenn Sie diesen Kontakt teilen m\u00F6chten, m\u00FCssen Sie den zugeh\u00F6rigen Fall zuerst an das gleiche Zielsystem senden.')]");
  public static By SEND_TO_REPORTING_TOOL_BUTTON = By.xpath("//span[contains(text(), 'Senden')]");
  public static final By RELEVANT_STATUS_INPUT =
      By.cssSelector("[id='relevanceStatus'] [class='v-filterselect-input']");

  public static final By RELEVANT_STATUS_COMBOBOX = By.xpath("//div[@id='relevanceStatus']/div");
  public static final By POPUP_NOTIFICATION_DESCRIPTION =
      By.cssSelector(".v-Notification-description");
  public static final By POPUP_NOTIFICATION_CAPTION = By.cssSelector(".v-Notification-caption");

  public static By getCheckboxInputById(Integer index) {
    return By.xpath(String.format("//label[contains(text(), 'Selects row number %d.')]/..", index));
  }

  public static By getCaseUUIDBasedOnRowInTable(Integer index) {
    return By.xpath(
        String.format(
            "//label[contains(text(), 'Selects row number %d.')]/../../../td[2]/a", index));
  }

  public static final By BULK_OPERATION_PROGRESS_BAR =
      By.xpath("//div[contains(text(), 'Bulk operation progress')]");
  public static final By PROGRESSBAR_TOTAL_NUMBER_OF_CASES_LABEL =
      By.xpath("//div[contains(text(), 'Completed')]");
  public static final By PROGRESSBAR_TOTAL_NUMBER_OF_SUCCESSFUL_CASES_LABEL =
      By.xpath("//div[contains(text(), 'Successful')]");
  public static final By PROGRESSBAR_TOTAL_NUMBER_OF_SKIPPED_CASES_LABEL =
      By.xpath("//div[contains(text(), 'Skipped')]");
  public static final By BULK_MODE_SUCCESS_IMAGE = By.cssSelector(".popupContent .v-image");
  public static final By BULK_RESTORE_BUTTON = By.xpath("//span[contains(text(),'Restore')]");
  public static final By CONFIRM_RESTORATION_WINDOWS_HEADER =
      By.xpath("//div[contains(text(), 'Confirm restoration')]");
}
