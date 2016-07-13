package de.symeda.sormas.ui.surveillance.caze;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseController {

	private PersonFacade pf = FacadeProvider.getPersonFacade();
	private CaseFacade cf = FacadeProvider.getCaseFacade();
	
    public CaseController() {
    	
    }
    
    public boolean isAdmin() {
    	return SurveillanceUI.get().getAccessControl().isUserInRole("admin");
    }

    public void registerViews(Navigator navigator) {
    	navigator.addView(CasesView.VIEW_NAME, CasesView.class);
    	navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
    	navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
	}
    
    public void create() {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent();
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
    }
    
    public void edit(CaseDataDto caze) {
   		String navigationState = CaseDataView.VIEW_NAME + "/" + caze.getUuid();
   		SurveillanceUI.get().getNavigator().navigateTo(navigationState);
	
    }
    
    public void overview(CaseDataDto caze) {
    	String navigationState = CasesView.VIEW_NAME;
    	SurveillanceUI.get().getNavigator().navigateTo(navigationState);
    }

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

    private CaseDataDto createNewCase() {
    	CaseDataDto caze = new CaseDataDto();
    	caze.setReportDate(new Date());
    	caze.setUuid(DataHelper.createUuid());
    	caze.setDisease(Disease.EBOLA);
    	caze.setCaseStatus(CaseStatus.POSSIBLE);
    	return caze;
    }
    
    public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent() {
    	
    	CaseCreateForm caseCreateForm = new CaseCreateForm();
        caseCreateForm.setDto(createNewCase());
        final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(caseCreateForm, caseCreateForm.getFieldGroup());
        editView.setWidth(400, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (caseCreateForm.getFieldGroup().isValid()) {
        			CaseDataDto dto = caseCreateForm.getDto();
        			cf.saveCase(dto);
        			overview(null);
        		}
        	}
        });
        
        return editView;
    }

    public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(String caseUuid) {
    	
    	CaseDataForm caseEditForm = new CaseDataForm();
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
	
	public CommitDiscardWrapperComponent<CasePersonForm> getCasePersonEditComponent(String caseUuid) {
    	
    	
    	VerticalLayout formLayout = new VerticalLayout();
    	CasePersonForm caseEditForm = new CasePersonForm();
        formLayout.addComponent(caseEditForm);
        formLayout.setSizeFull();
        formLayout.setExpandRatio(caseEditForm, 1);
        
        CaseDataDto caseDataDto = findCase(caseUuid);
        CasePersonDto personDto = pf.getCasePersonByUuid(caseDataDto.getPerson().getUuid());
        caseEditForm.setDto(personDto);
        
        final CommitDiscardWrapperComponent<CasePersonForm> editView = new CommitDiscardWrapperComponent<CasePersonForm>(caseEditForm, caseEditForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (caseEditForm.getFieldGroup().isValid()) {
        			CasePersonDto dto = caseEditForm.getDto();
        			pf.savePerson(dto);
        			overview(null);
        		}
        	}
        });
        
        return editView;
    }

    
    
}
