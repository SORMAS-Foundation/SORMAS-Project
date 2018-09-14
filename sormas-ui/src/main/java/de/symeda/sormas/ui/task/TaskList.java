package de.symeda.sormas.ui.task;

import java.util.Comparator;
import java.util.List;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class TaskList extends VerticalLayout {

	private final TaskCriteria taskCriteria = new TaskCriteria();

	public TaskList(TaskContext context, ReferenceDto entityRef) {

		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST);

		switch (context) {
		case CASE:
			taskCriteria.cazeEquals((CaseReferenceDto) entityRef);
			break;
		case CONTACT:
			taskCriteria.contactEquals((ContactReferenceDto) entityRef);
			break;
		case EVENT:
			taskCriteria.eventEquals((EventReferenceDto) entityRef);
			break;
		case GENERAL:
		default:
			throw new IndexOutOfBoundsException(context.toString());
		}
	}

	public void reload() {
		List<TaskIndexDto> tasks = FacadeProvider.getTaskFacade()
				.getIndexList(LoginHelper.getCurrentUserAsReference().getUuid(), taskCriteria);

		tasks.sort(new Comparator<TaskIndexDto>() {

			@Override
			public int compare(TaskIndexDto o1, TaskIndexDto o2) {
				if (o1.getTaskStatus() != o2.getTaskStatus()) {
					if (o1.getTaskStatus() == TaskStatus.PENDING)
						return -1;
					if (o2.getTaskStatus() == TaskStatus.PENDING)
						return 1;
				}

				if (o1.getTaskStatus() == TaskStatus.PENDING) {
					if (o1.getPriority() != o2.getPriority()) {
						return o1.getPriority().compareTo(o2.getPriority());
					}
					return o1.getDueDate().compareTo(o2.getDueDate());
				} else {
					return -o1.getDueDate().compareTo(o2.getDueDate());
				}
			}
		});

		removeAllComponents();
		boolean hasEditRight = LoginHelper.hasUserRight(UserRight.TASK_EDIT);

		// build entries
		for (TaskIndexDto task : tasks) {
			TaskListEntry listEntry = new TaskListEntry(task);
			if (hasEditRight) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getTaskController().edit(listEntry.getTask(), TaskList.this::reload);		
					}
				});
			}
			addComponent(listEntry);
		}
	}
}
