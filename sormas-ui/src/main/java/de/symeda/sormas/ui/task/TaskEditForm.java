package de.symeda.sormas.ui.task;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.TaskStatusValidator;

@SuppressWarnings("serial")
public class TaskEditForm extends AbstractEditForm<TaskDto> {
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.fluidRow(LayoutUtil.loc(TaskDto.TASK_CONTEXT), 
    				LayoutUtil.locs(TaskDto.CAZE, TaskDto.EVENT, TaskDto.CONTACT))+
			LayoutUtil.fluidRowLocs(TaskDto.TASK_TYPE)+
			LayoutUtil.fluidRowLocs(TaskDto.SUGGESTED_START, TaskDto.DUE_DATE)+
			LayoutUtil.fluidRowLocs(TaskDto.ASSIGNEE_USER, TaskDto.PRIORITY)+
			LayoutUtil.fluidRowLocs(TaskDto.CREATOR_COMMENT)+
			LayoutUtil.fluidRowLocs(TaskDto.ASSIGNEE_REPLY)+
			LayoutUtil.fluidRowLocs(TaskDto.TASK_STATUS)
			;

    private UserRight editOrCreateUserRight;
    
    public TaskEditForm(boolean create, UserRight editOrCreateUserRight) {
        super(TaskDto.class, TaskDto.I18N_PREFIX, editOrCreateUserRight);
        this.editOrCreateUserRight = editOrCreateUserRight;
        addValueChangeListener(e -> {
    		updateByTaskContext();
    		updateByCreatingAndAssignee();
        });
        
        setWidth(680, Unit.PIXELS);
        
        if (create) {
        	hideValidationUntilNextCommit();
        }
    }
    
	@Override
	protected void addFields() {

    	addField(TaskDto.CAZE, ComboBox.class);
    	addField(TaskDto.EVENT, ComboBox.class);
    	addField(TaskDto.CONTACT, ComboBox.class);
    	addField(TaskDto.SUGGESTED_START, DateTimeField.class);
    	DateTimeField dueDate = addField(TaskDto.DUE_DATE, DateTimeField.class);
    	dueDate.setImmediate(true);
    	addField(TaskDto.PRIORITY, ComboBox.class);
    	OptionGroup taskStatus = addField(TaskDto.TASK_STATUS, OptionGroup.class);
    	OptionGroup taskContext = addField(TaskDto.TASK_CONTEXT, OptionGroup.class);
    	taskContext.setImmediate(true);
    	taskContext.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				updateByTaskContext();
			}
		});
    	    	
    	ComboBox taskTypeField = addField(TaskDto.TASK_TYPE, ComboBox.class);
    	taskTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
    	taskTypeField.setImmediate(true);
    	taskTypeField.addValueChangeListener(e -> {
    		TaskType taskType = (TaskType)e.getProperty().getValue();
    		if (taskType != null) {
    			setRequired(taskType.isCreatorCommentRequired(), TaskDto.CREATOR_COMMENT);
    		}
    	});
    	
    	ComboBox assigneeUser = addField(TaskDto.ASSIGNEE_USER, ComboBox.class);
    	assigneeUser.addValueChangeListener(e -> updateByCreatingAndAssignee());
    	assigneeUser.setImmediate(true);

    	TextArea creatorComment = addField(TaskDto.CREATOR_COMMENT, TextArea.class);
    	creatorComment.setRows(2);
    	creatorComment.setImmediate(true);
    	addField(TaskDto.ASSIGNEE_REPLY, TextArea.class).setRows(2);

    	setRequired(true, TaskDto.TASK_CONTEXT, TaskDto.TASK_TYPE, TaskDto.ASSIGNEE_USER, TaskDto.DUE_DATE);
    	setReadOnly(true, TaskDto.TASK_CONTEXT, TaskDto.CAZE, TaskDto.CONTACT, TaskDto.EVENT);
    	
    	addValueChangeListener(e -> {
	    	TaskDto taskDto = getValue();
	    	
	    	if (taskDto.getTaskType() == TaskType.CASE_INVESTIGATION && taskDto.getCaze() != null) {
	        	taskStatus.addValidator(new TaskStatusValidator(taskDto.getCaze().getUuid(), "Not allowed to set investigation status to done for an unclassified case."));
	    	}
	    	
	    	DistrictReferenceDto district = null;
	    	RegionReferenceDto region = null;
	    	if (taskDto.getCaze() != null) {
	    		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(taskDto.getCaze().getUuid());
	    		district = caseDto.getDistrict();
	    		region = caseDto.getRegion();
	    	} else if (taskDto.getContact() != null) {
	    		ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(taskDto.getContact().getUuid());
	    		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
	    		district = caseDto.getDistrict();
	    		region = caseDto.getRegion();
	    	} else if (taskDto.getEvent() != null) {
	    		EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(taskDto.getEvent().getUuid());
	    		district = eventDto.getEventLocation().getDistrict();
	    		region = eventDto.getEventLocation().getRegion();
	    	} else {
	    		UserDto userDto = LoginHelper.getCurrentUser();
	    		district = userDto.getDistrict();
	    		region = userDto.getRegion();
	    	}
	    	
	    	List<UserReferenceDto> users = new ArrayList<>();
	    	if (district != null) {
	    		users = FacadeProvider.getUserFacade().getAssignableUsersByDistrict(district, true);
	    	} else if (region != null) {
	    		users = FacadeProvider.getUserFacade().getAssignableUsersByRegion(region);
	    	} else {
	    		// fallback - just show all users
	    		users = FacadeProvider.getUserFacade().getAllAfterAsReference(null);
	    	}
	    	
	    	TaskController taskController = ControllerProvider.getTaskController();
    		for (UserReferenceDto user : users) {
    			assigneeUser.addItem(user);
    			assigneeUser.setItemCaption(user, taskController.getUserCaptionWithPendingTaskCount(user));
    		}
    	});
    }

	private void updateByCreatingAndAssignee() {
		
		TaskDto value = getValue();
		if (value != null) {
			boolean creating = value.getCreationDate() == null;
	
			UserDto user = LoginHelper.getCurrentUser();
			boolean creator = user.equals(value.getCreatorUser());
			boolean supervisor = UserRole.isSupervisor(user.getUserRoles());
			boolean assignee = user.equals(getFieldGroup().getField(TaskDto.ASSIGNEE_USER).getValue());
			
			setVisible(!creating || assignee, TaskDto.ASSIGNEE_REPLY, TaskDto.TASK_STATUS);
			if (creating && !assignee) {
				discard(TaskDto.ASSIGNEE_REPLY, TaskDto.TASK_STATUS);
			}
			
			if (LoginHelper.hasUserRight(editOrCreateUserRight)) {
				setReadOnly(!(assignee || creator), TaskDto.TASK_STATUS);
				setReadOnly(!assignee, TaskDto.ASSIGNEE_REPLY);
				setReadOnly(!creator, TaskDto.TASK_TYPE, TaskDto.PRIORITY, 
						TaskDto.SUGGESTED_START, TaskDto.DUE_DATE,
						TaskDto.ASSIGNEE_USER, TaskDto.CREATOR_COMMENT);
				setReadOnly(!(creator || supervisor), 
						TaskDto.PRIORITY, TaskDto.SUGGESTED_START, TaskDto.DUE_DATE,
						TaskDto.ASSIGNEE_USER, TaskDto.CREATOR_COMMENT);
			}
		}
	}

	private void updateByTaskContext() {		
		TaskContext taskContext = (TaskContext)getFieldGroup().getField(TaskDto.TASK_CONTEXT).getValue();
		
		// Task types depending on task context
		ComboBox taskType = (ComboBox) getFieldGroup().getField(TaskDto.TASK_TYPE);
		FieldHelper.updateItems(taskType, TaskType.getTaskTypes(taskContext));
		
		// context reference depending on task context
		ComboBox caseField = (ComboBox) getFieldGroup().getField(TaskDto.CAZE);
		ComboBox eventField = (ComboBox) getFieldGroup().getField(TaskDto.EVENT);
		ComboBox contactField = (ComboBox) getFieldGroup().getField(TaskDto.CONTACT);
		if (taskContext != null) {
			switch (taskContext) {
			case CASE:
				FieldHelper.setFirstVisibleClearOthers(caseField, eventField, contactField);
				FieldHelper.setFirstRequired(caseField, eventField, contactField);
				break;
			case EVENT:
				FieldHelper.setFirstVisibleClearOthers(eventField, caseField, contactField);
				FieldHelper.setFirstRequired(eventField, caseField, contactField);
				break;
			case CONTACT:
				FieldHelper.setFirstVisibleClearOthers(contactField, caseField, eventField);
				FieldHelper.setFirstRequired(contactField, caseField, eventField);
				break;
			case GENERAL:
				FieldHelper.setFirstVisibleClearOthers(null, caseField, contactField, eventField);
				FieldHelper.setFirstRequired(null, caseField, contactField, eventField);
				break;
			}
		} else {
			FieldHelper.setFirstVisibleClearOthers(null, caseField, eventField, contactField);
			FieldHelper.setFirstRequired(null, caseField, eventField, contactField);
		}
	}
	
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
