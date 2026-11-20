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

package de.symeda.sormas.ui.configuration.system;

import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateCosts;
import de.symeda.sormas.api.climate.ClimateHistorySource;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

/**
 * Warnings shown under a system configuration input while a particular value is selected, for example about the cost of
 * choosing it. Register a setting here by its key; the function receives the selected value and returns the warning, or
 * null when that value needs none.
 */
public final class SystemConfigurationOptionWarnings {

	private static final Map<String, Function<String, String>> WARNINGS =
		Map.of(ClimateConfiguration.HISTORY_SOURCE, SystemConfigurationOptionWarnings::climateHistorySourceWarning);

	private SystemConfigurationOptionWarnings() {
	}

	/**
	 * @return the warning for the selected value of the setting, or null
	 */
	public static String getWarning(String configKey, String selectedValue) {

		Function<String, String> warning = configKey != null ? WARNINGS.get(configKey) : null;
		return warning != null ? warning.apply(selectedValue) : null;
	}

	/**
	 * History from OpenWeather is charged per call, so choosing it shows how many calls a backfill of this deployment's
	 * districts needs. Calls only, no money: a stored price would go stale without anyone noticing.
	 */
	private static String climateHistorySourceWarning(String selectedValue) {

		if (!ClimateHistorySource.OPENWEATHER.name().equals(selectedValue)) {
			return null;
		}

		int years = parseYears(FacadeProvider.getSystemConfigurationValueFacade().getValue(ClimateConfiguration.HISTORY_YEARS));
		int districts = FacadeProvider.getDistrictFacade().getAllActiveAsReference().size();

		return String.format(
			I18nProperties.getString(Strings.infoClimateHistorySourceCostWarning),
			String.format("%,d", districts),
			years,
			String.format("%,d", ClimateCosts.historyCalls(districts, years)),
			ClimateCosts.historyCalls(1, 1));
	}

	private static int parseYears(String value) {

		try {
			int years = Integer.parseInt(value.trim());
			return years > 0 ? years : ClimateConfiguration.DEFAULT_HISTORY_YEARS;
		} catch (NumberFormatException | NullPointerException e) {
			return ClimateConfiguration.DEFAULT_HISTORY_YEARS;
		}
	}
}
