package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ViewMode;

public class CaseSymptomsView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/symptoms";

    public CaseSymptomsView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	
    	if (getViewMode() == ViewMode.SIMPLE) {
    		ControllerProvider.getCaseController().navigateToCase(getCaseRef().getUuid());
    		return;
    	}

    	setSubComponent(ControllerProvider.getCaseController().getCaseSymptomsEditComponent(getCaseRef().getUuid(), getViewMode()));
    }
}
