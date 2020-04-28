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

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCountDto;
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
			StatisticsCaseAttribute columnsAttribute, StatisticsCaseSubAttribute columnsSubAttribute, 
			boolean showCaseIncidence, int incidenceDivisor, List<StatisticsCaseCountDto> cellValues, StatisticsCaseCriteria caseCriteria) {

		super();

		setSelectionMode(SelectionMode.NONE);
		setHeightMode(HeightMode.UNDEFINED);
		setWidth(100, Unit.PERCENTAGE);
		setCellStyleGenerator(new StatisticsCaseGridCellStyleGenerator());

		if (cellValues.isEmpty()) {
			return;
		}
		
		CaseCountOrIncidence dataStyle = showCaseIncidence ? CaseCountOrIncidence.CASE_INCIDENCE : CaseCountOrIncidence.CASE_COUNT;

		// If no displayed attributes are selected, simply show the total number or incidence of cases
		if (rowsAttribute == null && columnsAttribute == null) {
			addColumn(CASE_COUNT_OR_INCIDENCE_COLUMN);
			getColumn(CASE_COUNT_OR_INCIDENCE_COLUMN).setHeaderCaption(dataStyle.toString());
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
			// When no column grouping has been selected, simply display the number of cases or case incidence for the respective row
			totalColumnIndex = getColumns().size();
			addColumn(CASE_COUNT_OR_INCIDENCE_COLUMN)
			.setHeaderCaption(dataStyle.toString());
		} else {
			boolean addColumnUnknown = false;
			// Iterate over content and add new columns to the list
			for (StatisticsCaseCountDto cellValue : cellValues) {
				if (StatisticsHelper.isNullOrUnknown(cellValue.getColumnKey())) {
					addColumnUnknown = true;
				} else {
					columns.putIfAbsent((StatisticsGroupingKey) cellValue.getColumnKey(), cellValue.getColumnKey().toString());
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
		int[] caseCountTotalRow = new int[getColumns().size()];
		int[] populationTotalRow = new int[getColumns().size()];

		StatisticsGroupingKey currentRowKey = null;
		Object[] currentRow = null;
		int rowTotal = 0;
		int rowPopulation = 0;

		for (StatisticsCaseCountDto cellValue : cellValues) {
			boolean isUnknownColumn = StatisticsHelper.isNullOrUnknown(cellValue.getColumnKey());
			boolean isUnknownRow = StatisticsHelper.isNullOrUnknown(cellValue.getRowKey());

			if (currentRow != null && !DataHelper.equal(currentRowKey, cellValue.getRowKey())) {
				// New grouping entry has been reached, add the current row to the rows map
				if (columnsAttribute != null) {
					// Calculate total
					if (rowTotal == 0) {
						currentRow[totalColumnIndex] = null;					
					} else if (!showCaseIncidence) {
						currentRow[totalColumnIndex] = String.valueOf(rowTotal);
					} else if (rowPopulation > 0) {
						currentRow[totalColumnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(rowTotal, rowPopulation, incidenceDivisor));
					} else {
						currentRow[totalColumnIndex] = I18nProperties.getCaption(Captions.notAvailableShort);
					}
					
					if (!(showCaseIncidence && rowPopulation == 0)) {
						caseCountTotalRow[totalColumnIndex] += rowTotal;
						
						if (rowsAttribute != null && rowsAttribute.isPopulationData()) {
							populationTotalRow[totalColumnIndex] += rowPopulation;
						} else if (populationTotalRow[totalColumnIndex] == 0) {
							populationTotalRow[totalColumnIndex] = rowPopulation;
						}						
					}
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
				currentRowKey = (StatisticsGroupingKey) cellValue.getRowKey();
				if (isUnknownRow) {
					currentRow[0] = I18nProperties.getCaption(Captions.unknown);
				} else {
					currentRow[0] = cellValue.getRowKey().toString();
				}
			}

			int columnIndex = 0;

			if (columnsAttribute == null) {
				columnIndex = totalColumnIndex;
			} else if (isUnknownColumn) {
				columnIndex = unknowColumnIndex;
			} else {
				columnIndex = columns.headMap((StatisticsGroupingKey) cellValue.getColumnKey()).size() + 1;
			}

			if (!showCaseIncidence) {
				if (cellValue.getCaseCount() == 0) {
					currentRow[columnIndex] = null;
				} else {
					currentRow[columnIndex] = String.valueOf(cellValue.getCaseCount());
				}
			} else {
				BigDecimal incidence = cellValue.getIncidence(incidenceDivisor);
				if (incidence != null) {
					if (BigDecimal.ZERO.compareTo(incidence) == 0) {
						currentRow[columnIndex] = null;
					} else {
						currentRow[columnIndex] = String.valueOf(incidence);
					}
				} else {
					currentRow[columnIndex] = I18nProperties.getCaption(Captions.notAvailableShort);
				}
			}

			if (cellValue.getCaseCount() != null && !(showCaseIncidence && cellValue.getPopulation() == null)) {
				// Don't add to case sum when we are looking at incidence and population is not provided 
				rowTotal += cellValue.getCaseCount();
				caseCountTotalRow[columnIndex] += cellValue.getCaseCount();
			}

			if (cellValue.getPopulation() != null) {
				if (columnsAttribute != null && columnsAttribute.isPopulationData()) {
					rowPopulation += cellValue.getPopulation();
				} else if (rowPopulation == 0) {
					rowPopulation = cellValue.getPopulation();
				}
				if (rowsAttribute != null && rowsAttribute.isPopulationData()) {
					populationTotalRow[columnIndex] += cellValue.getPopulation();
				} else if (populationTotalRow[columnIndex] == 0) {
					populationTotalRow[columnIndex] = cellValue.getPopulation();
				}
			}
		}

		// Add the last calculated row to the list (since this was not done in the loop) or calculate
		// the totals for the unknown row if this was the last row processed
		if (currentRow != null) {
			if (columnsAttribute != null) {
				if (rowTotal == 0) {
					currentRow[totalColumnIndex] = null;
				} else if (!showCaseIncidence) {
					currentRow[totalColumnIndex] = String.valueOf(rowTotal);
				} else if (rowPopulation > 0) {
					currentRow[totalColumnIndex] = String.valueOf(InfrastructureHelper.getCaseIncidence(rowTotal, rowPopulation, incidenceDivisor));
				} else {
					currentRow[totalColumnIndex] = null;
				}
				
				if (!(showCaseIncidence && rowPopulation == 0)) {
					caseCountTotalRow[totalColumnIndex] += rowTotal;

					if (rowsAttribute != null && rowsAttribute.isPopulationData()) {
						populationTotalRow[totalColumnIndex] += rowPopulation;
					} else if (populationTotalRow[totalColumnIndex] == 0) {
						populationTotalRow[totalColumnIndex] = rowPopulation;
					}
				}				
			}
			rows.putIfAbsent(currentRowKey, currentRow);
		}

		// Add rows to the grid
		for (StatisticsGroupingKey groupingKey : rows.keySet()) {
			addRow(rows.get(groupingKey));
		}

		// Add total row
		Object[] totalRow = new Object[getColumns().size()];
		totalRow[0] = I18nProperties.getCaption(StatisticsHelper.TOTAL);
		for (int i = 1; i < caseCountTotalRow.length; i++) {
			if (!showCaseIncidence) {
				if (caseCountTotalRow[i] > 0) {
					totalRow[i] = String.valueOf(caseCountTotalRow[i]);
				} else {
					totalRow[i] = null;
				}
			} else {
				if (caseCountTotalRow[i] > 0 && populationTotalRow[i] > 0) {
					totalRow[i] = String.valueOf(InfrastructureHelper.getCaseIncidence((int) caseCountTotalRow[i], populationTotalRow[i], incidenceDivisor));
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