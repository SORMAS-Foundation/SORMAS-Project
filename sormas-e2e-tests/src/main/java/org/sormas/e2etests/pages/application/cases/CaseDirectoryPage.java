/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
  public static final By PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT =
      By.cssSelector("#personLike");
  public static final By CASE_OUTCOME_FILTER_COMBOBOX =
      By.cssSelector("[id='outcome'] [class='v-filterselect-button']");
  public static final By CASE_CLASSIFICATION_FILTER_COMBOBOX =
      By.cssSelector("[id='caseClassification'] [class='v-filterselect-button']");
  public static final By CASE_DISEASE_FILTER_COMBOBOX =
      By.cssSelector("[id='disease'] [class='v-filterselect-button']");
  public static final By CASE_APPLY_FILTERS_BUTTON = By.cssSelector("[id='actionApplyFilters']");
  public static final By CASE_RESET_FILTERS_BUTTON = By.cssSelector("[id='actionResetFilters']");
  public static final String CASE_RESULTS_UUID_LOCATOR = "[title = '%s']";
  public static final By CASE_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By LINE_LISTING_BUTTON = By.id("lineListing");
  public static final By GRID_HEADERS = By.xpath("//thead//tr//th");
  public static final By GRID_RESULTS_DISEASE =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(5)");
  public static final By GRID_RESULTS_FIRST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(11)");
  public static final By GRID_RESULTS_LAST_NAME =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(12)");
  public static final By GRID_RESULTS_DISTRICT =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(13)");
  public static final By GRID_RESULTS_HEALTH_FACILITY =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(14)");
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
  public static final By CASE_DISPLAY_FILTER_COMBOBOX =
      By.cssSelector("[id='relevanceStatus'] [class='v-filterselect-button']");
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
  public static final By DATE_TO_COMBOBOX = By.cssSelector("#dateTo input");
  // TODO refactor the other headers based on the last one added
  public static final By CASE_DATA_TAB = By.cssSelector("#tab-cases-data");
  public static final By CASE_INFO_BUTTON = By.cssSelector("[id='info']");
  public static final By CASE_CLOSE_WINDOW_BUTTON =
      By.xpath("//div[contains(@class,\"v-window-closebox\")]");
}
