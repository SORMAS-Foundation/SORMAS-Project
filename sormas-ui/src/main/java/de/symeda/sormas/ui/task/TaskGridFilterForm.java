package de.symeda.sormas.ui.task;

import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class TaskGridFilterForm extends AbstractFilterForm<TaskCriteria> {

	private static final long serialVersionUID = -8661345403078183133L;

	protected TaskGridFilterForm() {
		super(TaskCriteria.class, TaskDto.I18N_PREFIX);
		getContent().removeComponent(APPLY_BUTTON_ID);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			TaskDto.TASK_STATUS };
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(TaskDto.TASK_STATUS, 200));
	}
}
