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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Every day of the period shown, newest first, missing days included (decision 100). Each row names its source; users
 * with the climate edit right correct a day of the environment's own record, which saves a manual value. In the district
 * fallback the record belongs to the district, so there is no edit here.
 */
public class EnvironmentClimateDaysPanel extends VerticalLayout {

	private static final long serialVersionUID = 2913771503542826094L;

	private final Grid<ClimateDayDto> grid = new Grid<>();

	/**
	 * @param editableFor
	 *            the environment whose own record the user may correct, or null for no edit action
	 */
	public EnvironmentClimateDaysPanel(EnvironmentReferenceDto editableFor, Runnable onChanged) {

		setMargin(false);
		setSpacing(false);
		setWidthFull();
		addStyleName(CssStyles.VSPACE_TOP_4);

		Label heading = new Label(I18nProperties.getCaption(Captions.climateDailyValues));
		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);

		grid.setWidthFull();
		grid.addColumn(day -> ClimateCaptions.date(day.getDate())).setCaption(caption(ClimateObservationIndexDto.OBSERVATION_DATE));
		grid.addColumn(day -> ClimateCaptions.number(day.getTemperatureMin())).setCaption(caption(ClimateObservationIndexDto.TEMPERATURE_MIN));
		grid.addColumn(day -> ClimateCaptions.number(day.getTemperatureMean())).setCaption(caption(ClimateObservationIndexDto.TEMPERATURE_MEAN));
		grid.addColumn(day -> ClimateCaptions.number(day.getTemperatureMax())).setCaption(caption(ClimateObservationIndexDto.TEMPERATURE_MAX));
		grid.addColumn(day -> ClimateCaptions.number(day.getRainfall())).setCaption(caption(ClimateObservationIndexDto.RAINFALL));
		grid.addColumn(EnvironmentClimateDaysPanel::humidity).setCaption(caption(ClimateObservationIndexDto.RELATIVE_HUMIDITY));
		grid.addColumn(EnvironmentClimateDaysPanel::source).setCaption(caption(ClimateObservationIndexDto.SOURCE)).setExpandRatio(1);
		if (editableFor != null) {
			grid.addComponentColumn(day -> {
				Button edit = new Button(VaadinIcons.EDIT, e -> ClimateObservationForm.openForEnvironment(editableFor, day.getDate(), onChanged));
				edit.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_SMALL);
				edit.setDescription(I18nProperties.getCaption(Captions.edit));
				return edit;
			}).setWidth(70);
		}
		grid.setHeightByRows(10);
		grid.addStyleName(CssStyles.CLIMATE_GRID);
		addComponents(heading, grid);
	}

	public void update(List<ClimateDayDto> days) {
		List<ClimateDayDto> newestFirst = new ArrayList<>(days);
		Collections.reverse(newestFirst);
		grid.setItems(newestFirst);
		grid.setHeightByRows(Math.max(1, Math.min(newestFirst.size(), 10)));
	}

	/**
	 * @return "84 (noon reading)", or empty
	 */
	static String humidity(ClimateDayDto day) {
		if (day.getRelativeHumidity() == null) {
			return "";
		}
		String value = ClimateCaptions.number(day.getRelativeHumidity());
		return day.getHumidityMeasure() == HumidityMeasure.NOON_READING
			? value + " " + I18nProperties.getString(Strings.climateDetailNoonReading)
			: value;
	}

	/**
	 * @return the source of the day's values, or "No data" for a missing day (never 0)
	 */
	static String source(ClimateDayDto day) {
		return day.getSource() != null ? day.getSource().toString() : I18nProperties.getString(Strings.climateNoData);
	}

	private static String caption(String property) {
		return I18nProperties.getPrefixCaption(ClimateObservationIndexDto.I18N_PREFIX, property);
	}

	Grid<ClimateDayDto> getGrid() {
		return grid;
	}
}
