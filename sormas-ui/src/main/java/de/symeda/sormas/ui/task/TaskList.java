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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.task;

import java.util.List;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class TaskList extends PaginationList<TaskIndexDto> {

	private final TaskCriteria taskCriteria = new TaskCriteria();
	private final TaskContext context;

	public TaskList(TaskContext context, ReferenceDto entityRef) {
		super(5);
		this.context = context;

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
			default:
				throw new IndexOutOfBoundsException(context.toString());
		}
	}

	@Override
	public void reload() {
		List<TaskIndexDto> tasks = FacadeProvider.getTaskFacade()
				.getIndexList(taskCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(tasks);
		if (!tasks.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noTasksLabel = new Label(String.format(I18nProperties.getCaption(Captions.taskNoTasks), context.toString()));
			listLayout.addComponent(noTasksLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<TaskIndexDto> displayedEntries = getDisplayedEntries();

		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			TaskIndexDto task = displayedEntries.get(i);
			TaskListEntry listEntry = new TaskListEntry(task);
			if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_EDIT)) {
				listEntry.addEditListener(i, (ClickListener) event -> ControllerProvider.getTaskController().edit(listEntry.getTask(), TaskList.this::reload));
			}
			listLayout.addComponent(listEntry);
		}
	}
}
