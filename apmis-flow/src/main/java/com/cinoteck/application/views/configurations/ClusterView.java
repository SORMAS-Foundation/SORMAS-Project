package com.cinoteck.application.views.configurations;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

@PageTitle("Clusters")
@Route(value = "clusters", layout = ConfigurationsView.class)
public class ClusterView extends Div { 

	
	public ClusterView() {
		Grid<CommunityDto> grid = new Grid<>(CommunityDto.class, false);
		
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		grid.addColumn(CommunityDto::getAreaname).setHeader("Region").setSortable(true).setResizable(true);
		grid.addColumn(CommunityDto::getAreaexternalId).setHeader("Rcode").setResizable(true).setSortable(true);
		grid.addColumn(CommunityDto::getRegion).setHeader("Province").setSortable(true).setResizable(true);
		grid.addColumn(CommunityDto::getRegionexternalId).setHeader("PCode").setResizable(true).setSortable(true);
		grid.addColumn(CommunityDto::getDistrict).setHeader("District").setSortable(true).setResizable(true);
		grid.addColumn(CommunityDto::getDistrictexternalId).setHeader("DCode").setResizable(true).setSortable(true);
		grid.addColumn(CommunityDto::getName).setHeader("Cluster").setSortable(true).setResizable(true);
		grid.addColumn(CommunityDto::getExternalId).setHeader("CCode").setResizable(true).setSortable(true);
		
		grid.setVisible(true);
		grid.setAllRowsVisible(true);
		List<CommunityDto> clusters = FacadeProvider.getCommunityFacade().getAllCommunities();
		GridListDataView<CommunityDto> dataView = grid.setItems(clusters);
		
		addFilters();
		add(grid);
	}
	
	//TODO: Hide the filter bar on smaller screens
	public void addFilters() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setPadding(false);
		
		ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>("Region");
		regionFilter.setPlaceholder("All Regions");
		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		layout.add(regionFilter);
		
		ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>("Province");
		provinceFilter.setPlaceholder("All Provinces");
		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		layout.add(provinceFilter);
		
		ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>("District");
		districtFilter.setPlaceholder("All Districts");
		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		layout.add(districtFilter);
		
		ComboBox<RegionIndexDto> communityFilter = new ComboBox<>("Cluster");
		communityFilter.setPlaceholder("All Clusters");
		//communityFilter.setItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(null));
		layout.add(communityFilter);
		
		TextField searchField = new TextField();
		searchField.setWidth("10%");
		searchField.addClassName("filterBar");
		searchField.setPlaceholder("Search");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> {

		});
		
		layout.add(searchField);
		
		
		Button primaryButton = new Button("Reset Filters");
		primaryButton.addClassName("resetButton");
		primaryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		layout.add(primaryButton);
		
		
		
		add(layout);
	}
}
