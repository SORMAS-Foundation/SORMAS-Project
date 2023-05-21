package com.cinoteck.application.views.reports;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.report.JsonDictionaryReportModelDto;
import de.symeda.sormas.ui.utils.FilteredGrid;


@SuppressWarnings("serial")
@Route(layout = ReportView.class)

public class JsonDIctionaryGridView extends VerticalLayout implements RouterLayout{
	Grid<JsonDictionaryReportModelDto> grid = new Grid<>(JsonDictionaryReportModelDto.class, false);
	GridListDataView<JsonDictionaryReportModelDto> dataView;
	List<JsonDictionaryReportModelDto> analysis = FacadeProvider.getCampaignFormDataFacade().getByJsonFormDefinitonToCSV();
	
	public JsonDIctionaryGridView() {
		configureJsonDictionaryGrid();
		
	}
	
	public void configureJsonDictionaryGrid() {
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		
		grid.addColumn(JsonDictionaryReportModelDto::getFormid).setHeader("Id").setSortable(true).setResizable(true);
		grid.addColumn(JsonDictionaryReportModelDto::getId).setHeader("Form Id ").setSortable(true).setResizable(true);
		grid.addColumn(JsonDictionaryReportModelDto::getCaption).setHeader("Caption").setSortable(true).setResizable(true);
		grid.addColumn(JsonDictionaryReportModelDto::getFormtype).setHeader("Form Type").setSortable(true).setResizable(true);
		grid.addColumn(JsonDictionaryReportModelDto::getModality).setHeader("Campaign Modality").setSortable(true).setResizable(true);
		grid.addColumn(JsonDictionaryReportModelDto::getDatatype).setHeader("IData Type").setSortable(true).setResizable(true);
		
		dataView = grid.setItems(analysis);
		grid.setVisible(true);
		grid.setAllRowsVisible(true);
		
		add(grid);
}
	
	

}
