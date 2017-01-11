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
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.HtmlReferenceDtoConverter;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class TaskGrid extends Grid implements ItemClickListener {

	private static final String EDIT_BTN_ID = "edit";

	private final class TaskGridRowStyleGenerator implements RowStyleGenerator {
		@Override
		public String getStyle(RowReference row) {
			TaskDto task = (TaskDto)row.getItemId();
			if (task != null && task.getTaskStatus() != null) {
				switch (task.getTaskStatus()) {
				case DISCARDED:
					return CssStyles.STATUS_DISCARDED;
				case NOT_EXECUTABLE:
					return CssStyles.STATUS_NOT;
				case DONE:
					return CssStyles.STATUS_DONE;
				case PENDING:
					return CssStyles.STATUS_PENDING;
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
			if (TaskDto.PRIORITY.equals(cell.getPropertyId())) {
				TaskPriority priority = (TaskPriority)cell.getProperty().getValue();
				if (priority != null) {
				switch (priority) {
				case HIGH:
					return CssStyles.PRIORITY_HIGH;
				case NORMAL:
					return CssStyles.PRIORITY_NORMAL;
				case LOW:
					return CssStyles.PRIORITY_LOW;
				default:
					throw new IndexOutOfBoundsException(priority.toString());
				}
				}
			}
			else if (TaskDto.DUE_DATE.equals(cell.getPropertyId())) {
				Date dueDate = (Date)cell.getProperty().getValue();
				if (dueDate != null && dueDate.before(new Date())) {
					return CssStyles.PRIORITY_HIGH;
				}
			}
			return null;
		}
	}

	public TaskGrid() {
        setSizeFull();

        setCellStyleGenerator(new TaskGridCellStyleGenerator());
		setRowStyleGenerator(new TaskGridRowStyleGenerator());

        BeanItemContainer<TaskDto> container = new BeanItemContainer<TaskDto>(TaskDto.class);
		GeneratedPropertyContainer editContainer = new GeneratedPropertyContainer(container);
        VaadinUiUtil.addEditColumn(editContainer, EDIT_BTN_ID);
        setContainerDataSource(editContainer);

        setColumns(EDIT_BTN_ID, TaskDto.CONTEXT_REFERENCE, TaskDto.TASK_TYPE,  TaskDto.PRIORITY,
        		TaskDto.SUGGESTED_START, TaskDto.DUE_DATE, //TaskDto.TASK_CONTEXT, 
        		TaskDto.ASSIGNEE_USER, TaskDto.ASSIGNEE_REPLY, 
        		TaskDto.CREATOR_USER, TaskDto.CREATOR_COMMENT, TaskDto.TASK_STATUS);

        getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
        
        getColumn(TaskDto.DUE_DATE).setRenderer(new DateRenderer("%1$tH:%1$tM %1$td.%1$tm.%1$ty"));
        getColumn(TaskDto.SUGGESTED_START).setRenderer(new DateRenderer("%1$tH:%1$tM %1$td.%1$tm.%1$ty"));
        
        getColumn(TaskDto.CONTEXT_REFERENCE).setRenderer(new HtmlRenderer(), new HtmlReferenceDtoConverter());
        
        getColumn(TaskDto.ASSIGNEE_USER).setConverter(new HtmlReferenceDtoConverter() {
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
        			TaskDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        setSelectionMode(SelectionMode.NONE);        
		addItemClickListener(this);

        reload();
	}
    
    public void filterAssignee(UserReferenceDto userDto) {
		getContainer().removeContainerFilters(TaskDto.ASSIGNEE_USER);
		if (userDto != null) {
			Filter filter = new Equal(TaskDto.ASSIGNEE_USER, userDto);  
	        getContainer().addContainerFilter(filter);
		}
    	reload();
	}

    public void filterExcludeAssignee(UserReferenceDto userDto) {
		getContainer().removeContainerFilters(TaskDto.ASSIGNEE_USER);
		if (userDto != null) {
	    	Filter filter = new Not(new Equal(TaskDto.ASSIGNEE_USER, userDto));  
	        getContainer().addContainerFilter(filter);
		}
    	reload();
	}

	public void filterTaskStatus(TaskStatus statusToFilter) {
    	getContainer().removeContainerFilters(TaskDto.TASK_STATUS);
    	if (statusToFilter != null) {
    		Filter filter = new Equal(TaskDto.TASK_STATUS, statusToFilter);  
	        getContainer().addContainerFilter(filter);
    	}
    	reload();
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<TaskDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<TaskDto>) container.getWrappedContainer();
    }
    
    public void reload() {
    	List<TaskDto> tasks = ControllerProvider.getTaskController().getAllTasks();
    	tasks.sort(new Comparator<TaskDto>() {

			@Override
			public int compare(TaskDto o1, TaskDto o2) {
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
    }

	@Override
	public void itemClick(ItemClickEvent event) {
		TaskDto task = (TaskDto)event.getItemId();
		if (TaskDto.CONTEXT_REFERENCE.equals(event.getPropertyId())) {
			switch (task.getTaskContext()) {
			case CASE:
				ControllerProvider.getCaseController().navigateToData(task.getCaze().getUuid());
				return;
			case CONTACT:
				ControllerProvider.getContactController().navigateToData(task.getContact().getUuid());
				return;
			case EVENT:
				ControllerProvider.getEventController().navigateToData(task.getEvent().getUuid());
				return;
			default:
				throw new IndexOutOfBoundsException(task.getTaskContext().toString());
			}
		} 
		else if (EDIT_BTN_ID.equals(event.getPropertyId()) || event.isDoubleClick()) {
			ControllerProvider.getTaskController().edit(task);
		}
	}
}


