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

import static de.symeda.sormas.ui.climate.ClimateSummaryStrip.formatCount;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateJobCriteria;
import de.symeda.sormas.api.climate.ClimateJobIndexDto;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * The climate jobs: nightly fetches, fetch now, backfills, automatic top-ups and district relinks, newest first, with
 * cancel and start again for users who manage the sources, and the days a relink did not copy.
 */
public class ClimateJobsGrid extends FilteredGrid<ClimateJobIndexDto, ClimateJobCriteria> {

	private static final long serialVersionUID = -1806398150245519186L;

	private static final String ACTIONS = "actions";
	private static final String NOTE = "lastError";
	private static final int MAX_VISIBLE_ROWS = 12;

	private final Runnable onChanged;

	public ClimateJobsGrid(ClimateJobCriteria criteria, Runnable onChanged) {

		super(ClimateJobIndexDto.class);
		this.onChanged = onChanged;
		setWidthFull();
		setLazyDataProvider(FacadeProvider.getClimateJobFacade()::getIndexList, FacadeProvider.getClimateJobFacade()::count);
		setCriteria(criteria);
		fitHeight();

		removeColumn(ClimateJobIndexDto.CREATION_DATE);
		addColumn(j -> DateFormatHelper.formatLocalDateTime(j.getCreationDate())).setId(ClimateJobIndexDto.CREATION_DATE);
		addColumn(ClimateJobsGrid::scope).setId(ClimateJobIndexDto.SCOPE).setDescriptionGenerator(ClimateJobsGrid::scope);
		addColumn(ClimateJobsGrid::period).setId(ClimateJobIndexDto.PERIOD);
		addColumn(ClimateJobsGrid::locations).setId(ClimateJobIndexDto.LOCATIONS);
		addColumn(ClimateJobsGrid::calls).setId(ClimateJobIndexDto.CALLS);
		addColumn(ClimateJobsGrid::rows).setId(ClimateJobIndexDto.ROWS).setDescriptionGenerator(ClimateJobsGrid::rows);
		removeColumn(ClimateJobIndexDto.FINISHED_AT);
		addColumn(j -> DateFormatHelper.formatLocalDateTime(j.getFinishedAt())).setId(ClimateJobIndexDto.FINISHED_AT);
		removeColumn(NOTE);
		addColumn(ClimateJobIndexDto::getLastError).setId(NOTE).setDescriptionGenerator(ClimateJobIndexDto::getLastError);
		addComponentColumn(this::actions).setId(ACTIONS);

		setColumns(
			ClimateJobIndexDto.CREATION_DATE,
			ClimateJobIndexDto.JOB_TYPE,
			ClimateJobIndexDto.SCOPE,
			ClimateJobIndexDto.STATUS,
			ClimateJobIndexDto.PERIOD,
			ClimateJobIndexDto.LOCATIONS,
			ClimateJobIndexDto.CALLS,
			ClimateJobIndexDto.ROWS,
			ClimateJobIndexDto.STARTED_BY,
			ClimateJobIndexDto.FINISHED_AT,
			NOTE,
			ACTIONS);

		for (Column<ClimateJobIndexDto, ?> column : getColumns()) {
			column.setSortable(false);
			column.setCaption(I18nProperties.getPrefixCaption(ClimateJobIndexDto.I18N_PREFIX, column.getId(), ""));
		}
	}

	/**
	 * @return what the job covers: "All districts", "Lagos and environment points", a top-up's place, or "Ikeja moved to a
	 *         new point" for a relink
	 */
	static String scope(ClimateJobIndexDto job) {

		switch (job.getJobType()) {
		case DAILY_FETCH:
			return I18nProperties.getString(Strings.climateJobScopeAllLocations);
		case RELINK:
			return String.format(I18nProperties.getString(Strings.climateJobScopeRelink), job.getDistrictName());
		case TOP_UP:
			return !job.getPlaceNames().isEmpty()
				? String.join(", ", job.getPlaceNames())
				: job.getLatitude() != null ? "(" + job.getLatitude() + ", " + job.getLongitude() + ")" : "";
		default:
			String place = job.getDistrictName() != null
				? job.getDistrictName()
				: job.getRegionName() != null ? job.getRegionName() : I18nProperties.getString(Strings.climateJobScopeAllDistricts);
			return job.isIncludeEnvironments() ? String.format(I18nProperties.getString(Strings.climateJobScopeWithEnvironments), place) : place;
		}
	}

	private static String period(ClimateJobIndexDto job) {
		if (job.getJobType() == ClimateJobType.RELINK || job.getFromDate() == null) {
			return "";
		}
		return DateFormatHelper.formatDate(job.getFromDate()) + " – " + DateFormatHelper.formatDate(job.getToDate());
	}

	private static String locations(ClimateJobIndexDto job) {
		if (job.getJobType() == ClimateJobType.RELINK || job.getLocationsTotal() == 0) {
			return "";
		}
		return job.getLocationsFailed() > 0
			? String.format(
				I18nProperties.getString(Strings.climateJobLocationsFailed),
				formatCount(job.getLocationsDone()),
				formatCount(job.getLocationsTotal()),
				formatCount(job.getLocationsFailed()))
			: String.format(
				I18nProperties.getString(Strings.climateJobLocations),
				formatCount(job.getLocationsDone()),
				formatCount(job.getLocationsTotal()));
	}

	/**
	 * @return the calls this job made over all the days it ran, and what the user confirmed for a backfill
	 */
	private static String calls(ClimateJobIndexDto job) {
		if (job.getJobType() == ClimateJobType.RELINK) {
			return "";
		}
		return job.getEstimatedCalls() != null
			? String.format(
				I18nProperties.getString(Strings.climateJobCallsOfConfirmed),
				formatCount(job.getCallsMade()),
				formatCount(job.getEstimatedCalls()))
			: formatCount(job.getCallsMade());
	}

	static String rows(ClimateJobIndexDto job) {
		if (job.getJobType() == ClimateJobType.RELINK) {
			return job.getRowsSkipped() > 0
				? String.format(
					I18nProperties.getString(Strings.climateJobRowsRelinkSkipped),
					formatCount(job.getRowsAdded()),
					formatCount(job.getRowsSkipped()),
					DateFormatHelper.formatDate(job.getFromDate()),
					DateFormatHelper.formatDate(job.getToDate()))
				: String.format(I18nProperties.getString(Strings.climateJobRowsRelink), formatCount(job.getRowsAdded()));
		}
		return String.format(I18nProperties.getString(Strings.climateJobRows), formatCount(job.getRowsAdded()), formatCount(job.getRowsUpdated()));
	}

	private HorizontalLayout actions(ClimateJobIndexDto job) {

		HorizontalLayout actions = new HorizontalLayout();
		actions.setMargin(false);
		if (job.getJobType() == ClimateJobType.RELINK && job.getRowsSkipped() > 0) {
			actions.addComponent(
				ButtonHelper
					.createButton(Captions.climateShowDays, e -> ClimateRelinkDaysWindow.open(job), ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_LINK));
		}
		if (UiUtil.permitted(UserRight.CLIMATE_MANAGE_SOURCES)) {
			if (job.isCancellable()) {
				Button cancel = ButtonHelper.createButton(Captions.climateCancelJob, e -> confirmCancel(job), ValoTheme.BUTTON_SMALL);
				actions.addComponent(cancel);
			}
			if (job.isResumable()) {
				actions.addComponent(ButtonHelper.createButton(Captions.climateStartAgain, e -> run(() -> {
					FacadeProvider.getClimateJobFacade().startAgain(job.getUuid());
				}), ValoTheme.BUTTON_SMALL));
			}
		}
		return actions;
	}

	private void confirmCancel(ClimateJobIndexDto job) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingClimateCancelJob),
			new Label(I18nProperties.getString(Strings.confirmationClimateCancelJob)),
			I18nProperties.getCaption(Captions.actionConfirm),
			I18nProperties.getCaption(Captions.actionCancel),
			480,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					run(() -> FacadeProvider.getClimateJobFacade().cancel(job.getUuid()));
				}
			});
	}

	private void run(Runnable action) {
		try {
			action.run();
		} catch (ValidationRuntimeException e) {
			Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
		reload();
		onChanged.run();
	}

	public void reload() {
		getDataProvider().refreshAll();
		fitHeight();
	}

	/**
	 * As tall as the jobs listed, up to {@link #MAX_VISIBLE_ROWS}; beyond that the list scrolls.
	 */
	private void fitHeight() {
		setHeightByRows(Math.max(1, Math.min(FacadeProvider.getClimateJobFacade().count(getCriteria()), MAX_VISIBLE_ROWS)));
	}
}
