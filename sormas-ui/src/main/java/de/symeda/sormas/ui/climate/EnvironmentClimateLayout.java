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
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateDailyExportDto;
import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.IgnoreCancelDownloader;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.api.climate.ClimateEnvironmentContextDto;
import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The content of the environment Climate data tab (decision 85): context strip, period and actions, summary cards, the
 * stacked chart, cases near the site, site readings and daily values. The period drives everything but the cards.
 */
public class EnvironmentClimateLayout extends VerticalLayout {

	private static final long serialVersionUID = -6203816730541296364L;

	/** The periods offered (decision 88), each ending yesterday. */
	public enum ClimatePeriod {

		DAYS_30(30, Captions.climatePeriod30Days),
		DAYS_90(90, Captions.climatePeriod90Days),
		YEAR(365, Captions.climatePeriod1Year);

		private final int days;
		private final String caption;

		ClimatePeriod(int days, String caption) {
			this.days = days;
			this.caption = caption;
		}

		public int getDays() {
			return days;
		}
	}

	public static final ClimatePeriod DEFAULT_PERIOD = ClimatePeriod.DAYS_90;

	private final EnvironmentReferenceDto environment;
	private final boolean environmentEditAllowed;
	private final ClimateEnvironmentContextDto context;

	private final EnvironmentClimateStrip strip = new EnvironmentClimateStrip();
	private final HorizontalLayout actions = new HorizontalLayout();
	private final Map<ClimatePeriod, Button> periodButtons = new EnumMap<>(ClimatePeriod.class);
	private final EnvironmentClimateCards cards = new EnvironmentClimateCards();
	private final EnvironmentClimateChart chart = new EnvironmentClimateChart();
	/** Cases near the site, or null without the case view right. */
	private final EnvironmentNearbyCasesPanel nearbyCases;
	private final SiteClimateReadingsPanel siteReadings;
	private final EnvironmentClimateDaysPanel days;

	/** The environment's samples, or null without the right to see them; loaded once. */
	private final List<EnvironmentSampleIndexDto> samples;
	/** The events linked to the environment, or null without the right to see them; loaded once. */
	private final List<EventIndexDto> events;

	private ClimatePeriod period = DEFAULT_PERIOD;
	private ClimateDailySeriesDto series;
	private List<SiteClimateReadingDto> readings;

	/**
	 * @param environmentEditAllowed
	 *            whether the user may edit the environment itself (jurisdiction, not archived or deleted): site readings
	 *            follow it
	 */
	public EnvironmentClimateLayout(EnvironmentReferenceDto environment, boolean environmentEditAllowed) {

		this.environment = environment;
		this.environmentEditAllowed = environmentEditAllowed;
		this.context = FacadeProvider.getClimateSeriesFacade().getEnvironmentContext(environment);
		this.samples = UiUtil.permitted(FeatureType.SAMPLES_LAB, UserRight.ENVIRONMENT_SAMPLE_VIEW)
			? FacadeProvider.getEnvironmentSampleFacade().getIndexList(new EnvironmentSampleCriteria().withEnvironment(environment), null, null, null)
			: null;
		this.events = UiUtil.permitted(FeatureType.EVENT_SURVEILLANCE, UserRight.EVENT_VIEW)
			? FacadeProvider.getEventFacade().getIndexList(new EventCriteria().environment(environment), null, null, null)
			: null;

		setMargin(false);
		setWidthFull();

		addComponent(strip);
		addComponent(createActions());
		addComponent(cards);
		addComponent(chart);
		if (UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_VIEW)) {
			nearbyCases = new EnvironmentNearbyCasesPanel(environment, !context.isOwnLocation());
			addComponent(nearbyCases);
		} else {
			nearbyCases = null;
		}
		siteReadings = new SiteClimateReadingsPanel(isSiteReadingEditAllowed(), this::reload);
		addComponent(siteReadings);
		days = new EnvironmentClimateDaysPanel(isManualEntryAllowed() ? environment : null, this::reload);
		addComponent(days);

		reload();
	}

	private HorizontalLayout createActions() {

		actions.setMargin(false);
		actions.addStyleName(CssStyles.VSPACE_TOP_4);
		for (ClimatePeriod option : ClimatePeriod.values()) {
			Button button = ButtonHelper.createButton(option.caption, e -> selectPeriod(option));
			periodButtons.put(option, button);
			actions.addComponent(button);
		}
		if (isSiteReadingEditAllowed()) {
			actions.addComponent(
				ButtonHelper.createIconButton(
					Captions.climateNewSiteReading,
					VaadinIcons.PLUS_CIRCLE,
					e -> SiteClimateReadingForm.openNew(environment, this::reload),
					ValoTheme.BUTTON_PRIMARY));
		}
		if (isManualEntryAllowed()) {
			actions.addComponent(
				ButtonHelper.createIconButton(
					Captions.climateManualEntry,
					VaadinIcons.PLUS_CIRCLE,
					e -> ClimateObservationForm.openForEnvironment(environment, null, this::reload),
					ValoTheme.BUTTON_PRIMARY));
		}
		if (UiUtil.permitted(UserRight.CLIMATE_EXPORT)) {
			Button export = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			export.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			StreamResource resource = DownloadUtil.createCsvExportStreamResource(
				ClimateDailyExportDto.class,
				null,
				// the whole period comes in one call; it is never more than a year of days
				(Integer start, Integer max) -> start == 0
					? FacadeProvider.getClimateSeriesFacade().getDailyExportList(environment, getFrom(), getTo())
					: Collections.emptyList(),
				(propertyId, type) -> {
					String caption = I18nProperties.findPrefixCaption(
						propertyId,
						ClimateDailyExportDto.I18N_PREFIX,
						ClimateObservationExportDto.I18N_PREFIX,
						ClimateObservationIndexDto.I18N_PREFIX);
					return Date.class.isAssignableFrom(type) ? caption + " (" + DateFormatHelper.getDateFormatPattern() + ")" : caption;
				},
				ExportEntityName.CLIMATE_DAILY_VALUES,
				null);
			new IgnoreCancelDownloader(resource).extend(export);
			actions.addComponent(export);
		}
		return actions;
	}

	/**
	 * @return true when the user may add and correct site readings: environment edit right and an environment they may
	 *         edit (decision 99); no climate right needed beyond seeing the tab
	 */
	public boolean isSiteReadingEditAllowed() {
		return environmentEditAllowed && UiUtil.permitted(UserRight.ENVIRONMENT_EDIT);
	}

	/**
	 * @return true when the user may enter and correct daily values here: climate edit right and the environment's own
	 *         record; in the district fallback the record belongs to the district (decision 100)
	 */
	public boolean isManualEntryAllowed() {
		return context.isOwnLocation() && UiUtil.permitted(UserRight.CLIMATE_EDIT);
	}

	/**
	 * @return yesterday: the last day of every period
	 */
	public LocalDate getTo() {
		return LocalDate.now().minusDays(1);
	}

	public LocalDate getFrom() {
		return getTo().minusDays(period.getDays() - 1L);
	}

	public ClimatePeriod getPeriod() {
		return period;
	}

	public void selectPeriod(ClimatePeriod period) {
		this.period = period;
		reload();
	}

	EnvironmentClimateStrip getStrip() {
		return strip;
	}

	public ClimateEnvironmentContextDto getContext() {
		return context;
	}

	public ClimateDailySeriesDto getSeries() {
		return series;
	}

	/**
	 * Loads the period's daily series and updates every part that shows it.
	 */
	public void reload() {

		periodButtons.forEach((option, button) -> {
			button.removeStyleName(ValoTheme.BUTTON_PRIMARY);
			if (option == period) {
				button.addStyleName(ValoTheme.BUTTON_PRIMARY);
			}
		});

		LocalDate from = getFrom();
		LocalDate to = getTo();
		series = FacadeProvider.getClimateSeriesFacade().getDailySeries(environment, from, to);
		readings = FacadeProvider.getSiteClimateReadingFacade().getReadings(environment, from, to);
		strip.update(context, series, from, LocalDate.now());
		cards.update(series);
		chart.update(series, from, to, context.getReportDate(), samples, events, readings);
		if (nearbyCases != null) {
			nearbyCases.update(from, to);
		}
		siteReadings.update(readings);
		days.update(series.getDays());
	}

	SiteClimateReadingsPanel getSiteReadings() {
		return siteReadings;
	}

	EnvironmentClimateDaysPanel getDays() {
		return days;
	}

	EnvironmentNearbyCasesPanel getNearbyCases() {
		return nearbyCases;
	}

	public List<SiteClimateReadingDto> getReadings() {
		return readings;
	}

	EnvironmentClimateChart getChart() {
		return chart;
	}

	protected EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	protected boolean isEnvironmentEditAllowed() {
		return environmentEditAllowed;
	}
}
