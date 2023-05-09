package com.cinoteck.application.views.reports;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@SuppressWarnings("serial")
@Route(layout = ReportView.class)
public class CompletionAnalysisView extends VerticalLayout implements RouterLayout{

	private ComboBox<CampaignReferenceDto> campaign = new ComboBox<>();
	private ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>();
	private ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>();
	private ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>();
	private Button resetButton;

	List<CampaignReferenceDto> campaigns;
	List<CampaignReferenceDto> campaignPhases;
	List<AreaReferenceDto> regions;
	List<RegionReferenceDto> provinces;
	List<DistrictReferenceDto> districts;
	Grid<CampaignFormDataIndexDto> grid = new Grid<>(CampaignFormDataIndexDto.class, false);
	GridListDataView<CampaignFormDataIndexDto> dataView;
	List<CampaignFormDataIndexDto> analysis = FacadeProvider.getCampaignFormDataFacade().getByCompletionAnalysisNew(null, null, null);

	
	
	
	public CompletionAnalysisView() {
		
		
		
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setPadding(false);
		filterLayout.setVisible(false);
		filterLayout.setAlignItems(Alignment.END);

		campaign.setLabel("Campaigns");
		campaign.setPlaceholder("All Campaigns");
		campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
		campaign.setItems(campaigns);

		regionFilter.setLabel("Region");
		regionFilter.setPlaceholder("All Regions");
		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		regionFilter.addValueChangeListener(e -> {
			provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());
			provinceFilter.setItems(provinces);
		});

		
		provinceFilter.setLabel("Province");
		provinceFilter.setPlaceholder("All Province");
		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		provinceFilter.addValueChangeListener(e -> {
			districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid());
			districtFilter.setItems(districts);
		});
		
		districtFilter.setLabel("District");
		districtFilter.setPlaceholder("All District");
		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		
		resetButton =  new Button("Reset Filters");
		resetButton.addClickListener(e->{
			provinceFilter.clear();
			districtFilter.clear();
			regionFilter.clear();
	
		});
		
		
		Button displayFilters = new Button("Show Filters", new Icon(VaadinIcon.SLIDERS));
		displayFilters.addClickListener(e->{
			if(filterLayout.isVisible() == false) {
				filterLayout.setVisible(true);
				displayFilters.setText("Hide Filters");
			}else {
				filterLayout.setVisible(false);
				displayFilters.setText("Show Filters");
			}
		});
		
		filterLayout.add(campaign, regionFilter, provinceFilter, districtFilter, resetButton);
		
		
		
		
		add(displayFilters,filterLayout);
		completionAnalysisGrid();
		
		
	}
	private void completionAnalysisGrid() {
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		grid.addColumn(CampaignFormDataIndexDto::getArea).setHeader("Region").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getRegion).setHeader("Province").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getDistrict).setHeader("District").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getCcode).setHeader("CCode").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getClusternumber).setHeader("Cluster Number").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getAnalysis_a).setHeader("ICM Household Monitoringr").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getAnalysis_b).setHeader("ICM Revisits").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getAnalysis_c).setHeader("ICM Supervisor Monitoring").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto::getAnalysis_d).setHeader("ICM Team Monitoring").setSortable(true).setResizable(true);

		dataView = grid.setItems(analysis);
		grid.setVisible(true);
		grid.setAllRowsVisible(true);
		
		add(grid);
		
	}

}
