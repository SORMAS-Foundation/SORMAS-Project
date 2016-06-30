package de.symeda.sormas.ui.surveillance.person;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.DataHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PersonController {

	private PersonFacade pf = FacadeProvider.getPersonFacade();
	
    public PersonController() {
    	
    }
    
    public void create() {
//    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent();
//    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
    }
    
    private PersonDto createNewPerson() {
    	PersonDto person = new PersonDto();
    	return person;
    }
    
//    public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent() {
//    	
//    	CaseCreateForm caseCreateForm = new CaseCreateForm();
//        caseCreateForm.setDto(createNewCase());
//        final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(caseCreateForm, caseCreateForm.getFieldGroup());
//        editView.setWidth(400, Unit.PIXELS);
//        
//        editView.addCommitListener(new CommitListener() {
//        	@Override
//        	public void onCommit() {
//        		if (caseCreateForm.getFieldGroup().isValid()) {
//        			CaseDataDto dto = caseCreateForm.getDto();
//        			cf.saveCase(dto);
//        			overview(null);
//        		}
//        	}
//        });
//        
//        return editView;
//    }  
}
