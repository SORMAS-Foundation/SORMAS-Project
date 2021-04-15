/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.task;

import java.util.Collections;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class TasksView extends AbstractView {

	public static final String VIEW_NAME = "tasks";

	private final TaskGridComponent taskListComponent;
	private ViewConfiguration viewConfiguration;

	public TasksView() {

		super(VIEW_NAME);

		if (!ViewModelProviders.of(TasksView.class).has(TaskCriteria.class)) {
			// init default filter
			TaskCriteria taskCriteria = new TaskCriteria();
			taskCriteria.taskStatus(TaskStatus.PENDING);
			ViewModelProviders.of(TasksView.class).get(TaskCriteria.class, taskCriteria);
		}

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		taskListComponent = new TaskGridComponent(getViewTitleLabel(), this);
		addComponent(taskListComponent);

		if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_EXPORT)) {
			Button basicExportButton = ButtonHelper.createIconButton(Captions.exportBasic, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			basicExportButton.setDescription(I18nProperties.getString(Strings.infoBasicExport));
			addHeaderComponent(basicExportButton);

			StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
				taskListComponent.getGrid(),
				() -> viewConfiguration.isInEagerMode() ? taskListComponent.getGrid().asMultiSelect().getSelectedItems() : Collections.emptySet(),
				ExportEntityName.TASKS,
				TaskGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(basicExportButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());

			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());

			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				taskListComponent.getBulkOperationsDropdown().setVisible(true);
				ViewModelProviders.of(TasksView.class).get(ViewConfiguration.class).setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				taskListComponent.getGrid().reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				taskListComponent.getBulkOperationsDropdown().setVisible(false);
				ViewModelProviders.of(TasksView.class).get(ViewConfiguration.class).setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				navigateTo(taskListComponent.getCriteria());
			});
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE)) {
			Button createButton = ButtonHelper.createIconButton(
				Captions.taskNewTask,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getTaskController().create(TaskContext.GENERAL, null, taskListComponent.getGrid()::reload),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		taskListComponent.reload(event);
	}

	public ViewConfiguration getViewConfiguration() {
		return viewConfiguration;
	}
}
