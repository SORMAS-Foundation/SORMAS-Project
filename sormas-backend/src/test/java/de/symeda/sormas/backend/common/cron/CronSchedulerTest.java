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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.context.control.RequestContextController;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.symeda.sormas.api.systemconfiguration.CronJobRunNowResult;
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
		verify(timerService, times(CronJob.active().size())).createCalendarTimer(expressions.capture(), configs.capture());

		assertEquals(
			CronJob.active().stream().collect(Collectors.toSet()),
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
		verify(timerService, times(CronJob.active().size())).createCalendarTimer(any(), configs.capture());
		assertEquals(
			CronJob.active().stream().collect(Collectors.toSet()),
			configs.getAllValues().stream().map(TimerConfig::getInfo).collect(Collectors.toSet()));
	}

	@Test
	public void createsNoTimerForAJobDisabledByAnEmptyValue() {

		TimerService timerService = MockProducer.getTimerService();
		setConfigurationValue(CronJob.ARCHIVE_CASES, "");
		reset(timerService);

		getBean(CronScheduler.class).scheduleAllJobs();

		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(timerService, times(CronJob.active().size() - 1)).createCalendarTimer(any(), configs.capture());
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
		verify(timerService, times(CronJob.active().size())).createCalendarTimer(expressions.capture(), configs.capture());

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

	@Test
	public void aFailedBulkCancellationAtStartupSchedulesNothing() {

		TimerService timerService = MockProducer.getTimerService();
		org.mockito.Mockito.when(timerService.getTimers()).thenThrow(new IllegalStateException("timer store unavailable"));

		assertDoesNotThrow(() -> getBean(CronScheduler.class).scheduleAllJobs());

		verify(timerService, never()).createCalendarTimer(any(), any());

		reset(timerService);
	}

	@Test
	public void aFailedRescheduleKeepsTheExistingTimer() {

		TimerService timerService = MockProducer.getTimerService();
		reset(timerService);

		javax.ejb.Timer existingTimer = org.mockito.Mockito.mock(javax.ejb.Timer.class);
		org.mockito.Mockito.when(existingTimer.getInfo()).thenReturn(CronJob.ARCHIVE_CASES);
		org.mockito.Mockito.when(timerService.getTimers()).thenReturn(Arrays.asList(existingTimer));
		org.mockito.Mockito.doThrow(new IllegalStateException("cannot create timer")).when(timerService).createCalendarTimer(any(), any());

		getBean(CronScheduler.class).reschedule(CronJob.ARCHIVE_CASES);

		verify(existingTimer, never()).cancel();
	}

	@Test
	public void runNowQueuesAOneOffNonPersistentTimer() {

		assertEquals(CronJobRunNowResult.QUEUED, getBean(CronScheduler.class).runNow(CronJob.ARCHIVE_CASES));

		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(MockProducer.getTimerService()).createSingleActionTimer(anyLong(), configs.capture());
		assertEquals(new ManualCronJobRun(CronJob.ARCHIVE_CASES), configs.getValue().getInfo());
		assertFalse(configs.getValue().isPersistent());
	}

	@Test
	public void aSecondRunNowWhileQueuedIsIgnored() {

		CronScheduler scheduler = getBean(CronScheduler.class);

		assertEquals(CronJobRunNowResult.QUEUED, scheduler.runNow(CronJob.ARCHIVE_CASES));
		assertEquals(CronJobRunNowResult.ALREADY_QUEUED, scheduler.runNow(CronJob.ARCHIVE_CASES));

		verify(MockProducer.getTimerService(), times(1)).createSingleActionTimer(anyLong(), any());
	}

	@Test
	public void aManualRunIsRecordedAndReleasesTheQueuedFlag() {

		CronScheduler scheduler = getBean(CronScheduler.class);
		Date beforeRun = new Date();

		scheduler.runNow(CronJob.ARCHIVE_CASES);
		scheduler.executeJob(manualTimer(CronJob.ARCHIVE_CASES));

		CronJobRun recorded = getBean(CronJobRunRepository.class).findLatest(CronJob.ARCHIVE_CASES).orElseThrow();
		assertEquals(CronJobRunOutcome.SUCCESS, recorded.getOutcome());
		assertFalse(recorded.getStart().before(beforeRun));
		assertEquals(CronJobRunNowResult.QUEUED, scheduler.runNow(CronJob.ARCHIVE_CASES));
	}

	@Test
	public void aFailedManualRunAllowsTheNextRunNow() {

		Mockito.doThrow(new RuntimeException("boom")).when(cronService).archiveCases();
		CronScheduler scheduler = getBean(CronScheduler.class);

		scheduler.runNow(CronJob.ARCHIVE_CASES);
		scheduler.executeJob(manualTimer(CronJob.ARCHIVE_CASES));

		assertEquals(CronJobRunOutcome.FAILED, getBean(CronJobRunRepository.class).findLatest(CronJob.ARCHIVE_CASES).orElseThrow().getOutcome());
		assertEquals(CronJobRunNowResult.QUEUED, scheduler.runNow(CronJob.ARCHIVE_CASES));
	}

	@Test
	public void aFailedTimerCreationReleasesTheQueuedFlag() {

		TimerService timerService = MockProducer.getTimerService();
		CronScheduler scheduler = getBean(CronScheduler.class);
		when(timerService.createSingleActionTimer(anyLong(), any())).thenThrow(new IllegalStateException());

		assertThrows(IllegalStateException.class, () -> scheduler.runNow(CronJob.ARCHIVE_CASES));

		reset(timerService);
		assertEquals(CronJobRunNowResult.QUEUED, scheduler.runNow(CronJob.ARCHIVE_CASES));
	}

	@Test
	public void aManualRunWaitsForTheRunningScheduledRun() throws InterruptedException {

		CountDownLatch entered = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		blockArchiveCases(entered, release);
		Timer timer = timerFiring(CronJob.ARCHIVE_CASES, new ManualCronJobRun(CronJob.ARCHIVE_CASES));
		Thread scheduledRun = executeInBackground(timer);
		Thread manualRun = executeInBackground(timer);

		try {
			scheduledRun.start();
			assertTrue(entered.await(5, TimeUnit.SECONDS));
			manualRun.start();
			Thread.sleep(200);
			verify(cronService, times(1)).archiveCases();
		} finally {
			release.countDown();
			scheduledRun.join(5000);
			manualRun.join(5000);
		}

		assertFalse(scheduledRun.isAlive());
		assertFalse(manualRun.isAlive());
		verify(cronService, times(2)).archiveCases();
	}

	@Test
	public void aScheduledRunIsSkippedWhileTheJobRuns() throws InterruptedException {

		CountDownLatch entered = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		blockArchiveCases(entered, release);
		Timer timer = timerFiring(CronJob.ARCHIVE_CASES);
		Thread firstRun = executeInBackground(timer);
		Thread collidingRun = executeInBackground(timer);

		try {
			firstRun.start();
			assertTrue(entered.await(5, TimeUnit.SECONDS));
			collidingRun.start();
			collidingRun.join(5000);
			assertFalse(collidingRun.isAlive());
		} finally {
			release.countDown();
			firstRun.join(5000);
			collidingRun.join(5000);
		}

		assertFalse(firstRun.isAlive());
		verify(cronService, times(1)).archiveCases();
	}

	@Test
	public void differentJobsDoNotBlockEachOther() throws InterruptedException {

		CountDownLatch entered = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		blockArchiveCases(entered, release);
		Timer timer = timerFiring(CronJob.ARCHIVE_CASES, CronJob.ARCHIVE_EVENTS);
		Thread archiveCasesRun = executeInBackground(timer);

		try {
			archiveCasesRun.start();
			assertTrue(entered.await(5, TimeUnit.SECONDS));
			getBean(CronScheduler.class).executeJob(timer);
			verify(cronService).archiveEvents();
		} finally {
			release.countDown();
			archiveCasesRun.join(5000);
		}

		assertFalse(archiveCasesRun.isAlive());
	}

	@Test
	public void aQueuedManualRunSurvivesRescheduling() {

		TimerService timerService = MockProducer.getTimerService();
		Timer manualTimer = manualTimer(CronJob.ARCHIVE_CASES);
		when(timerService.createSingleActionTimer(anyLong(), any())).thenReturn(manualTimer);
		CronScheduler scheduler = getBean(CronScheduler.class);

		assertEquals(CronJobRunNowResult.QUEUED, scheduler.runNow(CronJob.ARCHIVE_CASES));
		when(timerService.getTimers()).thenReturn(List.of(manualTimer));
		scheduler.reschedule(CronJob.ARCHIVE_CASES);

		verify(manualTimer, never()).cancel();
	}

	@Test
	public void aDisabledJobCanStillRunNow() {

		setConfigurationValue(CronJob.ARCHIVE_CASES, "");
		CronScheduler scheduler = getBean(CronScheduler.class);

		assertEquals(CronJobRunNowResult.QUEUED, scheduler.runNow(CronJob.ARCHIVE_CASES));
		scheduler.executeJob(manualTimer(CronJob.ARCHIVE_CASES));

		verify(cronService).archiveCases();
	}

	@Test
	public void aRetiredJobIsNeitherScheduledNorRunNow() {

		CronScheduler scheduler = getBean(CronScheduler.class);
		reset(MockProducer.getTimerService());

		scheduler.scheduleAllJobs();
		scheduler.reschedule(CronJob.SOFT_DELETE_OLD_NEGATIVE_SAMPLES);

		verify(MockProducer.getTimerService(), never())
			.createCalendarTimer(any(), argThat(config -> CronJob.SOFT_DELETE_OLD_NEGATIVE_SAMPLES.equals(config.getInfo())));
		assertThrows(IllegalArgumentException.class, () -> scheduler.runNow(CronJob.SOFT_DELETE_OLD_NEGATIVE_SAMPLES));
		verify(MockProducer.getTimerService(), never()).createSingleActionTimer(anyLong(), any());
	}

	private Timer manualTimer(CronJob job) {
		return timerFiring(new ManualCronJobRun(job));
	}

	// cdi-test allows one mock per type per test, so one timer answers in turn
	private Timer timerFiring(Serializable firstInfo, Serializable... laterInfos) {
		Timer timer = mock(Timer.class);
		when(timer.getInfo()).thenReturn(firstInfo, laterInfos);
		return timer;
	}

	private Thread executeInBackground(Timer timer) {
		CronScheduler scheduler = getBean(CronScheduler.class);
		RequestContextController requestContext = getBean(RequestContextController.class);
		return new Thread(() -> {
			requestContext.activate();
			try {
				scheduler.executeJob(timer);
			} finally {
				requestContext.deactivate();
			}
		});
	}

	private void blockArchiveCases(CountDownLatch entered, CountDownLatch release) {
		Mockito.doAnswer(invocation -> {
			entered.countDown();
			if (!release.await(5, TimeUnit.SECONDS)) {
				throw new IllegalStateException("The blocked job was never released");
			}
			return null;
		}).when(cronService).archiveCases();
	}
}
