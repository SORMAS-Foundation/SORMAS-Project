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

package de.symeda.sormas.ui.configuration.system;

import java.util.Comparator;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.systemconfiguration.CronJobRunOutcome;
import de.symeda.sormas.api.systemconfiguration.CronJobStatusDto;

public class ScheduledJobsGrid extends Grid<CronJobStatusDto> {

	private static final long serialVersionUID = 1L;

	public ScheduledJobsGrid() {

		super(CronJobStatusDto.class);
		setSizeFull();
		removeAllColumns();

		addColumn(CronJobStatusDto::getJobName).setId("jobName").setCaption(I18nProperties.getCaption(Captions.cronJobName));
		addColumn(this::describeJob).setId("description").setCaption(I18nProperties.getCaption(Captions.cronJobDescription));
		addColumn(this::describeSchedule).setId("schedule").setCaption(I18nProperties.getCaption(Captions.cronJobSchedule));
		addColumn(this::describeStatus).setId("status").setCaption(I18nProperties.getCaption(Captions.cronJobEnabledStatus));
		addColumn(CronJobStatusDto::getNextFireTime, new DateRenderer("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", ""))
			.setId("nextFireTime")
			.setCaption(I18nProperties.getCaption(Captions.cronJobNextRun));
		addColumn(this::describeLastRun).setId("lastRun").setCaption(I18nProperties.getCaption(Captions.cronJobLastRun));
		addColumn(this::describeDuration).setId("duration").setCaption(I18nProperties.getCaption(Captions.cronJobDuration));
		addColumn(this::describeOutcome).setId("outcome").setCaption(I18nProperties.getCaption(Captions.cronJobOutcome));

		setDescriptionGenerator(this::describeRow);
		reload();
	}

	public void reload() {

		List<CronJobStatusDto> statuses = FacadeProvider.getCronJobFacade().getAllJobStatuses();
		statuses.sort(Comparator.comparing(CronJobStatusDto::getNextFireTime, Comparator.nullsLast(Comparator.naturalOrder())));
		setItems(statuses);
	}

	private String describeJob(CronJobStatusDto status) {

		if (status.getDescriptionKey() == null || status.getDescriptionKey().isEmpty()) {
			return "";
		}
		StringBuilder description = new StringBuilder();
		SystemConfigurationI18nHelper.processI18nString(status.getDescriptionKey(), key -> description.append(I18nProperties.getString(key)));
		return description.toString();
	}

	private String describeSchedule(CronJobStatusDto status) {
		return status.isEnabled() ? status.getExpression() : I18nProperties.getCaption(Captions.cronJobDisabled);
	}

	private String describeStatus(CronJobStatusDto status) {

		if (!status.isExpressionValid()) {
			return I18nProperties.getCaption(Captions.cronJobInvalid);
		}
		return status.isEnabled()
			? I18nProperties.getCaption(Captions.cronJobEnabledStatus)
			: I18nProperties.getCaption(Captions.cronJobDisabled);
	}

	private String describeLastRun(CronJobStatusDto status) {
		return status.getLastRunStart() == null
			? I18nProperties.getCaption(Captions.cronJobNotRunSinceRestart)
			: String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", status.getLastRunStart());
	}

	private String describeDuration(CronJobStatusDto status) {
		return status.getLastRunDurationMillis() == null ? "—" : String.format("%.2f s", status.getLastRunDurationMillis() / 1000.0);
	}

	private String describeOutcome(CronJobStatusDto status) {

		CronJobRunOutcome outcome = status.getLastRunOutcome();
		return outcome == null ? "—" : outcome.name();
	}

	private String describeRow(CronJobStatusDto status) {

		if (!status.isExpressionValid()) {
			return status.getExpression() + " → " + status.getDefaultExpression();
		}
		return status.getLastRunFailureMessage();
	}
}
