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
import de.symeda.sormas.api.statistics.CaseCountDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsHelper;
import de.symeda.sormas.api.statistics.StatisticsHelper.StatisticsKeyComparator;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsCaseGrid extends Grid {

	private static final String ROW_CAPTION_COLUMN = "RowCaptionColumn";
	private static final String CASE_COUNT_OR_INCIDENCE_COLUMN = "CaseCountOrIncidenceColumn";
	private static final String UNKNOWN_COLUMN = "UnknownColumn";
	private static final String TOTAL_COLUMN = "TotalColumn";
	
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

	/**
	 * @param cellValues Ordered by rows, then columns
	 */
	@SuppressWarnings("unchecked")
	public StatisticsCaseGrid(StatisticsCaseAttribute rowsAttribute, StatisticsCaseSubAttribute rowsSubAttribute,
			StatisticsCaseAttribute columnsAttribute, StatisticsCaseSubAttribute columnsSubAttribute, boolean showZeroValues, 
			boolean showCaseIncidence, int incidenceDivisor, List<CaseCountDto> cellValues, StatisticsCaseCriteria caseCriteria) {
		
		super();

		setSelectionMode(SelectionMode.NONE);
		setHeightMode(HeightMode.UNDEFINED);
		setWidth(100, Unit.PERCENTAGE);
		setCellStyleGenerator(new StatisticsCaseGridCellStyleGenerator());

		if (cellValues.isEmpty()) {
			return;
		}

		// If no displayed attributes are selected, simply show the total number or incidence of cases
		if (rowsAttribute == null && columnsAttribute == null) {
			addColumn(CASE_COUNT_OR_INCIDENCE_COLUMN);
			getColumn(CASE_COUNT_OR_INCIDENCE_COLUMN).setHeaderCaption(showCaseIncidence ? I18nProperties.getCaption(StatisticsHelper.CASE_INCIDENCE) : I18nProperties.getCaption(StatisticsHelper.CASE_COUNT));
			if (!showCaseIncidence) {
				addRow(new Object[]{String.valueOf(cellValues.get(0).getCaseCount())});
			} else {
				addRow(new Object[]{String.valueOf(cellValues.get(0).getIncidence(incidenceDivisor))});
			}
			return;
		}

		// ## COLUMNS
		// Extract columns from content and add them to the grid
		Column captionColumn = addColumn(ROW_CAPTION_COLUMN);
		captionColumn.setHeaderCaption("");
		captionColumn.setSortable(false);
		captionColumn.setMaximumWidth(150);
		
		int unknowColumnIndex = -1;
		int totalColumnIndex;
		
		TreeMap<StatisticsGroupingKey, String> columns = new TreeMap<>(new StatisticsKeyComparator());
		if (columnsAttribute == null && columnsSubAttribute == null) {
			// When no column grouping has been selected, simply display the number of cases for the respective row
			totalColumnIndex = getColumns().size();
			addColumn(CASE_COUNT_OR_INCIDENCE_COLUMN)
				.setHeaderCaption(showCaseIncidence ? I18nProperties.getCaption(StatisticsHelper.CASE_INCIDENCE) : I18nProperties.getCaption(StatisticsHelper.CASE_COUNT));
		} else {
			boolean addColumnUnknown = false;
			// Iterate over content and add new columns to the list
			for (CaseCountDto cellValue : cellValues) {
				if (StatisticsHelper.isNullOrUnknown(cellValue.getColumnKey())) {
					addColumnUnknown = true;
				} else {
					columns.putIfAbsent((StatisticsGroupingKey) cellValue.getColumnKey(), cellValue.getColumnKey().toString());
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
				unknowColumnIndex = getColumns().size();
				Column column = addColumn(UNKNOWN_COLUMN);
				column.setHeaderCaption(I18nProperties.getCaption(Captions.unknown));
				column.setSortable(false);
				column.setMaximumWidth(120);
			}

			// Add total column
			totalColumnIndex = getColumns().size();
			Column totalColumn = addColumn(TOTAL_COLUMN);
			totalColumn.setHeaderCaption(I18nProperties.getCaption(StatisticsHelper.TOTAL));
			totalColumn.setSortable(false);
		}

		// ## ROWS
		// Extract rows from content and add them to the grid
		
		// Add a row for every value of the selected grouping
		TreeMap<StatisticsGroupingKey, Object[]> rows = new TreeMap<>(new StatisticsKeyComparator());
		int[] columnTotals = new int[getColumns().size()];
		int[] columnPopulations = new int[getColumns().size()];

		StatisticsGroupingKey currentRowKey = null;
		Object[] currentRow = null;
		int rowTotal = 0;
		int rowPopulation = 0;

		for (CaseCountDto entry : cellValues) {
			
			boolean isUnknownColumn = StatisticsHelper.isNullOrUnknown(entry.getColumnKey());
			boolean isUnknownRow = StatisticsHelper.isNullOrUnknown(entry.getRowKey());

			if (currentRow != null && !DataHelper.equal(currentRowKey, entry.getRowKey())) {
				// New grouping entry has been reached, add the current row to the rows map

				if (columnsAttribute != null) {
					// calc total
					if (!showCaseIncidence) {
						currentRow[totalColumnIndex] = String.valueOf(rowTotal);
					} else if (rowPopulation > 0) {
						currentRow[totalColumnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(rowTotal, rowPopulation, incidenceDivisor));
					} else {
						currentRow[totalColumnIndex] = null;
					}
					columnTotals[totalColumnIndex] += rowTotal;
					columnPopulations[totalColumnIndex] += rowPopulation;
				}
				
				rows.putIfAbsent(currentRowKey, currentRow);
				currentRow = null;
				currentRowKey = null;
				rowTotal = 0;
				rowPopulation = 0;
			}

			if (currentRow == null) {
				// New grouping entry has been reached, set up currentRow and currentRowKey
				currentRow = new Object[getColumns().size()];
				currentRowKey = (StatisticsGroupingKey) entry.getRowKey();
				if (isUnknownRow) {
					currentRow[0] = I18nProperties.getCaption(Captions.unknown);
				} else {
					currentRow[0] = entry.getRowKey().toString();
				}
			}

			int columnIndex = 0;

			if (columnsAttribute == null) {
				columnIndex = totalColumnIndex;
			} else if (isUnknownColumn) {
				columnIndex = unknowColumnIndex;
			} else {
				columnIndex = columns.headMap((StatisticsGroupingKey) entry.getColumnKey()).size() + 1;
			}

			if (!showCaseIncidence) {
				currentRow[columnIndex] = String.valueOf(entry.getCaseCount());
			} else {
				currentRow[columnIndex] = String.valueOf(entry.getIncidence(incidenceDivisor));
			}

			if (entry.getCaseCount() != null
					&& !(showCaseIncidence && entry.getPopulation() == null)) {
				// don't add to case sum when we are looking at incidence and not population is provided 
				rowTotal += entry.getCaseCount();
				columnTotals[columnIndex] += entry.getCaseCount();
			}
			
			if (entry.getPopulation() != null) {
				rowPopulation += entry.getPopulation();
				columnPopulations[columnIndex] += entry.getPopulation();
			}
		}

		// Add the last calculated row to the list (since this was not done in the loop) or calculate
		// the totals for the unknown row if this was the last row processed
		if (currentRow != null) {
			if (columnsAttribute != null) {
				if (!showCaseIncidence) {
					currentRow[totalColumnIndex] = String.valueOf(rowTotal);
				} else if (rowPopulation > 0) {
					currentRow[totalColumnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(rowTotal, rowPopulation, incidenceDivisor));
				} else {
					currentRow[totalColumnIndex] = null;
				}
				columnTotals[totalColumnIndex] += rowTotal;
				columnPopulations[totalColumnIndex] += rowPopulation;
			}
			rows.putIfAbsent(currentRowKey, currentRow);
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

//		// Add unknown row if existing
//		if (unknownRow != null && rowsAttribute != null && rowsAttribute.isUnknownValueAllowed()) {
//			addRow(unknownRow);
//		}

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