package de.symeda.sormas.ui.configuration.linelisting;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.DateHelper8;

@SuppressWarnings("serial")
public class LineListingConfigurationsGrid extends Grid<FeatureConfigurationIndexDto> {

	private boolean nationLevel;
	private List<FeatureConfigurationIndexDto> configurations;
	private Set<FeatureConfigurationIndexDto> changedConfigurations;
	private Map<FeatureConfigurationIndexDto, DateField> dateFieldMap;

	public LineListingConfigurationsGrid(List<FeatureConfigurationIndexDto> configurations, boolean nationLevel) {

		this.nationLevel = nationLevel;
		this.configurations = configurations;
		this.changedConfigurations = new HashSet<>();
		this.dateFieldMap = new HashMap<>();
		buildGrid();
		reload();
	}

	public void enableAll() {

		configurations.stream().forEach(config -> {
			config.setEnabled(true);
			if (config.getEndDate() == null) {
				config.setEndDate(DateHelper.addDays(new Date(), 21));
			}
		});
		changedConfigurations.addAll(configurations);
		reload();
	}

	public void disableAll() {
		configurations.stream().forEach(config -> {
			config.setEnabled(false);
			config.setEndDate(null);
		});
		changedConfigurations.addAll(configurations);
		reload();
	}

	public void setEndDateForAll(LocalDate endDate) {
		configurations.stream().forEach(config -> config.setEndDate(DateHelper8.toDate(endDate)));
		changedConfigurations.addAll(configurations);
		reload();
	}

	public boolean validateDates() {
		for (FeatureConfigurationIndexDto config : configurations) {
			if (dateFieldMap.get(config) != null && dateFieldMap.get(config).getErrorMessage() != null) {
				return false;
			}
		}

		return true;
	}

	private void buildGrid() {

		setSelectionMode(SelectionMode.NONE);

		if (nationLevel) {
			addColumn(FeatureConfigurationIndexDto::getRegionName)
				.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.REGION_NAME));
		}

		addColumn(FeatureConfigurationIndexDto::getDistrictName)
			.setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.DISTRICT_NAME));
		addComponentColumn(config -> {
			CheckBox cbActive = new CheckBox();
			cbActive.setValue(config.isEnabled());
			cbActive.addValueChangeListener(e -> {
				config.setEnabled(e.getValue());
				if (Boolean.TRUE.equals(e.getValue())) {
					config.setEndDate(DateHelper.addDays(new Date(), 21));
					dateFieldMap.get(config).setValue(DateHelper8.toLocalDate(DateHelper.addDays(new Date(), 21)));
				} else {
					config.setEndDate(null);
					dateFieldMap.get(config).setValue(null);
				}
				changedConfigurations.add(config);
			});
			return cbActive;
		}).setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.ENABLED));
		addComponentColumn(config -> {
			DateField dfEndDate = new DateField();
			dfEndDate.setValue(DateHelper8.toLocalDate(config.getEndDate()));
			dfEndDate.addValueChangeListener(e -> {
				if (e.getValue() != null && e.getValue().isBefore(LocalDate.now())) {
					Notification errorNotification = new Notification(
						I18nProperties.getString(Strings.headingInvalidDateEntered),
						I18nProperties.getValidationError(Validations.noPastDateAllowed),
						Type.TRAY_NOTIFICATION);
					errorNotification.setStyleName("tray notification-error");
					errorNotification.show(Page.getCurrent());
				} else {
					config.setEndDate(DateHelper8.toDate(e.getValue()));
					changedConfigurations.add(config);
				}
			});
			dfEndDate.setRangeStart(LocalDate.now());
			dateFieldMap.put(config, dfEndDate);
			return dfEndDate;
		}).setCaption(I18nProperties.getPrefixCaption(FeatureConfigurationIndexDto.I18N_PREFIX, FeatureConfigurationIndexDto.END_DATE));
	}

	public void reload() {
		setItems(configurations);
	}

	public Set<FeatureConfigurationIndexDto> getChangedConfigurations() {
		return changedConfigurations;
	}
}
