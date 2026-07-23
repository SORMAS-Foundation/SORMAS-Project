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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.systemconfiguration.CronJobStatusDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.AbstractBeanTest;

public class CronJobFacadeEjbTest extends AbstractBeanTest {

	@AfterEach
	public void restoreArchiveCasesDefaultExpression() {
		setConfigurationValue(CronJob.ARCHIVE_CASES, CronJob.ARCHIVE_CASES.getDefaultExpression());
	}

	private void setConfigurationValue(CronJob job, String expression) {
		new CronTestConfigurationHelper(getBean(SystemConfigurationValueFacade.class), getSystemConfigurationCategoryFacade())
			.setConfigurationValue(job, expression);
	}

	private Map<String, CronJobStatusDto> statusesByJobName() {
		return getBean(CronJobFacadeEjb.CronJobFacadeEjbLocal.class).getAllJobStatuses()
			.stream()
			.collect(Collectors.toMap(CronJobStatusDto::getJobName, Function.identity()));
	}

	@Test
	public void everyJobIsReported() {

		List<CronJobStatusDto> statuses = getBean(CronJobFacadeEjb.CronJobFacadeEjbLocal.class).getAllJobStatuses();

		assertEquals(CronJob.values().length, statuses.size());
		for (CronJob job : CronJob.values()) {
			CronJobStatusDto status = statusesByJobName().get(job.name());
			assertNotNull(status, job.name());
			assertEquals(job.getConfigKey(), status.getConfigKey());
			assertEquals(job.getDefaultExpression(), status.getDefaultExpression());
		}
	}

	@Test
	public void anUnconfiguredJobFallsBackToItsDefaultAndIsValid() {

		CronJobStatusDto status = statusesByJobName().get(CronJob.ARCHIVE_EVENTS.name());

		assertEquals(CronJob.ARCHIVE_EVENTS.getDefaultExpression(), status.getExpression());
		assertTrue(status.isEnabled());
		assertTrue(status.isExpressionValid());
	}

	@Test
	public void anEmptyValueReportsDisabledWithNoNextFireTime() {

		setConfigurationValue(CronJob.ARCHIVE_CASES, "");

		CronJobStatusDto status = statusesByJobName().get(CronJob.ARCHIVE_CASES.name());
		assertFalse(status.isEnabled());
		assertNull(status.getNextFireTime());
	}

	@Test
	public void aSemanticallyInvalidValueIsReportedAsInvalid() {

		setConfigurationValue(CronJob.ARCHIVE_CASES, "0 */70 * * * *");

		CronJobStatusDto status = statusesByJobName().get(CronJob.ARCHIVE_CASES.name());
		assertFalse(status.isExpressionValid());
		assertEquals("0 */70 * * * *", status.getExpression());
		assertEquals(CronJob.ARCHIVE_CASES.getDefaultExpression(), status.getDefaultExpression());
	}

	@Test
	public void aRecordedRunIsReported() {

		javax.ejb.Timer timer = org.mockito.Mockito.mock(javax.ejb.Timer.class);
		org.mockito.Mockito.when(timer.getInfo()).thenReturn(CronJob.CLEAN_UP_TEMPORARY_FILES);
		getBean(CronScheduler.class).executeJob(timer);

		CronJobStatusDto status = statusesByJobName().get(CronJob.CLEAN_UP_TEMPORARY_FILES.name());
		assertNotNull(status.getLastRunStart());
		assertNotNull(status.getLastRunOutcome());
	}
}
