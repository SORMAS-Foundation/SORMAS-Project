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

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class TaskListEntry extends HorizontalLayout {

	private final TaskIndexDto task;
	private Button editButton;

	public TaskListEntry(TaskIndexDto task) {

		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		this.task = task;

		HorizontalLayout labelLayout = new HorizontalLayout();
		labelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(labelLayout);
		setExpandRatio(labelLayout, 1);

		// very hacky: clean up when needed elsewher!
		String htmlLeft = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				task.getTaskType().toString())
				+ LayoutUtil.div(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.SUGGESTED_START)
						+ ": " + DateHelper.formatLocalShortDate(task.getSuggestedStart()))
				+ LayoutUtil.div(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.DUE_DATE) + ": "
						+ DateHelper.formatLocalShortDate(task.getDueDate()));
		Label labelLeft = new Label(htmlLeft, ContentMode.HTML);
		labelLayout.addComponent(labelLeft);

		String htmlRight = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				DataHelper.toStringNullable(task.getTaskStatus()))
				+ LayoutUtil.divCss(
						TaskPriority.HIGH == task.getPriority() ? CssStyles.LABEL_IMPORTANT
								: (TaskPriority.NORMAL == task.getPriority() ? CssStyles.LABEL_NEUTRAL : ""),
						I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.PRIORITY) + ": "
								+ DataHelper.toStringNullable(task.getPriority()))
				+ LayoutUtil.div(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.ASSIGNEE_USER) + ": "
						+ task.getAssigneeUser().getCaption());
		Label labelRight = new Label(htmlRight, ContentMode.HTML);
		labelRight.addStyleName(CssStyles.ALIGN_RIGHT);
		labelLayout.addComponent(labelRight);
		labelLayout.setComponentAlignment(labelRight, Alignment.MIDDLE_RIGHT);

		switch (task.getTaskStatus()) {
		case DONE:
			labelLeft.addStyleName(CssStyles.LABEL_DONE);
			labelRight.addStyleName(CssStyles.LABEL_DONE);
			break;
		case NOT_EXECUTABLE:
			labelLeft.addStyleName(CssStyles.LABEL_NOT);
			labelRight.addStyleName(CssStyles.LABEL_NOT);
			break;
		case REMOVED:
			labelLeft.addStyleName(CssStyles.LABEL_DISCARDED);
			labelRight.addStyleName(CssStyles.LABEL_DISCARDED);
			break;
		case PENDING:
			break;
		}
	}

	public void addEditListener(ClickListener editClickListener) {
		if (editButton == null) {
			editButton = new Button(FontAwesome.PENCIL);
			CssStyles.style(editButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_NO_PADDING);
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
