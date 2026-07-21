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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.common.CronService;
import de.symeda.sormas.backend.systemconfiguration.event.SystemConfigurationValueChangedEvent;

@Singleton
@Startup
@RunAs(UserRight._SYSTEM)
@DependsOn("SystemConfigurationValueFacade")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class CronScheduler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private TimerService timerService;

	@EJB
	private CronService cronService;

	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	@PostConstruct
	public void scheduleAllJobs() {

		synchronized (this) {
			cancelAllTimers();
			for (CronJob job : CronJob.values()) {
				try {
					scheduleJob(job, resolveExpression(job));
				} catch (Exception e) {
					logger.error("Failed to schedule job {} with configuration key {}", job, job.getConfigKey(), e);
				}
			}
		}
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void executeJob(Timer timer) {

		try {
			CronJob job = (CronJob) timer.getInfo();
			job.execute(cronService);
		} catch (Exception e) {
			logger.error("Scheduled job execution failed for timer info {}", timer.getInfo(), e);
		}
	}

	public void reschedule(CronJob job) {

		String configured = systemConfigurationValueFacade.getValue(job.getConfigKey());
		if (configured != null && !CronExpressionParser.isValid(configured)) {
			logger.error(
				"Configuration key {} holds the invalid cron expression [{}]; the current schedule is kept",
				job.getConfigKey(),
				configured);
			return;
		}

		synchronized (this) {
			cancelTimer(job);
			try {
				scheduleJob(job, configured == null ? job.getDefaultExpression() : configured);
			} catch (Exception e) {
				logger.error("Failed to schedule job {} with configuration key {}", job, job.getConfigKey(), e);
			}
		}
	}

	public void onSystemConfigurationValueChanged(
		@Observes(during = TransactionPhase.AFTER_SUCCESS) SystemConfigurationValueChangedEvent event) {

		String key = event.getKey();
		if (key == null || !key.startsWith(CronJob.CONFIG_KEY_PREFIX)) {
			return;
		}

		try {
			reschedule(CronJob.valueOf(key.substring(CronJob.CONFIG_KEY_PREFIX.length())));
		} catch (IllegalArgumentException e) {
			logger.warn("Ignoring a change to the unknown cron configuration key {}", key);
		}
	}

	private String resolveExpression(CronJob job) {

		String configured = systemConfigurationValueFacade.getValue(job.getConfigKey());
		if (configured == null) {
			logger.warn("Configuration key {} is missing; using the default [{}]", job.getConfigKey(), job.getDefaultExpression());
			return job.getDefaultExpression();
		}
		if (!CronExpressionParser.isValid(configured)) {
			logger.error(
				"Configuration key {} holds the invalid cron expression [{}]; using the default [{}]",
				job.getConfigKey(),
				configured,
				job.getDefaultExpression());
			return job.getDefaultExpression();
		}
		return configured;
	}

	private void scheduleJob(CronJob job, String expression) {

		if (CronExpressionParser.isDisabled(expression)) {
			logger.warn("Scheduled job {} is disabled by configuration key {}", job, job.getConfigKey());
			return;
		}

		timerService.createCalendarTimer(CronExpressionParser.parse(expression), new TimerConfig(job, false));
		logger.info("Scheduled job {} with expression [{}]", job, expression);
	}

	private void cancelAllTimers() {
		for (Timer timer : timerService.getTimers()) {
			timer.cancel();
		}
	}

	private void cancelTimer(CronJob job) {
		for (Timer timer : timerService.getTimers()) {
			if (job.equals(timer.getInfo())) {
				timer.cancel();
			}
		}
	}
}
