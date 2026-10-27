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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateHistoryCoverageDto;
import de.symeda.sormas.api.climate.ClimateHistorySource;
import de.symeda.sormas.api.climate.ClimateJobCriteria;
import de.symeda.sormas.api.climate.ClimateJobIndexDto;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Backfill tab of the Climate data screen: new backfills, the climate jobs (fetches, backfills, top-ups and district
 * relinks) and the history each district holds.
 */
@SuppressWarnings("java:S110") // suppress sonar too many parents warning
public class ClimateJobsView extends AbstractClimateDataView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/climateBackfill";

	/** How often the job list refreshes while the tab is open, so running jobs show their progress. */
	private static final int REFRESH_MILLIS = 30_000;
	/** The periods offered for the job list, in days; 0 is all. The nightly fetch adds 365 jobs a year. */
	private static final List<Integer> PERIODS = Arrays.asList(7, 30, 90, 0);
	private static final int DEFAULT_PERIOD = 30;

	private final Label backfillUnavailable = new Label();
	private Button newBackfillButton;
	private final ClimateJobCriteria criteria = new ClimateJobCriteria();
	private final ClimateJobsGrid jobsGrid;
	private final Grid<ClimateHistoryCoverageDto> coverageGrid = new Grid<>();
	private final ComboBox<RegionReferenceDto> coverageRegion;
	private Registration pollRegistration;
	private boolean ownsPolling;
	private long lastRefresh;

	public ClimateJobsView() {

		super(VIEW_NAME);
		// two lists one below the other: the page scrolls
		setHeightUndefined();
		contentLayout.setHeightUndefined();

		if (UiUtil.permitted(UserRight.CLIMATE_MANAGE_SOURCES)) {
			newBackfillButton = ButtonHelper.createIconButton(
				Captions.climateNewBackfill,
				VaadinIcons.CLOUD_DOWNLOAD,
				e -> ClimateBackfillDialog.open(getSummary().getHistoryYears(), this::refresh),
				ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(newBackfillButton);
		}

		backfillUnavailable.setWidthFull();
		backfillUnavailable.addStyleName(CssStyles.LABEL_WARNING);
		backfillUnavailable.setVisible(false);

		criteria.createdFrom(daysAgo(DEFAULT_PERIOD));
		jobsGrid = new ClimateJobsGrid(criteria, this::refreshSummary);
		jobsGrid.setHeight(420, Unit.PIXELS);

		coverageRegion = new ComboBox<>(I18nProperties.getCaption(Captions.Region), FacadeProvider.getRegionFacade().getAllActiveAsReference());
		coverageRegion.addValueChangeListener(e -> loadCoverage());
		configureCoverageGrid();

		contentLayout.addComponents(
			backfillUnavailable,
			heading(Captions.climateJobs),
			createJobFilters(),
			jobsGrid,
			heading(Captions.climateHistoryCoverage),
			coverageRegion,
			coverageGrid);

		addAttachListener(e -> startPolling());
		addDetachListener(e -> stopPolling());
	}

	private static Label heading(String caption) {
		Label heading = new Label(I18nProperties.getCaption(caption));
		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_TOP_3);
		return heading;
	}

	private HorizontalLayout createJobFilters() {

		ComboBox<ClimateJobType> type =
			new ComboBox<>(I18nProperties.getPrefixCaption(ClimateJobIndexDto.I18N_PREFIX, ClimateJobIndexDto.JOB_TYPE), Arrays.asList(ClimateJobType.values()));
		type.addValueChangeListener(e -> {
			criteria.jobType(e.getValue());
			jobsGrid.reload();
		});
		ComboBox<ClimateJobStatus> status = new ComboBox<>(
			I18nProperties.getPrefixCaption(ClimateJobIndexDto.I18N_PREFIX, ClimateJobIndexDto.STATUS),
			Arrays.asList(ClimateJobStatus.values()));
		status.addValueChangeListener(e -> {
			criteria.status(e.getValue());
			jobsGrid.reload();
		});
		ComboBox<Integer> period = new ComboBox<>(I18nProperties.getCaption(Captions.climateJobPeriod), PERIODS);
		period.setItemCaptionGenerator(
			days -> days == 0
				? I18nProperties.getString(Strings.climateAllTime)
				: String.format(I18nProperties.getString(Strings.climateLastDays), days));
		period.setEmptySelectionAllowed(false);
		period.setValue(DEFAULT_PERIOD);
		period.addValueChangeListener(e -> {
			criteria.createdFrom(e.getValue() == 0 ? null : daysAgo(e.getValue()));
			jobsGrid.reload();
		});
		CheckBox onlySkipped = new CheckBox(I18nProperties.getCaption(Captions.climateOnlyRelinksWithSkippedDays));
		onlySkipped.addValueChangeListener(e -> {
			criteria.onlyRelinksWithSkippedDays(e.getValue() ? Boolean.TRUE : null);
			jobsGrid.reload();
		});
		Button refresh = ButtonHelper.createIconButton(Captions.cronJobRefresh, VaadinIcons.REFRESH, e -> refresh());

		HorizontalLayout filters = new HorizontalLayout(type, status, period, onlySkipped, refresh);
		filters.setMargin(false);
		filters.addStyleName(CssStyles.VSPACE_3);
		filters.setComponentAlignment(onlySkipped, Alignment.BOTTOM_LEFT);
		filters.setComponentAlignment(refresh, Alignment.BOTTOM_LEFT);
		return filters;
	}

	private void configureCoverageGrid() {

		coverageGrid.setWidthFull();
		coverageGrid.addColumn(c -> c.getRegion().getCaption()).setCaption(coverageCaption(ClimateHistoryCoverageDto.REGION));
		coverageGrid.addColumn(c -> c.getDistrict().getCaption()).setCaption(coverageCaption(ClimateHistoryCoverageDto.DISTRICT));
		coverageGrid.addColumn(c -> I18nProperties.getString(c.isHasPoint() ? Strings.climateHasPoint : Strings.climateNoPoint))
			.setCaption(coverageCaption(ClimateHistoryCoverageDto.HAS_POINT));
		coverageGrid.addColumn(c -> c.getHistoryFrom() != null ? DateFormatHelper.formatDate(toDate(c.getHistoryFrom())) : "")
			.setCaption(coverageCaption(ClimateHistoryCoverageDto.HISTORY_FROM));
		coverageGrid.addColumn(c -> ClimateSummaryStrip.formatCount(c.getDaysHeld())).setCaption(coverageCaption(ClimateHistoryCoverageDto.DAYS_HELD));
		coverageGrid.addColumn(c -> {
			Double completeness = c.getCompleteness(LocalDate.now());
			return completeness != null ? Math.round(completeness * 100) + " %" : "";
		}).setCaption(coverageCaption(ClimateHistoryCoverageDto.COMPLETENESS));
		coverageGrid.addColumn(this::historyStatus).setCaption(coverageCaption(ClimateHistoryCoverageDto.STATUS));
		coverageGrid.addColumn(ClimateCaptions::sources)
			.setCaption(coverageCaption(ClimateHistoryCoverageDto.SOURCES))
			.setDescriptionGenerator(ClimateCaptions::sources);
	}

	/**
	 * @return "Building climate history: 1 year 2 months of 3 years", or "Enough history: 5 years 2 months"
	 */
	private String historyStatus(ClimateHistoryCoverageDto coverage) {

		if (coverage.getHistoryFrom() == null) {
			return I18nProperties.getString(Strings.climateHistoryNone);
		}
		LocalDate today = LocalDate.now();
		String held = ClimateCaptions.period(coverage.getHeldPeriod(today));
		int minYears = getSummary().getMinHistoryYears();
		return coverage.hasEnoughHistory(today, minYears)
			? String.format(I18nProperties.getString(Strings.climateHistoryEnough), held)
			: String.format(I18nProperties.getString(Strings.climateHistoryBuilding), held, minYears);
	}

	private static String coverageCaption(String property) {
		return I18nProperties.getPrefixCaption(ClimateHistoryCoverageDto.I18N_PREFIX, property);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		updateBackfillAvailability();
		jobsGrid.reload();
		loadCoverage();
	}

	private void loadCoverage() {
		List<ClimateHistoryCoverageDto> coverage = FacadeProvider.getClimateObservationFacade().getHistoryCoverage(coverageRegion.getValue());
		coverageGrid.setItems(coverage);
		coverageGrid.setHeightByRows(Math.max(1, Math.min(coverage.size(), 15)));
	}

	/**
	 * Backfill needs OpenWeather as the history source and an API key; otherwise the tab says why and shows only the job
	 * list and history coverage.
	 */
	private void updateBackfillAvailability() {

		String reason = null;
		if (getSummary().getHistorySource() != ClimateHistorySource.OPENWEATHER) {
			reason = I18nProperties.getValidationError(Validations.climateBackfillOff);
		} else if (!getSummary().isApiKeySet()) {
			reason = I18nProperties.getString(Strings.climateBackfillNoApiKey);
		}
		backfillUnavailable.setValue(reason);
		backfillUnavailable.setVisible(reason != null);
		if (newBackfillButton != null) {
			newBackfillButton.setVisible(reason == null);
		}
	}

	private void refresh() {
		lastRefresh = System.currentTimeMillis();
		refreshSummary();
		jobsGrid.reload();
	}

	/**
	 * Refreshes the job list every 30 seconds while the tab is open. Polling another component started (an import) is left
	 * as it is.
	 */
	private void startPolling() {
		UI ui = getUI();
		if (ui.getPollInterval() < 0) {
			ui.setPollInterval(REFRESH_MILLIS);
			ownsPolling = true;
		}
		pollRegistration = ui.addPollListener(e -> {
			// another component may poll much more often
			if (System.currentTimeMillis() - lastRefresh >= REFRESH_MILLIS - 1000) {
				refresh();
			}
		});
	}

	private void stopPolling() {
		if (pollRegistration != null) {
			pollRegistration.remove();
			pollRegistration = null;
		}
		UI ui = getUI();
		if (ownsPolling && ui != null && ui.getPollInterval() == REFRESH_MILLIS) {
			ui.setPollInterval(-1);
		}
		ownsPolling = false;
	}

	private static Date daysAgo(int days) {
		return toDate(LocalDate.now().minusDays(days));
	}

	private static Date toDate(LocalDate day) {
		return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}
