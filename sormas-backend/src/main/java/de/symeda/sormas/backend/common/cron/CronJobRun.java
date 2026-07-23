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

import java.util.Date;

import de.symeda.sormas.api.systemconfiguration.CronJobRunOutcome;

public final class CronJobRun {

	private final Date start;
	private final Date end;
	private final CronJobRunOutcome outcome;
	private final String failureMessage;

	public CronJobRun(Date start, Date end, CronJobRunOutcome outcome, String failureMessage) {
		this.start = new Date(start.getTime());
		this.end = new Date(end.getTime());
		this.outcome = outcome;
		this.failureMessage = failureMessage;
	}

	public Date getStart() {
		return new Date(start.getTime());
	}

	public Date getEnd() {
		return new Date(end.getTime());
	}

	public long getDurationMillis() {
		return end.getTime() - start.getTime();
	}

	public CronJobRunOutcome getOutcome() {
		return outcome;
	}

	public String getFailureMessage() {
		return failureMessage;
	}
}
