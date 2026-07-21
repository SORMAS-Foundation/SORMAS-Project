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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryReferenceDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;

public class CronSchedulerTest extends AbstractBeanTest {

	private static final String CRON_CATEGORY_NAME = "CRON";

	@BeforeEach
	public void resetTimerService() {
		getBean(CronScheduler.class).scheduleAllJobs();
		reset(MockProducer.getTimerService());
	}

	private SystemConfigurationValueDto setConfigurationValue(CronJob job, String expression) {

		SystemConfigurationValueFacade configurationValues = getBean(SystemConfigurationValueFacade.class);
		List<String> existingUuids = configurationValues.getAllUuids();
		SystemConfigurationValueDto value = existingUuids.isEmpty()
			? buildConfigurationValue(job)
			: configurationValues.getByUuids(existingUuids)
				.stream()
				.filter(candidate -> job.getConfigKey().equals(candidate.getKey()))
				.findFirst()
				.orElseGet(() -> buildConfigurationValue(job));
		value.setValue(expression);
		return configurationValues.save(value);
	}

	private SystemConfigurationValueDto buildConfigurationValue(CronJob job) {

		SystemConfigurationValueDto value = SystemConfigurationValueDto.build();
		value.setKey(job.getConfigKey());
		value.setCategory(cronCategoryReference());
		value.setOptional(true);
		value.setEncrypt(false);
		value.setPattern(CronExpressionParser.VALUE_PATTERN);
		return value;
	}

	private SystemConfigurationCategoryReferenceDto cronCategoryReference() {

		SystemConfigurationCategoryFacade categories = getSystemConfigurationCategoryFacade();
		SystemConfigurationCategoryReferenceDto existing = categories.getCategoryReferenceDtoByName(CRON_CATEGORY_NAME);
		if (existing != null) {
			return existing;
		}

		SystemConfigurationCategoryDto category = SystemConfigurationCategoryDto.build();
		category.setName(CRON_CATEGORY_NAME);
		category.setCaption("Scheduled jobs");
		return categories.getReferenceByUuid(categories.save(category).getUuid());
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
	public void createsNoTimerForAJobDisabledByAnEmptyValue() {

		TimerService timerService = MockProducer.getTimerService();
		setConfigurationValue(CronJob.ARCHIVE_CASES, "");
		reset(timerService);

		getBean(CronScheduler.class).scheduleAllJobs();

		ArgumentCaptor<TimerConfig> configs = ArgumentCaptor.forClass(TimerConfig.class);
		verify(timerService, times(CronJob.values().length - 1)).createCalendarTimer(any(), configs.capture());
		assertTrue(configs.getAllValues().stream().noneMatch(config -> CronJob.ARCHIVE_CASES.equals(config.getInfo())));

		setConfigurationValue(CronJob.ARCHIVE_CASES, CronJob.ARCHIVE_CASES.getDefaultExpression());
	}

	@Test
	public void theDispatcherSwallowsJobFailures() {

		javax.ejb.Timer timer = org.mockito.Mockito.mock(javax.ejb.Timer.class);
		org.mockito.Mockito.when(timer.getInfo()).thenReturn(CronJob.ARCHIVE_CASES);

		assertDoesNotThrow(() -> getBean(CronScheduler.class).executeJob(timer));
	}
}
