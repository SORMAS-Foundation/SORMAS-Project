package de.symeda.sormas.ui.surveillance.navigation;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.surveillance.caze.CaseController;

public class CaseNavigation extends TabSheet {

	private static final long serialVersionUID = 5368303509266105L;
	
	private static final String OVERVIEW = "Overview";
	private static final String CASE_DATA = "Case data";
	private static final String PATIENT_INFORMATION = "Patient information";
	
	private CaseController caseController;
	
	public CaseNavigation(CaseController caseController) {
		this.caseController = caseController;
		init();
		addBackToOverviewListener();
	}

	@SuppressWarnings("serial")
	private void addBackToOverviewListener() {
		this.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
		    public void selectedTabChange(SelectedTabChangeEvent event) {
		        TabSheet tabsheet = event.getTabSheet();
		        String caption = tabsheet.getTab(tabsheet.getSelectedTab()).getCaption();
		        
		        if(OVERVIEW.equals(caption)) {
		        	caseController.showCaseOverView();
				}
		    }
		}); 
		
	}

	private void init() {
		// position 0
		this.addTab(new CssLayout(), OVERVIEW);
		// position 1
		this.addTab(caseController.getCaseDataView(), CASE_DATA);
		// position 2
		VerticalLayout tab2 = new VerticalLayout();
		tab2.addComponent(new Label("its me, the second tab"));
		this.addTab(tab2, PATIENT_INFORMATION);
		
		// preselect the case data tab
		setInitialSelectedTab();
	}
	
	public void setInitialSelectedTab() {
		// make sure this numeber matches with the positions in the init method above 
		this.setSelectedTab(1);
	}
	
	
	

}
