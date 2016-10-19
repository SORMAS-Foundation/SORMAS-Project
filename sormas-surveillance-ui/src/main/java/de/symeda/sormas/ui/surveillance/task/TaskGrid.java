package de.symeda.sormas.ui.surveillance.task;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Not;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.HtmlReferenceDtoConverter;

@SuppressWarnings("serial")
public class TaskGrid extends Grid {

	private static final String EDIT_BTN_ID = "edit";

	public TaskGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<TaskDto> container = new BeanItemContainer<TaskDto>(TaskDto.class);
        GeneratedPropertyContainer editContainer = new GeneratedPropertyContainer(container);
        // edit button
        editContainer.addGeneratedProperty(EDIT_BTN_ID, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				return FontAwesome.PENCIL_SQUARE.getHtml();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
        setContainerDataSource(editContainer);

        setColumns(EDIT_BTN_ID, TaskDto.CONTEXT_REFERENCE, TaskDto.TASK_TYPE,  TaskDto.PRIORITY,
        		TaskDto.SUGGESTED_START, TaskDto.DUE_DATE, //TaskDto.TASK_CONTEXT, 
        		TaskDto.ASSIGNEE_USER, TaskDto.ASSIGNEE_REPLY, 
        		TaskDto.CREATOR_USER, TaskDto.CREATOR_COMMENT, TaskDto.TASK_STATUS);

        getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
        
        getColumn(TaskDto.DUE_DATE).setRenderer(new DateRenderer("%1$tH:%1$tM %1$td.%1$tm.%1$ty"));
        getColumn(TaskDto.SUGGESTED_START).setRenderer(new DateRenderer("%1$tH:%1$tM %1$td.%1$tm.%1$ty"));
        
        setCellStyleGenerator(new CellStyleGenerator() {
			
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
		});

        getColumn(TaskDto.CONTEXT_REFERENCE).setRenderer(new HtmlRenderer(), new HtmlReferenceDtoConverter());
        
        getColumn(TaskDto.ASSIGNEE_USER).setConverter(new HtmlReferenceDtoConverter() {
        	@Override
        	public String convertToPresentation(ReferenceDto value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        		String html;
        		if (value != null) {
        			html = ControllerProvider.getTaskController().getUserCaptionWithTaskCount(value);
        		} else {
        			html = "";
        		}
        		return html;
        	}
        });
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getFieldCaption(
        			TaskDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        reload();
	}
    
    public void filterAssignee(ReferenceDto userDto) {
		getContainer().removeContainerFilters(TaskDto.ASSIGNEE_USER);
		if (userDto != null) {
			Filter filter = new Equal(TaskDto.ASSIGNEE_USER, userDto);  
	        getContainer().addContainerFilter(filter);
		}
    	reload();
	}

    public void filterExcludeAssignee(ReferenceDto userDto) {
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
        getContainer().removeAllItems();
        getContainer().addAll(tasks);    	
    }

    public void refresh(TaskDto entry) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<TaskDto> item = getContainer().getItem(entry);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(DataTransferObject.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(entry);
        }
    }

    public void remove(TaskDto entry) {
        getContainer().removeItem(entry);
    }
}


