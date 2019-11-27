package de.symeda.sormas.ui.configuration.linelisting;

import java.util.List;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.DateHelper8;

@SuppressWarnings("serial")
public class LineListingConfigurationsGrid extends Grid<FeatureConfigurationIndexDto> {
	
	private List<FeatureConfigurationIndexDto> configurations;
	
	public LineListingConfigurationsGrid(List<FeatureConfigurationIndexDto> configurations) {
		this.configurations = configurations;
		buildGrid();
		reload();		
	}
	
	private void buildGrid() {
		setSelectionMode(SelectionMode.NONE);
		
		addColumn(FeatureConfigurationIndexDto::getDistrictName)
		.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.DISTRICT_NAME));
		addComponentColumn(config -> {
			CheckBox cbActive = new CheckBox();
			cbActive.addValueChangeListener(e -> {
				config.setActive(e.getValue());
			});
			cbActive.setValue(config.getActive() != null ? config.getActive() : Boolean.FALSE);
			return cbActive;
		})
		.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.ACTIVE));
		addComponentColumn(config -> {
			DateField dfEndDate = new DateField();
			dfEndDate.addValueChangeListener(e -> {
				config.setEndDate(DateHelper8.toDate(e.getValue()));
			});
			dfEndDate.setValue(DateHelper8.toLocalDate(config.getEndDate()));
			return dfEndDate;
		})
		.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.END_DATE));
	}
	
	public void reload() {
		setItems(configurations);
	}
	
}
