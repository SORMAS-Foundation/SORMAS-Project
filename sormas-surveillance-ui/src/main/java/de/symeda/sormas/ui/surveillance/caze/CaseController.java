package de.symeda.sormas.ui.surveillance.caze;

import java.io.Serializable;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;

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

	private PersonFacade pf = FacadeProvider.getPersonFacade();
	private CaseFacade cf = FacadeProvider.getCaseFacade();
	
	private static final long serialVersionUID = 1L;
	
    public CaseController() {
    	
    }
    
    public boolean isAdmin() {
    	return SurveillanceUI.get().getAccessControl().isUserInRole("admin");
    }

    public void init() {
        // Create demo-content
        registerViews();
    }
    
    private void registerViews() {
		Navigator navigator = SurveillanceUI.get().getNavigator();
    	navigator.addView(CasesView.VIEW_NAME, CasesView.class);
    	navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
    	navigator.addView(PatientInformationView.VIEW_NAME, PatientInformationView.class);
	}
    
    public void edit(CaseDataDto caze) {
   		String navigationState = CaseDataView.VIEW_NAME + "/" + caze.getUuid();
   		SurveillanceUI.get().getNavigator().navigateTo(navigationState);
	
    }
    
    public void overview(CaseDataDto caze) {
    	String navigationState = CasesView.VIEW_NAME;
    	SurveillanceUI.get().getNavigator().navigateTo(navigationState);
    }
    
//  public void updateCase(CaseDataDto caze) {
//	//FacadeProvider.getCaseFacade().update(caze);
//    view.showSaveNotification(CaseHelper.getShortUuid(caze) + " updated");
//    view.clearSelection();
//    view.edit(null);
//    view.refresh(caze);
//    setUriFragmentParameter("");
//}
    


    /**
     * Update the fragment without causing navigator to change view
     */
    public void setUriFragmentParameter(String caseUuid) {
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
    

    public List<CaseDataDto> getAllCaseData() {
    	return FacadeProvider.getCaseFacade().getAllCases();
    }
    
    private CaseDataDto findCase(String uuid) {
        return cf.getCaseDataByUuid(uuid);
    }
    
    


//    public void newCase() {
//        view.clearSelection();
//        setUriFragmentParameter("new");
//        view.edit(new CaseDataDto());
//    }
    

//
//    public void deleteProduct(CaseDataDto caze) {
//        //FacadeProvider.getCaseFacade().delete(caze.getUuid());
//        view.showSaveNotification(CaseHelper.getShortUuid(caze) + " removed");
//
//        view.clearSelection();
//        view.edit(null);
//        view.remove(caze);
//        setUriFragmentParameter("");
//    }

    public void rowSelected(CaseDataDto caseDataDto) {
        if (isAdmin()) {
            edit(caseDataDto);
        }
    }
    
    public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(String caseUuid) {
    	
    	VerticalLayout formLayout = new VerticalLayout();
    	CaseDataForm caseEditForm = new CaseDataForm();
        formLayout.addComponent(caseEditForm);
        formLayout.setSizeFull();
        formLayout.setExpandRatio(caseEditForm, 1);
        
        caseEditForm.setDto(findCase(caseUuid));
        
        final CommitDiscardWrapperComponent<CaseDataForm> editView = new CommitDiscardWrapperComponent<CaseDataForm>(caseEditForm, caseEditForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (caseEditForm.getFieldGroup().isValid()) {
        			CaseDataDto dto = caseEditForm.getDto();
        			cf.saveCase(dto);
        			overview(null);
        		}
        	}
        });
        
        return editView;
    }
	
	public CommitDiscardWrapperComponent<PatientInformationForm> getPatientInformationEditComponent(String caseUuid) {
    	
    	
    	VerticalLayout formLayout = new VerticalLayout();
    	PatientInformationForm caseEditForm = new PatientInformationForm();
        formLayout.addComponent(caseEditForm);
        formLayout.setSizeFull();
        formLayout.setExpandRatio(caseEditForm, 1);
        
        CaseDataDto caseDataDto = findCase(caseUuid);
        PersonDto personDto = pf.getByUuid(caseDataDto.getPerson().getUuid());
        caseEditForm.setDto(personDto);
        
        final CommitDiscardWrapperComponent<PatientInformationForm> editView = new CommitDiscardWrapperComponent<PatientInformationForm>(caseEditForm, caseEditForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (caseEditForm.getFieldGroup().isValid()) {
        			PersonDto dto = caseEditForm.getDto();
        			pf.savePerson(dto);
        			overview(null);
        		}
        	}
        });
        
        return editView;
    }

    
    
}
