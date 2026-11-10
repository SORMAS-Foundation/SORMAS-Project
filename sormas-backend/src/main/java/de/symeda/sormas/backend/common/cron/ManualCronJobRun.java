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

import java.io.Serializable;
import java.util.Objects;

public final class ManualCronJobRun implements Serializable {

	private static final long serialVersionUID = 1L;

	private final CronJob job;

	public ManualCronJobRun(CronJob job) {
		this.job = Objects.requireNonNull(job);
	}

	public CronJob getJob() {
		return job;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof ManualCronJobRun && job == ((ManualCronJobRun) other).job;
	}

	@Override
	public int hashCode() {
		return job.hashCode();
	}

	@Override
	public String toString() {
		return "ManualCronJobRun[" + job + "]";
	}
}
