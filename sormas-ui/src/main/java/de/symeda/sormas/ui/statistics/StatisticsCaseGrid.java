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
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsCaseGrid extends Grid {

	private static final String UNKNOWN = "Unknown";
	
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

		// Set columns
		Map<String, String> columns = new TreeMap<>();
		for (Object[] entry : content) {
			formatContentEntry(entry);
			
			String rawColumnName = null;
			String columnName = null;
			
			if (entry[entry.length - 1] == null) {
				rawColumnName = UNKNOWN;
				columnName = UNKNOWN;
			} else {
				rawColumnName = entry[entry.length - 1].toString();
				columnName = buildHeader(rawColumnName != null ? rawColumnName : null, columnsAttribute, columnsSubAttribute);
			}
			
			columns.put(rawColumnName, columnName);
		}

		addColumn("");

		for (String column : columns.keySet()) {
			addColumn(column);
			getColumn(column).setHeaderCaption(columns.get(column));
		}

		// Build map of column properties
		Map<Object, Integer> columnsMap = new HashMap<>();
		List<Column> gridColumns = getColumns();
		for (Column column : gridColumns) {
			columnsMap.put(column.getPropertyId(), gridColumns.indexOf(column));
		}

		// Add data
		Object[] currentRow = null;
		Object rowHeader = null;
		for (Object[] entry : content) {
			if (currentRow != null && rowHeader != null && !rowHeader.equals(entry[1])) {
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
				columnIndex = columnsMap.get(UNKNOWN);
			}
			currentRow[columnIndex] = entry[0].toString();
		}

		if (currentRow[0] != null) {
			addRow(currentRow);
		}

		setCaption("Case count by " + "Row" + " and " + "Column");

		setHeightMode(HeightMode.ROW);
		setHeightByRows(getContainerDataSource().size() >= 15 ? 15 : getContainerDataSource().size());
	}

	private String buildHeader(String rawHeader, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		if (rawHeader == null) {
			return UNKNOWN;
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

	private void formatContentEntry(Object[] entry) {
		if (columnsAttribute == StatisticsCaseAttribute.DISEASE) {
			entry[entry.length - 1] = Disease.valueOf(entry[entry.length - 1].toString()).toShortString();
			return;
		}
		
		if (columnsSubAttribute == StatisticsCaseSubAttribute.MONTH) {
			if ((int) entry[entry.length - 1] < 10) {
				entry[entry.length - 1] = "0" + entry[entry.length - 1];
			}
			return;
		}
	}
	
}
