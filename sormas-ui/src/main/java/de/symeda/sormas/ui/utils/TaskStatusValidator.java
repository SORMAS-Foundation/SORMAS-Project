package de.symeda.sormas.ui.utils;

import com.vaadin.data.validator.AbstractValidator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.ValidationException;

@SuppressWarnings("serial")
public class TaskStatusValidator extends AbstractValidator<TaskStatus> {

	private String caseUuid;
	
	public TaskStatusValidator(String caseUuid, String errorMessage) {
		super(errorMessage);
		this.caseUuid = caseUuid;
	}

	@Override
	protected boolean isValidValue(TaskStatus value) {
		if (value == TaskStatus.DONE) {
			try {
				CaseLogic.validateInvestigationDoneAllowed(FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid));
				return true;
			} catch (ValidationException e) {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public Class<TaskStatus> getType() {
		return TaskStatus.class;
	}

}