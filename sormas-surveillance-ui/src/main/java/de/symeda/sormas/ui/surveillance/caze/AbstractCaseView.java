package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.surveillance.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractView {

    protected CaseController viewLogic = ControllerProvider.getCaseController();
    public SubNavigationMenu caseNavigationMenu;
    protected CssLayout caseEditLayout;
    protected String viewName;

	private String caseUuid;
	

    protected AbstractCaseView(String viewName) {
        setWidth(900, Unit.PIXELS);
        setHeight(100, Unit.PERCENTAGE);
        setMargin(true);
        this.viewName = viewName;
        
        caseNavigationMenu = new SubNavigationMenu();
    	addComponent(caseNavigationMenu);
    	setExpandRatio(caseNavigationMenu, 0);
        
        caseEditLayout = new CssLayout();
        caseEditLayout.setWidth(100, Unit.PERCENTAGE);
        caseEditLayout.setHeightUndefined();
        addComponent(caseEditLayout);
    	setExpandRatio(caseEditLayout, 1);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
    	caseUuid = event.getParameters();
    	refreshCaseEditMenu(caseUuid);
		selectInMenu();
    };
    
    public void refreshCaseEditMenu(String uuid) {
    	caseNavigationMenu.removeAllViews();
    	caseNavigationMenu.addView(CasesView.VIEW_NAME, "Cases List");
    	caseNavigationMenu.addView(CaseDataView.VIEW_NAME, "Case Data", uuid);
    	caseNavigationMenu.addView(PatientInformationView.VIEW_NAME, "Patient Information", uuid);
    }
    
    protected String getCaseUuid() {
		return caseUuid;
	}
    
    protected void setEditComponent(Component newComponent) {
    	caseEditLayout.removeAllComponents();
    	caseEditLayout.addComponent(newComponent);
    }
    
    protected CaseController getViewLogic() {
		return this.viewLogic;
	}
    
    public void selectInMenu() {
    	caseNavigationMenu.setActiveView(viewName);
    }
}
