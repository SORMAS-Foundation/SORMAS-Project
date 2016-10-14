package de.symeda.sormas.ui.surveillance.task;

import java.util.Date;
import java.util.List;

import com.vaadin.server.Sizeable.Unit;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TaskController {

    public TaskController() {
    	
    }
    
    public List<TaskDto> getAllTasks() {
    	UserDto user = LoginHelper.getCurrentUser();
    	return FacadeProvider.getTaskFacade().getAllAfter(null, user.getUuid());
    }

	public void create() {
    	CommitDiscardWrapperComponent<TaskEditForm> createComponent = getTaskCreateComponent();
    	VaadinUiUtil.showModalPopupWindow(createComponent, "Create new task");   
	}
	
    public void overview() {
    	String navigationState = TasksView.VIEW_NAME;
    	SurveillanceUI.get().getNavigator().navigateTo(navigationState);
    }
    
    public CommitDiscardWrapperComponent<TaskEditForm> getTaskCreateComponent() {
    	
    	TaskEditForm createForm = new TaskEditForm();
        createForm.setValue(createNewTask());
        final CommitDiscardWrapperComponent<TaskEditForm> editView = new CommitDiscardWrapperComponent<TaskEditForm>(createForm, createForm.getFieldGroup());
        editView.setWidth(560, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (createForm.getFieldGroup().isValid()) {
        			TaskDto dto = createForm.getValue();
        			FacadeProvider.getTaskFacade().saveTask(dto);
        			overview();
        		}
        	}
        });
        return editView;
    }  
    
    private TaskDto createNewTask() {
    	TaskDto task = new TaskDto();
    	task.setUuid(DataHelper.createUuid());
    	task.setDueDate(new Date());
    	task.setCreatorUser(LoginHelper.getCurrentUserAsReference());
    	task.setTaskStatus(TaskStatus.PENDING);
    	return task;
    }
}
