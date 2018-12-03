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

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Not;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.HtmlReferenceDtoConverter;
import de.symeda.sormas.ui.utils.ShortStringRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class TaskGrid extends Grid implements ItemClickListener {

	private static final String EDIT_BTN_ID = "edit";
	
	private final TaskCriteria taskCriteria = new TaskCriteria();

	private final class TaskGridRowStyleGenerator implements RowStyleGenerator {
		@Override
		public String getStyle(RowReference row) {
			TaskIndexDto task = (TaskIndexDto)row.getItemId();
			if (task != null && task.getTaskStatus() != null) {
				switch (task.getTaskStatus()) {
				case REMOVED:
					return CssStyles.GRID_ROW_STATUS_DISCARDED;
				case NOT_EXECUTABLE:
					return CssStyles.GRID_ROW_STATUS_NOT;
				case DONE:
					return CssStyles.GRID_ROW_STATUS_DONE;
				case PENDING:
					return CssStyles.GRID_ROW_STATUS_PENDING;
				default:
					throw new IndexOutOfBoundsException(task.getTaskStatus().toString());
				}
			}
			return null;
		}
	}

	private final class TaskGridCellStyleGenerator implements CellStyleGenerator {
		@Override
		public String getStyle(CellReference cell) {
			if (TaskIndexDto.PRIORITY.equals(cell.getPropertyId())) {
				TaskPriority priority = (TaskPriority)cell.getProperty().getValue();
				if (priority != null) {
				switch (priority) {
				case HIGH:
					return CssStyles.GRID_CELL_PRIORITY_HIGH;
				case NORMAL:
					return CssStyles.GRID_CELL_PRIORITY_NORMAL;
				case LOW:
					return CssStyles.GRID_CELL_PRIORITY_LOW;
				default:
					throw new IndexOutOfBoundsException(priority.toString());
				}
				}
			}
			else if (TaskIndexDto.DUE_DATE.equals(cell.getPropertyId())) {
				Date dueDate = (Date)cell.getProperty().getValue();
				if (dueDate != null && dueDate.before(new Date())) {
					return CssStyles.GRID_CELL_WARNING;
				}
			}
			return null;
		}
	}

	public TaskGrid() {
        setSizeFull();
        
        taskCriteria.archived(false);

        setCellStyleGenerator(new TaskGridCellStyleGenerator());
		setRowStyleGenerator(new TaskGridRowStyleGenerator());

        BeanItemContainer<TaskIndexDto> container = new BeanItemContainer<TaskIndexDto>(TaskIndexDto.class);
		GeneratedPropertyContainer editContainer = new GeneratedPropertyContainer(container);
        VaadinUiUtil.addIconColumn(editContainer, EDIT_BTN_ID, FontAwesome.PENCIL_SQUARE);
        setContainerDataSource(editContainer);

        setColumns(EDIT_BTN_ID, TaskIndexDto.CONTEXT_REFERENCE, TaskIndexDto.TASK_TYPE,  TaskIndexDto.PRIORITY,
        		TaskIndexDto.SUGGESTED_START, TaskIndexDto.DUE_DATE,
        		TaskIndexDto.ASSIGNEE_USER, TaskIndexDto.ASSIGNEE_REPLY, 
        		TaskIndexDto.CREATOR_USER, TaskIndexDto.CREATOR_COMMENT, TaskIndexDto.TASK_STATUS);

        getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
        
        getColumn(TaskIndexDto.DUE_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
        getColumn(TaskIndexDto.SUGGESTED_START).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
        getColumn(TaskIndexDto.CREATOR_COMMENT).setRenderer(new ShortStringRenderer(50));
        
        getColumn(TaskIndexDto.CONTEXT_REFERENCE).setRenderer(new HtmlRenderer(), new HtmlReferenceDtoConverter());
        
        getColumn(TaskIndexDto.ASSIGNEE_USER).setConverter(new HtmlReferenceDtoConverter() {
        	@Override
        	public String convertToPresentation(ReferenceDto value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        		String html;
        		if (value != null) {
        			html = ControllerProvider.getTaskController().getUserCaptionWithPendingTaskCount((UserReferenceDto)value);
        		} else {
        			html = "";
        		}
        		return html;
        	}
        });
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			TaskIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
        	setSelectionMode(SelectionMode.MULTI);
        } else {
        	setSelectionMode(SelectionMode.NONE);
        }     
        
		addItemClickListener(this);
	}
	
	public TaskGrid(TaskContext context, ReferenceDto entityRef) {
		this();
		removeColumn(TaskIndexDto.CONTEXT_REFERENCE);
		filterTaskStatus(null, false);
		switch (context) {
		case CASE:
			taskCriteria.cazeEquals((CaseReferenceDto) entityRef);
			break;
		case CONTACT:
			taskCriteria.contactEquals((ContactReferenceDto) entityRef);
			break;
		case EVENT:
			taskCriteria.eventEquals((EventReferenceDto) entityRef);
			break;
		case GENERAL:
		default:
			throw new IndexOutOfBoundsException(context.toString());
		}
	}
	
    public void filterAssignee(UserReferenceDto userDto, boolean reload) {
		getContainer().removeContainerFilters(TaskIndexDto.ASSIGNEE_USER);
		if (userDto != null) {
			Filter filter = new Equal(TaskIndexDto.ASSIGNEE_USER, userDto);  
	        getContainer().addContainerFilter(filter);
		}
		if (reload) {
			reload();
		}
	}

    public void filterExcludeAssignee(UserReferenceDto userDto, boolean reload) {
		getContainer().removeContainerFilters(TaskIndexDto.ASSIGNEE_USER);
		if (userDto != null) {
	    	Filter filter = new Not(new Equal(TaskIndexDto.ASSIGNEE_USER, userDto));  
	        getContainer().addContainerFilter(filter);
		}
		if (reload) {
			reload();
		}
	}

	public void filterTaskStatus(TaskStatus statusToFilter, boolean reload) {
    	getContainer().removeContainerFilters(TaskIndexDto.TASK_STATUS);
    	if (statusToFilter != null) {
    		Filter filter = new Equal(TaskIndexDto.TASK_STATUS, statusToFilter);  
	        getContainer().addContainerFilter(filter);
    	}
		if (reload) {
			reload();
		}
    }

    @SuppressWarnings("unchecked")
	public BeanItemContainer<TaskIndexDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<TaskIndexDto>) container.getWrappedContainer();
    }
    
    public void reload() {
    	List<TaskIndexDto> tasks = FacadeProvider.getTaskFacade().getIndexList(CurrentUser.getCurrent().getUserReference().getUuid(),
    			taskCriteria);
    	
    	tasks.sort(new Comparator<TaskIndexDto>() {

			@Override
			public int compare(TaskIndexDto o1, TaskIndexDto o2) {
				if (o1.getTaskStatus() != o2.getTaskStatus()) {
					if (o1.getTaskStatus() == TaskStatus.PENDING)
						return -1;
					if (o2.getTaskStatus() == TaskStatus.PENDING)
						return 1;
				}
				
				if (o1.getTaskStatus() == TaskStatus.PENDING) {
					if (o1.getPriority() != o2.getPriority()) {
						return o1.getPriority().compareTo(o2.getPriority());
					}
					return o1.getDueDate().compareTo(o2.getDueDate());
				}
				else {
					return -o1.getDueDate().compareTo(o2.getDueDate());
				}
			}
		});
        getContainer().removeAllItems();
        getContainer().addAll(tasks); 
        
        if (taskCriteria.hasContextCriteria()) {
        	this.setHeightByRows(getContainer().size() < 10 ? (getContainer().size() > 0 ? getContainer().size() : 1) : 10);
        }
    }

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.getPropertyId() == null) {
			return;
		}
		
		TaskIndexDto task = (TaskIndexDto)event.getItemId();
		if (TaskIndexDto.CONTEXT_REFERENCE.equals(event.getPropertyId())) {
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
		} else if (EDIT_BTN_ID.equals(event.getPropertyId()) || event.isDoubleClick()) {
			ControllerProvider.getTaskController().edit(task, this::reload);
		}
	}

	public TaskCriteria getTaskCriteria() {
		return taskCriteria;
	}

}
