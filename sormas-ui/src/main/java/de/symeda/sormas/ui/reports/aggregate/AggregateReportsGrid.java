package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportGroupingLevel;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.ui.utils.FilteredGrid;

@SuppressWarnings("serial")
public class AggregateReportsGrid extends FilteredGrid<AggregatedCaseCountDto, AggregateReportCriteria> {

	public AggregateReportsGrid(AggregateReportCriteria criteria) {

		super(AggregatedCaseCountDto.class);
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
			AggregatedCaseCountDto.DISEASE,
			AggregatedCaseCountDto.REPORTING_USER,
			AggregatedCaseCountDto.REGION_NAME,
			AggregatedCaseCountDto.DISTRICT_NAME,
			AggregatedCaseCountDto.HEALTH_FACILITY_NAME,
			AggregatedCaseCountDto.POINT_OF_ENTRY_NAME,
			AggregatedCaseCountDto.YEAR,
			AggregatedCaseCountDto.EPI_WEEK,
			AggregatedCaseCountDto.AGE_GROUP,
			AggregatedCaseCountDto.NEW_CASES,
			AggregatedCaseCountDto.LAB_CONFIRMATIONS,
			AggregatedCaseCountDto.DEATHS);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(AggregatedCaseCountDto.I18N_PREFIX, column.getId(), column.getCaption()));
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
		getColumn(AggregatedCaseCountDto.REGION_NAME).setHidden(!visible);
	}

	private void setDistrictColumnVisible(boolean visible) {
		getColumn(AggregatedCaseCountDto.DISTRICT_NAME).setHidden(!visible);
	}

	private void setHealthFacilityColumnVisible(boolean visible) {
		getColumn(AggregatedCaseCountDto.HEALTH_FACILITY_NAME).setHidden(!visible);
	}

	private void setPointOfEntryColumnVisible(boolean visible) {
		getColumn(AggregatedCaseCountDto.POINT_OF_ENTRY_NAME).setHidden(!visible);
	}

	public void reload() {

		ListDataProvider<AggregatedCaseCountDto> dataProvider =
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
