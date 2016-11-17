package de.symeda.sormas.ui.contact;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ContactController {

	private ContactFacade cof = FacadeProvider.getContactFacade();
	
    public ContactController() {
    	
    }
    
    public void registerViews(Navigator navigator) {
    	navigator.addView(ContactsView.VIEW_NAME, ContactsView.class);
    	navigator.addView(ContactDataView.VIEW_NAME, ContactDataView.class);
    	//navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
	}
    
    public void create() {
    	CommitDiscardWrapperComponent<ContactCreateForm> createComponent = getContactCreateComponent();
    	VaadinUiUtil.showModalPopupWindow(createComponent, "Create new contact");    	
    }
    
    public void editData(String contactUuid) {
   		String navigationState = ContactDataView.VIEW_NAME + "/" + contactUuid;
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
    }
    
    public List<ContactIndexDto> getIndexList() {
    	UserDto user = LoginHelper.getCurrentUser();
    	return FacadeProvider.getContactFacade().getIndexList(user.getUuid());
    }


//    public void editPerson(String contactUuid) {
//   		String navigationState = CasePersonView.VIEW_NAME + "/" + contactUuid;
//   		SormasUI.get().getNavigator().navigateTo(navigationState);	
//    }
    
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

    private ContactDto createNewContact() {
    	ContactDto contact = new ContactDto();
    	contact.setUuid(DataHelper.createUuid());
    	
    	contact.setReportDateTime(new Date());
    	UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
    	contact.setReportingUser(userReference);
    	contact.setContactStatus(ContactStatus.FOLLOW_UP);
    	contact.setLastContactDate(new Date());
    	
    	return contact;
    }
    
    public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent() {
    	
    	ContactCreateForm createForm = new ContactCreateForm();
        createForm.setValue(createNewContact());
        final CommitDiscardWrapperComponent<ContactCreateForm> createComponent = new CommitDiscardWrapperComponent<ContactCreateForm>(createForm, createForm.getFieldGroup());
        createComponent.setWidth(520, Unit.PIXELS);
        
        createComponent.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (createForm.getFieldGroup().isValid()) {
        			final ContactDto dto = createForm.getValue();
        			
        			ControllerProvider.getPersonController().selectOrCreatePerson(
        					createForm.getPersonFirstName(), createForm.getPersonLastName(), 
        					person -> {
        						dto.setPerson(person);
        						cof.saveContact(dto);
        	        			Notification.show("New contact created", Type.TRAY_NOTIFICATION);
        	        			editData(dto.getUuid());
        					});
        		}
        	}
        });
        
        return createComponent;
    }

    public CommitDiscardWrapperComponent<ContactDataForm> getContactDataEditComponent(final String contactUuid) {
    	
    	ContactDataForm editForm = new ContactDataForm();
    	ContactDto contact = cof.getContactByUuid(contactUuid);
        editForm.setValue(contact);
        final CommitDiscardWrapperComponent<ContactDataForm> editComponent = new CommitDiscardWrapperComponent<ContactDataForm>(editForm, editForm.getFieldGroup());
        
        editComponent.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (editForm.getFieldGroup().isValid()) {
        			ContactDto dto = editForm.getValue();
        			dto = cof.saveContact(dto);
        			Notification.show("Contact data saved", Type.TRAY_NOTIFICATION);
        			editData(dto.getUuid());
        		}
        	}
        });
        
        return editComponent;
    }
	
//	public CommitDiscardWrapperComponent<CasePersonForm> getCasePersonEditComponent(String caseUuid) {
//    	    	
//    	VerticalLayout formLayout = new VerticalLayout();
//    	CasePersonForm caseEditForm = new CasePersonForm();
//        formLayout.addComponent(caseEditForm);
//        formLayout.setSizeFull();
//        formLayout.setExpandRatio(caseEditForm, 1);
//        
//        CaseDataDto caseDataDto = findCase(caseUuid);
//        CasePersonDto personDto = pf.getCasePersonByUuid(caseDataDto.getPerson().getUuid());
//        caseEditForm.setValue(personDto);
//        
//        final CommitDiscardWrapperComponent<CasePersonForm> editView = new CommitDiscardWrapperComponent<CasePersonForm>(caseEditForm, caseEditForm.getFieldGroup());
//        
//        editView.addCommitListener(new CommitListener() {
//        	
//        	@Override
//        	public void onCommit() {
//        		if (caseEditForm.getFieldGroup().isValid()) {
//        			CasePersonDto dto = caseEditForm.getValue();
//        			dto = pf.savePerson(dto);
//        			Notification.show("Patient information saved", Type.TRAY_NOTIFICATION);
//        			editPerson(dto.getCaseUuid());
//        		}
//        	}
//        });
//        
//        return editView;
//    }

}
