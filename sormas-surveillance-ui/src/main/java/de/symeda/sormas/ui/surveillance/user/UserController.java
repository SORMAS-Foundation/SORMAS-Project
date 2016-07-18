package de.symeda.sormas.ui.surveillance.user;

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
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

    public void create() {
    	CommitDiscardWrapperComponent<UserEditForm> caseCreateComponent = getUserCreateComponent();
    	VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new user");    	
    }
    
    public void edit(UserDto user) {
    	CommitDiscardWrapperComponent<UserEditForm> userComponent = getUserEditComponent(user.getUuid());
    	VaadinUiUtil.showModalPopupWindow(userComponent, "Edit a user");
    }


    public void overview() {
    	String navigationState = UsersView.VIEW_NAME;
    	SurveillanceUI.get().getNavigator().navigateTo(navigationState);
    }
    
    /**
     * @TODO check for Session-login - userrole
     * @return
     */
    public Object[] getUserRoles() {
    	UserRole[] roles = {UserRole.SURVEILLANCE_OFFICER,UserRole.INFORMANT};
    	return roles;
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
    	return FacadeProvider.getUserFacade().getAll(UserRole.SURVEILLANCE_OFFICER, UserRole.INFORMANT);
    }
    
    
    public CommitDiscardWrapperComponent<UserEditForm> getUserEditComponent(final String userUuid) {
    	
    	UserEditForm userEditForm = new UserEditForm();
    	UserDto caze = findUser(userUuid);
        userEditForm.setValue(caze);
        final CommitDiscardWrapperComponent<UserEditForm> editView = new CommitDiscardWrapperComponent<UserEditForm>(userEditForm, userEditForm.getFieldGroup());
        editView.setWidth(400, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (userEditForm.getFieldGroup().isValid()) {
        			UserDto dto = userEditForm.getValue();
        			uf.saveUser(dto);
        			overview();
        		}
        	}
        });
        
        return editView;
    }
    
    private UserDto createNewUser() {
    	UserDto user = new UserDto();
    	user.setUuid(DataHelper.createUuid());
    	
    	LocationDto address = new LocationDto();
    	address.setUuid(DataHelper.createUuid());
    	user.setAddress(address);
    	
    	return user;
    }
    
    public CommitDiscardWrapperComponent<UserEditForm> getUserCreateComponent() {
    	
    	UserEditForm createForm = new UserEditForm();
        createForm.setValue(createNewUser());
        final CommitDiscardWrapperComponent<UserEditForm> editView = new CommitDiscardWrapperComponent<UserEditForm>(createForm, createForm.getFieldGroup());
        editView.setWidth(400, Unit.PIXELS);
        
        editView.addCommitListener(new CommitListener() {
        	@Override
        	public void onCommit() {
        		if (createForm.getFieldGroup().isValid()) {
        			UserDto dto = createForm.getValue();
        			uf.saveUser(dto);
        			overview();
        		}
        	}
        });
        return editView;
    }  
    
    
    private UserDto findUser(String uuid) {
        return uf.getByUuid(uuid);
    }

}
