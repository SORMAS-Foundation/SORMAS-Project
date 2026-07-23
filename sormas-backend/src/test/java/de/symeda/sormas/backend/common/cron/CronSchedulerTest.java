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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.symeda.sormas.api.systemconfiguration.CronJobRunOutcome;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.CronService;
import de.symeda.sormas.backend.systemconfiguration.event.SystemConfigurationValueChangedEvent;

public class CronSchedulerTest extends AbstractBeanTest {

	@Mock
	private CronService cronService;

	@BeforeEach
	public void warmUpSchedulerThenResetTimerService() {
		getBean(CronScheduler.class).scheduleAllJobs();
		reset(MockProducer.getTimerService());
	}

	@AfterEach
	public void restoreArchiveCasesDefaultExpression() {
		setConfigurationValue(CronJob.ARCHIVE_CASES, CronJob.ARCHIVE_CASES.getDefaultExpression());
	}

	private void setConfigurationValue(CronJob job, String expression) {
		configurationHelper().setConfigurationValue(job, expression);
	}

	private CronTestConfigurationHelper configurationHelper() {
		return new CronTestConfigurationHelper(getBean(SystemConfigurationValueFacade.class), getSystemConfigurationCategoryFacade());
	}

	@Test
	public void schedulesOneNonPersistentTimerPerJob() {

		TimerService timerService = MockProducer.getTimerService();
		getBean(CronScheduler.class).scheduleAllJobs();

		ArgumentCaptor<ScheduleExpression> expressions = ArgumentCaptor.forClass(ScheduleExpression.class);
		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(timerService, times(CronJob.values().length)).createCalendarTimer(expressions.capture(), configs.capture());

		assertEquals(
			Arrays.stream(CronJob.values()).collect(Collectors.toSet()),
			configs.getAllValues().stream().map(TimerConfig::getInfo).collect(Collectors.toSet()));
		assertTrue(configs.getAllValues().stream().noneMatch(TimerConfig::isPersistent));
	}

	@Test
	public void aJobThatFailsToScheduleDoesNotPreventTheOthersFromScheduling() {

		TimerService timerService = MockProducer.getTimerService();
		Mockito.doThrow(new IllegalArgumentException())
			.when(timerService)
			.createCalendarTimer(any(), argThat(config -> CronJob.ARCHIVE_CASES.equals(config.getInfo())));

		assertDoesNotThrow(() -> getBean(CronScheduler.class).scheduleAllJobs());

		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(timerService, times(CronJob.values().length)).createCalendarTimer(any(), configs.capture());
		assertEquals(
			Arrays.stream(CronJob.values()).collect(Collectors.toSet()),
			configs.getAllValues().stream().map(TimerConfig::getInfo).collect(Collectors.toSet()));
	}

	@Test
	public void createsNoTimerForAJobDisabledByAnEmptyValue() {

		TimerService timerService = MockProducer.getTimerService();
		setConfigurationValue(CronJob.ARCHIVE_CASES, "");
		reset(timerService);

		getBean(CronScheduler.class).scheduleAllJobs();

		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(timerService, times(CronJob.values().length - 1)).createCalendarTimer(any(), configs.capture());
		assertTrue(configs.getAllValues().stream().noneMatch(config -> CronJob.ARCHIVE_CASES.equals(config.getInfo())));
	}

	@Test
	public void schedulesTheConfiguredExpressionInsteadOfTheCompiledInDefault() {

		TimerService timerService = MockProducer.getTimerService();
		setConfigurationValue(CronJob.ARCHIVE_CASES, "0 45 3 * * *");
		reset(timerService);

		getBean(CronScheduler.class).scheduleAllJobs();

		ArgumentCaptor<ScheduleExpression> expressions = ArgumentCaptor.forClass(ScheduleExpression.class);
		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(timerService, times(CronJob.values().length)).createCalendarTimer(expressions.capture(), configs.capture());

		List<TimerConfig> capturedConfigs = configs.getAllValues();
		int archiveCasesIndex = IntStream.range(0, capturedConfigs.size())
			.filter(index -> CronJob.ARCHIVE_CASES.equals(capturedConfigs.get(index).getInfo()))
			.findFirst()
			.orElseThrow();
		ScheduleExpression archiveCasesExpression = expressions.getAllValues().get(archiveCasesIndex);

		assertEquals("0", archiveCasesExpression.getSecond());
		assertEquals("45", archiveCasesExpression.getMinute());
		assertEquals("3", archiveCasesExpression.getHour());
	}

	@Test
	public void theDispatcherSwallowsJobFailures() {

		javax.ejb.Timer timer = org.mockito.Mockito.mock(javax.ejb.Timer.class);
		org.mockito.Mockito.when(timer.getInfo()).thenReturn("not-a-cron-job");

		assertDoesNotThrow(() -> getBean(CronScheduler.class).executeJob(timer));
	}

	@Test
	public void savingACronValueReschedulesOnlyThatJob() {

		TimerService timerService = MockProducer.getTimerService();
		reset(timerService);

		setConfigurationValue(CronJob.ARCHIVE_CASES, "0 45 3 * * *");

		ArgumentCaptor<ScheduleExpression> expressions = ArgumentCaptor.forClass(ScheduleExpression.class);
		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(timerService, times(1)).createCalendarTimer(expressions.capture(), configs.capture());

		assertEquals(CronJob.ARCHIVE_CASES, configs.getValue().getInfo());
		assertEquals("45", expressions.getValue().getMinute());
		assertEquals("3", expressions.getValue().getHour());
	}

	@Test
	public void savingANonCronValueReschedulesNothing() {

		TimerService timerService = MockProducer.getTimerService();
		reset(timerService);

		getBean(CronScheduler.class).onSystemConfigurationValueChanged(
			new SystemConfigurationValueChangedEvent("MENU_SUBTITLE"));

		verify(timerService, never()).createCalendarTimer(any(), any());
	}

	@Test
	public void anUnknownCronKeyReschedulesNothing() {

		TimerService timerService = MockProducer.getTimerService();
		reset(timerService);

		getBean(CronScheduler.class).onSystemConfigurationValueChanged(
			new SystemConfigurationValueChangedEvent("CRON.NO_SUCH_JOB"));

		verify(timerService, never()).createCalendarTimer(any(), any());
	}

	@Test
	public void aSuccessfulDispatchIsRecorded() {

		javax.ejb.Timer timer = org.mockito.Mockito.mock(javax.ejb.Timer.class);
		org.mockito.Mockito.when(timer.getInfo()).thenReturn(CronJob.CLEAN_UP_TEMPORARY_FILES);

		getBean(CronScheduler.class).executeJob(timer);

		Optional<CronJobRun> recorded = getBean(CronJobRunRepository.class).findLatest(CronJob.CLEAN_UP_TEMPORARY_FILES);
		assertTrue(recorded.isPresent());
		assertEquals(CronJobRunOutcome.SUCCESS, recorded.get().getOutcome());
	}

	@Test
	public void aFailedDispatchIsRecorded() {

		Mockito.doThrow(new RuntimeException("boom")).when(cronService).archiveCases();

		javax.ejb.Timer timer = org.mockito.Mockito.mock(javax.ejb.Timer.class);
		org.mockito.Mockito.when(timer.getInfo()).thenReturn(CronJob.ARCHIVE_CASES);

		getBean(CronScheduler.class).executeJob(timer);

		Optional<CronJobRun> recorded = getBean(CronJobRunRepository.class).findLatest(CronJob.ARCHIVE_CASES);
		assertTrue(recorded.isPresent());
		assertEquals(CronJobRunOutcome.FAILED, recorded.get().getOutcome());
		assertEquals("boom", recorded.get().getFailureMessage());
	}

	@Test
	public void anExhaustedTimerDoesNotHideTheNextFireTimeOfAnotherJob() {

		javax.ejb.Timer timer = org.mockito.Mockito.mock(javax.ejb.Timer.class);
		org.mockito.Mockito.when(timer.getInfo()).thenReturn(CronJob.ARCHIVE_CASES, CronJob.ARCHIVE_EVENTS);
		Date liveNextTimeout = new Date();
		org.mockito.Mockito.when(timer.getNextTimeout()).thenThrow(new javax.ejb.NoMoreTimeoutsException()).thenReturn(liveNextTimeout);

		TimerService timerService = MockProducer.getTimerService();
		org.mockito.Mockito.when(timerService.getTimers()).thenReturn(Arrays.asList(timer, timer));

		Map<CronJob, Date> nextFireTimes = getBean(CronScheduler.class).getNextFireTimes();

		assertEquals(liveNextTimeout, nextFireTimes.get(CronJob.ARCHIVE_EVENTS));
		assertFalse(nextFireTimes.containsKey(CronJob.ARCHIVE_CASES));
	}
}
