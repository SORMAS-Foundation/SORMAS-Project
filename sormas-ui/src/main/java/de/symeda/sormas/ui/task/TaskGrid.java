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

import java.util.Date;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.jurisdiction.CaseJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.EventJurisdictionHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ReferenceDtoHtmlProvider;
import de.symeda.sormas.ui.utils.ShortStringRenderer;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class TaskGrid extends FilteredGrid<TaskIndexDto, TaskCriteria> {

	@SuppressWarnings("unchecked")
	public TaskGrid(TaskCriteria criteria) {
		super(TaskIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(TasksView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		addEditColumn(e -> ControllerProvider.getTaskController().edit(e, this::reload, true));

		setStyleGenerator(item -> {
			if (item != null && item.getTaskStatus() != null) {
				switch (item.getTaskStatus()) {
				case REMOVED:
					return CssStyles.GRID_ROW_STATUS_DISCARDED;
				case NOT_EXECUTABLE:
					return CssStyles.GRID_ROW_STATUS_NOT;
				case DONE:
					return CssStyles.GRID_ROW_STATUS_DONE;
				case PENDING:
					return CssStyles.GRID_ROW_STATUS_PENDING;
				default:
					throw new IndexOutOfBoundsException(item.getTaskStatus().toString());
				}
			}
			return null;
		});

		setColumns(
			EDIT_BTN_ID,
			TaskIndexDto.CONTEXT_REFERENCE,
			TaskIndexDto.TASK_CONTEXT,
			TaskIndexDto.TASK_TYPE,
			TaskIndexDto.REGION,
			TaskIndexDto.DISTRICT,
			TaskIndexDto.PRIORITY,
			TaskIndexDto.SUGGESTED_START,
			TaskIndexDto.DUE_DATE,
			TaskIndexDto.ASSIGNEE_USER,
			TaskIndexDto.ASSIGNEE_REPLY,
			TaskIndexDto.CREATOR_USER,
			TaskIndexDto.CREATOR_COMMENT,
			TaskIndexDto.TASK_STATUS);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<TaskIndexDto, Date>) getColumn(TaskIndexDto.DUE_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<TaskIndexDto, Date>) getColumn(TaskIndexDto.SUGGESTED_START))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<TaskIndexDto, String>) getColumn(TaskIndexDto.CREATOR_COMMENT)).setRenderer(new ShortStringRenderer(50));

		Column<TaskIndexDto, ReferenceDto> contextColumn = (Column<TaskIndexDto, ReferenceDto>) getColumn(TaskIndexDto.CONTEXT_REFERENCE);
		contextColumn.setRenderer(new ReferenceDtoHtmlProvider(), new HtmlRenderer());
		contextColumn.setSortable(false);

		Column<TaskIndexDto, UserReferenceDto> assigneeUserColumn = (Column<TaskIndexDto, UserReferenceDto>) getColumn(TaskIndexDto.ASSIGNEE_USER);
		assigneeUserColumn.setRenderer(user -> {
			String text;
			if (user != null) {
				text = user.getCaption();
			} else {
				text = "";
			}
			return text;
		}, new TextRenderer());

		Column<TaskIndexDto, TaskPriority> priorityColumn = (Column<TaskIndexDto, TaskPriority>) getColumn(TaskIndexDto.PRIORITY);
		priorityColumn.setStyleGenerator(item -> {
			if (item.getPriority() != null) {
				switch (item.getPriority()) {
				case HIGH:
					return CssStyles.GRID_CELL_PRIORITY_HIGH;
				case NORMAL:
					return CssStyles.GRID_CELL_PRIORITY_NORMAL;
				case LOW:
					return CssStyles.GRID_CELL_PRIORITY_LOW;
				default:
					throw new IndexOutOfBoundsException(item.getPriority().toString());
				}
			}
			return null;
		});

		Column<TaskIndexDto, Date> dueDateColumn = (Column<TaskIndexDto, Date>) getColumn(TaskIndexDto.DUE_DATE);
		dueDateColumn.setStyleGenerator(item -> {
			Date dueDate = item.getDueDate();
			if (dueDate != null && dueDate.before(new Date())) {
				return CssStyles.GRID_CELL_WARNING;
			}
			return null;
		});

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(TaskIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		getColumn(TaskIndexDto.CONTEXT_REFERENCE).setStyleGenerator(new FieldAccessColumnStyleGenerator<>(task -> {

			UserDto currentUser = UserProvider.getCurrent().getUser();
			boolean isInJurisdiction = true;
			switch (task.getTaskContext()) {
			case CASE:
				isInJurisdiction = FieldAccessColumnStyleGenerator.callJurisdictionChecker(
					CaseJurisdictionHelper::isInJurisdictionOrOwned,
					currentUser,
					task.getJurisdiction().getCaseJurisdiction());
				break;
			case CONTACT:
				isInJurisdiction = FieldAccessColumnStyleGenerator.callJurisdictionChecker(
					ContactJurisdictionHelper::isInJurisdictionOrOwned,
					currentUser,
					task.getJurisdiction().getContactJurisdiction());
				break;
			case EVENT:
				isInJurisdiction = FieldAccessColumnStyleGenerator.callJurisdictionChecker(
					EventJurisdictionHelper::isInJurisdictionOrOwned,
					currentUser,
					task.getJurisdiction().getEventJurisdiction());
				break;
			}

			return UiFieldAccessCheckers.getDefault(!isInJurisdiction).hasRight();
		}));

		addItemClickListener(new ShowDetailsListener<>(TaskIndexDto.CONTEXT_REFERENCE, false, e -> navigateToData(e)));
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
	}

	private void navigateToData(TaskIndexDto task) {

		switch (task.getTaskContext()) {
		case CASE:
			ControllerProvider.getCaseController().navigateToCase(task.getCaze().getUuid());
			return;
		case CONTACT:
			ControllerProvider.getContactController().navigateToData(task.getContact().getUuid());
			return;
		case EVENT:
			ControllerProvider.getEventController().navigateToData(task.getEvent().getUuid());
			return;
		case GENERAL:
			return;
		default:
			throw new IndexOutOfBoundsException(task.getTaskContext().toString());
		}
	}

	public void setLazyDataProvider() {
		DataProvider<TaskIndexDto, TaskCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getTaskFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getTaskFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<TaskIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getTaskFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
