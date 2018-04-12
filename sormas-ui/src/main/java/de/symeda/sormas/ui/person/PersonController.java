package de.symeda.sormas.ui.person;

import java.util.function.Consumer;

import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.events.EventParticipantsView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class PersonController {

	private PersonFacade personFacade = FacadeProvider.getPersonFacade();
	
    public PersonController() {
    	
    }

    public void create(Consumer<PersonReferenceDto> doneConsumer) {
    	create("", "", doneConsumer);
    }

    public void create(String firstName, String lastName, Consumer<PersonReferenceDto> doneConsumer) {
    	PersonDto person = createNewPerson();
    	person.setFirstName(firstName);
    	person.setLastName(lastName);
    	
    	person = personFacade.savePerson(person);
    	doneConsumer.accept(person.toReference()); 
    }
    
    public void selectOrCreatePerson(String firstName, String lastName, Consumer<PersonReferenceDto> resultConsumer) {
    	PersonSelectField personSelect = new PersonSelectField();
    	personSelect.setFirstName(firstName);
    	personSelect.setLastName(lastName);
    	personSelect.setWidth(1024, Unit.PIXELS);

    	if (personSelect.hasMatches()) {
    		personSelect.selectBestMatch();
            // TODO add user right parameter
	    	final CommitDiscardWrapperComponent<PersonSelectField> selectOrCreateComponent = 
	    			new CommitDiscardWrapperComponent<PersonSelectField>(personSelect);
	    	
	    	selectOrCreateComponent.addCommitListener(new CommitListener() {
	        	@Override
	        	public void onCommit() {
	        		PersonIndexDto person = personSelect.getValue();
	        		if (person != null) {
	        			if (resultConsumer != null) {
	        				resultConsumer.accept(person.toReference());
	        			}
	        		} else {	
	        			create(personSelect.getFirstName(), personSelect.getLastName(), resultConsumer);
	        		}
	        	}
	        });
        
	    	VaadinUiUtil.showModalPopupWindow(selectOrCreateComponent, "Pick or create person");
    	} else {
    		create(personSelect.getFirstName(), personSelect.getLastName(), resultConsumer);
    	}
    }
    
    private PersonDto createNewPerson() {
    	PersonDto person = new PersonDto();
    	person.setUuid(DataHelper.createUuid());
    	return person;
    }
    
    public CommitDiscardWrapperComponent<PersonCreateForm> getPersonCreateComponent(PersonDto person, UserRight editOrCreateUserRight) {
    	
    	PersonCreateForm createForm = new PersonCreateForm(editOrCreateUserRight);
        createForm.setValue(person);
        final CommitDiscardWrapperComponent<PersonCreateForm> editComponent = new CommitDiscardWrapperComponent<PersonCreateForm>(createForm, createForm.getFieldGroup());
        
        editComponent.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (!createForm.getFieldGroup().isModified()) {
        			PersonDto dto = createForm.getValue();
        			personFacade.savePerson(dto);
        		}
        	}
        });
        
        return editComponent;
    }  
    
	
	public CommitDiscardWrapperComponent<PersonEditForm> getPersonEditComponent(String personUuid, Disease disease, String diseaseDetails, UserRight editOrCreateUserRight, final ViewMode viewMode) {
    	PersonEditForm personEditForm = new PersonEditForm(disease, diseaseDetails, editOrCreateUserRight, viewMode);
        
        PersonDto personDto = personFacade.getPersonByUuid(personUuid);
        personEditForm.setValue(personDto);
        
        return getPersonEditView(personEditForm, editOrCreateUserRight);
    }
	
	private CommitDiscardWrapperComponent<PersonEditForm> getPersonEditView(PersonEditForm editForm, UserRight editOrCreateUserRight) {
		final CommitDiscardWrapperComponent<PersonEditForm> editView = new CommitDiscardWrapperComponent<PersonEditForm>(editForm, editForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (!editForm.getFieldGroup().isModified()) {
        			PersonDto dto = editForm.getValue();
        			personFacade.savePerson(dto);
        			Notification.show("Person data saved", Type.WARNING_MESSAGE);
        			refreshView();
        		}
        	}
        });
        
        return editView;
	}
	
	private void refreshView() {
		View currentView = SormasUI.get().getNavigator().getCurrentView();
    	if (currentView instanceof EventParticipantsView) {
    		// force refresh, because view didn't change
    		((EventParticipantsView)currentView).enter(null);
    	}
	}
	
}
