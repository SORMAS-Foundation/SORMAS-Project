package de.symeda.sormas.ui.surveillance.user;

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class UserController {
	
	private UserFacade uf = FacadeProvider.getUserFacade();

    public UserController() {
    	
    }
    
    public boolean isAdmin() {
    	return SurveillanceUI.get().getAccessControl().isUserInRole("admin");
    }

//    public void create() {
//    	CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent();
//    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
//    }
//    
    public void edit(UserDto caze) {
    	CommitDiscardWrapperComponent<UserEditForm> caseCreateComponent = getUserEditComponent();
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Edit a user");
    }


    public void overview() {
    	String navigationState = UsersView.VIEW_NAME;
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
        page.setUriFragment("!" + UsersView.VIEW_NAME + "/"
                + fragmentParameter, false);
    }
    

    public List<UserDto> getAllSurveillanceOfficers() {
    	return FacadeProvider.getUserFacade().getAll(UserRole.SURVEILLANCE_OFFICER);
    }
    
    
    public CommitDiscardWrapperComponent<UserEditForm> getUserEditComponent() {
    	
    	UserEditForm userEditForm = new UserEditForm();
//        userEditForm.setValue(createNewCase());
        final CommitDiscardWrapperComponent<UserEditForm> editView = new CommitDiscardWrapperComponent<UserEditForm>(userEditForm, userEditForm.getFieldGroup());
        editView.setWidth(400, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (userEditForm.getFieldGroup().isValid()) {
        			UserDto dto = userEditForm.getValue();
        			uf.saveUser(dto);
        			edit(dto);
        		}
        	}
        });
        
        return editView;
    }

}
