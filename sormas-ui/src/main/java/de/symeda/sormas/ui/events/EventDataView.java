package de.symeda.sormas.ui.events;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.task.TaskListComponent;

public class EventDataView extends AbstractEventView {
	
	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "events/data";
	
	public EventDataView() {
		super(VIEW_NAME);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
    	setHeightUndefined();
		setSubComponent(ControllerProvider.getEventController().getEventDataEditComponent(getEventRef().getUuid()));
		
    	TaskListComponent taskListComponent = new TaskListComponent(TaskContext.EVENT, getEventRef());
    	addComponent(taskListComponent);
	}

}
