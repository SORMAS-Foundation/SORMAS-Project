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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

@PageTitle("Province")
@Route(value = "province", layout = ConfigurationsView.class)

public class ProvinceView extends VerticalLayout implements RouterLayout{ 

	private static final long serialVersionUID = 8159316049907141477L;

	private ProvinceFilter provinceFilter = new ProvinceFilter();

	public ProvinceView() {
		setHeightFull();
		
		Grid<RegionIndexDto> grid = new Grid<>(RegionIndexDto.class, false);

		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		grid.addColumn(RegionIndexDto::getArea).setHeader("Region").setSortable(true).setResizable(true);
		grid.addColumn(RegionIndexDto::getAreaexternalId).setHeader("Rcode").setResizable(true).setSortable(true);
		grid.addColumn(RegionIndexDto::getName).setHeader("Province").setSortable(true).setResizable(true);
		grid.addColumn(RegionIndexDto::getExternalId).setHeader("PCode").setSortable(true).setResizable(true);
		
		
		grid.setVisible(true);
		grid.setAllRowsVisible(true);
		grid.setHeight("76vh");
		List<RegionIndexDto> regions = FacadeProvider.getRegionFacade().getAllRegions();
		GridListDataView<RegionIndexDto> dataView = grid.setItems(regions);

		

		//VerticalLayout layout = new VerticalLayout(searchField, grid);
		//layout.setPadding(false);
		
		add(provinceFilter,grid);
		
	}

	
	
}
