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

package de.symeda.sormas.api.systemconfiguration;

import java.io.Serializable;
import java.util.Date;

public class CronJobStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String jobName;
	private String configKey;
	private String configValueUuid;
	private String descriptionKey;
	private String expression;
	private String defaultExpression;
	private boolean enabled;
	private boolean expressionValid;
	private Date nextFireTime;
	private Date lastRunStart;
	private Long lastRunDurationMillis;
	private CronJobRunOutcome lastRunOutcome;
	private String lastRunFailureMessage;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigValueUuid() {
		return configValueUuid;
	}

	public void setConfigValueUuid(String configValueUuid) {
		this.configValueUuid = configValueUuid;
	}

	public String getDescriptionKey() {
		return descriptionKey;
	}

	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getDefaultExpression() {
		return defaultExpression;
	}

	public void setDefaultExpression(String defaultExpression) {
		this.defaultExpression = defaultExpression;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isExpressionValid() {
		return expressionValid;
	}

	public void setExpressionValid(boolean expressionValid) {
		this.expressionValid = expressionValid;
	}

	public Date getNextFireTime() {
		return nextFireTime == null ? null : new Date(nextFireTime.getTime());
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime == null ? null : new Date(nextFireTime.getTime());
	}

	public Date getLastRunStart() {
		return lastRunStart == null ? null : new Date(lastRunStart.getTime());
	}

	public void setLastRunStart(Date lastRunStart) {
		this.lastRunStart = lastRunStart == null ? null : new Date(lastRunStart.getTime());
	}

	public Long getLastRunDurationMillis() {
		return lastRunDurationMillis;
	}

	public void setLastRunDurationMillis(Long lastRunDurationMillis) {
		this.lastRunDurationMillis = lastRunDurationMillis;
	}

	public CronJobRunOutcome getLastRunOutcome() {
		return lastRunOutcome;
	}

	public void setLastRunOutcome(CronJobRunOutcome lastRunOutcome) {
		this.lastRunOutcome = lastRunOutcome;
	}

	public String getLastRunFailureMessage() {
		return lastRunFailureMessage;
	}

	public void setLastRunFailureMessage(String lastRunFailureMessage) {
		this.lastRunFailureMessage = lastRunFailureMessage;
	}
}
