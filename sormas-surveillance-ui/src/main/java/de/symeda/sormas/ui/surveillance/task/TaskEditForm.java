package de.symeda.sormas.ui.surveillance.task;

import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class TaskEditForm extends AbstractEditForm<TaskDto> {
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.fluidRow(LayoutUtil.loc(TaskDto.TASK_CONTEXT), LayoutUtil.locs(TaskDto.CAZE, TaskDto.EVENT, TaskDto.CONTACT))+
			LayoutUtil.fluidRowLocs(TaskDto.TASK_TYPE)+
			LayoutUtil.fluidRowLocs(TaskDto.ASSIGNEE_USER, TaskDto.DUE_DATE)+
			LayoutUtil.fluidRowLocs(TaskDto.CREATOR_COMMENT)
			;

    public TaskEditForm() {
        super(TaskDto.class, TaskDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {

    	addField(TaskDto.CAZE, ComboBox.class);
    	addField(TaskDto.EVENT, ComboBox.class);
    	addField(TaskDto.CONTACT, ComboBox.class);
    	addField(TaskDto.DUE_DATE);

    	OptionGroup taskContext = addField(TaskDto.TASK_CONTEXT, OptionGroup.class);
    	taskContext.setImmediate(true);
    	taskContext.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				TaskContext taskContext = (TaskContext)event.getProperty().getValue();
				updateByTaskContext(taskContext);
			}
		});
    	    	
    	ComboBox taskType = addField(TaskDto.TASK_TYPE, ComboBox.class);
    	taskType.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
    	taskType.setImmediate(true);
    	taskType.addValueChangeListener(new Property.ValueChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				TaskType taskType = (TaskType)event.getProperty().getValue();
				if (taskType != null) {
					((Field<TaskContext>)getFieldGroup().getField(TaskDto.TASK_CONTEXT)).setValue(taskType.getTaskContext());
				}
			}
		});
    	
    	ComboBox assigneeUser = addField(TaskDto.ASSIGNEE_USER, ComboBox.class);
    	List<ReferenceDto> users = FacadeProvider.getUserFacade().getAllAfterAsReference(null);
    	assigneeUser.addItems(users);

    	addField(TaskDto.CREATOR_COMMENT, TextArea.class).setRows(2);
    	
    	setRequired(true, TaskDto.TASK_CONTEXT, TaskDto.TASK_TYPE, TaskDto.ASSIGNEE_USER, TaskDto.DUE_DATE);
    	
    	updateByTaskContext((TaskContext) taskContext.getValue());
    }

	private void updateByTaskContext(TaskContext taskContext) {
		// Task types depending on task context
		ComboBox taskType = (ComboBox) getFieldGroup().getField(TaskDto.TASK_TYPE);
		Object tempValue = taskType.getValue();
		taskType.removeAllItems();
		taskType.addItems(TaskType.getTaskTypes(taskContext));
		if (taskType.containsId(tempValue)) {
			taskType.setValue(tempValue);
		}
		
		// context reference depending on task context
		ComboBox caze = (ComboBox) getFieldGroup().getField(TaskDto.CAZE);
		ComboBox eventField = (ComboBox) getFieldGroup().getField(TaskDto.EVENT);
		ComboBox contact = (ComboBox) getFieldGroup().getField(TaskDto.CONTACT);
		if (taskContext != null) {
			switch (taskContext) {
			case CASE:
				FieldHelper.setFirstVisibleClearOthers(caze, eventField, contact);
				FieldHelper.setFirstRequired(caze, eventField, contact);
				List<ReferenceDto> cases = FacadeProvider.getCaseFacade().getAllCasesAfterAsReference(null, LoginHelper.getCurrentUser().getUuid());
				caze.removeAllItems();
				caze.addItems(cases);
				break;
			case EVENT:
				FieldHelper.setFirstVisibleClearOthers(eventField, caze, contact);
				FieldHelper.setFirstRequired(eventField, caze, contact);
				eventField.removeAllItems();
				break;
			case CONTACT:
				FieldHelper.setFirstVisibleClearOthers(contact, caze, eventField);
				FieldHelper.setFirstRequired(contact, caze, eventField);
				contact.removeAllItems();
				break;
			}
		}
		else {
			FieldHelper.setFirstVisibleClearOthers(null, caze, eventField, contact);
			FieldHelper.setFirstRequired(null, caze, eventField, contact);
		}
	}
	
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
