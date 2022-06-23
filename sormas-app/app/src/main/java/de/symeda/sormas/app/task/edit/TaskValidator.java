package de.symeda.sormas.app.task.edit;

import org.joda.time.DateTimeComparator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public class TaskValidator {

	static void initializeTaskValidation(final FragmentTaskEditLayoutBinding contentBinding, final Task task) {

		ResultCallback<Boolean> taskSuggestedStartCallback = () -> {
			if (contentBinding.taskSuggestedStart.getValue() != null && contentBinding.taskDueDate.getValue() != null) {
				if (DateTimeComparator.getDateOnlyInstance()
					.compare(contentBinding.taskDueDate.getValue(), contentBinding.taskSuggestedStart.getValue())
					< 0) {
					contentBinding.taskSuggestedStart.enableErrorState(
						I18nProperties.getValidationError(
							Validations.beforeDate,
							contentBinding.taskSuggestedStart.getCaption(),
							contentBinding.taskDueDate.getCaption()));
					return true;
				}
			}
			return false;
		};

		ResultCallback<Boolean> taskDueDateCallback = () -> {
			if (contentBinding.taskSuggestedStart.getValue() != null && contentBinding.taskDueDate.getValue() != null) {
				if (DateTimeComparator.getDateOnlyInstance()
					.compare(contentBinding.taskDueDate.getValue(), contentBinding.taskSuggestedStart.getValue())
					< 0) {
					contentBinding.taskDueDate.enableErrorState(
						I18nProperties.getValidationError(
							Validations.afterDate,
							contentBinding.taskDueDate.getCaption(),
							contentBinding.taskSuggestedStart.getCaption()));
					return true;
				}
			}
			return false;
		};

		contentBinding.taskSuggestedStart.setValidationCallback(taskSuggestedStartCallback);
		contentBinding.taskDueDate.setValidationCallback(taskDueDateCallback);
	};
}
