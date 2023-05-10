package com.cinoteck.application.views.configurations;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;

public class ProvinceFilter extends VerticalLayout{
	
public ProvinceFilter() {
	setMargin(true);
	HorizontalLayout layout = new HorizontalLayout();
	layout.setPadding(false);
	layout.setVisible(false);
	layout.setAlignItems(Alignment.END);
	
	
	TextField searchField = new TextField();
	searchField.setWidth("30%");
	searchField.addClassName("filterBar");
	searchField.setPlaceholder("Search");
	searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
	searchField.setValueChangeMode(ValueChangeMode.EAGER);
	searchField.addValueChangeListener(e -> {

	});
	
	layout.add(searchField);
	
	ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>();
	regionFilter.setLabel("Regions");
	regionFilter.setPlaceholder("All Regions");
	regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
	layout.add(regionFilter);
	

	Button resetButton = new Button("Reset Filters");
	resetButton.addClassName("resetButton");
	layout.add(resetButton);
	
	
	Button displayFilters = new Button("Show Filters", new Icon(VaadinIcon.SLIDERS));
	displayFilters.addClickListener(e->{
		if(layout.isVisible() == false) {
			layout.setVisible(true);
			displayFilters.setText("Hide Filters");
		}else {
			layout.setVisible(false);
			displayFilters.setText("Show Filters");
		}
	});
	
	
	add(displayFilters, layout);
}
}
