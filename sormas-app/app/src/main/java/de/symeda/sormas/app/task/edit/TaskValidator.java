package de.symeda.sormas.app.task.edit;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public class TaskValidator {

	static void initializeTaskValidation(final FragmentTaskEditLayoutBinding contentBinding, final Task task) {

		ResultCallback<Boolean> taskSuggestedStartCallback = () -> {
			if (DateHelper.isDateAfter(contentBinding.taskSuggestedStart.getValue(), contentBinding.taskDueDate.getValue())) {
				contentBinding.taskSuggestedStart.enableErrorState(
					I18nProperties.getValidationError(
						Validations.beforeDate,
						contentBinding.taskSuggestedStart.getCaption(),
						contentBinding.taskDueDate.getCaption()));
				return true;
			}
			return false;
		};

		ResultCallback<Boolean> taskDueDateCallback = () -> {
			if (DateHelper.isDateBefore(contentBinding.taskDueDate.getValue(), contentBinding.taskSuggestedStart.getValue())) {
				contentBinding.taskDueDate.enableErrorState(
					I18nProperties.getValidationError(
						Validations.afterDate,
						contentBinding.taskDueDate.getCaption(),
						contentBinding.taskSuggestedStart.getCaption()));
				return true;
			}
			return false;
		};

		contentBinding.taskSuggestedStart.setValidationCallback(taskSuggestedStartCallback);
		contentBinding.taskDueDate.setValidationCallback(taskDueDateCallback);
	};
}
