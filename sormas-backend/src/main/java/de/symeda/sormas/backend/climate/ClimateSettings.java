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

package de.symeda.sormas.backend.climate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateDailySource;
import de.symeda.sormas.api.climate.ClimateHistorySource;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.common.cron.CronJob;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;

/**
 * The climate settings the jobs read, from System Configuration. They are read each time they are used, so a change
 * applies without a restart. A missing or invalid value falls back to its default.
 */
@Stateless
@LocalBean
public class ClimateSettings {

	/** Default and largest value of CLIMATE_FETCH_CATCHUP_DAYS: one call returns 10 days, yesterday included. */
	public static final int MAX_CATCHUP_DAYS = 9;

	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	public boolean isClimateEnabled() {
		return featureConfigurationFacade.isFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT);
	}

	public ClimateDailySource getDailySource() {
		try {
			return ClimateDailySource.valueOf(StringUtils.trimToEmpty(systemConfigurationValueFacade.getValue(ClimateConfiguration.DAILY_SOURCE)));
		} catch (IllegalArgumentException e) {
			return ClimateDailySource.OPENWEATHER;
		}
	}

	public ClimateHistorySource getHistorySource() {
		try {
			return ClimateHistorySource
				.valueOf(StringUtils.trimToEmpty(systemConfigurationValueFacade.getValue(ClimateConfiguration.HISTORY_SOURCE)));
		} catch (IllegalArgumentException e) {
			return ClimateHistorySource.NONE;
		}
	}

	/**
	 * @return the OpenWeather API key, or null when none is set
	 */
	public String getApiKey() {
		return StringUtils.trimToNull(systemConfigurationValueFacade.getValue(ClimateConfiguration.OPENWEATHER_API_KEY));
	}

	public int getCatchupDays() {
		try {
			int days = Integer.parseInt(StringUtils.trimToEmpty(systemConfigurationValueFacade.getValue(ClimateConfiguration.FETCH_CATCHUP_DAYS)));
			return days >= 0 && days <= MAX_CATCHUP_DAYS ? days : MAX_CATCHUP_DAYS;
		} catch (NumberFormatException e) {
			return MAX_CATCHUP_DAYS;
		}
	}

	public int getHistoryYears() {
		try {
			int years = Integer.parseInt(StringUtils.trimToEmpty(systemConfigurationValueFacade.getValue(ClimateConfiguration.HISTORY_YEARS)));
			return years >= 1 && years <= ClimateConfiguration.MAX_HISTORY_YEARS ? years : ClimateConfiguration.DEFAULT_HISTORY_YEARS;
		} catch (NumberFormatException e) {
			return ClimateConfiguration.DEFAULT_HISTORY_YEARS;
		}
	}

	public boolean isDailyFromOpenWeather() {
		return getDailySource() == ClimateDailySource.OPENWEATHER;
	}

	public boolean isHistoryFromOpenWeather() {
		return getHistorySource() == ClimateHistorySource.OPENWEATHER;
	}

	/**
	 * @return true when a nightly fetch is expected each day: the daily source is OpenWeather, a key is set and the
	 *         schedule is not disabled. History jobs then wait for it, so they never take its calls.
	 */
	public boolean isNightlyFetchActive() {
		String schedule = systemConfigurationValueFacade.getValue(CronJob.FETCH_CLIMATE_DATA.getConfigKey());
		return isDailyFromOpenWeather() && getApiKey() != null && (schedule == null || !CronExpressionValidator.isDisabled(schedule));
	}
}
