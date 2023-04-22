package com.cinoteck.application.views.configurations;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;

public class RegionFilter extends VerticalLayout {
	String searchTerm;

	public RegionFilter() {
		setMargin(true);
		Button displayActionButtons = new Button("Show Filters", new Icon(VaadinIcon.SLIDERS));

		HorizontalLayout layout = new HorizontalLayout();
		layout.setPadding(false);
		layout.setVisible(false);
		layout.setAlignItems(Alignment.END);
		
		Icon searchIcon = new Icon(VaadinIcon.SEARCH);
		searchIcon.getStyle().set("color", "green !important");

		TextField searchField = new TextField();

		searchField.setPlaceholder("Search");
		searchField.setPrefixComponent(searchIcon);
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.setWidth("30%");

		searchField.addClassName("filterBar");
		searchField.addValueChangeListener(e -> {

		});

		Button clear = new Button("Clear Search");
		clear.getStyle().set("color", "white");
		clear.getStyle().set("background", "#0C5830");

		ComboBox relevanceStatusFilter = new ComboBox<>();

		relevanceStatusFilter.getStyle().set("color", "green");
		relevanceStatusFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());

		relevanceStatusFilter.setItems((Object[]) EntityRelevanceStatus.values());
		relevanceStatusFilter.setItems(EntityRelevanceStatus.ACTIVE,
				I18nProperties.getCaption(Captions.districtActiveDistricts));
		relevanceStatusFilter.setItems(EntityRelevanceStatus.ARCHIVED,
				I18nProperties.getCaption(Captions.districtArchivedDistricts));
		relevanceStatusFilter.setItems(EntityRelevanceStatus.ALL,
				I18nProperties.getCaption(Captions.districtAllDistricts));
		relevanceStatusFilter.addValueChangeListener(e -> {

		});
		layout.add(searchField, clear, relevanceStatusFilter);

		displayActionButtons.addClickListener(e -> {
			if (layout.isVisible() == false) {

				layout.setVisible(true);
				displayActionButtons.setText("Hide Filters");
			} else {

				layout.setVisible(false);
				displayActionButtons.setText("Show Filters");
			}
		});
		add(displayActionButtons, layout);
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public boolean test(RegionIndexDto e) {
		boolean matchesFullName = matches(e.getName(), searchTerm);
		return matchesFullName;
	}

	private boolean matches(String value, String searchTerm) {
		return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
	}
}
