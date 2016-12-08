package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseController {

	private CaseFacade cf = FacadeProvider.getCaseFacade();
	private SymptomsFacade sf = FacadeProvider.getSymptomsFacade();
	
    public CaseController() {
    	
    }
    
    public void registerViews(Navigator navigator) {
    	navigator.addView(CasesView.VIEW_NAME, CasesView.class);
    	navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
    	navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
    	navigator.addView(CaseSymptomsView.VIEW_NAME, CaseSymptomsView.class);
    	navigator.addView(CaseContactsView.VIEW_NAME, CaseContactsView.class);
	}
    
    public void create() {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent();
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
    }
    
    public void navigateToData(String caseUuid) {
   		String navigationState = CaseDataView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void navigateToSymptoms(String caseUuid) {
   		String navigationState = CaseSymptomsView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void nativagateToPerson(String caseUuid) {
   		String navigationState = CasePersonView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }
    
    public void navigateToIndex() {
    	String navigationState = CasesView.VIEW_NAME;
    	SormasUI.get().getNavigator().navigateTo(navigationState);
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

        Page page = SormasUI.get().getPage();
        page.setUriFragment("!" + CasesView.VIEW_NAME + "/"
                + fragmentParameter, false);
    }
    

    public List<CaseDataDto> getCaseIndexList() {
    	UserDto user = LoginHelper.getCurrentUser();
    	return FacadeProvider.getCaseFacade().getAllCasesAfter(null, user.getUuid());
    }
    
    private CaseDataDto findCase(String uuid) {
        return cf.getCaseDataByUuid(uuid);
    }

    private CaseDataDto createNewCase() {
    	CaseDataDto caze = new CaseDataDto();
    	caze.setUuid(DataHelper.createUuid());
    	
    	caze.setDisease(Disease.EVD);
    	caze.setCaseStatus(CaseStatus.POSSIBLE);
    	
    	caze.setReportDate(new Date());
    	UserDto user = LoginHelper.getCurrentUser();
    	UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
    	caze.setReportingUser(userReference);
    	caze.setRegion(user.getRegion());
    	caze.setDistrict(user.getDistrict());
    	
    	return caze;
    }
    
    public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent() {
    	
    	CaseCreateForm caseCreateForm = new CaseCreateForm();
        caseCreateForm.setValue(createNewCase());
        final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(caseCreateForm, caseCreateForm.getFieldGroup());
        editView.setWidth(520, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (caseCreateForm.getFieldGroup().isValid()) {
        			CaseDataDto dto = caseCreateForm.getValue();
        			cf.saveCase(dto);
        			Notification.show("New case created", Type.TRAY_NOTIFICATION);
        			navigateToData(dto.getUuid());
        		}
        	}
        });
        
        return editView;
    }

    public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(final String caseUuid) {
    	
    	CaseDataForm caseEditForm = new CaseDataForm();
    	CaseDataDto caze = findCase(caseUuid);
        caseEditForm.setValue(caze);
        final CommitDiscardWrapperComponent<CaseDataForm> editView = new CommitDiscardWrapperComponent<CaseDataForm>(caseEditForm, caseEditForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (caseEditForm.getFieldGroup().isValid()) {
        			CaseDataDto cazeDto = caseEditForm.getValue();
        			cazeDto = cf.saveCase(cazeDto);
        			Notification.show("Case data saved", Type.TRAY_NOTIFICATION);
        			navigateToData(cazeDto.getUuid());
        		}
        	}
        });
        
        caseEditForm.setStatusChangeButtons(caze.getCaseStatus(),
        		CaseHelper.getPossibleStatusChanges(caze.getCaseStatus(), UserRole.SURVEILLANCE_SUPERVISOR), 
        		status -> {
        			if (editView.isModified()) {
        				editView.commit();
        			}
        			CaseDataDto cazeDto = cf.changeCaseStatus(caseUuid, status);
        			navigateToData(cazeDto.getUuid()); // might be done twice - that's ok		
        		});
        
        return editView;
    }

	public CommitDiscardWrapperComponent<SymptomsForm> getCaseSymptomsEditComponent(final String caseUuid) {
    	
        CaseDataDto caseDataDto = findCase(caseUuid);

        VerticalLayout formLayout = new VerticalLayout();
    	SymptomsForm symptomsForm = new SymptomsForm(caseDataDto.getDisease());
        formLayout.addComponent(symptomsForm);
        formLayout.setSizeFull();
        formLayout.setExpandRatio(symptomsForm, 1);
        
        symptomsForm.setValue(caseDataDto.getSymptoms());
        
        final CommitDiscardWrapperComponent<SymptomsForm> editView = new CommitDiscardWrapperComponent<SymptomsForm>(symptomsForm, symptomsForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (symptomsForm.getFieldGroup().isValid()) {
        			SymptomsDto dto = symptomsForm.getValue();
        			dto = sf.saveSymptoms(dto);
        			Notification.show("Case symptoms saved", Type.TRAY_NOTIFICATION);
        			navigateToSymptoms(caseUuid);
        		}
        	}
        });
        
        return editView;
    }    
}
