package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;

/**
 * View for reading and editing the patient information fields.
 * Contains the {@link PersonEditForm}.
 * @author Stefan Szczesny
 */
public class ContactPersonView extends AbstractContactView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "contacts/person";

    public ContactPersonView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	ContactDto dto = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
    	CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(dto.getCaze().getUuid());
    	setSubComponent(ControllerProvider.getPersonController().getPersonEditComponent(dto.getPerson().getUuid(), caseDto.getDisease(), caseDto.getDiseaseDetails(), UserRight.CONTACT_EDIT));
    }
}
