package de.symeda.sormas.ui.surveillance.caze;

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
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseController {

	private PersonFacade pf = FacadeProvider.getPersonFacade();
	private CaseFacade cf = FacadeProvider.getCaseFacade();
	private SymptomsFacade sf = FacadeProvider.getSymptomsFacade();
	
    public CaseController() {
    	
    }
    
    public void registerViews(Navigator navigator) {
    	navigator.addView(CasesView.VIEW_NAME, CasesView.class);
    	navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
    	navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
    	navigator.addView(CaseSymptomsView.VIEW_NAME, CaseSymptomsView.class);
	}
    
    public void create() {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent();
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
    }
    
    public void editData(String caseUuid) {
   		String navigationState = CaseDataView.VIEW_NAME + "/" + caseUuid;
   		SurveillanceUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void editSymptoms(String caseUuid) {
   		String navigationState = CaseSymptomsView.VIEW_NAME + "/" + caseUuid;
   		SurveillanceUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void editPerson(String caseUuid) {
   		String navigationState = CasePersonView.VIEW_NAME + "/" + caseUuid;
   		SurveillanceUI.get().getNavigator().navigateTo(navigationState);	
    }
    
    public void overview() {
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
    	caze.setUuid(DataHelper.createUuid());
    	
    	caze.setDisease(Disease.EBOLA);
    	caze.setCaseStatus(CaseStatus.POSSIBLE);
    	
    	caze.setReportDate(new Date());
    	UserDto user = LoginHelper.getCurrentUser();
    	ReferenceDto userReference = DataHelper.toReferenceDto(user);
    	caze.setReportingUser(userReference);
    	if (user.getUserRoles().contains(UserRole.SURVEILLANCE_SUPERVISOR)) {
    		caze.setSurveillanceSupervisor(userReference);
    	}

    	return caze;
    }
    
    public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent() {
    	
    	CaseCreateForm caseCreateForm = new CaseCreateForm();
        caseCreateForm.setValue(createNewCase());
        final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(caseCreateForm, caseCreateForm.getFieldGroup());
        editView.setWidth(400, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (caseCreateForm.getFieldGroup().isValid()) {
        			CaseDataDto dto = caseCreateForm.getValue();
        			cf.saveCase(dto);
        			Notification.show("New case created", Type.TRAY_NOTIFICATION);
        			editData(dto.getUuid());
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
        			editData(cazeDto.getUuid());
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
        			editData(cazeDto.getUuid()); // might be done twice - that's ok		
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
        caseEditForm.setValue(personDto);
        
        final CommitDiscardWrapperComponent<CasePersonForm> editView = new CommitDiscardWrapperComponent<CasePersonForm>(caseEditForm, caseEditForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (caseEditForm.getFieldGroup().isValid()) {
        			CasePersonDto dto = caseEditForm.getValue();
        			dto = pf.savePerson(dto);
        			Notification.show("Patient information saved", Type.TRAY_NOTIFICATION);
        			editPerson(dto.getCaseUuid());
        		}
        	}
        });
        
        return editView;
    }

	public CommitDiscardWrapperComponent<CaseSymptomsForm> getCaseSymptomsEditComponent(final String caseUuid) {
    	
    	VerticalLayout formLayout = new VerticalLayout();
    	CaseSymptomsForm caseEditForm = new CaseSymptomsForm();
        formLayout.addComponent(caseEditForm);
        formLayout.setSizeFull();
        formLayout.setExpandRatio(caseEditForm, 1);
        
        CaseDataDto caseDataDto = findCase(caseUuid);
        caseEditForm.setValue(caseDataDto.getSymptoms());
        
        final CommitDiscardWrapperComponent<CaseSymptomsForm> editView = new CommitDiscardWrapperComponent<CaseSymptomsForm>(caseEditForm, caseEditForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (caseEditForm.getFieldGroup().isValid()) {
        			SymptomsDto dto = caseEditForm.getValue();
        			dto = sf.saveSymptoms(dto);
        			Notification.show("Case symptoms saved", Type.TRAY_NOTIFICATION);
        			editSymptoms(caseUuid);
        		}
        	}
        });
        
        return editView;
    }    
}
