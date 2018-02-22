package de.symeda.sormas.ui.hospitalization;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public class CaseHospitalizationView extends AbstractCaseView {
	
	public static final String VIEW_NAME = "cases/hospitalization";
	
	public CaseHospitalizationView() {
		super(VIEW_NAME); 	
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
    	if (getViewMode() == ViewMode.SIMPLE) {
    		ControllerProvider.getCaseController().navigateToCase(getCaseRef().getUuid());
    		return;
    	}
		
		setSubComponent(ControllerProvider.getCaseController().getCaseHospitalizationComponent(getCaseRef().getUuid(), getViewMode()));
	}
}
