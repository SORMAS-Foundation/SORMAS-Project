package de.symeda.sormas.ui.person;

import java.util.function.Consumer;

import com.vaadin.server.Sizeable.Unit;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PersonController {

	private PersonFacade personFacade = FacadeProvider.getPersonFacade();
	
    public PersonController() {
    	
    }
    
    public void create(final Consumer<PersonReferenceDto> doneConsumer) {
    	final CommitDiscardWrapperComponent<PersonCreateForm> createComponent = getPersonCreateComponent();
    	if (doneConsumer != null) {
    		createComponent.addDoneListener(() -> 
    		doneConsumer.accept(createComponent.isCommited() ? createComponent.getWrappedComponent().getValue() : null));
    	}
    	VaadinUiUtil.showModalPopupWindow(createComponent, "Create new person");    	
    }
    
    private PersonReferenceDto createNewPerson() {
    	PersonReferenceDto person = new PersonReferenceDto();
    	person.setUuid(DataHelper.createUuid());
    	return person;
    }
    
    public CommitDiscardWrapperComponent<PersonCreateForm> getPersonCreateComponent() {
    	
    	PersonCreateForm createForm = new PersonCreateForm();
        createForm.setValue(createNewPerson());
        final CommitDiscardWrapperComponent<PersonCreateForm> editView = new CommitDiscardWrapperComponent<PersonCreateForm>(createForm, createForm.getFieldGroup());
        editView.setWidth(400, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (createForm.getFieldGroup().isValid()) {
        			PersonReferenceDto dto = createForm.getValue();
        			personFacade.savePerson(dto);
        		}
        	}
        });
        
        return editView;
    }  
}
