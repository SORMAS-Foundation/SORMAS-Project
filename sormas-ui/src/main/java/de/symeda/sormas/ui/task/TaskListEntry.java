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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class TaskListEntry extends HorizontalLayout {

	private final TaskIndexDto task;
	private Button editButton;

	public TaskListEntry(TaskIndexDto task) {
		
		this.task = task;

		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(topLayout);
		setExpandRatio(topLayout, 1);
		
		// TOP LEFT
		VerticalLayout topLeftLayout = new VerticalLayout();
				
		topLeftLayout.setMargin(false);
		topLeftLayout.setSpacing(false);
	
		Label taskTypeLabel = new Label(DataHelper.toStringNullable(task.getTaskType()));
		CssStyles.style(taskTypeLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		topLeftLayout.addComponent(taskTypeLabel);
		
		Label suggestedStartLabel = new Label(I18nProperties.getPrefixCaption(TaskDto.I18N_PREFIX, TaskDto.SUGGESTED_START)
				+ ": " + DateHelper.formatLocalShortDate(task.getSuggestedStart()));
		topLeftLayout.addComponent(suggestedStartLabel);
		
		Label dueDateLabel = new Label(I18nProperties.getPrefixCaption(TaskDto.I18N_PREFIX, TaskDto.DUE_DATE)
				+ ": " + DateHelper.formatLocalShortDate(task.getDueDate()));
		topLeftLayout.addComponent(dueDateLabel);		
		
		topLayout.addComponent(topLeftLayout);

		// TOP RIGHT
		VerticalLayout topRightLayout = new VerticalLayout();

		topRightLayout.addStyleName(CssStyles.ALIGN_RIGHT);
		topRightLayout.setMargin(false);
		topRightLayout.setSpacing(false);
		
		Label statusLabel = new Label(DataHelper.toStringNullable(task.getTaskStatus()));
		CssStyles.style(statusLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		topRightLayout.addComponent(statusLabel);
		
		Label priorityLabel = new Label(DataHelper.toStringNullable(I18nProperties.getPrefixCaption(TaskDto.I18N_PREFIX, TaskDto.PRIORITY) + ": " + task.getPriority()));
		if (TaskPriority.HIGH == task.getPriority()) {
			priorityLabel.addStyleName(CssStyles.LABEL_IMPORTANT);
		} else if (TaskPriority.NORMAL == task.getPriority()) {
			priorityLabel.addStyleName(CssStyles.LABEL_NEUTRAL);				
		}
		topRightLayout.addComponent(priorityLabel);
		
		Label userLabel = new Label(I18nProperties.getPrefixCaption(TaskDto.I18N_PREFIX, TaskDto.ASSIGNEE_USER) + ": "
				+ task.getAssigneeUser().getCaption());
		topRightLayout.addComponent(userLabel);

		topLayout.addComponent(topRightLayout);
		topLayout.setComponentAlignment(topRightLayout, Alignment.TOP_RIGHT);

		String statusStyle;
		switch (task.getTaskStatus()) {
		case DONE:
			statusStyle = CssStyles.LABEL_DONE;
			break;
		case NOT_EXECUTABLE:
			statusStyle = CssStyles.LABEL_NOT;
			break;
		case REMOVED:
			statusStyle = CssStyles.LABEL_DISCARDED;
			break;
		default:
			statusStyle = null;
		}

		if (statusStyle != null) {
			taskTypeLabel.addStyleName(statusStyle);
			suggestedStartLabel.addStyleName(statusStyle);
			dueDateLabel.addStyleName(statusStyle);
			statusLabel.addStyleName(statusStyle);
			priorityLabel.addStyleName(statusStyle);
			userLabel.addStyleName(statusStyle);
		}
	}

	public void addEditListener(int rowIndex, ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption("edit-task-" + rowIndex, null, VaadinIcons.PENCIL, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public TaskIndexDto getTask() {
		return task;
	}
}
