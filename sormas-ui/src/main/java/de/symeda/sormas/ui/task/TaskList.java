package de.symeda.sormas.ui.task;

import java.util.Comparator;
import java.util.List;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

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
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class TaskList extends PaginationList<TaskIndexDto> {

	private final TaskCriteria taskCriteria = new TaskCriteria();
	private final TaskContext context;
	
	public TaskList(TaskContext context, ReferenceDto entityRef) {
		super(5);
		this.context = context;

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
		default:
			throw new IndexOutOfBoundsException(context.toString());
		}
	}

	@Override
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

		setEntries(tasks);
		if (!tasks.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noTasksLabel = new Label("There are no tasks for this " + context.toString() + ".");
			listLayout.addComponent(noTasksLabel);
		}
	}
	
	@Override
	protected void drawDisplayedEntries() {
		for (TaskIndexDto task : getDisplayedEntries()) {
			TaskListEntry listEntry = new TaskListEntry(task);
			if (LoginHelper.hasUserRight(UserRight.TASK_EDIT)) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getTaskController().edit(listEntry.getTask(), TaskList.this::reload);		
					}
				});
			}
			listLayout.addComponent(listEntry);
		}
	}
}
