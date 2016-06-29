package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.surveillance.AbstractCaseView;

/**
 * View for reading and editing the patient information fields.
 * Contains the {@link CasePersonForm}.
 * @author Stefan Szczesny
 *
 */
public class PatientInformationView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "cases/patient";

    public PatientInformationView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	setComponent(getViewLogic().getPatientInformationEditComponent(getCaseUuid()));
    }
}
