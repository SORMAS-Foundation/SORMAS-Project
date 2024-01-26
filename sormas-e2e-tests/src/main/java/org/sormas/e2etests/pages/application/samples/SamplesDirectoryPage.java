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

package org.sormas.e2etests.pages.application.samples;

import org.openqa.selenium.By;

public class SamplesDirectoryPage {
  public static final By SAMPLE_SEARCH_INPUT = By.cssSelector("[id='caseCodeIdLike']");
  public static final By TOTAL_SAMPLE_COUNTER = By.cssSelector(".badge");
  public static final By SAMPLE_EDIT_PURPOSE_OPTIONS =
      By.cssSelector("#samplePurpose .v-select-option");
  public static final By TEST_RESULTS_SEARCH_COMBOBOX =
      By.cssSelector("[id='pathogenTestResult'] [class='v-filterselect-button']");
  public static final By SAMPLE_GRID_RESULTS_ROWS =
      By.cssSelector("[class='v-grid-tablewrapper'] tbody>tr");
  public static final By RELEVANCE_STATUS_FILTER_COMBOBOX =
      By.cssSelector("[id='relevanceStatusFilter'] [class='v-filterselect-button']");
  public static final By SPECIMEN_CONDITION_SEARCH_COMBOBOX =
      By.cssSelector("[id='specimenCondition'] [class='v-filterselect-button']");
  public static final By LABORATORY_SEARCH_COMBOBOX =
      By.cssSelector("[id='laboratory'] [class='v-filterselect-button']");
  public static final By RESET_FILTER_BUTTON = By.cssSelector("[id='actionResetFilters']");
  public static final By APPLY_FILTER_BUTTON = By.cssSelector("[id='actionApplyFilters']");
  public static final By RESULT_VERIFIED_BY_LAB_SUPERVISOR_EDIT_OPTIONS =
      By.cssSelector(".popupContent #testResultVerified .v-select-option");
  public static final By SEARCH_RESULT_SAMPLE = By.cssSelector("[role='gridcell'] a");
  public static final By FINAL_LABORATORY_RESULT =
      By.cssSelector(("tbody>tr:first-child>td:last-child"));
  public static final By EDIT_TEST_RESULTS_BUTTON =
      By.cssSelector("[location='pathogenTests'] [class='v-slot v-slot-s-list'] [role='button']");
  public static final By EDIT_ADDITIONAL_TEST_RESULTS_BUTTON =
      By.cssSelector("[location='additionalTests'] [class='v-slot v-slot-s-list'] [role='button']");
  public static final By SAMPLE_CLASIFICATION_SEARCH_COMBOBOX =
      By.cssSelector("[id='caseClassification'] [class='v-filterselect-button']");
  public static final By SAMPLE_DISEASE_SEARCH_COMBOBOX =
      By.cssSelector("[id='disease'] [class='v-filterselect-button']");
  public static final By SAMPLE_REGION_SEARCH_COMBOBOX =
      By.cssSelector("[id='region'] [class='v-filterselect-button']");
  public static final By SAMPLE_DISTRICT_SEARCH_COMBOBOX =
      By.cssSelector("[id='district'] [class='v-filterselect-button']");
  public static final By SAMPLE_NOT_SHIPPED = By.id("sampleNotShipped");
  public static final By SAMPLE_SHIPPED = By.id("sampleShipped");
  public static final By SAMPLE_RECEIVED = By.id("sampleReceived");
  public static final By SAMPLE_SHIPPED_CHECKBOX = By.xpath("//span[@id='shipped']/label");
  public static final By SAMPLE_RECEIVED_CHECKBOX = By.xpath("//span[@id='received']/label");
  public static final By SAMPLE_REFFERED_TO_OTHER_LAB = By.id("sampleReferred");
  public static final By CREATE_CASE_POSITIVE_TEST_RESULT_LABEL =
      By.cssSelector(".popupContent [class='v-window-header']");
  public static final By CONFIRM_BUTTON = By.cssSelector(".popupContent [id='actionConfirm']");
  public static final By EDIT_PATHOGEN_TEST_BUTTON =
      By.xpath(
          "//div[@class='v-button v-widget link v-button-link compact v-button-compact caption-overflow-label v-button-caption-overflow-label']");
  public static final By EXPORT_SAMPLE_BUTTON = By.id("export");
  public static final By BASIC_EXPORT_SAMPLE_BUTTON = By.id("exportBasic");
  public static final By DETAILED_EXPORT_SAMPLE_BUTTON = By.id("exportDetailed");
  public static final By COMMIT_BUTTON = By.cssSelector("#commit");
  public static final By PENDING_TEST_TABLE_RESULTS = By.xpath("//td[text()='Pending']");
  public static final By POSITIVE_TEST_TABLE_RESULTS = By.xpath("//td[text()='Positive']");
}
