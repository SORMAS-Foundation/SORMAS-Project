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
package org.sormas.e2etests.pages.application.statistics;

import org.openqa.selenium.By;

public class StatisticsPage {
  public static final By ADD_FILTER_BUTTON = By.id("statisticsAddFilter");
  public static final By RESET_FILTER_BUTTON = By.id("statisticsResetFilters");
  public static final By REMOVE_FILTER_BUTTON = By.id("close");
  public static final By SELECT_ATTRIBUTE_DROPDOWN = By.cssSelector("[id='statisticsAttribute-0']");
  public static final By SELECT_DISEASE_DROPDOWN =
      By.cssSelector("[class='v-filterselect-button']");
  public static final By ATTRIBUTE_DROPDOWN_VALUES =
      By.cssSelector("[class='v-menubar-menuitem-caption']");
  public static final By DISEASE_DROPDOWN_VALUES = By.cssSelector("[class='gwt-MenuItem']");
  public static final By VISUALISATION_TYPE_TABLE_RADIO_BUTTON =
      By.xpath("//label[text()='Table']");
  public static final By VISUALISATION_TYPE_MAP_RADIO_BUTTON = By.xpath("//label[text()='Map']");
  public static final By VISUALISATION_TYPE_CHART_RADIO_BUTTON =
      By.xpath("//label[text()='Chart']");
  public static final By TABLE_ROWS_DROPDOWN = By.id("visualizationType");
  public static final By SWITCH_ROWS_AND_COLUMNS_BUTTON = By.id("switchRowsAndColumns");
  public static final By DATABASE_EXPORT_TAB = By.cssSelector("#tab-statistics-database-export");
  public static final By MAP_TYPE_REGIONS_RADIO_BUTTON = By.xpath("//label[text()='Regions']");
  public static final By MAP_TYPE_DISTRICTS_RADIO_BUTTON = By.xpath("//label[text()='Districts']");
  public static final By CHART_TYPE_COLUMN_RADIO_BUTTON = By.xpath("//label[text()='Column']");
  public static final By CHART_TYPE_STACKED_COLUMN_RADIO_BUTTON =
      By.xpath("//label[text()='Stacked column']");
  public static final By CHART_TYPE_LINE_RADIO_BUTTON = By.xpath("//label[text()='Line']");
  public static final By CHART_TYPE_PIE_RADIO_BUTTON = By.xpath("//label[text()='Pie']");
  public static final By DATA_DISPLAYED_CASE_COUNT_RADIO_BUTTON =
      By.xpath("//label[text()='Case count']");
  public static final By DATA_DISPLAYED_CASE_INCIDENCE_RADIO_BUTTON =
      By.xpath("//label[text()='Case incidence']");
  public static final By SHOW_ZERO_VALUES_CHECKBOX = By.id("statisticsShowZeroValues");
  public static final By INCIDENCE_DIVISOR_INPUT = By.id("incidenceDivisor");
  public static final By GENERATE_BUTTON = By.id("actionGenerate");
  public static final By SELECT_ALL_BUTTON_EXPORT_DATA = By.id("actionSelectAll");
  public static final By SELECT_ALL_SORMAS_DATA_BUTTON_EXPORT_DATA =
      By.id("exportSelectSormasData");
  public static final By DESELECT_ALL_SORMAS_DATA_BUTTON_EXPORT_DATA = By.id("actionDeselectAll");
  public static final By CASES_CHECKBOX =
      By.xpath("//label[text()='Cases']/preceding-sibling::input");
  public static final By HOSPITALIZATIONS_CHECKBOX =
      By.xpath("//label[text()='Hospitalizations']/preceding-sibling::input");
  public static final By PREVIOUS_HOSPITALIZATIONS_CHECKBOX =
      By.xpath("//label[text()='Previous hospitalizations']/preceding-sibling::input");
  public static final By THERAPIES_CHECKBOX =
      By.xpath("//label[text()='Therapies']/preceding-sibling::input");
  public static final By PRESCRIPTIONS_CHECKBOX =
      By.xpath("//label[text()='Prescriptions']/preceding-sibling::input");
  public static final By TREATMENTS_CHECKBOX =
      By.xpath("//label[text()='Treatments']/preceding-sibling::input");
  public static final By CLINICAL_COURSES_CHECKBOX =
      By.xpath("//label[text()='Clinical courses']/preceding-sibling::input");
  public static final By CLINICAL_VISITS_CHECKBOX =
      By.xpath("//label[text()='Clinical visits']/preceding-sibling::input");
  public static final By PORT_HEALTH_INFORMATION_CHECKBOX =
      By.xpath("//label[text()='Port health information']/preceding-sibling::input");
  public static final By MATERNAL_HISTORIES_CHECKBOX =
      By.xpath("//label[text()='Maternal histories']/preceding-sibling::input");
  public static final By EPIDEMIOLOGICAL_DATA_CHECKBOX =
      By.xpath("//label[text()='Epidemiological data']/preceding-sibling::input");
  public static final By EXPOSURES_CHECKBOX =
      By.xpath("//label[text()='Exposures']/preceding-sibling::input");
  public static final By ACTIVITIES_AS_CASE_CHECKBOX =
      By.xpath("//label[text()='Activities as case']/preceding-sibling::input");
  public static final By HEALTH_CONDITIONS_CHECKBOX =
      By.xpath("//label[text()='Health conditions']/preceding-sibling::input");
  public static final By CONTACTS_CHECKBOX =
      By.xpath("//label[text()='Contacts']/preceding-sibling::input");
  public static final By VISITS_CHECKBOX =
      By.xpath("//label[text()='Visits']/preceding-sibling::input");
  public static final By SYMPTOMS_CHECKBOX =
      By.xpath("//label[text()='Symptoms']/preceding-sibling::input");
  public static final By EVENTS_CHECKBOX =
      By.xpath("//label[text()='Events']/preceding-sibling::input");
  public static final By EVENT_GROUPS_CHECKBOX = By.xpath("//label[text()='Event groups']");
  public static final By PERSONS_LOCATIONS_CHECKBOX =
      By.xpath("//label[text()='Person locations']/preceding-sibling::input");
  public static final By ACTIONS_CHECKBOX =
      By.xpath("//label[text()='Actions']/preceding-sibling::input");
  public static final By IMMUNIZATIONS_CHECKBOX =
      By.xpath("//label[text()='Immunizations']/preceding-sibling::input");
  public static final By VACCINATIONS_CHECKBOX =
      By.xpath("//label[text()='Vaccinations']/preceding-sibling::input");
  public static final By SAMPLES_CHECKBOX =
      By.xpath("//label[text()='Samples']/preceding-sibling::input");
  public static final By PATHOGEN_TESTS_CHECKBOX =
      By.xpath("//label[text()='Pathogen tests']/preceding-sibling::input");
  public static final By ADDITIONAL_TESTS_CHECKBOX =
      By.xpath("//label[text()='Additional tests']/preceding-sibling::input");
  public static final By TASKS_CHECKBOX =
      By.xpath("//label[text()='Tasks']/preceding-sibling::input");
  public static final By PERSONS_CHECKBOX =
      By.xpath("//label[text()='Persons']/preceding-sibling::input");
  public static final By PERSON_CONTACT_DETAILS_CHECKBOX =
      By.xpath("//label[text()='Person contact details']/preceding-sibling::input");
  public static final By LOCATIONS_CHECKBOX =
      By.xpath("//label[text()='Locations']/preceding-sibling::input");
  public static final By OUTBREAKS_CHECKBOX =
      By.xpath("//label[text()='Outbreaks']/preceding-sibling::input");
  public static final By USERS_CHECKBOX =
      By.xpath("//label[text()='Users']/preceding-sibling::input");
  public static final By USER_ROLES_CHECKBOX =
      By.xpath("//label[text()='User roles']/preceding-sibling::input");
  public static final By AGGREGATE_REPORTS_CHECKBOX =
      By.xpath("//label[text()='Aggregate reports']/preceding-sibling::input");
  public static final By WEEKLY_REPORTS_CHECKBOX =
      By.xpath("//label[text()='Weekly reports']/preceding-sibling::input");
  public static final By WEEKLY_REPORT_ENTRIES_CHECKBOX =
      By.xpath("//label[text()='Weekly report entries']/preceding-sibling::input");
  public static final By DOCUMENTS_CHECKBOX =
      By.xpath("//label[text()='Documents']/preceding-sibling::input");
  public static final By CONTINENTS_CHECKBOX =
      By.xpath("//label[text()='Continents']/preceding-sibling::input");
  public static final By SUBCONTINENTS_CHECKBOX =
      By.xpath("//label[text()='Subcontinents']/preceding-sibling::input");
  public static final By COUNTRIES_CHECKBOX =
      By.xpath("//label[text()='Countries']/preceding-sibling::input");
  public static final By REGIONS_CHECKBOX =
      By.xpath("//label[text()='Regions']/preceding-sibling::input");
  public static final By DISTRICTS_CHECKBOX =
      By.xpath("//label[text()='Districts']/preceding-sibling::input");
  public static final By COMMUNITIES_CHECKBOX =
      By.xpath("//label[text()='Communities']/preceding-sibling::input");
  public static final By FACILITIES_CHECKBOX =
      By.xpath("//label[text()='Facilities']/preceding-sibling::input");
  public static final By POINTS_OF_ENTRY_CHECKBOX =
      By.xpath("//label[text()='Points of entry']/preceding-sibling::input");
  public static final By POPULATION_DATA_CHECKBOX =
      By.xpath("//label[text()='Population data']/preceding-sibling::input");
  public static final By CUSTOMIZABLE_ENUM_VALUES_CHECKBOX =
      By.xpath("//label[text()='Customizable enum values']/preceding-sibling::input");
  public static final By EXPORT_CONFIGURATION_CHECKBOX =
      By.xpath("//label[text()='Export configurations']/preceding-sibling::input");
  public static final By FEATURE_CONFIGURATIONS_CHECKBOX =
      By.xpath("//label[text()='Feature configurations']/preceding-sibling::input");
  public static final By DISEASE_CONFIGURATIONS_CHECKBOX =
      By.xpath("//label[text()='Disease configurations']/preceding-sibling::input");
  public static final By DELETION_CONFIGURATIONS_CHECKBOX =
      By.xpath("//label[text()='Deletion configurations']/preceding-sibling::input");
  public static final By STATISTICS_EXPORT_BUTTON = By.id("export");
  public static final By TABLE_RESULTS = By.cssSelector("td[class*='v-grid-cell']");
  public static final By MAP_CONTAINER_STATISTICS_PAGE =
      By.cssSelector("[class*='leaflet-container']");
  public static final By CHART_RESULTS = By.cssSelector("[class*='highcharts-container']");
}
