package de.symeda.sormas.ui.contact;

import java.util.Date;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ContactController {

	private ContactFacade cof = FacadeProvider.getContactFacade();
	
    public ContactController() {
    	
    }
    
    public void registerViews(Navigator navigator) {
    	navigator.addView(ContactsView.VIEW_NAME, ContactsView.class);
    	navigator.addView(ContactDataView.VIEW_NAME, ContactDataView.class);
    	navigator.addView(ContactPersonView.VIEW_NAME, ContactPersonView.class);
    	navigator.addView(ContactVisitsView.VIEW_NAME, ContactVisitsView.class);
	}

    public void create() {
    	create(null);
    }
    public void create(CaseReferenceDto caze) {
    	CommitDiscardWrapperComponent<ContactCreateForm> createComponent = getContactCreateComponent(caze);
    	VaadinUiUtil.showModalPopupWindow(createComponent, "Create new contact");    	
    }
    
    public void navigateToData(String contactUuid) {
   		String navigationState = ContactDataView.VIEW_NAME + "/" + contactUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }
    
    public void editData(String contactUuid) {
   		String navigationState = ContactDataView.VIEW_NAME + "/" + contactUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }
    
    public void editPerson(String contactUuid) {
   		String navigationState = ContactPersonView.VIEW_NAME + "/" + contactUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }
    
    public void overview() {
    	String navigationState = ContactsView.VIEW_NAME;
    	SormasUI.get().getNavigator().navigateTo(navigationState);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    public void setUriFragmentParameter(String contactUuid) {
        String fragmentParameter;
        if (contactUuid == null || contactUuid.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = contactUuid;
        }

        Page page = SormasUI.get().getPage();
        page.setUriFragment("!" + ContactsView.VIEW_NAME + "/"
                + fragmentParameter, false);
    }

    private ContactDto createNewContact(CaseReferenceDto caze) {
    	ContactDto contact = new ContactDto();
    	contact.setUuid(DataHelper.createUuid());
    	
    	contact.setCaze(caze);
    	
    	contact.setReportDateTime(new Date());
    	UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
    	contact.setReportingUser(userReference);
    	contact.setContactClassification(ContactClassification.POSSIBLE);
    	
    	return contact;
    }
    
    public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(CaseReferenceDto caze) {
    	
    	ContactCreateForm createForm = new ContactCreateForm();
        createForm.setValue(createNewContact(caze));
        final CommitDiscardWrapperComponent<ContactCreateForm> createComponent = new CommitDiscardWrapperComponent<ContactCreateForm>(createForm, createForm.getFieldGroup());
        
        createComponent.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (!createForm.getFieldGroup().isModified()) {
        			final ContactDto dto = createForm.getValue();
        			
        			ControllerProvider.getPersonController().selectOrCreatePerson(
        					createForm.getPersonFirstName(), createForm.getPersonLastName(), 
        					person -> {
        						if (person != null) {
	        						dto.setPerson(person);
	        						
        							// set the contact person's address to the one of the case when it is currently empty and
        		        			// the relationship with the case has been set to living in the same household
        							if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD) {
        								PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(person.getUuid());
        								if (personDto.getAddress().isEmptyLocation()) {
        									CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caze.getUuid());
	        	        					personDto.getAddress().setRegion(caseDto.getRegion());
	        	        					personDto.getAddress().setDistrict(caseDto.getDistrict());
	        	        					personDto.getAddress().setCommunity(caseDto.getCommunity());
        								}
        								FacadeProvider.getPersonFacade().savePerson(personDto);
        							}
        							
	        						cof.saveContact(dto);
	        	        			Notification.show("New contact created", Type.WARNING_MESSAGE);
	        	        			editData(dto.getUuid());
        						}
        					});
        		}
        	}
        });
        
        return createComponent;
    }

    public CommitDiscardWrapperComponent<ContactDataForm> getContactDataEditComponent(String contactUuid) {
    	
    	ContactDataForm editForm = new ContactDataForm();
		//editForm.setWidth(editForm.getWidth() * 8/12, Unit.PIXELS);
    	ContactDto contact = cof.getContactByUuid(contactUuid);
        editForm.setValue(contact);
        final CommitDiscardWrapperComponent<ContactDataForm> editComponent = new CommitDiscardWrapperComponent<ContactDataForm>(editForm, editForm.getFieldGroup());
        
        editComponent.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (!editForm.getFieldGroup().isModified()) {
        			ContactDto dto = editForm.getValue();
        			
        			// set the contact person's address to the one of the case when it is currently empty and
        			// the relationship with the case has been set to living in the same household
        			if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD) {
        				PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(dto.getPerson().getUuid());
        				if (person.getAddress().isEmptyLocation()) {
        					CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(dto.getCaze().getUuid());
        					person.getAddress().setRegion(caze.getRegion());
        					person.getAddress().setDistrict(caze.getDistrict());
        					person.getAddress().setCommunity(caze.getCommunity());
        				}
        				FacadeProvider.getPersonFacade().savePerson(person);
        			}
        			
        			dto = cof.saveContact(dto);
        			Notification.show("Contact data saved", Type.WARNING_MESSAGE);
        			editData(dto.getUuid());
        		}
        	}
        });
        
        if (LoginHelper.getCurrentUserRoles().contains(UserRole.ADMIN)) {
			editComponent.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getContactFacade().deleteContact(contact.toReference(), LoginHelper.getCurrentUserAsReference().getUuid());
					UI.getCurrent().getNavigator().navigateTo(ContactsView.VIEW_NAME);
				}
			}, I18nProperties.getFieldCaption("Contact"));
		}
        
        return editComponent;
    }
    
}
