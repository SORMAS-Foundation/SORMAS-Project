package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.CaseDataForm;
import de.symeda.sormas.ui.task.TaskListComponent;

/**
 * CaseDataView for reading and editing the case data fields.
 * Contains the {@link CaseDataForm}.
 * @author Stefan Szczesny
 *
 */
public class ContactDataView extends AbstractContactView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "contacts/data";

    public ContactDataView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	setHeightUndefined();
    	setSubComponent(ControllerProvider.getContactController().getContactDataEditComponent(getContactRef().getUuid()));
    	
    	TaskListComponent taskListComponent = new TaskListComponent(TaskContext.CONTACT, getContactRef());
    	addComponent(taskListComponent);
    }
}
