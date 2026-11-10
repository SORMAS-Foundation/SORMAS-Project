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

import java.time.LocalDate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The nightly climate run (CRON.FETCH_CLIMATE_DATA), while the climate feature is on. Each step runs in its own
 * transaction and a failed step does not stop the next one:
 * <ol>
 * <li>Linking safety net: districts and environments are linked to the climate location for their point. This also
 * relinks everything after a grid size change, because linking uses the current grid size.</li>
 * <li>Cleanup: climate locations that nothing refers to are deleted (they hold no data).</li>
 * <li>Tonight's fetch is queued; the job processor works through it.</li>
 * </ol>
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ClimateNightlyRun {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ClimateSettings climateSettings;
	@EJB
	private ClimateLinkingService climateLinkingService;
	@EJB
	private ClimateLocationService climateLocationService;
	@EJB
	private ClimateJobService climateJobService;

	public void run() {

		if (!climateSettings.isClimateEnabled()) {
			return;
		}

		try {
			int districts = climateLinkingService.linkAllDistricts();
			int environments = climateLinkingService.linkAllEnvironments();
			if (districts > 0 || environments > 0) {
				logger.info("Climate nightly run: linked {} districts and {} environments to climate locations", districts, environments);
			}
		} catch (RuntimeException e) {
			logger.error("Climate nightly run: linking districts and environments failed", e);
		}

		try {
			int deleted = climateLocationService.deleteUnreferenced();
			if (deleted > 0) {
				logger.info("Climate nightly run: deleted {} climate locations that nothing refers to and that hold no data", deleted);
			}
		} catch (RuntimeException e) {
			logger.error("Climate nightly run: deleting unused climate locations failed; nothing was deleted", e);
		}

		try {
			ClimateJob job = climateJobService.queueDailyFetch(LocalDate.now());
			if (job != null && job.getLastError() != null) {
				logger.warn("Climate nightly fetch not run: {}", job.getLastError());
			}
		} catch (RuntimeException e) {
			logger.error("Climate nightly run: queueing the fetch failed", e);
		}
	}
}
