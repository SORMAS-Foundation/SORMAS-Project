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

import java.util.function.Consumer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

@SuppressWarnings("serial")
public class TaskListComponent extends SideComponent {

	private TaskList list;

	public TaskListComponent(TaskContext context, ReferenceDto entityRef, Disease disease, Consumer<Runnable> actionCallback, boolean isEditAllowed) {

		super(I18nProperties.getString(Strings.entityTasks), actionCallback);

		list = new TaskList(context, entityRef, isEditAllowed);
		addComponent(list);
		list.reload();

		if (UiUtil.permitted(isEditAllowed, UserRight.TASK_CREATE)) {
			addCreateButton(
				I18nProperties.getCaption(Captions.taskNewTask),
				() -> ControllerProvider.getTaskController().create(context, entityRef, disease, this::reload),
				UserRight.TASK_CREATE);
		}
	}

	public void reload() {
		list.reload();
	}
}
