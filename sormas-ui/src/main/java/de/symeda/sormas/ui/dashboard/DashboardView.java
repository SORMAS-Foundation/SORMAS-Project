package de.symeda.sormas.ui.dashboard;

import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class DashboardView extends AbstractView {

	public static final String VIEW_NAME = "dashboard";
	
	private final MapComponent mapComponent;

    
	public DashboardView() {
		setSizeFull();
		
		mapComponent = new MapComponent();
        addComponent(mapComponent);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
    	List<CaseDataDto> cases = ControllerProvider.getCaseController().getCaseIndexList();
    	mapComponent.showCases(cases);
	}
}
