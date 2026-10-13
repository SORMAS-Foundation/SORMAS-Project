/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.common.cron;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;

@Singleton
public class InMemoryCronJobRunRepository implements CronJobRunRepository {

	private final Map<CronJob, CronJobRun> latestRunByJob = new ConcurrentHashMap<>();

	@Override
	public void record(CronJob job, CronJobRun run) {
		latestRunByJob.put(job, run);
	}

	@Override
	public Optional<CronJobRun> findLatest(CronJob job) {
		return Optional.ofNullable(latestRunByJob.get(job));
	}
}
