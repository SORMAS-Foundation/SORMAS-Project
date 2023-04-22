package com.cinoteck.application.views.configurations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;
import de.symeda.sormas.api.utils.SortProperty;

//TODO: Change use of Region to Province where appropriate in line with the new naming rules
public class RegionGrid extends Grid<RegionIndexDto> {

	private RegionDataProvider dataProvider = new RegionDataProvider();

	private ConfigurableFilterDataProvider<RegionIndexDto, Void, RegionFilter> filterDataProvider = dataProvider
			.withConfigurableFilter();

	public RegionGrid() {
		Grid<RegionIndexDto> grid = new Grid<>(RegionIndexDto.class, true);
		grid.setSizeFull();
		grid.addColumn(RegionIndexDto::getArea).setHeader("Region").setSortable(true);
		grid.addColumn(RegionIndexDto::getAreaexternalId).setHeader("Rcode").setSortable(true);
		grid.addColumn(RegionIndexDto::getName).setHeader("Province").setSortable(true);
		grid.addColumn(RegionIndexDto::getExternalId).setHeader("PCode").setSortable(true);
		grid.setVisible(true);
		grid.setHeightFull();

		List<RegionIndexDto> regions = FacadeProvider.getRegionFacade().getAllRegions();
		GridListDataView<RegionIndexDto> dataView = grid.setItems(regions);


		TextField searchField = new TextField();
		searchField.setWidth("20%");
		searchField.setPlaceholder("Search");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> {

		});
	}
}
