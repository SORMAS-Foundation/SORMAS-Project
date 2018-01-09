package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

/**
 * View for reading and editing the patient information fields.
 * Contains the {@link PersonEditForm}.
 * @author Stefan Szczesny
 */
public class CasePersonView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "cases/person";

    public CasePersonView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	CaseDataDto caseData = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
    	CommitDiscardWrapperComponent<PersonEditForm> personEditComponent = ControllerProvider.getPersonController().getPersonEditComponent(caseData.getPerson().getUuid(), caseData.getDisease(), caseData.getDiseaseDetails(), UserRight.CASE_EDIT);
    	setSubComponent(personEditComponent);
    }
}
