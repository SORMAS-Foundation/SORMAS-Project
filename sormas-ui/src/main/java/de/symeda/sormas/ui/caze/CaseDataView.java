package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;

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
    	setSubComponent(ControllerProvider.getCaseController().getCaseDataEditComponent(getCaseRef().getUuid()));
    }
}
