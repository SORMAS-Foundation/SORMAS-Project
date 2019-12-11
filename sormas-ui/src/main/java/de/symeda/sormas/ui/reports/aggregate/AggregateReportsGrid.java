package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;

@SuppressWarnings("serial")
public class AggregateReportsGrid extends FilteredGrid<AggregatedCaseCountDto, AggregateReportCriteria> {
	
	public AggregateReportsGrid(AggregateReportCriteria criteria) {
		super(AggregatedCaseCountDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		setInEagerMode(true);
		setCriteria(criteria);
		
		ListDataProvider<AggregatedCaseCountDto> dataProvider = DataProvider.fromStream(
				FacadeProvider.getAggregateReportFacade().getIndexList(getCriteria(), UserProvider.getCurrent().getUuid()).stream());
		setDataProvider(dataProvider);
		
		setColumns(AggregatedCaseCountDto.DISEASE, AggregatedCaseCountDto.NEW_CASES,
				AggregatedCaseCountDto.LAB_CONFIRMATIONS, AggregatedCaseCountDto.DEATHS);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					AggregatedCaseCountDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
	}
	
	public void reload() {
		getDataProvider().refreshAll();
	}

}
