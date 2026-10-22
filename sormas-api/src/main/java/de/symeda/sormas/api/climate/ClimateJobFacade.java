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

import java.util.Date;

import javax.ejb.Remote;

import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

/**
 * Starts and controls climate jobs that call OpenWeather. The scope is all districts (no region and no district), one
 * region, or one district; with {@code includeEnvironments}, the locations of the environments in that scope are covered
 * too. Calls count against the OpenWeather account's daily limit, and calls above the free allowance are charged, so the
 * screen shows the number of calls before a job is started.
 */
@Remote
public interface ClimateJobFacade {

	/**
	 * @return the calls a backfill of {@code years} years would make: from 1 January of the year that many years back to
	 *         yesterday, skipping the days already held from any source
	 */
	long calculateBackfillCalls(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, int years);

	/**
	 * Queues a backfill. Needs OpenWeather as the climate history source and an API key.
	 *
	 * @param confirmedCalls
	 *            the number of calls the user confirmed, stored with the job
	 * @return the job's uuid
	 */
	String startBackfill(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, int years, long confirmedCalls);

	/**
	 * @return the calls fetching the period again would make (one per 10 days per location)
	 */
	long calculateFetchCalls(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, Date from, Date to);

	/**
	 * Queues a fetch of the period ("fetch now"), which runs at once and updates OpenWeather rows whose values changed.
	 * Needs OpenWeather as the climate daily source and an API key.
	 *
	 * @return the job's uuid
	 */
	String fetchNow(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, Date from, Date to);

	/**
	 * Stops a queued, running or paused job. Rows already saved stay.
	 */
	void cancel(String jobUuid);

	/**
	 * Queues a paused, failed or cancelled backfill, top-up or fetch again; it carries on where it stopped and skips the
	 * days already held.
	 */
	void startAgain(String jobUuid);
}
