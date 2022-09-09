package org.sormas.e2etests.pages.application.statistics;

import org.openqa.selenium.By;

public class StatisticsPage {
  public static final By ADD_FILTER_BUTTON = By.id("statisticsAddFilter");
  public static final By RESET_FILTER_BUTTON = By.id("statisticsResetFilters");
  public static final By REMOVE_FILTER_BUTTON = By.id("close");
  public static final By SELECT_ATTRIBUTE_DROPDOWN = By.cssSelector("[id='statisticsAttribute-0']");
  public static final By VISUALISATION_TYPE_TABLE_RADIO_BUTTON =
      By.xpath("//label[text()='Table']");
  public static final By VISUALISATION_TYPE_MAP_RADIO_BUTTON = By.xpath("//label[text()='Map']");
  public static final By VISUALISATION_TYPE_CHART_RADIO_BUTTON =
      By.xpath("//label[text()='Chart']");
  public static final By TABLE_ROWS_DROPDOWN = By.id("visualizationType");
  public static final By SWITCH_ROWS_AND_COLUMNS_BUTTON = By.id("switchRowsAndColumns");
  public static final By DATABASE_EXPORT_TAB = By.cssSelector("#tab-statistics-database-export");
  public static final By EVENT_GROUPS_CHECKBOX = By.xpath("//label[text()='Event groups']");
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
}
