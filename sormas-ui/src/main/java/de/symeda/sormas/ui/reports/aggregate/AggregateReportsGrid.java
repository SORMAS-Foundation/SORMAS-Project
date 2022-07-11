package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.report.AggregateCaseCountDto;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportGroupingLevel;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.ui.utils.FilteredGrid;

@SuppressWarnings("serial")
public class AggregateReportsGrid extends FilteredGrid<AggregateCaseCountDto, AggregateReportCriteria> {

	public AggregateReportsGrid(AggregateReportCriteria criteria) {

		super(AggregateCaseCountDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		setInEagerMode(true);
		setCriteria(criteria);

		addDefaultColumns();
		setColumnsVisibility(criteria.getAggregateReportGroupingLevel());

		reload();
	}

	protected void addDefaultColumns() {
		setColumns(
			AggregateCaseCountDto.DISEASE,
			AggregateCaseCountDto.REPORTING_USER,
			AggregateCaseCountDto.REGION_NAME,
			AggregateCaseCountDto.DISTRICT_NAME,
			AggregateCaseCountDto.HEALTH_FACILITY_NAME,
			AggregateCaseCountDto.POINT_OF_ENTRY_NAME,
			AggregateCaseCountDto.YEAR,
			AggregateCaseCountDto.EPI_WEEK,
			AggregateCaseCountDto.AGE_GROUP,
			AggregateCaseCountDto.NEW_CASES,
			AggregateCaseCountDto.LAB_CONFIRMATIONS,
			AggregateCaseCountDto.DEATHS);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(AggregateCaseCountDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void setColumnsVisibility(AggregateReportGroupingLevel groupingLevel) {
		setRegionColumnVisible(
			AggregateReportGroupingLevel.REGION.equals(groupingLevel)
				|| AggregateReportGroupingLevel.DISTRICT.equals(groupingLevel)
				|| AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel)
				|| AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel));
		setDistrictColumnVisible(
			AggregateReportGroupingLevel.DISTRICT.equals(groupingLevel)
				|| AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel)
				|| AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel));

		setHealthFacilityColumnVisible(AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel));

		setPointOfEntryColumnVisible(AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel));
	}

	private void setRegionColumnVisible(boolean visible) {
		getColumn(AggregateCaseCountDto.REGION_NAME).setHidden(!visible);
	}

	private void setDistrictColumnVisible(boolean visible) {
		getColumn(AggregateCaseCountDto.DISTRICT_NAME).setHidden(!visible);
	}

	private void setHealthFacilityColumnVisible(boolean visible) {
		getColumn(AggregateCaseCountDto.HEALTH_FACILITY_NAME).setHidden(!visible);
	}

	private void setPointOfEntryColumnVisible(boolean visible) {
		getColumn(AggregateCaseCountDto.POINT_OF_ENTRY_NAME).setHidden(!visible);
	}

	public void reload() {

		ListDataProvider<AggregateCaseCountDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getAggregateReportFacade().getIndexList(getCriteria()).stream().map(aggregatedCaseCountDto -> {
				if (aggregatedCaseCountDto.getAgeGroup() != null) {
					aggregatedCaseCountDto.setAgeGroup(AgeGroupUtils.createCaption(aggregatedCaseCountDto.getAgeGroup()));
				}
				return aggregatedCaseCountDto;
			}));
		setDataProvider(dataProvider);
		dataProvider.refreshAll();
	}
}
