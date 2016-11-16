package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;

/**
 * View for reading and editing the patient information fields.
 * Contains the {@link CasePersonForm}.
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
    	setEditComponent(ControllerProvider.getCaseController().getCasePersonEditComponent(getEntityUuid()));
    }
}
