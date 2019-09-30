/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.statistics;

import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsHelper;
import de.symeda.sormas.api.statistics.StatisticsHelper.StatisticsKeyComparator;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsCaseGrid extends Grid {

	private static final String ROW_CAPTION_COLUMN = "RowCaptionColumn";
	private static final String CASE_COUNT_OR_INCIDENCE_COLUMN = "CaseCountOrIncidenceColumn";
	private static final String UNKNOWN_COLUMN = "UnknownColumn";
	private static final String TOTAL_COLUMN = "TotalColumn";
	
	private final int COUNT_POSITION = 0;
	private final int POPULATION_POSITION = 1;
	private final int ROW_GROUP_POSITION = 2;
	private final int COLUMN_GROUP_POSITION = 3;
	
	private final class StatisticsCaseGridCellStyleGenerator implements CellStyleGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public String getStyle(CellReference cell) {
			if (cell.getPropertyId().equals(ROW_CAPTION_COLUMN)) {
				return CssStyles.GRID_ROW_TITLE;
			}

			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public StatisticsCaseGrid(StatisticsCaseAttribute rowsAttribute, StatisticsCaseSubAttribute rowsSubAttribute,
			StatisticsCaseAttribute columnsAttribute, StatisticsCaseSubAttribute columnsSubAttribute, boolean showZeroValues, 
			boolean showCaseIncidence, int incidenceDivisor, List<Object[]> content, StatisticsCaseCriteria caseCriteria) {
		
		super();

		setSelectionMode(SelectionMode.NONE);
		setHeightMode(HeightMode.UNDEFINED);
		setWidth(100, Unit.PERCENTAGE);
		setCellStyleGenerator(new StatisticsCaseGridCellStyleGenerator());

		if (content.isEmpty()) {
			return;
		}

		// If no displayed attributes are selected, simply show the total number or incidence of cases
		if (rowsAttribute == null && columnsAttribute == null) {
			addColumn(CASE_COUNT_OR_INCIDENCE_COLUMN);
			getColumn(CASE_COUNT_OR_INCIDENCE_COLUMN).setHeaderCaption(showCaseIncidence ? I18nProperties.getCaption(StatisticsHelper.CASE_INCIDENCE) : I18nProperties.getCaption(StatisticsHelper.CASE_COUNT));
			if (!showCaseIncidence) {
				addRow(new Object[]{String.valueOf(content.get(0)[COUNT_POSITION])});
			} else {
				addRow(new Object[]{String.valueOf(InfrastructureHelper.getCaseIncidence(((Number) content.get(0)[COUNT_POSITION]).intValue(), ((Number) content.get(0)[POPULATION_POSITION]).doubleValue(), incidenceDivisor))});
			}
			return;
		}

		// Extract columns from content and add them to the grid
		Column captionColumn = addColumn(ROW_CAPTION_COLUMN);
		captionColumn.setHeaderCaption("");
		captionColumn.setSortable(false);
		captionColumn.setMaximumWidth(150);

		TreeMap<StatisticsGroupingKey, String> columns = new TreeMap<>(new StatisticsKeyComparator());
		if (columnsAttribute == null && columnsSubAttribute == null) {
			// When no column grouping has been selected, simply display the number of cases for the respective row
			addColumn(CASE_COUNT_OR_INCIDENCE_COLUMN);
			getColumn(CASE_COUNT_OR_INCIDENCE_COLUMN).setHeaderCaption(showCaseIncidence ? I18nProperties.getCaption(StatisticsHelper.CASE_INCIDENCE) : I18nProperties.getCaption(StatisticsHelper.CASE_COUNT));
		} else {
			boolean addColumnUnknown = false;
			// Iterate over content and add new columns to the list
			for (Object[] entry : content) {
				if (StatisticsHelper.isNullOrUnknown(entry[COLUMN_GROUP_POSITION])) {
					addColumnUnknown = true;
				} else {
					columns.putIfAbsent((StatisticsGroupingKey) entry[COLUMN_GROUP_POSITION], entry[COLUMN_GROUP_POSITION].toString());
				}
			}

			// If zero values are ticked, add missing columns to the list; this involves every possible value of the chosen column attribute unless a filter has been
			// set for the same attribute; in this case, only values that are part of the filter are chosen
			if (showZeroValues) {
				List<Object> values = StatisticsHelper.getAllAttributeValues(columnsAttribute, columnsSubAttribute);
				List<StatisticsGroupingKey> filterValues = (List<StatisticsGroupingKey>) caseCriteria.getFilterValuesForGrouping(columnsAttribute, columnsSubAttribute);
				for (Object value : values) {
					Object formattedValue = StatisticsHelper.buildGroupingKey(value, columnsAttribute, columnsSubAttribute);
					if (formattedValue != null && (CollectionUtils.isEmpty(filterValues) || filterValues.contains(formattedValue))) {
						columns.putIfAbsent((StatisticsGroupingKey) formattedValue, formattedValue.toString()); 
					}
				}
			}

			// Add all collected columns to the grid
			for (StatisticsGroupingKey columnId : columns.keySet()) {
				Column column = addColumn(columnId);
				column.setHeaderCaption(columns.get(columnId));
				column.setSortable(false);
				column.setMaximumWidth(120);
			}

			// Add the column to display unknown numbers if required
			if (addColumnUnknown && columnsAttribute.isUnknownValueAllowed()) {
				Column column = addColumn(UNKNOWN_COLUMN);
				column.setHeaderCaption(I18nProperties.getCaption(Captions.unknown));
				column.setSortable(false);
				column.setMaximumWidth(120);
			}

			// Add total column
			Column totalColumn = addColumn(TOTAL_COLUMN);
			totalColumn.setHeaderCaption(I18nProperties.getCaption(StatisticsHelper.TOTAL));
			totalColumn.setSortable(false);
		}

		boolean columnsHavePopulationGrouping = columnsAttribute == StatisticsCaseAttribute.SEX || columnsAttribute == StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS
				|| columnsAttribute == StatisticsCaseAttribute.REGION_DISTRICT;
		boolean rowsHavePopulationGrouping = rowsAttribute == StatisticsCaseAttribute.SEX || rowsAttribute == StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS
				|| rowsAttribute == StatisticsCaseAttribute.REGION_DISTRICT;

		// Extract rows from content and add them to the grid
		if (rowsAttribute == null && rowsSubAttribute == null) {
			// When no row grouping has been selected, simply display the number of cases for the respective column
			Object[] row = new Object[getColumns().size()];
			row[0] = showCaseIncidence ? I18nProperties.getCaption(StatisticsHelper.CASE_INCIDENCE) : I18nProperties.getCaption(StatisticsHelper.CASE_COUNT);
			long totalCaseCount = 0;
			Double rowPopulation = null;
			
			for (int i = 0; i < content.size(); i++) {
				Object[] entry = content.get(i);
				if (StatisticsHelper.isNullOrUnknown(entry[COLUMN_GROUP_POSITION])) {
					if (!showCaseIncidence || columnsAttribute.isUnknownValueAllowed()) {
						// 'Unknown' column is always the second-last
						int columnIndex = getColumns().size() - 2;
						if (!showCaseIncidence) {
							row[columnIndex] = entry[COUNT_POSITION].toString();
						} else {
							row[columnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(((Number) entry[COUNT_POSITION]).intValue(), ((Number) entry[POPULATION_POSITION]).doubleValue(), incidenceDivisor));
						}
					} else {
						continue;
					}
				} else {
					// Retrieve the column index by using the headMap function
					int columnIndex = columns.headMap((StatisticsGroupingKey) entry[COLUMN_GROUP_POSITION]).size() + 1;
					if (!showCaseIncidence) {
						row[columnIndex] = entry[COUNT_POSITION].toString();
					} else {
						row[columnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(((Number) entry[COUNT_POSITION]).intValue(), ((Number) entry[POPULATION_POSITION]).doubleValue(), incidenceDivisor));
					}
				}
				
				totalCaseCount += ((Number) entry[COUNT_POSITION]).longValue();
				if (showCaseIncidence) {
					if (columnsHavePopulationGrouping && rowPopulation != null) {
						rowPopulation += ((Number) entry[POPULATION_POSITION]).doubleValue();
					} else if (rowPopulation == null) {
						rowPopulation = ((Number) entry[POPULATION_POSITION]).doubleValue();
					}
				}
			}
			
			// Add content to 'Total' column
			if (!showCaseIncidence) {
				row[row.length - 1] = String.valueOf(totalCaseCount);
			} else {
				if (totalCaseCount > 0 && rowPopulation != null && rowPopulation > 0) {
					row[row.length - 1] = String.valueOf(InfrastructureHelper.getCaseIncidence((int) totalCaseCount, rowPopulation, incidenceDivisor));
				} else {
					row[row.length - 1] = null;
				}
			}
			
			addRow(row);
		} else {
			// Add a row for every value of the selected grouping
			TreeMap<StatisticsGroupingKey, Object[]> rows = new TreeMap<>(new StatisticsKeyComparator());
			Object[] currentRow = null;
			Object[] unknownRow = null;
			StatisticsGroupingKey currentRowKey = null;
			long[] columnTotals = new long[getColumns().size()];
			double[] columnPopulations = new double[getColumns().size()];
			long rowTotal = 0;
			Double rowPopulation = null;

			for (Object[] entry : content) {
				if (columnsAttribute == null && columnsSubAttribute == null) {
					// Only display the number of cases if there is no column grouping
					if (!StatisticsHelper.isNullOrUnknown(entry[ROW_GROUP_POSITION])) {
						currentRow = new Object[getColumns().size()];
						currentRowKey = (StatisticsGroupingKey) entry[ROW_GROUP_POSITION];
						currentRow[0] = entry[ROW_GROUP_POSITION].toString();

						if (!showCaseIncidence) {
							currentRow[1] = entry[COUNT_POSITION].toString();
						} else {
							currentRow[1] = String.valueOf(InfrastructureHelper.getCaseIncidence(((Number) entry[COUNT_POSITION]).intValue(), ((Number) entry[POPULATION_POSITION]).doubleValue(), incidenceDivisor));
						}
						
						rows.putIfAbsent((StatisticsGroupingKey) entry[ROW_GROUP_POSITION], currentRow);
						columnTotals[columnTotals.length - 1] += ((Number) entry[COUNT_POSITION]).longValue();
					} else if (unknownRow == null) {
						unknownRow = new Object[getColumns().size()];
						unknownRow[0] = I18nProperties.getCaption(Captions.unknown);
						
						if (!showCaseIncidence) {
							unknownRow[1] = entry[COUNT_POSITION].toString();
						} else {
							unknownRow[1] = String.valueOf(InfrastructureHelper.getCaseIncidence(((Number) entry[COUNT_POSITION]).intValue(), ((Number) entry[POPULATION_POSITION]).doubleValue(), incidenceDivisor));
						}
						
						columnTotals[columnTotals.length - 1] += ((Number) entry[COUNT_POSITION]).longValue();
					}
					
					if (showCaseIncidence) {
						if (rowsHavePopulationGrouping) {
							columnPopulations[columnPopulations.length - 1] += ((Number) entry[POPULATION_POSITION]).doubleValue();
						} else {
							columnPopulations[columnPopulations.length - 1] = ((Number) entry[POPULATION_POSITION]).doubleValue();
						}
					}
				} else {
					if (currentRow != null && currentRowKey != null && !currentRowKey.equals(entry[ROW_GROUP_POSITION])) {
						// New grouping entry has been reached, add the current row to the rows map
						if (!showCaseIncidence) {
							currentRow[currentRow.length - 1] = String.valueOf(rowTotal);
						} else {
							if (rowTotal > 0 && rowPopulation != null && rowPopulation > 0) {
								currentRow[currentRow.length - 1] = String.valueOf(InfrastructureHelper.getCaseIncidence((int) rowTotal, rowPopulation, incidenceDivisor));
							} else {
								currentRow[currentRow.length - 1] = null;
							}
						}
						columnTotals[columnTotals.length - 1] += rowTotal;
						rows.putIfAbsent(currentRowKey, currentRow);
						currentRow = null;
						currentRowKey = null;
						rowTotal = 0;
						rowPopulation = null;
					}

					if (currentRow == null && currentRowKey == null) {
						// New grouping entry has been reached, set up currentRow and currentRowKey
						if (StatisticsHelper.isNullOrUnknown(entry[ROW_GROUP_POSITION])) {
							if (unknownRow == null) {
								unknownRow = new Object[getColumns().size()];
								unknownRow[0] = I18nProperties.getCaption(Captions.unknown);
							}
						} else {
							currentRow = new Object[getColumns().size()];
							currentRowKey = (StatisticsGroupingKey) entry[ROW_GROUP_POSITION];
							currentRow[0] = entry[ROW_GROUP_POSITION].toString();
						}
					}

					int columnIndex = 0;

					if (!StatisticsHelper.isNullOrUnknown(entry[COLUMN_GROUP_POSITION])) {
						columnIndex = columns.headMap((StatisticsGroupingKey) entry[COLUMN_GROUP_POSITION]).size() + 1;
					} else {
						columnIndex = getColumns().size() - 2;
					}

					if (StatisticsHelper.isNullOrUnknown(entry[ROW_GROUP_POSITION])) {
						if (!showCaseIncidence) {
							unknownRow[columnIndex] = entry[COUNT_POSITION].toString();
						} else {
							unknownRow[columnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(((Number) entry[COUNT_POSITION]).intValue(), ((Number) entry[POPULATION_POSITION]).doubleValue(), incidenceDivisor));
						}
					} else {
						if (!showCaseIncidence) {
							currentRow[columnIndex] = entry[COUNT_POSITION].toString();
						} else {
							currentRow[columnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(((Number) entry[COUNT_POSITION]).intValue(), ((Number) entry[POPULATION_POSITION]).doubleValue(), incidenceDivisor));
						}
					}	
					
					rowTotal += ((Number) entry[COUNT_POSITION]).longValue();	

					if (showCaseIncidence) {
						if (columnsHavePopulationGrouping && rowPopulation != null) {
							rowPopulation += ((Number) entry[POPULATION_POSITION]).doubleValue();
						} else if (rowPopulation == null) {
							rowPopulation = ((Number) entry[POPULATION_POSITION]).doubleValue();
						}

						for (int column = 0; column < columnPopulations.length; column++) {
							if (rowsHavePopulationGrouping) {
								columnPopulations[column] += rowPopulation;
							} else {
								columnPopulations[column] = rowPopulation;
							}
						}
					}

					columnTotals[columnIndex] += ((Number) entry[COUNT_POSITION]).longValue();
				}
			}

			// Add the last calculated row to the list (since this was not done in the loop) or calculate
			// the totals for the unknown row if this was the last row processed
			if (columnsAttribute != null || columnsSubAttribute != null) {
				if (currentRow != null && currentRow[COUNT_POSITION] != null) {
					if (!showCaseIncidence) {
						currentRow[currentRow.length - 1] = String.valueOf(rowTotal);
					} else {
						if (rowTotal > 0 && rowPopulation != null && rowPopulation > 0) {
							currentRow[currentRow.length - 1] = String.valueOf(InfrastructureHelper.getCaseIncidence((int) rowTotal, rowPopulation, incidenceDivisor));
						} else {
							currentRow[currentRow.length - 1] = null;
						}
						
						for (int column = 0; column < columnPopulations.length; column++) {
							if (rowsHavePopulationGrouping) {
								columnPopulations[column] += rowPopulation;
							} else {
								columnPopulations[column] = rowPopulation;
							}
						}
					}
					columnTotals[columnTotals.length - 1] += rowTotal;
					rows.putIfAbsent(currentRowKey, currentRow);
				} else if (unknownRow != null && unknownRow[COUNT_POSITION] != null) {
					if (!showCaseIncidence) {
						unknownRow[unknownRow.length - 1] = String.valueOf(rowTotal);
					} else {
						if (rowTotal > 0 && rowPopulation != null && rowPopulation > 0) {
							unknownRow[unknownRow.length - 1] = String.valueOf(InfrastructureHelper.getCaseIncidence((int) rowTotal, rowPopulation, incidenceDivisor));
						} else {
							unknownRow[unknownRow.length - 1] = null;
						}
						
						for (int column = 0; column < columnPopulations.length; column++) {
							if (rowsHavePopulationGrouping) {
								columnPopulations[column] += rowPopulation;
							} else {
								columnPopulations[column] = rowPopulation;
							}
						}
					}
					columnTotals[columnTotals.length - 1] += rowTotal;
				}
			}

			// If zero values are ticked, add missing rows to the list; this involves every possible value of the chosen row attribute unless a filter has been
			// set for the same attribute; in this case, only values that are part of the filter are chosen
			if (showZeroValues) {
				List<Object> values = StatisticsHelper.getAllAttributeValues(rowsAttribute, rowsSubAttribute);
				List<StatisticsGroupingKey> filterValues = (List<StatisticsGroupingKey>) caseCriteria.getFilterValuesForGrouping(rowsAttribute, rowsSubAttribute);
				for (Object value : values) {
					Object formattedValue = StatisticsHelper.buildGroupingKey(value, rowsAttribute, rowsSubAttribute);
					if (formattedValue != null && (CollectionUtils.isEmpty(filterValues) || filterValues.contains(formattedValue))) {
						Object[] zeroRow = new Object[getColumns().size()];
						zeroRow[0] = formattedValue.toString();
						zeroRow[zeroRow.length - 1] = null;
						rows.putIfAbsent((StatisticsGroupingKey) formattedValue, zeroRow); 
					}
				}
			}

			// Add rows to the grid
			for (StatisticsGroupingKey groupingKey : rows.keySet()) {
				addRow(rows.get(groupingKey));
			}

			// Add unknown row if existing
			if (unknownRow != null && rowsAttribute.isUnknownValueAllowed()) {
				addRow(unknownRow);
			}

			// Add total row
			Object[] totalRow = new Object[getColumns().size()];
			totalRow[0] = I18nProperties.getCaption(StatisticsHelper.TOTAL);
			for (int i = 1; i < columnTotals.length; i++) {
				if (!showCaseIncidence) {
					if (columnTotals[i] > 0) {
						totalRow[i] = String.valueOf(columnTotals[i]);
					} else {
						totalRow[i] = null;
					}
				} else {
					if (columnTotals[i] > 0 && columnPopulations[i] > 0) {
						totalRow[i] = String.valueOf(InfrastructureHelper.getCaseIncidence((int) columnTotals[i], columnPopulations[i], incidenceDivisor));
					} else {
						totalRow[i] = null;
					}
				}
			}

			addRow(totalRow);
		}
	}

	private int calculateTotalForRow(Object[] row) {
		int total = 0;
		for (int i = 1; i < row.length; i++) {
			if (row[i] != null) {
				total += Integer.valueOf(row[i].toString());
			}
		}

		return total;
	}

}