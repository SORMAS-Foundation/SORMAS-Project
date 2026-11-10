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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.ClimateEnvironmentContextDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

/**
 * The context card on the right of the environment Climate data tab (decision 105): which point the data belongs to,
 * its sources over the period shown, how far back history goes and the latest day, then a line per situation that needs
 * saying (no usable GPS, shared, outside the district, archived, history loading, no early history). One fact per line,
 * each with an icon.
 */
public class EnvironmentClimateContextCard extends SideComponent {

	private static final long serialVersionUID = -2185338040815560410L;

	/** The latest day turns to warning colour when older than this: the provider's 1-day delay plus 2 days. */
	public static final int STALE_AFTER_DAYS = 3;

	private final VerticalLayout lines = new VerticalLayout();

	public EnvironmentClimateContextCard() {
		super(I18nProperties.getCaption(Captions.environmentClimateData));
		addStyleNames(CssStyles.SIDE_COMPONENT, CssStyles.SIDE_COMPONENT_FLUID);
		lines.setMargin(false);
		lines.setSpacing(false);
		lines.setWidthFull();
		lines.addStyleName(CssStyles.VSPACE_TOP_4);
		addComponent(lines);
	}

	public void update(ClimateEnvironmentContextDto context, ClimateDailySeriesDto series, LocalDate periodFrom, LocalDate today) {

		lines.removeAllComponents();

		if (!context.isOwnLocation() && context.getDistrict() == null) {
			addLine(VaadinIcons.WARNING, I18nProperties.getString(Strings.climateContextNoDistrict), CssStyles.LABEL_WARNING);
			return;
		}

		if (context.isOwnLocation()) {
			addLine(
				VaadinIcons.MAP_MARKER,
				String.format(
					I18nProperties.getString(Strings.climateContextPoint),
					ClimateCaptions.coordinate(context.getLatitude()),
					ClimateCaptions.coordinate(context.getLongitude()),
					ClimateCaptions.coordinate(context.getCellLatitude()),
					ClimateCaptions.coordinate(context.getCellLongitude())),
				null);
		} else {
			addLine(
				VaadinIcons.MAP_MARKER,
				String.format(I18nProperties.getString(Strings.climateContextNoGps), context.getDistrict().getCaption()),
				CssStyles.LABEL_WARNING);
			if (!context.isDistrictHasPoint()) {
				addLine(
					VaadinIcons.INFO_CIRCLE,
					String.format(I18nProperties.getString(Strings.climateContextDistrictNoPoint), context.getDistrict().getCaption()),
					null);
			}
		}

		String sources = sources(series);
		if (sources != null) {
			addLine(VaadinIcons.DATABASE, String.format(I18nProperties.getString(Strings.climateContextSources), sources), null);
		}
		if (context.getHistoryFrom() != null) {
			addLine(
				VaadinIcons.TIME_BACKWARD,
				String.format(I18nProperties.getString(Strings.climateContextHistoryFrom), ClimateCaptions.date(context.getHistoryFrom())),
				null);
		}

		if (context.getLatestDay() == null) {
			addLine(VaadinIcons.CALENDAR, I18nProperties.getString(Strings.climateContextNoData), null);
		} else {
			boolean stale = context.getLatestDay().isBefore(today.minusDays(STALE_AFTER_DAYS));
			addLine(
				VaadinIcons.CALENDAR,
				String.format(I18nProperties.getString(Strings.climateContextLatestDay), ClimateCaptions.date(context.getLatestDay())),
				stale ? CssStyles.LABEL_WARNING : null);
		}

		// where the chart's normal ranges come from, or how much history has built up before they exist (decision 108)
		String normals = EnvironmentClimateChart.status(series);
		if (!normals.isEmpty()) {
			addLine(VaadinIcons.CHART, normals, null);
		}

		if (context.isOwnLocation() && context.isShared()) {
			HorizontalLayout shared =
				addLine(VaadinIcons.SHARE, String.format(I18nProperties.getString(Strings.climateContextShared), sharedWith(context)), null);
			List<String> names = new ArrayList<>(context.getSharedDistricts());
			names.addAll(context.getSharedEnvironments());
			shared.setDescription(String.join(", ", names));
		}

		if (context.isOutsideDistrict()) {
			addLine(
				VaadinIcons.WARNING,
				String.format(I18nProperties.getString(Strings.climateContextOutsideDistrict), context.getDistrict().getCaption()),
				CssStyles.LABEL_WARNING);
		}

		if (context.isArchived() || context.isDeleted()) {
			VaadinIcons icon = context.isDeleted() ? VaadinIcons.TRASH : VaadinIcons.ARCHIVE;
			if (context.isStillUpdated()) {
				String key = context.isDeleted() ? Strings.climateContextDeletedShared : Strings.climateContextArchivedShared;
				addLine(icon, String.format(I18nProperties.getString(key), stillUpdatedBy(context)), null);
			} else {
				String key = context.isDeleted() ? Strings.climateContextDeletedStopped : Strings.climateContextArchivedStopped;
				addLine(icon, I18nProperties.getString(key), null);
			}
		}

		if (context.getLoadingJobType() != null) {
			addLine(
				VaadinIcons.REFRESH,
				String.format(
					I18nProperties.getString(Strings.climateContextLoading),
					context.getLoadingLocationsDone(),
					context.getLoadingLocationsTotal()),
				null);
		} else if (context.getHistoryFrom() != null && context.getHistoryFrom().isAfter(periodFrom)) {
			addLine(
				VaadinIcons.INFO_CIRCLE,
				String.format(I18nProperties.getString(Strings.climateContextNotLoaded), ClimateCaptions.date(context.getHistoryFrom())),
				null);
		}
	}

	/**
	 * @return "OpenWeather 85 days, Manual 2 days" over the days shown, or null when none has data
	 */
	static String sources(ClimateDailySeriesDto series) {

		Map<ClimateDataSource, Integer> days = new EnumMap<>(ClimateDataSource.class);
		for (ClimateDayDto day : series.getDays()) {
			if (day.getSource() != null) {
				days.merge(day.getSource(), 1, Integer::sum);
			}
		}
		if (days.isEmpty()) {
			return null;
		}
		return days.entrySet()
			.stream()
			.map(e -> String.format(I18nProperties.getString(Strings.climateSourceDays), e.getKey(), e.getValue()))
			.collect(Collectors.joining(", "));
	}

	/**
	 * @return "Bagaces district and 1 nearby environment"
	 */
	private static String sharedWith(ClimateEnvironmentContextDto context) {

		List<String> parts = new ArrayList<>();
		for (String district : context.getSharedDistricts()) {
			parts.add(String.format(I18nProperties.getString(Strings.climateContextDistrictName), district));
		}
		int environments = context.getSharedEnvironments().size();
		if (environments == 1) {
			parts.add(I18nProperties.getString(Strings.climateContextNearbyEnvironment));
		} else if (environments > 1) {
			parts.add(String.format(I18nProperties.getString(Strings.climateContextNearbyEnvironments), environments));
		}
		return ClimateCaptions.joinAnd(parts);
	}

	/**
	 * @return who keeps an archived or deleted environment's location in use: the district it falls back to, or the
	 *         districts and environments sharing its own location
	 */
	private static String stillUpdatedBy(ClimateEnvironmentContextDto context) {
		if (!context.isOwnLocation()) {
			return String.format(I18nProperties.getString(Strings.climateContextDistrictName), context.getDistrict().getCaption());
		}
		return sharedWith(context);
	}

	/**
	 * Adds one fact: the icon, then the text, which wraps within the card.
	 */
	private HorizontalLayout addLine(VaadinIcons icon, String text, String style) {

		Label iconLabel = new Label(icon.getHtml(), ContentMode.HTML);
		iconLabel.setWidthUndefined();
		Label textLabel = new Label(text);
		textLabel.setWidthFull();

		HorizontalLayout line = new HorizontalLayout(iconLabel, textLabel);
		line.setMargin(false);
		line.setSpacing(true);
		line.setWidthFull();
		line.setExpandRatio(textLabel, 1);
		if (style != null) {
			iconLabel.addStyleName(style);
			textLabel.addStyleName(style);
		}
		lines.addComponent(line);
		return line;
	}
}
