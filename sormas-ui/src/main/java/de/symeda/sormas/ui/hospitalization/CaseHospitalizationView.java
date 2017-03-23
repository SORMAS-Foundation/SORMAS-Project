package de.symeda.sormas.ui.hospitalization;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.AbstractCaseView;

@SuppressWarnings("serial")
public class CaseHospitalizationView extends AbstractCaseView {
	
	public static final String VIEW_NAME = "cases/hospitalization";
	
	public CaseHospitalizationView() {
		super(VIEW_NAME);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setSubComponent(ControllerProvider.getCaseController().getCaseHospitalizationComponent(getCaseRef().getUuid()));
	}

}
