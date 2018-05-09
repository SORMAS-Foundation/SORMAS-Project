package de.symeda.sormas.ui.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsCaseGrid extends Grid {

	private static final String COLUMN_UNKNOWN = "Column_Unknown";
	private static final String COLUMN_TOTAL = "Column_Total";

	private final StatisticsCaseAttribute rowsAttribute;
	private final StatisticsCaseSubAttribute rowsSubAttribute;
	private final StatisticsCaseAttribute columnsAttribute;
	private final StatisticsCaseSubAttribute columnsSubAttribute;

	private final class StatisticsCaseGridCellStyleGenerator implements CellStyleGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public String getStyle(CellReference cell) {
			if (cell.getPropertyId().equals("")) {
				return CssStyles.GRID_ROW_TITLE;
			}

			return null;
		}
	}

	public StatisticsCaseGrid(StatisticsCaseAttribute rowsAttribute, StatisticsCaseSubAttribute rowsSubAttribute,
			StatisticsCaseAttribute columnsAttribute, StatisticsCaseSubAttribute columnsSubAttribute, List<Object[]> content) {
		super();

		this.rowsAttribute = rowsAttribute;
		this.rowsSubAttribute = rowsSubAttribute;
		this.columnsAttribute = columnsAttribute;
		this.columnsSubAttribute = columnsSubAttribute;

		setSelectionMode(SelectionMode.NONE);
		setCellStyleGenerator(new StatisticsCaseGridCellStyleGenerator());

		if (content.isEmpty()) {
			return;
		}

		// If no displayed attributes are selected, simply show the total number of cases
		if (rowsAttribute == null && columnsAttribute == null) {
			addColumn("Number of cases");
			addRow(new Object[]{String.valueOf(content.get(0))});
		} else {
			// Set columns
			Map<Object, Integer> columnsMap = null;

			addColumn("");

			if (columnsAttribute != null || columnsSubAttribute != null) {
				Map<String, String> columns = new TreeMap<>();
				for (Object[] entry : content) {
					formatContentEntry(entry, entry.length - 1, columnsAttribute, columnsSubAttribute);

					String rawColumnName = null;
					String columnName = null;

					if (entry[entry.length - 1] == null) {
						rawColumnName = COLUMN_UNKNOWN;
						columnName = "Unknown";
					} else {
						rawColumnName = entry[entry.length - 1].toString();
						columnName = buildHeader(rawColumnName != null ? rawColumnName : null, columnsAttribute, columnsSubAttribute);
					}

					columns.put(rawColumnName, columnName);
				}

				if (columnsAttribute.isSortByCaption()) {
					for (Map.Entry<String, String> mapEntry : DataHelper.entriesSortedByValues(columns)) {
						addColumn(mapEntry.getKey());
						getColumn(mapEntry.getKey()).setHeaderCaption(mapEntry.getValue());
					}
				} else {
					for (String column : columns.keySet()) {
						addColumn(column);
						getColumn(column).setHeaderCaption(columns.get(column));
					}
				}

				addColumn(COLUMN_TOTAL);
				getColumn(COLUMN_TOTAL).setHeaderCaption("Total");

				// Build map of column properties
				columnsMap = new HashMap<>();
				List<Column> gridColumns = getColumns();
				for (Column column : gridColumns) {
					columnsMap.put(column.getPropertyId(), gridColumns.indexOf(column));
				}
			} else {
				addColumn("Number of cases");
			}

			// Add data
			if (rowsAttribute != null || rowsSubAttribute != null) {
				Object[] currentRow = null;
				Object rowHeader = null;

				long[] columnTotals = new long[getColumns().size()];
				for (Object[] entry : content) {
					formatContentEntry(entry, 1, rowsAttribute, rowsSubAttribute);
					if (columnsAttribute != null || columnsSubAttribute != null) {

						if (currentRow != null && rowHeader != null && !rowHeader.equals(entry[1])) {
							int totalForRow = calculateTotalForRow(currentRow);
							currentRow[currentRow.length - 1] = String.valueOf(totalForRow);
							columnTotals[columnTotals.length - 1] += totalForRow;
							addRow(currentRow);
							currentRow = null;
							rowHeader = null;
						}

						if (currentRow == null && rowHeader == null) {
							currentRow = new Object[getColumns().size()];
							currentRow[0] = buildHeader(entry[1] == null ? null : entry[1].toString(), rowsAttribute, rowsSubAttribute);
							rowHeader = entry[1];
						}

						int columnIndex;
						if (entry[entry.length - 1] != null) {
							columnIndex = columnsMap.get(entry[entry.length - 1].toString());
						} else {
							columnIndex = columnsMap.get(COLUMN_UNKNOWN);
						}
						currentRow[columnIndex] = entry[0].toString();
						columnTotals[columnIndex] += (long) entry[0];
					} else {
						currentRow = new Object[getColumns().size()];
						currentRow[0] = buildHeader(entry[1] == null ? null : entry[1].toString(), rowsAttribute, rowsSubAttribute);
						currentRow[1] = entry[0].toString();
						addRow(currentRow);
						columnTotals[columnTotals.length - 1] += (long) entry[0];
					}
				}

				if (columnsAttribute != null || columnsSubAttribute != null) {
					if (currentRow[0] != null) {
						int totalForRow = calculateTotalForRow(currentRow);
						currentRow[currentRow.length - 1] = String.valueOf(totalForRow);
						columnTotals[columnTotals.length - 1] += totalForRow;
						addRow(currentRow);
					}
				}

				if (rowsAttribute.isSortByCaption()) {
					sort("");
				}

				// Total row
				currentRow = new Object[getColumns().size()];
				currentRow[0] = "Total";
				for (int i = 1; i < columnTotals.length; i++) {
					currentRow[i] = String.valueOf(columnTotals[i]);
				}
				addRow(currentRow);
			} else {
				Object[] row = new Object[getColumns().size()];
				row[0] = "Number of cases";
				long totalAmountOfCases = 0;
				for (int i = 0; i < content.size(); i++) {
					Object[] entry = content.get(i);
					formatContentEntry(entry, 1, rowsAttribute, rowsSubAttribute);
					if (entry[1] != null) {
						row[columnsMap.get(entry[entry.length - 1].toString())] = entry[0].toString();
					} else {
						row[columnsMap.get(COLUMN_UNKNOWN)] = entry[0].toString();
					}
					totalAmountOfCases += (long) entry[0];
				}
				row[row.length - 1] = String.valueOf(totalAmountOfCases);
				addRow(row);
			}
		}

		setHeightMode(HeightMode.ROW);
		setHeightByRows(Math.max(1, Math.min(getContainerDataSource().size(), 15)));
	}

	private String buildHeader(String rawHeader, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		if (rawHeader == null) {
			return "Unknown";
		}

		if (subAttribute != null) {
			switch (subAttribute) {
			case QUARTER:
				return "Q" + rawHeader;
			case MONTH:
				return Month.values()[Integer.valueOf(rawHeader) - 1].toString();
			case QUARTER_OF_YEAR:
				return "Q" + rawHeader.charAt(rawHeader.length() - 1) + " " + rawHeader.substring(0, 4);
			case MONTH_OF_YEAR:
				int month = Integer.valueOf(rawHeader.substring(4));
				return Month.values()[month - 1].toString() + " " + rawHeader.substring(0, 4);
			case REGION:
				return FacadeProvider.getRegionFacade().getRegionByUuid(rawHeader).toString();
			case DISTRICT:
				return FacadeProvider.getDistrictFacade().getDistrictByUuid(rawHeader).toString();
			default:
				return rawHeader;
			}
		} else {
			switch (attribute) {
			//			case DISEASE:
			//				return Disease.valueOf(rawHeader).toShortString();
			case CLASSIFICATION:
				return CaseClassification.valueOf(rawHeader).toString();
			case OUTCOME:
				return CaseOutcome.valueOf(rawHeader).toString();
			case SEX:
				return Sex.valueOf(rawHeader).toString();
			default:
				return rawHeader;
			}
		}
	}

	private void formatContentEntry(Object[] entry, int indexToFormat, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		if (entry[indexToFormat] == null) {
			return;
		}

		if (attribute == StatisticsCaseAttribute.DISEASE) {
			entry[indexToFormat] = Disease.valueOf(entry[indexToFormat].toString()).toShortString();
			return;
		}

		if (subAttribute == StatisticsCaseSubAttribute.MONTH) {
			if ((int) entry[indexToFormat] < 10) {
				entry[indexToFormat] = "0" + entry[indexToFormat];
			}
			return;
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
