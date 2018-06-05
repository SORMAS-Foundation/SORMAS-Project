package de.symeda.sormas.ui.visit;

import java.util.Collection;
import java.util.Date;
import java.util.function.Consumer;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class VisitController {

    public VisitController() {
    	
    }

	public void editVisit(VisitReferenceDto visitRef, Consumer<VisitReferenceDto> doneConsumer) {
    	VisitDto dto = FacadeProvider.getVisitFacade().getVisitByUuid(visitRef.getUuid());
    	VisitReferenceDto referenceDto = dto.toReference();
    	PersonDto visitPerson = FacadeProvider.getPersonFacade().getPersonByUuid(dto.getPerson().getUuid());
    	VisitEditForm editForm = new VisitEditForm(dto.getDisease(), null, visitPerson, false, UserRight.VISIT_EDIT);
        editForm.setValue(dto);
        final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<VisitEditForm>(editForm, editForm.getFieldGroup());
        editView.setWidth(100, Unit.PERCENTAGE);

        Window window = VaadinUiUtil.showModalPopupWindow(editView, "Edit visit");
        // visit form is too big for typical screens
		window.setWidth(editForm.getWidth() + 90, Unit.PIXELS); 
		window.setHeight(80, Unit.PERCENTAGE); 
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (!editForm.getFieldGroup().isModified()) {
        			VisitDto dto = editForm.getValue();
        			dto = FacadeProvider.getVisitFacade().saveVisit(dto);
        			if (doneConsumer != null) {
        				doneConsumer.accept(referenceDto);
        			}
        		}
        	}
        });
        
        if (LoginHelper.getCurrentUserRoles().contains(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getVisitFacade().deleteVisit(referenceDto, LoginHelper.getCurrentUserAsReference().getUuid());
					UI.getCurrent().removeWindow(window);
        			if (doneConsumer != null) {
        				doneConsumer.accept(referenceDto);
        			}
				}
			}, I18nProperties.getFieldCaption("Visit"));
		}
	}

	public void createVisit(ContactReferenceDto contactRef, Consumer<VisitReferenceDto> doneConsumer) {
		VisitDto visit = createNewVisit(contactRef);
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactRef.getUuid());
		PersonDto contactPerson = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
    	VisitEditForm createForm = new VisitEditForm(visit.getDisease(), contact, contactPerson, true, UserRight.VISIT_CREATE);
        createForm.setValue(visit);
        final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<VisitEditForm>(createForm, createForm.getFieldGroup());
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (!createForm.getFieldGroup().isModified()) {
        			VisitDto dto = createForm.getValue();
        			dto = FacadeProvider.getVisitFacade().saveVisit(dto);
        			if (doneConsumer != null) {
        				doneConsumer.accept(dto.toReference());
        			}
        		}
        	}
        });

        Window window = VaadinUiUtil.showModalPopupWindow(editView, "Create new visit");
        // visit form is too big for typical screens
		window.setWidth(createForm.getWidth() + 64 + 24, Unit.PIXELS); 
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
	
	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No visits selected", "You have not selected any visits.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected visits?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getVisitFacade().deleteVisit(new VisitReferenceDto(((VisitDto) selectedRow).getUuid()), LoginHelper.getCurrentUser().getUuid());
					}
					callback.run();
					new Notification("Visits deleted", "All selected visits have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}
	
}
