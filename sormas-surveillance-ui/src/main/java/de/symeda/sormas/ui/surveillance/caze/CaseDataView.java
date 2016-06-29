package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * CaseDataView for reading and editing the case data fields.
 * Contains the {@link CaseDataForm}.
 * @author Stefan Szczesny
 *
 */
public class CaseDataView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "cases/data";

    public CaseDataView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	setEditComponent(getViewLogic().getCaseDataEditComponent(getCaseUuid()));
    }
}
