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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;
import de.symeda.sormas.api.systemconfiguration.CronJobFacade;
import de.symeda.sormas.api.systemconfiguration.CronJobStatusDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "CronJobFacade")
@RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
public class CronJobFacadeEjb implements CronJobFacade {

	@EJB
	private CronScheduler cronScheduler;

	@EJB
	private CronJobRunRepository cronJobRunRepository;

	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	@Override
	public List<CronJobStatusDto> getAllJobStatuses() {

		Map<CronJob, Date> nextFireTimes = cronScheduler.getNextFireTimes();
		Map<String, SystemConfigurationValueDto> valuesByKey = configurationValuesByKey();
		List<CronJobStatusDto> statuses = new ArrayList<>();

		for (CronJob job : CronJob.values()) {
			statuses.add(toStatus(job, valuesByKey.get(job.getConfigKey()), nextFireTimes.get(job)));
		}
		return statuses;
	}

	private Map<String, SystemConfigurationValueDto> configurationValuesByKey() {

		List<String> uuids = systemConfigurationValueFacade.getAllUuids();
		if (uuids == null || uuids.isEmpty()) {
			return Map.of();
		}
		Map<String, SystemConfigurationValueDto> byKey = new HashMap<>();
		for (SystemConfigurationValueDto value : systemConfigurationValueFacade.getByUuids(uuids)) {
			byKey.put(value.getKey(), value);
		}
		return byKey;
	}

	private CronJobStatusDto toStatus(CronJob job, SystemConfigurationValueDto configured, Date nextFireTime) {

		String expression = configured == null ? job.getDefaultExpression() : configured.getValue();

		CronJobStatusDto status = new CronJobStatusDto();
		status.setJobName(job.name());
		status.setConfigKey(job.getConfigKey());
		status.setConfigValueUuid(configured == null ? null : configured.getUuid());
		status.setDescriptionKey(configured == null ? null : configured.getDescription());
		status.setExpression(expression);
		status.setDefaultExpression(job.getDefaultExpression());
		status.setEnabled(!CronExpressionValidator.isDisabled(expression));
		status.setExpressionValid(CronExpressionValidator.isValid(expression));
		status.setNextFireTime(nextFireTime);

		Optional<CronJobRun> lastRun = cronJobRunRepository.findLatest(job);
		lastRun.ifPresent(run -> {
			status.setLastRunStart(run.getStart());
			status.setLastRunDurationMillis(run.getDurationMillis());
			status.setLastRunOutcome(run.getOutcome());
			status.setLastRunFailureMessage(run.getFailureMessage());
		});
		return status;
	}

	@Stateless
	@LocalBean
	public static class CronJobFacadeEjbLocal extends CronJobFacadeEjb {

	}
}
