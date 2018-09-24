package de.symeda.sormas.ui.task;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class TasksView extends AbstractView {

	public static final String VIEW_NAME = "tasks";
	
	private final TaskGridComponent taskListComponent;

    public TasksView() {
    	super(VIEW_NAME);
    	
        taskListComponent = new TaskGridComponent();
        addComponent(taskListComponent);
        
    	if (LoginHelper.hasUserRight(UserRight.TASK_CREATE)) {
	    	Button createButton = new Button("New task");
	        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
	        createButton.addClickListener(e -> ControllerProvider.getTaskController().create(TaskContext.GENERAL, null, taskListComponent.getGrid()::reload));
	        addHeaderComponent(createButton);
    	}
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	taskListComponent.reload();
    	taskListComponent.updateActiveStatusButtonCaption();
    }
}
