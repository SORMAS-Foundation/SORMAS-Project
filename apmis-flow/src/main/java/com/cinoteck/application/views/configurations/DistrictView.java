package com.cinoteck.application.views.configurations;

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
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
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;


@PageTitle("Districts")
@Route(value = "districts", layout = ConfigurationsView.class)
public class DistrictView extends Div { 

	private DistrictFilter districtFilter = new DistrictFilter();
	GridListDataView<DistrictIndexDto> dataView;
	
	public DistrictView() {
		
		Grid<DistrictIndexDto> grid = new Grid<>(DistrictIndexDto.class, false);
		
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		grid.addColumn(DistrictIndexDto::getAreaname).setHeader("Region").setSortable(true).setResizable(true);
		grid.addColumn(DistrictIndexDto::getAreaexternalId).setHeader("Rcode").setResizable(true).setSortable(true);
		grid.addColumn(DistrictIndexDto::getRegion).setHeader("Province").setSortable(true).setResizable(true);
		grid.addColumn(DistrictIndexDto::getRegionexternalId).setHeader("PCode").setResizable(true).setSortable(true);
		grid.addColumn(DistrictIndexDto::getName).setHeader("District").setSortable(true).setResizable(true);
		grid.addColumn(DistrictIndexDto::getExternalId).setHeader("DCode").setResizable(true).setSortable(true);

		
		grid.setVisible(true);
		grid.setAllRowsVisible(true);
		List<DistrictIndexDto> regions = FacadeProvider.getDistrictFacade().getAllDistricts();
		this.dataView = grid.setItems(regions);
		
//		addFilters();
		add(districtFilter, grid);
	}
	
//	public Component addFilters() {
//		HorizontalLayout layout = new HorizontalLayout();
//		layout.setPadding(false);
//		
//		ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>("Region");
//		regionFilter.setPlaceholder("All Regions");
//		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
//		layout.add(regionFilter);
//		
//		ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>("Province");
//		provinceFilter.setPlaceholder("All Provinces");
//		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
//		layout.add(provinceFilter);
//		
//		//TODO: Implement auto selection of filter values when User level restriction has been implemented
////		if(UserProvider.getCurrent().getUser().getArea() != null) {
////			regionFilter.setItems(UserProvider.getCurrent().getUser().getArea());
////		}else {
////			regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
////		}
//		regionFilter.addValueChangeListener(e -> {
//			provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid()));
//			dataView.addFilter(f -> f.getAreaname().equalsIgnoreCase(regionFilter.getValue().getCaption()));
//			//dataView.refreshAlsl();
//		});
//		
//		provinceFilter.addValueChangeListener(e -> {
//			dataView.addFilter(f -> f.getRegion().getCaption().equalsIgnoreCase(provinceFilter.getValue().getCaption()));
//			//dataView.refreshAll();
//		});
//		
//		
//		//TODO: Confirm if cluster level and district filter need to be available for District view
////		ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>("District");
////		districtFilter.setPlaceholder("All Districts");
////		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
////		layout.add(districtFilter);
////		
////		ComboBox<RegionIndexDto> communityFilter = new ComboBox<>("Cluster");
////		communityFilter.setPlaceholder("All Clusters");
////		//communityFilter.setItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(null));
////		layout.add(communityFilter);
//		
//		TextField searchField = new TextField();
//		searchField.setWidth("10%");
//		searchField.addClassName("filterBar");
//		searchField.setPlaceholder("Search");
//		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
//		searchField.setValueChangeMode(ValueChangeMode.EAGER);
//		searchField.addValueChangeListener(e -> {
//			dataView.refreshAll();
//		});
//
////        dataView.addFilter(e -> {
////            String searchTerm = searchField.getValue().trim();
////
////            if (searchTerm.isEmpty())
////                return true;
////
////            boolean matchesRegionName = matchesTerm(e.getAreaname(),
////                    searchTerm);
////            boolean matchesProvinceName = matchesTerm(e.getRegion().getCaption(),
////                    searchTerm);
////            boolean matchesDistrictName = matchesTerm(e.getName(),
////                    searchTerm);
////            return matchesRegionName || matchesProvinceName || matchesDistrictName;
////        });
//
//		layout.add(searchField);
//	
//		Button primaryButton = new Button("Reset Filters");
//		primaryButton.addClassName("resetButton");
//		primaryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		layout.add(primaryButton);
//		primaryButton.addClickListener(e ->{
//			dataView.removeFilters();
//			regionFilter.clear();
//			provinceFilter.clear();
//			dataView.refreshAll();
//		});
//		
//		
//		add(layout);
//		return layout;
//	}
	
	
	private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
	
//	private boolean matchesCode(Long value, Long searchTerm) {
//        return assertThat(value.equals(searchTerm)).isTrue();
//    }
}
