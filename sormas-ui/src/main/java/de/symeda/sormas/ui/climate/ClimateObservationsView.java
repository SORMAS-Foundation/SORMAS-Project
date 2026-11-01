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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateDailySource;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDistrictDto;
import de.symeda.sormas.api.climate.ClimateObservationCriteria;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.climate.ClimatePlaceType;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.IgnoreCancelDownloader;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Observations tab of the Climate data screen: the daily values of districts and environments, with manual entry, import
 * and export.
 */
@SuppressWarnings("java:S110") // suppress sonar too many parents warning
public class ClimateObservationsView extends AbstractClimateDataView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/climate";

	/** How far back the list goes by default: the table can hold millions of rows. */
	private static final int DEFAULT_DAYS = 30;

	private final ClimateObservationCriteria criteria;
	private final ClimateObservationsGrid grid;
	private final Button districtsWithoutPoint;
	private Button fetchNowButton;

	private ComboBox<RegionReferenceDto> regionFilter;
	private ComboBox<DistrictReferenceDto> districtFilter;
	private ComboBox<ClimatePlaceType> placeTypeFilter;
	private ComboBox<EnvironmentReferenceDto> environmentFilter;

	public ClimateObservationsView() {

		super(VIEW_NAME);

		criteria = ViewModelProviders.of(ClimateObservationsView.class).get(ClimateObservationCriteria.class, defaultCriteria());
		grid = new ClimateObservationsGrid(
			criteria,
			UiUtil.permitted(UserRight.CLIMATE_EDIT) ? row -> ClimateObservationForm.openEdit(row, this::reload) : null);

		HorizontalLayout actions = new HorizontalLayout();
		actions.setMargin(false);
		actions.addStyleName(CssStyles.VSPACE_3);
		if (UiUtil.permitted(UserRight.CLIMATE_EDIT)) {
			actions.addComponent(
				ButtonHelper.createIconButton(
					Captions.climateNewObservation,
					VaadinIcons.PLUS_CIRCLE,
					e -> ClimateObservationForm.openNew(this::reload),
					ValoTheme.BUTTON_PRIMARY));
		}
		if (UiUtil.permitted(UserRight.CLIMATE_IMPORT)) {
			actions.addComponent(ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new ClimateImportLayout());
				window.setCaption(I18nProperties.getString(Strings.headingClimateImport));
				window.addCloseListener(c -> reload());
			}, ValoTheme.BUTTON_PRIMARY));
		}
		if (UiUtil.permitted(UserRight.CLIMATE_EXPORT)) {
			Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			StreamResource export = DownloadUtil.createCsvExportStreamResource(
				ClimateObservationExportDto.class,
				null,
				(Integer start, Integer max) -> FacadeProvider.getClimateObservationFacade().getExportList(criteria, start, max),
				(propertyId, type) -> {
					String caption =
						I18nProperties.findPrefixCaption(propertyId, ClimateObservationExportDto.I18N_PREFIX, ClimateObservationIndexDto.I18N_PREFIX);
					return Date.class.isAssignableFrom(type) ? caption + " (" + DateFormatHelper.getDateFormatPattern() + ")" : caption;
				},
				ExportEntityName.CLIMATE_OBSERVATIONS,
				null);
			new IgnoreCancelDownloader(export).extend(exportButton);
			actions.addComponent(exportButton);
		}
		if (UiUtil.permitted(UserRight.CLIMATE_MANAGE_SOURCES)) {
			fetchNowButton = ButtonHelper.createIconButton(
				Captions.climateFetchNow,
				VaadinIcons.CLOUD_DOWNLOAD,
				e -> ClimateFetchNowDialog.open(this::refreshSummary),
				ValoTheme.BUTTON_PRIMARY);
			actions.addComponent(fetchNowButton);
		}
		addHeaderComponent(actions);

		districtsWithoutPoint = ButtonHelper.createButton("", e -> showDistrictsWithoutPoint(), ValoTheme.BUTTON_LINK);
		districtsWithoutPoint.setVisible(false);

		RowCount rowCount = new RowCount(Strings.labelNumberOfClimateObservations, grid.getDataSize());
		grid.addDataSizeChangeListener(e -> rowCount.update(grid.getDataSize()));

		sideLayout.addComponent(districtsWithoutPoint);
		contentLayout.addComponents(createFilterBar(), rowCount, grid);
		contentLayout.setExpandRatio(grid, 1);
	}

	private static ClimateObservationCriteria defaultCriteria() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		return new ClimateObservationCriteria().dateBetween(toDate(yesterday.minusDays(DEFAULT_DAYS - 1)), toDate(yesterday));
	}

	private HorizontalLayout createFilterBar() {

		HorizontalLayout filters = new HorizontalLayout();
		filters.setMargin(false);
		filters.setSpacing(true);
		filters.addStyleName(CssStyles.VSPACE_3);

		regionFilter = new ComboBox<>(
			I18nProperties.getCaption(Captions.Region),
			FacadeProvider.getRegionFacade().getAllActiveAsReference());
		regionFilter.setValue(criteria.getRegion());
		districtFilter = new ComboBox<>(I18nProperties.getCaption(Captions.District));
		updateDistricts();
		districtFilter.setValue(criteria.getDistrict());
		placeTypeFilter = new ComboBox<>(
			I18nProperties.getPrefixCaption(ClimateObservationIndexDto.I18N_PREFIX, ClimateObservationCriteria.PLACE_TYPE),
			Arrays.asList(ClimatePlaceType.values()));
		placeTypeFilter.setEmptySelectionAllowed(false);
		placeTypeFilter.setValue(criteria.getPlaceType());
		environmentFilter = new ComboBox<>(I18nProperties.getCaption(Captions.climatePlaceKindEnvironment));
		updateEnvironments();
		environmentFilter.setValue(criteria.getEnvironment());

		DateField dateFrom = new DateField(I18nProperties.getCaption(Captions.climateFilterDateFrom), toLocalDate(criteria.getDateFrom()));
		DateField dateTo = new DateField(I18nProperties.getCaption(Captions.climateFilterDateTo), toLocalDate(criteria.getDateTo()));
		for (DateField field : Arrays.asList(dateFrom, dateTo)) {
			field.setDateFormat(DateFormatHelper.getDateFormatPattern());
			field.setWidth(130, Unit.PIXELS);
		}
		ComboBox<ClimateDataSource> sourceFilter = new ComboBox<>(
			I18nProperties.getPrefixCaption(ClimateObservationIndexDto.I18N_PREFIX, ClimateObservationIndexDto.SOURCE),
			Arrays.asList(ClimateDataSource.values()));
		sourceFilter.setValue(criteria.getSource());

		regionFilter.addValueChangeListener(e -> {
			criteria.region(e.getValue()).district(null).environment(null);
			districtFilter.setValue(null);
			updateDistricts();
			updateEnvironments();
			reload();
		});
		districtFilter.addValueChangeListener(e -> {
			criteria.district(e.getValue()).environment(null);
			updateEnvironments();
			reload();
		});
		placeTypeFilter.addValueChangeListener(e -> {
			criteria.placeType(e.getValue());
			reload();
		});
		environmentFilter.addValueChangeListener(e -> {
			criteria.environment(e.getValue());
			reload();
		});
		dateFrom.addValueChangeListener(e -> {
			criteria.dateBetween(toDate(e.getValue()), criteria.getDateTo());
			reload();
		});
		dateTo.addValueChangeListener(e -> {
			criteria.dateBetween(criteria.getDateFrom(), toDate(e.getValue()));
			reload();
		});
		sourceFilter.addValueChangeListener(e -> {
			criteria.source(e.getValue());
			reload();
		});

		Button reset = ButtonHelper.createButton(Captions.actionResetFilters, e -> {
			ViewModelProviders.of(ClimateObservationsView.class).remove(ClimateObservationCriteria.class);
			navigateTo(null);
		}, CssStyles.FORCE_CAPTION);

		filters.addComponents(regionFilter, districtFilter, placeTypeFilter, environmentFilter, dateFrom, dateTo, sourceFilter, reset);
		filters.setComponentAlignment(reset, Alignment.BOTTOM_LEFT);
		return filters;
	}

	private void updateDistricts() {
		districtFilter.setItems(
			regionFilter.getValue() != null
				? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionFilter.getValue().getUuid())
				: Collections.emptyList());
	}

	private void updateEnvironments() {
		// listing every environment of the deployment would make a long list; a region or district narrows it
		environmentFilter.setItems(
			regionFilter.getValue() != null || districtFilter.getValue() != null
				? FacadeProvider.getClimateObservationFacade().getEnvironmentsWithClimateLocation(regionFilter.getValue(), districtFilter.getValue())
				: Collections.emptyList());
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		updateDistrictsWithoutPoint();
		updateFetchNowButton();
		grid.reload();
	}

	private void reload() {
		grid.reload();
	}

	/**
	 * Shows how many districts get no API data because they have no climate point; only meaningful when OpenWeather is
	 * the daily source.
	 */
	private void updateDistrictsWithoutPoint() {

		int count = getSummary().getDistrictsWithoutPoint();
		boolean relevant = count > 0 && getSummary().getDailySource() == ClimateDailySource.OPENWEATHER;
		districtsWithoutPoint.setVisible(relevant);
		if (relevant) {
			districtsWithoutPoint.setCaption(
				count == 1
					? I18nProperties.getString(Strings.climateDistrictWithoutPoint)
					: String.format(I18nProperties.getString(Strings.climateDistrictsWithoutPoint), ClimateSummaryStrip.formatCount(count)));
		}
	}

	/**
	 * Fetch now calls OpenWeather for daily data: hidden when daily data comes from elsewhere, disabled without a key.
	 */
	private void updateFetchNowButton() {

		if (fetchNowButton == null) {
			return;
		}
		fetchNowButton.setVisible(getSummary().getDailySource() == ClimateDailySource.OPENWEATHER);
		fetchNowButton.setEnabled(getSummary().isApiKeySet());
		fetchNowButton.setDescription(getSummary().isApiKeySet() ? null : I18nProperties.getString(Strings.climateFetchNowNoApiKey));
	}

	private static void showDistrictsWithoutPoint() {

		List<ClimateDistrictDto> districts = FacadeProvider.getClimateObservationFacade().getDistrictsWithoutPoint();
		Grid<ClimateDistrictDto> list = new Grid<>();
		list.setItems(districts);
		list.setWidthFull();
		list.setHeightByRows(Math.max(1, Math.min(districts.size(), 12)));
		list.addColumn(d -> d.getRegion().getCaption()).setCaption(I18nProperties.getPrefixCaption(ClimateDistrictDto.I18N_PREFIX, ClimateDistrictDto.REGION));
		list.addColumn(d -> d.getDistrict().getCaption())
			.setCaption(I18nProperties.getPrefixCaption(ClimateDistrictDto.I18N_PREFIX, ClimateDistrictDto.DISTRICT));
		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_EDIT)) {
			list.addItemClickListener(e -> ControllerProvider.getInfrastructureController().editDistrict(e.getItem().getDistrict().getUuid()));
		}

		Label info = new Label(I18nProperties.getString(Strings.infoClimateDistrictsWithoutPoint));
		info.setWidthFull();
		VerticalLayout layout = new VerticalLayout(info, list);
		layout.setWidth(640, Unit.PIXELS);
		VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingClimateDistrictsWithoutPoint));
	}

	private static Date toDate(LocalDate day) {
		return day != null ? Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
	}

	private static LocalDate toLocalDate(Date date) {
		return date != null ? Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate() : null;
	}
}
