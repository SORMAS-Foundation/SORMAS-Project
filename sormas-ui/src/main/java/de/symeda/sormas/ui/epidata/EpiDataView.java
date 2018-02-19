package de.symeda.sormas.ui.epidata;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public class EpiDataView extends AbstractCaseView {
	
	public static final String VIEW_NAME = "cases/epidata";
	
	public EpiDataView() {
		super(VIEW_NAME);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
    	if (getViewMode() == ViewMode.OUTBREAK) {
    		ControllerProvider.getCaseController().navigateToCase(getCaseRef().getUuid());
    		return;
    	}
		
		setSubComponent(ControllerProvider.getCaseController().getCaseEpiDataComponent(getCaseRef().getUuid(), getViewMode()));
	}

}
