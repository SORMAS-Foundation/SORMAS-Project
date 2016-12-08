package de.symeda.sormas.ui.visit;

import java.util.Date;
import java.util.function.Consumer;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class VisitController {

    public VisitController() {
    	
    }

	public void editVisit(VisitReferenceDto visitRef, Consumer<VisitReferenceDto> doneConsumer) {
		
    	VisitDto dto = FacadeProvider.getVisitFacade().getVisitByUuid(visitRef.getUuid());
    	VisitEditForm editForm = new VisitEditForm(dto.getDisease());
        editForm.setValue(dto);
        final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<VisitEditForm>(editForm, editForm.getFieldGroup());
        editView.setWidth(100, Unit.PERCENTAGE);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (editForm.getFieldGroup().isValid()) {
        			VisitDto dto = editForm.getValue();
        			dto = FacadeProvider.getVisitFacade().saveVisit(dto);
        			if (doneConsumer != null) {
        				doneConsumer.accept(dto);
        			}
        		}
        	}
        });

        Window window = VaadinUiUtil.showModalPopupWindow(editView, "Edit visit");
        // visit form is too big for typical screens
        window.setWidth(900, Unit.PIXELS);
		window.setHeight(80, Unit.PERCENTAGE); 
	}

	public void createVisit(ContactReferenceDto contactRef, Consumer<VisitReferenceDto> doneConsumer) {
		
		VisitDto visit = createNewVisit(contactRef);
    	VisitEditForm createForm = new VisitEditForm(visit.getDisease());
        createForm.setValue(visit);
        final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<VisitEditForm>(createForm, createForm.getFieldGroup());
        editView.setWidth(100, Unit.PERCENTAGE);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (createForm.getFieldGroup().isValid()) {
        			VisitDto dto = createForm.getValue();
        			dto = FacadeProvider.getVisitFacade().saveVisit(dto);
        			if (doneConsumer != null) {
        				doneConsumer.accept(dto);
        			}
        		}
        	}
        });

        Window window = VaadinUiUtil.showModalPopupWindow(editView, "Create new visit");
        // visit form is too big for typical screens
        window.setWidth(900, Unit.PIXELS);
		window.setHeight(80, Unit.PERCENTAGE); 
	}
	
    private VisitDto createNewVisit(ContactReferenceDto contactRef) {
    	
    	ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactRef.getUuid());
    	CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(contact.getCaze().getUuid());
    	
    	VisitDto visit = new VisitDto();
    	visit.setUuid(DataHelper.createUuid());
    	
    	visit.setPerson(contact.getPerson());
    	visit.setDisease(caze.getDisease());

    	SymptomsDto symptoms = new SymptomsDto();
    	visit.setSymptoms(symptoms);
    	
    	visit.setVisitDateTime(new Date());
    	UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
    	visit.setVisitUser(userReference);
    	
    	return visit;
    }
}
