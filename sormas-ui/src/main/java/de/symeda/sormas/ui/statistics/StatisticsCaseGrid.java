package de.symeda.sormas.ui.statistics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.EnumUtils;

import com.vaadin.data.Item;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsCaseGrid extends Grid {

	private static final String COLUMN_ID = "Column_ID";
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
			StatisticsCaseAttribute columnsAttribute, StatisticsCaseSubAttribute columnsSubAttribute, boolean zeroValues, List<Object[]> content) {
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
			addRow(new Object[]{String.valueOf(content.get(0)[0])});
		} else {
			// Set columns
			Map<Object, Integer> columnsMap = null;

			addColumn(COLUMN_ID);
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

				// Add missing columns if zero values are ticked
				if (zeroValues) {
					List<Object> values = getAllAttributeValues(columnsAttribute, columnsSubAttribute);
					for (Object value : values) {
						Object[] valueArray = new Object[]{value};
						formatContentEntry(valueArray, 0, columnsAttribute, columnsSubAttribute);
						columns.put(valueArray[0].toString(), buildHeader(valueArray[0].toString() != null ? valueArray[0].toString() : null, columnsAttribute, columnsSubAttribute));
					}
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
							currentRow[0] = entry[1] != null ? entry[1] : COLUMN_UNKNOWN;
							currentRow[1] = buildHeader(entry[1] == null ? null : entry[1].toString(), rowsAttribute, rowsSubAttribute);
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
						currentRow[1] = buildHeader(entry[1] == null ? null : entry[1].toString(), rowsAttribute, rowsSubAttribute);
						currentRow[2] = entry[0].toString();
						addRow(currentRow);
						columnTotals[columnTotals.length - 1] += (long) entry[0];
					}
				}
				
				// Add missing rows if zero values are ticked
//				if (zeroValues) {
//					List<Object> values = getAllAttributeValues(rowsAttribute, rowsSubAttribute);
//					List<Object> existingValues = new ArrayList<>();
//					for (Object itemId : getContainerDataSource().getItemIds()) {
//						existingValues.add(getContainerDataSource().getContainerProperty(itemId, "").getValue());
//					}
//					
//					for (Object value : values) {
//						Object[] valueArray = new Object[]{value};
//						formatContentEntry(valueArray, 0, columnsAttribute, columnsSubAttribute);
//						Object[] row = new Object[getColumns().size()];
//						String header = buildHeader(valueArray[0].toString() != null ? valueArray[0].toString() : null, rowsAttribute, rowsSubAttribute);
//						if (!existingValues.contains(header)) {
//							row[1] = header;
//							addRow(row);
//						}
//					}
//				}
				
				if (columnsAttribute != null || columnsSubAttribute != null) {
					if (currentRow[1] != null) {
						int totalForRow = calculateTotalForRow(currentRow);
						currentRow[currentRow.length - 1] = String.valueOf(totalForRow);
						columnTotals[columnTotals.length - 1] += totalForRow;
						addRow(currentRow);
					}
				}
				
//				if (rowsAttribute.isSortByCaption() || zeroValues) {
//					getColumn("").setHidden(true);
//					sort("");
//				}
				getColumn(COLUMN_ID).setHidden(true);
				sort(COLUMN_ID);


				Item unknownItem = getContainerDataSource().getItem(COLUMN_UNKNOWN);
				if (unknownItem != null) {
					getContainerDataSource().removeItem(unknownItem);
					Item newItem = getContainerDataSource().addItemAt(getContainerDataSource().indexOfId(COLUMN_TOTAL), COLUMN_UNKNOWN);
					for (Object propertyId : getContainerDataSource().getContainerPropertyIds()) {
						newItem.getItemProperty(propertyId).setValue(unknownItem.getItemProperty(propertyId).getValue());
					}
				}

				// Total row
				currentRow = new Object[getColumns().size()];
				currentRow[1] = "Total";
				for (int i = 2; i < columnTotals.length; i++) {
					currentRow[i] = String.valueOf(columnTotals[i]);
				}
				addRow(currentRow);
			} else {
				Object[] row = new Object[getColumns().size()];
				row[1] = "Number of cases";
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

		setHeightMode(HeightMode.UNDEFINED);
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
			case EPI_WEEK:
				return "Wk " + rawHeader;
			case QUARTER_OF_YEAR:
				return "Q" + rawHeader.charAt(rawHeader.length() - 1) + " " + rawHeader.substring(0, 4);
			case MONTH_OF_YEAR:
				int month = Integer.valueOf(rawHeader.substring(4));
				return Month.values()[month - 1].toString() + " " + rawHeader.substring(0, 4);
			case EPI_WEEK_OF_YEAR:
				// see EpiWeek.toString
				return "Wk " + rawHeader.substring(4) + "-" + rawHeader.substring(0, 4);
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

		if (subAttribute == StatisticsCaseSubAttribute.MONTH || subAttribute == StatisticsCaseSubAttribute.EPI_WEEK) {
			if ((int) entry[indexToFormat] < 10) {
				entry[indexToFormat] = "0" + entry[indexToFormat];
			}
			return;
		}
	}

	private int calculateTotalForRow(Object[] row) {
		int total = 0;
		for (int i = 2; i < row.length; i++) {
			if (row[i] != null) {
				total += Integer.valueOf(row[i].toString());
			}
		}

		return total;
	}

	private List<Object> getAllAttributeValues(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		if (subAttribute != null) {
			switch (subAttribute) {
			case YEAR:
			case QUARTER:
			case MONTH:
			case EPI_WEEK:
			case QUARTER_OF_YEAR:
			case MONTH_OF_YEAR:
			case EPI_WEEK_OF_YEAR:
				return getListOfDateValues(attribute, subAttribute);
			case REGION:
				return new ArrayList<>(FacadeProvider.getRegionFacade().getAllUuids());
			case DISTRICT:
				return new ArrayList<>(FacadeProvider.getDistrictFacade().getAllUuids());
			default:
				throw new IllegalArgumentException(subAttribute.toString());
			}
		} else {
			switch (attribute) {
			case SEX:
				return new ArrayList<>(EnumUtils.getEnumList(Sex.class).stream()
						.map(s -> s.getName())
						.collect(Collectors.toList()));
			case DISEASE:
				return new ArrayList<>(EnumUtils.getEnumList(Disease.class).stream()
						.map(d -> d.getName())
						.collect(Collectors.toList()));
			case CLASSIFICATION:
				return new ArrayList<>(EnumUtils.getEnumList(CaseClassification.class).stream()
						.map(c -> c.getName())
						.collect(Collectors.toList()));
			case OUTCOME:
				return new ArrayList<>(EnumUtils.getEnumList(CaseOutcome.class).stream()
						.map(o -> o.getName())
						.collect(Collectors.toList()));
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				return getListOfAgeIntervalValues(attribute);
			default:
				throw new IllegalArgumentException(attribute.toString());
			}
		}
	}

	private List<Object> getListOfDateValues(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		Date oldestCaseDate = null;
		switch (attribute) {
		case ONSET_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseOnsetDate();
			break;
		case RECEPTION_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseReceptionDate();
			break;
		case REPORT_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseReportDate();
			break;
		default:
			return new ArrayList<>();
		}

		LocalDate earliest = oldestCaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate now = LocalDate.now();

		switch (subAttribute) {
		case YEAR:
			return IntStream.rangeClosed(earliest.getYear(), now.getYear()).boxed()
					.collect(Collectors.toList());
		case QUARTER:
			return IntStream.rangeClosed(1, 4).boxed()
					.collect(Collectors.toList());
		case MONTH:
			return IntStream.rangeClosed(1, 12).boxed()
					.collect(Collectors.toList());
		case EPI_WEEK:
			return IntStream.rangeClosed(1, DateHelper.getMaximumEpiWeekNumber()).boxed()
					.collect(Collectors.toList());
		case QUARTER_OF_YEAR:
			List<Object> quarterOfYearList = new ArrayList<>();
			QuarterOfYear earliestQuarter = new QuarterOfYear(1, earliest.getYear());
			QuarterOfYear latestQuarter = new QuarterOfYear(4, now.getYear());
			while (earliestQuarter.getYear() <= latestQuarter.getYear()) {
				QuarterOfYear newQuarter = new QuarterOfYear(earliestQuarter.getQuarter(), earliestQuarter.getYear());
				quarterOfYearList.add(newQuarter.getYear() * 10 + newQuarter.getQuarter());
				earliestQuarter.increaseQuarter();
			}
			return quarterOfYearList;
		case MONTH_OF_YEAR:
			List<Object> monthOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				final int thisYear = year;
				monthOfYearList.addAll(
						IntStream.rangeClosed(1, 12).boxed()
						.map(m -> thisYear * 100 + m)
						.collect(Collectors.toList()));
			}
			return monthOfYearList;
		case EPI_WEEK_OF_YEAR:
			List<Object> epiWeekOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				final int thisYear = year;
				epiWeekOfYearList.addAll(
						IntStream.rangeClosed(1, DateHelper.createEpiWeekList(year).size()).boxed()
						.map(w -> thisYear * 100 + w)
						.collect(Collectors.toList()));
			}
			return epiWeekOfYearList;
		default:
			return new ArrayList<>();
		}
	}

	private List<Object> getListOfAgeIntervalValues(StatisticsCaseAttribute attribute) {
		List<Object> ageIntervalList = new ArrayList<>();
		switch (attribute) {
		case AGE_INTERVAL_1_YEAR:
			for (int i = 0; i < 80; i++) {
				ageIntervalList.add(i < 10 ? "0" + i : String.valueOf(i));
			}
			break;
		case AGE_INTERVAL_5_YEARS:
			for (int i = 0; i < 80; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			break;
		case AGE_INTERVAL_CHILDREN_COARSE:
			ageIntervalList.add(new IntegerRange(0, 14));
			for (int i = 15; i < 30; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new IntegerRange(i, i + 9));
			}
			break;
		case AGE_INTERVAL_CHILDREN_FINE:
			for (int i = 0; i < 5; i++) {
				ageIntervalList.add(new IntegerRange(i, i));
			}
			for (int i = 5; i < 30; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new IntegerRange(i, i + 9));
			}
			break;
		case AGE_INTERVAL_CHILDREN_MEDIUM:
			for (int i = 0; i < 30; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new IntegerRange(i, i + 9));
			}
			break;
		case AGE_INTERVAL_BASIC:
			ageIntervalList.add(new IntegerRange(0, 0));
			ageIntervalList.add(new IntegerRange(1, 4));
			ageIntervalList.add(new IntegerRange(5, 14));
			ageIntervalList.add(new IntegerRange(15, null));
			break;
		default:
			throw new IllegalArgumentException(attribute.toString());
		}
		
		if (attribute != StatisticsCaseAttribute.AGE_INTERVAL_BASIC) {
			ageIntervalList.add(new IntegerRange(80, null));
		}
		ageIntervalList.add(new IntegerRange(null, null));
		return ageIntervalList;
	}

}
