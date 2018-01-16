package de.symeda.sormas.ui.caze;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.epidata.EpiDataView;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationForm;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationView;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseController {

	private CaseFacade cf = FacadeProvider.getCaseFacade();
	private SymptomsFacade sf = FacadeProvider.getSymptomsFacade();
	private HospitalizationFacade hf = FacadeProvider.getHospitalizationFacade();
	private EpiDataFacade edf = FacadeProvider.getEpiDataFacade();
	private ContactFacade conf = FacadeProvider.getContactFacade();
	
    public CaseController() {
    	
    }
    
    public void registerViews(Navigator navigator) {
    	navigator.addView(CasesView.VIEW_NAME, CasesView.class);
    	navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
    	navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
    	navigator.addView(CaseSymptomsView.VIEW_NAME, CaseSymptomsView.class);
    	if (LoginHelper.hasUserRight(UserRight.CONTACT_VIEW)) {
    		navigator.addView(CaseContactsView.VIEW_NAME, CaseContactsView.class);
    	}
    	navigator.addView(CaseHospitalizationView.VIEW_NAME, CaseHospitalizationView.class);
    	navigator.addView(EpiDataView.VIEW_NAME, EpiDataView.class);
    }
    
    public void create() {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(null, null, null);
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
    }
    
    public void create(PersonReferenceDto person, Disease disease) {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(person, disease, null);
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case"); 
    }
    
    public void create(PersonReferenceDto person, Disease disease, ContactDto contact) {
    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(person, disease, contact);
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");
    }
    
    public void navigateToData(String caseUuid) {
   		String navigationState = CaseDataView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }
    
    public Link createLinkToData(String caseUuid, String caption) {
    	Link link = new Link(caption, new ExternalResource("#!" + CaseDataView.VIEW_NAME + "/" + caseUuid));
    	return link;
    }

    public void navigateToSymptoms(String caseUuid) {
   		String navigationState = CaseSymptomsView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void navigateToPerson(String caseUuid) {
   		String navigationState = CasePersonView.VIEW_NAME + "/" + caseUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }

    public void navigateToHospitalization(String caseUuid) {
    	String navigationState = CaseHospitalizationView.VIEW_NAME + "/" + caseUuid;
    	SormasUI.get().getNavigator().navigateTo(navigationState);
    }
    
    public void navigateToEpiData(String caseUuid) {
    	String navigationState = EpiDataView.VIEW_NAME + "/" + caseUuid;
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

    private CaseDataDto findCase(String uuid) {
        return cf.getCaseDataByUuid(uuid);
    }

    private CaseDataDto createNewCase(PersonReferenceDto person, Disease disease) {
    	CaseDataDto caze = new CaseDataDto();
    	caze.setUuid(DataHelper.createUuid());
    	
    	if(person != null) {
    		caze.setPerson(person);
    	}
    	if(disease == null) {
    		caze.setDisease(Disease.EVD);
    	} else {
    		caze.setDisease(disease);
    	}
    	caze.setInvestigationStatus(InvestigationStatus.PENDING);
    	caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
    	caze.setOutcome(CaseOutcome.NO_OUTCOME);
    	
    	caze.setReportDate(new Date());
    	UserDto user = LoginHelper.getCurrentUser();
    	UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
    	caze.setReportingUser(userReference);
    	caze.setRegion(user.getRegion());
    	caze.setDistrict(user.getDistrict());
    	
    	return caze;
    }
    
    public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent(PersonReferenceDto person, Disease disease, ContactDto contact) {
    	
    	CaseCreateForm createForm = new CaseCreateForm(UserRight.CASE_CREATE);
    	CaseDataDto caze = createNewCase(person, disease);
        createForm.setValue(caze);
        
        if (person != null) {
        	createForm.setPerson(FacadeProvider.getPersonFacade().getPersonByUuid(person.getUuid()));
        	createForm.setNameReadOnly(true);
        }
        if (contact != null) {
        	createForm.setDiseaseReadOnly(true);
        }
        final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(createForm, createForm.getFieldGroup(), UserRight.CASE_CREATE);
       
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (!createForm.getFieldGroup().isModified()) {
        			final CaseDataDto dto = createForm.getValue();
        			// Generate EPID number prefix
    	    		Calendar calendar = Calendar.getInstance();
    	    		String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
    	    		RegionDto region = FacadeProvider.getRegionFacade().getRegionByUuid(dto.getRegion().getUuid());
    	    		DistrictDto district = FacadeProvider.getDistrictFacade().getDistrictByUuid(dto.getDistrict().getUuid());
    	    		dto.setEpidNumber(region.getEpidCode() != null ? region.getEpidCode() : "" 
    	    				+ "-" + district.getEpidCode() != null ? district.getEpidCode() : "" 
    	    				+ "-" + year + "-");
        			
        			if (contact != null) {
        				// automatically change the contact classification to "converted"
						contact.setContactClassification(ContactClassification.CONVERTED);
						conf.saveContact(contact);

						// use the person of the contact we are creating a case for
        				dto.setPerson(person);
        				cf.saveCase(dto);        				
	        			Notification.show("New case created", Type.ASSISTIVE_NOTIFICATION);
	        			navigateToPerson(dto.getUuid());
        			} else {
	        			ControllerProvider.getPersonController().selectOrCreatePerson(
	        					createForm.getPersonFirstName(), createForm.getPersonLastName(), 
	        					person -> {
	        						if (person != null) {
		        						dto.setPerson(person);
		        						cf.saveCase(dto);
		        	        			Notification.show("New case created", Type.ASSISTIVE_NOTIFICATION);
		        	        			navigateToPerson(dto.getUuid());
	        						}
	        					});
					}
        		}
        	}
        });
        
        return editView;
    }

    public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(final String caseUuid) {
    	CaseDataDto caze = findCase(caseUuid);
    	CaseDataForm caseEditForm = new CaseDataForm(FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid()), caze.getDisease(), UserRight.CASE_EDIT);
        caseEditForm.setValue(caze);
        final CommitDiscardWrapperComponent<CaseDataForm> editView = new CommitDiscardWrapperComponent<CaseDataForm>(caseEditForm, caseEditForm.getFieldGroup(), UserRight.CASE_EDIT);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (!caseEditForm.getFieldGroup().isModified()) {
        			final CaseDataDto cazeDto = caseEditForm.getValue();   
        			saveCase(cazeDto);
        		}
        	}
        });
        
        if (LoginHelper.getCurrentUserRoles().contains(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getCaseFacade().deleteCase(caze.toReference(), LoginHelper.getCurrentUserAsReference().getUuid());
					UI.getCurrent().getNavigator().navigateTo(CasesView.VIEW_NAME);
				}
			}, I18nProperties.getFieldCaption("Case"));
		}
        
        // Initialize 'Move case to another health facility' button
        if (LoginHelper.hasUserRight(UserRight.CASE_MOVE)) {
	        Button moveCaseButton = new Button();
	        moveCaseButton.addStyleName(ValoTheme.BUTTON_LINK);
	        moveCaseButton.setCaption("Move case to another health facility");
	        moveCaseButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					caseEditForm.commit();
					CaseDataDto cazeDto = caseEditForm.getValue();
					cazeDto = cf.saveCase(cazeDto);
					moveCase(cazeDto);
				}
			});
	        
	        editView.getButtonsPanel().addComponentAsFirst(moveCaseButton);
	        editView.getButtonsPanel().setComponentAlignment(moveCaseButton, Alignment.BOTTOM_LEFT);
        }
        
        return editView;
    }

	public CommitDiscardWrapperComponent<SymptomsForm> getCaseSymptomsEditComponent(final String caseUuid) {
    	
        CaseDataDto caseDataDto = findCase(caseUuid);
        PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());

    	SymptomsForm symptomsForm = new SymptomsForm(caseDataDto.getDisease(), person, SymptomsContext.CASE, UserRight.CASE_EDIT);
        symptomsForm.setValue(caseDataDto.getSymptoms());
    	symptomsForm.initializeSymptomRequirementsForCase();
        final CommitDiscardWrapperComponent<SymptomsForm> editView = new CommitDiscardWrapperComponent<SymptomsForm>(symptomsForm, symptomsForm.getFieldGroup(), UserRight.CASE_EDIT);
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (!symptomsForm.getFieldGroup().isModified()) {
        			SymptomsDto dto = symptomsForm.getValue();
        			saveSymptoms(dto, caseUuid);
        		}
        	}
        });
        
        return editView;
    }    
	
	public CommitDiscardWrapperComponent<CaseHospitalizationForm> getCaseHospitalizationComponent(final String caseUuid) {
		CaseDataDto caze = findCase(caseUuid);
		CaseHospitalizationForm hospitalizationForm = new CaseHospitalizationForm(caze, UserRight.CASE_EDIT);
		hospitalizationForm.setValue(caze.getHospitalization());
	
		final CommitDiscardWrapperComponent<CaseHospitalizationForm> editView = new CommitDiscardWrapperComponent<CaseHospitalizationForm>(hospitalizationForm, hospitalizationForm.getFieldGroup(), UserRight.CASE_EDIT);
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!hospitalizationForm.getFieldGroup().isModified()) {
					HospitalizationDto dto = hospitalizationForm.getValue();
					hf.saveHospitalization(dto);
					Notification.show("Case hospitalization saved", Type.WARNING_MESSAGE);
					navigateToHospitalization(caseUuid);
				}
			}
		});
		
		return editView;
	}
	
	public CommitDiscardWrapperComponent<EpiDataForm> getEpiDataComponent(final String caseUuid) {
		CaseDataDto caze = findCase(caseUuid);
		EpiDataForm epiDataForm = new EpiDataForm(caze.getDisease(), UserRight.CASE_EDIT);
		epiDataForm.setValue(caze.getEpiData());
		
		final CommitDiscardWrapperComponent<EpiDataForm> editView = new CommitDiscardWrapperComponent<EpiDataForm>(epiDataForm, epiDataForm.getFieldGroup(), UserRight.CASE_EDIT);
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!epiDataForm.getFieldGroup().isModified()) {
					EpiDataDto dto = epiDataForm.getValue();
					edf.saveEpiData(dto);
					Notification.show("Case epidemiological data saved", Type.WARNING_MESSAGE);
					navigateToEpiData(caseUuid);
				}
			}
		});
		
		return editView;
	}
	
	public void moveCase(CaseDataDto caze) {
		CaseFacilityChangeForm facilityChangeForm = new CaseFacilityChangeForm(UserRight.CASE_MOVE);
		facilityChangeForm.setValue(caze);
		CommitDiscardWrapperComponent<CaseFacilityChangeForm> facilityChangeView = new CommitDiscardWrapperComponent<CaseFacilityChangeForm>(facilityChangeForm, facilityChangeForm.getFieldGroup(), UserRight.CASE_MOVE);
		facilityChangeView.getCommitButton().setCaption("Move case");
		facilityChangeView.setMargin(true);
		
		Window popupWindow = VaadinUiUtil.showPopupWindow(facilityChangeView);
		popupWindow.setCaption("Move case to another health facility");
		
		facilityChangeView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!facilityChangeForm.getFieldGroup().isModified()) {
					CaseDataDto dto = facilityChangeForm.getValue();
					cf.moveCase(cf.getReferenceByUuid(dto.getUuid()), dto.getCommunity(), dto.getHealthFacility(), dto.getHealthFacilityDetails(), dto.getSurveillanceOfficer());
					popupWindow.close();
					Notification.show("Case has been moved to the new facility", Type.WARNING_MESSAGE);
					navigateToData(caze.getUuid());
				}
			}
		});
		
		Button cancelButton = new Button("cancel");
		cancelButton.setStyleName(ValoTheme.BUTTON_LINK);
		cancelButton.addClickListener(e -> {
			popupWindow.close();
		});
		facilityChangeView.getButtonsPanel().replaceComponent(facilityChangeView.getDiscardButton(), cancelButton);
	}
	
	private void saveCase(CaseDataDto cazeDto) {
		cazeDto = cf.saveCase(cazeDto);
		Notification.show("Case data saved", Type.WARNING_MESSAGE);
		navigateToData(cazeDto.getUuid());
	}
	
	private void saveSymptoms(SymptomsDto symptomsDto, String caseUuid) {
		sf.saveSymptoms(symptomsDto);
		
		CaseDataDto caseDataDto = findCase(caseUuid);		
		if (caseDataDto.getDisease() == Disease.PLAGUE) {
			PlagueType plagueType = DiseaseHelper.getPlagueTypeForSymptoms(caseDataDto.getSymptoms());
			if (plagueType != caseDataDto.getPlagueType() && plagueType != null) {
				openPlagueTypeChangeLayout(plagueType, caseDataDto);
				return;
			}
		}
		
		Notification.show("Case symptoms saved", Type.WARNING_MESSAGE);
		navigateToSymptoms(caseUuid);
	}
	
	private void openPlagueTypeChangeLayout(PlagueType plagueType, CaseDataDto caseDto) {
		ConfirmationComponent plagueTypeChangeComponent = new ConfirmationComponent(false) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfirm() {
			}
			@Override
			protected void onCancel() {
			}
		};
		plagueTypeChangeComponent.getConfirmButton().setCaption("Confirm");
		plagueTypeChangeComponent.removeComponent(plagueTypeChangeComponent.getCancelButton());

		VerticalLayout layout = new VerticalLayout();	
		layout.setMargin(true);
		Label description = new Label("The symptoms selected match the clinical criteria for " + plagueType.toString() + ". "
				+ "The plague type has been set to " + plagueType.toString() + " for this case.");
		description.setContentMode(ContentMode.HTML);
		description.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(description);
		layout.addComponent(plagueTypeChangeComponent);
		layout.setComponentAlignment(plagueTypeChangeComponent, Alignment.BOTTOM_RIGHT);
		layout.setSizeUndefined();
		layout.setSpacing(true);
		
		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setSizeUndefined();
		popupWindow.setCaption("Confirm Plague Type Change");
		plagueTypeChangeComponent.getConfirmButton().addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				popupWindow.close();
				caseDto.setPlagueType(plagueType);
				cf.saveCase(caseDto);
				Notification.show("Case symptoms saved", Type.WARNING_MESSAGE);
				navigateToSymptoms(caseDto.getUuid());
			}
		});
	}
	
}
