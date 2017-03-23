package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.CaseDataForm;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
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
    	
    	ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
    	CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
    	
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.setSpacing(true);
    	layout.addComponent(ControllerProvider.getContactController().getContactDataEditComponent(getContactRef().getUuid()));
    	CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
    	caseInfoLayout.setMargin(new MarginInfo(true, false, false, false));
    	layout.addComponent(caseInfoLayout);
    	addComponent(layout);
    	
    	TaskListComponent taskListComponent = new TaskListComponent(TaskContext.CONTACT, getContactRef());
    	addComponent(taskListComponent);
    }
}
