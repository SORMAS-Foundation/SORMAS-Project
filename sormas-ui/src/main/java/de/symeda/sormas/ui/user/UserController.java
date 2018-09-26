package de.symeda.sormas.ui.user;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class UserController {

	public UserController() {
	}

	public void create() {
		CommitDiscardWrapperComponent<UserEditForm> userCreateComponent = getUserCreateComponent();
		Window window = VaadinUiUtil.showModalPopupWindow(userCreateComponent, "Create new user");
		// user form is too big for typical screens
		window.setWidth(userCreateComponent.getWrappedComponent().getWidth() + 64+20, Unit.PIXELS);
		window.setHeight(90, Unit.PERCENTAGE); 
	}

	public void edit(UserDto user) {
		CommitDiscardWrapperComponent<UserEditForm> userComponent = getUserEditComponent(user.getUuid());
		Window window = VaadinUiUtil.showModalPopupWindow(userComponent, "Edit a user");
		// user form is too big for typical screens
		window.setWidth(userComponent.getWrappedComponent().getWidth() + 64+20, Unit.PIXELS); 
		window.setHeight(90, Unit.PERCENTAGE); 
	}


	public void overview() {
		String navigationState = UsersView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
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

		Page page = SormasUI.get().getPage();
		page.setUriFragment("!" + UsersView.VIEW_NAME + "/"
				+ fragmentParameter, false);
	}

	public CommitDiscardWrapperComponent<UserEditForm> getUserEditComponent(final String userUuid) {

		UserEditForm userEditForm = new UserEditForm(false, UserRight.USER_EDIT);
		UserDto userDto = FacadeProvider.getUserFacade().getByUuid(userUuid);
		userEditForm.setValue(userDto);
		final CommitDiscardWrapperComponent<UserEditForm> editView = new CommitDiscardWrapperComponent<UserEditForm>(userEditForm, userEditForm.getFieldGroup());

		// Add reset password button
		Button resetPasswordButton = createResetPasswordButton(userUuid, editView);        
		editView.getButtonsPanel().addComponent(resetPasswordButton, 0);

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!userEditForm.getFieldGroup().isModified()) {
					UserDto dto = userEditForm.getValue();
					FacadeProvider.getUserFacade().saveUser(dto);
					refreshView();
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

		UserEditForm createForm = new UserEditForm(true, UserRight.USER_CREATE);
		createForm.setValue(createNewUser());
		final CommitDiscardWrapperComponent<UserEditForm> editView = new CommitDiscardWrapperComponent<UserEditForm>(createForm, createForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					UserDto dto = createForm.getValue();
					dto = FacadeProvider.getUserFacade().saveUser(dto);
					refreshView();
					makeNewPassword(dto.getUuid());
				}
			}
		});
		return editView;
	}  

	public boolean isLoginUnique(String uuid, String userName) {
		return FacadeProvider.getUserFacade().isLoginUnique(uuid, userName);
	}

	public void makeNewPassword(String userUuid) {
		String newPassword = FacadeProvider.getUserFacade().resetPassword(userUuid);

		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("Please copy this password, it is shown only once."));
		layout.addComponent(new Label("<h2>"+newPassword+"</h2>", ContentMode.HTML));
		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setCaption("New password");
		layout.setMargin(true);    	
	}

	public List<UserReferenceDto> filterByDistrict(List<UserReferenceDto> users, DistrictReferenceDto district) {
		List<UserDto> userDtos = new ArrayList<>();
		for(UserReferenceDto userRef : users) {
			userDtos.add(FacadeProvider.getUserFacade().getByUuid(userRef.getUuid()));
		}

		userDtos.removeIf(user -> user.getDistrict() == null || !user.getDistrict().equals(district));

		List<UserReferenceDto> userRefs = new ArrayList<>();
		for(UserDto user : userDtos) {
			userRefs.add(FacadeProvider.getUserFacade().getByUserNameAsReference(user.getUserName()));
		}

		return userRefs;
	}

	private void refreshView() {
		View currentView = SormasUI.get().getNavigator().getCurrentView();
		if (currentView instanceof UsersView) {
			// force refresh, because view didn't change
			((UsersView) currentView).enter(null);
		}
	}

	public Button createResetPasswordButton(String userUuid, CommitDiscardWrapperComponent<UserEditForm> editView) {
		Button resetPasswordButton = new Button(null, FontAwesome.UNLOCK_ALT);
		resetPasswordButton.setCaption("create new password");
		resetPasswordButton.addStyleName(ValoTheme.BUTTON_LINK);
		resetPasswordButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmationComponent resetPasswordComponent = getResetPasswordConfirmationComponent(userUuid, editView);
				Window popupWindow = VaadinUiUtil.showPopupWindow(resetPasswordComponent);
				resetPasswordComponent.addDoneListener(new DoneListener() {
					public void onDone() {
						popupWindow.close();
					}
				});
				resetPasswordComponent.getCancelButton().addClickListener(new ClickListener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void buttonClick(ClickEvent event) {
						popupWindow.close();
					}
				});
				popupWindow.setCaption("Update password");
			}
		});

		return resetPasswordButton;
	}

	public ConfirmationComponent getResetPasswordConfirmationComponent(String userUuid, CommitDiscardWrapperComponent<UserEditForm> editView) {
		ConfirmationComponent resetPasswordConfirmationComponent = new ConfirmationComponent(false) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfirm() {
				onDone();
				editView.discard();
				makeNewPassword(userUuid);
			}
			@Override
			protected void onCancel() {
			}
		};
		resetPasswordConfirmationComponent.getConfirmButton().setCaption("Really update password?");
		resetPasswordConfirmationComponent.getCancelButton().setCaption("Cancel");
		resetPasswordConfirmationComponent.setMargin(true);
		return resetPasswordConfirmationComponent;
	}

}
