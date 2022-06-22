package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class ReportDataGrid extends FilteredGrid<AggregateReportDto, AggregateReportCriteria> {

	public ReportDataGrid(AggregateReportCriteria criteria) {
		super(AggregateReportDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		setInEagerMode(true);
		setCriteria(criteria);

		addDefaultColumns();

		setStyleGenerator(aggregateReportDto -> {
			if (aggregateReportDto.getDuplicate()) {
				return CssStyles.BACKGROUND_DUPLICATE_AGGREGATE_REPORT;
			}
			return "";
		});

		reload();
	}

	protected void addDefaultColumns() {
		setColumns(
			AggregateReportDto.REPORTING_USER,
			AggregateReportDto.DISEASE,
			AggregateReportDto.REGION,
			AggregateReportDto.DISTRICT,
			AggregateReportDto.HEALTH_FACILITY,
			AggregateReportDto.POINT_OF_ENTRY,
			AggregateReportDto.YEAR,
			AggregateReportDto.EPI_WEEK,
			AggregateReportDto.AGE_GROUP,
			AggregateReportDto.NEW_CASES,
			AggregateReportDto.LAB_CONFIRMATIONS,
			AggregateReportDto.DEATHS);
	}

	public void reload() {
		ListDataProvider<AggregateReportDto> dataProvider = DataProvider
			.fromStream(FacadeProvider.getAggregateReportFacade().getDuplicateAggregateReports(getCriteria()).stream().map(aggregatedReportDto -> {
				if (aggregatedReportDto.getAgeGroup() != null) {
					aggregatedReportDto.setAgeGroup(AgeGroupUtils.createCaption(aggregatedReportDto.getAgeGroup()));
				}
				return aggregatedReportDto;
			}));
		setDataProvider(dataProvider);
		dataProvider.refreshAll();
	}
}
