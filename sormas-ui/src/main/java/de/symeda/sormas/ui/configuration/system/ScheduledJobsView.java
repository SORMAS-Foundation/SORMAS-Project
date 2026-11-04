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

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;
import de.symeda.sormas.api.systemconfiguration.CronJobFacade;
import de.symeda.sormas.api.systemconfiguration.CronJobRunNowResult;
import de.symeda.sormas.api.systemconfiguration.CronJobStatusDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ScheduledJobsView extends AbstractConfigurationView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/scheduledJobs";

	private static final int RUN_NOW_POPUP_WIDTH = 480;

	private final ScheduledJobsGrid grid;

	public ScheduledJobsView() {

		super(VIEW_NAME);

		grid = new ScheduledJobsGrid(this::openScheduleEditor, this::confirmRunNow);

		HorizontalLayout toolbar = new HorizontalLayout();
		TextField searchField = new TextField();
		searchField.setPlaceholder(I18nProperties.getCaption(Captions.cronJobSearchPlaceholder));
		searchField.setWidth(300, Unit.PIXELS);
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> grid.setSearchText(event.getValue()));
		toolbar.addComponent(searchField);
		toolbar.addComponent(ButtonHelper.createButton(Captions.cronJobRefresh, event -> grid.reload()));

		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(toolbar);
		layout.addComponent(grid);
		layout.setMargin(true);
		layout.setSpacing(false);
		layout.setExpandRatio(grid, 1);
		layout.setSizeFull();
		layout.setStyleName("crud-main-layout");

		addComponent(layout);
	}

	private void openScheduleEditor(CronJobStatusDto status) {

		CronExpressionField field = new CronExpressionField();
		field.setValue(status.getExpression());

		VaadinUiUtil.showConfirmationPopup(
			status.getJobName(),
			field,
			I18nProperties.getCaption(Captions.actionConfirm),
			I18nProperties.getCaption(Captions.actionCancel),
			680,
			confirmed -> {
				if (!Boolean.TRUE.equals(confirmed)) {
					return true;
				}
				if (!field.isExpressionValid()) {
					return false;
				}
				saveScheduleExpression(status, field.getValue());
				grid.reload();
				return true;
			});
	}

	private void confirmRunNow(CronJobStatusDto status) {

		Label message = new Label(String.format(I18nProperties.getString(Strings.confirmationCronJobRunNow), status.getJobName()));
		message.setWidthFull();

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.cronJobRunNow),
			message,
			I18nProperties.getCaption(Captions.actionConfirm),
			I18nProperties.getCaption(Captions.actionCancel),
			RUN_NOW_POPUP_WIDTH,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					runNow(status);
				}
			});
	}

	private void runNow(CronJobStatusDto status) {

		CronJobRunNowResult result = FacadeProvider.getCronJobFacade().executeJobNow(status.getJobName());
		String messageKey = result == CronJobRunNowResult.QUEUED ? Strings.messageCronJobRunQueued : Strings.messageCronJobRunAlreadyQueued;
		Notification.show(I18nProperties.getString(messageKey), Notification.Type.TRAY_NOTIFICATION);
		grid.reload();
	}

	private void saveScheduleExpression(CronJobStatusDto status, String expression) {

		SystemConfigurationValueFacade facade = FacadeProvider.getSystemConfigurationValueFacade();
		SystemConfigurationValueDto value =
			status.getConfigValueUuid() != null ? facade.getByUuid(status.getConfigValueUuid()) : buildConfigurationValue(status);
		value.setValue(expression);
		facade.save(value);
	}

	private SystemConfigurationValueDto buildConfigurationValue(CronJobStatusDto status) {

		SystemConfigurationValueDto value = SystemConfigurationValueDto.build();
		value.setKey(status.getConfigKey());
		value.setCategory(
			FacadeProvider.getSystemConfigurationCategoryFacade().getCategoryReferenceDtoByName(CronJobFacade.CRON_CONFIGURATION_CATEGORY));
		value.setOptional(true);
		value.setEncrypt(false);
		value.setPattern(CronExpressionValidator.VALUE_PATTERN);
		return value;
	}
}
