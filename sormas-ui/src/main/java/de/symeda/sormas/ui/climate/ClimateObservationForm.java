/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.climate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateEarlierValueDto;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.climate.ClimateObservationValidation;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Manual entry of one day's climate values for a district or an environment, or correction of an existing row. A manual
 * value always wins; the value it replaces stays in the row's change history, which the form lists.
 */
public class ClimateObservationForm extends VerticalLayout {

	private static final long serialVersionUID = -4316722560311390112L;

	private static final String DISTRICT = "district";
	private static final String ENVIRONMENT = "environment";

	private final Runnable onChanged;
	private Window window;

	private RadioButtonGroup<String> placeKind;
	private ComboBox<RegionReferenceDto> region;
	private ComboBox<DistrictReferenceDto> district;
	private ComboBox<EnvironmentReferenceDto> environment;
	private DateField date;

	private final Label placeInfo = new Label("", ContentMode.TEXT);
	private final TextField temperatureMin = numberField(ClimateObservationEntryDto.TEMPERATURE_MIN);
	private final TextField temperatureMean = numberField(ClimateObservationEntryDto.TEMPERATURE_MEAN);
	private final TextField temperatureMax = numberField(ClimateObservationEntryDto.TEMPERATURE_MAX);
	private final TextField rainfall = numberField(ClimateObservationEntryDto.RAINFALL);
	private final TextField relativeHumidity = numberField(ClimateObservationEntryDto.RELATIVE_HUMIDITY);
	private final ComboBox<HumidityMeasure> humidityMeasure = new ComboBox<>(
		I18nProperties.getPrefixCaption(ClimateObservationEntryDto.I18N_PREFIX, ClimateObservationEntryDto.HUMIDITY_MEASURE),
		Arrays.asList(HumidityMeasure.values()));
	private final TextField sourceDetail = new TextField(
		I18nProperties.getPrefixCaption(ClimateObservationEntryDto.I18N_PREFIX, ClimateObservationEntryDto.SOURCE_DETAIL));
	private final Grid<ClimateEarlierValueDto> earlierValues = new Grid<>();
	private final Button deleteButton;

	/** The location and day being edited, with its current values; null until a new entry has a place and a date. */
	private ClimateObservationIndexDto current;

	/**
	 * Opens the form for an existing row.
	 */
	public static void openEdit(ClimateObservationIndexDto observation, Runnable onChanged) {
		ClimateObservationForm form = new ClimateObservationForm(onChanged, false);
		form.show(observation);
		form.window = VaadinUiUtil.showPopupWindow(form, I18nProperties.getString(Strings.headingClimateObservation));
	}

	/**
	 * Opens an empty form, where the user picks a district or an environment and a day.
	 */
	public static void openNew(Runnable onChanged) {
		ClimateObservationForm form = new ClimateObservationForm(onChanged, true);
		form.window = VaadinUiUtil.showPopupWindow(form, I18nProperties.getString(Strings.headingClimateNewObservation));
	}

	private ClimateObservationForm(Runnable onChanged, boolean newEntry) {

		this.onChanged = onChanged;
		setMargin(true);
		setSpacing(true);
		setWidth(720, Unit.PIXELS);

		if (newEntry) {
			addComponent(createPlaceFields());
		}

		placeInfo.setWidthFull();
		placeInfo.addStyleName(CssStyles.LABEL_SECONDARY);
		addComponent(placeInfo);

		FormLayout values = new FormLayout();
		values.setMargin(false);
		humidityMeasure.setEmptySelectionAllowed(true);
		sourceDetail.setWidth(320, Unit.PIXELS);
		values.addComponents(temperatureMin, temperatureMean, temperatureMax, rainfall, relativeHumidity, humidityMeasure, sourceDetail);
		addComponent(values);

		Label meanHint = new Label(I18nProperties.getString(Strings.infoClimateObservationMean));
		meanHint.setWidthFull();
		meanHint.addStyleName(CssStyles.LABEL_SECONDARY);
		addComponent(meanHint);

		configureEarlierValues();
		addComponent(earlierValues);

		Button cancel = ButtonHelper.createButton(Captions.actionCancel, e -> window.close());
		deleteButton = ButtonHelper.createButton(Captions.actionDelete, e -> confirmDelete(), ValoTheme.BUTTON_DANGER);
		deleteButton.setVisible(false);
		Button save = ButtonHelper.createButton(Captions.actionSave, e -> save(), ValoTheme.BUTTON_PRIMARY);
		HorizontalLayout buttons = new HorizontalLayout(deleteButton, cancel, save);
		addComponent(buttons);
		setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);

		setValuesEnabled(!newEntry);
	}

	private VerticalLayout createPlaceFields() {

		placeKind = new RadioButtonGroup<>(I18nProperties.getCaption(Captions.climatePlaceKind), Arrays.asList(DISTRICT, ENVIRONMENT));
		placeKind.setItemCaptionGenerator(
			kind -> I18nProperties.getCaption(DISTRICT.equals(kind) ? Captions.climatePlaceKindDistrict : Captions.climatePlaceKindEnvironment));
		placeKind.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		placeKind.setValue(DISTRICT);

		region = new ComboBox<>(
			I18nProperties.getPrefixCaption(ClimateObservationEntryDto.I18N_PREFIX, ClimateObservationEntryDto.REGION),
			FacadeProvider.getRegionFacade().getAllActiveAsReference());
		district = new ComboBox<>(I18nProperties.getPrefixCaption(ClimateObservationEntryDto.I18N_PREFIX, ClimateObservationEntryDto.DISTRICT));
		environment = new ComboBox<>(I18nProperties.getCaption(Captions.climatePlaceKindEnvironment));
		environment.setVisible(false);
		date = new DateField(I18nProperties.getPrefixCaption(ClimateObservationEntryDto.I18N_PREFIX, ClimateObservationEntryDto.DATE));
		date.setDateFormat(DateFormatHelper.getDateFormatPattern());
		date.setRangeEnd(LocalDate.now().minusDays(1));

		region.addValueChangeListener(e -> {
			district.setItems(
				e.getValue() != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid()) : Collections.emptyList());
			updateEnvironments();
		});
		district.addValueChangeListener(e -> {
			updateEnvironments();
			loadCurrent();
		});
		placeKind.addValueChangeListener(e -> {
			environment.setVisible(ENVIRONMENT.equals(e.getValue()));
			loadCurrent();
		});
		environment.addValueChangeListener(e -> loadCurrent());
		date.addValueChangeListener(e -> loadCurrent());

		HorizontalLayout fields = new HorizontalLayout(region, district, environment, date);
		fields.setMargin(false);
		VerticalLayout place = new VerticalLayout(placeKind, fields);
		place.setMargin(false);
		return place;
	}

	private void updateEnvironments() {
		environment.setItems(
			region.getValue() != null || district.getValue() != null
				? FacadeProvider.getClimateObservationFacade().getEnvironmentsWithClimateLocation(region.getValue(), district.getValue())
				: Collections.emptyList());
	}

	/**
	 * Loads the chosen place and day: who shares its location, and its current values when it has a row.
	 */
	private void loadCurrent() {

		ClimateObservationEntryDto key = new ClimateObservationEntryDto();
		if (ENVIRONMENT.equals(placeKind.getValue())) {
			key.setEnvironmentUuid(environment.getValue() != null ? environment.getValue().getUuid() : null);
		} else {
			key.setDistrictUuid(district.getValue() != null ? district.getValue().getUuid() : null);
		}
		if (date.getValue() == null || (key.getEnvironmentUuid() == null && key.getDistrictUuid() == null)) {
			current = null;
			placeInfo.setValue("");
			setValuesEnabled(false);
			return;
		}
		key.setDate(date.getValue().toString());
		try {
			show(FacadeProvider.getClimateObservationFacade().getObservation(key));
		} catch (ValidationRuntimeException e) {
			current = null;
			placeInfo.setValue(e.getMessage());
			setValuesEnabled(false);
		}
	}

	private void show(ClimateObservationIndexDto observation) {

		current = observation;
		setValuesEnabled(true);

		List<String> info = new ArrayList<>();
		info.add(ClimateCaptions.place(observation) + " · " + DateFormatHelper.formatDate(observation.getObservationDate()));
		if (observation.isShared()) {
			info.add(String.format(I18nProperties.getString(Strings.climateSharedBy), String.join(", ", ClimateCaptions.placeNames(observation))));
		}
		if (observation.getSource() != null && observation.getSource() != ClimateDataSource.MANUAL) {
			info.add(String.format(I18nProperties.getString(Strings.infoClimateObservationReplaces), observation.getSource()));
		}
		placeInfo.setValue(String.join("\n", info));
		placeInfo.setContentMode(ContentMode.PREFORMATTED);

		temperatureMin.setValue(ClimateCaptions.number(observation.getTemperatureMin()));
		temperatureMean.setValue(ClimateCaptions.number(observation.getTemperatureMean()));
		temperatureMax.setValue(ClimateCaptions.number(observation.getTemperatureMax()));
		rainfall.setValue(ClimateCaptions.number(observation.getRainfall()));
		relativeHumidity.setValue(ClimateCaptions.number(observation.getRelativeHumidity()));
		humidityMeasure.setValue(observation.getHumidityMeasure());
		sourceDetail.setValue(StringUtils.defaultString(observation.getSourceDetail()));

		List<ClimateEarlierValueDto> earlier =
			observation.getUuid() != null ? FacadeProvider.getClimateObservationFacade().getEarlierValues(observation.getUuid()) : Collections.emptyList();
		earlierValues.setItems(earlier);
		earlierValues.setVisible(!earlier.isEmpty());
		earlierValues.setHeightByRows(Math.max(1, Math.min(earlier.size(), 5)));

		deleteButton.setVisible(
			observation.getUuid() != null && (observation.getSource() == ClimateDataSource.MANUAL || observation.getSource() == ClimateDataSource.IMPORT));
	}

	private void configureEarlierValues() {

		earlierValues.setCaption(I18nProperties.getCaption(Captions.climateEarlierValues));
		earlierValues.setWidthFull();
		earlierValues.setVisible(false);
		earlierValues.addColumn(v -> DateFormatHelper.formatLocalDateTime(v.getChangeDate()))
			.setCaption(earlierCaption(ClimateEarlierValueDto.CHANGE_DATE));
		earlierValues.addColumn(ClimateEarlierValueDto::getSource).setCaption(earlierCaption(ClimateEarlierValueDto.SOURCE));
		earlierValues.addColumn(v -> ClimateCaptions.number(v.getTemperatureMin())).setCaption(earlierCaption(ClimateEarlierValueDto.TEMPERATURE_MIN));
		earlierValues.addColumn(v -> ClimateCaptions.number(v.getTemperatureMean()))
			.setCaption(earlierCaption(ClimateEarlierValueDto.TEMPERATURE_MEAN));
		earlierValues.addColumn(v -> ClimateCaptions.number(v.getTemperatureMax())).setCaption(earlierCaption(ClimateEarlierValueDto.TEMPERATURE_MAX));
		earlierValues.addColumn(v -> ClimateCaptions.number(v.getRainfall())).setCaption(earlierCaption(ClimateEarlierValueDto.RAINFALL));
		earlierValues.addColumn(v -> ClimateCaptions.number(v.getRelativeHumidity()))
			.setCaption(earlierCaption(ClimateEarlierValueDto.RELATIVE_HUMIDITY));
		earlierValues.addColumn(ClimateEarlierValueDto::getSourceDetail).setCaption(earlierCaption(ClimateEarlierValueDto.SOURCE_DETAIL));
	}

	private static String earlierCaption(String property) {
		return I18nProperties.findPrefixCaption(property, ClimateEarlierValueDto.I18N_PREFIX, ClimateObservationIndexDto.I18N_PREFIX);
	}

	private void setValuesEnabled(boolean enabled) {
		for (Component field : Arrays
			.asList(temperatureMin, temperatureMean, temperatureMax, rainfall, relativeHumidity, humidityMeasure, sourceDetail)) {
			field.setEnabled(enabled);
		}
		if (!enabled) {
			earlierValues.setVisible(false);
			deleteButton.setVisible(false);
		}
	}

	private void save() {

		if (current == null) {
			Notification.show(I18nProperties.getString(Strings.messageClimateChoosePlaceAndDate), Notification.Type.WARNING_MESSAGE);
			return;
		}
		ClimateObservationEntryDto entry = new ClimateObservationEntryDto();
		entry.setClimateLocationUuid(current.getClimateLocationUuid());
		entry.setDate(ClimateObservationValidation.formatDate(current.getObservationDate()));
		try {
			entry.setTemperatureMin(readNumber(temperatureMin));
			entry.setTemperatureMean(readNumber(temperatureMean));
			entry.setTemperatureMax(readNumber(temperatureMax));
			entry.setRainfall(readNumber(rainfall));
			entry.setRelativeHumidity(readNumber(relativeHumidity));
		} catch (NumberFormatException e) {
			return;
		}
		entry.setHumidityMeasure(humidityMeasure.getValue());
		entry.setSourceDetail(StringUtils.trimToNull(sourceDetail.getValue()));

		try {
			FacadeProvider.getClimateObservationFacade().saveManualEntry(entry);
		} catch (ValidationRuntimeException e) {
			Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return;
		}
		Notification.show(I18nProperties.getString(Strings.messageClimateObservationSaved), Notification.Type.TRAY_NOTIFICATION);
		window.close();
		onChanged.run();
	}

	private void confirmDelete() {

		String uuid = current.getUuid();
		VaadinUiUtil.showDeleteConfirmationWindow(
			String.format(
				I18nProperties.getString(Strings.confirmationClimateDeleteObservation),
				current.getSource(),
				ClimateCaptions.place(current),
				DateFormatHelper.formatDate(current.getObservationDate())),
			() -> {
				try {
					FacadeProvider.getClimateObservationFacade().deleteObservation(uuid);
				} catch (ValidationRuntimeException e) {
					Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
					return;
				}
				Notification.show(I18nProperties.getString(Strings.messageClimateObservationDeleted), Notification.Type.TRAY_NOTIFICATION);
				window.close();
				onChanged.run();
			});
	}

	/**
	 * @return the field's number (a comma is read as the decimal mark), or null when it is empty
	 * @throws NumberFormatException
	 *             when the field holds something else; the field then shows the error
	 */
	private static Double readNumber(TextField field) {

		field.setComponentError(null);
		String text = StringUtils.trimToNull(field.getValue());
		if (text == null) {
			return null;
		}
		try {
			return Double.valueOf(text.replace(',', '.'));
		} catch (NumberFormatException e) {
			field.setComponentError(new UserError(I18nProperties.getString(Strings.messageClimateEnterNumber)));
			throw e;
		}
	}

	private static TextField numberField(String property) {
		TextField field = new TextField(I18nProperties.getPrefixCaption(ClimateObservationEntryDto.I18N_PREFIX, property));
		field.setWidth(120, Unit.PIXELS);
		return field;
	}
}
