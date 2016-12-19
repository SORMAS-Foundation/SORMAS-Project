package de.symeda.sormas.ui.task;

import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable.Unit;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
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

        VaadinUiUtil.showModalPopupWindow(editView, "Create new task");   
	}

	public void edit(TaskDto dto) {
		
		// get fresh data
		dto = FacadeProvider.getTaskFacade().getByUuid(dto.getUuid());
		
    	TaskEditForm form = new TaskEditForm();
        form.setValue(dto);
        final CommitDiscardWrapperComponent<TaskEditForm> editView = new CommitDiscardWrapperComponent<TaskEditForm>(form, form.getFieldGroup());
        editView.setWidth(560, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (form.getFieldGroup().isValid()) {
        			TaskDto dto = form.getValue();
        			FacadeProvider.getTaskFacade().saveTask(dto);
        			overview();
        		}
        	}
        });

        VaadinUiUtil.showModalPopupWindow(editView, "Edit task");
	}

    public void overview() {
    	View currentView = SormasUI.get().getNavigator().getCurrentView();
    	if (currentView instanceof TasksView) {
    		// force refresh, because view didn't change
    		((TasksView)currentView).enter(null);
    	} else {
	    	String navigationState = TasksView.VIEW_NAME;
	    	SormasUI.get().getNavigator().navigateTo(navigationState);
    	}
    }
    
    private TaskDto createNewTask() {
    	TaskDto task = new TaskDto();
    	task.setUuid(DataHelper.createUuid());
    	task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
    	task.setDueDate(TaskHelper.getDefaultDueDate());
    	task.setCreatorUser(LoginHelper.getCurrentUserAsReference());
    	task.setTaskStatus(TaskStatus.PENDING);
    	task.setPriority(TaskPriority.NORMAL);
    	return task;
    }
    
    public String getUserCaptionWithPendingTaskCount(UserReferenceDto user) {
    	long taskCount = FacadeProvider.getTaskFacade().getPendingTaskCount(user.getUuid());
    	return user.getCaption() + " (" + taskCount + ")";
    }
}
