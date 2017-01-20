package de.symeda.sormas.ui.task;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class TasksView extends AbstractView {

	public static final String VIEW_NAME = "tasks";
	
	private final TaskListComponent taskListComponent;

    public TasksView() {
        taskListComponent = new TaskListComponent();
        setSizeFull();
        addComponent(taskListComponent);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	taskListComponent.enter(event);
    }
}
