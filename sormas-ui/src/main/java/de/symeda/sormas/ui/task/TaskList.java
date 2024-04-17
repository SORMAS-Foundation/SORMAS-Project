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

import java.util.List;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class TaskList extends PaginationList<TaskIndexDto> {

	private final TaskCriteria taskCriteria = new TaskCriteria();
	private final Label noTasksLabel;
	private final boolean isEditAllowed;

	public TaskList(TaskContext context, ReferenceDto entityRef, boolean isEditAllowed) {

		super(5);
		switch (context) {
		case CASE:
			taskCriteria.caze((CaseReferenceDto) entityRef);
			break;
		case CONTACT:
			taskCriteria.contact((ContactReferenceDto) entityRef);
			break;
		case EVENT:
			taskCriteria.event((EventReferenceDto) entityRef);
			break;
		case TRAVEL_ENTRY:
			taskCriteria.travelEntry((TravelEntryReferenceDto) entityRef);
			break;
		case ENVIRONMENT:
			taskCriteria.environment((EnvironmentReferenceDto) entityRef);
			break;
		default:
			throw new IndexOutOfBoundsException(context.toString());
		}
		noTasksLabel = new Label(String.format(I18nProperties.getCaption(Captions.taskNoTasks), context.toString().toLowerCase()));
		this.isEditAllowed = isEditAllowed;
	}

	@Override
	public void reload() {
		List<TaskIndexDto> tasks = FacadeProvider.getTaskFacade().getIndexList(taskCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(tasks);
		if (!tasks.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noTasksLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {

		List<TaskIndexDto> displayedEntries = getDisplayedEntries();

		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			TaskIndexDto task = displayedEntries.get(i);
			TaskListEntry listEntry = new TaskListEntry(task);

			listEntry.addActionButton(
				String.valueOf(i),
				(ClickListener) event -> ControllerProvider.getTaskController()
					.edit(listEntry.getTask(), TaskList.this::reload, false, listEntry.getTask().getDisease()),
				hasEditOrDeleteRights());

			listEntry.setEnabled(isEditAllowed);
			listLayout.addComponent(listEntry);
		}
	}

	private boolean hasEditOrDeleteRights() {
		return UiUtil.permitted(UserRight.TASK_EDIT) || UiUtil.permitted(UserRight.TASK_DELETE);
	}

}
