package de.symeda.sormas.ui.surveillance.user;

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.surveillance.SurveillanceUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
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
//    	return FacadeProvider.getUserFacade().getAll(UserRole.SURVEILLANCE_OFFICER, UserRole.INFORMANT);
    	return FacadeProvider.getUserFacade().getAll(UserRole.SURVEILLANCE_OFFICER);
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

	public boolean isLoginUnique(String uuid, String userName) {
		return uf.isLoginUnique(uuid, userName);
	}

	public String getSuggestedUsername(String value, String value2) {
		StringBuilder sb = new StringBuilder();
		String trim = value.toLowerCase().replaceAll("\\s", "");
		sb.append(trim.length()>4?trim.substring(0, 4):trim);
		String trim2 = value2.toLowerCase().replaceAll("\\s", "");
		sb.append(trim2.length()>4?trim2.substring(0, 4):trim2);
		return sb.toString();
	}
	
	
	public void confirmNewPassword(String userUuid) {
		final ConfirmationComponent newPasswortComponent = new ConfirmationComponent(false) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfirm() {
				makeNewPassword(userUuid);
			}

			@Override
			protected void onCancel() {
			}
		};
		newPasswortComponent.getConfirmButton().setCaption("Really update password?");
		newPasswortComponent.getCancelButton().setCaption("Cancel");
		Window popupWindow = VaadinUiUtil.showPopupWindow(newPasswortComponent);
		
		newPasswortComponent.addDoneListener(new DoneListener() {
			public void onDone() {
				popupWindow.close();
			}
		});
		popupWindow.setCaption("Update password");
		newPasswortComponent.setMargin(true);    	
    }
	
	public void makeNewPassword(String userUuid) {
		String newPassword = uf.resetPassword(userUuid);
		
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("Please copy this password, it is shown only once."));
		layout.addComponent(new Label("<h2>"+newPassword+"</h2>", ContentMode.HTML));
		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setCaption("Update password");
		layout.setMargin(true);    	
	}

}
