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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.systemconfiguration.CronJobStatusDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ScheduledJobsView extends AbstractConfigurationView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/scheduledJobs";

	private final ScheduledJobsGrid grid;

	public ScheduledJobsView() {

		super(VIEW_NAME);

		grid = new ScheduledJobsGrid(this::openScheduleEditor);

		HorizontalLayout toolbar = new HorizontalLayout();
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

		if (status.getConfigValueUuid() == null) {
			return;
		}

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
				SystemConfigurationValueDto value = FacadeProvider.getSystemConfigurationValueFacade().getByUuid(status.getConfigValueUuid());
				value.setValue(field.getValue());
				FacadeProvider.getSystemConfigurationValueFacade().save(value);
				grid.reload();
				return true;
			});
	}
}
