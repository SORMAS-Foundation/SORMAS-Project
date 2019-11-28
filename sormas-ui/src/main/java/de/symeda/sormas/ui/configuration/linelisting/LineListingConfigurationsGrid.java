package de.symeda.sormas.ui.configuration.linelisting;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.DateHelper8;

@SuppressWarnings("serial")
public class LineListingConfigurationsGrid extends Grid<FeatureConfigurationIndexDto> {

	private List<FeatureConfigurationIndexDto> configurations;
	private Set<FeatureConfigurationIndexDto> changedConfigurations;
	private Set<CheckBox> activeCheckBoxes;
	private Set<DateField> endDateFields;

	public LineListingConfigurationsGrid(List<FeatureConfigurationIndexDto> configurations, Set<FeatureConfigurationIndexDto> changedConfigurations) {
		this.configurations = configurations;
		this.changedConfigurations = changedConfigurations;
		this.activeCheckBoxes = new HashSet<>();
		this.endDateFields = new HashSet<>();
		buildGrid();
		reload();		
	}

	public void enableAll() {
		activeCheckBoxes.stream().forEach(checkBox -> checkBox.setValue(true));
	}

	public void disableAll() {
		activeCheckBoxes.stream().forEach(checkBox -> checkBox.setValue(false));
		endDateFields.stream().forEach(dateField -> dateField.setValue(null));
	}

	public void setEndDateForAll(LocalDate endDate) {
		endDateFields.stream().forEach(dateField -> dateField.setValue(endDate));
	}
	
	public boolean validateDates() {
		for (DateField dateField : endDateFields) {
			if (dateField.getErrorMessage() != null) {
				return false;
			}
		}
		
		return true;
	}

	private void buildGrid() {
		setSelectionMode(SelectionMode.NONE);

		addColumn(FeatureConfigurationIndexDto::getDistrictName)
		.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.DISTRICT_NAME));
		addComponentColumn(config -> {
			CheckBox cbActive = new CheckBox();
			cbActive.setValue(config.getActive() != null ? config.getActive() : Boolean.FALSE);
			cbActive.addValueChangeListener(e -> {
				config.setActive(e.getValue());
				changedConfigurations.add(config);
			});
			activeCheckBoxes.add(cbActive);
			return cbActive;
		})
		.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.ACTIVE));
		addComponentColumn(config -> {
			DateField dfEndDate = new DateField();
			dfEndDate.setValue(DateHelper8.toLocalDate(config.getEndDate()));
			dfEndDate.addValueChangeListener(e -> {
				if (e.getValue() != null && e.getValue().isBefore(LocalDate.now())) {
					Notification errorNotification = new Notification(I18nProperties.getString(Strings.headingInvalidDateEntered), 
							I18nProperties.getValidationError(Validations.noPastDateAllowed), Type.TRAY_NOTIFICATION);
					errorNotification.setStyleName("tray notification-error");
					errorNotification.show(Page.getCurrent());
				} else {
					config.setEndDate(DateHelper8.toDate(e.getValue()));
					changedConfigurations.add(config);
				}
			});
			dfEndDate.setRangeStart(LocalDate.now());
			endDateFields.add(dfEndDate);
			return dfEndDate;
		})
		.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.END_DATE));
	}

	public void reload() {
		setItems(configurations);
	}

}
