package de.symeda.sormas.ui.surveillance.caze;

import java.io.Serializable;

import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CaseHelper;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class CaseController implements Serializable {

	private static final long serialVersionUID = 1L;
	private CasesView view;
	private CaseForm caseEditForm;

    public CaseController(CasesView simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        showCaseNavigation(null);
        // Hide and disable if not admin
        if (!SurveillanceUI.get().getAccessControl().isUserInRole("admin")) {
            view.setNewCaseEnabled(false);
        }
        
        // Create demo-content
        FacadeProvider.getCaseFacade().createDemo();
        
        showCaseOverView();
    }

    public void cancelCase() {
        setFragmentParameter("");
        view.clearSelection();
        view.edit(null);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String caseUuid) {
        String fragmentParameter;
        if (caseUuid == null || caseUuid.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = caseUuid;
        }

        Page page = SurveillanceUI.get().getPage();
        page.setUriFragment("!" + CasesView.VIEW_NAME + "/"
                + fragmentParameter, false);
    }

    public void enter(String uuidOrNew) {
        if (uuidOrNew != null && !uuidOrNew.isEmpty()) {
            if (uuidOrNew.equals("new")) {
                newCase();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    CaseDto caze = findCase(uuidOrNew);
                    view.selectRow(caze);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private CaseDto findCase(String uuid) {
        return FacadeProvider.getCaseFacade().getByUuid(uuid);
    }
    
    
    public void showCaseOverView() {
		view.clearSelection();
		view.edit(null);
		view.show(FacadeProvider.getCaseFacade().getAllCases());
	}

    public void showCaseNavigation(CaseDto caze) {
    	view.clearSelection();
        if (caze == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(caze.getUuid());
        }
        view.edit(caze);
        caseEditForm.editCase(caze);
    }

    public void newCase() {
        view.clearSelection();
        setFragmentParameter("new");
        view.edit(new CaseDto());
    }
    
    public void updateCase(CaseDto caze) {
    	//FacadeProvider.getCaseFacade().update(caze);
        view.showSaveNotification(CaseHelper.getShortUuid(caze) + " updated");
        view.clearSelection();
        view.edit(null);
        view.refresh(caze);
        setFragmentParameter("");
    }

    public void deleteProduct(CaseDto caze) {
        //FacadeProvider.getCaseFacade().delete(caze.getUuid());
        view.showSaveNotification(CaseHelper.getShortUuid(caze) + " removed");

        view.clearSelection();
        view.edit(null);
        view.remove(caze);
        setFragmentParameter("");
    }

    public void rowSelected(CaseDto product) {
        if (SurveillanceUI.get().getAccessControl().isUserInRole("admin")) {
            showCaseNavigation(product);
        }
    }
    
    
    
    public Component getCaseDataView() {
    	VerticalLayout formLayout = new VerticalLayout();
        caseEditForm = new CaseForm(this);
        formLayout.addComponent(caseEditForm);
        formLayout.setSizeFull();
        formLayout.setExpandRatio(caseEditForm, 1);
        return formLayout;
    }

	
}
