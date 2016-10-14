package de.symeda.sormas.ui.surveillance.task;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.utils.ReferenceDtoToStringConverter;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class TaskGrid extends Grid {

	public TaskGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<TaskDto> container = new BeanItemContainer<TaskDto>(TaskDto.class);
        setContainerDataSource(container);
        setColumns(TaskDto.TASK_STATUS, TaskDto.DUE_DATE,
        		TaskDto.TASK_TYPE, TaskDto.TASK_CONTEXT, TaskDto.CAZE,
        		TaskDto.ASSIGNEE_USER, TaskDto.ASSIGNEE_REPLY, 
        		TaskDto.CREATOR_USER, TaskDto.CREATOR_COMMENT);

        getColumn(TaskDto.CAZE).setConverter(new ReferenceDtoToStringConverter());
        getColumn(TaskDto.CAZE).setRenderer(new UuidRenderer());
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getFieldCaption(
        			TaskDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        reload();
	}
    
    public void filterAssignee(ReferenceDto userDto) {
		getContainer().removeContainerFilters(TaskDto.ASSIGNEE_USER);
		if (userDto != null) {
	    	Equal filter = new Equal(TaskDto.ASSIGNEE_USER, userDto);  
	        getContainer().addContainerFilter(filter);
		}
    	reload();
	}

	public void filterTaskStatus(TaskStatus statusToFilter) {
    	getContainer().removeContainerFilters(TaskDto.TASK_STATUS);
    	if (statusToFilter != null) {
	    	Equal filter = new Equal(TaskDto.TASK_STATUS, statusToFilter);  
	        getContainer().addContainerFilter(filter);
    	}
    	reload();
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<TaskDto> getContainer() {
        return (BeanItemContainer<TaskDto>) super.getContainerDataSource();
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


