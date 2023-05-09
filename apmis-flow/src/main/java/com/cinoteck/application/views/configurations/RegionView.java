package com.cinoteck.application.views.configurations;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import java.util.List;

@PageTitle("Regions")
@Route(value = "regions", layout = ConfigurationsView.class)
public class RegionView extends VerticalLayout implements RouterLayout {

	
	GridListDataView<AreaDto> dataView;

	public RegionView() {
		RegionFilter regionFilter = new RegionFilter();
		Grid<AreaDto> grid = new Grid<>(AreaDto.class, false);

		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		grid.addColumn(AreaDto::getName).setHeader("Region").setSortable(true).setResizable(true);
		grid.addColumn(AreaDto::getExternalId).setHeader("Rcode").setResizable(true).setSortable(true);

		grid.setVisible(true);
		grid.setAllRowsVisible(true);
		List<AreaDto> regions = FacadeProvider.getAreaFacade().getAllActiveAsReferenceAndPopulation();
		this.dataView = grid.setItems(regions);

		// VerticalLayout layout = new VerticalLayout(searchField, grid);
		// layout.setPadding(false);
		add(regionFilter);
		add(grid);

	}

	// TODO: Hide the filter bar on smaller screens
//	public Component addRegionAction() {
//		Button displayActionButtons = new Button("Show Filters", new Icon(VaadinIcon.SLIDERS));
//		HorizontalLayout layout = new HorizontalLayout();
//		layout.setPadding(false);
//		layout.setVisible(false);
//
//		Icon searchIcon = new Icon(VaadinIcon.SEARCH);
//		searchIcon.getStyle().set("color", "green !important");
//
//		TextField searchField = new TextField();
//
//		searchField.setPlaceholder("Search");
//		searchField.setPrefixComponent(searchIcon);
//		searchField.setValueChangeMode(ValueChangeMode.EAGER);
////		searchField.setWidth("10%");
//
//		searchField.addClassName("filterBar");
//		searchField.addValueChangeListener(e -> {
//
//		});
//	
//
//		Button clear = new Button("Clear Search");
//		clear.getStyle().set("color", "white");
//		clear.getStyle().set("background", "#0C5830");
//		
//		
//		ComboBox<AreaReferenceDto> activeRegions = new ComboBox<>();
//
//		activeRegions.getStyle().set("color", "green");
//		activeRegions.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
//		layout.add(searchField, clear,activeRegions);
//		
//		displayActionButtons.addClickListener(e->{
//			if (layout.isVisible() == false) {
//				
//				layout.setVisible(true);
//				displayActionButtons.setText("Hide Filters");
//			}else {
//				
//				layout.setVisible(false);
//				displayActionButtons.setText("Show Filters");
//			}
//		});
//		add(displayActionButtons, layout);
//		
//		return null;
//	}
//		HorizontalLayout layout = new HorizontalLayout();
//		layout.setPadding(false);
//
//		Icon searchIcon = new Icon(VaadinIcon.SEARCH);
//		searchIcon.getStyle().set("color", "green !important");
//
//		TextField searchField = new TextField();
//
//		searchField.setPlaceholder("Search");
//		searchField.setPrefixComponent(searchIcon);
//		searchField.setValueChangeMode(ValueChangeMode.EAGER);
////		searchField.setWidth("10%");
//
//		searchField.addClassName("filterBar");
//		searchField.addValueChangeListener(e -> {
//
//		});
//		layout.add(searchField);
//
//		Button clear = new Button("Clear Search");
//		clear.getStyle().set("color", "white");
//		clear.getStyle().set("background", "#0C5830");
//		layout.add(clear);
//
////		ComboBox<AreaReferenceDto> importanceFilter = new ComboBox<>("Region");
////		importanceFilter.setPlaceholder("");
////		ComboBox.addItem("");
////		layout.add(importanceFilter);
//
////
//		ComboBox<AreaReferenceDto> activeRegions = new ComboBox<>();
//
//		activeRegions.getStyle().set("color", "green");
////		activeRegions.getStyle().set("border", "1px solid green");
//
//		activeRegions.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
//		layout.add(activeRegions);
////
////		ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>("Region");
////		regionFilter.setPlaceholder("All Regions");
////		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
////		layout.add(regionFilter);
////
////		ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>("Province");
////		provinceFilter.setPlaceholder("All Provinces");
////		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
////		layout.add(provinceFilter);
////
////		ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>("District");
////		districtFilter.setPlaceholder("All Districts");
////		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
////		layout.add(districtFilter);
////
////		ComboBox<RegionIndexDto> communityFilter = new ComboBox<>("Cluster");
////		communityFilter.setPlaceholder("All Clusters");
////		// communityFilter.setItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(null));
////		layout.add(communityFilter);
////
////		Button primaryButton = new Button("Reset Filters");
////		primaryButton.addClassName("resetButton");
////		primaryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
////		layout.add(primaryButton);
//
//		add(layout);
//		return layout;
//	}
}
