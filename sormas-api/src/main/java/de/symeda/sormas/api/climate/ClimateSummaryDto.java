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

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * The read-only settings and call figures at the top of the Climate data screen: where data comes from, whether a key
 * is set, the calls needed per night and how last night's fetch went.
 */
@AuditedClass
public class ClimateSummaryDto implements Serializable {

	private static final long serialVersionUID = 7391806563095416204L;

	private ClimateDailySource dailySource;
	private ClimateHistorySource historySource;
	private boolean apiKeySet;
	private boolean nightlyFetchActive;
	private int historyYears;
	private int minHistoryYears;
	private int districtLocations;
	private int environmentOnlyLocations;
	private int districtsWithoutPoint;
	private ClimateJobIndexDto lastNightlyFetch;

	public ClimateDailySource getDailySource() {
		return dailySource;
	}

	public void setDailySource(ClimateDailySource dailySource) {
		this.dailySource = dailySource;
	}

	public ClimateHistorySource getHistorySource() {
		return historySource;
	}

	public void setHistorySource(ClimateHistorySource historySource) {
		this.historySource = historySource;
	}

	public boolean isApiKeySet() {
		return apiKeySet;
	}

	public void setApiKeySet(boolean apiKeySet) {
		this.apiKeySet = apiKeySet;
	}

	/**
	 * @return true when a nightly fetch runs each night: daily source OpenWeather, a key set and the schedule not disabled
	 */
	public boolean isNightlyFetchActive() {
		return nightlyFetchActive;
	}

	public void setNightlyFetchActive(boolean nightlyFetchActive) {
		this.nightlyFetchActive = nightlyFetchActive;
	}

	public int getHistoryYears() {
		return historyYears;
	}

	public void setHistoryYears(int historyYears) {
		this.historyYears = historyYears;
	}

	public int getMinHistoryYears() {
		return minHistoryYears;
	}

	public void setMinHistoryYears(int minHistoryYears) {
		this.minHistoryYears = minHistoryYears;
	}

	/**
	 * @return locations with coordinates that districts in use link to
	 */
	public int getDistrictLocations() {
		return districtLocations;
	}

	public void setDistrictLocations(int districtLocations) {
		this.districtLocations = districtLocations;
	}

	/**
	 * @return locations with coordinates that only environments in use link to
	 */
	public int getEnvironmentOnlyLocations() {
		return environmentOnlyLocations;
	}

	public void setEnvironmentOnlyLocations(int environmentOnlyLocations) {
		this.environmentOnlyLocations = environmentOnlyLocations;
	}

	/**
	 * @return the calls the nightly fetch needs: one per location in use with coordinates
	 */
	public int getCallsPerNight() {
		return districtLocations + environmentOnlyLocations;
	}

	/**
	 * @return districts that are not archived and get no API data, because their location has no coordinates
	 */
	public int getDistrictsWithoutPoint() {
		return districtsWithoutPoint;
	}

	public void setDistrictsWithoutPoint(int districtsWithoutPoint) {
		this.districtsWithoutPoint = districtsWithoutPoint;
	}

	/**
	 * @return the most recent nightly fetch, or null when none has run
	 */
	public ClimateJobIndexDto getLastNightlyFetch() {
		return lastNightlyFetch;
	}

	public void setLastNightlyFetch(ClimateJobIndexDto lastNightlyFetch) {
		this.lastNightlyFetch = lastNightlyFetch;
	}
}
