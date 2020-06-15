package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.ui.utils.FilteredGrid;

@SuppressWarnings("serial")
public class AggregateReportsGrid extends FilteredGrid<AggregatedCaseCountDto, AggregateReportCriteria> {

	public AggregateReportsGrid() {

		super(AggregatedCaseCountDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		setInEagerMode(true);

		setColumns(
			AggregatedCaseCountDto.DISEASE,
			AggregatedCaseCountDto.NEW_CASES,
			AggregatedCaseCountDto.LAB_CONFIRMATIONS,
			AggregatedCaseCountDto.DEATHS);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(AggregatedCaseCountDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}

		reload();
	}

	public void reload() {

		ListDataProvider<AggregatedCaseCountDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getAggregateReportFacade().getIndexList(getCriteria()).stream());
		setDataProvider(dataProvider);
		dataProvider.refreshAll();
	}
}
