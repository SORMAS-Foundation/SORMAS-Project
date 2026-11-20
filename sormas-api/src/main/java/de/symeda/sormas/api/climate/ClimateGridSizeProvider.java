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

package de.symeda.sormas.api.climate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

/**
 * Dropdown for CLIMATE_GRID_SIZE, in degrees.
 */
public class ClimateGridSizeProvider extends AbstractClimateDropdownProvider {

	private static final long serialVersionUID = 1L;

	/** Approximate cell width at the equator, in km, for each offered grid size. */
	private static final Map<String, String> APPROXIMATE_KM = Map.of("0.01", "1", "0.025", "2.5", "0.05", "5.5", "0.1", "11", "0.25", "28");

	@Override
	protected List<String> getOptionValues() {
		return Arrays.stream(ClimateGrid.ALLOWED_GRID_SIZES).mapToObj(ClimateGridSizeProvider::format).collect(Collectors.toList());
	}

	@Override
	public String getOptionCaption(String optionValue) {

		String km = APPROXIMATE_KM.get(optionValue);
		return km != null ? String.format(I18nProperties.getString(Strings.climateGridSizeOption), optionValue, km) : optionValue;
	}

	/**
	 * Double.toString gives the shortest exact form of each offered size ("0.01", "0.025", "0.05", "0.1", "0.25"), which
	 * is also how the setting is stored.
	 */
	private static String format(double gridSize) {
		return String.valueOf(gridSize);
	}
}
