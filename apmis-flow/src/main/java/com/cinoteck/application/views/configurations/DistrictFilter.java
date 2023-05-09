package com.cinoteck.application.views.configurations;

import java.util.List;

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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

public class DistrictFilter extends VerticalLayout {
	private ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>();
	private ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>();
	private ComboBox<Object> riskFilter = new ComboBox<>();

	List<RegionReferenceDto> provinces;

	public DistrictFilter() {
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

		regionFilter.setLabel("Regions");
		regionFilter.setPlaceholder("All Regions");
		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		regionFilter.addValueChangeListener(e -> {
			provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());
			provinceFilter.setItems(provinces);
		});
		layout.add(regionFilter);

		provinceFilter.setLabel("Province");
		provinceFilter.setPlaceholder("All Provinces");
		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		layout.add(provinceFilter);

		riskFilter.setLabel(I18nProperties.getPrefixCaption(DistrictDto.I18N_PREFIX, DistrictDto.RISK));
		riskFilter.setItems("None Risk District (NRD)");
		riskFilter.setItems("High Risk District (HRD)");
		riskFilter.setItems("Very High Risk District (VHRD)");
		layout.add(riskFilter);

		Button resetButton = new Button("Reset Filters");
		resetButton.addClassName("resetButton");
		layout.add(resetButton);

		Button displayFilters = new Button("Show Filters", new Icon(VaadinIcon.SLIDERS));
		displayFilters.addClickListener(e -> {
			if (layout.isVisible() == false) {
				layout.setVisible(true);
				displayFilters.setText("Hide Filters");
			} else {
				layout.setVisible(false);
				displayFilters.setText("Show Filters");
			}
		});
		add(displayFilters, layout);

	}
}
