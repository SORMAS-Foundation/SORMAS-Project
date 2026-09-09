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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.systemconfiguration.CronJobRunOutcome;

public class InMemoryCronJobRunRepositoryTest {

	private CronJobRunRepository repository;

	@BeforeEach
	public void setUp() {
		repository = new InMemoryCronJobRunRepository();
	}

	@Test
	public void findLatestIsEmptyBeforeAnyRun() {
		assertFalse(repository.findLatest(CronJob.ARCHIVE_CASES).isPresent());
	}

	@Test
	public void recordRoundTripsEveryField() {

		Date start = new Date(1_784_785_200_000L);
		Date end = new Date(1_784_785_200_310L);
		repository.record(CronJob.ARCHIVE_CASES, new CronJobRun(start, end, CronJobRunOutcome.FAILED, "boom"));

		CronJobRun stored = repository.findLatest(CronJob.ARCHIVE_CASES).orElseThrow(AssertionError::new);
		assertEquals(start, stored.getStart());
		assertEquals(end, stored.getEnd());
		assertEquals(310L, stored.getDurationMillis());
		assertEquals(CronJobRunOutcome.FAILED, stored.getOutcome());
		assertEquals("boom", stored.getFailureMessage());
	}

	@Test
	public void aLaterRunSupersedesTheEarlierOne() {

		repository.record(CronJob.ARCHIVE_CASES, new CronJobRun(new Date(1_000L), new Date(2_000L), CronJobRunOutcome.FAILED, "old"));
		repository.record(CronJob.ARCHIVE_CASES, new CronJobRun(new Date(3_000L), new Date(4_000L), CronJobRunOutcome.SUCCESS, null));

		CronJobRun stored = repository.findLatest(CronJob.ARCHIVE_CASES).orElseThrow(AssertionError::new);
		assertEquals(CronJobRunOutcome.SUCCESS, stored.getOutcome());
		assertEquals(new Date(3_000L), stored.getStart());
	}

	@Test
	public void jobsAreRecordedIndependently() {

		repository.record(CronJob.ARCHIVE_CASES, new CronJobRun(new Date(1_000L), new Date(2_000L), CronJobRunOutcome.SUCCESS, null));
		assertTrue(repository.findLatest(CronJob.ARCHIVE_CASES).isPresent());
		assertFalse(repository.findLatest(CronJob.ARCHIVE_EVENTS).isPresent());
	}
}
