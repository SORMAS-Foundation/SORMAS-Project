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

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.ClimateEnvironmentContextDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The context strip of the environment Climate data tab: which point the data belongs to, its sources over the period
 * shown, the latest day and how far back history goes, then a line per situation that needs saying (no usable GPS,
 * shared, outside the district, archived, history loading, no early history).
 */
public class EnvironmentClimateStrip extends VerticalLayout {

	private static final long serialVersionUID = -2185338040815560410L;

	/** The latest day turns to warning colour when older than this: the provider's 1-day delay plus 2 days. */
	public static final int STALE_AFTER_DAYS = 3;

	public EnvironmentClimateStrip() {
		setMargin(false);
		setSpacing(false);
		setWidthFull();
	}

	public void update(ClimateEnvironmentContextDto context, ClimateDailySeriesDto series, LocalDate periodFrom, LocalDate today) {

		removeAllComponents();

		if (!context.isOwnLocation() && context.getDistrict() == null) {
			addLine(I18nProperties.getString(Strings.climateContextNoDistrict), CssStyles.LABEL_WARNING);
			return;
		}

		if (!context.isOwnLocation()) {
			addLine(String.format(I18nProperties.getString(Strings.climateContextNoGps), context.getDistrict().getCaption()), CssStyles.LABEL_WARNING);
			if (!context.isDistrictHasPoint()) {
				addLine(String.format(I18nProperties.getString(Strings.climateContextDistrictNoPoint), context.getDistrict().getCaption()), null);
			}
		}

		List<String> facts = new ArrayList<>();
		if (context.isOwnLocation()) {
			facts.add(
				String.format(
					I18nProperties.getString(Strings.climateContextPoint),
					ClimateCaptions.coordinate(context.getLatitude()),
					ClimateCaptions.coordinate(context.getLongitude()),
					ClimateCaptions.coordinate(context.getCellLatitude()),
					ClimateCaptions.coordinate(context.getCellLongitude())));
		}
		String sources = sources(series);
		if (sources != null) {
			facts.add(String.format(I18nProperties.getString(Strings.climateContextSources), sources));
		}
		if (context.getHistoryFrom() != null) {
			facts.add(String.format(I18nProperties.getString(Strings.climateContextHistoryFrom), ClimateCaptions.date(context.getHistoryFrom())));
		}
		if (!facts.isEmpty()) {
			addLine(String.join(" · ", facts), null);
		}

		if (context.getLatestDay() == null) {
			addLine(I18nProperties.getString(Strings.climateContextNoData), null);
		} else {
			boolean stale = context.getLatestDay().isBefore(today.minusDays(STALE_AFTER_DAYS));
			addLine(
				String.format(I18nProperties.getString(Strings.climateContextLatestDay), ClimateCaptions.date(context.getLatestDay())),
				stale ? CssStyles.LABEL_WARNING : null);
		}

		if (context.isOwnLocation() && context.isShared()) {
			Label shared = addLine(String.format(I18nProperties.getString(Strings.climateContextShared), sharedWith(context)), null);
			List<String> names = new ArrayList<>(context.getSharedDistricts());
			names.addAll(context.getSharedEnvironments());
			shared.setDescription(String.join(", ", names));
		}

		if (context.isOutsideDistrict()) {
			addLine(String.format(I18nProperties.getString(Strings.climateContextOutsideDistrict), context.getDistrict().getCaption()), CssStyles.LABEL_WARNING);
		}

		if (context.isArchived() || context.isDeleted()) {
			String key;
			if (context.isStillUpdated()) {
				key = context.isDeleted() ? Strings.climateContextDeletedShared : Strings.climateContextArchivedShared;
				addLine(String.format(I18nProperties.getString(key), stillUpdatedBy(context)), null);
			} else {
				key = context.isDeleted() ? Strings.climateContextDeletedStopped : Strings.climateContextArchivedStopped;
				addLine(I18nProperties.getString(key), null);
			}
		}

		if (context.getLoadingJobType() != null) {
			addLine(
				String.format(
					I18nProperties.getString(Strings.climateContextLoading),
					context.getLoadingLocationsDone(),
					context.getLoadingLocationsTotal()),
				null);
		} else if (context.getHistoryFrom() != null && context.getHistoryFrom().isAfter(periodFrom)) {
			addLine(String.format(I18nProperties.getString(Strings.climateContextNotLoaded), ClimateCaptions.date(context.getHistoryFrom())), null);
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

	private Label addLine(String text, String style) {
		Label label = new Label(text);
		label.setWidthFull();
		if (style != null) {
			label.addStyleName(style);
		}
		addComponent(label);
		return label;
	}
}
