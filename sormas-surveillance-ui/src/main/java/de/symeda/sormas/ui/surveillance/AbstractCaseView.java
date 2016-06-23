package de.symeda.sormas.ui.surveillance;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.ui.surveillance.caze.CaseController;
import de.symeda.sormas.ui.surveillance.caze.CaseDataView;
import de.symeda.sormas.ui.surveillance.caze.CasesView;
import de.symeda.sormas.ui.surveillance.caze.PatientInformationView;
import de.symeda.sormas.ui.utils.AbstractView;

public abstract class AbstractCaseView extends AbstractView {

	private static final long serialVersionUID = -1L;
	
    protected CaseController viewLogic = ControllerProvider.getCaseController();
    protected CaseEditMenu caseEditMenu;
    protected HorizontalLayout caseEditLayout;
    protected String viewName;

	private String caseUuid;

    protected AbstractCaseView(String viewName) {
    	
        setSizeFull();
        addStyleName("crud-view");
        this.viewName = viewName;
        
        makeCaseEditMenu(null);
        
        caseEditLayout = new HorizontalLayout();
        caseEditLayout.setMargin(true);
        caseEditLayout.setSpacing(true);
        caseEditLayout.setSizeFull();
        addComponent(caseEditLayout,1);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
    	caseUuid = event.getParameters();
    	makeCaseEditMenu(caseUuid);
		selectInMenu();
    };
    
    public void makeCaseEditMenu(String uuid) {
    	caseEditMenu = new CaseEditMenu();
    	caseEditMenu.addView(CasesView.VIEW_NAME, "Overview");
    	caseEditMenu.addView(CaseDataView.VIEW_NAME, "Case Data", uuid);
    	caseEditMenu.addView(PatientInformationView.VIEW_NAME, "Patient Information", uuid);
    	addComponent(caseEditMenu,0);
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
    

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

}
