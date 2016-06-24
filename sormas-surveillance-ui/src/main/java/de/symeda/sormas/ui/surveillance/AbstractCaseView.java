package de.symeda.sormas.ui.surveillance;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.ui.surveillance.caze.CaseController;
import de.symeda.sormas.ui.surveillance.caze.CaseDataView;
import de.symeda.sormas.ui.surveillance.caze.CasesView;
import de.symeda.sormas.ui.surveillance.caze.PatientInformationView;
import de.symeda.sormas.ui.utils.AbstractView;

public abstract class AbstractCaseView extends AbstractView {

	private static final long serialVersionUID = -1L;
	
    protected CaseController viewLogic = ControllerProvider.getCaseController();
    public CaseEditMenu caseEditMenu;
    protected HorizontalLayout caseEditLayout;
    protected String viewName;

	private String caseUuid;
	

    protected AbstractCaseView(String viewName) {
        setSizeFull();
        addStyleName("crud-view");
        this.viewName = viewName;
        
        caseEditMenu = new CaseEditMenu();
    	addComponent(caseEditMenu);
        
        caseEditLayout = new HorizontalLayout();
        caseEditLayout.setMargin(true);
        caseEditLayout.setSpacing(true);
        caseEditLayout.setSizeFull();
        addComponent(caseEditLayout);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
    	caseUuid = event.getParameters();
    	refreshCaseEditMenu(caseUuid);
		selectInMenu();
    };
    
    public void refreshCaseEditMenu(String uuid) {
    	caseEditMenu.removeAllViews();
    	caseEditMenu.addView(CasesView.VIEW_NAME, "Overview");
    	caseEditMenu.addView(CaseDataView.VIEW_NAME, "Case Data", uuid);
    	caseEditMenu.addView(PatientInformationView.VIEW_NAME, "Patient Information", uuid);
    }
    
    protected String getCaseUuid() {
		return caseUuid;
	}
    
    protected void setComponent(Component newComponent) {
    	caseEditLayout.removeAllComponents();
    	caseEditLayout.addComponent(newComponent);
    }
    
    protected CaseController getViewLogic() {
		return this.viewLogic;
	}
    
    public void selectInMenu() {
    	caseEditMenu.setActiveView(viewName);
    }
}
